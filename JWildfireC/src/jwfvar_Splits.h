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

class SplitsFunc: public Variation {
public:
	SplitsFunc() {
		x = 0.40;
		y = 0.60;
		initParameterNames(2, "x", "y");
	}

	const char* getName() const {
		return "splits";
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
		if (pAffineTP->x >= 0.0) {
			pVarTP->x += pAmount * (pAffineTP->x + x);
		}
		else {
			pVarTP->x += pAmount * (pAffineTP->x - x);
		}

		if (pAffineTP->y >= 0.0) {
			pVarTP->y += pAmount * (pAffineTP->y + y);
		}
		else {
			pVarTP->y += pAmount * (pAffineTP->y - y);
		}
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	SplitsFunc* makeCopy() {
		return new SplitsFunc(*this);
	}

private:
	JWF_FLOAT x;
	JWF_FLOAT y;
};

