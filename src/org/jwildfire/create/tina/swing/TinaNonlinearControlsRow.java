package org.jwildfire.create.tina.swing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;

import org.jwildfire.create.tina.variation.VariationFuncList;

public class TinaNonlinearControlsRow {
  private final JComboBox nonlinearVarCmb;
  private final JComboBox nonlinearParamsCmb;
  private final JWFNumberField nonlinearVarREd;
  private final JWFNumberField nonlinearParamsREd;
  private final JButton nonlinearParamsLeftButton;

  public TinaNonlinearControlsRow(JComboBox pNonlinearVarCmb, JComboBox pNonlinearParamsCmb, JWFNumberField pNonlinearVarREd, JWFNumberField pNonlinearParamsREd,
      JButton pNonlinearParamsLeftButton) {
    nonlinearVarCmb = pNonlinearVarCmb;
    nonlinearParamsCmb = pNonlinearParamsCmb;
    nonlinearVarREd = pNonlinearVarREd;
    nonlinearParamsREd = pNonlinearParamsREd;
    nonlinearParamsLeftButton = pNonlinearParamsLeftButton;
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

}
