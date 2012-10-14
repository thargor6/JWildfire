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

class KaleidoscopeFunc: public Variation {
public:
	KaleidoscopeFunc() {
	  pull = 0.0;
	  rotate = 1.0;
	  line_up = 1.0;
	  x = 0.0;
	  y = 0.0;
		initParameterNames(5, "pull", "rotate", "line_up", "x", "y");
	}

	const char* getName() const {
		return "kaleidoscope";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "pull") == 0) {
			pull = pValue;
		}
		else if (strcmp(pName, "rotate") == 0) {
			rotate = pValue;
		}
		else if (strcmp(pName, "line_up") == 0) {
			line_up = pValue;
		}
		else if (strcmp(pName, "x") == 0) {
			x = pValue;
		}
		else if (strcmp(pName, "y") == 0) {
			y = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    /* Kaleidoscope by Will Evans, http://eevans1.deviantart.com/art/kaleidoscope-plugin-122185469  */
    pVarTP->x += ((_w * pAffineTP->x) * JWF_COS(45.0) - pAffineTP->y * JWF_SIN(45.0) + _e) + _r;
    // the if function splits the plugin in two.
    if (pAffineTP->y > 0) {
      pVarTP->y += ((_w * pAffineTP->y) * JWF_COS(45.0) + pAffineTP->x * JWF_SIN(45.0) + _q + _e) + _t;
    }
    else {
      pVarTP->y += (_w * pAffineTP->y) * JWF_COS(45.0) + pAffineTP->x * JWF_SIN(45.0) - _q - _e;
    }
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	KaleidoscopeFunc* makeCopy() {
		return new KaleidoscopeFunc(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
    _q = pull; // pulls apart the 2 sections of the plugin
    _w = rotate; // rotates both halves of the plugin
    _e = line_up;
    _r = x; // changes x co-ordinates
    _t = y; // changes y co-ordinates for 1 part of the plugin
	}

private:
  JWF_FLOAT pull;
  JWF_FLOAT rotate;
  JWF_FLOAT line_up;
  JWF_FLOAT x;
  JWF_FLOAT y;

  JWF_FLOAT _q, _w, _e, _r, _t, _i;
};

