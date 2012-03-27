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

import org.jwildfire.create.tina.random.RandomNumberGenerator;
import org.jwildfire.create.tina.random.RandomNumberGenerator.RandGenStatus;
import org.jwildfire.create.tina.render.FlameRenderer;

public class DefaultFlameTransformationContextImpl implements FlameTransformationContext {
  private final RandomNumberGenerator randGen;
  private final FlameRenderer flameRenderer;

  public DefaultFlameTransformationContextImpl(FlameRenderer pFlameRenderer) {
    randGen = pFlameRenderer.getRandomNumberGenerator();
    flameRenderer = pFlameRenderer;
  }

  @Override
  public double random() {
    return randGen.random();
  }

  @Override
  public int random(int pMax) {
    return randGen.random(pMax);
  }

  @Override
  public void setRandGenStatus(RandGenStatus pRandGenStatus) {
    randGen.setStatus(pRandGenStatus);
  }

  @Override
  public double sin(double a) {
    return sin(a);
  }

  @Override
  public double cos(double a) {
    return cos(a);
  }

  @Override
  public double tan(double a) {
    return tan(a);
  }

  @Override
  public FlameRenderer getFlameRenderer() {
    return flameRenderer;
  }

  @Override
  public double exp(double a) {
    return Math.exp(a);
  }

  @Override
  public double sqrt(double a) {
    return sqrt(a);
  }

  @Override
  public double fmod(double a, double b) {
    return a % b;
  }

  @Override
  public double sqr(double a) {
    return a * a;
  }

}
