/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2019 Andreas Maschke

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
package org.jwildfire.create.tina.randomflame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jwildfire.base.Unchecker;

public class RandomFlameGeneratorList {
  public static final String DEFAULT_GENERATOR_NAME = new AllRandomFlameGenerator().getName();
  private static List<Class<? extends RandomFlameGenerator>> items = new ArrayList<Class<? extends RandomFlameGenerator>>();
  private static final List<String> nameList;

  static {
    registerRandomFlameGenerator(AllRandomFlameGenerator.class);
    registerRandomFlameGenerator(BlackAndWhiteRandomFlameGenerator.class);
    registerRandomFlameGenerator(BokehRandomFlameGenerator.class);
    registerRandomFlameGenerator(BrokatRandomFlameGenerator.class);
    registerRandomFlameGenerator(Brokat3DRandomFlameGenerator.class);
    registerRandomFlameGenerator(BubblesRandomFlameGenerator.class);
    registerRandomFlameGenerator(Bubbles3DRandomFlameGenerator.class);
    registerRandomFlameGenerator(CrossRandomFlameGenerator.class);
    registerRandomFlameGenerator(ExperimentalBubbles3DRandomFlameGenerator.class);
    registerRandomFlameGenerator(DualityRandomFlameGenerator.class);
    registerRandomFlameGenerator(DuckiesRandomFlameGenerator.class);
    registerRandomFlameGenerator(GalaxiesRandomFlameGenerator.class);
    registerRandomFlameGenerator(GnarlRandomFlameGenerator.class);
    registerRandomFlameGenerator(Gnarl3DRandomFlameGenerator.class);
    registerRandomFlameGenerator(EDiscRandomFlameGenerator.class);
    registerRandomFlameGenerator(ExperimentalGnarlRandomFlameGenerator.class);
    registerRandomFlameGenerator(Flowers3DRandomFlameGenerator.class);
    registerRandomFlameGenerator(FilledFlowers3DRandomFlameGenerator.class);
    registerRandomFlameGenerator(GhostsRandomFlameGenerator.class);
    registerRandomFlameGenerator(OrchidsRandomFlameGenerator.class);
    registerRandomFlameGenerator(OutlinesRandomFlameGenerator.class);
    registerRandomFlameGenerator(RasterRandomFlameGenerator.class);
    registerRandomFlameGenerator(SpiralsRandomFlameGenerator.class);
    registerRandomFlameGenerator(Spirals3DRandomFlameGenerator.class);
    registerRandomFlameGenerator(JulianDiscRandomFlameGenerator.class);
    registerRandomFlameGenerator(JuliansRandomFlameGenerator.class);
    registerRandomFlameGenerator(JulianRingsRandomFlameGenerator.class);
    registerRandomFlameGenerator(LayersRandomFlameGenerator.class);
    registerRandomFlameGenerator(MachineRandomFlameGenerator.class);
    registerRandomFlameGenerator(MandelbrotRandomFlameGenerator.class);
    registerRandomFlameGenerator(PhoenixRandomFlameGenerator.class);
    registerRandomFlameGenerator(RaysRandomFlameGenerator.class);
    registerRandomFlameGenerator(SimpleRandomFlameGenerator.class);
    registerRandomFlameGenerator(ExperimentalSimpleRandomFlameGenerator.class);
    registerRandomFlameGenerator(Affine3DRandomFlameGenerator.class);
    registerRandomFlameGenerator(LinearRandomFlameGenerator.class);
    registerRandomFlameGenerator(SierpinskyRandomFlameGenerator.class);
    registerRandomFlameGenerator(SimpleTilingRandomFlameGenerator.class);
    registerRandomFlameGenerator(SolidExperimentalRandomFlameGenerator.class);
    registerRandomFlameGenerator(SolidStunningRandomFlameGenerator.class);
    registerRandomFlameGenerator(SolidLabyrinthRandomFlameGenerator.class);
    registerRandomFlameGenerator(SolidJulia3DRandomFlameGenerator.class);
    registerRandomFlameGenerator(SolidShadowsRandomFlameGenerator.class);
    registerRandomFlameGenerator(SphericalRandomFlameGenerator.class);
    registerRandomFlameGenerator(Spherical3DRandomFlameGenerator.class);
    registerRandomFlameGenerator(SplitsRandomFlameGenerator.class);
    registerRandomFlameGenerator(SubFlameRandomFlameGenerator.class);
    registerRandomFlameGenerator(SynthRandomFlameGenerator.class);
    registerRandomFlameGenerator(TentacleRandomFlameGenerator.class);
    registerRandomFlameGenerator(TileBallRandomFlameGenerator.class);
    registerRandomFlameGenerator(ColorMapRandomFlameGenerator.class);
    registerRandomFlameGenerator(XenomorphRandomFlameGenerator.class);
    Collections.sort(items, new Comparator<Class<? extends RandomFlameGenerator>>() {

      @Override
      public int compare(Class<? extends RandomFlameGenerator> o1, Class<? extends RandomFlameGenerator> o2) {
        try {
          return o1.newInstance().getName().compareTo(o2.newInstance().getName());
        }
        catch (Exception ex) {
          Unchecker.rethrow(ex);
          return 0;
        }
      }

    });

    nameList = new ArrayList<>();
    for (Class<? extends RandomFlameGenerator> funcCls : items) {
      try {
        nameList.add(funcCls.newInstance().getName());
      }
      catch (InstantiationException e) {
        e.printStackTrace();
      }
      catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }

  private static void registerRandomFlameGenerator(Class<? extends RandomFlameGenerator> pRandomFlameGenerator) {
    items.add(pRandomFlameGenerator);
  }

  public static List<String> getNameList() {
    return nameList;
  }

  public static RandomFlameGenerator getRandomFlameGeneratorInstance(String pName) {
    return getRandomFlameGeneratorInstance(pName, false);
  }

  public static Class<? extends RandomFlameGenerator> getGeneratorClassByName(String pName) {
    int idx = getNameList().indexOf(pName);
    return idx >= 0 ? items.get(idx) : null;
  }

  public static RandomFlameGenerator getRandomFlameGeneratorInstance(String pName, boolean pFatal) {
    Class<? extends RandomFlameGenerator> funcCls = getGeneratorClassByName(pName);
    if (funcCls != null) {
      try {
        RandomFlameGenerator func = funcCls.newInstance();
        return func;
      }
      catch (Throwable e) {
        e.printStackTrace();
      }
    }
    if (pFatal) {
      throw new IllegalArgumentException(pName);
    }
    return null;
  }

}
