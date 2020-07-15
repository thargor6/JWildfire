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

import java.util.*;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.animate.AnimAware;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.edit.Assignable;

import java.io.Serializable;
import java.util.Map.Entry;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.fabs;

public class Variation implements Assignable<Variation>, Serializable {
  private static final long serialVersionUID = 1L;
  public static final String WFIELD_AMOUNT_PARAM = "amount";
  @AnimAware
  private double amount;

  private final MotionCurve amountCurve = new MotionCurve();
  @AnimAware
  private VariationFunc func;

  private int priority;

  private final Map<String, MotionCurve> motionCurves = new HashMap<String, MotionCurve>();

  private class WeightFieldParamValue {
    public String paramName;
    public double intensity;
    public boolean isAmount;
    public Object oldParamValue;

    public WeightFieldParamValue(String paramName, double intensity) {
      this.paramName = paramName;
      this.isAmount = WFIELD_AMOUNT_PARAM.equals(paramName);
      this.intensity = intensity;
    }
  }



  private List<WeightFieldParamValue> weightFieldParamValueList;
  private boolean weightFieldParamValueIsOnlyAmount;

  public Variation() {

  }

  public Variation(double pAmount, VariationFunc pFunc) {
    amount = pAmount;
    func = pFunc;
    priority = pFunc.getPriority();
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double pAmount) {
    amount = pAmount;
  }

  public VariationFunc getFunc() {
    return func;
  }

  public void setFunc(VariationFunc pFunc) {
    func = pFunc;
  }

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP) {
    if(fabs(pAffineTP.weightMapValue)>EPSILON && fabs(pXForm.getWeightingFieldVarAmountIntensity())>EPSILON) {
      double nAmount = amount * (1.0 + pAffineTP.weightMapValue * pXForm.getWeightingFieldVarAmountIntensity());
      executeTransform(pContext, pXForm, pAffineTP, pVarTP, nAmount);
    }
    else {
      executeTransform(pContext, pXForm, pAffineTP, pVarTP, amount);
    }
  }

  private void executeTransform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double nAmount) {
    if(weightFieldParamValueList!=null && !weightFieldParamValueList.isEmpty()) {
      if(weightFieldParamValueIsOnlyAmount) {
        for(WeightFieldParamValue weightFieldParamValue: weightFieldParamValueList) {
          double vAmount = nAmount * (1.0 + pAffineTP.weightMapValue * weightFieldParamValue.intensity);
          func.transform(pContext, pXForm, pAffineTP, pVarTP, vAmount);
        }
      }
      else {
        for(WeightFieldParamValue weightFieldParamValue: weightFieldParamValueList) {
          if(!weightFieldParamValue.isAmount) {
            weightFieldParamValue.oldParamValue = getFunc().getParameter(weightFieldParamValue.paramName);
            if (weightFieldParamValue.oldParamValue instanceof Integer) {
              int newParamValue = Tools.FTOI(((Integer) weightFieldParamValue.oldParamValue).intValue() * (1.0 + pAffineTP.weightMapValue * weightFieldParamValue.intensity));
              getFunc().setParameter(weightFieldParamValue.paramName, newParamValue);
            } else {
              double newParamValue = ((Double) weightFieldParamValue.oldParamValue).doubleValue() * (1.0 + pAffineTP.weightMapValue * weightFieldParamValue.intensity);
              getFunc().setParameter(weightFieldParamValue.paramName, newParamValue);
            }
          }
        }
        try {
          double vAmount = nAmount;
          for(WeightFieldParamValue weightFieldParamValue: weightFieldParamValueList) {
            if(weightFieldParamValue.isAmount) {
              vAmount = nAmount * (1.0 + pAffineTP.weightMapValue * weightFieldParamValue.intensity);
              break;
            }
          }
          getFunc().init(pContext, pXForm.getOwner(), pXForm, vAmount);
          func.transform(pContext, pXForm, pAffineTP, pVarTP, vAmount);
        }
        finally {
          for(WeightFieldParamValue weightFieldParamValue: weightFieldParamValueList) {
            if(!weightFieldParamValue.isAmount) {
              if (weightFieldParamValue.oldParamValue instanceof Integer) {
                getFunc().setParameter(weightFieldParamValue.paramName, (Integer) weightFieldParamValue.oldParamValue);
              } else {
                getFunc().setParameter(weightFieldParamValue.paramName, (Double) weightFieldParamValue.oldParamValue);
              }
            }
          }
          getFunc().init(pContext, pXForm.getOwner(), pXForm, nAmount);
        }
      }
    }
    else {
      func.transform(pContext, pXForm, pAffineTP, pVarTP, nAmount);
    }
  }

  public void invtransform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP) {
    if(fabs(pAffineTP.weightMapValue)>EPSILON && fabs(pXForm.getWeightingFieldVarAmountIntensity())>EPSILON) {
      double nAmount = amount * (1.0 + pAffineTP.weightMapValue * pXForm.getWeightingFieldVarAmountIntensity());
      executeInvTransform(pContext, pXForm, pAffineTP, pVarTP, nAmount);
    }
    else {
      executeInvTransform(pContext, pXForm, pAffineTP, pVarTP, amount);
    }
  }

  private void executeInvTransform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double nAmount) {
    if(weightFieldParamValueList!=null && !weightFieldParamValueList.isEmpty()) {
      if(weightFieldParamValueIsOnlyAmount) {
        for(WeightFieldParamValue weightFieldParamValue: weightFieldParamValueList) {
          double vAmount = nAmount * (1.0 + pAffineTP.weightMapValue * weightFieldParamValue.intensity);
          func.invtransform(pContext, pXForm, pAffineTP, pVarTP, vAmount);
        }
      }
      else {
        for (WeightFieldParamValue weightFieldParamValue : weightFieldParamValueList) {
          if(!weightFieldParamValue.isAmount) {
            weightFieldParamValue.oldParamValue = getFunc().getParameter(weightFieldParamValue.paramName);
            if (weightFieldParamValue.oldParamValue instanceof Integer) {
              int newParamValue = Tools.FTOI(((Integer) weightFieldParamValue.oldParamValue).intValue() * (1.0 + pAffineTP.weightMapValue * weightFieldParamValue.intensity));
              getFunc().setParameter(weightFieldParamValue.paramName, newParamValue);
            } else {
              double newParamValue = ((Double) weightFieldParamValue.oldParamValue).doubleValue() * (1.0 + pAffineTP.weightMapValue * weightFieldParamValue.intensity);
              getFunc().setParameter(weightFieldParamValue.paramName, newParamValue);
            }
          }
        }
        try {
          double vAmount = nAmount;
          for(WeightFieldParamValue weightFieldParamValue: weightFieldParamValueList) {
            if(weightFieldParamValue.isAmount) {
              vAmount = nAmount * (1.0 + pAffineTP.weightMapValue * weightFieldParamValue.intensity);
              break;
            }
          }
          getFunc().init(pContext, pXForm.getOwner(), pXForm, vAmount);
          func.invtransform(pContext, pXForm, pAffineTP, pVarTP, vAmount);
        }
        finally {
          for (WeightFieldParamValue weightFieldParamValue : weightFieldParamValueList) {
            if(!weightFieldParamValue.isAmount) {
              if (weightFieldParamValue.oldParamValue instanceof Integer) {
                getFunc().setParameter(weightFieldParamValue.paramName, (Integer) weightFieldParamValue.oldParamValue);
              } else {
                getFunc().setParameter(weightFieldParamValue.paramName, (Double) weightFieldParamValue.oldParamValue);
              }
            }
          }
          getFunc().init(pContext, pXForm.getOwner(), pXForm, nAmount);
        }
      }
    }
    else {
      func.invtransform(pContext, pXForm, pAffineTP, pVarTP, nAmount);
    }
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
    priority = var.priority;
  }

  @Override
  public Variation makeCopy() {
    Variation res = new Variation();
    res.assign(this);
    return res;
  }

  @Override
  public boolean isEqual(Variation pSrc) {
    if (fabs(amount - pSrc.amount) > EPSILON || (priority != pSrc.priority) ||
            !amountCurve.isEqual(pSrc.amountCurve) ||
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
      for (Entry<String, MotionCurve> entry : motionCurves.entrySet()) {
        if (!entry.getValue().isEqual(pSrc.motionCurves.get(entry.getKey())))
          return false;
      }
    }
    // param values
    {
      Object vals[] = func.getParameterValues();
      if (vals != null) {
        Object srcVals[] = pSrc.func.getParameterValues();
        if (vals.length != srcVals.length) {
          return false;
        }
        for (int i = 0; i < vals.length; i++) {
          Object o = vals[i];
          Object s = srcVals[i];
          if ((o != null && s == null) || (o == null && s != null)) {
            return false;
          } else if (o != null && s != null) {
            if (o instanceof Integer) {
              if (!(s instanceof Integer)) {
                return false;
              }
              if (((Integer) o).intValue() != ((Integer) s).intValue()) {
                return false;
              }
            } else if (o instanceof Double) {
              if (!(s instanceof Double)) {
                return false;
              }
              if (fabs(((Double) o).doubleValue() - ((Double) s).doubleValue()) > EPSILON) {
                return false;
              }
            } else {
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

  public void removeMotionCurve(String paramName) {
    if(motionCurves.containsKey(paramName)) {
      motionCurves.remove(paramName);
    }
  }

  public Map<String, MotionCurve> getClonedMotionCurves() {
    Map<String, MotionCurve> res = new HashMap<String, MotionCurve>();
    for (Entry<String, MotionCurve> curveToCopy : motionCurves.entrySet()) {
      MotionCurve copy = new MotionCurve();
      copy.assign(curveToCopy.getValue());
      res.put(curveToCopy.getKey(), copy);
    }
    return res;
  }

  public MotionCurve getAmountCurve() {
    return amountCurve;
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(int pPriority) {
    priority = pPriority;
  }

  public void clearWeightingFieldParams() {
    weightFieldParamValueList = null;
  }

  public void addWeightingFieldParam(String paramName, double intensity) {
     if(VariationFuncList.isValidVariationForWeightingFields(getFunc().getName()) && ("amount".equals(paramName) || Arrays.asList(getFunc().getParameterNames()).indexOf(paramName)>=0)) {
        if(weightFieldParamValueList==null) {
          weightFieldParamValueList = new ArrayList<>();
          weightFieldParamValueIsOnlyAmount = true;
        }
        else {
          for(WeightFieldParamValue weightFieldParamValue: weightFieldParamValueList) {
            if(weightFieldParamValue.paramName.equals(paramName)) {
              // no duplicates
              return;
            }
          }
        }
       WeightFieldParamValue value = new WeightFieldParamValue(paramName, intensity);
       weightFieldParamValueIsOnlyAmount &= value.isAmount;
       weightFieldParamValueList.add(value);
     }
  }
}
