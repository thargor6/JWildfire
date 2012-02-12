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

import org.jwildfire.base.ConvolveTools;
import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyMax;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class LineArtTransformer extends Mesh2DTransformer {

  public enum Mode {
    GRAY, COLOR, INVERSE_GREY, INVERSE_COLOR
  }

  @Property(description = "Color mode", editorClass = ModeEditor.class)
  private Mode mode = Mode.GRAY;

  @Property(description = "Contrast correction")
  @PropertyMin(-255)
  @PropertyMax(255)
  private int contrast = 0;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    int cvAdd;
    if ((mode == Mode.GRAY) || (mode == Mode.COLOR))
      cvAdd = 255;
    else
      cvAdd = 0;

    int matrix9[][] = new int[3][3];
    matrix9[0][0] = -1;
    matrix9[0][1] = -1;
    matrix9[0][2] = 1;
    matrix9[1][0] = 0;
    matrix9[1][1] = 2;
    matrix9[1][2] = 0;
    matrix9[2][0] = -1;
    matrix9[2][1] = -1;
    matrix9[2][2] = 1;

    srcImg = srcImg.clone();
    if ((mode == Mode.GRAY) || (mode == Mode.INVERSE_GREY)) {
      ColorToGrayTransformer cT = new ColorToGrayTransformer();
      cT.setWeights(ColorToGrayTransformer.Weights.LUMINANCE);
      cT.transformImage(srcImg);
    }
    if (this.contrast != 0) {
      BalancingTransformer bT = new BalancingTransformer();
      bT.setContrast(this.contrast);
      bT.transformImage(srcImg);
    }
    if ((mode == Mode.GRAY) || (mode == Mode.INVERSE_GREY))
      ConvolveTools.convolve_3x3_grey(srcImg, img, matrix9, cvAdd);
    else
      ConvolveTools.convolve_3x3_color(srcImg, img, matrix9, cvAdd);
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    mode = Mode.INVERSE_COLOR;
    contrast = 160;
  }

  public static class ModeEditor extends ComboBoxPropertyEditor {
    public ModeEditor() {
      super();
      setAvailableValues(new Mode[] { Mode.GRAY, Mode.COLOR, Mode.INVERSE_GREY, Mode.INVERSE_COLOR });
    }
  }

  public Mode getMode() {
    return mode;
  }

  public void setMode(Mode mode) {
    this.mode = mode;
  }

  public int getContrast() {
    return contrast;
  }

  public void setContrast(int contrast) {
    this.contrast = contrast;
  }

}
