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

#include <stdlib.h>
#include "jwf_Constants.h"
#include "jwf_Variation.h"

class GlynnSim2Func: public Variation {
public:
	GlynnSim2Func() {
    radius = 1.0;
    thickness = 0.1;
    contrast = 0.5;
    _pow = 1.5;
    phi1 = 110.0;
    phi2 = 150.0;
		initParameterNames(6, "radius", "thickness", "contrast", "pow", "phi1", "phi2");
		toolPoint=(Point*)calloc(1, sizeof(Point));
	}

	~GlynnSim2Func() {
		free(toolPoint);
	}

	const char* getName() const {
		return "glynnSim2";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "radius") == 0) {
			radius = pValue;
		}
		else if (strcmp(pName, "thickness") == 0) {
			thickness = pValue;
		}
		else if (strcmp(pName, "contrast") == 0) {
			contrast = pValue;
		}
		else if (strcmp(pName, "pow") == 0) {
			_pow = pValue;
		}
		else if (strcmp(pName, "phi1") == 0) {
			phi1 = pValue;
		}
		else if (strcmp(pName, "phi2") == 0) {
			phi2 = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    /* GlynnSim2 by eralex61, http://eralex61.deviantart.com/art/GlynnSim-plugin-112621621 */
    JWF_FLOAT r = JWF_SQRT(pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y);
    JWF_FLOAT alpha = radius / r;
    if (r < radius) {
      circle(pContext, toolPoint);
      pVarTP->x += pAmount * toolPoint->x;
      pVarTP->y += pAmount * toolPoint->y;
    }
    else {
      if (pContext->randGen->random() > contrast * JWF_POW(alpha, _absPow)) {
        pVarTP->x += pAmount * pAffineTP->x;
        pVarTP->y += pAmount * pAffineTP->y;
      }
      else {
        pVarTP->x += pAmount * alpha * alpha * pAffineTP->x;
        pVarTP->y += pAmount * alpha * alpha * pAffineTP->y;
      }
    }
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	GlynnSim2Func* makeCopy() {
		return new GlynnSim2Func(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
    _phi10 = M_PI * phi1 / 180.0;
    _phi20 = M_PI * phi2 / 180.0;
    _gamma = thickness * (2.0 * radius + thickness) / (radius + thickness);
    _delta = _phi20 - _phi10;
    _absPow = JWF_FABS(_pow);
	}

private:
  JWF_FLOAT radius;
  JWF_FLOAT thickness;
  JWF_FLOAT contrast;
  JWF_FLOAT _pow;
  JWF_FLOAT phi1;
  JWF_FLOAT phi2;

  struct Point {
  	JWF_FLOAT x, y;
  };

  Point *toolPoint;

  JWF_FLOAT _phi10, _phi20, _gamma, _delta, _absPow;

  void circle(FlameTransformationContext *pContext, Point *p) {
    JWF_FLOAT r = radius + thickness - _gamma * pContext->randGen->random();
    JWF_FLOAT phi = _phi10 + _delta * pContext->randGen->random();
    JWF_FLOAT sinPhi, cosPhi;
    JWF_SINCOS(phi, &sinPhi, &cosPhi);
    p->x = r * cosPhi;
    p->y = r * sinPhi;
  }

};

