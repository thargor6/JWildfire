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
package org.jwildfire.create.tina.randomweightingfield;

import java.util.List;
import java.util.ArrayList;

public class RandomWeightingFieldGeneratorList {
  public static final String DEFAULT_GENERATOR_NAME = new AllSparseRandomWeightingFieldGenerator().getName();
  private static List<Class<? extends RandomWeightingFieldGenerator>> items = new ArrayList<>();
  private static final List<String> nameList;

  public static final RandomWeightingFieldGenerator NONE = new NoneRandomWeightingFieldGenerator();
  public static final RandomWeightingFieldGenerator SPARSE = new AllSparseRandomWeightingFieldGenerator();

  static {
    registerRandomWeightingFieldGenerator(NoneRandomWeightingFieldGenerator.class);
    registerRandomWeightingFieldGenerator(AllRandomWeightingFieldGenerator.class);
    registerRandomWeightingFieldGenerator(AllSparseRandomWeightingFieldGenerator.class);
    registerRandomWeightingFieldGenerator(CellularNoiseNoiseRandomWeightingFieldGenerator.class);
    registerRandomWeightingFieldGenerator(BasicNoiseRandomWeightingFieldGenerator.class);
    registerRandomWeightingFieldGenerator(FractalNoiseRandomWeightingFieldGenerator.class);
    registerRandomWeightingFieldGenerator(ImageMapRandomWeightingFieldGenerator.class);
    nameList = new ArrayList<>();
    for (Class<? extends RandomWeightingFieldGenerator> funcCls : items) {
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

  private static void registerRandomWeightingFieldGenerator(Class<? extends RandomWeightingFieldGenerator> pRandomWeightingFieldGenerator) {
    items.add(pRandomWeightingFieldGenerator);
  }

  public static List<String> getNameList() {
    return nameList;
  }

  public static RandomWeightingFieldGenerator getRandomSymmetryGeneratorInstance(String pName) {
    return getRandomWeightingFieldGeneratorInstance(pName, false);
  }

  public static Class<? extends RandomWeightingFieldGenerator> getGeneratorClassByName(String pName) {
    int idx = getNameList().indexOf(pName);
    return idx >= 0 ? items.get(idx) : null;
  }

  public static RandomWeightingFieldGenerator getRandomWeightingFieldGeneratorInstance(String pName, boolean pFatal) {
    Class<? extends RandomWeightingFieldGenerator> funcCls = getGeneratorClassByName(pName);
    if (funcCls != null) {
      try {
        RandomWeightingFieldGenerator func = funcCls.newInstance();
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
