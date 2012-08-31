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
#ifndef JWFVAR_WAVES4_WF_H_
#define JWFVAR_WAVES4_WF_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class Waves4WFFunc: public Variation {
public:
	Waves4WFFunc() {
		scalex = 0.25f;
		scaley = 0.5f;
		freqx = M_PI / 2.0f;
		freqy = M_PI / 4.0f;
		use_cos_x = true;
		use_cos_y = false;
    dampx = 0.0f;
    dampy = 0.0f;
		initParameterNames(8, "scalex", "scaley", "freqx", "freqy", "use_cos_x", "uses_cos_y", "dampx", "danmpy");
	}

	const char* getName() const {
		return "waves2";
	}

	void setParameter(char *pName, float pValue) {
		if (strcmp(pName, "scalex") == 0) {
			scalex = pValue;
		}
		else if (strcmp(pName, "scaley") == 0) {
			scaley = pValue;
		}
		else if (strcmp(pName, "freqx") == 0) {
			freqx = pValue;
		}
		else if (strcmp(pName, "freqy") == 0) {
			freqy = pValue;
		}
		else if (strcmp(pName, "use_cos_x") == 0) {
			use_cos_x = FTOI(pValue)==1;
		}
		else if (strcmp(pName, "use_cos_y") == 0) {
			use_cos_y = FTOI(pValue)==1;
		}
		else if (strcmp(pName, "dampx") == 0) {
			dampx = pValue;
		}
		else if (strcmp(pName, "dampy") == 0) {
			dampy = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    float dampingX = fabs(dampx) < EPSILON ? 1.0f : expf(dampx);
    float dampingY = fabs(dampy) < EPSILON ? 1.0f : expf(dampy);
    if (use_cos_x) {
      pVarTP->x += pAmount * (pAffineTP->x + dampingX * scalex * cosf(pAffineTP->y * freqx) * sinf(pAffineTP->y * freqx) * cosf(pAffineTP->y * freqx)) * dampingX;
    }
    else {
      pVarTP->x += pAmount * (pAffineTP->x + dampingX * scalex * sinf(pAffineTP->y * freqx) * cosf(pAffineTP->y * freqx) * sinf(pAffineTP->y * freqx)) * dampingX;
    }
    if (use_cos_y) {
      pVarTP->y += pAmount * (pAffineTP->y + dampingY * scaley * cosf(pAffineTP->x * freqy) * sinf(pAffineTP->x * freqy) * cosf(pAffineTP->x * freqy)) * dampingY;
    }
    else {
      pVarTP->y += pAmount * (pAffineTP->y + dampingY * scaley * sinf(pAffineTP->x * freqy) * cosf(pAffineTP->x * freqy) * sinf(pAffineTP->x * freqy)) * dampingY;
    }
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	Waves4WFFunc* makeCopy() {
		return new Waves4WFFunc(*this);
	}

private:
	float scalex;
	float scaley;
	float freqx;
	float freqy;
	bool use_cos_x;
	bool use_cos_y;
	float dampx;
	float dampy;
};

#endif // JWFVAR_WAVES4_WF_H_
