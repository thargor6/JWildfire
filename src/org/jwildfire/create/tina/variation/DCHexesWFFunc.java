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

import org.jwildfire.create.tina.base.XYZPoint;

public class DCHexesWFFunc extends HexesFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_COLOR_SCALE = "color_scale";
  private static final String PARAM_COLOR_OFFSET = "color_offset";

  private static final String[] additionalParamNames = {PARAM_COLOR_SCALE, PARAM_COLOR_OFFSET};

  private double colorScale = 0.5;
  private double colorOffset = 0.0;

  // dc_hexes_wf by thargor6
  // This is just a specialization of the hexes plugin originally invented by slobo777, see http://slobo777.deviantart.com/art/Apo-Plugins-Hexes-And-Crackle-99243824
  // It uses the Voronoi-distance as color index making it a "dc_"-variation which may produce very interesting textures

  @Override
  public String getName() {
    return "dc_hexes_wf";
  }

  @Override
  public String[] getParameterNames() {
    return joinArrays(paramNames, additionalParamNames);
  }

  @Override
  public Object[] getParameterValues() {
    return joinArrays(super.getParameterValues(), new Object[]{colorScale, colorOffset});
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_COLOR_SCALE.equalsIgnoreCase(pName))
      colorScale = pValue;
    else if (PARAM_COLOR_OFFSET.equalsIgnoreCase(pName))
      colorOffset = pValue;
    else
      super.setParameter(pName, pValue);
  }

  @Override
  protected void applyCellCalculation(XYZPoint pVarTP, double pAmount, double L, double Vx, double Vy) {
    pVarTP.color = L * colorScale + colorOffset;
    if (pVarTP.color < 0)
      pVarTP.color = 0.0;
    else if (pVarTP.color > 1.0)
      pVarTP.color = 1.0;

    pVarTP.x += pAmount * Vx;
    pVarTP.y += pAmount * Vy;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float P[VORONOI_MAXPOINTS][2];\n"
        + "float DXo, DYo, L, L1, L2, R, s, trgL, Vx, Vy;\n"
        + "float U[2];\n"
        + "int Hx, Hy;\n"
        + "float rotSin = sinf(__dc_hexes_wf_rotate * 2.0f * PI);\n"
        + "float rotCos = cosf(__dc_hexes_wf_rotate * 2.0f * PI);\n"
        + "s = __dc_hexes_wf_cellsize;\n"
        + "if (0.0 != s) {\n"
        + "  U[_x_] = __x;\n"
        + "  U[_y_] = __y;\n"
        + "  Hx = (int)floorf((a_hex * U[_x_] + b_hex * U[_y_]) / s);\n"
        + "  Hy = (int)floorf((c_hex * U[_x_] + d_hex * U[_y_]) / s);\n"
        + "  int i = 0;\n"
        + "  int di, dj;\n"
        + "  for (di = -1; di < 2; di++) {\n"
        + "    for (dj = -1; dj < 2; dj++) {\n"
        + "      dc_hexes_cell_centre(Hx + di, Hy + dj, s, P[i]);\n"
        + "      i++;\n"
        + "    }\n"
        + "  }\n"
        + "\n"

        + "  int q = dc_hexes_closest(P, 9, U);\n"
        + "  Hx += dc_hexes_cell_choice[q][_x_];\n"
        + "  Hy += dc_hexes_cell_choice[q][_y_];\n"
        + "  dc_hexes_cell_centre(Hx, Hy, s, P[0]);\n"
        + "\n"
        + "  dc_hexes_cell_centre(Hx, Hy + 1, s, P[1]);\n"
        + "  dc_hexes_cell_centre(Hx + 1, Hy + 1, s, P[2]);\n"
        + "  dc_hexes_cell_centre(Hx + 1, Hy, s, P[3]);\n"
        + "  dc_hexes_cell_centre(Hx, Hy - 1, s, P[4]);\n"
        + "  dc_hexes_cell_centre(Hx - 1, Hy - 1, s, P[5]);\n"
        + "  dc_hexes_cell_centre(Hx - 1, Hy, s, P[6]);\n"
        + "\n"
        + "  L1 = dc_hexes_voronoi(P, 7, 0, U);\n"
        + "  DXo = U[_x_] - P[0][_x_];\n"
        + "  DYo = U[_y_] - P[0][_y_];\n"
        + "  trgL = powf(L1 + 1e-06f, __dc_hexes_wf_power) * __dc_hexes_wf_scale;\n"
        + "  Vx = DXo * rotCos + DYo * rotSin;\n"
        + "  Vy = -DXo * rotSin + DYo * rotCos;\n"
        + "  U[_x_] = Vx + P[0][_x_];\n"
        + "  U[_y_] = Vy + P[0][_y_];\n"
        + "  L2 = dc_hexes_voronoi(P, 7, 0, U);\n"
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
        + "  __pal = L * __dc_hexes_wf_color_scale + __dc_hexes_wf_color_offset;\n"
        + "    if (__pal < 0.f)\n"
        + "      __pal = 0.f;\n"
        + "    else if (__pal > 1.0f)\n"
        + "      __pal = 1.0f;\n"
        + "  __px += __dc_hexes_wf * Vx;\n"
        + "  __py += __dc_hexes_wf * Vy;\n"
        + "}\n"
        + (context.isPreserveZCoordinate() ? "__pz += __dc_hexes_wf * __z;\n" : "");
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
            + "__constant__ int dc_hexes_cell_choice[9][2] = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 0}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};\n\n"
            + "__device__ void dc_hexes_cell_centre(int x, int y, float s, float *V) {\n"
            + "    V[_x_] = (a_cart * x + b_cart * y) * s;\n"
            + "    V[_y_] = (c_cart * x + d_cart * y) * s;\n"
            + "  }\n\n"
            + "__device__ int dc_hexes_closest(float P[VORONOI_MAXPOINTS][2], int n, float U[]) {\n"
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
            + "__device__ float dc_hexes_vratio(float P[], float Q[], float U[]) {\n"
            + "    float PmQx, PmQy;\n"
            + "    PmQx = P[_x_] - Q[_x_];\n"
            + "    PmQy = P[_y_] - Q[_y_];\n"
            + "    if (0.0f == PmQx && 0.0f == PmQy) {\n"
            + "      return 1.0f;\n"
            + "    }\n"
            + "    return 2.0f * ((U[_x_] - Q[_x_]) * PmQx + (U[_y_] - Q[_y_]) * PmQy) / (PmQx * PmQx + PmQy * PmQy);\n"
            + "  }\n"
            + "\n"
            + "__device__ float dc_hexes_voronoi(float P[VORONOI_MAXPOINTS][2], int n, int q, float U[]) {\n"
            + "   float ratio;\n"
            + "   float ratiomax = -1.0e20f;\n"
            + "   for (int i = 0; i < n; i++) {\n"
            + "      if (i != q) {\n"
            + "        ratio = dc_hexes_vratio(P[i], P[q], U);\n"
            + "        if (ratio > ratiomax) {\n"
            + "          ratiomax = ratio;\n"
            + "        }\n"
            + "      }\n"
            + "    }\n"
            + "    return ratiomax;\n"
            + "  }\n";
  }

}
