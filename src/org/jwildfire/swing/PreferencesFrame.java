package org.jwildfire.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.swing.*;

import org.jwildfire.base.Prefs;

public class PreferencesFrame extends JFrame {
  private static final long serialVersionUID = 1L;
  private JWildfire desktop = null;// @jve:decl-index=0:
  private MainController mainController = null; //  @jve:decl-index=0:
  private Prefs prefs = null; //  @jve:decl-index=0:
  private Prefs editPrefs = null; //  @jve:decl-index=0:
  private JPanel jContentPane = null;
  private JPanel southPanel = null;
  private JButton savePrefsButton = null;
  private JButton cancelButton = null;

  /**
   * This is the xxx default constructor
   */
  public PreferencesFrame() {
    super();
    setResizable(true);
    initialize();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(800, 600);
    this.setLocation(200, 80);
    this.setTitle("Preferences");
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
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
      jContentPane.add(getSouthPanel(), BorderLayout.SOUTH);
      jContentPane.add(getMainPanel(), BorderLayout.CENTER);
    }
    return jContentPane;
  }

  public void initApp() {
    // TODO Auto-generated method stub

  }

  public void setDesktop(JWildfire desktop) {
    this.desktop = desktop;
  }

  public void enableControls() {
    // TODO Auto-generated method stub

  }

  /**
   * This method initializes southPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getSouthPanel() {
    if (southPanel == null) {
      southPanel = new JPanel();
      southPanel.setLayout(new FlowLayout());
      southPanel.setPreferredSize(new Dimension(0, 34));
      southPanel.add(getSavePrefsButton(), null);
      southPanel.add(getCancelButton(), null);
    }
    return southPanel;
  }

  /**
   * This method initializes savePrefsButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getSavePrefsButton() {
    if (savePrefsButton == null) {
      savePrefsButton = new JButton();
      savePrefsButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      savePrefsButton.setText("Save and Close");
      savePrefsButton.setMnemonic(KeyEvent.VK_S);
      savePrefsButton.setPreferredSize(new Dimension(125, 24));
      savePrefsButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          try {
            prefs.assign(editPrefs);
            prefs.saveToFile();
          }
          catch (Exception ex) {
            mainController.handleError(ex);
          }
          setVisible(false);
          desktop.enableControls();
        }
      });
    }
    return savePrefsButton;
  }

  /**
   * This method initializes cancelButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getCancelButton() {
    if (cancelButton == null) {
      cancelButton = new JButton();
      cancelButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      cancelButton.setText("Cancel and Close");
      cancelButton.setMnemonic(KeyEvent.VK_C);
      cancelButton.setPreferredSize(new Dimension(125, 24));
      cancelButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          editPrefs.assign(prefs);
          switchCreatorPropertiesPanel();
          setVisible(false);
          desktop.enableControls();
        }
      });
    }
    return cancelButton;
  }

  public void setPrefs(Prefs pPrefs) {
    prefs = pPrefs;
    editPrefs = Prefs.newInstance();
    editPrefs.assign(prefs);
    switchCreatorPropertiesPanel();
  }

  void setMainController(MainController mainController) {
    this.mainController = mainController;
  }

  private Object prefsPropertyPanel = null; // @jve:decl-index=0:
  private JPanel mainPanel = null;

  void switchCreatorPropertiesPanel() {
    if (prefsPropertyPanel != null)
      mainPanel.remove((JPanel) prefsPropertyPanel);

    prefsPropertyPanel = new PropertyPanel(editPrefs);

    mainPanel.add((JPanel) prefsPropertyPanel,
        BorderLayout.CENTER);
    mainPanel.invalidate();
    mainPanel.validate();
  }

  /**
   * This method initializes mainPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getMainPanel() {
    if (mainPanel == null) {
      mainPanel = new JPanel();
      mainPanel.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      mainPanel.setLayout(new BorderLayout());
      mainPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    }
    return mainPanel;
  }

} //  @jve:decl-index=0:visual-constraint="10,10"
