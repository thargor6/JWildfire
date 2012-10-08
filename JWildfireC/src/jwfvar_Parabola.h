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

class ParabolaFunc: public Variation {
public:
	ParabolaFunc() {
		width = 1.0;
		height = 0.5;
		initParameterNames(2, "width", "height");
	}

	const char* getName() const {
		return "parabola";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "width") == 0) {
			width = pValue;
		}
		else if (strcmp(pName, "height") == 0) {
			height = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT r = pAffineTP->getPrecalcSqrt();
		JWF_FLOAT sr = JWF_SIN(r);
		JWF_FLOAT cr = JWF_COS(r);
		pVarTP->x += height * pAmount * sr * sr * pContext->randGen->random();
		pVarTP->y += width * pAmount * cr * pContext->randGen->random();
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	ParabolaFunc* makeCopy() {
		return new ParabolaFunc(*this);
	}

private:
	JWF_FLOAT width;
	JWF_FLOAT height;
};

