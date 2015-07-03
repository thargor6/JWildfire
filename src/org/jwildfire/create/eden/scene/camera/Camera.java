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
package org.jwildfire.create.eden.scene.camera;

import org.jwildfire.create.eden.base.Point3f;
import org.jwildfire.create.eden.scene.PositionableSceneElement;

public class Camera extends PositionableSceneElement {
  private Point3f eye = new Point3f(0.0, 0.0, -100.0);
  private Point3f target = new Point3f(0.0, 0.0, 0.0);
  private Point3f up = new Point3f(0.0, 1.0, 0.0);
  private float fov = 32.0f;
  private float aspect = 1.3333333333333333f;

  public Camera(PositionableSceneElement pParent) {
    super(pParent);
  }

  public Point3f getUp() {
    return up;
  }

  public Point3f getEye() {
    return eye;
  }

  public Point3f getTarget() {
    return target;
  }

  public float getFov() {
    return fov;
  }

  public void setFov(double pFov) {
    fov = (float) pFov;
  }

  public float getAspect() {
    return aspect;
  }

  public void setAspect(double pAspect) {
    aspect = (float) pAspect;
  }

  public void setUp(double pX, double pY, double pZ) {
    up.setX(pX);
    up.setY(pY);
    up.setZ(pZ);
  }

  public void setEye(double pX, double pY, double pZ) {
    eye.setX(pX);
    eye.setY(pY);
    eye.setZ(pZ);
  }

  public void setTarget(double pX, double pY, double pZ) {
    target.setX(pX);
    target.setY(pY);
    target.setZ(pZ);
  }

}
