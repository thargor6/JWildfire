/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2023 Andreas Maschke

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

public class FractMeteorsWFFunc extends AbstractFractWFFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public String getName() {
    return "fract_meteors_wf";
  }

  @Override
  protected void initParams() {
    xmin = -1.7;
    xmax = 1.7;
    ymin = -1.1;
    ymax = 1.1;
    clip_iter_min = 3;
    scale = 5.7;
  }

  @Override
  protected boolean setCustomParameter(String pName, double pValue) {
    return false;
  }

  @Override
  protected void addCustomParameterNames(List<String> pList) {
    // no op
  }

  @Override
  protected void addCustomParameterValues(List<Object> pList) {
    // no op
  }

  public class MeteorsIterator extends Iterator {

    @Override
    protected void nextIteration() {
      double x1 = this.currX;
      double y1 = this.currY;
      this.xs = x1 * x1;
      this.ys = y1 * y1;
      double x2 = (this.startX * x1 - this.startY * y1) - (this.startX * x1 + this.startY * y1) / (this.xs + this.ys);
      y1 = (this.startX * y1 + this.startY * x1) + (this.startX * y1 - this.startY * x1) / (this.xs + this.ys);
      x1 = x2;
      setCurrPoint(x1, y1);
    }
  }

  private MeteorsIterator iterator = new MeteorsIterator();

  @Override
  protected Iterator getIterator() {
    return iterator;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_ESCAPE_TIME_FRACTAL, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTS_BACKGROUND};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "if (__fract_meteors_wf_buddhabrot_mode > 0) {\n"
            + "    float x0=0, y0 = 0;\n"
            + "    int bb_max_clip_iter = 250;\n"
            + "    if (varpar->fract_meteors_wf_chooseNewPoint) {\n"
            + "      for (int i = 0; i < bb_max_clip_iter; i++) {\n"
            + "        x0 = (__fract_meteors_wf_xmax - __fract_meteors_wf_xmin) * RANDFLOAT() + __fract_meteors_wf_xmin;\n"
            + "        y0 = (__fract_meteors_wf_ymax - __fract_meteors_wf_ymin) * RANDFLOAT() + __fract_meteors_wf_ymin;\n"
            + "        if (fract_meteors_wf_preBuddhaIterate(varpar, x0, y0, __fract_meteors_wf_max_iter)) {\n"
            + "          break;\n"
            + "        }\n"
            + "        if (i == bb_max_clip_iter - 1) {\n"
            + "          __doHide = true;\n"
            + "          break;\n"
            + "        }\n"
            + "      }\n"
            + "      if(!__doHide) {\n"
            + "        *(&varpar->fract_meteors_wf_chooseNewPoint) = false;\n"
            + "        fract_meteors_wf_init(varpar, x0, y0);\n"
            + "        for (int skip = 0; skip < __fract_meteors_wf_buddhabrot_min_iter; skip++) {\n"
            + "          fract_meteors_wf_nextIteration(varpar);\n"
            + "        }\n"
            + "      }\n"
            + "    }\n"
            + "    if(!__doHide) {\n"
            + "      fract_meteors_wf_nextIteration(varpar);\n"
            + "      if (varpar->fract_meteors_wf_currIter >= varpar->fract_meteors_wf_maxIter) {\n"
            + "        *(&varpar->fract_meteors_wf_chooseNewPoint) = true;\n"
            + "      }\n"
            + "      __px += __fract_meteors_wf_scale * __fract_meteors_wf * (varpar->fract_meteors_wf_currX + __fract_meteors_wf_offsetx);\n"
            + "      __py += __fract_meteors_wf_scale * __fract_meteors_wf * (varpar->fract_meteors_wf_currY + __fract_meteors_wf_offsety);\n"
            + "      __pal += (float) varpar->fract_meteors_wf_currIter / (float) varpar->fract_meteors_wf_maxIter;\n"
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
            + "    for (int i = 0; i < __fract_meteors_wf_max_clip_iter; i++) {\n"
            + "      x0 = (__fract_meteors_wf_xmax - __fract_meteors_wf_xmin) * RANDFLOAT() + __fract_meteors_wf_xmin;\n"
            + "      y0 = (__fract_meteors_wf_ymax - __fract_meteors_wf_ymin) * RANDFLOAT() + __fract_meteors_wf_ymin;\n"
            + "      iterCount = fract_meteors_wf_iterate(varpar, x0, y0, __fract_meteors_wf_max_iter);\n"
            + "      if ((__fract_meteors_wf_clip_iter_max < 0 && iterCount >= (__fract_meteors_wf_max_iter + __fract_meteors_wf_clip_iter_max)) || (__fract_meteors_wf_clip_iter_min > 0 && iterCount <= __fract_meteors_wf_clip_iter_min)) {\n"
            + "        if (i == __fract_meteors_wf_max_clip_iter - 1) {\n"
            + "          __doHide = true;\n"
            + "          break;\n"
            + "        }\n"
            + "      } else {\n"
            + "        break;\n"
            + "      }\n"
            + "    }\n"
            + "    if(!__doHide) {\n"
            + "      __px += __fract_meteors_wf_scale * __fract_meteors_wf * (x0 + __fract_meteors_wf_offsetx);\n"
            + "      __py += __fract_meteors_wf_scale * __fract_meteors_wf * (y0 + __fract_meteors_wf_offsety);\n"
            + "      float z;\n"
            + "      if (lroundf(__fract_meteors_wf_z_logscale) == 1) {\n"
            + "        z = __fract_meteors_wf_scale * __fract_meteors_wf * (__fract_meteors_wf_scalez / 10 * log10f(1.0 + (float) iterCount / (float) __fract_meteors_wf_max_iter) + __fract_meteors_wf_offsetz);\n"
            + "        if (__fract_meteors_wf_z_fill > 1.e-6f && RANDFLOAT() < __fract_meteors_wf_z_fill) {\n"
            + "          float prevZ = __fract_meteors_wf_scale * __fract_meteors_wf * (__fract_meteors_wf_scalez / 10 * log10f(1.0 + (float) (iterCount - 1) / (float) __fract_meteors_wf_max_iter) + __fract_meteors_wf_offsetz);\n"
            + "          z = (prevZ - z) * RANDFLOAT() + z;\n"
            + "        }\n"
            + "      } else {\n"
            + "        z = __fract_meteors_wf_scale * __fract_meteors_wf * (__fract_meteors_wf_scalez / 10 * ((float) iterCount / (float) __fract_meteors_wf_max_iter) + __fract_meteors_wf_offsetz);\n"
            + "        if (__fract_meteors_wf_z_fill > 1.e-6f && RANDFLOAT() < __fract_meteors_wf_z_fill) {\n"
            + "          float prevZ = __fract_meteors_wf_scale * __fract_meteors_wf * (__fract_meteors_wf_scalez / 10 * ((float) (iterCount - 1) / (float) __fract_meteors_wf_max_iter) + __fract_meteors_wf_offsetz);\n"
            + "          z = (prevZ - z) * RANDFLOAT() + z;\n"
            + "        }\n"
            + "      }\n"
            + "     __pz += z;\n"
            + "      if (lroundf(__fract_meteors_wf_direct_color) != 0) {\n"
            + "        __pal += (float) iterCount / (float) __fract_meteors_wf_max_iter;\n"
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
    return "__device__ void fract_meteors_wf_init(struct VarPar__jwf_fract_meteors_wf *varpar,float pX0, float pY0) {\n"
        + "    *(&varpar->jwf_fract_meteors_wf_xs) = *(&varpar->jwf_fract_meteors_wf_ys) = 0;\n"
        + "    *(&varpar->jwf_fract_meteors_wf_startX) = *(&varpar->jwf_fract_meteors_wf_currX) = pX0;\n"
        + "    *(&varpar->jwf_fract_meteors_wf_startY) = *(&varpar->jwf_fract_meteors_wf_currY) = pY0;\n"
        + "    *(&varpar->jwf_fract_meteors_wf_currIter) = 0;\n"
        + "}\n"
        + "\n"
        + "__device__ void fract_meteors_wf_setCurrPoint(struct VarPar__jwf_fract_meteors_wf *varpar, float pX, float pY) {\n"
        + "   *(&varpar->jwf_fract_meteors_wf_currX) = pX;\n"
        + "   *(&varpar->jwf_fract_meteors_wf_currY) = pY;\n"
        + "   *(&varpar->jwf_fract_meteors_wf_currIter) = varpar->jwf_fract_meteors_wf_currIter + 1;\n"
        + "}\n"
        + "\n"
        + "__device__ void fract_meteors_wf_nextIteration(struct VarPar__jwf_fract_meteors_wf *varpar) {\n"
        + "      float x1 = varpar->jwf_fract_meteors_wf_currX;\n"
        + "      float y1 = varpar->jwf_fract_meteors_wf_currY;\n"
        + "      *(&varpar->jwf_fract_meteors_wf_xs) = x1 * x1;\n"
        + "      *(&varpar->jwf_fract_meteors_wf_ys) = y1 * y1;\n"
        + "      float x2 = (varpar->jwf_fract_meteors_wf_startX * x1 - varpar->jwf_fract_meteors_wf_startY * y1) - (varpar->jwf_fract_meteors_wf_startX * x1 + varpar->jwf_fract_meteors_wf_startY * y1) / (varpar->jwf_fract_meteors_wf_xs + varpar->jwf_fract_meteors_wf_ys);\n"
        + "      y1 = (varpar->jwf_fract_meteors_wf_startX * y1 + varpar->jwf_fract_meteors_wf_startY * x1) + (varpar->jwf_fract_meteors_wf_startX * y1 - varpar->jwf_fract_meteors_wf_startY * x1) / (varpar->jwf_fract_meteors_wf_xs + varpar->jwf_fract_meteors_wf_ys);\n"
        + "      x1 = x2;"
        + "      fract_meteors_wf_setCurrPoint(varpar, x1, y1);\n"
        + "}\n"
        + "\n"
        + "__device__ bool fract_meteors_wf_preBuddhaIterate(struct VarPar__jwf_fract_meteors_wf *varpar, float pStartX, float pStartY, int pMaxIter) {\n"
        + "     fract_meteors_wf_init(varpar, pStartX, pStartY);\n"
        + "     int currIter = 0;\n"
        + "     while ((currIter++ < pMaxIter) && !(varpar->jwf_fract_meteors_wf_xs + varpar->jwf_fract_meteors_wf_ys >= 4.0)) {\n"
        + "        fract_meteors_wf_nextIteration(varpar);\n"
        + "     }\n"
        + "     if (varpar->jwf_fract_meteors_wf_xs + varpar->jwf_fract_meteors_wf_ys >= 4.0) {\n"
        + "        *(&varpar->jwf_fract_meteors_wf_maxIter) = currIter;\n"
        + "        return varpar->jwf_fract_meteors_wf_maxIter > varpar->jwf_fract_meteors_wf_buddhabrot_min_iter;\n"
        + "      } else {\n"
        + "        return false;\n"
        + "      }\n"
        + "}\n"
        + "\n"
        + "__device__ int fract_meteors_wf_iterate(struct VarPar__jwf_fract_meteors_wf *varpar,float pStartX, float pStartY, float pMaxIter) {\n"
        + "    fract_meteors_wf_init(varpar, pStartX, pStartY);\n"
        + "    int currIter = 0;\n"
        + "    while ((currIter++ < pMaxIter) && !(varpar->jwf_fract_meteors_wf_xs + varpar->jwf_fract_meteors_wf_ys >= 4.0)) {\n"
        + "      fract_meteors_wf_nextIteration(varpar);\n"
        + "    }\n"
        + "    return currIter;\n"
        + "}\n";
  }


}
