/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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
package org.jwildfire.create.tina.randomflame;

import org.jwildfire.create.tina.base.DrawMode;
import org.jwildfire.create.tina.base.EditPlane;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class SolidExperimentalRandomFlameGenerator extends SolidRandomFlameGenerator {

  public SolidExperimentalRandomFlameGenerator() {
  }

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCamRoll(0);
    flame.setCamPitch(90.0 - Math.random() * 180.0);
    flame.setCamYaw(30.0 - Math.random() * 60.0);
    flame.setCamBank(45.0 - Math.random() * 90.0);
    flame.setCamPerspective(Math.random() * 0.2);
    flame.setWidth(601);
    flame.setHeight(338);
    flame.setPixelsPerUnit(92.48366013);
    flame.setCamZoom(0.2 + Math.random() * 0.4);

    randomizeSolidRenderingSettings(flame);

    layer.getFinalXForms().clear();
    layer.getXForms().clear();

    // create transform 1
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(Math.random() < 0.3 ? 0.5 : 0.25 + Math.random());
      xForm.setColor(Math.random());
      xForm.setColorSymmetry(1.0 - 2 * Math.random());
      xForm.setMaterial(0);
      xForm.setMaterialSpeed(0);

      xForm.setXYCoeff00(0.58839186); // a
      xForm.setXYCoeff10(-0.93477004); // b
      xForm.setXYCoeff20(0.88339811); // e
      xForm.setXYCoeff01(0.93477004); // c
      xForm.setXYCoeff11(0.58839186); // d
      xForm.setXYCoeff21(0.59357541); // f

      xForm.setXYPostCoeff00(-1);
      xForm.setXYPostCoeff10(0.0000000000000001);
      xForm.setXYPostCoeff01(-0.0000000000000001);
      xForm.setXYPostCoeff11(-1);
      xForm.setXYPostCoeff20(0);
      xForm.setXYPostCoeff21(0);

      xForm.setYZCoeff00(1);
      xForm.setYZCoeff10(0);
      xForm.setYZCoeff20(0);
      xForm.setYZCoeff01(0);
      xForm.setYZCoeff11(1);
      xForm.setYZCoeff21(0);

      xForm.setYZPostCoeff00(-0.00074102);
      xForm.setYZPostCoeff10(0.99999973);
      xForm.setYZPostCoeff01(-0.99999973);
      xForm.setYZPostCoeff11(-0.00074102);
      xForm.setYZPostCoeff20(0);
      xForm.setYZPostCoeff21(0);

      xForm.setZXCoeff00(1);
      xForm.setZXCoeff10(0);
      xForm.setZXCoeff20(0);
      xForm.setZXCoeff01(0);
      xForm.setZXCoeff11(1);
      xForm.setZXCoeff21(0);

      xForm.setZXPostCoeff00(1);
      xForm.setZXPostCoeff10(0);
      xForm.setZXPostCoeff01(0);
      xForm.setZXPostCoeff11(1);
      xForm.setZXPostCoeff20(0);
      xForm.setZXPostCoeff21(0);

      // variation 1
      if (Math.random() < 0.85) {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("hypertile3D2", true);
        if (Math.random() > 0.75) {
          varFunc.setParameter("p", 4 + Math.random() * 3.0);
          varFunc.setParameter("q", 4 + Math.random() * 2.0);
        }
        else {
          varFunc.setParameter("p", 5);
          varFunc.setParameter("q", 4);
        }
        xForm.addVariation(0.25 + Math.random() * 0.75, varFunc);
      }
      else {
        xForm.addVariation(0.25 + Math.random() * 0.75, getRandomVariation());
      }
      // set default edit plane
      flame.setEditPlane(EditPlane.XY);
      // random affine transforms (uncomment to play around)
      //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, false);
      //   XFormTransformService.rotate(xForm, 360.0*Math.random(), false);
      //   XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), false);
      // random affine post transforms (uncomment to play around)
      //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, true);
      //   XFormTransformService.rotate(xForm, 360.0*Math.random(), true);
      //   XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), true);
    }
    // create transform 2
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.setColor(Math.random());
      xForm.setColorSymmetry(1.0 - 2 * Math.random());
      xForm.setMaterial(0);
      xForm.setMaterialSpeed(0);

      xForm.setCoeff00(1); // a
      xForm.setCoeff10(0); // b
      xForm.setCoeff20(0); // e
      xForm.setCoeff01(0); // c
      xForm.setCoeff11(1); // d
      xForm.setCoeff21(0); // f

      xForm.setPostCoeff00(1);
      xForm.setPostCoeff10(0);
      xForm.setPostCoeff01(0);
      xForm.setPostCoeff11(1);
      xForm.setPostCoeff20(0);
      xForm.setPostCoeff21(0);

      if (Math.random() > 0.125) {
        xForm.setDrawMode(DrawMode.HIDDEN);
      }

      // variation 1
      xForm.addVariation(0.2 + Math.random() * 0.5, getRandom3DShape());
      // set default edit plane
      flame.setEditPlane(Math.random() > 0.666 ? EditPlane.XY : Math.random() < 0.5 ? EditPlane.YZ : EditPlane.ZX);
      XFormTransformService.scale(xForm, 1.25 - Math.random() * 0.5, true, true, false);
      XFormTransformService.rotate(xForm, 360.0 * Math.random(), false);
      XFormTransformService.localTranslate(xForm, 1.0 - 2.0 * Math.random(), 1.0 - 2.0 * Math.random(), false);

      if (Math.random() > 0.5) {
        XFormTransformService.scale(xForm, 1.25 - Math.random() * 0.5, true, true, true);
        XFormTransformService.rotate(xForm, 360.0 * Math.random(), true);
        XFormTransformService.localTranslate(xForm, 1.0 - 2.0 * Math.random(), 1.0 - 2.0 * Math.random(), true);
      }
    }
    // create final transform 1
    if (Math.random() > 0.666) {
      XForm xForm = new XForm();
      layer.getFinalXForms().add(xForm);
      xForm.setWeight(0);
      xForm.setColor(0);
      xForm.setColorSymmetry(0);
      xForm.setMaterial(0);
      xForm.setMaterialSpeed(0);

      xForm.setCoeff00(1); // a
      xForm.setCoeff10(0); // b
      xForm.setCoeff20(0); // e
      xForm.setCoeff01(0); // c
      xForm.setCoeff11(1); // d
      xForm.setCoeff21(0); // f

      xForm.setPostCoeff00(1);
      xForm.setPostCoeff10(0);
      xForm.setPostCoeff01(0);
      xForm.setPostCoeff11(1);
      xForm.setPostCoeff20(0);
      xForm.setPostCoeff21(0);

      // variation 1
      {
        xForm.addVariation(0.75 + Math.random() * 0.25, getRandomVariation());
      }
      // variation 2
      if (Math.random() > 0.5) {
        xForm.addVariation(0.25 + Math.random() * 0.25, VariationFuncList.getVariationFuncInstance("linear3D", true));
      }
      // set default edit plane
      flame.setEditPlane(EditPlane.XY);
      if (Math.random() > 0.5) {
        XFormTransformService.scale(xForm, 1.25 - Math.random() * 0.5, true, true, false);
        XFormTransformService.rotate(xForm, 360.0 * Math.random(), false);
        XFormTransformService.localTranslate(xForm, 1.0 - 2.0 * Math.random(), 1.0 - 2.0 * Math.random(), false);
      }
      if (Math.random() > 0.5) {
        XFormTransformService.scale(xForm, 1.25 - Math.random() * 0.5, true, true, true);
        XFormTransformService.rotate(xForm, 360.0 * Math.random(), true);
        XFormTransformService.localTranslate(xForm, 1.0 - 2.0 * Math.random(), 1.0 - 2.0 * Math.random(), true);
      }
    }

    return flame;
  }

  @Override
  public String getName() {
    return "Solid (experimental)";
  }

  @Override
  public boolean isUseFilter(RandomFlameGeneratorState pState) {
    return false;
  }

  @Override
  protected Flame postProcessFlameAfterRendering(RandomFlameGeneratorState pState, Flame pFlame) {
    return pFlame;
  }
}
