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

public class TurnTransformer extends Mesh2DTransformer {

  public enum Angle {
    _90, _180, _270
  }

  public enum Direction {
    LEFT, RIGHT
  }

  @Property(description = "Turn angle", editorClass = AngleEditor.class)
  private Angle angle = Angle._90;
  @Property(description = "Turn direction", editorClass = DirectionEditor.class)
  private Direction direction = Direction.LEFT;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    if ((this.angle == Angle._90) || (this.angle == Angle._270))
      img.resetImage(height, width);
    Pixel pixel = new Pixel();
    if (((this.angle == Angle._90) && (this.direction == Direction.LEFT))
        || ((this.angle == Angle._270) && (this.direction == Direction.RIGHT)))
    {

      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          pixel.setARGBValue(srcImg.getARGBValue(j, i));
          img.setRGB(i, width - j - 1, pixel);
        }
      }
    }
    else if (this.angle == Angle._180) {
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          pixel.setARGBValue(srcImg.getARGBValue(j, i));
          img.setRGB(width - j - 1, height - i - 1, pixel);
        }
      }
    }
    else if (((angle == Angle._270) && (this.direction == Direction.LEFT))
        || ((angle == Angle._90) && (this.direction == Direction.RIGHT)))
    {
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          pixel.setARGBValue(srcImg.getARGBValue(j, i));
          img.setRGB(height - i - 1, j, pixel);
        }
      }
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    angle = Angle._90;
    direction = Direction.LEFT;
  }

  public static class AngleEditor extends ComboBoxPropertyEditor {
    public AngleEditor() {
      super();
      setAvailableValues(new Angle[] { Angle._90, Angle._180, Angle._270 });
    }
  }

  public static class DirectionEditor extends ComboBoxPropertyEditor {
    public DirectionEditor() {
      super();
      setAvailableValues(new Direction[] { Direction.LEFT, Direction.RIGHT });
    }
  }

  public Angle getAngle() {
    return angle;
  }

  public void setAngle(Angle angle) {
    this.angle = angle;
  }

  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
  }

}
