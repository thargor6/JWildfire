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
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.create.tina.transform.XFormTransformService;

public class LeapMotionEditorListenerThread implements Runnable {
  private final TinaController tinaController;
  private boolean cancelSignalled;

  private final Stack<LeapMotionEditorEvent> eventStack = new Stack<LeapMotionEditorEvent>();

  public LeapMotionEditorListenerThread(TinaController pTinaController) {
    tinaController = pTinaController;
  }

  @Override
  public void run() {
    long t0 = System.currentTimeMillis();
    long frameCount = 0;
    while (!cancelSignalled) {
      try {
        if (!eventStack.isEmpty()) {
          //            LeapMotionEditorEvent event = eventStack.pop();
          //            eventStack.clear();

          List<LeapMotionEditorEvent> events = new ArrayList<LeapMotionEditorEvent>();
          while (!eventStack.isEmpty()) {
            events.add(eventStack.pop());
          }
          eventStack.clear();
          LeapMotionEditorEvent event = average(events);
          if (event != null) {
            Flame flame = tinaController.getCurrFlame();
            XForm xform = tinaController.getCurrXForm();
            if (xform == null && flame != null) {
              Layer layer = tinaController.getCurrLayer();
              if (layer == null) {
                layer = flame.getFirstLayer();
              }
              if (layer != null && layer.getXForms().size() > 0) {
                xform = layer.getXForms().get(0);
              }
            }
            if (flame != null && xform != null) {
              XFormTransformService.reset(xform, tinaController.data.affineEditPostTransformButton.isSelected());
              if (event.getRightHand() != null) {

                //                  flame.setCamRoll(event.getRightHand().getRoll());
                //                  flame.setCamPitch(event.getRightHand().getPitch());
                //                  flame.setCamYaw(event.getRightHand().getYaw());
                //
                XFormTransformService.rotate(xform, event.getRightHand().getRoll(), tinaController.data.affineEditPostTransformButton.isSelected());
                double SCALE_SCALE = 90.0;
                double scale = 1.0 + event.getRightHand().getPitch() / SCALE_SCALE;
                double POS_SCALE = 50.0;
                double POS_OFFSET_X = 0.0;
                double POS_OFFSET_Y = 1.5;
                double dx = event.getRightHand().getPosX() / POS_SCALE + POS_OFFSET_X;
                double dy = -event.getRightHand().getPosY() / POS_SCALE + POS_OFFSET_Y;
                if (tinaController.data.affineEditPostTransformButton.isSelected()) {
                  xform.setPostCoeff20(dx);
                  xform.setPostCoeff21(dy);
                }
                else {
                  xform.setCoeff20(dx);
                  xform.setCoeff21(dy);
                }
                XFormTransformService.scale(xform, scale, true, true, tinaController.data.affineEditPostTransformButton.isSelected());
              }
              if (event.getLeftHand() != null) {
                double dy = event.getLeftHand().getPosY() - 50;
                if (dy < -255.0)
                  dy = -255;
                else if (dy > 255)
                  dy = 255;
                System.out.println(dy);
                flame.getFirstLayer().getPalette().setModShift((int) dy);

              }
              tinaController.fastRefreshFlameImage(true, false, 1);
              frameCount++;
              if (frameCount % 30 == 0) {
                long t1 = System.currentTimeMillis();
                double t = (double) (t1 - t0) / 1000.0;
                System.out.println(frameCount + ", time=" + t + "s, fps=" + (double) frameCount / t);
              }
            }
          }
        }
        else {
          Thread.sleep(1);
        }
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  // TODO
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
