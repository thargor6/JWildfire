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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jwildfire.create.tina.audio.JLayerInterface;
import org.jwildfire.create.tina.audio.RecordedFFT;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.io.Flam3Writer;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.VariationFuncList;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ImagePanel;

public class RealtimeAnimRenderThread implements Runnable {
  private final DancingFractalsController controller;
  private boolean forceAbort;
  private boolean running;
  private XForm xForm0, xForm1, xForm2, xForm3, xFormF;
  private RecordedFFT fftData;
  private JLayerInterface musicPlayer;
  private ImagePanel fftPanel;
  private double finalXFormAlpha = 0.0;
  private int framesPerSecond = 12;
  private boolean drawTriangles = true;
  private long timeRenderStarted = 0;

  public RealtimeAnimRenderThread(DancingFractalsController pController) {
    controller = pController;
  }

  private double getFFTValue(short[] pFFT, int pIdx) {
    short val = pIdx < pFFT.length ? pFFT[pIdx] : 0;
    return 2.0 * val / (double) Short.MAX_VALUE;
  }

  @Override
  public void run() {
    running = forceAbort = false;
    finalXFormAlpha = 0.0;
    boolean doDrawFFT = true;
    // TODO unify
    try {
      long fpsMeasureMentFrameCount = 0;
      long startFPSMeasurement = System.currentTimeMillis();
      long nextFrame = timeRenderStarted = startFPSMeasurement;
      double fps = 0.0;
      running = true;
      while (!forceAbort) {
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
          if (!currIsMorphing) {
            transformXForm1(currFFT, flame);
            transformXForm2(currFFT, flame);
            transformXForm3(currFFT, flame);
            transformXForm4(currFFT, flame);
            transformXFormF(currFFT, flame);
          }
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
      running = false;
    }
  }

  private void transformXForm1(short[] pFFT, Flame pFlame) {
    if (xForm0 != null) {
      double amp0 = getFFTValue(pFFT, 0);
      System.out.println("TX: " + amp0);
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

  private void transformXForm4(short[] pFFT, Flame pFlame) {
    if (xForm3 != null) {
      double amp10 = getFFTValue(pFFT, 50);
      double amp11 = getFFTValue(pFFT, 51);
      double amp12 = getFFTValue(pFFT, 52);
      XForm xForm = xForm3.makeCopy();
      XFormTransformService.rotate(xForm, amp10 * 3, false);
      XFormTransformService.localTranslate(xForm, amp11 * 0.5, amp12 * 0.25);
      pFlame.getXForms().set(3, xForm);
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
  private Flame currFlame = null;
  private boolean currIsMorphing = false;

  public void changeFlame(Flame pFlame, boolean pIsMorphing) {
    flameChanging = true;
    try {
      if (pFlame != null) {
        xForm0 = pFlame.getXForms().get(0);
        xForm1 = pFlame.getXForms().size() > 1 ? pFlame.getXForms().get(1) : null;
        xForm2 = pFlame.getXForms().size() > 2 ? pFlame.getXForms().get(2) : null;
        xForm3 = pFlame.getXForms().size() > 3 ? pFlame.getXForms().get(3) : null;
        xFormF = pFlame.getFinalXForms().size() > 0 ? pFlame.getFinalXForms().get(pFlame.getFinalXForms().size() - 1) : null;
        if (xFormF == null) {
          xFormF = new XForm();
          xFormF.addVariation(1.0, VariationFuncList.getVariationFuncInstance("linear3D"));
        }
      }
      currIsMorphing = pIsMorphing;
      currFlame = pFlame;
    }
    finally {
      flameChanging = false;
    }
  }

  public boolean isDone() {
    return !running;
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

  private abstract class RecordedAction {
    protected long time;

    public long getTime() {
      return time;
    }
  }

  private class StartAction extends RecordedAction {
    private final Flame flame;

    public StartAction(Flame pFlame) {
      flame = pFlame;
      time = 0;
    }

    public Flame getFlame() {
      return flame;
    }
  }

  private class StopAction extends RecordedAction {
    public StopAction(long pTime) {
      time = pTime;
    }
  }

  private class FlameChangeAction extends RecordedAction {
    private final Flame flame;
    private final int morphFrameCount;

    public FlameChangeAction(long pTime, Flame pFlame, int pMorphFrameCount) {
      flame = pFlame;
      time = pTime;
      morphFrameCount = pMorphFrameCount;
    }

    public Flame getFlame() {
      return flame;
    }

    public int getMorphFrameCount() {
      return morphFrameCount;
    }
  }

  private List<RecordedAction> recordedActions = new ArrayList<RecordedAction>();

  public void recordFlameChange(Flame pFlame, int pMorphFrameCount) {
    if (!running) {
      recordedActions.clear();
      recordedActions.add(new StartAction(pFlame));
    }
    else {
      recordedActions.add(new FlameChangeAction(System.currentTimeMillis() - timeRenderStarted, pFlame, pMorphFrameCount));
    }

    System.out.println(recordedActions.get(recordedActions.size() - 1).getTime());
  }

  public void recordStop() {
    recordedActions.add(new StopAction(System.currentTimeMillis() - timeRenderStarted));
  }

  public void createRecordedFlameFiles(String pAbsolutePath) throws Exception {
    if (recordedActions.size() >= 2) {
      int actionIdx = 0;
      StartAction startAction = (StartAction) recordedActions.get(actionIdx++);
      Flame currFlame = startAction.getFlame();
      changeFlame(currFlame, false);
      List<Flame> flames = new ArrayList<Flame>();
      flames.add(currFlame);

      RecordedAction nextAction = recordedActions.get(actionIdx++);
      long timeRenderStarted = System.currentTimeMillis();
      long nextFrame = (long) (timeRenderStarted + 1000.0 / (double) framesPerSecond + 0.5);
      while (true) {
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
        if (fftData != null) {
          short currFFT[] = fftData.getDataByTimeOffset(time - timeRenderStarted);
          if (!currIsMorphing) {
            transformXForm1(currFFT, flame);
            transformXForm2(currFFT, flame);
            transformXForm3(currFFT, flame);
            transformXForm4(currFFT, flame);
            transformXFormF(currFFT, flame);
          }
        }

        //
        flame.setBGTransparency(false);
        flame.setGamma(1.6);
        //

        flames.add(flame.makeCopy());
        if (time >= timeRenderStarted + nextAction.getTime()) {
          if (nextAction instanceof StopAction) {
            break;
          }
          else if (nextAction instanceof FlameChangeAction) {
            currFlame = ((FlameChangeAction) nextAction).getFlame();
            changeFlame(currFlame, false);
            nextAction = recordedActions.get(actionIdx++);
          }
          else {
            throw new Exception("Unknown action type <" + nextAction.getClass() + ">");
          }
        }
      }

      System.out.println("flames: " + flames.size() + " (fps: " + framesPerSecond + ")");

      if (flames.size() > 0) {
        File file = new File(pAbsolutePath);
        String fn = file.getName();
        {
          int p = fn.indexOf(".flame");
          if (p > 0 && p == fn.length() - 6) {
            fn = fn.substring(0, p);
          }
        }

        int fileIdx = 1;
        for (Flame flame : flames) {
          String hs = String.valueOf(fileIdx++);
          while (hs.length() < 5) {
            hs = "0" + hs;
          }
          new Flam3Writer().writeFlame(flame, new File(file.getParent(), fn + hs + ".flame").getAbsolutePath());
        }
      }
      else {
        throw new Exception("No flame files where created");
      }

    }
    else {
      throw new Exception("No valid recording");
    }
  }
}
