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

class FociFunc: public Variation {
public:
	FociFunc() {
	}

	const char* getName() const {
		return "foci";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT expx = JWF_EXP(pAffineTP->x) * 0.5;
		JWF_FLOAT expnx = 0.25 / expx;
		if (expx <= EPSILON || expnx <= EPSILON) {
			return;
		}
		JWF_FLOAT siny = JWF_SIN(pAffineTP->y);
		JWF_FLOAT cosy = JWF_COS(pAffineTP->y);

		JWF_FLOAT tmp = (expx + expnx - cosy);
		if (tmp == 0)
			tmp = 1e-6f;
		tmp = pAmount / tmp;

		pVarTP->x += (expx - expnx) * tmp;
		pVarTP->y += siny * tmp;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	FociFunc* makeCopy() {
		return new FociFunc(*this);
	}

};

