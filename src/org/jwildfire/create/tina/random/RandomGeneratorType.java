/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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

import org.jwildfire.base.Prefs;

public enum RandomGeneratorType {
  SIMPLE {

    @Override
    public Class<? extends AbstractRandomGenerator> getGeneratorClass(Prefs pPrefs) {
      return SimpleRandomGenerator.class;
    }
  },

  MARSAGLIA {

    @Override
    protected Class<? extends AbstractRandomGenerator> getGeneratorClass(Prefs pPrefs) {
      return MarsagliaRandomGenerator.class;
    }

  },

  MERSENNE_TWISTER {

    @Override
    protected Class<? extends AbstractRandomGenerator> getGeneratorClass(Prefs pPrefs) {
      return MersenneTwisterRandomGenerator.class;
    }

  },
  ZIGGURAT {

    @Override
    protected Class<? extends AbstractRandomGenerator> getGeneratorClass(Prefs pPrefs) {
      return ZigguratRandomGenerator.class;
    }

  },
  JAVA_INTERNAL {

    @Override
    protected Class<? extends AbstractRandomGenerator> getGeneratorClass(Prefs pPrefs) {
      return JavaInternalRandomGenerator.class;
    }

  };

  protected abstract Class<? extends AbstractRandomGenerator> getGeneratorClass(Prefs pPrefs);

  public AbstractRandomGenerator createInstance(Prefs pPrefs) {
    AbstractRandomGenerator res;
    try {
      res = getGeneratorClass(pPrefs).newInstance();
    }
    catch (InstantiationException e) {
      throw new RuntimeException(e);
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    res.randomize((long) (Integer.MAX_VALUE * Math.random()));
    return res;
  }

  public static RandomGeneratorType getDefaultValue() {
    return MARSAGLIA;
  }
}
