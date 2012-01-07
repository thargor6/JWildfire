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

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.transform.XFormTransformService;

public abstract class RandomFlameGenerator {

  protected abstract Flame createFlame();

  public abstract String getName();

  public Flame createFlame(boolean pWithSymmetry, boolean pWithPostTransforms) {
    Flame flame = createFlame();
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

  protected void addSymmetry(Flame pFlame, boolean pPostTransformation) {
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

  protected void addPostTransforms(Flame pFlame) {
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
      XFormTransformService.scale(xForm, scl, true, true, true);
    }
  }

}
