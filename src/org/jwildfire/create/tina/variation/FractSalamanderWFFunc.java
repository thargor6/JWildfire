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

package org.jwildfire.create.tina.variation;

import java.util.List;

public class FractSalamanderWFFunc extends AbstractFractWFFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_XSEED = "xseed";
  private static final String PARAM_YSEED = "yseed";

  private double xseed;
  private double yseed;

  @Override
  public String getName() {
    return "fract_salamander_wf";
  }

  @Override
  protected void initParams() {
    xmin = -2.0;
    xmax = 2.0;
    ymin = -2.0;
    ymax = 2.0;
    clip_iter_min = 1;
    scale = 2.45;
    xseed = 0.8;
    yseed = -0.15;
  }

  @Override
  protected boolean setCustomParameter(String pName, double pValue) {
    if (PARAM_XSEED.equalsIgnoreCase(pName)) {
      xseed = pValue;
      return true;
    } else if (PARAM_YSEED.equalsIgnoreCase(pName)) {
      yseed = pValue;
      return true;
    }
    return false;
  }

  @Override
  protected void addCustomParameterNames(List<String> pList) {
    pList.add(PARAM_XSEED);
    pList.add(PARAM_YSEED);
  }

  @Override
  protected void addCustomParameterValues(List<Object> pList) {
    pList.add(xseed);
    pList.add(yseed);
  }

  public class SalamanderIterator extends Iterator {

    @Override
    protected void nextIteration() {
      double x1 = this.currX;
      double y1 = this.currY;
      this.xs = x1 * x1;
      this.ys = y1 * y1;
      double x2 = (this.xs - this.ys) * xseed - (2.0 * x1 * y1) * yseed - 1.0;
      y1 = (this.xs - this.ys) * yseed + (2.0 * x1 * y1) * xseed;
      x1 = x2;
      setCurrPoint(x1, y1);
    }

  }

  private SalamanderIterator iterator = new SalamanderIterator();

  @Override
  protected Iterator getIterator() {
    return iterator;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_ESCAPE_TIME_FRACTAL, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "if (varpar->fract_salamander_wf_buddhabrot_mode > 0) {\n"
            + "    float x0=0, y0 = 0;\n"
            + "    int bb_max_clip_iter = 250;\n"
            + "    if (varpar->fract_salamander_wf_chooseNewPoint) {\n"
            + "      for (int i = 0; i < bb_max_clip_iter; i++) {\n"
            + "        x0 = (varpar->fract_salamander_wf_xmax - varpar->fract_salamander_wf_xmin) * RANDFLOAT() + varpar->fract_salamander_wf_xmin;\n"
            + "        y0 = (varpar->fract_salamander_wf_ymax - varpar->fract_salamander_wf_ymin) * RANDFLOAT() + varpar->fract_salamander_wf_ymin;\n"
            + "        if (fract_salamander_wf_preBuddhaIterate(varpar, x0, y0, varpar->fract_salamander_wf_max_iter)) {\n"
            + "          break;\n"
            + "        }\n"
            + "        if (i == bb_max_clip_iter - 1) {\n"
            + "          __doHide = true;\n"
            + "          break;\n"
            + "        }\n"
            + "      }\n"
            + "      if(!__doHide) {\n"
            + "        *(&varpar->fract_salamander_wf_chooseNewPoint) = false;\n"
            + "        fract_salamander_wf_init(varpar, x0, y0);\n"
            + "        for (int skip = 0; skip < varpar->fract_salamander_wf_buddhabrot_min_iter; skip++) {\n"
            + "          fract_salamander_wf_nextIteration(varpar);\n"
            + "        }\n"
            + "      }\n"
            + "    }\n"
            + "    if(!__doHide) {\n"
            + "      fract_salamander_wf_nextIteration(varpar);\n"
            + "      if (varpar->fract_salamander_wf_currIter >= varpar->fract_salamander_wf_maxIter) {\n"
            + "        *(&varpar->fract_salamander_wf_chooseNewPoint) = true;\n"
            + "      }\n"
            + "      __px += varpar->fract_salamander_wf_scale * varpar->fract_salamander_wf * (varpar->fract_salamander_wf_currX + varpar->fract_salamander_wf_offsetx);\n"
            + "      __py += varpar->fract_salamander_wf_scale * varpar->fract_salamander_wf * (varpar->fract_salamander_wf_currY + varpar->fract_salamander_wf_offsety);\n"
            + "      __pal += (float) varpar->fract_salamander_wf_currIter / (float) varpar->fract_salamander_wf_maxIter;\n"
            + "      if (__pal < 0)\n"
            + "        __pal = 0;\n"
            + "      else if (__pal > 1.0)\n"
            + "        __pal = 1.0;\n"
            + "    }\n"
            + "}\n"
            + "else {\n"
            + "    __doHide = false;\n"
            + "    float x0 = 0.0, y0 = 0.0;\n"
            + "    int iterCount = 0;\n"
            + "    for (int i = 0; i < varpar->fract_salamander_wf_max_clip_iter; i++) {\n"
            + "      x0 = (varpar->fract_salamander_wf_xmax - varpar->fract_salamander_wf_xmin) * RANDFLOAT() + varpar->fract_salamander_wf_xmin;\n"
            + "      y0 = (varpar->fract_salamander_wf_ymax - varpar->fract_salamander_wf_ymin) * RANDFLOAT() + varpar->fract_salamander_wf_ymin;\n"
            + "      iterCount = fract_salamander_wf_iterate(varpar, x0, y0, varpar->fract_salamander_wf_max_iter);\n"
            + "      if ((varpar->fract_salamander_wf_clip_iter_max < 0 && iterCount >= (varpar->fract_salamander_wf_max_iter + varpar->fract_salamander_wf_clip_iter_max)) || (varpar->fract_salamander_wf_clip_iter_min > 0 && iterCount <= varpar->fract_salamander_wf_clip_iter_min)) {\n"
            + "        if (i == varpar->fract_salamander_wf_max_clip_iter - 1) {\n"
            + "          __doHide = true;\n"
            + "          break;\n"
            + "        }\n"
            + "      } else {\n"
            + "        break;\n"
            + "      }\n"
            + "    }\n"
            + "    if(!__doHide) {\n"
            + "      __px += varpar->fract_salamander_wf_scale * varpar->fract_salamander_wf * (x0 + varpar->fract_salamander_wf_offsetx);\n"
            + "      __py += varpar->fract_salamander_wf_scale * varpar->fract_salamander_wf * (y0 + varpar->fract_salamander_wf_offsety);\n"
            + "      float z;\n"
            + "      if (lroundf(varpar->fract_salamander_wf_z_logscale) == 1) {\n"
            + "        z = varpar->fract_salamander_wf_scale * varpar->fract_salamander_wf * (varpar->fract_salamander_wf_scalez / 10 * log10f(1.0 + (float) iterCount / (float) varpar->fract_salamander_wf_max_iter) + varpar->fract_salamander_wf_offsetz);\n"
            + "        if (varpar->fract_salamander_wf_z_fill > 1.e-6f && RANDFLOAT() < varpar->fract_salamander_wf_z_fill) {\n"
            + "          float prevZ = varpar->fract_salamander_wf_scale * varpar->fract_salamander_wf * (varpar->fract_salamander_wf_scalez / 10 * log10f(1.0 + (float) (iterCount - 1) / (float) varpar->fract_salamander_wf_max_iter) + varpar->fract_salamander_wf_offsetz);\n"
            + "          z = (prevZ - z) * RANDFLOAT() + z;\n"
            + "        }\n"
            + "      } else {\n"
            + "        z = varpar->fract_salamander_wf_scale * varpar->fract_salamander_wf * (varpar->fract_salamander_wf_scalez / 10 * ((float) iterCount / (float) varpar->fract_salamander_wf_max_iter) + varpar->fract_salamander_wf_offsetz);\n"
            + "        if (varpar->fract_salamander_wf_z_fill > 1.e-6f && RANDFLOAT() < varpar->fract_salamander_wf_z_fill) {\n"
            + "          float prevZ = varpar->fract_salamander_wf_scale * varpar->fract_salamander_wf * (varpar->fract_salamander_wf_scalez / 10 * ((float) (iterCount - 1) / (float) varpar->fract_salamander_wf_max_iter) + varpar->fract_salamander_wf_offsetz);\n"
            + "          z = (prevZ - z) * RANDFLOAT() + z;\n"
            + "        }\n"
            + "      }\n"
            + "     __pz += z;\n"
            + "      if (lroundf(varpar->fract_salamander_wf_direct_color) != 0) {\n"
            + "        __pal += (float) iterCount / (float) varpar->fract_salamander_wf_max_iter;\n"
            + "        if (__pal > 1.0)\n"
            + "          __pal -= 1.0;\n"
            + "        if (__pal < 0)\n"
            + "          __pal = 0;\n"
            + "        else if (__pal > 1.0)\n"
            + "          __pal = 1.0;\n"
            + "      }\n"
            + "   }\n"
            + "}";
  }

  @Override
  public String[] getGPUExtraParameterNames() {
    return new String[]{"chooseNewPoint", "xs", "ys", "currIter", "maxIter", "startX", "startY", "currX", "currY"};
  }

  @Override
  public String getGPUFunctions(FlameTransformationContext context) {
    return "__device__ void fract_salamander_wf_init(struct VarPar__jwf_fract_salamander_wf *varpar,float pX0, float pY0) {\n"
        + "    *(&varpar->jwf_fract_salamander_wf_xs) = *(&varpar->jwf_fract_salamander_wf_ys) = 0;\n"
        + "    *(&varpar->jwf_fract_salamander_wf_startX) = *(&varpar->jwf_fract_salamander_wf_currX) = pX0;\n"
        + "    *(&varpar->jwf_fract_salamander_wf_startY) = *(&varpar->jwf_fract_salamander_wf_currY) = pY0;\n"
        + "    *(&varpar->jwf_fract_salamander_wf_currIter) = 0;\n"
        + "}\n"
        + "\n"
        + "__device__ void fract_salamander_wf_setCurrPoint(struct VarPar__jwf_fract_salamander_wf *varpar, float pX, float pY) {\n"
        + "   *(&varpar->jwf_fract_salamander_wf_currX) = pX;\n"
        + "   *(&varpar->jwf_fract_salamander_wf_currY) = pY;\n"
        + "   *(&varpar->jwf_fract_salamander_wf_currIter) = varpar->jwf_fract_salamander_wf_currIter + 1;\n"
        + "}\n"
        + "\n"
        + "__device__ void fract_salamander_wf_nextIteration(struct VarPar__jwf_fract_salamander_wf *varpar) {\n"
        + "      float x1 = varpar->jwf_fract_salamander_wf_currX;\n"
        + "      float y1 = varpar->jwf_fract_salamander_wf_currY;\n"
        + "      *(&varpar->jwf_fract_salamander_wf_xs) = x1 * x1;\n"
        + "      *(&varpar->jwf_fract_salamander_wf_ys) = y1 * y1;\n"
        + "      float x2 = (varpar->jwf_fract_salamander_wf_xs - varpar->jwf_fract_salamander_wf_ys) * varpar->jwf_fract_salamander_wf_xseed - (2.0 * x1 * y1) * varpar->jwf_fract_salamander_wf_yseed - 1.0;\n"
        + "      y1 = (varpar->jwf_fract_salamander_wf_xs - varpar->jwf_fract_salamander_wf_ys) * varpar->jwf_fract_salamander_wf_yseed + (2.0 * x1 * y1) * varpar->jwf_fract_salamander_wf_xseed;\n"
        + "      x1 = x2;\n"
        + "      fract_salamander_wf_setCurrPoint(varpar, x1, y1);\n"
        + "}\n"
        + "\n"
        + "__device__ bool fract_salamander_wf_preBuddhaIterate(struct VarPar__jwf_fract_salamander_wf *varpar, float pStartX, float pStartY, int pMaxIter) {\n"
        + "     fract_salamander_wf_init(varpar, pStartX, pStartY);\n"
        + "     int currIter = 0;\n"
        + "     while ((currIter++ < pMaxIter) && !(varpar->jwf_fract_salamander_wf_xs + varpar->jwf_fract_salamander_wf_ys >= 4.0)) {\n"
        + "        fract_salamander_wf_nextIteration(varpar);\n"
        + "     }\n"
        + "     if (varpar->jwf_fract_salamander_wf_xs + varpar->jwf_fract_salamander_wf_ys >= 4.0) {\n"
        + "        *(&varpar->jwf_fract_salamander_wf_maxIter) = currIter;\n"
        + "        return varpar->jwf_fract_salamander_wf_maxIter > varpar->jwf_fract_salamander_wf_buddhabrot_min_iter;\n"
        + "      } else {\n"
        + "        return false;\n"
        + "      }\n"
        + "}\n"
        + "\n"
        + "__device__ int fract_salamander_wf_iterate(struct VarPar__jwf_fract_salamander_wf *varpar,float pStartX, float pStartY, float pMaxIter) {\n"
        + "    fract_salamander_wf_init(varpar, pStartX, pStartY);\n"
        + "    int currIter = 0;\n"
        + "    while ((currIter++ < pMaxIter) && !(varpar->jwf_fract_salamander_wf_xs + varpar->jwf_fract_salamander_wf_ys >= 4.0)) {\n"
        + "      fract_salamander_wf_nextIteration(varpar);\n"
        + "    }\n"
        + "    return currIter;\n"
        + "}\n";
  }

}
