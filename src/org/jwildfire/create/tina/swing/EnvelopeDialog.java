/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2020 Andreas Maschke

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
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.base.solidrender.ShadowType;
import org.jwildfire.create.tina.base.solidrender.SolidRenderSettings;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanel;
import org.jwildfire.envelope.Envelope;
import org.jwildfire.envelope.Envelope.Interpolation;
import org.jwildfire.envelope.EnvelopePanel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ErrorHandler;

public class EnvelopeDialog extends JDialog implements FlameHolder {
  private static final long serialVersionUID = 1L;
  private JPanel jContentPane = null;
  private JPanel bottomPanel = null;
  private JPanel topPanel = null;
  private JPanel centerPanel = null;
  private JButton okButton = null;
  private JButton cancelButton = null;
  private boolean confirmed = false;
  private boolean removed = false;
  private JPanel panel;
  private JPanel panel_1;
  private final EnvelopeDlgController ctrl;

  private Flame flameToPreview;
  private MotionCurve curveToPreview;
  private String curveToPreviewPropertyPath;
  private int frameToPreview;
  private double curveValueToPreview;
  private EnvelopeDialogFlamePreviewType flamePreviewType = EnvelopeDialogFlamePreviewType.NONE;
  private final ErrorHandler errorHandler;

  public EnvelopeDialog(Window pOwner, ErrorHandler pErrorHandler, Envelope pEnvelope, boolean pAllowRemove) {
    super(pOwner);
    errorHandler = pErrorHandler;
    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentShown(ComponentEvent e) {
        try {
          refreshFlameImage();
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });
    initialize();
    getEnvelopePanel().setEnvelope(pEnvelope);
    Rectangle rootBounds = pOwner.getBounds();
    Dimension size = getSize();
    getBtnRemove().setVisible(pAllowRemove);

    JLabel lblFrameRange = new JLabel();
    lblFrameRange.setBounds(6, 29, 74, 26);
    panel_3.add(lblFrameRange);
    lblFrameRange.setFont(new Font("Dialog", Font.BOLD, 10));
    lblFrameRange.setText("Frame range");
    lblFrameRange.setPreferredSize(new Dimension(38, 26));
    lblFrameRange.setHorizontalAlignment(SwingConstants.RIGHT);
    lblFrameRange.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

    JLabel lblAmplitudeRange = new JLabel();
    lblAmplitudeRange.setBounds(6, 56, 74, 26);
    panel_3.add(lblAmplitudeRange);
    lblAmplitudeRange.setFont(new Font("Dialog", Font.BOLD, 10));
    lblAmplitudeRange.setText("Value range");
    lblAmplitudeRange.setPreferredSize(new Dimension(38, 26));
    lblAmplitudeRange.setHorizontalAlignment(SwingConstants.RIGHT);

    envelopeYMinREd = new JWFNumberField();
    envelopeYMinREd.setBounds(84, 56, 80, 26);
    panel_3.add(envelopeYMinREd);
    envelopeYMinREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    envelopeYMinREd.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        if (ctrl != null)
          ctrl.editFieldChanged(false);
      }
    });
    envelopeYMinREd.setPreferredSize(new Dimension(80, 26));

    envelopeXMaxREd = new JWFNumberField();
    envelopeXMaxREd.setBounds(166, 30, 80, 26);
    panel_3.add(envelopeXMaxREd);
    envelopeXMaxREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    envelopeXMaxREd.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        if (ctrl != null)
          ctrl.editFieldChanged(false);
      }
    });
    envelopeXMaxREd.setPreferredSize(new Dimension(80, 26));

    envelopeYMaxREd = new JWFNumberField();
    envelopeYMaxREd.setBounds(166, 57, 80, 26);
    panel_3.add(envelopeYMaxREd);
    envelopeYMaxREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    envelopeYMaxREd.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        if (ctrl != null)
          ctrl.editFieldChanged(false);
      }
    });
    envelopeYMaxREd.setPreferredSize(new Dimension(80, 26));

    envelopeInterpolationCmb = new JComboBox();
    envelopeInterpolationCmb.setBounds(84, 94, 80, 26);
    panel_3.add(envelopeInterpolationCmb);
    envelopeInterpolationCmb.setFont(new Font("Dialog", Font.PLAIN, 10));
    envelopeInterpolationCmb.addItem(Envelope.Interpolation.SPLINE);
    envelopeInterpolationCmb.addItem(Envelope.Interpolation.BEZIER);
    envelopeInterpolationCmb.addItem(Envelope.Interpolation.LINEAR);

    envelopeInterpolationCmb.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        if (ctrl != null) {
          ctrl.interpolationCmbChanged();
        }
      }
    });
    envelopeInterpolationCmb.setPreferredSize(new Dimension(141, 26));

    JLabel label_6 = new JLabel();
    label_6.setBounds(6, 96, 74, 26);
    panel_3.add(label_6);
    label_6.setFont(new Font("Dialog", Font.BOLD, 10));
    label_6.setText("Interpolation");
    label_6.setPreferredSize(new Dimension(81, 26));
    label_6.setHorizontalAlignment(SwingConstants.RIGHT);

    JLabel lblFrame = new JLabel();
    lblFrame.setBounds(6, 134, 74, 26);
    panel_3.add(lblFrame);
    lblFrame.setFont(new Font("Dialog", Font.BOLD, 10));
    lblFrame.setText("Frame");
    lblFrame.setPreferredSize(new Dimension(38, 26));
    lblFrame.setHorizontalAlignment(SwingConstants.RIGHT);

    envelopeXREd = new JWFNumberField();
    envelopeXREd.setBounds(84, 134, 80, 26);
    panel_3.add(envelopeXREd);
    envelopeXREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    envelopeXREd.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        if (ctrl != null)
          ctrl.editFieldChanged(true);
      }
    });
    envelopeXREd.setPreferredSize(new Dimension(80, 26));

    JLabel lblAmplitude = new JLabel();
    lblAmplitude.setBounds(6, 160, 74, 26);
    panel_3.add(lblAmplitude);
    lblAmplitude.setFont(new Font("Dialog", Font.BOLD, 10));
    lblAmplitude.setText("Value");
    lblAmplitude.setPreferredSize(new Dimension(38, 26));
    lblAmplitude.setHorizontalAlignment(SwingConstants.RIGHT);

    envelopeYREd = new JWFNumberField();
    envelopeYREd.setBounds(84, 160, 80, 26);
    panel_3.add(envelopeYREd);
    envelopeYREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    envelopeYREd.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        if (ctrl != null)
          ctrl.editFieldChanged(true);
      }
    });
    envelopeYREd.setPreferredSize(new Dimension(80, 26));
    panel_4.setLayout(new BorderLayout(0, 0));

    JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    tabbedPane.setBorder(new TitledBorder(null, "Editing", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, null, null));
    panel_4.add(tabbedPane, BorderLayout.CENTER);

    JPanel panel_5 = new JPanel();
    tabbedPane.addTab("Edit", null, panel_5, null);
    panel_5.setPreferredSize(new Dimension(174, 10));
    panel_5.setLayout(null);

    envelopeAddPointButton = new JButton();
    envelopeAddPointButton.setBounds(6, 6, 141, 50);
    panel_5.add(envelopeAddPointButton);
    envelopeAddPointButton.setFont(new Font("Dialog", Font.BOLD, 10));
    envelopeAddPointButton.setToolTipText("Add a point to the curve (click at the area to place it)");
    envelopeAddPointButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (ctrl != null)
          ctrl.addPoint();
      }
    });
    envelopeAddPointButton.setText("Add point");
    envelopeAddPointButton.setPreferredSize(new Dimension(141, 50));

    envelopeRemovePointButton = new JButton();
    envelopeRemovePointButton.setBounds(6, 96, 141, 26);
    panel_5.add(envelopeRemovePointButton);
    envelopeRemovePointButton.setFont(new Font("Dialog", Font.BOLD, 10));
    envelopeRemovePointButton.setToolTipText("Remove a point (after clicking this button, click at a point)");
    envelopeRemovePointButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (ctrl != null)
          ctrl.removePoint();
      }
    });
    envelopeRemovePointButton.setText("Remove point");
    envelopeRemovePointButton.setPreferredSize(new Dimension(141, 26));

    envelopeClearButton = new JButton();
    envelopeClearButton.setBounds(425, 96, 141, 26);
    panel_5.add(envelopeClearButton);
    envelopeClearButton.setFont(new Font("Dialog", Font.BOLD, 10));
    envelopeClearButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (ctrl != null) {
          ctrl.clearEnvelope();
        }
      }
    });
    envelopeClearButton.setText("Reset curve");
    envelopeClearButton.setPreferredSize(new Dimension(141, 26));

    timeField = new JWFNumberField();
    timeField.setEditable(false);
    timeField.setPreferredSize(new Dimension(80, 26));
    timeField.setFont(new Font("Dialog", Font.PLAIN, 10));
    timeField.setBounds(207, 132, 80, 26);
    panel_3.add(timeField);

    JLabel lblTime = new JLabel();
    lblTime.setText("Time");
    lblTime.setPreferredSize(new Dimension(38, 26));
    lblTime.setHorizontalAlignment(SwingConstants.RIGHT);
    lblTime.setFont(new Font("Dialog", Font.BOLD, 10));
    lblTime.setBounds(165, 132, 38, 26);
    panel_3.add(lblTime);

    curveFPSField = new JWFNumberField();
    curveFPSField.setEditable(false);
    curveFPSField.setText("25");
    curveFPSField.setPreferredSize(new Dimension(80, 26));
    curveFPSField.setOnlyIntegers(true);
    curveFPSField.setMinValue(1.0);
    curveFPSField.setMaxValue(200.0);
    curveFPSField.setHasMinValue(true);
    curveFPSField.setHasMaxValue(true);
    curveFPSField.setFont(new Font("Dialog", Font.PLAIN, 10));
    curveFPSField.setBounds(207, 94, 80, 26);
    panel_3.add(curveFPSField);

    JLabel label = new JLabel();
    label.setText("FPS");
    label.setPreferredSize(new Dimension(38, 26));
    label.setHorizontalAlignment(SwingConstants.RIGHT);
    label.setFont(new Font("Dialog", Font.BOLD, 10));
    label.setBounds(165, 94, 38, 26);
    panel_3.add(label);

    panel_7 = new JPanel();
    tabbedPane.addTab("MP3 Sound", null, panel_7, null);
    panel_7.setLayout(null);

    envelopeImportMP3Button = new JButton();
    envelopeImportMP3Button.setBounds(6, 6, 184, 26);
    panel_7.add(envelopeImportMP3Button);
    envelopeImportMP3Button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (ctrl != null)
          ctrl.importMP3();
      }
    });
    envelopeImportMP3Button.setText("Import mp3 file...");
    envelopeImportMP3Button.setPreferredSize(new Dimension(141, 26));
    envelopeImportMP3Button.setFont(new Font("Dialog", Font.BOLD, 10));

    envelopeMP3ChannelREd = new JWFNumberField();
    envelopeMP3ChannelREd.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        if (ctrl != null)
          ctrl.mp3SettingsChanged();
      }
    });
    envelopeMP3ChannelREd.setHasMinValue(true);
    envelopeMP3ChannelREd.setHasMaxValue(true);
    envelopeMP3ChannelREd.setMinValue(1.0);
    envelopeMP3ChannelREd.setMaxValue(64.0);
    envelopeMP3ChannelREd.setOnlyIntegers(true);
    envelopeMP3ChannelREd.setText("3");
    envelopeMP3ChannelREd.setPreferredSize(new Dimension(80, 26));
    envelopeMP3ChannelREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    envelopeMP3ChannelREd.setBounds(111, 44, 80, 26);
    panel_7.add(envelopeMP3ChannelREd);

    JLabel lblChannel = new JLabel();
    lblChannel.setToolTipText("Choose from one of the available 64 frequency channels");
    lblChannel.setText("Frequency Band");
    lblChannel.setPreferredSize(new Dimension(38, 26));
    lblChannel.setHorizontalAlignment(SwingConstants.RIGHT);
    lblChannel.setFont(new Font("Dialog", Font.BOLD, 10));
    lblChannel.setBounds(16, 44, 93, 26);
    panel_7.add(lblChannel);

    JLabel lblOffsetms = new JLabel();
    lblOffsetms.setText("Offset (frames)");
    lblOffsetms.setPreferredSize(new Dimension(38, 26));
    lblOffsetms.setHorizontalAlignment(SwingConstants.RIGHT);
    lblOffsetms.setFont(new Font("Dialog", Font.BOLD, 10));
    lblOffsetms.setBounds(202, 32, 102, 26);
    panel_7.add(lblOffsetms);

    envelopeMP3OffsetREd = new JWFNumberField();
    envelopeMP3OffsetREd.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        if (ctrl != null)
          ctrl.mp3SettingsChanged();
      }
    });
    envelopeMP3OffsetREd.setPreferredSize(new Dimension(80, 26));
    envelopeMP3OffsetREd.setOnlyIntegers(true);
    envelopeMP3OffsetREd.setMinValue(1.0);
    envelopeMP3OffsetREd.setMaxValue(64.0);
    envelopeMP3OffsetREd.setHasMinValue(true);
    envelopeMP3OffsetREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    envelopeMP3OffsetREd.setBounds(306, 32, 80, 26);
    panel_7.add(envelopeMP3OffsetREd);

    envelopeMP3DurationREd = new JWFNumberField();
    envelopeMP3DurationREd.setText("300");
    envelopeMP3DurationREd.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        if (ctrl != null)
          ctrl.mp3SettingsChanged();
      }
    });
    envelopeMP3DurationREd.setPreferredSize(new Dimension(80, 26));
    envelopeMP3DurationREd.setOnlyIntegers(true);
    envelopeMP3DurationREd.setMinValue(1.0);
    envelopeMP3DurationREd.setMaxValue(64.0);
    envelopeMP3DurationREd.setHasMinValue(true);
    envelopeMP3DurationREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    envelopeMP3DurationREd.setBounds(306, 58, 80, 26);
    panel_7.add(envelopeMP3DurationREd);

    JLabel lblDurationframes = new JLabel();
    lblDurationframes.setToolTipText("Duration (including the offset of the sound, in frames)");
    lblDurationframes.setText("Max. Duration");
    lblDurationframes.setPreferredSize(new Dimension(38, 26));
    lblDurationframes.setHorizontalAlignment(SwingConstants.RIGHT);
    lblDurationframes.setFont(new Font("Dialog", Font.BOLD, 10));
    lblDurationframes.setBounds(202, 58, 102, 26);
    panel_7.add(lblDurationframes);

    envelopeMP3FPSREd = new JWFNumberField();
    envelopeMP3FPSREd.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        if (ctrl != null)
          ctrl.mp3SettingsChanged();
      }
    });
    envelopeMP3FPSREd.setHasMaxValue(true);
    envelopeMP3FPSREd.setText("25");
    envelopeMP3FPSREd.setPreferredSize(new Dimension(80, 26));
    envelopeMP3FPSREd.setOnlyIntegers(true);
    envelopeMP3FPSREd.setMinValue(1.0);
    envelopeMP3FPSREd.setMaxValue(200.0);
    envelopeMP3FPSREd.setHasMinValue(true);
    envelopeMP3FPSREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    envelopeMP3FPSREd.setBounds(306, 6, 80, 26);
    panel_7.add(envelopeMP3FPSREd);

    JLabel lblFps = new JLabel();
    lblFps.setText("FPS");
    lblFps.setPreferredSize(new Dimension(38, 26));
    lblFps.setHorizontalAlignment(SwingConstants.RIGHT);
    lblFps.setFont(new Font("Dialog", Font.BOLD, 10));
    lblFps.setBounds(202, 6, 102, 26);
    panel_7.add(lblFps);

    JPanel panel_2 = new JPanel();
    tabbedPane.addTab("Raw data", null, panel_2, null);
    panel_2.setLayout(null);

    rawDataImportFromFileButton = new JButton();
    rawDataImportFromFileButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (ctrl != null)
          ctrl.importRawDataFromFile();
      }
    });
    rawDataImportFromFileButton.setText("Import from file...");
    rawDataImportFromFileButton.setPreferredSize(new Dimension(141, 26));
    rawDataImportFromFileButton.setFont(new Font("Dialog", Font.BOLD, 10));
    rawDataImportFromFileButton.setBounds(6, 6, 184, 26);
    panel_2.add(rawDataImportFromFileButton);

    rawDataFrameColumnField = new JWFNumberField();
    rawDataFrameColumnField.setText("1");
    rawDataFrameColumnField.setPreferredSize(new Dimension(80, 26));
    rawDataFrameColumnField.setOnlyIntegers(true);
    rawDataFrameColumnField.setMinValue(1.0);
    rawDataFrameColumnField.setMaxValue(64.0);
    rawDataFrameColumnField.setHasMinValue(true);
    rawDataFrameColumnField.setFont(new Font("Dialog", Font.PLAIN, 10));
    rawDataFrameColumnField.setBounds(102, 74, 80, 26);
    panel_2.add(rawDataFrameColumnField);

    JLabel lblXcolumn = new JLabel();
    lblXcolumn.setToolTipText("Choose the column in the raw-data-file which should interpreted as frame-numbers");
    lblXcolumn.setText("Frame-Column");
    lblXcolumn.setPreferredSize(new Dimension(38, 26));
    lblXcolumn.setHorizontalAlignment(SwingConstants.RIGHT);
    lblXcolumn.setFont(new Font("Dialog", Font.BOLD, 10));
    lblXcolumn.setBounds(7, 74, 93, 26);
    panel_2.add(lblXcolumn);

    rawDataAmplitudeColumnField = new JWFNumberField();
    rawDataAmplitudeColumnField.setText("2");
    rawDataAmplitudeColumnField.setPreferredSize(new Dimension(80, 26));
    rawDataAmplitudeColumnField.setOnlyIntegers(true);
    rawDataAmplitudeColumnField.setMinValue(1.0);
    rawDataAmplitudeColumnField.setMaxValue(64.0);
    rawDataAmplitudeColumnField.setHasMinValue(true);
    rawDataAmplitudeColumnField.setFont(new Font("Dialog", Font.PLAIN, 10));
    rawDataAmplitudeColumnField.setBounds(101, 99, 80, 26);
    panel_2.add(rawDataAmplitudeColumnField);

    JLabel lblYcolumn = new JLabel();
    lblYcolumn.setToolTipText("Choose the column in the raw-data-file which should interpreted as values");
    lblYcolumn.setText("Amplitude-Column");
    lblYcolumn.setPreferredSize(new Dimension(38, 26));
    lblYcolumn.setHorizontalAlignment(SwingConstants.RIGHT);
    lblYcolumn.setFont(new Font("Dialog", Font.BOLD, 10));
    lblYcolumn.setBounds(6, 99, 93, 26);
    panel_2.add(lblYcolumn);

    rawDataImportFromClipboardButton = new JButton();
    rawDataImportFromClipboardButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (ctrl != null)
          ctrl.importRawDataFromClipboard();
      }
    });
    rawDataImportFromClipboardButton.setText("Import from Clipboard");
    rawDataImportFromClipboardButton.setPreferredSize(new Dimension(141, 26));
    rawDataImportFromClipboardButton.setFont(new Font("Dialog", Font.BOLD, 10));
    rawDataImportFromClipboardButton.setBounds(6, 36, 184, 26);
    panel_2.add(rawDataImportFromClipboardButton);

    rawDataExportToFileButton = new JButton();
    rawDataExportToFileButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (ctrl != null)
          ctrl.exportRawDataToFile();
      }
    });
    rawDataExportToFileButton.setText("Export to file...");
    rawDataExportToFileButton.setPreferredSize(new Dimension(141, 26));
    rawDataExportToFileButton.setFont(new Font("Dialog", Font.BOLD, 10));
    rawDataExportToFileButton.setBounds(382, 6, 184, 26);
    panel_2.add(rawDataExportToFileButton);

    rawDataExportToClipboardButton = new JButton();
    rawDataExportToClipboardButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (ctrl != null)
          ctrl.exportRawDataToClipboard();
      }
    });
    rawDataExportToClipboardButton.setText("Export to Clipboard");
    rawDataExportToClipboardButton.setPreferredSize(new Dimension(141, 26));
    rawDataExportToClipboardButton.setFont(new Font("Dialog", Font.BOLD, 10));
    rawDataExportToClipboardButton.setBounds(382, 34, 184, 26);
    panel_2.add(rawDataExportToClipboardButton);

    rawDataFrameScaleField = new JWFNumberField();
    rawDataFrameScaleField.setText("1");
    rawDataFrameScaleField.setPreferredSize(new Dimension(80, 26));
    rawDataFrameScaleField.setFont(new Font("Dialog", Font.PLAIN, 10));
    rawDataFrameScaleField.setBounds(289, 74, 80, 26);
    panel_2.add(rawDataFrameScaleField);

    JLabel label_1 = new JLabel();
    label_1.setText("Frame Scale");
    label_1.setPreferredSize(new Dimension(38, 26));
    label_1.setHorizontalAlignment(SwingConstants.RIGHT);
    label_1.setFont(new Font("Dialog", Font.BOLD, 10));
    label_1.setBounds(194, 74, 93, 26);
    panel_2.add(label_1);

    rawDataAmplitudeScaleField = new JWFNumberField();
    rawDataAmplitudeScaleField.setText("1");
    rawDataAmplitudeScaleField.setPreferredSize(new Dimension(80, 26));
    rawDataAmplitudeScaleField.setFont(new Font("Dialog", Font.PLAIN, 10));
    rawDataAmplitudeScaleField.setBounds(289, 99, 80, 26);
    panel_2.add(rawDataAmplitudeScaleField);

    JLabel label_2 = new JLabel();
    label_2.setText("Amplitude Scale");
    label_2.setPreferredSize(new Dimension(38, 26));
    label_2.setHorizontalAlignment(SwingConstants.RIGHT);
    label_2.setFont(new Font("Dialog", Font.BOLD, 10));
    label_2.setBounds(194, 99, 93, 26);
    panel_2.add(label_2);

    JPanel panel_6 = new JPanel();
    tabbedPane.addTab("Transform", null, panel_6, null);
    panel_6.setPreferredSize(new Dimension(204, 10));
    panel_6.setLayout(null);

    envelopeXScaleREd = new JWFNumberField();
    envelopeXScaleREd.setText("1");
    envelopeXScaleREd.setPreferredSize(new Dimension(80, 26));
    envelopeXScaleREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    envelopeXScaleREd.setBounds(101, 6, 80, 26);
    panel_6.add(envelopeXScaleREd);

    JLabel lblFrameScale = new JLabel();
    lblFrameScale.setText("Frame Scale");
    lblFrameScale.setPreferredSize(new Dimension(38, 26));
    lblFrameScale.setHorizontalAlignment(SwingConstants.RIGHT);
    lblFrameScale.setFont(new Font("Dialog", Font.BOLD, 10));
    lblFrameScale.setBounds(6, 6, 93, 26);
    panel_6.add(lblFrameScale);

    JLabel lblFrameOffset = new JLabel();
    lblFrameOffset.setText("Frame Offset");
    lblFrameOffset.setPreferredSize(new Dimension(38, 26));
    lblFrameOffset.setHorizontalAlignment(SwingConstants.RIGHT);
    lblFrameOffset.setFont(new Font("Dialog", Font.BOLD, 10));
    lblFrameOffset.setBounds(6, 32, 93, 26);
    panel_6.add(lblFrameOffset);

    envelopeXOffsetREd = new JWFNumberField();
    envelopeXOffsetREd.setPreferredSize(new Dimension(80, 26));
    envelopeXOffsetREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    envelopeXOffsetREd.setBounds(101, 32, 80, 26);
    panel_6.add(envelopeXOffsetREd);

    envelopeYScaleREd = new JWFNumberField();
    envelopeYScaleREd.setText("1");
    envelopeYScaleREd.setPreferredSize(new Dimension(80, 26));
    envelopeYScaleREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    envelopeYScaleREd.setBounds(101, 70, 80, 26);
    panel_6.add(envelopeYScaleREd);

    JLabel lblAmplitudeScale = new JLabel();
    lblAmplitudeScale.setText("Amplitude Scale");
    lblAmplitudeScale.setPreferredSize(new Dimension(38, 26));
    lblAmplitudeScale.setHorizontalAlignment(SwingConstants.RIGHT);
    lblAmplitudeScale.setFont(new Font("Dialog", Font.BOLD, 10));
    lblAmplitudeScale.setBounds(6, 70, 93, 26);
    panel_6.add(lblAmplitudeScale);

    JLabel lblAmplitudeOffset = new JLabel();
    lblAmplitudeOffset.setText("Amplitude Offset");
    lblAmplitudeOffset.setPreferredSize(new Dimension(38, 26));
    lblAmplitudeOffset.setHorizontalAlignment(SwingConstants.RIGHT);
    lblAmplitudeOffset.setFont(new Font("Dialog", Font.BOLD, 10));
    lblAmplitudeOffset.setBounds(6, 96, 93, 26);
    panel_6.add(lblAmplitudeOffset);

    envelopeYOffsetREd = new JWFNumberField();
    envelopeYOffsetREd.setPreferredSize(new Dimension(80, 26));
    envelopeYOffsetREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    envelopeYOffsetREd.setBounds(101, 96, 80, 26);
    panel_6.add(envelopeYOffsetREd);

    envelopeApplyTransformBtn = new JButton();
    envelopeApplyTransformBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (ctrl != null)
          ctrl.applyTransform(false);
      }
    });
    envelopeApplyTransformBtn.setToolTipText("");
    envelopeApplyTransformBtn.setText("Apply");
    envelopeApplyTransformBtn.setPreferredSize(new Dimension(141, 26));
    envelopeApplyTransformBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    envelopeApplyTransformBtn.setBounds(362, 6, 80, 26);
    panel_6.add(envelopeApplyTransformBtn);

    envelopeApplyTransformReverseBtn = new JButton();
    envelopeApplyTransformReverseBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (ctrl != null)
          ctrl.applyTransform(true);
      }
    });
    envelopeApplyTransformReverseBtn.setToolTipText("Apply, but revert params, can also be used as undo-function");
    envelopeApplyTransformReverseBtn.setText("Reverse");
    envelopeApplyTransformReverseBtn.setPreferredSize(new Dimension(141, 26));
    envelopeApplyTransformReverseBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    envelopeApplyTransformReverseBtn.setBounds(454, 6, 80, 26);
    panel_6.add(envelopeApplyTransformReverseBtn);

    editModeCmb = new JComboBox();
    editModeCmb.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        if (ctrl != null) {
          ctrl.editModeChanged();
        }
      }
    });
    editModeCmb.setPreferredSize(new Dimension(141, 26));
    editModeCmb.setFont(new Font("Dialog", Font.PLAIN, 10));
    editModeCmb.setBounds(257, 96, 141, 26);
    editModeCmb.addItem(Envelope.EditMode.DRAG_POINTS);
    editModeCmb.addItem(Envelope.EditMode.DRAG_CURVE_HORIZ);
    editModeCmb.addItem(Envelope.EditMode.DRAG_CURVE_VERT);
    editModeCmb.addItem(Envelope.EditMode.SCALE_CURVE_HORIZ);
    editModeCmb.addItem(Envelope.EditMode.SCALE_CURVE_VERT);
    panel_5.add(editModeCmb);

    JLabel lblMode = new JLabel();
    lblMode.setText("Editing-Mode");
    lblMode.setPreferredSize(new Dimension(81, 26));
    lblMode.setHorizontalAlignment(SwingConstants.RIGHT);
    lblMode.setFont(new Font("Dialog", Font.BOLD, 10));
    lblMode.setBounds(178, 96, 74, 26);
    panel_5.add(lblMode);

    smoothCurveBtn = new JButton();
    smoothCurveBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (ctrl != null) {
          ctrl.smoothEnvelope();
        }
      }
    });
    smoothCurveBtn.setToolTipText("");
    smoothCurveBtn.setText("Smooth curve");
    smoothCurveBtn.setPreferredSize(new Dimension(141, 50));
    smoothCurveBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    smoothCurveBtn.setBounds(425, 6, 141, 50);
    panel_5.add(smoothCurveBtn);

    smoothCurveAmountField = new JWFNumberField();
    smoothCurveAmountField.setText("1");
    smoothCurveAmountField.setOnlyIntegers(true);
    smoothCurveAmountField.setMaxValue(1000.0);
    smoothCurveAmountField.setMinValue(1.0);
    smoothCurveAmountField.setPreferredSize(new Dimension(80, 26));
    smoothCurveAmountField.setFont(new Font("Dialog", Font.PLAIN, 10));
    smoothCurveAmountField.setBounds(486, 54, 80, 26);
    panel_5.add(smoothCurveAmountField);

    JLabel lblSmoothStrength = new JLabel();
    lblSmoothStrength.setText("Strength");
    lblSmoothStrength.setPreferredSize(new Dimension(38, 26));
    lblSmoothStrength.setHorizontalAlignment(SwingConstants.RIGHT);
    lblSmoothStrength.setFont(new Font("Dialog", Font.BOLD, 10));
    lblSmoothStrength.setBounds(408, 54, 74, 26);
    panel_5.add(lblSmoothStrength);

    ctrl = new EnvelopeDlgController(pEnvelope, getEnvelopeAddPointButton(), getEnvelopeRemovePointButton(), getEnvelopeClearButton(),
        getEnvelopeXMinREd(), getEnvelopeXMaxREd(), getEnvelopeYMinREd(), getEnvelopeYMaxREd(), getEnvelopeXREd(),
        getEnvelopeYREd(), getEnvelopeInterpolationCmb(), getEnvelopeViewAllButton(), getEnvelopeViewLeftButton(),
        getEnvelopeViewRightButton(), getEnvelopeViewUpButton(), getEnvelopeViewDownButton(), getEnvelopePanel(), getEnvelopeInterpolationCmb(),
        getEnvelopeXScaleREd(), getEnvelopeXOffsetREd(), getEnvelopeYScaleREd(), getEnvelopeYOffsetREd(),
        getEnvelopeApplyTransformBtn(), getEnvelopeApplyTransformReverseBtn(), getEnvelopeImportMP3Button(),
        getEnvelopeMP3ChannelREd(), getEnvelopeMP3FPSREd(), getEnvelopeMP3OffsetREd(), getEnvelopeMP3DurationREd(),
        errorHandler, getAutofitCBx(), getCurveFPSField(), getEditModeCmb(), getSmoothCurveBtn(), getTimeField(),
        getRawDataImportFromFileButton(), getRawDataImportFromClipboardButton(), getRawDataExportToFileButton(),
        getRawDataExportToClipboardButton(), getRawDataFrameColumnField(), getRawDataFrameScaleField(),
        getRawDataAmplitudeColumnField(), getRawDataAmplitudeScaleField(), getSmoothCurveAmountField());

    ctrl.setNoRefresh(true);

    EnvelopeChangeListener changeListener = new EnvelopeChangeListener() {

      @Override
      public void notify(int pSelectedPoint, int pX, double pY) {
        notifyChange(pSelectedPoint, pX, pY);
      }

    };

    ctrl.registerSelectionChangeListener(changeListener);
    ctrl.registerValueChangeListener(changeListener);
    try {
      ctrl.refreshEnvelope();
      ctrl.enableControls();
    }
    finally {
      ctrl.setNoRefresh(false);
    }
    setLocation(rootBounds.x + (rootBounds.width - size.width) / 2, rootBounds.y + (rootBounds.height - size.height) / 2);
  }

  private EnvelopePanel envelopePanel;
  private JWFNumberField envelopeXMinREd;
  private JWFNumberField envelopeYMinREd;
  private JWFNumberField envelopeXMaxREd;
  private JWFNumberField envelopeYMaxREd;
  private JWFNumberField envelopeYREd;
  private JWFNumberField envelopeXREd;
  private JButton envelopeAddPointButton;
  private JButton envelopeRemovePointButton;
  private JButton envelopeClearButton;
  private JButton envelopeViewAllButton;
  private JButton envelopeViewLeftButton;
  private JButton envelopeViewRightButton;
  private JButton envelopeViewUpButton;
  private JButton envelopeViewDownButton;
  private JComboBox envelopeInterpolationCmb;
  private JButton btnRemove;
  private JPanel previewPanel;

  public EnvelopePanel getEnvelopePanel() {
    if (envelopePanel == null) {
      envelopePanel = new EnvelopePanel();
      envelopePanel.setLayout(null);
    }
    return envelopePanel;
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(1200, 696);
    this.setContentPane(getJContentPane());
    getCenterPanel().add((Component) getEnvelopePanel(), BorderLayout.CENTER);
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
      jContentPane.add(getBottomPanel(), BorderLayout.SOUTH);
      jContentPane.add(getTopPanel(), BorderLayout.NORTH);
      jContentPane.add(getCenterPanel(), BorderLayout.CENTER);
    }
    return jContentPane;
  }

  /**
   * This method initializes bottomPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getBottomPanel() {
    if (bottomPanel == null) {
      bottomPanel = new JPanel();
      bottomPanel.setLayout(null);
      bottomPanel.setPreferredSize(new Dimension(0, 40));
      bottomPanel.add(getOkButton(), null);
      bottomPanel.add(getCancelButton(), null);

      btnRemove = new JButton();
      btnRemove.setToolTipText("Disable the motion curve (does not delete the curve itself)");
      btnRemove.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          confirmed = true;
          removed = true;
          setVisible(false);
        }
      });
      btnRemove.setText("Disable motion curve");
      btnRemove.setSize(new Dimension(125, 24));
      btnRemove.setSelected(true);
      btnRemove.setPreferredSize(new Dimension(125, 24));
      btnRemove.setMnemonic(KeyEvent.VK_R);
      btnRemove.setLocation(new Point(276, 10));
      btnRemove.setFont(new Font("Dialog", Font.BOLD, 10));
      btnRemove.setBounds(679, 10, 179, 24);
      bottomPanel.add(btnRemove);
    }
    return bottomPanel;
  }

  /**
   * This method initializes topPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getTopPanel() {
    if (topPanel == null) {
      topPanel = new JPanel();
      topPanel.setLayout(new GridBagLayout());
      topPanel.setPreferredSize(new Dimension(0, 10));
    }
    return topPanel;
  }

  /**
   * This method initializes centerPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getCenterPanel() {
    if (centerPanel == null) {
      centerPanel = new JPanel();
      centerPanel.setLayout(new BorderLayout());
      centerPanel.setFont(new Font("Dialog", Font.PLAIN, 10));
      centerPanel.add(getPanel(), BorderLayout.EAST);
      centerPanel.add(getPanel_1(), BorderLayout.SOUTH);
    }
    return centerPanel;
  }

  /**
   * This method initializes okButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getOkButton() {
    if (okButton == null) {
      okButton = new JButton();
      okButton.setToolTipText("Apply all changes");
      okButton.setPreferredSize(new Dimension(125, 24));
      okButton.setText("Apply and Close");
      okButton.setMnemonic(KeyEvent.VK_O);
      okButton.setSize(new Dimension(179, 24));
      okButton.setLocation(new Point(105, 10));
      okButton.setSelected(true);
      okButton.setFont(new Font("Dialog", Font.BOLD, 10));
      okButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          confirmed = true;
          removed = false;
          setVisible(false);
        }
      });
    }
    return okButton;
  }

  /**
   * This method initializes cancelButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getCancelButton() {
    if (cancelButton == null) {
      cancelButton = new JButton();
      cancelButton.setToolTipText("Discard all changes");
      cancelButton.setPreferredSize(new Dimension(125, 24));
      cancelButton.setMnemonic(KeyEvent.VK_C);
      cancelButton.setText("Cancel");
      cancelButton.setSize(new Dimension(179, 24));
      cancelButton.setLocation(new Point(393, 10));
      cancelButton.setFont(new Font("Dialog", Font.BOLD, 10));
      cancelButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          confirmed = false;
          setVisible(false);
        }
      });
    }
    return cancelButton;
  }

  public boolean isConfirmed() {
    return confirmed;
  }

  private JPanel getPanel() {
    if (panel == null) {
      panel = new JPanel();
      panel.setBorder(new TitledBorder(null, "View", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, null, null));
      panel.setPreferredSize(new Dimension(152, 10));
      panel.setLayout(null);

      envelopeViewUpButton = new JButton();
      envelopeViewUpButton.setFont(new Font("Dialog", Font.BOLD, 10));
      envelopeViewUpButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (ctrl != null)
            ctrl.viewUp();
        }
      });
      envelopeViewUpButton.setText("^");
      envelopeViewUpButton.setSize(new Dimension(48, 26));
      envelopeViewUpButton.setPreferredSize(new Dimension(48, 26));
      envelopeViewUpButton.setLocation(new Point(51, 99));
      envelopeViewUpButton.setBounds(52, 178, 48, 26);
      panel.add(envelopeViewUpButton);

      envelopeViewLeftButton = new JButton();
      envelopeViewLeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      envelopeViewLeftButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (ctrl != null)
            ctrl.viewLeft();
        }
      });
      envelopeViewLeftButton.setText("<");
      envelopeViewLeftButton.setSize(new Dimension(48, 26));
      envelopeViewLeftButton.setPreferredSize(new Dimension(48, 26));
      envelopeViewLeftButton.setLocation(new Point(5, 126));
      envelopeViewLeftButton.setBounds(16, 205, 48, 26);
      panel.add(envelopeViewLeftButton);

      envelopeViewRightButton = new JButton();
      envelopeViewRightButton.setFont(new Font("Dialog", Font.BOLD, 10));
      envelopeViewRightButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (ctrl != null) {
            ctrl.viewRight();
          }
        }
      });
      envelopeViewRightButton.setText(">");
      envelopeViewRightButton.setSize(new Dimension(48, 26));
      envelopeViewRightButton.setPreferredSize(new Dimension(48, 26));
      envelopeViewRightButton.setLocation(new Point(98, 126));
      envelopeViewRightButton.setBounds(86, 205, 48, 26);
      panel.add(envelopeViewRightButton);

      envelopeViewDownButton = new JButton();
      envelopeViewDownButton.setFont(new Font("Dialog", Font.BOLD, 10));
      envelopeViewDownButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (ctrl != null)
            ctrl.viewDown();
        }
      });
      envelopeViewDownButton.setText("v");
      envelopeViewDownButton.setSize(new Dimension(48, 26));
      envelopeViewDownButton.setPreferredSize(new Dimension(48, 26));
      envelopeViewDownButton.setLocation(new Point(51, 153));
      envelopeViewDownButton.setBounds(52, 232, 48, 26);
      panel.add(envelopeViewDownButton);

      envelopeViewAllButton = new JButton();
      envelopeViewAllButton.setFont(new Font("Dialog", Font.BOLD, 10));
      envelopeViewAllButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (ctrl != null)
            ctrl.viewAll();
        }
      });
      envelopeViewAllButton.setText("View all");
      envelopeViewAllButton.setSize(new Dimension(141, 24));
      envelopeViewAllButton.setPreferredSize(new Dimension(141, 26));
      envelopeViewAllButton.setLocation(new Point(5, 190));
      envelopeViewAllButton.setBounds(16, 269, 118, 26);
      panel.add(envelopeViewAllButton);

      autofitCBx = new JCheckBox("Auto fit to view");
      autofitCBx.setBounds(16, 26, 118, 18);
      panel.add(autofitCBx);
    }
    return panel;
  }

  private JPanel getPanel_1() {
    if (panel_1 == null) {
      panel_1 = new JPanel();
      panel_1.setPreferredSize(new Dimension(10, 240));
      panel_1.setLayout(new BorderLayout(0, 0));

      JPanel panel_2 = new JPanel();
      panel_1.add(panel_2);
      panel_2.setLayout(new BorderLayout(0, 0));

      previewPanel = new JPanel();
      panel_2.add(previewPanel);
      previewPanel.setBorder(new TitledBorder(null, "Preview", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, null, null));
      previewPanel.setLayout(new BorderLayout(0, 0));

      previewRootPanel = new JPanel();
      previewPanel.add(previewRootPanel, BorderLayout.CENTER);
      previewRootPanel.setLayout(new BorderLayout(0, 0));

      panel_3 = new JPanel();
      panel_3.setBorder(new TitledBorder(null, "Curve", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, null, null));
      panel_3.setPreferredSize(new Dimension(302, 180));
      panel_3.setSize(new Dimension(282, 180));
      panel_3.setMinimumSize(new Dimension(272, 180));
      panel_1.add(panel_3, BorderLayout.WEST);
      panel_3.setLayout(null);

      envelopeXMinREd = new JWFNumberField();
      envelopeXMinREd.setBounds(84, 29, 80, 26);
      panel_3.add(envelopeXMinREd);
      envelopeXMinREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      envelopeXMinREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (ctrl != null)
            ctrl.editFieldChanged(false);
        }
      });
      envelopeXMinREd.setPreferredSize(new Dimension(80, 26));

      panel_4 = new JPanel();
      panel_4.setSize(new Dimension(600, 180));
      panel_4.setPreferredSize(new Dimension(600, 180));
      panel_4.setMinimumSize(new Dimension(200, 180));
      panel_1.add(panel_4, BorderLayout.EAST);
    }
    return panel_1;
  }

  public JButton getEnvelopeAddPointButton() {
    return envelopeAddPointButton;
  }

  public JButton getEnvelopeRemovePointButton() {
    return envelopeRemovePointButton;
  }

  public JButton getEnvelopeClearButton() {
    return envelopeClearButton;
  }

  public JButton getEnvelopeViewAllButton() {
    return envelopeViewAllButton;
  }

  public JButton getEnvelopeViewLeftButton() {
    return envelopeViewLeftButton;
  }

  public JButton getEnvelopeViewRightButton() {
    return envelopeViewRightButton;
  }

  public JButton getEnvelopeViewUpButton() {
    return envelopeViewUpButton;
  }

  public JButton getEnvelopeViewDownButton() {
    return envelopeViewDownButton;
  }

  public JComboBox getEnvelopeInterpolationCmb() {
    return envelopeInterpolationCmb;
  }

  public JWFNumberField getEnvelopeXMinREd() {
    return envelopeXMinREd;
  }

  public JWFNumberField getEnvelopeYMinREd() {
    return envelopeYMinREd;
  }

  public JWFNumberField getEnvelopeXMaxREd() {
    return envelopeXMaxREd;
  }

  public JWFNumberField getEnvelopeYMaxREd() {
    return envelopeYMaxREd;
  }

  public JWFNumberField getEnvelopeXREd() {
    return envelopeXREd;
  }

  public JWFNumberField getEnvelopeYREd() {
    return envelopeYREd;
  }

  public boolean isRemoved() {
    return removed;
  }

  public JButton getBtnRemove() {
    return btnRemove;
  }

  private FlamePanel flamePanel;

  private FlamePanel getFlamePanel() {
    if (flamePanel == null) {
      int width = getPreviewRootPanel().getWidth();
      int height = getPreviewRootPanel().getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      flamePanel = new FlamePanel(Prefs.getPrefs(), img, 0, 0, getPreviewRootPanel().getWidth(), this, null, null);
      flamePanel.setRenderWidth(800);
      flamePanel.setRenderHeight(600);
      flamePanel.setFocusable(true);
      getPreviewRootPanel().add(flamePanel, BorderLayout.CENTER);
      //      getPreviewRootPanel().getParent().validate();
      //      getPreviewRootPanel().repaint();
      //      flamePanel.requestFocusInWindow();
    }
    return flamePanel;
  }

  public void refreshFlameImage() {
    FlamePanel imgPanel = getFlamePanel();
    Rectangle bounds = imgPanel.getImageBounds();
    int width = bounds.width;
    int height = bounds.height;
    if (width >= 16 && height >= 16) {
      RenderInfo info = new RenderInfo(width, height, RenderMode.PREVIEW);
      Flame flame = getFlame();
      if (flame != null) {
        double oldSpatialFilterRadius = flame.getSpatialFilterRadius();
        double oldSampleDensity = flame.getSampleDensity();
        try {
          double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
          double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
          flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
          flame.setWidth(info.getImageWidth());
          flame.setHeight(info.getImageHeight());

          FlameRenderer renderer = new FlameRenderer(flame, Prefs.getPrefs(), false, false);
          renderer.setProgressUpdater(null);
          if (flame.getSolidRenderSettings().isSolidRenderingEnabled()) {
            flame.setCamDOF(0.0);
            flame.getSolidRenderSettings().setAoEnabled(false);
            if (ShadowType.SMOOTH.equals(flame.getSolidRenderSettings().getShadowType())) {
              flame.getSolidRenderSettings().setShadowType(ShadowType.FAST);
            }
            else {
              flame.getSolidRenderSettings().setShadowType(ShadowType.OFF);
            }
            flame.setSampleDensity(Prefs.getPrefs().getTinaRenderRealtimeQuality() * 2.0);
          }
          else {
            flame.setSampleDensity(Prefs.getPrefs().getTinaRenderRealtimeQuality() * 6.0);
          }
          flame.setSpatialFilterRadius(0.0);
          RenderedFlame res = renderer.renderFlame(info);
          imgPanel.setImage(res.getImage());
        }
        finally {
          flame.setSpatialFilterRadius(oldSpatialFilterRadius);
          flame.setSampleDensity(oldSampleDensity);
        }
      }
    }
    else {
      imgPanel.setImage(new SimpleImage(width, height));
    }
    getPreviewRootPanel().repaint();
  }

  @Override
  public Flame getFlame() {
    if (flameToPreview != null) {
      Flame res = flameToPreview.makeCopy();
      res.setFrame(frameToPreview);
      switch (flamePreviewType) {
        case MOTION_CURVE:
          if (curveToPreviewPropertyPath != null) {
            MotionCurve curve = getMotionCurve(res, curveToPreviewPropertyPath);
            if (curve != null) {
              curve.setPoints(new int[] { frameToPreview }, new double[] { curveValueToPreview });
              curve.setInterpolation(Interpolation.LINEAR);
              curve.setEnabled(true);
            }
          }
          break;
        case COLOR_CURVE:
          if (curveToPreviewPropertyPath != null) {
            MotionCurve curve = getMotionCurve(res, curveToPreviewPropertyPath);
            if (curve != null) {
              curve.assignFromEnvelope(ctrl.getCurrEnvelope());
            }
          }
          break;
        default: // nothing to do
          break;
      }
      return res;
    }
    return null;
  }

  public void setFlameToPreview(EnvelopeDialogFlamePreviewType pFlamePreviewType, Flame pFlameToPreview, MotionCurve pCurveToPreview) {
    flamePreviewType = pFlamePreviewType;
    flameToPreview = pFlameToPreview;
    frameToPreview = pFlameToPreview.getFrame();
    curveToPreview = pCurveToPreview;
    curveValueToPreview = 0.0;
    curveToPreviewPropertyPath = getPropertyPath(flameToPreview, curveToPreview);
    getCurveFPSField().setValue(pFlameToPreview.getFps());
    getEnvelopeMP3FPSREd().setValue(pFlameToPreview.getFps());
    //    System.out.println("PATH: " + curveToPreviewPropertyPath);
    //    MotionCurve curve = getMotionCurve(flameToPreview, curveToPreviewPropertyPath);
    //    System.out.println("CURVE: " + curve);
  }

  protected void notifyChange(int pSelectedPoint, int pX, double pY) {
    frameToPreview = pX;
    curveValueToPreview = pY;
    refreshFlameImage();
  }

  private String getPropertyPath(Object pObject, Object pProperty) {
    String path = findProperty(pObject, pProperty, null);
    return path;
  }

  private static final String PATH_SEPARATOR = "#";
  private JPanel panel_3;
  private JPanel panel_4;
  private JButton envelopeImportMP3Button;
  private JWFNumberField envelopeYScaleREd;
  private JWFNumberField envelopeYOffsetREd;
  private JWFNumberField envelopeXScaleREd;
  private JWFNumberField envelopeXOffsetREd;
  private JButton envelopeApplyTransformReverseBtn;
  private JButton envelopeApplyTransformBtn;
  private JPanel panel_7;
  private JWFNumberField envelopeMP3ChannelREd;
  private JWFNumberField envelopeMP3FPSREd;
  private JWFNumberField envelopeMP3OffsetREd;
  private JWFNumberField envelopeMP3DurationREd;
  private JCheckBox autofitCBx;
  private JComboBox editModeCmb;
  private JWFNumberField curveFPSField;
  private JButton smoothCurveBtn;
  private JWFNumberField timeField;
  private JPanel previewRootPanel;
  private JButton rawDataImportFromFileButton;
  private JButton rawDataImportFromClipboardButton;
  private JButton rawDataExportToFileButton;
  private JButton rawDataExportToClipboardButton;
  private JWFNumberField rawDataFrameColumnField;
  private JWFNumberField rawDataFrameScaleField;
  private JWFNumberField rawDataAmplitudeColumnField;
  private JWFNumberField rawDataAmplitudeScaleField;
  private JWFNumberField smoothCurveAmountField;

  private String findProperty(Object pObject, Object pProperty, String pPath) {
    if (pObject == pProperty) {
      return pPath;
    }
    Class<?> cls = pObject.getClass();
    for (Field field : cls.getDeclaredFields()) {
      field.setAccessible(true);
      Object fieldValue;
      try {
        fieldValue = field.get(pObject);
      }
      catch (Exception ex) {
        fieldValue = null;
      }
      if (fieldValue != null) {
        if (fieldValue == pProperty) {
          String pathExt = field.getName();
          return pPath == null ? pathExt : pPath + PATH_SEPARATOR + pathExt;
        }
        if (fieldValue instanceof List) {
          List<?> list = (List<?>) fieldValue;
          for (int i = 0; i < list.size(); i++) {
            String pathExt = field.getName() + "[" + i + "]";
            String subPath = pPath == null ? pathExt : pPath + PATH_SEPARATOR + pathExt;
            String subResult = findProperty(list.get(i), pProperty, subPath);
            if (subResult != null) {
              return subResult;
            }
          }
        }
        else if (fieldValue instanceof Map) {
          Map<?, ?> map = (Map<?, ?>) fieldValue;
          Set<?> entrySet = map.entrySet();
          for (Object obj : entrySet) {
            Entry<?, ?> entry = (Entry<?, ?>) obj;
            String pathExt = field.getName() + "[" + entry.getKey().toString() + "]";
            String subPath = pPath == null ? pathExt : pPath + PATH_SEPARATOR + pathExt;
            String subResult = findProperty(entry.getValue(), pProperty, subPath);
            if (subResult != null) {
              return subResult;
            }
          }
        }
        else if (fieldValue instanceof RGBPalette) {
          RGBPalette palette = (RGBPalette) fieldValue;
          String pathExt = field.getName();
          String subPath = pPath == null ? pathExt : pPath + PATH_SEPARATOR + pathExt;
          String subResult = findProperty(palette, pProperty, subPath);
          if (subResult != null) {
            return subResult;
          }
        }
        else if (fieldValue instanceof SolidRenderSettings) {
          SolidRenderSettings settings = (SolidRenderSettings) fieldValue;
          String pathExt = field.getName();
          String subPath = pPath == null ? pathExt : pPath + PATH_SEPARATOR + pathExt;
          String subResult = findProperty(settings, pProperty, subPath);
          if (subResult != null) {
            return subResult;
          }
        }
      }
    }
    return null;
  }

  // layers[0]#xForms[1]#rotateCurve
  private MotionCurve getMotionCurve(Flame pFlame, String pPropertyPath) {
    if (pFlame != null && pPropertyPath != null) {
      List<String> path = new ArrayList<String>();
      StringTokenizer tokenizer = new StringTokenizer(pPropertyPath, PATH_SEPARATOR);
      while (tokenizer.hasMoreElements()) {
        path.add((String) tokenizer.nextElement());
      }
      return findMotionCurve(pFlame, path, 0);
    }
    return null;
  }

  private MotionCurve findMotionCurve(Object pObject, List<String> pPath, int pPathIndex) {
    String pathName;
    String index;
    {
      String fullPathName = pPath.get(pPathIndex);
      int p1 = fullPathName.lastIndexOf("[");
      int p2 = fullPathName.indexOf("]", p1 + 1);
      if (p1 > 0 && p2 > p1) {
        pathName = fullPathName.substring(0, p1);
        index = fullPathName.substring(p1 + 1, p2);
      }
      else {
        pathName = fullPathName;
        index = "";
      }
    }
    if (pPath.size() > 0) {
      Class<?> cls = pObject.getClass();
      if (pPathIndex == pPath.size() - 1) {
        for (Field field : cls.getDeclaredFields()) {
          field.setAccessible(true);
          Object fieldValue;
          try {
            fieldValue = field.get(pObject);
          }
          catch (Exception ex) {
            fieldValue = null;
          }
          if (fieldValue != null && field.getName().equals(pathName)) {
            if (fieldValue instanceof MotionCurve) {
              return (MotionCurve) fieldValue;
            }
            else if (fieldValue instanceof Map) {
              Map<?, ?> map = (Map<?, ?>) fieldValue;
              Object curve = map.get(index);
              if (curve != null && curve instanceof MotionCurve) {
                return (MotionCurve) curve;
              }
            }
          }
        }
      }
      else {
        for (Field field : cls.getDeclaredFields()) {
          field.setAccessible(true);
          Object fieldValue;
          try {
            fieldValue = field.get(pObject);
          }
          catch (Exception ex) {
            fieldValue = null;
          }
          if (fieldValue != null) {
            if (fieldValue instanceof List && field.getName().equals(pathName) && index.length() > 0) {
              List<?> list = (List<?>) fieldValue;
              MotionCurve subResult = findMotionCurve(list.get(Integer.parseInt(index)), pPath, pPathIndex + 1);
              if (subResult != null) {
                return subResult;
              }
            }
            else if (fieldValue instanceof Map && field.getName().equals(pathName) && index.length() > 0) {
              Map<?, ?> map = (Map<?, ?>) fieldValue;
              MotionCurve subResult = findMotionCurve(map.get(index), pPath, pPathIndex + 1);
              if (subResult != null) {
                return subResult;
              }
            }
            else if (field.getName().equals(pathName)) {
              MotionCurve subResult = findMotionCurve(fieldValue, pPath, pPathIndex + 1);
              if (subResult != null) {
                return subResult;
              }
            }
          }
        }
      }
    }
    return null;
  }

  public JWFNumberField getEnvelopeYScaleREd() {
    return envelopeYScaleREd;
  }

  public JWFNumberField getEnvelopeYOffsetREd() {
    return envelopeYOffsetREd;
  }

  public JButton getEnvelopeImportMP3Button() {
    return envelopeImportMP3Button;
  }

  public JWFNumberField getEnvelopeXScaleREd() {
    return envelopeXScaleREd;
  }

  public JWFNumberField getEnvelopeXOffsetREd() {
    return envelopeXOffsetREd;
  }

  public JButton getEnvelopeApplyTransformReverseBtn() {
    return envelopeApplyTransformReverseBtn;
  }

  public JButton getEnvelopeApplyTransformBtn() {
    return envelopeApplyTransformBtn;
  }

  public JWFNumberField getEnvelopeMP3ChannelREd() {
    return envelopeMP3ChannelREd;
  }

  public JWFNumberField getEnvelopeMP3FPSREd() {
    return envelopeMP3FPSREd;
  }

  public JWFNumberField getEnvelopeMP3OffsetREd() {
    return envelopeMP3OffsetREd;
  }

  public JWFNumberField getEnvelopeMP3DurationREd() {
    return envelopeMP3DurationREd;
  }

  public JCheckBox getAutofitCBx() {
    return autofitCBx;
  }

  public JComboBox getEditModeCmb() {
    return editModeCmb;
  }

  public JWFNumberField getCurveFPSField() {
    return curveFPSField;
  }

  public JButton getSmoothCurveBtn() {
    return smoothCurveBtn;
  }

  public JWFNumberField getTimeField() {
    return timeField;
  }

  public JPanel getPreviewRootPanel() {
    return previewRootPanel;
  }

  public JButton getRawDataImportFromFileButton() {
    return rawDataImportFromFileButton;
  }

  public JButton getRawDataImportFromClipboardButton() {
    return rawDataImportFromClipboardButton;
  }

  public JButton getRawDataExportToFileButton() {
    return rawDataExportToFileButton;
  }

  public JButton getRawDataExportToClipboardButton() {
    return rawDataExportToClipboardButton;
  }

  public JWFNumberField getRawDataFrameColumnField() {
    return rawDataFrameColumnField;
  }

  public JWFNumberField getRawDataFrameScaleField() {
    return rawDataFrameScaleField;
  }

  public JWFNumberField getRawDataAmplitudeColumnField() {
    return rawDataAmplitudeColumnField;
  }

  public JWFNumberField getRawDataAmplitudeScaleField() {
    return rawDataAmplitudeScaleField;
  }

  public JWFNumberField getSmoothCurveAmountField() {
    return smoothCurveAmountField;
  }
}
