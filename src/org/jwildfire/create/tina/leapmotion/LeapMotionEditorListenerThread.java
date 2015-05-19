/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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
package org.jwildfire.create.tina.leapmotion;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.swing.TinaController;

public class LeapMotionEditorListenerThread implements Runnable {
  private final TinaController tinaController;
  private boolean cancelSignalled;
  private Flame lastFlame = null;
  private final LeapMotionConnectedProperties config;

  private final Stack<LeapMotionEditorEvent> eventStack = new Stack<LeapMotionEditorEvent>();

  public LeapMotionEditorListenerThread(LeapMotionConnectedProperties pConfig, TinaController pTinaController) {
    config = pConfig;
    tinaController = pTinaController;
  }

  @Override
  public void run() {
    long t0 = System.currentTimeMillis();
    long frameCount = 0;
    while (!cancelSignalled) {
      try {
        if (!eventStack.isEmpty()) {
          List<LeapMotionEditorEvent> events = new ArrayList<LeapMotionEditorEvent>();
          while (!eventStack.isEmpty()) {
            events.add(eventStack.pop());
          }
          eventStack.clear();
          LeapMotionEditorEvent event = average(events);
          if (event != null) {
            Flame flame = tinaController.getCurrFlame();

            if (flame != lastFlame) {
              if (flame != null) {
                for (LeapMotionConnectedProperty property : config.getProperties()) {
                  property.init(flame);
                }
              }
              lastFlame = flame;
            }
            if (flame != null) {
              for (LeapMotionConnectedProperty property : config.getProperties()) {
                if (property.isEnabled()) {
                  property.apply(event, flame);
                }
              }
              tinaController.fastRefreshFlameImage(true, true, 1);
              frameCount++;
              if (frameCount % 25 == 0) {
                long t1 = System.currentTimeMillis();
                double t = (double) (t1 - t0) / 1000.0;
                System.out.println(frameCount + ", time=" + t + "s, fps=" + (double) frameCount / t);
              }
            }
          }
        }
        else {
          Thread.sleep(10);
        }
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private LeapMotionEditorEvent average(List<LeapMotionEditorEvent> pEvents) {
    if (pEvents != null && pEvents.size() > 0) {
      return pEvents.get(pEvents.size() / 2);
    }
    else {
      return null;
    }
  }

  public void signalCancel() {
    cancelSignalled = true;
  }

  public void signalEvent(LeapMotionEditorEvent pEvent) {
    eventStack.push(pEvent);
  }

}
