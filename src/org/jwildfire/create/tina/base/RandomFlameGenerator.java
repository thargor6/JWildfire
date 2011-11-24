/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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
package org.jwildfire.create.tina.base;

import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.Linear3DFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class RandomFlameGenerator {

  public Flame createFlame(RandomFlameGeneratorStyle pStyle) {
    switch (pStyle) {
      case V0:
        return createFlame_V0(false);
      case V1:
        return createFlame_V1(false);
      case DDD:
        return createFlame_3D(false);
      case V0_SYMM:
        return createFlame_V0(true);
      case V1_SYMM:
        return createFlame_V1(true);
      case DDD_SYMM:
        return createFlame_3D(true);
      case ALL: {
        double r = Math.random();
        if (r < 0.1) {
          return createFlame_V0(false);
        }
        else if (r < 0.2) {
          return createFlame_V0(true);
        }
        else if (r < 0.3) {
          return createFlame_V1(false);
        }
        else if (r < 0.4) {
          return createFlame_V1(true);
        }
        else if (r < 0.5) {
          return createFlame_3D(false);
        }
        else if (r < 0.6) {
          return createFlame_3D(true);
        }
        // always good default
        else {
          return createFlame_V1(false);
        }
      }
      default:
        throw new IllegalStateException();
    }
  }

  private Flame createFlame_V0(boolean pWithSymmetry) {
    Flame flame = new Flame();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setPixelsPerUnit(200);
    flame.setSpatialFilterRadius(1.0);
    flame.setFinalXForm(null);
    flame.getXForms().clear();

    int maxXForms = (int) (1.0 + Math.random() * 5.0);
    if (pWithSymmetry && maxXForms < 2) {
      maxXForms = 2;
    }
    double scl = 1.0;
    for (int i = 0; i < maxXForms; i++) {
      XForm xForm = new XForm();
      flame.getXForms().add(xForm);
      if (Math.random() < 0.5) {
        XFormTransformService.rotate(xForm, 360.0 * Math.random());
      }
      else {
        XFormTransformService.rotate(xForm, -360.0 * Math.random());
      }
      XFormTransformService.translate(xForm, Math.random() - 1.0, Math.random() - 1.0);
      scl *= 0.75 + Math.random() / 4;
      XFormTransformService.scale(xForm, scl);

      xForm.setColor(Math.random());
      xForm.addVariation(Math.random() * 0.8 + 0.2, new Linear3DFunc());
      if (Math.random() > 0.33) {
        String[] fnc = { "blur3D", "bubble", "curl3D", "diamond", "disc", "julia3D", "fan2", "heart",
                                 "julia3D", "hemisphere", "horseshoe", "blob3D", "julia3D", "pdj", "popcorn", "rings2",
                                 "spherical3D", "spiral", "rectangles", "blur", "waves", "swirl" };
        int fncIdx = (int) (Math.random() * fnc.length);
        xForm.addVariation(Math.random() * 0.5, VariationFuncList.getVariationFuncInstance(fnc[fncIdx]));
      }

      xForm.setWeight(Math.random() * 0.9 + 0.1);
    }
    if (pWithSymmetry) {
      addSymmetry(flame);
    }
    return flame;
  }

  private void addSymmetry(Flame pFlame) {
    //    int symmetry = 2 + (int) (2 * Math.random() + 0.5);
    int tCount = pFlame.getXForms().size();
    int symmetry = tCount;
    if (symmetry == 1) {
      return;
    }
    int i = 0;
    for (XForm xForm : pFlame.getXForms()) {
      double alpha = 2 * Math.PI / symmetry;
      xForm.setCoeff00(Math.cos(i * alpha));
      xForm.setCoeff01(Math.sin(i * alpha));
      xForm.setCoeff10(-xForm.getCoeff01());
      xForm.setCoeff11(xForm.getCoeff00());
      xForm.setCoeff20(0.0);
      xForm.setCoeff21(0.0);
      i++;
      if (i >= 3) {
        break;
      }
    }
  }

  private Flame createFlame_V1(boolean pWithSymmetry) {
    Flame flame = new Flame();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setPixelsPerUnit(200);
    flame.setSpatialFilterRadius(1.0);
    flame.setFinalXForm(null);
    flame.getXForms().clear();

    int maxXForms = (int) (1.0 + Math.random() * 5.0);
    if (pWithSymmetry && maxXForms < 2) {
      maxXForms = 2;
    }
    double scl = 1.0;
    for (int i = 0; i < maxXForms; i++) {
      XForm xForm = new XForm();
      flame.getXForms().add(xForm);
      if (Math.random() < 0.5) {
        XFormTransformService.rotate(xForm, 360.0 * Math.random());
      }
      else {
        XFormTransformService.rotate(xForm, -360.0 * Math.random());
      }
      XFormTransformService.translate(xForm, Math.random() - 1.0, Math.random() - 1.0);
      scl *= 0.75 + Math.random() / 4;
      XFormTransformService.scale(xForm, scl);

      xForm.setColor(Math.random());
      xForm.addVariation(Math.random() * 0.8 + 0.2, new Linear3DFunc());
      if (Math.random() > 0.33) {
        String[] fnc = { "blur3D", "bubble", "curl3D", "diamond", "disc", "butterfly3D", "julia3D", "fan2", "heart",
            "julia3D", "hemisphere", "horseshoe", "blob3D", "julia3D", "pie3D", "pdj", "popcorn", "rings2",
            "spherical3D", "spiral", "rectangles", "blur", "waves", "swirl" };
        int fncIdx = (int) (Math.random() * fnc.length);
        xForm.addVariation(Math.random() * 0.5, VariationFuncList.getVariationFuncInstance(fnc[fncIdx]));
      }

      xForm.setWeight(Math.random() * 0.9 + 0.1);
    }
    if (pWithSymmetry) {
      addSymmetry(flame);
    }
    return flame;
  }

  private Flame createFlame_3D(boolean pWithSymmetry) {
    Flame flame = new Flame();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setPixelsPerUnit(200);
    flame.setSpatialFilterRadius(1.0);
    flame.setFinalXForm(null);
    flame.getXForms().clear();

    int maxXForms = (int) (2.0 + Math.random() * 5.0);
    double scl = 1.0;
    for (int i = 0; i < maxXForms; i++) {
      XForm xForm = new XForm();
      flame.getXForms().add(xForm);
      if (Math.random() < 0.5) {
        XFormTransformService.rotate(xForm, 360.0 * Math.random());
      }
      else {
        XFormTransformService.rotate(xForm, -360.0 * Math.random());
      }
      XFormTransformService.translate(xForm, Math.random() - 1.0, Math.random() - 1.0);
      scl *= 0.75 + Math.random() / 4;
      XFormTransformService.scale(xForm, scl);

      xForm.setColor(Math.random());
      xForm.addVariation(Math.random() * 0.8 + 0.2, new Linear3DFunc());
      if (Math.random() > 0.33) {
        String[] fnc = { "blur3D", "julia3D", "curl3D", "butterfly3D", "julia3D",
            "julia3D", "hemisphere", "blob3D", "square3D", "julia3D", "pie3D", "pdj",
            "spherical3D", "blur", "julia3Dz" };
        int fncIdx = (int) (Math.random() * fnc.length);
        xForm.addVariation(Math.random() * 0.5, VariationFuncList.getVariationFuncInstance(fnc[fncIdx]));
      }

      xForm.setWeight(Math.random() * 0.9 + 0.1);
    }
    if (pWithSymmetry) {
      addSymmetry(flame);
    }
    return flame;
  }
}
