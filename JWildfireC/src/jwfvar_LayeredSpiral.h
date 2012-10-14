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

class LayeredSpiralFunc: public Variation {
public:
	LayeredSpiralFunc() {
		radius = 1.0;
		initParameterNames(1, "radius");
	}

	const char* getName() const {
		return "layered_spiral";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "radius") == 0) {
			radius = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    /* layered_spiral by Will Evans, http://eevans1.deviantart.com/art/kaleidoscope-plugin-122185469  */
    JWF_FLOAT a = pAffineTP->x * radius; // adjusts the layered_spiral radius.
    JWF_FLOAT t = JWF_SQR(pAffineTP->x) + JWF_SQR(pAffineTP->y) + EPSILON;
    JWF_FLOAT sint, cost;
    JWF_SINCOS(t, &sint, &cost);
    pVarTP->x += pAmount * a * cost;
    pVarTP->y += pAmount * a * sint;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	LayeredSpiralFunc* makeCopy() {
		return new LayeredSpiralFunc(*this);
	}

private:
	JWF_FLOAT radius;
};

