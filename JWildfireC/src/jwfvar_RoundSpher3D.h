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


#include "jwf_Variation.h"

class RoundSpher3DFunc: public Variation {
public:
	RoundSpher3DFunc() {
	}

	const char* getName() const {
		return "roundspher3D";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    /* roundspher3D by Larry Berlin, http://aporev.deviantart.com/art/3D-Plugins-Collection-One-138514007?q=gallery%3Aaporev%2F8229210&qo=15 */
    JWF_FLOAT inZ = pAffineTP->z;
    JWF_FLOAT otherZ = pVarTP->z;
    JWF_FLOAT f = JWF_SQRT(JWF_SQR(pAffineTP->x) + JWF_SQR(pAffineTP->y));
    JWF_FLOAT tempTZ, tempPZ;
    if (inZ == 0.0) {
      tempTZ = JWF_COS(f);
    }
    else {
      tempTZ = pAffineTP->z;
    }
    if (otherZ == 0.0) {
      tempPZ = JWF_COS(f);
    }
    else {
      tempPZ = pVarTP->z;
    }
    JWF_FLOAT d = JWF_SQR(pAffineTP->x) + JWF_SQR(pAffineTP->y) + JWF_SQR(tempTZ);
    JWF_FLOAT e = 1.0 / d + JWF_SQR(M_2_PI);

    pVarTP->x += pAmount * (pAmount / d * pAffineTP->x / e);
    pVarTP->y += pAmount * (pAmount / d * pAffineTP->y / e);
    pVarTP->z = tempPZ + pAmount * (pAmount / d * tempTZ / e);
	}

	RoundSpher3DFunc* makeCopy() {
		return new RoundSpher3DFunc(*this);
	}

};

