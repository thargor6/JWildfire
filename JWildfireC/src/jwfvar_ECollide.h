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
#ifndef JWFVAR_ECOLLIDE_H_
#define JWFVAR_ECOLLIDE_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class ECollideFunc: public Variation {
public:
	ECollideFunc() {
		num = 1;
		a = 0.0f;
		initParameterNames(2, "num", "a");
	}

	const char* getName() const {
		return "eCollide";
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
		float tmp = pAffineTP->y * pAffineTP->y + pAffineTP->x * pAffineTP->x + 1.0f;
		float tmp2 = 2.0f * pAffineTP->x;
		float xmax = (sqrtf_safe(tmp + tmp2) + sqrtf_safe(tmp - tmp2)) * 0.5f;
		float sinnu, cosnu;
		int alt;
		if (xmax < 1.0f)
			xmax = 1.0f;

		double t = pAffineTP->x / xmax;
		if (t > 1.0f)
			t = 1.0f;
		else if (t < -1.0f)
			t = -1.0f;
		double nu = acosf(t); // -Pi < nu < Pi

		if (pAffineTP->y > 0.0f) {
			alt = (int) (nu * _eCn_pi);
			if (alt % 2 == 0)
				nu = alt * _pi_eCn + fmodf(nu + _eCa_eCn, _pi_eCn);
			else
				nu = alt * _pi_eCn + fmodf(nu - _eCa_eCn, _pi_eCn);

		}
		else {
			alt = (int) (nu * _eCn_pi);
			if (alt % 2 == 0.0f)
				nu = alt * _pi_eCn + fmodf(nu + _eCa_eCn, _pi_eCn);
			else
				nu = alt * _pi_eCn + fmodf(nu - _eCa_eCn, _pi_eCn);

			nu *= -1.0f;
		}

		sinnu = JWF_SIN(nu);
		cosnu = JWF_COS(nu);
		pVarTP->x += pAmount * xmax * cosnu;
		pVarTP->y += pAmount * JWF_SQRT(xmax - 1.0) * JWF_SQRT(xmax + 1.0) * sinnu;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	ECollideFunc* makeCopy() {
		return new ECollideFunc(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		_eCn_pi = (JWF_FLOAT) num * M_1_PI;
		_pi_eCn = M_PI / (JWF_FLOAT) num;
		_eCa = M_PI * a;
		_eCa_eCn = _eCa / (JWF_FLOAT) num;
	}

private:
	float sqrtf_safe(float x) {
		if (x <= 0.0f)
			return 0.0f;
		return JWF_SQRT(x);
	}

	int num;
	float a;
	float _eCa, _eCn_pi, _eCa_eCn, _pi_eCn;
};

#endif // JWFVAR_ECOLLIDE_H_
