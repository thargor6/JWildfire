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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.EditPlane;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class SolidJulia3DRandomFlameGenerator extends SolidRandomFlameGenerator {

  public SolidJulia3DRandomFlameGenerator() {
  }

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCamRoll(0);
    flame.setCamPitch(90.0 - Math.random() * 180.0);
    flame.setCamYaw(30.0 - Math.random() * 60.0);
    flame.setCamBank(15.0 - Math.random() * 30.0);
    flame.setCamPerspective(Math.random() * 0.2);
    flame.setWidth(601);
    flame.setHeight(338);
    flame.setPixelsPerUnit(92.48366013);
    flame.setCamZoom(0.3 + Math.random() * 0.5);

    randomizeSolidRenderingSettings(flame);

    layer.getFinalXForms().clear();
    layer.getXForms().clear();

    // create transform 1
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.setColor(0.16556899);
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
        xForm.addVariation(0.5 + Math.random() * 2.0, getRandom3DShape());
      }
      // variation 2
      if (Math.random() > 0.75) {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("pre_wave3D_wf", true);
        varFunc.setParameter("wavelen", 0.75 + Math.random() * 0.5);
        varFunc.setParameter("phase", 0.5 + Math.random());
        varFunc.setParameter("damping", 0.01);
        Variation variation = xForm.addVariation(0.2 + Math.random() * 0.2, varFunc);
        variation.setPriority(0);
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
      xForm.setColor(0.02637356);
      xForm.setColorSymmetry(0);
      xForm.setMaterial(0);
      xForm.setMaterialSpeed(0);

      xForm.setXYCoeff00(0.53041695); // a
      xForm.setXYCoeff10(-0.14810786); // b
      xForm.setXYCoeff20(2.09422612); // e
      xForm.setXYCoeff01(0.14810786); // c
      xForm.setXYCoeff11(0.53041695); // d
      xForm.setXYCoeff21(0.23893855); // f

      xForm.setXYPostCoeff00(1);
      xForm.setXYPostCoeff10(0);
      xForm.setXYPostCoeff01(0);
      xForm.setXYPostCoeff11(1);
      xForm.setXYPostCoeff20(0);
      xForm.setXYPostCoeff21(0);

      xForm.setYZCoeff00(1);
      xForm.setYZCoeff10(0);
      xForm.setYZCoeff20(0.01405521);
      xForm.setYZCoeff01(0);
      xForm.setYZCoeff11(1);
      xForm.setYZCoeff21(-0.01405521);

      xForm.setYZPostCoeff00(1);
      xForm.setYZPostCoeff10(0);
      xForm.setYZPostCoeff01(0);
      xForm.setYZPostCoeff11(1);
      xForm.setYZPostCoeff20(0);
      xForm.setYZPostCoeff21(0);

      xForm.setZXCoeff00(0.74081053);
      xForm.setZXCoeff10(-0.04366518);
      xForm.setZXCoeff20(-0.07027604);
      xForm.setZXCoeff01(0.04366518);
      xForm.setZXCoeff11(0.74081053);
      xForm.setZXCoeff21(-0.14055209);

      xForm.setZXPostCoeff00(1);
      xForm.setZXPostCoeff10(0);
      xForm.setZXPostCoeff01(0);
      xForm.setZXPostCoeff11(1);
      xForm.setZXPostCoeff20(0);
      xForm.setZXPostCoeff21(0);

      // variation 1
      xForm.addVariation(1, VariationFuncList.getVariationFuncInstance("linear3D", true));
      // variation 2
      xForm.addVariation(-0.524, VariationFuncList.getVariationFuncInstance("zscale", true));
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
    // create final transform 1
    {
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
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("julia3Dq", true);
        varFunc.setParameter("power", Tools.FTOI(3 + Math.random() * 4.0));
        varFunc.setParameter("divisor", Math.random() > 0.5 ? 2 : 1.0 + Math.random() * 2.0);
        xForm.addVariation(1, varFunc);
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

    return flame;
  }

  @Override
  public String getName() {
    return "Solid Julia3D";
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
