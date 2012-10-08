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

class Blur3DFunc: public Variation {
public:
	Blur3DFunc() {
	}

	const char* getName() const {
		return "blur3D";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT angle = randGen->random() * twoPi;
		JWF_FLOAT sina = JWF_SIN(angle);
		JWF_FLOAT cosa = JWF_COS(angle);
		JWF_FLOAT r = pAmount * (gauss_rnd[0] + gauss_rnd[1] + gauss_rnd[2] + gauss_rnd[3] - 2.0);
		gauss_rnd[gauss_N] = randGen->random();
		gauss_N = (gauss_N + 1) & 3;
		angle = randGen->random() * M_PI;
		double sinb = JWF_SIN(angle);
		double cosb = JWF_COS(angle);
		pVarTP->x += r * sinb * cosa;
		pVarTP->y += r * sinb * sina;
		pVarTP->z += r * cosb;
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		randGen = pContext->randGen;
		gauss_rnd[0] = randGen->random();
		gauss_rnd[1] = randGen->random();
		gauss_rnd[2] = randGen->random();
		gauss_rnd[3] = randGen->random();
		gauss_N = 0;
		twoPi = 2 * M_PI;
	}

	Blur3DFunc* makeCopy() {
		return new Blur3DFunc(*this);
	}

private:
	JWF_FLOAT gauss_rnd[4];
	JWF_FLOAT twoPi;
	RandGen *randGen;
	int gauss_N;
};

