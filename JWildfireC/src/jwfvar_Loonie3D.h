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

#ifndef JWFVAR_LOONIE_3D_H_
#define JWFVAR_LOONIE_3D_H_

#include "jwf_Variation.h"

class Loonie3DFunc: public Variation {
public:
	Loonie3DFunc() {
	}

	const char* getName() const {
		return "loonie_3D";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    float sqrvvar = pAmount * pAmount;
    float efTez = pAffineTP->z;
    float kikr;
    kikr = atan2f(pAffineTP->y, pAffineTP->x);

    if (efTez == 0.0) {
      efTez = kikr;
    }

    float r2 = pAffineTP->x*pAffineTP->x + pAffineTP->y*pAffineTP->y + efTez*efTez; // added the z element
    if (r2 < sqrvvar) {
    	float r = pAmount * sqrtf(sqrvvar / r2 - 1.0f);
      pVarTP->x += r * pAffineTP->x;
      pVarTP->y += r * pAffineTP->y;
      pVarTP->z += r * efTez * 0.5f;
    }
    else {
      pVarTP->x += pAmount * pAffineTP->x;
      pVarTP->y += pAmount * pAffineTP->y;
      pVarTP->z += pAmount * efTez * 0.5f;
    }
	}

	Loonie3DFunc* makeCopy() {
		return new Loonie3DFunc(*this);
	}

};

#endif // JWFVAR_LOONIE_3D_H_
