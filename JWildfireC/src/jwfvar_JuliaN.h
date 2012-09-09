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
#ifndef JWFVAR_JULIAN_H_
#define JWFVAR_JULIAN_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class JuliaNFunc: public Variation {
public:

	JuliaNFunc() {
		power=3.0f;
		dist=1.0f;
		initParameterNames(2, "power", "dist");
	}

	const char* getName() const {
		return "julian";
	}

	void setParameter(char *pName, float pValue) {
		if (strcmp(pName, "power") == 0) {
			power = pValue;
		}
		else if (strcmp(pName, "dist") == 0) {
			dist = pValue;
		}
	}

	void init(FlameTransformationContext *pContext, float pAmount) {
		_absPower = fabs(power);
		_cPower = dist / power * 0.5f;
		_pAmount2 = pAmount * sqrtf(2.0f) / 2.0f;
	}

	void transform(FlameTransformationContext *pContext, XYZPoint *pAffineTP,
			XYZPoint *pVarTP, float pAmount) {
		float a = (atan2f(pAffineTP->y, pAffineTP->x)
				+ 2.0f * M_PI * pContext->randGen->random(_absPower)) / power;
		float sina = sinf(a);
		float cosa = cosf(a);
		float r = pAmount
				* powf(pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y,
						_cPower);

		pVarTP->x = pVarTP->x + r * cosa;
		pVarTP->y = pVarTP->y + r * sina;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	JuliaNFunc* makeCopy() {
		return new JuliaNFunc(*this);
	}


private:
	float _absPower, _cPower, _pAmount2;
	float power, dist;
};

#endif // JWFVAR_JULIAN_H_
