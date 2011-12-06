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

  private static final String FNCLST_ORIGINAL[] = { "blur3D", "bubble", "curl3D", "diamond", "disc", "julia3D", "fan2", "heart",
      "julia3D", "hemisphere", "horseshoe", "blob3D", "julia3D", "pdj", "popcorn", "rings2", "t_rose",
      "spherical3D", "spiral", "rectangles", "blur", "waves", "swirl", "secant2" };

  private static final String FNCLST_EXPERIMENTAL[] = { "blur3D", "bubble", "escher", "rays", "t_epispiral", "curl3D", "diamond",
      "disc", "sech", "loonie", "exp", "cosh", "split",
      "butterfly3D", "cpow", "conic", "julia3D", "fan2", "cell", "stripes",
      "flower", "heart", "julia3D", "disc2", "polar2", "foci", "scry",
      "hemisphere", "popcorn2", "sec", "lazysusan", "sin", "separation",
      "t_rose", "edisc", "waves2", "twintrian", "coth", "super_shape",
      "tanh", "bipolar", "cot", "horseshoe", "wedge", "sinh", "modulus",
      "cross", "tangent3D", "csc", "curve", "csch", "bent2", "splits",
      "tan", "blob3D", "julia3D", "log", "cos", "oscilloscope",
      "pdj", "popcorn", "parabola", "rings2", "spherical3D", "spiral", "rectangles", "elliptic", "waves", "swirl", "boarders", "secant2" };

  private static final String FNCLST_3D[] = { "blur3D", "julia3D", "curl3D", "butterfly3D", "julia3D",
      "julia3D", "blade3D", "hemisphere", "blob3D", "tangent3D", "square3D", "julia3D", "pie3D", "pdj",
      "spherical3D", "julia3Dz" };

  public Flame createFlame(RandomFlameGeneratorStyle pStyle, boolean pWithSymmetry, boolean pWithPostTransforms) {
    Flame flame;
    switch (pStyle) {
      case ORIGINAL:
        flame = createFlame_original();
        break;
      case EXPERIMENTAL:
        flame = createFlame_experimental();
        break;
      case ONLY3D:
        flame = createFlame_3D();
        break;
      case TENTACLE:
        flame = createFlame_tentacle();
        break;
      case ALL: {
        double r = Math.random();
        if (r < 0.25) {
          flame = createFlame_original();
        }
        else if (r < 0.50) {
          flame = createFlame_experimental();
        }
        else if (r < 0.50) {
          flame = createFlame_tentacle();
        }
        else {
          flame = createFlame_3D();
        }
        break;
      }
      default:
        throw new IllegalStateException();
    }
    if (pWithSymmetry && !pWithPostTransforms) {
      addSymmetry(flame, false);
    }
    if (pWithPostTransforms) {
      addPostTransforms(flame);
      if (pWithSymmetry) {
        addSymmetry(flame, true);
      }
    }
    return flame;
  }

  private Flame createFlame_tentacle() {
    Flame flame = new Flame();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setPixelsPerUnit(200);
    flame.setSpatialFilterRadius(1.0);
    flame.setFinalXForm(null);
    flame.getXForms().clear();
    int maxXFormsX = (int) (2.0 + Math.random() * 3.0);
    int maxXFormsY = (int) (2.0 + Math.random() * 3.0);
    double xMin = -(double) maxXFormsX * 0.5;
    double yMin = -(double) maxXFormsY * 0.5 + 1;

    String[] fnc = FNCLST_EXPERIMENTAL;

    double scl = 1.0;
    for (int y = 0; y < maxXFormsY; y++) {
      for (int x = 0; x < maxXFormsX; x++) {
        XForm xForm = new XForm();
        xForm.setWeight(1.0);
        flame.getXForms().add(xForm);
        XFormTransformService.globalTranslate(xForm, xMin + x, yMin + y, false);
        if (Math.random() < 0.5) {
          XFormTransformService.rotate(xForm, 360.0 * Math.random(), true);
        }
        else {
          XFormTransformService.rotate(xForm, -360.0 * Math.random(), true);
        }
        XFormTransformService.localTranslate(xForm, Math.random() - 1.0, Math.random() - 1.0, true);
        scl *= 0.75 + Math.random() / 4;
        XFormTransformService.scale(xForm, scl, true);
        int fncIdx = (int) (Math.random() * fnc.length);
        xForm.addVariation(Math.random() * 0.9 + 0.1, VariationFuncList.getVariationFuncInstance(fnc[fncIdx], true));
        xForm.setColor(Math.random());
      }
    }

    return flame;
  }

  private Flame createFlame_original() {
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
      XFormTransformService.localTranslate(xForm, Math.random() - 1.0, Math.random() - 1.0);
      scl *= 0.75 + Math.random() / 4;
      XFormTransformService.scale(xForm, scl);

      xForm.setColor(Math.random());
      xForm.addVariation(Math.random() * 0.8 + 0.2, new Linear3DFunc());
      if (Math.random() > 0.33) {
        String[] fnc = FNCLST_ORIGINAL;
        int fncIdx = (int) (Math.random() * fnc.length);
        xForm.addVariation(Math.random() * 0.5, VariationFuncList.getVariationFuncInstance(fnc[fncIdx], true));
      }

      xForm.setWeight(Math.random() * 0.9 + 0.1);
    }
    return flame;
  }

  private void addSymmetry(Flame pFlame, boolean pPostTransformation) {
    //    int symmetry = 2 + (int) (2 * Math.random() + 0.5);
    int tCount = pFlame.getXForms().size();
    int symmetry = tCount;
    if (symmetry == 1) {
      return;
    }
    int iMax = pFlame.getXForms().size() - 1;
    if (iMax < 2) {
      iMax = 2;
    }
    for (int i = 0; i < iMax; i++) {
      XForm xForm = pFlame.getXForms().get(i);
      double alpha = 2 * Math.PI / symmetry;
      if (pPostTransformation) {
        xForm.setPostCoeff00(Math.cos(i * alpha));
        xForm.setPostCoeff01(Math.sin(i * alpha));
        xForm.setPostCoeff10(-xForm.getPostCoeff01());
        xForm.setPostCoeff11(xForm.getPostCoeff00());
        xForm.setPostCoeff20(0.0);
        xForm.setPostCoeff21(0.0);
      }
      else {
        xForm.setCoeff00(Math.cos(i * alpha));
        xForm.setCoeff01(Math.sin(i * alpha));
        xForm.setCoeff10(-xForm.getCoeff01());
        xForm.setCoeff11(xForm.getCoeff00());
        xForm.setCoeff20(0.0);
        xForm.setCoeff21(0.0);
      }
    }
  }

  private Flame createFlame_experimental() {
    Flame flame = new Flame();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setPixelsPerUnit(200);
    flame.setSpatialFilterRadius(1.0);
    flame.setFinalXForm(null);
    flame.getXForms().clear();

    int maxXForms = (int) (1.0 + Math.random() * 5.0);
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
      XFormTransformService.localTranslate(xForm, Math.random() - 1.0, Math.random() - 1.0);
      scl *= 0.75 + Math.random() / 4;
      XFormTransformService.scale(xForm, scl);

      xForm.setColor(Math.random());
      xForm.addVariation(Math.random() * 0.3 + 0.2, new Linear3DFunc());
      if (Math.random() > 0.1) {
        String[] fnc = FNCLST_EXPERIMENTAL;
        int fncIdx = (int) (Math.random() * fnc.length);
        xForm.addVariation(0.2 + Math.random() * 0.6, VariationFuncList.getVariationFuncInstance(fnc[fncIdx], true));
      }

      xForm.setWeight(Math.random() * 0.9 + 0.1);
    }
    return flame;
  }

  private Flame createFlame_3D() {
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
      XFormTransformService.localTranslate(xForm, Math.random() - 1.0, Math.random() - 1.0);
      scl *= 0.75 + Math.random() / 4;
      XFormTransformService.scale(xForm, scl);

      xForm.setColor(Math.random());
      xForm.addVariation(Math.random() * 0.3 + 0.2, new Linear3DFunc());
      if (Math.random() > 0.1) {
        String[] fnc = FNCLST_3D;
        int fncIdx = (int) (Math.random() * fnc.length);
        xForm.addVariation(Math.random() * 0.4 + 0.1, VariationFuncList.getVariationFuncInstance(fnc[fncIdx], true));
      }

      xForm.setWeight(Math.random() * 0.9 + 0.1);
    }
    return flame;
  }

  private void addPostTransforms(Flame pFlame) {
    double scl = 1.0;
    for (XForm xForm : pFlame.getXForms()) {
      if (Math.random() < 0.5) {
        XFormTransformService.rotate(xForm, 360.0 * Math.random(), true);
      }
      else {
        XFormTransformService.rotate(xForm, -360.0 * Math.random(), true);
      }
      XFormTransformService.localTranslate(xForm, Math.random() - 1.0, Math.random() - 1.0, true);
      scl *= 0.75 + Math.random() / 4;
      XFormTransformService.scale(xForm, scl, true);
    }
  }

}
