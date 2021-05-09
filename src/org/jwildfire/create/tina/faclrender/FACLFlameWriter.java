/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke

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
package org.jwildfire.create.tina.faclrender;

import java.util.*;
import java.util.stream.Collectors;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.io.AbstractFlameWriter;
import org.jwildfire.create.tina.io.SimpleXMLBuilder;
import org.jwildfire.create.tina.io.SimpleXMLBuilder.Attribute;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.swing.MessageLogger;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFunc;

public class FACLFlameWriter extends AbstractFlameWriter {
  private final MessageLogger logger;

  public FACLFlameWriter() {
    this(null);
  }

  public FACLFlameWriter(MessageLogger logger) {
    this.logger = logger;
  }

  public void writeFlame(Flame pFlame, String pFilename) throws Exception {
    writeFlame(getFlameXML(pFlame), pFilename);
  }

  public void writeFlame(String flameXML, String pFilename) throws Exception {
    Tools.writeUTF8Textfile(pFilename,flameXML);
  }

  public String getFlameXML(Flame pFlame) throws Exception {
    Flame transformedFlame = transformFlame(pFlame);

    SimpleXMLBuilder xb = new SimpleXMLBuilder();
    List<SimpleXMLBuilder.Attribute<?>> flamesAttrList = new ArrayList<>();
    flamesAttrList.add(new Attribute<String>("name", ""));

    xb.beginElement("Flames", flamesAttrList);

    // Flame
    List<SimpleXMLBuilder.Attribute<?>> attrList = filterFlameAttrList(createFlameAttributes(transformedFlame, xb));
    Layer layer = transformedFlame.getFirstLayer();
    VariationSet variationSet;
    if (FACLRenderTools.isExtendedFaclRender() && FACLRenderTools.isUsingCUDA()) {
      variationSet = new VariationSet(transformedFlame);
      attrList.add(new SimpleXMLBuilder.Attribute<String>("varset", variationSet.getUuid()));
    }
    else {
      variationSet = null;
    }

    xb.beginElement("flame", attrList);
    // XForm
    for (XForm xForm : layer.getXForms()) {
      xb.beginElement("xform", filterXFormAttrList( createXFormAttrList(xb, layer, xForm, null, false) ) );
      for(int priority: extractVariationPriorities(xForm)) {
        xb.emptyElement("variationGroup", createVariationGroupAttrList(xb, priority, xForm, variationSet));
      }
      xb.endElement("xform");
    }
    // FinalXForms
    for (XForm xForm : layer.getFinalXForms()) {
      xb.beginElement("finalxform", filterXFormAttrList(createXFormAttrList(xb, layer, xForm, null, false)));
      for(int priority: extractVariationPriorities(xForm)) {
        xb.emptyElement("variationGroup", createVariationGroupAttrList(xb, priority, xForm, variationSet));
      }
      xb.endElement("finalxform");
    }
    // Gradient
    addGradient(xb, layer);
    xb.endElement("flame");
    // VariationSet
    if(variationSet!=null) {
      xb.beginElement("variationSet", createVariationSetAttrList(xb, transformedFlame, variationSet.getVariationNames(), variationSet.getUuid()));
      String cudaLibrary = FACLRenderTools.getCudaLibrary();
      for(String varname: variationSet.getVariationNames()) {
        boolean found = false;
        String code = variationSet.getCode(varname);
        if(code!=null) {
          xb.addContent(code);
          found=true;
        }
        if(!found) {
          int startIdx = cudaLibrary.indexOf("<variation name=\""+varname+"\"");
          if(startIdx>0) {
            int endIdx = cudaLibrary.indexOf("</variation>", startIdx);
            if(endIdx>startIdx) {
              String varEntry = cudaLibrary.substring(startIdx, endIdx + 12);
              xb.addContent(varEntry);
              found = true;
            }
          }
        }
        if(!found) {
          String msg = "Could not find variation code for \"" + varname + "\"\n";
          if(logger!=null) {
            logger.logMessage(msg);
          } else {
            System.err.println(msg);
          }
        }
      }
      xb.endElement("variationSet");
    }
    xb.endElement("Flames");
    return xb.buildXML();
  }

  private List<Attribute<?>> createVariationGroupAttrList(SimpleXMLBuilder xb, int priority, XForm xForm, VariationnameTransformer variationnameTransformer) {
    List<Attribute<?>> attrList = new ArrayList<>();
    attrList.add(new Attribute<String>("normal", "1" /*priority >= 0 ? "1" : "0"*/));

    Set<String> varNames = new HashSet<>();
    for (int vIdx = 0; vIdx < xForm.getVariationCount(); vIdx++) {
      Variation v = xForm.getVariation(vIdx);
      if (v.getPriority() == priority) {
        VariationFunc func = v.getFunc();
        String transformedFuncName;
        String uniqueFuncName;
        int funcNameCounter = 1;
        {
          transformedFuncName =
              variationnameTransformer != null
                  ? variationnameTransformer.transformVariationName(func.getName())
                  : func.getName();
          while (true) {
            uniqueFuncName = transformedFuncName + "-" + funcNameCounter;
            if (!varNames.contains(uniqueFuncName)) {
              varNames.add(uniqueFuncName);
              break;
            } else {
              funcNameCounter++;
            }
          }
        }

        attrList.add(xb.createAttr(transformedFuncName /* uniqueFuncName*/, v.getAmount()));
        // params
        {
          String params[] = func.getParameterNames();
          if (params != null) {
            Object vals[] = func.getParameterValues();
            for (int i = 0; i < params.length; i++) {
              if (vals[i] instanceof Integer) {
                attrList.add(xb.createAttr((transformedFuncName + "_" + params[i]/*+"-"+funcNameCounter*/), (Integer) vals[i]));
              }
              else if (vals[i] instanceof Double) {
                attrList.add(xb.createAttr((transformedFuncName + "_" + params[i]/*+"-"+funcNameCounter*/), (Double) vals[i]));
              }
              else {
                throw new IllegalStateException();
              }
            }
          }
        }



      }
    }

    return attrList;
  }

  private List<Integer> extractVariationPriorities(XForm xForm) {
    List<Integer> res = new ArrayList<>();
    for(int i=0;i<xForm.getVariationCount();i++) {
      Integer boxedVal=xForm.getVariation(i).getPriority();
      if(!res.contains(boxedVal)) {
        res.add(boxedVal);
      }
    }
    res.sort(Integer::compareTo);
    return res;
  }

  private List<Attribute<?>> createVariationSetAttrList(SimpleXMLBuilder xb, Flame pFlame, Set<String> variationNames, String varSetUuid) {
    ArrayList<Attribute<?>> res = new ArrayList<>();
    res.add(xb.createAttr("name", "JWFVarSet_"+varSetUuid));
    res.add(xb.createAttr("version", "1.0"));
    res.add(xb.createAttr("defaultVariation", "linear"));
    res.add(xb.createAttr("is3Dcapable", "yes"));
    res.add(xb.createAttr("uuid", varSetUuid));
    res.add(xb.createAttr("structName", "varpar"));
    return res;
  }

  // remove unnecessary xform-attributes
  private final static Set<String> XFORM_ATTR_BLACKLIST = new HashSet<>(Arrays.asList("color_type", "material", "material_speed", "mod_gamma", "mod_gamma_speed",
          "mod_contrast", "mod_contrast_speed", "mod_saturation", "mod_saturation_speed", "mod_saturation_speed",
          "mod_hue", "mod_hue_speed"));

  private List<Attribute<?>> filterXFormAttrList(List<Attribute<?>> pSrc) {
    return pSrc.stream().filter(a ->  !a.getName().endsWith("fx_priority") && !a.getName().startsWith("wfield_") &&  !a.getName().contains("Curve_") && !XFORM_ATTR_BLACKLIST.contains(a.getName())).collect(Collectors.toList());
  }

  // remove unnecessary flame-attributes
  private final static Set<String> FLAME_ATTR_BLACKLIST = new HashSet<>(Arrays.asList("smooth_gradient", "version", "filter_type", "filter_indicator",
          "filter_sharpness", "filter_low_density", "ai_post_denoiser", "post_optix_denoiser_blend", "background_type",
          "background_ul", "background_ur", "background_ll", "background_lr", "background_cc", "fg_opacity",
          "post_blur_radius", "post_blur_fade", "post_blur_falloff", "mixer_mode", "frame", "frame_count",
          "fps", "zbuffer_scale", "zbuffer_bias", "zbuffer_filename", "low_density_brightness", "balancing_red",
          "balancing_green", "balancing_blue"));
  private List<Attribute<?>> filterFlameAttrList(List<Attribute<?>> pSrc) {
    return pSrc.stream().filter(a -> !a.getName().startsWith("grad_edit_") && !a.getName().contains("Curve_") && !a.getName().startsWith("post_symmetry_") && !FLAME_ATTR_BLACKLIST.contains(a.getName())).collect(Collectors.toList());
  }

  // apply some FACLRender-specific changes
  private Flame transformFlame(Flame pFlame) {
    Flame flame = pFlame.makeCopy();
    flame.setPixelsPerUnit(flame.getPixelsPerUnit() * flame.getCamZoom());
    flame.setCamZoom(1.0);
    return flame;
  }

  private void addGradient(SimpleXMLBuilder xb, Layer layer) {
    RGBPalette palette = layer.getPalette();
    for (int i = 0; i < 256; i++) {
      List<SimpleXMLBuilder.Attribute<?>> attrList = new ArrayList<>();
      attrList.add(new Attribute<Integer>("index", i));
      String rgbStr = palette.getColor(i).getRed() + " " + palette.getColor(i).getGreen() + " " + palette.getColor(i).getBlue();
      attrList.add(new Attribute<String>("rgb", rgbStr));
      xb.simpleElement("color", null, attrList);
    }
  }


}
