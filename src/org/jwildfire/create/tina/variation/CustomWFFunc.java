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

public class CustomWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String PARAM_D = "d";
  private static final String PARAM_E = "e";
  private static final String PARAM_F = "f";
  private static final String PARAM_G = "g";

  private static final String RESSOURCE_CODE = "code";

  private static final String[] paramNames = {PARAM_A, PARAM_B, PARAM_C, PARAM_D, PARAM_E, PARAM_F, PARAM_G};
  private static final String[] ressourceNames = {RESSOURCE_CODE};

  private double a = 0.0;
  private double b = 0.0;
  private double c = 0.0;
  private double d = 0.0;
  private double e = 0.0;
  private double f = 0.0;
  private double g = 0.0;

  private String code = "import org.jwildfire.create.tina.base.XForm;\r\n" +
          "import org.jwildfire.create.tina.variation.FlameTransformationContext;\r\n" +
          "import org.jwildfire.create.tina.base.XYZPoint;\r\n" +
          "import static org.jwildfire.base.mathlib.MathLib.*;\r\n" +
          "\r\n" +
          "  public void init(FlameTransformationContext pContext, XForm pXForm) {\r\n" +
          "\r\n" +
          "  }\r\n" +
          "\r\n" +
          "  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {\r\n" +
          "    // Some examples:\r\n" +
          "    // \"hemisphere\" variation\r\n" +
          "    //   double r = pAmount / sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y + 1);\r\n" +
          "    //   pVarTP.x += pAffineTP.x * r;\r\n" +
          "    //   pVarTP.y += pAffineTP.y * r;\r\n" +
          "    //   pVarTP.z += r;\r\n" +
          "    // ----------------------------------\r\n" +
          "    // change the color dynamically\r\n" +
          "    //   if(pAffineTP.x<0) {\r\n" +
          "    //     pVarTP.color = 0;\r\n" +
          "    //   }\r\n" +
          "    //   else {\r\n" +
          "    //     pVarTP.color = 0.75;\r\n" +
          "    //   }\r\n" +
          "    // ----------------------------------\r\n" +
          "    // \"rose_wf\" variation\r\n" +
          "    //   final double amp=0.5;\r\n" +
          "    //   final double waves=4;\r\n" +
          "    //   double a0 = pAffineTP.getPrecalcAtan(pContext);\r\n" +
          "    //   double r0 = pAffineTP.getPrecalcSqrt(pContext);\r\n" +
          "    //\r\n" +
          "    //   double r = amp * cos(waves * a0);\r\n" +
          "    //\r\n" +
          "    //   double nx = sin(a0) * r;\r\n" +
          "    //   double ny = cos(a0) * r;\r\n" +
          "    //   pVarTP.x += pAmount * nx;\r\n" +
          "    //   pVarTP.y += pAmount * ny;\r\n" +
          "    // ----------------------------------\r\n" +
          "    // \"bubble\" variation\r\n" +
          "       double r = ((pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y) / 4.0 + 1.0);\r\n" +
          "       double t = pAmount / r;\r\n" +
          "       pVarTP.x += t * pAffineTP.x;\r\n" +
          "       pVarTP.y += t * pAffineTP.y;\r\n" +
          "       pVarTP.z += pAmount * (2.0 / r - 1.0);\r\n" +
          "    // ----------------------------------\r\n" +
          "    // ...\r\n" +
          "  }\r\n" +
          "";
  private CustomWFFuncRunner customFuncRunner = null;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    if (customFuncRunner != null) {
      customFuncRunner.transform(pContext, pXForm, pAffineTP, pVarTP, pAmount);
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{a, b, c, d, e, f, g};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_A.equalsIgnoreCase(pName))
      a = pValue;
    else if (PARAM_B.equalsIgnoreCase(pName))
      b = pValue;
    else if (PARAM_C.equalsIgnoreCase(pName))
      c = pValue;
    else if (PARAM_D.equalsIgnoreCase(pName))
      d = pValue;
    else if (PARAM_E.equalsIgnoreCase(pName))
      e = pValue;
    else if (PARAM_F.equalsIgnoreCase(pName))
      f = pValue;
    else if (PARAM_G.equalsIgnoreCase(pName))
      g = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][]{(code != null ? code.getBytes() : null)};
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (pName.equals(RESSOURCE_CODE)) {
      return RessourceType.JAVA_CODE;
    } else {
      return super.getRessourceType(pName);
    }
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_CODE.equalsIgnoreCase(pName)) {
      code = pValue != null ? new String(pValue) : "";
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "custom_wf";
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    if (customFuncRunner == null) {
      compile();
    }
    if (customFuncRunner != null) {
      customFuncRunner.setA(a);
      customFuncRunner.setB(b);
      customFuncRunner.setC(c);
      customFuncRunner.setD(d);
      customFuncRunner.setE(e);
      customFuncRunner.setF(f);
      customFuncRunner.setG(g);
      customFuncRunner.init(pContext, pXForm);
    }
  }

  private void compile() {
    try {
      customFuncRunner = CustomWFFuncRunner.compile(code);
    } catch (Throwable ex) {
      System.out.println("##############################################################");
      System.out.println(ex.getMessage());
      System.out.println("##############################################################");
      System.out.println(code);
      System.out.println("##############################################################");
    }
  }

  @Override
  public void validate() {
    try {
      if (code != null) {
        CustomWFFuncRunner.compile(code);
      }
    } catch (Throwable ex) {
      throw new RuntimeException(ex);
    }
  }

}
