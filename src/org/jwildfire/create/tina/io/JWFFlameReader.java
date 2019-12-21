package org.jwildfire.create.tina.io;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.base.Tools.XMLAttributes;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;

public class JWFFlameReader extends AbstractFlameReader {
  public static final String ATTR_FLAME = "flame";
  public static final String ATTR_JWF_FLAME = "jwf-flame";
  public static final String ATTR_LAYER = "layer";
  public static final String ATTR_DENSITY = "density";

  protected JWFFlameReader(Prefs pPrefs) {
    super(pPrefs);
  }

  public List<Flame> readFlamesfromXML(String pXML) {
    List<Flame> res = new ArrayList<Flame>();
    int pFlames = 0;
    while (true) {
      String flameXML;
      {
        int ps = pXML.indexOf("<" + ATTR_JWF_FLAME + " ", pFlames);
        if (ps < 0)
          break;
        int pe = pXML.indexOf("</" + ATTR_JWF_FLAME + ">", ps + 1);
        if (pe < 0)
          break;
        pFlames = pe + ATTR_JWF_FLAME.length() + 3;
        flameXML = pXML.substring(ps, pFlames);
      }

      Flame flame = new Flame();
      res.add(flame);
      // Flame attributes
      {
        int ps = flameXML.indexOf("<" + ATTR_JWF_FLAME + " ");
        int pe = -1;
        boolean qt = false;
        for (int i = ps + 1; i < flameXML.length(); i++) {
          if (flameXML.charAt(i) == '\"') {
            qt = !qt;
          }
          else if (!qt && flameXML.charAt(i) == '>') {
            pe = i;
            break;
          }
        }
        String hs = flameXML.substring(ps + 11, pe);
        parseFlameAttributes(flame, hs);
      }
      flame.getLayers().clear();
      // Layers
      int pLayers = 0;
      while (true) {
        int layerStart = flameXML.indexOf("<" + ATTR_LAYER + " ", pLayers);
        if (layerStart < 0)
          break;
        int layerEnd = flameXML.indexOf("</" + ATTR_LAYER + ">", layerStart + 1);
        if (layerEnd < 0)
          break;
        pLayers = layerEnd + ATTR_LAYER.length() + 3;
        String layerXML = flameXML.substring(layerStart, pLayers);

        Layer layer = new Layer();
        layer.getGradientEditorHueCurve().clear();
        layer.getGradientEditorSaturationCurve().clear();
        layer.getGradientEditorLuminosityCurve().clear();
        flame.getLayers().add(layer);
        XMLAttributes atts;
        // Layer attributes
        {
          int ps = layerXML.indexOf("<" + ATTR_LAYER + " ");
          int pe = layerXML.indexOf(">", ps);
          String hs = layerXML.substring(ps + 7, pe);
          atts = parseLayerAttributes(layer, hs);
        }

        readXForms(layerXML, flame, layer);
        readFinalXForms(layerXML, flame, layer);
        readColors(layerXML, layer);
        if(layer.getGradientEditorHueCurve().getX().length==0) {
          layer.recalcHSLCurves();
        }
        readMotionCurves(layer.getPalette(), atts, "palette_");
      }
    }
    return res;
  }

  protected XMLAttributes parseLayerAttributes(Layer pLayer, String pXML) {
    XMLAttributes atts = Tools.parseAttributes(pXML);
    String hs;
    if ((hs = atts.get(ATTR_WEIGHT)) != null) {
      pLayer.setWeight(Double.parseDouble(hs));
    }
    if ((hs = atts.get(ATTR_DENSITY)) != null) {
      pLayer.setDensity(Double.parseDouble(hs));
    }
    if ((hs = atts.get(ATTR_VISIBLE)) != null) {
      pLayer.setVisible(Integer.parseInt(hs) == 1);
    }
    if ((hs = atts.get(ATTR_NAME)) != null) {
      pLayer.setName(hs);
    }
    if ((hs = atts.get(ATTR_GRADIENT_MAP)) != null) {
      pLayer.setGradientMapFilename(hs);
    }
    if ((hs = atts.get(ATTR_GRADIENT_MAP_HOFFSET)) != null) {
      pLayer.setGradientMapHorizOffset(Double.parseDouble(hs));
    }
    if ((hs = atts.get(ATTR_GRADIENT_MAP_HSCALE)) != null) {
      pLayer.setGradientMapHorizScale(Double.parseDouble(hs));
    }
    if ((hs = atts.get(ATTR_GRADIENT_MAP_VOFFSET)) != null) {
      pLayer.setGradientMapVertOffset(Double.parseDouble(hs));
    }
    if ((hs = atts.get(ATTR_GRADIENT_MAP_VSCALE)) != null) {
      pLayer.setGradientMapVertScale(Double.parseDouble(hs));
    }
    if ((hs = atts.get(ATTR_GRADIENT_MAP_LCOLOR_ADD)) != null) {
      pLayer.setGradientMapLocalColorAdd(Double.parseDouble(hs));
    }
    if ((hs = atts.get(ATTR_GRADIENT_MAP_LCOLOR_SCALE)) != null) {
      pLayer.setGradientMapLocalColorScale(Double.parseDouble(hs));
    }
    if ((hs = atts.get(ATTR_SMOOTH_GRADIENT)) != null) {
      pLayer.setSmoothGradient("1".equals(hs));
    }
    readMotionCurves(pLayer, atts, "");
    return atts;
  }
}
