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
#ifndef JWFVAR_WAFFLE_H_
#define JWFVAR_WAFFLE_H_

#include "jwf_Variation.h"

class WaffleFunc: public Variation {
public:
	WaffleFunc() {
		slices = 6;
		xthickness = 0.5f;
		ythickness = 0.5f;
		rotation = 0.0f;
		initParameterNames(4, "slices", "xthickness", "ythickness", "rotation");
	}

	const char* getName() const {
		return "waffle";
	}

	void setParameter(char *pName, float pValue) {
		if (strcmp(pName, "slices") == 0) {
			slices = FTOI(pValue);
		}
		else if (strcmp(pName, "xthickness") == 0) {
			xthickness = pValue;
		}
		else if (strcmp(pName, "ythickness") == 0) {
			ythickness = pValue;
		}
		else if (strcmp(pName, "rotation") == 0) {
			rotation = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    float a = 0.0, r = 0.0;
    switch (pContext->randGen->random(5)) {
      case 0:
        a = (pContext->randGen->random(slices) + pContext->randGen->random() * xthickness) / slices;
        r = (pContext->randGen->random(slices) + pContext->randGen->random() * ythickness) / slices;
        break;
      case 1:
        a = (pContext->randGen->random(slices) + pContext->randGen->random()) / slices;
        r = (pContext->randGen->random(slices) + ythickness) / slices;
        break;
      case 2:
        a = (pContext->randGen->random(slices) + xthickness) / slices;
        r = (pContext->randGen->random(slices) + pContext->randGen->random()) / slices;
        break;
      case 3:
        a = pContext->randGen->random();
        r = (pContext->randGen->random(slices) + ythickness + pContext->randGen->random() * (1.0f - ythickness)) / slices;
        break;
      case 4:
        a = (pContext->randGen->random(slices) + xthickness + pContext->randGen->random() * (1.0f - xthickness)) / slices;
        r = pContext->randGen->random();
        break;
    }
    pVarTP->x += (_vcosr * a + _vsinr * r); // note that post-transforms make this redundant!
    pVarTP->y += (-_vsinr * a + _vcosr * r);
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, float pAmount) {
    _vcosr = pAmount * cosf(rotation);
    _vsinr = pAmount * sinf(rotation);
	}

	WaffleFunc* makeCopy() {
		return new WaffleFunc(*this);
	}

private:
	int slices;
	float xthickness;
	float ythickness;
	float rotation;
	float _vcosr, _vsinr;
};

#endif // JWFVAR_WAFFLE_H_
