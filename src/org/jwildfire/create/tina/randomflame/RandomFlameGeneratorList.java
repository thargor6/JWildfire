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
package org.jwildfire.create.tina.randomflame;

import java.util.ArrayList;
import java.util.List;

public class RandomFlameGeneratorList {
  public static final String DEFAULT_GENERATOR_NAME = "All";
  private static List<Class<? extends RandomFlameGenerator>> items = new ArrayList<Class<? extends RandomFlameGenerator>>();
  private static List<String> nameList = new ArrayList<String>();

  static {
    registerRandomFlameGenerator(AllRandomFlameGenerator.class);
    registerRandomFlameGenerator(BrokatRandomFlameGenerator.class);
    registerRandomFlameGenerator(Brokat3DRandomFlameGenerator.class);
    registerRandomFlameGenerator(BubblesRandomFlameGenerator.class);
    registerRandomFlameGenerator(Bubbles3DRandomFlameGenerator.class);
    registerRandomFlameGenerator(ExperimentalBubbles3DRandomFlameGenerator.class);
    registerRandomFlameGenerator(DuckiesRandomFlameGenerator.class);
    registerRandomFlameGenerator(GnarlRandomFlameGenerator.class);
    registerRandomFlameGenerator(Gnarl3DRandomFlameGenerator.class);
    registerRandomFlameGenerator(ExperimentalGnarlRandomFlameGenerator.class);
    registerRandomFlameGenerator(Flowers3DRandomFlameGenerator.class);
    registerRandomFlameGenerator(FilledFlowers3DRandomFlameGenerator.class);
    registerRandomFlameGenerator(LayerzRandomFlameGenerator.class);
    registerRandomFlameGenerator(MandelbrotRandomFlameGenerator.class);
    registerRandomFlameGenerator(JulianDiscRandomFlameGenerator.class);
    registerRandomFlameGenerator(SimpleRandomFlameGenerator.class);
    registerRandomFlameGenerator(ExperimentalSimpleRandomFlameGenerator.class);
    registerRandomFlameGenerator(LinearRandomFlameGenerator.class);
    registerRandomFlameGenerator(SierpinskyRandomFlameGenerator.class);
    registerRandomFlameGenerator(SimpleTilingRandomFlameGenerator.class);
    registerRandomFlameGenerator(SphericalRandomFlameGenerator.class);
    registerRandomFlameGenerator(Spherical3DRandomFlameGenerator.class);
    registerRandomFlameGenerator(SplitsRandomFlameGenerator.class);
    registerRandomFlameGenerator(SubFlameRandomFlameGenerator.class);
    registerRandomFlameGenerator(TentacleRandomFlameGenerator.class);
  }

  private static void registerRandomFlameGenerator(Class<? extends RandomFlameGenerator> pRandomFlameGenerator) {
    items.add(pRandomFlameGenerator);
    nameList = null;
  }

  public static List<String> getNameList() {
    if (nameList == null) {
      nameList = new ArrayList<String>();
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
    return nameList;
  }

  public static RandomFlameGenerator getRandomFlameGeneratorInstance(String pName) {
    return getRandomFlameGeneratorInstance(pName, false);
  }

  public static RandomFlameGenerator getRandomFlameGeneratorInstance(String pName, boolean pFatal) {
    int idx = getNameList().indexOf(pName);
    if (idx >= 0) {
      Class<? extends RandomFlameGenerator> funcCls = items.get(idx);
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
