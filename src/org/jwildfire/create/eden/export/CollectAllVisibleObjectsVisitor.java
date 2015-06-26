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
package org.jwildfire.create.eden.export;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.create.eden.scene.SceneElement;
import org.jwildfire.create.eden.scene.SceneElementVisitor;
import org.jwildfire.create.eden.scene.VisibleSceneElement;

public class CollectAllVisibleObjectsVisitor implements SceneElementVisitor {
  private final List<VisibleSceneElement> elements = new ArrayList<VisibleSceneElement>();

  @Override
  public void visitBefore(SceneElement pSceneElement) {
    if (pSceneElement instanceof VisibleSceneElement) {
      elements.add((VisibleSceneElement) pSceneElement);
    }
  }

  @Override
  public void visitAfter(SceneElement pSceneElement) {
    // EMPTY
  }

  public List<VisibleSceneElement> getElements() {
    return elements;
  }

}
