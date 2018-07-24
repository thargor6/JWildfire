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
package org.jwildfire.create.tina.script.ui;

import java.awt.Dialog.ModalityType;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.script.ScriptParam;
import org.jwildfire.create.tina.script.ScriptRunner;
import org.jwildfire.create.tina.script.ScriptRunnerEnvironment;
import org.jwildfire.create.tina.swing.JWFNumberField;
import org.jwildfire.swing.ErrorHandler;

public class ScriptParamsForm implements ScriptRunnerEnvironment {
  private final ErrorHandler errorHandler;
  private final ScriptParamsDialog dialog;
  private final Map<String, ScriptParam> params = new HashMap<String, ScriptParam>();
  private final ScriptParam emptyParam = new ScriptParam("");
  private final List<Object> namedControls = new ArrayList<Object>();
  private ScriptRunnerEnvironment scriptRunnerEnvironment;
  private ScriptRunner scriptRunner;

  public ScriptParamsForm(JInternalFrame pParent, ErrorHandler pErrorHandler) {
    errorHandler = pErrorHandler;
    Window owner = SwingUtilities.getWindowAncestor(pParent);
    dialog = new ScriptParamsDialog(owner);

    int dialogWidth = 500;
    int dialogHeight = 400;
    dialog.setSize(dialogWidth, dialogHeight);
    dialog.setLocationRelativeTo(owner);

    dialog.getRunScriptButton().addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        runScript();
      }
    });
  }

  public ScriptParamsForm(JFrame pParent, ErrorHandler pErrorHandler) {
    errorHandler = pErrorHandler;
    Window owner = SwingUtilities.getWindowAncestor(pParent);
    dialog = new ScriptParamsDialog(owner);

    int dialogWidth = 500;
    int dialogHeight = 400;
    dialog.setSize(dialogWidth, dialogHeight);
    dialog.setLocationRelativeTo(owner);

    dialog.getRunScriptButton().addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        runScript();
      }
    });
  }

  protected void runScript() {
    try {
      collectFieldValues();
      if (scriptRunner != null) {
        scriptRunner.run(this);
      }
    }
    catch (Exception ex) {
      errorHandler.handleError(ex);
    }
  }

  public boolean showModal(ScriptRunnerEnvironment pScriptRunnerEnvironment, ScriptRunner pScriptRunner) {
    scriptRunnerEnvironment = pScriptRunnerEnvironment;
    scriptRunner = pScriptRunner;
    dialog.setModalityType(ModalityType.APPLICATION_MODAL);
    dialog.setVisible(true);
    return true;
  }

  private void collectFieldValues() {
    params.clear();
    for (Object control : namedControls) {
      if (control instanceof JWFNumberField) {
        JWFNumberField numberField = (JWFNumberField) control;
        params.put(numberField.getName(), new ScriptParam(numberField.getDoubleValue()));
      }
      else if (control instanceof JTextField) {
        JTextField textField = (JTextField) control;
        params.put(textField.getName(), new ScriptParam(textField.getText()));
      }
      else if (control instanceof JComboBox) {
        JComboBox comboBox = (JComboBox) control;
        params.put(comboBox.getName(), new ScriptParam((String) comboBox.getSelectedItem()));
      }
      else if (control instanceof JCheckBox) {
        JCheckBox checkBox = (JCheckBox) control;
        params.put(checkBox.getName(), new ScriptParam(checkBox.isSelected()));
      }
      else {
        throw new IllegalStateException(control.getClass().getName());
      }
    }
  }

  public JPanel addContainer(String pCaption) {
    JPanel panel = new JPanel();
    dialog.getRootTabbedPane().addTab(pCaption, null, panel, null);
    panel.setLayout(null);
    return panel;
  }

  public void registerNamedControl(Object pField) {
    if (!namedControls.contains(pField)) {
      namedControls.add(pField);
    }
  }

  @Override
  public Flame getCurrFlame() {
    return scriptRunnerEnvironment.getCurrFlame();
  }

  @Override
  public Flame getCurrFlame(boolean autoGenerateIfEmpty) {
    return scriptRunnerEnvironment.getCurrFlame(autoGenerateIfEmpty);
  }

  @Override
  public void setCurrFlame(Flame pFlame) {
    scriptRunnerEnvironment.setCurrFlame(pFlame);
  }

  @Override
  public Layer getCurrLayer() {
    return scriptRunnerEnvironment.getCurrLayer();
  }

  @Override
  public void refreshUI() {
    scriptRunnerEnvironment.refreshUI();
  }

  @Override
  public ScriptParam getParamByName(String pName) {
    ScriptParam res = params.get(pName);
    return res != null ? res : emptyParam;
  }

  @Override
  public void setScriptProperty(ScriptRunner runner, String propName, String propVal) {
    scriptRunnerEnvironment.setScriptProperty(runner, propName, propVal);
  }

  @Override
  public String getScriptProperty(ScriptRunner runner, String propName) {
    return scriptRunnerEnvironment.getScriptProperty(runner, propName);
  }

  @Override
  public String getScriptProperty(ScriptRunner runner, String propName, String defaultVal) {
    return scriptRunnerEnvironment.getScriptProperty(runner, propName, defaultVal);
  }

}
