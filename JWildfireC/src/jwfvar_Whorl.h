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

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class WhorlFunc: public Variation {
public:
	WhorlFunc() {
		inside = 0.10f;
		outside = 0.20f;
		initParameterNames(2, "inside", "outside");
	}

	const char* getName() const {
		return "whorl";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "inside") == 0) {
			inside = pValue;
		}
		else if (strcmp(pName, "outside") == 0) {
			outside = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT r = pAffineTP->getPrecalcSqrt();
		JWF_FLOAT a;
		if (r < pAmount)
			a = pAffineTP->getPrecalcAtanYX() + inside / (pAmount - r);
		else
			a = pAffineTP->getPrecalcAtanYX() + outside / (pAmount - r);

		JWF_FLOAT sa = JWF_SIN(a);
		JWF_FLOAT ca = JWF_COS(a);

		pVarTP->x += pAmount * r * ca;
		pVarTP->y += pAmount * r * sa;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	WhorlFunc* makeCopy() {
		return new WhorlFunc(*this);
	}

private:
	JWF_FLOAT inside;
	JWF_FLOAT outside;
};

