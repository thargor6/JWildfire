package org.jwildfire.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.jwildfire.base.Prefs;

public class PreferencesInternalFrame extends JInternalFrame {
  private static final long serialVersionUID = 1L;
  private Desktop desktop = null;// @jve:decl-index=0:
  private MainController mainController = null; //  @jve:decl-index=0:
  private Prefs prefs = null; //  @jve:decl-index=0:
  private JPanel jContentPane = null;
  private JPanel southPanel = null;
  private JButton savePrefsButton = null;
  private JButton cancelButton = null;
  private JTabbedPane mainTabbedPane = null;
  private JPanel generalPanel = null;
  private JPanel pathsPanel = null;
  private JLabel imagesPathLbl = null;
  private JTextField imagesPathREd = null;
  private JButton imagesPathBtn = null;

  /**
   * This is the xxx default constructor
   */
  public PreferencesInternalFrame() {
    super();
    initialize();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(768, 561);
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
      jContentPane.add(getMainTabbedPane(), BorderLayout.CENTER);
    }
    return jContentPane;
  }

  public void initApp() {
    // TODO Auto-generated method stub

  }

  public void setDesktop(Desktop desktop) {
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
      savePrefsButton.setFont(new Font("Dialog", Font.BOLD, 10));
      savePrefsButton.setText("Save and Close");
      savePrefsButton.setMnemonic(KeyEvent.VK_S);
      savePrefsButton.setPreferredSize(new Dimension(125, 24));
      savePrefsButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          try {
            prefs.saveToFromFile();
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
      cancelButton.setFont(new Font("Dialog", Font.BOLD, 10));
      cancelButton.setText("Cancel and Close");
      cancelButton.setMnemonic(KeyEvent.VK_C);
      cancelButton.setPreferredSize(new Dimension(125, 24));
      cancelButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          setVisible(false);
          desktop.enableControls();
        }
      });
    }
    return cancelButton;
  }

  public void setPrefs(Prefs pPrefs) {
    prefs = pPrefs;
  }

  /**
   * This method initializes mainTabbedPane	
   * 	
   * @return javax.swing.JTabbedPane	
   */
  private JTabbedPane getMainTabbedPane() {
    if (mainTabbedPane == null) {
      mainTabbedPane = new JTabbedPane();
      mainTabbedPane.setFont(new Font("Dialog", Font.BOLD, 10));
      mainTabbedPane.addTab("General", null, getGeneralPanel(), null);
      mainTabbedPane.addTab("Paths", null, getPathsPanel(), null);
    }
    return mainTabbedPane;
  }

  /**
   * This method initializes generalPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getGeneralPanel() {
    if (generalPanel == null) {
      generalPanel = new JPanel();
      generalPanel.setLayout(null);
    }
    return generalPanel;
  }

  /**
   * This method initializes pathsPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getPathsPanel() {
    if (pathsPanel == null) {
      imagesPathLbl = new JLabel();
      imagesPathLbl.setPreferredSize(new Dimension(94, 22));
      imagesPathLbl.setText("Images");
      imagesPathLbl.setSize(new Dimension(94, 22));
      imagesPathLbl.setLocation(new Point(4, 4));
      imagesPathLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      pathsPanel = new JPanel();
      pathsPanel.setLayout(null);
      pathsPanel.add(imagesPathLbl, null);
      pathsPanel.add(getImagesPathREd(), null);
      pathsPanel.add(getImagesPathBtn(), null);
    }
    return pathsPanel;
  }

  /**
   * This method initializes imagesPathREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JTextField getImagesPathREd() {
    if (imagesPathREd == null) {
      imagesPathREd = new JTextField();
      imagesPathREd.setPreferredSize(new Dimension(55, 22));
      imagesPathREd.setText("");
      imagesPathREd.setSize(new Dimension(603, 22));
      imagesPathREd.setLocation(new Point(100, 5));
      imagesPathREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return imagesPathREd;
  }

  /**
   * This method initializes imagesPathBtn	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getImagesPathBtn() {
    if (imagesPathBtn == null) {
      imagesPathBtn = new JButton();
      imagesPathBtn.setPreferredSize(new Dimension(42, 24));
      imagesPathBtn.setText("...");
      imagesPathBtn.setLocation(new Point(706, 4));
      imagesPathBtn.setSize(new Dimension(42, 24));
      imagesPathBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return imagesPathBtn;
  }

  void setMainController(MainController mainController) {
    this.mainController = mainController;
  }

} //  @jve:decl-index=0:visual-constraint="10,10"
