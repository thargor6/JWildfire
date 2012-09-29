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
#ifndef JWFVAR_ECLIPSE_H_
#define JWFVAR_ECLIPSE_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class EclipseFunc: public Variation {
public:
	EclipseFunc() {
		shift = 0.0f;
		initParameterNames(1, "shift");
	}

	const char* getName() const {
		return "eclipse";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "shift") == 0) {
			shift = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		if (fabsf(pAffineTP->y) <= pAmount) {
			float c_2 = JWF_SQRT(pAmount * pAmount - pAffineTP->y * pAffineTP->y);
			if (fabsf(pAffineTP->x) <= c_2) {
				double x = pAffineTP->x + shift * pAmount;
				if (fabsf(x) >= c_2) {
					pVarTP->x -= pAmount * pAffineTP->x;
				}
				else {
					pVarTP->x += pAmount * x;
				}
			}
			else {
				pVarTP->x += pAmount * pAffineTP->x;
			}
			pVarTP->y += pAmount * pAffineTP->y;
		}
		else {
			pVarTP->x += pAmount * pAffineTP->x;
			pVarTP->y += pAmount * pAffineTP->y;
		}
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	EclipseFunc* makeCopy() {
		return new EclipseFunc(*this);
	}

private:
	float shift;
};

#endif // JWFVAR_ECLIPSE_H_
