package org.jwildfire.create.tina.integration.chaotica;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class ChaoticaExporter {
  private ChaoticaPluginTranslators translator = new ChaoticaPluginTranslators();

  public void doExport(Flame pFlame) {
    pFlame.resetCameraSettings();
    pFlame.resetBokehSettings();
    pFlame.resetDOFSettings();
    pFlame.resetMixerCurves();
    pFlame.resetPostSymmetrySettings();
    pFlame.resetShadingSettings();
    pFlame.resetStereo3DSettings();
    while (pFlame.getLayers().size() > 1) {
      pFlame.getLayers().remove(pFlame.getLayers().size() - 1);
    }
    Layer layer = pFlame.getFirstLayer();
    while (layer.getFinalXForms().size() > 1) {
      layer.getFinalXForms().remove(layer.getFinalXForms().size() - 1);
    }
    for (XForm xForm : layer.getXForms()) {
      chaotifyXForm(xForm);
    }
    for (XForm xForm : layer.getFinalXForms()) {
      chaotifyXForm(xForm);
    }
  }

  private void chaotifyXForm(XForm xForm) {
    List<Variation> removeList = new ArrayList<Variation>();
    for (Variation var : xForm.getVariations()) {
      String varName = var.getFunc().getName();
      String translatedVarName = translator.translateVarName(varName);
      if (translatedVarName == null) {
        removeList.add(var);
      }
      else if (!translatedVarName.equals(varName)) {
        VariationFunc newVarFunc = VariationFuncList.getVariationFuncInstance(translatedVarName, false);
        if (newVarFunc != null) {
          var.setFunc(newVarFunc);
          for (String srcProperty : var.getFunc().getParameterNames()) {
            String dstProperty = translator.translatePropertyName(varName, srcProperty);
            if (dstProperty != null) {
              try {
                if (dstProperty.startsWith(translatedVarName)) {
                  dstProperty = dstProperty.substring(translatedVarName.length() + 1, dstProperty.length());
                }
                Object val = var.getFunc().getParameter(srcProperty);
                if (val != null && val instanceof Double) {
                  newVarFunc.setParameter(dstProperty, (Double) val);
                }
                else if (val != null && val instanceof Integer) {
                  newVarFunc.setParameter(dstProperty, (Integer) val);
                }
              }
              catch (Exception ex) {
                ex.printStackTrace();
              }
            }
          }

          for (String fixedValue : translator.getFixedValueNames(varName)) {
            String dstProperty = translator.translatePropertyName(varName, fixedValue);
            if (dstProperty.startsWith(translatedVarName)) {
              dstProperty = dstProperty.substring(translatedVarName.length() + 1, dstProperty.length());
            }
            newVarFunc.setParameter(dstProperty, translator.getFixedValue(varName, fixedValue));
          }
        }
      }
    }
    if (removeList.size() > 0) {
      xForm.getVariations().removeAll(removeList);
    }
  }
}
