/*
/*
Copyright 2008 Steven Brodhead, Jr.
Copyright 2011-2016 Steven Brodhead, Sr., Centcom Inc.

// All rights reserved.
 
//     Fractal Architect Render Engine - a GPU accelerated flame fractal renderer written in C++
//
//     This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
//     General Public License as published by the Free Software Foundation; either version 2.1 of the
//     License, or (at your option) any later version.
//
//     This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
//     even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//     Lesser General Public License for more details.
//
//     You should have received a copy of the GNU Lesser General Public License along with this software;
//     if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
//     02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

/*
Extended version for the use from within JWildfire7+. Requires the extended FACLRenderJWF.exe as client to execute.
See https://bitbucket.org/amaschke/faengine/src/JWildfireExperiments/ for more details.
Copyright 2021 Andreas Maschke, with contributions made by Jesus Sosa.
*/

// the following switches are made to help to keep the kernel small and include only features which are actually used (="poor module system")
// activate noise features
//  #define ADD_FEATURE_CELLULAR_NOISE
//  #define ADD_FEATURE_CUBIC_NOISE
//  #define ADD_FEATURE_PERLIN_NOISE
//  #define ADD_FEATURE_SIMPLEX_NOISE
//  #define ADD_FEATURE_VALUE_NOISE
//  #define ADD_FEATURE_WHITE_NOISE
// activate wfields, please note that you must ensure to enable all the required noise types, otherwise all noise (and so the wfield) will be zero
//   #define ADD_FEATURE_WFIELDS
//   #define ADD_FEATURE_WFIELDS_JITTER
// activate additional features
//   #define ADD_FEATURE_DOF
// Usually, these switches are set by the client by replacing the following placeholder:
__GLOBAL_DEFINITIONS__

#if defined(ADD_FEATURE_CELLULAR_NOISE) || defined(ADD_FEATURE_CUBIC_NOISE) || defined(ADD_FEATURE_PERLIN_NOISE) || defined(ADD_FEATURE_SIMPLEX_NOISE) || defined(ADD_FEATURE_VALUE_NOISE) || defined(ADD_FEATURE_WHITE_NOISE)
  #define ADD_FEATURE_FAST_NOISE
#else
  #undef ADD_FEATURE_FAST_NOISE
#endif

#define NUM_ITERATIONS 100
// #define DENSITY_KERNAL_RADIUS 7
#define DENSITY_KERNAL_RADIUS_16KB 7
#define DENSITY_KERNAL_RADIUS_32KB 14
#define DENSITY_KERNAL_RADIUS_48KB 19
#define NUM_FRAMES 160
#define FRAME_RATE 30
#define BITRATE 54000000

#ifndef SUPERSAMPLE_WIDTH
#define SUPERSAMPLE_WIDTH 0.25f
#endif

#ifndef FLAMEDATA_H
#define FLAMEDATA_H

#define MAX_XFORMS 58 // We're limited to 64KB constant memory for compute capacity 1.0.
// All xForms must fit in this.


#define NO_RGBA_CONSTRUCTOR

#define uint  unsigned
#define ulong unsigned long

#define M_PI_F    3.141592653589793f
#define M_PI_2_F  1.5707963267949f
#define M_PI_4_F  0.78539816339745f
#define M_1_PI_F  0.31830988618379f
#define M_2_PI_F  0.63661977236758f

#define JWF_EXTENSIONS

#ifndef RGBA_H
#define RGBA_H

struct  __align__(16) rgba
{
    float r;
    float g;
    float b;
    float a;
};

#endif

#ifdef JWF_EXTENSIONS
__device__ float sqrtf_safe(float x) {
  if (x <= 0.0f)
    return 0.0f;
  else	
    return sqrtf(x);
}

__device__ float lerpf(float a, float b, float p) {
    return a + (b - a) * p;
}

__device__ float blerpf(float c00, float c10, float c01, float c11, float tx, float ty) {
    return lerpf(lerpf(c00, c10, tx), lerpf(c01, c11, tx), ty);
}

__device__ float fracf(float x) {
  return x - truncf(x);
}

#define EPSILON 0.000000001f


#ifdef ADD_FEATURE_FAST_NOISE
//--------------------------------- Noise (for supporting wfields) ----------------------------------
// partial CUDA-port of FastNoise: https://github.com/Auburn/FastNoise_Java
// restrictions:
//  - only 3d-noise is supported
// -  and NoiseLookup-return-type of cellular noise is not supported because it is very complicated to set up (at least on GPU, in comparison to all other types)
typedef enum {Value, ValueFractal, Perlin, PerlinFractal, Simplex, SimplexFractal, Cellular, WhiteNoise, Cubic, CubicFractal} NoiseType;
typedef enum {Linear, Hermite, Quintic} Interp;
typedef enum {FBM, Billow, RigidMulti} FractalType;
typedef enum {Euclidean, Manhattan, Natural} CellularDistanceFunction;
typedef enum {CellValue, Distance, Distance2, Distance2Add, Distance2Sub, Distance2Mul, Distance2Div} CellularReturnType;

typedef struct __align__(8)
{
  int m_seed; // seed used for all noise types
              // Default: 1337
  float m_frequency; // frequency for all noise types
                     // Default: 0.01
  Interp m_interp; // possible interpolation methods (lowest to highest quality): Linear, Hermite, Quintic
                   // used in Value, Gradient Noise and Position Perturbing
                   // Default: Quintic
  NoiseType m_noiseType; // Default: Simplex
  int m_octaves; // octave count for all fractal noise types
                 // Default: 3
  float m_lacunarity; // octave lacunarity for all fractal noise types
                      // Default: 2.0
  float m_gain; //  octave gain for all fractal noise types
               	// Default: 0.5
  FractalType m_fractalType; // method for combining octaves in all fractal noise types
                            // Default: FBM
  CellularDistanceFunction m_cellularDistanceFunction; 	// distance function used in cellular noise calculations
                                                       	// Default: Euclidean
  CellularReturnType m_cellularReturnType; 	// return type from cellular noise calculations
                                           	// Default: CellValue
  float m_fractalBounding;
} FastNoise;


__device__ void calculateFractalBounding(FastNoise* n) {
    float amp = n->m_gain;
    float ampFractal = 1;
    for (int i = 1; i < n->m_octaves; i++) {
        ampFractal += amp;
        amp *= n->m_gain;
    }
    n->m_fractalBounding = 1 / ampFractal;
}

__device__ void fastNoise_init(FastNoise* n) {
  n->m_seed = 1337;
  n->m_frequency = 0.01f;
  n->m_interp = Quintic;
  n->m_noiseType = Simplex;
  n->m_octaves = 3;
  n->m_lacunarity = 2.0f;
  n->m_gain = 0.5f;
  n->m_fractalType = FBM;
  n->m_cellularDistanceFunction = Euclidean;
  n->m_cellularReturnType = Distance;
  calculateFractalBounding(n);
}

__device__ void fastNoise_prepare(FastNoise* n) {
  calculateFractalBounding(n);
}

__device__ int fastFloor(float f) {
  return (f >= 0 ? (int) f : (int) f - 1);
}

__device__ int fastRound(float f) {
  return (f >= 0) ? (int) (f + (float) 0.5) : (int) (f - (float) 0.5);
}

__device__ float lerp(float a, float b, float t) {
  return a + t * (b - a);
}

__device__ float interpHermiteFunc(float t) {
  return t * t * (3 - 2 * t);
}

__device__ float interpQuinticFunc(float t) {
  return t * t * t * (t * (t * 6 - 15) + 10);
}

__device__ float cubicLerp(float a, float b, float c, float d, float t) {
  float p = (d - c) - (a - b);
  return t * t * t * p + t * t * ((a - b) - p) + t * (c - a) + b;
}

__device__ __constant__ float GRAD_3D_x[16] =  { 1, -1, 1, -1, 1, -1, 1, -1, 0, 0, 0, 0, 1, 0, -1, 0 };
__device__ __constant__ float GRAD_3D_y[16] =  { 1, 1, -1, -1, 0, 0, 0, 0, 1, -1, 1, -1, 1, -1, 1, -1 };
__device__ __constant__ float GRAD_3D_z[16] =  { 0, 0, 0, 0, 1, 1, -1, -1, 1, 1, -1, -1, 0, 1, 0, -1 };

// Hashing
__device__ __constant__ int X_PRIME = 1619;
__device__ __constant__ int Y_PRIME = 31337;
__device__ __constant__ int Z_PRIME = 6971;
__device__ __constant__ int W_PRIME = 1013;

__device__ int hash2D(int seed, int x, int y) {
    int hash = seed;
    hash ^= X_PRIME * x;
    hash ^= Y_PRIME * y;

    hash = hash * hash * hash * 60493;
    hash = (hash >> 13) ^ hash;

    return hash;
}

__device__ int hash3D(int seed, int x, int y, int z) {
    int hash = seed;
    hash ^= X_PRIME * x;
    hash ^= Y_PRIME * y;
    hash ^= Z_PRIME * z;

    hash = hash * hash * hash * 60493;
    hash = (hash >> 13) ^ hash;

    return hash;
}

__device__ int hash4D(int seed, int x, int y, int z, int w) {
    int hash = seed;
    hash ^= X_PRIME * x;
    hash ^= Y_PRIME * y;
    hash ^= Z_PRIME * z;
    hash ^= W_PRIME * w;

    hash = hash * hash * hash * 60493;
    hash = (hash >> 13) ^ hash;

    return hash;
}

__device__ float valCoord2D(int seed, int x, int y) {
    int n = seed;
    n ^= X_PRIME * x;
    n ^= Y_PRIME * y;

    return (n * n * n * 60493) / (float) 2147483648.0;
}

__device__ float valCoord3D(int seed, int x, int y, int z) {
    int n = seed;
    n ^= X_PRIME * x;
    n ^= Y_PRIME * y;
    n ^= Z_PRIME * z;

    return (n * n * n * 60493) / (float) 2147483648.0;
}

__device__ float valCoord4D(int seed, int x, int y, int z, int w) {
    int n = seed;
    n ^= X_PRIME * x;
    n ^= Y_PRIME * y;
    n ^= Z_PRIME * z;
    n ^= W_PRIME * w;

    return (n * n * n * 60493) / (float) 2147483648.0;
}

__device__ float gradCoord3D(int seed, int x, int y, int z, float xd, float yd, float zd) {
    int hash = seed;
    hash ^= X_PRIME * x;
    hash ^= Y_PRIME * y;
    hash ^= Z_PRIME * z;

    hash = hash * hash * hash * 60493;
    hash = (hash >> 13) ^ hash;

    int idx = hash & 15;

    return xd * GRAD_3D_x[idx] + yd * GRAD_3D_y[idx] + zd * GRAD_3D_z[idx];
}

__device__ float gradCoord4D(int seed, int x, int y, int z, int w, float xd, float yd, float zd, float wd) {
    int hash = seed;
    hash ^= X_PRIME * x;
    hash ^= Y_PRIME * y;
    hash ^= Z_PRIME * z;
    hash ^= W_PRIME * w;

    hash = hash * hash * hash * 60493;
    hash = (hash >> 13) ^ hash;

    hash &= 31;
    float a = yd, b = zd, c = wd;            // X,Y,Z
    switch (hash >> 3) {          // OR, DEPENDING ON HIGH ORDER 2 BITS:
        case 1:
            a = wd;
            b = xd;
            c = yd;
            break;     // W,X,Y
        case 2:
            a = zd;
            b = wd;
            c = xd;
            break;     // Z,W,X
        case 3:
            a = yd;
            b = zd;
            c = wd;
            break;     // Y,Z,W
    }
    return ((hash & 4) == 0 ? -a : a) + ((hash & 2) == 0 ? -b : b) + ((hash & 1) == 0 ? -c : c);
}

// White Noise
#ifdef ADD_FEATURE_WHITE_NOISE
__device__ int floatToIntBits(float  x)
{
  union {
    float f;  // assuming 32-bit IEEE 754 single-precision
    int i;    // assuming 32-bit 2's complement int
  } u;

  if (isnan(x)) {
    return 0x7fc00000;
  } else {
    u.f = x;
    return u.i;
  }
}

__device__  int floatCast2Int(float f) {
    int i = floatToIntBits(f);
    return i ^ (i >> 16);
}
#endif // ADD_FEATURE_WHITE_NOISE

#ifdef ADD_FEATURE_WHITE_NOISE
__device__ float getWhiteNoise(FastNoise* n, float x, float y, float z) {
    int xi = floatCast2Int(x);
    int yi = floatCast2Int(y);
    int zi = floatCast2Int(z);

    return valCoord3D(n->m_seed, xi, yi, zi);
}

__device__ float getWhiteNoiseInt(FastNoise* n, int x, int y, int z) {
    return valCoord3D(n->m_seed, x, y, z);
}
#endif // ADD_FEATURE_WHITE_NOISE

// Value Noise
#ifdef ADD_FEATURE_VALUE_NOISE
__device__ float singleValue(FastNoise* n,int seed, float x, float y, float z) {
    int x0 = fastFloor(x);
    int y0 = fastFloor(y);
    int z0 = fastFloor(z);
    int x1 = x0 + 1;
    int y1 = y0 + 1;
    int z1 = z0 + 1;

    float xs, ys, zs;
    switch (n->m_interp) {
        default:
        case Linear:
            xs = x - x0;
            ys = y - y0;
            zs = z - z0;
            break;
        case Hermite:
            xs = interpHermiteFunc(x - x0);
            ys = interpHermiteFunc(y - y0);
            zs = interpHermiteFunc(z - z0);
            break;
        case Quintic:
            xs = interpQuinticFunc(x - x0);
            ys = interpQuinticFunc(y - y0);
            zs = interpQuinticFunc(z - z0);
            break;
    }

    float xf00 = lerp(valCoord3D(seed, x0, y0, z0), valCoord3D(seed, x1, y0, z0), xs);
    float xf10 = lerp(valCoord3D(seed, x0, y1, z0), valCoord3D(seed, x1, y1, z0), xs);
    float xf01 = lerp(valCoord3D(seed, x0, y0, z1), valCoord3D(seed, x1, y0, z1), xs);
    float xf11 = lerp(valCoord3D(seed, x0, y1, z1), valCoord3D(seed, x1, y1, z1), xs);

    float yf0 = lerp(xf00, xf10, ys);
    float yf1 = lerp(xf01, xf11, ys);

    return lerp(yf0, yf1, zs);
}

__device__ float singleValueFractalFBM(FastNoise* n, float x, float y, float z) {
    int seed = n->m_seed;
    float sum = singleValue(n, seed, x, y, z);
    float amp = 1;

    for (int i = 1; i < n->m_octaves; i++) {
        x *= n->m_lacunarity;
        y *= n->m_lacunarity;
        z *= n->m_lacunarity;

        amp *= n->m_gain;
        sum += singleValue(n, ++seed, x, y, z) * amp;
    }

    return sum * n->m_fractalBounding;
}

__device__ float singleValueFractalBillow(FastNoise* n, float x, float y, float z) {
    int seed = n->m_seed;
    float sum = fabsf(singleValue(n, seed, x, y, z)) * 2 - 1;
    float amp = 1;

    for (int i = 1; i < n->m_octaves; i++) {
        x *= n->m_lacunarity;
        y *= n->m_lacunarity;
        z *= n->m_lacunarity;

        amp *= n->m_gain;
        sum += (fabsf(singleValue(n, ++seed, x, y, z)) * 2 - 1) * amp;
    }

    return sum * n->m_fractalBounding;
}

__device__ float singleValueFractalRigidMulti(FastNoise* n, float x, float y, float z) {
    int seed = n->m_seed;
    float sum = 1 - fabsf(singleValue(n, seed, x, y, z));
    float amp = 1;

    for (int i = 1; i < n->m_octaves; i++) {
        x *= n->m_lacunarity;
        y *= n->m_lacunarity;
        z *= n->m_lacunarity;

        amp *= n->m_gain;
        sum -= (1 - fabsf(singleValue(n, ++seed, x, y, z))) * amp;
    }

    return sum;
}

__device__ float getValue(FastNoise* n, float x, float y, float z) {
    return singleValue(n, n->m_seed, x * n->m_frequency, y * n->m_frequency, z * n->m_frequency);
}

__device__ float getValueFractal(FastNoise* n, float x, float y, float z) {
    x *= n->m_frequency;
    y *= n->m_frequency;
    z *= n->m_frequency;

    switch (n->m_fractalType) {
        case FBM:
            return singleValueFractalFBM(n, x, y, z);
        case Billow:
            return singleValueFractalBillow(n, x, y, z);
        case RigidMulti:
            return singleValueFractalRigidMulti(n, x, y, z);
        default:
            return 0;
    }
}
#endif // ADD_FEATURE_VALUE_NOISE

#ifdef ADD_FEATURE_PERLIN_NOISE
// Perlin Noise
__device__ float singlePerlin(FastNoise* n, int seed, float x, float y, float z) {
    int x0 = fastFloor(x);
    int y0 = fastFloor(y);
    int z0 = fastFloor(z);
    int x1 = x0 + 1;
    int y1 = y0 + 1;
    int z1 = z0 + 1;

    float xs, ys, zs;
    switch (n->m_interp) {
        default:
        case Linear:
            xs = x - x0;
            ys = y - y0;
            zs = z - z0;
            break;
        case Hermite:
            xs = interpHermiteFunc(x - x0);
            ys = interpHermiteFunc(y - y0);
            zs = interpHermiteFunc(z - z0);
            break;
        case Quintic:
            xs = interpQuinticFunc(x - x0);
            ys = interpQuinticFunc(y - y0);
            zs = interpQuinticFunc(z - z0);
            break;
    }

    float xd0 = x - x0;
    float yd0 = y - y0;
    float zd0 = z - z0;
    float xd1 = xd0 - 1;
    float yd1 = yd0 - 1;
    float zd1 = zd0 - 1;

    float xf00 = lerp(gradCoord3D(seed, x0, y0, z0, xd0, yd0, zd0), gradCoord3D(seed, x1, y0, z0, xd1, yd0, zd0), xs);
    float xf10 = lerp(gradCoord3D(seed, x0, y1, z0, xd0, yd1, zd0), gradCoord3D(seed, x1, y1, z0, xd1, yd1, zd0), xs);
    float xf01 = lerp(gradCoord3D(seed, x0, y0, z1, xd0, yd0, zd1), gradCoord3D(seed, x1, y0, z1, xd1, yd0, zd1), xs);
    float xf11 = lerp(gradCoord3D(seed, x0, y1, z1, xd0, yd1, zd1), gradCoord3D(seed, x1, y1, z1, xd1, yd1, zd1), xs);

    float yf0 = lerp(xf00, xf10, ys);
    float yf1 = lerp(xf01, xf11, ys);

    return lerp(yf0, yf1, zs);
}

__device__ float getPerlin(FastNoise* n, float x, float y, float z) {
    return singlePerlin(n, n->m_seed, x * n->m_frequency, y * n->m_frequency, z * n->m_frequency);
}

__device__ float singlePerlinFractalFBM(FastNoise* n, float x, float y, float z) {
    int seed = n->m_seed;
    float sum = singlePerlin(n, seed, x, y, z);
    float amp = 1;

    for (int i = 1; i < n->m_octaves; i++) {
        x *= n->m_lacunarity;
        y *= n->m_lacunarity;
        z *= n->m_lacunarity;

        amp *= n->m_gain;
        sum += singlePerlin(n, ++seed, x, y, z) * amp;
    }

    return sum * n->m_fractalBounding;
}

__device__ float singlePerlinFractalBillow(FastNoise* n, float x, float y, float z) {
    int seed = n->m_seed;
    float sum = fabsf(singlePerlin(n, seed, x, y, z)) * 2 - 1;
    float amp = 1;

    for (int i = 1; i < n->m_octaves; i++) {
        x *= n->m_lacunarity;
        y *= n->m_lacunarity;
        z *= n->m_lacunarity;

        amp *= n->m_gain;
        sum += (fabsf(singlePerlin(n, ++seed, x, y, z)) * 2 - 1) * amp;
    }

    return sum * n->m_fractalBounding;
}

__device__ float singlePerlinFractalRigidMulti(FastNoise* n, float x, float y, float z) {
    int seed = n->m_seed;
    float sum = 1 - fabsf(singlePerlin(n, seed, x, y, z));
    float amp = 1;

    for (int i = 1; i < n->m_octaves; i++) {
        x *= n->m_lacunarity;
        y *= n->m_lacunarity;
        z *= n->m_lacunarity;

        amp *= n->m_gain;
        sum -= (1 - fabsf(singlePerlin(n, ++seed, x, y, z))) * amp;
    }

    return sum;
}

__device__ float getPerlinFractal(FastNoise* n, float x, float y, float z) {
    x *= n->m_frequency;
    y *= n->m_frequency;
    z *= n->m_frequency;

    switch (n->m_fractalType) {
        case FBM:
            return singlePerlinFractalFBM(n, x, y, z);
        case Billow:
            return singlePerlinFractalBillow(n, x, y, z);
        case RigidMulti:
            return singlePerlinFractalRigidMulti(n, x, y, z);
        default:
            return 0;
    }
}
#endif // ADD_FEATURE_PERLIN_NOISE

// Simplex Noise
#ifdef ADD_FEATURE_SIMPLEX_NOISE
__device__ __constant__ float F3 = (float) (1.0 / 3.0);
__device__ __constant__ float G3 = (float) (1.0 / 6.0);
__device__ __constant__ float G33 =(float) ((1.0 / 6.0) * 3 - 1);
#endif // ADD_FEATURE_SIMPLEX_NOISE

#ifdef ADD_FEATURE_SIMPLEX_NOISE
__device__ float singleSimplex(int seed, float x, float y, float z) {
    float t = (x + y + z) * F3;
    int i = fastFloor(x + t);
    int j = fastFloor(y + t);
    int k = fastFloor(z + t);

    t = (i + j + k) * G3;
    float x0 = x - (i - t);
    float y0 = y - (j - t);
    float z0 = z - (k - t);

    int i1, j1, k1;
    int i2, j2, k2;

    if (x0 >= y0) {
        if (y0 >= z0) {
            i1 = 1;
            j1 = 0;
            k1 = 0;
            i2 = 1;
            j2 = 1;
            k2 = 0;
        } else if (x0 >= z0) {
            i1 = 1;
            j1 = 0;
            k1 = 0;
            i2 = 1;
            j2 = 0;
            k2 = 1;
        } else // x0 < z0
        {
            i1 = 0;
            j1 = 0;
            k1 = 1;
            i2 = 1;
            j2 = 0;
            k2 = 1;
        }
    } else // x0 < y0
    {
        if (y0 < z0) {
            i1 = 0;
            j1 = 0;
            k1 = 1;
            i2 = 0;
            j2 = 1;
            k2 = 1;
        } else if (x0 < z0) {
            i1 = 0;
            j1 = 1;
            k1 = 0;
            i2 = 0;
            j2 = 1;
            k2 = 1;
        } else // x0 >= z0
        {
            i1 = 0;
            j1 = 1;
            k1 = 0;
            i2 = 1;
            j2 = 1;
            k2 = 0;
        }
    }

    float x1 = x0 - i1 + G3;
    float y1 = y0 - j1 + G3;
    float z1 = z0 - k1 + G3;
    float x2 = x0 - i2 + F3;
    float y2 = y0 - j2 + F3;
    float z2 = z0 - k2 + F3;
    float x3 = x0 + G33;
    float y3 = y0 + G33;
    float z3 = z0 + G33;

    float n0, n1, n2, n3;

    t = (float) 0.6 - x0 * x0 - y0 * y0 - z0 * z0;
    if (t < 0) n0 = 0;
    else {
        t *= t;
        n0 = t * t * gradCoord3D(seed, i, j, k, x0, y0, z0);
    }

    t = (float) 0.6 - x1 * x1 - y1 * y1 - z1 * z1;
    if (t < 0) n1 = 0;
    else {
        t *= t;
        n1 = t * t * gradCoord3D(seed, i + i1, j + j1, k + k1, x1, y1, z1);
    }

    t = (float) 0.6 - x2 * x2 - y2 * y2 - z2 * z2;
    if (t < 0) n2 = 0;
    else {
        t *= t;
        n2 = t * t * gradCoord3D(seed, i + i2, j + j2, k + k2, x2, y2, z2);
    }

    t = (float) 0.6 - x3 * x3 - y3 * y3 - z3 * z3;
    if (t < 0) n3 = 0;
    else {
        t *= t;
        n3 = t * t * gradCoord3D(seed, i + 1, j + 1, k + 1, x3, y3, z3);
    }

    return 32 * (n0 + n1 + n2 + n3);
}

__device__ float getSimplex(FastNoise* n, float x, float y, float z) {
    return singleSimplex(n->m_seed, x * n->m_frequency, y * n->m_frequency, z * n->m_frequency);
}

__device__ float singleSimplexFractalFBM(FastNoise* n, float x, float y, float z) {
    int seed = n->m_seed;
    float sum = singleSimplex(seed, x, y, z);
    float amp = 1;

    for (int i = 1; i < n->m_octaves; i++) {
        x *= n->m_lacunarity;
        y *= n->m_lacunarity;
        z *= n->m_lacunarity;

        amp *= n->m_gain;
        sum += singleSimplex(++seed, x, y, z) * amp;
    }

    return sum * n->m_fractalBounding;
}

__device__ float singleSimplexFractalBillow(FastNoise* n, float x, float y, float z) {
    int seed = n->m_seed;
    float sum = fabsf(singleSimplex(seed, x, y, z)) * 2 - 1;
    float amp = 1;

    for (int i = 1; i < n->m_octaves; i++) {
        x *= n->m_lacunarity;
        y *= n->m_lacunarity;
        z *= n->m_lacunarity;

        amp *= n->m_gain;
        sum += (fabsf(singleSimplex(++seed, x, y, z)) * 2 - 1) * amp;
    }

    return sum * n->m_fractalBounding;
}

__device__ float singleSimplexFractalRigidMulti(FastNoise* n, float x, float y, float z) {
    int seed = n->m_seed;
    float sum = 1 - fabsf(singleSimplex(seed, x, y, z));
    float amp = 1;

    for (int i = 1; i < n->m_octaves; i++) {
        x *= n->m_lacunarity;
        y *= n->m_lacunarity;
        z *= n->m_lacunarity;

        amp *= n->m_gain;
        sum -= (1 - fabsf(singleSimplex(++seed, x, y, z))) * amp;
    }

    return sum;
}

__device__ float getSimplexFractal(FastNoise* n, float x, float y, float z) {
    x *= n->m_frequency;
    y *= n->m_frequency;
    z *= n->m_frequency;

    switch (n->m_fractalType) {
        case FBM:
            return singleSimplexFractalFBM(n, x, y, z);
        case Billow:
            return singleSimplexFractalBillow(n, x, y, z);
        case RigidMulti:
            return singleSimplexFractalRigidMulti(n, x, y, z);
        default:
            return 0;
    }
}
#endif // ADD_FEATURE_SIMPLEX_NOISE

#ifdef ADD_FEATURE_SIMPLEX_NOISE
__device__ __constant__ float F2 = (float) (1.0 / 2.0);
__device__ __constant__ float G2 = (float) (1.0 / 4.0);
#endif

// Cubic Noise
#ifdef ADD_FEATURE_CUBIC_NOISE
__device__ __constant__ float CUBIC_3D_BOUNDING = 1 / (float) (1.5 * 1.5 * 1.5);

__device__ float singleCubic(FastNoise* n, int seed, float x, float y, float z) {
    int x1 = fastFloor(x);
    int y1 = fastFloor(y);
    int z1 = fastFloor(z);

    int x0 = x1 - 1;
    int y0 = y1 - 1;
    int z0 = z1 - 1;
    int x2 = x1 + 1;
    int y2 = y1 + 1;
    int z2 = z1 + 1;
    int x3 = x1 + 2;
    int y3 = y1 + 2;
    int z3 = z1 + 2;

    float xs = x - (float) x1;
    float ys = y - (float) y1;
    float zs = z - (float) z1;

    return cubicLerp(
        cubicLerp(
            cubicLerp(valCoord3D(seed, x0, y0, z0), valCoord3D(seed, x1, y0, z0), valCoord3D(seed, x2, y0, z0), valCoord3D(seed, x3, y0, z0), xs),
            cubicLerp(valCoord3D(seed, x0, y1, z0), valCoord3D(seed, x1, y1, z0), valCoord3D(seed, x2, y1, z0), valCoord3D(seed, x3, y1, z0), xs),
            cubicLerp(valCoord3D(seed, x0, y2, z0), valCoord3D(seed, x1, y2, z0), valCoord3D(seed, x2, y2, z0), valCoord3D(seed, x3, y2, z0), xs),
            cubicLerp(valCoord3D(seed, x0, y3, z0), valCoord3D(seed, x1, y3, z0), valCoord3D(seed, x2, y3, z0), valCoord3D(seed, x3, y3, z0), xs),
            ys),
        cubicLerp(
            cubicLerp(valCoord3D(seed, x0, y0, z1), valCoord3D(seed, x1, y0, z1), valCoord3D(seed, x2, y0, z1), valCoord3D(seed, x3, y0, z1), xs),
            cubicLerp(valCoord3D(seed, x0, y1, z1), valCoord3D(seed, x1, y1, z1), valCoord3D(seed, x2, y1, z1), valCoord3D(seed, x3, y1, z1), xs),
            cubicLerp(valCoord3D(seed, x0, y2, z1), valCoord3D(seed, x1, y2, z1), valCoord3D(seed, x2, y2, z1), valCoord3D(seed, x3, y2, z1), xs),
            cubicLerp(valCoord3D(seed, x0, y3, z1), valCoord3D(seed, x1, y3, z1), valCoord3D(seed, x2, y3, z1), valCoord3D(seed, x3, y3, z1), xs),
            ys),
        cubicLerp(
            cubicLerp(valCoord3D(seed, x0, y0, z2), valCoord3D(seed, x1, y0, z2), valCoord3D(seed, x2, y0, z2), valCoord3D(seed, x3, y0, z2), xs),
            cubicLerp(valCoord3D(seed, x0, y1, z2), valCoord3D(seed, x1, y1, z2), valCoord3D(seed, x2, y1, z2), valCoord3D(seed, x3, y1, z2), xs),
            cubicLerp(valCoord3D(seed, x0, y2, z2), valCoord3D(seed, x1, y2, z2), valCoord3D(seed, x2, y2, z2), valCoord3D(seed, x3, y2, z2), xs),
            cubicLerp(valCoord3D(seed, x0, y3, z2), valCoord3D(seed, x1, y3, z2), valCoord3D(seed, x2, y3, z2), valCoord3D(seed, x3, y3, z2), xs),
            ys),
        cubicLerp(
            cubicLerp(valCoord3D(seed, x0, y0, z3), valCoord3D(seed, x1, y0, z3), valCoord3D(seed, x2, y0, z3), valCoord3D(seed, x3, y0, z3), xs),
            cubicLerp(valCoord3D(seed, x0, y1, z3), valCoord3D(seed, x1, y1, z3), valCoord3D(seed, x2, y1, z3), valCoord3D(seed, x3, y1, z3), xs),
            cubicLerp(valCoord3D(seed, x0, y2, z3), valCoord3D(seed, x1, y2, z3), valCoord3D(seed, x2, y2, z3), valCoord3D(seed, x3, y2, z3), xs),
            cubicLerp(valCoord3D(seed, x0, y3, z3), valCoord3D(seed, x1, y3, z3), valCoord3D(seed, x2, y3, z3), valCoord3D(seed, x3, y3, z3), xs),
            ys),
        zs) * CUBIC_3D_BOUNDING;
}


__device__ float getCubic(FastNoise* n, float x, float y, float z) {
    return singleCubic(n, n->m_seed, x * n->m_frequency, y * n->m_frequency, z * n->m_frequency);
}

__device__ float singleCubicFractalFBM(FastNoise* n, float x, float y, float z) {
    int seed = n->m_seed;
    float sum = singleCubic(n, seed, x, y, z);
    float amp = 1;
    int i = 0;

    while (++i < n->m_octaves) {
        x *= n->m_lacunarity;
        y *= n->m_lacunarity;
        z *= n->m_lacunarity;

        amp *= n->m_gain;
        sum += singleCubic(n, ++seed, x, y, z) * amp;
    }

    return sum * n->m_fractalBounding;
}

__device__ float singleCubicFractalBillow(FastNoise* n, float x, float y, float z) {
    int seed = n->m_seed;
    float sum = fabsf(singleCubic(n, seed, x, y, z)) * 2 - 1;
    float amp = 1;
    int i = 0;

    while (++i < n->m_octaves) {
        x *= n->m_lacunarity;
        y *= n->m_lacunarity;
        z *= n->m_lacunarity;

        amp *= n->m_gain;
        sum += (fabsf(singleCubic(n, ++seed, x, y, z)) * 2 - 1) * amp;
    }

    return sum * n->m_fractalBounding;
}

__device__ float singleCubicFractalRigidMulti(FastNoise* n, float x, float y, float z) {
    int seed = n->m_seed;
    float sum = 1 - fabsf(singleCubic(n, seed, x, y, z));
    float amp = 1;
    int i = 0;

    while (++i < n->m_octaves) {
        x *= n->m_lacunarity;
        y *= n->m_lacunarity;
        z *= n->m_lacunarity;

        amp *= n->m_gain;
        sum -= (1 - fabsf(singleCubic(n, ++seed, x, y, z))) * amp;
    }

    return sum;
}

__device__ float getCubicFractal(FastNoise* n, float x, float y, float z) {
    x *= n->m_frequency;
    y *= n->m_frequency;
    z *= n->m_frequency;

    switch (n->m_fractalType) {
        case FBM:
            return singleCubicFractalFBM(n, x, y, z);
        case Billow:
            return singleCubicFractalBillow(n, x, y, z);
        case RigidMulti:
            return singleCubicFractalRigidMulti(n, x, y, z);
        default:
            return 0;
    }
}
#endif // ADD_FEATURE_CUBIC_NOISE

// Cellular Noise
#ifdef ADD_FEATURE_CELLULAR_NOISE
__device__ __constant__ float CELL_3D_x[] =  {
    0.1453787434f, -0.01242829687f, 0.2877979582f, -0.07732986802f, 0.1107205875f, 0.2755209141f, 0.294168941f, 0.4000921098f,
    -0.1697304074f, -0.1483224484f, 0.2623596946f, -0.2709003183f, -0.03516550699f, -0.1267712655f, 0.02952021915f, -0.2806854217f,
    -0.171159547f, 0.2113227183f, -0.1024352839f, -0.3304249877f, 0.2091111325f, 0.344678154f, 0.1984478035f, -0.2929008603f,
    -0.1617332831f, -0.3582060271f, -0.1852067326f, 0.3046301062f, -0.03816768434f, -0.4084952196f, -0.02687443361f, -0.03801098351f,
    0.2371120802f, 0.4447660503f, 0.01985147278f, 0.4274339143f, -0.2072988631f, -0.3791240978f, -0.2098721267f, 0.01582798878f,
    -0.1888129464f, 0.1612988974f, -0.08974491322f, 0.07041229526f, -0.1082925611f, 0.2474100658f, -0.1068836661f, 0.2396452163f,
    -0.3063886072f, 0.1593342891f, 0.2709690528f, -0.1519780427f, 0.1699773681f, -0.1986155616f, -0.1887482106f, 0.2659103394f,
    -0.08838976154f, -0.04201869311f, -0.3230334656f, 0.2612720941f, 0.385713046f, 0.07654967953f, 0.4317038818f, -0.2890436293f,
    -0.2201947582f, 0.4161322773f, 0.2204718095f, -0.1040307469f, -0.1432122615f, 0.3978380468f, -0.2599274663f, 0.4032618332f,
    -0.08953470255f, 0.118937202f, 0.02167047076f, -0.3411343612f, 0.3162964612f, 0.2355138889f, -0.02874541518f, -0.2461455173f,
    0.04208029445f, 0.2727458746f, -0.1347522818f, 0.3829624424f, -0.3547613644f, 0.2305790207f, -0.08323845599f, 0.2993663085f,
    -0.2154865723f, 0.01683355354f, 0.05240429123f, 0.00940104872f, 0.3465688735f, -0.3706867948f, 0.2741169781f, 0.06413433865f,
    -0.388187972f, 0.06419469312f, -0.1986120739f, -0.203203009f, -0.1389736354f, -0.06555641638f, -0.2529246486f, 0.1444476522f,
    -0.3643780054f, 0.4286142488f, 0.165872923f, 0.2219610524f, 0.04322940318f, -0.08481269795f, 0.1822082075f, -0.3269323334f,
    -0.4080485344f, 0.2676025294f, 0.3024892441f, 0.1448494052f, 0.4198402157f, -0.3008872161f, 0.3639310428f, 0.3295806598f,
    0.2776259487f, 0.4149000507f, 0.145016715f, 0.09299023471f, 0.1028907093f, 0.2683057049f, -0.4227307273f, -0.1781224702f,
    0.4390788626f, 0.2972583585f, -0.1707002821f, 0.3806686614f, -0.1751445661f, -0.2227237566f, 0.1369633021f, -0.3529503428f,
    -0.2590744185f, -0.3784019401f, -0.05635805671f, 0.3251428613f, -0.4190995804f, -0.3253150961f, 0.2857945863f, -0.2733604046f,
    0.219003657f, 0.3182767252f, -0.03222023115f, -0.3087780231f, -0.06487611647f, 0.3921171432f, -0.1606404506f, -0.03767771199f,
    0.1394866832f, -0.4345093872f, -0.1044637494f, 0.2658727501f, 0.2051461999f, -0.266085566f, 0.07849405464f, -0.2160686338f,
    -0.185779186f, 0.02492421743f, -0.120167831f, -0.02160084693f, 0.2597670064f, -0.1611553854f, -0.3278896792f, 0.2822734956f,
    0.03169341113f, 0.2202613604f, 0.2933396046f, -0.3194922995f, -0.3441586045f, 0.2703645948f, 0.2298568861f, 0.09326603877f,
    -0.1116165319f, 0.2172907365f, 0.1991339479f, -0.0541918155f, 0.08871336998f, 0.2787673278f, -0.322166438f, -0.4277366384f,
    0.240131882f, 0.1448607981f, -0.3837065682f, -0.4382627882f, -0.37728353f, 0.1259579313f, -0.1406285511f, -0.1580694418f,
    0.2477612106f, 0.2916132853f, 0.07365265219f, -0.26126526f, -0.3721862032f, -0.3691191571f, 0.2278441737f, 0.363398169f,
    -0.304231482f, -0.3199312232f, 0.2874852279f, -0.1451096801f, 0.3220090754f, -0.1247400865f, -0.2829555867f, 0.1069384374f,
    -0.1420661144f, -0.250548338f, 0.3265787872f, 0.07646097258f, 0.3451771584f, 0.298137964f, 0.2812250376f, 0.4390345476f,
    0.2148373234f, 0.2595421179f, 0.3182823114f, -0.4089859285f, -0.2826749061f, 0.3483864637f, -0.3226415069f, 0.4330734858f,
    -0.08717822568f, -0.2149678299f, -0.2687330705f, 0.2105665099f, 0.4361845915f, 0.05333333359f, -0.05986216652f, 0.3664988455f,
    -0.2341015558f, -0.04730947785f, -0.2391566239f, -0.1242081035f, 0.2614832715f, -0.2728794681f, 0.007892900508f, -0.01730330376f,
    0.2054835762f, -0.3231994983f, -0.2669545963f, -0.05554372779f, -0.2083935713f, 0.06989323478f, 0.3847566193f, -0.3026215288f,
    0.3450735512f, 0.1814473292f, -0.03855010448f, 0.3533670318f, -0.007945601311f, 0.4063099273f, -0.2016773589f, -0.07527055435f,
};

__device__ __constant__ float CELL_3D_y[] = {
    -0.4149781685f, -0.1457918398f, -0.02606483451f, 0.2377094325f, -0.3552302079f, 0.2640521179f, 0.1526064594f, -0.2034056362f,
    0.3970864695f, -0.3859694688f, -0.2354852944f, 0.3505271138f, 0.3885234328f, 0.1920044036f, 0.4409685861f, -0.266996757f,
    0.2141185563f, 0.3902405947f, 0.2128044156f, -0.1566986703f, 0.3133278055f, -0.1944240454f, -0.3214342325f, 0.2262915116f,
    0.006314769776f, -0.148303178f, -0.3454119342f, 0.1026310383f, -0.2551766358f, 0.1805950793f, -0.2749741471f, 0.3277859044f,
    0.2900386767f, 0.03946930643f, -0.01503183293f, 0.03345994256f, 0.2871414597f, 0.1281177671f, -0.1007087278f, 0.4263894424f,
    -0.3160996813f, -0.1974805082f, 0.229148752f, 0.4150230285f, -0.1586061639f, -0.3309414609f, -0.2701644537f, 0.06803600538f,
    0.2597428179f, -0.3114350249f, 0.1412648683f, 0.3623355133f, 0.3456012883f, 0.3836276443f, -0.2050154888f, 0.3015631259f,
    -0.4288819642f, 0.3099592485f, 0.201549922f, 0.2759854499f, 0.2193460345f, 0.3721732183f, -0.02577753072f, -0.3418179959f,
    0.383023377f, -0.1669634289f, 0.02654238946f, 0.3890079625f, 0.371614387f, -0.06206669342f, 0.2616724959f, -0.1124593585f,
    -0.3048244735f, -0.2875221847f, -0.03284630549f, 0.2500031105f, 0.3082064153f, -0.3439334267f, -0.3955933019f, 0.02020282325f,
    -0.4470439576f, 0.2288471896f, -0.02720848277f, 0.1231931484f, 0.1271702173f, 0.3063895591f, -0.1922245118f, -0.2619918095f,
    0.2706747713f, -0.2680655787f, 0.4335128183f, -0.4472890582f, 0.01141914583f, -0.2551104378f, 0.2139972417f, 0.1708718512f,
    -0.03973280434f, -0.2803682491f, -0.3391173584f, -0.3871641506f, -0.2775901578f, 0.342253257f, -0.2904227915f, 0.1069184044f,
    -0.2447099973f, -0.1358496089f, -0.3136808464f, -0.3658139958f, -0.3832730794f, -0.4404869674f, -0.3953259299f, 0.3036542563f,
    0.04227858267f, -0.01299671652f, -0.1009990293f, 0.425921681f, 0.08062320474f, -0.333040905f, -0.1291284382f, 0.0184175994f,
    -0.2974929052f, -0.144793182f, -0.0398992945f, -0.299732164f, -0.361266869f, -0.07076041213f, -0.07933161816f, 0.1806857196f,
    -0.02841848598f, 0.2382799621f, 0.2215845691f, 0.1471852559f, -0.274887877f, -0.2316778837f, 0.1341343041f, -0.2472893463f,
    -0.2985577559f, 0.2199816631f, 0.1485737441f, 0.09666046873f, 0.1406751354f, -0.3080335042f, -0.05796152095f, 0.1973770973f,
    0.2410037886f, -0.271342949f, -0.3331161506f, 0.1992794134f, -0.4311322747f, -0.06294284106f, -0.358928121f, -0.2290351443f,
    -0.3602213994f, 0.005751117145f, 0.4168128432f, 0.2551943237f, 0.1975390727f, 0.23483312f, -0.3300346342f, 0.05376451292f,
    0.2148499206f, -0.3229954284f, 0.4017266681f, -0.06885389554f, 0.3096300784f, -0.09823036005f, 0.1461670309f, 0.03754421121f,
    0.347405252f, -0.3460788041f, 0.3031973659f, 0.2453752201f, -0.1698856132f, -0.3574277231f, 0.3744156221f, -0.3170108894f,
    -0.2985018719f, -0.3460005203f, 0.3820341668f, -0.2103145071f, 0.2012117383f, 0.3505404674f, 0.3067213525f, 0.132066775f,
    -0.1612516055f, -0.2387819045f, -0.2206398454f, -0.09082753406f, 0.05445141085f, 0.348394558f, -0.270877371f, 0.4162931958f,
    -0.2927867412f, 0.3312535401f, -0.1666159848f, -0.2422237692f, 0.252790166f, -0.255281188f, -0.3358364886f, -0.2310190248f,
    -0.2698452035f, 0.316332536f, 0.1642275508f, 0.3277541114f, 0.0511344108f, -0.04333605335f, -0.3056190617f, 0.3491024667f,
    -0.3055376754f, 0.3156466809f, 0.1871229129f, -0.3026690852f, 0.2757120714f, 0.2852657134f, 0.3466716415f, -0.09790429955f,
    0.1850172527f, -0.07946825393f, -0.307355516f, -0.04647718411f, 0.07417482322f, 0.225442246f, -0.1420585388f, -0.118868561f,
    -0.3909896417f, 0.3939973956f, 0.322686276f, -0.1961317136f, -0.1105517485f, -0.313639498f, 0.1361029153f, 0.2550543014f,
    -0.182405731f, -0.4222150243f, -0.2577696514f, 0.4256953395f, -0.3650179274f, -0.3499628774f, -0.1672771315f, 0.2978486637f,
    -0.3252600376f, 0.1564282844f, 0.2599343665f, 0.3170813944f, -0.310922837f, -0.3156141536f, -0.1605309138f, -0.3001537679f,
    0.08611519592f, -0.2788782453f, 0.09795110726f, 0.2665752752f, 0.140359426f, -0.1491768253f, 0.008816271194f, -0.425643481f,
};

__device__ __constant__ float CELL_3D_z[] = {
    -0.0956981749f, -0.4255470325f, -0.3449535616f, 0.3741848704f, -0.2530858567f, -0.238463215f, 0.3044271714f, 0.03244149937f,
    -0.1265461359f, 0.1775613147f, 0.2796677792f, -0.07901746678f, 0.2243054374f, 0.3867342179f, 0.08470692262f, 0.2289725438f,
    0.3568720405f, -0.07453178509f, -0.3830421561f, 0.2622305365f, -0.2461670583f, -0.2142341261f, -0.2445373252f, 0.2559320961f,
    -0.4198838754f, -0.2284613961f, -0.2211087107f, 0.314908508f, -0.3686842991f, 0.05492788837f, 0.3551999201f, 0.3059600725f,
    -0.2493099024f, 0.05590469027f, -0.4493105419f, -0.1366772882f, -0.2776273824f, 0.2057929936f, -0.3851122467f, 0.1429738373f,
    -0.2587096108f, -0.3707885038f, -0.3767448739f, -0.1590534329f, 0.4069604477f, 0.1782302128f, -0.3436379634f, -0.3747549496f,
    0.2028785103f, -0.2830561951f, -0.3303331794f, 0.2193527988f, 0.2327390037f, -0.1260225743f, -0.353330953f, -0.2021172246f,
    -0.1036702021f, 0.3235115047f, -0.2398478873f, -0.2409749453f, 0.07491837764f, 0.241095919f, 0.1243675091f, -0.04598084447f,
    -0.08548310451f, -0.03817251927f, -0.391391981f, -0.2008741118f, -0.2095065525f, 0.2009293758f, -0.2578084893f, 0.1650235939f,
    0.3186935478f, 0.325092195f, -0.4482761547f, 0.1537068389f, -0.08640228117f, -0.1695376245f, 0.2125550295f, -0.3761704803f,
    0.02968078139f, -0.2752065618f, -0.4284874806f, -0.2016512234f, 0.2459107769f, 0.2354968222f, 0.3982726409f, -0.2103333191f,
    0.287751117f, -0.3610505186f, -0.1087217856f, 0.04841609928f, -0.2868093776f, 0.003156692623f, -0.2855959784f, 0.4113266307f,
    -0.2241236325f, 0.3460819069f, 0.2192091725f, 0.1063600375f, -0.3257760473f, -0.2847192729f, 0.2327739768f, 0.4125570634f,
    -0.09922543227f, -0.01829506817f, -0.2767498872f, 0.1393320198f, 0.2318037215f, -0.03574965489f, 0.1140946023f, 0.05838957105f,
    -0.184956522f, 0.36155217f, -0.3174892964f, -0.0104580805f, 0.1404780841f, -0.03241355801f, -0.2310412139f, -0.3058388149f,
    -0.1921504723f, -0.09691688386f, 0.4241205002f, -0.3225111565f, 0.247789732f, -0.3542668666f, -0.1323073187f, -0.3716517945f,
    -0.09435116353f, -0.2394997452f, 0.3525077196f, -0.1895464869f, 0.3102596268f, 0.3149912482f, -0.4071228836f, -0.129514612f,
    -0.2150435121f, -0.1044989934f, 0.4210102279f, -0.2957006485f, -0.08405978803f, -0.04225456877f, 0.3427271751f, -0.2980207554f,
    -0.3105713639f, 0.1660509868f, -0.300824678f, -0.2596995338f, 0.1114273361f, -0.2116183942f, -0.2187812825f, 0.3855169162f,
    0.2308332918f, 0.1169124335f, -0.1336202785f, 0.2582393035f, 0.3484154868f, 0.2766800993f, -0.2956616708f, -0.3910546287f,
    0.3490352499f, -0.3123343347f, 0.1633259825f, 0.4441762538f, 0.1978643903f, 0.4085091653f, 0.2713366126f, -0.3484423997f,
    -0.2842624114f, -0.1849713341f, 0.1565989581f, -0.200538455f, -0.2349334659f, 0.04060059933f, 0.0973588921f, 0.3054595587f,
    0.3177080142f, -0.1885958001f, -0.1299829458f, 0.39412061f, 0.3926114802f, 0.04370535101f, 0.06804996813f, 0.04582286686f,
    0.344723946f, 0.3528435224f, 0.08116235683f, -0.04664855374f, 0.2391488697f, 0.2554522098f, -0.3306796947f, -0.06491553533f,
    -0.2353514536f, 0.08793624968f, 0.411478311f, 0.2748965434f, 0.008634938242f, 0.03290232422f, 0.1944244981f, 0.1306597909f,
    0.1926830856f, -0.008816977938f, -0.304764754f, -0.2720669462f, 0.3101538769f, -0.4301882115f, -0.1703910946f, -0.2630430352f,
    -0.2982682484f, -0.2002316239f, 0.2466400438f, 0.324106687f, -0.0856480183f, 0.179547284f, 0.05684409612f, -0.01278335452f,
    0.3494474791f, 0.3589187731f, -0.08203022006f, 0.1818526372f, 0.3421885344f, -0.1740766085f, -0.2796816575f, -0.02859407492f,
    -0.2050050172f, -0.03247898316f, -0.1617284888f, -0.3459683451f, 0.004616608544f, -0.3182543336f, -0.4247264031f, -0.05590974511f,
    0.3382670703f, -0.1483114513f, -0.2808182972f, -0.07652336246f, 0.02980623099f, 0.07458404908f, 0.4176793787f, -0.3368779738f,
    -0.2334146693f, -0.2712420987f, -0.2523278991f, -0.3144428146f, -0.2497981362f, 0.3130537363f, -0.1693876312f, -0.1443188342f,
    0.2756962409f, -0.3029914042f, 0.4375151083f, 0.08105160988f, -0.4274764309f, -0.1231199324f, -0.4021797064f, -0.1251477955f,
};

#define MAX_CELL_POS 32000
#define MIN_CELL_POS -32000

__device__ float singleCellular(FastNoise* n, float x, float y, float z) {
    int xr = max(min(fastRound(x), MAX_CELL_POS), MIN_CELL_POS);
    int yr = max(min(fastRound(y), MAX_CELL_POS), MIN_CELL_POS);
    int zr = max(min(fastRound(z), MAX_CELL_POS), MIN_CELL_POS);

    float distance = 999999;
    int xc = 0, yc = 0, zc = 0;

    switch (n->m_cellularDistanceFunction) {
        case Euclidean:
            for (int xi = xr - 1; xi <= xr + 1; xi++) {
                for (int yi = yr - 1; yi <= yr + 1; yi++) {
                    for (int zi = zr - 1; zi <= zr + 1; zi++) {
                        int idx = hash3D(n->m_seed, xi, yi, zi) & 255;


                        float vecX = xi - x + CELL_3D_x[idx];
                        float vecY = yi - y + CELL_3D_y[idx];
                        float vecZ = zi - z + CELL_3D_z[idx];

                        float newDistance = vecX * vecX + vecY * vecY + vecZ * vecZ;

                        if (newDistance < distance) {
                            distance = newDistance;
                            xc = xi;
                            yc = yi;
                            zc = zi;
                        }
                    }
                }
            }
            break;
        case Manhattan:
            for (int xi = xr - 1; xi <= xr + 1; xi++) {
                for (int yi = yr - 1; yi <= yr + 1; yi++) {
                    for (int zi = zr - 1; zi <= zr + 1; zi++) {
                        int idx = hash3D(n->m_seed, xi, yi, zi) & 255;

                        float vecX = xi - x + CELL_3D_x[idx];
                        float vecY = yi - y + CELL_3D_y[idx];
                        float vecZ = zi - z + CELL_3D_z[idx];

                        float newDistance = fabsf(vecX) + fabsf(vecY) + fabsf(vecZ);

                        if (newDistance < distance) {
                            distance = newDistance;
                            xc = xi;
                            yc = yi;
                            zc = zi;
                        }
                    }
                }
            }
            break;
        case Natural:
            for (int xi = xr - 1; xi <= xr + 1; xi++) {
                for (int yi = yr - 1; yi <= yr + 1; yi++) {
                    for (int zi = zr - 1; zi <= zr + 1; zi++) {
                        int idx = hash3D(n->m_seed, xi, yi, zi) & 255;

                        float vecX = xi - x + CELL_3D_x[idx];
                        float vecY = yi - y + CELL_3D_y[idx];
                        float vecZ = zi - z + CELL_3D_z[idx];

                        float newDistance = (fabsf(vecX) + fabsf(vecY) + fabsf(vecZ)) + (vecX * vecX + vecY * vecY + vecZ * vecZ);

                        if (newDistance < distance) {
                            distance = newDistance;
                            xc = xi;
                            yc = yi;
                            zc = zi;
                        }
                    }
                }
            }
            break;
    }

    switch (n->m_cellularReturnType) {
        case CellValue:
            return valCoord3D(0, xc, yc, zc);
        case Distance:
            return distance - 1;
        default:
            return 0;
    }
}

__device__ float singleCellular2Edge(FastNoise* n, float x, float y, float z) {
    int xr = max(min(fastRound(x), MAX_CELL_POS), MIN_CELL_POS);
    int yr = max(min(fastRound(y), MAX_CELL_POS), MIN_CELL_POS);
    int zr = max(min(fastRound(z), MAX_CELL_POS), MIN_CELL_POS);

    float distance = 999999;
    float distance2 = 999999;

    switch (n->m_cellularDistanceFunction) {
        case Euclidean:
            for (int xi = xr - 1; xi <= xr + 1; xi++) {
                for (int yi = yr - 1; yi <= yr + 1; yi++) {
                    for (int zi = zr - 1; zi <= zr + 1; zi++) {
                       int idx = hash3D(n->m_seed, xi, yi, zi) & 255;
                        float vecX = xi - x + CELL_3D_x[idx];
                        float vecY = yi - y + CELL_3D_y[idx];
                        float vecZ = zi - z + CELL_3D_z[idx];

                        float newDistance = vecX * vecX + vecY * vecY + vecZ * vecZ;

                        distance2 = fmaxf(fminf(distance2, newDistance), distance);
                        distance = fminf(distance, newDistance);
                    }
                }
            }
            break;
        case Manhattan:
            for (int xi = xr - 1; xi <= xr + 1; xi++) {
                for (int yi = yr - 1; yi <= yr + 1; yi++) {
                    for (int zi = zr - 1; zi <= zr + 1; zi++) {
                        int idx = hash3D(n->m_seed, xi, yi, zi) & 255;

                        float vecX = xi - x + CELL_3D_x[idx];
                        float vecY = yi - y + CELL_3D_y[idx];
                        float vecZ = zi - z + CELL_3D_z[idx];

                        float newDistance = fabsf(vecX) + fabsf(vecY) + fabsf(vecZ);

                        distance2 = fmaxf(fminf(distance2, newDistance), distance);
                        distance = fminf(distance, newDistance);
                    }
                }
            }
            break;
        case Natural:
            for (int xi = xr - 1; xi <= xr + 1; xi++) {
                for (int yi = yr - 1; yi <= yr + 1; yi++) {
                    for (int zi = zr - 1; zi <= zr + 1; zi++) {
                        int idx = hash3D(n->m_seed, xi, yi, zi) & 255;

                        float vecX = xi - x + CELL_3D_x[idx];
                        float vecY = yi - y + CELL_3D_y[idx];
                        float vecZ = zi - z + CELL_3D_z[idx];

                        float newDistance = (fabsf(vecX) + fabsf(vecY) + fabsf(vecZ)) + (vecX * vecX + vecY * vecY + vecZ * vecZ);

                        distance2 = fmaxf(fminf(distance2, newDistance), distance);
                        distance = fminf(distance, newDistance);
                    }
                }
            }
            break;
        default:
            break;
    }

    switch (n->m_cellularReturnType) {
        case Distance2:
            return distance2 - 1;
        case Distance2Add:
            return distance2 + distance - 1;
        case Distance2Sub:
            return distance2 - distance - 1;
        case Distance2Mul:
            return distance2 * distance - 1;
        case Distance2Div:
            return distance / distance2 - 1;
        default:
            return 0;
    }
}

__device__ float getCellular(FastNoise* n, float x, float y, float z) {
    x *= n->m_frequency;
    y *= n->m_frequency;
    z *= n->m_frequency;

    switch (n->m_cellularReturnType) {
        case CellValue:
        case Distance:
            return singleCellular(n, x, y, z);
        default:
            return singleCellular2Edge(n, x, y, z);
    }
}
#endif // ADD_FEATURE_CELLULAR_NOISE

// MAIN FUNCTION
__device__ float getNoise(FastNoise* n, float x, float y, float z) {
    x *= n->m_frequency;
    y *= n->m_frequency;
    z *= n->m_frequency;

    switch (n->m_noiseType) {
        case Value:
#ifdef ADD_FEATURE_VALUE_NOISE
            return singleValue(n, n->m_seed, x, y, z);
#else
            return 0.f;
#endif
        case ValueFractal:
#ifdef ADD_FEATURE_VALUE_NOISE
            switch (n->m_fractalType) {
                case FBM:
                    return singleValueFractalFBM(n, x, y, z);
                case Billow:
                    return singleValueFractalBillow(n, x, y, z);
                case RigidMulti:
                    return singleValueFractalRigidMulti(n, x, y, z);
                default:
                    return 0;
            }
#else
            return 0.f;
#endif
        case Perlin:
#ifdef ADD_FEATURE_PERLIN_NOISE
            return singlePerlin(n, n->m_seed, x, y, z);
#else
            return 0.f;
#endif
        case PerlinFractal:
#ifdef ADD_FEATURE_PERLIN_NOISE
            switch (n->m_fractalType) {
                case FBM:
                    return singlePerlinFractalFBM(n, x, y, z);
                case Billow:
                    return singlePerlinFractalBillow(n, x, y, z);
                case RigidMulti:
                    return singlePerlinFractalRigidMulti(n, x, y, z);
                default:
                    return 0;
            }
#else
            return 0.f;
#endif
        case Simplex:
#ifdef ADD_FEATURE_SIMPLEX_NOISE
            return singleSimplex(n->m_seed, x, y, z);
#else
            return 0.f;
#endif
        case SimplexFractal:
#ifdef ADD_FEATURE_SIMPLEX_NOISE
            switch (n->m_fractalType) {
                case FBM:
                    return singleSimplexFractalFBM(n, x, y, z);
                case Billow:
                    return singleSimplexFractalBillow(n, x, y, z);
                case RigidMulti:
                    return singleSimplexFractalRigidMulti(n, x, y, z);
                default:
                    return 0;
            }
#else
            return 0.f;
#endif
        case Cellular:
#ifdef ADD_FEATURE_CELLULAR_NOISE
            switch (n->m_cellularReturnType) {
                case CellValue:
                case Distance:
                    return singleCellular(n, x, y, z);
                default:
                    return singleCellular2Edge(n, x, y, z);
            }
#else
            return 0.f;
#endif
        case WhiteNoise:
#ifdef ADD_FEATURE_WHITE_NOISE
            return getWhiteNoise(n, x, y, z);
#else
            return 0.f;
#endif
        case Cubic:
#ifdef ADD_FEATURE_CUBIC_NOISE
            return singleCubic(n, n->m_seed, x, y, z);
#else
            return 0.f;
#endif
        case CubicFractal:
#ifdef ADD_FEATURE_CUBIC_NOISE
            switch (n->m_fractalType) {
                case FBM:
                    return singleCubicFractalFBM(n, x, y, z);
                case Billow:
                    return singleCubicFractalBillow(n, x, y, z);
                case RigidMulti:
                    return singleCubicFractalRigidMulti(n, x, y, z);
                default:
                    return 0;
            }
#else
            return 0.f;
#endif
        default:
            return 0;
    }
}
#endif // ADD_FEATURE_FAST_NOISE

//------------- START of JS CODE--------------------------
// vector operations 2D,3D, 4D

#define vec2 float2
#define vec3 float3
#define vec4 float4

 __device__ float2 operator+(const float2 &a, float d) {
   return make_float2(a.x+d, a.y+d);
 }

 __device__ float2 operator-(const float2 &a, float d) {
   return make_float2(a.x-d, a.y-d);
 }
  __device__ float2 operator*(const float2 &a, float d) {
   return make_float2(a.x*d, a.y*d);
 }
  __device__ float2 operator/(const float2 &a, float d) {
   return make_float2(a.x/d, a.y/d);
 }
 __device__ float2 operator+(const float2 &a, const float2 &b) {
   return make_float2(a.x+b.x, a.y+b.y);
 }
 __device__ float2 operator-(const float2 &a, const float2 &b) {
   return make_float2(a.x-b.x, a.y-b.y);
 }
 __device__ float2 operator*(const float2 &a, const float2 &b) {
   return make_float2(a.x*b.x, a.y*b.y);
 }
  __device__ float2 operator/(const float2 &a, const float2 &b) {
   return make_float2(a.x/b.x, a.y/b.y);
 }
 
  __device__ float3 operator+(const float3 &a, float d) {
   return make_float3(a.x+d, a.y+d, a.z+d);
 }
  __device__ float3 operator-(const float3 &a, float d) {
   return make_float3(a.x-d, a.y-d, a.z-d);
 }
  __device__ float3 operator*(const float3 &a, float d) {
   return make_float3(a.x*d, a.y*d, a.z*d);
 }
  __device__ float3 operator/(const float3 &a, float d) {
   return make_float3(a.x/d, a.y/d, a.z/d);
 }
  
__device__ float3 operator+(const float3 &a, const float3 &b) {
   return make_float3(a.x+b.x, a.y+b.y, a.z+b.z  );
 } 
 __device__ float3 operator-(const float3 &a, const float3 &b) {
   return make_float3(a.x-b.x, a.y-b.y, a.z-b.z  );
 }
  __device__ float3 operator*(const float3 &a, const float3 &b) {
   return make_float3(a.x*b.x, a.y*b.y, a.z*b.z  );
 }
  __device__ float3 operator/(const float3 &a, const float3 &b) {
   return make_float3(a.x/b.x, a.y/b.y, a.z/b.z  );
 }
 
   __device__ float4 operator+(const float4 &a, float d) {
   return make_float4(a.x+d, a.y+d, a.z+d, a.w+d);
 }
   __device__ float4 operator-(const float4 &a, float d) {
   return make_float4(a.x-d, a.y-d, a.z-d, a.w-d);
 }
   __device__ float4 operator*(const float4 &a, float d) {
   return make_float4(a.x*d, a.y*d, a.z*d, a.w*d);
 }
   __device__ float4 operator/(const float4 &a, float d) {
   return make_float4(a.x/d, a.y/d, a.z/d, a.w/d);
 }
 
 __device__ float4 operator+(const float4 &a, const float4 &b) {
   return make_float4(a.x+b.x, a.y+b.y, a.z+b.z, a.w+b.w  );
 } 
 __device__ float4 operator-(const float4 &a, const float4 &b) {
   return make_float4(a.x-b.x, a.y-b.y, a.z-b.z, a.w-b.w  );
 } 
  __device__ float4 operator*(const float4 &a, const float4 &b) {
   return make_float4(a.x*b.x, a.y*b.y, a.z*b.z, a.w*b.w  );
 }
 __device__ float4 operator/(const float4 &a, const float4 &b) {
   return make_float4(a.x/b.x, a.y/b.y, a.z/b.z, a.w/b.w  );
 } 

__device__ float atan(float n, float d)
{
	return atanf(n/d);
}

__device__ float atan2 (float y, float x)
{
	return atan2f(y,x);
}
__device__ float sqrt(float a)
{
	return sqrtf(a);
}

__device__ float2 sqrt(float2 a)
{
	return make_float2(sqrtf(a.x),sqrtf(a.y));
}

__device__ float3 sqrt(float3 a)
{
	return make_float3( sqrtf(a.x),sqrtf(a.y),sqrtf(a.z) );
}
		
__device__ float4 sqrt(float4 a)
{
	return make_float4( sqrtf(a.x),sqrtf(a.y),sqrtf(a.z),sqrtf(a.w) );
}	

__device__ float pow(float x,float y)
{
	return  powf(x,y);
}
		
__device__ float2 pow(float2 x,float2 y)
{
	float xr= powf(x.x,y.x);
	float yr= powf(x.y,y.y);
	return make_float2(xr,yr);
}

__device__ float3 pow(float3 x,float3 y)
{
	float xr= powf(x.x,y.x);
	float yr= powf(x.y,y.y);
	float zr= powf(x.z,y.z);
	return make_float3(xr,yr,zr);
}
		
__device__ float4 pow(float4 x,float4 y)
{
	float xr= powf(x.x,y.x);
	float yr= powf(x.y,y.y);
	float zr= powf(x.z,y.z);
	float wr= powf(x.w,y.w);
	return make_float4(xr,yr,zr,wr);
}

		
__device__ float2 expf(float2 x)
{
	float xr= expf(x.x);
	float yr= expf(x.y);
	return make_float2(xr,yr);
}

__device__ float3 expf(float3 x)
{
	float xr= expf(x.x);
	float yr= expf(x.y);
	float zr= expf(x.z);
	return make_float3(xr,yr,zr);
}
		
__device__ float4 expf(float4 x)
{
	float xr= expf(x.x);
	float yr= expf(x.y);
	float zr= expf(x.z);
	float wr= expf(x.w);
	return make_float4(xr,yr,zr,wr);
}
		

__device__ float2 exp2f(float2 x)
{
 	float xr= powf(2.0,x.x);
 	float yr= powf(2.0,x.y);
 	return make_float2(xr,yr);
}

__device__ float3 exp2f(float3 x)
{
 	float xr= powf(2.0,x.x);
 	float yr= powf(2.0,x.y);
 	float zr= powf(2.0,x.z);
 	return make_float3(xr,yr,zr);
}

__device__ float4 exp2f(float4 x)
{
 	float xr= powf(2.0,x.x);
 	float yr= powf(2.0,x.y);
 	float zr= powf(2.0,x.z);
 	float wr= powf(2.0,x.w);
 	return make_float4(xr,yr,zr,wr);
}

__device__ float log2(float d) {
	return log2f(d);
}

__device__ float sin(float x)
{
    return sinf(x);
}

__device__ float2 sinf(float2 x)
{
	float v1,v2;
	v1=sinf(x.x);
	v2=sinf(x.y);
	return make_float2(v1,v2);
}

__device__ float2 sin(float2 x)
{
	float v1,v2;
	v1=sinf(x.x);
	v2=sinf(x.y);
	return make_float2(v1,v2);
}

__device__ float3 sinf(float3 a)
{
	float v1,v2,v3;
	v1=sinf(a.x);
	v2=sinf(a.y);
	v3=sinf(a.z);
	return make_float3(v1,v2,v3);
}

__device__ float3 sin(float3 a)
{
	float v1,v2,v3;
	v1=sinf(a.x);
	v2=sinf(a.y);
	v3=sinf(a.z);
	return make_float3(v1,v2,v3);
}

__device__ float4 sin(float4 a)
{
	float v1,v2,v3,v4;
	v1=sinf(a.x);
	v2=sinf(a.y);
	v3=sinf(a.z);
	v4=sinf(a.w);
	return make_float4(v1,v2,v3,v4);
}

__device__ float4 sinf(float4 a)
{
	float v1,v2,v3,v4;
	v1=sinf(a.x);
	v2=sinf(a.y);
	v3=sinf(a.z);
	v4=sinf(a.w);
	return make_float4(v1,v2,v3,v4);
}
		
__device__ float cos(float x)
{
	return cosf(x);
}

__device__ float2 cosf(float2 x)
{
	float v1,v2;
	v1=cosf(x.x);
	v2=cosf(x.y);
	return make_float2(v1,v2);
}

__device__ float2 cos(float2 x)
{
	float v1,v2;
	v1=cosf(x.x);
	v2=cosf(x.y);
	return make_float2(v1,v2);
}

__device__ float3 cosf(float3 a)
{
	float v1,v2,v3;
	v1=cosf(a.x);
	v2=cosf(a.y);
	v3=cosf(a.z);
	return make_float3(v1,v2,v3);
}

__device__ float3 cos(float3 a)
{
	float v1,v2,v3;
	v1=cosf(a.x);
	v2=cosf(a.y);
	v3=cosf(a.z);
	return make_float3(v1,v2,v3);
}

__device__ float4 cos(float4 a)
{
	float v1,v2,v3,v4;
	v1=cosf(a.x);
	v2=cosf(a.y);
	v3=cosf(a.z);
	v4=cosf(a.w);
	return make_float4(v1,v2,v3,v4);
}

		
__device__ float length(float2 a)
{
	return sqrtf(a.x*a.x+a.y*a.y);
}

__device__ float length(float3 a)
{
	return sqrtf(a.x*a.x+a.y*a.y+a.z*a.z);
}

__device__ float length(float4 a)
{
	return  sqrtf(a.x*a.x+a.y*a.y+a.z*a.z+ a.w*a.w);
}

__device__ float2 abs(float2 v)
{
	return make_float2(fabsf(v.x),fabsf(v.y));
}

__device__ float3 abs(float3 v)
{
	return make_float3(fabsf(v.x),fabsf(v.y),fabsf(v.z));
}

__device__ float4 abs(float4 v)
{
	return make_float4(fabsf(v.x),fabsf(v.y),fabsf(v.z),fabsf(v.w));
}

__device__ float sign(float v)
{
    float val=0.0;
    if(v>0.0)
	   val=1.0;
	else if(v<0.0)
	   val=-1.0;
	else
	   val=0.0;
	return val;
}

__device__ float2 sign(float2 v)
{
	return make_float2(sign(v.x),sign(v.y));
}
	
	__device__ float3 sign(float3 v)
{
	return make_float3(sign(v.x),sign(v.y),sign(v.z));
}

__device__ float dot(float v1,float v2)
{
	return v1*v2;
}

__device__ float dot(float2 v1,float2 v2)
{
	return v1.x*v2.x + v1.y*v2.y;
}

__device__ float dot(float3 v1,float3 v2)
{
	return v1.x*v2.x + v1.y*v2.y + v1.z*v2.z;
}

__device__ float dot(float4 v1,float4 v2)
{
	return v1.x*v2.x + v1.y*v2.y + v1.z*v2.z + v1.w*v2.w;
}
__device__ float3 cross(float3 x,float3 y)
{
	float x1,y1,z1;
	x1=x.y*y.z -y.y*x.z;
	y1=x.z*y.x-y.z*x.x;
	z1=x.x*y.y-y.x*x.y;
	return make_float3(x1,y1,z1);
}

__device__ float mod(float x,float y)
{
	return x- y*floorf(x/y);
}

__device__ float2 mod(float2 x,float y)
{
	float x1,y1;
	x1=	x.x- y*floorf(x.x/y);
	y1=	x.y- y*floorf(x.y/y);
	return make_float2(x1,y1);
}

__device__ float2 mod(float2 x,float2 y)
{

	float x1,y1;
	x1=	x.x- y.x*floorf(x.x/y.x);
	y1=	x.y- y.y*floorf(x.y/y.y);
	return make_float2(x1,y1);
}

__device__ float3 mod(float3 x,float y)
{
	float x1,y1,z1;
	x1=	x.x- y*floorf(x.x/y);
	y1=	x.y- y*floorf(x.y/y);
	z1=	x.z- y*floorf(x.z/y);
	return make_float3(x1,y1,z1);
}

__device__ float3 mod(float3 x,float3 y)
{
	float x1,y1,z1;
	x1 = 	x.x- y.x*floorf(x.x/y.x);
	y1 = 	x.y- y.y*floorf(x.y/y.y);
	z1 = 	x.z- y.z*floorf(x.z/y.z);
	return make_float3(x1,y1,z1);
}

__device__ float4 mod(float4 x,float y)
{
	float x1,y1,z1,w1;
	x1=	x.x- y*floorf(x.x/y);
	y1=	x.y- y*floorf(x.y/y);
	z1=	x.z- y*floorf(x.z/y);
	w1=	x.w- y*floorf(x.w/y);
	return make_float4(x1,y1,z1,w1);
}
__device__ float step(float lim, float x)
{
    return (x<lim)?0.0f:1.0f;
}

__device__ float2 step(float  lim, float2 x)
{
	float x1,y1;
	x1=(x.x<lim)?0.0f:1.0f;
	y1=(x.y<lim)?0.0f:1.0;
	return make_float2(x1,y1);
}
		
__device__ float2 step(float2 lim, float2 x)
{
	float x1,y1;
	x1=(x.x<lim.x)?0.0f:1.0f;
	y1=(x.y<lim.y)?0.0f:1.0f;
	return make_float2(x1,y1);
}

__device__ float3 step(float lim, float3 x)
{
	float x1,y1,z1;
	x1=(x.x<lim)?0.0f:1.0f;
	y1=(x.y<lim)?0.0f:1.0f;
	z1=(x.z<lim)?0.0f:1.0f;
	return make_float3(x1,y1,z1);
}
		
__device__ float3 step(float3 lim, float3 x)
{
	float x1,y1,z1;
	x1=(x.x<lim.x)?0.0f:1.0f;
	y1=(x.y<lim.y)?0.0f:1.0f;
	z1=(x.z<lim.z)?0.0f:1.0f;
	return make_float3(x1,y1,z1);
}

__device__ float4 step(float4 lim, float4 x)
{
	float x1,y1,z1,w1;
	x1=(x.x<lim.x)?0.0f:1.0f;
	y1=(x.y<lim.y)?0.0f:1.0f;
	z1=(x.z<lim.z)?0.0f:1.0f;
	w1=(x.w<lim.w)?0.0f:1.0f;
	return make_float4(x1,y1,z1,w1);
}

__device__ float ceil(float x)
{
   return ceilf(x);
}

__device__ float2 ceil(float2 v)
{
   return make_float2(ceilf(v.x),ceilf(v.y));
}

__device__ float3 ceil(float3 v)
{
   return make_float3(ceilf(v.x),ceilf(v.y),ceilf(v.z));
}

__device__ float4 ceil(float4 v)
{
   return make_float4(ceilf(v.x),ceilf(v.y),ceilf(v.z),ceilf(v.w));
}

__device__ float2 floorf(float2 v)
{
	return make_float2(floorf(v.x),floorf(v.y));
}

__device__ float3 floorf(float3 v)
{
	return make_float3(floorf(v.x),floorf(v.y),floorf(v.z));
}

__device__ float4 floorf(float4 v)
{
	return make_float4(floorf(v.x),floorf(v.y),floorf(v.z),floorf(v.w));
}

	
		
__device__ float2 truncf(float2 v)
{
	float x1,y1;
	x1=truncf(v.x);
	y1=truncf(v.y);
	return make_float2(x1,y1);
}

__device__ float3 truncf(float3 v)
{
	float x1,y1,z1;
	x1=truncf(v.x);
	y1=truncf(v.y);
	z1=truncf(v.z);
	return make_float3(x1,y1,z1);
}

__device__ float4 truncf(float4 v)
{
	float x1,y1,z1,w1;
	x1=truncf(v.x);
	y1=truncf(v.y);
	z1=truncf(v.z);
	w1=truncf(v.w);
	return make_float4(x1,y1,z1,w1);
}

		
__device__ float2 roundf(float2 v)
{
	float x1,y1;
	x1=roundf(v.x);
	y1=roundf(v.y);
	return make_float2(x1,y1);
}

__device__ float3 roundf(float3 v)
{
	float x1,y1,z1;
	x1=roundf(v.x);
	y1=roundf(v.y);
	z1=roundf(v.z);
	return make_float3(x1,y1,z1);
}

__device__ float4 roundf(float4 v)
{
	float x1,y1,z1,w1;
	x1=roundf(v.x);
	y1=roundf(v.y);
	z1=roundf(v.z);
	w1=roundf(v.w);
	return make_float4(x1,y1,z1,w1);
}
	
		
__device__ float2 ceilf(float2 v)
{
	float x1,y1;
	x1=ceilf(v.x);
	y1=ceilf(v.y);
	return make_float2(x1,y1);
}

__device__ float3 ceilf(float3 v)
{
	float x1,y1,z1;
	x1=ceilf(v.x);
	y1=ceilf(v.y);
	z1=ceilf(v.z);
	return make_float3(x1,y1,z1);
}

__device__ float4 ceilf(float4 v)
{
	float x1,y1,z1,w1;
	x1=ceilf(v.x);
	y1=ceilf(v.y);
	z1=ceilf(v.z);
	w1=ceilf(v.w);
	return make_float4(x1,y1,z1,w1);
}	
__device__ float fract(float x)
{
	return x-floorf(x);
}

__device__ float2 fract(float2 x)
{
	return make_float2(x.x-floorf(x.x),x.y-floorf(x.y));
}

__device__ float3 fract(float3 x)
{
	float xr,yr,zr;
	xr=x.x-floorf(x.x);
	yr=x.y-floorf(x.y);
	zr=x.z-floorf(x.z);
	return make_float3(xr,yr,zr);
}

__device__ float4 fract(float4 x)
{
	float xr,yr,zr,wr;
	xr=x.x-floorf(x.x);
	yr=x.y-floorf(x.y);
	zr=x.z-floorf(x.z);
	wr=x.w-floorf(x.w);
	return make_float4(xr,yr,zr,wr);
}

__device__ float mix(float x,float y, float a)
{
	float z;
	z= (x*(1.0f-a) + y*a);
	return z;
}

__device__ float2 mix(float2 x,float2 y, float a)
{
	float x1,y1;
	x1= (x.x*(1.0f-a) + y.x*a);
	y1= (x.y*(1.0f-a) + y.y*a);
	return make_float2(x1,y1);
}  

__device__ float3 mix(float3 x,float3 y, float a)
{
	return make_float3((x.x*(1.0f-a) + y.x*a), (x.y*(1.0f-a) + y.y*a), (x.z*(1.0f-a) + y.z*a));
}  

__device__ float4 mix(float4 x,float4 y, float a)
{
	float vx,vy,vz,vw;
	vx= (x.x*(1.0f-a) + y.x*a);
	vy= (x.y*(1.0f-a) + y.y*a);
	vz= (x.z*(1.0f-a) + y.z*a);
	vw= (x.w*(1.0f-a) + y.w*a);
	return make_float4(vx,vy,vz,vw);
} 

__device__ float clamp(float val, float min, float  max) {
    return fmaxf(min, fminf(max, val));
}

__device__ float2 clamp(float2 x,float minVal, float maxVal)
{
	float vx,vy;
	vx=fminf(fmaxf(x.x, minVal), maxVal);
	vy=fminf(fmaxf(x.y, minVal), maxVal);
	return make_float2(vx,vy);
}

__device__ float3 clamp(float3 x,float minVal, float maxVal)
{
	float vx,vy,vz;
	vx=fminf(fmaxf(x.x, minVal), maxVal);
	vy=fminf(fmaxf(x.y, minVal), maxVal);
	vz=fminf(fmaxf(x.z, minVal), maxVal);
	return make_float3(vx,vy,vz);
}

__device__ float4 clamp(float4 x,float minVal, float maxVal)
{
	float vx,vy,vz,vw;
	vx=fminf(fmaxf(x.x, minVal), maxVal);
	vy=fminf(fmaxf(x.y, minVal), maxVal);
	vz=fminf(fmaxf(x.z, minVal), maxVal);
	vw=fminf(fmaxf(x.w, minVal), maxVal);
	return make_float4(vx,vy,vz,vw);
}

__device__ float smootherstep(float edge0, float edge1, float x) {
    x = clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
    return x * x * x * (x * (x * 6.f - 15.f) + 10.f);
 }
  

__device__ float smoothstep(float edge0, float edge1, float x)
{
	float t= clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
	return t * t * (3.0f - 2.0f * t);
}

__device__ float2 smoothstep(float e0,float e1,float2 x)
{
	return make_float2(smoothstep(e0,e1,x.x),smoothstep(e0,e1,x.y));
}
		
		
__device__ float2 smoothstep(float2 e0,float2 e1, float2 x) 
{

	return make_float2(smoothstep(e0.x,e1.x,x.x),smoothstep(e0.y,e1.y,x.y));
}
__device__ float3 smoothstep(float e0,float  e1, float3 x) 
{
	return make_float3(smoothstep(e0,e1,x.x),smoothstep(e0,e1,x.y),smoothstep(e0,e1,x.z));
}
		
__device__ float3 smoothstep(float3 e0,float3 e1, float3 x) 
{
	return make_float3(smoothstep(e0.x,e1.x,x.x),smoothstep(e0.y,e1.y,x.y),smoothstep(e0.z,e1.z,x.z));
}

__device__ float4 smoothstep(float e0,float e1, float4 x) 
{
	return make_float4(smoothstep(e0,e1,x.x),smoothstep(e0,e1,x.y),smoothstep(e0,e1,x.z),smoothstep(e0,e1,x.w));
}
		
__device__ float4 smoothstep(float4 e0,float4 e1, float4 x) 
{
	return make_float4(smoothstep(e0.x,e1.x,x.x),smoothstep(e0.y,e1.y,x.y),smoothstep(e0.z,e1.z,x.z),smoothstep(e0.w,e1.w,x.w));
}

__device__ float distance(float2 v1,float2 v2)
{
		float2 dif=v1-v2;
		return sqrt(dif.x*dif.x+dif.y*dif.y);
}

__device__ float distance(float3 v1,float3 v2)
{
	float3 dif=v1-v2;
	return sqrt(dif.x*dif.x+dif.y*dif.y + dif.z*dif.z);
}

__device__ float distance(float4 v1,float4 v2)
{
	float4 dif=v1-v2;
	return sqrt(dif.x*dif.x+dif.y*dif.y + dif.z*dif.z + dif.w*dif.w);
}

__device__ float2 normalize(float2 v1)
{
	float x= v1.x/length(v1);
	float y= v1.y/length(v1);
	return make_float2(x,y);
}

__device__ float3 normalize(float3 v1)
{
	float x= v1.x/length(v1);
	float y= v1.y/length(v1);
	float z= v1.z/length(v1);
	return make_float3(x,y,z);
}
		
__device__ float4 normalize(float4 v1)
{
	float x= v1.x/length(v1);
	float y= v1.y/length(v1);
	float z= v1.z/length(v1);
	float w= v1.w/length(v1);
	return make_float4(x,y,z,w);
}


__device__ float2 min(float2 x, float2 y)
{
	float x1,y1;
	x1=fminf(x.x,y.x);
	y1=fminf(x.y,y.y);
	return make_float2(x1,y1);
}
		
__device__ float3 min(float3 x, float3 y)
{
	float x1,y1,z1;
	x1=fminf(x.x,y.x);
	y1=fminf(x.y,y.y);
	z1=fminf(x.z,y.z);
	return make_float3(x1,y1,z1);
}

__device__ float4 min(float4 x, float4 y)
{
	float x1,y1,z1,w1;
	x1=fminf(x.x,y.x);
	y1=fminf(x.y,y.y);
	z1=fminf(x.z,y.z);
	w1=fminf(x.w,y.w);
	return make_float4(x1,y1,z1,w1);
}
		
__device__ float2 min(float2 x, float y)
{
	float x1,y1;
	x1=fminf(x.x,y);
	y1=fminf(x.y,y);
	return make_float2(x1,y1);
}
		
__device__ float3 min(float3 x, float y)
{
	float x1,y1,z1;
	x1=fminf(x.x,y);
	y1=fminf(x.y,y);
	z1=fminf(x.z,y);
	return make_float3(x1,y1,z1);
}

__device__ float4 min(float4 x, float y)
{
	float x1,y1,z1,w1;
	x1=fminf(x.x,y);
	y1=fminf(x.y,y);
	z1=fminf(x.z,y);
	w1=fminf(x.w,y);
	return make_float4(x1,y1,z1,w1);
}


__device__ float2 max(float2 x,float2 y)
{
	float v1,v2;
	v1=fmaxf(x.x,y.x);
	v2=fmaxf(x.y,y.y);
	return make_float2(v1,v2);
}

__device__ float3 max(float3 x,float3 y)
{
	float v1,v2,v3;
	v1=fmaxf(x.x,y.x); 
	v2=fmaxf(x.y,y.y);
	v3=fmaxf(x.z,y.z);
	return make_float3(v1,v2,v3);
}

__device__ float4 max(float4 x,float4 y)
{
	float v1,v2,v3,v4;
	v1=fmaxf(x.x,y.x);
	v2=fmaxf(x.y,y.y);
	v3=fmaxf(x.z,y.z);
	v4=fmaxf(x.w,y.w);
	return make_float4(v1,v2,v3,v4);
}

__device__ float2 fmaxf(float2 x,float y)
{
	float v1,v2;
	v1=fmaxf(x.x,y);
	v2=fmaxf(x.y,y);
	return make_float2(v1,v2);
}
__device__ float2 max(float2 x,float y)
{
	float v1,v2;
	v1=fmaxf(x.x,y);
	v2=fmaxf(x.y,y);
	return make_float2(v1,v2);
}

__device__ float3 max(float3 x,float y)
{
	float v1,v2,v3;
	v1=fmaxf(x.x,y);
	v2=fmaxf(x.y,y);
	v3=fmaxf(x.z,y);
	return make_float3(v1,v2,v3);
}
	
__device__ float4 max(float4 x,float y)
{
	float v1,v2,v3,v4;
	v1=fmaxf(x.x,y);
	v2=fmaxf(x.y,y);
	v3=fmaxf(x.z,y);
	v4=fmaxf(x.w,y);
	return make_float4(v1,v2,v3,v4);
}


struct __align__(8) Mat2 {
	 float a00;
	 float a01;
	 float a10;
	 float a11;
};

__device__ void Mat2_Init(Mat2 *m, float v00, float v10, float v01, float v11) {
  m->a00 = v00;
  m->a01 = v01;
  m->a10 = v10;
  m->a11 = v11;
}

__device__ void Mat2_Init(Mat2 *m, float4 v) {
  m->a00 = v.x;
  m->a10 = v.y;
  m->a01 = v.z;
  m->a11 = v.w;
}

__device__ void Mat2_Init(Mat2 *m, float2 v1, float2 v2) {
  m->a00 = v1.x;
  m->a10 = v1.y;
  m->a01 = v2.x;
  m->a11 = v2.y;
}

__device__ float2 times(Mat2 *m, float2 v){
   	return  make_float2(m->a00*v.x + m->a01*v.y , m->a10*v.x + m->a11*v.y); 
}

__device__ void add(Mat2 *m, float v) {
  m->a00 += v;
  m->a10 += v;
  m->a01 += v;
  m->a11 += v;
}

__device__ void minus(Mat2 *m, float v) {
  m->a00 -= v;
  m->a10 -= v;
  m->a01 -= v;
  m->a11 -= v;
}

__device__ void times(Mat2 *m, float v) {
  m->a00 *= v;
  m->a10 *= v;
  m->a01 *= v;
  m->a11 *= v;
}

__device__ void division(Mat2 *m, float v) {
  m->a00 /= v;
  m->a10 /= v;
  m->a01 /= v;
  m->a11 /= v;
}

struct __align__(8) Mat3 {
	 float a00;
	 float a10;
	 float a20;
	 float a01;
	 float a11;
	 float a21;
	 float a02;
	 float a12;
	 float a22;
};

__device__ void Mat3_Init(Mat3 *m, float v00, float v10, float v20, float v01, float v11, float v21, float v02, float v12, float v22 ) {
  m->a00 = v00;
  m->a10 = v10;
  m->a20 = v20;
  m->a01 = v01;
  m->a11 = v11;
  m->a21 = v21;
  m->a02 = v02;
  m->a12 = v12;
  m->a22 = v22;
}

__device__ void Mat3_Init(Mat3 *m, float3 v1, float3 v2, float3 v3) {
  m->a00 = v1.x;
  m->a10 = v1.y;
  m->a20 = v1.z;
  m->a01 = v2.x;
  m->a11 = v2.y;
  m->a21 = v2.z;
  m->a02 = v3.x;
  m->a12 = v3.y;
  m->a22 = v3.z;
}

__device__ float3 times(Mat3 *m, float3 v){
     return make_float3(m->a00*v.x + m->a01*v.y + m->a02*v.z , m->a10*v.x + m->a11*v.y + m->a12*v.z , m->a20*v.x + m->a21*v.y + m->a22*v.z);
   	
}

__device__ void add(Mat3 *m, float v) {
  m->a00 += v;
  m->a10 += v;
  m->a20 += v;
  m->a01 += v;
  m->a11 += v;
  m->a21 += v;
  m->a02 += v;
  m->a12 += v;
  m->a22 += v;
}

__device__ void minus(Mat3 *m, float v) {
  m->a00 -= v;
  m->a10 -= v;
  m->a20 -= v;
  m->a01 -= v;
  m->a11 -= v;
  m->a21 -= v;
  m->a02 -= v;
  m->a12 -= v;
  m->a22 -= v;
}

__device__ void times(Mat3 *m, float v) {
  m->a00 *= v;
  m->a10 *= v;
  m->a20 *= v;
  m->a01 *= v;
  m->a11 *= v;
  m->a21 *= v;
  m->a02 *= v;
  m->a12 *= v;
  m->a22 *= v;
}

__device__ void division(Mat3 *m, float v) {
  m->a00 /= v;
  m->a10 /= v;
  m->a20 /= v;
  m->a01 /= v;
  m->a11 /= v;
  m->a21 /= v;
  m->a02 /= v;
  m->a12 /= v;
  m->a22 /= v;
}

struct __align__(8) Mathc {
	 float a;
	 float b;
	 float c;
	 float d;
	 float e;
	 float f;	 
};

__device__ Mat3 rotEuler (float3 s) {
		float 	sa = sinf(s.x),
		ca = cosf(s.x),
		sb = sinf(s.y),
		cb = cosf(s.y),
		sc = sinf(s.z),
		cc = cosf(s.z);
		Mat3 M;
		Mat3_Init (&M,make_float3(cb*cc, -cb*sc, sb),
		              make_float3(sa*sb*cc+ca*sc, -sa*sb*sc+ca*cc, -sa*cb),
		              make_float3(-ca*sb*cc+sa*sc, ca*sb*sc+sa*cc, ca*cb)  );
	return M;
}

__device__ float distance_color(float p_red,float p_green,float p_blue,float red,float green,float blue)
{
	float dist_r = fabsf(p_red - red);
	float dist_g = fabsf(p_green - green);
	float dist_b = fabsf(p_blue - blue);
	float dist_3d_sqd = (dist_r * dist_r) + (dist_g * dist_g) + (dist_b * dist_b);
	return dist_3d_sqd;
}

__device__ float2  transfhcf (float2 xy,float a,float b,float c,float d,float e,float f)
{
  float xt=a*xy.x+b*xy.y+c;
  float yt=d*xy.x+e*xy.y+f;
  return make_float2(xt,yt);
}

__device__ float greyscale(int r,int  g,int b)
{
  int lum,red,green,blue;
  red = (r * 0.299);         
  green = (g * 0.587);         
  blue = (b * 0.114);    
  lum = red + green + blue;    
  return (float)lum/255.0f;
}

__device__ int3 dbl2int(float3 theColor)
  	{
  		int red   =  max(0, min(255, (int)floorf(theColor.x * 256.0f)));
  		int green =  max(0, min(255, (int)floorf(theColor.y * 256.0f)));
  		int blue  =  max(0, min(255, (int)floorf(theColor.z * 256.0f)));
  		return make_int3(red,green,blue);
  	}
	
	
__device__ float3  hsv2rgb (float3 c) 
	{
	  float4 K = make_float4(1.0f, 2.0f / 3.0f, 1.0f / 3.0f, 3.0f);
	  float3 p = abs(fract(make_float3(c.x,c.x,c.x)+(make_float3(K.x,K.y,K.z)))*(6.0f)-(make_float3(K.w,K.w,K.w)));
	  return mix(make_float3(K.x,K.x,K.x), clamp(p - make_float3(K.x,K.x,K.x), 0.0f, 1.0f), c.y)*c.z;
	}
	
	struct __align__(8) Jacobi_elliptic_result
{ float cn;
  float dn;
  float sn;
};
	
__device__ void Jacobi_elliptic( float uu, float emmc, Jacobi_elliptic_result *res)
{
    res->cn=0.0;
	res->dn=0.0;
	res->sn=0.0;
    
    float CA = 0.0003; 
    float a, b, c = 0.0, d = 0.0, em[13] , en[13];
    int bo;
    int l = 0;
    int ii;
    int i;
    
    
    float emc = emmc;
    float u = uu;
    if (emc != 0.0) {
      bo = 0;
      if (emc < 0.0)
        bo = 1;
      if (bo != 0) {
        d = 1.0 - emc;
        emc = -emc / d;
        d = sqrtf(d);
        u = d * u;
      }
      a = 1.0;
      res->dn = 1.0;
      
      for (i = 0; i < 8; i++) {
        l = i;
        em[i] = a;
        emc = sqrtf(emc);
        en[i] = emc;
        c = 0.5 * (a + emc);
        if (fabsf(a - emc) <= CA * a)
          break;
        emc = a * emc;
        a = c;
      }
      u = c * u;
      res->sn = sinf(u);
      res->cn = cosf(u);
      if (res->sn != 0.0) {
        a = res->cn / res->sn;
        c = a * c;
        for (ii = l; ii >= 0; --ii) {
          b = em[ii];
          a = c * a;
          c = res->dn * c;
          res->dn = (en[ii] + a) / (b + a);
          a = c / b;
        }
        a = 1.0 / sqrtf(c * c + 1.0);
        if (res->sn < 0.0)
          (res->sn) = -a;
        else
          res->sn = a;
        res->cn = c * (res->sn);
      }
      if (bo != 0) {
        a = res->dn;
        res->dn = res->cn;
        res->cn = a;
        res->sn = (res->sn) / d;
      }
    } else {
      res->cn = 1.0 / coshf(u);
      res->dn = res->cn;
      (res->sn) = tanhf(u);
    }
}

//------------- END of JS CODE--------------------------


struct __align__(8) Complex
{
  float per_fix;
  float re;
  float im;
  float save_re;
  float save_im;
};

__device__ void Complex_Init(Complex *c, float Rp, float Ip) {
  c->re = Rp;
  c->im = Ip;
  c->save_re = 0.f;
  c->save_im = 0.f;
  c->per_fix = 0.f;  
}


	
__device__ float Complex_Mag2(Complex *c) {
    return c->re * c->re + c->im * c->im;
}
  
__device__ float Complex_MagInv(Complex *c) {
    float M2 = Complex_Mag2(c);
    return (M2 < 1e-10 ? 1.0f : 1.0f / M2);
}
  
__device__ void Complex_Recip(Complex *c) {
    float mi = Complex_MagInv(c);
    c->re = c->re * mi;
    c->im = -c->im * mi;
}

__device__ void Complex_Dec(Complex *c) {
  c->re -= 1.0f;
}

__device__ void Complex_Inc(Complex *c) {
  c->re += 1.0f;
}

__device__ void Complex_Neg(Complex *c) {
  c->re = -c->re;
  c->im = -c->im;
}
  
__device__ void Complex_Div(Complex *c, Complex *zz) {
  float r2 = c->im * zz->im + c->re * zz->re;
  float i2 = c->im * zz->re - c->re * zz->im;
  float M2 = Complex_MagInv(zz);
  c->re = r2 * M2;
  c->im = i2 * M2;
}
  
  __device__ void Complex_DivR(Complex *T,Complex *zz) {
	float r2 = zz->im * T->im + zz->re * T->re;
	float i2 = zz->im * T->re - zz->re * T->im;
	float M2 = Complex_MagInv(T);
	T->re = r2 * M2;
	T->im = i2 * M2;
} 

__device__ void Complex_Copy(Complex *c, Complex *zz) {
  c->re = zz->re;
  c->im = zz->im;
}
  
__device__ float Complex_Mag2eps(Complex *c) {
    return c->re * c->re + c->im * c->im + 1e-10;
}

__device__ float Complex_Arg(Complex *c) {
  return (c->per_fix + atan2f(c->im, c->re));
}

__device__ void Complex_Log(Complex *c) {
  Complex L_eps;
  Complex_Init(&L_eps, 0.5f * logf(Complex_Mag2eps(c)), Complex_Arg(c));
  Complex_Copy(c, &L_eps);
}

__device__ void Complex_Scale(Complex *c, float mul) {
    c->re = c->re * mul;
    c->im = c->im * mul;
}
  
__device__ void Complex_AtanH(Complex *c) {
    Complex D;
	Complex_Init(&D, c->re, c->im);
    Complex_Dec(&D);
    Complex_Neg(&D);
    Complex_Inc(c);
    Complex_Div(c, &D);
    Complex_Log(c);
    Complex_Scale(c, 0.5f);
}

__device__ void Complex_AcotH(Complex *c) {
   Complex_Recip(c);
   Complex_AtanH(c);
}

__device__ void Complex_Flip(Complex *c) {
    float r2 = c->im;
    float i2 = c->re;
    c->re = r2;
    c->im = i2;
  }
  
__device__ void Complex_Sqr(Complex *c) {
  float r2 = c->re * c->re - c->im * c->im;
  float i2 = 2.f * c->re * c->im;
  c->re = r2;
  c->im = i2;
}  

  
__device__ void Complex_Add(Complex *c, Complex *zz) {
  c->re += zz->re;
  c->im += zz->im;
}

__device__ void Complex_Sub(Complex *c, Complex *zz) {
  c->re -= zz->re;
  c->im -= zz->im;
}


__device__ void Complex_Mul(Complex *c, Complex *zz) {
   if (zz->im == 0.0) {
      Complex_Scale(c, zz->re);
      return;
   }
   float  r2 = c->re * zz->re - c->im * zz->im;
   float  i2 = c->re * zz->im + c->im * zz->re;
   c->re = r2;
   c->im = i2;
}
    
  
__device__ void Complex_One(Complex *c) {
  c->re = 1.0f;
  c->im = 0.0f;
}

__device__ void Complex_Conj(Complex *c) {
  c->im = -c->im;
}


__device__ float Complex_Radius(Complex *c) {
    return hypotf(c->re, c->im);
}

__device__ void Complex_Sqrt(Complex *c) {
  float Rad = Complex_Radius(c);
  float sb = (c->im < 0) ? -1.f : 1.f;
  c->im = sb * sqrtf(0.5f * (Rad - c->re));
  c->re = sqrtf(0.5f * (Rad + c->re));
  if (c->per_fix < 0)
    Complex_Neg(c);
}
  
  
__device__ void Complex_ToP(Complex *c, Complex *dst) {
  Complex_Init(dst, Complex_Radius(c), Complex_Arg(c));
}
  
  
__device__ void Complex_UnP(Complex *c, Complex *dst) {
  Complex_Init(dst, c->re * cosf(c->im), c->re * sinf(c->im));
}  
  
__device__ void Complex_Pow(Complex *c, float exp) {
    if (exp == 0.0f) {
      Complex_One(c);
      return;
    }
    float ex = fabsf(exp);
    if (exp < 0) {
      Complex_Recip(c);
    }
    if (ex == 0.5f) {
      Complex_Sqrt(c);
      return;
    }
    if (ex == 1.0f) {
      return;
    }
    if (ex == 2.0f) {
      Complex_Sqr(c);
      return;
    }
    // In general we need sin, cos etc
    Complex PF;
    Complex_ToP(c, &PF);
    PF.re = powf(PF.re, ex);
    PF.im = PF.im * ex;
	
	Complex PFU;	
	Complex_UnP(&PF, &PFU);	
    Complex_Copy(c, &PFU);
  }
  
 
__device__ void Complex_AsinH(Complex *c) {
  Complex D;
  Complex_Init(&D, c->re, c->im);
  Complex_Sqr(&D);
  Complex_Inc(&D);
  Complex_Pow(&D, 0.5f);
  Complex_Add(c, &D);
  Complex_Log(c);
}

__device__ void Complex_AsecH(Complex *c) {
   Complex_Recip(c);
   Complex_AsinH(c);
}

__device__ void Complex_Exp(Complex *c) {
   c->re = expf(c->re);
   Complex unp;
   Complex_UnP(c, &unp);
   Complex_Copy(c, &unp);
}

__device__ void Complex_AcosH(Complex *c) {
  Complex D;
  Complex_Init(&D, c->re, c->im);
  Complex_Sqr(&D);
  Complex_Dec(&D);
  Complex_Pow(&D, 0.5f);
  Complex_Add(c, &D);
  Complex_Log(c);
}

__device__ void Complex_AcosecH(Complex *c) {
   Complex_Recip(c);
   Complex_AcosH(c);
}

__device__ void Complex_SinH(Complex *c) {
    float rr = 0.0;
    float ri = 0.0;
    float er = 1.0;
    c->re = expf(c->re);
    er /= c->re;
    rr = 0.5 * (c->re - er);
    ri = rr + er;
    c->re = cosf(c->im) * rr;
    c->im = sinf(c->im) * ri;
}
  
__device__ void Complex_CosH(Complex *c) {
    float rr = 0.0;
    float ri = 0.0;
    float er = 1.0;
    c->re = expf(c->re);
    er /= c->re;
    rr = 0.5 * (c->re - er);
    ri = rr + er;
    c->re = cosf(c->im) * ri;
    c->im = sinf(c->im) * rr;
}

__device__ void Complex_Sin(Complex *c) {
    Complex_Flip(c);
    Complex_SinH(c);
    Complex_Flip(c);
}

__device__ void Complex_Cos(Complex *c) {
    Complex_Flip(c);
    Complex_CosH(c);
    Complex_Flip(c);
}

__device__ void Complex_Asin(Complex *c) {
    Complex_Flip(c);
    Complex_AsinH(c);
    Complex_Flip(c);
}

__device__ void Complex_Acos(Complex *c) {
    Complex_Flip(c);
    Complex_AsinH(c);
    Complex_Flip(c); 
    c->re = (M_PI_F/2.0) - (c->re);
    c->im = -(c->im); 
}

__device__ void Complex_Atan(Complex *c) { 
    Complex_Flip(c);
    Complex_AtanH(c);
    Complex_Flip(c);
} 


// Additional complex Functions

__device__ float Complex_arg (Complex z) {
    float result;
    result = atan2f(z.im, z.re);
    return result;
  }
  
__device__ float Complex_norm(Complex z) {
    double u = z.re;
    double v = z.im;
    return (u * u + v * v);
  }
  
__device__ float Complex_mag (Complex z) {
    return sqrtf(z.re*z.re + z.im*z.im);
 }
__device__ Complex Complex_plus (Complex a,Complex z) {
   Complex tmp;
   Complex_Init(&tmp, a.re+ z.re, a.im + z.im);
   return tmp;
  }
  
__device__ Complex Complex_minus (Complex a,Complex z) {
   Complex tmp;
   Complex_Init(&tmp, a.re - z.re, a.im - z.im);
   return tmp;
  }

__device__ Complex Complex_times (Complex a, float x) {
    Complex tmp;
    Complex_Init(&tmp,x*a.re,x*a.im);
	return tmp;
}

__device__ Complex Complex_times (Complex a, Complex z) {
   Complex tmp;
   Complex_Init(&tmp, a.re*z.re - a.im*z.im,a.re*z.im + a.im*z.re);
   return tmp;
}

__device__ Complex Complex_divideBy (Complex a, Complex z) {
    Complex tmp;
    float rz = Complex_mag(z);
    if(fabsf(rz) > 1.0e-12)
    {
	  Complex_Init(&tmp,(a.re * z.re + a.im * z.im)/(rz * rz),
                        (a.im * z.re - a.re * z.im)/(rz * rz));
    }	
	return tmp;
}

__device__ Complex Complex_sqrt(Complex z) {
    Complex tmp;
	float r = sqrtf(Complex_mag(z));
    float phi = Complex_arg(z)/2.0;
	Complex_Init(&tmp,r*cosf(phi),r*sinf(phi));
	return tmp;
}

__device__ Complex Complex_ln(Complex z) {
    Complex tmp;
    float rr = logf(Complex_mag(z))/logf(2.718);
    float ii = Complex_arg(z);
    Complex_Init(&tmp,rr,ii);
	return tmp;
}
  
__device__ Complex Complex_sin(Complex z) { 
    float r = sinf(z.re) * coshf(z.im);
    float i = cosf(z.re) * sinhf(z.im);
	Complex tmp;
	Complex_Init(&tmp,r,i);
    return tmp;
  }

__device__ Complex Complex_asinh(Complex zz)  {
    Complex i,z;
	Complex_Init(&i,1.0,0.0);
	z = Complex_plus(i,Complex_times(zz,zz));
    z = Complex_sqrt(z);
    z = Complex_plus(zz,z);
    z = Complex_ln(z);
    return z;
}

__device__ Complex Complex_asin(Complex z) {
    Complex j,zz;
	Complex_Init(&j,0.0, 1.0);
	Complex one;
	Complex_Init(&one,1.0,0.0);
	zz = Complex_minus(one , Complex_times(z,z));
    zz = Complex_sqrt(zz);
    zz = Complex_plus(zz,Complex_times(j,z));
    zz = Complex_times(Complex_times(j,Complex_ln(zz)), -1.0);
    return zz;
  }

__device__ Complex Complex_acos(Complex z) {
      Complex i,j,zz;
	  Complex_Init(&i,1.0,0.0);
	  Complex_Init(&j,0.0,1.0);
	  zz=Complex_minus(Complex_times(z,z),i);
      zz = Complex_sqrt(zz);
      zz = Complex_plus(z,zz);
      zz = Complex_times(Complex_times(j,Complex_ln(zz)),-1.0);
      return zz;
}

__device__ Complex Complex_tan(Complex z) {
    Complex tmp;
    float nenner = cosf(2.*z.re) + coshf(2*z.im);
    float r = sinf(2.*z.re) / nenner;
    float i = sinhf(2.*z.im) / nenner;
	Complex_Init(&tmp,r,i);
    return tmp;;
}


  
#endif

struct __align__(8) xForm
{
    float a;
    float b;
    float c;
    float d;
    float e;
    float f;
    float pa;
    float pb;
    float pc;
    float pd;
    float pe;
    float pf;
    float color;
    float symmetry;
    float weight;
    float opacity;
    float var_color;
    int   rotates;
#ifdef JWF_EXTENSIONS	
    float yzA;
    float yzB;
    float yzC;
    float yzD;
    float yzE;
    float yzF;
    float yzPa;
    float yzPb;
    float yzPc;
    float yzPd;
    float yzPe;
    float yzPf;
    float zxA;
    float zxB;
    float zxC;
    float zxD;
    float zxE;
    float zxF;
    float zxPa;
    float zxPb;
    float zxPc;
    float zxPd;
    float zxPe;
    float zxPf;
	int useXyz;
	int wfield_type;
	int wfield_input;
	float wfield_var_amount;
	int	wfield_param1_var_idx;
	int wfield_param1_param_idx;
	float wfield_param1_amount;
	int	wfield_param2_var_idx;
	int wfield_param2_param_idx;
	float wfield_param2_amount;
	int	wfield_param3_var_idx;
	int wfield_param3_param_idx;
	float wfield_param3_amount;
	float wfield_color_amount;
	float wfield_jitter_amount;
	int wfield_seed;
	int wfield_octaves;
	float wfield_gain;
	float wfield_lacunarity;
	float wfield_scale;
	int wfield_fractal_type;
    int wfield_cell_noise_dist_func;
    int wfield_cell_noise_ret_val;
#endif	
};

// each xform has a variable length list of active variations and each variation has its own specific variable sized varpar struct
struct __align__(16) VariationListNode
{                        // all of the lists are concatenated into a single buffer - a separate xformUsageIndex has the offset to the xform's first variation in this list
    uint variationID;    // the numeric value identifying the variation from the variation set - NOTE id of zero is used to signify end of list
    uint varparOffset;   // the offset in varpar union list for this variation's specific varpar struct
    uint enterGroup;     // the state transition that handles entering Pre, Normal, and Post variation groups
};

struct __align__(8) unAnimatedxForm
{
    float a;
    float b;
    float d;
    float e;
    int rotates;
};

struct __align__(16) FlameParams
{
    struct rgba background;
    float center[2];                //{x,y}
    float size[2];                    //size/(scale*zoom)
    float scale;
    float zoom;
    float cam_yaw;
    float cam_pitch;
    float cam_perspective;
    int   clipToNDC;
    float cam_dof;
    float cam_zpos;
    float cam_x;
    float cam_y;
    float cam_z;
    float cam_fov;
    float cam_near;
    float cam_orthowide;
    float hue;
    float numBatches;
    float quality;
    float desiredQuality;
    float rotation;
    float symmetryKind;
    float brightness;
    float gamma;
    float gammaThreshold;
    float alphaGamma;
    float vibrancy;
    unsigned int   numTrans;
    unsigned int   numFinal;
    int   useFinal;
    float supersampleWidth;
    int   frame;
    int   useXaos;
    int   oversample;
    float   highlightPower;
    int    estimatorRadius;			// default 7
    float  estimatorCurve;			// default 0.4
#ifdef JWF_EXTENSIONS
	float cam_roll;
	float cam_bank;
	float cam_xfocus;
	float cam_yfocus;
	float cam_zfocus;
	float cam_xpos;
	float cam_ypos;
	float cam_dist;
	float cam_dof_exponent;
	float cam_dof_area;
	bool legacy_dof;
	int dof_type;
	float dof_scale;
	float dof_fade;
    float balanceRed, balanceGreen, balanceBlue;
	float intensityAdjust;
	int render_pass;
	float z_buffer_scale, z_buffer_shift, z_buffer_bias;
#endif
};


struct Flame //  : public std::enable_shared_from_this<Flame>
{
    struct FlameParams params;
    int                numColors;
    struct xForm      *trans;
    struct xForm      *finals;
    struct rgba       *colorIndex;
    float             *colorLocations;
    float             *switchMatrix;
    //  std::vector<SharedVariationChain> xformVarChains;
    //  std::vector<SharedVariationChain> finalVarChains;
//#ifdef __cplusplus
//    Flame();
//    Flame(int numTrans,int paletteSize, int numFinal);
//    Flame(int numTrans,int paletteSize, int numFinal, int alignment);
//    Flame(const Flame &other);
//    Flame(const Flame &other, int alignment);
//    
//    void Clone(Flame** target);
//    void CloneAligned(Flame** target, int alignment);
//    void deleteChildren();
//    void prepareSwitchMatrix (float *brick);
//    ~Flame();
//#endif
} __attribute__ ((aligned (16)));
#endif

__VARPAR_STRUCT_DECLS__


#define PI 3.141592653589793f

#ifndef FLAM4_KERNAL_CUH
#define FLAM4_KERNAL_CUH

struct __align__(16) point
{
    float x;
    float y;
    float z;
    float pal;
#ifdef JWF_EXTENSIONS
	float colorR, colorG, colorB, colorA;
	bool useRgb;
	bool doHide;
#endif
};
#endif

#ifndef FOR_2D
struct  __align__(16) CameraViewProperties
{
    float matrix[16];
    float yaw;
    float pitch;
    float roll;
    float perspective;
    float dof;
    float zpos;
    float cosRoll;
    float sinRoll;
    float camWidth;
    float camHeight;
    float centerX;
    float centerY;
    int   clipToNDC;
    float rotatedViewOffsetx;
    float rotatedViewOffsety;
#ifdef JWF_EXTENSIONS
	float bank;
	float focusX;
	float focusY;
	float focusZ;
	float camPosX;
	float camPosY;
	float camPosZ;
	float camDist;
	float camDOFExponent;
	float camDOFArea;
	float camDOF;
	bool legacyDOF;
	int dofType;
	float dofScale;
	float dofFade;	
#endif	
};
#endif

// so it can be precompiled as part of the build for syntax checking
#ifndef KERNEL_RUNTIME
#define WARP_SIZE 32
#define NUM_POINTS 64
#endif
#define ADD_EPSILON +epsilon
//#define ADD_EPSILON +1.e-7f
//#define ADD_EPSILON +1.e-10f
//#define ADD_EPSILON

#define WARPS_PER_BLOCK 2
#define BLOCKDIM WARPS_PER_BLOCK*WARP_SIZE

#define RANDFLOAT() randFloat(randStates)
#define RANDINT()   randInt(randStates)

__VARIATION_INDEX_DEFINES__

__constant__ FlameParams d_g_Flame;
__constant__ xForm d_g_Xforms[MAX_XFORMS+MAX_XFORMS];  // has both normal & final xforms

__constant__ unsigned int shift1[4] = {6, 2, 13, 3};
__constant__ unsigned int shift2[4] = {13, 27, 21, 12};
__constant__ unsigned int shift3[4] = {18, 2, 7, 13};
__constant__ unsigned int offset[4] = {4294967294, 4294967288, 4294967280, 4294967168};

texture<uchar4, cudaTextureType1D, cudaReadModeNormalizedFloat> texRef;

__VARIATION_FUNCTION_PROTOTYPES__


__device__ float sqrf(float x) {
  return x*x;
} 

__device__ unsigned int TausStep(unsigned int z, int S1, int S2, int S3, unsigned int M)
{
    unsigned int b = (((z << S1) ^ z) >> S2);
    return (((z &M) << S3) ^ b);
}

__device__ unsigned int randInt(unsigned int *randStates)
{
    unsigned int index = threadIdx.x;
    randStates[index&(WARP_SIZE-1)] = TausStep(randStates[index&(WARP_SIZE-1)], shift1[index&3], shift2[index&3],shift3[index&3],offset[index&3]);
    return (randStates[(index)&(WARP_SIZE-1)]^randStates[(index+1)&(WARP_SIZE-1)]^randStates[(index+2)&(WARP_SIZE-1)]^randStates[(index+3)&(WARP_SIZE-1)]);
}

__device__ float randFloat(unsigned int *randStates)
//This function returns a random float in [0,1] and updates seed
{
    unsigned int y = randInt(randStates);
    return __int_as_float((y&0x007FFFFF)|0x3F800000)-1.0f;
}

__device__ float randFloatWarp(unsigned int *randStates, uint index)
//This function is a workaround for getting a warp wide rand number
{
    randInt(randStates);
    return __int_as_float((randStates[index]&0x007FFFFF)|0x3F800000)-1.0f;
}

__device__ int linear_range_search(float* X, float x, int n)
{
    int index = 256;
    for (int j = n-1; j >= 0; j--) {
        index = (x <= X[j+1]) ? j : index;
    }
    return index;
}

__device__ float curveAdjust(float x,
        float* X,
        float* A,
        float* B,
        float* C,
        float* D,
        uint cpCount)
{
    int index    = linear_range_search(X, x, cpCount - 1);
    float result = (x <= X[0]) ? A[0] : A[cpCount - 1];

    if (index >= 0 && index < (int)cpCount - 1) {
        float t = x - X[index];
        result = A[index] + B[index]*t + C[index]*t*t + D[index]*t*t*t;
    }
    return result;
}


__device__ float4 RGBtoHSV(float4 color)
{
    float r = color.x;
    float g = color.y;
    float b = color.z;
    float mx = fmaxf(fmaxf(r,g),b);
    float mn = fminf(fminf(r,g),b);
    float h,s,v;
    if (mx == mn)
        h = 0.0f;
    else if (mx == r)
        h = .16666666667f*(g-b)/(mx-mn);
    else if (mx == g)
        h = .16666666667f*(b-r)/(mx-mn)+.33333333f;
    else
        h = .16666666667f*(r-g)/(mx-mn)+.66666667f;
    h = h-floorf(h);
    if (mx == 0.0f)
        s = 0.0f;
    else
        s = (mx-mn)/(mx);
    v = mx;
    if (v > 1.0f) // clamp to 1.f if to high value
        v = 1.0f;
    return make_float4(h,s,v,color.w);
}

__device__ float4 RGBtoHSVHueAdjusted(float4 color)
{
    float r = color.x;
    float g = color.y;
    float b = color.z;
    float mx = fmaxf(fmaxf(r,g),b);
    float mn = fminf(fminf(r,g),b);
    float h,s,v;
    if (mx == mn)
        h = 0.0f;
    else if (mx == r)
        h = .16666666667f*(g-b)/(mx-mn);
    else if (mx == g)
        h = .16666666667f*(b-r)/(mx-mn)+.33333333f;
    else
        h = .16666666667f*(r-g)/(mx-mn)+.66666667f;
    h = h-floorf(h);
    if (mx == 0.0f)
        s = 0.0f;
    else
        s = (mx-mn)/(mx);
    v = mx;
    if (v > 1.0f)
    {
        if (h < .33333333f)
        {
            h += (.16666667f-h)*(1.0f-powf(.75f,v-1.0f));
        }
        else if (h < 0.5f)
        {
            h += (h-0.5f)*(1.0f-powf(.75f,v-1.0f));
        }
        else if (h > 0.8333333f)
        {
            h += (h-0.8333333f)*(1.0f-powf(.75f,v-1.0f));
        }
        //float l = .2126f*r+.7152f*g+.0722f*b;
        //float l = (40.0f*r+20.0f*g+b)/61.0f;
        float l = 0.4f+0.4f*cosf(2.0f*PI*(h-0.16666666667f));
        s = fminf(s*powf(1.0f/v,0.6f*(1.0f-l)),s);
    }
    return make_float4(h,s,v,color.w);
}

__device__ float4 HSVtoRGB(float4 color)
{
    float h = color.x;
    float s = color.y;
    float v = color.z;
    float r,g,b;
    int hi = ((int)floorf(h*6.0f))%6;
    float f = h*6.0f-floorf(h*6.0f);
    float p = v*(1.0f-s);
    float q = v*(1.0f-f*s);
    float t = v*(1.0f-(1.0f-f)*s);
    switch (hi)
    {
        case 0:
        {
            r = v;
            g = t;
            b = p;
        }break;
        case 1:
        {
            r = q;
            g = v;
            b = p;
        }break;
        case 2:
        {
            r = p;
            g = v;
            b = t;
        }break;
        case 3:
        {
            r = p;
            g = q;
            b = v;
        }break;
        case 4:
        {
            r = t;
            g = p;
            b = v;
        }break;
        case 5:
        {
            r = v;
            g = p;
            b = q;
        }break;
    }
    return make_float4(r,g,b,color.w);
}

__device__ float4 read_imageStepMode(float4 * image, int length, float index)
{
    float clampedIndex = index - floorf(index);
    float scaledIndex = clampedIndex*(float)(length - 1);
    int iLow = floorf(scaledIndex);
    return image[iLow];
}

__device__ float sinhcoshf(float theta, float* ch)
{
    float expt = expf(theta);
    float exptinv = 1.0f / expt;
    *ch =  (expt + exptinv) * 0.5f;
    return (expt - exptinv) * 0.5f;
}

__VARIATION_FUNCTIONS__

#ifdef ADD_FEATURE_WFIELDS
__device__ float calcWFieldIntensity(float3 *__wFieldPos, struct xForm* xform) {
        switch(xform->wfield_type) {
#ifdef ADD_FEATURE_CELLULAR_NOISE
             case 1: // Cellular Noise
               {
                  FastNoise noise;
                  fastNoise_init(&noise);
                  noise.m_seed = xform->wfield_seed;
                  noise.m_frequency = xform->wfield_scale;
                  noise.m_noiseType = Cellular;
                  switch(xform->wfield_cell_noise_ret_val) {
                    case 0: // Cell value
                      noise.m_cellularReturnType = CellValue;
                      break;
                    case 1: // Distance1
                      noise.m_cellularReturnType = Distance;
                      break;
                    case 2: // Distance2
                      noise.m_cellularReturnType = Distance2;
                      break;
                    case 3: // Dist add
                      noise.m_cellularReturnType = Distance2Add;
                      break;
                    case 4: // Dist sub
                      noise.m_cellularReturnType = Distance2Sub;
                      break;
                    case 5: // Dist mul
                      noise.m_cellularReturnType = Distance2Mul;
                      break;
                    case 6: // Dist div
                      noise.m_cellularReturnType = Distance2Div;
                      break;
                    default:
                      noise.m_cellularReturnType = Distance2;
                      break;
                  }
                  noise.m_cellularDistanceFunction = xform->wfield_cell_noise_dist_func == 1 ? Manhattan : xform->wfield_cell_noise_dist_func == 2 ? Natural : Euclidean;
                  fastNoise_prepare(&noise);
                  return getNoise(&noise, __wFieldPos->x, __wFieldPos->y, __wFieldPos->z);
               }
               break;
#endif
#ifdef ADD_FEATURE_CUBIC_NOISE
             case 2: // Cubic Noise
               {
                  FastNoise noise;
                  fastNoise_init(&noise);
                  noise.m_seed = xform->wfield_seed;
                  noise.m_frequency = xform->wfield_scale;
                  noise.m_noiseType = Cubic;
                  fastNoise_prepare(&noise);
                  return getNoise(&noise, __wFieldPos->x, __wFieldPos->y, __wFieldPos->z);
               }
               break;
#endif
#ifdef ADD_FEATURE_CUBIC_NOISE
             case 3: // Cubic Fractal Noise
                {
                  FastNoise noise;
                  fastNoise_init(&noise);
                  noise.m_seed = xform->wfield_seed;
                  noise.m_frequency = xform->wfield_scale;
                  noise.m_noiseType = CubicFractal;
                  noise.m_octaves = xform->wfield_octaves;
                  noise.m_lacunarity = xform->wfield_lacunarity;
                  noise.m_gain = xform->wfield_gain;
                  noise.m_fractalType = xform->wfield_fractal_type == 1 ? Billow : xform->wfield_fractal_type == 2 ? RigidMulti : FBM;
                  fastNoise_prepare(&noise);
                  return getNoise(&noise, __wFieldPos->x, __wFieldPos->y, __wFieldPos->z);
               }
               break;
#endif
#ifdef ADD_FEATURE_PERLIN_NOISE
             case 4: // Perlin Noise
               {
                  FastNoise noise;
                  fastNoise_init(&noise);
                  noise.m_seed = xform->wfield_seed;
                  noise.m_frequency = xform->wfield_scale;
                  noise.m_noiseType = Perlin;
                  fastNoise_prepare(&noise);
                  return getNoise(&noise, __wFieldPos->x, __wFieldPos->y, __wFieldPos->z);
               }
               break;
#endif
#ifdef ADD_FEATURE_PERLIN_NOISE
             case 5: // Perlin Fractal Noise
               {
                  FastNoise noise;
                  fastNoise_init(&noise);
                  noise.m_seed = xform->wfield_seed;
                  noise.m_frequency = xform->wfield_scale;
                  noise.m_noiseType = PerlinFractal;
                  noise.m_octaves = xform->wfield_octaves;
                  noise.m_lacunarity = xform->wfield_lacunarity;
                  noise.m_gain = xform->wfield_gain;
                  noise.m_fractalType = xform->wfield_fractal_type == 1 ? Billow : xform->wfield_fractal_type == 2 ? RigidMulti : FBM;
                  fastNoise_prepare(&noise);
                  return getNoise(&noise, __wFieldPos->x, __wFieldPos->y, __wFieldPos->z);
               }
               break;
#endif
#ifdef ADD_FEATURE_SIMPLEX_NOISE
             case 6: // Simplex Noise
               {
                  FastNoise noise;
                  fastNoise_init(&noise);
                  noise.m_seed = xform->wfield_seed;
                  noise.m_frequency = xform->wfield_scale;
                  noise.m_noiseType = Simplex;
                  fastNoise_prepare(&noise);
                  return getNoise(&noise, __wFieldPos->x, __wFieldPos->y, __wFieldPos->z);
               }
               break;
#endif
#ifdef ADD_FEATURE_SIMPLEX_NOISE
             case 7: // Simplex Fractal Noise
               {
                  FastNoise noise;
                  fastNoise_init(&noise);
                  noise.m_seed = xform->wfield_seed;
                  noise.m_frequency = xform->wfield_scale;
                  noise.m_noiseType = SimplexFractal;
                  noise.m_octaves = xform->wfield_octaves;
                  noise.m_lacunarity = xform->wfield_lacunarity;
                  noise.m_gain = xform->wfield_gain;
                  noise.m_fractalType = xform->wfield_fractal_type == 1 ? Billow : xform->wfield_fractal_type == 2 ? RigidMulti : FBM;
                  fastNoise_prepare(&noise);
                  return getNoise(&noise, __wFieldPos->x, __wFieldPos->y, __wFieldPos->z);
               }
               break;
#endif
#ifdef ADD_FEATURE_VALUE_NOISE
             case 8: // Value Noise
               {
                  FastNoise noise;
                  fastNoise_init(&noise);
                  noise.m_seed = xform->wfield_seed;
                  noise.m_frequency = xform->wfield_scale;
                  noise.m_noiseType = Value;
                  fastNoise_prepare(&noise);
                  return getNoise(&noise, __wFieldPos->x, __wFieldPos->y, __wFieldPos->z);
               }
               break;
#endif
#ifdef ADD_FEATURE_VALUE_NOISE
             case 9: // Value Fractal Noise
               {
                  FastNoise noise;
                  fastNoise_init(&noise);
                  noise.m_seed = xform->wfield_seed;
                  noise.m_frequency = xform->wfield_scale;
                  noise.m_noiseType = ValueFractal;
                  noise.m_octaves = xform->wfield_octaves;
                  noise.m_lacunarity = xform->wfield_lacunarity;
                  noise.m_gain = xform->wfield_gain;
                  noise.m_fractalType = xform->wfield_fractal_type == 1 ? Billow : xform->wfield_fractal_type == 2 ? RigidMulti : FBM;
                  fastNoise_prepare(&noise);
                  return getNoise(&noise, __wFieldPos->x, __wFieldPos->y, __wFieldPos->z);
               }
               break;
#endif
#ifdef ADD_FEATURE_WHITE_NOISE
             case 10: // White Noise
               {
                  FastNoise noise;
                  fastNoise_init(&noise);
                  noise.m_seed = xform->wfield_seed;
                  noise.m_noiseType = WhiteNoise;
                  fastNoise_prepare(&noise);
                  return getNoise(&noise, __wFieldPos->x, __wFieldPos->y, __wFieldPos->z);
               }
               break;
#endif
/*
             case 11: // Image Map -> not supported
*/
             default:
               return  0.f;
               break;
           }
  //return  0.f;
}

#endif


__device__ void iteratePoint(struct VariationListNode *varUsageList,
                float *varpars,
                struct xForm* xform,
                uint xformIndex,
                float epsilon,
                struct point *fromPoint,
                struct point *activePoint,
                unsigned int *randStates,
                uint *permutations,
#ifdef JWF_EXTENSIONS
                float4* palette,
                uint numColors,
#endif
                float4 *gradients)
{
    unsigned int index = threadIdx.x;
    activePoint[index] = *fromPoint;

    float s0 = xform->symmetry;
    float s1 = .5f-.5f*s0;
    float __pal = (activePoint[index].pal+xform->color)*s1+activePoint[index].pal*s0;
    float pal0 = __pal;

     float __x, __y, __z;
	 float __colorR=0.f, __colorG=0.f, __colorB=0.f, __colorA=0.f;
	 bool __doHide = false;
	 bool __useRgb = false;
	
	 if(xform->useXyz) {
		 __x = xform->a*activePoint[index].x+xform->b*activePoint[index].y;
		 __y = xform->d*activePoint[index].x+xform->e*activePoint[index].y;
		 __z = activePoint[index].z;

         float nx, ny, nz;  
         ny = xform->yzA * __y + xform->yzB * __z;
         nz = xform->yzD * __y + xform->yzE * __z;
         __y = ny;
         __z = nz;

         nx = xform->zxA * __x + xform->zxB * __z;
         nz = xform->zxD * __x + xform->zxE * __z;
         __x = nx;
         __z = nz;
		 
		 __x += xform->c + xform->zxC;
		 __y += xform->f + xform->yzC;
		 __z += xform->yzF + xform->zxF;
	 }
	 else {
		 __x = xform->a*activePoint[index].x+xform->b*activePoint[index].y+xform->c;
		 __y = xform->d*activePoint[index].x+xform->e*activePoint[index].y+xform->f;
		 __z = activePoint[index].z; // 3d hack does not transform them here
	 }
////WFIELD
     bool __useWFields = false;
     float __wFieldValue;
     float __wFieldAmountScale;
#ifdef ADD_FEATURE_WFIELDS
     if(xform->wfield_type>0) {
         float3 __wFieldPos;
         if(xform->wfield_input == 1) { // Position
           __wFieldPos = make_float3(activePoint[index].x, activePoint[index].y, activePoint[index].z);
         }
         else {
           __wFieldPos = make_float3(__x, __y, __z); // Affine
         }
         __wFieldValue = calcWFieldIntensity(&__wFieldPos, xform);
          if(fabs(__wFieldValue)>EPSILON) {
                __useWFields = true;
                __wFieldAmountScale = 1.0f + __wFieldValue * xform->wfield_var_amount;
          }
          else {
                __wFieldAmountScale = 1.f;
          }
     }
     else {
         __wFieldAmountScale = 1.f;
         __wFieldValue = 0.f;
     }
#else
     __wFieldAmountScale = 1.f;
     __wFieldValue = 0.f;
#endif


////WFIELD
	float __r2, __r, __rinv, __phi, __theta;
    float __px = __x;  // note that enterGroup action will handle resetting these to zero -- also works correctly for xforms with NO variations set
    float __py = __y;
    float __pz = __z;

	float __x0 = __x;
	float __y0 = __y;
	float __z0 = __z;

	bool __was_pre = 0;


			__r2 = __x * __x + __y * __y;
			__r = sqrtf(__r2);
			__rinv = 1.0f / __r;

			__phi = atan2f(__x, __y);
			__theta = .5f*PI - __phi;
			if (__theta > PI)
				__theta -= 2.0f*PI;


     __px = 0.f;
     __py = 0.f;
     __pz = 0.f;


    uint varIndex = 0;
    uint varCounter = 0;
    while ((varIndex = varUsageList->variationID) != 0) {
        float *varparCluster = &varpars[varUsageList->varparOffset];
        switch (varIndex) {
            //Now apply the Variations
            __VARIATION_SWITCH_CASES__
            default:
            break;
        }
        varUsageList++;
        varCounter++;
    }

    if(xform->useXyz) {
      float px = xform->pa*__px+xform->pb*__py;
      float py = xform->pd*__px+xform->pe*__py;
      float pz = __pz;
	  
	  float nx, ny, nz;
	    
      ny = xform->yzPa * py + xform->yzPb * pz;
      nz = xform->yzPd * py + xform->yzPe * pz;
      py = ny;
      pz = nz;
	 
	  nx = xform->zxPa * px + xform->zxPb * pz;
      nz = xform->zxPd * px + xform->zxPe * pz;
      px = nx;
      pz = nz;
	  
      activePoint[index].x = px + xform->pc + xform->zxPc;
      activePoint[index].y = py + xform->pf + xform->yzPc;
      activePoint[index].z = pz + xform->yzPf + xform->zxPf;
	 
	}
	else {
      activePoint[index].x = xform->pa*__px+xform->pb*__py+xform->pc;
      activePoint[index].y = xform->pd*__px+xform->pe*__py+xform->pf;
      activePoint[index].z = __pz;
    }
    //    activePoint[index].z=z;  // 3d hack does not transform them here
#ifdef ADD_FEATURE_WFIELDS_JITTER
    if(__useWFields && fabsf(xform->wfield_jitter_amount) > EPSILON) {
      float jitterIntensity = 0.1 * xform->wfield_jitter_amount;
      {
          float3 __wFieldJitterPos = make_float3(activePoint[index].x, activePoint[index].y, activePoint[index].z);
          float __wFieldJitterValue = calcWFieldIntensity(&__wFieldJitterPos, xform);
          activePoint[index].x += (__wFieldJitterValue * jitterIntensity);
      }
      {
          float3 __wFieldJitterPos = make_float3(activePoint[index].y, activePoint[index].x, activePoint[index].z);
          float __wFieldJitterValue = calcWFieldIntensity(&__wFieldJitterPos, xform);
          activePoint[index].y += (__wFieldJitterValue * jitterIntensity);
      }
      {
          float3 __wFieldJitterPos = make_float3(activePoint[index].z, activePoint[index].x, activePoint[index].y);
          float __wFieldJitterValue = calcWFieldIntensity(&__wFieldJitterPos, xform);
          activePoint[index].z += (__wFieldJitterValue * jitterIntensity);
      }
    }
#endif
    if (d_g_Flame.symmetryKind != 0.0f && xformIndex < d_g_Flame.numTrans) // does not apply to final xform
    {
        if (d_g_Flame.symmetryKind > 0.0f)
        {
            float rn;
            rn = randFloat(randStates);
            float sina, cosa;
            sincosf(2.0f*PI*floorf(rn*d_g_Flame.symmetryKind)/d_g_Flame.symmetryKind, &sina, &cosa);

            __x = cosa*activePoint[index].x-sina*activePoint[index].y;
            __y = sina*activePoint[index].x+cosa*activePoint[index].y;
            activePoint[index].x = __x;
            activePoint[index].y = __y;
        }
        else
        {
            //pick a random symmetry plane and reflect across it.
            float rn;
            float rn2;
            rn2 = randFloat(randStates);
            rn = randFloat(randStates);
            float sina, cosa;
            sincosf(2.0f*PI*floorf(rn*d_g_Flame.symmetryKind)/d_g_Flame.symmetryKind, &sina, &cosa);

            __x = cosa*activePoint[index].x-sina*activePoint[index].y;
            __y = sina*activePoint[index].x+cosa*activePoint[index].y;
            if (rn2>0.5f)
                __x = -__x;
            activePoint[index].x = __x;
            activePoint[index].y = __y;
        }
    }
    activePoint[index].pal =  pal0 + xform->var_color * (__pal - pal0);
#ifdef JWF_EXTENSIONS	
    activePoint[index].doHide = __doHide;
	activePoint[index].useRgb = __useRgb;
    if(__useRgb) {
	  activePoint[index].colorR = __colorR; 
	  activePoint[index].colorG = __colorG; 
	  activePoint[index].colorB = __colorB; 
	  activePoint[index].colorA = __colorA; 
	}
#ifdef ADD_FEATURE_WFIELDS
	////WFIELD
	if(__useWFields && fabsf(xform->wfield_color_amount)>EPSILON) {
		  if(activePoint[index].pal<0.f)
		    activePoint[index].pal = 0.f;
		  else if(activePoint[index].pal>1.0f)
            activePoint[index].pal = 1.f;
          activePoint[index].pal *= (1.0f + __wFieldValue *  xform->wfield_color_amount * 0.1);
		  if(activePoint[index].pal<0.f)
		    activePoint[index].pal = 0.f;
		  else if(activePoint[index].pal>1.0f) 
            activePoint[index].pal = 1.f;  		  
        }
#endif
	////WFIELD

#endif
}

#ifndef FOR_2D
__device__ void applyRotation(struct point* point, float rotatedViewOffsetx, float rotatedViewOffsety)
{
    point->x += rotatedViewOffsetx;
    point->y += rotatedViewOffsety;
}

__device__ void applyOnlyCamera(struct point* point, float srcX, float srcY, float srcZ, float zdist, float zr)
{
    point->x = srcX / zr;
    point->y = srcY / zr;
}

__device__ void applyDOFAndCamera(struct point* point, float srcX, float srcY, float srcZ, float zdist, float zr, int dofType, float dofScale, float dofFade, float camDOF_10, unsigned int *randStates)
{    
    float fade;    
	if (dofFade <= 1.e-6f) {
      fade = 1.0f;
    }
    else if (dofFade >= 1.0f - 1.e-6f) {
      fade = randFloat(randStates);
    }
    else {
      fade = randFloat(randStates) <= dofFade ? randFloat(randStates) : 1.0f;
    }

	float dr = fade * camDOF_10 * zdist * dofScale;
#ifdef ADD_FEATURE_DOF
    switch(dofType) {
	  case 0: // BUBBLE
	  default:
	    {
			float a = 2 * PI * randFloat(randStates);
			float dsina, dcosa;
			sincosf(a, &dsina, &dcosa);
			point->x = (srcX + dr * dcosa) / zr;
			point->y = (srcY + dr * dsina) / zr;
			break;
		}
	   case 1: // SINEBLUR
         {
		   float power = 4.2f;
		   float a = 2 * PI * randFloat(randStates);
		   float dsina, dcosa;
		   sincosf(a, &dsina, &dcosa);

           dr *= (acosf(expf(logf(randFloat(randStates)) * power) * 2.0f - 1.0f) / PI);

   		   point->x = (srcX + dr * dcosa) / zr;
		   point->y = (srcY + dr * dsina) / zr;
		   break;
         }		 
	}
#endif
}



__device__ void projectJWF(struct point *p, struct CameraViewProperties *properties, unsigned int *randStates)
{
#ifndef JWF_EXTENSIONS	
    float px, py, pz, pw;
    px = properties->matrix[0]*p->x + properties->matrix[4]*p->y + properties->matrix[8]*p->z+ properties->matrix[12];
    py = properties->matrix[1]*p->x + properties->matrix[5]*p->y + properties->matrix[9]*p->z+ properties->matrix[13];
    pz = properties->matrix[2]*p->x + properties->matrix[6]*p->y + properties->matrix[10]*p->z+ properties->matrix[14];
    pw = properties->matrix[3]*p->x + properties->matrix[7]*p->y + properties->matrix[11]*p->z+ properties->matrix[15];

    // handle Apophysis perspective perspective == 0.f ==> Ortho, perspective == 1.f ==> Normal Perspective
    pw  = 1.f + (pw - 1.f) * properties->perspective;

    if (properties->dof > 1.e-6f) {
        float zdist = properties->zpos - pz;
        float t     = randFloat(randStates) * 2.f * M_PI_F;
        float dr    = randFloat(randStates) * 0.1f * properties->dof * zdist;
        float sina, cosa;
        sincosf(t, &sina, &cosa);

        if (zdist > 0.f) {
            p->x = (px + dr*cosa)/pw;
            p->y = (py + dr*sina)/pw;
            p->z = pz/pw;
        }
        else {
            p->x = px/pw;
            p->y = py/pw;
            p->z = pz/pw;
        }
    }
    else {
        p->x = px/pw;
        p->y = py/pw;
        p->z = pz/pw;
    }
#else 
    float camPointX = properties->matrix[0]*p->x + properties->matrix[4]*p->y + properties->matrix[8]*p->z;
    float camPointY = properties->matrix[1]*p->x + properties->matrix[5]*p->y + properties->matrix[9]*p->z;
    float camPointZ = properties->matrix[2]*p->x + properties->matrix[6]*p->y + properties->matrix[10]*p->z;
	  float camDOF_10 = 0.1 * properties->camDOF; 	  
	  float area = properties->camDOFArea;
      float fade = properties->camDOFArea / 2.25f;
      float areaMinusFade = area - fade;
  	  
      camPointX += properties->camPosX;
      camPointY += properties->camPosY;
      camPointZ += properties->camPosZ;

      float zr = 1.0f - properties->perspective * camPointZ + properties->camPosZ;
      if (zr < 1.e-6f) {
        zr = 1.e-6f;
      }
      p->z = camPointZ;
#ifdef ADD_FEATURE_DOF
      if (properties->camDOF > 1.e-6f) {
        if (properties->legacyDOF) {
          float zdist = properties->camDist - camPointZ;
          if (zdist > 0.0f) {
            applyDOFAndCamera(p, camPointX, camPointY, camPointZ, zdist, zr, properties->dofType, properties->dofScale, properties->dofFade, camDOF_10, randStates);
          }
          else {
            applyOnlyCamera(p, camPointX, camPointY, camPointZ, zdist, zr);
          }
        }
        else {
          float xdist = (camPointX - properties->focusX);
          float ydist = (camPointY - properties->focusY);
          float zdist = (camPointZ - properties->focusZ);

          float dist = powf(xdist * xdist + ydist * ydist + zdist * zdist, 1.0f / properties->camDOFExponent );
          if (dist > area) {
            applyDOFAndCamera(p, camPointX, camPointY, camPointZ, zdist, zr, properties->dofType, properties->dofScale, properties->dofFade, camDOF_10, randStates);
          }
          else if (dist > areaMinusFade) {
            double scl = smootherstep(0.0f, 1.0f, (dist - areaMinusFade) / fade);
            double sclDist = scl * dist;
            applyDOFAndCamera(p, camPointX, camPointY, camPointZ, zdist, zr, properties->dofType, properties->dofScale, properties->dofFade, camDOF_10, randStates);
          }
          else {
            applyOnlyCamera(p, camPointX, camPointY, camPointZ, zdist, zr);
          }
        }
      }
      else {
#endif // ADD_FEATURE_DOF
        p->x = camPointX / zr;
        p->y = camPointY / zr;
#ifdef ADD_FEATURE_DOF
      }
#endif // ADD_FEATURE_DOF
#endif	
}

#else
__device__ 
    void applyRotation(struct point* Point, float cosRotation, float sinRotation)
{
    float x,y;
    x = Point->x-d_g_Flame.center[0];
    y = Point->y-d_g_Flame.center[1];

    Point->x = x*cosRotation - y*sinRotation + d_g_Flame.center[0];
    Point->y = x*sinRotation + y*cosRotation + d_g_Flame.center[1];
}

#endif

__device__
float4 read_image(float4 * image, int length, float index)
{
    float clampedIndex = index - floor(index);
    float scaledIndex = clampedIndex*(float)(length - 1);
    int iLow = floor(scaledIndex);
    int iHigh = ceil(scaledIndex);
    float iFract = scaledIndex - floor(scaledIndex);
    float4 c0 = image[iLow];
    float4 c1 = image[iHigh];
    return make_float4(iFract*c1.x+(1.0f-iFract)*c0.x, 
                        iFract*c1.y+(1.0f-iFract)*c0.y, 
                        iFract*c1.z+(1.0f-iFract)*c0.z, 
                        iFract*c1.w+(1.0f-iFract)*c0.w);
}

extern "C" __global__
void reductionKernal(unsigned* buffer,
                     unsigned length,
                     unsigned * result)
{
    volatile __shared__ unsigned scratch[BLOCKDIM];

    unsigned global_index = blockIdx.x*blockDim.x+threadIdx.x;

    // Perform parallel reduction
    int local_index      = threadIdx.x;
    scratch[local_index] = global_index < length ? buffer[global_index] : 0;
	__syncthreads();

    for(int offset = blockDim.x / 2; offset > 0; offset >>= 1) {
        if (local_index < offset) {
            scratch[local_index] += scratch[local_index + offset];
        }
    	__syncthreads();
    }
    if (local_index == 0) {
        result[blockIdx.x] = scratch[0];
    }
}

extern "C" __global__ void iteratePointsKernal(struct VariationListNode *d_g_varUsages,
                                uint *d_g_varUsageIndexes,
                                float *varpars,
                                float *d_g_switchMatrix,
#ifndef FOR_2D
                                struct CameraViewProperties *d_g_Camera,
#endif
                                float4* renderTarget,
                                struct point* points,
                                uint* pointIterations,
                                uint* perThreadRandSeeds,
                                float4* palette,
                                uint numColors,
                                int  paletteStepMode,
                                float epsilon,
                                uint fuseIterations,
                                int xDim,
                                int yDim,
                                uint *startingXform,
                                uint *markedCounts,
                                uint *pixelCounts,
                                uint xformPointPoolSize,
                                uint *permutations,
                                float4 *gradients,
                                uint *shuffle,
                                uint *iterationCount)
{
    __shared__ struct point activePoint[BLOCKDIM];
    __shared__ uint         randStates[WARP_SIZE];
#ifdef PARALLEL_SELECT
    __shared__ uint         rw[2];
#endif
    uint maxR            = d_g_Flame.numTrans - 1;
    uint index           = threadIdx.x; // blockDim.x should be 2 * WARP_SIZE
    uint blockIndex      = blockIdx.y * gridDim.x + blockIdx.x;
    const int ix         = (blockDim.x * blockIndex) + index;
    const uint warpIndex = ix/WARP_SIZE;
    //    const uint whichWarp = warpIndex % WARPS_PER_BLOCK;
    const uint whichWarp = warpIndex & 1;
#ifdef FOR_2D
    float sinRotation, cosRotation;
    sincosf(d_g_Flame.rotation, &sinRotation, &cosRotation);
#endif

    // want to measure the actual number of batches, suspect driver is not executing all batches
    if (blockIndex == 0 && index == 0)
        iterationCount[0] += 1;

    // Iterate some points!
    randStates[index&(WARP_SIZE-1)] = perThreadRandSeeds[ix];

    // want randStates buffers to be populated for entire block before continuing
	__syncthreads();

    uint fromXform = startingXform[warpIndex];
    uint toXform   = 0;
	
	
    for (int j = 0; j < NUM_ITERATIONS; j++)
    {
        //Pick xform for this iteration
#ifdef PARALLEL_SELECT
        float w       = randFloatWarp(randStates, whichWarp);
        rw[whichWarp] = 0;
        __syncthreads();

        uint offset = 0;
        uint windex = index & (WARP_SIZE-1);
        while (windex + offset <= maxR) {
            uint lixw = fromXform * d_g_Flame.numTrans + windex + offset;
            if (windex + offset > 0 && w >= d_g_switchMatrix[lixw - 1] && w < d_g_switchMatrix[lixw]) {
                rw[whichWarp] = windex + offset;
            }
            offset += WARP_SIZE;
        }
        __syncthreads();
        uint r = rw[whichWarp];
        toXform = r;
#else
        float w;
        w=randFloatWarp(randStates, whichWarp);
        uint r    = 0;
        while ((r < maxR) && (w > d_g_switchMatrix[fromXform * d_g_Flame.numTrans + r]))
        {
            r++;
        }
        toXform = r;
#endif
        //Now each thread chooses a point at random from the point pool.  This is done to allow each point to have a seperate xform path while retaining SIMD
        uint p               = shuffle[index + NUM_POINTS * j];
        uint fromPointIndex  = fromXform * xformPointPoolSize + NUM_POINTS*blockIndex + p;
        uint toPointIndex    = toXform   * xformPointPoolSize + NUM_POINTS*blockIndex + p;
        uint iterations      = pointIterations[fromPointIndex];
        uint varUsagesIndex  = d_g_varUsageIndexes[r];

        struct point fromPoint = points[fromPointIndex];

        //Iterate the chosen point and store it back to the pool
        iteratePoint(&d_g_varUsages[varUsagesIndex],
                     varpars,
                     &d_g_Xforms[r],
                     r,
                     epsilon,
                     &fromPoint,
                     activePoint,
                     randStates,
                     permutations,
#ifdef JWF_EXTENSIONS
                     palette,
                     numColors,
#endif
                     gradients);

#ifndef FOR_2D
        if (! isfinite(activePoint[index].x + activePoint[index].y + activePoint[index].z)) {
            // test to add back a random point (ala Flam3) to get Flam3 like images in borderline cases
            activePoint[index].x = 2.f*randFloat(randStates) - 1.f;
            activePoint[index].y = 2.f*randFloat(randStates) - 1.f;
            activePoint[index].z = 0; //2.f*randFloat(randStates) - 1.f;
            iterations           = 0;
        }

#else
        if (! isfinite(activePoint[index].x + activePoint[index].y)) {
            // test to add back a random point (ala Flam3) to get Flam3 like images in borderline cases
            activePoint[index].x = 2.f*randFloat(randStates) - 1.f;
            activePoint[index].y = 2.f*randFloat(randStates) - 1.f;
            iterations           = 0;
        }

#endif
        ++iterations;
        struct point toPoint = activePoint[index]; // capture point state before final xform application
	

        if (iterations >= fuseIterations) { // dont store until fuse for each point is finished
            markedCounts[ix]++;  // keep track of number of iterations that could mark (versus unmarked because of unfused points)

            //Prepare the point for displey.  First the final transformation is applied
            if (d_g_Flame.useFinal) {
                for (uint fIndex = 0; fIndex < d_g_Flame.numFinal; fIndex++) {
                    uint varUsagesIndex  = d_g_varUsageIndexes[d_g_Flame.numTrans + fIndex];
                    struct point tempPoint = activePoint[index];
					float pal_save = activePoint[index].pal;
                    iteratePoint(&d_g_varUsages[varUsagesIndex],
                                 varpars,
                                 &d_g_Xforms[d_g_Flame.numTrans + fIndex],
                                 d_g_Flame.numTrans  + fIndex,
                                 epsilon,
                                 &tempPoint,
                                 activePoint,
                                 randStates,
                                 permutations,
#ifdef JWF_EXTENSIONS
                                 palette,
                                 numColors,
#endif
                                 gradients);
								activePoint[index].pal = pal_save; 
                }
            }

#ifdef JWF_EXTENSIONS
         if(!activePoint[index].doHide) {
#endif			


#ifndef FOR_2D
            projectJWF(&activePoint[index], d_g_Camera, randStates);
			if(fabsf(d_g_Camera->pitch)<1.0e-6 && fabsf(d_g_Camera->yaw)<1.0e-6 && fabsf(d_g_Camera->bank)<1.0e-6) {
              applyRotation(&activePoint[index], d_g_Camera->rotatedViewOffsetx, d_g_Camera->rotatedViewOffsety);
			}
#else
            applyRotation( &activePoint[index], cosRotation, sinRotation);
#endif
            //Finally, we randomly jitter the point within a 1/2 pixel radius to obtain antialiasing
            float dr;
            dr = randFloat(randStates);
            dr = expf(d_g_Flame.supersampleWidth*sqrtf(-logf(dr)))-1.0f;
            float rn;
            rn = randFloat(randStates);
            float dtheta = (rn)*2.0f*PI;

            // mark the histogram
            float z = (d_g_Flame.clipToNDC != 0) ? activePoint[index].z : 0.f;
            int x,y;
            float sina, cosa;
            sincosf(dtheta, &sina, &cosa);

            x = floorf((((activePoint[index].x-d_g_Flame.center[0])/d_g_Flame.size[0]+.5f)*(float)xDim)+dr*cosa);
            y = floorf(((-(activePoint[index].y-d_g_Flame.center[1])/d_g_Flame.size[1]+.5f)*(float)yDim)+dr*sina);
            //And render the point to the accumulation buffer
            if ((z >= -1.f) && (z <= 1.f) && (x < xDim)&&(y < yDim)&&(x>=0)&&(y>=0)) {
              if(d_g_Flame.render_pass==1) {
                 float zBias = d_g_Flame.z_buffer_bias;
                 float zScale = d_g_Flame.z_buffer_scale;
                 float zOffset = d_g_Flame.z_buffer_shift;

                  // negative zScale: white to black (black near camera, white background)
                  int grayValue;
                  if (zScale < 0.0) {
                      int lvl = (int)(-zScale * (activePoint[index].z + zOffset) * 32767.0 + 32767.5);
                      if (lvl < 0) {
                        lvl = 0;
                      } else if (lvl > 0xffff) {
                        lvl = 0xffff;
                      }
                      grayValue = 0xffff - lvl & 0xffff;
                  }
                  // positive zScale: black to white (white near camera, black background, which is the default)
                  else {
                      int lvl = (int)(zScale * (activePoint[index].z + zOffset) * 32767.0 + 32767.5);
                      if (lvl < 0) {
                        lvl = 0;
                      } else if (lvl > 0xffff) {
                        lvl = 0xffff;
                      }
                      grayValue = lvl & 0xffff;
                  }

                  float zValue = (float)grayValue / 65536.0f;

                 if(zValue > renderTarget[y*xDim+x].x) {
                   renderTarget[y*xDim+x].x = zValue;
                   renderTarget[y*xDim+x].y = 0.0f;
                   renderTarget[y*xDim+x].z = 0.0f;
                   renderTarget[y*xDim+x].w = 1.0f;
                   pixelCounts[y*xDim+x]++;
                 }
              }
              else {
                  float4 output;
    #ifdef JWF_EXTENSIONS
                if(activePoint[index].useRgb) {
                   output.x = activePoint[index].colorR;
                   output.y = activePoint[index].colorG;
                   output.z = activePoint[index].colorB;
                   output.w = activePoint[index].colorA;
                 }
                 else {
    #endif
                    if (paletteStepMode)
                        output = read_imageStepMode(palette, numColors, activePoint[index].pal);
                    else
                        output = read_image(palette, numColors, activePoint[index].pal);
                    // output = tex1D(texRef,activePoint[threadIdx.x].pal);

    #ifdef JWF_EXTENSIONS
                } // if(activePoint[index].useRgb) {
                if(d_g_Flame.intensityAdjust!=1) {
                  output.x *= d_g_Flame.intensityAdjust;
                  output.y *= d_g_Flame.intensityAdjust;
                  output.z *= d_g_Flame.intensityAdjust;
                  output.w *= d_g_Flame.intensityAdjust;
                }
    #endif

    #ifdef USE_ATOMICS
                    float *ptr = (float *)&(renderTarget[y*xDim+x]);
                    atomicAdd(ptr,     output.x*d_g_Xforms[r].opacity);
                    atomicAdd(ptr + 1, output.y*d_g_Xforms[r].opacity);
                    atomicAdd(ptr + 2, output.z*d_g_Xforms[r].opacity);
                    atomicAdd(ptr + 3, output.w*d_g_Xforms[r].opacity);
                    atomicAdd(&pixelCounts[y*xDim+x], 1);
    #else
                    renderTarget[y*xDim+x].x += output.x*d_g_Xforms[r].opacity;
                    renderTarget[y*xDim+x].y += output.y*d_g_Xforms[r].opacity;
                    renderTarget[y*xDim+x].z += output.z*d_g_Xforms[r].opacity;
                    renderTarget[y*xDim+x].w += output.w*d_g_Xforms[r].opacity;
                    pixelCounts[y*xDim+x]++;
    #endif
                }
#ifdef JWF_EXTENSIONS
             }
         } // if(activePoint[index].doHide==0) {
#endif			
			
        }
        pointIterations[toPointIndex] = iterations;
        points[toPointIndex]          = toPoint;
        fromXform                     = toXform;
        // essential to prevent cross-warp corruption with linear
    	__syncthreads();
        
    }
    startingXform[warpIndex] = toXform;
    perThreadRandSeeds[ix]   = randStates[index&(WARP_SIZE-1)];
}

extern "C" __global__ void postProcessStep1Kernal(
                                float4* renderTarget,
                                float4* accumBuffer,
                                uint xDim,
                                uint yDim,
                                int blocksY,
                                float fuseCompensation)
{
    const uint ix = (blockDim.x*blockIdx.x)+threadIdx.x;
    const uint iy = (blockDim.y*blockIdx.y)+threadIdx.y;

    if ((ix < xDim)&&(iy < yDim)) {
        if(d_g_Flame.render_pass==1) {
            float4 rgba = accumBuffer[iy*xDim+ix];
            rgba.y = rgba.x;
            rgba.z = rgba.x;
            rgba.w = 1.0;
            accumBuffer[iy*xDim+ix] = rgba;
        }
        else {
            float k1 = (d_g_Flame.brightness*268.0f)/255.0f;
            float area = fabsf(d_g_Flame.size[0]*d_g_Flame.size[1]);
            float k2 = ((float)(xDim*yDim))/(area*fuseCompensation*((float)(NUM_ITERATIONS))*d_g_Flame.numBatches*32.f*1024.0f*((float)blocksY/32.f));
            float4 rgba = accumBuffer[iy*xDim+ix];
            float a = (k1* logf(1.0f+k2*rgba.w));
            float ls = a/rgba.w;
            rgba.x = ls*rgba.x;
            rgba.y = ls*rgba.y;
            rgba.z = ls*rgba.z;

            accumBuffer[iy*xDim+ix] = rgba;
        }
    }
}

extern "C" __global__ void postProcessStep2Kernal(
                                float4* renderTarget,
                                float4* accumBuffer,
                                uint xDim,
                                uint yDim,
                                int blocksY,
                                float fuseCompensation,
                                float4 adjust)
{

    const uint ix = (blockDim.x*blockIdx.x)+threadIdx.x;
    const uint iy = (blockDim.y*blockIdx.y)+threadIdx.y;
    if ((ix < xDim)&&(iy < yDim)) {
        if(d_g_Flame.render_pass==1) {
            float4 rgba = accumBuffer[iy*xDim+ix];
                renderTarget[iy*xDim+ix].x=rgba.x;
                renderTarget[iy*xDim+ix].y=rgba.y;
                renderTarget[iy*xDim+ix].z=rgba.z;
                renderTarget[iy*xDim+ix].w=1.0;
        }
        else {
            float k1   = (d_g_Flame.brightness*268.0f)/255.0f;
            float area = fabsf(d_g_Flame.size[0]*d_g_Flame.size[1]);
            float _k2  = ((float)(xDim*yDim))/
                    (area*fuseCompensation*((float)(NUM_ITERATIONS))*d_g_Flame.numBatches*32.f*1024.0f*((float)blocksY/32.f));
            float gammaThreshold = d_g_Flame.gammaThreshold;
            float gamma          = d_g_Flame.gamma;
            float alphaGamma     = d_g_Flame.alphaGamma;

            float4 k2   = make_float4(_k2/adjust.x, _k2/adjust.y, _k2/adjust.z, _k2/adjust.w);
            float4 rgba = accumBuffer[iy*xDim+ix];

            float4 a = make_float4(k1 * logf(1.0f + k2.x*rgba.w),
                                   k1 * logf(1.0f + k2.y*rgba.w),
                                   k1 * logf(1.0f + k2.z*rgba.w),
                                   k1 * logf(1.0f + k2.w*rgba.w));


            float4 fraction = make_float4(a.x/gammaThreshold,
                                          a.y/gammaThreshold,
                                          a.z/gammaThreshold,
                                          a.w/gammaThreshold);

            float4 alpha = make_float4(powf(a.x, 1.0f/gamma-1.0f),
                                        powf(a.y, 1.0f/gamma-1.0f),
                                        powf(a.z, 1.0f/gamma-1.0f),
                                        powf(a.w, 1.0f/gamma-1.0f));


            float alphaTx =  (1.f - fraction.x) * a.x * (powf(gammaThreshold, gamma)/gammaThreshold) + fraction.x * alpha.x;
            float alphaTy =  (1.f - fraction.y) * a.y * (powf(gammaThreshold, gamma)/gammaThreshold) + fraction.y * alpha.y;
            float alphaTz =  (1.f - fraction.z) * a.z * (powf(gammaThreshold, gamma)/gammaThreshold) + fraction.z * alpha.z;
            float alphaTw =  (1.f - fraction.w) * a.w * (powf(gammaThreshold, gamma)/gammaThreshold) + fraction.w * alpha.w;

            float4 alphaT   = make_float4(alphaTx, alphaTy, alphaTz, alphaTw);

            float4 ls = make_float4(
                d_g_Flame.vibrancy * (a.x < gammaThreshold ? alphaT.x : alpha.x),
                d_g_Flame.vibrancy * (a.y < gammaThreshold ? alphaT.y : alpha.y),
                d_g_Flame.vibrancy * (a.z < gammaThreshold ? alphaT.z : alpha.z),
                d_g_Flame.vibrancy * (a.w < gammaThreshold ? alphaT.w : alpha.w));

            float4 sign = make_float4(
                rgba.x >= 0.f ? 1.f : -1.f,
                rgba.y >= 0.f ? 1.f : -1.f,
                rgba.z >= 0.f ? 1.f : -1.f,
                rgba.w >= 0.f ? 1.f : -1.f);

            rgba.x        = ls.x*rgba.x + (1.0f-d_g_Flame.vibrancy)*sign.x*powf(fabsf(rgba.x), 1.0f/gamma);
            rgba.y        = ls.y*rgba.y + (1.0f-d_g_Flame.vibrancy)*sign.y*powf(fabsf(rgba.y), 1.0f/gamma);
            rgba.z        = ls.z*rgba.z + (1.0f-d_g_Flame.vibrancy)*sign.z*powf(fabsf(rgba.z), 1.0f/gamma);
            rgba.w        = ls.w*rgba.w + (1.0f-d_g_Flame.vibrancy)*sign.w*powf(fabsf(rgba.w), 1.0f/gamma);

            alpha.x       = powf(a.x, 1.0f/gamma);
            alpha.y       = powf(a.y, 1.0f/gamma);
            alpha.z       = powf(a.z, 1.0f/gamma);
            alpha.w       = powf(a.w, 1.0f/gamma);

            alphaT.x      = (1.f - fraction.x) * a.x * (powf(gammaThreshold, gamma)/gammaThreshold) + fraction.x * alpha.x;
            alphaT.y      = (1.f - fraction.y) * a.y * (powf(gammaThreshold, gamma)/gammaThreshold) + fraction.y * alpha.y;
            alphaT.z      = (1.f - fraction.z) * a.z * (powf(gammaThreshold, gamma)/gammaThreshold) + fraction.z * alpha.z;
            alphaT.w      = (1.f - fraction.w) * a.w * (powf(gammaThreshold, gamma)/gammaThreshold) + fraction.w * alpha.w;

            alpha.x      = fminf((a.x < gammaThreshold ? alphaT.x : alpha.x), 1.f);
            alpha.y      = fminf((a.y < gammaThreshold ? alphaT.y : alpha.y), 1.f);
            alpha.z      = fminf((a.z < gammaThreshold ? alphaT.z : alpha.z), 1.f);
            alpha.w      = fminf((a.w < gammaThreshold ? alphaT.w : alpha.w), 1.f);


            float alphaCw  = powf(a.w, 1.0f/alphaGamma);
            float alphaTCw =  (1.f - fraction.w) * a.w * (powf(gammaThreshold, alphaGamma)/gammaThreshold) + fraction.w * alphaCw;
            alphaCw        = fminf((a.w < gammaThreshold ? alphaTCw : alphaCw), 1.f);


            if (d_g_Flame.highlightPower >= 0.f) {
                rgba = RGBtoHSVHueAdjusted(rgba);
                if (rgba.z > 1.0f)
                {
                    //rgba.y /= rgba.z;
                    rgba.z = 1.0f;
                }
                rgba = HSVtoRGB(rgba);
            }
            if (isfinite(rgba.x + rgba.y + rgba.z + rgba.w))
            {
                renderTarget[iy*xDim+ix].x=rgba.x+renderTarget[iy*xDim+ix].x*(1.0f-alpha.x);
                renderTarget[iy*xDim+ix].y=rgba.y+renderTarget[iy*xDim+ix].y*(1.0f-alpha.y);
                renderTarget[iy*xDim+ix].z=rgba.z+renderTarget[iy*xDim+ix].z*(1.0f-alpha.z);
                renderTarget[iy*xDim+ix].w=alphaCw;
            }
            else {
                renderTarget[iy*xDim+ix].w=0.f;
            }
        }
    }
}

extern "C" __global__ void colorCurveRGB3ChannelsKernal(float4* accumBuffer,
                                        float* X,
                                        float* A,
                                        float* B,
                                        float* C,
                                        float* D,
                                        uint xDim,
                                        uint yDim,
                                        uint cpCount)

{
    __shared__ float xs[256];
    __shared__ float as[256];
    __shared__ float bs[256];
    __shared__ float cs[256];
    __shared__ float ds[256];

    cpCount    = cpCount > 256 ? 256 : cpCount;
    uint index = threadIdx.x;

    for (uint i = 0; index+i < cpCount; i += blockDim.x) {
        xs[index+i] = X[index+i];
        as[index+i] = A[index+i];
        cs[index+i] = C[index+i];
    }
    for (uint i = 0; index+i < cpCount-1; i += blockDim.x) {
        bs[index+i] = B[index+i];
        ds[index+i] = D[index+i];
    }
	__syncthreads();

    const uint ix = (blockDim.x*blockIdx.x)+threadIdx.x;
    const uint iy = (blockDim.y*blockIdx.y)+threadIdx.y;
    if ((ix < xDim)&&(iy < yDim))
    {
        float4 rgba = accumBuffer[iy*xDim+ix];
        // sRGB luma   0.212656 R  0.715158 G  0.072186 B
        // AdobeRGB luma
        float preluma  = 0.297361f * rgba.x + 0.627355f * rgba.y + 0.075285f * rgba.z;
        preluma       /= rgba.w;
        float postluma = curveAdjust(preluma, xs, as, bs, cs, ds, cpCount);

        if (preluma != 0.f) {
            rgba.x = postluma/preluma * rgba.x;
            rgba.y = postluma/preluma * rgba.y;
            rgba.z = postluma/preluma * rgba.z;
            rgba.w = postluma/preluma * rgba.w;
        }
        else {
            rgba.x = postluma;
            rgba.y = postluma;
            rgba.z = postluma;
            rgba.w = postluma;
        }
        accumBuffer[iy*xDim+ix] = rgba;
    }
}

extern "C" __global__ void colorCurveRGBChannelKernal(float4* accumBuffer,
                                        float* X,
                                        float* A,
                                        float* B,
                                        float* C,
                                        float* D,
                                        uint xDim,
                                        uint yDim,
                                        uint cpCount,
                                        uint channel)
{
    __shared__ float xs[256];
    __shared__ float as[256];
    __shared__ float bs[256];
    __shared__ float cs[256];
    __shared__ float ds[256];

    cpCount    = cpCount > 256 ? 256 : cpCount;
    uint index = threadIdx.x;

    for (uint i = 0; index+i < cpCount; i += blockDim.x) {
        xs[index+i] = X[index+i];
        as[index+i] = A[index+i];
        cs[index+i] = C[index+i];
    }
    for (uint i = 0; index+i < cpCount-1; i += blockDim.x) {
        bs[index+i] = B[index+i];
        ds[index+i] = D[index+i];
    }
	__syncthreads();

    const uint ix = (blockDim.x*blockIdx.x)+threadIdx.x;
    const uint iy = (blockDim.y*blockIdx.y)+threadIdx.y;
    if ((ix < xDim)&&(iy < yDim))
    {
        float4 rgba = accumBuffer[iy*xDim+ix];

        float preluma  = 0.297361f * rgba.x + 0.627355f * rgba.y + 0.075285f * rgba.z;
        switch (channel) {
            default:
            case 0:
            rgba.x = rgba.w * curveAdjust(rgba.x/rgba.w, xs, as, bs, cs, ds, cpCount);
            break;
            case 1:
            rgba.y = rgba.w * curveAdjust(rgba.y/rgba.w, xs, as, bs, cs, ds, cpCount);
            break;
            case 2:
            rgba.z = rgba.w * curveAdjust(rgba.z/rgba.w, xs, as, bs, cs, ds, cpCount);
            break;
            case 3:
            break;
        }
        float postluma  = 0.297361f * rgba.x + 0.627355f * rgba.y + 0.075285f * rgba.z;
        // maintain same luminance afterwards
        if (preluma != 0)
            rgba.w *= postluma/preluma;
        else
            rgba.w  = postluma;

        accumBuffer[iy*xDim+ix] = rgba;
    }
}

extern "C" __global__ void setBufferKernal(float4* renderTarget, float4 value, uint xDim, uint yDim)
{
    //This kernal simply fills the render target with value
    const uint ix   = (blockDim.x*blockIdx.x)+threadIdx.x;
    const uint iy   = (blockDim.y*blockIdx.y)+threadIdx.y;

    if ((ix < xDim)&&(iy < yDim))
        renderTarget[iy*xDim+ix] = value;
}

extern "C" __global__ void FlexibleDensityEstimationKernal(float* output, float* input, unsigned int xDim, unsigned int yDim,
                                                            float baseThreshold, int radius)
{
    __shared__ float filterLocal[4*(DENSITY_KERNAL_RADIUS*2+16)*(DENSITY_KERNAL_RADIUS*2+16)];
    unsigned int index    = threadIdx.x;
    const unsigned int ix = blockDim.x*blockIdx.x + index;
    const unsigned int iy = blockDim.y*blockIdx.y + threadIdx.y;
    const unsigned int lidx = (blockDim.x+radius*2)*threadIdx.y + index;
    //First, we load up the block of pixels we will be working on into shared memory
    for (unsigned int y = 0; y < blockDim.y+radius*2-threadIdx.y; y += blockDim.y)
    {
        for(unsigned int x = 0; x < blockDim.x+radius*2-index; x += blockDim.x)
        {
            unsigned toIndex4   = 4*(lidx+x+y*(blockDim.x+radius*2));
            //clamped addressing
            unsigned fromIndex4 = 4*(max(min(ix+x-radius,xDim-1),0U)+max(min(iy+y-radius,yDim-1),0U)*xDim);
            filterLocal[toIndex4]     = input[fromIndex4];
            filterLocal[toIndex4 + 1] = input[fromIndex4 + 1];
            filterLocal[toIndex4 + 2] = input[fromIndex4 + 2];
            filterLocal[toIndex4 + 3] = input[fromIndex4 + 3];
        }
    }
    __syncthreads();
    //Next, apply the actual filter
    if ((ix < xDim)&& (iy < yDim))
    {
        unsigned idx = 4*(ix+iy*xDim);
        float pntw = input[idx + 3];
        float sumx = 0.f;
        float sumy = 0.f;
        float sumz = 0.f;
        float sumw = 0.f;

        float count = 0.0f;
        for (int y = 0; y < radius*2+1; y++)
        {
            for (int x = 0; x<radius*2+1; x++)
            {
                int cellIdx   = 4*(lidx+x+y*(blockDim.x+2*radius));
                float invDist = 1.0f/(((float)(x-radius)*(float)(x-radius)+(float)(y-radius)*(float)(y-radius))+1.0f);
                float deviation = fabsf(erff((filterLocal[cellIdx + 3]-pntw)/(sqrtf(8.0f*pntw)+5.0f)));
                if (deviation<=powf(baseThreshold*.9f,sqrtf(1.0f/invDist))*powf(pntw+1.0f,-0.25f))
                {
                    sumx += filterLocal[cellIdx]*invDist;
                    sumy += filterLocal[cellIdx + 1]*invDist;
                    sumz += filterLocal[cellIdx + 2]*invDist;
                    sumw += filterLocal[cellIdx + 3]*invDist;
                    count += invDist;
                }
            }
        }
        sumx/=count;
        sumy/=count;
        sumz/=count;
        sumw/=count;
        //And store the result
        output[idx]     = isfinite(sumx)  ? sumx : 0.f;
        output[idx + 1] =  isfinite(sumy) ? sumy : 0.f;
        output[idx + 2] =  isfinite(sumz) ? sumz : 0.f;
        output[idx + 3] =  isfinite(sumw) ? sumw : 0.f;
    }
}

extern "C" __global__ void RGBA128FtoRGBA32UKernal(uchar4* output, float4* input, uint xDim, uint yDim, int useAlpha)
{
    //This kernal converts a 32bit per channel floating point image to a 8bit per channel integer image
    const uint ix = (blockDim.x*blockIdx.x)+threadIdx.x;
    const uint iy = (blockDim.y*blockIdx.y)+threadIdx.y;
    if ((ix < xDim)&&(iy < yDim))
    {
        if (useAlpha)
        {
//            // straight alpha
//            if (isfinite(input[iy*xDim+ix].x+input[iy*xDim+ix].y+input[iy*xDim+ix].z+input[iy*xDim+ix].w) && (input[iy*xDim+ix].w!=0.0f))
//                output[iy*xDim+ix] = make_uchar4(
//                    fmaxf(fminf(input[iy*xDim+ix].x/input[iy*xDim+ix].w,1.0f),0.0f)*255.0f,
//                    fmaxf(fminf(input[iy*xDim+ix].y/input[iy*xDim+ix].w,1.0f),0.0f)*255.0f,
//                    fmaxf(fminf(input[iy*xDim+ix].z/input[iy*xDim+ix].w,1.0f),0.0f)*255.0f,
//                    fmaxf(fminf(input[iy*xDim+ix].w,1.0f),0.0f)*255.0f);
            // premultiplied alpha
            if (isfinite(input[iy*xDim+ix].x+input[iy*xDim+ix].y+input[iy*xDim+ix].z+input[iy*xDim+ix].w) && (input[iy*xDim+ix].w!=0.0f)) {
                float alpha = fmaxf(fminf(input[iy*xDim+ix].w,1.0f),0.0f)*255.0f;
                output[iy*xDim+ix] = make_uchar4(
                    fmaxf(fminf(input[iy*xDim+ix].x/input[iy*xDim+ix].w,1.0f),0.0f)*alpha,
                    fmaxf(fminf(input[iy*xDim+ix].y/input[iy*xDim+ix].w,1.0f),0.0f)*alpha,
                    fmaxf(fminf(input[iy*xDim+ix].z/input[iy*xDim+ix].w,1.0f),0.0f)*alpha,
                    alpha);
            }
            else
                output[iy*xDim+ix]=make_uchar4(0,0,0,0);
            }
        else
        {
        if (isfinite(input[iy*xDim+ix].x+input[iy*xDim+ix].y+input[iy*xDim+ix].z+input[iy*xDim+ix].w))
            output[iy*xDim+ix] = make_uchar4(
                fmaxf(fminf(input[iy*xDim+ix].x,1.0f),0.0f)*255.0f,
                fmaxf(fminf(input[iy*xDim+ix].y,1.0f),0.0f)*255.0f,
                fmaxf(fminf(input[iy*xDim+ix].z,1.0f),0.0f)*255.0f,
                255);
        else
            output[iy*xDim+ix]=make_uchar4(0,0,0,255);
        }
    }
}

extern "C" __global__ void RGBA128FtoBGRA32UKernal(uchar4* output, float4* input, uint xDim, uint yDim)
{
    //This kernal converts a 32bit per channel floating point image to a 8bit per channel integer image
    // in BGRA format for little endian Intel with premultiplied alpha
    const uint ix = (blockDim.x*blockIdx.x)+threadIdx.x;
    const uint iy = (blockDim.y*blockIdx.y)+threadIdx.y;
    if ((ix < xDim)&&(iy < yDim))
    {
        if (isfinite(input[iy*xDim+ix].x+input[iy*xDim+ix].y+input[iy*xDim+ix].z+input[iy*xDim+ix].w))
        output[iy*xDim+ix] = make_uchar4(
            fmaxf(fminf(input[iy*xDim+ix].z,1.0f),0.0f)*255.0f, // blue
            fmaxf(fminf(input[iy*xDim+ix].y,1.0f),0.0f)*255.0f, // green
            fmaxf(fminf(input[iy*xDim+ix].x,1.0f),0.0f)*255.0f, // red
            255);
        else
            output[iy*xDim+ix]=make_uchar4(0,0,0,255);
    }
}

extern "C" __global__ void RGBA128FtoRGBA64UKernal(ushort4* output, float4* input, uint xDim, uint yDim, int useAlpha)
{
    //This kernal converts a 32bit per channel floating point image to a 16bit per channel integer image
    const uint ix = (blockDim.x*blockIdx.x)+threadIdx.x;
    const uint iy = (blockDim.y*blockIdx.y)+threadIdx.y;
    if ((ix < xDim)&&(iy < yDim))
    {
        if (useAlpha)
        {
//            // straight alpha
//            if (isfinite(input[iy*xDim+ix].x+input[iy*xDim+ix].y+input[iy*xDim+ix].z+input[iy*xDim+ix].w) && (input[iy*xDim+ix].w!=0.0f))
//                output[iy*xDim+ix] = make_ushort4(
//                    fmaxf(fminf(input[iy*xDim+ix].x/input[iy*xDim+ix].w,1.0f),0.0f)*65535.0f,
//                    fmaxf(fminf(input[iy*xDim+ix].y/input[iy*xDim+ix].w,1.0f),0.0f)*65535.0f,
//                    fmaxf(fminf(input[iy*xDim+ix].z/input[iy*xDim+ix].w,1.0f),0.0f)*65535.0f,
//                    fmaxf(fminf(input[iy*xDim+ix].w,1.0f),0.0f)*65535.0f);
            // premultiplied alpha
            if (isfinite(input[iy*xDim+ix].x+input[iy*xDim+ix].y+input[iy*xDim+ix].z+input[iy*xDim+ix].w) && (input[iy*xDim+ix].w!=0.0f)) {
                float alpha = fmaxf(fminf(input[iy*xDim+ix].w,1.0f),0.0f)*65535.0f;
                output[iy*xDim+ix] = make_ushort4(
                    fmaxf(fminf(input[iy*xDim+ix].x/input[iy*xDim+ix].w,1.0f),0.0f)*alpha,
                    fmaxf(fminf(input[iy*xDim+ix].y/input[iy*xDim+ix].w,1.0f),0.0f)*alpha,
                    fmaxf(fminf(input[iy*xDim+ix].z/input[iy*xDim+ix].w,1.0f),0.0f)*alpha,
                    alpha);
            }
            else
                output[iy*xDim+ix]=make_ushort4(0,0,0,0);
        }
        else
        {
            if (isfinite(input[iy*xDim+ix].x+input[iy*xDim+ix].y+input[iy*xDim+ix].z+input[iy*xDim+ix].w))
                output[iy*xDim+ix] = make_ushort4(
                    fmaxf(fminf(input[iy*xDim+ix].x,1.0f),0.0f)*65535.0f,
                    fmaxf(fminf(input[iy*xDim+ix].y,1.0f),0.0f)*65535.0f,
                    fmaxf(fminf(input[iy*xDim+ix].z,1.0f),0.0f)*65535.0f,
                    65535);
            else
                output[iy*xDim+ix]=make_ushort4(0,0,0,65535);
        }
    }
}

extern "C" __global__ void RGBA128FtoRGBA128FKernal(float4* output, float4* input, uint xDim, uint yDim, int useAlpha)
{
    const uint ix = (blockDim.x*blockIdx.x)+threadIdx.x;
    const uint iy = (blockDim.y*blockIdx.y)+threadIdx.y;
    if ((ix < xDim)&&(iy < yDim))
    {
        if (useAlpha)
        {
//            // straight alpha
//            if (isfinite(input[iy*xDim+ix].x+input[iy*xDim+ix].y+input[iy*xDim+ix].z+input[iy*xDim+ix].w) && (input[iy*xDim+ix].w!=0.0f))
//                output[iy*xDim+ix] = make_float4(
//                    fmaxf(fminf(input[iy*xDim+ix].x/input[iy*xDim+ix].w,1.0f),0.0f),
//                    fmaxf(fminf(input[iy*xDim+ix].y/input[iy*xDim+ix].w,1.0f),0.0f),
//                    fmaxf(fminf(input[iy*xDim+ix].z/input[iy*xDim+ix].w,1.0f),0.0f),
//                    fmaxf(fminf(input[iy*xDim+ix].w,1.0f),0.0f));
            // premultiplied alpha
            if (isfinite(input[iy*xDim+ix].x+input[iy*xDim+ix].y+input[iy*xDim+ix].z+input[iy*xDim+ix].w) && (input[iy*xDim+ix].w!=0.0f)) {
                float alpha = fmaxf(fminf(input[iy*xDim+ix].w,1.0f),0.0f);
                output[iy*xDim+ix] = make_float4(
                    fmaxf(fminf(input[iy*xDim+ix].x/input[iy*xDim+ix].w,1.0f),0.0f)*alpha,
                    fmaxf(fminf(input[iy*xDim+ix].y/input[iy*xDim+ix].w,1.0f),0.0f)*alpha,
                    fmaxf(fminf(input[iy*xDim+ix].z/input[iy*xDim+ix].w,1.0f),0.0f)*alpha,
                alpha);
            }
            else
                output[iy*xDim+ix]=make_float4(0.f,0.f,0.f,0.f);
        }
        else
        {
            if (isfinite(input[iy*xDim+ix].x+input[iy*xDim+ix].y+input[iy*xDim+ix].z+input[iy*xDim+ix].w))
                output[iy*xDim+ix] = make_float4(
                    fmaxf(fminf(input[iy*xDim+ix].x,1.0f),0.0f),
                    fmaxf(fminf(input[iy*xDim+ix].y,1.0f),0.0f),
                    fmaxf(fminf(input[iy*xDim+ix].z,1.0f),0.0f),
                    1.f);
            else
                output[iy*xDim+ix]=make_float4(0.f,0.f,0.f,1.f);
        }
    }
}

extern "C" __global__ void MergeKernal(float4* accum, float4* input, uint xDim, uint yDim)
{
    const uint ix = (blockDim.x*blockIdx.x)+threadIdx.x;
    const uint iy = (blockDim.y*blockIdx.y)+threadIdx.y;
    if ((ix < xDim)&&(iy < yDim))
    {
        accum[iy*xDim+ix].x += input[iy*xDim+ix].x;
        accum[iy*xDim+ix].y += input[iy*xDim+ix].y;
        accum[iy*xDim+ix].z += input[iy*xDim+ix].z;
        accum[iy*xDim+ix].w += input[iy*xDim+ix].w;
    }
}

extern "C" __global__ void readChannelKernel(float* output, float4* input, uint xDim, uint yDim, uint channel)
{
    const uint ix   = (blockDim.x*blockIdx.x)+threadIdx.x;
    const uint iy   = (blockDim.y*blockIdx.y)+threadIdx.y;
    if ((ix < xDim)&&(iy < yDim))
    {
        switch (channel) {
            default:
            case 0:
            output[iy*xDim+ix] = input[iy*xDim+ix].x;
            break;
            case 1:
            output[iy*xDim+ix] = input[iy*xDim+ix].y;
            break;
            case 2:
            output[iy*xDim+ix] = input[iy*xDim+ix].z;
            break;
            case 3:
            output[iy*xDim+ix] = input[iy*xDim+ix].w;
            break;
        }
    }
}

extern "C" __global__ void writeChannelKernel(float4* output, float* input, uint xDim, uint yDim, uint channel)
{
    const uint ix   = (blockDim.x*blockIdx.x)+threadIdx.x;
    const uint iy   = (blockDim.y*blockIdx.y)+threadIdx.y;
    if ((ix < xDim)&&(iy < yDim))
    {
        switch (channel) {
            default:
            case 0:
            output[iy*xDim+ix].x = input[iy*xDim+ix];
            break;
            case 1:
            output[iy*xDim+ix].y = input[iy*xDim+ix];
            break;
            case 2:
            output[iy*xDim+ix].z = input[iy*xDim+ix];
            break;
            case 3:
            output[iy*xDim+ix].w = input[iy*xDim+ix];
            break;
        }
    }
}

extern "C" __global__ void writeChannelStripedKernel(float4* output,
                                        float* input,
                                        uint xDim,
                                        uint yDim,
                                        uint channel,
                                        uint supersample)
{
    const uint resampledXdim = xDim / supersample;
    const uint ix   = (blockDim.x*blockIdx.x)+threadIdx.x;
    const uint iy   = (blockDim.y*blockIdx.y)+threadIdx.y;
    if ((ix < xDim) && (iy < yDim) && (ix % supersample == 0) && (iy % supersample == 0))
    {
        const uint x = ix / supersample;
        const uint y = iy / supersample;
        switch (channel) {
            default:
            case 0:
            output[y*resampledXdim+x].x = input[iy*xDim+ix];
            break;
            case 1:
            output[y*resampledXdim+x].y = input[iy*xDim+ix];
            break;
            case 2:
            output[y*resampledXdim+x].z = input[iy*xDim+ix];
            break;
            case 3:
            output[y*resampledXdim+x].w = input[iy*xDim+ix];
            break;
        }
    }
}


///////////////////////////////////////////////////////////////////////////////
/*
* Copyright 1993-2007 NVIDIA Corporation.  All rights reserved.
* OpenCL port & resampling kernels Copyright 2014 Centcom Inc. All rights reserved.
*
* NOTICE TO USER:
*
* This source code is subject to NVIDIA ownership rights under U.S. and
* international Copyright laws.  Users and possessors of this source code
* are hereby granted a nonexclusive, royalty-free license to use this code
* in individual and commercial software.
*
* NVIDIA MAKES NO REPRESENTATION ABOUT THE SUITABILITY OF THIS SOURCE
* CODE FOR ANY PURPOSE.  IT IS PROVIDED "AS IS" WITHOUT EXPRESS OR
* IMPLIED WARRANTY OF ANY KIND.  NVIDIA DISCLAIMS ALL WARRANTIES WITH
* REGARD TO THIS SOURCE CODE, INCLUDING ALL IMPLIED WARRANTIES OF
* MERCHANTABILITY, NONINFRINGEMENT, AND FITNESS FOR A PARTICULAR PURPOSE.
* IN NO EVENT SHALL NVIDIA BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL,
* OR CONSEQUENTIAL DAMAGES, OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS
* OF USE, DATA OR PROFITS,  WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE
* OR OTHER TORTIOUS ACTION,  ARISING OUT OF OR IN CONNECTION WITH THE USE
* OR PERFORMANCE OF THIS SOURCE CODE.
*
* U.S. Government End Users.   This source code is a "commercial item" as
* that term is defined at  48 C.F.R. 2.101 (OCT 1995), consisting  of
* "commercial computer  software"  and "commercial computer software
* documentation" as such terms are  used in 48 C.F.R. 12.212 (SEPT 1995)
* and is provided to the U.S. Government only as a commercial end item.
* Consistent with 48 C.F.R.12.212 and 48 C.F.R. 227.7202-1 through
* 227.7202-4 (JUNE 1995), all U.S. Government End Users acquire the
* source code with only those rights set forth herein.
*
* Any use of this source code in individual and commercial software must
* include, in the user documentation and internal comments to the code,
* the above Disclaimer and U.S. Government End Users Notice.
*/

////////////////////////////////////////////////////////////////////////////////
// Row convolution filter
////////////////////////////////////////////////////////////////////////////////

// Assuming ROW_TILE_W, KERNEL_RADIUS_ALIGNED and dataW
// are multiples of coalescing granularity size,
// all global memory operations are coalesced in convolutionRowGPU()
#define            ROW_TILE_W 128
#define KERNEL_RADIUS_ALIGNED 16

// Assuming COLUMN_TILE_W and dataW are multiples
// of coalescing granularity size, all global memory operations
// are coalesced in convolutionColumnGPU()
#define COLUMN_TILE_W 16
#define COLUMN_TILE_H 48

extern "C" __global__ void convolutionRowsKernel(float *d_Kernel,
                                float *d_Result,
                                float *d_Data,
                                int dataW,
                                int dataH,
                                int KERNEL_RADIUS
){
    //Data cache
    __shared__ float data[KERNEL_RADIUS_ALIGNED + ROW_TILE_W + KERNEL_RADIUS_ALIGNED];

    //Current tile and apron limits, relative to row start
    const int         tileStart = blockIdx.x * ROW_TILE_W;
    const int           tileEnd = tileStart + ROW_TILE_W - 1;
    const int        apronStart = tileStart - KERNEL_RADIUS;
    const int          apronEnd = tileEnd   + KERNEL_RADIUS;

    //Clamp tile and apron limits by image borders
    const int    tileEndClamped = min(tileEnd, dataW - 1);
    const int apronStartClamped = max(apronStart, 0);
    const int   apronEndClamped = min(apronEnd, dataW - 1);

    //Row start index in d_Data[]
    const int          rowStart = blockIdx.y * dataW;

    //Aligned apron start. Assuming dataW and ROW_TILE_W are multiples
    //of half-warp size, rowStart + apronStartAligned is also a
    //multiple of half-warp size, thus having proper alignment
    //for coalesced d_Data[] read.
    const int apronStartAligned = tileStart - KERNEL_RADIUS_ALIGNED;

    const int loadPos = apronStartAligned + threadIdx.x;
    //Set the entire data cache contents
    //Load global memory values, if indices are within the image borders,
    //or initialize with zeroes otherwise
    if(loadPos >= apronStart){
        const int smemPos = loadPos - apronStart;

        // out of bounds set to 0
        // data[smemPos] =
        //     ((loadPos >= apronStartClamped) && (loadPos <= apronEndClamped)) ? d_Data[rowStart + loadPos] : 0;

        // reflected at boundary
        // data[smemPos] =
        // loadPos < apronStartClamped ? d_Data[rowStart + apronStartClamped + apronStartClamped - loadPos] :
        //  (loadPos > apronEndClamped ? d_Data[rowStart + apronEndClamped   + apronEndClamped   - loadPos] :
        //     d_Data[rowStart + loadPos]);

        // clamp to border
        data[smemPos] =
            loadPos < apronStartClamped ? d_Data[rowStart + apronStartClamped] :
            (loadPos > apronEndClamped ? d_Data[rowStart + apronEndClamped] :
            d_Data[rowStart + loadPos]);
    }


    //Ensure the completness of the loading stage
    //because results, emitted by each thread depend on the data,
    //loaded by another threads
	__syncthreads();

    const int writePos = tileStart + threadIdx.x;

    //Assuming dataW and ROW_TILE_W are multiples of half-warp size,
    //rowStart + tileStart is also a multiple of half-warp size,
    //thus having proper alignment for coalesced d_Result[] write.
    if(writePos <= tileEndClamped){
        const int smemPos = writePos - apronStart;
        float sum = 0;

        for(int k = -KERNEL_RADIUS; k <= KERNEL_RADIUS; k++)
            sum += data[smemPos + k] * d_Kernel[KERNEL_RADIUS - k];

        d_Result[rowStart + writePos] = isfinite(sum) ? sum : 0.f;
    }
}

extern "C" __global__ void convolutionColumnsKernel(float *d_Kernel,
                                        float *d_Result,
                                        float *d_Data,
                                        int dataW,
                                        int dataH,
                                        int smemStride,
                                        int gmemStride,
                                        int KERNEL_RADIUS
){
    //Data cache
    __shared__ float data[COLUMN_TILE_W * (KERNEL_RADIUS_ALIGNED + COLUMN_TILE_H + KERNEL_RADIUS_ALIGNED)];

    //Current tile and apron limits, in rows
    const int         tileStart = blockIdx.y * COLUMN_TILE_H;
    const int           tileEnd = tileStart + COLUMN_TILE_H - 1;
    const int        apronStart = tileStart - KERNEL_RADIUS;
    const int          apronEnd = tileEnd   + KERNEL_RADIUS;

    //Clamp tile and apron limits by image borders
    const int    tileEndClamped = min(tileEnd, dataH - 1);
    const int apronStartClamped = max(apronStart, 0);
    const int   apronEndClamped = min(apronEnd, dataH - 1);

    //Current column index
    const int       columnStart = blockIdx.x * COLUMN_TILE_W + threadIdx.x;

    //Shared and global memory indices for current column
    int smemPos    = threadIdx.y * COLUMN_TILE_W + threadIdx.x;
    int gmemPos    = (apronStart + threadIdx.y) * dataW + columnStart;
    int gmemPosMin = columnStart;
    int gmemPosMax = columnStart + dataW * (dataH - 1);

    //Cycle through the entire data cache
    //Load global memory values, if indices are within the image borders,
    //or initialize with zero otherwise
    for(int y = apronStart + threadIdx.y; y <= apronEnd; y += blockDim.y){
        // out of bounds set to 0
        //data[smemPos] =
        //    ((y >= apronStartClamped) && (y <= apronEndClamped)) ? d_Data[gmemPos] : 0;

        // reflected at boundary
        // data[smemPos] =
        //     y < apronStartClamped ? d_Data[gmemPosMin + (apronStartClamped - y) * dataW] :
        //    (y > apronEndClamped   ? d_Data[gmemPosMax + (apronEndClamped   - y) * dataW] : d_Data[gmemPos]);

        // clamp to border
        data[smemPos] =
            y < apronStartClamped ? d_Data[gmemPosMin] :
            (y > apronEndClamped   ? d_Data[gmemPosMax] : d_Data[gmemPos]);

        smemPos += smemStride;
        gmemPos += gmemStride;
    }

    //Ensure the completness of the loading stage
    //because results, emitted by each thread depend on the data,
    //loaded by another threads
	__syncthreads();

    //Shared and global memory indices for current column
    smemPos = (threadIdx.y + KERNEL_RADIUS) * COLUMN_TILE_W + threadIdx.x;
    gmemPos = (tileStart + threadIdx.y) * dataW + columnStart;

    //Cycle through the tile body, clamped by image borders
    //Calculate and output the results
    for(int y = tileStart + threadIdx.y; y <= tileEndClamped; y += blockDim.y){
        float sum = 0;

        for(int k = -KERNEL_RADIUS; k <= KERNEL_RADIUS; k++)
            sum += data[smemPos + k * COLUMN_TILE_W] * d_Kernel[KERNEL_RADIUS - k];

        d_Result[gmemPos] = isfinite(sum) ? sum : 0.f;
        smemPos += smemStride;
        gmemPos += gmemStride;
    }
}
