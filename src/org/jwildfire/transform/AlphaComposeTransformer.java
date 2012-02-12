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

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyCategory;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.swing.Buffer;
import org.jwildfire.swing.NonHDRImageBufferComboBoxEditor;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class AlphaComposeTransformer extends Mesh2DTransformer {
  public enum HAlignment {
    OFF, CENTRE, LEFT, RIGHT
  }

  public enum VAlignment {
    OFF, CENTRE, TOP, BOTTOM
  }

  public enum Genlock {
    NONE, COLOR, IN_RANGE, OUT_RANGE
  }

  @Property(category = PropertyCategory.PRIMARY, description = "Image to put in foreground (background image is received from the input channel)", editorClass = NonHDRImageBufferComboBoxEditor.class)
  private Buffer foreground;
  private SimpleImage foregroundImage; // Alternative way to specify the foreground image directly
  @Property(category = PropertyCategory.PRIMARY, description = "Image which holds the alpha channel information", editorClass = NonHDRImageBufferComboBoxEditor.class)
  private Buffer alphaChannel;
  private SimpleImage alphaChannelImage; // Alternative way to specify the foreground image directly
  @Property(category = PropertyCategory.SECONDARY, description = "Left offset of the foreground image")
  private int fgLeft = 0;
  @Property(category = PropertyCategory.SECONDARY, description = "Top offset of the foreground image")
  private int fgTop = 0;
  @Property(category = PropertyCategory.SECONDARY, description = "Left offset of the alpha image")
  private int alphaLeft = 0;
  @Property(category = PropertyCategory.SECONDARY, description = "Top offset of the alpha image")
  private int alphaTop = 0;
  @Property(category = PropertyCategory.PRIMARY, description = "Horizontal alignment of the foreground image")
  private HAlignment fgHAlign = HAlignment.CENTRE;
  @Property(category = PropertyCategory.PRIMARY, description = "Vertical alignment of the foreground image")
  private VAlignment fgVAlign = VAlignment.CENTRE;
  @Property(category = PropertyCategory.PRIMARY, description = "Horizontal alignment of the alpha image")
  private HAlignment alphaHAlign = HAlignment.CENTRE;
  @Property(category = PropertyCategory.PRIMARY, description = "Vertical alignment of the alpha image")
  private VAlignment alphaVAlign = VAlignment.CENTRE;
  @Property(category = PropertyCategory.SECONDARY, description = "Genlock mode", editorClass = GenlockEditor.class)
  private Genlock genlock = Genlock.NONE;
  @Property(category = PropertyCategory.SECONDARY, description = "Genlock color A")
  protected Color colorA = new Color(0, 0, 0);
  @Property(category = PropertyCategory.SECONDARY, description = "Genlock color B")
  protected Color colorB = new Color(0, 0, 0);

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage bImg = (SimpleImage) pImg;
    SimpleImage fImg = (foregroundImage != null) ? foregroundImage : foreground.getImage();
    if (fImg == bImg)
      fImg = fImg.clone();
    SimpleImage aImg = (alphaChannelImage != null) ? alphaChannelImage : alphaChannel.getImage();
    if (aImg == bImg)
      aImg = aImg.clone();
    Pixel hPixel = new Pixel();
    Pixel bPixel = new Pixel();
    // calculate left and top edge
    int left, top;
    if (this.fgHAlign == HAlignment.CENTRE) {
      left = (bImg.getImageWidth() - fImg.getImageWidth()) / 2;
    }
    else if (this.fgHAlign == HAlignment.LEFT) {
      left = 0;
    }
    else if (this.fgHAlign == HAlignment.RIGHT) {
      left = bImg.getImageWidth() - fImg.getImageWidth();
    }
    else {
      left = this.fgLeft;
    }

    if (this.fgVAlign == VAlignment.CENTRE) {
      top = (bImg.getImageHeight() - fImg.getImageHeight()) / 2;
    }
    else if (this.fgVAlign == VAlignment.TOP) {
      top = 0;
    }
    else if (this.fgVAlign == VAlignment.BOTTOM) {
      top = bImg.getImageHeight() - fImg.getImageHeight();
    }
    else {
      top = this.fgTop;
    }
    // 
    // calculate alhpa left and top edge
    int aleft, atop;
    if (this.alphaHAlign == HAlignment.CENTRE) {
      aleft = (bImg.getImageWidth() - aImg.getImageWidth()) / 2;
    }
    else if (this.alphaHAlign == HAlignment.LEFT) {
      aleft = 0;
    }
    else if (this.alphaHAlign == HAlignment.RIGHT) {
      aleft = bImg.getImageWidth() - aImg.getImageWidth();
    }
    else {
      aleft = this.alphaLeft;
    }

    if (this.alphaVAlign == VAlignment.CENTRE) {
      atop = (bImg.getImageHeight() - aImg.getImageHeight()) / 2;
    }
    else if (this.alphaVAlign == VAlignment.TOP) {
      atop = 0;
    }
    else if (this.alphaVAlign == VAlignment.BOTTOM) {
      atop = bImg.getImageHeight() - aImg.getImageHeight();
    }
    else {
      atop = this.alphaTop;
    }
    // calculate affected region 
    int hsize = 0, vsize = 0;
    int bgleft = 0, bgtop = 0;
    int sleft = 0, stop = 0;
    int swidth = fImg.getImageWidth();
    int sheight = fImg.getImageHeight();
    int bgwidth = bImg.getImageWidth();
    int bgheight = bImg.getImageHeight();
    /* case 1 */
    if ((left >= 0) && (top >= 0)) {
      if ((left >= bgwidth) || (top >= bgheight))
        return;

      hsize = bgwidth - left;
      if (hsize > swidth)
        hsize = swidth;
      vsize = bgheight - top;
      if (vsize > sheight)
        vsize = sheight;
      bgtop = top;
      bgleft = left;
      sleft = 0;
      stop = 0;
    }

    /* case 2 */
    else if ((left < 0) && (top >= 0)) {
      if ((left <= (0 - swidth)) || (top >= bgheight))
        return;

      hsize = swidth + left;
      if (hsize > bgwidth)
        hsize = bgwidth;
      vsize = bgheight - top;
      if (vsize > sheight)
        vsize = sheight;
      bgtop = top;
      bgleft = 0;
      sleft = 0 - left;
      stop = 0;
    }
    /* case 3 */
    else if ((left >= 0) && (top < 0)) {
      if ((left >= bgwidth) || (top <= (0 - sheight)))
        return;
      hsize = bgwidth - left;
      if (hsize > swidth)
        hsize = swidth;
      vsize = sheight + top;
      if (vsize > bgheight)
        vsize = bgheight;
      bgtop = 0;
      bgleft = left;
      stop = 0 - top;
      sleft = 0;
    }
    /* case 4 */
    else if ((left < 0) && (top < 0)) {
      if ((left <= (0 - swidth)) || (top <= (0 - sheight)))
        return;
      hsize = swidth + left;
      if (hsize > bgwidth)
        hsize = bgwidth;
      vsize = sheight + top;
      if (vsize > bgheight)
        vsize = bgheight;
      bgtop = 0;
      bgleft = 0;
      stop = 0 - top;
      sleft = 0 - left;
    }
    // Genlock colors
    int credA = this.colorA.getRed();
    int cgreenA = this.colorA.getGreen();
    int cblueA = this.colorA.getBlue();
    int credB = this.colorB.getRed();
    int cgreenB = this.colorB.getGreen();
    int cblueB = this.colorB.getBlue();
    {
      int tc;
      if (credA > credB) {
        tc = credA;
        credA = credB;
        credB = tc;
      }
      if (cgreenA > cgreenB) {
        tc = cgreenA;
        cgreenA = cgreenB;
        cgreenB = tc;
      }
      if (cblueA > cblueB) {
        tc = cblueA;
        cblueA = cblueB;
        cblueB = tc;
      }
    }
    //
    if (this.genlock == Genlock.NONE) {
      for (int i = 0; i < vsize; i++) {
        for (int j = 0; j < hsize; j++) {
          hPixel.setARGBValue(fImg.getARGBValue(sleft + j, stop + i));
          int bgX = bgleft + j;
          int bgY = bgtop + i;
          int aX = bgX - aleft;
          int aY = bgY - atop;
          int mix = aImg.getRValueIgnoreBounds(aX, aY);
          int m1 = 255 - mix;
          int m2 = mix;
          bPixel.setARGBValue(bImg.getARGBValue(bgX, bgY));
          int r = ((int) ((int) bPixel.r * m1) + (int) ((int) hPixel.r) * m2) / (int) 255;
          int g = ((int) ((int) bPixel.g * m1) + (int) ((int) hPixel.g) * m2) / (int) 255;
          int b = ((int) ((int) bPixel.b * m1) + (int) ((int) hPixel.b) * m2) / (int) 255;
          bImg.setRGB(bgX, bgY, r, g, b);
        }
      }
    }
    else if (this.genlock == Genlock.COLOR) {
      for (int i = 0; i < vsize; i++) {
        for (int j = 0; j < hsize; j++) {
          hPixel.setARGBValue(fImg.getARGBValue(sleft + j, stop + i));
          if ((hPixel.r != credA) || (hPixel.g != cgreenA) || (hPixel.b != cblueA)) {
            int bgX = bgleft + j;
            int bgY = bgtop + i;
            int aX = bgX - aleft;
            int aY = bgY - atop;
            int mix = aImg.getRValueIgnoreBounds(aX, aY);
            int m1 = 255 - mix;
            int m2 = mix;
            bPixel.setARGBValue(bImg.getARGBValue(bgX, bgY));
            int r = ((int) ((int) bPixel.r * m1) + (int) ((int) hPixel.r) * m2) / (int) 255;
            int g = ((int) ((int) bPixel.g * m1) + (int) ((int) hPixel.g) * m2) / (int) 255;
            int b = ((int) ((int) bPixel.b * m1) + (int) ((int) hPixel.b) * m2) / (int) 255;
            bImg.setRGB(bgX, bgY, r, g, b);
          }
        }
      }
    }
    else if (this.genlock == Genlock.IN_RANGE) {
      for (int i = 0; i < vsize; i++) {
        for (int j = 0; j < hsize; j++) {
          hPixel.setARGBValue(fImg.getARGBValue(sleft + j, stop + i));
          if (((hPixel.r < credA) || (hPixel.r > credB))
              && ((hPixel.g < cgreenA) || (hPixel.g > cgreenB))
              && ((hPixel.b < cblueA) || (hPixel.b > cblueB))) {
            int bgX = bgleft + j;
            int bgY = bgtop + i;
            int aX = bgX - aleft;
            int aY = bgY - atop;
            int mix = aImg.getRValueIgnoreBounds(aX, aY);
            int m1 = 255 - mix;
            int m2 = mix;
            bPixel.setARGBValue(bImg.getARGBValue(bgX, bgY));
            int r = ((int) ((int) bPixel.r * m1) + (int) ((int) hPixel.r) * m2) / (int) 255;
            int g = ((int) ((int) bPixel.g * m1) + (int) ((int) hPixel.g) * m2) / (int) 255;
            int b = ((int) ((int) bPixel.b * m1) + (int) ((int) hPixel.b) * m2) / (int) 255;
            bImg.setRGB(bgX, bgY, r, g, b);
          }
        }
      }
    }
    else if (this.genlock == Genlock.OUT_RANGE) {
      for (int i = 0; i < vsize; i++) {
        for (int j = 0; j < hsize; j++) {
          hPixel.setARGBValue(fImg.getARGBValue(sleft + j, stop + i));
          if (((hPixel.r >= credA) && (hPixel.r <= credB))
              && ((hPixel.g >= cgreenA) && (hPixel.g <= cgreenB))
              && ((hPixel.b >= cblueA) && (hPixel.b <= cblueB))) {
            int bgX = bgleft + j;
            int bgY = bgtop + i;
            int aX = bgX - aleft;
            int aY = bgY - atop;
            int mix = aImg.getRValueIgnoreBounds(aX, aY);
            int m1 = 255 - mix;
            int m2 = mix;
            bPixel.setARGBValue(bImg.getARGBValue(bgX, bgY));
            int r = ((int) ((int) bPixel.r * m1) + (int) ((int) hPixel.r) * m2) / (int) 255;
            int g = ((int) ((int) bPixel.g * m1) + (int) ((int) hPixel.g) * m2) / (int) 255;
            int b = ((int) ((int) bPixel.b * m1) + (int) ((int) hPixel.b) * m2) / (int) 255;
            bImg.setRGB(bgX, bgY, r, g, b);
          }
        }
      }
    }

  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    fgLeft = 0;
    fgTop = 0;
    fgHAlign = HAlignment.CENTRE;
    fgVAlign = VAlignment.CENTRE;
    alphaLeft = 0;
    alphaTop = 0;
    alphaHAlign = HAlignment.CENTRE;
    alphaVAlign = VAlignment.CENTRE;
    genlock = Genlock.NONE;
    colorA = new Color(0, 0, 0);
    colorB = new Color(0, 0, 0);
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

  public static class GenlockEditor extends ComboBoxPropertyEditor {
    public GenlockEditor() {
      super();
      setAvailableValues(new Genlock[] { Genlock.NONE, Genlock.COLOR, Genlock.IN_RANGE,
          Genlock.OUT_RANGE });
    }
  }

  public void setForegroundImage(SimpleImage pForegroundImage) {
    foregroundImage = pForegroundImage;
  }

  public void setAlphaChannelImage(SimpleImage pAlphaChannelImage) {
    alphaChannelImage = pAlphaChannelImage;
  }

  public Buffer getForeground() {
    return foreground;
  }

  public void setForeground(Buffer foreground) {
    this.foreground = foreground;
  }

  public Buffer getAlphaChannel() {
    return alphaChannel;
  }

  public void setAlphaChannel(Buffer alphaChannel) {
    this.alphaChannel = alphaChannel;
  }

  public int getFgLeft() {
    return fgLeft;
  }

  public void setFgLeft(int fgLeft) {
    this.fgLeft = fgLeft;
  }

  public int getFgTop() {
    return fgTop;
  }

  public void setFgTop(int fgTop) {
    this.fgTop = fgTop;
  }

  public int getAlphaLeft() {
    return alphaLeft;
  }

  public void setAlphaLeft(int alphaLeft) {
    this.alphaLeft = alphaLeft;
  }

  public int getAlphaTop() {
    return alphaTop;
  }

  public void setAlphaTop(int alphaTop) {
    this.alphaTop = alphaTop;
  }

  public HAlignment getFgHAlign() {
    return fgHAlign;
  }

  public void setFgHAlign(HAlignment fgHAlign) {
    this.fgHAlign = fgHAlign;
  }

  public VAlignment getFgVAlign() {
    return fgVAlign;
  }

  public void setFgVAlign(VAlignment fgVAlign) {
    this.fgVAlign = fgVAlign;
  }

  public HAlignment getAlphaHAlign() {
    return alphaHAlign;
  }

  public void setAlphaHAlign(HAlignment alphaHAlign) {
    this.alphaHAlign = alphaHAlign;
  }

  public VAlignment getAlphaVAlign() {
    return alphaVAlign;
  }

  public void setAlphaVAlign(VAlignment alphaVAlign) {
    this.alphaVAlign = alphaVAlign;
  }

  public Genlock getGenlock() {
    return genlock;
  }

  public void setGenlock(Genlock genlock) {
    this.genlock = genlock;
  }

  public Color getColorA() {
    return colorA;
  }

  public void setColorA(Color colorA) {
    this.colorA = colorA;
  }

  public Color getColorB() {
    return colorB;
  }

  public void setColorB(Color colorB) {
    this.colorB = colorB;
  }

}
