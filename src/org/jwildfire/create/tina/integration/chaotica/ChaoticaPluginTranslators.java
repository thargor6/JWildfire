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

import org.jwildfire.create.tina.variation.Bubble2Func;
import org.jwildfire.create.tina.variation.CannabisCurveWFFunc;
import org.jwildfire.create.tina.variation.CloverLeafWFFunc;
import org.jwildfire.create.tina.variation.Curl3DFunc;
import org.jwildfire.create.tina.variation.CylinderApoFunc;
import org.jwildfire.create.tina.variation.Disc3DFunc;
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
import org.jwildfire.create.tina.variation.InflateZ_1Func;
import org.jwildfire.create.tina.variation.InflateZ_2Func;
import org.jwildfire.create.tina.variation.InflateZ_3Func;
import org.jwildfire.create.tina.variation.InflateZ_4Func;
import org.jwildfire.create.tina.variation.InflateZ_5Func;
import org.jwildfire.create.tina.variation.InflateZ_6Func;
import org.jwildfire.create.tina.variation.Julia3DFunc;
import org.jwildfire.create.tina.variation.Julia3DQFunc;
import org.jwildfire.create.tina.variation.Julia3DZFunc;
import org.jwildfire.create.tina.variation.JuliaN3DXFunc;
import org.jwildfire.create.tina.variation.KaleidoscopeFunc;
import org.jwildfire.create.tina.variation.Spherical3DWFFunc;
import org.jwildfire.create.tina.variation.Square3DFunc;
import org.jwildfire.create.tina.variation.SubFlameWFFunc;
import org.jwildfire.create.tina.variation.Waves2BFunc;
import org.jwildfire.create.tina.variation.Waves2WFFunc;
import org.jwildfire.create.tina.variation.Waves3WFFunc;
import org.jwildfire.create.tina.variation.Waves4WFFunc;
import org.jwildfire.create.tina.variation.WedgeSphFunc;
import org.jwildfire.create.tina.variation.ZConeFunc;
import org.jwildfire.create.tina.variation.ZTranslateFunc;

public class ChaoticaPluginTranslators {

  private static Map<String, ChaoticaPluginTranslator> translators = new HashMap<String, ChaoticaPluginTranslator>();

  static {
    EmptyChaoticaPluginTranslator emptyTranslator = new EmptyChaoticaPluginTranslator();
    translators.put(new Bubble2Func().getName(), new ChaoticaPluginTranslator("bubble"));
    translators.put(new CannabisCurveWFFunc().getName(), new ChaoticaPluginTranslator("flower", fixedValue("holes", 0.4), fixedValue("petals", 7.0)));
    translators.put(new CloverLeafWFFunc().getName(), new ChaoticaPluginTranslator("flower", fixedValue("holes", 0.4), fixedValue("petals", 7.0)));
    translators.put(new Curl3DFunc().getName(), new ChaoticaPluginTranslator("curl", name("cx"), name("cy")));
    translators.put(new CylinderApoFunc().getName(), new ChaoticaPluginTranslator("cylinder"));
    translators.put(new Disc3DFunc().getName(), new ChaoticaPluginTranslator("disc"));
    translators.put(new FourthFunc().getName(), new ChaoticaPluginTranslator("linear"));
    translators.put(new FractDragonWFFunc().getName(), new ChaoticaPluginTranslator("square"));
    translators.put(new FractFormulaJuliaWFFunc().getName(), new ChaoticaPluginTranslator("square"));
    translators.put(new FractFormulaMandWFFunc().getName(), new ChaoticaPluginTranslator("square"));
    translators.put(new FractJuliaWFFunc().getName(), new ChaoticaPluginTranslator("square"));
    translators.put(new FractMandelbrotWFFunc().getName(), new ChaoticaPluginTranslator("square"));
    translators.put(new FractMeteorsWFFunc().getName(), new ChaoticaPluginTranslator("square"));
    translators.put(new FractPearlsWFFunc().getName(), new ChaoticaPluginTranslator("square"));
    translators.put(new FractSalamanderWFFunc().getName(), new ChaoticaPluginTranslator("square"));
    translators.put(new HOFunc().getName(), new ChaoticaPluginTranslator("hypertile"));
    translators.put(new InflateZ_1Func().getName(), emptyTranslator);
    translators.put(new InflateZ_2Func().getName(), emptyTranslator);
    translators.put(new InflateZ_3Func().getName(), emptyTranslator);
    translators.put(new InflateZ_4Func().getName(), emptyTranslator);
    translators.put(new InflateZ_5Func().getName(), emptyTranslator);
    translators.put(new InflateZ_6Func().getName(), emptyTranslator);
    translators.put(new Julia3DFunc().getName(), new ChaoticaPluginTranslator("julian", name("power"), fixedValue("dist", 1.0)));
    translators.put(new Julia3DZFunc().getName(), new ChaoticaPluginTranslator("julian", name("power"), fixedValue("dist", 1.0)));
    translators.put(new Julia3DQFunc().getName(), new ChaoticaPluginTranslator("julian", name("power"), fixedValue("dist", 1.0)));
    translators.put(new JuliaN3DXFunc().getName(), new ChaoticaPluginTranslator("julian", name("power"), name("dist")));
    translators.put(new KaleidoscopeFunc().getName(), new ChaoticaPluginTranslator("linear"));
    translators.put(new Spherical3DWFFunc().getName(), new ChaoticaPluginTranslator("spherical3D"));
    translators.put(new Square3DFunc().getName(), new ChaoticaPluginTranslator("square"));
    translators.put(new SubFlameWFFunc().getName(), new ChaoticaPluginTranslator("blur"));
    translators.put(new Waves2WFFunc().getName(), new ChaoticaPluginTranslator("waves2", name("scalex"), name("freqx"), name("scaley"), name("freqy")));
    translators.put(new Waves3WFFunc().getName(), new ChaoticaPluginTranslator("waves2", name("scalex"), name("freqx"), name("scaley"), name("freqy")));
    translators.put(new Waves4WFFunc().getName(), new ChaoticaPluginTranslator("waves2", name("scalex"), name("freqx"), name("scaley"), name("freqy")));
    translators.put(new Waves2BFunc().getName(), new ChaoticaPluginTranslator("waves2", name("scalex"), name("freqx"), name("scaley"), name("freqy")));
    translators.put(new WedgeSphFunc().getName(), new ChaoticaPluginTranslator("wedge", name("angle"), name("hole"), name("count"), name("swirl")));
    translators.put(new ZConeFunc().getName(), emptyTranslator);
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
