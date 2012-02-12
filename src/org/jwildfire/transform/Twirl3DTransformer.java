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
package org.jwildfire.transform;

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyCategory;
import org.jwildfire.base.PropertyMax;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.image.WFImage;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class Twirl3DTransformer extends Mesh3DTransformer {

  public enum Axis {
    X, Y, Z
  };

  @Property(description = "Twirl axis", editorClass = AxisEditor.class)
  private Axis axis = Axis.Y;

  @Property(description = "X-coordinate of the effect origin")
  private double originX = 400.0;

  @Property(description = "Y-coordinate of the effect origin")
  private double originY = 400.0;

  @Property(description = "Z-coordinate of the effect origin")
  private double originZ = 0.0;

  @Property(description = "Twirl radius")
  @PropertyMin(0.0)
  private double radius = 300.0;

  @Property(description = "Twirl angle")
  @PropertyMin(-360.0)
  @PropertyMax(360.0)
  private double amount = 190.0;

  @Property(category = PropertyCategory.SECONDARY, description = "Twirl power")
  @PropertyMin(0.001)
  @PropertyMax(10.0)
  private double power = 3.0;

  @Override
  protected void transformMesh(Mesh3D pMesh3D, int pImageWidth, int pImageHeight) {
    int pCount = pMesh3D.getPCount();
    int width = pImageWidth;
    int height = pImageHeight;
    double x[] = pMesh3D.getX();
    double y[] = pMesh3D.getY();
    double z[] = pMesh3D.getZ();
    double originX = this.originX - (double) width / 2;
    double originY = this.originY - (double) height / 2;
    double originZ = 0.0 - this.originZ;
    double power = this.power;
    if (power < 1.0)
      power = 1.0;
    else if (power > 10.0)
      power = 10.0;
    double angle0 = (double) this.amount * Math.PI / 180.0;
    double radius = this.radius;
    double da = angle0 / Math.pow(radius, power);

    if (this.axis == Axis.Z) {
      for (int i = 0; i < pCount; i++) {
        double dx = x[i] - originX;
        double dy = y[i] - originY;
        double dr = Math.sqrt(dx * dx + dy * dy);
        if (dr <= radius) {
          dr = radius - dr;
          if (dr < 0.00001)
            dr = 0.00001;
          double angle = Math.pow(dr, power) * da;
          double ca = Math.cos(angle);
          double sa = Math.sin(angle);
          x[i] = dx * ca + dy * sa + originX;
          y[i] = dy * ca - dx * sa + originY;
        }
      }
    }
    else if (this.axis == Axis.X) {
      for (int i = 0; i < pCount; i++) {
        double dz = z[i] - originZ;
        double dy = y[i] - originY;
        double dr = Math.sqrt(dz * dz + dy * dy);
        if (dr <= radius) {
          dr = radius - dr;
          if (dr < 0.00001)
            dr = 0.00001;
          double angle = Math.pow(dr, power) * da;
          double ca = Math.cos(angle);
          double sa = Math.sin(angle);
          z[i] = dz * ca + dy * sa + originZ;
          y[i] = dy * ca - dz * sa + originY;
        }
      }
    }
    else if (this.axis == Axis.Y) {
      for (int i = 0; i < pCount; i++) {
        double dx = x[i] - originX;
        double dz = z[i] - originZ;
        double dr = Math.sqrt(dx * dx + dz * dz);
        if (dr <= radius) {
          dr = radius - dr;
          if (dr < 0.00001)
            dr = 0.00001;
          double angle = Math.pow(dr, power) * da;
          double ca = Math.cos(angle);
          double sa = Math.sin(angle);
          x[i] = dx * ca + dz * sa + originX;
          z[i] = dz * ca - dx * sa + originZ;
        }
      }
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    super.initDefaultParams(pImg);
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    axis = Axis.Z;
    originX = (double) width / 2.0;
    originY = (double) height / 2.0;
    originZ = 0.0;
    radius = (double) height / 2.2;
    amount = 190.0;
    power = 3.0;
  }

  public Axis getAxis() {
    return axis;
  }

  public void setAxis(Axis axis) {
    this.axis = axis;
  }

  public double getOriginX() {
    return originX;
  }

  public void setOriginX(double originX) {
    this.originX = originX;
  }

  public double getOriginY() {
    return originY;
  }

  public void setOriginY(double originY) {
    this.originY = originY;
  }

  public double getOriginZ() {
    return originZ;
  }

  public void setOriginZ(double originZ) {
    this.originZ = originZ;
  }

  public double getRadius() {
    return radius;
  }

  public void setRadius(double radius) {
    this.radius = radius;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public double getPower() {
    return power;
  }

  public void setPower(double power) {
    this.power = power;
  }

  public static class AxisEditor extends ComboBoxPropertyEditor {
    public AxisEditor() {
      super();
      setAvailableValues(new Axis[] { Axis.X, Axis.Y, Axis.Z });
    }
  }

}
