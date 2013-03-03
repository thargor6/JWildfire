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

import static org.jwildfire.base.mathlib.MathLib.EPSILON;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.DrawMode;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Shading;
import org.jwildfire.create.tina.base.ShadingInfo;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFunc;

public class Flam3Writer {

  public void writeFlame(Flame pFlame, String pFilename) throws Exception {
    Tools.writeUTF8Textfile(pFilename, getFlameXML(pFlame));
  }

  private List<SimpleXMLBuilder.Attribute<?>> createXFormAttrList(SimpleXMLBuilder pXB, Flame pFlame, XForm pXForm) {
    List<SimpleXMLBuilder.Attribute<?>> attrList = new ArrayList<SimpleXMLBuilder.Attribute<?>>();
    attrList.add(pXB.createAttr("weight", pXForm.getWeight()));
    attrList.add(pXB.createAttr("color", pXForm.getColor()));
    if (pXForm.getDrawMode().equals(DrawMode.OPAQUE)) {
      attrList.add(pXB.createAttr("opacity", pXForm.getOpacity()));
    }
    else if (pXForm.getDrawMode().equals(DrawMode.HIDDEN)) {
      attrList.add(pXB.createAttr("opacity", 0.0));
    }
    attrList.add(pXB.createAttr("symmetry", pXForm.getColorSymmetry()));
    if (pXForm.getAntialiasAmount() > EPSILON) {
      attrList.add(pXB.createAttr("antialias_amount", pXForm.getAntialiasAmount()));
      attrList.add(pXB.createAttr("antialias_radius", pXForm.getAntialiasRadius()));
    }

    for (int vIdx = 0; vIdx < pXForm.getVariationCount(); vIdx++) {
      Variation v = pXForm.getVariation(vIdx);
      VariationFunc func = v.getFunc();
      attrList.add(pXB.createAttr(func.getName(), v.getAmount()));
      // params
      {
        String params[] = func.getParameterNames();
        if (params != null) {
          Object vals[] = func.getParameterValues();
          for (int i = 0; i < params.length; i++) {
            if (vals[i] instanceof Integer) {
              attrList.add(pXB.createAttr((func.getName() + "_" + params[i]), (Integer) vals[i]));
            }
            else if (vals[i] instanceof Double) {
              attrList.add(pXB.createAttr((func.getName() + "_" + params[i]), (Double) vals[i]));
            }
            else {
              throw new IllegalStateException();
            }
          }
        }
      }
      // ressources
      {
        String ressNames[] = func.getRessourceNames();
        if (ressNames != null) {
          byte vals[][] = func.getRessourceValues();
          for (int i = 0; i < ressNames.length; i++) {
            String hexStr = vals[i] != null && vals[i].length > 0 ? Tools.byteArrayToHexString(vals[i]) : "";
            attrList.add(pXB.createAttr((func.getName() + "_" + ressNames[i]), hexStr));
          }
        }
      }
    }

    attrList.add(pXB.createAttr("coefs", pXForm.getCoeff00() + " " + pXForm.getCoeff01() + " " + pXForm.getCoeff10() + " " + pXForm.getCoeff11() + " " + pXForm.getCoeff20() + " " + pXForm.getCoeff21()));
    if (pXForm.isHasPostCoeffs()) {
      attrList.add(pXB.createAttr("post", pXForm.getPostCoeff00() + " " + pXForm.getPostCoeff01() + " " + pXForm.getPostCoeff10() + " " + pXForm.getPostCoeff11() + " " + pXForm.getPostCoeff20() + " " + pXForm.getPostCoeff21()));
    }
    {
      String hs = "";
      for (int i = 0; i < pFlame.getXForms().size() - 1; i++) {
        hs += pXForm.getModifiedWeights()[i] + " ";
      }
      hs += pXForm.getModifiedWeights()[pFlame.getXForms().size() - 1];
      attrList.add(pXB.createAttr("chaos", hs));
    }

    return attrList;
  }

  public String getFlameXML(Flame pFlame) {
    SimpleXMLBuilder xb = new SimpleXMLBuilder();
    // Flame
    List<SimpleXMLBuilder.Attribute<?>> attrList = new ArrayList<SimpleXMLBuilder.Attribute<?>>();
    if (!pFlame.getName().equals("")) {
      attrList.add(xb.createAttr("name", pFlame.getName()));
    }
    attrList.add(xb.createAttr("version", Tools.APP_TITLE + " " + Tools.APP_VERSION));
    attrList.add(xb.createAttr("size", pFlame.getWidth() + " " + pFlame.getHeight()));
    attrList.add(xb.createAttr("center", pFlame.getCentreX() + " " + pFlame.getCentreY()));
    attrList.add(xb.createAttr("scale", pFlame.getPixelsPerUnit()));
    attrList.add(xb.createAttr("rotate", pFlame.getCamRoll()));
    attrList.add(xb.createAttr("filter", pFlame.getSpatialFilterRadius()));
    attrList.add(xb.createAttr("filter_kernel", pFlame.getSpatialFilterKernel().toString()));
    attrList.add(xb.createAttr("quality", pFlame.getSampleDensity()));
    attrList.add(xb.createAttr("background", (double) pFlame.getBGColorRed() / 255.0 + " " + (double) pFlame.getBGColorGreen() / 255.0 + " " + (double) pFlame.getBGColorBlue() / 255.0));
    attrList.add(xb.createAttr("bg_transparency", pFlame.isBGTransparency() ? "1" : "0"));
    attrList.add(xb.createAttr("brightness", pFlame.getBrightness()));
    attrList.add(xb.createAttr("gamma", pFlame.getGamma()));
    attrList.add(xb.createAttr("gamma_threshold", pFlame.getGammaThreshold()));
    attrList.add(xb.createAttr("vibrancy", pFlame.getVibrancy()));
    attrList.add(xb.createAttr("contrast", pFlame.getContrast()));
    attrList.add(xb.createAttr("estimator_enabled", pFlame.isDeFilterEnabled() ? "1" : "0"));
    attrList.add(xb.createAttr("estimator_radius", pFlame.getDeFilterMaxRadius()));
    attrList.add(xb.createAttr("estimator_minimum", pFlame.getDeFilterMinRadius()));
    attrList.add(xb.createAttr("estimator_curve", pFlame.getDeFilterCurve()));
    attrList.add(xb.createAttr("estimator_kernel", pFlame.getDeFilterKernel().toString()));
    attrList.add(xb.createAttr("temporal_samples", 1.0));
    attrList.add(xb.createAttr("cam_zoom", pFlame.getCamZoom()));
    attrList.add(xb.createAttr("cam_pitch", (pFlame.getCamPitch() * Math.PI) / 180.0));
    attrList.add(xb.createAttr("cam_yaw", (pFlame.getCamYaw() * Math.PI) / 180.0));
    attrList.add(xb.createAttr("cam_persp", pFlame.getCamPerspective()));
    attrList.add(xb.createAttr("cam_xfocus", pFlame.getFocusX()));
    attrList.add(xb.createAttr("cam_yfocus", pFlame.getFocusY()));
    attrList.add(xb.createAttr("cam_zfocus", pFlame.getFocusZ()));
    if (pFlame.getDimishZ() != 0.0) {
      attrList.add(xb.createAttr("cam_zdimish", pFlame.getDimishZ()));
    }
    attrList.add(xb.createAttr("cam_zpos", pFlame.getCamZ()));
    attrList.add(xb.createAttr("cam_dof", pFlame.getCamDOF()));
    attrList.add(xb.createAttr("cam_dof_area", pFlame.getCamDOFArea()));
    attrList.add(xb.createAttr("cam_dof_exponent", pFlame.getCamDOFExponent()));
    if (pFlame.isNewCamDOF()) {
      attrList.add(xb.createAttr("new_dof", "1"));
    }
    if (pFlame.isPreserveZ()) {
      attrList.add(xb.createAttr("preserve_z", "1"));
    }
    if (pFlame.getResolutionProfile() != null && pFlame.getResolutionProfile().length() > 0)
      attrList.add(xb.createAttr("resolution_profile", pFlame.getResolutionProfile()));
    if (pFlame.getQualityProfile() != null && pFlame.getQualityProfile().length() > 0)
      attrList.add(xb.createAttr("quality_profile", pFlame.getQualityProfile()));
    ShadingInfo shadingInfo = pFlame.getShadingInfo();
    attrList.add(xb.createAttr("shading_shading", shadingInfo.getShading().toString()));
    if (shadingInfo.getShading() == Shading.PSEUDO3D) {
      attrList.add(xb.createAttr("shading_ambient", shadingInfo.getAmbient()));
      attrList.add(xb.createAttr("shading_diffuse", shadingInfo.getDiffuse()));
      attrList.add(xb.createAttr("shading_phong", shadingInfo.getPhong()));
      attrList.add(xb.createAttr("shading_phongSize", shadingInfo.getPhongSize()));
      attrList.add(xb.createAttr("shading_lightCount", shadingInfo.getLightCount()));
      for (int i = 0; i < shadingInfo.getLightCount(); i++) {
        attrList.add(xb.createAttr("shading_lightPosX_" + i, shadingInfo.getLightPosX()[i]));
        attrList.add(xb.createAttr("shading_lightPosY_" + i, shadingInfo.getLightPosY()[i]));
        attrList.add(xb.createAttr("shading_lightPosZ_" + i, shadingInfo.getLightPosZ()[i]));
        attrList.add(xb.createAttr("shading_lightRed_" + i, shadingInfo.getLightRed()[i]));
        attrList.add(xb.createAttr("shading_lightGreen_" + i, shadingInfo.getLightGreen()[i]));
        attrList.add(xb.createAttr("shading_lightBlue_" + i, shadingInfo.getLightBlue()[i]));
      }
    }
    else if (shadingInfo.getShading() == Shading.BLUR) {
      attrList.add(xb.createAttr("shading_blurRadius", shadingInfo.getBlurRadius()));
      attrList.add(xb.createAttr("shading_blurFade", shadingInfo.getBlurFade()));
      attrList.add(xb.createAttr("shading_blurFallOff", shadingInfo.getBlurFallOff()));
    }
    else if (shadingInfo.getShading() == Shading.DISTANCE_COLOR) {
      attrList.add(xb.createAttr(Flam3Reader.ATTR_SHADING_DISTANCE_COLOR_RADIUS, shadingInfo.getDistanceColorRadius()));
      attrList.add(xb.createAttr(Flam3Reader.ATTR_SHADING_DISTANCE_COLOR_SCALE, shadingInfo.getDistanceColorScale()));
      attrList.add(xb.createAttr(Flam3Reader.ATTR_SHADING_DISTANCE_COLOR_EXPONENT, shadingInfo.getDistanceColorExponent()));
      attrList.add(xb.createAttr(Flam3Reader.ATTR_SHADING_DISTANCE_COLOR_OFFSETX, shadingInfo.getDistanceColorOffsetX()));
      attrList.add(xb.createAttr(Flam3Reader.ATTR_SHADING_DISTANCE_COLOR_OFFSETY, shadingInfo.getDistanceColorOffsetY()));
      attrList.add(xb.createAttr(Flam3Reader.ATTR_SHADING_DISTANCE_COLOR_OFFSETZ, shadingInfo.getDistanceColorOffsetZ()));
      attrList.add(xb.createAttr(Flam3Reader.ATTR_SHADING_DISTANCE_COLOR_STYLE, shadingInfo.getDistanceColorStyle()));
      attrList.add(xb.createAttr(Flam3Reader.ATTR_SHADING_DISTANCE_COLOR_COORDINATE, shadingInfo.getDistanceColorCoordinate()));
      attrList.add(xb.createAttr(Flam3Reader.ATTR_SHADING_DISTANCE_COLOR_SHIFT, shadingInfo.getDistanceColorShift()));
    }
    xb.beginElement("flame", attrList);
    // XForm
    for (XForm xForm : pFlame.getXForms()) {
      xb.emptyElement("xform", createXFormAttrList(xb, pFlame, xForm));
    }
    // FinalXForms
    for (XForm xForm : pFlame.getFinalXForms()) {
      xb.emptyElement("finalxform", createXFormAttrList(xb, pFlame, xForm));
    }
    // Palette
    {
      RGBPalette palette = pFlame.getPalette();
      xb.beginElement("palette",
          xb.createAttr("count", palette.getSize()),
          xb.createAttr("format", "RGB"));
      StringBuilder rgb = new StringBuilder();
      for (int i = 0; i < palette.getSize(); i++) {
        String hs;
        hs = Integer.toHexString(palette.getColor(i).getRed()).toUpperCase();
        rgb.append(hs.length() > 1 ? hs : "0" + hs);
        hs = Integer.toHexString(palette.getColor(i).getGreen()).toUpperCase();
        rgb.append(hs.length() > 1 ? hs : "0" + hs);
        hs = Integer.toHexString(palette.getColor(i).getBlue()).toUpperCase();
        rgb.append(hs.length() > 1 ? hs : "0" + hs);
        if ((i + 1) % 12 == 0) {
          rgb.append("\n");
        }
      }
      xb.addContent(rgb.toString());
      xb.endElement("palette");
    }
    xb.endElement("flame");
    return xb.buildXML();
  }
}
