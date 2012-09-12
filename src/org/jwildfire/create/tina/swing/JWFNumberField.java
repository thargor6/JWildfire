/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2012 Andreas Maschke

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

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.jwildfire.base.Tools;

public class JWFNumberField extends JSpinner {
  private static final double DFLT_STEP = 0.5;
  private static final double DFLT_MOUSE_THRESHOLD = 10;
  private static final long serialVersionUID = 1L;
  private double mouseThreshold;
  private double valueStep;
  private boolean hasMinValue = false;
  private boolean hasMaxValue = false;
  private double minValue = 0.0;
  private double maxValue = 0.0;
  private int xMouseOrigin = 0;
  private double originValue = 0.0;
  private boolean onlyIntegers = false;
  private SpinnerNumberModel spinnerModel;
  private boolean mouseAdjusting = false;
  private int mouseChangeCount = 0;

  public JWFNumberField() {
    spinnerModel = new SpinnerNumberModel(new Double(0), null, null, new Double(valueStep));
    setModel(spinnerModel);
    setValueStep(DFLT_STEP);
    setMouseThreshold(DFLT_MOUSE_THRESHOLD);
    addEvents();
    ((JSpinner.DefaultEditor) getEditor()).getTextField().setHorizontalAlignment(JTextField.LEADING);
    setCursor();
  }

  public double getValueStep() {
    return valueStep;
  }

  public void setValueStep(double valueStep) {
    this.valueStep = valueStep;
    spinnerModel.setStepSize(valueStep);
  }

  private void setCursor() {
    ((JSpinner.DefaultEditor) getEditor()).getTextField().setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
  }

  private void addEvents() {
    ((JSpinner.DefaultEditor) getEditor()).getTextField().addMouseListener(
        new java.awt.event.MouseAdapter() {

          @Override
          public void mousePressed(MouseEvent e) {
            xMouseOrigin = e.getX();
            originValue = (Double) getValue();
            mouseAdjusting = true;
            mouseChangeCount = 0;
          }

          @Override
          public void mouseReleased(MouseEvent e) {
            mouseAdjusting = false;
          }

        });

    ((JSpinner.DefaultEditor) getEditor()).getTextField().addMouseMotionListener(
        new java.awt.event.MouseMotionAdapter() {

          @Override
          public void mouseDragged(MouseEvent e) {
            double threshold = mouseThreshold;
            if (threshold < 1.0) {
              threshold = 1.0;
            }
            double MINMOVE = 3;
            double dx = xMouseOrigin - e.getX();
            if (mouseChangeCount > 0 || dx > MINMOVE || dx < -MINMOVE) {
              if (dx > 0) {
                dx = (dx /*- MINMOVE*/) - threshold;
              }
              else {
                dx = (dx /*+ MINMOVE*/) + threshold;
              }
              double value = (originValue) - dx * valueStep;
              if (hasMinValue && value < minValue) {
                value = minValue;
              }
              else if (hasMaxValue && value > maxValue) {
                value = maxValue;
              }
              setValue(value);
              mouseChangeCount++;
            }
          }

        });

  }

  public void setText(String pText) {
    if (pText != null && pText.length() > 0) {
      double val = Tools.stringToDouble(pText);
      if (onlyIntegers) {
        val = Tools.FTOI(val);
      }
      setValue(val);
    }
    else
      setValue(0.0);
  }

  public String getText() {
    return Tools.doubleToString((Double) getValue());
  }

  public double getMouseThreshold() {
    return mouseThreshold;
  }

  public void setMouseThreshold(double mouseThreshold) {
    this.mouseThreshold = mouseThreshold;
  }

  public boolean isHasMinValue() {
    return hasMinValue;
  }

  public void setHasMinValue(boolean hasMinValue) {
    this.hasMinValue = hasMinValue;
    spinnerModel.setMinimum(hasMinValue ? minValue : null);
  }

  public boolean isHasMaxValue() {
    return hasMaxValue;
  }

  public void setHasMaxValue(boolean hasMaxValue) {
    this.hasMaxValue = hasMaxValue;
    spinnerModel.setMaximum(hasMaxValue ? maxValue : null);
  }

  public double getMinValue() {
    return minValue;
  }

  public void setMinValue(double minValue) {
    this.minValue = minValue;
    spinnerModel.setMinimum(hasMinValue ? minValue : null);
  }

  public double getMaxValue() {
    return maxValue;
  }

  public void setMaxValue(double maxValue) {
    this.maxValue = maxValue;
    spinnerModel.setMaximum(hasMaxValue ? maxValue : null);
  }

  public void setEditable(boolean enabled) {
    setEnabled(enabled);
  }

  public boolean isOnlyIntegers() {
    return onlyIntegers;
  }

  public void setOnlyIntegers(boolean onlyIntegers) {
    this.onlyIntegers = onlyIntegers;
    if (onlyIntegers) {
      spinnerModel.setStepSize(1.0);
    }
    else {
      spinnerModel.setStepSize(valueStep);
    }
  }

  @Override
  public Object getValue() {
    Object val = super.getValue();
    if (val != null && val instanceof Double && onlyIntegers) {
      val = new Double(Tools.FTOI((Double) val));
    }
    return val;
  }

  public boolean isMouseAdjusting() {
    return mouseAdjusting;
  }

  public int getMouseChangeCount() {
    return mouseChangeCount;
  }

}
