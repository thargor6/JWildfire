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
package org.jwildfire.create.eden.scene.light;

import org.jwildfire.create.eden.base.Point3f;
import org.jwildfire.create.eden.scene.PositionableSceneElement;

public class SkyLight extends PositionableSceneElement {
  private Point3f up = new Point3f(0.0, 0.0, 1.0);
  private Point3f east = new Point3f(0.0, 1.0, 0.0);
  private Point3f sundir = new Point3f(1.0, -1.0, 0.31);
  private float turbidity = 1.8f;
  private int samples = 16;

  public SkyLight(PositionableSceneElement pParent) {
    super(pParent);
  }

  public Point3f getUp() {
    return up;
  }

  public Point3f getEast() {
    return east;
  }

  public Point3f getSundir() {
    return sundir;
  }

  public float getTurbidity() {
    return turbidity;
  }

  public void setTurbidity(double pTurbidity) {
    turbidity = (float) pTurbidity;
  }

  public int getSamples() {
    return samples;
  }

  public void setSamples(int pSamples) {
    samples = pSamples;
  }

  public void setUp(double pX, double pY, double pZ) {
    up.setX(pX);
    up.setY(pY);
    up.setZ(pZ);
  }

  public void setEast(double pX, double pY, double pZ) {
    east.setX(pX);
    east.setY(pY);
    east.setZ(pZ);
  }

  public void setSundir(double pX, double pY, double pZ) {
    sundir.setX(pX);
    sundir.setY(pY);
    sundir.setZ(pZ);
  }

}
