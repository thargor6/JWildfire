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

class NPolarFunc: public Variation {
public:
	NPolarFunc() {
	  parity = 0;
	  n = 1;
		initParameterNames(2, "parity", "n");
	}

	const char* getName() const {
		return "npolar";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "parity") == 0) {
			parity = FTOI(pValue);
		}
		else if (strcmp(pName, "n") == 0) {
			n = FTOI(pValue);
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    JWF_FLOAT x = (_isodd != 0) ? pAffineTP->x : _vvar * JWF_ATAN2(pAffineTP->x, pAffineTP->y);
    JWF_FLOAT y = (_isodd != 0) ? pAffineTP->y : _vvar_2 * JWF_LOG(pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y);
    JWF_FLOAT angle = (JWF_ATAN2(y, x) + M_2PI * (pContext->randGen->random(32768) % (int) _absn)) / _nnz;
    JWF_FLOAT r = pAmount * JWF_POW(JWF_SQR(x) + JWF_SQR(y), _cn) * ((_isodd == 0) ? 1.0 : parity);
    JWF_FLOAT sina, cosa;
    JWF_SINCOS(angle, &sina, &cosa);
    cosa *= r;
    sina *= r;
    x = (_isodd != 0) ? cosa : (_vvar_2 * JWF_LOG(cosa * cosa + sina * sina));
    y = (_isodd != 0) ? sina : (_vvar * JWF_ATAN2(cosa, sina));
    pVarTP->x += x;
    pVarTP->y += y;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	NPolarFunc* makeCopy() {
		return new NPolarFunc(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
    _nnz = (n == 0) ? 1 : n;
    _vvar = pAmount / M_PI;
    _vvar_2 = _vvar * 0.5;
    _absn = JWF_FABS(_nnz);
    _cn = 1.0 / _nnz / 2.0;
    _isodd = abs(parity) % 2;
	}

private:
  int parity;
  int n;
  JWF_FLOAT _vvar, _vvar_2;
  int _nnz, _isodd;
  JWF_FLOAT _cn, _absn;
};

