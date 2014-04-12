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
package org.jwildfire.create.tina.randomgradient;

import java.util.ArrayList;
import java.util.List;

public class RandomGradientGeneratorList {
  public static final String DEFAULT_GENERATOR_NAME = new AllRandomGradientGenerator().getName();
  private static List<Class<? extends RandomGradientGenerator>> items = new ArrayList<Class<? extends RandomGradientGenerator>>();
  private static List<String> nameList = new ArrayList<String>();

  static {
    registerRandomGradientGenerator(AllRandomGradientGenerator.class);
    registerRandomGradientGenerator(StrongHueRandomGradientGenerator.class);
    registerRandomGradientGenerator(MonochromeRandomGradientGenerator.class);
    registerRandomGradientGenerator(SmoothRandomGradientGenerator.class);
  }

  private static void registerRandomGradientGenerator(Class<? extends RandomGradientGenerator> pRandomMovieGenerator) {
    items.add(pRandomMovieGenerator);
    nameList = null;
  }

  public static List<String> getNameList() {
    if (nameList == null) {
      nameList = new ArrayList<String>();
      for (Class<? extends RandomGradientGenerator> funcCls : items) {
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

  public static RandomGradientGenerator getRandomGradientGeneratorInstance(String pName) {
    return getRandomGradientGeneratorInstance(pName, false);
  }

  public static Class<? extends RandomGradientGenerator> getGeneratorClassByName(String pName) {
    int idx = getNameList().indexOf(pName);
    return idx >= 0 ? items.get(idx) : null;
  }

  public static RandomGradientGenerator getRandomGradientGeneratorInstance(String pName, boolean pFatal) {
    Class<? extends RandomGradientGenerator> funcCls = getGeneratorClassByName(pName);
    if (funcCls != null) {
      try {
        RandomGradientGenerator func = funcCls.newInstance();
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
