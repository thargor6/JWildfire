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

class PreSpinZFunc: public Variation {
public:
	PreSpinZFunc() {
	}

	const char* getName() const {
		return "pre_spin_z";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    JWF_FLOAT y = _rz_cos * pAffineTP->y - _rz_sin * pAffineTP->x;
    pAffineTP->x = _rz_sin * pAffineTP->y + _rz_cos * pAffineTP->x;
    pAffineTP->y = y;
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
    JWF_SINCOS(pAmount * M_PI_2, &_rz_sin, &_rz_cos);
	}

	int const getPriority() {
		return -1;
	}

	PreSpinZFunc* makeCopy() {
		return new PreSpinZFunc(*this);
	}

private:
	JWF_FLOAT _rz_sin, _rz_cos;
};

