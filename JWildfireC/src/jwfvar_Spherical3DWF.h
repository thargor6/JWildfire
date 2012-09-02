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
#ifndef JWFVAR_SPHERICAL3D_WF_H_
#define JWFVAR_SPHERICAL3D_WF_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class Spherical3DWFFunc: public Variation {
public:
	Spherical3DWFFunc() {
		invert = 0.0f;
		exponent = 2.0f;
		initParameterNames(2, "invert", "exponent");
	}

	const char* getName() const {
		return "spherical3D_wf";
	}

	void setParameter(char *pName, float pValue) {
		if (strcmp(pName, "invert") == 0) {
			invert = pValue;
		}
		else if (strcmp(pName, "exponent") == 0) {
			exponent = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
/*
		float r;
    if (_regularForm) {
      r = pAmount / (pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y + pAffineTP->z * pAffineTP->z + EPSILON);
    }
    else {
      r = pAmount / powf(pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y + pAffineTP->z * pAffineTP->z + EPSILON, exponent / 2.0f);
    }
    if (_dontInvert) {
      pVarTP->x += pAffineTP->x * r;
      pVarTP->y += pAffineTP->y * r;
      pVarTP->z += pAffineTP->z * r;
    }
    else {
      pVarTP->x -= pAffineTP->x * r;
      pVarTP->y -= pAffineTP->y * r;
      pVarTP->z -= pAffineTP->z * r;
    }
    */
	}

	void init(FlameTransformationContext *pContext, float pAmount) {
    _regularForm = fabs(exponent - 2.0f) < EPSILON;
    _dontInvert =fabs(invert)<EPSILON;
	}

	Spherical3DWFFunc* makeCopy() {
		return new Spherical3DWFFunc(*this);
	}

private:
	float invert;
	float exponent;

  bool _dontInvert;
	bool _regularForm;
};

#endif // JWFVAR_SPHERICAL3D_WF_H_
