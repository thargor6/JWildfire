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

class PostMirrorWFFunc: public Variation {
public:
	PostMirrorWFFunc() {
		xaxis = 1;
		yaxis = 0;
		zaxis = 0;
		xshift = 0.0;
		yshift = 0.0;
		zshift = 0.0;
		initParameterNames(6, "xaxis", "yaxis", "zaxis", "xshift", "yshift", "zshift");
	}

	const char* getName() const {
		return "post_mirror_wf";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "xaxis") == 0) {
			xaxis = FTOI(pValue);
		}
		else if (strcmp(pName, "yaxis") == 0) {
			yaxis = FTOI(pValue);
		}
		else if (strcmp(pName, "zaxis") == 0) {
			zaxis = FTOI(pValue);
		}
		else if (strcmp(pName, "xshift") == 0) {
			xshift = pValue;
		}
		else if (strcmp(pName, "yshift") == 0) {
			yshift = pValue;
		}
		else if (strcmp(pName, "zshift") == 0) {
			zshift = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		if (xaxis > 0 && pContext->randGen->random() < 0.5) {
			pVarTP->x = -pVarTP->x - xshift;
		}

		if (yaxis > 0 && pContext->randGen->random() < 0.5) {
			pVarTP->y = -pVarTP->y - yshift;
		}

		if (zaxis > 0 && pContext->randGen->random() < 0.5) {
			pVarTP->z = -pVarTP->z - zshift;
		}
	}

	PostMirrorWFFunc* makeCopy() {
		return new PostMirrorWFFunc(*this);
	}

	int const getPriority() {
		return 1;
	}

private:
	int xaxis, yaxis, zaxis;
	JWF_FLOAT xshift, yshift, zshift;
};

