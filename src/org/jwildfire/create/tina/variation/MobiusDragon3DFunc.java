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

import org.jwildfire.base.mathlib.Complex;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.base.Tools;
import static org.jwildfire.base.mathlib.MathLib.*;


public class MobiusDragon3DFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_RE = "re";
  private static final String PARAM_IM = "im";
  private static final String PARAM_X_SPREAD = "x_spread";
  private static final String PARAM_Y_SPREAD = "y_spread";
  private static final String PARAM_Z_SPREAD = "z_spread";
  private static final String PARAM_X_ADD = "x_add";
  private static final String PARAM_Y_ADD = "y_add";
  private static final String PARAM_LOG_SPREAD = "log_spread";
  private static final String PARAM_LINE_ENABLE = "line_enable";
  private static final String PARAM_LINE_WEIGHT = "line_weight";
  private static final String PARAM_LINE_COLOR_SHIFT = "line_color_shift";
  private static final String PARAM_MAG_COLOR = "mag_color";
  private static final String PARAM_MAG_COLOR_SCALE = "mag_color_scale";
  private static final String PARAM_ITERATIONS = "iterations";
  private static final String[] paramNames = { PARAM_RE, PARAM_IM, PARAM_X_SPREAD, PARAM_Y_SPREAD, PARAM_Z_SPREAD, PARAM_X_ADD, PARAM_Y_ADD, PARAM_LOG_SPREAD, PARAM_LINE_ENABLE, PARAM_LINE_WEIGHT, PARAM_LINE_COLOR_SHIFT, PARAM_MAG_COLOR, PARAM_MAG_COLOR_SCALE, PARAM_ITERATIONS};
  private double re = 1.0;
  private double im = 0.0;
  private double x_spread = 1.0;
  private double y_spread = 0.0;
  private double z_spread = 0.0;
  private double x_add = 0.0;
  private double y_add = 0.0;
  private double log_spread = 2.71828;
  private int line_enable = 1;
  private double line_weight = 0.125;
  private double line_color_shift = 0.1;
  private int mag_color = 1;
  private double mag_color_scale = 0.5;
  private int iterations = 1;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // mobius_dragon_3D by Whittaker Courtney with built-in offsets and variables etc.

    double zc = pVarTP.color;
    double random = pContext.random();
    double x1 = pAffineTP.x;
    double y1 = pAffineTP.y;
    double z1 = pAffineTP.z;
    double spreadx = -x_spread;
    double spready = -y_spread;
    double spreadz = -z_spread;
    Complex z = new Complex(pAffineTP.x, pAffineTP.y + 1.0);
    Complex z2 = new Complex(re, im);

    for (int i = 0; i < iterations; i++){
      z.re = pAffineTP.x;
      z.im = pAffineTP.y + 1.0;
      z.Div(z2);

      // reciprocal of z for 3D
      double r1 = pAmount/(z.re*z.re+z.im*z.im+z1*z1);
      z.re = z.re * r1;
      z.im = -z.im * r1;
      pVarTP.z = -z1 * r1;

      z.Scale(pAmount);
      pAffineTP.x = z.re;
      pAffineTP.y = z.im + 1.0;


      // line
      if (line_enable == 1){
        if ( random < line_weight){
          z.re = pContext.random() * spreadx;
          z.im = 0.0;
          pVarTP.z = 0.0;
          pVarTP.color += line_color_shift;
        }
      }
      // experimental iteration coloring that didn't work out but may add in the future:
      //zc += ((double)k*.1);
    }

    // Log_tile by Zy0rg implemented into JWildfire by Brad Stefanov
    //converted to a post variation by Whittaker Courtney

    if (pContext.random() < 0.5)
      spreadx = x_spread;

    if (pContext.random() < 0.5)
      spready = y_spread;

    if (pContext.random() < 0.5)
      spreadz = z_spread;

    if(random < 0.5){
      z.re = pAmount * x_add + (z.re + spreadx * round(log(pContext.random())/log(log_spread)));
      z.im = pAmount * (y_add + (z.im + spready * round(log(pContext.random())/log(log_spread))))+1.0;
      pVarTP.z = pAmount * (pVarTP.z + spreadz * round(log(pContext.random())/log(log_spread)));
    }
    else{
      z.re = pAmount * (-z.re + spreadx * round(log(pContext.random())/log(log_spread)));
      z.im = pAmount * (-2 -(z.im + spready * round(log(pContext.random())/log(log_spread))))+1.0;
      pVarTP.z = pAmount * (-pVarTP.z + spreadz * round(log(pContext.random())/log(log_spread)));
    }

// magnitude coloring:
    double mag = sqrt(z.re*z.re+z.im*z.im+z1*z1)*(mag_color_scale /6);

    if (mag_color == 1){
      zc += tanh(mag);
    }


    pVarTP.x = z.re;
    pVarTP.y = z.im;
    pVarTP.color = zc;



    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] {re, im, x_spread, y_spread, z_spread, x_add, y_add, log_spread, line_enable, line_weight, line_color_shift, mag_color, mag_color_scale, iterations};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RE.equalsIgnoreCase(pName))
      re = pValue;
    else if (PARAM_IM.equalsIgnoreCase(pName))
      im = pValue;
    else if (PARAM_X_SPREAD.equalsIgnoreCase(pName))
      x_spread = pValue;
    else if (PARAM_Y_SPREAD.equalsIgnoreCase(pName))
      y_spread = pValue;
    else if (PARAM_Z_SPREAD.equalsIgnoreCase(pName))
      z_spread = pValue;
    else if (PARAM_X_ADD.equalsIgnoreCase(pName))
      x_add = pValue;
    else if (PARAM_Y_ADD.equalsIgnoreCase(pName))
      y_add = pValue;
    else if (PARAM_LOG_SPREAD.equalsIgnoreCase(pName))
      log_spread = pValue;
    else if (PARAM_LINE_ENABLE.equalsIgnoreCase(pName))
      line_enable = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else if (PARAM_LINE_WEIGHT.equalsIgnoreCase(pName))
      line_weight = pValue;
    else if (PARAM_LINE_COLOR_SHIFT.equalsIgnoreCase(pName))
      line_color_shift = pValue;
    else if (PARAM_MAG_COLOR.equalsIgnoreCase(pName))
      mag_color = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else if (PARAM_MAG_COLOR_SCALE.equalsIgnoreCase(pName))
      mag_color_scale = pValue;
    else if (PARAM_ITERATIONS.equalsIgnoreCase(pName))
      iterations = limitIntVal(Tools.FTOI(pValue), 0, 24);
    else
      throw new IllegalArgumentException(pName);
  }


  @Override
  public String getName() {
    return "mobius_dragon_3D";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return  "    float zc = __pal;\n" +
            "    float random = RANDFLOAT();\n" +
            "    float x1 = __x;\n" +
            "    float y1 = __y;\n" +
            "    float z1 = __z;\n" +
            "    float spreadx = -__mobius_dragon_3D_x_spread;\n" +
            "    float spready = -__mobius_dragon_3D_y_spread;\n" +
            "    float spreadz = -__mobius_dragon_3D_z_spread;\n" +
            "    Complex z;\n" +
            "    Complex_Init(&z, __x, __y + 1.0f);\n" +
            "    Complex z2;\n" +
            "    Complex_Init(&z2, __mobius_dragon_3D_re, __mobius_dragon_3D_im);\n" +
            "\n" +
            "    for (int i = 0; i < __mobius_dragon_3D_iterations; i++){\n" +
            "      z.re = __x;\n" +
            "      z.im = __y + 1.0;\n" +
            "      Complex_Div(&z, &z2);\n" +
            "\n" +
            "      \n" +
            "      float r1 = __mobius_dragon_3D/(z.re*z.re+z.im*z.im+z1*z1);\n" +
            "      z.re = z.re * r1;\n" +
            "      z.im = -z.im * r1;\n" +
            "      __pz = -z1 * r1;\n" +
            "\n" +
            "      Complex_Scale(&z, __mobius_dragon_3D);\n" +
            "      __x = z.re;\n" +
            "      __y = z.im + 1.0f;\n" +
            "\n" +
            "      \n" +
            "      if (__mobius_dragon_3D_line_enable == 1){\n" +
            "        if (random < __mobius_dragon_3D_line_weight){\n" +
            "          z.re = RANDFLOAT() * spreadx;\n" +
            "          z.im = 0.0f;\n" +
            "          __pz = 0.0f;\n" +
            "          __pal += __mobius_dragon_3D_line_color_shift;\n" +
            "        }\n" +
            "      }\n" +
            "      \n" +
            "      \n" +
            "    }\n" +
            "\n" +
            "    \n" +
            "    \n" +
            "\n" +
            "    if (RANDFLOAT() < 0.5f)\n" +
            "      spreadx = __mobius_dragon_3D_x_spread;\n" +
            "\n" +
            "    if (RANDFLOAT() < 0.5f)\n" +
            "      spready = __mobius_dragon_3D_y_spread;\n" +
            "\n" +
            "    if (RANDFLOAT() < 0.5f)\n" +
            "      spreadz = __mobius_dragon_3D_z_spread;\n" +
            "\n" +
            "    if(random < 0.5f){\n" +
            "      z.re = __mobius_dragon_3D * __mobius_dragon_3D_x_add + (z.re + spreadx * roundf(logf(RANDFLOAT())/logf(__mobius_dragon_3D_log_spread)));\n" +
            "      z.im = __mobius_dragon_3D * (__mobius_dragon_3D_y_add + (z.im + spready * roundf(logf(RANDFLOAT())/logf(__mobius_dragon_3D_log_spread))))+1.0;\n" +
            "      __pz = __mobius_dragon_3D * (__pz + spreadz * roundf(logf(RANDFLOAT())/logf(__mobius_dragon_3D_log_spread)));\n" +
            "    }\n" +
            "    else{\n" +
            "      z.re = __mobius_dragon_3D * (-z.re + spreadx * roundf(logf(RANDFLOAT())/logf(__mobius_dragon_3D_log_spread)));\n" +
            "      z.im = __mobius_dragon_3D * (-2 -(z.im + spready * roundf(logf(RANDFLOAT())/logf(__mobius_dragon_3D_log_spread))))+1.0;\n" +
            "      __pz = __mobius_dragon_3D * (-__pz + spreadz * roundf(logf(RANDFLOAT())/logf(__mobius_dragon_3D_log_spread)));\n" +
            "    }\n" +
            "\n" +
            "\n" +
            "    float mag = sqrtf(z.re*z.re+z.im*z.im+z1*z1)*(__mobius_dragon_3D_mag_color_scale / 6.0f);\n" +
            "\n" +
            "    if (__mobius_dragon_3D_mag_color == 1){\n" +
            "      zc += tanhf(mag);\n" +
            "    }\n" +
            "\n" +
            "\n" +
            "    __px = z.re;\n" +
            "    __py = z.im;\n" +
            "    __pal = zc;\n" +
            "\n" +
            (context.isPreserveZCoordinate() ? "      __pz += __mobius_dragon_3D * __z;\n" : "");
  }

}
