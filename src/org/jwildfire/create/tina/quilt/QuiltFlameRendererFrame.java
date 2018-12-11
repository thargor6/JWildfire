/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2018 Andreas Maschke

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
package org.jwildfire.create.tina.quilt;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.swing.JWFNumberField;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.swing.JWildfire;

public class QuiltFlameRendererFrame extends JFrame {
  private TinaController tinaController;
  private static final long serialVersionUID = 1L;
  private JPanel jContentPane = null;
  private JPanel quiltRenderPanel = null;
  private JButton openFlameButton = null;
  private JProgressBar segmentProgressBar = null;
  private JProgressBar totalProgressBar = null;
  private JLabel quiltRenderSegmentProgressLbl = null;
  private JLabel quiltRenderTotalProgressLbl = null;
  private JButton renderButton = null;

  public QuiltFlameRendererFrame() {
    super();
    initialize();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(832, 557);
    this.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    this.setLocation(new Point(200 + JWildfire.DEFAULT_WINDOW_LEFT, 50 + JWildfire.DEFAULT_WINDOW_TOP));
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setTitle("Quilt flame renderer");
    this.setVisible(false);
    this.setResizable(true);
    this.setContentPane(getJContentPane());
  }

  /**
   * This method initializes jContentPane
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJContentPane() {
    if (jContentPane == null) {
      jContentPane = new JPanel();
      jContentPane.setLayout(new BorderLayout());
      jContentPane.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      jContentPane.setSize(new Dimension(1097, 617));
      jContentPane.add(getQuiltRenderPanel(), BorderLayout.CENTER);
    }
    return jContentPane;
  }

  /**
   * This method initializes quiltRenderPanel
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getQuiltRenderPanel() {
    if (quiltRenderPanel == null) {
      quiltRenderPanel = new JPanel();
      quiltRenderPanel.setLayout(new BorderLayout(0, 0));

      JPanel panel_1 = new JPanel();
      panel_1.setBorder(new EmptyBorder(10, 10, 10, 10));
      quiltRenderPanel.add(panel_1);
      panel_1.setLayout(new BorderLayout(0, 0));

      JPanel panel_2 = new JPanel();
      panel_2.setPreferredSize(new Dimension(160, 72));
      panel_1.add(panel_2, BorderLayout.NORTH);
      panel_2.setLayout(null);
      panel_2.add(getOpenFlameButton());

      importFlameFromEditorButton = new JButton();
      importFlameFromEditorButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          tinaController.getQuiltRendererController().importFlameFromEditorButton_clicked();
        }
      });
      importFlameFromEditorButton.setText("From editor");
      importFlameFromEditorButton.setSize(new Dimension(159, 24));
      importFlameFromEditorButton.setPreferredSize(new Dimension(125, 24));
      importFlameFromEditorButton.setMinimumSize(new Dimension(159, 24));
      importFlameFromEditorButton.setMaximumSize(new Dimension(159, 24));
      importFlameFromEditorButton.setFont(new Font("Dialog", Font.BOLD, 10));
      importFlameFromEditorButton.setAlignmentX(0.5f);
      importFlameFromEditorButton.setBounds(0, 47, 159, 24);
      panel_2.add(importFlameFromEditorButton);

      JLabel lblRenderSize = new JLabel();
      lblRenderSize.setText("Render size");
      lblRenderSize.setPreferredSize(new Dimension(110, 22));
      lblRenderSize.setHorizontalAlignment(SwingConstants.RIGHT);
      lblRenderSize.setFont(new Font("Dialog", Font.BOLD, 10));
      lblRenderSize.setBounds(376, 0, 110, 22);
      panel_2.add(lblRenderSize);

      JLabel lblX = new JLabel();
      lblX.setText("x");
      lblX.setPreferredSize(new Dimension(110, 22));
      lblX.setHorizontalAlignment(SwingConstants.RIGHT);
      lblX.setFont(new Font("Dialog", Font.BOLD, 10));
      lblX.setBounds(595, 1, 18, 22);
      panel_2.add(lblX);

      JLabel lblSegmentationLevel = new JLabel();
      lblSegmentationLevel.setText("Segmentation level");
      lblSegmentationLevel.setPreferredSize(new Dimension(110, 22));
      lblSegmentationLevel.setHorizontalAlignment(SwingConstants.RIGHT);
      lblSegmentationLevel.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSegmentationLevel.setBounds(171, 24, 110, 22);
      panel_2.add(lblSegmentationLevel);

      JLabel lblSegmentSize = new JLabel();
      lblSegmentSize.setText("Segment size");
      lblSegmentSize.setPreferredSize(new Dimension(110, 22));
      lblSegmentSize.setHorizontalAlignment(SwingConstants.RIGHT);
      lblSegmentSize.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSegmentSize.setBounds(376, 24, 110, 22);
      panel_2.add(lblSegmentSize);

      outputFilenameEdit = new JTextField();
      outputFilenameEdit.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          if (tinaController != null && tinaController.getQuiltRendererController() != null) {
            tinaController.getQuiltRendererController().refreshPreviewImage();
            tinaController.getQuiltRendererController().enableControls();
          }
        }
      });
      outputFilenameEdit.setPreferredSize(new Dimension(56, 22));
      outputFilenameEdit.setFont(new Font("Dialog", Font.PLAIN, 10));
      outputFilenameEdit.setBounds(286, 48, 438, 22);
      panel_2.add(outputFilenameEdit);

      JLabel lblOutputFilename = new JLabel();
      lblOutputFilename.setText("Output filename");
      lblOutputFilename.setPreferredSize(new Dimension(110, 22));
      lblOutputFilename.setHorizontalAlignment(SwingConstants.RIGHT);
      lblOutputFilename.setFont(new Font("Dialog", Font.BOLD, 10));
      lblOutputFilename.setBounds(171, 48, 110, 22);
      panel_2.add(lblOutputFilename);

      segmentationLevelEdit = new JWFNumberField();
      segmentationLevelEdit.setValueStep(1.0);
      segmentationLevelEdit.setText("");
      segmentationLevelEdit.setSize(new Dimension(100, 24));
      segmentationLevelEdit.setPreferredSize(new Dimension(100, 24));
      segmentationLevelEdit.setOnlyIntegers(true);
      segmentationLevelEdit.setLocation(new Point(584, 2));
      segmentationLevelEdit.setHasMinValue(true);
      segmentationLevelEdit.setMinValue(2.0);
      segmentationLevelEdit.setHasMaxValue(true);
      segmentationLevelEdit.setMaxValue(4096.0);
      segmentationLevelEdit.setFont(new Font("Dialog", Font.PLAIN, 10));
      segmentationLevelEdit.setEditable(true);
      segmentationLevelEdit.setBounds(286, 24, 78, 24);
      segmentationLevelEdit.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getQuiltRendererController() != null) {
            tinaController.getQuiltRendererController().recalcSizes();
          }
        }
      });

      panel_2.add(segmentationLevelEdit);

      renderWidthEdit = new JWFNumberField();
      renderWidthEdit.setValueStep(1.0);
      renderWidthEdit.setText("");
      renderWidthEdit.setSize(new Dimension(100, 24));
      renderWidthEdit.setPreferredSize(new Dimension(100, 24));
      renderWidthEdit.setOnlyIntegers(true);
      renderWidthEdit.setLocation(new Point(584, 2));
      renderWidthEdit.setHasMinValue(true);
      renderWidthEdit.setFont(new Font("Dialog", Font.PLAIN, 10));
      renderWidthEdit.setEditable(true);
      renderWidthEdit.setBounds(491, 0, 99, 24);
      renderWidthEdit.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getQuiltRendererController() != null) {
            tinaController.getQuiltRendererController().recalcSizes();
          }
        }
      });
      panel_2.add(renderWidthEdit);

      renderHeightEdit = new JWFNumberField();
      renderHeightEdit.setValueStep(1.0);
      renderHeightEdit.setText("");
      renderHeightEdit.setSize(new Dimension(100, 24));
      renderHeightEdit.setPreferredSize(new Dimension(100, 24));
      renderHeightEdit.setOnlyIntegers(true);
      renderHeightEdit.setLocation(new Point(584, 2));
      renderHeightEdit.setHasMinValue(true);
      renderHeightEdit.setFont(new Font("Dialog", Font.PLAIN, 10));
      renderHeightEdit.setEditable(true);
      renderHeightEdit.setBounds(625, 0, 99, 24);
      renderHeightEdit.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getQuiltRendererController() != null) {
            tinaController.getQuiltRendererController().recalcSizes();
          }
        }
      });
      panel_2.add(renderHeightEdit);

      segmentWidthEdit = new JWFNumberField();
      segmentWidthEdit.setValueStep(1.0);
      segmentWidthEdit.setText("");
      segmentWidthEdit.setSize(new Dimension(100, 24));
      segmentWidthEdit.setPreferredSize(new Dimension(100, 24));
      segmentWidthEdit.setOnlyIntegers(true);
      segmentWidthEdit.setLocation(new Point(584, 2));
      segmentWidthEdit.setHasMinValue(true);
      segmentWidthEdit.setFont(new Font("Dialog", Font.PLAIN, 10));
      segmentWidthEdit.setEditable(true);
      segmentWidthEdit.setBounds(491, 24, 99, 24);
      panel_2.add(segmentWidthEdit);

      JLabel label = new JLabel();
      label.setText("x");
      label.setPreferredSize(new Dimension(110, 22));
      label.setHorizontalAlignment(SwingConstants.RIGHT);
      label.setFont(new Font("Dialog", Font.BOLD, 10));
      label.setBounds(595, 25, 18, 22);
      panel_2.add(label);

      segmentHeightEdit = new JWFNumberField();
      segmentHeightEdit.setValueStep(1.0);
      segmentHeightEdit.setText("");
      segmentHeightEdit.setSize(new Dimension(100, 24));
      segmentHeightEdit.setPreferredSize(new Dimension(100, 24));
      segmentHeightEdit.setOnlyIntegers(true);
      segmentHeightEdit.setLocation(new Point(584, 2));
      segmentHeightEdit.setHasMinValue(true);
      segmentHeightEdit.setFont(new Font("Dialog", Font.PLAIN, 10));
      segmentHeightEdit.setEditable(true);
      segmentHeightEdit.setBounds(625, 24, 99, 24);
      panel_2.add(segmentHeightEdit);

      qualityEdit = new JWFNumberField();
      qualityEdit.setValueStep(1.0);
      qualityEdit.setText("");
      qualityEdit.setSize(new Dimension(100, 24));
      qualityEdit.setPreferredSize(new Dimension(100, 24));
      qualityEdit.setOnlyIntegers(true);
      qualityEdit.setLocation(new Point(584, 2));
      qualityEdit.setHasMinValue(true);
      qualityEdit.setFont(new Font("Dialog", Font.PLAIN, 10));
      qualityEdit.setEditable(true);
      qualityEdit.setBounds(286, 0, 78, 24);
      qualityEdit.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getQuiltRendererController() != null) {
            tinaController.getQuiltRendererController().recalcSizes();
          }
        }
      });

      panel_2.add(qualityEdit);

      JLabel lblRenderQuality = new JLabel();
      lblRenderQuality.setText("Render quality");
      lblRenderQuality.setPreferredSize(new Dimension(110, 22));
      lblRenderQuality.setHorizontalAlignment(SwingConstants.RIGHT);
      lblRenderQuality.setFont(new Font("Dialog", Font.BOLD, 10));
      lblRenderQuality.setBounds(171, 0, 110, 22);
      panel_2.add(lblRenderQuality);

      importFlameFromClipboardButton = new JButton();
      importFlameFromClipboardButton.setToolTipText("Load flame from clipboard and render");
      importFlameFromClipboardButton.setText("From Clipboard");
      importFlameFromClipboardButton.setPreferredSize(new Dimension(125, 24));
      importFlameFromClipboardButton.setMinimumSize(new Dimension(100, 24));
      importFlameFromClipboardButton.setMaximumSize(new Dimension(32000, 24));
      importFlameFromClipboardButton.setFont(new Font("Dialog", Font.BOLD, 10));
      importFlameFromClipboardButton.setBounds(0, 24, 159, 24);
      importFlameFromClipboardButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          tinaController.getQuiltRendererController().importFlameFromClipboardButton_clicked();
        }
      });

      panel_2.add(importFlameFromClipboardButton);

      JPanel panel_3 = new JPanel();
      panel_3.setPreferredSize(new Dimension(10, 100));
      panel_1.add(panel_3, BorderLayout.SOUTH);
      panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.X_AXIS));
      panel_3.add(getPanel_25());
      panel_3.add(getPanel_26());
      panel_3.add(getRenderButton());
      panel_1.add(getPreviewRootPanel(), BorderLayout.CENTER);
    }
    return quiltRenderPanel;
  }

  private JPanel panel_23;
  private JPanel panel_24;
  private JPanel panel_25;
  private JPanel panel_26;
  private JPanel previewRootPanel;
  private JTextField outputFilenameEdit;
  private JButton importFlameFromEditorButton;
  private JWFNumberField qualityEdit;
  private JWFNumberField renderWidthEdit;
  private JWFNumberField renderHeightEdit;
  private JWFNumberField segmentationLevelEdit;
  private JWFNumberField segmentWidthEdit;
  private JWFNumberField segmentHeightEdit;
  private JButton importFlameFromClipboardButton;

  public JButton getOpenFlameButton() {
    if (openFlameButton == null) {
      openFlameButton = new JButton();
      openFlameButton.setLocation(0, 0);
      openFlameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
      openFlameButton.setSize(new Dimension(159, 24));
      openFlameButton.setMinimumSize(new Dimension(159, 24));
      openFlameButton.setMaximumSize(new Dimension(159, 24));
      openFlameButton.setPreferredSize(new Dimension(125, 24));
      openFlameButton.setText("Open flame...");
      openFlameButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      openFlameButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.getQuiltRendererController().openFlameButton_clicked();
        }
      });
    }
    return openFlameButton;
  }

  public JProgressBar getSegmentProgressBar() {
    if (segmentProgressBar == null) {
      segmentProgressBar = new JProgressBar();
      segmentProgressBar.setValue(0);
      segmentProgressBar.setPreferredSize(new Dimension(568, 21));
      segmentProgressBar.setStringPainted(true);
    }
    return segmentProgressBar;
  }

  public JProgressBar getTotalProgressBar() {
    if (totalProgressBar == null) {
      totalProgressBar = new JProgressBar();
      totalProgressBar.setValue(0);
      totalProgressBar.setPreferredSize(new Dimension(568, 21));
      totalProgressBar.setStringPainted(true);
    }
    return totalProgressBar;
  }

  public JButton getRenderButton() {
    if (renderButton == null) {
      renderButton = new JButton();
      renderButton.setMinimumSize(new Dimension(159, 52));
      renderButton.setMaximumSize(new Dimension(159, 59));
      renderButton.setPreferredSize(new Dimension(159, 52));
      renderButton.setText("Render");
      renderButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      renderButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.getQuiltRendererController().quiltRenderStartButton_clicked();
        }
      });
    }
    return renderButton;
  }

  private JPanel getPanel_23() {
    if (panel_23 == null) {
      panel_23 = new JPanel();
      panel_23.setPreferredSize(new Dimension(10, 28));
      panel_23.setMinimumSize(new Dimension(10, 28));
      panel_23.setMaximumSize(new Dimension(32767, 28));
      panel_23.setLayout(new BoxLayout(panel_23, BoxLayout.X_AXIS));
      quiltRenderSegmentProgressLbl = new JLabel();
      quiltRenderSegmentProgressLbl.setMinimumSize(new Dimension(100, 0));
      panel_23.add(quiltRenderSegmentProgressLbl);
      quiltRenderSegmentProgressLbl.setPreferredSize(new Dimension(100, 22));
      quiltRenderSegmentProgressLbl.setText("Segment progress");
      quiltRenderSegmentProgressLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      panel_23.add(getSegmentProgressBar());
    }
    return panel_23;
  }

  private JPanel getPanel_24() {
    if (panel_24 == null) {
      panel_24 = new JPanel();
      panel_24.setPreferredSize(new Dimension(10, 28));
      panel_24.setMinimumSize(new Dimension(10, 28));
      panel_24.setMaximumSize(new Dimension(32767, 28));
      panel_24.setLayout(new BoxLayout(panel_24, BoxLayout.X_AXIS));
      quiltRenderTotalProgressLbl = new JLabel();
      quiltRenderTotalProgressLbl.setMinimumSize(new Dimension(100, 0));
      panel_24.add(quiltRenderTotalProgressLbl);
      quiltRenderTotalProgressLbl.setPreferredSize(new Dimension(100, 22));
      quiltRenderTotalProgressLbl.setText("Total progress");
      quiltRenderTotalProgressLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      panel_24.add(getTotalProgressBar());
    }
    return panel_24;
  }

  private JPanel getPanel_25() {
    if (panel_25 == null) {
      panel_25 = new JPanel();
      panel_25.setLayout(new BoxLayout(panel_25, BoxLayout.Y_AXIS));
      panel_25.add(getPanel_23());
      panel_25.add(getPanel_24());
    }
    return panel_25;
  }

  private JPanel getPanel_26() {
    if (panel_26 == null) {
      panel_26 = new JPanel();
      panel_26.setPreferredSize(new Dimension(6, 10));
      panel_26.setMinimumSize(new Dimension(6, 10));
      panel_26.setMaximumSize(new Dimension(6, 32767));
    }
    return panel_26;
  }

  public JPanel getPreviewRootPanel() {
    if (previewRootPanel == null) {
      previewRootPanel = new JPanel();
      previewRootPanel.setMaximumSize(new Dimension(32767, 120));
      previewRootPanel.setLayout(new BorderLayout(0, 0));
    }
    return previewRootPanel;
  }

  public void setTinaController(TinaController tinaController) {
    this.tinaController = tinaController;
  }

  public JButton getImportFlameFromEditorButton() {
    return importFlameFromEditorButton;
  }

  public JWFNumberField getQualityEdit() {
    return qualityEdit;
  }

  public JWFNumberField getRenderWidthEdit() {
    return renderWidthEdit;
  }

  public JWFNumberField getRenderHeightEdit() {
    return renderHeightEdit;
  }

  public JWFNumberField getSegmentationLevelEdit() {
    return segmentationLevelEdit;
  }

  public JWFNumberField getSegmentWidthEdit() {
    return segmentWidthEdit;
  }

  public JWFNumberField getSegmentHeightEdit() {
    return segmentHeightEdit;
  }

  public JTextField getOutputFilenameEdit() {
    return outputFilenameEdit;
  }

  public JButton getImportFlameFromClipboardButton() {
    return importFlameFromClipboardButton;
  }
}
