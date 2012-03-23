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
package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public abstract class VariationFunc {

  public void init(FlameTransformationContext pContext, XForm pXForm) {

  }

  public int getPriority() {
    return 0;
  }

  public abstract void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount);

  public abstract String getName();

  public abstract String[] getParameterNames();

  // For few variations like mobius which use a different naming scheme 
  public String[] getParameterAlternativeNames() {
    return null;
  }

  public abstract Object[] getParameterValues();

  public abstract void setParameter(String pName, double pValue);

  public String[] getRessourceNames() {
    return null;
  }

  public byte[][] getRessourceValues() {
    return null;
  }

  public void setRessource(String pName, byte[] pValue) {
  }

  public int getParameterIndex(String pName) {
    String paramNames[] = getParameterNames();
    if (paramNames != null) {
      for (int i = 0; i < paramNames.length; i++) {
        if (paramNames[i].equals(pName)) {
          return i;
        }
      }
    }
    return -1;
  }

  public int getRessourceIndex(String pName) {
    String ressourceNames[] = getRessourceNames();
    if (ressourceNames != null) {
      for (int i = 0; i < ressourceNames.length; i++) {
        if (ressourceNames[i].equals(pName)) {
          return i;
        }
      }
    }
    return -1;
  }

  protected double limitVal(double pValue, double pMin, double pMax) {
    if (pValue < pMin) {
      return pMin;
    }
    else if (pValue > pMax) {
      return pMax;
    }
    else {
      return pValue;
    }
  }

  protected int limitIntVal(int pValue, int pMin, int pMax) {
    if (pValue < pMin) {
      return pMin;
    }
    else if (pValue > pMax) {
      return pMax;
    }
    else {
      return pValue;
    }
  }
}
