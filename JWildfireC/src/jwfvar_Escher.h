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

class EscherFunc: public Variation {
public:
	EscherFunc() {
		beta = 0.30;
		initParameterNames(1, "beta");
	}

	const char* getName() const {
		return "escher";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "beta") == 0) {
			beta = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT a = pAffineTP->getPrecalcAtanYX();
		JWF_FLOAT lnr = 0.5 * JWF_LOG(pAffineTP->getPrecalcSumsq());

		JWF_FLOAT seb, ceb;
		JWF_SINCOS(beta, &seb, &ceb);

		JWF_FLOAT vc = 0.5 * (1.0 + ceb);
		JWF_FLOAT vd = 0.5 * seb;

		JWF_FLOAT m = pAmount * JWF_EXP(vc * lnr - vd * a);
		JWF_FLOAT n = vc * a + vd * lnr;

		JWF_FLOAT sn, cn;
		JWF_SINCOS(n, &sn, &cn);

		pVarTP->x += m * cn;
		pVarTP->y += m * sn;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	EscherFunc* makeCopy() {
		return new EscherFunc(*this);
	}

private:
	JWF_FLOAT beta;
};

