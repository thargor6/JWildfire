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

class BCollideFunc: public Variation {
public:
	BCollideFunc() {
		num = 1;
		a = 0.0;
		initParameterNames(2, "num", "a");
	}

	const char* getName() const {
		return "bCollide";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "num") == 0) {
			num = FTOI(pValue);
		}
		else if (strcmp(pName, "a") == 0) {
			a = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT tau, sigma;
		JWF_FLOAT temp;
		JWF_FLOAT cosht, sinht;
		JWF_FLOAT sins, coss;
		int alt;

		tau = 0.5
				* (JWF_LOG((pAffineTP->x + 1.0) * (pAffineTP->x + 1.0) + pAffineTP->y * pAffineTP->y) - JWF_LOG((pAffineTP->x - 1.0) * (pAffineTP->x - 1.0) + pAffineTP->y * pAffineTP->y));
		sigma = M_PI - JWF_ATAN2(pAffineTP->y, pAffineTP->x + 1.0) - JWF_ATAN2(pAffineTP->y, 1.0 - pAffineTP->x);

		alt = (int) (sigma * _bCn_pi);
		if (alt % 2 == 0)
			sigma = alt * _pi_bCn + JWF_FMOD(sigma + _bCa_bCn, _pi_bCn);
		else
			sigma = alt * _pi_bCn + JWF_FMOD(sigma - _bCa_bCn, _pi_bCn);
		sinht = JWF_SINH(tau);
		cosht = JWF_COSH(tau);
		sins = JWF_SIN(sigma);
		coss = JWF_COS(sigma);
		temp = cosht - coss;
		pVarTP->x += pAmount * sinht / temp;
		pVarTP->y += pAmount * sins / temp;

		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	BCollideFunc* makeCopy() {
		return new BCollideFunc(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		_bCn_pi = (JWF_FLOAT) num * M_1_PI;
		_pi_bCn = M_PI / (JWF_FLOAT) num;
		_bCa = M_PI * a;
		_bCa_bCn = _bCa / (JWF_FLOAT) num;
	}

private:
	JWF_FLOAT sqrtf_safe(JWF_FLOAT x) {
		if (x <= 0.0)
			return 0.0;
		return JWF_SQRT(x);
	}

	int num;
	JWF_FLOAT a;
	JWF_FLOAT _bCa, _bCn_pi, _bCa_bCn, _pi_bCn;
};

