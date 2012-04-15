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
package org.jwildfire.create.tina.swing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.animate.SWFAnimation;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.Flam3Reader;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImageFileChooser;

import com.flagstone.transform.Background;
import com.flagstone.transform.Movie;
import com.flagstone.transform.MovieHeader;
import com.flagstone.transform.Place2;
import com.flagstone.transform.PlaceType;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.datatype.Color;
import com.flagstone.transform.datatype.CoordTransform;
import com.flagstone.transform.datatype.WebPalette;
import com.flagstone.transform.fillstyle.FillStyle;
import com.flagstone.transform.fillstyle.MorphSolidFill;
import com.flagstone.transform.image.ImageTag;
import com.flagstone.transform.linestyle.LineStyle;
import com.flagstone.transform.linestyle.LineStyle1;
import com.flagstone.transform.linestyle.MorphLineStyle;
import com.flagstone.transform.shape.Curve;
import com.flagstone.transform.shape.DefineMorphShape;
import com.flagstone.transform.shape.Line;
import com.flagstone.transform.shape.Shape;
import com.flagstone.transform.shape.ShapeRecord;
import com.flagstone.transform.shape.ShapeStyle;
import com.flagstone.transform.shape.ShapeTag;
import com.flagstone.transform.util.image.ImageFactory;
import com.flagstone.transform.util.image.ImageShape;

public class TinaSWFAnimatorController {
  private final SWFAnimation currAnimation = new SWFAnimation();
  private final TinaController parentCtrl;
  private final Prefs prefs;
  private final ErrorHandler errorHandler;
  private final JComboBox swfAnimatorScriptCmb;
  private final JComboBox swfAnimatorXFormScriptCmb;
  private final JTextField swfAnimatorFramesREd;
  private final JTextField swfAnimatorFramesPerSecondREd;
  private final JButton swfAnimatorGenerateButton;
  private final JTextArea swfAnimatorScriptArea;
  private final JComboBox swfAnimatorResolutionProfileCmb;
  private final JComboBox swfAnimatorQualityProfileCmb;
  private final JButton swfAnimatorCompileScriptButton;
  private final JButton swfAnimatorLoadFlameFromMainButton;
  private final JButton swfAnimatorLoadFlameFromClipboardButton;
  private final JButton swfAnimatorLoadFlameButton;
  private final JToggleButton swfAnimatorCustomScriptBtn;
  private final JToggleButton swfAnimatorHalveSizeButton;
  private final JProgressBar swfAnimatorProgressBar;
  private final JButton swfAnimatorCancelButton;
  private final JButton swfAnimatorLoadSoundButton;
  private final JButton swfAnimatorClearSoundButton;

  public TinaSWFAnimatorController(TinaController pParentCtrl, ErrorHandler pErrorHandler, Prefs pPrefs, JComboBox pSWFAnimatorScriptCmb,
      JComboBox pSWFAnimatorXFormScriptCmb, JTextField pSWFAnimatorFramesREd, JTextField pSWFAnimatorFramesPerSecondREd,
      JButton pSWFAnimatorGenerateButton, JTextArea pSWFAnimatorScriptArea, JComboBox pSWFAnimatorResolutionProfileCmb,
      JComboBox pSWFAnimatorQualityProfileCmb, JButton pSWFAnimatorCompileScriptButton, JButton pSWFAnimatorLoadFlameFromMainButton,
      JButton pSWFAnimatorLoadFlameFromClipboardButton, JButton pSWFAnimatorLoadFlameButton, JToggleButton pSWFAnimatorCustomScriptBtn,
      JToggleButton pSWFAnimatorHalveSizeButton, JProgressBar pSWFAnimatorProgressBar, JButton pSWFAnimatorCancelButton,
      JButton pSWFAnimatorLoadSoundButton, JButton pSWFAnimatorClearSoundButton) {
    parentCtrl = pParentCtrl;
    prefs = pPrefs;
    errorHandler = pErrorHandler;
    swfAnimatorScriptCmb = pSWFAnimatorScriptCmb;
    swfAnimatorXFormScriptCmb = pSWFAnimatorXFormScriptCmb;
    swfAnimatorFramesREd = pSWFAnimatorFramesREd;
    swfAnimatorFramesPerSecondREd = pSWFAnimatorFramesPerSecondREd;
    swfAnimatorGenerateButton = pSWFAnimatorGenerateButton;
    swfAnimatorScriptArea = pSWFAnimatorScriptArea;
    swfAnimatorResolutionProfileCmb = pSWFAnimatorResolutionProfileCmb;
    swfAnimatorQualityProfileCmb = pSWFAnimatorQualityProfileCmb;
    swfAnimatorCompileScriptButton = pSWFAnimatorCompileScriptButton;
    swfAnimatorLoadFlameFromMainButton = pSWFAnimatorLoadFlameFromMainButton;
    swfAnimatorLoadFlameFromClipboardButton = pSWFAnimatorLoadFlameFromClipboardButton;
    swfAnimatorLoadFlameButton = pSWFAnimatorLoadFlameButton;
    swfAnimatorCustomScriptBtn = pSWFAnimatorCustomScriptBtn;
    swfAnimatorHalveSizeButton = pSWFAnimatorHalveSizeButton;
    swfAnimatorProgressBar = pSWFAnimatorProgressBar;
    swfAnimatorCancelButton = pSWFAnimatorCancelButton;
    swfAnimatorLoadSoundButton = pSWFAnimatorLoadSoundButton;
    swfAnimatorClearSoundButton = pSWFAnimatorClearSoundButton;
    enableControls();
  }

  protected void enableControls() {
    // TODO Auto-generated method stub

  }

  /*
   
      public void animationGenerateButton_clicked() {
    try {
      int frames = Integer.parseInt(animateFramesREd.getText());
      boolean doMorph = morphCheckBox.isSelected();
      GlobalScript globalScript = (GlobalScript) animateGlobalScriptCmb.getSelectedItem();
      XFormScript xFormScript = (XFormScript) animateXFormScriptCmb.getSelectedItem();
      LightScript lightScript = (LightScript) animateLightScriptCmb.getSelectedItem();
      String imagePath = animateOutputREd.getText();
      ResolutionProfile resProfile = getResolutionProfile();
      QualityProfile qualProfile = getQualityProfile();
      int width = resProfile.getWidth();
      int height = resProfile.getHeight();
      int quality = qualProfile.getQuality();
      for (int frame = 1; frame <= frames; frame++) {
        Flame flame1 = doMorph ? morphFlame1.makeCopy() : currFlame.makeCopy();
        Flame flame2 = doMorph ? morphFlame2.makeCopy() : null;
        flame1.setSpatialOversample(qualProfile.getSpatialOversample());
        flame1.setColorOversample(qualProfile.getColorOversample());
        AnimationService.renderFrame(frame, frames, flame1, flame2, doMorph, globalScript, xFormScript, lightScript, imagePath, width, height, quality, prefs);
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

    
    animateFramesREd.setText(String.valueOf(prefs.getTinaRenderMovieFrames()));
    
   */

  private void test2() {
    try {
      Movie movie = new Movie();

      int uid = 1;

      for (int i = 1; i <= 60; i++) {
        final ImageFactory factory = new ImageFactory();
        String id = String.valueOf(i);
        while (id.length() < 4) {
          id = "0" + id;
        }
        factory.read(new File("C:\\TMP\\wf\\render\\Img" + id + ".jpg"));
        System.out.println("C:\\TMP\\wf\\render\\Img" + id + ".jpg");
        final ImageTag image = factory.defineImage(uid++);

        /*
         * Generate the shape that actually displays the image. The origin is
         * in the centre of the shape so the registration point for the image
         * is -width/2 -height/2 so the centre of the image and the shape
         * coincide.
         */
        final int xOrigin = -image.getWidth() / 2;
        final int yOrigin = -image.getHeight() / 2;
        final int width = 20;
        final Color color = WebPalette.BLACK.color();

        final ShapeTag shape = new ImageShape().defineShape(uid++, image,
            xOrigin, yOrigin, new LineStyle1(width, color));

        /***************************************************
         * Put all the objects together in a movie
         ***************************************************/
        if (i == 1) {
          MovieHeader header = new MovieHeader();
          header.setFrameRate(12f);
          header.setFrameSize(shape.getBounds());
          movie.add(header);
          movie.add(new Background(WebPalette.LIGHT_BLUE.color()));
        }
        movie.add(image);
        movie.add(shape);
        System.out.println(shape.getIdentifier());
        movie.add(Place2.show(shape.getIdentifier(), i, i * 2, i * 2));
        movie.add(ShowFrame.getInstance());
      }
      movie.encodeToFile(new File("C:\\TMP\\wf\\z2.swf"));
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Shape createRectangle() {
    int width = 4000;
    int height = 4000;

    List<LineStyle> lineStyles = new ArrayList<LineStyle>();
    List<FillStyle> fillStyles = new ArrayList<FillStyle>();

    lineStyles.add(new MorphLineStyle(20, 20, WebPalette.BLACK.color(),
        WebPalette.WHITE.color()));
    fillStyles.add(new MorphSolidFill(WebPalette.BLACK.color(),
        WebPalette.WHITE.color()));

    /*
     * Create the outline of the shape. Morphing requires that both shapes
     * have the same number of vertices.
     */
    List<ShapeRecord> shapeRecords = new ArrayList<ShapeRecord>();

    shapeRecords.add(new ShapeStyle().setLineStyle(1).setFillStyle(1)
        .setMove(width / 2, 0));
    shapeRecords.add(new Line(0, height / 2));
    shapeRecords.add(new Line(-width / 2, 0));
    shapeRecords.add(new Line(-width / 2, 0));
    shapeRecords.add(new Line(0, -height / 2));
    shapeRecords.add(new ShapeStyle().setMove(-width / 2, 0));
    shapeRecords.add(new Line(0, -height / 2));
    shapeRecords.add(new Line(width / 2, 0));
    shapeRecords.add(new Line(width / 2, 0));
    shapeRecords.add(new Line(0, height / 2));

    return new Shape(shapeRecords);
  }

  private Shape createCircle() {
    int x = 0;
    int y = 0;

    int rx = 2000;
    int ry = 2000;

    int startX = (int) (0.707 * rx) + x;
    int startY = (int) (0.707 * ry) + y;

    int ax = (int) (0.293 * rx);
    int ay = (int) (0.293 * ry);
    int cx = (int) (0.414 * rx);
    int cy = (int) (0.414 * ry);

    /*
     * Create the outline of the shape. Morphing requires that both shapes
     * have the same number of vertices.
     */
    List<ShapeRecord> shapeRecords = new ArrayList<ShapeRecord>();

    shapeRecords.add(new ShapeStyle().setLineStyle(1).setFillStyle(1)
        .setMove(startX, startY));
    shapeRecords.add(new Curve(-ax, ay, -cx, 0));
    shapeRecords.add(new Curve(-cx, 0, -ax, -ay));
    shapeRecords.add(new Curve(-ax, -ay, 0, -cy));
    shapeRecords.add(new Curve(0, -cy, ax, -ay));
    shapeRecords.add(new Curve(ax, -ay, cx, 0));
    shapeRecords.add(new Curve(cx, 0, ax, ay));
    shapeRecords.add(new Curve(ax, ay, 0, cy));
    shapeRecords.add(new Curve(0, cy, -ax, ay));

    return new Shape(shapeRecords);
  }

  void createMovie2(Movie movie) {

    MovieHeader header = new MovieHeader();
    header.setFrameRate(12.0f);
    header.setFrameSize(new Bounds(0, 0, 8000, 8000));
    movie.add(header);

    movie.add(new Background(WebPalette.LIGHT_BLUE.color()));

    Shape rectangle = createRectangle();
    Shape circle = createCircle();

    Bounds startBounds = new Bounds(-2000, -2000, 2000, 2000);
    Bounds endBounds = new Bounds(-2000, -2000, 2000, 2000);

    List<LineStyle> lineStyles = new ArrayList<LineStyle>();
    List<FillStyle> fillStyles = new ArrayList<FillStyle>();

    lineStyles.add(new MorphLineStyle(20, 20, WebPalette.BLACK.color(),
        WebPalette.WHITE.color()));
    fillStyles.add(new MorphSolidFill(WebPalette.BLACK.color(),
        WebPalette.WHITE.color()));

    DefineMorphShape shape = new DefineMorphShape(1, startBounds,
        endBounds, fillStyles, lineStyles, rectangle, circle);

    movie.add(shape);
    movie.add(Place2.show(shape.getIdentifier(), 1, 4000, 4000)
        .setRatio(0));
    movie.add(ShowFrame.getInstance());

    CoordTransform location = CoordTransform.translate(4000, 4000);

    for (int i = 4095; i <= 65535; i += 4096) {
      movie.add(new Place2().setType(PlaceType.MODIFY).setLayer(1)
          .setRatio(i).setTransform(location));
      movie.add(ShowFrame.getInstance());
    }

    // Add a delay so the circle is displayed for 2 seconds
    for (int i = 0; i < 24; i++) {
      movie.add(ShowFrame.getInstance());
    }

  }

  void createMovie(Movie movie) {

    MovieHeader header = new MovieHeader();
    header.setFrameRate(12.0f);
    header.setFrameSize(new Bounds(0, 0, 8000, 8000));
    movie.add(header);

    movie.add(new Background(WebPalette.LIGHT_BLUE.color()));

    Shape rectangle = createRectangle();
    Shape circle = createCircle();

    Bounds startBounds = new Bounds(-2000, -2000, 2000, 2000);
    Bounds endBounds = new Bounds(-2000, -2000, 2000, 2000);

    List<LineStyle> lineStyles = new ArrayList<LineStyle>();
    List<FillStyle> fillStyles = new ArrayList<FillStyle>();

    lineStyles.add(new MorphLineStyle(20, 20, WebPalette.BLACK.color(),
        WebPalette.WHITE.color()));
    fillStyles.add(new MorphSolidFill(WebPalette.BLACK.color(),
        WebPalette.WHITE.color()));

    DefineMorphShape shape = new DefineMorphShape(1, startBounds,
        endBounds, fillStyles, lineStyles, rectangle, circle);

    movie.add(shape);
    movie.add(Place2.show(shape.getIdentifier(), 1, 4000, 4000)
        .setRatio(0));
    movie.add(ShowFrame.getInstance());

    CoordTransform location = CoordTransform.translate(4000, 4000);

    for (int i = 4095; i <= 65535; i += 4096) {
      movie.add(new Place2().setType(PlaceType.MODIFY).setLayer(1)
          .setRatio(i).setTransform(location));
      movie.add(ShowFrame.getInstance());
    }

    // Add a delay so the circle is displayed for 2 seconds
    for (int i = 0; i < 24; i++) {
      movie.add(ShowFrame.getInstance());
    }

  }

  private void test() {
    String out = "C:\\TMP\\wf\\z3.swf";
    Movie movie = new Movie();
    createMovie(movie);
    try {
      movie.encodeToFile(new File(out));
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (DataFormatException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void loadFlameFromMainButton_clicked() {
    // TODO Auto-generated method stub

  }

  public void loadFlameFromClipboardButton_clicked() {
    // TODO Auto-generated method stub

  }

  public void loadFlameButton_clicked() {
    try {
      JFileChooser chooser = new FlameFileChooser(prefs);
      if (prefs.getInputFlamePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(prefs.getInputFlamePath()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(swfAnimatorScriptArea) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        List<Flame> flames = new Flam3Reader().readFlames(file.getAbsolutePath());
        Flame newFlame = flames.get(0);
        prefs.setLastInputFlameFile(file);
        currAnimation.setFlames(newFlame);
        enableControls();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void customScriptButton_clicked() {
    enableControls();
  }

  public void compileScriptButton_clicked() {
    // TODO Auto-generated method stub

  }

  public void halveSizeButton_clicked() {
    // TODO Auto-generated method stub

  }

  public void cancelButton_clicked() {
    // TODO Auto-generated method stub

  }

  public void generateButton_clicked() {
    JFileChooser chooser = new ImageFileChooser();
    if (prefs.getSwfPath() != null) {
      try {
        chooser.setCurrentDirectory(new File(prefs.getSwfPath()));
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    if (chooser.showSaveDialog(swfAnimatorScriptArea) == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      prefs.setLastOutputSwfFile(file);
      // TODO
    }
  }

  public void loadSoundButton_clicked() {
    try {
      JFileChooser chooser = new SoundFileChooser(prefs);
      if (prefs.getInputFlamePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(prefs.getInputSoundFilePath()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(swfAnimatorScriptArea) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        prefs.setLastInputSoundFile(file);
        // TODO
        enableControls();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void clearSoundButton_clicked() {
    // TODO Auto-generated method stub

  }

}
