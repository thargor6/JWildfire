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

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public abstract class AbstractFractWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_MAX_ITER = "max_iter";
  private static final String PARAM_XMIN = "xmin";
  private static final String PARAM_XMAX = "xmax";
  private static final String PARAM_YMIN = "ymin";
  private static final String PARAM_YMAX = "ymax";
  private static final String PARAM_DIRECT_COLOR = "direct_color";
  private static final String PARAM_SCALEZ = "scalez";
  private static final String PARAM_CLIP_ITER_MIN = "clip_iter_min";
  private static final String PARAM_CLIP_ITER_MAX = "clip_iter_max";
  private static final String PARAM_MAX_CLIP_ITER = "max_clip_iter";
  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_OFFSETX = "offsetx";
  private static final String PARAM_OFFSETY = "offsety";
  private static final String PARAM_OFFSETZ = "offsetz";

  protected int max_iter = 100;
  protected double xmin = -1.6;
  protected double xmax = 1.6;
  protected double ymin = -1.2;
  protected double ymax = 1.2;
  protected int direct_color = 1;
  protected double scalez = 1.0;
  protected int clip_iter_min = 3;
  protected int clip_iter_max = -5;
  protected double scale = 3.0;
  protected double offsetx = 0.0;
  protected double offsety = 0.0;
  protected double offsetz = 0.0;
  protected int max_clip_iter = 3;

  public AbstractFractWFFunc() {
    initParams();
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double x0 = 0.0, y0 = 0.0;
    int iterCount = 0;
    for (int i = 0; i < max_clip_iter; i++) {
      x0 = (xmax - xmin) * pContext.random() + xmin;
      y0 = (ymax - ymin) * pContext.random() + ymin;
      iterCount = iterate(x0, y0);
      if ((clip_iter_max < 0 && iterCount >= (max_iter + clip_iter_max)) || (clip_iter_min > 0 && iterCount <= clip_iter_min)) {
        if (i == max_clip_iter - 1) {
          pVarTP.x = pVarTP.y = pVarTP.z = -120000.0 * (Math.random() + 0.5);
          return;
        }
      }
      else {
        break;
      }
    }

    pVarTP.x += scale * pAmount * (x0 + offsetx);
    pVarTP.y += scale * pAmount * (y0 + offsety);
    pVarTP.z += scale * pAmount * (scalez / 10 * ((double) iterCount / (double) max_iter) + offsetz);
    if (direct_color != 0) {
      pVarTP.color = (double) iterCount / (double) max_iter;
      if (pVarTP.color < 0)
        pVarTP.color = 0;
      else if (pVarTP.color > 1.0)
        pVarTP.color = 1.0;
    }
  }

  protected abstract int iterate(double pX, double pY);

  protected abstract void initParams();

  protected abstract void addCustomParameterNames(List<String> pList);

  protected abstract void addCustomParameterValues(List<Object> pList);

  protected abstract boolean setCustomParameter(String pName, double pValue);

  @Override
  public String[] getParameterNames() {
    List<String> lst = new ArrayList<String>();
    lst.add(PARAM_MAX_ITER);
    lst.add(PARAM_XMIN);
    lst.add(PARAM_XMAX);
    lst.add(PARAM_YMIN);
    lst.add(PARAM_YMAX);
    addCustomParameterNames(lst);
    lst.add(PARAM_DIRECT_COLOR);
    lst.add(PARAM_SCALEZ);
    lst.add(PARAM_CLIP_ITER_MIN);
    lst.add(PARAM_CLIP_ITER_MAX);
    lst.add(PARAM_MAX_CLIP_ITER);
    lst.add(PARAM_SCALE);
    lst.add(PARAM_OFFSETX);
    lst.add(PARAM_OFFSETY);
    lst.add(PARAM_OFFSETZ);
    return lst.toArray(new String[0]);
  }

  @Override
  public Object[] getParameterValues() {
    List<Object> lst = new ArrayList<Object>();
    lst.add(max_iter);
    lst.add(xmin);
    lst.add(xmax);
    lst.add(ymin);
    lst.add(ymax);
    addCustomParameterValues(lst);
    lst.add(direct_color);
    lst.add(scalez);
    lst.add(clip_iter_min);
    lst.add(clip_iter_max);
    lst.add(max_clip_iter);
    lst.add(scale);
    lst.add(offsetx);
    lst.add(offsety);
    lst.add(offsetz);
    return lst.toArray();
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_MAX_ITER.equalsIgnoreCase(pName))
      max_iter = Tools.FTOI(pValue);
    else if (PARAM_XMIN.equalsIgnoreCase(pName))
      xmin = pValue;
    else if (PARAM_XMAX.equalsIgnoreCase(pName))
      xmax = pValue;
    else if (PARAM_YMIN.equalsIgnoreCase(pName))
      ymin = pValue;
    else if (PARAM_YMAX.equalsIgnoreCase(pName))
      ymax = pValue;
    else if (PARAM_DIRECT_COLOR.equalsIgnoreCase(pName))
      direct_color = Tools.FTOI(pValue);
    else if (PARAM_SCALEZ.equalsIgnoreCase(pName))
      scalez = pValue;
    else if (PARAM_CLIP_ITER_MIN.equalsIgnoreCase(pName))
      clip_iter_min = Tools.FTOI(pValue);
    else if (PARAM_CLIP_ITER_MAX.equalsIgnoreCase(pName))
      clip_iter_max = Tools.FTOI(pValue);
    else if (PARAM_MAX_CLIP_ITER.equalsIgnoreCase(pName))
      max_clip_iter = Tools.FTOI(pValue);
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else if (PARAM_OFFSETX.equalsIgnoreCase(pName))
      offsetx = pValue;
    else if (PARAM_OFFSETY.equalsIgnoreCase(pName))
      offsety = pValue;
    else if (PARAM_OFFSETZ.equalsIgnoreCase(pName))
      offsetz = pValue;
    else if (!setCustomParameter(pName, pValue))
      throw new IllegalArgumentException(pName);
  }

}
