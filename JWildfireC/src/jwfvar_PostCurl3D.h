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

class PostCurl3DFunc: public Variation {
public:
	PostCurl3DFunc() {
		cx = 0.0;
		cy = 0.0;
		cz = 0.0;
		initParameterNames(3, "cx", "cy", "cz");
	}

	const char* getName() const {
		return "post_curl3D";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "cx") == 0) {
			cx = pValue;
		}
		else if (strcmp(pName, "cy") == 0) {
			cy = pValue;
		}
		else if (strcmp(pName, "cz") == 0) {
			cz = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    JWF_FLOAT r2 = JWF_SQR(pVarTP->x) + JWF_SQR(pVarTP->y) + JWF_SQR(pVarTP->z);
    JWF_FLOAT r = 1.0 / (r2 * _c_2 + _c2x * pVarTP->x - _c2y * pVarTP->y + _c2z * pVarTP->z + 1.0);
    pVarTP->x = r * (pVarTP->x + _cx * r2);
    pVarTP->y = r * (pVarTP->y + _cy * r2);
    pVarTP->z = r * (pVarTP->z + _cz * r2);
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
    _cx = pAmount * cx;
    _cy = pAmount * cy;
    _cz = pAmount * cz;

    _c2x = 2 * _cx;
    _c2y = 2 * _cy;
    _c2z = 2 * _cz;

    _cx2 = JWF_SQR(_cx);
    _cy2 = JWF_SQR(_cy);
    _cz2 = JWF_SQR(_cz);

    _c_2 = _cx2 + _cy2 + _cz2;
	}

	PostCurl3DFunc* makeCopy() {
		return new PostCurl3DFunc(*this);
	}

	int const getPriority() {
		return 1;
	}

private:
	JWF_FLOAT cx;
	JWF_FLOAT cy;
	JWF_FLOAT cz;

	JWF_FLOAT _cx, _cy, _cz, _c2x, _c2y, _c2z, _cx2, _cy2, _cz2, _c_2;
};

