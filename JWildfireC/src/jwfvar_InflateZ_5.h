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

#ifndef JWFVAR_INFLATEZ_5_H_
#define JWFVAR_INFLATEZ_5_H_

#include "jwf_Variation.h"

class InflateZ_5Func: public Variation {
public:
	InflateZ_5Func() {
	}

	const char* getName() const {
		return "inflateZ_5";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    float ang1 = atan2f(pAffineTP->y, pAffineTP->x);
    float val1 = cosf(M_PI_2 - ang1) / 2.0f;
    pVarTP->z += pAmount * val1;
	}

	InflateZ_5Func* makeCopy() {
		return new InflateZ_5Func(*this);
	}

};

#endif // JWFVAR_INFLATEZ_5_H_
