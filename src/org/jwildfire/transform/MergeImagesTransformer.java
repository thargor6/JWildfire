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
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.swing.Buffer;
import org.jwildfire.swing.Buffer.BufferType;
import org.jwildfire.swing.NonHDRImageBufferComboBoxEditor;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class MergeImagesTransformer extends Transformer {
  public enum Mode {
    HORIZONTAL, VERTICAL
  }

  @Property(description = "2nd image", editorClass = NonHDRImageBufferComboBoxEditor.class)
  private Buffer image2;

  @Property(description = "Merge mode", editorClass = ModeEditor.class)
  private Mode mode = Mode.HORIZONTAL;

  @Override
  protected void performImageTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    int width1 = pImg.getImageWidth();
    int height1 = pImg.getImageHeight();
    int width2 = image2.getImage().getImageWidth();
    int height2 = image2.getImage().getImageHeight();

    int bgWidth, bgHeight;
    if (this.mode == Mode.HORIZONTAL) {
      bgWidth = width1 + width2;
      bgHeight = (height1 > height2) ? height1 : height2;
    }
    else {
      bgWidth = (width1 > width2) ? width1 : width2;
      bgHeight = height1 + height2;
    }
    SimpleImage bgImg = new SimpleImage(bgWidth, bgHeight);
    if (this.mode == Mode.HORIZONTAL) {
      {
        ComposeTransformer cT = new ComposeTransformer();
        cT.setForegroundImage(img);
        cT.setHAlign(ComposeTransformer.HAlignment.OFF);
        cT.setLeft(0);
        cT.setVAlign(ComposeTransformer.VAlignment.CENTRE);
        cT.transformImage(bgImg);
      }
      {
        ComposeTransformer cT = new ComposeTransformer();
        cT.setForegroundImage(image2.getImage());
        cT.setHAlign(ComposeTransformer.HAlignment.OFF);
        cT.setLeft(width1);
        cT.setVAlign(ComposeTransformer.VAlignment.CENTRE);
        cT.transformImage(bgImg);
      }
    }
    else {
      {
        ComposeTransformer cT = new ComposeTransformer();
        cT.setForegroundImage(img);
        cT.setHAlign(ComposeTransformer.HAlignment.CENTRE);
        cT.setVAlign(ComposeTransformer.VAlignment.OFF);
        cT.setTop(0);
        cT.transformImage(bgImg);
      }
      {
        ComposeTransformer cT = new ComposeTransformer();
        cT.setForegroundImage(image2.getImage());
        cT.setHAlign(ComposeTransformer.HAlignment.CENTRE);
        cT.setVAlign(ComposeTransformer.VAlignment.OFF);
        cT.setTop(height1);
        cT.transformImage(bgImg);
      }
    }
    img.setBufferedImage(bgImg.getBufferedImg(), bgWidth, bgHeight);
  }

  @Override
  public boolean supports3DOutput() {
    return false;
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    mode = Mode.HORIZONTAL;
  }

  @Override
  public boolean acceptsInputBufferType(BufferType pBufferType) {
    return pBufferType.equals(BufferType.IMAGE);
  }

  public static class ModeEditor extends ComboBoxPropertyEditor {
    public ModeEditor() {
      super();
      setAvailableValues(new Mode[] { Mode.HORIZONTAL, Mode.VERTICAL });
    }
  }

  public Buffer getImage2() {
    return image2;
  }

  public void setImage2(Buffer image2) {
    this.image2 = image2;
  }

  public Mode getMode() {
    return mode;
  }

  public void setMode(Mode mode) {
    this.mode = mode;
  }

}
