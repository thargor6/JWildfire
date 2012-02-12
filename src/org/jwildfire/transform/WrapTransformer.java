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
import org.jwildfire.base.PropertyMax;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.image.WFImage;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class WrapTransformer extends Mesh3DTransformer {
  public enum Axis {
    X, Y, XY
  }

  public enum Method {
    AMOUNT, RADIUS
  }

  @Property(description = "X-coordinate of the effect origin")
  private double originX;

  @Property(description = "Y-coordinate of the effect origin")
  private double originY;

  @Property(description = "Wrap amount (method=AMOUNT)")
  @PropertyMin(0.0)
  @PropertyMax(100.0)
  private double amount;

  @Property(description = "Wrap radius (method=RADIUS)")
  @PropertyMin(0.0)
  private double radius;

  @Property(description = "Wrap axis", editorClass = AxisEditor.class)
  private Axis axis;

  @Property(description = "Wrap method", editorClass = MethodEditor.class)
  private Method method;

  public static class AxisEditor extends ComboBoxPropertyEditor {
    public AxisEditor() {
      super();
      setAvailableValues(new Axis[] { Axis.X, Axis.Y, Axis.XY });
    }
  }

  public static class MethodEditor extends ComboBoxPropertyEditor {
    public MethodEditor() {
      super();
      setAvailableValues(new Method[] { Method.AMOUNT, Method.RADIUS });
    }
  }

  @Override
  protected void transformMesh(Mesh3D pMesh3D, int pImageWidth, int pImageHeight) {
    int width = pImageWidth;
    int height = pImageHeight;
    int pCount = pMesh3D.getPCount();
    double x[] = pMesh3D.getX();
    double y[] = pMesh3D.getY();
    double z[] = pMesh3D.getZ();

    if (this.method == Method.AMOUNT) {
      double log100 = Math.log(100.0);
      if (this.amount >= 0.001) {
        if (this.axis == Axis.X) {
          double twoPi = 2.0 * Math.PI;
          double amount = this.amount;
          double logAmount = Math.log(amount);
          double centreY = this.originY - (double) height / 2;
          double radiusi = (double) height / twoPi;
          double radius0 = 50.0 * radiusi;
          double radius = radius0 + (radiusi - radius0) / log100 * logAmount;

          for (int i = 0; i < pCount; i++) {
            double dr = y[i] - centreY;
            double angle = dr / radius;
            double sa = Math.sin(angle);
            double ca = Math.cos(angle);
            double zz = z[i];
            y[i] = sa * (radius - zz) + centreY;
            z[i] = radius - ca * (radius - zz);
          }
        }
        else if (this.axis == Axis.Y) {
          double twoPi = 2.0 * Math.PI;
          double amount = this.amount;
          double logAmount = Math.log(amount);
          double centreX = this.originX - (double) width / 2;
          double radiusi = (double) width / twoPi;
          double radius0 = 50.0 * radiusi;
          double radius = radius0 + (radiusi - radius0) / log100 * logAmount;

          for (int i = 0; i < pCount; i++) {
            double dr = x[i] - centreX;
            double angle = dr / radius;
            double sa = Math.sin(angle);
            double ca = Math.cos(angle);
            double zz = z[i];
            x[i] = sa * (radius - zz) + centreX;
            z[i] = radius - ca * (radius - zz);
          }
        }
        else {
          double twoPi = 2.0 * Math.PI;
          double mPI2 = 0.0 - Math.PI / 2.0;
          double pPI2 = Math.PI / 2.0;
          double amount = this.amount;
          double logAmount = Math.log(amount);
          double dx = (double) width;
          double dy = (double) height;
          double centreX = this.originX - (double) width / 2;
          double centreY = this.originY - (double) height / 2;
          double radiusi = (double) Math.sqrt(dx * dx + dy * dy) / twoPi;
          double radius0 = 50.0 * radiusi;
          double radius = radius0 + (radiusi - radius0) / log100 * logAmount;

          for (int i = 0; i < pCount; i++) {
            dx = x[i] - centreX;
            dy = y[i] - centreY;
            double zz = z[i];
            double dr = Math.sqrt(dx * dx + dy * dy);
            /* inverse rotation */
            double angle2;
            if (dx != 0.0) {
              angle2 = Math.atan(dy / dx);
            }
            else {
              if (dy < 0.0)
                angle2 = mPI2;
              else
                angle2 = pPI2;
            }
            /* wrapping */
            double angle = dr / radius;
            double sa = Math.sin(angle);
            double ca = Math.cos(angle);
            double rx;
            if (dx < 0)
              rx = 0.0 - (radius - zz) * sa;
            else
              rx = (radius - zz) * sa;
            z[i] = radius - (radius - zz) * ca;
            /* rotation */
            x[i] = rx * Math.cos(angle2) + centreX;
            y[i] = rx * Math.sin(angle2) + centreY;
          }
        }
      }
    }
    else if (this.method == Method.RADIUS) {
      if (this.radius >= 0.001) {
        if (this.axis == Axis.X) {
          double centreY = this.originY - (double) height / 2;
          double radius = this.radius;
          if (radius < 1.0)
            radius = 1.0;
          double ds = radius * Math.PI / 2.0;
          double dsrad = radius - ds;

          for (int i = 0; i < pCount; i++) {
            double dr = y[i] - centreY;
            if (Math.abs(dr) <= ds) {
              double angle = dr / radius;
              double sa = Math.sin(angle);
              double ca = Math.cos(angle);
              double zz = z[i];
              y[i] = sa * (radius - zz) + centreY;
              z[i] = 0.0 - (ca * radius - zz);
            }
            else {
              if (dr >= 0.0) {
                y[i] = dr + dsrad + centreY;
              }
              else {
                y[i] = dr - dsrad + centreY;
              }
            }
          }
        }
        else if (this.axis == Axis.Y) {
          double centreX = this.originX - (double) width / 2;
          double radius = this.radius;
          if (radius < 1.0)
            radius = 1.0;
          double ds = radius * Math.PI / 2.0;
          double dsrad = radius - ds;

          for (int i = 0; i < pCount; i++) {
            double dr = x[i] - centreX;
            if (Math.abs(dr) <= ds) {
              double angle = dr / radius;
              double sa = Math.sin(angle);
              double ca = Math.cos(angle);
              double zz = z[i];
              x[i] = sa * (radius - zz) + centreX;
              z[i] = 0.0 - ca * (radius - zz);
            }
            else {
              if (dr >= 0.0) {
                x[i] = dr + dsrad + centreX;
              }
              else {
                x[i] = dr - dsrad + centreX;
              }
            }
          }
        }
        else {
          double mPI2 = 0.0 - Math.PI / 2.0;
          double pPI2 = Math.PI / 2.0;
          double dx = (double) width;
          double dy = (double) height;
          double centreX = this.originX - (double) width / 2;
          double centreY = this.originY - (double) height / 2;
          double radius = this.radius;
          if (radius < 1.0)
            radius = 1.0;
          double ds = radius * Math.PI / 2.0;
          double dsrad = radius - ds;

          for (int i = 0; i < pCount; i++) {
            dx = x[i] - centreX;
            dy = y[i] - centreY;
            double zz = z[i];
            double dr = Math.sqrt(dx * dx + dy * dy);
            /* inverse rotation */
            double angle2;
            if (dx != 0.0) {
              angle2 = Math.atan(dy / dx);
            }
            else {
              if (dy < 0.0)
                angle2 = mPI2;
              else
                angle2 = pPI2;
            }
            if (dr <= ds) {
              /* wrapping */
              double angle = dr / radius;
              double sa = Math.sin(angle);
              double ca = Math.cos(angle);
              double rx;
              if (dx < 0)
                rx = 0.0 - (radius - zz) * sa;
              else
                rx = (radius - zz) * sa;
              z[i] = 0.0 - (radius - zz) * ca;
              /* rotation */
              x[i] = rx * Math.cos(angle2) + centreX;
              y[i] = rx * Math.sin(angle2) + centreY;
            }
            else {
              if (dx >= 0.0) {
                x[i] = dx + dsrad * Math.cos(angle2) + centreX;
                y[i] = dy + dsrad * Math.sin(angle2) + centreY;
              }
              else {
                x[i] = dx - dsrad * Math.cos(angle2) + centreX;
                y[i] = dy - dsrad * Math.sin(angle2) + centreY;
              }
            }
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
    method = Method.RADIUS;
    amount = 98.0;
    originX = Math.round(width / 2.0);
    originY = Math.round(height / 2.0);
    radius = calcRad(originX, originY);
    axis = Axis.XY;
    faces = Faces.DOUBLE;
  }

  private double calcRad(double cx, double cy) {
    return Math.sqrt(cx * cx + cy * cy) / 3.4;
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

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public double getRadius() {
    return radius;
  }

  public void setRadius(double radius) {
    this.radius = radius;
  }

  public Axis getAxis() {
    return axis;
  }

  public void setAxis(Axis axis) {
    this.axis = axis;
  }

  public Method getMethod() {
    return method;
  }

  public void setMethod(Method method) {
    this.method = method;
  }

}
