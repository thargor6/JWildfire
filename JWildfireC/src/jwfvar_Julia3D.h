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

#ifndef JWFVAR_JULIA3D_H_
#define JWFVAR_JULIA3D_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class Julia3DFunc: public Variation {
public:
	Julia3DFunc() {
		power = 3.0f;
		initParameterNames(1, "power");
	}

	const char* getName() const {
		return "julia3D";
	}

	void setParameter(char *pName, float pValue) {
		if (strcmp(pName, "power") == 0) {
			power = pValue;
		}
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, float pAmount) {
		_absPower = abs(FTOI(power));
		_cPower = (1.0f / power - 1.0f) * 0.5f;
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
		float z = pAffineTP->z / _absPower;
		float r2d = pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y;
		float r = pAmount * powf(r2d + z * z, _cPower);

		float r2 = r * sqrtf(r2d);
		int rnd = (int) (pContext->randGen->random() * _absPower);
		float angle = (atan2f(pAffineTP->y, pAffineTP->x) + 2 * M_PI * rnd) / (float) power;
		float sina = sinf(angle);
		float cosa = cosf(angle);

		pVarTP->x += r2 * cosa;
		pVarTP->y += r2 * sina;
		pVarTP->z += r * z;
	}

	Julia3DFunc* makeCopy() {
		return new Julia3DFunc(*this);
	}

private:
	float power;
	float _absPower, _cPower;
};

#endif // JWFVAR_JULIA3D_H_
