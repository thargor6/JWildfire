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

class PieFunc: public Variation {
public:
	PieFunc() {
		slices = 6.0;
		rotation = 0.0;
		thickness = 0.5;
		initParameterNames(3, "slices", "rotation", "thickness");
	}

	const char* getName() const {
		return "pie";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "slices") == 0) {
			slices = pValue;
		}
		else if (strcmp(pName, "rotation") == 0) {
			rotation = pValue;
		}
		else if (strcmp(pName, "thickness") == 0) {
			thickness = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		int sl = (int) (pContext->randGen->random() * slices + 0.5);
		JWF_FLOAT a = rotation + 2.0 * M_PI * (sl + pContext->randGen->random() * thickness) / slices;
		JWF_FLOAT r = pAmount * pContext->randGen->random();
		double sina, cosa;
		JWF_SINCOS(a, &sina, &cosa);
		pVarTP->x += r * cosa;
		pVarTP->y += r * sina;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	PieFunc* makeCopy() {
		return new PieFunc(*this);
	}

private:
	JWF_FLOAT slices;
	JWF_FLOAT rotation;
	JWF_FLOAT thickness;
};

