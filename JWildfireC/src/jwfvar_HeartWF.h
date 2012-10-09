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

class HeartWFFunc: public Variation {
public:
	HeartWFFunc() {
    scale_x = 1.0;
    scale_t = 1.0;
    shift_t = 0.0;
    scale_t_left = 1.0;
    scale_t_right = 1.0;
		initParameterNames(5, "scale_x", "scale_t", "shift_t", "scale_t_left", "scale_t_right");
	}

	const char* getName() const {
		return "heart_wf";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "scale_x") == 0) {
			scale_x = pValue;
		}
		else if (strcmp(pName, "scale_t") == 0) {
			scale_t = pValue;
		}
		else if (strcmp(pName, "shift_t") == 0) {
			shift_t = pValue;
		}
		else if (strcmp(pName, "scale_t_left") == 0) {
			scale_t_left = pValue;
		}
		else if (strcmp(pName, "scale_t_right") == 0) {
			scale_t_right = pValue;
		}
  }

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    JWF_FLOAT a = JWF_ATAN2(pAffineTP->x, pAffineTP->y);
    JWF_FLOAT r = JWF_SQRT(pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y);
    JWF_FLOAT nx, t;
    if (a < 0) {
      t = -a / M_PI * T_MAX * scale_t_left - shift_t;
      if (t > T_MAX) {
        t = T_MAX;
      }
      nx = -0.001 * (-t * t + 40 * t + 1200) * JWF_SIN(M_PI * t / 180.0) * r;
    }
    else {
      t = a / M_PI * T_MAX * scale_t_right - shift_t;
      if (t > T_MAX) {
        t = T_MAX;
      }
      nx = 0.001 * (-t * t + 40 * t + 1200) * JWF_SIN(M_PI * t / 180.0) * r;
    }
    JWF_FLOAT ny = -0.001 * (-t * t + 40 * t + 400) * JWF_COS(M_PI * t / 180.0) * r;
    nx *= scale_x;
    pVarTP->x += pAmount * nx;
    pVarTP->y += pAmount * ny;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	HeartWFFunc* makeCopy() {
		return new HeartWFFunc(*this);
	}

private:
	static const JWF_FLOAT T_MAX = 60.0;

  JWF_FLOAT scale_x;
  JWF_FLOAT scale_t;
  JWF_FLOAT shift_t;
  JWF_FLOAT scale_t_left;
  JWF_FLOAT scale_t_right;
};

