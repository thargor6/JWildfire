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

class StripesFunc: public Variation {
public:
	StripesFunc() {
	  space = 0.20;
	  warp = 0.60;
		initParameterNames(2, "space", "warp");
	}

	const char* getName() const {
		return "stripes";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "space") == 0) {
			space = pValue;
		}
		else if (strcmp(pName, "warp") == 0) {
			warp = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    JWF_FLOAT roundx = JWF_FLOOR(pAffineTP->x + 0.5);
    JWF_FLOAT offsetx = pAffineTP->x - roundx;

    pVarTP->x += pAmount * (offsetx * (1.0 - space) + roundx);
    pVarTP->y += pAmount * (pAffineTP->y + offsetx * offsetx * warp);
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	StripesFunc* makeCopy() {
		return new StripesFunc(*this);
	}

private:
  JWF_FLOAT space;
  JWF_FLOAT warp;
};

