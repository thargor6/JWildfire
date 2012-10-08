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

class EPushFunc: public Variation {
public:
	EPushFunc() {
		push = 0.0;
		dist = 1.0;
		rotate = 0.0;
		initParameterNames(3, "push", "dist", "rotate");
	}

	const char* getName() const {
		return "ePush";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
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

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT tmp = pAffineTP->y * pAffineTP->y + pAffineTP->x * pAffineTP->x + 1.0;
		JWF_FLOAT tmp2 = 2.0 * pAffineTP->x;
		JWF_FLOAT xmax = (sqrtf_safe(tmp + tmp2) + sqrtf_safe(tmp - tmp2)) * 0.5;
		if (xmax < 1.0)
			xmax = 1.0;
		JWF_FLOAT sinhmu, coshmu;

		JWF_FLOAT mu = JWF_ACOSH(xmax); //  mu > 0
		JWF_FLOAT t = pAffineTP->x / xmax;
		if (t > 1.0)
			t = 1.0;
		else if (t < -1.0)
			t = -1.0;

		JWF_FLOAT nu = JWF_ACOS(t); // -Pi < nu < Pi
		if (pAffineTP->y < 0)
			nu *= -1.0;
		nu += rotate;

		mu *= dist;
		mu += push;

		sinhmu = JWF_SINH(mu);
		coshmu = JWF_COSH(mu);

		pVarTP->x += pAmount * coshmu * JWF_COS(nu);
		pVarTP->y += pAmount * sinhmu * JWF_SIN(nu);

		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	EPushFunc* makeCopy() {
		return new EPushFunc(*this);
	}

private:
	JWF_FLOAT sqrtf_safe(JWF_FLOAT x) {
		if (x <= 0.0)
			return 0.0;
		return JWF_SQRT(x);
	}

	JWF_FLOAT push;
	JWF_FLOAT dist;
	JWF_FLOAT rotate;
};

