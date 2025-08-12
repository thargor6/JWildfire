
/*
 * JWildfire - an image and animation processor written in Java
 * Copyright (C) 1995-2021 Andreas Maschke
 *
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this software;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.M_PI;

/**
 * CamouflageFunc - A JWildfire variation for generating camouflage-like patterns.
 *
 * This variation uses multiple, selectable noise algorithms (Value, Perlin, Cellular)
 * to create organic, blob-like structures. The noise field is quantized into distinct
 * levels, which form the basis of the camouflage shapes. It supports coloring and
 * 3D layering for each level. This is a CPU-only variation.
 *
 * @author Gemini based on user request and base code by Andreas Maschke
 */
public class CamouflageFunc extends VariationFunc {
  private static final long serialVersionUID = 8L; // Bumped version for new features and cleanup

  // Parameter names
  private static final String PARAM_SEED = "seed";
  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_NOISE_TYPE = "noiseType";
  private static final String PARAM_LEVELS = "levels";
  private static final String PARAM_OCTAVES = "octaves";
  private static final String PARAM_PERSISTENCE = "persistence";
  private static final String PARAM_LACUNARITY = "lacunarity";
  private static final String PARAM_STRETCH = "stretch";
  private static final String PARAM_ROTATION = "rotation";
  private static final String PARAM_DISPLACEMENT = "displacement";
  private static final String PARAM_Z_OFFSET = "zOffset";
  private static final String PARAM_COLORIZE = "colorize";

  private static final String[] paramNames = {PARAM_SEED, PARAM_SCALE, PARAM_NOISE_TYPE, PARAM_LEVELS, PARAM_OCTAVES, PARAM_PERSISTENCE, PARAM_LACUNARITY, PARAM_STRETCH, PARAM_ROTATION, PARAM_DISPLACEMENT, PARAM_Z_OFFSET, PARAM_COLORIZE};

  // Default parameter values
  private double seed = 1000.0;
  private double scale = 2.0;
  private int noiseType = 0; // 0=Value, 1=Perlin, 2=Cellular
  private int levels = 4;
  private int octaves = 5;
  private double persistence = 0.5;
  private double lacunarity = 2.0;
  private double stretch = 1.0;
  private double rotation = 0.5;
  private double displacement = 0.1;
  private double zOffset = 0.1;
  private int colorize = 1; // 0=off, 1=on

  // --- Noise Implementation ---

  private double hash(int x, int y, int z) {
    int h = x * 374761393 + y * 668265263 + z * 1103515245;
    h = (h ^ (h >> 13)) * 1274126177;
    h = h ^ (h >> 16);
    return (double)(h & 0x7fffffff) / 1073741823.5 - 1.0;
  }
  
  private double hash(int i) {
    i = (i ^ 61) ^ (i >> 16);
    i = i + (i << 3);
    i = i ^ (i >> 4);
    i = i * 0x27d4eb2d;
    i = i ^ (i >> 15);
    return (double)(i & 0x7fffffff) / 1073741823.5 - 1.0;
  }

  private double lerp(double a, double b, double t) {
    return a + t * (b - a);
  }

  private double smoothstep(double t) {
    return t * t * (3.0 - 2.0 * t);
  }

  private double valueNoise(double x, double y, double z) {
    int ix = (int)Math.floor(x);
    int iy = (int)Math.floor(y);
    int iz = (int)Math.floor(z);
    double fx = x - ix;
    double fy = y - iy;
    double fz = z - iz;
    double u = smoothstep(fx);
    double v = smoothstep(fy);
    double w = smoothstep(fz);
    double n000 = hash(ix, iy, iz);
    double n100 = hash(ix + 1, iy, iz);
    double n010 = hash(ix, iy + 1, iz);
    double n110 = hash(ix + 1, iy + 1, iz);
    double n001 = hash(ix, iy, iz + 1);
    double n101 = hash(ix + 1, iy, iz + 1);
    double n011 = hash(ix, iy + 1, iz + 1);
    double n111 = hash(ix + 1, iy + 1, iz + 1);
    double nx00 = lerp(n000, n100, u);
    double nx10 = lerp(n010, n110, u);
    double nx01 = lerp(n001, n101, u);
    double nx11 = lerp(n011, n111, u);
    double nxy0 = lerp(nx00, nx10, v);
    double nxy1 = lerp(nx01, nx11, v);
    return lerp(nxy0, nxy1, w);
  }

  private double cellularNoise(double x, double y, double z) {
      int ix = (int)Math.floor(x);
      int iy = (int)Math.floor(y);
      int iz = (int)Math.floor(z);
      double minDist = Double.MAX_VALUE;
      for (int dz = -1; dz <= 1; dz++) {
          for (int dy = -1; dy <= 1; dy++) {
              for (int dx = -1; dx <= 1; dx++) {
                  int cellX = ix + dx;
                  int cellY = iy + dy;
                  int cellZ = iz + dz;
                  double pointX = cellX + 0.5 * (1.0 + hash(cellX, cellY, cellZ));
                  double pointY = cellY + 0.5 * (1.0 + hash(cellY, cellZ, cellX));
                  double pointZ = cellZ + 0.5 * (1.0 + hash(cellZ, cellX, cellY));
                  double distSq = (x - pointX) * (x - pointX) + 
                                  (y - pointY) * (y - pointY) + 
                                  (z - pointZ) * (z - pointZ);
                  if (distSq < minDist) {
                      minDist = distSq;
                  }
              }
          }
      }
      return Math.sqrt(minDist) * 2.0 - 1.0;
  }
  
  private static final int[] p = new int[512];
  static {
      int[] permutation = { 151,160,137,91,90,15,131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,88,237,149,56,87,174,20,125,136,171,168, 68,175,74,165,71,134,139,48,27,166,77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,102,143,54, 65,25,63,161, 1,216,80,73,209,76,132,187,208, 89,18,169,200,196,135,130,116,188,159,86,164,100,109,198,173,186, 3,64,52,217,226,250,124,123,5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,223,183,170,213,119,248,152, 2,44,154,163, 70,221,153,101,155,167, 43,172,9,129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228,251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107,49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254,138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180};
      for (int i=0; i < 256 ; i++) p[256+i] = p[i] = permutation[i];
  }
  private double fade(double t) { return t * t * t * (t * (t * 6 - 15) + 10); }
  private double grad(int hash, double x, double y, double z) {
      int h = hash & 15;
      double u = h<8 ? x : y, v = h<4 ? y : h==12||h==14 ? x : z;
      return ((h&1) == 0 ? u : -u) + ((h&2) == 0 ? v : -v);
  }
  private double perlinNoise(double x, double y, double z) {
      int X = (int)Math.floor(x) & 255, Y = (int)Math.floor(y) & 255, Z = (int)Math.floor(z) & 255;
      x -= Math.floor(x); y -= Math.floor(y); z -= Math.floor(z);
      double u = fade(x), v = fade(y), w = fade(z);
      int A = p[X]+Y, AA = p[A]+Z, AB = p[A+1]+Z, B = p[X+1]+Y, BA = p[B]+Z, BB = p[B+1]+Z;
      return lerp(w, lerp(v, lerp(u, grad(p[AA  ], x  , y  , z   ), grad(p[BA  ], x-1, y  , z   )), lerp(u, grad(p[AB  ], x  , y-1, z   ), grad(p[BB  ], x-1, y-1, z   ))), lerp(v, lerp(u, grad(p[AA+1], x  , y  , z-1 ), grad(p[BA+1], x-1, y  , z-1 )), lerp(u, grad(p[AB+1], x  , y-1, z-1 ), grad(p[BB+1], x-1, y-1, z-1 ))));
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double x = pAffineTP.x * scale;
    double y = pAffineTP.y * scale * stretch;
    double z = pAffineTP.z * scale;

    double noiseValue = 0.0;
    double freq = 1.0;
    double amp = 1.0;
    for (int i = 0; i < octaves; i++) {
        double n;
        switch(noiseType) {
            case 1: n = perlinNoise(x * freq + seed, y * freq + seed, z * freq + seed); break;
            case 2: n = cellularNoise(x * freq + seed, y * freq + seed, z * freq + seed); break;
            default: n = valueNoise(x * freq, y * freq, z * freq + seed); break;
        }
      noiseValue += n * amp;
      freq *= lacunarity;
      amp *= persistence;
    }

    double quantizedNoise = Math.floor(noiseValue * levels);

    if (this.colorize == 1) {
      double colorIndex = (quantizedNoise % this.levels + this.levels) % this.levels;
      pVarTP.color = colorIndex / (double) this.levels;
    }

    double angle = quantizedNoise * M_PI * 2.0 * rotation / levels;
    double mag = quantizedNoise * displacement / levels;
    double z_offset_val = quantizedNoise * zOffset / levels;

    double sin_a = Math.sin(angle);
    double cos_a = Math.cos(angle);

    double x_new = pAffineTP.x * cos_a - pAffineTP.y * sin_a + mag;
    double y_new = pAffineTP.x * sin_a + pAffineTP.y * cos_a + mag;
    double z_new = pAffineTP.z + z_offset_val;

    pVarTP.x += (x_new - pAffineTP.x) * pAmount;
    pVarTP.y += (y_new - pAffineTP.y) * pAmount;
    pVarTP.z += (z_new - pAffineTP.z) * pAmount;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{seed, scale, noiseType, levels, octaves, persistence, lacunarity, stretch, rotation, displacement, zOffset, colorize};
  }
  
  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SEED.equalsIgnoreCase(pName)) {
      seed = pValue;
    } else if (PARAM_SCALE.equalsIgnoreCase(pName)) {
      scale = pValue;
    } else if (PARAM_NOISE_TYPE.equalsIgnoreCase(pName)) {
      noiseType = Math.max(0, Math.min(2, (int)pValue)); // Clamp to valid range 0-2
    } else if (PARAM_LEVELS.equalsIgnoreCase(pName)) {
      levels = Math.max(1, (int) pValue);
    } else if (PARAM_OCTAVES.equalsIgnoreCase(pName)) {
      octaves = Math.max(1, (int) pValue);
    } else if (PARAM_PERSISTENCE.equalsIgnoreCase(pName)) {
      persistence = pValue;
    } else if (PARAM_LACUNARITY.equalsIgnoreCase(pName)) {
      lacunarity = pValue;
    } else if (PARAM_STRETCH.equalsIgnoreCase(pName)) {
      stretch = pValue;
    } else if (PARAM_ROTATION.equalsIgnoreCase(pName)) {
      rotation = pValue;
    } else if (PARAM_DISPLACEMENT.equalsIgnoreCase(pName)) {
      displacement = pValue;
    } else if (PARAM_Z_OFFSET.equalsIgnoreCase(pName)) {
      zOffset = pValue;
    } else if (PARAM_COLORIZE.equalsIgnoreCase(pName)) {
      colorize = pValue > 0.5 ? 1 : 0; // Treat as a boolean toggle
    } else {
      throw new IllegalArgumentException(pName);
    }
  }

  @Override
  public String getName() {
    return "camouflage";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D};
  }

}
