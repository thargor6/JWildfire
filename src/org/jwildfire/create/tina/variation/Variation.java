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

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.fabs;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jwildfire.create.tina.animate.AnimAware;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.edit.Assignable;

public class Variation implements Assignable<Variation>, Serializable {
  private static final long serialVersionUID = 1L;
  @AnimAware
  private double amount;
  private final MotionCurve amountCurve = new MotionCurve();
  @AnimAware
  private VariationFunc func;

  private final Map<String, MotionCurve> motionCurves = new HashMap<String, MotionCurve>();

  public Variation() {

  }

  public Variation(double pAmount, VariationFunc pFunc) {
    amount = pAmount;
    func = pFunc;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double pAmount) {
    this.amount = pAmount;
  }

  public VariationFunc getFunc() {
    return func;
  }

  public void setFunc(VariationFunc func) {
    this.func = func;
  }

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP) {
    func.transform(pContext, pXForm, pAffineTP, pVarTP, amount);
  }

  @Override
  public String toString() {
    return func.getName() + "(" + amount + ")";
  }

  @Override
  public void assign(Variation var) {
    amount = var.amount;
    amountCurve.assign(var.amountCurve);

    // motionCurves
    motionCurves.clear();
    for (String name : var.motionCurves.keySet()) {
      MotionCurve copy = new MotionCurve();
      copy.assign(var.motionCurves.get(name));
      motionCurves.put(name, copy);
    }
    
    // variation function
    func = var.func.makeCopy();
  }

  @Override
  public Variation makeCopy() {
    Variation res = new Variation();
    res.assign(this);
    return res;
  }

  @Override
  public boolean isEqual(Variation pSrc) {
    if (fabs(amount - pSrc.amount) > EPSILON || !amountCurve.isEqual(pSrc.amountCurve) ||
        ((func != null && pSrc.func == null) || (func == null && pSrc.func != null) ||
        (func != null && pSrc.func != null && !func.getName().equals(pSrc.func.getName())))) {
      return false;
    }
    // curves
    {
      if (motionCurves.keySet().size() != pSrc.motionCurves.keySet().size())
        return false;
      for (String name : motionCurves.keySet()) {
        if (!pSrc.motionCurves.containsKey(name))
          return false;
      }
      for (String name : pSrc.motionCurves.keySet()) {
        if (!motionCurves.containsKey(name))
          return false;
      }
      for (String name : motionCurves.keySet()) {
        if (!motionCurves.get(name).isEqual(pSrc.motionCurves.get(name)))
          return false;
      }
    }
    // param values
    {
      Object vals[] = func.getParameterValues();
      if (vals != null) {
        Object srcVals[] = pSrc.func.getParameterValues();
        if (vals.length != srcVals.length) { return false; }
        for (int i = 0; i < vals.length; i++) {
          Object o = vals[i];
          Object s = srcVals[i];
          if ((o != null && s == null) || (o == null && s != null)) {
            return false;
          }
          else if (o != null && s != null) {
            if (o instanceof Integer) {
              if (! (s instanceof Integer)) { return false; }
              if (((Integer) o).intValue() != ((Integer) s).intValue()) {
                return false;
              }
            }
            else if (o instanceof Double) {
              if (! (s instanceof Double)) { return false; }
              if (fabs(((Double) o).doubleValue() - ((Double) s).doubleValue()) > EPSILON) {
                return false;
              }
            }
            else {
              throw new IllegalStateException();
            }
          }
        }
      }
    }
    // ressources
    {
      byte vals[][] = func.getRessourceValues();
      if (vals != null) {
        byte srcVals[][] = pSrc.func.getRessourceValues();
        for (int i = 0; i < vals.length; i++) {
          byte[] o = vals[i];
          byte[] s = srcVals[i];
          if ((o != null && s == null) || (o == null && s != null) || (o != null && s != null && o.length != s.length)) {
            return false;
          }
          if (o != null && s != null) {
            for (int j = 0; j < o.length; j++) {
              if (o[j] != s[j]) {
                return false;
              }
            }
          }
        }
      }
    }
    return true;
  }

  public MotionCurve getMotionCurve(String pName) {
    return motionCurves.get(pName);
  }

  public MotionCurve createMotionCurve(String pName) {
    if (getMotionCurve(pName) != null) {
      throw new RuntimeException("Motion curve <" + pName + "> already exists");
    }
    MotionCurve curve = new MotionCurve();
    motionCurves.put(pName, curve);
    return curve;
  }

  public Map<String, MotionCurve> getClonedMotionCurves() {
    Map<String, MotionCurve> res = new HashMap<String, MotionCurve>();
    for (String name : motionCurves.keySet()) {
      MotionCurve copy = new MotionCurve();
      copy.assign(motionCurves.get(name));
      res.put(name, copy);
    }
    return res;
  }

  public MotionCurve getAmountCurve() {
    return amountCurve;
  }

}
