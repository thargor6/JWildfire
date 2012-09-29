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

#ifndef JWFVAR_LISSAJOUS_H_
#define JWFVAR_LISSAJOUS_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class LissajousFunc: public Variation {
public:
	LissajousFunc() {
		tmin = -M_PI;
		tmax = M_PI;
		a = 3.0f;
		b = 2.0f;
		c = 0.0f;
		d = 0.0f;
		e = 0.0f;
		initParameterNames(7, "tmin", "tmax", "a", "b", "c", "d", "e");
	}

	const char* getName() const {
		return "lissajous";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "tmin") == 0) {
			tmin = pValue;
		}
		else if (strcmp(pName, "tmax") == 0) {
			tmax = pValue;
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
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		float t = (tmax - tmin) * pContext->randGen->random() + tmin;
		float y = pContext->randGen->random() - 0.5f;
		float x1 = JWF_SIN(a * t + d);
		float y1 = JWF_SIN(b * t);
		pVarTP->x += pAmount * (x1 + c * t + e * y);
		pVarTP->y += pAmount * (y1 + c * t + e * y);
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	LissajousFunc* makeCopy() {
		return new LissajousFunc(*this);
	}

private:
	float tmin, tmax, a, b, c, d, e;
};

#endif // JWFVAR_LISSAJOUS_H_
