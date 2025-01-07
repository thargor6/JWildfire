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

public class PostColorScaleWFFunc extends VariationFunc implements SupportsGPU {
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
    pVarTP.x += pAmount * scale_x * pVarTP.x;
    pVarTP.y += pAmount * scale_y * pVarTP.y;
    double dz = pVarTP.color * scale_z * pAmount + offset_z;
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
    return "post_colorscale_wf";
  }

  @Override
  public int getPriority() {
    return 1;
  }

  @Override
  public void randomize() {
    if (Math.random() < 0.75) {
      scale_x = 0.0;
      scale_y = 0.0;
    } else {
      scale_x = Math.random();
      scale_y = Math.random();
    }
    scale_z = Math.random() * 2.0 - 1.0;
    offset_z = Math.random() * 2.0 - 1.0;
    reset_z = (int) (Math.random() * 2);
    sides = (int) (Math.random() * 2);
  }
  
  public void mutate(double pAmount) {
    switch ((int) (Math.random() * 6)) {
    case 0:
      scale_x += mutateStep(scale_x, pAmount);
      break;
    case 1:
      scale_y += mutateStep(scale_y, pAmount);
      break;
    case 2:
      scale_z += mutateStep(scale_z, pAmount);
      break;
    case 3:
      offset_z += mutateStep(offset_z, pAmount);
      break;
    case 4:
      reset_z = (reset_z > 0) ? 0 : 1;
      break;
    case 5:
      sides = (sides > 0) ? 0 : 1;
      break;
    }
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_POST, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }


  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "__px += __post_colorscale_wf * __post_colorscale_wf_scale_x * __px;\n"
        + "__py += __post_colorscale_wf * __post_colorscale_wf_scale_y * __py;\n"
        + "float dz = __pal * __post_colorscale_wf_scale_z * __post_colorscale_wf + __post_colorscale_wf_offset_z;\n"
        + "if (lroundf(__post_colorscale_wf_reset_z) > 0) {\n"
        + "  __pz = dz;\n"
        + "} else {\n"
        + "  if (lroundf(__post_colorscale_wf_sides) > 0) {\n"
        + "    __pz += dz * RANDFLOAT();\n"
        + "  } else {\n"
        + "     __pz += dz;\n"
        + "  }\n"
        + "}";
  }
}
