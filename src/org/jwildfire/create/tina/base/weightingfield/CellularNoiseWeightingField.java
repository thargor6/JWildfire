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

public class CellularNoiseWeightingField extends NoiseWeightingField {
  private CellularNoiseReturnType returnType = CellularNoiseReturnType.DISTANCE2;
  private CellularNoiseDistanceFunction distanceFunction = CellularNoiseDistanceFunction.EUCLIDIAN;

  public CellularNoiseWeightingField() {
    super(FastNoise.NoiseType.Cellular);
  }

  public CellularNoiseReturnType getReturnType() {
    return returnType;
  }

  public void setReturnType(CellularNoiseReturnType returnType) {
    this.returnType = returnType;
    noise.SetCellularReturnType(returnType.toFastNoiseType());
  }

  public CellularNoiseDistanceFunction getDistanceFunction() {
    return distanceFunction;
  }

  public void setDistanceFunction(CellularNoiseDistanceFunction distanceFunction) {
    this.distanceFunction = distanceFunction;
    noise.SetCellularDistanceFunction(distanceFunction.toFastNoiseType());
  }
}
