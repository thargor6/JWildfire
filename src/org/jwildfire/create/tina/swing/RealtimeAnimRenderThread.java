/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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

import org.jwildfire.create.tina.audio.JLayerInterface;
import org.jwildfire.create.tina.audio.RecordedFFT;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.VariationFuncList;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ImagePanel;

public class RealtimeAnimRenderThread implements Runnable {
  private final DancingFractalsController controller;
  private boolean forceAbort;
  private boolean done;
  private XForm xForm0, xForm1, xForm2, xFormF;
  private RecordedFFT fftData;
  private JLayerInterface musicPlayer;
  private ImagePanel fftPanel;
  private double finalXFormAlpha = 0.0;
  private int framesPerSecond = 12;
  private boolean drawTriangles = true;

  public RealtimeAnimRenderThread(DancingFractalsController pController) {
    controller = pController;
  }

  private double getFFTValue(short[] pFFT, int pIdx) {
    short val = pIdx < pFFT.length ? pFFT[pIdx] : 0;
    return 2.0 * val / (double) Short.MAX_VALUE;
  }

  @Override
  public void run() {
    done = forceAbort = false;
    finalXFormAlpha = 0.0;
    boolean doDrawFFT = true;

    try {
      long fpsMeasureMentFrameCount = 0;
      long startFPSMeasurement = System.currentTimeMillis();
      long nextFrame = startFPSMeasurement;
      double fps = 0.0;
      while (!done && !forceAbort) {
        long time = System.currentTimeMillis();
        while (time < nextFrame) {
          try {
            Thread.sleep(1);
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
          time = System.currentTimeMillis();
        }
        nextFrame = (long) (time + 1000.0 / (double) framesPerSecond + 0.5);
        Flame flame = currFlame;
        while (flameChanging) {
          flame = currFlame;
        }
        if (fftPanel != null && fftData != null) {
          SimpleImage img = fftPanel.getImage();
          short currFFT[] = fftData.getData(musicPlayer.getPosition());
          if (doDrawFFT) {
            drawFFT(img, currFFT);
            fftPanel.repaint();
          }
          transformXForm1(currFFT, flame);
          transformXForm2(currFFT, flame);
          transformXForm3(currFFT, flame);
          transformXFormF(currFFT, flame);
        }
        fpsMeasureMentFrameCount++;
        long dt = (System.currentTimeMillis() - startFPSMeasurement);
        if (dt > 500) {
          fps = (double) (fpsMeasureMentFrameCount * 1000.0) / (double) dt;
          fpsMeasureMentFrameCount = 0;
          startFPSMeasurement = System.currentTimeMillis();
        }
        controller.refreshFlameImage(flame, drawTriangles, fps);
      }
    }
    finally {
      done = true;
    }
  }

  private void transformXForm1(short[] pFFT, Flame pFlame) {
    if (xForm0 != null) {
      double amp0 = getFFTValue(pFFT, 0);
      double amp1 = getFFTValue(pFFT, 1);
      double amp2 = getFFTValue(pFFT, 2);
      XForm xForm = xForm0.makeCopy();
      XFormTransformService.rotate(xForm, -amp0 * 7, false);
      XFormTransformService.localTranslate(xForm, -amp1 * 0.5, amp2 * 0.5);
      pFlame.getXForms().set(0, xForm);
    }
  }

  private void transformXForm2(short[] pFFT, Flame pFlame) {
    if (xForm1 != null) {
      double amp3 = getFFTValue(pFFT, 3);
      double amp4 = getFFTValue(pFFT, 4);
      double amp5 = getFFTValue(pFFT, 5);
      XForm xForm = xForm1.makeCopy();
      XFormTransformService.rotate(xForm, -amp3 * 2, false);
      XFormTransformService.localTranslate(xForm, amp4 * 0.25, -amp5 * 0.25);
      pFlame.getXForms().set(1, xForm);
    }
  }

  private void transformXForm3(short[] pFFT, Flame pFlame) {
    if (xForm2 != null) {
      double amp7 = getFFTValue(pFFT, 47);
      double amp8 = getFFTValue(pFFT, 48);
      double amp9 = getFFTValue(pFFT, 49);
      XForm xForm = xForm2.makeCopy();
      XFormTransformService.rotate(xForm, amp7 * 5, false);
      XFormTransformService.localTranslate(xForm, -amp8 * 0.75, amp9 * 0.5);
      pFlame.getXForms().set(2, xForm);
    }
  }

  private void transformXFormF(short[] pFFT, Flame pFlame) {
    if (xFormF != null) {
      double amp2 = getFFTValue(pFFT, 2);
      double amp6 = getFFTValue(pFFT, 6);
      XForm xForm = xFormF.makeCopy();
      XFormTransformService.rotate(xForm, finalXFormAlpha, false);
      finalXFormAlpha += amp6;
      if (finalXFormAlpha > 360)
        finalXFormAlpha -= 360;
      XFormTransformService.scale(xForm, 1.0 + amp2 * 0.1, true, true, false);
      pFlame.getFinalXForms().clear();
      pFlame.getFinalXForms().add(xForm);
    }
  }

  private boolean flameChanging = false;
  private Flame currFlame;

  public void changeFlame(Flame pFlame) {
    flameChanging = true;
    try {
      if (pFlame != null) {
        xForm0 = pFlame.getXForms().get(0);
        xForm1 = pFlame.getXForms().size() > 1 ? pFlame.getXForms().get(1) : null;
        xForm2 = pFlame.getXForms().size() > 2 ? pFlame.getXForms().get(2) : null;
        xFormF = pFlame.getFinalXForms().size() > 0 ? pFlame.getFinalXForms().get(pFlame.getFinalXForms().size() - 1) : null;
        if (xFormF == null) {
          xFormF = new XForm();
          xFormF.addVariation(1.0, VariationFuncList.getVariationFuncInstance("linear3D"));
        }
      }
      currFlame = pFlame;
    }
    finally {
      flameChanging = false;
    }
  }

  public boolean isDone() {
    return done;
  }

  public void setForceAbort(boolean forceAbort) {
    this.forceAbort = forceAbort;
  }

  public void setFFTData(RecordedFFT pFFTData) {
    fftData = pFFTData;
  }

  public void setMusicPlayer(JLayerInterface pMusicPlayer) {
    musicPlayer = pMusicPlayer;
  }

  public void setFFTPanel(ImagePanel pFFTPanel) {
    fftPanel = pFFTPanel;
  }

  private void drawFFT(SimpleImage img, short[] buffer) {
    final double hScale = 1.75;
    final int imgWidth = img.getImageWidth();
    final int imgHeight = img.getImageHeight();
    int blockSize = imgWidth / (buffer.length + 1);
    img.fillBackground(0, 0, 0);
    for (int i = 0; i < buffer.length; i++) {
      short val = buffer[i];
      int iVal = (int) ((double) val / (double) Short.MAX_VALUE * (double) imgHeight * hScale + 0.5);
      if (iVal < 0)
        iVal = 0;
      else if (iVal >= imgHeight)
        iVal = imgHeight - 1;

      for (int y = 0; y < iVal; y++) {
        img.setARGB(i * blockSize, imgHeight - 1 - y, 255, 255, 0, 0);
        img.setARGB((i + 1) * blockSize, imgHeight - 1 - y, 255, 255, 0, 0);
      }
      for (int x = i * blockSize; x < (i + 1) * blockSize; x++) {
        img.setARGB(x, imgHeight - 1 - iVal, 255, 255, 0, 0);
      }
    }
  }

  public void setFramesPerSecond(int pFPS) {
    framesPerSecond = pFPS;
  }

  public void setDrawTriangles(boolean drawTriangles) {
    this.drawTriangles = drawTriangles;
  }
}
