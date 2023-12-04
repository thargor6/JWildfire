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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;

import java.util.List;

public class FractMandelbrotWFFunc extends AbstractFractWFFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_POWER = "power";

  private int power;

  @Override
  public String getName() {
    return "fract_mandelbrot_wf";
  }

  @Override
  protected void initParams() {
    xmin = -2.35;
    xmax = 0.75;
    ymin = -1.2;
    ymax = 1.2;
    clip_iter_min = 3;
    power = 2;
    offsetx = 0.55;
    scale = 4.0;
  }

  @Override
  protected void addCustomParameterNames(List<String> pList) {
    pList.add(PARAM_POWER);
  }

  @Override
  protected void addCustomParameterValues(List<Object> pList) {
    pList.add(power);
  }

  @Override
  protected boolean setCustomParameter(String pName, double pValue) {
    if (PARAM_POWER.equalsIgnoreCase(pName)) {
      power = Tools.FTOI(pValue);
      if (power < 2)
        power = 2;
      return true;
    }
    return false;
  }

  public class Mandelbrot2Iterator extends Iterator {
    private static final long serialVersionUID = 1L;

    @Override
    protected void nextIteration() {
      double x1 = this.currX;
      double y1 = this.currY;
      this.xs = x1 * x1;
      this.ys = y1 * y1;

      y1 = 2.0 * x1 * y1 + this.startY;
      x1 = (this.xs - this.ys) + this.startX;

      setCurrPoint(x1, y1);
    }

  }

  public class Mandelbrot3Iterator extends Iterator {
    private static final long serialVersionUID = 1L;

    @Override
    protected void nextIteration() {
      double x1 = this.currX;
      double y1 = this.currY;
      this.xs = x1 * x1;
      this.ys = y1 * y1;

      double x2 = this.xs * x1 - 3.0 * x1 * this.ys + this.startX;
      y1 = 3.0 * this.xs * y1 - this.ys * y1 + this.startY;
      x1 = x2;
      setCurrPoint(x1, y1);
    }
  }

  public class Mandelbrot4Iterator extends Iterator {
    private static final long serialVersionUID = 1L;

    @Override
    protected void nextIteration() {
      double x1 = this.currX;
      double y1 = this.currY;
      this.xs = x1 * x1;
      this.ys = y1 * y1;
      double x2 = this.xs * this.xs + this.ys * this.ys - 6.0 * this.xs * this.ys + this.startX;
      y1 = 4.0 * x1 * y1 * (this.xs - this.ys) + this.startY;
      x1 = x2;
      setCurrPoint(x1, y1);
    }
  }

  public class MandelbrotNIterator extends Iterator {
    private static final long serialVersionUID = 1L;

    @Override
    protected void nextIteration() {
      double x1 = this.currX;
      double y1 = this.currY;
      this.xs = x1 * x1;
      this.ys = y1 * y1;
      double x2 = x1 * (this.xs * this.xs - 10.0 * this.xs * this.ys + 5.0 * this.ys * this.ys);
      double y2 = y1 * (this.ys * this.ys - 10.0 * this.xs * this.ys + 5.0 * this.xs * this.xs);
      for (int k = 5; k < power; k++) {
        double xa = x1 * x2 - y1 * y2;
        double ya = x1 * y2 + x2 * y1;
        x2 = xa;
        y2 = ya;
      }
      x1 = x2 + this.startX;
      y1 = y2 + this.startY;
      setCurrPoint(x1, y1);
    }

  }

  private Mandelbrot2Iterator iterator2 = new Mandelbrot2Iterator();
  private Mandelbrot3Iterator iterator3 = new Mandelbrot3Iterator();
  private Mandelbrot4Iterator iterator4 = new Mandelbrot4Iterator();
  private MandelbrotNIterator iteratorN = new MandelbrotNIterator();
  private Iterator iterator;

  @Override
  protected Iterator getIterator() {
    return iterator;
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    super.init(pContext, pLayer, pXForm, pAmount);
    switch (power) {
      case 2:
        iterator = iterator2;
        break;
      case 3:
        iterator = iterator3;
        break;
      case 4:
        iterator = iterator4;
        break;
      default:
        iterator = iteratorN;
        break;
    }
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_ESCAPE_TIME_FRACTAL, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTS_BACKGROUND};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "if (__fract_mandelbrot_wf_buddhabrot_mode > 0) {\n"
        + "    float x0=0, y0 = 0;\n"
        + "    int bb_max_clip_iter = 250;\n"
        + "    if (varpar->fract_mandelbrot_wf_chooseNewPoint) {\n"
        + "      for (int i = 0; i < bb_max_clip_iter; i++) {\n"
        + "        x0 = (__fract_mandelbrot_wf_xmax - __fract_mandelbrot_wf_xmin) * RANDFLOAT() + __fract_mandelbrot_wf_xmin;\n"
        + "        y0 = (__fract_mandelbrot_wf_ymax - __fract_mandelbrot_wf_ymin) * RANDFLOAT() + __fract_mandelbrot_wf_ymin;\n"
        + "        if (fract_mandelbrot_wf_preBuddhaIterate(varpar, x0, y0, __fract_mandelbrot_wf_max_iter)) {\n"
        + "          break;\n"
        + "        }\n"
        + "        if (i == bb_max_clip_iter - 1) {\n"
        + "          __doHide = true;\n"
        + "          break;\n"
        + "        }\n"
        + "      }\n"
        + "      if(!__doHide) {\n"
        + "        *(&varpar->fract_mandelbrot_wf_chooseNewPoint) = false;\n"
        + "        fract_mandelbrot_wf_init(varpar, x0, y0);\n"
        + "        for (int skip = 0; skip < __fract_mandelbrot_wf_buddhabrot_min_iter; skip++) {\n"
        + "          fract_mandelbrot_wf_nextIteration(varpar);\n"
        + "        }\n"
        + "      }\n"
        + "    }\n"
        + "    if(!__doHide) {\n"
        + "      fract_mandelbrot_wf_nextIteration(varpar);\n"
        + "      if (varpar->fract_mandelbrot_wf_currIter >= varpar->fract_mandelbrot_wf_maxIter) {\n"
        + "        *(&varpar->fract_mandelbrot_wf_chooseNewPoint) = true;\n"
        + "      }\n"
        + "      __px += __fract_mandelbrot_wf_scale * __fract_mandelbrot_wf * (varpar->fract_mandelbrot_wf_currX + __fract_mandelbrot_wf_offsetx);\n"
        + "      __py += __fract_mandelbrot_wf_scale * __fract_mandelbrot_wf * (varpar->fract_mandelbrot_wf_currY + __fract_mandelbrot_wf_offsety);\n"
        + "      __pal += (float) varpar->fract_mandelbrot_wf_currIter / (float) varpar->fract_mandelbrot_wf_maxIter;\n"
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
        + "    for (int i = 0; i < __fract_mandelbrot_wf_max_clip_iter; i++) {\n"
        + "      x0 = (__fract_mandelbrot_wf_xmax - __fract_mandelbrot_wf_xmin) * RANDFLOAT() + __fract_mandelbrot_wf_xmin;\n"
        + "      y0 = (__fract_mandelbrot_wf_ymax - __fract_mandelbrot_wf_ymin) * RANDFLOAT() + __fract_mandelbrot_wf_ymin;\n"
        + "      iterCount = fract_mandelbrot_wf_iterate(varpar, x0, y0, __fract_mandelbrot_wf_max_iter);\n"
        + "      if ((__fract_mandelbrot_wf_clip_iter_max < 0 && iterCount >= (__fract_mandelbrot_wf_max_iter + __fract_mandelbrot_wf_clip_iter_max)) || (__fract_mandelbrot_wf_clip_iter_min > 0 && iterCount <= __fract_mandelbrot_wf_clip_iter_min)) {\n"
        + "        if (i == __fract_mandelbrot_wf_max_clip_iter - 1) {\n"
        + "          __doHide = true;\n"
        + "          break;\n"
        + "        }\n"
        + "      } else {\n"
        + "        break;\n"
        + "      }\n"
        + "    }\n"
        + "    if(!__doHide) {\n"
        + "      __px += __fract_mandelbrot_wf_scale * __fract_mandelbrot_wf * (x0 + __fract_mandelbrot_wf_offsetx);\n"
        + "      __py += __fract_mandelbrot_wf_scale * __fract_mandelbrot_wf * (y0 + __fract_mandelbrot_wf_offsety);\n"
        + "      float z;\n"
        + "      if (lroundf(__fract_mandelbrot_wf_z_logscale) == 1) {\n"
        + "        z = __fract_mandelbrot_wf_scale * __fract_mandelbrot_wf * (__fract_mandelbrot_wf_scalez / 10 * log10f(1.0 + (float) iterCount / (float) __fract_mandelbrot_wf_max_iter) + __fract_mandelbrot_wf_offsetz);\n"
        + "        if (__fract_mandelbrot_wf_z_fill > 1.e-6f && RANDFLOAT() < __fract_mandelbrot_wf_z_fill) {\n"
        + "          float prevZ = __fract_mandelbrot_wf_scale * __fract_mandelbrot_wf * (__fract_mandelbrot_wf_scalez / 10 * log10f(1.0 + (float) (iterCount - 1) / (float) __fract_mandelbrot_wf_max_iter) + __fract_mandelbrot_wf_offsetz);\n"
        + "          z = (prevZ - z) * RANDFLOAT() + z;\n"
        + "        }\n"
        + "      } else {\n"
        + "        z = __fract_mandelbrot_wf_scale * __fract_mandelbrot_wf * (__fract_mandelbrot_wf_scalez / 10 * ((float) iterCount / (float) __fract_mandelbrot_wf_max_iter) + __fract_mandelbrot_wf_offsetz);\n"
        + "        if (__fract_mandelbrot_wf_z_fill > 1.e-6f && RANDFLOAT() < __fract_mandelbrot_wf_z_fill) {\n"
        + "          float prevZ = __fract_mandelbrot_wf_scale * __fract_mandelbrot_wf * (__fract_mandelbrot_wf_scalez / 10 * ((float) (iterCount - 1) / (float) __fract_mandelbrot_wf_max_iter) + __fract_mandelbrot_wf_offsetz);\n"
        + "          z = (prevZ - z) * RANDFLOAT() + z;\n"
        + "        }\n"
        + "      }\n"
        + "     __pz += z;\n"
        + "      if (lroundf(__fract_mandelbrot_wf_direct_color) != 0) {\n"
        + "        __pal += (float) iterCount / (float) __fract_mandelbrot_wf_max_iter;\n"
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
    return "__device__ void fract_mandelbrot_wf_init(struct VarPar__jwf_fract_mandelbrot_wf *varpar,float pX0, float pY0) {\n"
        + "    *(&varpar->jwf_fract_mandelbrot_wf_xs) = *(&varpar->jwf_fract_mandelbrot_wf_ys) = 0;\n"
        + "    *(&varpar->jwf_fract_mandelbrot_wf_startX) = *(&varpar->jwf_fract_mandelbrot_wf_currX) = pX0;\n"
        + "    *(&varpar->jwf_fract_mandelbrot_wf_startY) = *(&varpar->jwf_fract_mandelbrot_wf_currY) = pY0;\n"
        + "    *(&varpar->jwf_fract_mandelbrot_wf_currIter) = 0;\n"
        + "}\n"
        + "\n"
        + "__device__ void fract_mandelbrot_wf_setCurrPoint(struct VarPar__jwf_fract_mandelbrot_wf *varpar, float pX, float pY) {\n"
        + "   *(&varpar->jwf_fract_mandelbrot_wf_currX) = pX;\n"
        + "   *(&varpar->jwf_fract_mandelbrot_wf_currY) = pY;\n"
        + "   *(&varpar->jwf_fract_mandelbrot_wf_currIter) = varpar->jwf_fract_mandelbrot_wf_currIter + 1;\n"
        + "}\n"
        + "\n"
        + "__device__ void fract_mandelbrot_wf_nextIteration(struct VarPar__jwf_fract_mandelbrot_wf *varpar) {\n"
        + "    switch(lroundf(varpar->jwf_fract_mandelbrot_wf_power)) {\n"
        + "      case 2:\n"
        + "        {\n"
        + "          float x1 = varpar->jwf_fract_mandelbrot_wf_currX;\n"
        + "          float y1 = varpar->jwf_fract_mandelbrot_wf_currY;\n"
        + "          *(&varpar->jwf_fract_mandelbrot_wf_xs) = x1 * x1;\n"
        + "          *(&varpar->jwf_fract_mandelbrot_wf_ys) = y1 * y1;\n"
        + "          y1 = 2.0 * x1 * y1 + varpar->jwf_fract_mandelbrot_wf_startY;\n"
        + "          x1 = (varpar->jwf_fract_mandelbrot_wf_xs - varpar->jwf_fract_mandelbrot_wf_ys) + varpar->jwf_fract_mandelbrot_wf_startX;\n"
        + "          fract_mandelbrot_wf_setCurrPoint(varpar, x1, y1);\n"
        + "        }\n"
        + "        break;"
        + "      case 3:\n"
        + "        {\n"
        + "          float x1 =  varpar->jwf_fract_mandelbrot_wf_currX;\n"
        + "          float y1 =  varpar->jwf_fract_mandelbrot_wf_currY;\n"
        + "          *(& varpar->jwf_fract_mandelbrot_wf_xs) = x1 * x1;\n"
        + "          *(& varpar->jwf_fract_mandelbrot_wf_ys) = y1 * y1;\n"
        + "          float x2 = varpar->jwf_fract_mandelbrot_wf_xs * x1 - 3.0 * x1 *  varpar->jwf_fract_mandelbrot_wf_ys +  varpar->jwf_fract_mandelbrot_wf_startX;\n"
        + "          y1 = 3.0 * varpar->jwf_fract_mandelbrot_wf_xs * y1 -  varpar->jwf_fract_mandelbrot_wf_ys * y1 +  varpar->jwf_fract_mandelbrot_wf_startY;\n"
        + "          x1 = x2;\n"
        + "          fract_mandelbrot_wf_setCurrPoint(varpar, x1, y1);\n"
        + "        }\n"
        + "        break;"
        + "      case 4:\n"
        + "        {\n"
        + "          float x1 = varpar->jwf_fract_mandelbrot_wf_currX;\n"
        + "          float y1 = varpar->jwf_fract_mandelbrot_wf_currY;\n"
        + "          *(&varpar->jwf_fract_mandelbrot_wf_xs) = x1 * x1;\n"
        + "          *(&varpar->jwf_fract_mandelbrot_wf_ys) = y1 * y1;\n"
        + "          float x2 = varpar->jwf_fract_mandelbrot_wf_xs * varpar->jwf_fract_mandelbrot_wf_xs + varpar->jwf_fract_mandelbrot_wf_ys * varpar->jwf_fract_mandelbrot_wf_ys - 6.0 * varpar->jwf_fract_mandelbrot_wf_xs * varpar->jwf_fract_mandelbrot_wf_ys + varpar->jwf_fract_mandelbrot_wf_startX;\n"
        + "          y1 = 4.0 * x1 * y1 * (varpar->jwf_fract_mandelbrot_wf_xs - varpar->jwf_fract_mandelbrot_wf_ys) + varpar->jwf_fract_mandelbrot_wf_startY;\n"
        + "          x1 = x2;\n"
        + "          fract_mandelbrot_wf_setCurrPoint(varpar, x1, y1);\n"
        + "        }\n"
        + "        break;"
        + "      default:\n"
        + "        {\n"
        + "           float x1 = varpar->jwf_fract_mandelbrot_wf_currX;\n"
        + "           float y1 = varpar->jwf_fract_mandelbrot_wf_currY;\n"
        + "           *(&varpar->jwf_fract_mandelbrot_wf_xs) = x1 * x1;\n"
        + "           *(&varpar->jwf_fract_mandelbrot_wf_ys) = y1 * y1;\n"
        + "           float x2 = x1 * (varpar->jwf_fract_mandelbrot_wf_xs * varpar->jwf_fract_mandelbrot_wf_xs - 10.0 * varpar->jwf_fract_mandelbrot_wf_xs * varpar->jwf_fract_mandelbrot_wf_ys + 5.0 * varpar->jwf_fract_mandelbrot_wf_ys * varpar->jwf_fract_mandelbrot_wf_ys);\n"
        + "           float y2 = y1 * (varpar->jwf_fract_mandelbrot_wf_ys * varpar->jwf_fract_mandelbrot_wf_ys - 10.0 * varpar->jwf_fract_mandelbrot_wf_xs * varpar->jwf_fract_mandelbrot_wf_ys + 5.0 * varpar->jwf_fract_mandelbrot_wf_xs * varpar->jwf_fract_mandelbrot_wf_xs);\n"
        + "           for (int k = 5; k < varpar->jwf_fract_mandelbrot_wf_power; k++) {\n"
        + "             float xa = x1 * x2 - y1 * y2;\n"
        + "             float ya = x1 * y2 + x2 * y1;\n"
        + "             x2 = xa;\n"
        + "             y2 = ya;\n"
        + "           }\n"
        + "           x1 = x2 + varpar->jwf_fract_mandelbrot_wf_startX;\n"
        + "           y1 = y2 + varpar->jwf_fract_mandelbrot_wf_startY;\n"
        + "           fract_mandelbrot_wf_setCurrPoint(varpar, x1, y1);\n"
        + "         }\n"
        + "      }\n"
        + "}\n"
        + "\n"
        + "__device__ bool fract_mandelbrot_wf_preBuddhaIterate(struct VarPar__jwf_fract_mandelbrot_wf *varpar, float pStartX, float pStartY, int pMaxIter) {\n"
        + "     fract_mandelbrot_wf_init(varpar, pStartX, pStartY);\n"
        + "     int currIter = 0;\n"
        + "     while ((currIter++ < pMaxIter) && !(varpar->jwf_fract_mandelbrot_wf_xs + varpar->jwf_fract_mandelbrot_wf_ys >= 4.0)) {\n"
        + "        fract_mandelbrot_wf_nextIteration(varpar);\n"
        + "     }\n"
        + "     if (varpar->jwf_fract_mandelbrot_wf_xs + varpar->jwf_fract_mandelbrot_wf_ys >= 4.0) {\n"
        + "        *(&varpar->jwf_fract_mandelbrot_wf_maxIter) = currIter;\n"
        + "        return varpar->jwf_fract_mandelbrot_wf_maxIter > varpar->jwf_fract_mandelbrot_wf_buddhabrot_min_iter;\n"
        + "      } else {\n"
        + "        return false;\n"
        + "      }\n"
        + "}\n"
        + "\n"
        + "__device__ int fract_mandelbrot_wf_iterate(struct VarPar__jwf_fract_mandelbrot_wf *varpar,float pStartX, float pStartY, float pMaxIter) {\n"
        + "    fract_mandelbrot_wf_init(varpar, pStartX, pStartY);\n"
        + "    int currIter = 0;\n"
        + "    while ((currIter++ < pMaxIter) && !(varpar->jwf_fract_mandelbrot_wf_xs + varpar->jwf_fract_mandelbrot_wf_ys >= 4.0)) {\n"
        + "      fract_mandelbrot_wf_nextIteration(varpar);\n"
        + "    }\n"
        + "    return currIter;\n"
        + "}\n";
  }
}
