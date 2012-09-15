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
#ifndef JWFVAR_POST_MIRROR_WF_H_
#define JWFVAR_POST_MIRROR_WF_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class PostMirrorWFFunc: public Variation {
public:
	PostMirrorWFFunc() {
		xAxis = 1;
		yAxis = 0;
		zAxis = 0;
		xShift = 0.0;
		yShift = 0.0;
		zShift = 0.0;
		initParameterNames(6, "xAxis", "yAxis", "zAxis", "xShift", "yShift", "zShift");
	}

	const char* getName() const {
		return "post_mirror_wf";
	}

	void setParameter(char *pName, float pValue) {
		if (strcmp(pName, "xAxis") == 0) {
			xAxis = FTOI(pValue);
		}
		else if (strcmp(pName, "yAxis") == 0) {
			yAxis = FTOI(pValue);
		}
		else if (strcmp(pName, "zAxis") == 0) {
			zAxis = FTOI(pValue);
		}
		else if (strcmp(pName, "xShift") == 0) {
			xShift = pValue;
		}
		else if (strcmp(pName, "yShift") == 0) {
			yShift = pValue;
		}
		else if (strcmp(pName, "zShift") == 0) {
			zShift = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    if (xAxis > 0 && pContext->randGen->random() < 0.5f) {
      pVarTP->x = -pVarTP->x - xShift;
    }

    if (yAxis > 0 && pContext->randGen->random() < 0.5f) {
      pVarTP->y = -pVarTP->y - yShift;
    }

    if (zAxis > 0 && pContext->randGen->random() < 0.5) {
      pVarTP->z = -pVarTP->z - zShift;
    }
	}

	PostMirrorWFFunc* makeCopy() {
		return new PostMirrorWFFunc(*this);
	}

	int const getPriority() {
		return 1;
	}

private:
	int xAxis, yAxis, zAxis;
	float xShift, yShift, zShift;
};

#endif // JWFVAR_POST_MIRROR_WF_H_
