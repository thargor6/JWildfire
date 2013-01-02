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
package org.jwildfire.create.tina.dance.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.audio.RecordedFFT;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.dance.FlamePreparer;
import org.jwildfire.create.tina.dance.FlameStack;
import org.jwildfire.create.tina.dance.RealtimeAnimRenderThread;
import org.jwildfire.create.tina.dance.motion.DanceFlameTransformer;
import org.jwildfire.create.tina.io.Flam3Writer;

public class PostRecordFlameGenerator {
  private final RealtimeAnimRenderThread thread;
  private final ActionRecorder recorder;
  private final Prefs prefs;
  private final RecordedFFT fftData;
  private final DanceFlameTransformer transformer;

  public PostRecordFlameGenerator(Prefs pPrefs, ActionRecorder pRecorder, RealtimeAnimRenderThread pThread, RecordedFFT pFFTData) {
    prefs = pPrefs;
    recorder = pRecorder;
    thread = pThread;
    fftData = pFFTData;
    transformer = new DanceFlameTransformer();
  }

  public void createRecordedFlameFiles(String pAbsolutePath) throws Exception {
    if (recorder.getRecordedActions().size() >= 2) {
      List<Flame> flames = new ArrayList<Flame>();
      int actionIdx = 0;

      StartAction startAction = (StartAction) recorder.getRecordedActions().get(actionIdx++);
      FlameStack flameStack = new FlameStack(prefs);
      flameStack.addFlame(startAction.getFlame(), 0);
      RecordedAction nextAction = recorder.getRecordedActions().get(actionIdx++);
      long timeRenderStarted = System.currentTimeMillis();
      long nextFrame = (long) (timeRenderStarted + 1000.0 / (double) thread.getFramesPerSecond() + 0.5);
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
        nextFrame = (long) (time + 1000.0 / (double) thread.getFramesPerSecond() + 0.5);

        Flame flame = flameStack.getFlame();

        if (fftData != null) {
          short currFFT[] = fftData.getDataByTimeOffset(time - timeRenderStarted);
          transformer.transformFlame(flame, currFFT);
        }

        new FlamePreparer(prefs).prepareFlame(flame);

        flames.add(flame.makeCopy());
        if (time >= timeRenderStarted + nextAction.getTime()) {
          if (nextAction instanceof StopAction) {
            break;
          }
          else if (nextAction instanceof FlameChangeAction) {
            flameStack.addFlame(((FlameChangeAction) nextAction).getFlame(), ((FlameChangeAction) nextAction).getMorphFrameCount());
            nextAction = recorder.getRecordedActions().get(actionIdx++);
          }
          else {
            throw new Exception("Unknown action type <" + nextAction.getClass() + ">");
          }
        }
      }

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
