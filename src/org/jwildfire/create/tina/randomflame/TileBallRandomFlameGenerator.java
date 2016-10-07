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
/*
/*
Inspired by the 3D Tile Ball Script made by Shortgreenpigg 
http://shortgreenpigg.deviantart.com/art/3D-Tile-Ball-Script-82517500
 
//Script by Shortgreenpigg
//shortgreenpigg.deviantart.com

cl := 0;
If Not InputQuery('Tile Ball', 'Clear current flame? (1 - Clear, 0 - Overlay)', cl) then Exit
If Not cl=1 then cl := 0
If cl=1 then Clear

a := 0.5;
If Not InputQuery('Tile Ball', 'Tile Spacing', a) then Exit



AddTransform;
with Transform do
begin
 linear3D := 1;
 ztranslate := 1.5 + random*1.5;
 Weight := 0.5;
 color := random
 symmetry := 1

end;

AddTransform;
with Transform do
begin
 symmetry := 1
 translate(0,a)
end;

AddTransform;
with Transform do
begin
 symmetry := 1
 translate(a,a)
end;

AddTransform;
with Transform do
begin
 symmetry := 1
 translate(a,0)
end;

AddTransform;
with Transform do
begin
 symmetry := 1
 translate(a,-a)
end;

AddTransform;          
with Transform do
begin
 symmetry := 1
 translate(0,-a)
end;

AddTransform;
with Transform do
begin
 symmetry := 1
 translate(-a,-a)
end;

AddTransform;
with Transform do
begin
 symmetry := 1
 translate(-a,0)
end;

AddTransform;
with Transform do
begin
 symmetry := 1
 translate(-a,a)
end;


//final Transform
SetActiveTransform(transforms);
with Transform do

 transform.linear3D := 0;
 Transform.curl3D := 1.0;
 transform.curl3D_cx := 0
 transform.curl3D_cy := 0.8 + random*0.4
 transform.curl3D_cz := 0.025 + random*0.05  

 Transform.Symmetry := 1;
 Flame.FinalXformEnabled := true;
                                          
Flame.x := 0.3+random*0.3;
Flame.y := -0.55 +random*0.3;
Flame.scale := 30 + random*10;
flame.perspective := 0.2
*/

package org.jwildfire.create.tina.randomflame;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class TileBallRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    flame.setCentreX(0.1 - Math.random() * 0.2);
    flame.setCentreY(-0.6 + Math.random() * 0.3);
    flame.setPixelsPerUnit(200);
    flame.setCamZoom(1.0 + Math.random() * 0.5);
    flame.setCamPitch(25.0 + Math.random() * 40.0);
    flame.setCamPerspective(0.1 + Math.random() * 0.2);
    flame.setPreserveZ(true);
    Layer layer = flame.getFirstLayer();
    layer.getFinalXForms().clear();
    layer.getXForms().clear();

    double tileSpacing = 0.5;
    // 1st xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance("linear3D", true));
      xForm.addVariation(1.5 + Math.random() * 1.5, VariationFuncList.getVariationFuncInstance("ztranslate", true));
      xForm.setColor(Math.random());
      xForm.setColorSymmetry(1.0);
      xForm.getModifiedWeights()[10] = 0.0;
    }
    // 2nd xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance("linear3D", true));
      xForm.setColorSymmetry(1.0);
      XFormTransformService.localTranslate(xForm, 0, tileSpacing);
      xForm.getModifiedWeights()[10] = 0.0;
    }
    // 3rd xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance("linear3D", true));
      xForm.setColorSymmetry(1.0);
      XFormTransformService.localTranslate(xForm, tileSpacing, tileSpacing);
      xForm.getModifiedWeights()[10] = 0.0;
    }
    // 4th xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance("linear3D", true));
      xForm.setColorSymmetry(1.0);
      XFormTransformService.localTranslate(xForm, tileSpacing, 0);
      xForm.getModifiedWeights()[10] = 0.0;
    }
    // 5th xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance("linear3D", true));
      xForm.setColorSymmetry(1.0);
      XFormTransformService.localTranslate(xForm, tileSpacing, -tileSpacing);
      xForm.getModifiedWeights()[10] = 0.0;
    }
    // 6th xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance("linear3D", true));
      xForm.setColorSymmetry(1.0);
      XFormTransformService.localTranslate(xForm, 0, -tileSpacing);
      xForm.getModifiedWeights()[10] = 0.0;
    }
    // 7th xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance("linear3D", true));
      xForm.setColorSymmetry(1.0);
      XFormTransformService.localTranslate(xForm, -tileSpacing, -tileSpacing);
      xForm.getModifiedWeights()[10] = 0.0;
    }
    // 8th xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance("linear3D", true));
      xForm.setColorSymmetry(1.0);
      XFormTransformService.localTranslate(xForm, -tileSpacing, 0);
      xForm.getModifiedWeights()[10] = 0.0;
    }
    // 9th xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance("linear3D", true));
      xForm.setColorSymmetry(1.0);
      XFormTransformService.localTranslate(xForm, -tileSpacing, tileSpacing);
      xForm.getModifiedWeights()[10] = 0.0;
    }
    // 10th xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.25 + Math.random() * 1.25);
      VariationFunc var = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
      xForm.addVariation(0.2 + Math.random() * 0.6, var);
      randomizeParams(var);
      xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance("flatten", true));
      xForm.setColor(Math.random());
      xForm.setColorSymmetry(Math.random());

      xForm.getModifiedWeights()[0] = 0.0;
      xForm.getModifiedWeights()[1] = 0.0;
      xForm.getModifiedWeights()[2] = 0.0;
      xForm.getModifiedWeights()[3] = 0.0;
      xForm.getModifiedWeights()[4] = 0.0;
      xForm.getModifiedWeights()[5] = 0.0;
      xForm.getModifiedWeights()[6] = 0.0;
      xForm.getModifiedWeights()[7] = 0.0;
      xForm.getModifiedWeights()[8] = 0.0;
      xForm.getModifiedWeights()[9] = 0.0;
      xForm.getModifiedWeights()[10] = 1.0;
    }
    // 11st xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.25 + Math.random() * 1.25);
      VariationFunc var = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
      xForm.addVariation(0.1 + Math.random() * 0.3, var);
      randomizeParams(var);
      xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance("flatten", true));
      xForm.setColor(Math.random());
      xForm.setColorSymmetry(Math.random());
      xForm.getModifiedWeights()[10] = 0.0;
    }

    // final xForm
    {
      XForm xForm = new XForm();
      layer.getFinalXForms().add(xForm);
      xForm.setWeight(0.8 + Math.random() * 0.5);
      VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("curl3D", true);
      varFunc.setParameter("cx", -0.75 + Math.random() * 1.5);
      varFunc.setParameter("cy", 0.8 + Math.random() * 0.4);
      varFunc.setParameter("cz", 0.025 + Math.random() * 0.05);
      xForm.addVariation(1.0, varFunc);
    }

    return flame;
  }

  private void randomizeParams(VariationFunc var) {
    int rndParams = Tools.FTOI(Math.random() * var.getParameterNames().length);
    for (int i = 0; i < rndParams; i++) {
      int idx = (int) (Math.random() * var.getParameterNames().length);
      Object oldVal = var.getParameterValues()[idx];
      double pAmount = 0.1 + Math.random();
      if (oldVal instanceof Integer) {
        int o = (Integer) oldVal;
        int da = Tools.FTOI(pAmount);
        if (da < 1) {
          da = 1;
        }
        if (o >= 0) {
          o += da;
        }
        else {
          o -= da;
        }
        var.setParameter(var.getParameterNames()[idx], o);
      }
      else if (oldVal instanceof Double) {
        double o = (Double) oldVal;
        if (o < EPSILON || Math.random() < 0.3) {
          if (o >= 0) {
            o += 0.1 * pAmount;
          }
          else {
            o -= 0.1 * pAmount;
          }
        }
        else {
          if (o >= 0) {
            o += o / 100.0 * pAmount;
          }
          else {
            o -= o / 100.0 * pAmount;
          }
        }
        var.setParameter(var.getParameterNames()[idx], o);
      }

    }
  }

  @Override
  public String getName() {
    return "Tile Ball";
  }

  @Override
  public boolean isUseFilter(RandomFlameGeneratorState pState) {
    return false;
  }

  @Override
  protected Flame postProcessFlameBeforeRendering(RandomFlameGeneratorState pState, Flame pFlame) {
    return pFlame;
  }

  @Override
  public boolean supportsSymmetry() {
    return false;
  }

  @Override
  protected Flame postProcessFlameAfterRendering(RandomFlameGeneratorState pState, Flame pFlame) {
    return pFlame;
  }
}
