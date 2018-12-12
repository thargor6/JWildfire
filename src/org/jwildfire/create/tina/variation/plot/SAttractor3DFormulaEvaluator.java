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
package org.jwildfire.create.tina.variation.plot;

import org.codehaus.janino.ClassBodyEvaluator;
import org.codehaus.janino.Scanner;

import java.io.StringReader;


public class SAttractor3DFormulaEvaluator {

  public static SAttractor3DFormulaEvaluator compile(String pScript) throws Exception {
    return (SAttractor3DFormulaEvaluator) ClassBodyEvaluator.createFastClassBodyEvaluator(new Scanner(null, new StringReader(pScript)), SAttractor3DFormulaEvaluator.class, (ClassLoader) null);
  }

  public double evaluateX(double x, double y, double z, double delta_t) {
    return x + x * delta_t;
  }

  public double evaluateY(double x, double y, double z, double delta_t) {
    return y + y * delta_t;
  }

  public double evaluateZ(double x, double y, double z, double delta_t) {
    return z + z * delta_t;
  }
}
