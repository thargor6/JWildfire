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
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.StringReader;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.jwildfire.base.Tools;
import org.jwildfire.image.SimpleImage;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGUniverse;

public class SvgFilePreview extends JPanel implements PropertyChangeListener {
  private static final long serialVersionUID = 1L;
  private final JFileChooser fileChooser;

  private ImageIcon currThumbnail = null;
  private File currFile = null;

  private static final int BUTTON_WIDTH = 100;
  private static final int BUTTON_HEIGHT = 24;

  public SvgFilePreview(JFileChooser pFileChooser) {
    fileChooser = pFileChooser;
    Dimension outerSize = new Dimension(192, 120 + BUTTON_HEIGHT);
    Dimension innerSize = new Dimension(192, 120);
    Dimension buttonSize = new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT);
    setPreferredSize(outerSize);
    setSize(outerSize);
    pFileChooser.addPropertyChangeListener(this);
    PreviewPanel previewPnl = new PreviewPanel();
    previewPnl.setPreferredSize(innerSize);
    previewPnl.setSize(innerSize);
    JButton delButton = new JButton();
    delButton.setPreferredSize(buttonSize);
    delButton.setLocation(0, 120);
    delButton.setText("Delete");
    delButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        deleteFile();
      }
    });
    add(previewPnl);
    add(delButton);
  }

  protected void deleteFile() {
    if (currFile != null && currFile.exists() && StandardDialogs.confirm(this, "Do you really want to permanently remove this file?")) {
      currFile.delete();
      fileChooser.rescanCurrentDirectory();
      fileChooser.setSelectedFile(null);
      fileChooser.rescanCurrentDirectory();
      File selFile = fileChooser.getSelectedFile();
      propertyChange(new PropertyChangeEvent(this, JFileChooser.SELECTED_FILE_CHANGED_PROPERTY, currFile, selFile));
    }
  }

  public void createThumbnail() {
    if (currFile == null) {
      currThumbnail = null;
      return;
    }
    try {
      if (currFile.exists()) {

        SVGUniverse svgUniverse = new SVGUniverse();
        String svg = Tools.readUTF8Textfile(currFile.getAbsolutePath());
        StringReader reader = new StringReader(svg);
        SVGDiagram diagram = svgUniverse.getDiagram(svgUniverse.loadSVG(reader, "svgImage"));

        int imgWidth = this.getPreferredSize().width;
        int imgHeight = this.getPreferredSize().height - BUTTON_HEIGHT;

        SimpleImage imgMap = new SimpleImage(imgWidth, imgHeight);

        Graphics2D g = imgMap.getBufferedImg().createGraphics();

        double sclX = (double) imgWidth / diagram.getWidth();
        double sclY = (double) imgHeight / diagram.getHeight();
        double scl = sclX < sclY ? sclX : sclY;

        g.scale(scl, scl);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        diagram.render(g);

        currThumbnail = new ImageIcon(imgMap.getBufferedImg());

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

  private class PreviewPanel extends JComponent {
    private static final long serialVersionUID = 1L;

    protected void paintComponent(Graphics g) {
      if (currThumbnail == null) {
        createThumbnail();
      }
      if (currThumbnail != null) {
        currThumbnail.paintIcon(this, g, 0, 0);
      }
    }
  }

}
