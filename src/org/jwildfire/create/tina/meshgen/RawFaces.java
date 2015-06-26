/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

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
package org.jwildfire.create.tina.meshgen;

import java.util.List;

import org.jwildfire.create.tina.meshgen.marchingcubes.Point3f;

public class RawFaces {
  private final List<Point3f> vertices;
  private final List<Point3f> normals;

  public RawFaces(List<Point3f> pVertices, List<Point3f> pNormals) {
    vertices = pVertices;
    normals = pNormals;
  }

  public List<Point3f> getVertices() {
    return vertices;
  }

  public List<Point3f> getNormals() {
    return normals;
  }

}
