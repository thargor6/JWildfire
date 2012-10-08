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

class PostColorScaleWFFunc: public Variation {
public:
	PostColorScaleWFFunc() {
		scale_x = 0.0;
		scale_y = 0.0;
		scale_z = 0.5;
		offset_z = 0.0;
		reset_z = 0.0;
		initParameterNames(5, "scale_x", "scale_y", "scale_z", "offset_z", "reset_z");
	}

	const char* getName() const {
		return "post_colorscale_wf";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "scale_x") == 0) {
			scale_x = pValue;
		}
		else if (strcmp(pName, "scale_y") == 0) {
			scale_y = pValue;
		}
		else if (strcmp(pName, "scale_z") == 0) {
			scale_z = pValue;
		}
		else if (strcmp(pName, "offset_z") == 0) {
			offset_z = pValue;
		}
		else if (strcmp(pName, "reset_z") == 0) {
			reset_z = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    pVarTP->x += pAmount * scale_x * pVarTP->x;
    pVarTP->y += pAmount * scale_y * pVarTP->y;
    JWF_FLOAT dz = pVarTP->color * scale_z * pAmount + offset_z;
    if (reset_z > 0) {
      pVarTP->z = dz;
    }
    else {
      pVarTP->z += dz;
    }
	}

	int const getPriority() {
		return 1;
	}

	PostColorScaleWFFunc* makeCopy() {
		return new PostColorScaleWFFunc(*this);
	}

private:
	JWF_FLOAT scale_x;
	JWF_FLOAT scale_y;
	JWF_FLOAT scale_z;
	JWF_FLOAT offset_z;
	JWF_FLOAT reset_z;
};

