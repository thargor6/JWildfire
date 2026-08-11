/*

  JWildfire - an image and animation processor written in Java

  Copyright (C) 1995-2026 Andreas Maschke

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

import static org.jwildfire.base.mathlib.MathLib.atan2;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class TorusKnotFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_P = "p";
  private static final String PARAM_Q = "q";
  private static final String PARAM_R_BIG = "r_big";
  private static final String PARAM_R_SMALL = "r_small";
  private static final String[] paramNames = {PARAM_P, PARAM_Q, PARAM_R_BIG, PARAM_R_SMALL};

  private double p = 2.0;
  private double q = 3.0;
  private double r_big = 1.0;
  private double r_small = 0.3;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double t = atan2(pAffineTP.y, pAffineTP.x);
    double outer = this.r_big + this.r_small * cos(this.q * t);

    pVarTP.x += pAmount * outer * cos(this.p * t);
    pVarTP.y += pAmount * outer * sin(this.p * t);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{p, q, r_big, r_small}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_P.equalsIgnoreCase(pName)) p = pValue;
    else if (PARAM_Q.equalsIgnoreCase(pName)) q = pValue;
    else if (PARAM_R_BIG.equalsIgnoreCase(pName)) r_big = pValue;
    else if (PARAM_R_SMALL.equalsIgnoreCase(pName)) r_small = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "torus_knot"; }
  
  @Override
  public void randomize() {
  	p = Math.random() * 19.0 + 1.0;
  	q = Math.random() * 19.0 + 1.0;
  	if (Math.random() < 0.5) {
  		p = Math.round(p);
  		q = Math.round(q);
  	}
  	r_big = Math.random() * 3.0;
  	if (Math.random() < 0.6) {
  		r_small = Math.random() * r_big;
  	} else {
  		r_small = Math.random() * 2.75 + 0.25;
  	}
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float t = atan2f(__y, __x);\n"
         + "  float outer = __torus_knot_r_big + __torus_knot_r_small * cosf(__torus_knot_q * t);\n"
         + "  __px += __torus_knot * outer * cosf(__torus_knot_p * t);\n"
         + "  __py += __torus_knot * outer * sinf(__torus_knot_p * t);\n";
  }
}