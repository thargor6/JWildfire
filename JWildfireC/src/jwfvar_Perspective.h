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
#ifndef JWFVAR_PERSPECTIVE_H_
#define JWFVAR_PERSPECTIVE_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class PerspectiveFunc: public Variation {
public:
	PerspectiveFunc() {
		angle = 0.62;
		dist = 2.2;
		initParameterNames(2, "angle", "dist");
	}

	const char* getName() const {
		return "perspective";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "angle") == 0) {
			angle = pValue;
		}
		else if (strcmp(pName, "dist") == 0) {
			dist = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		float d = (dist - pAffineTP->y * _vsin);
		if (d == 0) {
			return;
		}
		float t = 1.0f / d;
		pVarTP->x += pAmount * dist * pAffineTP->x * t;
		pVarTP->y += pAmount * _vfcos * pAffineTP->y * t;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	PerspectiveFunc* makeCopy() {
		return new PerspectiveFunc(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		float ang = angle * M_PI / 2.0f;
		_vsin = JWF_SIN(ang);
		_vfcos = dist * JWF_COS(ang);
	}

private:
	float angle;
	float dist;
	float _vsin, _vfcos;
};

#endif // JWFVAR_PERSPECTIVE_H_
