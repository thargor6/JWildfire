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
package org.jwildfire.create.tina.render;

import org.jwildfire.base.Tools;

public class PhongIntArray {

  private final int PASIZE = 4096;
  private double dPhong = 0.0;
  private double phongArray[] = new double[PASIZE + 4];

  public PhongIntArray(double phong, double phongSize) {
    dPhong = 1.0 / (double) (PASIZE - 1);
    double a = 0.0;
    for (int i = 0; i < (PASIZE + 4); i++) {
      phongArray[i] = phong * Math.pow(a, phongSize);
      a += dPhong;
    }
  }

  public double phongInt(double cosa) {
    int p = Tools.FTOI(cosa / dPhong);
    return p >= 0 && p < phongArray.length ? phongArray[p] : 0.0;
  }

}
