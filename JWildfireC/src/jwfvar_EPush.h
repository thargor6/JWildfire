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
#ifndef JWFVAR_EPUSH_H_
#define JWFVAR_EPUSH_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class EPushFunc: public Variation {
public:
	EPushFunc() {
    push = 0.0f;
    dist = 1.0f;
    rotate = 0.0f;
		initParameterNames(3, "push", "dist", "rotate");
	}

	const char* getName() const {
		return "ePush";
	}

	void setParameter(char *pName, float pValue) {
		if (strcmp(pName, "push") == 0) {
			push = pValue;
		}
		else if (strcmp(pName, "dist") == 0) {
			dist = pValue;
		}
		else if (strcmp(pName, "rotate") == 0) {
			rotate = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    float tmp = pAffineTP->y * pAffineTP->y + pAffineTP->x * pAffineTP->x + 1.0f;
    float tmp2 = 2.0f * pAffineTP->x;
    float xmax = (sqrtf_safe(tmp + tmp2) + sqrtf_safe(tmp - tmp2)) * 0.5f;
    if (xmax < 1.0f)
      xmax = 1.0f;
    float sinhmu, coshmu;

    float mu = acoshf(xmax); //  mu > 0
    float t = pAffineTP->x / xmax;
    if (t > 1.0f)
      t = 1.0f;
    else if (t < -1.0f)
      t = -1.0f;

    float nu = acosf(t); // -Pi < nu < Pi
    if (pAffineTP->y < 0)
      nu *= -1.0f;
    nu += rotate;

    mu *= dist;
    mu += push;

    sinhmu = sinhf(mu);
    coshmu = coshf(mu);

    pVarTP->x += pAmount * coshmu * cosf(nu);
    pVarTP->y += pAmount * sinhmu * sinf(nu);

		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	EPushFunc* makeCopy() {
		return new EPushFunc(*this);
	}

private:
	float sqrtf_safe(float x) {
		if (x <= 0.0f)
			return 0.0f;
		return sqrtf(x);
	}

	float push;
	float dist;
	float rotate;
};

#endif // JWFVAR_EPUSH_H_
