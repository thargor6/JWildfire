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
package org.jwildfire.create.tina.dance.model;

import java.io.Serializable;

public abstract class AbstractProperty implements Serializable {
  private static final long serialVersionUID = 1L;
  protected final AbstractProperty parent;
  protected final String name;
  protected final Class<?> type;

  public AbstractProperty(AbstractProperty pParent, String pName, Class<?> pType) {
    parent = pParent;
    name = pName;
    type = pType;
  }

  public String getName() {
    return name;
  }

  public Class<?> getType() {
    return type;
  }

  public int getDepth() {
    int res = 0;
    AbstractProperty p = parent;
    while (p != null) {
      res++;
      p = p.getParent();
    }
    return res;
  }

  public AbstractProperty getParent() {
    return parent;
  }

}
