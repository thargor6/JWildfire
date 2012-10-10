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

class SuperShapeFunc: public Variation {
public:
	SuperShapeFunc() {
	  rnd = 3.0;
	  m = 1.0;
	  n1 = 1.0;
	  n2 = 1.0;
	  n3 = 1.0;
	  holes = 0.0;
		initParameterNames(6, "rnd", "m", "n1", "n2", "n3", "holes");
	}

	const char* getName() const {
		return "super_shape";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "rnd") == 0) {
			rnd = pValue;
		}
		else if (strcmp(pName, "m") == 0) {
			m = pValue;
		}
		else if (strcmp(pName, "n1") == 0) {
			n1 = pValue;
		}
		else if (strcmp(pName, "n2") == 0) {
			n2 = pValue;
		}
		else if (strcmp(pName, "n3") == 0) {
			n3 = pValue;
		}
		else if (strcmp(pName, "holes") == 0) {
			holes = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    JWF_FLOAT pm_4 = m / 4.0;
    JWF_FLOAT pneg1_n1 = -1.0 / n1;

    JWF_FLOAT theta = pm_4 * pAffineTP->getPrecalcAtanYX() + M_PI_4;

    JWF_FLOAT st = JWF_SIN(theta);
    JWF_FLOAT ct = JWF_COS(theta);

    JWF_FLOAT t1 = JWF_FABS(ct);
    t1 = JWF_POW(t1, n2);

    JWF_FLOAT t2 = JWF_FABS(st);
    t2 = JWF_POW(t2, n3);

    JWF_FLOAT myrnd = rnd;

    JWF_FLOAT r = pAmount * ((myrnd * pContext->randGen->random() + (1.0 - myrnd) * pAffineTP->getPrecalcSqrt()) - holes)
        * JWF_POW(t1 + t2, pneg1_n1) / pAffineTP->getPrecalcSqrt();

    pVarTP->x += r * pAffineTP->x;
    pVarTP->y += r * pAffineTP->y;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	SuperShapeFunc* makeCopy() {
		return new SuperShapeFunc(*this);
	}

private:
  JWF_FLOAT rnd;
  JWF_FLOAT m;
  JWF_FLOAT n1;
  JWF_FLOAT n2;
  JWF_FLOAT n3;
  JWF_FLOAT holes;
};

