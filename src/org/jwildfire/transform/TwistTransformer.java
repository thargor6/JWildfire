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

import java.awt.Color;

import org.jwildfire.base.Property;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.image.WFImage;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class TwistTransformer extends Mesh3DTransformer {
  public enum Axis {
    X, Y, Z
  }

  @Property(description = "X-coordinate of the effect origin")
  private double originX = 400.0;

  @Property(description = "Y-coordinate of the effect origin")
  private double originY = 400.0;

  @Property(description = "Z-coordinate of the effect origin")
  private double originZ = 0.0;

  @Property(description = "Twist amount")
  private double amount = 0.0;

  @Property(description = "Twist distance")
  private double dist = 0.0;

  @Property(description = "Twist axis", editorClass = AxisEditor.class)
  private Axis axis = Axis.X;

  @Override
  protected void transformMesh(Mesh3D pMesh3D, int pImageWidth, int pImageHeight) {
    int width = pImageWidth;
    int height = pImageHeight;
    int pCount = pMesh3D.getPCount();
    double x[] = pMesh3D.getX();
    double y[] = pMesh3D.getY();
    double z[] = pMesh3D.getZ();

    if ((Math.abs(amount) <= MathLib.EPSILON) || (Math.abs(dist) <= MathLib.EPSILON))
      return;
    double originX = this.originX - (double) width / 2.0;
    double originY = this.originY - (double) height / 2.0;
    double originZ = 0.0 - this.originZ;
    double dist = this.dist;
    double angle0 = this.amount * Math.PI / 180.0;
    double sa0 = Math.sin(angle0);
    double ca0 = Math.cos(angle0);

    if (this.axis == Axis.X) {
      if (dist > 0.0) {
        double ascale = angle0 / dist;
        for (int i = 0; i < pCount; i++) {
          double dx = x[i] - originX;
          if (dx >= 0.0) {
            double dy = y[i] - originY;
            double dz = z[i] - originZ;
            double sa, ca;
            if (dx <= dist) {
              double angle = dx * ascale;
              sa = Math.sin(angle);
              ca = Math.cos(angle);
            }
            else {
              sa = sa0;
              ca = ca0;
            }
            y[i] = originY + dy * ca + dz * sa;
            z[i] = originZ + dz * ca - dy * sa;
          }
        }
      }
      else {
        dist = 0.0 - dist;
        double ascale = angle0 / dist;
        for (int i = 0; i < pCount; i++) {
          double dx = originX - x[i];
          if (dx >= 0.0) {
            double dy = y[i] - originY;
            double dz = z[i] - originZ;
            double sa, ca;
            if (dx <= dist) {
              double angle = dx * ascale;
              sa = Math.sin(angle);
              ca = Math.cos(angle);
            }
            else {
              sa = sa0;
              ca = ca0;
            }
            y[i] = originY + dy * ca + dz * sa;
            z[i] = originZ + dz * ca - dy * sa;
          }
        }
      }
    }
    else if (this.axis == Axis.Y) {
      if (dist > 0.0) {
        double ascale = angle0 / dist;
        for (int i = 0; i < pCount; i++) {
          double dy = y[i] - originY;
          if (dy >= 0.0) {
            double dx = x[i] - originX;
            double dz = z[i] - originZ;
            double sa, ca;
            if (dy <= dist) {
              double angle = dy * ascale;
              sa = Math.sin(angle);
              ca = Math.cos(angle);
            }
            else {
              sa = sa0;
              ca = ca0;
            }
            x[i] = originX + dx * ca + dz * sa;
            z[i] = originZ + dz * ca - dx * sa;
          }
        }
      }
      else {
        dist = 0.0 - dist;
        double ascale = angle0 / dist;
        for (int i = 0; i < pCount; i++) {
          double dy = originY - y[i];
          if (dy >= 0.0) {
            double dx = x[i] - originX;
            double dz = z[i] - originZ;
            double sa, ca;
            if (dy <= dist) {
              double angle = dy * ascale;
              sa = Math.sin(angle);
              ca = Math.cos(angle);
            }
            else {
              sa = sa0;
              ca = ca0;
            }
            x[i] = originX + dx * ca + dz * sa;
            z[i] = originZ + dz * ca - dx * sa;
          }
        }
      }
    }
    else if (this.axis == Axis.Z) {
      if (dist > 0.0) {
        double ascale = angle0 / dist;
        for (int i = 0; i < pCount; i++) {
          double dz = z[i] - originZ;
          if (dz >= 0.0) {
            double dx = x[i] - originX;
            double dy = y[i] - originY;
            double sa, ca;
            if (dz <= dist) {
              double angle = dz * ascale;
              sa = Math.sin(angle);
              ca = Math.cos(angle);
            }
            else {
              sa = sa0;
              ca = ca0;
            }
            x[i] = originX + dx * ca + dy * sa;
            y[i] = originY + dy * ca - dx * sa;
          }
        }
      }
      else {
        dist = 0.0 - dist;
        double ascale = angle0 / dist;
        for (int i = 0; i < pCount; i++) {
          double dz = originZ - z[i];
          if (dz >= 0.0) {
            double dx = x[i] - originX;
            double dy = y[i] - originY;
            double sa, ca;
            if (dz <= dist) {
              double angle = dz * ascale;
              sa = Math.sin(angle);
              ca = Math.cos(angle);
            }
            else {
              sa = sa0;
              ca = ca0;
            }
            x[i] = originX + dx * ca + dy * sa;
            y[i] = originY + dy * ca - dx * sa;
          }
        }
      }
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    super.initDefaultParams(pImg);
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    double rr = Math.sqrt(width * width + height * height);
    axis = Axis.X;
    originX = Math.round((double) width / 4.0);
    originY = Math.round((double) height / 2.0);
    originZ = 0.0;
    dist = Math.round(rr - originX);
    amount = Math.round(rr / 2.0);
    setFaces(Faces.DOUBLE);
    setZoom(0.8);
    setLight2Color(new Color(255, 255, 220));
  }

  public static class AxisEditor extends ComboBoxPropertyEditor {
    public AxisEditor() {
      super();
      setAvailableValues(new Axis[] { Axis.X, Axis.Y, Axis.Z });
    }
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

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public double getDist() {
    return dist;
  }

  public void setDist(double dist) {
    this.dist = dist;
  }

  public Axis getAxis() {
    return axis;
  }

  public void setAxis(Axis axis) {
    this.axis = axis;
  }

}
