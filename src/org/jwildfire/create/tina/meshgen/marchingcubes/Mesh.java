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
package org.jwildfire.create.tina.meshgen.marchingcubes;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Mesh {
  private List<Point> points = Collections.emptyList();
  private Set<Face> faces = Collections.emptySet();
  private boolean dirty;
  private Point[] bounds = null;

  public Mesh() {
    dirty = true;
  }

  public Mesh(List<Point> pPoints, Set<Face> pFaces) {
    points = pPoints;
    faces = pFaces;
    dirty = true;
  }

  public List<Point> getPoints() {
    return points;
  }

  public void setPoints(List<Point> pPoints) {
    if (pPoints != null) {
      points = pPoints;
    }
    else {
      points = Collections.emptyList();
    }
    dirty = true;
  }

  public Set<Face> getFaces() {
    return faces;
  }

  public void setFaces(Set<Face> pFaces) {
    if (pFaces != null) {
      faces = pFaces;
    }
    else {
      faces = Collections.emptySet();
    }
    dirty = true;
  }

  private Point[] getBounds() {
    if (dirty) {
      Point min, max;
      bounds = new Point[2];
      bounds[0] = min = new Point();
      bounds[1] = max = new Point();
      if (points.size() > 0) {
        Point first = points.get(0);
        bounds[0].x = bounds[1].x = first.x;
        bounds[0].y = bounds[1].y = first.y;
        bounds[0].z = bounds[1].z = first.z;
        for (int i = 1; i < points.size(); i++) {
          Point point = points.get(i);
          if (point.x < min.x)
            min.x = point.x;
          else if (point.x > max.x)
            max.x = point.x;
          if (point.y < min.y)
            min.y = point.y;
          else if (point.y > max.y)
            max.y = point.y;
          if (point.z < min.z)
            min.z = point.z;
          else if (point.z > max.z)
            max.z = point.z;
        }
      }
      dirty = false;
    }
    return bounds;
  }

  public Point getPMin() {
    return getBounds()[0];
  }

  public Point getPMax() {
    return getBounds()[1];
  }
}
