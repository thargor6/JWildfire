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

class FarBlurFunc: public Variation {
public:
	FarBlurFunc() {
	  x = 1.0;
	  y = 1.0;
	  z = 1.0;
	  x_origin = 0.0;
	  y_origin = 0.0;
	  z_origin = 0.0;
		initParameterNames(6, "x", "y", "z", "x_origin", "y_origin", "z_origin");
	}

	const char* getName() const {
		return "farblur";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "x") == 0) {
			x = pValue;
		}
		else if (strcmp(pName, "y") == 0) {
			y = pValue;
		}
		else if (strcmp(pName, "z") == 0) {
			z = pValue;
		}
		else if (strcmp(pName, "x_origin") == 0) {
			x_origin = pValue;
		}
		else if (strcmp(pName, "y_origin") == 0) {
			y_origin = pValue;
		}
		else if (strcmp(pName, "z_origin") == 0) {
			z_origin = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    /* farblur by zephyrtronium, http://zephyrtronium.deviantart.com/art/Farblur-Apophysis-Plugin-170718419?q=gallery%3Afractal-resources%2F24660058&qo=10 */
    JWF_FLOAT r = pAmount * (JWF_SQR(pVarTP->x - x_origin) +
        JWF_SQR(pVarTP->y - y_origin) +
        JWF_SQR(pVarTP->z - z_origin)) * (_r[0] + _r[1] + _r[2] + _r[3] - 2.0);
    _r[_n] = pContext->randGen->random();
    _n = (_n + 1) & 3;
    JWF_FLOAT u = pContext->randGen->random() * M_2PI;
    JWF_FLOAT su, cu;
    JWF_SINCOS(u, &su, &cu);
    JWF_FLOAT v = pContext->randGen->random() * M_2PI;
    JWF_FLOAT sv, cv;
    JWF_SINCOS(v, &sv, &cv);

    pVarTP->x += x * r * sv * cu;
    pVarTP->y += y * r * sv * su;
    pVarTP->z += z * r * cv;
	}

	FarBlurFunc* makeCopy() {
		return new FarBlurFunc(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
    _n = 0;
    _r[0] = pContext->randGen->random();
    _r[1] = pContext->randGen->random();
    _r[2] = pContext->randGen->random();
    _r[3] = pContext->randGen->random();
	}

private:
  JWF_FLOAT x;
  JWF_FLOAT y;
  JWF_FLOAT z;
  JWF_FLOAT x_origin;
  JWF_FLOAT y_origin;
  JWF_FLOAT z_origin;

  JWF_FLOAT _r[4];
  int _n;
};

