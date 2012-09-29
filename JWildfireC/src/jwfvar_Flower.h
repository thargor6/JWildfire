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
#ifndef JWFVAR_FLOWER_H_
#define JWFVAR_FLOWER_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class FlowerFunc: public Variation {
public:
	FlowerFunc() {
		holes = 0.40f;
		petals = 7.0f;
		initParameterNames(2, "holes", "petals");
	}

	const char* getName() const {
		return "flower";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "holes") == 0) {
			holes = pValue;
		}
		else if (strcmp(pName, "petals") == 0) {
			petals = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		float theta = pAffineTP->getPrecalcAtanYX();
		double d = pAffineTP->getPrecalcSqrt();
		if (d == 0) {
			return;
		}
		float r = pAmount * (pContext->randGen->random() - holes) * JWF_COS(petals * theta) / d;
		pVarTP->x += r * pAffineTP->x;
		pVarTP->y += r * pAffineTP->y;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	FlowerFunc* makeCopy() {
		return new FlowerFunc(*this);
	}

private:
	float holes;
	float petals;
};

#endif // JWFVAR_FLOWER_H_
