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

class NgonFunc: public Variation {
public:
	NgonFunc() {
		circle = 1.0;
		corners = 2.0;
		power = 3.0;
		sides = 5.0;
		initParameterNames(4, "circle", "corners", "power", "sides");
	}

	const char* getName() const {
		return "ngon";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "circle") == 0) {
			circle = pValue;
		}
		else if (strcmp(pName, "corners") == 0) {
			corners = pValue;
		}
		else if (strcmp(pName, "power") == 0) {
			power = pValue;
		}
		else if (strcmp(pName, "sides") == 0) {
			sides = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT r_factor, theta, phi, b, amp;

		r_factor = JWF_POW(pAffineTP->getPrecalcSumsq(), power / 2.0);

		theta = pAffineTP->getPrecalcAtanYX();
		b = 2.0 * M_PI / sides;

		phi = theta - (b * JWF_FLOOR(theta / b));
		if (phi > b / 2.0)
			phi -= b;

		amp = corners * (1.0 / (JWF_COS(phi) + EPSILON) - 1.0) + circle;
		amp /= (r_factor + EPSILON);

		pVarTP->x += pAmount * pAffineTP->x * amp;
		pVarTP->y += pAmount * pAffineTP->y * amp;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	NgonFunc* makeCopy() {
		return new NgonFunc(*this);
	}

private:
	JWF_FLOAT circle;
	JWF_FLOAT corners;
	JWF_FLOAT power;
	JWF_FLOAT sides;
};

