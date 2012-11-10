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

class PostCurlFunc: public Variation {
public:
	PostCurlFunc() {
		c1 = 0.0;
		c2 = 0.0;
		initParameterNames(2, "c1", "c2");
	}

	const char* getName() const {
		return "post_curl";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "c1") == 0) {
			c1 = pValue;
		}
		else if (strcmp(pName, "c2") == 0) {
			c2 = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    JWF_FLOAT x = pVarTP->x;
    JWF_FLOAT y = pVarTP->y;

    JWF_FLOAT re = 1 + _c1 * x + _c2 * (JWF_SQR(x) - JWF_SQR(y));
    JWF_FLOAT im = _c1 * y + _c22 * x * y;

    JWF_FLOAT r = JWF_SQR(re) + JWF_SQR(im) + EPSILON;
    pVarTP->x = (x * re + y * im) / r;
    pVarTP->y = (y * re - x * im) / r;
	}

	PostCurlFunc* makeCopy() {
		return new PostCurlFunc(*this);
	}

	int const getPriority() {
		return 1;
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
    _c1 = c1 * pAmount;
    _c2 = c2 * pAmount;
    _c22 = 2 * _c2;
	}

private:
	JWF_FLOAT c1;
	JWF_FLOAT c2;

	JWF_FLOAT _c1, _c2, _c22;
};

