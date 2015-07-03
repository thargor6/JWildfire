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
package org.jwildfire.create.eden.base;

public class Face3i {
  private int a, b, c;

  public Face3i() {

  }

  public Face3i(Face3i pSrc) {
    a = pSrc.a;
    b = pSrc.b;
    c = pSrc.c;
  }

  public Face3i(int pA, int pB, int pC) {
    setPoints(pA, pB, pC);
  }

  public void setPoints(int pA, int pB, int pC) {
    a = pA;
    b = pB;
    c = pC;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + a;
    result = prime * result + b;
    result = prime * result + c;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Face3i other = (Face3i) obj;
    if (a != other.a)
      return false;
    if (b != other.b)
      return false;
    if (c != other.c)
      return false;
    return true;
  }

  public int getA() {
    return a;
  }

  public void setA(int pA) {
    a = pA;
  }

  public int getB() {
    return b;
  }

  public void setB(int pB) {
    b = pB;
  }

  public int getC() {
    return c;
  }

  public void setC(int pC) {
    c = pC;
  }

}
