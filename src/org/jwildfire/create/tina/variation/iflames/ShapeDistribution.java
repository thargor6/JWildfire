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
package org.jwildfire.create.tina.variation.iflames;

public enum ShapeDistribution {
  RANDOM {
    @Override
    public int getId() {
      return 0;
    }
  },
  HUE {
    @Override
    public int getId() {
      return 1;
    }
  },
  LUMINOSITY {
    @Override
    public int getId() {
      return 2;
    }
  },
  BRIGHTNESS {
    @Override
    public int getId() {
      return 3;
    }
  },
  GRID {
    @Override
    public int getId() {
      return 4;
    }
  };

  public abstract int getId();

  public static ShapeDistribution valueOf(int pId) {
    for (ShapeDistribution value : values()) {
      if (pId == value.getId()) {
        return value;
      }
    }
    return null;
  }
}
