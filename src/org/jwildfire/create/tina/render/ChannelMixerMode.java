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
package org.jwildfire.create.tina.render;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;

public enum ChannelMixerMode {
  OFF {
    @Override
    public Class<? extends ColorFunc> getColorFuncType() {
      return null;
    }
  },
  BRIGHTNESS {
    @Override
    protected Class<? extends ColorFunc> getColorFuncType() {
      return BrightnessColorFunc.class;
    }
  },
  RGB {
    @Override
    protected Class<? extends ColorFunc> getColorFuncType() {
      return RGBColorFunc.class;
    }
  },
  FULL {
    @Override
    protected Class<? extends ColorFunc> getColorFuncType() {
      return FullColorFunc.class;
    }
  };

  protected abstract Class<? extends ColorFunc> getColorFuncType();

  public ColorFunc getColorFunc(Flame pFlame, AbstractRandomGenerator pRandGen) {
    Class<? extends ColorFunc> cls = getColorFuncType();
    if (cls == null) {
      return ColorFunc.NULL;
    }
    ColorFunc res;
    try {
      res = cls.newInstance();
    }
    catch (InstantiationException ex) {
      throw new RuntimeException(ex);
    }
    catch (IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
    res.prepare(pFlame, pRandGen);
    return res;
  }
}
