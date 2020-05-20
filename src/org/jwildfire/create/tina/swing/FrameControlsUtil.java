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

import javax.swing.*;
import java.lang.reflect.Field;

public class FrameControlsUtil {
  private final TinaController ctrl;

  public FrameControlsUtil(TinaController ctrl) {
    this.ctrl = ctrl;
  }

  public void updateControl(Object pTarget, JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
    if(pTextField!=null) {
      if(pTarget!=null) {
        pTextField.setText(Tools.doubleToString(getPropertyValue(pTarget, pProperty)));
      }
      else {
        pTextField.setText(null);
      }
    }
    if(pSlider!=null) {
      if(pTarget!=null) {
        pSlider.setValue(Tools.FTOI(getPropertyValue(pTarget, pProperty) * pSliderScale));
      }
      else {
        pSlider.setValue(0);
      }
    }
  }

  private double getPropertyValue(Object pTarget, String pProperty) {
    Class<?> cls = pTarget.getClass();
    Field field;
    try {
      field = cls.getDeclaredField(pProperty);
      field.setAccessible(true);
      Class<?> fieldCls = field.getType();
      if (fieldCls == double.class || fieldCls == Double.class) {
        Double dValue = field.getDouble(pTarget);
        return dValue!=null ? dValue.doubleValue() : 0.0;
      }
      else if (fieldCls == int.class || fieldCls == Integer.class) {
        Integer iValue = field.getInt(pTarget);
        return iValue!=null ? iValue.doubleValue() : 0.0;
      }
      else {
        throw new IllegalStateException();
      }
    }
    catch (Throwable ex) {
      ex.printStackTrace();
      return 0.0;
    }
  }

  public void sliderChanged(Object pTarget, JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
    double propValue = pSlider.getValue() / pSliderScale;
    if(pTextField!=null) {
      pTextField.setText(Tools.doubleToString(propValue));
    }
    Class<?> cls = pTarget.getClass();
    Field field;
    try {
      field = cls.getDeclaredField(pProperty);
      field.setAccessible(true);
      Class<?> fieldCls = field.getType();
      if (fieldCls == double.class || fieldCls == Double.class) {
        field.setDouble(pTarget, propValue);
      }
      else if (fieldCls == int.class || fieldCls == Integer.class) {
        field.setInt(pTarget, Tools.FTOI(propValue));
      }
      else {
        throw new IllegalStateException();
      }
    }
    catch (Throwable ex) {
      ex.printStackTrace();
    }
  }

  public void textFieldChanged(Object pTarget, JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
    double propValue = Tools.stringToDouble(pTextField.getText());
    if(pSlider!=null) {
      pSlider.setValue(Tools.FTOI(propValue * pSliderScale));
    }
    Class<?> cls = pTarget.getClass();
    Field field;
    try {
      field = cls.getDeclaredField(pProperty);
      field.setAccessible(true);
      Class<?> fieldCls = field.getType();
      if (fieldCls == double.class || fieldCls == Double.class) {
        field.setDouble(pTarget, propValue);
      }
      else if (fieldCls == int.class || fieldCls == Integer.class) {
        field.setInt(pTarget, Tools.FTOI(propValue));
      }
      else {
        throw new IllegalStateException();
      }
    }
    catch (Throwable ex) {
      ex.printStackTrace();
    }
  }
}
