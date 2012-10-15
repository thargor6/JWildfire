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

class PhoenixJuliaFunc: public Variation {
public:
	PhoenixJuliaFunc() {
	  power = 3.0;
	  dist = 1.0;
	  x_distort = -0.5;
	  y_distort = 0.0;
		initParameterNames(4, "power", "dist", "x_distort", "y_distort");
	}

	const char* getName() const {
		return "phoenix_julia";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "power") == 0) {
			power = pValue;
		}
		else if (strcmp(pName, "dist") == 0) {
			dist = pValue;
		}
		else if (strcmp(pName, "x_distort") == 0) {
			x_distort = pValue;
		}
		else if (strcmp(pName, "y_distort") == 0) {
			y_distort = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    // phoenix_julia by TyrantWave, http://tyrantwave.deviantart.com/art/PhoenixJulia-Apophysis-Plugin-121246658
    JWF_FLOAT preX = pAffineTP->x * (x_distort + 1.0);
    JWF_FLOAT preY = pAffineTP->y * (y_distort + 1.0);

    JWF_FLOAT a = JWF_ATAN2(preY, preX) * _invN + pContext->randGen->random(32768) * _inv2PI_N;
    JWF_FLOAT sina, cosa;
    JWF_SINCOS(a, &sina, &cosa);
    JWF_FLOAT r = pAmount * JWF_POW(JWF_SQR(pAffineTP->x) + JWF_SQR(pAffineTP->y), _cN);

    pVarTP->x += r * cosa;
    pVarTP->y += r * sina;

    if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	PhoenixJuliaFunc* makeCopy() {
		return new PhoenixJuliaFunc(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
    _invN = dist / power;
    _inv2PI_N = M_2PI / power;
    _cN = dist / power / 2.0;
	}

private:
  JWF_FLOAT power;
  JWF_FLOAT dist;
  JWF_FLOAT x_distort;
  JWF_FLOAT y_distort;
  JWF_FLOAT _invN, _inv2PI_N, _cN;
};

