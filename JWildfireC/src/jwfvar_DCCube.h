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

class DCCubeFunc: public Variation {
public:
	DCCubeFunc() {
		c1 = 0.1;
		c2 = 0.2;
		c3 = 0.3;
		c4 = 0.4;
		c5 = 0.5;
		c6 = 0.6;
		x = 1.0;
		y = 1.0;
		z = 1.0;
		initParameterNames(9, "c1", "c2", "c3", "c4", "c5", "c6", "x", "y", "z");
	}

	const char* getName() const {
		return "dc_cube";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "c1") == 0) {
			c1 = pValue;
		}
		else if (strcmp(pName, "c2") == 0) {
			c2 = pValue;
		}
		else if (strcmp(pName, "c3") == 0) {
			c3 = pValue;
		}
		else if (strcmp(pName, "c4") == 0) {
			c4 = pValue;
		}
		else if (strcmp(pName, "c5") == 0) {
			c5 = pValue;
		}
		else if (strcmp(pName, "c6") == 0) {
			c6 = pValue;
		}
		else if (strcmp(pName, "x") == 0) {
			x = pValue;
		}
		else if (strcmp(pName, "y") == 0) {
			y = pValue;
		}
		else if (strcmp(pName, "z") == 0) {
			z = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT p = 2.0 * pContext->randGen->random() - 1.0;
		JWF_FLOAT q = 2.0 * pContext->randGen->random() - 1.0;
		int i = pContext->randGen->random(32768) % 3;
		boolean j = (pContext->randGen->random(32768) & 1) == 1;
		JWF_FLOAT x = 0.0, y = 0.0, z = 0.0;
		switch (i) {
		case 0:
			x = pAmount * (j ? -1 : 1);
			y = pAmount * p;
			z = pAmount * q;
			if (j)
				pVarTP->color = c1;
			else
				pVarTP->color = c2;
			break;
		case 1:
			x = pAmount * p;
			y = pAmount * (j ? -1 : 1);
			z = pAmount * q;
			if (j)
				pVarTP->color = c3;
			else
				pVarTP->color = c4;
			break;
		case 2:
			x = pAmount * p;
			y = pAmount * q;
			z = pAmount * (j ? -1 : 1);
			if (j)
				pVarTP->color = c5;
			else
				pVarTP->color = c6;
			break;
		}
		pVarTP->x += x * this->x;
		pVarTP->y += y * this->y;
		pVarTP->z += z * this->z;
	}

	DCCubeFunc* makeCopy() {
		return new DCCubeFunc(*this);
	}

private:
	JWF_FLOAT c1, c2, c3, c4, c5, c6;
	JWF_FLOAT x, y, z;
};

