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
#ifndef JWFVAR_CURVE_H_
#define JWFVAR_CURVE_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class CurveFunc: public Variation {
public:
	CurveFunc() {
		xamp = 0.25f;
		yamp = 0.5f;
		xlength = 1.0f;
		ylength = 1.0f;
		initParameterNames(4, "xamp", "yamp", "xlength","ylength");
	}

	const char* getName() const {
		return "curve";
	}

	void setParameter(char *pName, float pValue) {
		if (strcmp(pName, "xamp") == 0) {
			xamp = pValue;
		}
		else if (strcmp(pName, "yamp") == 0) {
			yamp = pValue;
		}
		else if (strcmp(pName, "xlength") == 0) {
			xlength = pValue;
		}
		else if (strcmp(pName, "ylength") == 0) {
			ylength = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    pVarTP->x += pAmount * (pAffineTP->x + xamp * expf(-pAffineTP->y * pAffineTP->y / _pc_xlen));
    pVarTP->y += pAmount * (pAffineTP->y + yamp * expf(-pAffineTP->x * pAffineTP->x / _pc_ylen));
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	CurveFunc* makeCopy() {
		return new CurveFunc(*this);
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, float pAmount) {
	  _pc_xlen = xlength * xlength;
	  _pc_ylen = ylength * ylength;
	  if (_pc_xlen < 1E-20f)
	    _pc_xlen = 1E-20f;
	  if (_pc_ylen < 1E-20f)
	    _pc_ylen = 1E-20f;
	}

private:
	float xamp, yamp;
	float xlength, ylength;
	float _pc_xlen, _pc_ylen;
};

#endif // JWFVAR_CURVE_H_
