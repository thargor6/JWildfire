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
#ifndef JWFVAR_PIE3D_H_
#define JWFVAR_PIE3D_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class Pie3DFunc: public Variation {
public:
	Pie3DFunc() {
		slices = 6.0f;
		rotation = 0.0f;
		thickness = 0.5f;
		initParameterNames(3, "slices", "rotation", "thickness");
	}

	const char* getName() const {
		return "pie3D";
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
		int sl = (int) (pContext->randGen->random() * slices + 0.5f);
		float a = rotation + 2.0 * M_PI * (sl + pContext->randGen->random() * thickness) / slices;
		float r = pAmount * pContext->randGen->random();
		double sina = JWF_SIN(a);
		double cosa = JWF_COS(a);
		pVarTP->x += r * cosa;
		pVarTP->y += r * sina;
		pVarTP->z += r * JWF_SIN(r);
	}

	Pie3DFunc* makeCopy() {
		return new Pie3DFunc(*this);
	}

private:
	float slices;
	float rotation;
	float thickness;
};

#endif // JWFVAR_PIE3D_H_
