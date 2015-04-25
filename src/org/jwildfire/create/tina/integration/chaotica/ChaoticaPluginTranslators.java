/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
package org.jwildfire.create.tina.integration.chaotica;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.variation.BCollideFunc;
import org.jwildfire.create.tina.variation.Blade3DFunc;
import org.jwildfire.create.tina.variation.Blob3DFunc;
import org.jwildfire.create.tina.variation.Blur3DFunc;
import org.jwildfire.create.tina.variation.BlurCircleFunc;
import org.jwildfire.create.tina.variation.Bubble2Func;
import org.jwildfire.create.tina.variation.Butterfly3DFunc;
import org.jwildfire.create.tina.variation.CannabisCurveWFFunc;
import org.jwildfire.create.tina.variation.CircleBlurFunc;
import org.jwildfire.create.tina.variation.CloverLeafWFFunc;
import org.jwildfire.create.tina.variation.CothFunc;
import org.jwildfire.create.tina.variation.CscFunc;
import org.jwildfire.create.tina.variation.CschFunc;
import org.jwildfire.create.tina.variation.Curl3DFunc;
import org.jwildfire.create.tina.variation.CylinderApoFunc;
import org.jwildfire.create.tina.variation.DCLinearFunc;
import org.jwildfire.create.tina.variation.DCZTranslFunc;
import org.jwildfire.create.tina.variation.Disc3DFunc;
import org.jwildfire.create.tina.variation.EpispiralFunc;
import org.jwildfire.create.tina.variation.EpispiralWFFunc;
import org.jwildfire.create.tina.variation.ExpFunc;
import org.jwildfire.create.tina.variation.Falloff2Func;
import org.jwildfire.create.tina.variation.Falloff3Func;
import org.jwildfire.create.tina.variation.Fan2Func;
import org.jwildfire.create.tina.variation.FanFunc;
import org.jwildfire.create.tina.variation.FlattenFunc;
import org.jwildfire.create.tina.variation.FlowerDbFunc;
import org.jwildfire.create.tina.variation.FourthFunc;
import org.jwildfire.create.tina.variation.FractDragonWFFunc;
import org.jwildfire.create.tina.variation.FractFormulaJuliaWFFunc;
import org.jwildfire.create.tina.variation.FractFormulaMandWFFunc;
import org.jwildfire.create.tina.variation.FractJuliaWFFunc;
import org.jwildfire.create.tina.variation.FractMandelbrotWFFunc;
import org.jwildfire.create.tina.variation.FractMeteorsWFFunc;
import org.jwildfire.create.tina.variation.FractPearlsWFFunc;
import org.jwildfire.create.tina.variation.FractSalamanderWFFunc;
import org.jwildfire.create.tina.variation.HOFunc;
import org.jwildfire.create.tina.variation.Hypertile3D1Func;
import org.jwildfire.create.tina.variation.Hypertile3D2Func;
import org.jwildfire.create.tina.variation.Hypertile3DFunc;
import org.jwildfire.create.tina.variation.InflateZ_1Func;
import org.jwildfire.create.tina.variation.InflateZ_2Func;
import org.jwildfire.create.tina.variation.InflateZ_3Func;
import org.jwildfire.create.tina.variation.InflateZ_4Func;
import org.jwildfire.create.tina.variation.InflateZ_5Func;
import org.jwildfire.create.tina.variation.InflateZ_6Func;
import org.jwildfire.create.tina.variation.Julia3DFunc;
import org.jwildfire.create.tina.variation.Julia3DQFunc;
import org.jwildfire.create.tina.variation.Julia3DZFunc;
import org.jwildfire.create.tina.variation.JuliaCFunc;
import org.jwildfire.create.tina.variation.JuliaN3DXFunc;
import org.jwildfire.create.tina.variation.KaleidoscopeFunc;
import org.jwildfire.create.tina.variation.LinearT3DFunc;
import org.jwildfire.create.tina.variation.LinearTFunc;
import org.jwildfire.create.tina.variation.LogApoFunc;
import org.jwildfire.create.tina.variation.LogFunc;
import org.jwildfire.create.tina.variation.MCarpetFunc;
import org.jwildfire.create.tina.variation.Ovoid3DFunc;
import org.jwildfire.create.tina.variation.PostDCZTranslFunc;
import org.jwildfire.create.tina.variation.PostMirrorWFFunc;
import org.jwildfire.create.tina.variation.SVFFunc;
import org.jwildfire.create.tina.variation.SinhFunc;
import org.jwildfire.create.tina.variation.Spherical3DFunc;
import org.jwildfire.create.tina.variation.Spherical3DWFFunc;
import org.jwildfire.create.tina.variation.Square3DFunc;
import org.jwildfire.create.tina.variation.StarBlurFunc;
import org.jwildfire.create.tina.variation.SubFlameWFFunc;
import org.jwildfire.create.tina.variation.TanCosFunc;
import org.jwildfire.create.tina.variation.TanhFunc;
import org.jwildfire.create.tina.variation.TargetFunc;
import org.jwildfire.create.tina.variation.TargetSpFunc;
import org.jwildfire.create.tina.variation.TextWFFunc;
import org.jwildfire.create.tina.variation.Waves2BFunc;
import org.jwildfire.create.tina.variation.Waves2WFFunc;
import org.jwildfire.create.tina.variation.Waves3WFFunc;
import org.jwildfire.create.tina.variation.Waves4WFFunc;
import org.jwildfire.create.tina.variation.WedgeSphFunc;
import org.jwildfire.create.tina.variation.ZConeFunc;
import org.jwildfire.create.tina.variation.ZScaleFunc;
import org.jwildfire.create.tina.variation.ZTranslateFunc;
import org.jwildfire.create.tina.variation.iflames.IFlamesFunc;

public class ChaoticaPluginTranslators {

  private static Map<String, ChaoticaPluginTranslator> translators = new HashMap<String, ChaoticaPluginTranslator>();

  static {
    EmptyChaoticaPluginTranslator emptyTranslator = new EmptyChaoticaPluginTranslator();
    ChaoticaPluginTranslator mandelbrotTranslator = new ChaoticaPluginTranslator("dc_mandelbrot",
        fixedValue("dcm_iter", 25.0), fixedValue("dcm_miniter", 1.0), fixedValue("dcm_smooth_iter", 0.0), fixedValue("dcm_retries", 50.0),
        fixedValue("dcm_mode", 0.0), fixedValue("dcm_pow", 2.0), fixedValue("dcm_color_method", 0.0), fixedValue("dcm_invert", 0.0),
        fixedValue("dcm_xmin", -2.0), fixedValue("dcm_xmax", 2.0), fixedValue("dcm_ymin", -1.5), fixedValue("dcm_ymax", 1.5),
        fixedValue("dcm_scatter", 0.0), fixedValue("dcm_sx", 0.0), fixedValue("dcm_sy", 0.0), fixedValue("dcm_zscale", 0.0));

    translators.put(new BCollideFunc().getName(), new ChaoticaPluginTranslator("linear"));
    translators.put(new Blade3DFunc().getName(), new ChaoticaPluginTranslator("blade"));
    translators.put(new Blob3DFunc().getName(), new ChaoticaPluginTranslator("blob"));
    translators.put(new Blur3DFunc().getName(), new ChaoticaPluginTranslator("gaussian_blur"));
    translators.put(new BlurCircleFunc().getName(), new ChaoticaPluginTranslator("blur"));
    translators.put(new Bubble2Func().getName(), new ChaoticaPluginTranslator("bubble"));
    translators.put(new Butterfly3DFunc().getName(), new ChaoticaPluginTranslator("butterfly"));
    translators.put(new CannabisCurveWFFunc().getName(), new ChaoticaPluginTranslator("flower", fixedValue("holes", 0.4), fixedValue("petals", 7.0)));
    translators.put(new CircleBlurFunc().getName(), new ChaoticaPluginTranslator("disc"));
    translators.put(new CloverLeafWFFunc().getName(), new ChaoticaPluginTranslator("flower", fixedValue("holes", 0.4), fixedValue("petals", 7.0)));
    translators.put(new CothFunc().getName(), new ChaoticaPluginTranslator("spherical"));
    translators.put(new CscFunc().getName(), new ChaoticaPluginTranslator("spherical", name("cx"), name("cy")));
    translators.put(new CschFunc().getName(), new ChaoticaPluginTranslator("cpow", fixedValue("r", 1.0), fixedValue("i", 0.1), fixedValue("power", 1.5)));
    translators.put(new Curl3DFunc().getName(), new ChaoticaPluginTranslator("curl", name("cx"), name("cy")));
    translators.put(new CylinderApoFunc().getName(), new ChaoticaPluginTranslator("cylinder"));
    translators.put(new DCLinearFunc().getName(), new ChaoticaPluginTranslator("linear"));
    translators.put(new DCZTranslFunc().getName(), emptyTranslator);
    translators.put(new Disc3DFunc().getName(), new ChaoticaPluginTranslator("disc"));
    translators.put(new EpispiralWFFunc().getName(), new ChaoticaPluginTranslator("Epispiral", namePair("waves", "n"), fixedValue("thickness", 0.0), fixedValue("holes", 1.0)));
    translators.put(new EpispiralFunc().getName(), new ChaoticaPluginTranslator("Epispiral", namePair("waves", "n"), fixedValue("thickness", 0.0), fixedValue("holes", 1.0)));
    translators.put(new EpispiralWFFunc().getName(), new ChaoticaPluginTranslator("cross"));
    translators.put(new ExpFunc().getName(), new ChaoticaPluginTranslator("exponential"));
    translators.put(new Falloff2Func().getName(), new ChaoticaPluginTranslator("linear"));
    translators.put(new Falloff3Func().getName(), new ChaoticaPluginTranslator("linear"));
    translators.put(new FanFunc().getName(), new ChaoticaPluginTranslator("waves2", fixedValue("scalex", 0.25), fixedValue("freqx", MathLib.M_PI / 4.0), fixedValue("scaley", 0.25), fixedValue("freqy", MathLib.M_PI / 4.0)));
    translators.put(new Fan2Func().getName(), new ChaoticaPluginTranslator("waves2", fixedValue("scalex", 0.25), fixedValue("freqx", MathLib.M_PI / 4.0), fixedValue("scaley", 0.25), fixedValue("freqy", MathLib.M_PI / 4.0)));
    translators.put(new FlattenFunc().getName(), new ChaoticaPluginTranslator("linear"));
    translators.put(new FlowerDbFunc().getName(), new ChaoticaPluginTranslator("flower", name("petals"), fixedValue("holes", 0.4)));
    translators.put(new FourthFunc().getName(), new ChaoticaPluginTranslator("linear"));
    translators.put(new FractDragonWFFunc().getName(), mandelbrotTranslator);
    translators.put(new FractFormulaJuliaWFFunc().getName(), mandelbrotTranslator);
    translators.put(new FractFormulaMandWFFunc().getName(), mandelbrotTranslator);
    translators.put(new FractJuliaWFFunc().getName(), mandelbrotTranslator);
    translators.put(new FractMandelbrotWFFunc().getName(), mandelbrotTranslator);
    translators.put(new FractMeteorsWFFunc().getName(), mandelbrotTranslator);
    translators.put(new FractPearlsWFFunc().getName(), mandelbrotTranslator);
    translators.put(new FractSalamanderWFFunc().getName(), mandelbrotTranslator);
    translators.put(new HOFunc().getName(), new ChaoticaPluginTranslator("hypertile"));
    translators.put(new Hypertile3DFunc().getName(), new ChaoticaPluginTranslator("hypertile", name("p"), name("q"), name("n")));
    translators.put(new Hypertile3D1Func().getName(), new ChaoticaPluginTranslator("hypertile1", name("p"), name("q")));
    translators.put(new Hypertile3D2Func().getName(), new ChaoticaPluginTranslator("hypertile1", name("p"), name("q")));// hypertile1 is not a bug here
    translators.put(new IFlamesFunc().getName(), emptyTranslator);
    translators.put(new InflateZ_1Func().getName(), emptyTranslator);
    translators.put(new InflateZ_2Func().getName(), emptyTranslator);
    translators.put(new InflateZ_3Func().getName(), emptyTranslator);
    translators.put(new InflateZ_4Func().getName(), emptyTranslator);
    translators.put(new InflateZ_5Func().getName(), emptyTranslator);
    translators.put(new InflateZ_6Func().getName(), emptyTranslator);
    translators.put(new Julia3DFunc().getName(), new ChaoticaPluginTranslator("julian", name("power"), fixedValue("dist", 1.0)));
    translators.put(new Julia3DZFunc().getName(), new ChaoticaPluginTranslator("julian", name("power"), fixedValue("dist", 1.0)));
    translators.put(new Julia3DQFunc().getName(), new ChaoticaPluginTranslator("julian", name("power"), fixedValue("dist", 1.0)));
    translators.put(new JuliaCFunc().getName(), new ChaoticaPluginTranslator("julia"));
    translators.put(new JuliaN3DXFunc().getName(), new ChaoticaPluginTranslator("julian", name("power"), name("dist")));
    translators.put(new MCarpetFunc().getName(), new ChaoticaPluginTranslator("handkerchief"));
    translators.put(new PostDCZTranslFunc().getName(), emptyTranslator);
    translators.put(new PostMirrorWFFunc().getName(), emptyTranslator);
    translators.put(new KaleidoscopeFunc().getName(), new ChaoticaPluginTranslator("linear"));
    translators.put(new LinearT3DFunc().getName(), new ChaoticaPluginTranslator("linear"));
    translators.put(new LinearTFunc().getName(), new ChaoticaPluginTranslator("linear"));
    translators.put(new LogApoFunc().getName(), new ChaoticaPluginTranslator("log", name("log_base")));
    translators.put(new LogFunc().getName(), new ChaoticaPluginTranslator("log", fixedValue("log_base", 2.718281828459)));
    translators.put(new Ovoid3DFunc().getName(), new ChaoticaPluginTranslator("spherical"));
    translators.put(new SinhFunc().getName(), new ChaoticaPluginTranslator("cos"));
    translators.put(new Spherical3DFunc().getName(), new ChaoticaPluginTranslator("spherical"));
    translators.put(new Spherical3DWFFunc().getName(), new ChaoticaPluginTranslator("spherical"));
    translators.put(new Square3DFunc().getName(), new ChaoticaPluginTranslator("square"));
    translators.put(new StarBlurFunc().getName(), new ChaoticaPluginTranslator("square"));
    translators.put(new SubFlameWFFunc().getName(), new ChaoticaPluginTranslator("blur"));
    translators.put(new SVFFunc().getName(), new ChaoticaPluginTranslator("bubble"));
    translators.put(new TanCosFunc().getName(), new ChaoticaPluginTranslator("ex"));
    translators.put(new TanhFunc().getName(), new ChaoticaPluginTranslator("cpow", fixedValue("r", 1.0), fixedValue("i", 0.1), fixedValue("power", 1.5)));
    translators.put(new TargetFunc().getName(), new ChaoticaPluginTranslator("disc"));
    translators.put(new TargetSpFunc().getName(), new ChaoticaPluginTranslator("disc"));
    translators.put(new TextWFFunc().getName(), new ChaoticaPluginTranslator("bubble"));
    translators.put(new Waves2WFFunc().getName(), new ChaoticaPluginTranslator("waves2", name("scalex"), name("freqx"), name("scaley"), name("freqy")));
    translators.put(new Waves3WFFunc().getName(), new ChaoticaPluginTranslator("waves2", name("scalex"), name("freqx"), name("scaley"), name("freqy")));
    translators.put(new Waves4WFFunc().getName(), new ChaoticaPluginTranslator("waves2", name("scalex"), name("freqx"), name("scaley"), name("freqy")));
    translators.put(new Waves2BFunc().getName(), new ChaoticaPluginTranslator("waves2", name("scalex"), name("freqx"), name("scaley"), name("freqy")));
    translators.put(new WedgeSphFunc().getName(), new ChaoticaPluginTranslator("wedge", name("angle"), name("hole"), name("count"), name("swirl")));
    translators.put(new ZConeFunc().getName(), emptyTranslator);
    translators.put(new ZScaleFunc().getName(), emptyTranslator);
    translators.put(new ZTranslateFunc().getName(), emptyTranslator);
  }

  private static NamePair name(String pFromTo) {
    return new NamePair(pFromTo, pFromTo);
  }

  private static NamePair namePair(String pFrom, String pTo) {
    return new NamePair(pFrom, pTo);
  }

  private static FixedValue fixedValue(String pName, Double pValue) {
    return new FixedValue(pName, pValue);
  }

  public String translateVarName(String varName) {
    ChaoticaPluginTranslator translator = translators.get(varName);
    return translator != null ? translator.getTranslatedName() : varName;
  }

  public String translatePropertyName(String varName, String propertyName) {
    ChaoticaPluginTranslator translator = translators.get(varName);
    if (translator != null) {
      return translator.translatePropertyName(propertyName);
    }
    else {
      return varName + "_" + propertyName;
    }
  }

  public List<String> getFixedValueNames(String pVarName) {
    ChaoticaPluginTranslator translator = translators.get(pVarName);
    if (translator != null) {
      return translator.getFixedValueNames();
    }
    else {
      return Collections.emptyList();
    }
  }

  public Double getFixedValue(String pVarName, String pName) {
    return translators.get(pVarName).getFixedValue(pName);
  }

}
