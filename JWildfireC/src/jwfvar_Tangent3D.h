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

class Tangent3DFunc: public Variation {
public:
	Tangent3DFunc() {
	}

	const char* getName() const {
		return "tangent3D";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    pVarTP->x += pAmount * JWF_SIN(pAffineTP->x) / JWF_COS(pAffineTP->y);
    pVarTP->y += pAmount * JWF_TAN(pAffineTP->y);
    pVarTP->z += pAmount * JWF_TAN(pAffineTP->x);
	}

	Tangent3DFunc* makeCopy() {
		return new Tangent3DFunc(*this);
	}

};

