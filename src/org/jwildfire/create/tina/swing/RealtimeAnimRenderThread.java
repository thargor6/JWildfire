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
  private XForm xForm0;
  private XForm xForm1;
  private XForm xFormF;
  private RecordedFFT fftData;
  private JLayerInterface musicPlayer;
  private ImagePanel fftPanel;

  public RealtimeAnimRenderThread(DancingFractalsController pController) {
    controller = pController;
  }

  @Override
  public void run() {
    done = forceAbort = false;
    double alpha = 0.0;
    boolean doDrawFFT = true;

    try {
      while (!done && !forceAbort) {
        if (fftPanel != null && fftData != null) {
          SimpleImage img = fftPanel.getImage();
          short currFFT[] = fftData.getData(musicPlayer.getPosition());
          if (doDrawFFT) {
            drawFFT(img, currFFT);
            fftPanel.repaint();
          }
          amp0 = 2.0 * currFFT[1] / (double) Short.MAX_VALUE;
          amp1 = 2.0 * currFFT[2] / (double) Short.MAX_VALUE;
          amp2 = 2.0 * currFFT[3] / (double) Short.MAX_VALUE;
          amp3 = 2.0 * currFFT[4] / (double) Short.MAX_VALUE;
          amp4 = 2.0 * currFFT[5] / (double) Short.MAX_VALUE;
          amp5 = 2.0 * currFFT[6] / (double) Short.MAX_VALUE;
          amp6 = 2.0 * currFFT[0] / (double) Short.MAX_VALUE;
        }
        else {
          amp0 = amp1 = amp2 = amp3 = amp4 = amp5 = amp6 = 0.0;
        }
        Flame flame = controller.getFlame();

        //        flame.setBGColorRed(200);
        //        flame.setBGColorGreen(225);
        //        flame.setBGColorBlue(255);
        {
          XForm xForm = xForm0.makeCopy();
          XFormTransformService.rotate(xForm, -amp0 * 7, false);
          XFormTransformService.localTranslate(xForm, -amp1 * 0.5, amp2 * 0.5);
          flame.getXForms().set(0, xForm);
        }
        {
          XForm xForm = xForm1.makeCopy();
          XFormTransformService.rotate(xForm, -amp3 * 2, false);
          XFormTransformService.localTranslate(xForm, amp4 * 0.25, -amp5 * 0.25);
          flame.getXForms().set(1, xForm);
        }
        {
          XForm xForm = xFormF.makeCopy();
          XFormTransformService.rotate(xForm, alpha, false);
          alpha += amp6;
          if (alpha > 360)
            alpha -= 360;
          XFormTransformService.scale(xForm, 1.0 + amp0 * 0.1, true, true, false);
          flame.setFinalXForm(xForm);
        }
        controller.refreshFlameImage(flame, doDrawFFT);
      }
    }
    finally {
      done = true;
    }
  }

  private double amp0, amp1, amp2, amp3, amp4, amp5, amp6;

  public void notifyFlameChange(Flame pFlame) {
    xForm0 = pFlame.getXForms().get(0);
    xForm1 = pFlame.getXForms().get(1);
    xFormF = pFlame.getFinalXForm();
    if (xFormF == null) {
      xFormF = new XForm();
      xFormF.addVariation(1.0, VariationFuncList.getVariationFuncInstance("linear3D"));
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
}
