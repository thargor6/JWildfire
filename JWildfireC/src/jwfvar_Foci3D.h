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

#include "jwf_Variation.h"

class Foci3DFunc: public Variation {
public:
	Foci3DFunc() {
	}

	const char* getName() const {
		return "foci_3D";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT expx = JWF_EXP(pAffineTP->x) * 0.5;
		JWF_FLOAT expnx = 0.25 / expx;
		JWF_FLOAT kikr, boot;
		boot = pAffineTP->z;
		kikr = JWF_ATAN2(pAffineTP->y, pAffineTP->x);
		if (boot == 0.0) {
			boot = kikr;
		}
		JWF_FLOAT siny = JWF_SIN(pAffineTP->y);
		JWF_FLOAT cosy = JWF_COS(pAffineTP->y);
		JWF_FLOAT sinz = JWF_SIN(boot);
		JWF_FLOAT cosz = JWF_COS(boot);
		JWF_FLOAT tmp = pAmount / (expx + expnx - (cosy * cosz));

		pVarTP->x += (expx - expnx) * tmp;
		pVarTP->y += siny * tmp;
		pVarTP->z += sinz * tmp;
	}

	Foci3DFunc* makeCopy() {
		return new Foci3DFunc(*this);
	}

};

