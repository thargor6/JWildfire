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

import java.io.Serializable;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

@SuppressWarnings("serial")
public abstract class VariationFunc implements Serializable {

  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

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

  public byte[] getRessource(String pName) {
    int idx = getRessourceIndex(pName);
    return idx >= 0 && idx < getRessourceValues().length ? getRessourceValues()[idx] : null;
  }

  public Object getParameter(String pName) {
    int idx = getParameterIndex(pName);
    return idx >= 0 ? getParameterValues()[idx] : null;
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

  public RessourceType getRessourceType(String pName) {
    return RessourceType.BYTEARRAY;
  }

  public static double limitVal(double pValue, double pMin, double pMax) {
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

  public static int limitIntVal(int pValue, int pMin, int pMax) {
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

  public Object[] joinArrays(Object[] array1, Object[] array2) {
    Object[] joinedArray = new Object[array1.length + array2.length];
    System.arraycopy(array1, 0, joinedArray, 0, array1.length);
    System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
    return joinedArray;
  }

  public String[] joinArrays(String[] array1, String[] array2) {
    String[] joinedArray = new String[array1.length + array2.length];
    System.arraycopy(array1, 0, joinedArray, 0, array1.length);
    System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
    return joinedArray;
  }

  public void validate() {

  }

  public VariationFunc makeCopy() {
    VariationFunc varCopy = VariationFuncList.getVariationFuncInstance(this.getName());
    // params
    String[] paramNames = this.getParameterNames();
    if (paramNames != null) {
      for (int i = 0; i < paramNames.length; i++) {
        Object val = this.getParameterValues()[i];
        if (val instanceof Number) {
          varCopy.setParameter(paramNames[i], ((Number)val).doubleValue());
        }
        else {
          throw new IllegalStateException();
        }
      }
    }
    // ressources
    String[] ressNames = this.getRessourceNames();
    if (ressNames != null) {
      for (int i = 0; i < ressNames.length; i++) {
        byte[] val = this.getRessourceValues()[i];
        varCopy.setRessource(ressNames[i], val);
      }
    }
    return varCopy;
  }

  public boolean ressourceCanModifyParams()  { return false; }

}
