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
package org.jwildfire.base.mathparser;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

public class Sawtooth extends PostfixMathCommand {

  public Sawtooth() {
    numberOfParameters = 1;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void run(@SuppressWarnings("rawtypes") Stack inStack) throws ParseException {
    // check the stack
    checkStack(inStack);
    // get the parameter from the stack
    double x = valueToDouble(inStack.pop()) / Math.PI;

    // calc
    double r = x - (int) (x);
    if (x < 0.0)
      r += 1.0;
    inStack.push(new Double(r));
  }

  private double valueToDouble(Object pValue) {
    if (pValue != null) {
      if (pValue instanceof Integer)
        return (Integer) pValue;
      else if (pValue instanceof Double)
        return (Double) pValue;
    }
    return 0.0;
  }
}
