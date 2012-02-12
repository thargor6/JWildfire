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

import java.awt.Color;
import java.awt.Graphics;

import org.jwildfire.base.Property;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

public class RectangleTransformer extends Mesh2DTransformer {

  @Property(description = "Left edge of the rectangle")
  private int left = 12;

  @Property(description = "Top edge of the rectangle")
  private int top = 12;

  @Property(description = "Width of the rectangle")
  private int width = 400;

  @Property(description = "Height of the rectangle")
  private int height = 300;

  @Property(description = "Thickness of the rectangle")
  private int thickness = 20;

  @Property(description = "Color of the rectangle")
  private Color color = new Color(255, 255, 255);

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    Graphics g = img.getGraphics();
    g.setColor(color);
    if (thickness == 1) {
      g.drawRect(left, top, width, height);
    }
    else if (thickness > 1) {
      // p1                          p2
      //   p5                      p6
      //   p7                      p8 
      // p3                          p4
      final int x1 = left;
      final int y1 = top;
      final int x2 = left + width - 1;
      final int y2 = top;
      final int x3 = left;
      final int y3 = top + height - 1;
      final int x4 = left + width - 1;
      final int y4 = top + height - 1;
      final int x5 = x1 + thickness;
      final int y5 = y1 + thickness;
      final int x6 = x2 - thickness;
      final int y6 = y2 + thickness;
      final int x7 = x3 + thickness;
      final int y7 = y3 - thickness;
      final int x8 = x4 - thickness;
      final int y8 = y4 - thickness;
      g.fillPolygon(new int[] { x1, x2, x6, x5 }, new int[] { y1, y2, y6, y5 }, 4);
      g.fillPolygon(new int[] { x7, x8, x4, x3 }, new int[] { y7, y8, y4, y3 }, 4);
      g.fillPolygon(new int[] { x1, x5, x7, x3 }, new int[] { y1, y5, y7, y3 }, 4);
      g.fillPolygon(new int[] { x2, x4, x8, x6 }, new int[] { y2, y4, y8, y6 }, 4);
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    left = 60;
    top = 48;
    width = pImg.getImageWidth() - 120;
    height = pImg.getImageHeight() - 96;
    thickness = pImg.getImageWidth() / 16;
    color = new Color(220, 220, 200);
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

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public int getThickness() {
    return thickness;
  }

  public void setThickness(int thickness) {
    this.thickness = thickness;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

}
