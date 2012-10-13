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

class EpispiralFunc: public Variation {
public:
	EpispiralFunc() {
		n = 6.0;
		thickness = 0.0;
		holes = 1.0;
		initParameterNames(3, "n", "thickness", "holes");
	}

	const char* getName() const {
		return "epispiral";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
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

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT theta = JWF_ATAN2(pAffineTP->y, pAffineTP->x);
		JWF_FLOAT t = -holes;
		if (JWF_FABS(thickness) > EPSILON) {
			JWF_FLOAT d = JWF_COS(n * theta);
			if (d == 0) {
				return;
			}
			t += (pContext->randGen->random() * thickness) * (1.0 / d);
		}
		else {
			JWF_FLOAT d = JWF_COS(n * theta);
			if (d == 0) {
				return;
			}
			t += 1.0 / d;
		}
		JWF_FLOAT sintheta, costheta;
		JWF_SINCOS(theta, &sintheta, &costheta);

		pVarTP->x += pAmount * t * costheta;
		pVarTP->y += pAmount * t * sintheta;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	EpispiralFunc* makeCopy() {
		return new EpispiralFunc(*this);
	}

private:
	JWF_FLOAT n;
	JWF_FLOAT thickness;
	JWF_FLOAT holes;
};

