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

class Curl3DFunc: public Variation {
public:
	Curl3DFunc() {
		cx = 0.0;
		cy = 0.0;
		cz = 0.0;
		initParameterNames(3, "cx", "cy", "cz");
	}

	const char* getName() const {
		return "curl3D";
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
		JWF_FLOAT r2 = pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y + pAffineTP->z * pAffineTP->z;
		JWF_FLOAT r = pAmount / (r2 * _c2 + _c2x * pAffineTP->x - _c2y * pAffineTP->y + _c2z * pAffineTP->z + 1.0);

		pVarTP->x += r * (pAffineTP->x + cx * r2);
		pVarTP->y += r * (pAffineTP->y - cy * r2);
		pVarTP->z += r * (pAffineTP->z + cz * r2);
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		_c2x = 2.0 * cx;
		_c2y = 2.0 * cy;
		_c2z = 2.0 * cz;

		JWF_FLOAT cx2 = cx * cx;
		JWF_FLOAT cy2 = cy * cy;
		JWF_FLOAT cz2 = cz * cz;

		_c2 = cx2 + cy2 + cz2;
	}

	Curl3DFunc* makeCopy() {
		return new Curl3DFunc(*this);
	}

private:
	JWF_FLOAT cx;
	JWF_FLOAT cy;
	JWF_FLOAT cz;

	JWF_FLOAT _c2x, _c2y, _c2z, _c2;
};

