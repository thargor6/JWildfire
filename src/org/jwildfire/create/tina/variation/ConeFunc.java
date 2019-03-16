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
 
 
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import static org.jwildfire.base.mathlib.MathLib.*;
 
public class ConeFunc extends VariationFunc {
    private static final long serialVersionUID = 1L;
 
    private static final String PARAM_RADIUS1 = "radius1";
    private static final String PARAM_RADIUS2 = "radius2";
    private static final String PARAM_SIZE1 = "size1";
    private static final String PARAM_SIZE2 = "size2";
    private static final String PARAM_YWAVE = "ywave";
    private static final String PARAM_XWAVE = "xwave";
    private static final String PARAM_HEIGHT = "height";
    private static final String PARAM_WARP = "warp";
    private static final String PARAM_WEIGHT = "weight";
 
    private static final String[] paramNames = { PARAM_RADIUS1, PARAM_RADIUS2, PARAM_SIZE1, PARAM_SIZE2, PARAM_YWAVE, PARAM_XWAVE, PARAM_HEIGHT,
            PARAM_WARP, PARAM_WEIGHT };
    private double radius1 = 0.5;
    private double radius2 = 1.0;
    private double size1 = 0.5;
    private double size2 = 2.0;
    private double ywave = 1.0;
    private double xwave = 1.0;
    private double height = 1.0;
    private double warp = 1.0;
    private double weight = 2.0;
 
    @Override
    public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
            double pAmount) {
    //A mix of julia and hemisphere that creates a cone shape by Brad Stefanov         
    double r = pAmount / sqrt(pAffineTP.x * pAffineTP.x *warp+ pAffineTP.y * pAffineTP.y + size1)*size2;
    double xx = pAffineTP.getPrecalcAtan()* radius1 + M_PI * (int) (weight * pContext.random())*radius2;
    double sina = sin(xx*ywave);
    double cosa = cos(xx*xwave);
    pVarTP.x += r * cosa;
    pVarTP.y += r * sina;
    pVarTP.z += r * height;
 
    }
 
    @Override
    public String[] getParameterNames() {
        return paramNames;
    }
 
    @Override
    public Object[] getParameterValues() {
        return new Object[] { radius1, radius2, size1, size2, ywave, xwave, height, warp, weight };
    }
 
    @Override
    public void setParameter(String pName, double pValue) {
        if (PARAM_RADIUS1.equalsIgnoreCase(pName)) {
            radius1 = pValue;
        } else if (PARAM_RADIUS2.equalsIgnoreCase(pName)) {
            radius2 = pValue;
        } else if (PARAM_SIZE1.equalsIgnoreCase(pName)) {
            size1 = pValue;
        } else if (PARAM_SIZE2.equalsIgnoreCase(pName)) {
            size2 = pValue;
        } else if (PARAM_YWAVE.equalsIgnoreCase(pName)) {
            ywave = pValue;
        } else if (PARAM_XWAVE.equalsIgnoreCase(pName)) {
            xwave = pValue;
        } else if (PARAM_HEIGHT.equalsIgnoreCase(pName)) {
            height = pValue;
        } else if (PARAM_WARP.equalsIgnoreCase(pName)) {
            warp = pValue;
        } else if (PARAM_WEIGHT.equalsIgnoreCase(pName)) {
            weight = pValue;           
        } else {
            System.out.println("pName not recognized: " + pName);
            throw new IllegalArgumentException(pName);
        }
    }
 
    @Override
    public String getName() {
        return "cone";
    }
 
 
}
