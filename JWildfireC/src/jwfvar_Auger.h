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

class AugerFunc: public Variation {
public:
	AugerFunc() {
		freq = 1.00;
		weight = 0.5;
		sym = 0.1;
		scale = 0.9;
		initParameterNames(4, "freq", "weight", "sym", "scale");
	}

	const char* getName() const {
		return "auger";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "freq") == 0) {
			freq = pValue;
		}
		else if (strcmp(pName, "weight") == 0) {
			weight = pValue;
		}
		else if (strcmp(pName, "sym") == 0) {
			sym = pValue;
		}
		else if (strcmp(pName, "scale") == 0) {
			scale = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT s = JWF_SIN(freq * pAffineTP->x);
		JWF_FLOAT t = JWF_SIN(freq * pAffineTP->y);
		JWF_FLOAT dy = pAffineTP->y + weight * (scale * s * 0.5 + JWF_FABS(pAffineTP->y) * s);
		JWF_FLOAT dx = pAffineTP->x + weight * (scale * t * 0.5 + JWF_FABS(pAffineTP->x) * t);

		pVarTP->x += pAmount * (pAffineTP->x + sym * (dx - pAffineTP->x));
		pVarTP->y += pAmount * dy;

		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	AugerFunc* makeCopy() {
		return new AugerFunc(*this);
	}

private:
	JWF_FLOAT freq;
	JWF_FLOAT weight;
	JWF_FLOAT sym;
	JWF_FLOAT scale;
};

