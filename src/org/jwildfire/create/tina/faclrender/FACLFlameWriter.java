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

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.PostSymmetryType;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.weightingfield.WeightingFieldType;
import org.jwildfire.create.tina.io.AbstractFlameWriter;
import org.jwildfire.create.tina.io.SimpleXMLBuilder;
import org.jwildfire.create.tina.io.SimpleXMLBuilder.Attribute;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.swing.MessageLogger;
import org.jwildfire.create.tina.variation.*;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.fabs;

public class FACLFlameWriter extends AbstractFlameWriter {
  private final MessageLogger messageLogger;

  public FACLFlameWriter() {
    this(null);
  }

  public FACLFlameWriter(MessageLogger messageLogger) {
    this.messageLogger = messageLogger;
  }

  public void writeFlame(List<Flame> pFlames, String pFilename) throws Exception {
    writeFlame(getFlameXML(pFlames), pFilename);
  }

  public void writeFlame(String flameXML, String pFilename) throws Exception {
    Tools.writeUTF8Textfile(pFilename,flameXML);
  }

  public String getFlameXML(List<Flame> pFlames) throws Exception {
    SimpleXMLBuilder xb = new SimpleXMLBuilder();

    List<SimpleXMLBuilder.Attribute<?>> flamesAttrList = new ArrayList<>();
    flamesAttrList.add(new Attribute<String>("name", ""));

    xb.beginElement("Flames", flamesAttrList);
    VariationSet variationSet = null;
    FlameTransformationContext transformationContext = null;
    for (Flame currFlame : pFlames) {
      Flame transformedFlame = transformFlame(currFlame);
      if (transformationContext == null) {
        transformationContext = createTransformCtx(transformedFlame);
      }
      // Flame
      List<SimpleXMLBuilder.Attribute<?>> attrList =
          filterFlameAttrList(createFlameAttributes(transformedFlame, xb));
      Layer layer = transformedFlame.getFirstLayer();
      if (variationSet == null) {
        variationSet = new VariationSet(transformedFlame, transformationContext, messageLogger);
      }
      attrList.add(new SimpleXMLBuilder.Attribute<String>("varset", variationSet.getUuid()));
      attrList.add(new SimpleXMLBuilder.Attribute<String>("highlight_power", "-1"));
      // HACK: misusing the zBufferScale-field because it will never be used by FACLRender, can later easily changed if necessary, by introducing a dedicated field
      attrList.add(new SimpleXMLBuilder.Attribute<Double>("intensity_adjust", transformedFlame.getZBufferScale()));
      xb.beginElement("flame", attrList);
      // XForm
      for (XForm xForm : layer.getXForms()) {
        xb.beginElement(
            "xform", addWFieldAttributes(filterXFormAttrList(createXFormAttrList(xb, layer, xForm, null, false)), xForm));
        for (int priority : extractVariationPriorities(xForm)) {
          xb.emptyElement(
              "variationGroup", createVariationGroupAttrList(xb, priority, xForm, variationSet));
        }
        xb.endElement("xform");
      }
      // FinalXForms
      for (XForm xForm : layer.getFinalXForms()) {
        xb.beginElement(
            "finalxform", addWFieldAttributes(filterXFormAttrList(createXFormAttrList(xb, layer, xForm, null, false)), xForm));
        for (int priority : extractVariationPriorities(xForm)) {
          xb.emptyElement(
              "variationGroup", createVariationGroupAttrList(xb, priority, xForm, variationSet));
        }
        xb.endElement("finalxform");
      }
      // Gradient
      addGradient(xb, layer);
      xb.endElement("flame");
    }
    // VariationSet
    if(variationSet!=null) {
      xb.beginElement("variationSet", createVariationSetAttrList(pFlames, xb, variationSet.getUuid()));
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
        // extra params
        {
          String params[] = ((SupportsGPU)func).getGPUExtraParameterNames();
          if (params != null) {
            for (int i = 0; i < params.length; i++) {
               attrList.add(xb.createAttr((transformedFuncName + "_" + params[i]/*+"-"+funcNameCounter*/), 0.0));
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

  private List<Attribute<?>> createVariationSetAttrList(List<Flame> pFlames, SimpleXMLBuilder xb, String varSetUuid) {
    ArrayList<Attribute<?>> res = new ArrayList<>();
    res.add(xb.createAttr("name", "JWFVarSet_"+varSetUuid));
    res.add(xb.createAttr("version", "1.0"));
    res.add(xb.createAttr("defaultVariation", "linear"));
    res.add(xb.createAttr("is3Dcapable", "yes"));
    res.add(xb.createAttr("uuid", varSetUuid));
    res.add(xb.createAttr("structName", "varpar"));
    res.add(xb.createAttr("globalDefinitions", buildGlobalDefinitions(pFlames)));
    return res;
  }

  private String buildGlobalDefinitions(List<Flame> pFlames) {
    Set<FACLRenderKernelSwitches> switches = new HashSet<>();
    for(Flame flame: pFlames) {
      if(fabs(flame.getCamDOF())>EPSILON) {
        switches.add(FACLRenderKernelSwitches.ADD_FEATURE_DOF);
      }
      for(Layer layer: flame.getLayers()) {
        for(List<XForm> xforms: Arrays.asList( layer.getXForms(), layer.getFinalXForms())) {
          for(XForm xform: xforms) {
            if(xform.getWeightingFieldType()!=null && xform.getWeightingFieldType()!=WeightingFieldType.NONE) {
              switches.add(FACLRenderKernelSwitches.ADD_FEATURE_WFIELDS);
              if(fabs(xform.getWeightingFieldJitterIntensity())>EPSILON) {
                switches.add(FACLRenderKernelSwitches.ADD_FEATURE_WFIELDS_JITTER);
              }
              switch(xform.getWeightingFieldType()) {
                case CELLULAR_NOISE: {
                  switches.add(FACLRenderKernelSwitches.ADD_FEATURE_WORLEY_NOISE);
                  break;
                }
                case CUBIC_NOISE:
                case CUBIC_FRACTAL_NOISE: {
                  switches.add(FACLRenderKernelSwitches.ADD_FEATURE_CUBIC_VALUE_NOISE);
                  break;
                }
                case PERLIN_NOISE:
                case PERLIN_FRACTAL_NOISE: {
                  switches.add(FACLRenderKernelSwitches.ADD_FEATURE_PERLIN_NOISE);
                  break;
                }
                case SIMPLEX_NOISE:
                case SIMPLEX_FRACTAL_NOISE: {
                  switches.add(FACLRenderKernelSwitches.ADD_FEATURE_SIMPLEX_NOISE);
                  break;
                }
                case VALUE_NOISE:
                case VALUE_FRACTAL_NOISE: {
                  switches.add(FACLRenderKernelSwitches.ADD_FEATURE_LINEAR_VALUE_NOISE);
                  break;
                }
                case IMAGE_MAP: {
                  switches.add(FACLRenderKernelSwitches.ADD_FEATURE_SPOTS_NOISE);
                  break;
                }
              }
            }
            // GPU-version of crackle uses simplexNoise from the library rather than its own, because this would be much slower
            for(int i=0;i<xform.getVariationCount();i++) {
              if(xform.getVariation(i).getFunc().getName().toLowerCase().contains("crackle")) {
                switches.add(FACLRenderKernelSwitches.ADD_FEATURE_SIMPLEX_NOISE);
              }
            }
          }
        }
      }
    }
    return switches.stream().map(sw -> "#define "+sw.toString()+"\n").collect(Collectors.joining());
  }

  // remove unnecessary xform-attributes
  private final static Set<String> XFORM_ATTR_BLACKLIST = new HashSet<>(Arrays.asList("color_type", "material", "material_speed", "mod_gamma", "mod_gamma_speed",
          "mod_contrast", "mod_contrast_speed", "mod_saturation", "mod_saturation_speed", "mod_saturation_speed",
          "mod_hue", "mod_hue_speed"));

  private List<Attribute<?>> filterXFormAttrList(List<Attribute<?>> pSrc) {
    return pSrc.stream().filter(a ->  !a.getName().endsWith("fx_priority") && !a.getName().startsWith("wfield_") &&  !a.getName().contains("Curve_") && !XFORM_ATTR_BLACKLIST.contains(a.getName())).collect(Collectors.toList());
  }

  List<Attribute<?>> addWFieldAttributes(List<Attribute<?>> pSrc, XForm pXForm) {
    if(pXForm.getWeightingFieldType()!= WeightingFieldType.NONE) {
      pSrc.add(new Attribute<Integer>("wfield_type", pXForm.getWeightingFieldType().ordinal()));
      pSrc.add(new Attribute<Integer>("wfield_input", pXForm.getWeightingFieldInput().ordinal()));
      pSrc.add(new Attribute<Double>("wfield_var_amount", pXForm.getWeightingFieldVarAmountIntensity()));
      String paramVarNames[]=new String[]{pXForm.getWeightingFieldVarParam1VarName(), pXForm.getWeightingFieldVarParam2VarName(), pXForm.getWeightingFieldVarParam3VarName()};
      String paramNames[]=new String[]{pXForm.getWeightingFieldVarParam1ParamName(), pXForm.getWeightingFieldVarParam2ParamName(), pXForm.getWeightingFieldVarParam3ParamName()};
      double paramAmountVales[] = new double[]{pXForm.getWeightingFieldVarParam1Intensity(), pXForm.getWeightingFieldVarParam2Intensity(), pXForm.getWeightingFieldVarParam3Intensity()};
      for(int i=0;i<paramNames.length;i++) {
        if(paramNames[i]!=null && paramNames[i].length()>0 && fabs(paramAmountVales[i])>EPSILON) {
            int varIdx = -1;
            int paramIdx = -1;
            for(int j=0;j<pXForm.getVariationCount();j++) {
              VariationFunc varFunc=pXForm.getVariation(j).getFunc();
              if(paramVarNames[i].equals(varFunc.getName())) {
                varIdx = j;
                for(int k=0;k<varFunc.getParameterNames().length;k++) {
                  if(paramNames[i].equals(varFunc.getParameterNames()[k])) {
                    paramIdx = k;
                    break;
                  }
                }
                break;
              }
            }
            if(varIdx>=0) {
              pSrc.add(new Attribute<Integer>("wfield_param"+(i+1)+"_var_idx", varIdx));
              // param_idx < 0 means amount (which is the intensity of the contributing variation)
              pSrc.add(new Attribute<Integer>("wfield_param"+(i+1)+"_param_idx", paramIdx));
              pSrc.add(new Attribute<Double>("wfield_param"+(i+1)+"_amount", paramAmountVales[i]));
            }
         }
      }
      pSrc.add(new Attribute<Double>("wfield_color_amount", pXForm.getWeightingFieldColorIntensity()));
      pSrc.add(new Attribute<Double>("wfield_jitter_amount", pXForm.getWeightingFieldJitterIntensity()));
      pSrc.add(new Attribute<Integer>("wfield_seed", pXForm.getWeightingFieldNoiseSeed()));
      pSrc.add(new Attribute<Integer>("wfield_octaves", pXForm.getWeightingFieldFractalNoiseOctaves()));
      pSrc.add(new Attribute<Double>("wfield_gain", pXForm.getWeightingFieldFractalNoiseGain()));
      pSrc.add(new Attribute<Double>("wfield_lacunarity", pXForm.getWeightingFieldFractalNoiseLacunarity()));
      pSrc.add(new Attribute<Double>("wfield_scale", pXForm.getWeightingFieldNoiseFrequency()));
    }
    return pSrc;
  }

  // remove unnecessary flame-attributes
  private final static Set<String> FLAME_ATTR_BLACKLIST = new HashSet<>(Arrays.asList("smooth_gradient", "version", "filter_type", "filter_indicator",
          "filter_sharpness", "filter_low_density", "ai_post_denoiser", "post_optix_denoiser_blend", "background_type",
          "background_ul", "background_ur", "background_ll", "background_lr", "background_cc", "fg_opacity",
          "post_blur_radius", "post_blur_fade", "post_blur_falloff", "mixer_mode", "frame", "frame_count",
          "fps", "zbuffer_scale", "zbuffer_bias", "zbuffer_filename", "low_density_brightness"));
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
            pre.setPriority(-1);
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
    FlameRenderer renderer = new FlameRenderer(transformedFlame, Prefs.getPrefs(), false, true);
    FlameTransformationContext context = new FlameTransformationContext(renderer, new MarsagliaRandomGenerator(), 1, 1);
    context.setPreserveZCoordinate(transformedFlame.isPreserveZ());
    return context;
  }

}
