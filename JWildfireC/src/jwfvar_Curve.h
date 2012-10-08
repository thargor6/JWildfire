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

class CurveFunc: public Variation {
public:
	CurveFunc() {
		xamp = 0.25;
		yamp = 0.5;
		xlength = 1.0;
		ylength = 1.0;
		initParameterNames(4, "xamp", "yamp", "xlength", "ylength");
	}

	const char* getName() const {
		return "curve";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "xamp") == 0) {
			xamp = pValue;
		}
		else if (strcmp(pName, "yamp") == 0) {
			yamp = pValue;
		}
		else if (strcmp(pName, "xlength") == 0) {
			xlength = pValue;
		}
		else if (strcmp(pName, "ylength") == 0) {
			ylength = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		pVarTP->x += pAmount * (pAffineTP->x + xamp * JWF_EXP(-pAffineTP->y * pAffineTP->y / _pc_xlen));
		pVarTP->y += pAmount * (pAffineTP->y + yamp * JWF_EXP(-pAffineTP->x * pAffineTP->x / _pc_ylen));
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	CurveFunc* makeCopy() {
		return new CurveFunc(*this);
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		_pc_xlen = xlength * xlength;
		_pc_ylen = ylength * ylength;
		if (_pc_xlen < 1E-20)
			_pc_xlen = 1E-20;
		if (_pc_ylen < 1E-20)
			_pc_ylen = 1E-20;
	}

private:
	JWF_FLOAT xamp, yamp;
	JWF_FLOAT xlength, ylength;
	JWF_FLOAT _pc_xlen, _pc_ylen;
};

