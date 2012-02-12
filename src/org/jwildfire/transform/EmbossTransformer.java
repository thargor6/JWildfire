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
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class EmbossTransformer extends Mesh2DTransformer {
  public enum Direction {
    NORTHWEST, NORTHEAST, SOUTHWEST, SOUTHEAST
  }

  @Property(description = "Direction of the embossment", editorClass = DirectionEditor.class)
  private Direction direction = Direction.NORTHWEST;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    int cvAdd = 128;
    int matrix9[][] = new int[3][3];
    switch (direction) {
      case NORTHWEST:
        matrix9[0][0] = -2;
        matrix9[0][1] = -1;
        matrix9[0][2] = 0;
        matrix9[1][0] = -1;
        matrix9[1][1] = 0;
        matrix9[1][2] = 1;
        matrix9[2][0] = 0;
        matrix9[2][1] = 1;
        matrix9[2][2] = 2;
        break;
      case NORTHEAST:
        matrix9[0][0] = 0;
        matrix9[0][1] = -1;
        matrix9[0][2] = -2;
        matrix9[1][0] = 1;
        matrix9[1][1] = 0;
        matrix9[1][2] = -1;
        matrix9[2][0] = 2;
        matrix9[2][1] = 1;
        matrix9[2][2] = 0;
        break;
      case SOUTHEAST:
        matrix9[0][0] = 2;
        matrix9[0][1] = 1;
        matrix9[0][2] = 0;
        matrix9[1][0] = 1;
        matrix9[1][1] = 0;
        matrix9[1][2] = -1;
        matrix9[2][0] = 0;
        matrix9[2][1] = -1;
        matrix9[2][2] = -2;
        break;
      case SOUTHWEST:
        matrix9[0][0] = 0;
        matrix9[0][1] = 1;
        matrix9[0][2] = 2;
        matrix9[1][0] = -1;
        matrix9[1][1] = 0;
        matrix9[1][2] = 1;
        matrix9[2][0] = -2;
        matrix9[2][1] = -1;
        matrix9[2][2] = 0;
        break;
    }

    SimpleImage greyMap = srcImg.clone();
    {
      ColorToGrayTransformer cT = new ColorToGrayTransformer();
      cT.setWeights(ColorToGrayTransformer.Weights.LUMINANCE);
      cT.transformImage(greyMap);
    }
    ConvolveTools.convolve_3x3_grey(greyMap, img, matrix9, cvAdd);
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    direction = Direction.NORTHWEST;
  }

  public static class DirectionEditor extends ComboBoxPropertyEditor {
    public DirectionEditor() {
      super();
      setAvailableValues(new Direction[] { Direction.NORTHWEST, Direction.NORTHEAST,
          Direction.SOUTHWEST, Direction.SOUTHEAST });
    }
  }

  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
  }

}
