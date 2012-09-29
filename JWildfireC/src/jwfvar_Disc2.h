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
#ifndef JWFVAR_DISC2_H_
#define JWFVAR_DISC2_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class Disc2Func: public Variation {
public:
	Disc2Func() {
		rot = 2.0f;
		twist = 0.5f;
		initParameterNames(2, "rot", "twist");
	}

	const char* getName() const {
		return "disc2";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "rot") == 0) {
			rot = pValue;
		}
		else if (strcmp(pName, "twist") == 0) {
			twist = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		float t = _timespi * (pAffineTP->x + pAffineTP->y);
		float sinr = JWF_SIN(t);
		float cosr = JWF_COS(t);
		float r = pAmount * pAffineTP->getPrecalcAtan() / M_PI;
		pVarTP->x += (sinr + _cosadd) * r;
		pVarTP->y += (cosr + _sinadd) * r;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	Disc2Func* makeCopy() {
		return new Disc2Func(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		float add = twist;
		_timespi = rot * M_PI;
		_sinadd = JWF_SIN(add);
		_cosadd = JWF_COS(add);
		_cosadd -= 1.0f;
		float k;
		if (add > 2.0f * M_PI) {
			k = (1.0f + add - 2.0f * M_PI);
			_cosadd *= k;
			_sinadd *= k;
		}
		if (add < -2.0f * M_PI) {
			k = (1.0f + add + 2.0f * M_PI);
			_cosadd *= k;
			_sinadd *= k;
		}
	}

private:
	float rot;
	float twist;
	float _timespi, _sinadd, _cosadd;
};

#endif // JWFVAR_DISC2_H_
