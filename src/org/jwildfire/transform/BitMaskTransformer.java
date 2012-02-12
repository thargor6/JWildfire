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
import org.jwildfire.image.Pixel;
import org.jwildfire.image.WFImage;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class BitMaskTransformer extends PixelTransformer {
  public enum Mode {
    AND, EOR, OR
  };

  @Property(category = PropertyCategory.PRIMARY, description = "The 8-bit mask as an integer value")
  @PropertyMin(0)
  @PropertyMax(255)
  private int mask = 128;

  @Property(category = PropertyCategory.PRIMARY, editorClass = ModeEditor.class, description = "How to to combine the pixel data with the mask")
  private Mode mode = Mode.OR;

  @Override
  protected void transformPixel(Pixel pPixel, int pX, int pY, int pImageWidth, int pImageHeight) {
    int lMask = mask;
    switch (mode) {
      case AND:
        pPixel.r = pPixel.r & lMask;
        pPixel.g = pPixel.g & lMask;
        pPixel.b = pPixel.b & lMask;
        break;
      case EOR:
        pPixel.r = pPixel.r ^ lMask;
        pPixel.g = pPixel.g ^ lMask;
        pPixel.b = pPixel.b ^ lMask;
        break;
      case OR:
        pPixel.r = pPixel.r | lMask;
        pPixel.g = pPixel.g | lMask;
        pPixel.b = pPixel.b | lMask;
        break;
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    mask = 128;
    mode = Mode.OR;
  }

  public int getMask() {
    return mask;
  }

  public void setMask(int mask) {
    this.mask = mask;
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
      setAvailableValues(new Mode[] { Mode.AND, Mode.EOR, Mode.OR });
    }
  }
}
