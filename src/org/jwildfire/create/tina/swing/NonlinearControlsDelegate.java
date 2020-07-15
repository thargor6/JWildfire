/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2020 Andreas Maschke

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

import com.l2fprod.common.swing.JFontChooser;
import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.variation.RessourceType;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;
import org.jwildfire.swing.ImageFileChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.io.File;
import java.util.Map;

public class NonlinearControlsDelegate {
  private final TinaController owner;
  private final TinaControllerData data;
  private VariationControlsDelegate[] variationControlsDelegates;
  private JPanel nonlinearParamsPanel;
  private int nonlinearParamsPanelBaseWidth, nonlinearParamsPanelBaseHeight;


  public NonlinearControlsDelegate(TinaController owner, VariationControlsDelegate[] variationControlsDelegates) {
    this.owner = owner;
    this.data = owner.data;
    this.variationControlsDelegates = variationControlsDelegates;
  }

  public void setVariationControlsDelegates(VariationControlsDelegate[] pVariationControlsDelegates) {
    variationControlsDelegates = pVariationControlsDelegates;
  }

  public void initPanels() {
    for (int i = 0; i < data.TinaNonlinearControlsRows.length; i++) {
      initNonlinearControls(data.TinaNonlinearControlsRows[i]);
      data.TinaNonlinearControlsRows[i].getToggleParamsPnlButton().setSelected(Prefs.getPrefs().isTinaDefaultExpandNonlinearParams());
    }
    nonlinearParamsPanel = (JPanel) data.TinaNonlinearControlsRows[0].getRootPanel().getParent();
    nonlinearParamsPanelBaseWidth = nonlinearParamsPanel.getPreferredSize().width;
    nonlinearParamsPanelBaseHeight = nonlinearParamsPanel.getPreferredSize().height;
  }

  public void refreshParamControls(XForm pXForm) {
    int idx = 0;
    for (TinaNonlinearControlsRow row : data.TinaNonlinearControlsRows) {
      if (pXForm == null || idx >= pXForm.getVariationCount()) {
        refreshParamControls(row, idx, null, null, true);
      } else {
        Variation var = pXForm.getVariation(idx);
        refreshParamControls(row, idx, pXForm, var, true);
      }
      idx++;
    }
  }

  public void nonlinearParamsEditMotionCurve(int pIdx) {
    String propertyname = (String) data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsCmb().getSelectedItem();
    XForm xForm = owner.getCurrXForm();
    if (xForm != null && propertyname != null && propertyname.length() > 0) {
      if (pIdx < xForm.getVariationCount()) {
        Variation var = xForm.getVariation(pIdx);
        if (var.getFunc().getParameterIndex(propertyname) >= 0) {
          double initialValue;
          try {
            String valStr = data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsREd().getText();
            if (valStr == null || valStr.length() == 0) {
              valStr = "0";
            }
            initialValue = Double.parseDouble(valStr);
          } catch (Exception ex) {
            initialValue = 0.0;
          }
          MotionCurve curve = var.getMotionCurve(propertyname);
          if (curve == null) {
            curve = var.createMotionCurve(propertyname);
          }
          variationControlsDelegates[pIdx].editMotionCurve(curve, initialValue, propertyname, "variation property \"" + propertyname + "\"");
          // Doesnt work after changing parameter -> now enable it always
          // variationControlsDelegates[pIdx].enableControl(data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsREd(), curve, false);
          owner.refreshFlameImage(true, false, 1, true, false);
        }
      }
    }
  }

  public void nonlinearVarEditMotionCurve(int pIdx) {
    XForm xForm = owner.getCurrXForm();
    if (xForm != null) {
      if (pIdx < xForm.getVariationCount()) {
        String propertyName = "amount";
        owner.saveUndoPoint();
        variationControlsDelegates[pIdx].editMotionCurve(propertyName, "variation amount");
        variationControlsDelegates[pIdx].enableControl(data.TinaNonlinearControlsRows[pIdx].getNonlinearVarREd(), propertyName, false);
        owner.refreshFlameImage(true, false, 1, true, false);
      }
    }
  }

  public void refreshParamControls(TinaNonlinearControlsRow pRow, int pIdx, XForm pXForm, Variation pVar, boolean pSelectFirst) {
    if (pXForm == null || pVar == null) {
      pRow.getNonlinearVarCmb().setSelectedIndex(-1);
      pRow.getNonlinearVarREd().setText(null);
      pRow.getNonlinearParamsCmb().setSelectedIndex(-1);
      pRow.getNonlinearParamsREd().setText(null);

      pRow.getNonlinearParamsLeftButton().setEnabled(false);
      pRow.getNonlinearParamsPreButton().setSelected(false);
      pRow.getNonlinearParamsPreButton().setEnabled(false);
      pRow.getNonlinearParamsPostButton().setSelected(false);
      pRow.getNonlinearParamsPostButton().setEnabled(false);
      pRow.getToggleParamsPnlButton().setEnabled(false);

      if (pRow.getNonlinearParamsUpButton() != null) {
        pRow.getNonlinearParamsUpButton().setEnabled(false);
      }

    } else {
      VariationFunc varFunc = pVar.getFunc();

      pRow.getNonlinearParamsPreButton().setEnabled(true);
      pRow.getNonlinearParamsPreButton().setSelected(pVar.getPriority() < 0 || pVar.getPriority() == 2);
      pRow.getNonlinearParamsPostButton().setEnabled(true);
      pRow.getNonlinearParamsPostButton().setSelected(pVar.getPriority() > 0 || pVar.getPriority() == -2);
      if (pRow.getNonlinearParamsUpButton() != null) {
        pRow.getNonlinearParamsUpButton().setEnabled(true);
      }

      pRow.getNonlinearVarCmb().setSelectedItem(varFunc.getName());
      if (pRow.getNonlinearVarCmb().getSelectedIndex() < 0) {
        pRow.getNonlinearVarCmb().addItem(varFunc.getName());
        pRow.getNonlinearVarCmb().setSelectedItem(varFunc.getName());
        pRow.getNonlinearVarLbl().getFont().getStyle();
      }

      if (VariationFuncList.getExcludedNameList().contains(varFunc.getName())) {
        Font font = pRow.getNonlinearVarLbl().getFont();
        Map attributes = font.getAttributes();
        if (!attributes.containsKey(TextAttribute.STRIKETHROUGH)) {
          attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
          pRow.getNonlinearVarLbl().setFont(new Font(attributes));
        }
      } else {
        Font font = pRow.getNonlinearVarLbl().getFont();
        Map attributes = font.getAttributes();
        if (attributes.containsKey(TextAttribute.STRIKETHROUGH)) {
          attributes.remove(TextAttribute.STRIKETHROUGH);
          pRow.getNonlinearVarLbl().setFont(new Font(attributes));
        }
      }
      //pRow.getNonlinearVarREd().setText(Tools.doubleToString(pVar.getAmount()));
      owner.getFrameControlsUtil().updateControl(pVar, null, pRow.getNonlinearVarREd(), "amount", 1.0);

      pRow.getNonlinearParamsCmb().removeAllItems();
      // ressources
      int resCount = 0;
      String[] resNames = varFunc.getRessourceNames();
      if (resNames != null) {
        for (String name : resNames) {
          pRow.getNonlinearParamsCmb().addItem(name);
          resCount++;
        }
      }
      // params
      String[] paramNames = varFunc.getParameterNames();
      if (paramNames != null) {
        for (String name : paramNames) {
          pRow.getNonlinearParamsCmb().addItem(name);
        }
      }
      if (paramNames != null && (paramNames.length > 1 || paramNames.length > 0 && resCount > 0)) {
        pRow.getToggleParamsPnlButton().setEnabled(true);
      } else {
        pRow.getToggleParamsPnlButton().setEnabled(false);
      }

      // preselection
      if (pSelectFirst) {
        if (resCount > 0) {
          pRow.getNonlinearParamsCmb().setSelectedIndex(0);
          pRow.getNonlinearParamsREd().setText(null);
          enableNonlinearControls(pRow, true);
        } else if (varFunc.getParameterNames().length > 0) {
          pRow.getNonlinearParamsCmb().setSelectedIndex(0);
          Object paramValue = varFunc.getParameterValues()[0];
          String paramName = varFunc.getParameterNames()[0];
          pRow.getNonlinearParamsREd().setOnlyIntegers(false);
          if (paramValue instanceof Double) {
            //pRow.getNonlinearParamsREd().setText(Tools.doubleToString((Double) val));
            owner.getFrameControlsUtil().updateControl(null, pRow.getNonlinearParamsREd(), paramName, paramValue, pVar.getMotionCurve(paramName), 1.0);
          } else if (paramValue instanceof Integer) {
            pRow.getNonlinearParamsREd().setOnlyIntegers(true);
            //pRow.getNonlinearParamsREd().setText(val.toString());
            owner.getFrameControlsUtil().updateControl(null, pRow.getNonlinearParamsREd(), paramName, paramValue, pVar.getMotionCurve(paramName), 1.0);
          } else {
            pRow.getNonlinearParamsREd().setText(paramValue.toString());
          }
          enableNonlinearControls(pRow, false);
        } else {
          pRow.getNonlinearParamsCmb().setSelectedIndex(-1);
          pRow.getNonlinearParamsREd().setText(null);
          pRow.getNonlinearParamsREd().setOnlyIntegers(false);
        }
      }
    }
    pRow.rebuildParamsPnl(pIdx, pXForm, pVar);
    resizeNonlinearParamsPanel();
  }

  void resizeNonlinearParamsPanel() {
    int currHeight = nonlinearParamsPanel.getPreferredSize().height;
    int newHeight = nonlinearParamsPanelBaseHeight;
    for (int i = 0; i < data.TinaNonlinearControlsRows.length; i++)
      newHeight += data.TinaNonlinearControlsRows[i].getExtraPanelSize();
    if (newHeight != currHeight) {
      nonlinearParamsPanel.setPreferredSize(new Dimension(nonlinearParamsPanelBaseWidth, newHeight));

      nonlinearParamsPanel.invalidate();
      nonlinearParamsPanel.validate();
      nonlinearParamsPanel.repaint();
      nonlinearParamsPanel.getParent().invalidate();
      nonlinearParamsPanel.getParent().validate();
      nonlinearParamsPanel.getParent().repaint();
      nonlinearParamsPanel.getParent().getParent().invalidate();
      nonlinearParamsPanel.getParent().getParent().validate();
      nonlinearParamsPanel.getParent().getParent().repaint();
      nonlinearParamsPanel.getParent().getParent().getParent().invalidate();
      nonlinearParamsPanel.getParent().getParent().getParent().validate();
      nonlinearParamsPanel.getParent().getParent().getParent().repaint();
    }
  }

  public void nonlinearParamsCmbChanged(int pIdx) {
    if (owner.cmbRefreshing) {
      return;
    }
    owner.cmbRefreshing = true;
    try {
      String selected = (String) data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsCmb().getSelectedItem();
      XForm xForm = owner.getCurrXForm();
      if (xForm != null && selected != null && selected.length() > 0) {
        if (pIdx < xForm.getVariationCount()) {
          Variation var = xForm.getVariation(pIdx);
          // params
          int idx;
          if ((idx = var.getFunc().getParameterIndex(selected)) >= 0) {
            enableNonlinearControls(data.TinaNonlinearControlsRows[pIdx], false);
            data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsREd().setOnlyIntegers(false);
            Object paramValue = var.getFunc().getParameterValues()[idx];
            String paramName = var.getFunc().getParameterNames()[idx];
            if (paramValue instanceof Double) {
              // data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsREd().setText(Tools.doubleToString((Double) paramValue));
              owner.getFrameControlsUtil().updateControl(null, data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsREd(), paramName, paramValue, var.getMotionCurve(paramName), 1.0);
            } else if (paramValue instanceof Integer) {
              // data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsREd().setText(paramValue.toString());
              owner.getFrameControlsUtil().updateControl(null, data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsREd(), paramName, paramValue, var.getMotionCurve(paramName), 1.0);
              data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsREd().setOnlyIntegers(true);
            } else {
              data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsREd().setText(paramValue.toString());
            }
          }
          // ressources
          else if ((idx = var.getFunc().getRessourceIndex(selected)) >= 0) {
            enableNonlinearControls(data.TinaNonlinearControlsRows[pIdx], true);
            data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsREd().setText(null);
          }
          // empty
          else {
            data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsREd().setText(null);
          }
        }
      }
    } finally {
      owner.cmbRefreshing = false;
    }
  }

  private void initNonlinearControls(TinaNonlinearControlsRow pRow) {
    pRow.getNonlinearParamsLeftButton().setEnabled(false);
    pRow.getNonlinearParamsPostButton().setEnabled(false);
    pRow.getNonlinearParamsPreButton().setEnabled(false);
    if (pRow.getNonlinearParamsUpButton() != null)
      pRow.getNonlinearParamsUpButton().setEnabled(false);
    pRow.getToggleParamsPnlButton().setEnabled(false);
  }

  void enableNonlinearControls(TinaNonlinearControlsRow pRow, boolean pRessource) {
    String selected = (String) pRow.getNonlinearParamsCmb().getSelectedItem();
    boolean enabled = selected != null && selected.length() > 0;
    pRow.getNonlinearParamsLeftButton().setEnabled(enabled && pRessource);
    pRow.getNonlinearParamsREd().setEnabled(enabled && !pRessource);
  }

  public void nonlinearResetVarParams(int pIdx) {
    nonlinearResetVarParam(pIdx, null);
  }

  public void nonlinearResetVarParam(int pIdx, String pParamname) {
    if (owner.cmbRefreshing) {
      return;
    }
    boolean oldCmbRefreshing = owner.cmbRefreshing;
    owner.cmbRefreshing = true;
    try {
      XForm xForm = owner.getCurrXForm();
      if (xForm != null) {
        if (pIdx < xForm.getVariationCount()) {
          Variation var = xForm.getVariation(pIdx);
          if (var != null && var.getFunc().getParameterNames().length > 0 || (var.getFunc().getRessourceNames() != null && var.getFunc().getRessourceNames().length > 0)) {
            owner.saveUndoPoint();
            String fName = (String) data.TinaNonlinearControlsRows[pIdx].getNonlinearVarCmb().getSelectedItem();
            VariationFunc tmpFunc = VariationFuncList.getVariationFuncInstance(fName);
            for (String paramName : var.getFunc().getParameterNames()) {
              if (pParamname == null || pParamname.equals(paramName)) {
                try {
                  Object paramValue = tmpFunc.getParameter(paramName);
                  if (paramValue instanceof Integer) {
                    var.getFunc().setParameter(paramName, (Integer) paramValue);
                  } else if (paramValue instanceof Double) {
                    var.getFunc().setParameter(paramName, (Double) paramValue);
                  }
                  MotionCurve curve = var.getMotionCurve(paramName);
                  if(curve!=null) {
                    var.removeMotionCurve(paramName);
                  }
                } catch (Exception ex) {
                  ex.printStackTrace();
                }
              }
            }
            if (var.getFunc().getRessourceNames() != null) {
              for (String ressName : var.getFunc().getRessourceNames()) {
                if (pParamname == null || pParamname.equals(ressName)) {
                  try {
                    byte[] resValue = tmpFunc.getRessource(ressName);
                    var.getFunc().setRessource(ressName, resValue);
                  } catch (Exception ex) {
                    ex.printStackTrace();
                  }
                }
              }
            }

            refreshParamControls(data.TinaNonlinearControlsRows[pIdx], pIdx, xForm, var, true);
            owner.refreshXFormUI(xForm);
            owner.refreshFlameImage(true, false, 1, true, false);

            data.transformationsTable.invalidate();
            data.transformationsTable.repaint();
          }
        }
      }
    } finally {
      owner.cmbRefreshing = oldCmbRefreshing;
    }
  }

  public void nonlinearVarCmbChanged(int pIdx) {
    if (owner.cmbRefreshing) {
      return;
    }
    boolean oldCmbRefreshing = owner.cmbRefreshing;
    owner.cmbRefreshing = true;
    try {
      XForm xForm = owner.getCurrXForm();
      if (xForm != null) {
        owner.saveUndoPoint();
        String fName = (String) data.TinaNonlinearControlsRows[pIdx].getNonlinearVarCmb().getSelectedItem();
        Variation var;
        if (pIdx < xForm.getVariationCount()) {
          var = xForm.getVariation(pIdx);
          if (fName == null || fName.length() == 0) {
            xForm.removeVariation(var);
          } else {
            if (var.getFunc() == null || !var.getFunc().getName().equals(fName)) {
              VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(fName);
              var.setFunc(varFunc);
              var.setPriority(varFunc.getPriority());
            }
          }
        } else {
          var = new Variation();
          String varStr = data.TinaNonlinearControlsRows[pIdx].getNonlinearVarREd().getText();
          if (varStr == null || varStr.length() == 0) {
            varStr = "0";
          }
          VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(fName);
          var.setFunc(varFunc);
          var.setPriority(varFunc.getPriority());
          var.setAmount(Tools.stringToDouble(varStr));
          xForm.addVariation(var);
        }
        refreshParamControls(data.TinaNonlinearControlsRows[pIdx], pIdx, xForm, var, true);
        owner.refreshXFormUI(xForm);
        owner.refreshFlameImage(true, false, 1, true, false);

        data.transformationsTable.invalidate();
        data.transformationsTable.repaint();
      }
    } finally {
      owner.cmbRefreshing = oldCmbRefreshing;
    }
  }

  public void nonlinearVarREdChanged(int pIdx) {
    nonlinearVarREdChanged(pIdx, 0.0);
  }

  public void propertyPnlValueChanged(int pIdx, String pPropertyName, double pPropertyValue, boolean pIsAdjusting) {
    if (owner.cmbRefreshing) {
      return;
    }
    owner.cmbRefreshing = true;
    try {
      XForm xForm = owner.getCurrXForm();
      if (xForm != null) {
        if (pIdx < xForm.getVariationCount()) {
          Variation var = xForm.getVariation(pIdx);
          var.getFunc().setParameter(pPropertyName, pPropertyValue);
          if (!pIsAdjusting && var.getFunc().dynamicParameterExpansion(pPropertyName)) {
            // if setting the parameter can change the total number of parameters,
            //    then refresh parameter UI
            this.refreshParamControls(data.TinaNonlinearControlsRows[pIdx], pIdx, xForm, var, true);
          }
          owner.refreshFlameImage(true, false, 1, true, false);
        }
      }
    } finally {
      owner.cmbRefreshing = false;
    }
  }

  public void nonlinearVarREdChanged(int pIdx, double pDelta) {
    if (owner.cmbRefreshing) {
      return;
    }
    owner.cmbRefreshing = true;
    try {
      XForm xForm = owner.getCurrXForm();
      if (xForm != null) {
        if (pIdx < xForm.getVariationCount()) {
          Variation var = xForm.getVariation(pIdx);
          /*
          String varStr = data.TinaNonlinearControlsRows[pIdx].getNonlinearVarREd().getText();
          if (varStr == null || varStr.length() == 0) {
            varStr = "0";
          }
          var.setAmount(Tools.stringToDouble(varStr) + pDelta);
          data.TinaNonlinearControlsRows[pIdx].getNonlinearVarREd().setText(Tools.doubleToString(var.getAmount()));
          */
          owner.getFrameControlsUtil().valueChangedByTextField(var, null, data.TinaNonlinearControlsRows[pIdx].getNonlinearVarREd(), "amount", 1.0, pDelta);
          owner.refreshFlameImage(true, false, 1, true, false);
        }
      }
    } finally {
      owner.cmbRefreshing = false;
    }
  }

  public void nonlinearParamsREdChanged(int pIdx) {
    nonlinearParamsREdChanged(pIdx, 0.0);
  }

  public void nonlinearParamsREdChanged(int pIdx, double pDelta) {
    if (owner.cmbRefreshing) {
      return;
    }
    owner.cmbRefreshing = true;
    try {
      String paramName = (String) data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsCmb().getSelectedItem();
      XForm xForm = owner.getCurrXForm();
      if (xForm != null && paramName != null && paramName.length() > 0) {
        owner.saveUndoPoint();
        if (pIdx < xForm.getVariationCount()) {
          final Variation var = xForm.getVariation(pIdx);
          int idx;
          if ((idx = var.getFunc().getParameterIndex(paramName)) >= 0) {
            String valStr = data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsREd().getText();
            if (valStr == null || valStr.length() == 0) {
              valStr = "0";
            }
            // round the delta to whole numbers if the parameter is of type integer
            if (Math.abs(pDelta) > MathLib.EPSILON) {
              Object val = var.getFunc().getParameterValues()[idx];
              if (val != null && val instanceof Integer) {
                if (Math.abs(pDelta) < 1.0) {
                  pDelta = pDelta < 0 ? -1 : 1;
                } else {
                  pDelta = Math.round(pDelta);
                }
              }
            }

            Object oldValue = var.getFunc().getParameter(paramName);
            double oldValueAsDouble=0.0;
            if(oldValue!=null) {
              if(oldValue instanceof Double) {
                oldValueAsDouble = ((Double)oldValue).doubleValue();
              }
              else if(oldValue instanceof Integer) {
                oldValueAsDouble = ((Integer)oldValue).intValue();
              }
            }
            double newValue = Tools.stringToDouble(valStr) + pDelta;
            var.getFunc().setParameter(paramName, newValue);
            MotionCurve curve = var.getMotionCurve(paramName);
            if(curve==null) {
              curve = var.createMotionCurve(paramName);
            }
            owner.getFrameControlsUtil().updateKeyFrame(newValue, oldValueAsDouble, curve);


            if (var.getFunc().dynamicParameterExpansion(paramName)) {
              // if setting the parameter can change the total number of parameters,
              //    then refresh parameter UI (and reselect parameter that was changed)
              this.refreshParamControls(data.TinaNonlinearControlsRows[pIdx], pIdx, xForm, var, false);
              data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsCmb().setSelectedItem(paramName);
            }
            data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsREd().setText(Tools.doubleToString(newValue));
            data.TinaNonlinearControlsRows[pIdx].refreshParamWithoutRefresh(paramName, newValue);
          } else if ((idx = var.getFunc().getRessourceIndex(paramName)) >= 0) {
            final String rName = var.getFunc().getRessourceNames()[idx];
            RessourceType resType = var.getFunc().getRessourceType(rName);
            switch (resType) {
              case FONT_NAME: {
                String oldFontname = null;
                {
                  byte val[] = var.getFunc().getRessourceValues()[idx];
                  if (val != null) {
                    oldFontname = new String(val);
                  }
                }
                Font oldFont = new Font(oldFontname != null ? oldFontname : "Arial", Font.PLAIN, 24);
                Font selectedFont = JFontChooser.showDialog(owner.centerPanel, "Choose font", oldFont);
                if (selectedFont != null) {
                  String valStr = selectedFont.getFontName();
                  byte[] valByteArray = valStr != null ? valStr.getBytes() : null;
                  var.getFunc().setRessource(rName, valByteArray);
                }
              }
              break;
              case IMAGE_FILENAME: {
                String oldFilename = null;
                {
                  byte val[] = var.getFunc().getRessourceValues()[idx];
                  if (val != null) {
                    oldFilename = new String(val);
                  }
                }
                JFileChooser chooser = new ImageFileChooser(Tools.FILEEXT_PNG);
                if (oldFilename != null && oldFilename.length() > 0) {
                  try {
                    chooser.setCurrentDirectory(new File(oldFilename).getAbsoluteFile().getParentFile());
                    chooser.setSelectedFile(new File(oldFilename));
                  } catch (Exception ex) {
                    ex.printStackTrace();
                  }
                } else {
                  if (Prefs.getPrefs().getInputImagePath() != null) {
                    try {
                      chooser.setCurrentDirectory(new File(Prefs.getPrefs().getInputImagePath()));
                    } catch (Exception ex) {
                      ex.printStackTrace();
                    }
                  }
                }
                if (chooser.showOpenDialog(owner.centerPanel) == JFileChooser.APPROVE_OPTION) {
                  File file = chooser.getSelectedFile();
                  String valStr = file.getAbsolutePath();
                  byte[] valByteArray = valStr != null ? valStr.getBytes() : null;
                  var.getFunc().setRessource(rName, valByteArray);
                }
              }
              break;
              case FLAME_FILENAME: {
                String oldFilename = null;
                {
                  byte val[] = var.getFunc().getRessourceValues()[idx];
                  if (val != null) {
                    oldFilename = new String(val);
                  }
                }
                JFileChooser chooser = new FlameFileChooser(Prefs.getPrefs());
                if (oldFilename != null && oldFilename.length() > 0) {
                  try {
                    chooser.setCurrentDirectory(new File(oldFilename).getAbsoluteFile().getParentFile());
                    chooser.setSelectedFile(new File(oldFilename));
                  } catch (Exception ex) {
                    ex.printStackTrace();
                  }
                } else {
                  if (Prefs.getPrefs().getInputImagePath() != null) {
                    try {
                      chooser.setCurrentDirectory(new File(Prefs.getPrefs().getInputFlamePath()));
                    } catch (Exception ex) {
                      ex.printStackTrace();
                    }
                  }
                }
                if (chooser.showOpenDialog(owner.centerPanel) == JFileChooser.APPROVE_OPTION) {
                  File file = chooser.getSelectedFile();
                  String valStr = file.getAbsolutePath();
                  byte[] valByteArray = valStr != null ? valStr.getBytes() : null;
                  var.getFunc().setRessource(rName, valByteArray);
                }
              }
              break;
              case IMAGE_FILE: {
                JFileChooser chooser = new ImageFileChooser(Tools.FILEEXT_PNG);
                if (Prefs.getPrefs().getInputImagePath() != null) {
                  try {
                    chooser.setCurrentDirectory(new File(Prefs.getPrefs().getInputImagePath()));
                  } catch (Exception ex) {
                    ex.printStackTrace();
                  }
                }
                if (chooser.showOpenDialog(owner.centerPanel) == JFileChooser.APPROVE_OPTION) {
                  try {
                    File file = chooser.getSelectedFile();
                    byte[] imgData = Tools.readFile(file.getAbsolutePath());
                    var.getFunc().setRessource(rName, imgData);
                  } catch (Exception ex) {
                    owner.errorHandler.handleError(ex);
                  }
                }
              }
              break;
              case SVG_FILE: {
                JFileChooser chooser = new SvgFileChooser(Prefs.getPrefs());
                if (Prefs.getPrefs().getTinaSVGPath() != null) {
                  try {
                    chooser.setCurrentDirectory(new File(Prefs.getPrefs().getTinaSVGPath()));
                  } catch (Exception ex) {
                    ex.printStackTrace();
                  }
                }
                if (chooser.showOpenDialog(owner.centerPanel) == JFileChooser.APPROVE_OPTION) {
                  try {
                    File file = chooser.getSelectedFile();
                    String svg = Tools.readUTF8Textfile(file.getAbsolutePath());
                    byte[] valByteArray = svg.getBytes();
                    var.getFunc().setRessource(rName, valByteArray);
                  } catch (Exception ex) {
                    owner.errorHandler.handleError(ex);
                  }
                }
              }
              break;
              case OBJ_MESH: {
                JFileChooser chooser = new MeshFileChooser(Prefs.getPrefs());
                if (Prefs.getPrefs().getTinaMeshPath() != null) {
                  try {
                    chooser.setCurrentDirectory(new File(Prefs.getPrefs().getTinaMeshPath()));
                  } catch (Exception ex) {
                    ex.printStackTrace();
                  }
                }
                if (chooser.showOpenDialog(owner.centerPanel) == JFileChooser.APPROVE_OPTION) {
                  try {
                    File file = chooser.getSelectedFile();
                    var.getFunc().setRessource(rName, file.getAbsolutePath().getBytes());
                  } catch (Exception ex) {
                    owner.errorHandler.handleError(ex);
                  }
                }
              }
              break;
              default: {
                final RessourceDialog dlg = new RessourceDialog(SwingUtilities.getWindowAncestor(owner.centerPanel), Prefs.getPrefs(), owner.errorHandler);
                dlg.setRessourceName(rName);
                byte val[] = var.getFunc().getRessourceValues()[idx];
                RessourceType type = var.getFunc().getRessourceType(rName);
                RessourceDialog.ContentType ct = type == RessourceType.JAVA_CODE ? RessourceDialog.ContentType.JAVA : RessourceDialog.ContentType.TEXT;
                if (val != null) {
                  dlg.setRessourceValue(ct, new String(val));
                }
                dlg.addValidation(new RessourceValidation() {
                  @Override
                  public void validate() {
                    String valStr = dlg.getRessourceValue();
                    byte[] valByteArray = valStr != null ? valStr.getBytes() : null;
                    byte[] oldValue = var.getFunc().getRessource(rName);
                    try {
                      var.getFunc().setRessource(rName, valByteArray);
                      var.getFunc().validate();
                    } catch (Throwable ex) {
                      var.getFunc().setRessource(rName, oldValue);
                      throw new RuntimeException(ex);
                    }
                  }
                });

                dlg.setModal(true);
                dlg.setVisible(true);

                if (dlg.isConfirmed()) {
                  try {
                    String valStr = dlg.getRessourceValue();
                    byte[] valByteArray = valStr != null ? valStr.getBytes() : null;
                    var.getFunc().setRessource(rName, valByteArray);
                    if (var.getFunc().ressourceCanModifyParams(rName)) {
                      // forcing refresh of params UI in case setting resource changes available params or param values
                      this.refreshParamControls(data.TinaNonlinearControlsRows[pIdx], pIdx, xForm, var, true);
                    }
                  } catch (Throwable ex) {
                    owner.errorHandler.handleError(ex);
                  }
                }
              }
            }
          }
          owner.refreshFlameImage(true, false, 1, true, false);
        }
      }
      resizeNonlinearParamsPanel();
    } finally {
      owner.cmbRefreshing = false;
    }
  }

  private static final double DELTA_PARAM = 0.1;

  public void nonlinearParamsLeftButtonClicked(int pIdx) {
    nonlinearParamsREdChanged(pIdx, -DELTA_PARAM);
  }

  public void nonlinearParamsRightButtonClicked(int pIdx) {
    nonlinearParamsREdChanged(pIdx, DELTA_PARAM);
  }

  public void nonlinearParamsPreButtonClicked(int pIdx) {
    int fpriority;
    XForm xForm = owner.getCurrXForm();
    if (xForm != null && pIdx < xForm.getVariationCount())
      fpriority = xForm.getVariation(pIdx).getFunc().getPriority();
    else
      fpriority = 0;
    if ((fpriority == 2 || fpriority == -2) && data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsPostButton().isSelected()) {
      nonlinearParamsPriorityChanged(pIdx, data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsPreButton().isSelected() ? fpriority : 1);
    } else {
      nonlinearParamsPriorityChanged(pIdx, data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsPreButton().isSelected() ? -1 : 0);
    }
  }

  public void nonlinearParamsPostButtonClicked(int pIdx) {
    int fpriority;
    XForm xForm = owner.getCurrXForm();
    if (xForm != null && pIdx < xForm.getVariationCount())
      fpriority = xForm.getVariation(pIdx).getFunc().getPriority();
    else
      fpriority = 0;
    if ((fpriority == 2 || fpriority == -2) && data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsPreButton().isSelected()) {
      nonlinearParamsPriorityChanged(pIdx, data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsPostButton().isSelected() ? fpriority : -1);
    } else {
      nonlinearParamsPriorityChanged(pIdx, data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsPostButton().isSelected() ? 1 : 0);
    }
  }

  public void nonlinearParamsToggleParamsPnlClicked(int pIdx) {
    XForm xForm = owner.getCurrXForm();
    Variation var;
    if (xForm != null && pIdx < xForm.getVariationCount()) {
      var = xForm.getVariation(pIdx);
    } else {
      var = null;
    }
    data.TinaNonlinearControlsRows[pIdx].rebuildParamsPnl(pIdx, xForm, var);
    resizeNonlinearParamsPanel();
  }

  public void nonlinearParamsUpButtonClicked(int pIdx) {
    if (owner.cmbRefreshing) {
      return;
    }
    boolean oldCmbRefreshing = owner.cmbRefreshing;
    owner.cmbRefreshing = true;
    try {
      XForm xForm = owner.getCurrXForm();
      if (xForm != null && pIdx > 0 && pIdx < xForm.getVariationCount()) {
        owner.saveUndoPoint();
        boolean prevExpanded = data.TinaNonlinearControlsRows[pIdx - 1].getToggleParamsPnlButton().isSelected();
        boolean selExpanded = data.TinaNonlinearControlsRows[pIdx].getToggleParamsPnlButton().isSelected();

        xForm.getVariations().add(pIdx - 1, xForm.getVariations().get(pIdx));
        xForm.getVariations().remove(pIdx + 1);
        data.TinaNonlinearControlsRows[pIdx - 1].getToggleParamsPnlButton().setSelected(selExpanded);
        data.TinaNonlinearControlsRows[pIdx].getToggleParamsPnlButton().setSelected(prevExpanded);

        owner.refreshXFormUI(xForm);
        owner.refreshFlameImage(true, false, 1, true, false);
        data.transformationsTable.invalidate();
        data.transformationsTable.repaint();
      }
    } finally {
      owner.cmbRefreshing = oldCmbRefreshing;
    }
  }

  public void nonlinearParamsPriorityChanged(int pIdx, int pPriority) {
    if (owner.cmbRefreshing) {
      return;
    }
    owner.cmbRefreshing = true;
    try {
      XForm xForm = owner.getCurrXForm();
      if (xForm != null) {
        owner.saveUndoPoint();
        if (pIdx < xForm.getVariationCount()) {
          final Variation var = xForm.getVariation(pIdx);
          var.setPriority(pPriority);
          if (pPriority == 0 || pPriority == 1) {
            data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsPreButton().setSelected(false);
          }
          if (pPriority == 0 || pPriority == -1) {
            data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsPostButton().setSelected(false);
          }
          owner.refreshFlameImage(true, false, 1, true, false);
        }
      }
    } finally {
      owner.cmbRefreshing = false;
    }
  }

}
