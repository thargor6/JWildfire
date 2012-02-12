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
import org.jwildfire.image.Pixel;
import org.jwildfire.image.WFImage;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class ThresholdTransformer extends PixelTransformer {

  public enum WhitePixelMode {
    WHITE, ORIGINAL
  };

  @Property(description = "Pixel intensity at which to treat a pixel as \"white pixel\"")
  @PropertyMin(0)
  @PropertyMax(255)
  private int level = 127;

  @Property(description = "How to treat \"white pixels\"", editorClass = WhitePixelModeEditor.class)
  private WhitePixelMode whitePixelMode = WhitePixelMode.WHITE;

  @Override
  protected void transformPixel(Pixel pPixel, int pX, int pY, int pImageWidth, int pImageHeight) {
    int level = this.level;
    long rs = 2990;
    long gs = 5880;
    long bs = 1130;
    rs = (rs * 1024) / 10000;
    gs = (gs * 1024) / 10000;
    bs = (bs * 1024) / 10000;
    int lval = (int) ((rs * (long) pPixel.r + gs * (long) pPixel.g + bs * (long) (pPixel.b)) >> 10);
    if (lval < level)
      pPixel.r = pPixel.g = pPixel.b = 0;
    else if (whitePixelMode == WhitePixelMode.WHITE)
      pPixel.r = pPixel.g = pPixel.b = 255;
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    level = 127;
    whitePixelMode = WhitePixelMode.WHITE;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public WhitePixelMode getWhitePixelMode() {
    return whitePixelMode;
  }

  public void setWhitePixelMode(WhitePixelMode whitePixelMode) {
    this.whitePixelMode = whitePixelMode;
  }

  public static class WhitePixelModeEditor extends ComboBoxPropertyEditor {
    public WhitePixelModeEditor() {
      super();
      setAvailableValues(new WhitePixelMode[] { WhitePixelMode.ORIGINAL, WhitePixelMode.WHITE });
    }
  }

}
