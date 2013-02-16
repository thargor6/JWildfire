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
package org.jwildfire.base.mathlib;

public enum BaseMathLibType {
  FAST_MATH {

    @Override
    public Class<? extends BaseMathLib> getBaseMathLibClass() {
      return FastMathLibImpl.class;
    }

  },
  JAVA_MATH {

    @Override
    public Class<? extends BaseMathLib> getBaseMathLibClass() {
      return JavaMathLibImpl.class;
    }
  };

  protected abstract Class<? extends BaseMathLib> getBaseMathLibClass();

  public BaseMathLib createInstance() {
    try {
      return getBaseMathLibClass().newInstance();
    }
    catch (InstantiationException e) {
      throw new RuntimeException(e);
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static BaseMathLibType getDefaultValue() {
    return FAST_MATH;
  }

}
