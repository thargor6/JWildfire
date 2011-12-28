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

import org.jwildfire.create.tina.base.RasterPoint;
import org.jwildfire.create.tina.random.RandomNumberGenerator.RandGenStatus;
import org.jwildfire.create.tina.render.FlameRenderer;

public interface FlameTransformationContext {
  public abstract double random();

  public abstract int random(int pMax);

  public abstract RasterPoint getPass1RasterPoint(double pX, double pY);

  public abstract void setRandGenStatus(RandGenStatus pRandGenStatus);

  public abstract double sin(double a);

  public abstract double cos(double a);

  public abstract double tan(double a);

  public abstract double exp(double a);

  abstract FlameRenderer getFlameRenderer();

}
