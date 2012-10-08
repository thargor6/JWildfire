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

class MobiusFunc: public Variation {
public:
	MobiusFunc() {
	  re_a = 0.1;
	  re_b = 0.2;
	  re_c = -0.15;
	  re_d = 0.21;
	  im_a = 0.2;
	  im_b = -0.12;
	  im_c = -0.15;
	  im_d = 0.1;
		initParameterNames(8, "re_a", "im_a", "re_b", "im_b", "re_c","im_c","re_d","im_d");
	}

	const char* getName() const {
		return "mobius";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "re_a") == 0) {
			re_a = pValue;
		}
		else if (strcmp(pName, "im_a") == 0) {
			im_a = pValue;
		}
		else if (strcmp(pName, "re_b") == 0) {
			re_b = pValue;
		}
		else if (strcmp(pName, "im_b") == 0) {
			im_b = pValue;
		}
		else if (strcmp(pName, "re_c") == 0) {
			re_c = pValue;
		}
		else if (strcmp(pName, "im_c") == 0) {
			im_c = pValue;
		}
		else if (strcmp(pName, "re_d") == 0) {
			re_d = pValue;
		}
		else if (strcmp(pName, "im_d") == 0) {
			im_d = pValue;
		}
  }

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    JWF_FLOAT re_u = re_a * pAffineTP->x - im_a * pAffineTP->y + re_b;
    JWF_FLOAT im_u = re_a * pAffineTP->y + im_a * pAffineTP->x + im_b;
    JWF_FLOAT re_v = re_c * pAffineTP->x - im_c * pAffineTP->y + re_d;
    JWF_FLOAT im_v = re_c * pAffineTP->y + im_c * pAffineTP->x + im_d;
    JWF_FLOAT d=(re_v * re_v + im_v * im_v);
    if(d==0) {
    	return;
    }
    JWF_FLOAT rad_v = pAmount / d;
    pVarTP->x += rad_v * (re_u * re_v + im_u * im_v);
    pVarTP->y += rad_v * (im_u * re_v - re_u * im_v);
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	MobiusFunc* makeCopy() {
		return new MobiusFunc(*this);
	}

private:
  JWF_FLOAT re_a;
  JWF_FLOAT re_b;
  JWF_FLOAT re_c;
  JWF_FLOAT re_d;
  JWF_FLOAT im_a;
  JWF_FLOAT im_b;
  JWF_FLOAT im_c;
  JWF_FLOAT im_d;
};

