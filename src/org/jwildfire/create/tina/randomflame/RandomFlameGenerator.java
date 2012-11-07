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

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;

public abstract class RandomFlameGenerator {

  protected abstract Flame createFlame();

  public abstract String getName();

  public Flame createFlame(Prefs pPrefs) {
    Flame flame = createFlame();
    if (flame.getName() == null || flame.getName().length() == 0)
      flame.setName(this.getName() + " - " + flame.hashCode());
    flame.setBGTransparency(pPrefs.isTinaDefaultBGTransparency());
    flame.setBGColorRed(pPrefs.getTinaRandomBatchBGColorRed());
    flame.setBGColorGreen(pPrefs.getTinaRandomBatchBGColorGreen());
    flame.setBGColorBlue(pPrefs.getTinaRandomBatchBGColorBlue());
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
