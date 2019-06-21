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

import org.jwildfire.create.tina.base.XForm;

public enum WeightingFieldType {

  NONE {
    @Override
    public WeightingField getInstance(XForm xform) {
      return new EmptyWeightingField();
    }

    @Override
    public String toString() {
      return "None";
    }
  },

  CELLULAR_NOISE {
    @Override
    public WeightingField getInstance(XForm xform) {
      CellularNoiseWeightingField noise = new CellularNoiseWeightingField();
      noise.setSeed(xform.getWeightingFieldNoiseSeed());
      noise.setFrequency(xform.getWeightingFieldNoiseFrequency());
      noise.setDistanceFunction(xform.getWeightingFieldCellularNoiseDistanceFunction());
      noise.setReturnType(xform.getWeightingFieldCellularNoiseReturnType());
      return noise;
    }

    @Override
    public String toString() {
      return "Cellular Noise";
    }
  },

  CUBIC_NOISE {
    @Override
    public WeightingField getInstance(XForm xform) {
      CubicNoiseWeightingField noise = new CubicNoiseWeightingField();
      noise.setSeed(xform.getWeightingFieldNoiseSeed());
      noise.setFrequency(xform.getWeightingFieldNoiseFrequency());
      return noise;
    }

    @Override
    public String toString() {
      return "Cubic Noise";
    }
  },

  CUBIC_FRACTAL_NOISE {
    @Override
    public WeightingField getInstance(XForm xform) {
      CubicFractalNoiseWeightingField noise = new CubicFractalNoiseWeightingField();
      noise.setSeed(xform.getWeightingFieldNoiseSeed());
      noise.setFrequency(xform.getWeightingFieldNoiseFrequency());
      noise.setFractalNoiseType(xform.getWeightingFieldFractalType());
      noise.setGain(xform.getWeightingFieldFractalNoiseGain());
      noise.setLacunarity(xform.getWeightingFieldFractalNoiseLacunarity());
      noise.setOctaves(xform.getWeightingFieldFractalNoiseOctaves());
      return noise;
    }

    @Override
    public String toString() {
      return "Cubic Fractal Noise";
    }
  },

  PERLIN_NOISE {
    @Override
    public WeightingField getInstance(XForm xform) {
      PerlinNoiseWeightingField noise = new PerlinNoiseWeightingField();
      noise.setSeed(xform.getWeightingFieldNoiseSeed());
      noise.setFrequency(xform.getWeightingFieldNoiseFrequency());
      return noise;
    }

    @Override
    public String toString() {
      return "Perlin Noise";
    }
  },

  PERLIN_FRACTAL_NOISE {
    @Override
    public WeightingField getInstance(XForm xform) {
      PerlinFractalNoiseWeightingField noise = new PerlinFractalNoiseWeightingField();
      noise.setSeed(xform.getWeightingFieldNoiseSeed());
      noise.setFrequency(xform.getWeightingFieldNoiseFrequency());
      noise.setFractalNoiseType(xform.getWeightingFieldFractalType());
      noise.setGain(xform.getWeightingFieldFractalNoiseGain());
      noise.setLacunarity(xform.getWeightingFieldFractalNoiseLacunarity());
      noise.setOctaves(xform.getWeightingFieldFractalNoiseOctaves());
      return noise;
    }

    @Override
    public String toString() {
      return "Perlin Fractal Noise";
    }
  },

  SIMPLEX_NOISE {
    @Override
    public WeightingField getInstance(XForm xform) {
      SimplexNoiseWeightingField noise = new SimplexNoiseWeightingField();
      noise.setSeed(xform.getWeightingFieldNoiseSeed());
      noise.setFrequency(xform.getWeightingFieldNoiseFrequency());
      return noise;
    }

    @Override
    public String toString() {
      return "Simplex Noise";
    }
  },

  SIMPLEX_FRACTAL_NOISE {
    @Override
    public WeightingField getInstance(XForm xform) {
      SimplexFractalNoiseWeightingField noise = new SimplexFractalNoiseWeightingField();
      noise.setSeed(xform.getWeightingFieldNoiseSeed());
      noise.setFrequency(xform.getWeightingFieldNoiseFrequency());
      noise.setFractalNoiseType(xform.getWeightingFieldFractalType());
      noise.setGain(xform.getWeightingFieldFractalNoiseGain());
      noise.setLacunarity(xform.getWeightingFieldFractalNoiseLacunarity());
      noise.setOctaves(xform.getWeightingFieldFractalNoiseOctaves());
      return noise;
    }

    @Override
    public String toString() {
      return "Simplex Fractal Noise";
    }
  },

  VALUE_NOISE {
    @Override
    public WeightingField getInstance(XForm xform) {
      ValueNoiseWeightingField noise = new ValueNoiseWeightingField();
      noise.setSeed(xform.getWeightingFieldNoiseSeed());
      noise.setFrequency(xform.getWeightingFieldNoiseFrequency());
      return noise;
    }

    @Override
    public String toString() {
      return "Value Noise";
    }
  },

  VALUE_FRACTAL_NOISE {
    @Override
    public WeightingField getInstance(XForm xform) {
      ValueFractalNoiseWeightingField noise = new ValueFractalNoiseWeightingField();
      noise.setSeed(xform.getWeightingFieldNoiseSeed());
      noise.setFrequency(xform.getWeightingFieldNoiseFrequency());
      noise.setFractalNoiseType(xform.getWeightingFieldFractalType());
      noise.setGain(xform.getWeightingFieldFractalNoiseGain());
      noise.setLacunarity(xform.getWeightingFieldFractalNoiseLacunarity());
      noise.setOctaves(xform.getWeightingFieldFractalNoiseOctaves());
      return noise;
    }

    @Override
    public String toString() {
      return "Value Fractal Noise";
    }
  },

  IMAGE_MAP {
    @Override
    public WeightingField getInstance(XForm xform) {
      ImageMapWeightingField noise = new ImageMapWeightingField();
      noise.setImageFilename(xform.getWeightingFieldColorMapFilename());
      noise.setxCentre(xform.getWeightingFieldColorMapXCentre());
      noise.setyCentre(xform.getWeightingFieldColorMapYCentre());
      noise.setxSize(xform.getWeightingFieldColorMapXSize());
      noise.setySize(xform.getWeightingFieldColorMapYSize());
      return noise;
    }

    @Override
    public String toString() {
      return "Image Map";
    }
  };

  public abstract WeightingField getInstance(XForm xform);
}
