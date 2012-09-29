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
#ifndef JWFVAR_BTRANSFORM_H_
#define JWFVAR_BTRANSFORM_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class BTransformFunc: public Variation {
public:
	BTransformFunc() {
		rotate = 0.0;
		power = 1;
		move = 0.0;
		split = 0.0;
		initParameterNames(4, "rotate", "power", "move", "split");
	}

	const char* getName() const {
		return "bTransform";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "rotate") == 0) {
			rotate = pValue;
		}
		else if (strcmp(pName, "power") == 0) {
			power = FTOI(pValue);
		}
		else if (strcmp(pName, "move") == 0) {
			move = pValue;
		}
		else if (strcmp(pName, "split") == 0) {
			split = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		float tau, sigma;
		float temp;
		float cosht, sinht;
		float sins, coss;

		tau = 0.5f
				* (logf((pAffineTP->x + 1.0f) * (pAffineTP->x + 1.0f) + pAffineTP->y * pAffineTP->y) - JWF_LOG((pAffineTP->x - 1.0f) * (pAffineTP->x - 1.0f) + pAffineTP->y * pAffineTP->y))
				/ power + move;
		sigma = M_PI - atan2f(pAffineTP->y, pAffineTP->x + 1.0f) - atan2f(pAffineTP->y, 1.0f - pAffineTP->x) + rotate;
		sigma = sigma / power + M_2PI / power * floorf(pContext->randGen->random() * power);

		if (pAffineTP->x >= 0.0)
			tau += split;
		else
			tau -= split;
		sinht = JWF_SINH(tau);
		cosht = JWF_COSH(tau);
		sins = JWF_SIN(sigma);
		coss = JWF_COS(sigma);
		temp = cosht - coss;
		pVarTP->x += pAmount * sinht / temp;
		pVarTP->y += pAmount * sins / temp;

		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	BTransformFunc* makeCopy() {
		return new BTransformFunc(*this);
	}

private:
	float sqrtf_safe(float x) {
		if (x <= 0.0f)
			return 0.0f;
		return JWF_SQRT(x);
	}

	float rotate;
	float power;
	float move;
	float split;
};

#endif // JWFVAR_BTRANSFORM_H_
