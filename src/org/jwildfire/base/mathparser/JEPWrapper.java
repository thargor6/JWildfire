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

import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;

public class JEPWrapper {
  private final JEP parser;

  public JEPWrapper() {
    parser = new JEP();
    parser.addStandardConstants();
    parser.addStandardFunctions();
    parser.addFunction("rect", new Rect());
    parser.addFunction("sawtooth", new Sawtooth());
    parser.addFunction("triangle", new Triangle());
  }

  public void addVariable(String pName, double pValue) {
    parser.addVariable(pName, pValue);
  }

  public void setVarValue(String pName, double pValue) {
    parser.setVarValue(pName, pValue);
  }

  public Node parse(String pExpression) {
    try {
      return parser.parse(pExpression);
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public double evaluate(Node pNode) {
    try {
      return (Double) parser.evaluate(pNode);
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

}
