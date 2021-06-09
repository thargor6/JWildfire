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
__device__ float lerpf(float a, float b, float p) {
    return a + (b - a) * p;
}

__device__ float sqrtf_safe(float x) {
  if (x <= 0.0f)
    return 0.0f;
  else	
    return sqrtf(x);
}


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


__device__ void iteratePoint(struct VariationListNode *varUsageList,
                float *varpars,
                struct xForm* xform,
                uint xformIndex,
                float epsilon,
                struct point *fromPoint,
                struct point *activePoint,
                unsigned int *randStates,
                uint *permutations,
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
    while ((varIndex = varUsageList->variationID) != 0) {
        float *varparCluster = &varpars[varUsageList->varparOffset];
        switch (varIndex) {
            //Now apply the Variations
            __VARIATION_SWITCH_CASES__
            default:
            break;
        }
        varUsageList++;
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
	point->z = srcZ;	
}

__device__ void applyDOFAndCamera(struct point* point, float srcX, float srcY, float srcZ, float zdist, float zr, int dofType, float dofScale, float dofFade, float camDOF_10, float rnd1, float rnd2)
{    
    float fade;    
	if (dofFade <= 1.e-6f) {
      fade = 1.0f;
    }
    else if (dofFade >= 1.0f - 1.e-6f) {
      fade = rnd1;
    }
    else {
      fade = rnd2 <= dofFade ? rnd1 : 1.0f;
    }

	float dr = fade * camDOF_10 * zdist * dofScale;

    switch(dofType) {
	  case 0: // BUBBLE
	  default:
	    {
			float a = 2.0f * PI * rnd2;
			float dsina, dcosa;
			sincosf(a, &dsina, &dcosa);
			point->x = (srcX + dr * dcosa) / zr;
			point->y = (srcY + dr * dsina) / zr;
			point->z = srcZ;
			break;
		}
	   case 1: // SINEBLUR
         {
		   float power = 4.2f;
		   float a = 2.0f * PI * rnd2;
		   float dsina, dcosa;
		   sincosf(a, &dsina, &dcosa);

           dr *= (acosf(expf(logf(rnd1) * power) * 2.0f - 1.0f) / PI);

   		   point->x = (srcX + dr * dcosa) / zr;
		   point->y = (srcY + dr * dsina) / zr;
		   point->z = srcZ;
		   break;
         }		 
	}

}

__device__ float clamp(float val, float min, float  max) {
    return fmaxf(min, fminf(max, val));
}

__device__ float smootherstep(float edge0, float edge1, float x) {
    x = clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
    return x * x * x * (x * (x * 6.f - 15.f) + 10.f);
  }

__device__ void projectJWF(struct point *p, struct CameraViewProperties *properties,
float rnd1, float rnd2)
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
        float t     = rnd1 * 2.f * M_PI_F;
        float dr    = rnd2 * 0.1f * properties->dof * zdist;
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
    float camPointX = properties->matrix[0]*p->x + properties->matrix[4]*p->y + properties->matrix[8]*p->z+ properties->matrix[12];
    float camPointY = properties->matrix[1]*p->x + properties->matrix[5]*p->y + properties->matrix[9]*p->z+ properties->matrix[13];
    float camPointZ = properties->matrix[2]*p->x + properties->matrix[6]*p->y + properties->matrix[10]*p->z+ properties->matrix[14];
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
	  
      if (properties->camDOF > 1.e-6f) {
        if (properties->legacyDOF) {
          float zdist = properties->camDist - camPointZ;
          if (zdist > 0.0f) {
            applyDOFAndCamera(p, camPointX, camPointY, camPointZ, zdist, zr, properties->dofType, properties->dofScale, properties->dofFade, camDOF_10, rnd1, rnd2);
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
            applyDOFAndCamera(p, camPointX, camPointY, camPointZ, zdist, zr, properties->dofType, properties->dofScale, properties->dofFade, camDOF_10, rnd1, rnd2);
          }
          else if (dist > areaMinusFade) {
            double scl = smootherstep(0.0f, 1.0f, (dist - areaMinusFade) / fade);
            double sclDist = scl * dist;
            applyDOFAndCamera(p, camPointX, camPointY, camPointZ, zdist, zr, properties->dofType, properties->dofScale, properties->dofFade, camDOF_10, rnd1, rnd2);
          }
          else {
            applyOnlyCamera(p, camPointX, camPointY, camPointZ, zdist, zr);
          }
        }
      }
      else {
        p->x = camPointX / zr;
        p->y = camPointY / zr;
        p->y = camPointY / zr;
      }
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
                     gradients);

#ifndef FOR_2D
        if (! isfinite(activePoint[index].x + activePoint[index].y + activePoint[index].z)) {
            // test to add back a random point (ala Flam3) to get Flam3 like images in borderline cases
            activePoint[index].x = 2.f*randFloat(randStates) - 1.f;
            activePoint[index].y = 2.f*randFloat(randStates) - 1.f;
            activePoint[index].z = 0.f; //2.f*randFloat(randStates) - 1.f;
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
                                 gradients);
								activePoint[index].pal = pal_save; 
                }
            }

#ifdef JWF_EXTENSIONS
         if(!activePoint[index].doHide) {
#endif			

#ifndef FOR_2D
            projectJWF(&activePoint[index], d_g_Camera, RANDFLOAT(), RANDFLOAT());
            applyRotation(&activePoint[index], d_g_Camera->rotatedViewOffsetx, d_g_Camera->rotatedViewOffsety);
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
            if ((z >= -1.f) && (z <= 1.f) && (x < xDim)&&(y < yDim)&&(x>=0)&&(y>=0))
            {
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
    if ((ix < xDim)&&(iy < yDim))
    {
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
    if ((ix < xDim)&&(iy < yDim))
    {
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

