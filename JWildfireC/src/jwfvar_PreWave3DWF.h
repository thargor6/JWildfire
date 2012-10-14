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

class PreWave3DWFFunc: public Variation {
public:
	PreWave3DWFFunc() {
	  wavelen = 0.5;
	  phase = 0.0;
	  damping = 0.01;
		initParameterNames(3, "wavelen", "phase", "damping");
	}

	const char* getName() const {
		return "pre_wave3D_wf";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "wavelen") == 0) {
			wavelen = pValue;
		}
		else if (strcmp(pName, "phase") == 0) {
			phase = pValue;
		}
		else if (strcmp(pName, "damping") == 0) {
			damping = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    JWF_FLOAT r = JWF_SQRT(pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y);
    JWF_FLOAT dl = r / wavelen;
    JWF_FLOAT amplitude = pAmount;
    if (JWF_FABS(damping) > EPSILON) {
      JWF_FLOAT dmp = -dl * damping;
      amplitude *= JWF_EXP(dmp);
    }
    pAffineTP->z += amplitude * JWF_SIN(2.0 * M_PI * dl + phase);
	}

	int const getPriority() {
		return -1;
	}

	PreWave3DWFFunc* makeCopy() {
		return new PreWave3DWFFunc(*this);
	}

private:
  JWF_FLOAT wavelen;
  JWF_FLOAT phase;
  JWF_FLOAT damping;
};

