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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class PostPointSymmetryWFFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  public static final String PARAM_CENTRE_X = "centre_x";
  public static final String PARAM_CENTRE_Y = "centre_y";
  public static final String PARAM_ORDER = "order";
  private static final String PARAM_COLORSHIFT = "colorshift";
  private static final String[] paramNames = {PARAM_CENTRE_X, PARAM_CENTRE_Y, PARAM_ORDER, PARAM_COLORSHIFT};

  private double centre_x = 0.25;
  private double centre_y = 0.5;
  private int order = 3;
  private double colorshift = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
                        double pAmount) {
    double dx = (pVarTP.x - centre_x) * pAmount;
    double dy = (pVarTP.y - centre_y) * pAmount;
    int idx = pContext.random(order);
    pVarTP.x = centre_x + dx * _cosa[idx] + dy * _sina[idx];
    pVarTP.y = centre_y + dy * _cosa[idx] - dx * _sina[idx];
    pVarTP.color = fmod(pVarTP.color + idx * colorshift, 1.0);
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{centre_x, centre_y, order, colorshift};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_CENTRE_X.equalsIgnoreCase(pName))
      centre_x = pValue;
    else if (PARAM_CENTRE_Y.equalsIgnoreCase(pName))
      centre_y = pValue;
    else if (PARAM_ORDER.equalsIgnoreCase(pName))
      order = limitIntVal(Tools.FTOI(pValue), 1, Integer.MAX_VALUE);
    else if (PARAM_COLORSHIFT.equalsIgnoreCase(pName))
      colorshift = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "post_point_symmetry_wf";
  }

  @Override
  public int getPriority() {
    return 1;
  }

  private double _sina[], _cosa[];

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _sina = new double[order];
    _cosa = new double[order];
    double da = M_2PI / (double) order;
    double angle = 0.0;
    for (int i = 0; i < order; i++) {
      _sina[i] = sin(angle);
      _cosa[i] = cos(angle);
      angle += da;
    }
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_POST, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "int order = lroundf(varpar->post_point_symmetry_wf_order);\n"
        + "if(order>36) order=36;\n"
        +"float _sina[36];\n"
        + "float _cosa[36];\n"
        + "    float da = (2.0f*PI) / (float) order;\n"
        + "    float angle = 0.0;\n"
        + "    for (int i = 0; i < order; i++) {\n"
        + "      _sina[i] = sinf(angle);\n"
        + "      _cosa[i] = cosf(angle);\n"
        + "      angle += da;\n"
        + "    }\n"
        + "    float dx = (__px - varpar->post_point_symmetry_wf_centre_x) * varpar->post_point_symmetry_wf;\n"
        + "    float dy = (__py - varpar->post_point_symmetry_wf_centre_y) * varpar->post_point_symmetry_wf;\n"
        + "    int idx = lroundf(RANDFLOAT() * (order-1));\n"
        + "    __px = varpar->post_point_symmetry_wf_centre_x + dx * _cosa[idx] + dy * _sina[idx];\n"
        + "    __py = varpar->post_point_symmetry_wf_centre_y + dy * _cosa[idx] - dx * _sina[idx];\n"
        + "    __pal = fmodf(__pal + idx * varpar->post_point_symmetry_wf_colorshift, 1.0);\n";
  }
}
