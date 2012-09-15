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
#ifndef JWFVAR_EPISPIRAL_H_
#define JWFVAR_EPISPIRAL_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class EpispiralFunc: public Variation {
public:
	EpispiralFunc() {
		n = 6.0f;
		thickness = 0.0f;
		holes = 1.0f;
		initParameterNames(3, "n", "thickness", "holes");
	}

	const char* getName() const {
		return "epispiral";
	}

	void setParameter(char *pName, float pValue) {
		if (strcmp(pName, "n") == 0) {
			n = pValue;
		}
		else if (strcmp(pName, "thickness") == 0) {
			thickness = pValue;
		}
		else if (strcmp(pName, "holes") == 0) {
			holes = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    float theta = atan2f(pAffineTP->y, pAffineTP->x);
    float t = -holes;
    if (fabs(thickness) > EPSILON) {
      t += (pContext->randGen->random() * thickness) * (1.0f / cosf(n * theta));
    }
    pVarTP->x += pAmount * t * cosf(theta);
    pVarTP->y += pAmount * t * sinf(theta);
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	EpispiralFunc* makeCopy() {
		return new EpispiralFunc(*this);
	}

private:
	float n;
	float thickness;
	float holes;
};

#endif // JWFVAR_EPISPIRAL_H_
