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
package org.jwildfire.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import org.jwildfire.image.FastHDRTonemapper;
import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.transform.Mesh3D;

public class Buffer {
  private BufferType bufferType;

  public BufferType getBufferType() {
    return bufferType;
  }

  public String getName() {
    return name;
  }

  public void setName(String pName) {
    name = pName;
  }

  @Override
  public String toString() {
    return getName();
  }

  private SimpleHDRImage hdrImg;
  private SimpleImage img;
  private Mesh3D mesh3D;
  private ImagePanel pnl;
  private JInternalFrame internalFrame;

  public JInternalFrame getInternalFrame() {
    return internalFrame;
  }

  private String name;

  public enum BufferType {
    IMAGE, MESH3D, HDR_IMAGE, RENDERED_FRAME
  };

  private static String getFrameName(int pFrame) {
    String res = String.valueOf(pFrame);
    while (res.length() < 5)
      res = "0" + res;
    return "Frame - " + res;
  }

  public Buffer(JDesktopPane pDesktop, int pFrame, SimpleImage pSimpleImage) {
    this(pDesktop, getFrameName(pFrame), pSimpleImage);
    bufferType = BufferType.RENDERED_FRAME;
  }

  public Buffer(JDesktopPane pDesktop, String pName, SimpleImage pSimpleImage) {
    img = pSimpleImage;
    bufferType = BufferType.IMAGE;
    name = pName;
    addImageBuffer(pDesktop);
  }

  private void addImageBuffer(JDesktopPane pDesktop) {
    final int TRANSFORMERS_WIDTH = 300;
    final int X_BORDER = 5 + 5 + TRANSFORMERS_WIDTH; // left + right border
    final int Y_BORDER = 30 + 5; // top + bottom border

    int imageWidth = img.getImageWidth();
    int imageHeight = img.getImageHeight();

    int maxWidth, maxHeight;
    if (pDesktop != null) {
      maxWidth = pDesktop.getWidth() - X_BORDER;
      maxHeight = pDesktop.getHeight() - Y_BORDER;
    }
    else {
      maxWidth = imageWidth;
      maxHeight = imageHeight;
    }

    int panelWidth;
    if ((imageWidth <= maxWidth) && (imageHeight <= maxHeight))
      panelWidth = imageWidth;
    else if ((imageWidth > maxWidth) && (imageHeight <= maxHeight))
      panelWidth = maxWidth;
    else if ((imageWidth <= maxWidth) && (imageHeight > maxHeight))
      panelWidth = (int) (((double) maxHeight / (double) imageHeight) * (double) imageWidth + 0.5);
    else {
      double sclWidth = (double) maxWidth / (double) imageWidth;
      double sclHeight = (double) maxHeight / (double) imageHeight;
      double scl = (sclWidth < sclHeight) ? sclWidth : sclHeight;
      panelWidth = (int) (scl * (double) imageWidth + 0.5);
    }

    if (pDesktop != null) {
      pnl = new ImagePanel(img, 0, 0, panelWidth);

      pnl.setLayout(null);

      internalFrame = new JInternalFrame();
      internalFrame.setTitle(getTitle(panelWidth));
      internalFrame.setClosable(true);
      internalFrame.setMaximizable(true);
      internalFrame.setIconifiable(true);
      internalFrame.setResizable(true);

      internalFrame.setVisible(true);
      internalFrame.setContentPane(pnl);
      internalFrame.pack();

      pDesktop.add(internalFrame, null);
      internalFrame.addComponentListener(new ComponentListener() {
        @Override
        public void componentHidden(ComponentEvent arg0) {
          // empty
        }

        @Override
        public void componentMoved(ComponentEvent arg0) {
          // empty
        }

        @Override
        public void componentResized(ComponentEvent evt) {
          Component c = (Component) evt.getSource();
          Dimension newSize = c.getSize();
          int imgWidth = img.getImageWidth();
          int windowWidth = newSize.width;
          int newWidth;
          if (Math.abs(windowWidth - imgWidth) < imgWidth * 0.03)
            newWidth = imgWidth;
          else
            newWidth = windowWidth;
          pnl.setImage(img, 0, 0, newWidth);
          internalFrame.setTitle(getTitle(newWidth));
        }

        @Override
        public void componentShown(ComponentEvent arg0) {
          // empty
        }
      });
      internalFrame.toFront();
      pDesktop.repaint();
    }
  }

  public Buffer(JDesktopPane pDesktop, String pName, SimpleHDRImage pSimpleHDRImage) {
    hdrImg = pSimpleHDRImage;
    img = new FastHDRTonemapper().renderImage(pSimpleHDRImage);
    bufferType = BufferType.HDR_IMAGE;
    name = pName;
    addImageBuffer(pDesktop);
  }

  private String getTitle(int pPanelWidth) {
    int sizePercent = (int) (100.0 * (double) pPanelWidth / (double) img.getImageWidth() + 0.5);
    return name + " (" + img.getImageWidth() + " x " + img.getImageHeight() + " - " + sizePercent
        + "%)";
  }

  public SimpleImage getImage() {
    return (bufferType == BufferType.IMAGE) ? img : null;
  }

  public SimpleHDRImage getHDRImage() {
    return (bufferType == BufferType.HDR_IMAGE) ? hdrImg : null;
  }

  public Buffer(JDesktopPane pDesktop, String pName, Mesh3D pMesh3D, SimpleImage pPreviewImage) {
    img = pPreviewImage;
    mesh3D = pMesh3D;
    int panelWidth = img.getImageWidth();
    pnl = new ImagePanel(img, 0, 0, panelWidth);
    pnl.setLayout(null);

    bufferType = BufferType.MESH3D;
    name = pName;

    if (pDesktop != null) {
      internalFrame = new JInternalFrame();

      internalFrame.setTitle(pName + " (" + mesh3D.getPCount() + " points, " + mesh3D.getFCount()
          + " faces)");
      internalFrame.setClosable(true);
      internalFrame.setMaximizable(true);
      internalFrame.setIconifiable(true);
      internalFrame.setResizable(true);

      internalFrame.setVisible(true);
      internalFrame.setContentPane(pnl);
      internalFrame.pack();

      pDesktop.add(internalFrame, null);
      internalFrame.toFront();
      pDesktop.repaint();
    }
  }

  public Mesh3D getMesh3D() {
    return (bufferType == BufferType.MESH3D) ? mesh3D : null;
  }

  public void flush() {
    if (img.getBufferedImg() != null) {
      img.getBufferedImg().flush();
    }
    img.setBufferedImage(null, 0, 0);
    mesh3D = null;
    pnl = null;
    internalFrame = null;
  }
}
