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
#ifndef JWFVAR_WAVES3_WF_H_
#define JWFVAR_WAVES3_WF_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class Waves3WFFunc: public Variation {
public:
	Waves3WFFunc() {
		scalex = 0.25f;
		scaley = 0.5f;
		freqx = M_PI / 2.0f;
		freqy = M_PI / 4.0f;
		use_cos_x = true;
		use_cos_y = false;
		dampx = 0.0f;
		dampy = 0.0f;
		initParameterNames(8, "scalex", "scaley", "freqx", "freqy", "use_cos_x", "uses_cos_y", "dampx", "danmpy");
	}

	const char* getName() const {
		return "waves3_wf";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "scalex") == 0) {
			scalex = pValue;
		}
		else if (strcmp(pName, "scaley") == 0) {
			scaley = pValue;
		}
		else if (strcmp(pName, "freqx") == 0) {
			freqx = pValue;
		}
		else if (strcmp(pName, "freqy") == 0) {
			freqy = pValue;
		}
		else if (strcmp(pName, "use_cos_x") == 0) {
			use_cos_x = FTOI(pValue) == 1;
		}
		else if (strcmp(pName, "use_cos_y") == 0) {
			use_cos_y = FTOI(pValue) == 1;
		}
		else if (strcmp(pName, "dampx") == 0) {
			dampx = pValue;
		}
		else if (strcmp(pName, "dampy") == 0) {
			dampy = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		if (use_cos_x == 1) {
			pVarTP->x += pAmount * (pAffineTP->x + _dampingX * scalex * JWF_COS(pAffineTP->y * freqx) * JWF_COS(pAffineTP->y * freqx)) * _dampingX;
		}
		else {
			pVarTP->x += pAmount * (pAffineTP->x + _dampingX * scalex * JWF_SIN(pAffineTP->y * freqx) * JWF_SIN(pAffineTP->y * freqx)) * _dampingX;
		}
		if (use_cos_y == 1) {
			pVarTP->y += pAmount * (pAffineTP->y + _dampingY * scaley * JWF_COS(pAffineTP->x * freqy) * JWF_COS(pAffineTP->x * freqy)) * _dampingY;
		}
		else {
			pVarTP->y += pAmount * (pAffineTP->y + _dampingY * scaley * JWF_SIN(pAffineTP->x * freqy) * JWF_SIN(pAffineTP->x * freqy)) * _dampingY;
		}
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		_dampingX = JWF_FABS(dampx) < EPSILON ? 1.0f : JWF_EXP(dampx);
		_dampingY = JWF_FABS(dampy) < EPSILON ? 1.0f : JWF_EXP(dampy);
	}

	Waves3WFFunc* makeCopy() {
		return new Waves3WFFunc(*this);
	}

private:
	float scalex;
	float scaley;
	float freqx;
	float freqy;
	bool use_cos_x;
	bool use_cos_y;
	float dampx;
	float dampy;
	float _dampingX, _dampingY;
};

#endif // JWFVAR_WAVES3_WF_H_
