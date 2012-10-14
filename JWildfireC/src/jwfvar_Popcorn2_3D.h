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

class Popcorn2_3DFunc: public Variation {
public:
	Popcorn2_3DFunc() {
    x = 0.1;
    y = 0.1;
    z = 0.1;
    c = 3.0;
		initParameterNames(4, "x", "y", "z", "c");
	}

	const char* getName() const {
		return "popcorn2_3D";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "x") == 0) {
			x = pValue;
		}
		else if (strcmp(pName, "y") == 0) {
			y = pValue;
		}
		else if (strcmp(pName, "z") == 0) {
			z = pValue;
		}
		else if (strcmp(pName, "c") == 0) {
			c = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    /* popcorn2_3D by Larry Berlin, http://aporev.deviantart.com/art/3D-Plugins-Collection-One-138514007?q=gallery%3Aaporev%2F8229210&qo=15 */
    JWF_FLOAT inZ, otherZ, tempTZ, tempPZ, tmpVV;
    inZ = pAffineTP->z;
    otherZ = pVarTP->z;

    if (JWF_FABS(pAmount) <= 1.0) {
      tmpVV = JWF_FABS(pAmount) * pAmount; //sqr(pAmount) value retaining sign
    }
    else {
      tmpVV = pAmount;
    }
    if (otherZ == 0.0) {
      tempPZ = tmpVV * JWF_SIN(JWF_TAN(c)) * JWF_ATAN2(pAffineTP->y, pAffineTP->x);
    }
    else {
      tempPZ = pVarTP->z;
    }
    if (inZ == 0.0) {
      tempTZ = tmpVV * JWF_SIN(JWF_TAN(c)) * JWF_ATAN2(pAffineTP->y, pAffineTP->x);
    }
    else {
      tempTZ = pAffineTP->z;
    }

    pVarTP->x += pAmount * 0.5 * (pAffineTP->x + x * JWF_SIN(JWF_TAN(c * pAffineTP->y)));
    pVarTP->y += pAmount * 0.5 * (pAffineTP->y + y * JWF_SIN(JWF_TAN(c * pAffineTP->x)));
    pVarTP->z = tempPZ + tmpVV * (z * JWF_SIN(JWF_TAN(c)) * tempTZ);
	}

	Popcorn2_3DFunc* makeCopy() {
		return new Popcorn2_3DFunc(*this);
	}

private:
	JWF_FLOAT x;
	JWF_FLOAT y;
	JWF_FLOAT z;
	JWF_FLOAT c;
};

