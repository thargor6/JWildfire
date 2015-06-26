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

public class Angle3f {
  private float alpha, beta, gamma;

  public static final Angle3f OPTIONAL = new Angle3f(0.0f, 0.0f, 0.0f);

  public Angle3f() {

  }

  public Angle3f(float pAlpha, float pBeta, float pGamma) {
    alpha = pAlpha;
    beta = pBeta;
    gamma = pGamma;
  }

  public Angle3f(double pAlpha, double pBeta, double pGamma) {
    alpha = (float) pAlpha;
    beta = (float) pBeta;
    gamma = (float) pGamma;
  }

  public float getAlpha() {
    return alpha;
  }

  public void setAlpha(float pAlpha) {
    alpha = pAlpha;
  }

  public float getBeta() {
    return beta;
  }

  public void setBeta(float pBeta) {
    beta = pBeta;
  }

  public float getGamma() {
    return gamma;
  }

  public void setGamma(float pGamma) {
    gamma = pGamma;
  }

  public void assign(Angle3f pSource) {
    alpha = pSource.getAlpha();
    beta = pSource.getBeta();
    gamma = pSource.getGamma();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Float.floatToIntBits(alpha);
    result = prime * result + Float.floatToIntBits(beta);
    result = prime * result + Float.floatToIntBits(gamma);
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
    Angle3f other = (Angle3f) obj;
    if (Float.floatToIntBits(alpha) != Float.floatToIntBits(other.alpha))
      return false;
    if (Float.floatToIntBits(beta) != Float.floatToIntBits(other.beta))
      return false;
    if (Float.floatToIntBits(gamma) != Float.floatToIntBits(other.gamma))
      return false;
    return true;
  }

  public void setGamma(double pGamma) {
    gamma = (float) pGamma;
  }

  public void setBeta(double pBeta) {
    beta = (float) pBeta;
  }

  public void setAlpha(double pAlpha) {
    alpha = (float) pAlpha;
  }
}
