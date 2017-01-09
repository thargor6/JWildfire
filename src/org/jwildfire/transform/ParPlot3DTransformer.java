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
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.base.mathparser.JEPWrapper;
import org.jwildfire.image.WFImage;
import org.nfunk.jep.Node;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class ParPlot3DTransformer extends Mesh3DTransformer {

  public enum Preset {
    NONE, PRESET01, PRESET02, PRESET03, PRESET04, PRESET05, PRESET06, PRESET07, PRESET08, PRESET09,
    PRESET10, PRESET11, PRESET12
  }

  @Property(description = "Minimum of the U-range")
  private double uMin;

  @Property(description = "Maximum of the U-range")
  private double uMax;

  @Property(description = "Minimum of the V-range")
  private double vMin;

  @Property(description = "Maximum of the V-range")
  private double vMax;

  @Property(description = "Formula(u,v) for the X-coordinate")
  private String xFormula;

  @Property(description = "Formula(u,v) for the Y-coordinate")
  private String yFormula;

  @Property(description = "Formula(u,v) for the Z-coordinate")
  private String zFormula;

  @Property(description = "Built-in preset", editorClass = PresetEditor.class)
  private Preset preset;

  @Property(description = "Scale factor for source z amplitude")
  private double zScale;

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
    parser.addVariable("u", 0.0);
    parser.addVariable("v", 0.0);
    Node xNode = parser.parse(xFormula);
    Node yNode = parser.parse(yFormula);
    Node zNode = parser.parse(zFormula);

    // Don't calculate the actual bounding box because this may cause unexpected results if the object was deformed by another tansformer before
    double objUMin = -(double) width / 2.0;
    //      double objXMax = (double) width / 2.0;
    double objVMin = -(double) height / 2.0;
    //      double objYMax = (double) height / 2.0;
    double objUSize = (double) width;
    double objVSize = (double) height;

    double du = this.uMax - this.uMin;
    double dv = this.vMax - this.vMin;

    double xMin = 0.0, yMin = 0.0, zMin = 0.0;
    double xMax = 0.0, yMax = 0.0, zMax = 0.0;

    double oriZMin = 0.0, oriZMax = 0.0;
    for (int i = 0; i < pCount; i++) {
      if (z[i] < oriZMin)
        oriZMin = z[i];
      else if (z[i] > oriZMax)
        oriZMax = z[i];
    }
    double oriZSize = oriZMax - oriZMin;
    double oriZScale = oriZSize / Math.sqrt(width * width + height * height);
    for (int i = 0; i < pCount; i++) {
      double zz = oriZSize > MathLib.EPSILON ? z[i] / oriZSize * oriZScale : 0.0;
      double uu = ((x[i] - objUMin) * du) / objUSize + uMin;
      double vv = ((y[i] - objVMin) * dv) / objVSize + vMin;
      parser.setVarValue("u", uu);
      parser.setVarValue("v", vv);
      x[i] = (Double) parser.evaluate(xNode) * (1 + zz * this.zScale);
      if (x[i] < xMin)
        xMin = x[i];
      else if (x[i] > xMax)
        xMax = x[i];
      y[i] = (Double) parser.evaluate(yNode) * (1 + zz * this.zScale);
      if (y[i] < yMin)
        yMin = y[i];
      else if (y[i] > yMax)
        yMax = y[i];
      z[i] = (Double) parser.evaluate(zNode) * (1 + zz * this.zScale);
      if (z[i] < zMin)
        zMin = z[i];
      else if (z[i] > zMax)
        zMax = z[i];
    }
    double xSize = xMax - xMin;
    if (xSize < MathLib.EPSILON)
      xSize = MathLib.EPSILON;
    double ySize = yMax - yMin;
    if (ySize < MathLib.EPSILON)
      ySize = MathLib.EPSILON;
    double zSize = zMax - zMin;
    if (zSize < MathLib.EPSILON)
      zSize = MathLib.EPSILON;

    double xScl = (double) width / xSize;
    double yScl = (double) height / ySize;
    double zScl = Math.sqrt(width * width + height * height) / zSize;

    for (int i = 0; i < pCount; i++) {
      x[i] *= xScl;
      y[i] *= yScl;
      z[i] *= zScl;
    }
  }

  private void initPreset() {
    switch (preset) {
      case PRESET01:
        uMin = 0.0;
        uMax = 2.0 * Math.PI;
        vMin = 0.0;
        vMax = 2.0 * Math.PI;
        xFormula = "cos(u)*(4+cos(v))";
        yFormula = "sin(u)*(4+cos(v))";
        zFormula = "4*sin(2*u)+sin(v)*(1.2-sin(v))";
        break;
      case PRESET02:
        uMin = 0.0;
        uMax = Math.PI;
        vMin = 0.0;
        vMax = Math.PI;
        xFormula = "cos(v)*sin(2*u)";
        yFormula = "sin(v)*sin(2*u)";
        zFormula = "sin(2*v)*(cos(u))^2";
        break;
      case PRESET03:
        uMin = 0.0;
        uMax = 5 * Math.PI;
        vMin = 0.0;
        vMax = 2 * Math.PI;
        xFormula = "cos(u)*(exp(u/10)-1)*(cos(v)+0.8)";
        yFormula = "sin(u)*(exp(u/10)-1)*(cos(v)+0.8)";
        zFormula = "(exp(u/10)-1)*sin(v)";
        break;
      case PRESET04:
        uMin = 0.0;
        uMax = 2 * Math.PI;
        vMin = 0.0;
        vMax = 2 * Math.PI;
        xFormula = "cos(v)*(2+sin(u+v/3))";
        yFormula = "sin(v)*(2+sin(u+v/3))";
        zFormula = "cos(u+v/3)";
        break;
      case PRESET05:
        uMin = 0.0;
        uMax = 2.0 * Math.PI;
        vMin = 0.0;
        vMax = 2.0 * Math.PI;
        xFormula = "cos(u)*(2+cos(v))";
        yFormula = "sin(u)*(2+cos(v))";
        zFormula = "sin(v)";
        break;
      case PRESET06:
        uMin = 0.0;
        uMax = 4.0 * Math.PI;
        vMin = 0.0;
        vMax = 2.0 * Math.PI;
        xFormula = "cos(u)*(2+cos(v))";
        yFormula = "sin(u)*(2+cos(v))";
        zFormula = "(u-2*pi)+sin(v)";
        break;
      case PRESET07:
        uMin = 0.0;
        uMax = Math.PI;
        vMin = 0.0;
        vMax = 2.0 * Math.PI;
        xFormula = "u*cos(v)";
        yFormula = "u*sin(v)";
        zFormula = "(cos(4*u))^2*exp(0-u)";
        break;
      case PRESET08:
        uMin = 0.0 - Math.PI;
        uMax = Math.PI;
        vMin = 0.0 - Math.PI;
        vMax = 2.0 * Math.PI;
        xFormula = "cos(u)*(2+(cos(u/2))^2*sin(v))";
        yFormula = "sin(u)*(2+(cos(u/2))^2*sin(v))";
        zFormula = "(cos(u/2))^2*cos(v)";
        break;
      case PRESET09:
        uMin = 0.0;
        uMax = 2.0 * Math.PI;
        vMin = 0.0;
        vMax = 2.0 * Math.PI;
        xFormula = "cos(u)*(4+cos(v))";
        yFormula = "sin(u)*(4+cos(v))";
        zFormula = "3*sin(u)+(sin(3*v)*(1.2+sin(3*v)))";
        break;
      case PRESET10:
        uMin = 0.0 - Math.PI;
        uMax = Math.PI;
        vMin = 0.0 - Math.PI;
        vMax = Math.PI;
        xFormula = "u*cos(v)";
        yFormula = "v*cos(u)";
        zFormula = "u*v*sin(u)*sin(v)";
        break;
      case PRESET11:
        uMin = 0.0;
        uMax = 2.0 * Math.PI;
        vMin = 0.0;
        vMax = Math.PI;
        xFormula = "cos(u)*sin(v^3/(3.1415926^2))";
        yFormula = "sin(u)*sin(v)";
        zFormula = "cos(v)";
        break;
      case PRESET12:
        uMin = 0.0;
        uMax = 2.0 * Math.PI;
        vMin = 0.0;
        vMax = 2.0 * Math.PI;
        xFormula = "cos(u)*((cos(3*u)+2)*sin(v)+0.5)";
        yFormula = "sin(u)*((cos(3*u)+2)*sin(v)+0.5)";
        zFormula = "(cos(3*u)+2)*cos(v)";
        break;
      default: // nothing to do
        break;
    }

  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    super.initDefaultParams(pImg);
    alpha = 10;
    beta = 20.0;
    zoom = 0.5;
    faces = Faces.DOUBLE;
    preset = Preset.NONE;
    zScale = 1.0;

    uMin = 0.0;
    uMax = 2 * Math.PI;
    vMin = 0.0;
    vMax = 2 * Math.PI;
    xFormula = "cos(v)*(2+sin(u+v/3))";
    yFormula = "sin(v)*(2+sin(u+v/3))";
    zFormula = "cos(u+v/3)";
  }

  public double getUMin() {
    return uMin;
  }

  public void setUMin(double uMin) {
    this.uMin = uMin;
  }

  public double getUMax() {
    return uMax;
  }

  public void setUMax(double uMax) {
    this.uMax = uMax;
  }

  public double getVMin() {
    return vMin;
  }

  public void setVMin(double vMin) {
    this.vMin = vMin;
  }

  public double getVMax() {
    return vMax;
  }

  public void setVMax(double vMax) {
    this.vMax = vMax;
  }

  public String getXFormula() {
    return xFormula;
  }

  public void setXFormula(String xFormula) {
    this.xFormula = xFormula;
  }

  public String getYFormula() {
    return yFormula;
  }

  public void setYFormula(String yFormula) {
    this.yFormula = yFormula;
  }

  public String getZFormula() {
    return zFormula;
  }

  public void setZFormula(String zFormula) {
    this.zFormula = zFormula;
  }

  public static class PresetEditor extends ComboBoxPropertyEditor {
    public PresetEditor() {
      super();
      setAvailableValues(new Preset[] { Preset.NONE, Preset.PRESET01, Preset.PRESET02,
          Preset.PRESET03, Preset.PRESET04, Preset.PRESET05, Preset.PRESET06, Preset.PRESET07,
          Preset.PRESET08, Preset.PRESET09, Preset.PRESET10, Preset.PRESET11, Preset.PRESET12 });
    }
  }

  public Preset getPreset() {
    return preset;
  }

  public void setPreset(Preset preset) {
    this.preset = preset;
  }

  public double getZScale() {
    return zScale;
  }

  public void setZScale(double zScale) {
    this.zScale = zScale;
  }

}
