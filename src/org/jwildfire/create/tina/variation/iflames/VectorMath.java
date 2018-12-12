/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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
package org.jwildfire.create.tina.variation.iflames;

import org.jwildfire.base.mathlib.MathLib;

public class VectorMath {
  public static Vector multiply(float pScale, Vector pVector) {
    return new Vector(pScale * pVector.getX(), pScale * pVector.getY(), pScale * pVector.getZ());
  }

  public static Vector inverseMultiply(float pScale, Vector pVector) {
    return new Vector(pVector.getX() / pScale, pVector.getY() / pScale, pVector.getZ() / pScale);
  }

  public static float len(Vector pVector) {
    return (float) MathLib.sqrt(pVector.getX() * pVector.getX() + pVector.getY() * pVector.getY() + pVector.getZ() * pVector.getZ());
  }

  public static Vector normalize(Vector pVector) {
    float r = len(pVector);
    if (r > MathLib.EPSILON) {
      return inverseMultiply(r, pVector);
    } else {
      return pVector;
    }
  }

  public static Vector cross(Vector pA, Vector pB) {
    return new Vector(pA.getY() * pB.getZ() - pA.getZ() * pB.getY(), pA.getZ() * pB.getX() - pA.getX() * pB.getZ(), pA.getX() * pB.getY() - pA.getY() * pB.getX());
  }

  public static Vector normal(Vector pA, Vector pCentre) {
    return new Vector(pA.getX() - pCentre.getX(), pA.getY() - pCentre.getY(), pA.getZ() - pCentre.getZ());
  }

  public static Vector add(Vector pA, Vector pB) {
    return new Vector(pA.getX() + pB.getX(), pA.getY() + pB.getY(), pA.getZ() + pB.getZ());
  }

  public static Vector subtract(Vector pA, Vector pB) {
    return new Vector(pA.getX() - pB.getX(), pA.getY() - pB.getY(), pA.getZ() - pB.getZ());
  }

  public static Vector tangent(Vector pA, Vector pCentre) {
    Vector r = normal(pA, pCentre);

    Vector c1 = cross(r, new Vector(0.0f, 0.0f, 1.0f));
    Vector c2 = cross(r, new Vector(0.0f, 1.0f, 0.0f));

    if (len(c1) > len(c2)) {
      return c1;
    } else {
      return c2;
    }
  }
}
