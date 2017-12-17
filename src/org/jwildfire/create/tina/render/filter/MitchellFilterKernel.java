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
package org.jwildfire.create.tina.render.filter;

// Filter based on code of the flam3 project: http://flam3.com/index.cgi?&menu=code
public class MitchellFilterKernel extends FilterKernel {

  @Override
  public double getSpatialSupport() {
    return 2.0;
  }

  // b + 2 * c = 1
  //  private final double mitchell_b = 0.42;
  //  private final double mitchell_c = 0.29;

  @Override
  public double getFilterCoeff(double t) {
    double tt;

    tt = t * t;
    if (t < 0)
      t = -t;
    if (t < 1.0) {
      t = (((12.0 - 9.0 * getMitchell_b() - 6.0 * getMitchell_c()) * (t * tt))
          + ((-18.0 + 12.0 * getMitchell_b() + 6.0 * getMitchell_c()) * tt)
          + (6.0 - 2 * getMitchell_b()));
      return (t / 6.0);
    }
    else if (t < 2.0) {
      t = (((-1.0 * getMitchell_b() - 6.0 * getMitchell_c()) * (t * tt))
          + ((6.0 * getMitchell_b() + 30.0 * getMitchell_c()) * tt)
          + ((-12.0 * getMitchell_b() - 48.0 * getMitchell_c()) * t)
          + (8.0 * getMitchell_b() + 24 * getMitchell_c()));
      return (t / 6.0);
    }
    return (0.0);
  }

  protected double getMitchell_b() {
    return (1.0 / 3.0);
  }

  protected double getMitchell_c() {
    return (1.0 / 3.0);
  }

}
