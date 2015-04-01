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

import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class FieldBuilder {
  protected static final int LABEL_WIDTH = 68;
  protected static final int LABEL_HEIGHT = 22;
  protected static final int EDITFIELD_WIDTH = 100;
  protected static final int CHECKBOX_WIDTH = 200;
  protected static final int COMBOBOX_WIDTH = 160;
  protected static final int FIELD_HEIGHT = 24;
  protected static final int H_BORDER = 4;

  protected final ContainerBuilder parent;
  protected String caption;
  protected String propertyName;

  protected FieldBuilder(ContainerBuilder pParent) {
    parent = pParent;
  }

  public FieldBuilder withCaption(String pCaption) {
    caption = pCaption;
    return this;
  }

  public FieldBuilder withPropertyName(String pPropertyName) {
    propertyName = pPropertyName;
    return this;
  }

  public String getCaption() {
    return caption;
  }

  public String getPropertyName() {
    return propertyName;
  }

  protected void createLabel(JPanel pPanel, int xOff, int yOff) {
    JLabel label = new JLabel();
    label.setText(caption);
    label.setSize(new Dimension(LABEL_WIDTH, LABEL_HEIGHT));
    label.setPreferredSize(new Dimension(LABEL_WIDTH, LABEL_HEIGHT));
    label.setLocation(new Point(xOff, yOff));
    label.setFont(new Font("Dialog", Font.BOLD, 10));
    label.setBounds(xOff, yOff, LABEL_WIDTH, LABEL_HEIGHT);
    pPanel.add(label);
  }

  public abstract Object buildPart(ScriptParamsForm pForm, JPanel pPanel, int xOff, int yOff);

}
