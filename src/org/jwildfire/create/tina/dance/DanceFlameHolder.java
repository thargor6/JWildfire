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
package org.jwildfire.create.tina.dance;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class DanceFlameHolder {
  private boolean flameChanging = false;
  private Flame currFlame = null;
  private boolean currIsMorphing = false;
  private XForm xForm0, xForm1, xForm2, xForm3, xFormF;

  public void changeFlame(Flame pFlame, boolean pIsMorphing) {
    flameChanging = true;
    try {
      if (pFlame != null) {
        xForm0 = pFlame.getXForms().get(0);
        xForm1 = pFlame.getXForms().size() > 1 ? pFlame.getXForms().get(1) : null;
        xForm2 = pFlame.getXForms().size() > 2 ? pFlame.getXForms().get(2) : null;
        xForm3 = pFlame.getXForms().size() > 3 ? pFlame.getXForms().get(3) : null;
        xFormF = pFlame.getFinalXForms().size() > 0 ? pFlame.getFinalXForms().get(pFlame.getFinalXForms().size() - 1) : null;
        if (getxFormF() == null) {
          xFormF = new XForm();
          getxFormF().addVariation(1.0, VariationFuncList.getVariationFuncInstance("linear3D"));
        }
      }
      currIsMorphing = pIsMorphing;
      currFlame = pFlame;
    }
    finally {
      flameChanging = false;
    }
  }

  public Flame getCurrFlame() {
    return currFlame;
  }

  public boolean isFlameChanging() {
    return flameChanging;
  }

  public boolean isCurrIsMorphing() {
    return currIsMorphing;
  }

  public XForm getxForm0() {
    return xForm0;
  }

  public XForm getxForm1() {
    return xForm1;
  }

  public XForm getxForm2() {
    return xForm2;
  }

  public XForm getxForm3() {
    return xForm3;
  }

  public XForm getxFormF() {
    return xFormF;
  }

}
