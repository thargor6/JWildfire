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

class Blob3DFunc: public Variation {
public:
	Blob3DFunc() {
		low = 0.3;
		high = 1.2;
		waves = 6.0;
		initParameterNames(3, "low", "high", "waves");
	}

	const char* getName() const {
		return "blob3D";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "low") == 0) {
			low = pValue;
		}
		else if (strcmp(pName, "high") == 0) {
			high = pValue;
		}
		else if (strcmp(pName, "waves") == 0) {
			waves = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT a = JWF_ATAN2(pAffineTP->x, pAffineTP->y);
		JWF_FLOAT r = JWF_SQRT(pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y);
		r = r * (low + (high - low) * (0.5 + 0.5 * JWF_SIN(waves * a)));
		JWF_FLOAT sina, cosa;
		JWF_SINCOS(a, &sina, &cosa);
		JWF_FLOAT nx = sina * r;
		JWF_FLOAT ny = cosa * r;
		JWF_FLOAT nz = JWF_SIN(waves * a) * r;
		pVarTP->x += pAmount * nx;
		pVarTP->y += pAmount * ny;
		pVarTP->z += pAmount * nz;
	}

	Blob3DFunc* makeCopy() {
		return new Blob3DFunc(*this);
	}

private:
	JWF_FLOAT low;
	JWF_FLOAT high;
	JWF_FLOAT waves;
};

