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

import javax.swing.JComboBox;
import javax.swing.JPanel;

public class ComboBoxBuilder extends FieldBuilder {
  private String[] choices = new String[0];
  private String initialValue = "";

  protected ComboBoxBuilder(ContainerBuilder pParent) {
    super(pParent);
  }

  @Override
  public ComboBoxBuilder withCaption(String pCaption) {
    super.withCaption(pCaption);
    return this;
  }

  @Override
  public ComboBoxBuilder withPropertyName(String pPropertyName) {
    super.withPropertyName(pPropertyName);
    return this;
  }

  public ComboBoxBuilder withChoices(String pChoices[]) {
    choices = pChoices != null ? pChoices : new String[0];
    return this;
  }

  public ComboBoxBuilder withInitialValue(String pInitialValue) {
    initialValue = pInitialValue;
    return this;
  }

  public ContainerBuilder closeComboBox() {
    return parent;
  }

  public JComboBox buildPart(ScriptParamsForm pForm, JPanel pPanel, int xOff, int yOff) {
    createLabel(pPanel, xOff, yOff);

    JComboBox comboBox = new JComboBox();
    comboBox.setMaximumRowCount(32);
    comboBox.setSize(new Dimension(COMBOBOX_WIDTH, FIELD_HEIGHT));
    comboBox.setPreferredSize(new Dimension(100, 24));
    comboBox.setLocation(new Point(xOff + LABEL_WIDTH + H_BORDER, yOff));
    comboBox.setFont(new Font("Dialog", Font.PLAIN, 10));
    comboBox.setBounds(xOff + LABEL_WIDTH + H_BORDER, yOff, COMBOBOX_WIDTH, FIELD_HEIGHT);
    comboBox.setName(propertyName);
    comboBox.removeAllItems();
    if (choices != null) {
      for (String choice : choices) {
        comboBox.addItem(choice);
      }
    }
    if (initialValue != null) {
      comboBox.setSelectedItem(initialValue);
    }
    else {
      comboBox.setSelectedIndex(0);
    }

    pPanel.add(comboBox);
    return comboBox;
  }
}
