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
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanel;
import org.jwildfire.envelope.Envelope;
import org.jwildfire.envelope.Envelope.Interpolation;
import org.jwildfire.envelope.EnvelopePanel;
import org.jwildfire.image.SimpleImage;

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

  public EnvelopeDialog(Window pOwner, Envelope pEnvelope, boolean pAllowRemove) {
    super(pOwner);
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
    lblFrameRange.setBounds(0, 6, 87, 26);
    panel_3.add(lblFrameRange);
    lblFrameRange.setFont(new Font("Dialog", Font.BOLD, 10));
    lblFrameRange.setText("Frame range");
    lblFrameRange.setPreferredSize(new Dimension(38, 26));
    lblFrameRange.setHorizontalAlignment(SwingConstants.RIGHT);
    lblFrameRange.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

    JLabel lblAmplitudeRange = new JLabel();
    lblAmplitudeRange.setBounds(0, 34, 87, 26);
    panel_3.add(lblAmplitudeRange);
    lblAmplitudeRange.setFont(new Font("Dialog", Font.BOLD, 10));
    lblAmplitudeRange.setText("Amplitude range");
    lblAmplitudeRange.setPreferredSize(new Dimension(38, 26));
    lblAmplitudeRange.setHorizontalAlignment(SwingConstants.RIGHT);

    envelopeYMinREd = new JWFNumberField();
    envelopeYMinREd.setBounds(99, 33, 80, 26);
    panel_3.add(envelopeYMinREd);
    envelopeYMinREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    envelopeYMinREd.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        ctrl.editFieldChanged();
      }
    });
    envelopeYMinREd.setPreferredSize(new Dimension(80, 26));

    envelopeXMaxREd = new JWFNumberField();
    envelopeXMaxREd.setBounds(181, 7, 80, 26);
    panel_3.add(envelopeXMaxREd);
    envelopeXMaxREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    envelopeXMaxREd.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        ctrl.editFieldChanged();
      }
    });
    envelopeXMaxREd.setPreferredSize(new Dimension(80, 26));

    envelopeYMaxREd = new JWFNumberField();
    envelopeYMaxREd.setBounds(181, 34, 80, 26);
    panel_3.add(envelopeYMaxREd);
    envelopeYMaxREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    envelopeYMaxREd.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        ctrl.editFieldChanged();
      }
    });
    envelopeYMaxREd.setPreferredSize(new Dimension(80, 26));

    envelopeInterpolationCmb = new JComboBox();
    envelopeInterpolationCmb.setBounds(99, 64, 165, 26);
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
    label_6.setBounds(0, 66, 87, 26);
    panel_3.add(label_6);
    label_6.setFont(new Font("Dialog", Font.BOLD, 10));
    label_6.setText("Interpolation");
    label_6.setPreferredSize(new Dimension(81, 26));
    label_6.setHorizontalAlignment(SwingConstants.RIGHT);

    JLabel lblFrame = new JLabel();
    lblFrame.setBounds(0, 102, 87, 26);
    panel_3.add(lblFrame);
    lblFrame.setFont(new Font("Dialog", Font.BOLD, 10));
    lblFrame.setText("Frame");
    lblFrame.setPreferredSize(new Dimension(38, 26));
    lblFrame.setHorizontalAlignment(SwingConstants.RIGHT);

    envelopeXREd = new JWFNumberField();
    envelopeXREd.setBounds(99, 102, 80, 26);
    panel_3.add(envelopeXREd);
    envelopeXREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    envelopeXREd.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        ctrl.editFieldChanged();
      }
    });
    envelopeXREd.setPreferredSize(new Dimension(80, 26));

    JLabel lblAmplitude = new JLabel();
    lblAmplitude.setBounds(0, 128, 87, 26);
    panel_3.add(lblAmplitude);
    lblAmplitude.setFont(new Font("Dialog", Font.BOLD, 10));
    lblAmplitude.setText("Amplitude");
    lblAmplitude.setPreferredSize(new Dimension(38, 26));
    lblAmplitude.setHorizontalAlignment(SwingConstants.RIGHT);

    envelopeRemovePointButton = new JButton();
    envelopeRemovePointButton.setBounds(6, 89, 141, 26);
    panel_4.add(envelopeRemovePointButton);
    envelopeRemovePointButton.setFont(new Font("Dialog", Font.BOLD, 10));
    envelopeRemovePointButton.setToolTipText("Remove a point (after clikcing this button, click at a point)");
    envelopeRemovePointButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ctrl.removePoint();
      }
    });
    envelopeRemovePointButton.setText("Remove point");
    envelopeRemovePointButton.setPreferredSize(new Dimension(141, 26));

    envelopeYREd = new JWFNumberField();
    envelopeYREd.setBounds(99, 128, 80, 26);
    panel_3.add(envelopeYREd);
    envelopeYREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    envelopeYREd.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        ctrl.editFieldChanged();
      }
    });
    envelopeYREd.setPreferredSize(new Dimension(80, 26));

    envelopeClearButton = new JButton();
    envelopeClearButton.setBounds(353, 6, 141, 26);
    panel_4.add(envelopeClearButton);
    envelopeClearButton.setFont(new Font("Dialog", Font.BOLD, 10));
    envelopeClearButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ctrl.clearEnvelope();
      }
    });
    envelopeClearButton.setText("Reset curve");
    envelopeClearButton.setPreferredSize(new Dimension(141, 26));

    ctrl = new EnvelopeDlgController(pEnvelope, getEnvelopeAddPointButton(), getEnvelopeRemovePointButton(), getEnvelopeClearButton(),
        getEnvelopeXMinREd(), getEnvelopeXMaxREd(), getEnvelopeYMinREd(), getEnvelopeYMaxREd(), getEnvelopeXREd(),
        getEnvelopeYREd(), getEnvelopeInterpolationCmb(), getEnvelopeViewAllButton(), getEnvelopeViewLeftButton(),
        getEnvelopeViewRightButton(), getEnvelopeViewUpButton(), getEnvelopeViewDownButton(), getEnvelopePanel(), getEnvelopeInterpolationCmb());
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
  private JPanel previewRootPanel;

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
    this.setSize(1200, 600);
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
      okButton.setPreferredSize(new Dimension(125, 24));
      okButton.setText("Apply motion curve");
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
      cancelButton.setPreferredSize(new Dimension(125, 24));
      cancelButton.setMnemonic(KeyEvent.VK_C);
      cancelButton.setText("Discard changes");
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
      panel.setPreferredSize(new Dimension(152, 10));
      panel.setLayout(null);

      envelopeViewUpButton = new JButton();
      envelopeViewUpButton.setFont(new Font("Dialog", Font.BOLD, 10));
      envelopeViewUpButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          ctrl.viewUp();
        }
      });
      envelopeViewUpButton.setText("^");
      envelopeViewUpButton.setSize(new Dimension(48, 26));
      envelopeViewUpButton.setPreferredSize(new Dimension(48, 26));
      envelopeViewUpButton.setLocation(new Point(51, 99));
      envelopeViewUpButton.setBounds(52, 85, 48, 26);
      panel.add(envelopeViewUpButton);

      envelopeViewLeftButton = new JButton();
      envelopeViewLeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      envelopeViewLeftButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          ctrl.viewLeft();
        }
      });
      envelopeViewLeftButton.setText("<");
      envelopeViewLeftButton.setSize(new Dimension(48, 26));
      envelopeViewLeftButton.setPreferredSize(new Dimension(48, 26));
      envelopeViewLeftButton.setLocation(new Point(5, 126));
      envelopeViewLeftButton.setBounds(6, 112, 48, 26);
      panel.add(envelopeViewLeftButton);

      envelopeViewRightButton = new JButton();
      envelopeViewRightButton.setFont(new Font("Dialog", Font.BOLD, 10));
      envelopeViewRightButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          ctrl.viewRight();
        }
      });
      envelopeViewRightButton.setText(">");
      envelopeViewRightButton.setSize(new Dimension(48, 26));
      envelopeViewRightButton.setPreferredSize(new Dimension(48, 26));
      envelopeViewRightButton.setLocation(new Point(98, 126));
      envelopeViewRightButton.setBounds(99, 112, 48, 26);
      panel.add(envelopeViewRightButton);

      envelopeViewDownButton = new JButton();
      envelopeViewDownButton.setFont(new Font("Dialog", Font.BOLD, 10));
      envelopeViewDownButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          ctrl.viewDown();
        }
      });
      envelopeViewDownButton.setText("v");
      envelopeViewDownButton.setSize(new Dimension(48, 26));
      envelopeViewDownButton.setPreferredSize(new Dimension(48, 26));
      envelopeViewDownButton.setLocation(new Point(51, 153));
      envelopeViewDownButton.setBounds(52, 139, 48, 26);
      panel.add(envelopeViewDownButton);

      envelopeViewAllButton = new JButton();
      envelopeViewAllButton.setFont(new Font("Dialog", Font.BOLD, 10));
      envelopeViewAllButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          ctrl.viewAll();
        }
      });
      envelopeViewAllButton.setText("View all");
      envelopeViewAllButton.setSize(new Dimension(141, 24));
      envelopeViewAllButton.setPreferredSize(new Dimension(141, 26));
      envelopeViewAllButton.setLocation(new Point(5, 190));
      envelopeViewAllButton.setBounds(6, 176, 141, 26);
      panel.add(envelopeViewAllButton);
    }
    return panel;
  }

  private JPanel getPanel_1() {
    if (panel_1 == null) {
      panel_1 = new JPanel();
      panel_1.setPreferredSize(new Dimension(10, 200));
      panel_1.setLayout(new BorderLayout(0, 0));

      JPanel panel_2 = new JPanel();
      panel_2.setBorder(new TitledBorder(null, "Preview", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_1.add(panel_2);
      panel_2.setLayout(new BorderLayout(0, 0));

      previewRootPanel = new JPanel();
      panel_2.add(previewRootPanel);
      previewRootPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
      previewRootPanel.setLayout(new BorderLayout(0, 0));

      panel_3 = new JPanel();
      panel_3.setPreferredSize(new Dimension(300, 180));
      panel_3.setSize(new Dimension(300, 180));
      panel_3.setMinimumSize(new Dimension(300, 180));
      panel_1.add(panel_3, BorderLayout.WEST);
      panel_3.setLayout(null);

      envelopeXMinREd = new JWFNumberField();
      envelopeXMinREd.setBounds(99, 6, 80, 26);
      panel_3.add(envelopeXMinREd);
      envelopeXMinREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      envelopeXMinREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          ctrl.editFieldChanged();
        }
      });
      envelopeXMinREd.setPreferredSize(new Dimension(80, 26));

      panel_4 = new JPanel();
      panel_4.setSize(new Dimension(500, 180));
      panel_4.setPreferredSize(new Dimension(500, 180));
      panel_4.setMinimumSize(new Dimension(200, 180));
      panel_1.add(panel_4, BorderLayout.EAST);
      panel_4.setLayout(null);

      envelopeAddPointButton = new JButton();
      envelopeAddPointButton.setBounds(6, 6, 141, 50);
      panel_4.add(envelopeAddPointButton);
      envelopeAddPointButton.setFont(new Font("Dialog", Font.BOLD, 10));
      envelopeAddPointButton.setToolTipText("Add a point to the curve (click at the area to place it)");
      envelopeAddPointButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          ctrl.addPoint();
        }
      });
      envelopeAddPointButton.setText("Add point");
      envelopeAddPointButton.setPreferredSize(new Dimension(141, 50));
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

  public JPanel getPreviewRootPanel() {
    return previewRootPanel;
  }

  private FlamePanel flamePanel;

  private FlamePanel getFlamePanel() {
    if (flamePanel == null) {
      int width = getPreviewRootPanel().getWidth();
      int height = getPreviewRootPanel().getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      flamePanel = new FlamePanel(Prefs.getPrefs(), img, 0, 0, getPreviewRootPanel().getWidth(), this, null);
      flamePanel.setRenderWidth(800);
      flamePanel.setRenderHeight(600);
      flamePanel.setFocusable(true);
      getPreviewRootPanel().add(flamePanel, BorderLayout.CENTER);
      getPreviewRootPanel().getParent().validate();
      getPreviewRootPanel().repaint();
      flamePanel.requestFocusInWindow();
    }
    return flamePanel;
  }

  public void refreshFlameImage() {
    FlamePanel imgPanel = getFlamePanel();
    if (!Tools.V2_FEATURE_ENABLE) {
      imgPanel.getParent().getParent().setVisible(false);
    }
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
          flame.setSampleDensity(Prefs.getPrefs().getTinaRenderRealtimeQuality() * 3.0);
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
      if (curveToPreviewPropertyPath != null) {
        MotionCurve curve = getMotionCurve(res, curveToPreviewPropertyPath);
        if (curve != null) {
          curve.setPoints(new int[] { frameToPreview }, new double[] { curveValueToPreview });
          curve.setInterpolation(Interpolation.LINEAR);
          curve.setEnabled(true);
        }
      }
      return res;
    }
    return null;
  }

  public void setFlameToPreview(Flame pFlameToPreview, MotionCurve pCurveToPreview) {
    flameToPreview = pFlameToPreview;
    frameToPreview = pFlameToPreview.getFrame();
    curveToPreview = pCurveToPreview;
    curveValueToPreview = 0.0;
    curveToPreviewPropertyPath = getPropertyPath(flameToPreview, curveToPreview);
    //    System.out.println(curveToPreviewPropertyPath);
    //    MotionCurve curve = getMotionCurve(flameToPreview, curveToPreviewPropertyPath);
    //    System.out.println(curve);
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

  private final String PATH_SEPARATOR = "#";
  private JPanel panel_3;
  private JPanel panel_4;

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
          Set<?> keySet = map.keySet();
          for (Object key : keySet) {
            String pathExt = field.getName() + "[" + key.toString() + "]";
            String subPath = pPath == null ? pathExt : pPath + PATH_SEPARATOR + pathExt;
            String subResult = findProperty(map.get(key), pProperty, subPath);
            if (subResult != null) {
              return subResult;
            }
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
}
