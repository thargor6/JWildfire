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

class SphericalNFunc: public Variation {
public:
	SphericalNFunc() {
	  power = 3.0;
	  dist = 1.0;
		initParameterNames(2, "power", "dist");
	}

	const char* getName() const {
		return "sphericalN";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "power") == 0) {
			power = pValue;
		}
		else if (strcmp(pName, "dist") == 0) {
			dist = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    JWF_FLOAT R = JWF_POW(JWF_SQRT(JWF_SQR(pAffineTP->x) + JWF_SQR(pAffineTP->y)), dist);
    int N = (int) JWF_FLOOR(power * pContext->randGen->random());
    JWF_FLOAT alpha = JWF_ATAN2(pAffineTP->y, pAffineTP->x) + N * M_2PI / JWF_FLOOR(power);
    JWF_FLOAT sina = JWF_SIN(alpha);
    JWF_FLOAT cosa = JWF_COS(alpha);

    if (R > EPSILON) {
      pVarTP->x += pAmount * cosa / R;
      pVarTP->y += pAmount * sina / R;
    }
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	SphericalNFunc* makeCopy() {
		return new SphericalNFunc(*this);
	}

private:
  JWF_FLOAT power;
  JWF_FLOAT dist;
};

