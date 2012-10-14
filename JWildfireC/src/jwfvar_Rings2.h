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

class Rings2Func: public Variation {
public:
	Rings2Func() {
		val = 0.0;
		initParameterNames(1, "val");
	}

	const char* getName() const {
		return "rings2";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "val") == 0) {
			val = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    JWF_FLOAT l = pAffineTP->getPrecalcSqrt();
    if (_dx == 0 || l == 0) {
      return;
    }
    JWF_FLOAT r = pAmount * (2.0 - _dx * ((int) ((l / _dx + 1) / 2) * 2 / l + 1));

    pVarTP->x += r * pAffineTP->x;
    pVarTP->y += r * pAffineTP->y;

    if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	Rings2Func* makeCopy() {
		return new Rings2Func(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
    _dx = pAmount * pAmount + EPSILON;
	}


private:
	JWF_FLOAT val;
	JWF_FLOAT _dx;
};

