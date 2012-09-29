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

#ifndef JWFVAR_JULIA3D_Z_H_
#define JWFVAR_JULIA3D_Z_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class Julia3DZFunc: public Variation {
public:
	Julia3DZFunc() {
		power = 3.0f;
		initParameterNames(1, "power");
	}

	const char* getName() const {
		return "julia3Dz";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "power") == 0) {
			power = pValue;
		}
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		_absPower = abs(FTOI(power));
		_cPower = 1.0f / power * 0.5f;
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		float r2d = pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y;
		float r = pAmount * JWF_POW(r2d, _cPower);

		int rnd = (int) (pContext->randGen->random() * _absPower);
		float angle = (atan2f(pAffineTP->y, pAffineTP->x) + 2.0f * M_PI * rnd) / power;
		float sina = JWF_SIN(angle);
		float cosa = JWF_COS(angle);
		pVarTP->x += r * cosa;
		pVarTP->y += r * sina;
		pVarTP->z += r * pAffineTP->z / (sqrtf(r2d) * _absPower);
	}

	Julia3DZFunc* makeCopy() {
		return new Julia3DZFunc(*this);
	}

private:
	float power;
	float _absPower, _cPower;
};

#endif // JWFVAR_JULIA3D_Z_H_
