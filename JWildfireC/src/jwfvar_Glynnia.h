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

#ifndef JWFVAR_GLYNNIA_H_
#define JWFVAR_GLYNNIA_H_

#include "jwf_Variation.h"

class GlynniaFunc: public Variation {
public:
	GlynniaFunc() {
	}

	const char* getName() const {
		return "glynnia";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		float r = JWF_SQRT(pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y);
		float d;

		if (r >= 1.0f) {
			if (pContext->randGen->random() > 0.5f) {
				d = JWF_SQRT(r + pAffineTP->x);
				if (d == 0) {
					return;
				}
				pVarTP->x += _vvar2 * d;
				pVarTP->y -= _vvar2 / d * pAffineTP->y; //+= _vvar2 / d * pAffineTP->y;
			}
			else {
				d = r + pAffineTP->x;
				float dx = JWF_SQRT(r * (pAffineTP->y * pAffineTP->y + d * d));
				if (dx == 0) {
					return;
				}
				r = pAmount / dx;
				pVarTP->x += r * d;
				pVarTP->y += r * pAffineTP->y; //-= r * pAffineTP->y;
			}
		}
		else {
			if (pContext->randGen->random() > 0.5f) {
				d = JWF_SQRT(r + pAffineTP->x);
				if (d == 0) {
					return;
				}
				pVarTP->x -= _vvar2 * d;
				pVarTP->y -= _vvar2 / d * pAffineTP->y;
			}
			else {
				d = r + pAffineTP->x;
				float dx = JWF_SQRT(r * (pAffineTP->y * pAffineTP->y + d * d));
				if (dx == 0) {
					return;
				}
				r = pAmount / dx;
				pVarTP->x -= r * d;
				pVarTP->y += r * pAffineTP->y;
			}
		}
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	GlynniaFunc* makeCopy() {
		return new GlynniaFunc(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		_vvar2 = pAmount * JWF_SQRT(2.0f) / 2.0f;
	}

private:
	float _vvar2;
};

#endif // JWFVAR_GLYNNIA_H_
