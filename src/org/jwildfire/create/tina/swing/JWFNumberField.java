/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2023 Andreas Maschke

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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Locale;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.fabs;

public class JWFNumberField extends JPanel implements MotionCurveEditor {
  private static final double DFLT_STEP = 0.5;
  private static final double DFLT_MOUSE_SPEED = 0.40;
  private static final long serialVersionUID = 1L;
  private final SpinnerNumberModel spinnerModel;
  private final JSpinner spinnerField;
  private final JButton motionCurveBtn;
  private double mouseSpeed;
  private double valueStep;
  private boolean hasMinValue = false;
  private boolean hasMaxValue = false;
  private double minValue = 0.0;
  private double maxValue = 0.0;
  private int xMouseOrigin = 0;
  private double originValue = 0.0;
  private boolean onlyIntegers = false;
  private boolean mouseAdjusting = false;
  private int mouseChangeCount = 0;
  private boolean withMotionCurve = false;
  private String motionPropertyName = "";
  private String linkedMotionControlName = "";
  private Component linkedMotionControl = null;
  private String linkedLabelControlName = "";
  private Component linkedLabelControl = null;

  public JWFNumberField() {
    setLayout(new BorderLayout());

    motionCurveBtn = new JWFNumberFieldButton(this);
    motionCurveBtn.setText("");
    motionCurveBtn.setToolTipText("Create/edit a motion curve");
    motionCurveBtn.setFont(new Font("Dialog", Font.BOLD, 8));
    motionCurveBtn.setIcon(
        new ImageIcon(
            getClass().getResource("/org/jwildfire/swing/icons/new/curve-money2-empty.png")));

    add(motionCurveBtn, BorderLayout.WEST);
    spinnerModel = new SpinnerNumberModel(new Double(0), null, null, new Double(valueStep));
    spinnerField =
        new JSpinner(spinnerModel) {
          @Override
          protected JComponent createEditor(SpinnerModel model) {
            final Locale currLocale = Locale.getDefault();
            try {
              Locale.setDefault(Locale.US);
              return new NumberEditor(this, "0.#######");
            } finally {
              Locale.setDefault(currLocale);
            }
          }
        };
    add(spinnerField, BorderLayout.CENTER);

    setValueStep(DFLT_STEP);
    setMouseSpeed(DFLT_MOUSE_SPEED);
    addEvents();
    ((JSpinner.DefaultEditor) spinnerField.getEditor())
        .getTextField()
        .setHorizontalAlignment(JTextField.LEADING);

    configureMotionCurveBtn(withMotionCurve);

    setCursor();
  }

  private void configureMotionCurveBtn(boolean visible) {
    Dimension size = visible ? new Dimension(22, 24) : new Dimension(0, 0);
    motionCurveBtn.setPreferredSize(size);
    motionCurveBtn.setMinimumSize(size);
    motionCurveBtn.setMaximumSize(size);
    motionCurveBtn.setVisible(visible);
  }

  public double getValueStep() {
    return valueStep;
  }

  public void setValueStep(double valueStep) {
    this.valueStep = valueStep;
    spinnerModel.setStepSize(valueStep);
  }

  private void setCursor() {
    ((JSpinner.DefaultEditor) spinnerField.getEditor())
        .getTextField()
        .setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
  }

  private void addEvents() {
    ((JSpinner.DefaultEditor) spinnerField.getEditor())
        .getTextField()
        .addMouseListener(
            new java.awt.event.MouseAdapter() {

              @Override
              public void mousePressed(MouseEvent e) {
                if (!isEnabled()) return;
                xMouseOrigin = e.getX();
                Object value = getValue();
                if (value != null && value instanceof Double) {
                  originValue = (Double) value;
                } else if (value != null && value instanceof Integer) {
                  originValue = (Integer) value;
                } else {
                  originValue = 0;
                }
                mouseAdjusting = true;
                mouseChangeCount = 0;
              }

              @Override
              public void mouseReleased(MouseEvent e) {
                mouseAdjusting = false;

                boolean hasChanged = false;
                Object value = getValue();
                if (value != null && value instanceof Double) {
                  double currValue = (Double) value;
                  hasChanged = fabs(currValue - originValue) > EPSILON;
                } else if (value != null && value instanceof Integer) {
                  int currValue = (Integer) value;
                  hasChanged = currValue != originValue;
                }
                if (hasChanged) {
                  ChangeListener[] changeListeners = spinnerField.getChangeListeners();
                  if (changeListeners != null && changeListeners.length > 0) {
                    changeListeners[0].stateChanged(new ChangeEvent(spinnerField));
                  }
                }
              }
            });

    ((JSpinner.DefaultEditor) spinnerField.getEditor())
        .getTextField()
        .addMouseMotionListener(
            new java.awt.event.MouseMotionAdapter() {

              @Override
              public void mouseDragged(MouseEvent e) {
                if (!isEnabled()) return;
                double MINMOVE = 3;
                double dx = xMouseOrigin - e.getX();
                if (mouseChangeCount > 0 || dx > MINMOVE || dx < -MINMOVE) {
                  double value =
                      (originValue)
                          - dx
                              * valueStep
                              * mouseSpeed
                              * EditingPrecision.getCurrent().getMultiplier();
                  if (hasMinValue && value < minValue) {
                    value = minValue;
                  } else if (hasMaxValue && value > maxValue) {
                    value = maxValue;
                  }
                  if(onlyIntegers) {
                    value = Tools.FTOI(value);
                  } 
                  spinnerField.setValue(value);
                  mouseChangeCount++;
                }
              }
            });

    ((JSpinner.DefaultEditor) spinnerField.getEditor())
        .getTextField()
        .addMouseWheelListener(
            new java.awt.event.MouseWheelListener() {

              @Override
              public void mouseWheelMoved(MouseWheelEvent e) {
                if (!isEnabled()) return;
                int notches = e.getWheelRotation();
                if (notches != 0) {
                  if (onlyIntegers) {
                    setValue(getDoubleValue() - (notches > 0 ? 1 : -1));
                  } else {
                    setValue(getDoubleValue() - notches * valueStep);
                  }
                }
              }
            });
  }

  public String getText() {
    Object value = getValue();
    if (value instanceof Double) {
      return Tools.doubleToString((Double) getValue());
    } else {
      return String.valueOf(value);
    }
    // return ((JSpinner.DefaultEditor) spinnerField.getEditor()).getTextField().getText();
  }

  public void setText(String pText) {
    if (pText != null && pText.length() > 0) {
      double val = Tools.stringToDouble(pText);
      if (onlyIntegers) {
        val = Tools.FTOI(val);
      }
      spinnerField.setValue(val);
    } else spinnerField.setValue(0.0);
  }

  public void setToolTipText(String pText) {
    spinnerField.setToolTipText(pText);
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
    } else {
      spinnerModel.setStepSize(valueStep);
    }
  }

  public Object getValue() {
    Object val = spinnerField.getValue();
    if (val != null && val instanceof Double && onlyIntegers) {
      val = new Double(Tools.FTOI((Double) val));
    }
    return val;
  }

  public void setValue(double value) {
    spinnerField.setValue(value);
  }

  public Integer getIntValue() {
    Object value = getValue();
    if (value instanceof Double) {
      return Tools.FTOI((Double) value);
    } else {
      return (Integer) value;
    }
  }

  public Double getDoubleValue() {
    Object value = getValue();
    if (value instanceof Integer) {
      return 1.0 * ((Integer) value);
    } else {
      return (Double) value;
    }
  }

  public boolean isMouseAdjusting() {
    return mouseAdjusting;
  }

  public int getMouseChangeCount() {
    return mouseChangeCount;
  }

  public void addChangeListener(ChangeListener listener) {
    spinnerField.addChangeListener(listener);
  }

  @Override
  public void setFont(Font font) {
    super.setFont(font);
    if (spinnerField != null) {
      spinnerField.setFont(font);
    }
  }

  public void addActionListener(ActionListener listener) {
    motionCurveBtn.addActionListener(listener);
  }

  @Override
  public boolean isWithMotionCurve() {
    return withMotionCurve;
  }

  @Override
  public void setWithMotionCurve(boolean pWithMotionCurve) {
    withMotionCurve = pWithMotionCurve;
    configureMotionCurveBtn(withMotionCurve);
  }

  public String getMotionPropertyName() {
    return motionPropertyName;
  }

  public void setMotionPropertyName(String pMotionPropertyName) {
    motionPropertyName = pMotionPropertyName;
  }

  public String getLinkedMotionControlName() {
    return linkedMotionControlName;
  }

  public void setLinkedMotionControlName(String pLinkedMotionControlName) {
    linkedMotionControlName = pLinkedMotionControlName;
    linkedMotionControl = null;
  }

  public Component getLinkedMotionControl() {
    if (linkedMotionControlName != null
        && linkedMotionControlName.length() > 0
        && linkedMotionControl == null) {
      for (Component component : getParent().getComponents()) {
        if (linkedMotionControlName.equals(component.getName())) {
          linkedMotionControl = component;
          break;
        }
      }
    }
    return linkedMotionControl;
  }

  public String getLinkedLabelControlName() {
    return linkedLabelControlName;
  }

  public void setLinkedLabelControlName(String pLinkedLabelControlName) {
    linkedLabelControlName = pLinkedLabelControlName;
    linkedLabelControl = null;
  }

  public JLabel _findControl(Container parent, String name) {
    for (Component component : parent.getComponents()) {
      if (component instanceof JLabel && name.equals(component.getName())) {
        return (JLabel) component;
      } else if (component instanceof Container) {
        JLabel res = _findControl((Container) component, name);
        if (res != null) {
          return res;
        }
      }
    }
    return null;
  }

  public JLabel getLinkedLabelControl() {
    if (linkedLabelControlName != null
        && linkedLabelControlName.length() > 0
        && linkedLabelControl == null) {
      Container parent = getParent();
      while (parent != null && linkedLabelControl == null) {
        linkedLabelControl = _findControl(parent, linkedLabelControlName);
        parent = parent.getParent();
      }
    }
    return (JLabel) linkedLabelControl;
  }

  public boolean isEnabled() {
    return spinnerField.isEnabled();
  }

  public void setEnabled(boolean enabled) {
    spinnerField.setEnabled(enabled);
    motionCurveBtn.setEnabled(enabled);
  }

  public void enableMotionCurveBtn(boolean enabled) {
    motionCurveBtn.setEnabled(enabled);
  }

  public double getMouseSpeed() {
    return mouseSpeed;
  }

  public void setMouseSpeed(double pMouseSpeed) {
    mouseSpeed = pMouseSpeed;
  }

  public JSpinner exposeInternalSpinnerField() {
    return spinnerField;
  }

  public JButton exposeInternalMotionCurveBtn() {
    return motionCurveBtn;
  }

  public void setHasCurve(boolean pHasCurve) {
    if (motionCurveBtn != null) {
      if (pHasCurve) {
        motionCurveBtn.setIcon(
            new ImageIcon(
                getClass().getResource("/org/jwildfire/swing/icons/new/curve-money2a.png")));
      } else {
        motionCurveBtn.setIcon(
            new ImageIcon(
                getClass().getResource("/org/jwildfire/swing/icons/new/curve-money2-empty.png")));
      }
    }
  }

  public static class JWFNumberFieldButton extends JButton {
    private static final long serialVersionUID = 1L;
    private JWFNumberField owner;

    public JWFNumberFieldButton() {
      super();
    }

    public JWFNumberFieldButton(JWFNumberField pOwner) {
      super();
      owner = pOwner;
    }

    public JWFNumberField getOwner() {
      return owner;
    }

    public void setOwner(JWFNumberField pOwner) {
      owner = pOwner;
    }
  }
}
