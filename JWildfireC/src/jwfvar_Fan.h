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

class FanFunc: public Variation {
public:
	FanFunc() {
	}

	const char* getName() const {
		return "fan";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT dx = M_PI * pXForm->coeff20 * pXForm->coeff20 + EPSILON;
		JWF_FLOAT dx2 = dx / 2;
		JWF_FLOAT a;
		if ((pAffineTP->getPrecalcAtan() + pXForm->coeff21 - ((int) ((pAffineTP->getPrecalcAtan() + pXForm->coeff21) / dx)) * dx) > dx2)
			a = pAffineTP->getPrecalcAtan() - dx2;
		else
			a = pAffineTP->getPrecalcAtan() + dx2;
		JWF_FLOAT sinr = JWF_SIN(a);
		JWF_FLOAT cosr = JWF_COS(a);
		JWF_FLOAT r = pAmount * JWF_SQRT(pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y);
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

