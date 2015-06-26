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
package org.jwildfire.create.eden.sunflow;

import org.jwildfire.create.eden.sunflow.base.PartBuilder;
import org.jwildfire.create.eden.sunflow.base.StringSingle;

public abstract class PrimitiveBuilder<T extends PartBuilder> implements PartBuilder {
  protected final TransformBuilder<PrimitiveBuilder<T>> transform = new TransformBuilder<PrimitiveBuilder<T>>(this);
  protected final SunflowSceneBuilder parent;
  protected StringSingle name = new StringSingle();
  protected StringSingle shader = new StringSingle("shiny");

  public PrimitiveBuilder(SunflowSceneBuilder pParent) {
    parent = pParent;
  }

  public SunflowSceneBuilder close() {
    return parent;
  }

  @SuppressWarnings("unchecked")
  public T withName(String pName) {
    name = new StringSingle(pName);
    return (T) this;
  }

  @SuppressWarnings("unchecked")
  public T withShader(String pShader) {
    shader = new StringSingle(pShader);
    return (T) this;
  }

  public TransformBuilder<PrimitiveBuilder<T>> withTransform() {
    return transform;
  }
}
