/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke

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

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;
import static org.jwildfire.create.tina.variation.VoronoiTools.*;

public class HexesFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_CELLSIZE = "cellsize";
  private static final String PARAM_POWER = "power";
  private static final String PARAM_ROTATE = "rotate";
  private static final String PARAM_SCALE = "scale";

  protected static final String[] paramNames = {PARAM_CELLSIZE, PARAM_POWER, PARAM_ROTATE, PARAM_SCALE};

  private double cellsize = 1.0;
  private double power = 1.0;
  private double rotate = 0.166;
  private double scale = 1.0;

  //Following are pre-calculated fixed multipliers for converting
  //between "Hex" co-ordinates and "Original" co-ordinates.

  //This is, in fact, an affine transform. So we'll use same notation
  //as for Apophysis transforms:
  //x_hex = a_hex * x_cartesian + b_hex * y_cartesian
  //y_hex = c_hex * x_cartesian + d_hex * y_cartesian
  // . . . and the reverse:
  //x_cartesian =  a_cart * x_hex + b_cart * y_hex
  //y_cartesian =  c_cart * x_hex + d_cart * y_hex
  //Values for e and f are 0.0 in both cases, so not required.

  //Xh = (Xo + sqrt(3) * Yo) / (3 * l)
  private static final double a_hex = 1.0 / 3.0;
  private static final double b_hex = 1.7320508075688772935 / 3.0;
  //Now:  Xh = ( a_hex * Xo + b_hex * Yo ) / l;

  //Yh = (-Xo + sqrt(3) * Yo) / (3 * l)
  private static final double c_hex = -1.0 / 3.0;
  private static final double d_hex = 1.7320508075688772935 / 3.0;
  //Now:  Yh = ( c_hex * Xo + d_hex * Yo ) / l;

  //Xo = 3/2 * l * (Xh - Yh)
  private static final double a_cart = 1.5;
  private static final double b_cart = -1.5;
  //Now:  Xo = ( a_cart * Xh + b_cart * Yh ) * l;

  //Yo = sqrt(3)/2 * l * (Xh + Yh)
  private static final double c_cart = 1.7320508075688772935 / 2.0;
  private static final double d_cart = 1.7320508075688772935 / 2.0;
  //Now:  Yo = ( c_cart * Xh + d_cart * Yh ) * l;

  private static final int cell_choice[][] = new int[][]{{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 0}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

  //centre gives centre co-ordinates either from cache,
  //or calculated from scratch if needed
  private void cell_centre(int x, int y, double s, double V[]) {
    V[_x_] = (a_cart * x + b_cart * y) * s;
    V[_y_] = (c_cart * x + d_cart * y) * s;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double P[][] = new double[VORONOI_MAXPOINTS][2];
    double DXo, DYo, L, L1, L2, R, s, trgL, Vx, Vy;
    double U[] = new double[2];
    int Hx, Hy;

    // For speed/convenience
    s = this.cellsize;

    // Infinite number of small cells? No effect . . .
    if (0.0 == s) {
      return;
    }

    // Get cartesian co-ordinates, and convert to hex co-ordinates
    U[_x_] = pAffineTP.x;
    U[_y_] = pAffineTP.y;

    Hx = (int) floor((a_hex * U[_x_] + b_hex * U[_y_]) / s);
    Hy = (int) floor((c_hex * U[_x_] + d_hex * U[_y_]) / s);

    // Get a set of 9 hex centre points, based around the one above
    int i = 0;
    int di, dj;
    for (di = -1; di < 2; di++) {
      for (dj = -1; dj < 2; dj++) {
        cell_centre(Hx + di, Hy + dj, s, P[i]);
        i++;
      }
    }

    int q = closest(P, 9, U);

    // Remake list starting from chosen hex, ensure it is completely surrounded (total 7 points)

    // First adjust centres according to which one was found to be closest
    Hx += cell_choice[q][_x_];
    Hy += cell_choice[q][_y_];

    // First point is central/closest
    cell_centre(Hx, Hy, cellsize, P[0]);

    // In hex co-ords, offsets are: (0,1) (1,1) (1,0) (0,-1) (-1,-1) (-1, 0)
    cell_centre(Hx, Hy + 1, s, P[1]);
    cell_centre(Hx + 1, Hy + 1, s, P[2]);
    cell_centre(Hx + 1, Hy, s, P[3]);
    cell_centre(Hx, Hy - 1, s, P[4]);
    cell_centre(Hx - 1, Hy - 1, s, P[5]);
    cell_centre(Hx - 1, Hy, s, P[6]);

    L1 = voronoi(P, 7, 0, U);

    // Delta vector from centre of hex
    DXo = U[_x_] - P[0][_x_];
    DYo = U[_y_] - P[0][_y_];

    /////////////////////////////////////////////////////////////////
    // Apply "interesting bit" to cell's DXo and DYo co-ordinates

    // trgL is the defined value of l, independent of any rotation
    trgL = pow(L1 + 1e-100, power) * scale;

    // Rotate
    Vx = DXo * rotCos + DYo * rotSin;
    Vy = -DXo * rotSin + DYo * rotCos;

    //////////////////////////////////////////////////////////////////
    // Measure voronoi distance again

    U[_x_] = Vx + P[0][_x_];
    U[_y_] = Vy + P[0][_y_];
    L2 = voronoi(P, 7, 0, U);

    //////////////////////////////////////////////////////////////////
    // Scale to meet target size . . . adjust according to how close
    // we are to the edge

    // Code here attempts to remove the "rosette" effect caused by
    // scaling difference between corners and closer edges

    // L is maximum of L1 or L2 . . .
    // When L = 0.8 or higher . . . match trgL/L2 exactly
    // When L = 0.5 or less . . . match trgL/L1 exactly

    L = (L1 > L2) ? L1 : L2;

    if (L < 0.5) {
      R = trgL / L1;
    } else {
      if (L > 0.8) {
        R = trgL / L2;
      } else {
        R = ((trgL / L1) * (0.8 - L) + (trgL / L2) * (L - 0.5)) / 0.3;
      }
    }

    Vx *= R;
    Vy *= R;

    // Add cell centre co-ordinates back in
    Vx += P[0][_x_];
    Vy += P[0][_y_];

    // Finally add values in
    applyCellCalculation(pVarTP, pAmount, L, Vx, Vy);
  }

  protected void applyCellCalculation(XYZPoint pVarTP, double pAmount, double L, double Vx, double Vy) {
    pVarTP.x += pAmount * Vx;
    pVarTP.y += pAmount * Vy;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{cellsize, power, rotate, scale};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_CELLSIZE.equalsIgnoreCase(pName))
      cellsize = pValue;
    else if (PARAM_POWER.equalsIgnoreCase(pName))
      power = pValue;
    else if (PARAM_ROTATE.equalsIgnoreCase(pName))
      rotate = pValue;
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "hexes";
  }

  private double rotSin, rotCos;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    rotSin = sin(rotate * 2.0 * MathLib.M_PI);
    rotCos = cos(rotate * 2.0 * MathLib.M_PI);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float P[VORONOI_MAXPOINTS][2];\n"
        + "float DXo, DYo, L, L1, L2, R, s, trgL, Vx, Vy;\n"
        + "float U[2];\n"
        + "int Hx, Hy;\n"
        + "float rotSin = sinf(__hexes_rotate * 2.0f * PI);\n"
        + "float rotCos = cosf(__hexes_rotate * 2.0f * PI);\n"
        + "s = __hexes_cellsize;\n"
        + "if (0.0 != s) {\n"
        + "  U[_x_] = __x;\n"
        + "  U[_y_] = __y;\n"
        + "  Hx = (int)floorf((a_hex * U[_x_] + b_hex * U[_y_]) / s);\n"
        + "  Hy = (int)floorf((c_hex * U[_x_] + d_hex * U[_y_]) / s);\n"
        + "  int i = 0;\n"
        + "  int di, dj;\n"
        + "  for (di = -1; di < 2; di++) {\n"
        + "    for (dj = -1; dj < 2; dj++) {\n"
        + "      hexes_cell_centre(Hx + di, Hy + dj, s, P[i]);\n"
        + "      i++;\n"
        + "    }\n"
        + "  }\n"
        + "\n"
        + "  int q = hexes_closest(P, 9, U);\n"
        + "  Hx += hexes_cell_choice[q][_x_];\n"
        + "  Hy += hexes_cell_choice[q][_y_];\n"
        + "  hexes_cell_centre(Hx, Hy, s, P[0]);\n"
        + "\n"
        + "  hexes_cell_centre(Hx, Hy + 1, s, P[1]);\n"
        + "  hexes_cell_centre(Hx + 1, Hy + 1, s, P[2]);\n"
        + "  hexes_cell_centre(Hx + 1, Hy, s, P[3]);\n"
        + "  hexes_cell_centre(Hx, Hy - 1, s, P[4]);\n"
        + "  hexes_cell_centre(Hx - 1, Hy - 1, s, P[5]);\n"
        + "  hexes_cell_centre(Hx - 1, Hy, s, P[6]);\n"
        + "\n"
        + "  L1 = hexes_voronoi(P, 7, 0, U);\n"
        + "  DXo = U[_x_] - P[0][_x_];\n"
        + "  DYo = U[_y_] - P[0][_y_];\n"
        + "  trgL = powf(L1 + 1e-06f, __hexes_power) * __hexes_scale;\n"
        + "  Vx = DXo * rotCos + DYo * rotSin;\n"
        + "  Vy = -DXo * rotSin + DYo * rotCos;\n"
        + "  U[_x_] = Vx + P[0][_x_];\n"
        + "  U[_y_] = Vy + P[0][_y_];\n"
        + "  L2 = hexes_voronoi(P, 7, 0, U);\n"
        + "  L = (L1 > L2) ? L1 : L2;\n"
        + "  if (L < 0.5f) {\n"
        + "    R = trgL / L1;\n"
        + "  } else {\n"
        + "    if (L > 0.8f) {\n"
        + "       R = trgL / L2;\n"
        + "     } else {\n"
        + "       R = ((trgL / L1) * (0.8f - L) + (trgL / L2) * (L - 0.5f)) / 0.3f;\n"
        + "     }\n"
        + "  }\n"
        + "  Vx *= R;\n"
        + "  Vy *= R;\n"
        + "  Vx += P[0][_x_];\n"
        + "  Vy += P[0][_y_];\n"
        + "  __px += __hexes * Vx;\n"
        + "  __py += __hexes * Vy;\n"
        + "}\n"
        + (context.isPreserveZCoordinate() ? "__pz += __hexes * __z;\n" : "");
  }

  @Override
  public String getGPUFunctions(FlameTransformationContext context) {
    return "#define _x_ 0\n"
        + "#define _y_ 1\n"
        + "#define _z_ 2\n"
        + "#define VORONOI_MAXPOINTS 25\n"
        + "#define a_hex 1.0f / 3.0f\n"
        + "#define b_hex 1.7320508075688772935f / 3.0f\n"
        + "#define c_hex -1.0f / 3.0f\n"
        + "#define d_hex 1.7320508075688772935f / 3.0f\n"
        + "#define a_cart 1.5f\n"
        + "#define b_cart -1.5f\n"
        + "#define c_cart 1.7320508075688772935f / 2.0f\n"
        + "#define d_cart 1.7320508075688772935f / 2.0f\n\n"
        + "__constant__ int hexes_cell_choice[9][2] = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 0}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};\n\n"
        + "__device__ void hexes_cell_centre(int x, int y, float s, float *V) {\n"
        + "    V[_x_] = (a_cart * x + b_cart * y) * s;\n"
        + "    V[_y_] = (c_cart * x + d_cart * y) * s;\n"
        + "  }\n\n"
        + "__device__ int hexes_closest(float P[VORONOI_MAXPOINTS][2], int n, float U[]) {\n"
        + "    float d2;\n"
        + "    float d2min = 1.0e32f;\n"
        + "    int j = 0;\n"
        + "    for (int i = 0; i < n; i++) {\n"
        + "      d2 = (P[i][_x_] - U[_x_]) * (P[i][_x_] - U[_x_]) + (P[i][_y_] - U[_y_]) * (P[i][_y_] - U[_y_]);\n"
        + "      if (d2 < d2min) {\n"
        + "        d2min = d2;\n"
        + "        j = i;\n"
        + "      }\n"
        + "    }\n"
        + "    return j;\n"
        + "  }\n"
        + "\n"
        + "__device__ float hexes_vratio(float P[], float Q[], float U[]) {\n"
        + "    float PmQx, PmQy;\n"
        + "    PmQx = P[_x_] - Q[_x_];\n"
        + "    PmQy = P[_y_] - Q[_y_];\n"
        + "    if (0.0f == PmQx && 0.0f == PmQy) {\n"
        + "      return 1.0f;\n"
        + "    }\n"
        + "    return 2.0f * ((U[_x_] - Q[_x_]) * PmQx + (U[_y_] - Q[_y_]) * PmQy) / (PmQx * PmQx + PmQy * PmQy);\n"
        + "  }\n"
        + "\n"
        + "__device__ float hexes_voronoi(float P[VORONOI_MAXPOINTS][2], int n, int q, float U[]) {\n"
        + "   float ratio;\n"
        + "   float ratiomax = -1.0e20f;\n"
        + "   for (int i = 0; i < n; i++) {\n"
        + "      if (i != q) {\n"
        + "        ratio = hexes_vratio(P[i], P[q], U);\n"
        + "        if (ratio > ratiomax) {\n"
        + "          ratiomax = ratio;\n"
        + "        }\n"
        + "      }\n"
        + "    }\n"
        + "    return ratiomax;\n"
        + "  }\n";
  }
}
