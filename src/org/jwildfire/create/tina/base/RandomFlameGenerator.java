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
import org.jwildfire.create.tina.variation.JuliaNFunc;
import org.jwildfire.create.tina.variation.Linear3DFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;
import org.jwildfire.create.tina.variation.Waves2Func;

public class RandomFlameGenerator {

  private static final String FNCLST_ORIGINAL[] = { "blur3D", "bubble", "curl3D", "diamond", "disc", "julia3D", "heart",
      "julia3D", "hemisphere", "bwraps7", "horseshoe", "blob3D", "julia3D", "pdj", "popcorn", "rings2", "t_rose",
      "spherical3D", "spiral", "rectangles", "blur", "waves", "swirl", "secant2" };

  private static final String FNCLST_EXPERIMENTAL[] = { "blur3D", "bubble", "escher", "rays", "t_epispiral", "curl3D", "diamond",
      "disc", "sech", "loonie", "exp", "cosh", "split", "wedge_sph", "circlize", "t_heart", "bwraps7", "t_colorscale",
      "butterfly3D", "cpow", "conic", "julia3D", "cell", "stripes", "t_post_mirror", "t_colorscale",
      "flower", "heart", "julia3D", "disc2", "polar2", "foci", "scry", "flux", "bwraps7", "t_colorscale",
      "hemisphere", "popcorn2", "sec", "lazysusan", "sin", "separation", "t_post_mirror", "t_heart",
      "t_rose", "edisc", "waves2", "twintrian", "coth", "super_shape", "auger", "t_pre_wave3D",
      "tanh", "bipolar", "cot", "horseshoe", "wedge", "sinh", "modulus", "mobius", "bwraps7", "t_colorscale",
      "cross", "tangent3D", "csc", "curve", "csch", "bent2", "splits", "whorl", "t_post_mirror",
      "tan", "blob3D", "julia3D", "log", "cos", "oscilloscope", "wedge_julia", "bwraps7", "t_heart",
      "pdj", "popcorn", "parabola", "rings2", "spherical3D", "spiral", "rectangles", "elliptic", "waves", "swirl",
      "bwraps7", "t_heart", "t_colorscale",
      "boarders", "secant2" };

  private static final String FNCLST_3D[] = { "blur3D", "julia3D", "curl3D", "butterfly3D", "julia3D",
      "julia3D", "blade3D", "bwraps7", "hemisphere", "blob3D", "t_pre_wave3D", "tangent3D", "square3D", "julia3D", "pie3D", "pdj",
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
      case GNARL:
        flame = createFlame_gnarl();
        break;
      case ALL: {
        double r = Math.random();
        if (r < 0.20) {
          flame = createFlame_original();
        }
        else if (r < 0.40) {
          flame = createFlame_experimental();
        }
        else if (r < 0.60) {
          flame = createFlame_tentacle();
        }
        else if (r < 0.80) {
          flame = createFlame_gnarl();
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

  private Flame createFlame_gnarl() {
    // Bases loosely on the W2R Batch Script by parrotdolphin.deviantart.com */ 
    Flame flame = new Flame();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setPixelsPerUnit(200);
    flame.setSpatialFilterRadius(1.0);
    flame.setFinalXForm(null);
    flame.getXForms().clear();
    // init
    double scaleX = Math.random() * 0.04 + 0.04;
    if (Math.random() > 0.75) {
      scaleX = 0 - scaleX;
    }

    double scaleY = Math.random() * 0.04 + 0.04;
    if (Math.random() > 0.75) {
      scaleY = 0 - scaleY;
    }
    else if (Math.random() < 0.25) {
      scaleY = scaleX;
    }

    double freqX = Math.random() * 2.0 + 2.0;
    double freqY;
    if (Math.random() > 0.5) {
      freqY = freqX;
    }
    else {
      freqY = Math.random() * 2.0 + 2.0;
    }

    double blurAmount = 0.0025 * Math.random();
    double wavesWeight = Math.random() * 20 + 35;
    double _2ndWeight = 0.5;
    double _3rdWeight = 0.5;
    double symmetry = 0.9 + Math.random() * 0.2 - Math.random() * 0.4;
    int sides = (int) (Math.random() * 8.0 + 3.0);
    // 1st XForm
    {
      XForm xForm = new XForm();
      flame.getXForms().add(xForm);
      xForm.setWeight(wavesWeight);
      Waves2Func w2 = (Waves2Func) VariationFuncList.getVariationFuncInstance("waves2", true);
      w2.setParameter("freqx", freqX);
      w2.setParameter("scalex", scaleX);
      w2.setParameter("freqy", freqY);
      w2.setParameter("scaley", scaleY);
      xForm.addVariation(1, w2);

      switch ((int) (Math.random() * 11.0)) {
        case 0:
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance("blur", true));
          break;
        case 1:
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance("cos", true));
          break;
        case 2:
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance("exp", true));
          break;
        case 3:
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance("exponential", true));
          break;
        case 4:
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance("lazysusan", true));
          break;
        case 5:
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance("ngon", true));
          break;
        case 6:
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance("sech", true));
          break;
        case 7:
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance("sinh", true));
          break;
        case 8:
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance("t_epispiral", true));
          break;
        case 9:
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance("tanh", true));
          break;
        case 10:
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance("twintrian", true));
          break;
        default:
          throw new IllegalStateException();
      }
      xForm.setColorSymmetry(symmetry);
      if (Math.random() > 0.5) {
        XFormTransformService.scale(xForm, 0.995);
      }
      double angle, tx, ty;
      switch (sides) {
        case 3:
          angle = -120;
          tx = Math.random() * 8 - 4;
          ty = Math.random() * 8 - 4;
          break;
        case 4:
          angle = -90;
          tx = Math.random() * 8 - 4;
          ty = Math.random() * 8 - 4;
          break;
        case 5:
          angle = -72;
          tx = Math.random() * 7 - 3.5;
          ty = Math.random() * 7 - 3.5;
          break;
        case 6:
          angle = -60;
          tx = Math.random() * 7 - 3.5;
          ty = Math.random() * 7 - 3.5;
          break;
        case 7:
          angle = -51.42857;
          tx = Math.random() * 6 - 3;
          ty = Math.random() * 6 - 3;
          break;
        case 8:
          angle = -135;
          tx = Math.random() * 6 - 3;
          ty = Math.random() * 6 - 3;
          break;
        case 9:
          angle = -40;
          tx = Math.random() * 5 - 2.5;
          ty = Math.random() * 5 - 2.5;
          break;
        case 10:
          angle = -36;
          tx = Math.random() * 4 - 2;
          ty = Math.random() * 4 - 2;
          break;
        default:
          throw new IllegalStateException();
      }
      XFormTransformService.rotate(xForm, angle);
      XFormTransformService.localTranslate(xForm, tx, ty);
    }
    // 2nd XForm
    {
      XForm xForm = new XForm();
      flame.getXForms().add(xForm);
      xForm.setWeight(_2ndWeight);
      int f = (int) (Math.random() * 3);
      switch (f) {
        case 0:
          xForm.addVariation(Math.random() * 0.7 + 0.1, VariationFuncList.getVariationFuncInstance("radial_blur", true));
          break;
        case 1:
          xForm.addVariation(Math.random() * 0.7 + 0.3, VariationFuncList.getVariationFuncInstance("bubble", true));
          xForm.addVariation(Math.random() * 0.7 + 0.1, VariationFuncList.getVariationFuncInstance("radial_blur", true));
          break;
        case 2:
          xForm.addVariation(Math.random() * 0.1, VariationFuncList.getVariationFuncInstance("radial_blur", true));
          JuliaNFunc juliaN = (JuliaNFunc) VariationFuncList.getVariationFuncInstance("julian", true);
          juliaN.setParameter("power", 50 - Math.random() * 100);
          juliaN.setParameter("dist", Math.random() * 10 - 2);
          xForm.addVariation(Math.random() * 0.06 + 0.1, juliaN);
          break;
      }
      xForm.setColorSymmetry(-1);
    }
    // 3rd XForm
    if (Math.random() > 0.5) {
      XForm xForm = new XForm();
      flame.getXForms().add(xForm);
      xForm.setWeight(_3rdWeight);
      xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance("linear3D", true));
      XFormTransformService.rotate(xForm, 180 - Math.random() * 360.0);
      XFormTransformService.localTranslate(xForm, 1.0 - 2.0 * Math.random(), 1.0 - 2.0 * Math.random());
      xForm.setColorSymmetry(-1);
    }
    return flame;
  }
}
