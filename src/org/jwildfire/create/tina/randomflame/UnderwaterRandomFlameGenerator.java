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
import org.jwildfire.create.tina.base.*;
import org.jwildfire.create.tina.mutagen.RandomGradientMutation;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;
import org.jwildfire.create.tina.variation.VariationFuncType;

public class UnderwaterRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    // Bases loosely on the W2R Batch Script by parrotdolphin.deviantart.com */ 
    // create a new flame
    Flame flame=new Flame();
    flame.getLayers().clear(); // get rid of the default layer because we create all layers by ourselves
    // set the flame main attributes
    flame.setCamRoll(-90.0 + Math.random() * 180.0);
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setCamPitch(0);
    flame.setCamYaw(0);
    flame.setCamBank(0);
    flame.setCamPerspective(0);
    flame.setWidth(1920);
    flame.setHeight(1080);
    flame.setPixelsPerUnit(313.97436855);
    flame.setCamZoom(1);
    flame.setBGTransparency(false);
    // Uncomment setBrightness or setGamma if essential for the flame
    // flame.setBrightness(9.0);
    // flame.setGamma(2.8);
    flame.setPreserveZ(false);
    // create layer 1
    {
      Layer layer = new Layer();
      flame.getLayers().add(layer);
      layer.setWeight(1);
      layer.setDensity(1);
      layer.setVisible(true);
      // create a random gradient
      new RandomGradientMutation().execute(layer);
      // create transform 1
      {
        XForm xForm = new XForm();
        layer.getXForms().add(xForm);
        xForm.setWeight(0.32019788);
        xForm.setColor(0.075+ Math.random() * 0.15);
        xForm.setColorSymmetry(0.3 + Math.random() * 0.15);
        xForm.setDrawMode(DrawMode.NORMAL);
        xForm.setMaterial(0);
        xForm.setMaterialSpeed(0);

        xForm.setCoeff00(0.75092904); // a
        xForm.setCoeff10(0.56527515); // b
        xForm.setCoeff20(0.3204811); // e
        xForm.setCoeff01(-0.56527515); // c
        xForm.setCoeff11(0.75092904); // d
        xForm.setCoeff21(-0.98334041); // f

        xForm.setPostCoeff00(1);
        xForm.setPostCoeff10(0);
        xForm.setPostCoeff01(0);
        xForm.setPostCoeff11(1);
        xForm.setPostCoeff20(0);
        xForm.setPostCoeff21(0);

        // variation 1
        if (Math.random() > 0.333) {
          xForm.addVariation(
              0.41432335, VariationFuncList.getVariationFuncInstance("linear3D", true));
        }
        else {
          xForm.addVariation(
                  0.25 + Math.random() * 0.5,
                  VariationFuncList.getVariationFuncInstance(
                          VariationFuncList.getRandomVariationname(
                                  Math.random() < 0.65
                                          ? VariationFuncType.VARTYPE_2D
                                          : VariationFuncType.VARTYPE_3D)));
        }
        // variation 2
        {
          if(Math.random()>0.5) {
            VariationFunc varFunc=VariationFuncList.getVariationFuncInstance("eclipse", true);
            varFunc.setParameter("shift", 0);
            xForm.addVariation(0.22103179, varFunc);
          }
          else {
            xForm.addVariation(0.15 + Math.random()* 0.15, VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(Math.random() < 0.5 ? VariationFuncType.VARTYPE_2D: VariationFuncType.VARTYPE_3D)));
          }
        }
        // set default edit plane
        flame.setEditPlane(EditPlane.XY);
        // random affine transforms (uncomment to play around)
        //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, false);
        if (Math.random() < 0.5) {
          XFormTransformService.rotate(xForm, -30.0 + 60.0 * Math.random(), false);
        }
        else {
          XFormTransformService.localTranslate(xForm, -0.05 + 0.1*Math.random(), -0.05 + 0.1*Math.random(), false);
        }
      }
      // create transform 2
      {
        XForm xForm = new XForm();
        layer.getXForms().add(xForm);
        xForm.setWeight(Math.random() < 0.5 ? 0.64084507 : 0.5 + Math.random() * 0.3);
        xForm.setColor(0.8 + Math.random() * 0.1);
        xForm.setColorSymmetry(0.87685786);
        xForm.setDrawMode(DrawMode.NORMAL);
        xForm.setMaterial(0);
        xForm.setMaterialSpeed(0);

        xForm.setCoeff00(-0.36319594); // a
        xForm.setCoeff10(-0.64089754); // b
        xForm.setCoeff20(-0.3641259); // e
        xForm.setCoeff01(0.64089754); // c
        xForm.setCoeff11(-0.36319594); // d
        xForm.setCoeff21(0.40615455); // f

        xForm.setPostCoeff00(1);
        xForm.setPostCoeff10(0);
        xForm.setPostCoeff01(0);
        xForm.setPostCoeff11(1);
        xForm.setPostCoeff20(0);
        xForm.setPostCoeff21(0);

        // variation 1
        if (Math.random() > 0.42) {
          xForm.addVariation(
              0.21369106, VariationFuncList.getVariationFuncInstance("linear3D", true));
        }
        else {
          xForm.addVariation(
                  0.125 + Math.random() * 0.25,
                  VariationFuncList.getVariationFuncInstance(
                          VariationFuncList.getRandomVariationname(
                                  Math.random() < 0.65
                                          ? VariationFuncType.VARTYPE_2D
                                          : VariationFuncType.VARTYPE_3D)));

        }
        // variation 2
        xForm.addVariation(0.61264366, VariationFuncList.getVariationFuncInstance("foci", true));
        // variation 3
        if (Math.random() > 0.5) {
          xForm.addVariation(
              0.05 + Math.random() * 0.15,
              VariationFuncList.getVariationFuncInstance(
                  VariationFuncList.getRandomVariationname(
                      Math.random() < 0.5
                          ? VariationFuncType.VARTYPE_2D
                          : VariationFuncType.VARTYPE_3D)));
        }
        // set default edit plane
        flame.setEditPlane(EditPlane.XY);
        // random affine transforms (uncomment to play around)
        //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, false);
        if (Math.random() < 0.5) {
          XFormTransformService.rotate(xForm, -30.0 + 60.0 * Math.random(), false);
        }
        else {
          XFormTransformService.localTranslate(xForm, -0.15 + 0.3*Math.random(), -0.15 + 0.3*Math.random(), false);
        }
      }
      // create final transform
      if(Math.random() > 0.72)  {
        XForm xForm = new XForm();
        layer.getFinalXForms().add(xForm);
        xForm.setWeight(0);
        xForm.setColor(0.5);
        xForm.setColorSymmetry(0);
        xForm.setDrawMode(DrawMode.NORMAL);
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
        if (Math.random() > 0.666) {
          xForm.addVariation(1, VariationFuncList.getVariationFuncInstance("log", true));
        }
        else {
          xForm.addVariation(1, VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(Math.random() > 0.25 ? VariationFuncType.VARTYPE_2D: VariationFuncType.VARTYPE_3D)));
        }
        // set default edit plane
        flame.setEditPlane(EditPlane.XY);
        // random affine transforms (uncomment to play around)
        XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, false);
        XFormTransformService.rotate(xForm, 360.0*Math.random(), false);
        XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), false);
        // random affine post transforms (uncomment to play around)
        //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, true);
        //   XFormTransformService.rotate(xForm, 360.0*Math.random(), true);
        //   XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), true);
      }
      /*if(Math.random()>0.5)*/ {
      flame.setBgColorType(BGColorType.SINGLE_COLOR);
      int r, g, b;
      if(Math.random()<0.125) {
        r = 0;
        g = 102;
        b = 102;
      }
      else {
        if(Math.random()<0.5) {
          r = 0;
          g = (int)(50 + 102 *  Math.random());
          b = g;
        }
        else {
          r = 0;
          g = (int)(50 + 102 *  Math.random());
          b = (int)(50 + 102 *  Math.random());
        }
      }
      flame.setBgColorRed(r);
      flame.setBgColorGreen(g);
      flame.setBgColorBlue(b);
    }
/*
        else {
          flame.setBgColorType(BGColorType.GRADIENT_2X2);
          int r, g, b;
          if(Math.random()<0.25) {
            r = 0;
            g = 102;
            b = 102;
          }
          else {
            if(Math.random()<0.5) {
              r = 0;
              g = (int)(60 + 82 *  Math.random());
              b = g;
            }
            else {
              r = 0;
              g = (int)(60 + 82 *  Math.random());
              b = (int)(60 + 82 *  Math.random());
            }
          }
          flame.setBgColorULRed(r);
          flame.setBgColorULGreen(g);
          flame.setBgColorULBlue(b);
          flame.setBgColorURRed(r);
          flame.setBgColorURGreen(g);
          flame.setBgColorURBlue(b);

          double fade = 0.3 + Math.random() * 0.3;
          int lr = Tools.FTOI(r * fade);
          int lg = Tools.FTOI(g * fade);
          int lb = Tools.FTOI(b * fade);

          flame.setBgColorLLRed(lr);
          flame.setBgColorLLGreen(lg);
          flame.setBgColorLLBlue(lb);
          flame.setBgColorLRRed(lr);
          flame.setBgColorLRGreen(lg);
          flame.setBgColorLRBlue(lb);
        }
*/
    }
    return flame;
  }

  @Override
  public String getName() {
    return "Underwater";
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
  protected Flame postProcessFlameAfterRendering(RandomFlameGeneratorState pState, Flame pFlame) {
    return pFlame;
  }

  @Override
  public boolean supportsSymmetry() {
    return false;
  }
}
