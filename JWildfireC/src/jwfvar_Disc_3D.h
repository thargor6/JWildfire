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
#ifndef JWFVAR_DISC_3D_H_
#define JWFVAR_DISC_3D_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class Disc_3DFunc: public Variation {
public:
	Disc_3DFunc() {
		pi = M_PI;
		initParameterNames(1, "pi");
	}

	const char* getName() const {
		return "disc3d";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "pi") == 0) {
			pi = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		float r = JWF_SQRT(pAffineTP->y * pAffineTP->y + pAffineTP->x * pAffineTP->x + EPSILON);
		float a = pi * r;
		float sr = JWF_SIN(a);
		float cr = JWF_COS(a);
		float vv = pAmount * atan2f(pAffineTP->x, pAffineTP->y) / pi;
		pVarTP->x += vv * sr;
		pVarTP->y += vv * cr;
		pVarTP->z += vv * (r * JWF_COS(pAffineTP->z));
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	Disc_3DFunc* makeCopy() {
		return new Disc_3DFunc(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		if (pi == 0)
			pi = EPSILON;
	}

private:
	float pi;
};

#endif // JWFVAR_DISC_3D_H_
