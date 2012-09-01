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

import static org.jwildfire.create.tina.base.Constants.AVAILABILITY_CUDA;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.DrawMode;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Shading;
import org.jwildfire.create.tina.base.ShadingInfo;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.palette.RGBColor;
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
            String hexStr = vals[i] != null ? Tools.byteArrayToHexString(vals[i]) : "";
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
    attrList.add(xb.createAttr("name", Tools.APP_TITLE));
    attrList.add(xb.createAttr("version", Tools.APP_VERSION));
    attrList.add(xb.createAttr("size", pFlame.getWidth() + " " + pFlame.getHeight()));
    attrList.add(xb.createAttr("center", pFlame.getCentreX() + " " + pFlame.getCentreY()));
    attrList.add(xb.createAttr("scale", pFlame.getPixelsPerUnit()));
    attrList.add(xb.createAttr("rotate", pFlame.getCamRoll()));
    attrList.add(xb.createAttr("oversample", pFlame.getSpatialOversample()));
    attrList.add(xb.createAttr("color_oversample", pFlame.getColorOversample()));
    attrList.add(xb.createAttr("filter", pFlame.getSpatialFilterRadius()));
    attrList.add(xb.createAttr("quality", pFlame.getSampleDensity()));
    attrList.add(xb.createAttr("background", (double) pFlame.getBGColorRed() / 255.0 + " " + (double) pFlame.getBGColorGreen() / 255.0 + " " + (double) pFlame.getBGColorBlue() / 255.0));
    attrList.add(xb.createAttr("brightness", pFlame.getBrightness()));
    attrList.add(xb.createAttr("gamma", pFlame.getGamma()));
    attrList.add(xb.createAttr("gamma_threshold", pFlame.getGammaThreshold()));
    attrList.add(xb.createAttr("estimator_radius", 9));
    attrList.add(xb.createAttr("estimator_minimum", 0));
    attrList.add(xb.createAttr("estimator_curve", 0.4));
    attrList.add(xb.createAttr("temporal_samples", 1.0));
    attrList.add(xb.createAttr("cam_zoom", pFlame.getCamZoom()));
    attrList.add(xb.createAttr("cam_pitch", (pFlame.getCamPitch() * Math.PI) / 180.0));
    attrList.add(xb.createAttr("cam_yaw", (pFlame.getCamYaw() * Math.PI) / 180.0));
    attrList.add(xb.createAttr("cam_persp", pFlame.getCamPerspective()));
    attrList.add(xb.createAttr("cam_zpos", pFlame.getCamZ()));
    attrList.add(xb.createAttr("cam_dof", pFlame.getCamDOF()));
    if (pFlame.isPreserveZ()) {
      attrList.add(xb.createAttr("preserve_z", "1"));
    }
    attrList.add(xb.createAttr("resolution_profile", pFlame.getResolutionProfile()));
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
    xb.beginElement("flame", attrList);
    // XForm
    for (XForm xForm : pFlame.getXForms()) {
      xb.emptyElement("xform", createXFormAttrList(xb, pFlame, xForm));
    }

    if (pFlame.getFinalXForm() != null) {
      xb.emptyElement("finalxform", createXFormAttrList(xb, pFlame, pFlame.getFinalXForm()));
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

  private String doubleToCUDA(double pVal) {
    String res = Tools.doubleToString(pVal);
    return res.indexOf(".") < 0 ? res + ".0" : res;
  }

  public void checkFlameForCUDA(Flame pFlame) {
    for (XForm xForm : pFlame.getXForms()) {
      if (xForm.getVariationCount() > 0) {
        for (int i = 0; i < xForm.getVariationCount(); i++) {
          checkVariationForCUDA(xForm.getVariation(i));
        }
      }
    }
    if (pFlame.getFinalXForm() != null && pFlame.getFinalXForm().getVariationCount() > 0) {
      for (int i = 0; i < pFlame.getFinalXForm().getVariationCount(); i++) {
        checkVariationForCUDA(pFlame.getFinalXForm().getVariation(i));
      }
    }
  }

  private void checkVariationForCUDA(Variation pVariation) {
    if ((pVariation.getFunc().getAvailability() & AVAILABILITY_CUDA) == 0) {
      throw new RuntimeException("Variation <" + pVariation.getFunc().getName() + "> is currently not available for the external renderer. Please try to remove it or contact the author.");
    }
  }

  public String getFlameCUDA(Flame pFlame) {
    checkFlameForCUDA(pFlame);
    StringBuffer sb = new StringBuffer();
    sb.append("Flame *createExampleFlame() {\n");
    sb.append("  Flame *flame;\n");
    sb.append("  hostMalloc((void**)&flame,sizeof(Flame));\n");
    sb.append("  flame->create();\n");
    sb.append("  flame->width=" + pFlame.getWidth() + ";\n");
    sb.append("  flame->height=" + pFlame.getHeight() + ";\n");
    sb.append("  flame->centreX=" + doubleToCUDA(pFlame.getCentreX()) + "f;\n");
    sb.append("  flame->centreY=" + doubleToCUDA(pFlame.getCentreY()) + "f;\n");
    sb.append("  flame->pixelsPerUnit=" + doubleToCUDA(pFlame.getPixelsPerUnit()) + "f;\n");
    sb.append("  flame->camRoll=" + doubleToCUDA(pFlame.getCamRoll()) + "f;\n");
    sb.append("  flame->spatialOversample=" + pFlame.getSpatialOversample() + ";\n");
    sb.append("  flame->colorOversample=" + pFlame.getColorOversample() + ";\n");
    sb.append("  flame->spatialFilterRadius=" + doubleToCUDA(pFlame.getSpatialFilterRadius()) + "f;\n");
    sb.append("  flame->sampleDensity=" + doubleToCUDA(pFlame.getSampleDensity()) + "f;\n");
    sb.append("  flame->bgColorRed=" + pFlame.getBGColorRed() + ";\n");
    sb.append("  flame->bgColorGreen=" + pFlame.getBGColorGreen() + ";\n");
    sb.append("  flame->bgColorBlue=" + pFlame.getBGColorBlue() + ";\n");
    sb.append("  flame->brightness=" + doubleToCUDA(pFlame.getBrightness()) + "f;\n");
    sb.append("  flame->gamma=" + doubleToCUDA(pFlame.getGamma()) + "f;\n");
    sb.append("  flame->gammaThreshold=" + doubleToCUDA(pFlame.getGammaThreshold()) + "f;\n");
    sb.append("  flame->camZoom=" + doubleToCUDA(pFlame.getCamZoom()) + "f;\n");
    sb.append("  flame->camPitch=" + doubleToCUDA(pFlame.getCamPitch()) + "f;\n");
    sb.append("  flame->camYaw=" + doubleToCUDA(pFlame.getCamYaw()) + ";\n");
    sb.append("  flame->camPerspective=" + doubleToCUDA(pFlame.getCamPerspective()) + "f;\n");
    sb.append("  flame->camZ=" + doubleToCUDA(pFlame.getCamZ()) + "f;\n");
    sb.append("  flame->camDOF=" + doubleToCUDA(pFlame.getCamDOF()) + "f;\n");
    sb.append("\n");
    for (int i = 0; i < pFlame.getPalette().getSize(); i++) {
      RGBColor color = pFlame.getPalette().getColor(i);
      sb.append("  flame->palette->setColor(" + i + "," + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ");\n");
    }
    sb.append("\n");
    sb.append("  flame->xFormCount=" + pFlame.getXForms().size() + ";\n");
    sb.append("  hostMalloc((void**)&flame->xForms, flame->xFormCount*sizeof(XForm*));\n");
    for (int i = 0; i < pFlame.getXForms().size(); i++) {
      XForm xForm = pFlame.getXForms().get(i);
      sb.append("  // xForm" + (i + 1) + "\n");
      sb.append("  hostMalloc((void**)&flame->xForms[" + i + "], sizeof(XForm));\n");
      sb.append("  flame->xForms[" + i + "]->init();\n");
      sb.append("  flame->xForms[" + i + "]->weight=" + doubleToCUDA(xForm.getWeight()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->color=" + doubleToCUDA(xForm.getColor()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->colorSymmetry=" + doubleToCUDA(xForm.getColorSymmetry()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->coeff00=" + doubleToCUDA(xForm.getCoeff00()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->coeff01=" + doubleToCUDA(xForm.getCoeff01()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->coeff10=" + doubleToCUDA(xForm.getCoeff10()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->coeff11=" + doubleToCUDA(xForm.getCoeff11()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->coeff20=" + doubleToCUDA(xForm.getCoeff20()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->coeff21=" + doubleToCUDA(xForm.getCoeff21()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->postCoeff00=" + doubleToCUDA(xForm.getPostCoeff00()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->postCoeff01=" + doubleToCUDA(xForm.getPostCoeff01()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->postCoeff10=" + doubleToCUDA(xForm.getPostCoeff10()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->postCoeff11=" + doubleToCUDA(xForm.getPostCoeff11()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->postCoeff20=" + doubleToCUDA(xForm.getPostCoeff20()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->postCoeff21=" + doubleToCUDA(xForm.getPostCoeff21()) + "f;\n");
      for (int j = 0; j < xForm.getVariationCount(); j++) {
        Variation var = xForm.getVariation(j);
        String paramNames[] = var.getFunc().getParameterNames();
        if (paramNames == null || paramNames.length == 0) {
          sb.append("  flame->xForms[" + i + "]->addVariation(variationFactory->newInstance(\"" + var.getFunc().getName() + "\") , " + doubleToCUDA(var.getAmount()) + "f);\n");
        }
        else {
          Object values[] = var.getFunc().getParameterValues();
          sb.append("  {\n");
          sb.append("    Variation *var=flame->xForms[" + i + "]->addVariation(variationFactory->newInstance(\"" + var.getFunc().getName() + "\") , " + doubleToCUDA(var.getAmount()) + "f);\n");
          for (int k = 0; k < paramNames.length; k++) {
            Object val = values[k];
            if (val == null) {
              sb.append("    var->setParameter(\"" + paramNames[k] + "\",0.0f);\n");
            }
            else if (val instanceof Integer) {
              sb.append("    var->setParameter(\"" + paramNames[k] + "\"," + ((Integer) val).intValue() + ".0f);\n");
            }
            else {
              sb.append("    var->setParameter(\"" + paramNames[k] + "\"," + doubleToCUDA((Double) val) + "f);\n");
            }
          }
          sb.append("  }\n");
        }
      }
    }
    if (pFlame.getFinalXForm() != null) {
      XForm xForm = pFlame.getFinalXForm();
      sb.append("  // final xForm\n");
      sb.append("  hostMalloc((void**)&flame->finalXForm, sizeof(XForm));\n");
      sb.append("  flame->finalXForm->init();\n");
      sb.append("  flame->finalXForm->weight=" + doubleToCUDA(xForm.getWeight()) + "f;\n");
      sb.append("  flame->finalXForm->color=" + doubleToCUDA(xForm.getColor()) + "f;\n");
      sb.append("  flame->finalXForm->colorSymmetry=" + doubleToCUDA(xForm.getColorSymmetry()) + "f;\n");
      sb.append("  flame->finalXForm->coeff00=" + doubleToCUDA(xForm.getCoeff00()) + "f;\n");
      sb.append("  flame->finalXForm->coeff01=" + doubleToCUDA(xForm.getCoeff01()) + "f;\n");
      sb.append("  flame->finalXForm->coeff10=" + doubleToCUDA(xForm.getCoeff10()) + "f;\n");
      sb.append("  flame->finalXForm->coeff11=" + doubleToCUDA(xForm.getCoeff11()) + "f;\n");
      sb.append("  flame->finalXForm->coeff20=" + doubleToCUDA(xForm.getCoeff20()) + "f;\n");
      sb.append("  flame->finalXForm->coeff21=" + doubleToCUDA(xForm.getCoeff21()) + "f;\n");
      sb.append("  flame->finalXForm->postCoeff00=" + doubleToCUDA(xForm.getPostCoeff00()) + "f;\n");
      sb.append("  flame->finalXForm->postCoeff01=" + doubleToCUDA(xForm.getPostCoeff01()) + "f;\n");
      sb.append("  flame->finalXForm->postCoeff10=" + doubleToCUDA(xForm.getPostCoeff10()) + "f;\n");
      sb.append("  flame->finalXForm->postCoeff11=" + doubleToCUDA(xForm.getPostCoeff11()) + "f;\n");
      sb.append("  flame->finalXForm->postCoeff20=" + doubleToCUDA(xForm.getPostCoeff20()) + "f;\n");
      sb.append("  flame->finalXForm->postCoeff21=" + doubleToCUDA(xForm.getPostCoeff21()) + "f;\n");
      for (int j = 0; j < xForm.getVariationCount(); j++) {
        Variation var = xForm.getVariation(j);
        String paramNames[] = var.getFunc().getParameterNames();
        if (paramNames == null || paramNames.length == 0) {
          sb.append("  flame->finalXForm->addVariation(variationFactory->newInstance(\"" + var.getFunc().getName() + "\") , " + doubleToCUDA(var.getAmount()) + "f);\n");
        }
        else {
          Object values[] = var.getFunc().getParameterValues();
          sb.append("  {\n");
          sb.append("    Variation *var=flame->finalXForm->addVariation(variationFactory->newInstance(\"" + var.getFunc().getName() + "\") , " + doubleToCUDA(var.getAmount()) + "f);\n");
          for (int k = 0; k < paramNames.length; k++) {
            Object val = values[k];
            if (val == null) {
              sb.append("    var->setParameter(\"" + paramNames[k] + "\",0.0f);\n");
            }
            else if (val instanceof Integer) {
              sb.append("    var->setParameter(\"" + paramNames[k] + "\"," + ((Integer) val).intValue() + ".0f);\n");
            }
            else {
              sb.append("    var->setParameter(\"" + paramNames[k] + "\"," + doubleToCUDA((Double) val) + "f);\n");
            }
          }
          sb.append("  }\n");
        }
      }
    }
    sb.append("\n");
    sb.append("  return flame;\n");
    sb.append("}\n\n");
    return sb.toString();
  }
}
