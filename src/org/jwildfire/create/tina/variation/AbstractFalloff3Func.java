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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public abstract class AbstractFalloff3Func extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_BLUR_TYPE = "blur_type";
  private static final String PARAM_BLUR_SHAPE = "blur_shape";
  private static final String PARAM_BLUR_STRENGTH = "blur_strength";

  private static final String PARAM_MIN_DISTANCE = "min_distance";
  private static final String PARAM_INVERT_DISTANCE = "invert_distance";

  private static final String PARAM_MUL_X = "mul_x";
  private static final String PARAM_MUL_Y = "mul_y";
  private static final String PARAM_MUL_Z = "mul_z";
  private static final String PARAM_MUL_C = "mul_c";

  private static final String PARAM_CENTER_X = "center_x";
  private static final String PARAM_CENTER_Y = "center_y";
  private static final String PARAM_CENTER_Z = "center_z";

  private static final String PARAM_ALPHA = "alpha";

  private static final String[] paramNames = {PARAM_BLUR_TYPE, PARAM_BLUR_SHAPE, PARAM_BLUR_STRENGTH, PARAM_MIN_DISTANCE, PARAM_INVERT_DISTANCE, PARAM_MUL_X, PARAM_MUL_Y, PARAM_MUL_Z, PARAM_MUL_C, PARAM_CENTER_X, PARAM_CENTER_Y, PARAM_CENTER_Z, PARAM_ALPHA};

  //adjustment coefficient
  private static final double SCATTER_ADJUST = 0.04;
  // blur types
  private static final int BT_GAUSSIAN = 0;
  private static final int BT_RADIAL = 1;
  private static final int BT_LOG = 2;
  // blur shapes
  private static final int BS_CIRCLE = 0;
  private static final int BS_SQUARE = 1;

  private int blur_type = 0;
  private int blur_shape = 0;
  private double blur_strength = 1.0;

  private double min_distance = 0.5;
  private int invert_distance = 0;

  private double mul_x = 1.0;
  private double mul_y = 1.0;
  private double mul_z = 0.0;
  private double mul_c = 0.0;

  private double center_x = 0.0;
  private double center_y = 0.0;
  private double center_z = 0.0;

  protected double alpha = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* falloff3 by Xyrus02, http://xyrus-02.deviantart.com/art/FallOff3-Plugin-for-Apophysis-7x-451048315 */
    fillVIn(pAffineTP, pVarTP, v_in);

    double weight = pAmount;
    double d_0 = min_distance;

    Double4 random = new Double4(pContext.random() - 0.5, pContext.random() - 0.5, pContext.random() - 0.5, pContext.random() - 0.5);

    double radius;
    switch (blur_shape) {
      case BS_CIRCLE:
        radius = bs_circle(v_in, center);
        break;
      case BS_SQUARE:
        radius = bs_square(v_in, center);
        break;
      default:
        throw new IllegalArgumentException("unsupported blur_shape <" + blur_shape + ">");
    }

    double dist = Math.max(((invert_distance != 0 ? Math.max(1 - radius, 0) : Math.max(radius, 0)) - d_0) * r_max, 0);

    Double4 v_out;
    switch (blur_type) {
      case BT_GAUSSIAN:
        v_out = bt_gaussian(v_in, mul, random, dist);
        break;
      case BT_RADIAL:
        v_out = bt_radial(v_in, mul, random, dist);
        break;
      case BT_LOG:
        v_out = bt_log(v_in, mul, random, dist);
        break;
      default:
        throw new IllegalArgumentException("unsupported blur_type <" + blur_type + ">");
    }

    // write back output vector
    applyVOut(pAffineTP, pVarTP, v_out, weight);
    // write back output color
    pVarTP.color = fabs(fmod(v_out.c, 1.0));
  }

  protected abstract void applyVOut(XYZPoint pAffineTP, XYZPoint pVarTP, Double4 pVOut, double weight);

  protected abstract void fillVIn(XYZPoint pAffineTP, XYZPoint pVarTP, Double4 pVIn);

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{blur_type, blur_shape, blur_strength, min_distance, invert_distance, mul_x, mul_y, mul_z, mul_c, center_x, center_y, center_z, alpha};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_BLUR_TYPE.equalsIgnoreCase(pName))
      blur_type = limitIntVal(Tools.FTOI(pValue), 0, 2);
    else if (PARAM_BLUR_SHAPE.equalsIgnoreCase(pName))
      blur_shape = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else if (PARAM_BLUR_STRENGTH.equalsIgnoreCase(pName)) {
      blur_strength = pValue;
      if (blur_strength < 1.0e-6)
        blur_strength = 1.0e-6;
    } else if (PARAM_MIN_DISTANCE.equalsIgnoreCase(pName)) {
      min_distance = pValue;
      if (min_distance < 0.0)
        min_distance = 0.0;
    } else if (PARAM_MUL_X.equalsIgnoreCase(pName))
      mul_x = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_MUL_Y.equalsIgnoreCase(pName))
      mul_y = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_MUL_Z.equalsIgnoreCase(pName))
      mul_z = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_MUL_C.equalsIgnoreCase(pName))
      mul_c = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_CENTER_X.equalsIgnoreCase(pName))
      center_x = pValue;
    else if (PARAM_CENTER_Y.equalsIgnoreCase(pName))
      center_y = pValue;
    else if (PARAM_CENTER_Z.equalsIgnoreCase(pName))
      center_z = pValue;
    else if (PARAM_INVERT_DISTANCE.equalsIgnoreCase(pName))
      invert_distance = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else if (PARAM_ALPHA.equalsIgnoreCase(pName))
      alpha = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "falloff3";
  }

  protected Double4 v_in;
  protected Double4 mul;
  protected Double3 center;
  protected double r_max;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    v_in = new Double4();
    mul = new Double4(mul_x, mul_y, mul_z, mul_c);
    center = new Double3(center_x, center_y, center_z);
    r_max = SCATTER_ADJUST * blur_strength;
  }

  protected static class Double4 {
    double x;
    double y;
    double z;
    double c;

    public Double4() {

    }

    public Double4(double pX, double pY, double pZ, double pC) {
      x = pX;
      y = pY;
      z = pZ;
      c = pC;
    }
  }

  protected static class Double3 {
    double x;
    double y;
    double z;

    public Double3() {

    }

    public Double3(double pX, double pY, double pZ) {
      x = pX;
      y = pY;
      z = pZ;
    }
  }

  private double sgnd(double x) {
    return x < 0 ? -1 : 1;
  }

  private double log_scale(double x) {
    return x == 0 ? 0 : log((fabs(x) + 1.0) * M_E) * sgnd(x) / M_E;
  }

  private double log_map(double x) {
    return x == 0 ? 0 : (M_E + log(x * M_E)) / 4.0 * sgnd(x);
  }

  private Double4 bt_gaussian(Double4 v_in, Double4 mul, Double4 random, double dist) {
    double sigma = dist * random.y * M_2PI;
    double phi = dist * random.z * M_PI;
    double rad = dist * random.x;

    double sigma_s = sin(sigma);
    double sigma_c = cos(sigma);

    double phi_s = sin(phi);
    double phi_c = cos(phi);

    return new Double4(
            v_in.x + mul.x * rad * sigma_c * phi_c,
            v_in.y + mul.y * rad * sigma_c * phi_s,
            v_in.z + mul.z * rad * sigma_s,
            v_in.c + mul.c * dist * random.c);
  }

  private Double4 bt_radial(Double4 v_in, Double4 mul, Double4 random, double dist) {
    if (v_in.x == 0 && v_in.y == 0 && v_in.z == 0)
      return v_in;

    double r_in = sqrt(sqr(v_in.x) + sqr(v_in.y) + sqr(v_in.z));

    double a = mul.y * dist + alpha * dist / sqrt(r_max);
    double b = mul.z * dist;

    double sigma = asin(v_in.z / r_in) + b * random.z;
    double phi = atan2(v_in.y, v_in.x) + a * random.y;
    double r = r_in + mul.x * random.x * dist;

    double sigma_s = sin(sigma);
    double sigma_c = cos(sigma);
    double phi_s = sin(phi);
    double phi_c = cos(phi);

    return new Double4(
            r * sigma_c * phi_c,
            r * sigma_c * phi_s,
            r * sigma_s,
            v_in.c + mul.c * random.c * dist);
  }

  private Double4 bt_log(Double4 v_in, Double4 mul, Double4 random, double dist) {
    double coeff = r_max <= EPSILON ? dist : dist + alpha * (log_map(dist) - dist);
    return new Double4(
            v_in.x + log_map(mul.x) * log_scale(random.x) * coeff,
            v_in.y + log_map(mul.y) * log_scale(random.y) * coeff,
            v_in.z + log_map(mul.z) * log_scale(random.z) * coeff,
            v_in.c + log_map(mul.c) * log_scale(random.c) * coeff);
  }

  private double bs_circle(Double4 v_in, Double3 center) {
    double distance = sqrt(
            sqr(v_in.x - center.x) +
                    sqr(v_in.y - center.y) +
                    sqr(v_in.z - center.z));
    return distance;
  }

  private double bs_square(Double4 v_in, Double3 center) {
    return Math.max(fabs(v_in.x - center.x), Math.max(fabs(v_in.y - center.y), (fabs(v_in.z - center.z))));
  }

}
