/*
 JWildfireC - an external C-based fractal-flame-renderer for JWildfire 
 Copyright (C) 2012 Andreas Maschke

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
#ifndef __JWF_MATH_H__
#define __JWF_MATH_H__

#include <math.h>
#include "jwf_FastMath.h"

//#define JWF_PREC_DOUBLE

#undef M_PI
#define M_PI 3.1415926535898

#undef M_PI_2
#define M_PI_2 (M_PI * 0.5)

#undef M_PI_4
#define M_PI_4 (M_PI * 0.25)

#undef M_1_PI
#define M_1_PI (1.0 / M_PI)

#undef M_2_PI
#define M_2_PI (2.0 / M_PI)

#undef M_2PI
#define M_2PI (2.0 * M_PI)

#ifndef TRUE
#define TRUE 1
#endif

#ifndef FALSE
#define FALSE 0
#endif

#define JWF_MIN(a,b) (((a)<(b))?(a):(b))
#define JWF_MAX(a,b) (((a)>(b))?(a):(b))

#define JWF_PREC_DOUBLE

#ifdef JWF_PREC_DOUBLE
#define JWF_FLOAT double
#define JWF_ACOS acos
#define JWF_ACOSH acosh
#define JWF_ASIN asin
#define JWF_ATAN atan
#define JWF_ATAN2 fast_atan2
#define JWF_CEIL ceil
#define JWF_COS fast_cos
#define JWF_COSH cosh
#define JWF_ERF erf
#define JWF_EXP exp
#define JWF_FABS fabs
#define JWF_FLOOR floor
#define JWF_FMOD fmod
#define JWF_FRAC frac
#define JWF_LOG log
#define JWF_LOG10 log10
#define JWF_POW pow
#define JWF_RINT rint
#define JWF_ROUND round
#define JWF_SIN fast_sin
#define JWF_SINH sinh
#define JWF_SQRT sqrt
#define JWF_TAN fast_tan
#define JWF_SINCOS fast_sincos

#ifdef EPSILON
#undef EPSILON
#endif
#define EPSILON 1.0e-8

#else // #ifdef JWF_PREC_DOUBLE
#define JWF_FLOAT float

#define JWF_ACOS acosf
#define JWF_ACOSH acoshf
#define JWF_ASIN asinf
#define JWF_ATAN atanf
#define JWF_ATAN2 atan2f
#define JWF_CEIL ceilf
#define JWF_COS cosf
#define JWF_COSH coshf
#define JWF_ERF erff
#define JWF_EXP expf
#define JWF_FABS fabsf
#define JWF_FLOOR floorf
#define JWF_FMOD fmodf
#define JWF_FRAC fracf
#define JWF_LOG logf
#define JWF_LOG10 log10f
#define JWF_POW powf
#define JWF_RINT rintf
#define JWF_ROUND roundf
#define JWF_SIN sinf
#define JWF_SINH sinhf
#define JWF_SQRT sqrtf
#define JWF_TAN tanf
#define JWF_SINCOS sincosf

inline JWF_SINCOS (JWF_FLOAT a, JWF_FLOAT *sine, JWF_FLOAT *cosine) {
	*sine = sinf(a);
	*cosine = cosf(a);
}


#ifdef EPSILON
#undef EPSILON
#endif
#define EPSILON 1.0e-6

#endif // #ifdef JWF_PREC_DOUBLE

inline JWF_FLOAT JWF_SQR(JWF_FLOAT a) {
	return a*a;
}

inline JWF_FLOAT JWF_TRUNC(JWF_FLOAT value) {
  return (value < 0) ? JWF_CEIL(value) : JWF_FLOOR(value);
}

inline JWF_FLOAT JWF_FRAC(JWF_FLOAT value) {
  return value - JWF_TRUNC(value);
}

#endif // __JWF_MATH_H__
