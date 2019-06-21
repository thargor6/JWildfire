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
package org.jwildfire.create.tina.randomsymmetry;

import java.util.ArrayList;
import java.util.List;

public class RandomSymmetryGeneratorList {
  public static final String DEFAULT_GENERATOR_NAME = new AllSparseRandomSymmetryGenerator().getName();
  private static List<Class<? extends RandomSymmetryGenerator>> items = new ArrayList<Class<? extends RandomSymmetryGenerator>>();
  private static final List<String> nameList;

  public static final RandomSymmetryGenerator NONE = new NoneRandomSymmetryGenerator();
  public static final RandomSymmetryGenerator SPARSE = new AllSparseRandomSymmetryGenerator();

  static {
    registerRandomSymmetryGenerator(NoneRandomSymmetryGenerator.class);
    registerRandomSymmetryGenerator(AllRandomSymmetryGenerator.class);
    registerRandomSymmetryGenerator(AllSparseRandomSymmetryGenerator.class);
    registerRandomSymmetryGenerator(XAxisRandomSymmetryGenerator.class);
    registerRandomSymmetryGenerator(YAxisRandomSymmetryGenerator.class);
    registerRandomSymmetryGenerator(PointRandomSymmetryGenerator.class);
    nameList = new ArrayList<>();
    for (Class<? extends RandomSymmetryGenerator> funcCls : items) {
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

  private static void registerRandomSymmetryGenerator(Class<? extends RandomSymmetryGenerator> pRandomSymmetryGenerator) {
    items.add(pRandomSymmetryGenerator);
  }

  public static List<String> getNameList() {
    return nameList;
  }

  public static RandomSymmetryGenerator getRandomSymmetryGeneratorInstance(String pName) {
    return getRandomSymmetryGeneratorInstance(pName, false);
  }

  public static Class<? extends RandomSymmetryGenerator> getGeneratorClassByName(String pName) {
    int idx = getNameList().indexOf(pName);
    return idx >= 0 ? items.get(idx) : null;
  }

  public static RandomSymmetryGenerator getRandomSymmetryGeneratorInstance(String pName, boolean pFatal) {
    Class<? extends RandomSymmetryGenerator> funcCls = getGeneratorClassByName(pName);
    if (funcCls != null) {
      try {
        RandomSymmetryGenerator func = funcCls.newInstance();
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
