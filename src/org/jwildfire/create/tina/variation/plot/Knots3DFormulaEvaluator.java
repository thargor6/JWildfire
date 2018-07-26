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

import java.io.StringReader;

import org.codehaus.janino.ClassBodyEvaluator;
import org.codehaus.janino.Scanner;


public class Knots3DFormulaEvaluator
{

	public static Knots3DFormulaEvaluator compile(String pScript) throws Exception {
		  return (Knots3DFormulaEvaluator) ClassBodyEvaluator.createFastClassBodyEvaluator(new Scanner(null, new StringReader(pScript)), Knots3DFormulaEvaluator.class, (ClassLoader) null);
		}
	
	  public double evaluateX( int t) {
		    return t;
		  }

		  public double evaluateY( int t) {
		    return t;
		  }

		  public double evaluateZ( int t) {
		    return t;
		  }
}
