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
    FFTMotion amp2 = new FFTMotion();
    amp2.setFftChannel(2);
    pProject.getMotions().add(amp0);
    pProject.getMotions().add(amp1);
    pProject.getMotions().add(amp2);

    FFTMotion amp3 = new FFTMotion();
    amp3.setFftChannel(3);
    FFTMotion amp4 = new FFTMotion();
    amp4.setFftChannel(4);
    FFTMotion amp5 = new FFTMotion();
    amp5.setFftChannel(5);
    pProject.getMotions().add(amp3);
    pProject.getMotions().add(amp4);
    pProject.getMotions().add(amp5);

    FFTMotion amp7 = new FFTMotion();
    amp7.setFftChannel(47);
    FFTMotion amp8 = new FFTMotion();
    amp8.setFftChannel(48);
    FFTMotion amp9 = new FFTMotion();
    amp9.setFftChannel(49);
    pProject.getMotions().add(amp7);
    pProject.getMotions().add(amp8);
    pProject.getMotions().add(amp9);

    FFTMotion amp10 = new FFTMotion();
    amp10.setFftChannel(50);
    FFTMotion amp11 = new FFTMotion();
    amp11.setFftChannel(51);
    FFTMotion amp12 = new FFTMotion();
    amp12.setFftChannel(52);
    pProject.getMotions().add(amp10);
    pProject.getMotions().add(amp11);
    pProject.getMotions().add(amp12);

    FFTMotion amp14 = new FFTMotion();
    amp14.setFftChannel(2);
    FFTMotion amp15 = new FFTMotion();
    amp15.setFftChannel(6);
    pProject.getMotions().add(amp14);
    pProject.getMotions().add(amp15);

    for (Flame flame : pProject.getFlames()) {
      PropertyModel model = AnimationModelService.createModel(flame);

      if (flame.getXForms().size() > 0) {
        addXFormLink(model, amp0, flame, 0, AnimationModelService.PROPNAME_ANGLE);
        //        XFormTransformService.rotate(xForm, -amp0 * 7, false);
        //        XFormTransformService.localTranslate(xForm, -amp1 * 0.5, amp2 * 0.5);

      }

      if (flame.getXForms().size() > 1) {

        //        XFormTransformService.rotate(xForm, -amp3 * 2, false);
        //        XFormTransformService.localTranslate(xForm, amp4 * 0.25, -amp5 * 0.25);

      }

      if (flame.getXForms().size() > 2) {

        //        XFormTransformService.rotate(xForm, amp7 * 5, false);
        //        XFormTransformService.localTranslate(xForm, -amp8 * 0.75, amp9 * 0.5);

      }

      if (flame.getXForms().size() > 3) {

        //        XFormTransformService.rotate(xForm, amp10 * 3, false);
        //        XFormTransformService.localTranslate(xForm, amp11 * 0.5, amp12 * 0.25);

      }

      if (flame.getFinalXForms().size() > 0) {

        //        XForm xForm = pFlame.getFinalXForms().get(pFlame.getFinalXForms().size() - 1);
        //        finalXFormAlpha += amp6;
        //        if (finalXFormAlpha > 360)
        //          finalXFormAlpha -= 360;
        //        XFormTransformService.scale(xForm, 1.0 + (amp2 + amp6) * 0.25, true, true, false);

      }

    }
  }

  protected void addXFormLink(PropertyModel pModel, Motion pMotion, Flame pFlame, int pXFormIndex, String pPropname) {
    FlamePropertyPath path = new FlamePropertyPath(pFlame, AnimationModelService.createXFormPropertyPath(pXFormIndex, pPropname));
    MotionLink link = new MotionLink(path);
    pMotion.getMotionLinks().add(link);
  }
}
