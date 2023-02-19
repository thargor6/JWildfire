/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2022 Andreas Maschke

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

import org.jwildfire.create.tina.base.*;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class SolidrecursiveRandomFlameGenerator extends SolidRandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCamRoll(0);
    flame.setCamPitch(90.0 - Math.random() * 180.0);
    flame.setCamYaw(30.0 - Math.random() * 60.0);
    flame.setCamBank(30.0 - Math.random() * 60.0);
    flame.setCamPerspective(Math.random() * 0.2);
    flame.setWidth(601);
    flame.setHeight(338);
    flame.setPixelsPerUnit(100.0 + Math.random() * 100.0);
    flame.setCamZoom(0.5 + Math.random() * 0.25);

    randomizeSolidRenderingSettings(flame);

    layer.getFinalXForms().clear();
    layer.getXForms().clear();

    // create transform 1
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
      xForm.addVariation( Math.random() < 0.75 ? 0.1 + Math.random() * 0.3 : 0.2 + Math.random()*0.5, getRandom3DShape());
      // set default edit plane
      flame.setEditPlane(
          Math.random() > 0.666 ? EditPlane.XY : Math.random() < 0.5 ? EditPlane.YZ : EditPlane.ZX);
      XFormTransformService.scale(xForm, 1.25 - Math.random() * 0.5, true, true, false);
      XFormTransformService.rotate(xForm, 360.0 * Math.random(), false);
      XFormTransformService.localTranslate(
          xForm, 1.0 - 2.0 * Math.random(), 1.0 - 2.0 * Math.random(), false);

      if (Math.random() > 0.5) {
        XFormTransformService.scale(xForm, 1.25 - Math.random() * 0.5, true, true, true);
        XFormTransformService.rotate(xForm, 360.0 * Math.random(), true);
        XFormTransformService.localTranslate(
            xForm, 1.0 - 2.0 * Math.random(), 1.0 - 2.0 * Math.random(), true);
      }
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

      // variation 1
      {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("affine3D", true);
        varFunc.setParameter("translateX", 0.5 + Math.random() * 2.0);
        varFunc.setParameter("translateY", 0.25 + Math.random());
        varFunc.setParameter("translateZ", 0);
        double scl = 0.55 + Math.random() * 0.2;
        varFunc.setParameter("scaleX", scl);
        varFunc.setParameter("scaleY", scl);
        varFunc.setParameter("scaleZ", scl);
        varFunc.setParameter("rotateX", Math.random() * 4.0);
        varFunc.setParameter("rotateY", 0);
        varFunc.setParameter("rotateZ", Math.random() * 4.0);
        varFunc.setParameter("shearXY", 0);
        varFunc.setParameter("shearXZ", 0);
        varFunc.setParameter("shearYX", 0);
        varFunc.setParameter("shearYZ", 0);
        varFunc.setParameter("shearZX", 0);
        varFunc.setParameter("shearZY", 0);
        xForm.addVariation(1.0, varFunc);
      }
    }
    // create transform 3
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.setColor(Math.random());
      xForm.setColorSymmetry(1.0 - 2 * Math.random());
      xForm.setDrawMode(DrawMode.NORMAL);
      xForm.setMaterial(0);
      xForm.setMaterialSpeed(0);

      // variation 1
      {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("affine3D", true);
        varFunc.setParameter("translateX", 0.1 + Math.random() * 0.2);
        varFunc.setParameter("translateY", 0.5 + Math.random() * 3.0);
        varFunc.setParameter("translateZ", 0.35 + Math.random());
        double scl = 0.45 + Math.random() * 0.3;
        varFunc.setParameter("scaleX", scl);
        varFunc.setParameter("scaleY", scl);
        varFunc.setParameter("scaleZ", scl);
        varFunc.setParameter("rotateX", 1.0 + Math.random() * 5.0);
        varFunc.setParameter("rotateY", 25.0 - 50.0 * Math.random());
        varFunc.setParameter("rotateZ", 2.0 - 4.0 * Math.random());
        varFunc.setParameter("shearXY", 0);
        varFunc.setParameter("shearXZ", 0);
        varFunc.setParameter("shearYX", 0);
        varFunc.setParameter("shearYZ", 0);
        varFunc.setParameter("shearZX", 0);
        varFunc.setParameter("shearZY", 0);
        xForm.addVariation(1, varFunc);
      }
    }
    // create transform 4
    if(Math.random()<0.25) {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.setColor(Math.random());
      xForm.setColorSymmetry(1.0 - 2 * Math.random());
      xForm.setDrawMode(DrawMode.NORMAL);
      xForm.setMaterial(0);
      xForm.setMaterialSpeed(0);

      // variation 1
      {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("affine3D", true);
        varFunc.setParameter("translateX", 0.5 - Math.random());
        varFunc.setParameter("translateY", 0.5 - Math.random());
        varFunc.setParameter("translateZ", 0.5 - Math.random());
        double scl = 0.35 + Math.random() * 0.2;
        varFunc.setParameter("scaleX", scl);
        varFunc.setParameter("scaleY", scl);
        varFunc.setParameter("scaleZ", scl);
        varFunc.setParameter("rotateX", 3.0 - Math.random() * 6.0);
        varFunc.setParameter("rotateY", 3.0 - Math.random() * 6.0);
        varFunc.setParameter("rotateZ", 3.0 - Math.random() * 6.0);
        varFunc.setParameter("shearXY", 0);
        varFunc.setParameter("shearXZ", 0);
        varFunc.setParameter("shearYX", 0);
        varFunc.setParameter("shearYZ", 0);
        varFunc.setParameter("shearZX", 0);
        varFunc.setParameter("shearZY", 0);
        xForm.addVariation(1, varFunc);
      }
    }
    return flame;
  }

  @Override
  public String getName() {
    return "Solid (recursive)";
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
