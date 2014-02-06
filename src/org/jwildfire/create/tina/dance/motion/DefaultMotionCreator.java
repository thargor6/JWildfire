/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

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
package org.jwildfire.create.tina.dance.motion;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.dance.DancingFlameProject;
import org.jwildfire.create.tina.dance.model.AnimationModelService;
import org.jwildfire.create.tina.dance.model.FlamePropertyPath;
import org.jwildfire.create.tina.dance.model.PropertyModel;

public class DefaultMotionCreator implements MotionCreator {

  @Override
  public void createMotions(DancingFlameProject pProject) {
    pProject.getMotions().clear();

    FFTMotion amp0 = new FFTMotion();
    amp0.setFftChannel(0);
    amp0.setAmplitude(-7.0);

    FFTMotion amp1 = new FFTMotion();
    amp1.setFftChannel(1);
    amp1.setAmplitude(-0.5);

    FFTMotion amp2 = new FFTMotion();
    amp2.setFftChannel(2);
    amp2.setAmplitude(-0.5);

    FFTMotion amp3 = new FFTMotion();
    amp3.setFftChannel(3);
    amp3.setAmplitude(-2.0);

    FFTMotion amp4 = new FFTMotion();
    amp4.setFftChannel(4);
    amp4.setAmplitude(0.25);

    FFTMotion amp5 = new FFTMotion();
    amp5.setFftChannel(5);
    amp5.setAmplitude(-0.25);

    FFTMotion amp7 = new FFTMotion();
    amp7.setFftChannel(47);
    amp7.setAmplitude(5.0);

    FFTMotion amp8 = new FFTMotion();
    amp8.setFftChannel(48);
    amp8.setAmplitude(-0.75);

    FFTMotion amp9 = new FFTMotion();
    amp9.setFftChannel(49);
    amp9.setAmplitude(0.5);

    FFTMotion amp10 = new FFTMotion();
    amp10.setFftChannel(50);
    amp10.setAmplitude(3.0);

    FFTMotion amp11 = new FFTMotion();
    amp11.setFftChannel(51);
    amp11.setAmplitude(0.25);

    FFTMotion amp12 = new FFTMotion();
    amp12.setFftChannel(52);
    amp12.setAmplitude(0.25);

    FFTMotion amp14 = new FFTMotion();
    amp14.setFftChannel(2);
    amp14.setAmplitude(0.25);
    amp14.setOffset(1.0);

    pProject.getMotions().add(amp0);
    pProject.getMotions().add(amp1);
    pProject.getMotions().add(amp2);
    pProject.getMotions().add(amp3);
    pProject.getMotions().add(amp4);
    pProject.getMotions().add(amp5);
    pProject.getMotions().add(amp7);
    pProject.getMotions().add(amp8);
    pProject.getMotions().add(amp9);
    pProject.getMotions().add(amp10);
    pProject.getMotions().add(amp11);
    pProject.getMotions().add(amp12);
    pProject.getMotions().add(amp14);

    for (Flame flame : pProject.getFlames()) {
      PropertyModel model = AnimationModelService.createModel(flame);
      for (int layerIdx = 0; layerIdx < flame.getLayers().size(); layerIdx++) {
        Layer layer = flame.getLayers().get(layerIdx);
        if (layer.getXForms().size() > 0) {
          addXFormLink(model, amp0, flame, layerIdx, 0, AnimationModelService.PROPNAME_ANGLE);
          addXFormLink(model, amp1, flame, layerIdx, 0, AnimationModelService.PROPNAME_ORIGIN_X);
          addXFormLink(model, amp2, flame, layerIdx, 0, AnimationModelService.PROPNAME_ORIGIN_Y);
        }

        if (layer.getXForms().size() > 1) {
          addXFormLink(model, amp3, flame, layerIdx, 1, AnimationModelService.PROPNAME_ANGLE);
          addXFormLink(model, amp4, flame, layerIdx, 1, AnimationModelService.PROPNAME_ORIGIN_X);
          addXFormLink(model, amp5, flame, layerIdx, 1, AnimationModelService.PROPNAME_ORIGIN_Y);
        }

        if (layer.getXForms().size() > 2) {
          addXFormLink(model, amp7, flame, layerIdx, 2, AnimationModelService.PROPNAME_ANGLE);
          addXFormLink(model, amp8, flame, layerIdx, 2, AnimationModelService.PROPNAME_ORIGIN_X);
          addXFormLink(model, amp9, flame, layerIdx, 2, AnimationModelService.PROPNAME_ORIGIN_Y);
        }

        if (layer.getXForms().size() > 3) {
          addXFormLink(model, amp10, flame, layerIdx, 3, AnimationModelService.PROPNAME_ANGLE);
          addXFormLink(model, amp11, flame, layerIdx, 3, AnimationModelService.PROPNAME_ORIGIN_X);
          addXFormLink(model, amp12, flame, layerIdx, 3, AnimationModelService.PROPNAME_ORIGIN_Y);
        }

        if (layer.getFinalXForms().size() > 0) {
          addFinalXFormLink(model, amp14, flame, layerIdx, 0, AnimationModelService.PROPNAME_ZOOM);
        }
      }
    }
  }

  protected void addXFormLink(PropertyModel pModel, Motion pMotion, Flame pFlame, int pLayerIdx, int pXFormIndex, String pPropname) {
    FlamePropertyPath path = new FlamePropertyPath(pFlame, AnimationModelService.createXFormPropertyPath(pLayerIdx, pXFormIndex, pPropname));
    MotionLink link = new MotionLink(path);
    pMotion.getMotionLinks().add(link);
  }

  protected void addFinalXFormLink(PropertyModel pModel, Motion pMotion, Flame pFlame, int pLayerIdx, int pXFormIndex, String pPropname) {
    FlamePropertyPath path = new FlamePropertyPath(pFlame, AnimationModelService.createFinalXFormPropertyPath(pLayerIdx, pXFormIndex, pPropname));
    MotionLink link = new MotionLink(path);
    pMotion.getMotionLinks().add(link);
  }
}
