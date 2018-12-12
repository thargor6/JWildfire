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

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class VariationFunc implements Serializable {


  /**
   * called for the first instance/thread (there is one instance per thread). It is useful to use this method to implement heavy calculations, rather than using init()
   */
  public void initOnce(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

  }

  /**
   * called for each instance (there is one instance per thread)
   */
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

  }

  /**
   * Specifies the type of variation:
   * 0: normal
   * -1: pre
   * 1: post
   * -2: prepost, with inverse post
   * 2: prepost, with inverse pre
   */
  public int getPriority() {
    return 0;
  }

  public abstract void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount);

  // Only used by prepost transforms
  public void invtransform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

  }

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
    } else if (pValue > pMax) {
      return pMax;
    } else {
      return pValue;
    }
  }

  public static int limitIntVal(int pValue, int pMin, int pMax) {
    if (pValue < pMin) {
      return pMin;
    } else if (pValue > pMax) {
      return pMax;
    } else {
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
          varCopy.setParameter(paramNames[i], ((Number) val).doubleValue());
        } else {
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

  /**
   * if resourceCanModifyParams is true, it means that variation function
   * can dynamically add (or remove) parameters, depending on values of resource resourceName
   * in other words, calls to setRessource(resourceName, ...))
   * can change what is returned by getParameterNames() and getParameterValues()
   */
  public boolean ressourceCanModifyParams(String resourceName) {
    return false;
  }

  /**
   * should return true if at least one resource can trigger parameter addition/removal
   */
  public boolean ressourceCanModifyParams() {
    return false;
  }

  /**
   * if dynamicParameterExpansion is true, it means that variation function
   * can dynamically add (or remove) parameters, depending on values of parameter paramName
   * in other words, calls to setParameter(paramName, ...))
   * can change what is returned by getParameterNames() and getParameterValues()
   * current implementation to handle this assumes only one level depth of parameter expansion
   * that is, if change to parameter paramName can cause a parameter B to be dynamically added/removed
   * then changes to parameter B cannot in turn cause additional parameters to be added/removed
   */
  public boolean dynamicParameterExpansion(String paramName) {
    return false;
  }

  /**
   * should return true if at least one parameter can trigger parameter addition/removal
   */
  public boolean dynamicParameterExpansion() {
    return false;
  }

}
