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

class MandelbrotFunc: public Variation {
public:
	MandelbrotFunc() {
	  iter = 100;
	  xmin = -1.6;
	  xmax = 1.6;
	  ymin = -1.2;
	  ymax = 1.2;
	  invert = 0;
	  skin = 0;
	  cx = 0.0;
	  cy = 0.0;
		initParameterNames(9, "iter", "xmin", "xmax", "ymin", "ymax", "invert", "skin", "cx", "cy");
	}

	const char* getName() const {
		return "mandelbrot";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "iter") == 0) {
			iter = FTOI(pValue);
		}
		else if (strcmp(pName, "xmin") == 0) {
			xmin = pValue;
		}
		else if (strcmp(pName, "xmax") == 0) {
			xmax = pValue;
		}
		else if (strcmp(pName, "ymin") == 0) {
			ymin = pValue;
		}
		else if (strcmp(pName, "ymax") == 0) {
			ymax = pValue;
		}
		else if (strcmp(pName, "clamp") == 0) {
			invert = FTOI(pValue);
		}
		else if (strcmp(pName, "skin") == 0) {
			skin = FTOI(pValue);
		}
		else if (strcmp(pName, "cx") == 0) {
			cx = pValue;
		}
		else if (strcmp(pName, "cy") == 0) {
			cy = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    JWF_FLOAT x1 = _x0;
    JWF_FLOAT x = _x0;
    JWF_FLOAT y1 = _y0;
    JWF_FLOAT y = _y0;
    int currIter;

    boolean inverted = pContext->randGen->random() < invert;
    if (inverted) {
      currIter = 0;
    }
    else {
      currIter = iter;
    }
    while ((inverted && (currIter < iter)) ||
        ((!inverted) && ((currIter >= iter) ||
        ((skin < 1) && (currIter < 0.1 * iter * (1 - skin)))))) {
      if ((_x0 == 0) && (_y0 == 0)) {
        // Choose a point at random
        _x0 = (xmax - xmin) * pContext->randGen->random() + xmin;
        _y0 = (ymax - ymin) * pContext->randGen->random() + ymin;
      }
      else {
        // Choose a point close to previous point
        _x0 = (skin + 0.001) * (pContext->randGen->random() - 0.5) + _x0;
        _y0 = (skin + 0.001) * (pContext->randGen->random() - 0.5) + _y0;
      }
      x1 = _x0;
      y1 = _y0;
      x = _x0;
      y = _y0;
      currIter = 0;
      while (((x * x + y * y < 2 * 2) && (currIter < iter))) {
        JWF_FLOAT xtemp = x * x - y * y + _x0;
        y = 2.0 * x * y + _y0;
        x = xtemp;
        currIter++;
      }
      if ((currIter >= iter) || (skin == 1) || (currIter < 0.1 * (iter * (1 - skin)))) {
        // Random point next time
        _x0 = 0;
        _y0 = 0;
      }
    }
    pVarTP->x += pAmount * (x1 + cx * x); // + FTx^;
    pVarTP->y += pAmount * (y1 + cy * y); // + FTy^;
    if (pContext->isPreserveZCoordinate) {
      pVarTP->z += pAmount * pAffineTP->z;
    }
	}

	MandelbrotFunc* makeCopy() {
		return new MandelbrotFunc(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
    _x0 = _y0 = 0.0;
	}

private:
  int iter;
  JWF_FLOAT xmin;
  JWF_FLOAT xmax;
  JWF_FLOAT ymin;
  JWF_FLOAT ymax;
  int invert;
  JWF_FLOAT skin;
  JWF_FLOAT cx;
  JWF_FLOAT cy;

  JWF_FLOAT _x0, _y0;
};

