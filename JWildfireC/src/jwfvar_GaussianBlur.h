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

#ifndef JWFVAR_GAUSSIAN_BLUR_H_
#define JWFVAR_GAUSSIAN_BLUR_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class GaussianBlurFunc: public Variation {
public:
	GaussianBlurFunc() {}

	const char* getName() const {
		return "gaussian_blur";
	}

  void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    float r = pContext->randGen->random() * 2.0f * M_PI;
    float sina = sinf(r);
    float cosa = cosf(r);
    r = pAmount * (pContext->randGen->random() + pContext->randGen->random() + pContext->randGen->random() + pContext->randGen->random() - 2.0f);
    pVarTP->x += r * cosa;
    pVarTP->y += r * sina;
    if (pContext->isPreserveZCoordinate) {
      pVarTP->z += pAmount * pAffineTP->z;
    }
  }

	GaussianBlurFunc* makeCopy() {
		return new GaussianBlurFunc(*this);
	}

};


#endif // JWFVAR_GAUSSIAN_BLUR_H_
