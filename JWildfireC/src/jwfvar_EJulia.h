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

class EJuliaFunc: public Variation {
public:
	EJuliaFunc() {
		power = 2;
		initParameterNames(1, "power");
	}

	const char* getName() const {
		return "eJulia";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "power") == 0) {
			power = FTOI(pValue);
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT r2 = pAffineTP->y * pAffineTP->y + pAffineTP->x * pAffineTP->x;
		JWF_FLOAT tmp2;
		JWF_FLOAT x;
		if (_sign == 1)
			x = pAffineTP->x;
		else {
			r2 = 1.0 / r2;
			x = pAffineTP->x * r2;
		}

		JWF_FLOAT tmp = r2 + 1.0;
		tmp2 = 2.0 * x;
		JWF_FLOAT xmax = (sqrtf_safe(tmp + tmp2) + sqrtf_safe(tmp - tmp2)) * 0.5;
		if (xmax < 1.0)
			xmax = 1.0;
		JWF_FLOAT sinhmu, coshmu, sinnu, cosnu;

		JWF_FLOAT mu = JWF_ACOSH(xmax); //  mu > 0
		JWF_FLOAT t = x / xmax;
		if (t > 1.0)
			t = 1.0;
		else if (t < -1.0)
			t = -1.0;

		JWF_FLOAT nu = JWF_ACOS(t); // -Pi < nu < Pi
		if (pAffineTP->y < 0.0)
			nu *= -1.0;

		nu = nu / power + M_2PI / power * JWF_FLOOR((JWF_FLOAT)(pContext->randGen->random() * power));
		mu /= power;

		sinhmu = JWF_SINH(mu);
		coshmu = JWF_COSH(mu);

		sinnu = JWF_SIN(nu);
		cosnu = JWF_COS(nu);
		pVarTP->x += pAmount * coshmu * cosnu;
		pVarTP->y += pAmount * sinhmu * sinnu;

		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	EJuliaFunc* makeCopy() {
		return new EJuliaFunc(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		_sign = 1;
		if (power < 0)
			_sign = -1;
	}

private:
	JWF_FLOAT sqrtf_safe(JWF_FLOAT x) {
		if (x <= 0.0)
			return 0.0;
		return JWF_SQRT(x);
	}

	int power;
	int _sign;
};

