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

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class OctapolFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_POLARWEIGHT = "polarweight";
  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_S = "s";
  private static final String PARAM_T = "t";

  private static final String[] paramNames = {PARAM_POLARWEIGHT, PARAM_RADIUS, PARAM_S, PARAM_T};

  private double polarweight = 0.0;
  private double radius = 1.0;
  private double s = 0.5;
  private double t = 0.5;

  private final Double2 _XY = new Double2();
  private final MutableDouble _r = new MutableDouble();
  private final MutableDouble _u = new MutableDouble();
  private final MutableDouble _v = new MutableDouble();

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Octapol, originally written by Georg K. (http://xyrus02.deviantart.com) */
    double x = pAffineTP.x * 0.15, y = pAffineTP.y * 0.15, z = pAffineTP.z, x2 = 0, y2 = 0;
    _r.value = _u.value = _v.value = 0;
    _XY.setXy(x, y);

    if ((_rad > 0) && hits_circle_around_origin(_rad, _XY, _r)) {
      double rd = MathLib.log(MathLib.sqr(_r.value / _rad));
      double phi = MathLib.atan2(y, x);
      pVarTP.x += pAmount * lerp(x, phi, rd * polarweight);
      pVarTP.y += pAmount * lerp(y, _r.value, rd * polarweight);
    } else if (hits_square_around_origin(_a, _XY)) {
      if (hits_rect(_H, _K, _XY) || hits_rect(_J, _D, _XY) ||
              hits_rect(_A, _J, _XY) || hits_rect(_K, _E, _XY) ||
              hits_triangle(_I, _A, _H, _XY, _u, _v) ||
      hits_triangle(_J, _B, _C, _XY, _u, _v) ||
      hits_triangle(_L, _D, _E, _XY, _u, _v) ||
      hits_triangle(_K, _F, _G, _XY, _u, _v)) {
        pVarTP.x += pAmount * x; pVarTP.y += pAmount * y;
      } else pVarTP.x = pVarTP.y = 0;
    } else pVarTP.x = pVarTP.y = 0;

    pVarTP.x += pAmount * x;
    pVarTP.y += pAmount * y;
    pVarTP.z += pAmount * z;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{polarweight, radius, s, t};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POLARWEIGHT.equalsIgnoreCase(pName))
      polarweight = pValue;
    else if (PARAM_RADIUS.equalsIgnoreCase(pName))
      radius = pValue;
    else if (PARAM_S.equalsIgnoreCase(pName))
      s = pValue;
    else if (PARAM_T.equalsIgnoreCase(pName))
      t = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "octapol";
  }

  private double _a, _rad;
  private double _ax, _bx, _cx, _dx, _ex, _fx, _gx, _hx, _ix, _jx, _kx, _lx;
  private double _ay, _by, _cy, _dy, _ey, _fy, _gy, _hy, _iy, _jy, _ky, _ly;
  private final Double2 _A = new Double2(), _B = new Double2(), _C = new Double2(), _D = new Double2(), _E = new Double2();
  private final Double2 _F = new Double2(), _G = new Double2(), _H = new Double2(), _I = new Double2(), _J = new Double2();
  private final Double2 _K = new Double2(), _L = new Double2();

  private final static double DENOM_SQRT2 = 0.707106781;

  private class Double2 {
    public double x, y;

    public Double2() {
    }

    public Double2(double x, double y) {
      this.x = x;
      this.y = y;
    }

    public void setXy(double x, double y) {
      this.x = x;
      this.y = y;
    }
  }

  private class MutableDouble {
    public double value;
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _a = s * 0.5 + t;

    _rad = DENOM_SQRT2 * s * MathLib.fabs(radius);

    _ax = -0.5 * s; _ay = 0.5 * s + t;
    _bx = 0.5 * s; _by = 0.5 * s + t;
    _cx = t; _cy = 0.5 * s;
    _dx = t; _dy = -0.5 * s;
    _ex = 0.5 * s; _ey = -0.5 * s - t;
    _fx = -0.5 * s; _fy = -0.5 * s - t;
    _gx = -t; _gy = -0.5 * s;
    _hx = -t; _hy = 0.5 * s;
    _ix = -0.5 * s; _iy = 0.5 * s;
    _jx = 0.5 * s; _jy = 0.5 * s;
    _kx = -0.5 * s; _ky = -0.5 * s;
    _lx = 0.5 * s; _ly = -0.5 * s;

    _A.setXy(_ax, _ay);
    _B.setXy(_bx, _by);
    _C.setXy(_cx, _cy);
    _D.setXy(_dx, _dy);
    _E.setXy(_ex, _ey);
    _F.setXy(_fx, _fy);
    _G.setXy(_gx, _gy);
    _H.setXy(_hx, _hy);
    _I.setXy(_ix, _iy);
    _J.setXy(_jx, _jy);
    _K.setXy(_kx, _ky);
    _L.setXy(_lx, _ly);
  }

  double dot(Double2 a, Double2 b) {
    return a.x * b.x + a.y * b.y;
  }

  double lerp(double a, double b, double p) {
    return a + p * (b - a);
  }

  boolean hits_rect(Double2 tl, Double2 br, Double2 p) {
    return (p.x >= tl.x && p.y >= tl.y && p.x <= br.x && p.y <= br.y);
  }

  private final Double2 _v0 = new Double2();
  private final Double2 _v1 = new Double2();
  private final Double2 _v2 = new Double2();

  boolean hits_triangle(Double2 a, Double2 b, Double2 c, Double2 p, MutableDouble u, MutableDouble v) {
    _v0.setXy(c.x - a.x, c.y - a.y);
    _v1.setXy(b.x - a.x, b.y - a.y);
    _v2.setXy(p.x - a.x, p.y - a.y);

    double d00 = dot(_v0, _v0);
    double d01 = dot(_v0, _v1);
    double d02 = dot(_v0, _v2);
    double d11 = dot(_v1, _v1);
    double d12 = dot(_v1, _v2);

    double denom = (d00 * d11 - d01 * d01);
    if (denom != 0) {
			u.value = (d11 * d02 - d01 * d12) / denom;
			v.value = (d00 * d12 - d01 * d02) / denom;
    } else {
			u.value = v.value = 0;
    }

    return ((u.value + v.value) < 1.0) && (u.value > 0) && (v.value > 0);
  }

  boolean hits_square_around_origin(double a, Double2 p) {
    return (MathLib.fabs(p.x) <= a && MathLib.fabs(p.y) <= a);
  }

  boolean hits_circle_around_origin(double radius, Double2 p, MutableDouble r) {
    if (radius == 0.0) return true;
		r.value = MathLib.sqrt(MathLib.sqr(p.x) + MathLib.sqr(p.y));
    return (r.value <= radius);
  }
}
