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

class ExFunc: public Variation {
public:
	ExFunc() {
	}

	const char* getName() const {
		return "ex";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT r = JWF_SQRT(pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y);
		JWF_FLOAT n0 = JWF_SIN(pAffineTP->getPrecalcAtan() + r);
		JWF_FLOAT n1 = JWF_COS(pAffineTP->getPrecalcAtan() - r);
		JWF_FLOAT m0 = n0 * n0 * n0;
		JWF_FLOAT m1 = n1 * n1 * n1;
		r = r * pAmount;
		pVarTP->x += r * (m0 + m1);
		pVarTP->y += r * (m0 - m1);
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	ExFunc* makeCopy() {
		return new ExFunc(*this);
	}

};

