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
import org.jwildfire.base.Unchecker;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.EditPlane;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.solidrender.ShadowType;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.create.tina.mutagen.RandomGradientMutation;
import org.jwildfire.create.tina.variation.SubFlameWFFunc;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class SolidLabyrinthRandomFlameGenerator extends SolidRandomFlameGenerator {

  public void embedSubFlame(Flame flame, Flame subFlame) {
    Layer layer = flame.getFirstLayer();
    layer.getFinalXForms().clear();
    layer.getXForms().clear();
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);

      try {
        SubFlameWFFunc var = new SubFlameWFFunc();
        String flameXML = new FlameWriter().getFlameXML(subFlame);
        var.setRessource("flame", flameXML.getBytes());
        var.setParameter("color_mode", 0);

        double cs = (1.5 - Math.random() * 3.0);
        if (MathLib.fabs(cs) < 0.4) {
          cs = (Math.random() < 0.5 ? -0.3 - Math.random() : 0.3 + Math.random());
        }

        var.setParameter("colorscale_z", cs);
        xForm.addVariation(1, var);
      }
      catch (Exception ex) {
        ex.printStackTrace();
        Unchecker.rethrow(ex);
      }
      xForm.setColor(0);
      xForm.setColorSymmetry(-0.22);
    }
  }

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCamRoll(0);
    flame.setCamPitch(30.0 + Math.random() * 40.0);
    flame.setCamYaw(30.0 - Math.random() * 60.0);
    flame.setCamBank(5.0 - Math.random() * 10.0);
    flame.setCamPerspective(Math.random() * 0.2);
    flame.setWidth(601);
    flame.setHeight(338);
    flame.setPixelsPerUnit(92.48366013);
    flame.setCamZoom(0.75 + Math.random() * 0.5);

    randomizeSolidRenderingSettings(flame);
    if(Math.random()<0.333) {
      // in many cases those flames look very well in non-solid-mode
      flame.getSolidRenderSettings().setSolidRenderingEnabled(false);
    }

    layer.getFinalXForms().clear();
    layer.getXForms().clear();

    embedSubFlame(flame, createSubFlame());

    return flame;
  }

  private Flame createSubFlame() {
    Flame flame = new Flame();
    flame.getLayers().clear();
    flame.setCamRoll(0);
    flame.setCamPitch(0);
    flame.setCamYaw(0);
    flame.setCamBank(0);
    flame.setCamPerspective(0);
    flame.setWidth(711);
    flame.setHeight(400);
    flame.setPixelsPerUnit(38.88427164);
    flame.setCamZoom(1);
    // create layer 1
    {
      Layer layer = new Layer();
      flame.getLayers().add(layer);
      layer.setWeight(1);
      layer.setVisible(true);
      // create a random gradient
      new RandomGradientMutation().execute(layer);
      // create transform 1
      {
        XForm xForm = new XForm();
        layer.getXForms().add(xForm);
        xForm.setWeight(0.5);
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

        if (Math.random() < 0.5) {
          if (Math.random() < 0.45) {
            VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("dc_crackle_wf", true);
            varFunc.setParameter("cellsize", 0.15 + Math.random() * 0.5);
            varFunc.setParameter("power", 0.2 + Math.random() * 1.2);
            varFunc.setParameter("distort", Math.random() > 0.2 ? Math.random() * 0.5 : 0.0);
            varFunc.setParameter("scale", Math.random() > 0.667 ? 1.06 - Math.random() * 0.12 : 1.0);
            varFunc.setParameter("z", Math.random() * 0.5);
            varFunc.setParameter("color_scale", 0.5);
            varFunc.setParameter("color_offset", 0);
            xForm.addVariation(1.0 + Math.random() * 2.0, varFunc);
          }
          else if (Math.random() < 0.45) {
            {
              VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("metaballs3d_wf", true);
              xForm.addVariation(0.15+Math.random()*0.75, varFunc);
            }
          }
          else {
            {
              VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("truchet", true);
              varFunc.setParameter("extended", Math.random() < 0.25 ? 1 : 0);
              varFunc.setParameter("exponent", 0.2 + Math.random() * 1.3);
              varFunc.setParameter("arc_width", 0.5 + Math.random() * 0.5);
              varFunc.setParameter("rotation", 0);
              varFunc.setParameter("size", 0.4 + Math.random() * 0.3);
              varFunc.setParameter("seed", 50.0 + Math.random() * 50.0);
              varFunc.setParameter("direct_color", 1);
              xForm.addVariation(1, varFunc);
            }
            xForm.addVariation(2.0 + Math.random() * 2.0, VariationFuncList.getVariationFuncInstance("pre_blur", true));
          }
        }
        else {
          VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("dc_perlin", true);
          varFunc.setParameter("shape", (int) (Math.random() * 3.0));

          varFunc.setParameter("map", Math.random() < 0.15 ? (int) (Math.random() * 6.0) : 2);
          varFunc.setParameter("select_centre", 0);
          varFunc.setParameter("select_range", 1);
          varFunc.setParameter("centre", 0.25 + Math.random() * 0.5);
          varFunc.setParameter("range", 0.25 + Math.random() * 0.5);
          varFunc.setParameter("edge", 0);
          varFunc.setParameter("scale", 1.0 + Math.random() * 5.0);
          varFunc.setParameter("octaves", Math.random() < 0.33 ? Tools.FTOI(2 + Math.random() * 2.0) : 2);
          varFunc.setParameter("amps", Math.random() < 0.33 ? Tools.FTOI(2 + Math.random() * 2.0) : 2);
          varFunc.setParameter("freqs", Math.random() < 0.33 ? Tools.FTOI(2 + Math.random() * 2.0) : 2);
          varFunc.setParameter("z", 0);
          varFunc.setParameter("select_bailout", 10);
          xForm.addVariation(3.0 + Math.random() * 4.0, varFunc);
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
    }
    return flame;
  }

  @Override
  public String getName() {
    return "Solid Labyrinth";
  }

  @Override
  public boolean isUseFilter(RandomFlameGeneratorState pState) {
    return false;
  }

  @Override
  protected Flame postProcessFlameAfterRendering(RandomFlameGeneratorState pState, Flame pFlame) {
    if (Math.random() > 0.25) {
      pFlame.getSolidRenderSettings().setShadowType(Math.random() > 0.25 ? ShadowType.FAST : ShadowType.SMOOTH);
    }
    return pFlame;
  }

  @Override
  public boolean supportsSymmetry() {
    return false;
  }
}
