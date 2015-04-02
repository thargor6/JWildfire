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
import javax.swing.JTextField;

public class TextFieldBuilder extends FieldBuilder {
  private String initialValue = "";

  protected TextFieldBuilder(ContainerBuilder pParent) {
    super(pParent);
  }

  @Override
  public TextFieldBuilder withCaption(String pCaption) {
    super.withCaption(pCaption);
    return this;
  }

  @Override
  public TextFieldBuilder withPropertyName(String pPropertyName) {
    super.withPropertyName(pPropertyName);
    return this;
  }

  public ContainerBuilder closeTextField() {
    return parent;
  }

  public TextFieldBuilder withInitialValue(String pInitialValue) {
    initialValue = pInitialValue;
    return this;
  }

  @Override
  public JTextField buildPart(ScriptParamsForm pForm, JPanel pPanel, int xOff, int yOff) {
    createLabel(pPanel, xOff, yOff);
    JTextField textField = new JTextField();
    textField.setText(initialValue);
    textField.setSize(new Dimension(EDITFIELD_WIDTH, FIELD_HEIGHT));
    textField.setPreferredSize(new Dimension(100, 24));
    textField.setLocation(new Point(xOff + LABEL_WIDTH + H_BORDER, yOff));
    textField.setFont(new Font("Dialog", Font.PLAIN, 10));
    textField.setBounds(xOff + LABEL_WIDTH + H_BORDER, yOff, EDITFIELD_WIDTH, FIELD_HEIGHT);
    textField.setName(propertyName);
    pPanel.add(textField);
    return textField;
  }

}
