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

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class RippleFunc: public Variation {
public:
	RippleFunc() {
    frequency = 2.0;
    velocity = 1.0;
    amplitude = 0.5;
    centerx = 0.0;
    centery = 0.0;
    phase = 0.0;
    scale = 1.0;
    fixed_dist_calc = false;
		initParameterNames(8, "frequency", "velocity", "amplitude", "centerx", "centery","phase","scale","fixed_dist_calc");
	}

	const char* getName() const {
		return "ripple";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "frequency") == 0) {
			frequency = pValue;
		}
		else if (strcmp(pName, "velocity") == 0) {
			velocity = pValue;
		}
		else if (strcmp(pName, "amplitude") == 0) {
			amplitude = pValue;
		}
		else if (strcmp(pName, "centerx") == 0) {
			centerx = pValue;
		}
		else if (strcmp(pName, "centery") == 0) {
			centery = pValue;
		}
		else if (strcmp(pName, "phase") == 0) {
			phase = pValue;
		}
		else if (strcmp(pName, "scale") == 0) {
			scale = pValue;
		}
		else if (strcmp(pName, "fixed_dist_calc") == 0) {
			fixed_dist_calc = FTOI(pValue)!=0;
		}
  }

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    // Ripple by Xyrus02, http://xyrus02.deviantart.com/art/Ripple-Plugin-for-Apophysis-154713493
    //align input x, y to given center and multiply with scale
    JWF_FLOAT x = (pAffineTP->x * _s) - centerx, y = (pAffineTP->y * _s) + centery;

    // calculate distance from center but constrain it to EPS
    JWF_FLOAT d = fixed_dist_calc ? JWF_SQRT(x * x + y * y) : JWF_SQRT(x * x * y * y);
    if (d < EPSILON)
      d = EPSILON;

    // normalize (x,y)
    JWF_FLOAT nx = x / d, ny = y / d;

    // calculate cosine wave with given frequency, velocity
    // and phase based on the distance to center
    JWF_FLOAT wave = JWF_COS(_f * d - _vxp);

    // calculate the wave offsets
    JWF_FLOAT d1 = wave * _pxa + d, d2 = wave * _pixa + d;

    // we got two offsets, so we also got two new positions (u,v)
    JWF_FLOAT u1 = (centerx + nx * d1), v1 = (-centery + ny * d1);
    JWF_FLOAT u2 = (centerx + nx * d2), v2 = (-centery + ny * d2);

    // interpolate the two positions by the given phase and
    // invert the multiplication with scale from before
    pVarTP->x = pAmount * (lerp(u1, u2, _p)) * _is;
    pVarTP->y = pAmount * (lerp(v1, v2, _p)) * _is;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	RippleFunc* makeCopy() {
		return new RippleFunc(*this);
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
    // some variables are settled in another range for edit comfort
    // - transform them
    _f = frequency * 5;
    JWF_FLOAT a = amplitude * 0.01;
    _p = phase * M_2PI - M_PI;

    // scale must not be zero
    _s = scale == 0 ? EPSILON : scale;

    // we will need the inverse scale
    _is = 1.0 / _s;

    // pre-multiply velocity+phase, phase+amplitude and (PI-phase)+amplitude
    _vxp = velocity * _p;
    _pxa = _p * a;
    _pixa = (M_PI - _p) * a;
	}

private:
	double frequency;
  JWF_FLOAT velocity;
  JWF_FLOAT amplitude;
  JWF_FLOAT centerx;
  JWF_FLOAT centery;
  JWF_FLOAT phase;
  JWF_FLOAT scale;
  bool fixed_dist_calc;

  JWF_FLOAT lerp(double a, JWF_FLOAT b, JWF_FLOAT p) {
    return a + (b - a) * p;
  }

  JWF_FLOAT _f, _p, _s, _is, _vxp, _pxa, _pixa;
};

