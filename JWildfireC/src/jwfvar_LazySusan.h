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

class LazySusanFunc: public Variation {
public:
	LazySusanFunc() {
	  space = 0.40;
	  twist = 0.20;
	  spin = 0.10;
	  x = 0.10;
	  y = 0.20;
		initParameterNames(5, "space", "twist", "spin", "x", "y");
	}

	const char* getName() const {
		return "lazysusan";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "space") == 0) {
			space = pValue;
		}
		else if (strcmp(pName, "twist") == 0) {
			twist = pValue;
		}
		else if (strcmp(pName, "spin") == 0) {
			spin = pValue;
		}
		else if (strcmp(pName, "x") == 0) {
			x = pValue;
		}
		else if (strcmp(pName, "y") == 0) {
			y = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    JWF_FLOAT xx = pAffineTP->x - x;
    JWF_FLOAT yy = pAffineTP->y + y;
    JWF_FLOAT rr = JWF_SQRT(xx * xx + yy * yy);

    if (rr < pAmount) {
      JWF_FLOAT a = JWF_ATAN2(yy, xx) + spin + twist * (pAmount - rr);
      JWF_FLOAT sina = JWF_SIN(a);
      JWF_FLOAT cosa = JWF_COS(a);
      rr = pAmount * rr;

      pVarTP->x += rr * cosa + x;
      pVarTP->y += rr * sina - y;
    }
    else {
      rr = pAmount * (1.0 + space / rr);

      pVarTP->x += rr * xx + x;
      pVarTP->y += rr * yy - y;
    }
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	LazySusanFunc* makeCopy() {
		return new LazySusanFunc(*this);
	}

private:
  JWF_FLOAT space;
  JWF_FLOAT twist;
  JWF_FLOAT spin;
  JWF_FLOAT x;
  JWF_FLOAT y;
};

