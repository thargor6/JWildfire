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

class SpirographFunc: public Variation {
public:
	SpirographFunc() {
		a = 3.0;
		b = 2.0;
		d = 0.0;
		c1 = 0.0;
		c2 = 0.0;
		tMin = -1.0;
		tMax = 1.0;
		yMin = -1.0;
		yMax = 1.0;
		initParameterNames(9, "a", "b", "d", "c1", "c2", "tMin", "tMax", "yMin", "yMax");
	}

	const char* getName() const {
		return "spirograph";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "a") == 0) {
			a = pValue;
		}
		else if (strcmp(pName, "b") == 0) {
			b = pValue;
		}
		else if (strcmp(pName, "d") == 0) {
			d = pValue;
		}
		else if (strcmp(pName, "c1") == 0) {
			c1 = pValue;
		}
		else if (strcmp(pName, "c2") == 0) {
			c2 = pValue;
		}
		else if (strcmp(pName, "tMin") == 0) {
			tMin = pValue;
		}
		else if (strcmp(pName, "tMax") == 0) {
			tMax = pValue;
		}
		else if (strcmp(pName, "yMin") == 0) {
			yMin = pValue;
		}
		else if (strcmp(pName, "yMax") == 0) {
			yMax = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT t = (tMax - tMin) * pContext->randGen->random() + tMin;
		JWF_FLOAT y = (yMax - yMin) * pContext->randGen->random() + yMin;
		JWF_FLOAT sint, cost;
		JWF_SINCOS(t, &sint, &cost);
		JWF_FLOAT sina,cosa;
		JWF_SINCOS((a + b) / b * t, &sina, &cosa);
		JWF_FLOAT x1 = (a + b) * cost - c1 * cosa;
		JWF_FLOAT y1 = (a + b) * sint - c2 * sina;
		pVarTP->x += pAmount * (x1 + d * cost + y);
		pVarTP->y += pAmount * (y1 + d * sint + y);
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	SpirographFunc* makeCopy() {
		return new SpirographFunc(*this);
	}

private:
	JWF_FLOAT a;
	JWF_FLOAT b;
	JWF_FLOAT d;
	JWF_FLOAT c1;
	JWF_FLOAT c2;
	JWF_FLOAT tMin;
	JWF_FLOAT tMax;
	JWF_FLOAT yMin;
	JWF_FLOAT yMax;
};

