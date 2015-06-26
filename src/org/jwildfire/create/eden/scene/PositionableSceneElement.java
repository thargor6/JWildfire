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
package org.jwildfire.create.eden.scene;

import org.jwildfire.create.eden.base.Angle3f;
import org.jwildfire.create.eden.base.Point3f;
import org.jwildfire.create.eden.base.Size3f;

public class PositionableSceneElement extends SceneElement implements Cloneable {
  private final PositionableSceneElement parent;
  private Point3f position = Point3f.OPTIONAL;
  private Angle3f orientation = Angle3f.OPTIONAL;
  private Size3f size = Size3f.OPTIONAL;

  public PositionableSceneElement(PositionableSceneElement pParent) {
    parent = pParent;
  }

  public Point3f getPosition() {
    return position;
  }

  public void setPosition(Point3f pPosition) {
    position = pPosition;
  }

  public void setPosition(double pX, double pY, double pZ) {
    position = new Point3f(pX, pY, pZ);
  }

  public Angle3f getOrientation() {
    return orientation;
  }

  public void setOrientation(Angle3f pOrientation) {
    orientation = pOrientation;
  }

  public void setOrientation(double pAlpha, double pBeta, double pGamma) {
    orientation = new Angle3f(pAlpha, pBeta, pGamma);
  }

  public Size3f getSize() {
    return size;
  }

  public void setSize(Size3f pSize) {
    size = pSize;
  }

  public void setSize(double pX, double pY, double pZ) {
    size = new Size3f(pX, pY, pZ);
  }

  public PositionableSceneElement getParent() {
    return parent;
  }

  public PositionableSceneElement clone() throws CloneNotSupportedException {
    PositionableSceneElement copy;
    copy = (PositionableSceneElement) super.clone();
    copy.position = new Point3f(position.getX(), position.getY(), position.getZ());
    copy.orientation = new Angle3f(orientation.getAlpha(), orientation.getBeta(), orientation.getGamma());
    copy.size = new Size3f(size.getX(), size.getY(), size.getZ());
    return copy;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((orientation == null) ? 0 : orientation.hashCode());
    result = prime * result + ((parent == null) ? 0 : parent.hashCode());
    result = prime * result + ((position == null) ? 0 : position.hashCode());
    result = prime * result + ((size == null) ? 0 : size.hashCode());
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
    PositionableSceneElement other = (PositionableSceneElement) obj;
    if (orientation == null) {
      if (other.orientation != null)
        return false;
    }
    else if (!orientation.equals(other.orientation))
      return false;
    if (parent == null) {
      if (other.parent != null)
        return false;
    }
    else if (!parent.equals(other.parent))
      return false;
    if (position == null) {
      if (other.position != null)
        return false;
    }
    else if (!position.equals(other.position))
      return false;
    if (size == null) {
      if (other.size != null)
        return false;
    }
    else if (!size.equals(other.size))
      return false;
    return true;
  }
}
