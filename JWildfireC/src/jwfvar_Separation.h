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

class SeparationFunc: public Variation {
public:
	SeparationFunc() {
	  x = 0.5;
	  xinside = 0.05;
	  y = 0.25;
	  yinside = 0.025;
		initParameterNames(4, "x", "xinside", "y", "yinside");
	}

	const char* getName() const {
		return "separation";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "x") == 0) {
			x = pValue;
		}
		else if (strcmp(pName, "xinside") == 0) {
			xinside = pValue;
		}
		else if (strcmp(pName, "y") == 0) {
			y = pValue;
		}
		else if (strcmp(pName, "yinside") == 0) {
			yinside = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    JWF_FLOAT sx2 = x * x;
    JWF_FLOAT sy2 = y * y;

    if (pAffineTP->x > 0.0) {
      pVarTP->x += pAmount * (JWF_SQRT(pAffineTP->x * pAffineTP->x + sx2) - pAffineTP->x * xinside);
    }
    else {
      pVarTP->x -= pAmount * (JWF_SQRT(pAffineTP->x * pAffineTP->x + sx2) + pAffineTP->x * xinside);
    }

    if (pAffineTP->y > 0.0) {
      pVarTP->y += pAmount * (JWF_SQRT(pAffineTP->y * pAffineTP->y + sy2) - pAffineTP->y * yinside);
    }
    else {
      pVarTP->y -= pAmount * (JWF_SQRT(pAffineTP->y * pAffineTP->y + sy2) + pAffineTP->y * yinside);
    }
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	SeparationFunc* makeCopy() {
		return new SeparationFunc(*this);
	}

private:
  JWF_FLOAT x;
  JWF_FLOAT xinside;
  JWF_FLOAT y;
  JWF_FLOAT yinside;
};

