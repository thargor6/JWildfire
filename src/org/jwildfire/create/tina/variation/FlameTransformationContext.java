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

import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.render.FlameRenderer;

public class FlameTransformationContext {
  private final AbstractRandomGenerator randGen;
  private final FlameRenderer flameRenderer;
  private boolean preserveZCoordinate;
  private boolean preview;

  public FlameTransformationContext(FlameRenderer pFlameRenderer, AbstractRandomGenerator pRandGen) {
    randGen = pRandGen;
    flameRenderer = pFlameRenderer;
  }

  public double random() {
    return randGen.random();
  }

  public int random(int pMax) {
    return randGen.random(pMax);
  }

  public FlameRenderer getFlameRenderer() {
    return flameRenderer;
  }

  public boolean isPreserveZCoordinate() {
    return preserveZCoordinate;
  }

  public void setPreserveZCoordinate(boolean pPreserveZCoordinate) {
    preserveZCoordinate = pPreserveZCoordinate;
  }

  public AbstractRandomGenerator getRandGen() {
    return randGen;
  }

  public boolean isPreview() {
    return preview;
  }

  public void setPreview(boolean pPreview) {
    preview = pPreview;
  }

}
