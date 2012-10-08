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

class CPowFunc: public Variation {
public:
	CPowFunc() {
		r = 1.0;
		i = 0.1;
		power = 1.5;
		initParameterNames(3, "r", "i", "power");
	}

	const char* getName() const {
		return "cpow";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "r") == 0) {
			r = pValue;
		}
		else if (strcmp(pName, "i") == 0) {
			i = pValue;
		}
		else if (strcmp(pName, "power") == 0) {
			power = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT a = pAffineTP->getPrecalcAtanYX();
		JWF_FLOAT lnr = 0.5 * JWF_LOG(pAffineTP->getPrecalcSumsq());
		JWF_FLOAT va = 2.0 * M_PI / power;
		JWF_FLOAT vc = r / power;
		JWF_FLOAT vd = i / power;
		JWF_FLOAT ang = vc * a + vd * lnr + va * JWF_FLOOR((JWF_FLOAT)(power * pContext->randGen->random()));

		JWF_FLOAT m = pAmount * JWF_EXP(vc * lnr - vd * a);
		JWF_FLOAT sa = JWF_SIN(ang);
		JWF_FLOAT ca = JWF_COS(ang);

		pVarTP->x += m * ca;
		pVarTP->y += m * sa;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	CPowFunc* makeCopy() {
		return new CPowFunc(*this);
	}

private:
	JWF_FLOAT r;
	JWF_FLOAT i;
	JWF_FLOAT power;
};

