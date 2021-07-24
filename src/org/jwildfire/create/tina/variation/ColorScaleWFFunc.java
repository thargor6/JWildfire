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

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class ColorScaleWFFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALEX = "scale_x";
  private static final String PARAM_SCALEY = "scale_y";
  private static final String PARAM_SCALEZ = "scale_z";
  private static final String PARAM_OFFSETZ = "offset_z";
  private static final String PARAM_RESETZ = "reset_z";
  private static final String PARAM_SIDES = "sides";


  private static final String[] paramNames = {PARAM_SCALEX, PARAM_SCALEY, PARAM_SCALEZ, PARAM_OFFSETZ, PARAM_RESETZ, PARAM_SIDES};

  private double scale_x = 0.0;
  private double scale_y = 0.0;
  private double scale_z = 0.5;
  private double offset_z = 0.0;
  private double reset_z = 0.0;
  private double sides = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    pVarTP.x += pAmount * scale_x * pAffineTP.x;
    pVarTP.y += pAmount * scale_y * pAffineTP.y;
    double dz = pAffineTP.color * scale_z * pAmount + offset_z;
    if (reset_z > 0) {
      pVarTP.z = dz;
    } else {
	if (sides > 0) {
		pVarTP.z += dz * pContext.random();
	} else {
		pVarTP.z += dz;
	}
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{scale_x, scale_y, scale_z, offset_z, reset_z, sides };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALEX.equalsIgnoreCase(pName))
      scale_x = pValue;
    else if (PARAM_SCALEY.equalsIgnoreCase(pName))
      scale_y = pValue;
    else if (PARAM_SCALEZ.equalsIgnoreCase(pName))
      scale_z = pValue;
    else if (PARAM_OFFSETZ.equalsIgnoreCase(pName))
      offset_z = pValue;
    else if (PARAM_RESETZ.equalsIgnoreCase(pName))
      reset_z = pValue;
    else if (PARAM_SIDES.equalsIgnoreCase(pName))
      sides = pValue;    
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "colorscale_wf";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "__px += __colorscale_wf * __colorscale_wf_scale_x * __x;\n"
            + "__py += __colorscale_wf * __colorscale_wf_scale_y * __y;\n"
            + "float dz = __pal * __colorscale_wf_scale_z * __colorscale_wf + __colorscale_wf_offset_z;\n"
            + "if (lroundf(__colorscale_wf_reset_z) > 0) {\n"
            + "  __pz = dz;\n"
            + "} else {\n"
            + "  if (lroundf(__colorscale_wf_sides) > 0) {\n"
            + "    __pz += dz * RANDFLOAT();\n"
            + "  } else {\n"
            + "     __pz += dz;\n"
            + "  }\n"
            + "}";
  }

}
