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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import org.jwildfire.image.FastHDRTonemapper;
import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageReader;
import org.jwildfire.transform.ScaleAspect;
import org.jwildfire.transform.ScaleTransformer;
import org.jwildfire.transform.ScaleTransformer.Unit;

public class ImageFilePreview extends JComponent implements PropertyChangeListener {
  private static final long serialVersionUID = 1L;
  private static final int THUMBNAIL_WIDTH = 192;
  private static final int THUMBNAIL_HEIGHT = 120;

  private ImageIcon currThumbnail = null;
  private File currFile = null;

  public ImageFilePreview(JFileChooser pFileChooser) {
    setPreferredSize(new Dimension(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT));
    pFileChooser.addPropertyChangeListener(this);
  }

  public void createThumbnail() {
    if (currFile == null) {
      currThumbnail = null;
      return;
    }
    try {
      if (currFile.exists()) {
        String fileExt = null;
        {
          String filename = currFile.getName();
          int p = filename.lastIndexOf(".");
          if (p >= 0 && p < filename.length() - 2) {
            fileExt = filename.substring(p + 1, filename.length());
          }
        }
        if ("hdr".equalsIgnoreCase(fileExt)) {
          SimpleHDRImage hdrImg = new ImageReader(this).loadHDRImage(currFile.getAbsolutePath());
          SimpleImage img = new FastHDRTonemapper().renderImage(hdrImg);
          ScaleTransformer scaleT = new ScaleTransformer();
          scaleT.setScaleWidth(THUMBNAIL_WIDTH);
          scaleT.setAspect(ScaleAspect.KEEP_WIDTH);
          scaleT.setUnit(Unit.PIXELS);
          scaleT.transformImage(img);
          currThumbnail = new ImageIcon(img.getBufferedImg(), currFile.getName());
        }
        else {
          ImageIcon tmpIcon = new ImageIcon(currFile.getPath());
          if (tmpIcon != null) {
            if (tmpIcon.getIconWidth() > THUMBNAIL_WIDTH) {
              currThumbnail = new ImageIcon(tmpIcon.getImage().
                  getScaledInstance(THUMBNAIL_WIDTH, -1,
                      Image.SCALE_DEFAULT));
            }
            else {
              currThumbnail = tmpIcon;
            }
          }
        }
      }
    }
    catch (Throwable ex) {
      ex.printStackTrace();
    }
  }

  public void propertyChange(PropertyChangeEvent e) {
    boolean update = false;
    String prop = e.getPropertyName();
    if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
      currFile = null;
      update = true;
    }
    else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
      currFile = (File) e.getNewValue();
      update = true;
    }
    if (update) {
      currThumbnail = null;
      if (isShowing()) {
        createThumbnail();
        repaint();
      }
    }
  }

  protected void paintComponent(Graphics g) {
    if (currThumbnail == null) {
      createThumbnail();
    }
    if (currThumbnail != null) {
      int x = getWidth() / 2 - currThumbnail.getIconWidth() / 2;
      int y = getHeight() / 2 - currThumbnail.getIconHeight() / 2;
      if (y < 0) {
        y = 0;
      }
      if (x < 5) {
        x = 5;
      }
      currThumbnail.paintIcon(this, g, x, y);
    }
  }
}
