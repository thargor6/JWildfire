package org.jwildfire.create.tina.swing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToggleButton;

import org.jwildfire.create.tina.variation.VariationFuncList;

public class TinaNonlinearControlsRow {
  private final JComboBox nonlinearVarCmb;
  private final JComboBox nonlinearParamsCmb;
  private final JWFNumberField nonlinearVarREd;
  private final JWFNumberField nonlinearParamsREd;
  private final JButton nonlinearParamsLeftButton;
  private final JToggleButton nonlinearParamsPreButton;
  private final JToggleButton nonlinearParamsPostButton;
  private final JButton nonlinearParamsUpButton;

  public TinaNonlinearControlsRow(JComboBox pNonlinearVarCmb, JComboBox pNonlinearParamsCmb, JWFNumberField pNonlinearVarREd, JWFNumberField pNonlinearParamsREd,
      JButton pNonlinearParamsLeftButton, JToggleButton pNonlinearParamsPreButton, JToggleButton pNonlinearParamsPostButton, JButton pNonlinearParamsUpButton) {
    nonlinearVarCmb = pNonlinearVarCmb;
    nonlinearParamsCmb = pNonlinearParamsCmb;
    nonlinearVarREd = pNonlinearVarREd;
    nonlinearParamsREd = pNonlinearParamsREd;
    nonlinearParamsLeftButton = pNonlinearParamsLeftButton;
    nonlinearParamsPreButton = pNonlinearParamsPreButton;
    nonlinearParamsPostButton = pNonlinearParamsPostButton;
    nonlinearParamsUpButton = pNonlinearParamsUpButton;
  }

  public void initControls() {
    nonlinearVarCmb.removeAllItems();
    List<String> nameList = new ArrayList<String>();
    nameList.addAll(VariationFuncList.getNameList());
    Collections.sort(nameList);
    nonlinearVarCmb.addItem(null);
    for (String name : nameList) {
      nonlinearVarCmb.addItem(name);
    }
    nonlinearVarCmb.setSelectedIndex(-1);

    nonlinearParamsCmb.removeAllItems();
    nonlinearParamsCmb.setSelectedIndex(-1);

    nonlinearParamsPreButton.setSelected(false);
    nonlinearParamsPostButton.setSelected(false);
  }

  public JComboBox getNonlinearVarCmb() {
    return nonlinearVarCmb;
  }

  public JComboBox getNonlinearParamsCmb() {
    return nonlinearParamsCmb;
  }

  public JWFNumberField getNonlinearVarREd() {
    return nonlinearVarREd;
  }

  public JWFNumberField getNonlinearParamsREd() {
    return nonlinearParamsREd;
  }

  public JButton getNonlinearParamsLeftButton() {
    return nonlinearParamsLeftButton;
  }

  public JToggleButton getNonlinearParamsPreButton() {
    return nonlinearParamsPreButton;
  }

  public JToggleButton getNonlinearParamsPostButton() {
    return nonlinearParamsPostButton;
  }

  public JButton getNonlinearParamsUpButton() {
    return nonlinearParamsUpButton;
  }

}
