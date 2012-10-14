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

class GlynnSim3Func: public Variation {
public:
	GlynnSim3Func() {
    radius = 1.0;
    thickness = 0.1;
    thickness2 = 0.1;
    contrast = 0.5;
    _pow = 1.5;
		initParameterNames(5, "radius", "thickness", "thickness2", "contrast", "pow");
		toolPoint=(Point*)calloc(1, sizeof(Point));
	}

	~GlynnSim3Func() {
		free(toolPoint);
	}

	const char* getName() const {
		return "glynnSim3";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "radius") == 0) {
			radius = pValue;
		}
		else if (strcmp(pName, "thickness") == 0) {
			thickness = pValue;
		}
		else if (strcmp(pName, "thickness2") == 0) {
			thickness2 = pValue;
		}
		else if (strcmp(pName, "contrast") == 0) {
			contrast = pValue;
		}
		else if (strcmp(pName, "pow") == 0) {
			_pow = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    /* GlynnSim3 by eralex61, http://eralex61.deviantart.com/art/GlynnSim-plugin-112621621 */
    JWF_FLOAT r = JWF_SQRT(pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y);
    JWF_FLOAT alpha = radius / r;
    if (r < _radius1) {
      circle2(pContext, toolPoint);
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

	GlynnSim3Func* makeCopy() {
		return new GlynnSim3Func(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
    _radius1 = radius + thickness;
    _radius2 = JWF_SQR(radius) / _radius1;
    _gamma = _radius1 / (_radius1 + _radius2);
    _absPow = JWF_FABS(_pow);
	}

private:
  JWF_FLOAT radius;
  JWF_FLOAT thickness;
  JWF_FLOAT thickness2;
  JWF_FLOAT contrast;
  JWF_FLOAT _pow;

  struct Point {
  	JWF_FLOAT x, y;
  };

  Point *toolPoint;

  JWF_FLOAT _radius1, _radius2, _gamma, _absPow;


  void circle2(FlameTransformationContext *pContext, Point *p) {
    //    JWF_FLOAT r = radius + thickness - Gamma * pContext.random();
    JWF_FLOAT phi = 2.0 * M_PI * pContext->randGen->random();
    JWF_FLOAT sinPhi, cosPhi;
    JWF_SINCOS(phi, &sinPhi, &cosPhi);
    JWF_FLOAT r;
    if (pContext->randGen->random() < _gamma) {
      r = _radius1;
    }
    else {
      r = _radius2;
    }
    p->x = r * cosPhi;
    p->y = r * sinPhi;
  }

};

