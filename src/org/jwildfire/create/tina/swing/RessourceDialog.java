package org.jwildfire.create.tina.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class RessourceDialog extends JDialog {

  private static final long serialVersionUID = 1L;
  private JPanel jContentPane = null;
  private JPanel bottomPanel = null;
  private JPanel topPanel = null;
  private JPanel centerPanel = null;
  private JButton okButton = null;
  private JButton cancelButton = null;
  private JScrollPane editrScrollPane = null;
  private JTextArea editorTextArea = null;
  private boolean confirmed = false;

  /**
   * @param owner
   */
  public RessourceDialog(Window owner) {
    super(owner);
    initialize();
    Rectangle rootBounds = owner.getBounds();
    Dimension size = getSize();
    setLocation(rootBounds.x + (rootBounds.width - size.width) / 2, rootBounds.y + (rootBounds.height - size.height) / 2);
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(800, 600);
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
      centerPanel.add(getEditrScrollPane(), BorderLayout.CENTER);
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
      okButton.setLocation(new Point(327, 8));
      okButton.setSelected(true);
      okButton.setFont(new Font("Dialog", Font.BOLD, 10));
      okButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          confirmed = true;
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
      cancelButton.setLocation(new Point(457, 8));
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

  /**
   * This method initializes editrScrollPane	
   * 	
   * @return javax.swing.JScrollPane	
   */
  private JScrollPane getEditrScrollPane() {
    if (editrScrollPane == null) {
      editrScrollPane = new JScrollPane();
      editrScrollPane.setViewportView(getEditorTextArea());
    }
    return editrScrollPane;
  }

  /**
   * This method initializes editorTextArea	
   * 	
   * @return javax.swing.JTextArea	
   */
  private JTextArea getEditorTextArea() {
    if (editorTextArea == null) {
      editorTextArea = new JTextArea();
    }
    return editorTextArea;
  }

  public String getRessourceValue() {
    return editorTextArea.getText();
  }

  public void setRessourceValue(String pRessourceValue) {
    editorTextArea.setText(pRessourceValue);
  }

  public void setRessourceName(String pRessourceName) {
    setTitle("Editing " + (pRessourceName != null ? pRessourceName : ""));
  }

  public boolean isConfirmed() {
    return confirmed;
  }

} //  @jve:decl-index=0:visual-constraint="10,10"
