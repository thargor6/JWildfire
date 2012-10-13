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

class CirclizeFunc: public Variation {
public:
	CirclizeFunc() {
		hole = 0.40;
		initParameterNames(1, "hole");
	}

	const char* getName() const {
		return "circlize";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "hole") == 0) {
			hole = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT var4_PI = pAmount / M_PI_4;

		JWF_FLOAT absx = JWF_FABS(pAffineTP->x);
		JWF_FLOAT absy = JWF_FABS(pAffineTP->y);
		JWF_FLOAT perimeter, side;
		if (absx >= absy) {
			if (pAffineTP->x >= absy) {
				perimeter = absx + pAffineTP->y;
			}
			else {
				perimeter = 5.0 * absx - pAffineTP->y;
			}
			side = absx;
		}
		else {
			if (pAffineTP->y >= absx) {
				perimeter = 3.0 * absy - pAffineTP->x;
			}
			else {
				perimeter = 7.0 * absy + pAffineTP->x;
			}
			side = absy;
		}

		double r = var4_PI * side + hole;
		double a = M_PI_4 * perimeter / side - M_PI_4;
		double sina, cosa;
		JWF_SINCOS(a, &sina, &cosa);

		pVarTP->x += r * cosa;
		pVarTP->y += r * sina;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	CirclizeFunc* makeCopy() {
		return new CirclizeFunc(*this);
	}

private:
	JWF_FLOAT hole;
};

