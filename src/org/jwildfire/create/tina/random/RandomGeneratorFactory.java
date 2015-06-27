/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

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
package org.jwildfire.create.tina.random;

import java.util.HashMap;
import java.util.Map;

import org.jwildfire.base.Prefs;

public class RandomGeneratorFactory {
  private static Map<String, AbstractRandomGenerator> generatorMap = new HashMap<String, AbstractRandomGenerator>();

  public static AbstractRandomGenerator getInstance(Prefs pPrefs, RandomGeneratorType pType, int pThreadId) {
    String key = pType.toString() + "#" + pThreadId;
    AbstractRandomGenerator res = generatorMap.get(key);
    if (res == null) {
      res = pType.createInstance(pPrefs);
      if (!pType.equals(RandomGeneratorType.MERSENNE_TWISTER)) {
        generatorMap.put(key, res);
      }
    }
    return res;
  }

  public static AbstractRandomGenerator getInstance(Prefs pPrefs, RandomGeneratorType pType) {
    return getInstance(pPrefs, pType, 0);
  }

  public static void cleanup() {
    for (AbstractRandomGenerator gen : generatorMap.values()) {
      try {
        gen.cleanup();
      }
      catch (Throwable ex) {
        ex.printStackTrace();
      }
    }
  }
}
