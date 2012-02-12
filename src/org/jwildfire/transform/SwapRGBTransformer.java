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
import org.jwildfire.image.Pixel;
import org.jwildfire.image.WFImage;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class SwapRGBTransformer extends PixelTransformer {

  public enum Mode {
    RBG, GRB, GBR, BRG, BGR
  }

  @Property(description = "How to swap the three channels R, G and B", editorClass = ModeEditor.class)
  private Mode mode = Mode.GBR;

  @Override
  protected void transformPixel(Pixel pPixel, int pX, int pY, int pImageWidth, int pImageHeight) {
    int r = pPixel.r;
    int g = pPixel.g;
    int b = pPixel.b;
    switch (mode) {
      case RBG:
        pPixel.r = r;
        pPixel.g = b;
        pPixel.b = g;
        break;
      case GRB:
        pPixel.r = g;
        pPixel.g = r;
        pPixel.b = b;
        break;
      case GBR:
        pPixel.r = g;
        pPixel.g = b;
        pPixel.b = r;
        break;
      case BRG:
        pPixel.r = b;
        pPixel.g = r;
        pPixel.b = g;
        break;
      case BGR:
        pPixel.r = b;
        pPixel.g = g;
        pPixel.b = r;
        break;
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    mode = Mode.GBR;
  }

  public Mode getMode() {
    return mode;
  }

  public void setMode(Mode mode) {
    this.mode = mode;
  }

  public static class ModeEditor extends ComboBoxPropertyEditor {
    public ModeEditor() {
      super();
      setAvailableValues(new Mode[] { Mode.RBG, Mode.GRB, Mode.GBR, Mode.BRG, Mode.BGR });
    }
  }

}
