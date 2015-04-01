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

import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class CheckBoxBuilder extends FieldBuilder {
  private Boolean initialValue = false;

  protected CheckBoxBuilder(ContainerBuilder pParent) {
    super(pParent);
  }

  @Override
  public CheckBoxBuilder withCaption(String pCaption) {
    super.withCaption(pCaption);
    return this;
  }

  @Override
  public CheckBoxBuilder withPropertyName(String pPropertyName) {
    super.withPropertyName(pPropertyName);
    return this;
  }

  public ContainerBuilder closeCheckBox() {
    return parent;
  }

  public CheckBoxBuilder withInitialValue(Boolean pInitialValue) {
    initialValue = pInitialValue;
    return this;
  }

  public JCheckBox buildPart(ScriptParamsForm pForm, JPanel pPanel, int xOff, int yOff) {
    JCheckBox checkBox = new JCheckBox(caption);
    checkBox.setSize(new Dimension(CHECKBOX_WIDTH, FIELD_HEIGHT));
    checkBox.setPreferredSize(new Dimension(100, 24));
    checkBox.setLocation(new Point(xOff, yOff));
    checkBox.setFont(new Font("Dialog", Font.PLAIN, 10));
    checkBox.setBounds(xOff, yOff, CHECKBOX_WIDTH, FIELD_HEIGHT);
    checkBox.setName(propertyName);
    if (initialValue != null) {
      checkBox.setSelected(initialValue);
    }
    pPanel.add(checkBox);
    return checkBox;
  }

}
