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

class CschFunc: public Variation {
public:
	CschFunc() {
	}

	const char* getName() const {
		return "csch";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT cschsin = JWF_SIN(pAffineTP->y);
		JWF_FLOAT cschcos = JWF_COS(pAffineTP->y);
		JWF_FLOAT cschsinh = JWF_SINH(pAffineTP->x);
		JWF_FLOAT cschcosh = JWF_COSH(pAffineTP->x);
		JWF_FLOAT d = (JWF_COSH(2.0 * pAffineTP->x) - JWF_COS(2.0 * pAffineTP->y));
		if (d == 0) {
			return;
		}
		JWF_FLOAT cschden = 2.0 / d;
		pVarTP->x += pAmount * cschden * cschsinh * cschcos;
		pVarTP->y -= pAmount * cschden * cschcosh * cschsin;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	CschFunc* makeCopy() {
		return new CschFunc(*this);
	}

};

