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

class LogApoFunc: public Variation {
public:
	LogApoFunc() {
		base = 2.71828182845905;
		initParameterNames(1, "base");
	}

	const char* getName() const {
		return "log_apo";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "base") == 0) {
			base = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		pVarTP->x += pAmount * JWF_LOG(pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y) * _denom;
		pVarTP->y += pAmount * JWF_ATAN2(pAffineTP->y, pAffineTP->x);
		pVarTP->z += pAmount * pAffineTP->z;
	}

	LogApoFunc* makeCopy() {
		return new LogApoFunc(*this);
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		_denom = 0.5 / JWF_LOG(base);
	}

private:
	JWF_FLOAT base;
	JWF_FLOAT _denom;
};

