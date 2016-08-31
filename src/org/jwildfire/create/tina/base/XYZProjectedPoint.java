/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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
package org.jwildfire.create.tina.base;

import java.io.Serializable;

public class XYZProjectedPoint implements Serializable {
  private static final long serialVersionUID = 1L;

  public double x, y, z;
  public double intensity;
  public double dofDist;
  public double lightX[], lightY[], lightZ[];
  public boolean hasLight[];

  public XYZProjectedPoint(int lightCount) {
    if (lightCount > 0) {
      lightX = new double[lightCount];
      lightY = new double[lightCount];
      lightZ = new double[lightCount];
      hasLight = new boolean[lightCount];
    }
    else {
      lightX = lightY = lightZ = null;
      hasLight = null;
    }
  }
}
