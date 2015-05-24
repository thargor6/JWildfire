/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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
package org.jwildfire.create.tina.leapmotion;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.Variation;

public enum LeapMotionOutputChannel {
  XFORM_RESET_ANGLES {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        pProperty.setEnabled(true);
      }
      else {
        pProperty.setEnabled(false);
      }
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        xform.setCoeff00(1.0);
        xform.setCoeff01(0.0);
        xform.setCoeff10(0.0);
        xform.setCoeff11(1.0);
      }
    }

    @Override
    public int getIndexCount() {
      return 1;
    }

    @Override
    public MotionCurve getMotionCurve(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      return null;
    }
  },
  XFORM_MOVE_X {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        pProperty.setEnabled(true);
        pProperty.setOffset(xform.getCoeff20());
      }
      else {
        pProperty.setEnabled(false);
      }
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        xform.setCoeff20(pValue);
      }
    }

    @Override
    public MotionCurve getMotionCurve(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        return xform.getXYCoeff20Curve();
      }
      else {
        return null;
      }
    }

    @Override
    public int getIndexCount() {
      return 1;
    }

  },
  XFORM_MOVE_Y {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        pProperty.setEnabled(true);
        pProperty.setOffset(xform.getCoeff21());
      }
      else {
        pProperty.setEnabled(false);
      }
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        xform.setCoeff21(pValue);
      }
    }

    @Override
    public MotionCurve getMotionCurve(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        return xform.getXYCoeff21Curve();
      }
      else {
        return null;
      }
    }

    @Override
    public int getIndexCount() {
      return 1;
    }
  },
  XFORM_ROTATE {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        pProperty.setEnabled(true);
      }
      else {
        pProperty.setEnabled(false);
      }
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        XFormTransformService.rotate(xform, pValue, false);
      }
    }

    @Override
    public MotionCurve getMotionCurve(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        return xform.getXYRotateCurve();
      }
      else {
        return null;
      }
    }

    @Override
    public int getIndexCount() {
      return 1;
    }
  },
  XFORM_SCALE {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        pProperty.setEnabled(true);
      }
      else {
        pProperty.setEnabled(false);
      }
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        XFormTransformService.scale(xform, pValue, true, true, false);
      }
    }

    @Override
    public MotionCurve getMotionCurve(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        return xform.getXYScaleCurve();
      }
      else {
        return null;
      }
    }

    @Override
    public int getIndexCount() {
      return 1;
    }
  },
  XFORM_RESET_POST {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        pProperty.setEnabled(true);
      }
      else {
        pProperty.setEnabled(false);
      }
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        xform.setPostCoeff00(1.0);
        xform.setPostCoeff01(0.0);
        xform.setPostCoeff10(0.0);
        xform.setPostCoeff11(1.0);
      }
    }

    @Override
    public MotionCurve getMotionCurve(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      return null;
    }

    @Override
    public int getIndexCount() {
      return 1;
    }
  },
  XFORM_MOVE_X_POST {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        pProperty.setEnabled(true);
        pProperty.setOffset(xform.getPostCoeff20());
      }
      else {
        pProperty.setEnabled(false);
      }
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        xform.setPostCoeff20(pValue);
      }
    }

    @Override
    public MotionCurve getMotionCurve(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        return xform.getXYPostCoeff20Curve();
      }
      else {
        return null;
      }
    }

    @Override
    public int getIndexCount() {
      return 1;
    }
  },
  XFORM_MOVE_Y_POST {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        pProperty.setEnabled(true);
        pProperty.setOffset(xform.getPostCoeff21());
      }
      else {
        pProperty.setEnabled(false);
      }
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        xform.setPostCoeff21(pValue);
      }
    }

    @Override
    public MotionCurve getMotionCurve(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        return xform.getXYPostCoeff21Curve();
      }
      else {
        return null;
      }
    }

    @Override
    public int getIndexCount() {
      return 1;
    }
  },
  XFORM_ROTATE_POST {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        pProperty.setEnabled(true);
      }
      else {
        pProperty.setEnabled(false);
      }
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        XFormTransformService.rotate(xform, pValue, true);
      }
    }

    @Override
    public MotionCurve getMotionCurve(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        return xform.getXYPostRotateCurve();
      }
      else {
        return null;
      }
    }

    @Override
    public int getIndexCount() {
      return 1;
    }
  },
  XFORM_SCALE_POST {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        pProperty.setEnabled(true);
      }
      else {
        pProperty.setEnabled(false);
      }
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        XFormTransformService.scale(xform, pValue, true, true, true);
      }
    }

    @Override
    public MotionCurve getMotionCurve(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        return xform.getXYPostScaleCurve();
      }
      else {
        return null;
      }
    }

    @Override
    public int getIndexCount() {
      return 1;
    }
  },

  XFORM_WEIGHT {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        pProperty.setEnabled(true);
        pProperty.setOffset(xform.getWeight());
      }
      else {
        pProperty.setEnabled(false);
      }
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        xform.setWeight(pValue);
      }
    }

    @Override
    public MotionCurve getMotionCurve(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        return xform.getWeightCurve();
      }
      else {
        return null;
      }
    }

    @Override
    public int getIndexCount() {
      return 1;
    }
  },

  FORMULA_AMOUNT {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      Variation var = getVariationByIndex(pProperty, pFlame);
      if (var != null) {
        pProperty.setEnabled(true);
        pProperty.setOffset(var.getAmount());
      }
      else {
        pProperty.setEnabled(false);
      }
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      Variation var = getVariationByIndex(pProperty, pFlame);
      if (var != null) {
        var.setAmount(pValue);
      }
    }

    @Override
    public MotionCurve getMotionCurve(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      Variation var = getVariationByIndex(pProperty, pFlame);
      if (var != null) {
        return var.getAmountCurve();
      }
      else {
        return null;
      }
    }

    @Override
    public int getIndexCount() {
      return 2;
    }
  },

  FORMULA_PARAM_AMOUNT {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      Variation var = getVariationByIndex(pProperty, pFlame);
      if (var != null && pProperty.getIndex3() >= 0 && pProperty.getIndex3() < var.getFunc().getParameterNames().length) {
        pProperty.setEnabled(true);
        Object value = var.getFunc().getParameterValues()[pProperty.getIndex3()];
        if (value instanceof Double) {
          pProperty.setOffset((Double) value);
        }
        else if (value instanceof Integer) {
          pProperty.setOffset((Integer) value);
        }
      }
      else {
        pProperty.setEnabled(false);
      }
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      Variation var = getVariationByIndex(pProperty, pFlame);
      if (var != null && pProperty.getIndex3() >= 0 && pProperty.getIndex3() < var.getFunc().getParameterNames().length) {
        String paramName = var.getFunc().getParameterNames()[pProperty.getIndex3()];
        var.getFunc().setParameter(paramName, pValue);
      }
    }

    @Override
    public MotionCurve getMotionCurve(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      Variation var = getVariationByIndex(pProperty, pFlame);
      if (var != null && pProperty.getIndex3() >= 0 && pProperty.getIndex3() < var.getFunc().getParameterNames().length) {
        String paramName = var.getFunc().getParameterNames()[pProperty.getIndex3()];
        MotionCurve curve = var.getMotionCurve(paramName);
        if (curve == null) {
          curve = var.createMotionCurve(paramName);
        }
        return curve;
      }
      else {
        return null;
      }
    }

    @Override
    public int getIndexCount() {
      return 3;
    }
  },

  CAM_MOVE_X {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      pProperty.setEnabled(true);
      pProperty.setOffset(pFlame.getCamPosX());
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      pFlame.setCamPosX(pValue);
    }

    @Override
    public MotionCurve getMotionCurve(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      return pFlame.getCamPosXCurve();
    }

    @Override
    public int getIndexCount() {
      return 0;
    }
  },
  CAM_MOVE_Y {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      pProperty.setEnabled(true);
      pProperty.setOffset(pFlame.getCamPosX());
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      pFlame.setCamPosY(pValue);
    }

    @Override
    public MotionCurve getMotionCurve(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      return pFlame.getCamPosYCurve();
    }

    @Override
    public int getIndexCount() {
      return 0;
    }
  },
  CAM_ZOOM {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      pProperty.setEnabled(true);
      pProperty.setOffset(pFlame.getPixelsPerUnit());
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      pFlame.setPixelsPerUnit(pValue);
    }

    @Override
    public MotionCurve getMotionCurve(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      return pFlame.getPixelsPerUnitCurve();
    }

    @Override
    public int getIndexCount() {
      return 0;
    }
  },
  CAM_ROLL {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      pProperty.setEnabled(true);
      pProperty.setOffset(pFlame.getCamRoll());
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      pFlame.setCamRoll(pValue);
    }

    @Override
    public MotionCurve getMotionCurve(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      return pFlame.getCamRollCurve();
    }

    @Override
    public int getIndexCount() {
      return 0;
    }
  },
  CAM_PITCH {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      pProperty.setEnabled(true);
      pProperty.setOffset(pFlame.getCamPitch());
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      pFlame.setCamPitch(pValue);
    }

    @Override
    public MotionCurve getMotionCurve(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      return pFlame.getCamPitchCurve();
    }

    @Override
    public int getIndexCount() {
      return 0;
    }
  },
  CAM_YAW {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      pProperty.setEnabled(true);
      pProperty.setOffset(pFlame.getCamYaw());
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      pFlame.setCamYaw(pValue);
    }

    @Override
    public MotionCurve getMotionCurve(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      return pFlame.getCamYawCurve();
    }

    @Override
    public int getIndexCount() {
      return 0;
    }
  };

  public abstract void init(LeapMotionConnectedProperty pProperty, Flame pFlame);

  private static Variation getVariationByIndex(LeapMotionConnectedProperty pProperty, Flame pFlame) {
    XForm xform = getXFormByIndex(pProperty, pFlame);
    if (xform != null) {
      if (pProperty.getIndex2() >= 0 && pProperty.getIndex2() < xform.getVariationCount()) {
        return xform.getVariation(pProperty.getIndex2());
      }
      else {
        return null;
      }
    }
    else {
      return null;
    }
  }

  private static XForm getXFormByIndex(LeapMotionConnectedProperty pProperty, Flame pFlame) {
    Layer layer = pFlame.getFirstLayer();
    if (pProperty.getIndex1() >= 0 && pProperty.getIndex1() < layer.getXForms().size()) {
      return layer.getXForms().get(pProperty.getIndex1());
    }
    else if (pProperty.getIndex1() >= layer.getXForms().size() && pProperty.getIndex1() < (layer.getXForms().size() + layer.getFinalXForms().size())) {
      return layer.getFinalXForms().get(pProperty.getIndex1() - layer.getXForms().size());
    }
    else {
      return null;
    }
  }

  public abstract void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue);

  public abstract int getIndexCount();

  public abstract MotionCurve getMotionCurve(LeapMotionConnectedProperty pProperty, Flame pFlame);

}
