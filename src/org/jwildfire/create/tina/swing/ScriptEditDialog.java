/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import jsyntaxpane.DefaultSyntaxKit;
import jsyntaxpane.util.Configuration;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.script.swing.JWFScriptUserNode;
import org.jwildfire.swing.ErrorHandler;

public class ScriptEditDialog extends JDialog {
  private static final long serialVersionUID = 1L;
  private JPanel jContentPane = null;
  private JPanel centerPanel = null;
  private JPanel scriptPanel = null;
  private JButton okButton = null;
  private JButton cancelButton = null;
  private JScrollPane editrScrollPane = null;
  private JEditorPane scriptEditor = null;
  private final ErrorHandler errorHandler;
  private JTabbedPane tabbedPane;
  private JPanel panel;
  private JPanel descriptionPanel;
  private JScrollPane scrollPane;
  private JEditorPane descriptionEditor;
  private JWFScriptUserNode scriptNode;
  private final TinaController tinaController;

  /**
   * @param pOwner
   */
  public ScriptEditDialog(TinaController pTinaController, Window pOwner, ErrorHandler pErrorHandler) {
    super(pOwner);
    errorHandler = pErrorHandler;
    tinaController = pTinaController;
    initialize();
    Rectangle rootBounds = pOwner.getBounds();
    Dimension size = getSize();
    setLocation(rootBounds.x + (rootBounds.width - size.width) / 2, rootBounds.y + (rootBounds.height - size.height) / 2);
    if (Prefs.getPrefs().isTinaAdvancedCodeEditor()) {
      try {
        DefaultSyntaxKit.initKit();
        // setting font size (and style) based on suggestion found on thread at
        // https://code.google.com/p/jsyntaxpane/issues/detail?id=1
        // reproducing relevant text of thread here, since Google Code is shutting down 
        // and issues text may not be retrievable soon
        //   -----------------------------------------------------------------------------
        //     #5 benneybopper, Feb 6 2013:
        //     After a brief but hilarious foray into the source code, 
        //     I have stumbled upon some marvelous undocumented code. 
        //     Set the "DefaultFont" property to the font name followed by the size eg.
        //           DefaultSyntaxKit.initKit()
        //           // override default syntax values
        //           Configuration config = DefaultSyntaxKit.getConfig(DefaultSyntaxKit.class);
        //           config.put("DefaultFont","monospaced 14");
        //   -----------------------------------------------------------------------------
        //     #6 trejkaz, Jul 5 2014:
        //     This is a good trick. Monospaced is a much better default than Courier, 
        //     because it will automatically pick the right monospaced font for the platform.
        //   -----------------------------------------------------------------------------
        Configuration config = DefaultSyntaxKit.getConfig(DefaultSyntaxKit.class);
        config.put("DefaultFont", "monospaced " + Integer.toString(Prefs.getPrefs().getTinaAdvancedCodeEditorFontSize()));
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
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
      jContentPane.add(getCenterPanel(), BorderLayout.CENTER);
      jContentPane.add(getPanel(), BorderLayout.NORTH);
    }
    return jContentPane;
  }

  /**
   * This method initializes topPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getCenterPanel() {
    if (centerPanel == null) {
      centerPanel = new JPanel();
      centerPanel.setPreferredSize(new Dimension(0, 10));
      centerPanel.setLayout(new BorderLayout(0, 0));
      centerPanel.add(getTabbedPane(), BorderLayout.CENTER);
    }
    return centerPanel;
  }

  /**
   * This method initializes centerPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getScriptPanel() {
    if (scriptPanel == null) {
      scriptPanel = new JPanel();
      scriptPanel.setLayout(new BorderLayout());
      scriptPanel.setFont(new Font("Dialog", Font.PLAIN, 10));
      scriptPanel.add(getEditrScrollPane(), BorderLayout.CENTER);
    }
    return scriptPanel;
  }

  /**
   * This method initializes okButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getOkButton() {
    if (okButton == null) {
      okButton = new JButton();
      okButton.setBounds(6, 6, 125, 24);
      okButton.setPreferredSize(new Dimension(125, 24));
      okButton.setText("Save and Close");
      okButton.setMnemonic(KeyEvent.VK_O);
      okButton.setSelected(true);
      okButton.setFont(new Font("Dialog", Font.BOLD, 10));
      okButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          saveScriptAndClose();
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
      cancelButton.setBounds(653, 6, 125, 24);
      cancelButton.setPreferredSize(new Dimension(125, 24));
      cancelButton.setMnemonic(KeyEvent.VK_C);
      cancelButton.setText("Cancel");
      cancelButton.setFont(new Font("Dialog", Font.BOLD, 10));
      cancelButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
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
      editrScrollPane.setViewportView(getScriptEditor());
    }
    return editrScrollPane;
  }

  /**
   * This method initializes editorTextArea	
   * 	
   * @return javax.swing.JTextArea	
   */
  private JEditorPane getScriptEditor() {
    if (scriptEditor == null) {
      scriptEditor = new JEditorPane();

      // if using advanced editor color fix, and one of JWildfire's dark look and feels (HiFi or Noire), 
      //   override look and feel to set scriptEditor background to white, 
      //   to work better with JSyntaxPane text colors
      LookAndFeel laf = UIManager.getLookAndFeel();
      String laf_name = laf.getName();
      boolean using_dark_theme = laf_name.equalsIgnoreCase("HiFi") || laf_name.equalsIgnoreCase("Noire");
      if (using_dark_theme &&
          Prefs.getPrefs().isTinaAdvancedCodeEditor() &&
          Prefs.getPrefs().isTinaAdvancedCodeEditorColorFix()) {
        scriptEditor.setBackground(Color.white);
      }
    }
    return scriptEditor;
  }

  private JTabbedPane getTabbedPane() {
    if (tabbedPane == null) {
      tabbedPane = new JTabbedPane(JTabbedPane.TOP);
      tabbedPane.addTab("Script", null, getScriptPanel(), null);
      tabbedPane.addTab("Description", null, getDescriptionPanel(), null);
    }
    return tabbedPane;
  }

  private JPanel getPanel() {
    if (panel == null) {
      panel = new JPanel();
      panel.setPreferredSize(new Dimension(10, 36));
      panel.setLayout(null);

      JButton btnCompile = new JButton();
      btnCompile.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          compileScript();
        }
      });
      btnCompile.setText("Compile");
      btnCompile.setSize(new Dimension(125, 24));
      btnCompile.setSelected(true);
      btnCompile.setPreferredSize(new Dimension(125, 24));
      btnCompile.setMnemonic(KeyEvent.VK_O);
      btnCompile.setLocation(new Point(327, 8));
      btnCompile.setFont(new Font("Dialog", Font.BOLD, 10));
      btnCompile.setBounds(234, 6, 125, 24);
      panel.add(btnCompile);
      panel.add(getOkButton());
      panel.add(getCancelButton());

      JButton btnRunScript = new JButton();
      btnRunScript.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          runScript();
        }
      });
      btnRunScript.setText("Run Script");
      btnRunScript.setSize(new Dimension(125, 24));
      btnRunScript.setSelected(true);
      btnRunScript.setPreferredSize(new Dimension(125, 24));
      btnRunScript.setMnemonic(KeyEvent.VK_R);
      btnRunScript.setLocation(new Point(327, 8));
      btnRunScript.setFont(new Font("Dialog", Font.BOLD, 10));
      btnRunScript.setBounds(445, 6, 125, 24);
      panel.add(btnRunScript);
    }
    return panel;
  }

  private JPanel getDescriptionPanel() {
    if (descriptionPanel == null) {
      descriptionPanel = new JPanel();
      descriptionPanel.setFont(new Font("Dialog", Font.PLAIN, 10));
      descriptionPanel.setLayout(new BorderLayout());
      descriptionPanel.add(getScrollPane(), BorderLayout.CENTER);
    }
    return descriptionPanel;
  }

  private JScrollPane getScrollPane() {
    if (scrollPane == null) {
      scrollPane = new JScrollPane();
      scrollPane.setViewportView(getDescriptionEditor());
    }
    return scrollPane;
  }

  private JEditorPane getDescriptionEditor() {
    if (descriptionEditor == null) {
      descriptionEditor = new JEditorPane();
    }
    return descriptionEditor;
  }

  public void setScriptNode(JWFScriptUserNode pScriptNode) throws Exception {
    String scriptname = pScriptNode.getUserObject().toString();
    setTitle("Editing " + scriptname);

    String script = pScriptNode.getScript();
    scriptEditor.setText("");
    scriptEditor.setContentType("text/java");
    scriptEditor.setText(script);
    scriptEditor.setCaretPosition(0);

    String description = pScriptNode.getDescription();
    descriptionEditor.setBackground(SystemColor.menu);
    descriptionEditor.setText("");
    descriptionEditor.setContentType("text/plain");
    descriptionEditor.setText(description);
    descriptionEditor.setCaretPosition(0);

    scriptNode = pScriptNode;
  }

  protected void runScript() {
    try {
      tinaController.runScript(scriptNode.getFilename(), scriptEditor.getText());
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  protected void compileScript() {
    try {
      tinaController.compileScript(scriptEditor.getText());
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  protected void saveScriptAndClose() {
    try {
      scriptNode.saveScript(scriptEditor.getText(), descriptionEditor.getText());
      setVisible(false);
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

}
