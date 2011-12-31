/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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

// Port of Hexes and Crackle plugin by slobo777, see http://slobo777.deviantart.com/art/Apo-Plugins-Hexes-And-Crackle-99243824
// All credits for this wonderful plugin to him!

public class VoronoiTools {
  public final static int _x_ = 0;
  public final static int _y_ = 1;
  public final static int _z_ = 2;

  public final static short VORONOI_MAXPOINTS = 25;

  //This implements some basic 2D vector routines that assist with Voronoi
  //cell calculations.

  //Distance between U and P compared to U and Q. If U is at P, then the value is 0, if it is
  //equally far to Q and P then the value is 1.0
  public static final double vratio(double P[], double Q[], double U[]) {
    double PmQx, PmQy;

    PmQx = P[_x_] - Q[_x_];
    PmQy = P[_y_] - Q[_y_];

    if (0.0 == PmQx && 0.0 == PmQy) {
      return 1.0;
    }

    return 2.0 * ((U[_x_] - Q[_x_]) * PmQx + (U[_y_] - Q[_y_]) * PmQy) / (PmQx * PmQx + PmQy * PmQy);
  }

  //Closest point to U from array P.
  // P is an array of points
  // n is number of points to check
  // U is location to find closest
  public static final int closest(double P[][], int n, double U[]) {
    double d2;
    double d2min = 1.0e100;
    int j = 0;

    for (int i = 0; i < n; i++) {
      d2 = (P[i][_x_] - U[_x_]) * (P[i][_x_] - U[_x_]) + (P[i][_y_] - U[_y_]) * (P[i][_y_] - U[_y_]);
      if (d2 < d2min) {
        d2min = d2;
        j = i;
      }
    }

    return j;
  }

  //Voronoi "value" is 0.0 (centre) to 1.0 (edge) if inside cell . . . higher values
  //mean that point is not in the cell defined by chosen centre.
  // P is an array of points defining cell centres
  // n is number of points in array
  // q is chosen centre to measure distance from
  // U is point to test
  public static final double voronoi(double P[][], int n, int q, double U[]) {
    double ratio;
    double ratiomax = -1.0e100;
    for (int i = 0; i < n; i++) {
      if (i != q) {
        ratio = vratio(P[i], P[q], U);
        if (ratio > ratiomax) {
          ratiomax = ratio;
        }
      }
    }

    return ratiomax;
  }

}
