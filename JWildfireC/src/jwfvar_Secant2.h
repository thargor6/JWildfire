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

#ifndef JWFVAR_SECANT2_H_
#define JWFVAR_SECANT2_H_

#include "jwf_Variation.h"

class Secant2Func: public Variation {
public:
	Secant2Func() {
	}

	const char* getName() const {
		return "secant2";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    float r = pAmount * pAffineTP->getPrecalcSqrt();
    float cr = cosf(r);
    if (cr == 0) {
      return;
    }
    float icr = 1.0f / cr;
    pVarTP->x += pAmount * pAffineTP->x;
    if (cr < 0) {
      pVarTP->y += pAmount * (icr + 1);
    }
    else {
      pVarTP->y += pAmount * (icr - 1);
    }
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	Secant2Func* makeCopy() {
		return new Secant2Func(*this);
	}

};

#endif // JWFVAR_SECANT2_H_
