/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2012 Andreas Maschke

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

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.animate.FlameMovie;
import org.jwildfire.create.tina.animate.FlameMoviePart;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.FlameMovieReader;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;

public class JWFMovieFilePreview extends JComponent implements PropertyChangeListener {
  private static final long serialVersionUID = 1L;
  private final Prefs prefs;

  private ImageIcon currThumbnail = null;
  private File currFile = null;

  public JWFMovieFilePreview(JFileChooser pFileChooser, Prefs pPrefs) {
    Dimension size = new Dimension(192, 120);
    setPreferredSize(size);
    setSize(size);
    pFileChooser.addPropertyChangeListener(this);
    prefs = pPrefs;
  }

  public void createThumbnail() {
    if (currFile == null) {
      currThumbnail = null;
      return;
    }
    try {
      if (currFile.exists()) {
        FlameMovie movie = new FlameMovieReader(prefs).readMovie(currFile.getAbsolutePath());
        Flame flame = null;
        if (movie != null) {
          for (FlameMoviePart part : movie.getParts()) {
            if (part.getFlame() != null) {
              flame = part.getFlame();
              break;
            }
          }
        }
        if (flame != null) {
          int imgWidth = this.getPreferredSize().width;
          int imgHeight = this.getPreferredSize().height;

          double wScl = (double) imgWidth / (double) flame.getWidth();
          double hScl = (double) imgHeight / (double) flame.getHeight();
          flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
          flame.setWidth(imgWidth);
          flame.setHeight(imgHeight);

          FlameRenderer renderer = new FlameRenderer(flame, prefs, false, true);
          renderer.setProgressUpdater(null);
          flame.setSampleDensity(50);
          flame.setSpatialFilterRadius(0.0);
          RenderInfo info = new RenderInfo(imgWidth, imgHeight, RenderMode.PREVIEW);
          RenderedFlame res = renderer.renderFlame(info);
          currThumbnail = new ImageIcon(res.getImage().getBufferedImg());
        }
      }
    }
    catch (Exception ex) {
      currThumbnail = null;
      if (ex.getCause() != null) {
        ex.getCause().printStackTrace();
      }
      else {
        ex.printStackTrace();
      }
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
