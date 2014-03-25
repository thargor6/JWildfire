/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2012 Andreas Maschke

  This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser 
  General Public License as published by the Free Software Foundation; either version 2.1 of the 
  License, or (at your option) any later version.
 
  This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License along with this software; 
  if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jwildfire.create.tina.animate;

import java.io.File;

import javax.imageio.ImageIO;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageWriter;

import com.flagstone.transform.Background;
import com.flagstone.transform.Movie;
import com.flagstone.transform.MovieHeader;
import com.flagstone.transform.MovieTag;
import com.flagstone.transform.Place2;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.datatype.Color;
import com.flagstone.transform.datatype.WebPalette;
import com.flagstone.transform.image.ImageTag;
import com.flagstone.transform.linestyle.LineStyle1;
import com.flagstone.transform.shape.ShapeTag;
import com.flagstone.transform.util.image.ImageShape;
import com.flagstone.transform.util.sound.SoundFactory;

public class SWFAnimationRenderThread implements Runnable {
  private final SWFAnimationRenderThreadController controller;
  private final String outputFilename;
  private boolean cancelSignalled;
  private final int fromFrame, toFrame;
  private FlameMovie flameMovie;
  private SoundFactory soundFactory;
  private Movie movie;
  private int uid;
  private Throwable lastError;

  public SWFAnimationRenderThread(SWFAnimationRenderThreadController pController, FlameMovie pAnimation, String pOutputFilename, int pFromFrame, int pToFrame) {
    controller = pController;
    flameMovie = pAnimation;
    outputFilename = pOutputFilename;
    fromFrame = pFromFrame;
    toFrame = pToFrame;
  }

  @Override
  public void run() {
    try {
      try {
        cancelSignalled = false;
        lastError = null;
        controller.getProgressUpdater().initProgress(flameMovie.getFrameCount());
        // Create frames
        int startFrame = this.fromFrame;
        if (startFrame < 1)
          startFrame = 1;
        int endFrame = this.toFrame;
        int frameCount = flameMovie.getFrameCount();
        if (endFrame < 1 || endFrame > frameCount)
          endFrame = frameCount;

        for (int i = startFrame; i <= endFrame; i++) {
          if (cancelSignalled) {
            break;
          }
          Flame currFlame = createFlame(i);
          switch (flameMovie.getOutputFormat()) {
            case SWF: {
              SimpleImage image = renderImage(currFlame);
              if (i == startFrame)
                initMovie(image);
              addImageToMovie(image, i);
            }
              break;
            case SWF_AND_PNG: {
              SimpleImage image = renderImage(currFlame);
              if (i == startFrame)
                initMovie(image);
              addImageToMovie(image, i);
              saveFrame(image, i);
            }
              break;
            case PNG: {
              SimpleImage image = renderImage(currFlame);
              saveFrame(image, i);
            }
              break;
            case FLAMES: {
              saveFlame(currFlame, i);
            }
              break;
          }
          controller.getProgressUpdater().updateProgress(i);
        }
        // Finalize SWF  
        switch (flameMovie.getOutputFormat()) {
          case SWF:
          case SWF_AND_PNG:
            finishMovie();
            break;
        }
      }
      catch (Throwable ex) {
        lastError = ex;
        throw new RuntimeException(ex);
      }
    }
    finally {
      controller.onRenderFinished();
    }
  }

  private void prepareFlame(Flame pFlame) {
    pFlame.setSampleDensity(flameMovie.getQuality());
  }

  private Flame createFlame(int pFrame) throws Exception {
    Flame flame1 = flameMovie.getFlame(pFrame);
    prepareFlame(flame1);
    Flame res = flameMovie.createAnimatedFlame(flame1, pFrame);
    return res;
  }

  private SimpleImage renderImage(Flame pFlame) throws Exception {
    return AnimationService.renderFrame(pFlame, flameMovie.getFrameWidth(), flameMovie.getFrameHeight(), controller.getPrefs());
  }

  private void saveFrame(SimpleImage pImage, int pFrame) throws Exception {
    String filename = outputFilename;
    {
      int pSlash = filename.lastIndexOf("/");
      int pSlash2 = filename.lastIndexOf("\\");
      if (pSlash2 > pSlash) {
        pSlash = pSlash2;
      }
      int pDot = filename.lastIndexOf(".");
      if (pDot > pSlash) {
        filename = filename.substring(0, pDot);
      }
    }
    String hs = String.valueOf(pFrame);
    int length = calcFrameNumberLength();
    while (hs.length() < length) {
      hs = "0" + hs;
    }
    filename += hs + ".png";
    ImageIO.setUseCache(false);
    new ImageWriter().saveImage(pImage, filename, true);
  }

  private void saveFlame(Flame pFlame, int pFrame) throws Exception {
    String filename = outputFilename;
    {
      int pSlash = filename.lastIndexOf("/");
      int pSlash2 = filename.lastIndexOf("\\");
      if (pSlash2 > pSlash) {
        pSlash = pSlash2;
      }
      int pDot = filename.lastIndexOf(".");
      if (pDot > pSlash) {
        filename = filename.substring(0, pDot);
      }
    }
    String hs = String.valueOf(pFrame);
    int length = calcFrameNumberLength();
    while (hs.length() < length) {
      hs = "0" + hs;
    }
    filename += hs + "." + Tools.FILEEXT_FLAME;
    new FlameWriter().writeFlame(pFlame, filename);
  }

  private int calcFrameNumberLength() {
    return Math.max(4, String.valueOf(flameMovie.getFrameCount()).length());
  }

  private void addImageToMovie(SimpleImage pImage, int pFrame) throws Exception {
    final JWFImageFactory factory = createImageFactory(pImage);
    final ImageTag image = factory.defineImage(uid++);
    final int xOrigin = -image.getWidth() / 2;
    final int yOrigin = -image.getHeight() / 2;
    final int width = 20;
    final Color color = WebPalette.BLACK.color();
    final ShapeTag shape = new ImageShape().defineShape(uid++, image,
        xOrigin, yOrigin, new LineStyle1(width, color));
    movie.add(image);
    movie.add(shape);
    if (pFrame == 1) {
      movie.add(Place2.show(shape.getIdentifier(), 1, 0, 0));
    }
    else {
      movie.add(Place2.replace(shape.getIdentifier(), 1, 0, 0));
    }
    if (soundFactory != null) {
      MovieTag block = soundFactory.streamSound();
      if (block != null) {
        movie.add(block);
      }
    }
    movie.add(ShowFrame.getInstance());
  }

  private void finishMovie() throws Exception {
    movie.encodeToFile(new File(outputFilename));
  }

  private JWFImageFactory createImageFactory(SimpleImage pImage) throws Exception {
    final JWFImageFactory factory = new JWFImageFactory();
    factory.read(pImage);
    return factory;
  }

  private void initMovie(SimpleImage pFirstImage) throws Exception {
    uid = 1;
    movie = new Movie();
    int movieWidth = pFirstImage.getImageWidth();
    int movieHeight = pFirstImage.getImageHeight();
    final int xOrigin = -movieWidth / 2;
    final int yOrigin = -movieHeight / 2;
    MovieHeader header = new MovieHeader();
    header.setFrameRate((float) flameMovie.getFramesPerSecond());
    final int width = 20;
    final Color color = WebPalette.BLACK.color();
    final JWFImageFactory factory = createImageFactory(new SimpleImage(movieWidth - 1, movieHeight - 1));
    final ImageTag image = factory.defineImage(uid++);
    ShapeTag shape = new ImageShape().defineShape(uid++, image,
        xOrigin, yOrigin, new LineStyle1(width, color));
    header.setFrameSize(shape.getBounds());
    movie.add(header);
    movie.add(new Background(WebPalette.LIGHT_BLUE.color()));
    uid = 1;
    // Add sound
    if (flameMovie.getSoundFilename() != null) {
      soundFactory = new SoundFactory();
      soundFactory.read(new File(flameMovie.getSoundFilename()));
      movie.add(soundFactory.streamHeader((float) flameMovie.getFramesPerSecond()));
    }
    else {
      soundFactory = null;
    }
  }

  public void setCancelSignalled(boolean cancelSignalled) {
    this.cancelSignalled = cancelSignalled;
  }

  public Throwable getLastError() {
    return lastError;
  }

  public boolean isCancelSignalled() {
    return cancelSignalled;
  }
}
