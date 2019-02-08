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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.jwildfire.image.FastHDRTonemapper;
import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.transform.Mesh3D;

public class Buffer {
  private JMenuItem saveMenuItem = null;
  private JMenuItem exitMenuItem = null;
  private JMenuBar mainJMenuBar = null;
  private JMenu fileMenu = null;

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
  private JFrame frame;

  public JFrame getFrame() {
    return frame;
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

  public Buffer(JFrame pRootFrame, int pFrame, SimpleImage pSimpleImage) {
    this(pRootFrame, getFrameName(pFrame), pSimpleImage);
    bufferType = BufferType.RENDERED_FRAME;
  }

  public Buffer(JFrame pRootFrame, String pName, SimpleImage pSimpleImage) {
    img = pSimpleImage;
    bufferType = BufferType.IMAGE;
    name = pName;
    addImageBuffer(pRootFrame);
  }

  private void addImageBuffer(JFrame pRootFrame) {
    final int TRANSFORMERS_WIDTH = 300;
    final int X_BORDER = 5 + 5 + TRANSFORMERS_WIDTH; // left + right border
    final int Y_BORDER = 30 + 5; // top + bottom border

    int imageWidth = img.getImageWidth();
    int imageHeight = img.getImageHeight();

    int maxWidth, maxHeight;
    if (pRootFrame != null) {
      maxWidth = pRootFrame.getWidth() - X_BORDER;
      maxHeight = pRootFrame.getHeight() - Y_BORDER;
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

    if (pRootFrame != null) {
      pnl = new ImagePanel(img, 0, 0, panelWidth);

      pnl.setLayout(null);

      frame = new JFrame();

      frame.setJMenuBar(getMainJMenuBar());

      frame.setTitle(getTitle(panelWidth));
      frame.setResizable(true);

      frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

      frame.setVisible(true);
      frame.add(pnl);
      frame.pack();



      frame.setLocation(30, 20);
      
      final int ALT_HINSET = 5 + 5;  // left + right insets to use if actual ones not set
      final int ALT_VINSET = 25 + 5;  // top + bottom insets to use if actual ones not set
      final int hInsets = (frame.getInsets().left == 0) ? ALT_HINSET : frame.getInsets().left +  frame.getInsets().right;
      final int vInsets = ((frame.getInsets().top == 0) ? ALT_VINSET : frame.getInsets().top +  frame.getInsets().bottom) + getMainJMenuBar().getHeight();
      frame.setSize(panelWidth + hInsets, (int)((double)panelWidth * (double)imageHeight / (double)imageWidth+0.5) +  vInsets);

      //pRootFrame.add(frame, null);
      frame.addComponentListener(new ComponentListener() {
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
          int windowWidth = newSize.width - hInsets;
          int newWidth;
          if (Math.abs(windowWidth - imgWidth) < imgWidth * 0.03)
            newWidth = imgWidth;
          else
            newWidth = windowWidth;
          pnl.setImage(img, 0, 0, newWidth);
          frame.setTitle(getTitle(newWidth));
        }

        @Override
        public void componentShown(ComponentEvent arg0) {
          // empty
        }
      });
      frame.toFront();
      pRootFrame.repaint();
    }
  }

  public Buffer(JFrame pRootFrame, String pName, SimpleHDRImage pSimpleHDRImage) {
    hdrImg = pSimpleHDRImage;
    img = new FastHDRTonemapper().renderImage(pSimpleHDRImage);
    bufferType = BufferType.HDR_IMAGE;
    name = pName;
    addImageBuffer(pRootFrame);
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

  public Buffer(JFrame pRootFrame, String pName, Mesh3D pMesh3D, SimpleImage pPreviewImage) {
    img = pPreviewImage;
    mesh3D = pMesh3D;
    int panelWidth = img.getImageWidth();
    pnl = new ImagePanel(img, 0, 0, panelWidth);
    pnl.setLayout(null);

    bufferType = BufferType.MESH3D;
    name = pName;

    if (pRootFrame != null) {
      frame = new JFrame();

      frame.setTitle(pName + " (" + mesh3D.getPCount() + " points, " + mesh3D.getFCount()
          + " faces)");
      frame.setResizable(true);

      frame.setVisible(true);
      frame.setContentPane(pnl);
      frame.pack();

      pRootFrame.add(frame, null);
      frame.toFront();
      pRootFrame.repaint();
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
    frame = null;
  }

  private JMenuBar getMainJMenuBar() {
    if (mainJMenuBar == null) {
      mainJMenuBar = new JMenuBar();
      mainJMenuBar.add(getFileMenu());
    }
    return mainJMenuBar;
  }

  private JMenu getFileMenu() {
    if (fileMenu == null) {
      fileMenu = new JMenu();
      fileMenu.setText("File");
      fileMenu.add(getSaveMenuItem());
      fileMenu.add(getExitMenuItem());
    }
    return fileMenu;
  }

  private JMenuItem getExitMenuItem() {
    if (exitMenuItem == null) {
      exitMenuItem = new JMenuItem();
      exitMenuItem.setText("Close");
      exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
              Event.CTRL_MASK, true));
      exitMenuItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        }
      });
    }
    return exitMenuItem;
  }

  private JMenuItem getSaveMenuItem() {
    if (saveMenuItem == null) {
      saveMenuItem = new JMenuItem();
      saveMenuItem.setText("Save image...");
      saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
              Event.CTRL_MASK, true));
      saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          JWildfire.getInstance().saveImageBuffer(frame);
        }
      });
    }
    return saveMenuItem;
  }

}
