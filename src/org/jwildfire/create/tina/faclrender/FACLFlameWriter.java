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
import org.jwildfire.create.tina.base.PostSymmetryType;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.io.AbstractFlameWriter;
import org.jwildfire.create.tina.io.SimpleXMLBuilder;
import org.jwildfire.create.tina.io.SimpleXMLBuilder.Attribute;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.swing.MessageLogger;
import org.jwildfire.create.tina.variation.*;

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
    FlameTransformationContext transformationContext = createTransformCtx(transformedFlame);

    SimpleXMLBuilder xb = new SimpleXMLBuilder();
    List<SimpleXMLBuilder.Attribute<?>> flamesAttrList = new ArrayList<>();
    flamesAttrList.add(new Attribute<String>("name", ""));
    xb.beginElement("Flames", flamesAttrList);

    // Flame
    List<SimpleXMLBuilder.Attribute<?>> attrList = filterFlameAttrList(createFlameAttributes(transformedFlame, xb));
    Layer layer = transformedFlame.getFirstLayer();
    VariationSet variationSet = new VariationSet(transformedFlame, transformationContext, logger);
    attrList.add(new SimpleXMLBuilder.Attribute<String>("varset", variationSet.getUuid()));
    attrList.add(new SimpleXMLBuilder.Attribute<String>("highlight_power", "-1"));
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
      for(VariationInstance instance: variationSet.getVariationInstances()) {
        xb.addContent(instance.getTransformedCode(transformationContext));
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
                  ? variationnameTransformer.transformVariationName(func)
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
      Integer rawPriority=xForm.getVariation(i).getPriority();
      if(!res.contains(rawPriority)) {
        res.add(rawPriority);
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
    // apply post symmetry by using a final transform
    switch(flame.getPostSymmetryType()) {
      case POINT:
        {
          PostPointSymmetryWFFunc func = new PostPointSymmetryWFFunc();
          func.setParameter(PostPointSymmetryWFFunc.PARAM_CENTRE_X, flame.getPostSymmetryCentreX());
          func.setParameter(PostPointSymmetryWFFunc.PARAM_CENTRE_Y, flame.getPostSymmetryCentreY());
          func.setParameter(PostPointSymmetryWFFunc.PARAM_ORDER, flame.getPostSymmetryOrder());
          addPostSymmetryVariation(flame, func, 1.0);
        }
        break;
      case X_AXIS:
      case Y_AXIS:
        {
          PostAxisSymmetryWFFunc func = new PostAxisSymmetryWFFunc();
          func.setParameter(PostAxisSymmetryWFFunc.PARAM_AXIS, flame.getPostSymmetryType() == PostSymmetryType.X_AXIS ? 0 : 1);
          func.setParameter(PostAxisSymmetryWFFunc.PARAM_CENTRE_X, flame.getPostSymmetryCentreX());
          func.setParameter(PostAxisSymmetryWFFunc.PARAM_CENTRE_Y, flame.getPostSymmetryCentreY());
          func.setParameter(PostAxisSymmetryWFFunc.PARAM_ROTATION, flame.getPostSymmetryRotation());
          addPostSymmetryVariation(flame, func, flame.getPostSymmetryDistance());
        }
        break;
    }
    flame.setPostSymmetryType(PostSymmetryType.NONE);

    // split prepost-variations into 2 variations
    for(Layer layer: flame.getLayers()) {
      for (XForm xForm : layer.getXForms()) {
        transformPrePostVars(xForm);
      }
      for (XForm xForm : layer.getFinalXForms()) {
        transformPrePostVars(xForm);
      }
    }
    return flame;
  }

  private void addPostSymmetryVariation(Flame flame, VariationFunc func, double amount) {
    XForm xForm = new XForm();
    xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance("linear3D", true));
    xForm.addVariation(amount, func);
    flame.getFirstLayer().getFinalXForms().add(xForm);
  }

  private void transformPrePostVars(XForm xForm) {
    Set<Variation> prepostVars=new HashSet<>();
    int i=0;
    while(i<xForm.getVariationCount()) {
      Variation var = xForm.getVariation(i);
      if((var.getPriority()==-2 || var.getPriority()==2) && var.getFunc() instanceof PrePostVariation) {
        prepostVars.add(var);
        xForm.removeVariation(var);
      }
      else {
        i++;
      }
    }
    for(Variation var: prepostVars) {
      try {
        if (var.getPriority() == 2 || var.getPriority()==-2) {
          {
            Variation pre = var.makeCopy();
            PrePostGPUImplementation impl;
            if(var.getPriority() == 2) {
              impl = ((PrePostVariation)var.getFunc()).getPreFuncType().newInstance();
              impl.setReversed(false);
            }
            else {
              impl = ((PrePostVariation)var.getFunc()).getPostFuncType().newInstance();
              impl.setReversed(true);
            }

            VariationFunc func = (VariationFunc)impl;
            for(String param: func.getParameterNames()) {
              Object value = var.getFunc().getParameter(param);
              if (value != null) {
                if (value instanceof Integer) {
                  func.setParameter(param, (Integer)value);
                }
                else if (value instanceof Double) {
                  func.setParameter(param, (Double)value);
                }
              }
            }
            pre.setFunc(func);
            pre.setPriority(0);
            xForm.addVariation(pre);
          }
          {
            Variation post = var.makeCopy();
            PrePostGPUImplementation impl;
            if(var.getPriority() == 2) {
              impl = ((PrePostVariation)var.getFunc()).getPostFuncType().newInstance();
              impl.setReversed(false);
            }
            else {
              impl = ((PrePostVariation)var.getFunc()).getPreFuncType().newInstance();
              impl.setReversed(true);
            }
            VariationFunc func = (VariationFunc)impl;
            for(String param: func.getParameterNames()) {
              Object value = var.getFunc().getParameter(param);
              if (value != null) {
                if (value instanceof Integer) {
                  func.setParameter(param, (Integer)value);
                }
                else if (value instanceof Double) {
                  func.setParameter(param, (Double)value);
                }
              }
            }
            post.setFunc(func);
            post.setPriority(1);
            xForm.addVariation(post);
          }
        }
      }
      catch(Exception ex) {
        ex.printStackTrace();
      }
    }
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

  private FlameTransformationContext createTransformCtx(Flame transformedFlame) {
    FlameTransformationContext context = new FlameTransformationContext(null, null, 1, 1);
    context.setPreserveZCoordinate(transformedFlame.isPreserveZ());
    return context;
  }

}
