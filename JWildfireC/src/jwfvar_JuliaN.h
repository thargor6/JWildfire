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

class JuliaNFunc: public Variation {
public:

	JuliaNFunc() {
		power = 3.0;
		dist = 1.0;
		initParameterNames(2, "power", "dist");
	}

	const char* getName() const {
		return "julian";
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
		_absPower = JWF_FABS(power);
		_cPower = dist / power * 0.5;
		_pAmount2 = pAmount * JWF_SQRT(2.0) / 2.0;
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT a = (JWF_ATAN2(pAffineTP->y, pAffineTP->x) + 2.0 * M_PI * pContext->randGen->random(_absPower)) / power;
		JWF_FLOAT sina, cosa;
		JWF_SINCOS(a, &sina, &cosa);
		JWF_FLOAT r = pAmount * JWF_POW(pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y, _cPower);

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
	JWF_FLOAT _absPower, _cPower, _pAmount2;
	JWF_FLOAT power, dist;
};

