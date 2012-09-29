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
#ifndef JWFVAR_BIPOLAR_H_
#define JWFVAR_BIPOLAR_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class BipolarFunc: public Variation {
public:
	BipolarFunc() {
		shift = 0.0f;
		initParameterNames(1, "shift");
	}

	const char* getName() const {
		return "bipolar";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "shift") == 0) {
			shift = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		float x2y2 = (pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y);
		float t = x2y2 + 1.0f;
		float x2 = 2 * pAffineTP->x;
		float ps = -M_PI_2 * shift;
		float y = 0.5 * atan2f(2.0f * pAffineTP->y, x2y2 - 1.0f) + ps;

		if (y > M_PI_2) {
			y = -M_PI_2 + fmodf(y + M_PI_2, M_PI);
		}
		else if (y < -M_PI_2) {
			y = M_PI_2 - fmodf(M_PI_2 - y, M_PI);
		}

		float f = t + x2;
		float g = t - x2;

		if ((g == 0) || (f / g <= 0))
			return;

		pVarTP->x += pAmount * 0.25f * M_2_PI * JWF_LOG((t + x2) / (t - x2));
		pVarTP->y += pAmount * M_2_PI * y;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	BipolarFunc* makeCopy() {
		return new BipolarFunc(*this);
	}

private:
	float shift;
};

#endif // JWFVAR_BIPOLAR_H_
