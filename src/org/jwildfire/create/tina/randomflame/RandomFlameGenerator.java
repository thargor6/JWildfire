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
package org.jwildfire.create.tina.randomflame;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;

public abstract class RandomFlameGenerator {

  protected abstract Flame createFlame(RandomFlameGeneratorState pState);

  public RandomFlameGeneratorState initState() {
    return new RandomFlameGeneratorState();
  }

  public abstract String getName();

  public Flame createFlame(Prefs pPrefs, RandomFlameGeneratorState pState) {
    Flame flame = createFlame(pState);
    if (flame.getName() == null || flame.getName().length() == 0)
      flame.setName(this.getName() + " - " + flame.hashCode());
    flame.setBGTransparency(pPrefs.isTinaDefaultBGTransparency());
    flame.setBGColorRed(pPrefs.getTinaRandomBatchBGColorRed());
    flame.setBGColorGreen(pPrefs.getTinaRandomBatchBGColorGreen());
    flame.setBGColorBlue(pPrefs.getTinaRandomBatchBGColorBlue());
    if (pPrefs.getTinaDefaultDEMaxRadius() < EPSILON) {
      flame.setDeFilterEnabled(false);
    }
    else {
      flame.setDeFilterEnabled(true);
      flame.setDeFilterMaxRadius(pPrefs.getTinaDefaultDEMaxRadius());
    }
    for (Layer layer : flame.getLayers()) {
      for (XForm xForm : layer.getXForms()) {
        xForm.setAntialiasAmount(pPrefs.getTinaDefaultAntialiasingAmount());
        xForm.setAntialiasRadius(pPrefs.getTinaDefaultAntialiasingRadius());
      }
      for (XForm xForm : layer.getFinalXForms()) {
        xForm.setAntialiasAmount(pPrefs.getTinaDefaultAntialiasingAmount());
        xForm.setAntialiasRadius(pPrefs.getTinaDefaultAntialiasingRadius());
      }
    }
    return flame;
  }

  //  protected void addSymmetry(Flame pFlame, boolean pPostTransformation) {
  //    //    int symmetry = 2 + (int) (2 * Math.random() + 0.5);
  //    int tCount = pFlame.getXForms().size();
  //    int symmetry = tCount;
  //    if (symmetry == 1) {
  //      return;
  //    }
  //    int iMax = pFlame.getXForms().size() - 1;
  //    if (iMax < 2) {
  //      iMax = 2;
  //    }
  //    for (int i = 0; i < iMax; i++) {
  //      XForm xForm = pFlame.getXForms().get(i);
  //      double alpha = 2 * Math.PI / symmetry;
  //      if (pPostTransformation) {
  //        xForm.setPostCoeff00(Math.cos(i * alpha));
  //        xForm.setPostCoeff01(Math.sin(i * alpha));
  //        xForm.setPostCoeff10(-xForm.getPostCoeff01());
  //        xForm.setPostCoeff11(xForm.getPostCoeff00());
  //        xForm.setPostCoeff20(0.0);
  //        xForm.setPostCoeff21(0.0);
  //      }
  //      else {
  //        xForm.setCoeff00(Math.cos(i * alpha));
  //        xForm.setCoeff01(Math.sin(i * alpha));
  //        xForm.setCoeff10(-xForm.getCoeff01());
  //        xForm.setCoeff11(xForm.getCoeff00());
  //        xForm.setCoeff20(0.0);
  //        xForm.setCoeff21(0.0);
  //      }
  //    }
  //  }

}
