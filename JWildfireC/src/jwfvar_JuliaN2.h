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

class JuliaN2Func: public Variation {
public:

	JuliaN2Func() {
		power = 3.0;
		dist = 1.0;
    a = 1.0;
    b = 0.0;
    c = 0.0;
    d = 1.0;
    e = 0.0;
    f = 0.0;
		initParameterNames(8, "power", "dist", "a", "b", "c", "d", "e", "f");
	}

	const char* getName() const {
		return "julian2";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "power") == 0) {
			power = pValue;
		}
		else if (strcmp(pName, "dist") == 0) {
			dist = pValue;
		}
		else if (strcmp(pName, "a") == 0) {
			a = pValue;
		}
		else if (strcmp(pName, "b") == 0) {
			b = pValue;
		}
		else if (strcmp(pName, "c") == 0) {
			c = pValue;
		}
		else if (strcmp(pName, "d") == 0) {
			d = pValue;
		}
		else if (strcmp(pName, "e") == 0) {
			e = pValue;
		}
		else if (strcmp(pName, "f") == 0) {
			f = pValue;
		}
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		_absN = JWF_FABS(power);
		_cN = dist / power * 0.5;
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    JWF_FLOAT x = a * pAffineTP->x + b * pAffineTP->y + e;
    JWF_FLOAT y = c * pAffineTP->x + d * pAffineTP->y + f;

    JWF_FLOAT angle = (JWF_ATAN2(y, x) + M_2PI * (pContext->randGen->random(INT_MAX) % _absN)) / power;
    JWF_FLOAT r = pAmount * JWF_POW(JWF_SQR(x) + JWF_SQR(y), _cN);

		JWF_FLOAT sina, cosa;
		JWF_SINCOS(angle, &sina, &cosa);
    pVarTP->x += r * cosa;
    pVarTP->y += r * sina;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	JuliaN2Func* makeCopy() {
		return new JuliaN2Func(*this);
	}

private:
	int _absN;
	JWF_FLOAT _cN;
	JWF_FLOAT power, dist;
	JWF_FLOAT a, b, c, d, e, f;
};

