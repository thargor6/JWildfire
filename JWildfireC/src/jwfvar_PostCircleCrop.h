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

class PostCircleCropFunc: public Variation {
public:
	PostCircleCropFunc() {
		radius = 1.0;
		x = 0.0;
		y = 0.0;
		scatter_area = 0.0;
		zero = true;
		initParameterNames(5, "radius", "x", "y", "scatter_area", "zero");
	}

	const char* getName() const {
		return "post_circlecrop";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "radius") == 0) {
			radius = pValue;
		}
		else if (strcmp(pName, "x") == 0) {
			x = pValue;
		}
		else if (strcmp(pName, "y") == 0) {
			y = pValue;
		}
		else if (strcmp(pName, "scatter_area") == 0) {
			scatter_area = pValue;
		}
		else if (strcmp(pName, "zero") == 0) {
			zero = FTOI(pValue) == 1;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    JWF_FLOAT x0 = x;
    JWF_FLOAT y0 = y;
    JWF_FLOAT cr = radius;
    JWF_FLOAT ca = _cA;
    JWF_FLOAT vv = pAmount;

    pVarTP->x -= x0;
    pVarTP->y -= y0;

    pVarTP->z += vv * pVarTP->z;

    JWF_FLOAT rad = JWF_SQRT(pVarTP->x * pVarTP->x + pVarTP->y * pVarTP->y);
    JWF_FLOAT ang = JWF_ATAN2(pVarTP->y, pVarTP->x);
    JWF_FLOAT rdc = cr + (pContext->randGen->random() * 0.5 * ca);

    boolean esc = rad > cr;
    boolean cr0 = zero == 1;

    JWF_FLOAT s = JWF_SIN(ang);
    JWF_FLOAT c = JWF_COS(ang);

    if (cr0 && esc) {
      pVarTP->x = pVarTP->y = 0;
    }
    else if (cr0 && !esc) {
      pVarTP->x += vv * pVarTP->x + x0;
      pVarTP->y += vv * pVarTP->y + y0;
    }
    else if (!cr0 && esc) {
      pVarTP->x += vv * rdc * c + x0;
      pVarTP->y += vv * rdc * s + y0;
    }
    else if (!cr0 && !esc) {
      pVarTP->x += vv * pVarTP->x + x0;
      pVarTP->y += vv * pVarTP->y + y0;
    }
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		_cA = JWF_MAX(-1.0, JWF_MIN(scatter_area, 1.0));
	}

	int const getPriority() {
		return 1;
	}

	PostCircleCropFunc* makeCopy() {
		return new PostCircleCropFunc(*this);
	}

private:
	JWF_FLOAT radius;
	JWF_FLOAT x;
	JWF_FLOAT y;
	JWF_FLOAT scatter_area;
	bool zero;
	JWF_FLOAT _cA;
};

