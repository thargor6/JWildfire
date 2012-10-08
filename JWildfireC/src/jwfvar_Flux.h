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

class FluxFunc: public Variation {
public:
	FluxFunc() {
		spread = 0.0;
		initParameterNames(1, "spread");
	}

	const char* getName() const {
		return "flux";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "spread") == 0) {
			spread = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT xpw = pAffineTP->x + pAmount;
		JWF_FLOAT xmw = pAffineTP->x - pAmount;
		JWF_FLOAT avgr = pAmount * (2.0 + spread) * JWF_SQRT(JWF_SQRT(pAffineTP->y * pAffineTP->y + xpw * xpw) / JWF_SQRT(pAffineTP->y * pAffineTP->y + xmw * xmw));
		JWF_FLOAT avga = (JWF_ATAN2(pAffineTP->y, xmw) - JWF_ATAN2(pAffineTP->y, xpw)) * 0.5;

		pVarTP->x += avgr * JWF_COS(avga);
		pVarTP->y += avgr * JWF_SIN(avga);
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	FluxFunc* makeCopy() {
		return new FluxFunc(*this);
	}

private:
	JWF_FLOAT spread;
};

