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
package org.jwildfire.create.tina.base.weightingfield;

import fastnoise.FastNoise;

public enum CellularNoiseReturnType {

  DISTANCE {
    @Override
    public FastNoise.CellularReturnType toFastNoiseType() {
      return FastNoise.CellularReturnType.Distance;
    }

    @Override
    public String toString() {
      return "Distance1";
    }
  },

  DISTANCE2 {
    @Override
    public FastNoise.CellularReturnType toFastNoiseType() {
      return FastNoise.CellularReturnType.Distance2;
    }

    @Override
    public String toString() {
      return "Distance2";
    }
  },

  DISTANCE_ADD {
    @Override
    public FastNoise.CellularReturnType toFastNoiseType() {
      return FastNoise.CellularReturnType.Distance2Add;
    }

    @Override
    public String toString() {
      return "Dist Add";
    }
  },

  DISTANCE_SUB {
    @Override
    public FastNoise.CellularReturnType toFastNoiseType() {
      return FastNoise.CellularReturnType.Distance2Sub;
    }

    @Override
    public String toString() {
      return "Dist Sub";
    }
  },

  DISTANCE_MUL {
    @Override
    public FastNoise.CellularReturnType toFastNoiseType() {
      return FastNoise.CellularReturnType.Distance2Mul;
    }

    @Override
    public String toString() {
      return "Dist Mul";
    }
  },

  DISTANCE_DIV {
    @Override
    public FastNoise.CellularReturnType toFastNoiseType() {
      return FastNoise.CellularReturnType.Distance2Div;
    }

    @Override
    public String toString() {
      return "Dist Div";
    }
  };

  public abstract FastNoise.CellularReturnType toFastNoiseType();
}
