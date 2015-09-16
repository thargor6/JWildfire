package org.jwildfire.create.tina.script.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.jwildfire.base.Tools;

public class ScriptParamsDialog extends JDialog {
  private static final long serialVersionUID = 1L;
  private final JPanel contentPanel = new JPanel();
  private JTabbedPane rootTabbedPane;
  private JButton runScriptButton;

  public ScriptParamsDialog(Window owner) {
    super(owner);
    setTitle(Tools.APP_TITLE);
    setSize(539, 339);

    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new BorderLayout(0, 0));
    {
      rootTabbedPane = new JTabbedPane(JTabbedPane.TOP);
      contentPanel.add(rootTabbedPane);
    }
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
        runScriptButton = new JButton("Run Script");
        runScriptButton.setActionCommand("OK");
        buttonPane.add(runScriptButton);
        getRootPane().setDefaultButton(runScriptButton);
      }
      {
        JButton cancelButton = new JButton("Close");
        cancelButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
      }
    }
  }

  public JTabbedPane getRootTabbedPane() {
    return rootTabbedPane;
  }

  public JButton getRunScriptButton() {
    return runScriptButton;
  }
}
