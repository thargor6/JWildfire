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

#ifndef JWFVAR_ARCH_H_
#define JWFVAR_ARCH_H_

#include "jwf_Variation.h"
#include "jwf_Constants.h"

class ArchFunc: public Variation {
public:
	ArchFunc() {
	}

	const char* getName() const {
		return "arch";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    float ang = pContext->randGen->random() * pAmount * M_PI;
    float sinr = sinf(ang);
    float cosr = cosf(ang);
    if (cosr == 0) {
      return;
    }
    pVarTP->x += pAmount * sinr;
    pVarTP->y += pAmount * (sinr * sinr) / cosr;
    if (pContext->isPreserveZCoordinate) {
      pVarTP->z += pAmount * pAffineTP->z;
    }
	}

	ArchFunc* makeCopy() {
		return new ArchFunc(*this);
	}

};

#endif // JWFVAR_ARCH_H_
