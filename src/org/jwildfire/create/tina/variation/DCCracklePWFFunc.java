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

// This version has a "preset" variable that sets the other variables to make common patterns. By Rick Sidwell
// Values are taken from the Built-in script Crackle_Styles_Chooser_Rev01_by_MH, originally from
// Ian Anderson and Anu Wilde.
// "Hexes" variation breaks plane into hexagonal cells and applies same
//    power, scaling, rotation.
// Direct color used from dc_crackle_wf implemented by Brad Stefanov

package org.jwildfire.create.tina.variation;

import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.floor;
import static org.jwildfire.base.mathlib.MathLib.pow;
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.create.tina.variation.NoiseTools.simplexNoise3D;
import static org.jwildfire.create.tina.variation.VoronoiTools.VORONOI_MAXPOINTS;
import static org.jwildfire.create.tina.variation.VoronoiTools._x_;
import static org.jwildfire.create.tina.variation.VoronoiTools._y_;
import static org.jwildfire.create.tina.variation.VoronoiTools._z_;
import static org.jwildfire.create.tina.variation.VoronoiTools.closest;
import static org.jwildfire.create.tina.variation.VoronoiTools.voronoi;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class DCCracklePWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  public static final String VAR_NAME = "dc_cracklep_wf";

  private static final String PARAM_PRESET = "preset";
  public static final String PARAM_CELLSIZE = "cellsize";
  public static final String PARAM_POWER = "power";
  public static final String PARAM_DISTORT = "distort";
  public static final String PARAM_SCALE = "scale";
  private static final String PARAM_Z = "z";
  private static final String PARAM_COLOR_SCALE = "colorScale";
  private static final String PARAM_COLOR_OFFSET = "colorOffset";   

  protected static final String[] paramNames = { PARAM_PRESET, PARAM_CELLSIZE, PARAM_POWER, PARAM_DISTORT, PARAM_SCALE, PARAM_Z,PARAM_COLOR_SCALE,PARAM_COLOR_OFFSET };

  private int preset = 0;
  private double cellsize = 1.0;
  private double power = 0.2;
  private double distort = 0.0;
  private double scale = 1.0;
  private double z = 0.0;
  private double colorScale = 0.5;
  private double colorOffset = 0.0;  

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

    // For a blur effect, base everything starting on a circle radius 1.0
    // (as opposed to reading the values from FTx and FTy)
    double blurr = (pContext.random() + pContext.random()) / 2.0 + (pContext.random() - 0.5) / 4.0;

    double theta = 2 * M_PI * pContext.random();

    U[_x_] = blurr * sin(theta);
    U[_y_] = blurr * cos(theta);

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

    int offset[][] = new int[][] { { -1, -1 }, { -1, 0 }, { -1, 1 },
        { 0, -1 }, { 0, 0 }, { 0, 1 },
        { 1, -1 }, { 1, 0 }, { 1, 1 } };

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
    pVarTP.color = L * colorScale + colorOffset; ;
    if (pVarTP.color < 0)
      pVarTP.color = 0.0;
    else if (pVarTP.color > 1.0)
      pVarTP.color = 1.0; 
    pVarTP.x += pAmount * DXo;
    pVarTP.y += pAmount * DYo;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { preset, cellsize, power, distort, scale, z,colorScale,colorOffset};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_PRESET.equalsIgnoreCase(pName)) {
      preset = Tools.FTOI(pValue);
      switch (preset) {
        case 0:
          cellsize = 1.0; power = 0.2; distort = 0.0; scale = 1.00; z=0.0;
          break;
        case 1:
          cellsize = 0.5; power = 0.5; distort = 1.0; scale = 0.95; z=10.0;
          break;
        case 2:
            cellsize = 0.5; power = 0.01; distort = 0.5; scale = 1.0; z=0.0;
            break;
          case 3:
            cellsize = 0.05; power = 0.9; distort = 0.9; scale = 0.5; z=0.0;
            break;
          case 4:
            cellsize = 0.5; power = 1.0; distort = 1.0; scale = 0.93; z=10.0;
            break;
          case 5:
            cellsize = 1.0; power = 1.0; distort = 0.0; scale = 0.9; z=0.0;
            break;
          case 6:
            cellsize = 0.8; power = 0.5; distort = 0.5; scale = 0.8; z=0.5;
            break;
          case 7:
            cellsize = 0.2; power = 0.01; distort = 0.0; scale = 0.4; z=2.0;
            break;
          case 8:
            cellsize = 1.0; power = 0.5; distort = 0.25; scale = 0.5; z=0.0;
            break;
          case 9:
            cellsize = 0.6; power = 0.75; distort = 1.0; scale = 0.25; z=0.75;
            break;
          case 10:
            cellsize = 0.5; power = 25.0; distort = 0.0; scale = 9.0; z=6.0;
            break;
          case 11:
            cellsize = 0.2; power = 1.0; distort = 0.0; scale = 0.4; z=0.0;
            break;
           case 12:
            cellsize = 1.5; power = 0.01; distort = 0.0; scale = 0.4; z=0.0;
            break;
          case 13:
            cellsize = 8.0; power = 0.01; distort = 0.0; scale = 0.4; z=0.0;
            break;
          case 14:
            cellsize = 0.2; power = 0.05; distort = 1.0; scale = 5.0; z=0.0;
            break;
          case 15:
            cellsize = 0.07; power = 0.05; distort = 0.5; scale = 9.0; z=6.0;
            break;
          case 16:
            cellsize = 0.2; power = 0.1; distort = 0.0; scale = 1.5; z=2.0;
            break;
          case 17:
            cellsize = 0.297494; power = 0.662265; distort = 0.0708866; scale = 0.228156; z=0.0;
            break;
          case 18:
            cellsize = 0.205939; power = 1.0; distort = 0.0; scale = 0.6298; z=0.35;
            break;
          case 19:
            cellsize = 0.5; power = 0.001; distort = 1.0; scale = 2.0; z=0.0;
            break;
          case 20:
            cellsize = 0.5; power = 0.0005; distort = 0.748; scale = 1.465; z=6.0;
            break;
      }
    }
    else if (PARAM_CELLSIZE.equalsIgnoreCase(pName))
      cellsize = pValue;
    else if (PARAM_POWER.equalsIgnoreCase(pName))
      power = pValue;
    else if (PARAM_DISTORT.equalsIgnoreCase(pName))
      distort = pValue;
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else if (PARAM_Z.equalsIgnoreCase(pName))
      z = pValue;
	else if (PARAM_COLOR_SCALE.equalsIgnoreCase(pName))
      colorScale = pValue;
    else if (PARAM_COLOR_OFFSET.equalsIgnoreCase(pName))
      colorOffset = pValue;	  
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
    }
    else {
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
