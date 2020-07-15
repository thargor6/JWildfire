/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2020 Andreas Maschke

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
package org.jwildfire.create.tina.swing;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.motion.MotionCurve;

import javax.swing.*;
import java.lang.reflect.Field;

public class FrameControlsUtil {
  private final TinaController ctrl;

  public FrameControlsUtil(TinaController ctrl) {
    this.ctrl = ctrl;
  }

  public void updateControl(JSlider pSlider, JWFNumberField pTextField, String pProperty, Object pPropertyValue, MotionCurve pCurve, double pSliderScale) {
    Object curveValue = null;
    if (pPropertyValue != null && pCurve != null && pCurve.isEnabled()) {
      if (pPropertyValue instanceof Double) {
        curveValue = Double.valueOf(AnimationService.evalCurve(ctrl.getCurrFrame(), pCurve));
      }
      else if (pPropertyValue instanceof Integer) {
        curveValue = Integer.valueOf(Tools.FTOI(AnimationService.evalCurve(ctrl.getCurrFrame(), pCurve)));
      }
    }

    if (pTextField != null) {
      if (pPropertyValue != null) {
        if (pPropertyValue instanceof Double) {
          pTextField.setText(Tools.doubleToString((Double)(curveValue!=null ? curveValue : pPropertyValue)));
        } else if (pPropertyValue instanceof Integer) {
          pTextField.setText(((Integer)curveValue!=null ? curveValue : pPropertyValue).toString());
        } else {
          pTextField.setText(pPropertyValue.toString());
        }
      } else {
        pTextField.setText(null);
      }
    }
    if (pSlider != null) {
      if (pPropertyValue != null) {
        if (pPropertyValue instanceof Double) {
          pSlider.setValue(Tools.FTOI(((Double)(curveValue!=null ? curveValue : pPropertyValue)).doubleValue() * pSliderScale));
        }
        else if (pPropertyValue instanceof Integer) {
          pSlider.setValue(Tools.FTOI(((Integer)(curveValue!=null ? curveValue : pPropertyValue)).intValue() * pSliderScale));
        }
        else {
          pSlider.setValue(0);
        }
      } else {
        pSlider.setValue(0);
      }
    }
  }

  public void updateControl(Object pTarget, JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
    double propertyValue = pTarget != null ? getEvaluatedPropertyValue(pTarget, pProperty) : 0.0;
    if (pTextField != null) {
      if (pTarget != null) {
        pTextField.setText(Tools.doubleToString(propertyValue));
      } else {
        pTextField.setText(null);
      }
    }
    if (pSlider != null) {
      if (pTarget != null) {
        pSlider.setValue(Tools.FTOI(propertyValue * pSliderScale));
      } else {
        pSlider.setValue(0);
      }
    }
  }

  public String getAffinePropertyName(XForm xform, String property, boolean isPostTransform) {
    if (isPostTransform) {
      switch (xform.getEditPlane()) {
        case YZ:
          return "yzPostCoeff" + property;
        case ZX:
          return "zxPostCoeff" + property;
        case XY:
        default:
          return "xyPostCoeff" + property;
      }
    } else {
      switch (xform.getEditPlane()) {
        case YZ:
          return "yzCoeff" + property;
        case ZX:
          return "zxCoeff" + property;
        case XY:
        default:
          return "xyCoeff" + property;
      }
    }
  }

  public double getAffineProperty(XForm xform, String property, boolean isPostTransform) {
    return getEvaluatedPropertyValue(xform, getAffinePropertyName(xform, property, isPostTransform));
  }

  public void setAffineProperty(XForm xform, String property, boolean isPostTransform, double value) {
    applyValueChange(xform, getAffinePropertyName(xform, property, isPostTransform), value);
    xform.notifyCoeffChange();
  }

  public double getEvaluatedPropertyValue(Object pTarget, String pProperty) {
    try {
      MotionCurve curve = getMotionCurve(pTarget, pProperty);
      if (curve != null && curve.isEnabled()) {
        return AnimationService.evalCurve(ctrl.getCurrFrame(), curve);
      } else {
        return getRawPropertyValue(pTarget, pProperty);
      }
    } catch (Throwable ex) {
      ex.printStackTrace();
      return 0.0;
    }
  }

  public double getRawPropertyValue(Object pTarget, String pProperty) {
    try {
      Class<?> cls = pTarget.getClass();
      Field field = cls.getDeclaredField(pProperty);
      field.setAccessible(true);
      Class<?> fieldCls = field.getType();
      if (fieldCls == double.class || fieldCls == Double.class) {
        Double dValue = field.getDouble(pTarget);
        return dValue != null ? dValue.doubleValue() : 0.0;
      } else if (fieldCls == int.class || fieldCls == Integer.class) {
        Integer iValue = field.getInt(pTarget);
        return iValue != null ? iValue.doubleValue() : 0.0;
      } else {
        throw new IllegalStateException();
      }
    } catch (Throwable ex) {
      ex.printStackTrace();
      return 0.0;
    }
  }

  private MotionCurve getMotionCurve(Object pTarget, String pProperty) {
    Class<?> cls = pTarget.getClass();
    try {
      Field field;
      try {
        field = cls.getDeclaredField(pProperty + "Curve");
      } catch (Exception ex) {
        field = null;
      }
      if (field != null) {
        field.setAccessible(true);
        Class<?> fieldCls = field.getType();
        if (fieldCls == MotionCurve.class) {
          MotionCurve curve = (MotionCurve) field.get(pTarget);
          return curve;
        }
      }
    } catch (Throwable ex) {
      ex.printStackTrace();
    }
    return null;
  }

  public void valueChangedBySlider(Object pTarget, JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
    double propValue = pSlider.getValue() / pSliderScale;
    if (pTextField != null) {
      pTextField.setText(Tools.doubleToString(propValue));
    }
    applyValueChange(pTarget, pProperty, propValue);
  }

  public void valueChangedByTextField(Object pTarget, JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale, double pDeltaValue) {
    double propValue = Tools.stringToDouble(pTextField.getText()) + pDeltaValue;
    if (pSlider != null) {
      pSlider.setValue(Tools.FTOI(propValue * pSliderScale));
    }
    applyValueChange(pTarget, pProperty, propValue);
  }

  public void applyValueChange(Object pTarget, String pProperty, double propValue) {
    Class<?> cls = pTarget.getClass();
    Field field;
    try {
      field = cls.getDeclaredField(pProperty);
      field.setAccessible(true);
      Class<?> fieldCls = field.getType();
      double oldValue;
      if (fieldCls == double.class || fieldCls == Double.class) {
        Double ov = field.getDouble(pTarget);
        oldValue = ov != null ? ov.doubleValue() : 0.0;
        field.setDouble(pTarget, propValue);
      } else if (fieldCls == int.class || fieldCls == Integer.class) {
        Integer ov = field.getInt(pTarget);
        oldValue = ov != null ? ov.intValue() : 0.0;
        field.setInt(pTarget, Tools.FTOI(propValue));
      } else {
        throw new IllegalStateException();
      }
      MotionCurve curve = getMotionCurve(pTarget, pProperty);
      updateKeyFrame(propValue, oldValue, curve);
    } catch (Throwable ex) {
      ex.printStackTrace();
    }
  }

  public void updateKeyFrame(double propValue, double oldValue, MotionCurve curve) {
    if (curve != null) {
      int frame = ctrl.getCurrFrame();
      // ensure that there is a keyframe at the first frame when we are somewhere in the middle of an animation,
      // also activate the motion curve
      if (frame > 1 && !curve.isEnabled()) {
        curve.setEnabled(true);
        if (!curve.hasKeyFrame(1)) {
          curve.addKeyFrame(1, oldValue);
        }
      }
      // add/modify the actual keyframe
      {
        int idx = curve.indexOfKeyFrame(frame);
        if (idx >= 0) {
          curve.getY()[idx] = propValue;
        } else {
          curve.addKeyFrame(frame, propValue);
        }
      }
      curve.autoFitRange();
    }
  }
}
