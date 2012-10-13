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

class CothFunc: public Variation {
public:
	CothFunc() {
	}

	const char* getName() const {
		return "coth";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT cothsin, cothcos;
		JWF_SINCOS(2.0 * pAffineTP->y, &cothsin, &cothcos);
		JWF_FLOAT cothsinh = JWF_SINH(2.0 * pAffineTP->x);
		JWF_FLOAT cothcosh = JWF_COSH(2.0 * pAffineTP->x);
		JWF_FLOAT d = (cothcosh - cothcos);
		if (d == 0)
			return;
		JWF_FLOAT cothden = 1.0 / d;
		pVarTP->x += pAmount * cothden * cothsinh;
		pVarTP->y += pAmount * cothden * cothsin;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	CothFunc* makeCopy() {
		return new CothFunc(*this);
	}

};

