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

class SplitFunc: public Variation {
public:
	SplitFunc() {
		xsize = 0.40;
		ysize = 0.60;
		initParameterNames(2, "xsize", "ysize");
	}

	const char* getName() const {
		return "split";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "xsize") == 0) {
			xsize = pValue;
		}
		else if (strcmp(pName, "ysize") == 0) {
			ysize = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		if (JWF_COS(pAffineTP->x * xsize * M_PI) >= 0) {
			pVarTP->y += pAmount * pAffineTP->y;
		}
		else {
			pVarTP->y -= pAmount * pAffineTP->y;
		}

		if (JWF_COS(pAffineTP->y * ysize * M_PI) >= 0) {
			pVarTP->x += pAmount * pAffineTP->x;
		}
		else {
			pVarTP->x -= pAmount * pAffineTP->x;
		}
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	SplitFunc* makeCopy() {
		return new SplitFunc(*this);
	}

private:
	JWF_FLOAT xsize;
	JWF_FLOAT ysize;
};

