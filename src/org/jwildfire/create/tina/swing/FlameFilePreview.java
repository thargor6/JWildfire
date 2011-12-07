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
package org.jwildfire.create.tina.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.Flam3Reader;
import org.jwildfire.create.tina.render.AffineZStyle;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.image.SimpleImage;

public class FlameFilePreview extends JComponent implements PropertyChangeListener {
  private static final long serialVersionUID = 1L;
  private final Prefs prefs;

  private ImageIcon currThumbnail = null;
  private File currFile = null;

  public FlameFilePreview(JFileChooser pFileChooser, Prefs pPrefs) {
    setPreferredSize(new Dimension(160, 100));
    pFileChooser.addPropertyChangeListener(this);
    prefs = pPrefs;
  }

  public void createThumbnail() {
    if (currFile == null) {
      currThumbnail = null;
      return;
    }
    try {
      List<Flame> flames = new Flam3Reader().readFlames(currFile.getAbsolutePath());
      Flame flame = flames.get(0);
      int imgWidth = this.getPreferredSize().width;
      int imgHeight = this.getPreferredSize().height;

      double wScl = (double) imgWidth / (double) flame.getWidth();
      double hScl = (double) imgHeight / (double) flame.getHeight();
      flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
      flame.setWidth(imgWidth);
      flame.setHeight(imgHeight);

      FlameRenderer renderer = new FlameRenderer();
      renderer.setProgressUpdater(null);
      flame.setSampleDensity(50);
      flame.setSpatialFilterRadius(0.0);
      flame.setSpatialOversample(1);
      flame.setColorOversample(2);
      renderer.setAffineZStyle(AffineZStyle.FLAT);
      SimpleImage img = new SimpleImage(imgWidth, imgHeight);
      renderer.renderFlame(flame, img, prefs.getTinaRenderThreads());

      currThumbnail = new ImageIcon(img.getBufferedImg());

    }
    catch (Exception ex) {
      currThumbnail = null;
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
      currThumbnail.paintIcon(this, g, 0, 0);
    }
  }
}
