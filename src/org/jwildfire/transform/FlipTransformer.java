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
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class FlipTransformer extends Mesh2DTransformer {
  public enum Axis {
    X, Y, XY
  }

  @Property(description = "Flip axis", editorClass = AxisEditor.class)
  private Axis axis = Axis.X;

  public static class AxisEditor extends ComboBoxPropertyEditor {
    public AxisEditor() {
      super();
      setAvailableValues(new Axis[] { Axis.X, Axis.Y, Axis.XY });
    }
  }

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    Pixel pixel = new Pixel();
    if (axis == Axis.X) {
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          pixel.setARGBValue(srcImg.getARGBValue(width - j - 1, i));
          img.setRGB(j, i, pixel);
        }
      }
    }
    else if (axis == Axis.Y) {
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          pixel.setARGBValue(srcImg.getARGBValue(j, height - i - 1));
          img.setRGB(j, i, pixel);
        }
      }
    }
    else if (axis == Axis.XY) {
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          pixel.setARGBValue(srcImg.getARGBValue(width - j - 1, height - i - 1));
          img.setRGB(j, i, pixel);
        }
      }
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    axis = Axis.X;
  }

  public Axis getAxis() {
    return axis;
  }

  public void setAxis(Axis axis) {
    this.axis = axis;
  }

}
