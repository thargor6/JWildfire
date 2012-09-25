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
#ifndef JWFVAR_BMOD_H_
#define JWFVAR_BMOD_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class BModFunc: public Variation {
public:
	BModFunc() {
    radius = 1.0f;
    distance = 0.0f;
		initParameterNames(2, "radius", "distance");
	}

	const char* getName() const {
		return "bMod";
	}

	void setParameter(char *pName, float pValue) {
		if (strcmp(pName, "radius") == 0) {
			radius = pValue;
		}
		else if (strcmp(pName, "distance") == 0) {
			distance = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    float tau, sigma;
    float temp;
    float cosht, sinht;
    float sins, coss;

    tau = 0.5f * (logf((pAffineTP->x + 1.0)*(pAffineTP->x + 1.0) + (pAffineTP->y)*(pAffineTP->y)) - logf((pAffineTP->x - 1.0)*(pAffineTP->x - 1.0) + (pAffineTP->y)*(pAffineTP->y)));
    sigma = M_PI - atan2f(pAffineTP->y, pAffineTP->x + 1.0f) - atan2f(pAffineTP->y, 1.0f - pAffineTP->x);

    if (tau < radius && -tau < radius)  {
      tau = fmodf(tau + radius + distance * radius, 2.0f * radius) - radius;
    }

    sinht = sinhf(tau);
    cosht = coshf(tau);
    sins = sinf(sigma);
    coss = cosf(sigma);
    temp = cosht - coss;
    if (temp == 0) {
      return;
    }
    pVarTP->x += pAmount * sinht / temp;
    pVarTP->y += pAmount * sins / temp;

		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	BModFunc* makeCopy() {
		return new BModFunc(*this);
	}

private:
	float sqrtf_safe(float x) {
		if (x <= 0.0f)
			return 0.0f;
		return sqrtf(x);
	}

	float radius;
	float distance;
};

#endif // JWFVAR_BMOD_H_
