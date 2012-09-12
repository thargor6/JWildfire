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
#ifndef JWFVAR_LINEART_3D_H_
#define JWFVAR_LINEART_3D_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class LinearT3DFunc: public Variation {
public:
	LinearT3DFunc() {
		powX = 1.2f;
		powY = 1.2f;
		powZ = 1.2f;
		initParameterNames(3, "powX", "powY", "powZ");
	}

	const char* getName() const {
		return "linearT3D";
	}

	void setParameter(char *pName, float pValue) {
		if (strcmp(pName, "powX") == 0) {
			powX = pValue;
		}
		else if (strcmp(pName, "powY") == 0) {
			powY = pValue;
		}
		else if (strcmp(pName, "powZ") == 0) {
			powZ = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    pVarTP->x += sgn(pAffineTP->x) * powf(fabs(pAffineTP->x), powX) * pAmount;
    pVarTP->y += sgn(pAffineTP->y) * powf(fabs(pAffineTP->y), powY) * pAmount;
    pVarTP->z += sgn(pAffineTP->z) * powf(fabs(pAffineTP->z), powZ) * pAmount;
	}

	LinearT3DFunc* makeCopy() {
		return new LinearT3DFunc(*this);
	}

private:
  float sgn(float arg) {
    if (arg > 0)
      return 1.0f;
    else
     return -1.0f;
  }

	float powX;
	float powY;
	float powZ;
};

#endif // JWFVAR_LINEART_3D_H_
