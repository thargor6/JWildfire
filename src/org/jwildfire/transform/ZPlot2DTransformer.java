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
import org.jwildfire.base.mathparser.JEPWrapper;
import org.jwildfire.image.WFImage;
import org.nfunk.jep.Node;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class ZPlot2DTransformer extends Mesh3DTransformer {

  public enum Axis {
    X, Y, XY, RADIAL
  };

  @Property(description = "Damping of the effect", category = PropertyCategory.SECONDARY)
  private double damping;

  @Property(description = "Damping on/off", category = PropertyCategory.SECONDARY)
  private boolean damp;

  @Property(description = "X-coordinate of the effect origin (for damping)", category = PropertyCategory.SECONDARY)
  private double originX;

  @Property(description = "Y-coordinate of the effect origin (for damping)", category = PropertyCategory.SECONDARY)
  private double originY;

  @Property(description = "Z-coordinate of the effect origin (for damping)", category = PropertyCategory.SECONDARY)
  private double originZ;

  @Property(description = "Propagation axis of the effect", editorClass = AxisEditor.class)
  private Axis axis;

  @Property(description = "Minimum of the X-range")
  private double xMin;

  @Property(description = "Maximum of the X-range")
  private double xMax;

  @Property(description = "Formula for the amplitude")
  private String formula;

  @Override
  protected void transformMesh(Mesh3D pMesh3D, int pImageWidth, int pImageHeight) {
    int pCount = pMesh3D.getPCount();
    int width = pImageWidth;
    int height = pImageHeight;
    double x[] = pMesh3D.getX();
    double y[] = pMesh3D.getY();
    double z[] = pMesh3D.getZ();

    JEPWrapper parser = new JEPWrapper();
    parser.addVariable("x", 0.0);
    Node node = parser.parse(formula);

    // Don't calculate the actual bounding box because this may cause unexpected results if the object was deformed by another tansformer before
    double objXMin = -(double) width / 2.0;
    //      double objXMax = (double) width / 2.0;
    double objYMin = -(double) height / 2.0;
    //      double objYMax = (double) height / 2.0;
    double objXSize = (double) width;
    double objYSize = (double) height;

    double dx = this.xMax - this.xMin;

    if (this.axis == Axis.X) {
      if (!this.damp) {
        for (int i = 0; i < pCount; i++) {
          double xx = ((x[i] - objXMin) * dx) / objXSize + xMin;
          parser.setVarValue("x", xx);
          double amp = (Double) parser.evaluate(node);
          z[i] -= amp;
        }
      }
      else {
        for (int i = 0; i < pCount; i++) {
          double xx = ((x[i] - objXMin) * dx) / objXSize + xMin;
          parser.setVarValue("x", xx);
          double amp = (Double) parser.evaluate(node);
          double dxx = ((x[i] - objXMin) * dx) / objXSize + xMin - originX;
          double drr = Math.abs(dxx);
          double dmp = drr * damping;
          amp *= Math.exp(dmp);
          z[i] -= amp;
        }
      }
    }
    else if (this.axis == Axis.Y) {
      if (!this.damp) {
        for (int i = 0; i < pCount; i++) {
          double yy = ((y[i] - objYMin) * dx) / objYSize + xMin;
          parser.setVarValue("x", yy);
          double amp = (Double) parser.evaluate(node);
          z[i] -= amp;
        }
      }
      else {
        for (int i = 0; i < pCount; i++) {
          double yy = ((y[i] - objYMin) * dx) / objYSize + xMin;
          parser.setVarValue("x", yy);
          double amp = (Double) parser.evaluate(node);
          double dyy = ((y[i] - objYMin) * dx) / objYSize + xMin - originY;
          double drr = Math.abs(dyy);
          double dmp = drr * damping;
          amp *= Math.exp(dmp);
          z[i] -= amp;
        }
      }
    }
    else if (this.axis == Axis.XY) {
      if (!this.damp) {
        for (int i = 0; i < pCount; i++) {
          double xx = ((x[i] - objXMin) * dx) / objXSize + xMin;
          double yy = ((y[i] - objYMin) * dx) / objYSize + xMin;
          double rr = Math.sqrt(xx * xx + yy * yy);
          parser.setVarValue("x", rr);
          double amp = (Double) parser.evaluate(node);
          z[i] -= amp;
        }
      }
      else {
        for (int i = 0; i < pCount; i++) {
          double xx = ((x[i] - objXMin) * dx) / objXSize + xMin;
          double yy = ((y[i] - objYMin) * dx) / objYSize + xMin;
          double rr = Math.sqrt(xx * xx + yy * yy);
          parser.setVarValue("x", rr);
          double amp = (Double) parser.evaluate(node);

          double dxx = ((x[i] - objXMin) * dx) / objXSize + xMin - originX;
          double dyy = ((y[i] - objYMin) * dx) / objYSize + xMin - originY;
          double drr = Math.sqrt(dxx * dxx + dyy * dyy);
          double dmp = drr * damping;
          amp *= Math.exp(dmp);
          z[i] -= amp;
        }
      }
    }
    else { /* radial */
      if (!this.damp) {
        for (int i = 0; i < pCount; i++) {
          double xx = ((x[i] - objXMin) * dx) / objXSize + xMin - originX;
          double yy = ((y[i] - objYMin) * dx) / objYSize + xMin - originY;
          double zz = z[i] - originZ;
          double rr = Math.sqrt(xx * xx + yy * yy + zz * zz);
          parser.setVarValue("x", rr);
          double amp = (Double) parser.evaluate(node);
          double vx, vy, vz;
          if (rr > 0.00001) {
            vx = xx / rr;
            vy = yy / rr;
            vz = zz / rr;
          }
          else {
            vx = vy = 0.0;
            vz = 1.0;
          }
          x[i] += vx * amp;
          y[i] += vy * amp;
          z[i] += vz * amp;
        }
      }
      else {
        for (int i = 0; i < pCount; i++) {
          double xx = ((x[i] - objXMin) * dx) / objXSize + xMin - originX;
          double yy = ((y[i] - objYMin) * dx) / objYSize + xMin - originY;
          double zz = z[i] - originZ;
          double rr = Math.sqrt(xx * xx + yy * yy + zz * zz);
          parser.setVarValue("x", rr);
          double amp = (Double) parser.evaluate(node);

          double drr = rr;
          double dmp = drr * damping;
          amp *= Math.exp(dmp);

          double vx, vy, vz;
          if (rr > 0.00001) {
            vx = xx / rr;
            vy = yy / rr;
            vz = zz / rr;
          }
          else {
            vx = vy = 0.0;
            vz = 1.0;
          }
          x[i] += vx * amp;
          y[i] += vy * amp;
          z[i] += vz * amp;
        }
      }
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    super.initDefaultParams(pImg);
    damping = -0.5;
    damp = true;
    originX = 0.0;
    originY = 0.0;
    originZ = 0.0;
    axis = Axis.XY;
    xMin = -5.0;
    xMax = 3.0;
    formula = "60*sin(2*x*x)";
    // 40*(sin(x)+2*sin(2*x)+1*sin(4*x)), x=-5..5
    beta = 45.0;
    zoom = 1.2;
  }

  public double getDamping() {
    return damping;
  }

  public void setDamping(double damping) {
    this.damping = damping;
  }

  public boolean isDamp() {
    return damp;
  }

  public void setDamp(boolean damp) {
    this.damp = damp;
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

  public Axis getAxis() {
    return axis;
  }

  public void setAxis(Axis axis) {
    this.axis = axis;
  }

  public static class AxisEditor extends ComboBoxPropertyEditor {
    public AxisEditor() {
      super();
      setAvailableValues(new Axis[] { Axis.X, Axis.Y, Axis.XY, Axis.RADIAL });
    }
  }

  public double getXMin() {
    return xMin;
  }

  public void setXMin(double xMin) {
    this.xMin = xMin;
  }

  public double getXMax() {
    return xMax;
  }

  public void setXMax(double xMax) {
    this.xMax = xMax;
  }

  public String getFormula() {
    return formula;
  }

  public void setFormula(String formula) {
    this.formula = formula;
  }

  @Override
  public void initPresets() {
    addPreset("60*sin(2*x*x)").
        addProperty("formula", "60*sin(2*x*x)").
        addProperty("axis", Axis.XY).
        addProperty("xMin", -5.0).
        addProperty("xMax", 3.0);
    addPreset("40*(sin(x)+2*sin(2*x)+1*sin(4*x))").
        addProperty("formula", "40*(sin(x)+2*sin(2*x)+1*sin(4*x))").
        addProperty("axis", Axis.XY).
        addProperty("xMin", -5.0).
        addProperty("xMax", 5.0);
  }
}
