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
#ifndef JWFVAR_MODULUS_H_
#define JWFVAR_MODULUS_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class ModulusFunc: public Variation {
public:
	ModulusFunc() {
		x = 0.20f;
		y = 0.50f;
		initParameterNames(2, "x", "y");
	}

	const char* getName() const {
		return "modulus";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "x") == 0) {
			x = pValue;
		}
		else if (strcmp(pName, "y") == 0) {
			y = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		if (pAffineTP->x > x) {
			pVarTP->x += pAmount * (-x + fmodf(pAffineTP->x + x, _xr));
		}
		else if (pAffineTP->x < -x) {
			pVarTP->x += pAmount * (x - fmodf(x - pAffineTP->x, _xr));
		}
		else {
			pVarTP->x += pAmount * pAffineTP->x;
		}

		if (pAffineTP->y > y) {
			pVarTP->y += pAmount * (-y + fmodf(pAffineTP->y + y, _yr));
		}
		else if (pAffineTP->y < -y) {
			pVarTP->y += pAmount * (y - fmodf(y - pAffineTP->y, _yr));
		}
		else {
			pVarTP->y += pAmount * pAffineTP->y;
		}
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		_xr = 2.0f * x;
		_yr = 2.0f * y;
	}

	ModulusFunc* makeCopy() {
		return new ModulusFunc(*this);
	}

private:
	float x, y;
	float _xr, _yr;
};

#endif // JWFVAR_MODULUS_H_
