/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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
package org.jwildfire.swing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import org.jwildfire.image.SimpleImage;

public class ImagePanel extends JPanel {
  private static final long serialVersionUID = 1L;
  private SimpleImage simpleImage;
  private int x, y, width, height;

  public ImagePanel(SimpleImage pSimpleImage, int pX, int pY, int pWidth) {
    super();
    setImage(pSimpleImage, pX, pY, pWidth);
  }

  protected void drawImage(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
        RenderingHints.VALUE_RENDER_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    g.drawImage(simpleImage.getBufferedImg(), x, y, width, height, this);
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    drawImage(g);
  }

  public void setImage(SimpleImage pSimpleImage) {
    SimpleImage img = preProcessImage(pSimpleImage);
    setImage(img, 0, 0, img.getImageWidth());
  }

  protected SimpleImage preProcessImage(SimpleImage pSimpleImage) {
    return pSimpleImage;
  }

  public void replaceImage(SimpleImage pSimpleImage) {
    SimpleImage img = preProcessImage(pSimpleImage);
    simpleImage.setBufferedImage(img.getBufferedImg(), simpleImage.getImageWidth(), simpleImage.getImageHeight());
  }

  public void setImage(SimpleImage pSimpleImage, int pX, int pY, int pWidth) {
    simpleImage = pSimpleImage;
    x = pX;
    y = pY;
    width = pWidth;
    height = simpleImage.getScaledHeight(width);
    setBounds(x, y, width, height);
  }

  public void setImage(SimpleImage pSimpleImage, int pX, int pY, int pWidth, int pHeight) {
    simpleImage = pSimpleImage;
    x = pX;
    y = pY;
    width = pWidth;
    height = pHeight;
    setBounds(x, y, width, height);
  }

  public int getImageWidth() {
    return simpleImage != null ? simpleImage.getImageWidth() : 0;
  }

  public int getImageHeight() {
    return simpleImage != null ? simpleImage.getImageHeight() : 0;
  }

  public SimpleImage getImage() {
    return simpleImage;
  }

}
