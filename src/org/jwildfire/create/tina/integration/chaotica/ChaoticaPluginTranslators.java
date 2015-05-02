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

import org.jwildfire.create.tina.variation.Blade3DFunc;
import org.jwildfire.create.tina.variation.Blob3DFunc;
import org.jwildfire.create.tina.variation.BubbleWFFunc;
import org.jwildfire.create.tina.variation.Butterfly3DFunc;
import org.jwildfire.create.tina.variation.ColorMapWFFunc;
import org.jwildfire.create.tina.variation.ConicFunc;
import org.jwildfire.create.tina.variation.CustomWFFunc;
import org.jwildfire.create.tina.variation.CylinderApoFunc;
import org.jwildfire.create.tina.variation.DCBubbleFunc;
import org.jwildfire.create.tina.variation.DCCrackleWFFunc;
import org.jwildfire.create.tina.variation.DCHexesWFFunc;
import org.jwildfire.create.tina.variation.DCLinearFunc;
import org.jwildfire.create.tina.variation.DCZTranslFunc;
import org.jwildfire.create.tina.variation.EpispiralFunc;
import org.jwildfire.create.tina.variation.ExtrudeFunc;
import org.jwildfire.create.tina.variation.FlattenFunc;
import org.jwildfire.create.tina.variation.InflateZ_1Func;
import org.jwildfire.create.tina.variation.InflateZ_2Func;
import org.jwildfire.create.tina.variation.InflateZ_3Func;
import org.jwildfire.create.tina.variation.InflateZ_4Func;
import org.jwildfire.create.tina.variation.InflateZ_5Func;
import org.jwildfire.create.tina.variation.InflateZ_6Func;
import org.jwildfire.create.tina.variation.LogApoFunc;
import org.jwildfire.create.tina.variation.LogFunc;
import org.jwildfire.create.tina.variation.PostColorMapWFFunc;
import org.jwildfire.create.tina.variation.PostCustomWFFunc;
import org.jwildfire.create.tina.variation.PostDCZTranslFunc;
import org.jwildfire.create.tina.variation.PostDepthFunc;
import org.jwildfire.create.tina.variation.PreCustomWFFunc;
import org.jwildfire.create.tina.variation.PreDCZTranslFunc;
import org.jwildfire.create.tina.variation.Spherical3DFunc;
import org.jwildfire.create.tina.variation.Splits3DFunc;
import org.jwildfire.create.tina.variation.Square3DFunc;
import org.jwildfire.create.tina.variation.WedgeFunc;
import org.jwildfire.create.tina.variation.ZBlurFunc;
import org.jwildfire.create.tina.variation.ZConeFunc;
import org.jwildfire.create.tina.variation.ZScaleFunc;
import org.jwildfire.create.tina.variation.ZTranslateFunc;
import org.jwildfire.create.tina.variation.iflames.IFlamesFunc;

public class ChaoticaPluginTranslators {

  private static Map<String, ChaoticaPluginTranslator> translators = new HashMap<String, ChaoticaPluginTranslator>();

  static {
    EmptyChaoticaPluginTranslator emptyTranslator = new EmptyChaoticaPluginTranslator();
    translators.put(new Blade3DFunc().getName(), new ChaoticaPluginTranslator("blade"));
    translators.put(new Blob3DFunc().getName(), new ChaoticaPluginTranslator("blob"));
    translators.put(new BubbleWFFunc().getName(), new ChaoticaPluginTranslator("bubble"));
    translators.put(new Butterfly3DFunc().getName(), new ChaoticaPluginTranslator("butterfly"));
    translators.put(new ColorMapWFFunc().getName(), emptyTranslator);
    translators.put(new ConicFunc().getName(), new ChaoticaPluginTranslator("conic_wf", name("eccentricity"), name("holes")));
    translators.put(new CustomWFFunc().getName(), emptyTranslator);
    translators.put(new CylinderApoFunc().getName(), new ChaoticaPluginTranslator("cylinder"));
    translators.put(new DCBubbleFunc().getName(), emptyTranslator);
    translators.put(new DCCrackleWFFunc().getName(), new ChaoticaPluginTranslator("crackle", name("cellsize"), name("power"), name("distort"), name("scale"), name("z")));
    translators.put(new DCLinearFunc().getName(), new ChaoticaPluginTranslator("linear"));
    translators.put(new DCHexesWFFunc().getName(), new ChaoticaPluginTranslator("hexes", name("cellsize"), name("power"), name("rotate"), name("scale")));
    translators.put(new DCZTranslFunc().getName(), new ChaoticaPluginTranslator("linear3D"));
    translators.put(new EpispiralFunc().getName(), new ChaoticaPluginTranslator("Epispiral", name("n"), name("thickness"), name("holes")));
    translators.put(new ExtrudeFunc().getName(), emptyTranslator);
    translators.put(new FlattenFunc().getName(), new ChaoticaPluginTranslator("linear"));
    translators.put(new IFlamesFunc().getName(), emptyTranslator);
    translators.put(new InflateZ_1Func().getName(), emptyTranslator);
    translators.put(new InflateZ_2Func().getName(), emptyTranslator);
    translators.put(new InflateZ_3Func().getName(), emptyTranslator);
    translators.put(new InflateZ_4Func().getName(), emptyTranslator);
    translators.put(new InflateZ_5Func().getName(), emptyTranslator);
    translators.put(new InflateZ_6Func().getName(), emptyTranslator);
    translators.put(new LogApoFunc().getName(), new ChaoticaPluginTranslator("log", name("base")));
    translators.put(new LogFunc().getName(), new ChaoticaPluginTranslator("log", fixedValue("base", 2.71828182845905)));
    translators.put(new PostColorMapWFFunc().getName(), emptyTranslator);
    translators.put(new PostCustomWFFunc().getName(), emptyTranslator);
    translators.put(new PostDepthFunc().getName(), new ChaoticaPluginTranslator("linear3D"));
    translators.put(new PostDCZTranslFunc().getName(), new ChaoticaPluginTranslator("linear3D"));
    translators.put(new PreCustomWFFunc().getName(), emptyTranslator);
    translators.put(new PreDCZTranslFunc().getName(), new ChaoticaPluginTranslator("linear3D"));
    translators.put(new Spherical3DFunc().getName(), new ChaoticaPluginTranslator("spherical"));
    translators.put(new Splits3DFunc().getName(), new ChaoticaPluginTranslator("splits", name("x"), name("y")));
    translators.put(new Square3DFunc().getName(), new ChaoticaPluginTranslator("square"));
    translators.put(new ZBlurFunc().getName(), emptyTranslator);
    translators.put(new ZConeFunc().getName(), emptyTranslator);
    translators.put(new ZScaleFunc().getName(), emptyTranslator);
    translators.put(new ZTranslateFunc().getName(), emptyTranslator);
    translators.put(new WedgeFunc().getName(), new ChaoticaPluginTranslator("wedge_wf", name("angle"), name("hole"), name("count"), name("swirl")));
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
