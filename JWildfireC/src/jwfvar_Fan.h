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

#ifndef JWFVAR_FAN_H_
#define JWFVAR_FAN_H_

#include "jwf_Variation.h"

class FanFunc: public Variation {
public:
	FanFunc() {
	}

	const char* getName() const {
		return "fan";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    float dx = M_PI * pXForm->coeff20 * pXForm->coeff20 + EPSILON;
    float dx2 = dx / 2;
    float a;
    if ((pAffineTP->getPrecalcAtan() + pXForm->coeff21 - ((int) ((pAffineTP->getPrecalcAtan() + pXForm->coeff21) / dx)) * dx) > dx2)
      a = pAffineTP->getPrecalcAtan() - dx2;
    else
      a = pAffineTP->getPrecalcAtan() + dx2;
    float sinr = sinf(a);
    float cosr = cosf(a);
    float r = pAmount * sqrtf(pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y);
    pVarTP->x += r * cosr;
    pVarTP->y += r * sinr;
    if (pContext->isPreserveZCoordinate) {
      pVarTP->z += pAmount * pAffineTP->z;
    }
	}

	FanFunc* makeCopy() {
		return new FanFunc(*this);
	}

};

#endif // JWFVAR_FAN_H_
