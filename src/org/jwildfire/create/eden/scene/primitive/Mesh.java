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
package org.jwildfire.create.eden.scene.primitive;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.create.eden.base.Face3i;
import org.jwildfire.create.eden.base.Point3f;
import org.jwildfire.create.eden.scene.PositionableSceneElement;
import org.jwildfire.create.eden.scene.VisibleSceneElement;

public class Mesh extends VisibleSceneElement {
  private final List<Point3f> points = new ArrayList<Point3f>();
  private final List<Face3i> faces = new ArrayList<Face3i>();

  public Mesh(PositionableSceneElement pParent) {
    super(pParent);
  }

  public List<Point3f> getPoints() {
    return points;
  }

  public List<Face3i> getFaces() {
    return faces;
  }

}
