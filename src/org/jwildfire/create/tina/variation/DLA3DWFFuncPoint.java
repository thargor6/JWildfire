/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2017 Andreas Maschke

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
package org.jwildfire.create.tina.variation;

import org.jwildfire.base.mathlib.VecMathLib.Matrix3D;

public class DLA3DWFFuncPoint {
  int pathLength;
  double relDepth;
  DLA3DWFFuncPoint parent;
  double x, y, z;
  double azimuth, elevation;
  Matrix3D rotation;

  public DLA3DWFFuncPoint() {
  }

  public DLA3DWFFuncPoint(DLA3DWFFuncPoint parent, double x, double y, double z) {
    this.parent = parent;
    this.pathLength = parent.pathLength + 1;
    this.x = x;
    this.y = y;
    this.z = z;
  }

}
