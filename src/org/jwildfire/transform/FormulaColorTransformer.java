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
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathparser.JEPWrapper;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.nfunk.jep.Node;

public class FormulaColorTransformer extends Mesh2DTransformer {

  @Property(description = "Formula of the red channel (Parameters: r, g, b, width, height, x, y)")
  private String formula1Red;

  @Property(description = "Formula of the green channel")
  private String formula2Green;

  @Property(description = "Formula of the blue channel")
  private String formula3Blue;

  @Property(description = "Use range 0..255 instead of 0..1.0 for colro values")
  private boolean useOriginalRGBValues;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();

    JEPWrapper parser = new JEPWrapper();
    parser.addVariable("r", 0.0);
    parser.addVariable("g", 0.0);
    parser.addVariable("b", 0.0);
    parser.addVariable("x", 0.0);
    parser.addVariable("y", 0.0);
    parser.addVariable("width", (double) width);
    parser.addVariable("height", (double) height);
    Node redNode = parser.parse(formula1Red);
    Node greenNode = parser.parse(formula2Green);
    Node blueNode = parser.parse(formula3Blue);

    Pixel pixel = new Pixel();
    for (int i = 0; i < height; i++) {
      parser.setVarValue("y", i);
      for (int j = 0; j < width; j++) {
        parser.setVarValue("x", j);
        pixel.setARGBValue(srcImg.getARGBValue(j, i));
        if (useOriginalRGBValues) {
          parser.setVarValue("r", (double) pixel.r);
          parser.setVarValue("g", (double) pixel.g);
          parser.setVarValue("b", (double) pixel.b);
          pixel.r = Tools.roundColor((Double) parser.evaluate(redNode));
          pixel.g = Tools.roundColor((Double) parser.evaluate(greenNode));
          pixel.b = Tools.roundColor((Double) parser.evaluate(blueNode));
        }
        else {
          parser.setVarValue("r", (double) pixel.r / 255.0);
          parser.setVarValue("g", (double) pixel.g / 255.0);
          parser.setVarValue("b", (double) pixel.b / 255.0);
          pixel.r = Tools.roundColor((Double) parser.evaluate(redNode) * 255.0);
          pixel.g = Tools.roundColor((Double) parser.evaluate(greenNode) * 255.0);
          pixel.b = Tools.roundColor((Double) parser.evaluate(blueNode) * 255.0);
        }
        img.setRGB(j, i, pixel);
      }
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    formula1Red = "0.13*exp(2*r)";
    formula2Green = "0.05*exp(3*g)";
    formula3Blue = "0.02*exp(4*b)";
    useOriginalRGBValues = false;
  }

  public String getFormula1Red() {
    return formula1Red;
  }

  public void setFormula1Red(String formula1Red) {
    this.formula1Red = formula1Red;
  }

  public String getFormula2Green() {
    return formula2Green;
  }

  public void setFormula2Green(String formula2Green) {
    this.formula2Green = formula2Green;
  }

  public String getFormula3Blue() {
    return formula3Blue;
  }

  public void setFormula3Blue(String formula3Blue) {
    this.formula3Blue = formula3Blue;
  }

  public boolean isUseOriginalRGBValues() {
    return useOriginalRGBValues;
  }

  public void setUseOriginalRGBValues(boolean useOriginalRGBValues) {
    this.useOriginalRGBValues = useOriginalRGBValues;
  }

}
