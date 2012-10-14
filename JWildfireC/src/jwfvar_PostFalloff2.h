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

class PostFalloff2Func: public Falloff2Func {
public:
	PostFalloff2Func() {
	}

	const char* getName() const {
		return "post_falloff2";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    /* post_falloff2 by Xyrus02 */
    switch (type) {
      case 1:
        calcFunctionRadial(pContext, pXForm, pVarTP->x, pVarTP->y, pVarTP->z, pAffineTP, pVarTP, pAmount);
        break;
      case 2:
        calcFunctionGaussian(pContext, pXForm, pVarTP->x, pVarTP->y, pVarTP->z, pAffineTP, pVarTP, pAmount);
        break;
      default:
        calcFunction(pContext, pXForm, pVarTP->x, pVarTP->y, pVarTP->z, pAffineTP, pVarTP, pAmount);
        break;
    }
	}

	int const getPriority() {
		return 1;
	}

	PostFalloff2Func* makeCopy() {
		return new PostFalloff2Func(*this);
	}

};

