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
#ifndef JWFVAR_EJULIA_H_
#define JWFVAR_EJULIA_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class EJuliaFunc: public Variation {
public:
	EJuliaFunc() {
    power = 2;
		initParameterNames(1, "power");
	}

	const char* getName() const {
		return "eJulia";
	}

	void setParameter(char *pName, float pValue) {
		if (strcmp(pName, "power") == 0) {
			power = FTOI(pValue);
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    float r2 = pAffineTP->y * pAffineTP->y + pAffineTP->x * pAffineTP->x;
    float tmp2;
    float x;
    if (_sign == 1)
      x = pAffineTP->x;
    else {
      r2 = 1.0f / r2;
      x = pAffineTP->x * r2;
    }

    float tmp = r2 + 1.0f;
    tmp2 = 2.0f * x;
    float xmax = (sqrtf_safe(tmp + tmp2) + sqrtf_safe(tmp - tmp2)) * 0.5f;
    if (xmax < 1.0f)
      xmax = 1.0f;
    float sinhmu, coshmu, sinnu, cosnu;

    float mu = acoshf(xmax); //  mu > 0
    float t = x / xmax;
    if (t > 1.0f)
      t = 1.0f;
    else if (t < -1.0f)
      t = -1.0f;

    float nu = acosf(t); // -Pi < nu < Pi
    if (pAffineTP->y < 0.0f)
      nu *= -1.0f;

    nu = nu / power + M_2PI / power * floorf(pContext->randGen->random() * power);
    mu /= power;

    sinhmu = sinhf(mu);
    coshmu = coshf(mu);

    sinnu = sinf(nu);
    cosnu = cosf(nu);
    pVarTP->x += pAmount * coshmu * cosnu;
    pVarTP->y += pAmount * sinhmu * sinnu;

		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	EJuliaFunc* makeCopy() {
		return new EJuliaFunc(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, float pAmount) {
    _sign = 1;
    if (power < 0)
      _sign = -1;
	}

private:
	float sqrtf_safe(float x) {
		if (x <= 0.0f)
			return 0.0f;
		return sqrtf(x);
	}

	int power;
	int _sign;
};

#endif // JWFVAR_EJULIA_H_
