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
package org.jwildfire.create.tina.randommovie;

import java.util.ArrayList;
import java.util.List;

public class RandomMovieGeneratorList {
  public static final String DEFAULT_GENERATOR_NAME = new AllRandomMovieGenerator().getName();
  private static List<Class<? extends RandomMovieGenerator>> items = new ArrayList<Class<? extends RandomMovieGenerator>>();
  private static final List<String> nameList;

  static {
    registerRandomMovieGenerator(AllRandomMovieGenerator.class);
    registerRandomMovieGenerator(TransformingBubblesRandomMovieGenerator.class);
    registerRandomMovieGenerator(TransformingDuckiesRandomMovieGenerator.class);
    registerRandomMovieGenerator(RotatingMandelbrotRandomMovieGenerator.class);
    nameList = new ArrayList<>();
    for (Class<? extends RandomMovieGenerator> funcCls : items) {
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

  private static void registerRandomMovieGenerator(Class<? extends RandomMovieGenerator> pRandomMovieGenerator) {
    items.add(pRandomMovieGenerator);
  }

  public static List<String> getNameList() {
    return nameList;
  }

  public static RandomMovieGenerator getRandomMovieGeneratorInstance(String pName) {
    return getRandomMovieGeneratorInstance(pName, false);
  }

  public static Class<? extends RandomMovieGenerator> getGeneratorClassByName(String pName) {
    int idx = getNameList().indexOf(pName);
    return idx >= 0 ? items.get(idx) : null;
  }

  public static RandomMovieGenerator getRandomMovieGeneratorInstance(String pName, boolean pFatal) {
    Class<? extends RandomMovieGenerator> funcCls = getGeneratorClassByName(pName);
    if (funcCls != null) {
      try {
        RandomMovieGenerator func = funcCls.newInstance();
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
