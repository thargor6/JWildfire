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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.jwildfire.create.tina.swing.JWFNumberField;

public class ContainerBuilder {
  private final FormBuilder parent;
  private String caption;
  private final List<NumberFieldBuilder> editFields = new ArrayList<NumberFieldBuilder>();

  public ContainerBuilder(FormBuilder pParent) {
    parent = pParent;
  }

  public ContainerBuilder withCaption(String pCaption) {
    caption = pCaption;
    return this;
  }

  public FormBuilder closeContainer() {
    return parent;
  }

  public NumberFieldBuilder openNumberField(String pPropertyName, String pCaption) {
    NumberFieldBuilder editField = new NumberFieldBuilder(this);
    editField.withPropertyName(pPropertyName);
    editField.withCaption(pCaption);
    editFields.add(editField);
    return editField;
  }

  public ContainerBuilder withNumberField(String pPropertyName, String pCaption) {
    openNumberField(pPropertyName, pCaption);
    return this;
  }

  public String getCaption() {
    return caption;
  }

  public void buildPart(ScriptParamsForm pForm) {
    JPanel panel = pForm.addContainer(caption);
    int boxWidth = 200;
    int boxHeight = 24;
    int border = 8;
    int x0 = border;
    int y0 = border;
    int colCount = 2;

    for (int i = 0; i < editFields.size(); i++) {
      NumberFieldBuilder editField = editFields.get(i);
      int x = x0 + (i % colCount) * (boxWidth + border);
      int y = y0 + (i / colCount) * (boxHeight + border);
      JWFNumberField numberField = editField.buildPart(pForm, panel, x, y);
      pForm.registerNamedControl(numberField);
    }
  }
}
