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

public class ZPlot3DTransformer extends Mesh3DTransformer {

  public enum Preset {
    NONE, PRESET01, PRESET02, PRESET03, PRESET04, PRESET05, PRESET06, PRESET07
  }

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

  @Property(description = "Minimum of the X-range")
  private double xMin;

  @Property(description = "Maximum of the X-range")
  private double xMax;

  @Property(description = "Minimum of the Y-range")
  private double yMin;

  @Property(description = "Maximum of the Y-range")
  private double yMax;

  @Property(description = "Formula(x,y) for the amplitude")
  private String formula;

  @Property(description = "Built-in preset", editorClass = PresetEditor.class)
  private Preset preset;

  @Override
  protected void transformMesh(Mesh3D pMesh3D, int pImageWidth, int pImageHeight) {
    initPreset();
    int pCount = pMesh3D.getPCount();
    int width = pImageWidth;
    int height = pImageHeight;
    double x[] = pMesh3D.getX();
    double y[] = pMesh3D.getY();
    double z[] = pMesh3D.getZ();

    JEPWrapper parser = new JEPWrapper();
    parser.addVariable("x", 0.0);
    parser.addVariable("y", 0.0);
    Node node = parser.parse(formula);

    // Don't calculate the actual bounding box because this may cause unexpected results if the object was deformed by another tansformer before
    double objXMin = -(double) width / 2.0;
    //      double objXMax = (double) width / 2.0;
    double objYMin = -(double) height / 2.0;
    //      double objYMax = (double) height / 2.0;
    double objXSize = (double) width;
    double objYSize = (double) height;

    double dx = this.xMax - this.xMin;
    double dy = this.yMax - this.yMin;

    if (!this.damp) {
      for (int i = 0; i < pCount; i++) {
        double xx = ((x[i] - objXMin) * dx) / objXSize + xMin;
        double yy = ((y[i] - objYMin) * dy) / objYSize + xMin;
        parser.setVarValue("x", xx);
        parser.setVarValue("y", yy);
        double amp = (Double) parser.evaluate(node);
        z[i] -= amp;
      }
    }
    else {
      for (int i = 0; i < pCount; i++) {
        double xx = ((x[i] - objXMin) * dx) / objXSize + xMin;
        double yy = ((y[i] - objYMin) * dy) / objYSize + xMin;
        parser.setVarValue("x", xx);
        parser.setVarValue("y", yy);
        double amp = (Double) parser.evaluate(node);

        double dxx = ((x[i] - objXMin) * dx) / objXSize + xMin - originX;
        double dyy = ((y[i] - objYMin) * dy) / objYSize + xMin - originY;
        double drr = Math.sqrt(dxx * dxx + dyy * dyy);
        double dmp = drr * damping;
        amp *= Math.exp(dmp);
        z[i] -= amp;
      }
    }
  }

  private void initPreset() {
    switch (preset) {
      case PRESET01:
        xMin = -5.0;
        xMax = 3.0;
        yMin = -4.0;
        yMax = 4.0;
        formula = "60*sin(x*x+y*y)";
        break;
      case PRESET02:
        xMin = -1.0;
        xMax = 1.6;
        yMin = -1.2;
        yMax = 1.2;
        formula = "300*sin(2*exp(-4*(x*x+y*y)))/2";
        break;
      case PRESET03:
        xMin = -1.0;
        xMax = 1.6;
        yMin = -1.2;
        yMax = 1.2;
        formula = "120*cos(x*y*12)/6";
        break;
      case PRESET04:
        xMin = -1.6;
        xMax = 1.2;
        yMin = -1.5;
        yMax = 1.5;
        formula = "160*cos(sqrt(x*x+y*y)*14)*exp(-2*(x*x+y*y))/2";
        break;
      case PRESET05:
        xMin = -2.2;
        xMax = 1.8;
        yMin = -2.0;
        yMax = 2.0;
        //    //formula = "160*cos(atan(x/y)*sig(y)*8)/4*sin(sqrt(x*x+y*y)*3)";
        formula = "160*cos(atan(x/y)*8)/4*sin(sqrt(x*x+y*y)*3)";
        break;
      case PRESET06:
        xMin = -5.0;
        xMax = 5.0;
        yMin = -5.0;
        yMax = 5.0;
        formula = "100*(sin(x)*sin(x)+cos(y)*cos(y))/(5+x*x+y*y)";
        break;
      case PRESET07:
        xMin = -2.0;
        xMax = 2.0;
        yMin = -2.0;
        yMax = 2.0;
        formula = "100*cos(2*pi*(x+y))*(1-sqrt(x*x+y*y))";
        break;
      default: // nothing to do
        break;
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    super.initDefaultParams(pImg);
    preset = Preset.NONE;
    damping = -0.5;
    damp = false;
    originX = 0.0;
    originY = 0.0;
    originZ = 0.0;

    xMin = -2.2;
    xMax = 1.8;
    yMin = -2.0;
    yMax = 2.0;
    formula = "100*((sin(x*4)+sin(y*4))/4+cos(sqrt(x*x+y*y)*16)/16)";

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

  public double getYMin() {
    return yMin;
  }

  public void setYMin(double yMin) {
    this.yMin = yMin;
  }

  public double getYMax() {
    return yMax;
  }

  public void setYMax(double yMax) {
    this.yMax = yMax;
  }

  public static class PresetEditor extends ComboBoxPropertyEditor {
    public PresetEditor() {
      super();
      setAvailableValues(new Preset[] { Preset.NONE, Preset.PRESET01, Preset.PRESET02,
          Preset.PRESET03, Preset.PRESET04, Preset.PRESET05, Preset.PRESET06, Preset.PRESET07 });
    }
  }

  public Preset getPreset() {
    return preset;
  }

  public void setPreset(Preset preset) {
    this.preset = preset;
  }
}
