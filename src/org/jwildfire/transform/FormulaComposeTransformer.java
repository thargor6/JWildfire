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
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathparser.JEPWrapper;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.swing.Buffer;
import org.jwildfire.swing.NonHDRImageBufferComboBoxEditor;
import org.nfunk.jep.Node;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class FormulaComposeTransformer extends Mesh2DTransformer {

  public enum HAlignment {
    OFF, CENTRE, LEFT, RIGHT
  }

  public enum VAlignment {
    OFF, CENTRE, TOP, BOTTOM
  }

  @Property(category = PropertyCategory.PRIMARY, description = "Image to put in foreground", editorClass = NonHDRImageBufferComboBoxEditor.class)
  private Buffer foreground;
  private SimpleImage foregroundImage; // Alternative way to specify the foreground image directly
  @Property(category = PropertyCategory.SECONDARY, description = "Left offset of the foreground image")
  private int left;
  @Property(category = PropertyCategory.SECONDARY, description = "Top offset of the foreground image")
  private int top;
  @Property(category = PropertyCategory.PRIMARY, description = "Horizontal alignment of the foreground image", editorClass = HAlignmentEditor.class)
  private HAlignment hAlign;
  @Property(category = PropertyCategory.PRIMARY, description = "Vertical alignment of the foreground image", editorClass = VAlignmentEditor.class)
  private VAlignment vAlign;
  @Property(description = "Formula of the red channel (Parameters: fgR, fgG, fgB, fgWidth, fgHeight, bgR, bgG, bgB, bgWidth, bgHeight, fgLeft, fgTop, fgX, fgY)")
  private String formula1Red;
  @Property(description = "Formula of the green channel")
  private String formula2Green;
  @Property(description = "Formula of the blue channel")
  private String formula3Blue;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage bgImg = (SimpleImage) pImg;
    SimpleImage fgImg = (foregroundImage != null) ? foregroundImage : foreground.getImage();
    if (fgImg == bgImg)
      fgImg = fgImg.clone();
    Pixel fgPixel = new Pixel();
    Pixel bgPixel = new Pixel();
    // calculate left and top edge
    int fgLeft, fgTop;
    int bgWidth = bgImg.getImageWidth();
    int bgHeight = bgImg.getImageHeight();
    int fgWidth = fgImg.getImageWidth();
    int fgHeight = fgImg.getImageHeight();
    if (hAlign == HAlignment.CENTRE) {
      fgLeft = (bgWidth - fgWidth) / 2;
    }
    else if (hAlign == HAlignment.LEFT) {
      fgLeft = 0;
    }
    else if (hAlign == HAlignment.RIGHT) {
      fgLeft = bgWidth - fgWidth;
    }
    else {
      fgLeft = this.left;
    }

    if (vAlign == VAlignment.CENTRE) {
      fgTop = (bgHeight - fgHeight) / 2;
    }
    else if (vAlign == VAlignment.TOP) {
      fgTop = 0;
    }
    else if (vAlign == VAlignment.BOTTOM) {
      fgTop = bgHeight - fgHeight;
    }
    else {
      fgTop = this.top;
    }

    // Initialize the parser
    JEPWrapper parser = new JEPWrapper();
    parser.addVariable("fgR", 0.0);
    parser.addVariable("fgG", 0.0);
    parser.addVariable("fgB", 0.0);
    parser.addVariable("fgWidth", (double) fgWidth);
    parser.addVariable("fgHeight", (double) fgHeight);
    parser.addVariable("bgR", 0.0);
    parser.addVariable("bgG", 0.0);
    parser.addVariable("bgB", 0.0);
    parser.addVariable("bgWidth", (double) bgWidth);
    parser.addVariable("bgHeight", (double) bgHeight);
    parser.addVariable("fgLeft", (double) fgLeft);
    parser.addVariable("fgTop", (double) fgTop);
    parser.addVariable("fgX", 0.0);
    parser.addVariable("fgY", 0.0);
    Node redNode = parser.parse(formula1Red);
    Node greenNode = parser.parse(formula2Green);
    Node blueNode = parser.parse(formula3Blue);
    // compose the images
    for (int i = 0; i < fgHeight; i++) {
      int top = fgTop + i;
      if (top >= 0 && top < bgHeight) {
        parser.setVarValue("fgY", (double) i / 255.0);
        for (int j = 0; j < fgWidth; j++) {
          int left = fgLeft + j;
          if (left >= 0 && left < bgWidth) {
            parser.setVarValue("fgX", (double) j / 255.0);
            bgPixel.setARGBValue(bgImg.getARGBValue(left, top));
            fgPixel.setARGBValue(fgImg.getARGBValue(j, i));
            parser.setVarValue("bgR", (double) bgPixel.r / 255.0);
            parser.setVarValue("bgG", (double) bgPixel.g / 255.0);
            parser.setVarValue("bgB", (double) bgPixel.b / 255.0);
            parser.setVarValue("fgR", (double) fgPixel.r / 255.0);
            parser.setVarValue("fgG", (double) fgPixel.g / 255.0);
            parser.setVarValue("fgB", (double) fgPixel.b / 255.0);

            // TODO Genlock: z. B. Testen, ob Intensitat 0 oder grober 0
            // genlockFormula, genlockOperator (gleich, grober), genlockRefValue

            bgPixel.r = Tools.roundColor((Double) parser.evaluate(redNode) * 255.0);
            bgPixel.g = Tools.roundColor((Double) parser.evaluate(greenNode) * 255.0);
            bgPixel.b = Tools.roundColor((Double) parser.evaluate(blueNode) * 255.0);
            bgImg.setRGB(left, top, bgPixel);
          }
        }
      }
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    left = 0;
    top = 0;
    hAlign = HAlignment.CENTRE;
    vAlign = VAlignment.CENTRE;
    formula1Red = "(0.13*exp(2*fgR)+0.02*exp(4*bgR))*1.9";
    formula2Green = "(0.13*exp(2*fgG)+0.02*exp(4*bgG))*1.2";
    formula3Blue = "(0.05*exp(3*fgG)+0.13*exp(2*bgG))*0.5";
  }

  public static class HAlignmentEditor extends ComboBoxPropertyEditor {
    public HAlignmentEditor() {
      super();
      setAvailableValues(new HAlignment[] { HAlignment.OFF, HAlignment.CENTRE, HAlignment.LEFT,
          HAlignment.RIGHT });
    }
  }

  public static class VAlignmentEditor extends ComboBoxPropertyEditor {
    public VAlignmentEditor() {
      super();
      setAvailableValues(new VAlignment[] { VAlignment.OFF, VAlignment.CENTRE, VAlignment.TOP,
          VAlignment.BOTTOM });
    }
  }

  public Buffer getForeground() {
    return foreground;
  }

  public void setForeground(Buffer foreground) {
    this.foreground = foreground;
  }

  public int getLeft() {
    return left;
  }

  public void setLeft(int left) {
    this.left = left;
  }

  public int getTop() {
    return top;
  }

  public void setTop(int top) {
    this.top = top;
  }

  public HAlignment getHAlign() {
    return hAlign;
  }

  public void setHAlign(HAlignment hAlign) {
    this.hAlign = hAlign;
  }

  public VAlignment getVAlign() {
    return vAlign;
  }

  public void setVAlign(VAlignment vAlign) {
    this.vAlign = vAlign;
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

  public void setForegroundImage(SimpleImage foregroundImage) {
    this.foregroundImage = foregroundImage;
  }

}
