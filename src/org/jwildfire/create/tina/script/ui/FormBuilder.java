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

import javax.swing.*;

import org.jwildfire.swing.ErrorHandler;

public class FormBuilder {
  private final List<ContainerBuilder> containers = new ArrayList<ContainerBuilder>();

  public ContainerBuilder openContainer(String pCaption) {
    ContainerBuilder container = new ContainerBuilder(this);
    container.withCaption(pCaption);
    containers.add(container);
    return container;
  }

  public ScriptParamsForm getProduct(JInternalFrame pParent, ErrorHandler pErrorHandler) {
    ScriptParamsForm form = new ScriptParamsForm(pParent, pErrorHandler);
    buildPart(form);
    return form;
  }

  public ScriptParamsForm getProduct(JFrame pParent, ErrorHandler pErrorHandler) {
    ScriptParamsForm form = new ScriptParamsForm(pParent, pErrorHandler);
    buildPart(form);
    return form;
  }

  public void buildPart(ScriptParamsForm pForm) {
    for (ContainerBuilder container : containers) {
      container.buildPart(pForm);
    }
  }
}
