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

#ifndef JWFVAR_BOARDERS_H_
#define JWFVAR_BOARDERS_H_

#include "jwf_Variation.h"

class BoardersFunc: public Variation {
public:
	BoardersFunc() {
	}

	const char* getName() const {
		return "boarders";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    float roundX = rintf(pAffineTP->x);
    float roundY = rintf(pAffineTP->y);
    float offsetX = pAffineTP->x - roundX;
    float offsetY = pAffineTP->y - roundY;

    if (pContext->randGen->random() >= 0.75f) {
      pVarTP->x += pAmount * (offsetX * 0.5f + roundX);
      pVarTP->y += pAmount * (offsetY * 0.5f + roundY);
    }
    else {
      if (fabs(offsetX) >= fabs(offsetY)) {

        if (offsetX >= 0.0) {
          pVarTP->x += pAmount * (offsetX * 0.5f + roundX + 0.25f);
          pVarTP->y += pAmount * (offsetY * 0.5f + roundY + 0.25f * offsetY / offsetX);
        }
        else {
          pVarTP->x += pAmount * (offsetX * 0.5f + roundX - 0.25f);
          pVarTP->y += pAmount * (offsetY * 0.5f + roundY - 0.25f * offsetY / offsetX);
        }

      }
      else {

        if (offsetY >= 0.0f) {
          pVarTP->y += pAmount * (offsetY * 0.5f + roundY + 0.25f);
          pVarTP->x += pAmount * (offsetX * 0.5f + roundX + offsetX / offsetY * 0.25f);
        }
        else {
          pVarTP->y += pAmount * (offsetY * 0.5f + roundY - 0.25f);
          pVarTP->x += pAmount * (offsetX * 0.5f + roundX - offsetX / offsetY * 0.25f);
        }
      }
    }
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	BoardersFunc* makeCopy() {
		return new BoardersFunc(*this);
	}

};

#endif // JWFVAR_BOARDERS_H_
