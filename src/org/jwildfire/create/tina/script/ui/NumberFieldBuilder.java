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
package org.jwildfire.create.tina.script.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;

import javax.swing.JPanel;

import org.jwildfire.create.tina.swing.JWFNumberField;

public class NumberFieldBuilder extends FieldBuilder {
  private Double initialValue = Double.valueOf(0.0);

  protected NumberFieldBuilder(ContainerBuilder pParent) {
    super(pParent);
  }

  @Override
  public NumberFieldBuilder withCaption(String pCaption) {
    super.withCaption(pCaption);
    return this;
  }

  @Override
  public NumberFieldBuilder withPropertyName(String pPropertyName) {
    super.withPropertyName(pPropertyName);
    return this;
  }

  public ContainerBuilder closeNumberField() {
    return parent;
  }

  public NumberFieldBuilder withInitialValue(Double pInitialValue) {
    initialValue = pInitialValue;
    return this;
  }

  @Override
  public JWFNumberField buildPart(ScriptParamsForm pForm, JPanel pPanel, int xOff, int yOff) {
    createLabel(pPanel, xOff, yOff);
    JWFNumberField numberField = new JWFNumberField();
    numberField.setValueStep(1.0);
    numberField.setText("");
    numberField.setSize(new Dimension(EDITFIELD_WIDTH, FIELD_HEIGHT));
    numberField.setPreferredSize(new Dimension(100, 24));
    numberField.setLocation(new Point(xOff + LABEL_WIDTH + H_BORDER, yOff));
    numberField.setFont(new Font("Dialog", Font.PLAIN, 10));
    numberField.setBounds(xOff + LABEL_WIDTH + H_BORDER, yOff, EDITFIELD_WIDTH, FIELD_HEIGHT);
    numberField.setName(propertyName);
    if (initialValue != null) {
      numberField.setValue(initialValue.doubleValue());
    }
    pPanel.add(numberField);
    return numberField;
  }

}
