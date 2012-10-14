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

class PostSpinZFunc: public Variation {
public:
	PostSpinZFunc() {
	}

	const char* getName() const {
		return "post_spin_z";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    /* post_spin_z by Larry Berlin, http://aporev.deviantart.com/art/New-3D-Plugins-136484533?q=gallery%3Aaporev%2F8229210&qo=22 */
    JWF_FLOAT y = _pz_cos * pVarTP->y - _pz_sin * pVarTP->x;
    pVarTP->x = _pz_sin * pVarTP->y + _pz_cos * pVarTP->x;
    pVarTP->y = y;
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
    JWF_SINCOS(pAmount * M_PI_2, &_pz_sin, &_pz_cos);
	}

	int const getPriority() {
		return 1;
	}

	PostSpinZFunc* makeCopy() {
		return new PostSpinZFunc(*this);
	}

private:
	JWF_FLOAT _pz_sin, _pz_cos;
};

