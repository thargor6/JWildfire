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
 
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import static org.jwildfire.base.mathlib.MathLib.*;
 
import org.jwildfire.base.Tools;
 
public class Glitchy1Func extends VariationFunc {
    private static final long serialVersionUID = 1L;
 
    private static final String PARAM_SCALEX = "scale_x";
    private static final String PARAM_SHIFTX = "shift_x";
    private static final String PARAM_NX = "n_x";
    private static final String PARAM_SCALEY = "scale_y";
    private static final String PARAM_SHIFTY = "shift_y";
    private static final String PARAM_NY = "n_y";
    private static final String PARAM_SCALEZ = "scale_z";
    private static final String PARAM_SHIFTZ = "shift_z";
    private static final String PARAM_NZ = "n_z";
    private static final String PARAM_WIDTH = "width";
    private static final String PARAM_SEED = "seed";
    private static final String PARAM_ANGLE = "angle";
    private static final String PARAM_UU = "u";
    private static final String PARAM_VV = "v";
    private static final String PARAM_WW = "w";
 
    private static final String[] paramNames = { PARAM_SCALEX, PARAM_SHIFTX, PARAM_NX, PARAM_SCALEY, PARAM_SHIFTY,
            PARAM_NY, PARAM_SCALEZ, PARAM_SHIFTZ, PARAM_NZ, PARAM_WIDTH, PARAM_SEED, PARAM_ANGLE, PARAM_UU, PARAM_VV,
            PARAM_WW };
    private double scale_x = 1;
    private double scale_y = 1;
    private double scale_z = 0;
    private double shift_x = 0;
    private double shift_y = 0;
    private double shift_z = 0;
    private int nx = 2;
    private int ny = 2;
    private int nz = 2;;
    private double width = 1.0;
    private int seed = 42;
    private double angle = 30;
    private double u = 1.0;
    private double v = 1.0;
    private double w = 1.0;
   
    private double h, k;
 
    private double hash(int a) { // http://burtleburtle.net/bob/hash/integer.html
        a = (a ^ 61) ^ (a >> 16);
        a = a + (a << 3);
        a = a ^ (a >> 4);
        a = a * 0x27d4eb2d;
        a = a ^ (a >> 15);
        return (double) a / (double) Integer.MAX_VALUE;
    }
 
    @Override
    public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
      h = sin(Math.toRadians(angle));
      k = cos(Math.toRadians(angle));      
    }
   
    @Override
    public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
            double pAmount) {
        /*
         * Mix of lazysensen and pixel flow by bezo97, https://bezo97.tk/plugins.html
         * Variables added by Brad Stefanov and Rick Sidwell and special thanks to
         * dark-beam
         */
        double x = pAffineTP.x;
        double y = pAffineTP.y;
        double z = pAffineTP.z;
 
        if (scale_x != 0.0) {
            double nr = (int) floor((x - shift_x) * scale_x);
            if (nr >= 0) {
                if (nr % nx == (nx - 1))
                    x = 2 * shift_x - x;
            } else {
                if (nr % nx == 0)
                    x = 2 * shift_x - x;
            }
        }
        if (scale_y != 0.0) {
            double nr = (int) floor((y - shift_y) * scale_y);
            if (nr >= 0) {
                if (nr % ny == (ny - 1))
                    y = 2 * shift_y - y;
            } else {
                if (nr % ny == 0)
                    y = 2 * shift_y - y;
            }
        }
        if (scale_z != 0.0) {
            double nr = (int) floor((z - shift_z) * scale_z);
            if (nr >= 0) {
                if (nr % nz == (nz - 1))
                    z = 2 * shift_z - z;
            } else {
                if (nr % nz == 0)
                    z = 2 * shift_z - z;
            }
        }
 
        double uu = u * pAffineTP.x;
        double vv = v * (k * uu + h * pAffineTP.y);
        double ww = w * (k * uu - h * pAffineTP.y);
        int blocku = (int) floor(uu * width);
        blocku += (2.0 - 4.0 * hash(blocku * seed + 1));// varying width and length
        int blockv = (int) floor(vv * width);
        blockv += (2.0 - 4.0 * hash(blockv * seed + 1));
        int blockw = (int) floor(ww * width);
        blockw += (2.0 - 4.0 * hash(blockw * seed + 1));
        double fLen = (hash(blocku + blockv + blockw * -seed) + hash(blocku + blockv + blockw * seed / 2));
        // Doesn't matter just needs to be random enough
 
        pVarTP.x += pAmount * x * fLen;
        pVarTP.y += pAmount * y * fLen;
        pVarTP.z += pAmount * z * fLen;
 
    }
 
    @Override
    public String[] getParameterNames() {
        return paramNames;
    }
 
    @Override
    public Object[] getParameterValues() {
        return new Object[] { scale_x, shift_x, nx, scale_y, shift_y, ny, scale_z, shift_z, nz, width, seed, angle, u,
                v, w };
    }
 
    @Override
    public void setParameter(String pName, double pValue) {
        if (PARAM_SCALEX.equalsIgnoreCase(pName))
            scale_x = pValue;
        else if (PARAM_SHIFTX.equalsIgnoreCase(pName))
            shift_x = pValue;
        else if (PARAM_NX.equalsIgnoreCase(pName))
            nx = Tools.limitValue(Tools.FTOI(pValue), 1, 100);
        else if (PARAM_SCALEY.equalsIgnoreCase(pName))
            scale_y = pValue;
        else if (PARAM_SHIFTY.equalsIgnoreCase(pName))
            shift_y = pValue;
        else if (PARAM_NY.equalsIgnoreCase(pName))
            ny = Tools.limitValue(Tools.FTOI(pValue), 1, 100);
        else if (PARAM_SCALEZ.equalsIgnoreCase(pName))
            scale_z = pValue;
        else if (PARAM_SHIFTZ.equalsIgnoreCase(pName))
            shift_z = pValue;
        else if (PARAM_NZ.equalsIgnoreCase(pName))
            nz = Tools.limitValue(Tools.FTOI(pValue), 1, 100);
        else if (PARAM_WIDTH.equalsIgnoreCase(pName))
            width = pValue;
        else if (PARAM_SEED.equalsIgnoreCase(pName))
            seed = Tools.FTOI(pValue);
        else if (PARAM_ANGLE.equalsIgnoreCase(pName))
            angle = pValue;
        else if (PARAM_UU.equalsIgnoreCase(pName)) {
            u = pValue;
        } else if (PARAM_VV.equalsIgnoreCase(pName)) {
            v = pValue;
        } else if (PARAM_WW.equalsIgnoreCase(pName)) {
            w = pValue;
        }
 
        else
            throw new IllegalArgumentException(pName);
    }
 
    @Override
    public String getName() {
        return "glitchy1";
    }
 
}
