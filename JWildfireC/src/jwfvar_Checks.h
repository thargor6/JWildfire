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

class ChecksFunc: public Variation {
public:
	ChecksFunc() {
	  x = 5.0;
	  y = 5.0;
	  size = 5.0;
	  rnd = 0.0;
		initParameterNames(4, "x", "y", "size", "rnd");
	}

	const char* getName() const {
		return "checks";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "x") == 0) {
			x = pValue;
		}
		else if (strcmp(pName, "y") == 0) {
			y = pValue;
		}
		else if (strcmp(pName, "size") == 0) {
			size = pValue;
		}
		else if (strcmp(pName, "rnd") == 0) {
			rnd = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    // Fixed checks plugin by Keeps and Xyrus02, http://xyrus02.deviantart.com/art/Checks-The-fixed-version-138967784?q=favby%3Aapophysis-plugins%2F39181234&qo=3
    JWF_FLOAT x = pAffineTP->x * _cs;
    JWF_FLOAT y = pAffineTP->y * _cs;

    int isXY = (int) JWF_RINT(pAffineTP->x * _cs) + (int) JWF_RINT(pAffineTP->y * _cs);

    // -X- This is just for code readability,
    //     if there is any impact on performance, its minimal :-)
    JWF_FLOAT rnx = rnd * pContext->randGen->random();
    JWF_FLOAT rny = rnd * pContext->randGen->random();
    JWF_FLOAT dx, dy;
    if (isXY % 2 == 0) {
      // -X- The -VAR(checks_#) stuff caused the error!
      dx = _ncx + rnx;
      dy = _ncy;
    }
    else {
      dx = x;
      dy = y + rny;
    }

    pVarTP->x += pAmount * (pAffineTP->x + dx);
    pVarTP->y += pAmount * (pAffineTP->y + dy);
    // -X- and as a little goodie, I pass through FTz so that
    //     neat lil variation does not kill 3Dness in hack & 7X
    pVarTP->z += pAmount * pAffineTP->z;
	}

	ChecksFunc* makeCopy() {
		return new ChecksFunc(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
    // Multiplication is faster than division, so divide in precalc, multiply in calc.
    _cs = 1.0 / (size + EPSILON);
    // -X- Then precalculate -checkx_x, -checks_y
    _ncx = x * -1.0;
    _ncy = y * -1.0;
  }

private:
  JWF_FLOAT x;
  JWF_FLOAT y;
  JWF_FLOAT size;
  JWF_FLOAT rnd;

  JWF_FLOAT _cs, _ncx, _ncy;
};

