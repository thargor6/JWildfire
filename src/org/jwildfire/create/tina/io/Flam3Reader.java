/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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
package org.jwildfire.create.tina.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.DrawMode;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Shading;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class Flam3Reader implements FlameReader {

  @Override
  public List<Flame> readFlames(String pFilename) {
    try {
      String flamesXML = Tools.readUTF8Textfile(pFilename);
      return readFlamesfromXML(flamesXML);
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private Map<String, String> parseAttributes(String pXML) {
    Map<String, String> res = new HashMap<String, String>();
    int p = 0;
    while (true) {
      int ps = pXML.indexOf("=\"", p + 1);
      if (ps < 0)
        break;
      int pe = pXML.indexOf("\"", ps + 2);
      String name = pXML.substring(p, ps).trim();
      String value = pXML.substring(ps + 2, pe);
      //      System.out.println("#" + name + "#" + value + "#");
      res.put(name, value);
      p = pe + 2;
    }
    return res;
  }

  private static final String ATTR_SIZE = "size";
  private static final String ATTR_CENTER = "center";
  private static final String ATTR_SCALE = "scale";
  private static final String ATTR_ROTATE = "rotate";
  private static final String ATTR_OVERSAMPLE = "oversample";
  private static final String ATTR_COLOR_OVERSAMPLE = "color_oversample";
  private static final String ATTR_FILTER = "filter";
  private static final String ATTR_QUALITY = "quality";
  private static final String ATTR_BACKGROUND = "background";
  private static final String ATTR_BRIGHTNESS = "brightness";
  private static final String ATTR_GAMMA = "gamma";
  private static final String ATTR_GAMMA_THRESHOLD = "gamma_threshold";
  private static final String ATTR_INDEX = "index";
  private static final String ATTR_RGB = "rgb";
  private static final String ATTR_CAM_PITCH = "cam_pitch";
  private static final String ATTR_CAM_YAW = "cam_yaw";
  private static final String ATTR_CAM_PERSP = "cam_persp";
  private static final String ATTR_CAM_ZPOS = "cam_zpos";
  private static final String ATTR_CAM_DOF = "cam_dof";
  private static final String ATTR_CAM_ZOOM = "cam_zoom";
  private static final String ATTR_SHADING_SHADING = "shading_shading";
  private static final String ATTR_SHADING_AMBIENT = "shading_ambient";
  private static final String ATTR_SHADING_DIFFUSE = "shading_diffuse";
  private static final String ATTR_SHADING_PHONG = "shading_phong";
  private static final String ATTR_SHADING_PHONGSIZE = "shading_phongSize";
  private static final String ATTR_SHADING_LIGHTCOUNT = "shading_lightCount";
  private static final String ATTR_SHADING_LIGHTPOSX_ = "shading_lightPosX_";
  private static final String ATTR_SHADING_LIGHTPOSY_ = "shading_lightPosY_";
  private static final String ATTR_SHADING_LIGHTPOSZ_ = "shading_lightPosZ_";
  private static final String ATTR_SHADING_LIGHTRED_ = "shading_lightRed_";
  private static final String ATTR_SHADING_LIGHTGREEN_ = "shading_lightGreen_";
  private static final String ATTR_SHADING_LIGHTBLUE_ = "shading_lightBlue_";

  private void parseFlameAttributes(Flame pFlame, String pXML) {
    Map<String, String> atts = parseAttributes(pXML);
    String hs;
    if ((hs = atts.get(ATTR_SIZE)) != null) {
      String s[] = hs.split(" ");
      pFlame.setWidth(Integer.parseInt(s[0]));
      pFlame.setHeight(Integer.parseInt(s[1]));
    }
    if ((hs = atts.get(ATTR_CENTER)) != null) {
      String s[] = hs.split(" ");
      pFlame.setCentreX(Double.parseDouble(s[0]));
      pFlame.setCentreY(Double.parseDouble(s[1]));
    }
    if ((hs = atts.get(ATTR_SCALE)) != null) {
      pFlame.setPixelsPerUnit(Double.parseDouble(hs));
    }
    if ((hs = atts.get(ATTR_ROTATE)) != null) {
      //      pFlame.setCamRoll(-Double.parseDouble(hs) * 180.0 / Math.PI);
      pFlame.setCamRoll(Double.parseDouble(hs));
    }
    if ((hs = atts.get(ATTR_OVERSAMPLE)) != null) {
      pFlame.setSpatialOversample(Integer.parseInt(hs));
    }
    if ((hs = atts.get(ATTR_COLOR_OVERSAMPLE)) != null) {
      pFlame.setColorOversample(Integer.parseInt(hs));
    }
    if ((hs = atts.get(ATTR_FILTER)) != null) {
      pFlame.setSpatialFilterRadius(Double.parseDouble(hs));
    }
    if ((hs = atts.get(ATTR_QUALITY)) != null) {
      pFlame.setSampleDensity(Double.parseDouble(hs));
    }
    if ((hs = atts.get(ATTR_BACKGROUND)) != null) {
      String s[] = hs.split(" ");
      pFlame.setBGColorRed(Tools.roundColor(255.0 * Double.parseDouble(s[0])));
      pFlame.setBGColorGreen(Tools.roundColor(255.0 * Double.parseDouble(s[1])));
      pFlame.setBGColorBlue(Tools.roundColor(255.0 * Double.parseDouble(s[2])));
    }
    if ((hs = atts.get(ATTR_BRIGHTNESS)) != null) {
      pFlame.setBrightness(Double.parseDouble(hs));
    }
    if ((hs = atts.get(ATTR_GAMMA)) != null) {
      pFlame.setGamma(Double.parseDouble(hs));
    }
    if ((hs = atts.get(ATTR_GAMMA_THRESHOLD)) != null) {
      pFlame.setGammaThreshold(Double.parseDouble(hs));
    }
    if ((hs = atts.get(ATTR_CAM_PERSP)) != null) {
      pFlame.setCamPerspective(Double.parseDouble(hs));
    }
    if ((hs = atts.get(ATTR_CAM_ZPOS)) != null) {
      pFlame.setCamZ(Double.parseDouble(hs));
    }
    if ((hs = atts.get(ATTR_CAM_DOF)) != null) {
      pFlame.setCamDOF(Double.parseDouble(hs));
    }
    if ((hs = atts.get(ATTR_CAM_PITCH)) != null) {
      pFlame.setCamPitch(Double.parseDouble(hs) * 180.0 / Math.PI);
    }
    if ((hs = atts.get(ATTR_CAM_YAW)) != null) {
      pFlame.setCamYaw(Double.parseDouble(hs) * 180.0 / Math.PI);
    }
    if ((hs = atts.get(ATTR_CAM_ZOOM)) != null) {
      pFlame.setCamZoom(Double.parseDouble(hs));
    }
    // Shading    
    if ((hs = atts.get(ATTR_SHADING_SHADING)) != null) {
      pFlame.getShadingInfo().setShading(hs.equalsIgnoreCase(Shading.PSEUDO3D.toString()) ? Shading.PSEUDO3D : Shading.FLAT);
    }
    if ((hs = atts.get(ATTR_SHADING_AMBIENT)) != null) {
      pFlame.getShadingInfo().setAmbient(Double.parseDouble(hs));
    }
    if ((hs = atts.get(ATTR_SHADING_DIFFUSE)) != null) {
      pFlame.getShadingInfo().setDiffuse(Double.parseDouble(hs));
    }
    if ((hs = atts.get(ATTR_SHADING_PHONG)) != null) {
      pFlame.getShadingInfo().setPhong(Double.parseDouble(hs));
    }
    if ((hs = atts.get(ATTR_SHADING_PHONGSIZE)) != null) {
      pFlame.getShadingInfo().setPhongSize(Double.parseDouble(hs));
    }
    int lightCount;
    if ((hs = atts.get(ATTR_SHADING_LIGHTCOUNT)) != null) {
      lightCount = Integer.parseInt(hs);
    }
    else {
      lightCount = 0;
    }
    for (int i = 0; i < lightCount; i++) {
      if ((hs = atts.get(ATTR_SHADING_LIGHTPOSX_ + i)) != null) {
        pFlame.getShadingInfo().setLightPosX(i, Double.parseDouble(hs));
      }
      if ((hs = atts.get(ATTR_SHADING_LIGHTPOSY_ + i)) != null) {
        pFlame.getShadingInfo().setLightPosY(i, Double.parseDouble(hs));
      }
      if ((hs = atts.get(ATTR_SHADING_LIGHTPOSZ_ + i)) != null) {
        pFlame.getShadingInfo().setLightPosZ(i, Double.parseDouble(hs));
      }
      if ((hs = atts.get(ATTR_SHADING_LIGHTRED_ + i)) != null) {
        pFlame.getShadingInfo().setLightRed(i, Integer.parseInt(hs));
      }
      if ((hs = atts.get(ATTR_SHADING_LIGHTGREEN_ + i)) != null) {
        pFlame.getShadingInfo().setLightGreen(i, Integer.parseInt(hs));
      }
      if ((hs = atts.get(ATTR_SHADING_LIGHTBLUE_ + i)) != null) {
        pFlame.getShadingInfo().setLightBlue(i, Integer.parseInt(hs));
      }
    }
  }

  private static final String ATTR_WEIGHT = "weight";
  private static final String ATTR_COLOR = "color";
  private static final String ATTR_OPACITY = "opacity";
  private static final String ATTR_COEFS = "coefs";
  private static final String ATTR_POST = "post";
  private static final String ATTR_CHAOS = "chaos";
  private static final String ATTR_SYMMETRY = "symmetry";

  private void parseXFormAttributes(XForm pXForm, String pXML) {
    Map<String, String> atts = parseAttributes(pXML);
    String hs;
    if ((hs = atts.get(ATTR_WEIGHT)) != null) {
      pXForm.setWeight(Double.parseDouble(hs));
    }
    if ((hs = atts.get(ATTR_COLOR)) != null) {
      pXForm.setColor(Double.parseDouble(hs));
    }
    if ((hs = atts.get(ATTR_OPACITY)) != null) {
      double opacity = Double.parseDouble(hs);
      pXForm.setOpacity(opacity);
      if (Math.abs(opacity) <= Tools.EPSILON) {
        pXForm.setDrawMode(DrawMode.HIDDEN);
      }
      else if (Math.abs(opacity - 1.0) > Tools.EPSILON) {
        pXForm.setDrawMode(DrawMode.OPAQUE);
      }
      else {
        pXForm.setDrawMode(DrawMode.NORMAL);
      }
    }
    if ((hs = atts.get(ATTR_SYMMETRY)) != null) {
      pXForm.setColorSymmetry(Double.parseDouble(hs));
    }
    if ((hs = atts.get(ATTR_COEFS)) != null) {
      String s[] = hs.split(" ");
      pXForm.setCoeff00(Double.parseDouble(s[0]));
      pXForm.setCoeff01(Double.parseDouble(s[1]));
      pXForm.setCoeff10(Double.parseDouble(s[2]));
      pXForm.setCoeff11(Double.parseDouble(s[3]));
      pXForm.setCoeff20(Double.parseDouble(s[4]));
      pXForm.setCoeff21(Double.parseDouble(s[5]));
    }
    if ((hs = atts.get(ATTR_POST)) != null) {
      String s[] = hs.split(" ");
      pXForm.setPostCoeff00(Double.parseDouble(s[0]));
      pXForm.setPostCoeff01(Double.parseDouble(s[1]));
      pXForm.setPostCoeff10(Double.parseDouble(s[2]));
      pXForm.setPostCoeff11(Double.parseDouble(s[3]));
      pXForm.setPostCoeff20(Double.parseDouble(s[4]));
      pXForm.setPostCoeff21(Double.parseDouble(s[5]));
    }
    if ((hs = atts.get(ATTR_CHAOS)) != null) {
      String s[] = hs.split(" ");
      for (int i = 0; i < s.length; i++) {
        pXForm.getModifiedWeights()[i] = Double.parseDouble(s[i]);
      }
    }
    // variations
    {
      List<String> variationNameList = VariationFuncList.getNameList();
      Iterator<String> it = atts.keySet().iterator();
      while (it.hasNext()) {
        String name = it.next();
        if (variationNameList.indexOf(name) >= 0) {
          VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(name);
          Variation variation = pXForm.addVariation(Double.parseDouble(atts.get(name)), varFunc);
          String paramNames[] = variation.getFunc().getParameterNames();
          for (String pName : paramNames) {
            String pHs;
            if ((pHs = atts.get(name + "_" + pName)) != null) {
              variation.getFunc().setParameter(pName, Double.parseDouble(pHs));
            }
          }
        }
      }
    }
  }

  public List<Flame> readFlamesfromXML(String pXML) {
    List<Flame> res = new ArrayList<Flame>();
    int pFlames = 0;
    while (true) {
      String flameXML;
      {
        int ps = pXML.indexOf("<flame ", pFlames);
        if (ps < 0)
          break;
        int pe = pXML.indexOf("</flame>", ps + 1);
        if (pe < 0)
          break;
        pFlames = pe + 8;
        flameXML = pXML.substring(ps, pFlames);
      }

      Flame flame = new Flame();
      res.add(flame);
      // Flame attributes
      {
        int ps = flameXML.indexOf("<flame ");
        int pe = flameXML.indexOf(">", ps + 1);
        String hs = flameXML.substring(ps + 7, pe);
        parseFlameAttributes(flame, hs);
      }
      // XForms
      {
        int p = 0;
        while (true) {
          int ps = flameXML.indexOf("<xform ", p + 1);
          if (ps < 0)
            break;
          int pe = flameXML.indexOf("/>", ps + 1);
          String hs = flameXML.substring(ps + 7, pe);
          XForm xForm = new XForm();
          parseXFormAttributes(xForm, hs);
          flame.getXForms().add(xForm);
          p = pe + 2;
        }
      }
      // FinalXForm
      {
        int p = 0;
        while (true) {
          int ps = flameXML.indexOf("<finalxform ", p + 1);
          if (ps < 0)
            break;
          int pe = flameXML.indexOf("/>", ps + 1);
          String hs = flameXML.substring(ps + 12, pe);
          XForm xForm = new XForm();
          parseXFormAttributes(xForm, hs);
          flame.setFinalXForm(xForm);
          p = pe + 2;
        }
      }

      // Colors
      {
        int p = 0;
        while (true) {
          int ps = flameXML.indexOf("<color ", p + 1);
          if (ps < 0)
            break;
          int pe = flameXML.indexOf("/>", ps + 1);
          String hs = flameXML.substring(ps + 7, pe);
          {
            int index = 0;
            int r = 0, g = 0, b = 0;
            Map<String, String> atts = parseAttributes(hs);
            String attr;
            if ((attr = atts.get(ATTR_INDEX)) != null) {
              index = Integer.parseInt(attr);
            }
            if ((attr = atts.get(ATTR_RGB)) != null) {
              String s[] = attr.split(" ");
              r = Integer.parseInt(s[0]);
              g = Integer.parseInt(s[1]);
              b = Integer.parseInt(s[2]);
            }
            flame.getPalette().setColor(index, r, g, b);
          }
          p = pe + 2;
        }
      }
      // Palette
      {
        int ps = flameXML.indexOf("<palette ");
        if (ps >= 0) {
          ps = flameXML.indexOf(">", ps + 1);
          int pe = flameXML.indexOf("</palette>", ps + 1);
          String hs = flameXML.substring(ps + 1, pe);
          StringBuilder sb = new StringBuilder();
          for (int i = 0; i < hs.length(); i++) {
            char c = hs.charAt(i);
            if ((c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
              sb.append(c);
            }
          }
          hs = sb.toString();
          if ((hs.length() % 6) != 0)
            throw new RuntimeException("Invalid/unknown palette");
          int index = 0;
          for (int i = 0; i < hs.length(); i += 6) {
            int r = Integer.parseInt(hs.substring(i, i + 2), 16);
            int g = Integer.parseInt(hs.substring(i + 2, i + 4), 16);
            int b = Integer.parseInt(hs.substring(i + 4, i + 6), 16);
            flame.getPalette().setColor(index++, r, g, b);
          }
        }
      }
    }
    return res;
  }

}
