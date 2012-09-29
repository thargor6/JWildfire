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
#ifndef JWFVAR_JULIASCOPE_H_
#define JWFVAR_JULIASCOPE_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class JuliascopeFunc: public Variation {
public:

	JuliascopeFunc() {
		power = 3.0f;
		dist = 1.0f;
		initParameterNames(2, "power", "dist");
	}

	const char* getName() const {
		return "juliascope";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "power") == 0) {
			power = pValue;
		}
		else if (strcmp(pName, "dist") == 0) {
			dist = pValue;
		}
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		_absPower = abs(FTOI(power));
		_cPower = dist / power * 0.5f;
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		int rnd = pContext->randGen->random(_absPower);
		float a;
		if ((rnd & 1) == 0)
			a = (2 * M_PI * rnd + atan2f(pAffineTP->y, pAffineTP->x)) / power;
		else
			a = (2 * M_PI * rnd - atan2f(pAffineTP->y, pAffineTP->x)) / power;
		float sina = JWF_SIN(a);
		float cosa = JWF_COS(a);

		double r = pAmount * JWF_POW(pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y, _cPower);
		pVarTP->x = pVarTP->x + r * cosa;
		pVarTP->y = pVarTP->y + r * sina;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	JuliascopeFunc* makeCopy() {
		return new JuliascopeFunc(*this);
	}

private:
	float power, dist;
	float _absPower, _cPower;
};

#endif // JWFVAR_JULIASCOPE_H_
