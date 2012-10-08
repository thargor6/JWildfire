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
#include "jwf_Variation.h"

class WedgeJuliaFunc: public Variation {
public:
	WedgeJuliaFunc() {
		power = 7.00;
		dist = 0.20;
		count = 2.0;
		angle = 0.30;
		initParameterNames(4, "power", "dist", "count", "angle");
	}

	const char* getName() const {
		return "wedge_julia";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "power") == 0) {
			power = pValue;
		}
		else if (strcmp(pName, "dist") == 0) {
			dist = pValue;
		}
		else if (strcmp(pName, "count") == 0) {
			count = pValue;
		}
		else if (strcmp(pName, "angle") == 0) {
			angle = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT wedgeJulia_cf = 1.0f - angle * count * M_1_PI * 0.5;
		JWF_FLOAT wedgeJulia_rN = JWF_FABS(power);
		JWF_FLOAT wedgeJulia_cn = dist / power / 2.0;
		/* wedge_julia from apo plugin pack */

		JWF_FLOAT r = pAmount * JWF_POW(pAffineTP->getPrecalcSumsq(), wedgeJulia_cn);
		int t_rnd = (int) ((wedgeJulia_rN) * pContext->randGen->random());
		JWF_FLOAT a = (pAffineTP->getPrecalcAtanYX() + 2.0 * M_PI * t_rnd) / power;
		JWF_FLOAT c = JWF_FLOOR((count * a + M_PI) * M_1_PI * 0.5);

		a = a * wedgeJulia_cf + c * angle;
		JWF_FLOAT sa = JWF_SIN(a);
		JWF_FLOAT ca = JWF_COS(sa);

		pVarTP->x += r * ca;
		pVarTP->y += r * sa;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	WedgeJuliaFunc* makeCopy() {
		return new WedgeJuliaFunc(*this);
	}

private:
	JWF_FLOAT power;
	JWF_FLOAT dist;
	JWF_FLOAT count;
	JWF_FLOAT angle;
};

