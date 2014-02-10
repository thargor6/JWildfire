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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jwildfire.envelope.Envelope;
import org.jwildfire.envelope.EnvelopePanel;

public class EnvelopeDialog extends JDialog {
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

  /**
   * @param pOwner
   */
  public EnvelopeDialog(Window pOwner, Envelope pEnvelope, boolean pAllowRemove) {
    super(pOwner);
    initialize();
    envelopeInterpolationCmb.addItem(Envelope.Interpolation.SPLINE);
    envelopeInterpolationCmb.addItem(Envelope.Interpolation.BEZIER);
    envelopeInterpolationCmb.addItem(Envelope.Interpolation.LINEAR);
    getEnvelopePanel().setEnvelope(pEnvelope);
    Rectangle rootBounds = pOwner.getBounds();
    Dimension size = getSize();
    getBtnRemove().setVisible(pAllowRemove);
    ctrl = new EnvelopeDlgController(pEnvelope, getEnvelopeAddPointButton(), getEnvelopeRemovePointButton(), getEnvelopeClearButton(),
        getEnvelopeXMinREd(), getEnvelopeXMaxREd(), getEnvelopeYMinREd(), getEnvelopeYMaxREd(), getEnvelopeXREd(),
        getEnvelopeYREd(), getEnvelopeInterpolationCmb(), getEnvelopeViewAllButton(), getEnvelopeViewLeftButton(),
        getEnvelopeViewRightButton(), getEnvelopeViewUpButton(), getEnvelopeViewDownButton(), getEnvelopePanel(), getEnvelopeInterpolationCmb());
    ctrl.setNoRefresh(true);
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
    this.setSize(907, 600);
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
      btnRemove.setText("Disable/Remove");
      btnRemove.setSize(new Dimension(125, 24));
      btnRemove.setSelected(true);
      btnRemove.setPreferredSize(new Dimension(125, 24));
      btnRemove.setMnemonic(KeyEvent.VK_R);
      btnRemove.setLocation(new Point(276, 10));
      btnRemove.setFont(new Font("Dialog", Font.BOLD, 10));
      btnRemove.setBounds(619, 10, 125, 24);
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
      okButton.setText("OK");
      okButton.setMnemonic(KeyEvent.VK_O);
      okButton.setSize(new Dimension(125, 24));
      okButton.setLocation(new Point(276, 10));
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
      cancelButton.setText("Cancel");
      cancelButton.setSize(new Dimension(125, 24));
      cancelButton.setLocation(new Point(406, 10));
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

      envelopeAddPointButton = new JButton();
      envelopeAddPointButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          ctrl.addPoint();
        }
      });
      envelopeAddPointButton.setText("Add Point");
      envelopeAddPointButton.setSize(new Dimension(141, 26));
      envelopeAddPointButton.setPreferredSize(new Dimension(141, 26));
      envelopeAddPointButton.setLocation(new Point(5, 5));
      envelopeAddPointButton.setBounds(6, 6, 141, 26);
      panel.add(envelopeAddPointButton);

      envelopeRemovePointButton = new JButton();
      envelopeRemovePointButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          ctrl.removePoint();
        }
      });
      envelopeRemovePointButton.setText("Remove Point");
      envelopeRemovePointButton.setSize(new Dimension(141, 26));
      envelopeRemovePointButton.setPreferredSize(new Dimension(141, 26));
      envelopeRemovePointButton.setLocation(new Point(5, 37));
      envelopeRemovePointButton.setBounds(6, 38, 141, 26);
      panel.add(envelopeRemovePointButton);

      envelopeViewUpButton = new JButton();
      envelopeViewUpButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          ctrl.viewUp();
        }
      });
      envelopeViewUpButton.setText("^");
      envelopeViewUpButton.setSize(new Dimension(48, 26));
      envelopeViewUpButton.setPreferredSize(new Dimension(48, 26));
      envelopeViewUpButton.setLocation(new Point(51, 99));
      envelopeViewUpButton.setBounds(52, 100, 48, 26);
      panel.add(envelopeViewUpButton);

      envelopeViewLeftButton = new JButton();
      envelopeViewLeftButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          ctrl.viewLeft();
        }
      });
      envelopeViewLeftButton.setText("<");
      envelopeViewLeftButton.setSize(new Dimension(48, 26));
      envelopeViewLeftButton.setPreferredSize(new Dimension(48, 26));
      envelopeViewLeftButton.setLocation(new Point(5, 126));
      envelopeViewLeftButton.setBounds(6, 127, 48, 26);
      panel.add(envelopeViewLeftButton);

      envelopeViewRightButton = new JButton();
      envelopeViewRightButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          ctrl.viewRight();
        }
      });
      envelopeViewRightButton.setText(">");
      envelopeViewRightButton.setSize(new Dimension(48, 26));
      envelopeViewRightButton.setPreferredSize(new Dimension(48, 26));
      envelopeViewRightButton.setLocation(new Point(98, 126));
      envelopeViewRightButton.setBounds(99, 127, 48, 26);
      panel.add(envelopeViewRightButton);

      envelopeViewDownButton = new JButton();
      envelopeViewDownButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          ctrl.viewDown();
        }
      });
      envelopeViewDownButton.setText("v");
      envelopeViewDownButton.setSize(new Dimension(48, 26));
      envelopeViewDownButton.setPreferredSize(new Dimension(48, 26));
      envelopeViewDownButton.setLocation(new Point(51, 153));
      envelopeViewDownButton.setBounds(52, 154, 48, 26);
      panel.add(envelopeViewDownButton);

      envelopeViewAllButton = new JButton();
      envelopeViewAllButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          ctrl.viewAll();
        }
      });
      envelopeViewAllButton.setText("View all");
      envelopeViewAllButton.setSize(new Dimension(141, 24));
      envelopeViewAllButton.setPreferredSize(new Dimension(141, 26));
      envelopeViewAllButton.setLocation(new Point(5, 190));
      envelopeViewAllButton.setBounds(6, 191, 141, 26);
      panel.add(envelopeViewAllButton);

      envelopeClearButton = new JButton();
      envelopeClearButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          ctrl.clearEnvelope();
        }
      });
      envelopeClearButton.setText("Clear");
      envelopeClearButton.setSize(new Dimension(141, 24));
      envelopeClearButton.setPreferredSize(new Dimension(141, 26));
      envelopeClearButton.setLocation(new Point(5, 240));
      envelopeClearButton.setBounds(6, 241, 141, 26);
      panel.add(envelopeClearButton);
    }
    return panel;
  }

  private JPanel getPanel_1() {
    if (panel_1 == null) {
      panel_1 = new JPanel();
      panel_1.setPreferredSize(new Dimension(10, 70));
      panel_1.setLayout(null);

      envelopeXMinREd = new JWFNumberField();
      envelopeXMinREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          ctrl.editFieldChanged();
        }
      });
      envelopeXMinREd.setSize(new Dimension(80, 26));
      envelopeXMinREd.setPreferredSize(new Dimension(80, 26));
      envelopeXMinREd.setLocation(new Point(45, 5));
      envelopeXMinREd.setBounds(119, 6, 80, 26);
      panel_1.add(envelopeXMinREd);

      JLabel lblFrameRange = new JLabel();
      lblFrameRange.setText("Frame range");
      lblFrameRange.setSize(new Dimension(38, 26));
      lblFrameRange.setPreferredSize(new Dimension(38, 26));
      lblFrameRange.setLocation(new Point(5, 5));
      lblFrameRange.setHorizontalAlignment(SwingConstants.RIGHT);
      lblFrameRange.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
      lblFrameRange.setBounds(6, 7, 114, 26);
      panel_1.add(lblFrameRange);

      JLabel lblAmplitudeRange = new JLabel();
      lblAmplitudeRange.setText("Amplitude range");
      lblAmplitudeRange.setSize(new Dimension(38, 26));
      lblAmplitudeRange.setPreferredSize(new Dimension(38, 26));
      lblAmplitudeRange.setLocation(new Point(5, 34));
      lblAmplitudeRange.setHorizontalAlignment(SwingConstants.RIGHT);
      lblAmplitudeRange.setBounds(6, 35, 114, 26);
      panel_1.add(lblAmplitudeRange);

      envelopeYMinREd = new JWFNumberField();
      envelopeYMinREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          ctrl.editFieldChanged();
        }
      });
      envelopeYMinREd.setSize(new Dimension(80, 26));
      envelopeYMinREd.setPreferredSize(new Dimension(80, 26));
      envelopeYMinREd.setLocation(new Point(45, 34));
      envelopeYMinREd.setBounds(119, 35, 80, 26);
      panel_1.add(envelopeYMinREd);

      envelopeXMaxREd = new JWFNumberField();
      envelopeXMaxREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          ctrl.editFieldChanged();
        }
      });
      envelopeXMaxREd.setSize(new Dimension(80, 26));
      envelopeXMaxREd.setPreferredSize(new Dimension(80, 26));
      envelopeXMaxREd.setLocation(new Point(168, 5));
      envelopeXMaxREd.setBounds(201, 7, 80, 26);
      panel_1.add(envelopeXMaxREd);

      envelopeYMaxREd = new JWFNumberField();
      envelopeYMaxREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          ctrl.editFieldChanged();
        }
      });
      envelopeYMaxREd.setSize(new Dimension(80, 26));
      envelopeYMaxREd.setPreferredSize(new Dimension(80, 26));
      envelopeYMaxREd.setLocation(new Point(168, 34));
      envelopeYMaxREd.setBounds(201, 36, 80, 26);
      panel_1.add(envelopeYMaxREd);

      JLabel lblFrame = new JLabel();
      lblFrame.setText("Frame");
      lblFrame.setSize(new Dimension(38, 26));
      lblFrame.setPreferredSize(new Dimension(38, 26));
      lblFrame.setLocation(new Point(277, 5));
      lblFrame.setHorizontalAlignment(SwingConstants.RIGHT);
      lblFrame.setBounds(346, 7, 87, 26);
      panel_1.add(lblFrame);

      JLabel lblAmplitude = new JLabel();
      lblAmplitude.setText("Amplitude");
      lblAmplitude.setSize(new Dimension(38, 26));
      lblAmplitude.setPreferredSize(new Dimension(38, 26));
      lblAmplitude.setLocation(new Point(277, 34));
      lblAmplitude.setHorizontalAlignment(SwingConstants.RIGHT);
      lblAmplitude.setBounds(346, 34, 87, 26);
      panel_1.add(lblAmplitude);

      envelopeYREd = new JWFNumberField();
      envelopeYREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          ctrl.editFieldChanged();
        }
      });
      envelopeYREd.setSize(new Dimension(80, 26));
      envelopeYREd.setPreferredSize(new Dimension(80, 26));
      envelopeYREd.setLocation(new Point(317, 34));
      envelopeYREd.setBounds(435, 34, 80, 26);
      panel_1.add(envelopeYREd);

      envelopeXREd = new JWFNumberField();
      envelopeXREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          ctrl.editFieldChanged();
        }
      });
      envelopeXREd.setSize(new Dimension(80, 26));
      envelopeXREd.setPreferredSize(new Dimension(80, 26));
      envelopeXREd.setLocation(new Point(317, 5));
      envelopeXREd.setBounds(435, 5, 80, 26);
      panel_1.add(envelopeXREd);

      JLabel label_6 = new JLabel();
      label_6.setText("Interpolation");
      label_6.setSize(new Dimension(81, 26));
      label_6.setPreferredSize(new Dimension(81, 26));
      label_6.setLocation(new Point(416, 5));
      label_6.setHorizontalAlignment(SwingConstants.RIGHT);
      label_6.setBounds(515, 5, 81, 26);
      panel_1.add(label_6);

      envelopeInterpolationCmb = new JComboBox();
      envelopeInterpolationCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (ctrl != null) {
            ctrl.interpolationCmbChanged();
          }
        }
      });
      envelopeInterpolationCmb.setSize(new Dimension(141, 26));
      envelopeInterpolationCmb.setPreferredSize(new Dimension(141, 26));
      envelopeInterpolationCmb.setLocation(new Point(499, 5));
      envelopeInterpolationCmb.setBounds(598, 5, 141, 26);
      panel_1.add(envelopeInterpolationCmb);
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
} //  @jve:decl-index=0:visual-constraint="10,10"
