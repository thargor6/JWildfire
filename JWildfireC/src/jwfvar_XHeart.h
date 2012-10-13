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

class XHeartFunc: public Variation {
public:
	XHeartFunc() {
		angle = 0.0f;
		ratio = 0.0f;
		initParameterNames(2, "angle", "ratio");
	}

	const char* getName() const {
		return "xheart";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "angle") == 0) {
			angle = pValue;
		}
		else if (strcmp(pName, "ratio") == 0) {
			ratio = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT r2_4 = pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y + 4;
		if (r2_4 == 0)
			r2_4 = 1;
		JWF_FLOAT bx = 4.0f / r2_4, by = _rat / r2_4;
		JWF_FLOAT x = _cosa * (bx * pAffineTP->x) - _sina * (by * pAffineTP->y);
		JWF_FLOAT y = _sina * (bx * pAffineTP->x) + _cosa * (by * pAffineTP->y);

		if (x > 0) {
			pVarTP->x += pAmount * x;
			pVarTP->y += pAmount * y;
		}
		else {
			pVarTP->x += pAmount * x;
			pVarTP->y += -pAmount * y;
		}
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	XHeartFunc* makeCopy() {
		return new XHeartFunc(*this);
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		JWF_FLOAT ang = M_PI_4 + (0.5 * M_PI_4 * angle);
		JWF_SINCOS(ang, &_sina, &_cosa);
		_rat = 6.0 + 2.0 * ratio;
	}

private:
	JWF_FLOAT angle;
	JWF_FLOAT ratio;
	JWF_FLOAT _sina;
	JWF_FLOAT _cosa;
	JWF_FLOAT _rat;
};

