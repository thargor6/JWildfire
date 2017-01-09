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
package org.jwildfire.create.tina.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.faclrender.FACLRenderTools;
import org.jwildfire.swing.JWildfire;

import com.l2fprod.common.demo.OutlookBarMain;

@SuppressWarnings("serial")
public class NavigatorInternalFrame extends JInternalFrame {
  private JPanel jContentPane = null;
  private JWildfire desktop;
  private JButton interactiveRendererButton;

  public NavigatorInternalFrame() {
    super();
    initialize();
  }

  private void initialize() {
    this.setSize(120, 400);
    this.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    this.setLocation(new Point(0, 0));
    this.setClosable(false);
    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    this.setIconifiable(true);
    this.setTitle("Navigator");
    this.setVisible(true);
    this.setResizable(true);
    this.setIconifiable(false);
    this.setMaximizable(false);
    this.setContentPane(getJContentPane());
  }

  private JPanel getJContentPane() {
    if (jContentPane == null) {
      jContentPane = new JPanel();
      jContentPane.setLayout(new BorderLayout());
      jContentPane.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      jContentPane.setSize(new Dimension(1097, 617));
      JPanel panel = new JPanel();
      FlowLayout flowLayout = (FlowLayout) panel.getLayout();
      flowLayout.setVgap(2);
      flowLayout.setHgap(2);
      jContentPane.add(panel, BorderLayout.CENTER);
      addButtons(panel);
    }
    return jContentPane;
  }

  private void addButtons(JPanel panel) {
    List<WindowEntry> windows = new ArrayList<>();
    windows.add(new WindowEntry("Editor", "/org/jwildfire/swing/icons/new/brick2.png", TinaInternalFrame.class));
    windows.add(new WindowEntry("IRender", "/org/jwildfire/swing/icons/new/fraqtive.png", InteractiveRendererInternalFrame.class));
    windows.add(new WindowEntry("Browser", "/org/jwildfire/swing/icons/new/application-view-tile.png", FlameBrowserInternalFrame.class));
    windows.add(new WindowEntry("Movies", "/org/jwildfire/swing/icons/new/applications-multimedia.png", EasyMovieMakerInternalFrame.class));
    windows.add(new WindowEntry("Dancing", "/org/jwildfire/swing/icons/new/kipina.png", DancingFlamesInternalFrame.class));
    windows.add(new WindowEntry("Batch", "/org/jwildfire/swing/icons/new/images.png", BatchFlameRendererInternalFrame.class));
    windows.add(new WindowEntry("MutaGen", "/org/jwildfire/swing/icons/new/kdissert.png", MutaGenInternalFrame.class));
    windows.add(new WindowEntry("3DMesh", "/org/jwildfire/swing/icons/new/sports-soccer.png", MeshGenInternalFrame.class));
    if (FACLRenderTools.isFaclRenderAvalailable()) {
      windows.add(new WindowEntry("GPURender", "/org/jwildfire/swing/icons/new/opencl.png", FlamesGPURenderInternalFrame.class));
    }

    for (final WindowEntry window : windows) {
      if (window != null) {
        if (window.getFrameType() != null) {
          JButton button = new JButton(window.getCaption());
          button.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
          button.setIcon(new ImageIcon(OutlookBarMain.class.getResource(window.getIconpath())));
          button.setPreferredSize(new Dimension(100, 28));
          //button.setIconTextGap(0);
          button.setFont(new Font("Dialog", Font.BOLD, 9));

          button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              desktop.toggleInternalFrame(window.getFrameType());
            }
          });

          panel.add(button);
        }
        else if (window.getCaption() != null && !window.getCaption().isEmpty()) {
          JLabel label = new JLabel();
          label.setPreferredSize(new Dimension(100, 28));
          label.setText(window.getCaption());
          label.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
          panel.add(label);
        }
      }
    }
  }

  private static class WindowEntry {
    private final String caption;
    private final String iconpath;
    private final Class<? extends JInternalFrame> frameType;

    public WindowEntry(String caption, String iconpath, Class<? extends JInternalFrame> frameType) {
      super();
      this.caption = caption;
      this.iconpath = iconpath;
      this.frameType = frameType;
    }

    public String getCaption() {
      return caption;
    }

    public String getIconpath() {
      return iconpath;
    }

    public Class<? extends JInternalFrame> getFrameType() {
      return frameType;
    }

  }

  public void setDesktop(JWildfire desktop) {
    this.desktop = desktop;
  }

  public JButton getInteractiveRendererButton() {
    return interactiveRendererButton;
  }
}
