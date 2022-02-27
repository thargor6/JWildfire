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

import org.jwildfire.base.mathlib.Complex;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.fmod;

public class CombimirrorFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;
  private static final String PARAM_VMIRROR = "vmirror";
  private static final String PARAM_VMOVE = "vmove";
  private static final String PARAM_HMIRROR = "hmirror";
  private static final String PARAM_HMOVE = "hmove";
  private static final String PARAM_ZMIRROR = "zmirror";
  private static final String PARAM_ZMOVE = "zmove";
  private static final String PARAM_PMIRROR = "pmirror";
  private static final String PARAM_PMOVEX = "pmovex";
  private static final String PARAM_PMOVEY = "pmovey";
  private static final String PARAM_VCOLORSHIFT = "vcolorshift";
  private static final String PARAM_HCOLORSHIFT = "hcolorshift";
  private static final String PARAM_ZCOLORSHIFT = "zcolorshift";
  private static final String PARAM_PCOLORSHIFT = "pcolorshift";

  private static final String[] paramNames = {
    PARAM_VMIRROR,
    PARAM_VMOVE,
    PARAM_HMIRROR,
    PARAM_HMOVE,
    PARAM_ZMIRROR,
    PARAM_ZMOVE,
          PARAM_PMIRROR,
          PARAM_PMOVEX,
          PARAM_PMOVEY,
    PARAM_VCOLORSHIFT,
    PARAM_HCOLORSHIFT,
    PARAM_ZCOLORSHIFT,
    PARAM_PCOLORSHIFT
  };
  private double vmirror = 1.0;
  private double vmove = 0.05;
  private double hmirror = 0.5;
  private double hmove = 0.35;
  private double zmirror = 0.0;
  private double zmove = 0.0;
  private double pmirror = 0.0;
  private double pmovex = 0.05;
  private double pmovey = 0.0;
  private double vcolorshift = 0.0;
  private double hcolorshift = 0.0;
  private double zcolorshift = 0.0;
  private double pcolorshift = 0.0;

  @Override
  public void transform(
      FlameTransformationContext pContext,
      XForm pXForm,
      XYZPoint pAffineTP,
      XYZPoint pVarTP,
      double pAmount) {
    /*
    Combination of vertical, horizontal, z mirror and point mirror
    Mirror parameters work in range of 0..2
    by Thomas Michels and the great support by Brad Stefanov
    https://www.jwfsanctuary.club/custom-variations/custom-variation-combimirror-final-release/
    */

    Complex z = new Complex(pAffineTP.x, pAffineTP.y);
    Complex z2 = new Complex(pAffineTP.z, pAffineTP.y);
    z.Scale(pAmount);
    z2.Scale(pAmount);
    pVarTP.x = z.re;
    pVarTP.y = z.im;
    pVarTP.z = z2.re;
    // Mirror around center point
    if (pContext.random() > pmirror / 2) {
      pVarTP.x = -pVarTP.x + pmovex;
      pVarTP.y = -pVarTP.y + pmovey;
      pVarTP.color = fmod(pVarTP.color + pcolorshift, 1.0);
    }
    // Mirror along vertical axis
    if (pContext.random() < vmirror / 2) {
      pVarTP.x = -pVarTP.x + vmove;
      pVarTP.color = fmod(pVarTP.color + vcolorshift, 1.0);
    }
    // Mirror along horizontal axis
    if (pContext.random() < hmirror / 2) {
      pVarTP.y = -pVarTP.y + hmove;
      pVarTP.color = fmod(pVarTP.color + hcolorshift, 1.0);
    }
    // Mirror along Z axis
    if (pContext.random() < zmirror / 2) {
      pVarTP.z = -pVarTP.z + zmove;
      pVarTP.color = fmod(pVarTP.color + zcolorshift, 1.0);
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] {
            vmirror, vmove, hmirror, hmove, zmirror, zmove, pmirror, pmovex, pmovey, vcolorshift, hcolorshift, zcolorshift, pcolorshift
    };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_VMIRROR.equalsIgnoreCase(pName)) vmirror = pValue;
    else if (PARAM_VMOVE.equalsIgnoreCase(pName)) vmove = pValue;
    else if (PARAM_HMIRROR.equalsIgnoreCase(pName)) hmirror = pValue;
    else if (PARAM_HMOVE.equalsIgnoreCase(pName)) hmove = pValue;
    else if (PARAM_ZMIRROR.equalsIgnoreCase(pName)) zmirror = pValue;
    else if (PARAM_ZMOVE.equalsIgnoreCase(pName)) zmove = pValue;
    else if (PARAM_PMIRROR.equalsIgnoreCase(pName)) pmirror = pValue;
    else if (PARAM_PMOVEX.equalsIgnoreCase(pName)) pmovex = pValue;
    else if (PARAM_PMOVEY.equalsIgnoreCase(pName)) pmovey = pValue;
    else if (PARAM_VCOLORSHIFT.equalsIgnoreCase(pName)) vcolorshift = pValue;
    else if (PARAM_HCOLORSHIFT.equalsIgnoreCase(pName)) hcolorshift = pValue;
    else if (PARAM_ZCOLORSHIFT.equalsIgnoreCase(pName)) zcolorshift = pValue;
    else if (PARAM_PCOLORSHIFT.equalsIgnoreCase(pName)) pcolorshift = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "combimirror";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[] {VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "   Complex z;\n"
        + "    Complex_Init(&z, __x, __y);\n"
        + "    Complex z2;\n"
        + "    Complex_Init(&z2, __z, __y);\n"
        + "    Complex_Scale(&z, __combimirror);\n"
        + "    Complex_Scale(&z2, __combimirror);\n"
        + "    __px = z.re;\n"
        + "    __py = z.im;\n"
        + "    __pz = z2.re;\n"
        + "    if (RANDFLOAT() > __combimirror_pmirror / 2) {\n"
        + "      __px = -__px + __combimirror_pmovex;\n"
        + "      __py = -__py + __combimirror_pmovey;\n"
        + "      __pal = fmodf(__pal + __combimirror_pcolorshift, 1.0);\n"
        + "    }\n"
        + "    if (RANDFLOAT() < __combimirror_vmirror / 2) {\n"
        + "      __px = -__px + __combimirror_vmove;\n"
        + "      __pal = fmodf(__pal + __combimirror_vcolorshift, 1.0);\n"
        + "    }\n"
        + "    if (RANDFLOAT() < __combimirror_hmirror / 2) {\n"
        + "      __py = -__py + __combimirror_hmove;\n"
        + "      __pal = fmodf(__pal + __combimirror_hcolorshift, 1.0);\n"
        + "    }\n"
        + "    if (RANDFLOAT() < __combimirror_zmirror / 2) {\n"
        + "      __pz = -__pz + __combimirror_zmove;\n"
        + "      __pal = fmodf(__pal + __combimirror_zcolorshift, 1.0);\n"
        + "    }\n";
  }
}
