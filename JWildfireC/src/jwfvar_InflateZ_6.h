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


#include "jwf_Variation.h"

class InflateZ_6Func: public Variation {
public:
	InflateZ_6Func() {
	}

	const char* getName() const {
		return "inflateZ_6";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT ang = JWF_ATAN2(pAffineTP->y, pAffineTP->x);
		JWF_FLOAT adf = pAffineTP->y - pAffineTP->x;
		JWF_FLOAT kik = ang * JWF_SIN(adf);
		pVarTP->z += pAmount * (1.5 - acosf(sinf(ang) * kik * 0.5));
	}

	InflateZ_6Func* makeCopy() {
		return new InflateZ_6Func(*this);
	}

};

