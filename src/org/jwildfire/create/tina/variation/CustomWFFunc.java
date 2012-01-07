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

public class CustomWFFunc extends VariationFunc {

  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String PARAM_D = "d";
  private static final String PARAM_E = "e";
  private static final String PARAM_F = "f";
  private static final String PARAM_G = "g";

  private static final String RESSOURCE_CODE = "code";

  private static final String[] paramNames = { PARAM_A, PARAM_B, PARAM_C, PARAM_D, PARAM_E, PARAM_F, PARAM_G };
  private static final String[] ressourceNames = { RESSOURCE_CODE };

  private double a = 0.0;
  private double b = 0.0;
  private double c = 0.0;
  private double d = 0.0;
  private double e = 0.0;
  private double f = 0.0;
  private double g = 0.0;

  private String code = "import org.jwildfire.create.tina.base.XForm;\r\n" +
      "import org.jwildfire.create.tina.base.XYZPoint;\r\n" +
      "import org.jwildfire.create.tina.variation.XFormTransformationContext;\r\n" +
      "import org.jwildfire.create.tina.variation.FlameTransformationContext;\r\n" +
      "\r\n" +
      "\r\n" +
      "  public void init(FlameTransformationContext pContext, XForm pXForm) {\r\n" +
      "\r\n" +
      "  }\r\n" +
      "\r\n" +
      "  public void transform(XFormTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {\r\n" +
      "    // defaults to linear3D transformation\r\n" +
      "    pVarTP.x += pAmount * pAffineTP.x;\r\n" +
      "    pVarTP.y += pAmount * pAffineTP.y;\r\n" +
      "    pVarTP.z += pAmount * pAffineTP.z;\r\n" +
      "  }\r\n" +
      "";
  private CustomWFFuncRunner customFuncRunner = null;

  @Override
  public void transform(XFormTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    customFuncRunner.transform(pContext, pXForm, pAffineTP, pVarTP, pAmount);
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { a, b, c, d, e, f, g };
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
    return new byte[][] { (code != null ? code.getBytes() : null) };
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_CODE.equalsIgnoreCase(pName)) {
      code = pValue != null ? new String(pValue) : "";
      // For validation
      compile();
    }
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "custom_wf";
  }

  @Override
  public void init(FlameTransformationContext pContext, XForm pXForm) {
    if (customFuncRunner == null) {
      compile();
    }
    customFuncRunner.setA(a);
    customFuncRunner.setB(b);
    customFuncRunner.setC(c);
    customFuncRunner.setD(d);
    customFuncRunner.setE(e);
    customFuncRunner.setF(f);
    customFuncRunner.setG(g);
    customFuncRunner.init(pContext, pXForm);
  }

  private void compile() {
    try {
      customFuncRunner = CustomWFFuncRunner.compile(code);
    }
    catch (Throwable ex) {
      throw new RuntimeException(ex);
    }
  }

}
