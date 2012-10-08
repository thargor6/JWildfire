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

class Fan2Func: public Variation {
public:
	Fan2Func() {
		x = 0.20;
		y = 0.50;
		initParameterNames(2, "x", "y");
	}

	const char* getName() const {
		return "fan2";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "x") == 0) {
			x = pValue;
		}
		else if (strcmp(pName, "y") == 0) {
			y = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT r = JWF_SQRT(pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y);
		JWF_FLOAT angle;
		if ((pAffineTP->x < -EPSILON) || (pAffineTP->x > EPSILON) || (pAffineTP->y < -EPSILON) || (pAffineTP->y > EPSILON)) {
			angle = JWF_ATAN2(pAffineTP->x, pAffineTP->y);
		}
		else {
			angle = 0.0f;
		}

		JWF_FLOAT dy = y;
		JWF_FLOAT dx = M_PI * (x * x) + EPSILON;
		JWF_FLOAT dx2 = dx * 0.5;

		JWF_FLOAT t = angle + dy - (int) ((angle + dy) / dx) * dx;
		JWF_FLOAT a;
		if (t > dx2) {
			a = angle - dx2;
		}
		else {
			a = angle + dx2;
		}

		pVarTP->x += pAmount * r * JWF_SIN(a);
		pVarTP->y += pAmount * r * JWF_COS(a);
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	Fan2Func* makeCopy() {
		return new Fan2Func(*this);
	}

private:
	JWF_FLOAT x, y;
};

