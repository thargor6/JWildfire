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

// Port of Hexes and Crackle plugin by slobo777, see http://slobo777.deviantart.com/art/Apo-Plugins-Hexes-And-Crackle-99243824
// All credits for this wonderful plugin to him!

// "Hexes" variation breaks plane into hexagonal cells and applies same
//    power, scaling, rotation.

package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;
import static org.jwildfire.create.tina.variation.NoiseTools.simplexNoise3D;
import static org.jwildfire.create.tina.variation.VoronoiTools.*;

public class CrackleFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  public static final String VAR_NAME = "crackle";

  public static final String PARAM_CELLSIZE = "cellsize";
  public static final String PARAM_POWER = "power";
  public static final String PARAM_DISTORT = "distort";
  public static final String PARAM_SCALE = "scale";
  public static final String PARAM_Z = "z";

  protected static final String[] paramNames = {PARAM_CELLSIZE, PARAM_POWER, PARAM_DISTORT, PARAM_SCALE, PARAM_Z};

  protected double cellsize = 1.0;
  protected double power = 0.2;
  protected double distort = 0.0;
  protected double scale = 1.0;
  protected double z = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double DXo, DYo, L, R, s, trgL;
    double U[] = new double[2];
    int XCv, YCv;

    // An infinite number of invisible cells? No thanks!
    if (0.0 == cellsize) {
      return;
    }

    // Scaling factor
    s = cellsize / 2.0;

    if (pAmount != 0) {
      // For a blur effect, base everything starting on a circle radius 1.0
      // (as opposed to reading the values from FTx and FTy)
      double blurr = (pContext.random() + pContext.random()) / 2.0 + (pContext.random() - 0.5) / 4.0;

      double theta = 2 * M_PI * pContext.random();

      U[_x_] = blurr * sin(theta);
      U[_y_] = blurr * cos(theta);
    } else {
      // pAmount=0, so color only (DC versions; won't affect normal one); don't blur
      U[_x_] = pAffineTP.x;
      U[_y_] = pAffineTP.y;
    }

    // Use integer values as Voronoi grid co-ordinates
    XCv = (int) floor(U[_x_] / s);
    YCv = (int) floor(U[_y_] / s);

    // Get a set of 9 square centre points, based around the one above
    int di, dj;
    int i = 0;
    for (di = -1; di < 2; di++) {
      for (dj = -1; dj < 2; dj++) {
        cached_position(C, XCv + di, YCv + dj, z, s, distort, P[i]);
        i++;
      }
    }

    int q = closest(P, 9, U);

    int offset[][] = new int[][]{{-1, -1}, {-1, 0}, {-1, 1},
            {0, -1}, {0, 0}, {0, 1},
            {1, -1}, {1, 0}, {1, 1}};

    // Remake list starting from chosen square, ensure it is completely surrounded (total 9 points)

    // First adjust centres according to which one was found to be closest
    XCv += offset[q][_x_];
    YCv += offset[q][_y_];

    // Get a new set of 9 square centre points, based around the definite closest point
    i = 0;
    for (di = -1; di < 2; di++) {
      for (dj = -1; dj < 2; dj++) {
        cached_position(C, XCv + di, YCv + dj, z, s, distort, P[i]);
        i++;
      }
    }

    L = voronoi(P, 9, 4, U); // index 4 is centre cell

    // Delta vector from centre
    DXo = U[_x_] - P[4][_x_];
    DYo = U[_y_] - P[4][_y_];

    /////////////////////////////////////////////////////////////////
    // Apply "interesting bit" to cell's DXo and DYo co-ordinates

    // trgL is the new value of L
    trgL = pow(L + 1e-100, power) * scale; // ( 0.9 )

    R = trgL / (L + 1e-100);

    DXo *= R;
    DYo *= R;

    // Add cell centre co-ordinates back in
    DXo += P[4][_x_];
    DYo += P[4][_y_];
    // Finally add values in
    applyCellCalculation(pVarTP, pAmount, DXo, DYo, L);
  }

  protected void applyCellCalculation(XYZPoint pVarTP, double pAmount, double DXo, double DYo, double L) {
    pVarTP.x += pAmount * DXo;
    pVarTP.y += pAmount * DYo;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{cellsize, power, distort, scale, z};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_CELLSIZE.equalsIgnoreCase(pName))
      cellsize = pValue;
    else if (PARAM_POWER.equalsIgnoreCase(pName))
      power = pValue;
    else if (PARAM_DISTORT.equalsIgnoreCase(pName))
      distort = pValue;
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else if (PARAM_Z.equalsIgnoreCase(pName))
      z = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return VAR_NAME;
  }

  //These set cache size for cell centres, they take a lot of processing, so it's handy to
  //keep values between calls
  private final static int CACHE_NUM = 10;
  private final static int CACHE_WIDTH = 21;

  // P is a working list of points
  private double P[][] = new double[VORONOI_MAXPOINTS][2];
  // C is a cache of pre-calculated centres
  private double C[][][] = new double[CACHE_WIDTH][CACHE_WIDTH][2];

  //Waffle's cells are based on a simple square grid which is distorted using a noise function

  //position() calculates cell centre for cell (x, y), given plane slice z, scale factor s, distortion d
  // and stores in supplied array
  private void position(int x, int y, double z, double s, double d, double V[]) {
    double E[] = new double[3];
    double F[] = new double[3];

    // Values here are arbitrary, chosen simply to be far enough apart so they do not correlate
    E[_x_] = x * 2.5;
    E[_y_] = y * 2.5;
    E[_z_] = z * 2.5;
    // Cross-over between x and y is intentional
    F[_x_] = y * 2.5 + 30.2;
    F[_y_] = x * 2.5 - 12.1;
    F[_z_] = z * 2.5 + 19.8;

    V[_x_] = (x + d * simplexNoise3D(E)) * s;
    V[_y_] = (y + d * simplexNoise3D(F)) * s;
  }

  //cached_position gives centre co-ordinates either from cache, or calculated from scratch if needed
  private void cached_position(double Cache[][][], int x, int y, double z, double s, double d, double V[]) {
    if (fabs(x) <= CACHE_NUM && fabs(y) <= CACHE_NUM) {
      V[_x_] = Cache[x + CACHE_NUM][y + CACHE_NUM][_x_];
      V[_y_] = Cache[x + CACHE_NUM][y + CACHE_NUM][_y_];
    } else {
      position(x, y, z, s, d, V);
    }
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    // Pre-calculate cache of grid centres, to save time later . . .
    for (int x = -CACHE_NUM; x <= CACHE_NUM; x++) {
      for (int y = -CACHE_NUM; y <= CACHE_NUM; y++) {
        position(x, y, z, cellsize / 2.0, distort, C[x + CACHE_NUM][y + CACHE_NUM]);
      }
    }
  }

}
