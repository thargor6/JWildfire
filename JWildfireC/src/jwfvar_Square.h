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
#ifndef JWFVAR_SQUARE_H_
#define JWFVAR_SQUARE_H_

#include "jwf_Variation.h"

class SquareFunc: public Variation {
public:
	SquareFunc() {
	}

	const char* getName() const {
		return "square";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    pVarTP->x += pAmount * (pContext->randGen->random() - 0.5f);
    pVarTP->y += pAmount * (pContext->randGen->random() - 0.5f);
    if (pContext->isPreserveZCoordinate) {
      pVarTP->z += pAmount * pAffineTP->z;
    }
	}

	SquareFunc* makeCopy() {
		return new SquareFunc(*this);
	}

};

#endif // JWFVAR_SQUARE_H_
