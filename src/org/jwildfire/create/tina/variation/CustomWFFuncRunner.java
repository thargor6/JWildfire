/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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

import org.codehaus.janino.ClassBodyEvaluator;
import org.codehaus.janino.Scanner;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import java.io.StringReader;

public class CustomWFFuncRunner {
  public static CustomWFFuncRunner compile(String pScript) throws Exception {
    CustomWFFuncRunner res = (CustomWFFuncRunner) ClassBodyEvaluator.createFastClassBodyEvaluator(new Scanner(null, new StringReader(pScript)), CustomWFFuncRunner.class, (ClassLoader) null);
    return res;
  }

  protected double a = 0.0;
  protected double b = 0.0;
  protected double c = 0.0;
  protected double d = 0.0;
  protected double e = 0.0;
  protected double f = 0.0;
  protected double g = 0.0;

  public void init(FlameTransformationContext pContext, XForm pXForm) {

  }

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // defaults to linear3D transformation
    pVarTP.x += pAmount * pAffineTP.x;
    pVarTP.y += pAmount * pAffineTP.y;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  public void setA(double a) {
    this.a = a;
  }

  public void setC(double c) {
    this.c = c;
  }

  public void setD(double d) {
    this.d = d;
  }

  public void setE(double e) {
    this.e = e;
  }

  public void setF(double f) {
    this.f = f;
  }

  public void setG(double g) {
    this.g = g;
  }

  public void setB(double b) {
    this.b = b;
  }
}
