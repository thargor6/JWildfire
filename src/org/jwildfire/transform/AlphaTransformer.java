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
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.swing.Buffer;
import org.jwildfire.swing.NonHDRImageBufferComboBoxEditor;

public class AlphaTransformer extends Mesh2DTransformer {
  @Property(category = PropertyCategory.PRIMARY, description = "Image which holds the alpha channel information", editorClass = NonHDRImageBufferComboBoxEditor.class)
  private Buffer alphaChannel;
  @Property(category = PropertyCategory.SECONDARY, description = "Left offset of the height map")
  private int left = 0;
  @Property(category = PropertyCategory.SECONDARY, description = "Top offset of the height map")
  private int top = 0;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    /* generate the op-table (op[value][alpha]*/
    int optable[][] = new int[256][256];
    for (int i = 0; i < 256; i++) {
      for (int j = 0; j < 256; j++) {
        int val = i * (j + 1) + (int) 128;
        val /= (int) 256;
        optable[i][j] = Tools.limitColor(val);
      }
    }

    SimpleImage alpha = this.alphaChannel.getImage();
    int awidth = alpha.getImageWidth();
    int aheight = alpha.getImageHeight();

    int bgwidth = pImg.getImageWidth();
    int bgheight = pImg.getImageHeight();
    int hsize = 0, vsize = 0;
    int bgleft = 0, bgtop = 0;
    int aleft = 0, atop = 0;
    /* case 1 */
    if ((left >= 0) && (top >= 0)) {
      if ((left >= bgwidth) || (top >= bgheight))
        return;

      hsize = bgwidth - left;
      if (hsize > awidth)
        hsize = awidth;
      vsize = bgheight - top;
      if (vsize > aheight)
        vsize = aheight;

      bgtop = top;
      bgleft = left;
      aleft = 0;
      atop = 0;
    }
    /* case 2 */
    else if ((left < 0) && (top >= 0)) {
      if ((left <= (0 - awidth)) || (top >= bgheight))
        return;

      hsize = awidth + left;
      if (hsize > bgwidth)
        hsize = bgwidth;
      vsize = bgheight - top;
      if (vsize > aheight)
        vsize = aheight;

      bgtop = top;
      bgleft = 0;
      atop = 0;
      aleft = 0 - left;
    }
    /* case 3 */
    else if ((left >= 0) && (top < 0)) {
      if ((left >= bgwidth) || (top <= (0 - aheight)))
        return;
      hsize = bgwidth - left;
      if (hsize > awidth)
        hsize = awidth;
      vsize = aheight + top;
      if (vsize > bgheight)
        vsize = bgheight;

      bgtop = 0;
      bgleft = left;
      atop = 0 - top;
      aleft = 0;
    }
    /* case 4 */
    else if ((left < 0) && (top < 0)) {
      if ((left <= (0 - awidth)) || (top <= (0 - aheight)))
        return;

      hsize = awidth + left;
      if (hsize > bgwidth)
        hsize = bgwidth;
      vsize = aheight + top;
      if (vsize > bgheight)
        vsize = bgheight;

      bgtop = 0;
      bgleft = 0;

      atop = 0 - top;
      aleft = 0 - left;
    }

    img.fillBackground(0, 0, 0);
    Pixel pixel = new Pixel();
    for (int i = 0; i < vsize; i++) {
      for (int j = 0; j < hsize; j++) {
        int x = j + bgleft;
        int y = i + bgtop;
        pixel.setARGBValue(srcImg.getARGBValue(x, y));
        int a = alpha.getRValue(j + aleft, i + atop);
        pixel.r = optable[pixel.r][a];
        pixel.g = optable[pixel.g][a];
        pixel.b = optable[pixel.b][a];
        img.setRGB(x, y, pixel);
      }
    }

  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    left = 0;
    top = 0;
  }

  public Buffer getAlphaChannel() {
    return alphaChannel;
  }

  public void setAlphaChannel(Buffer alphaChannel) {
    this.alphaChannel = alphaChannel;
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
}
