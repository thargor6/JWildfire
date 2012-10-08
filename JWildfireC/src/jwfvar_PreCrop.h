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

class PreCropFunc: public Variation {
public:
	PreCropFunc() {
		left = -1.0;
		top = 1.0;
		right = 1.0;
		bottom = 1.0;
		scatter_area = 0.0;
		zero = 0;
		initParameterNames(6, "left", "top", "right", "bottom", "scatter_area", "zero");
	}

	const char* getName() const {
		return "pre_crop";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "left") == 0) {
			left = pValue;
		}
		else if (strcmp(pName, "top") == 0) {
			top = pValue;
		}
		else if (strcmp(pName, "right") == 0) {
			right = pValue;
		}
		else if (strcmp(pName, "bottom") == 0) {
			bottom = pValue;
		}
		else if (strcmp(pName, "scatter_area") == 0) {
			scatter_area = pValue;
		}
		else if (strcmp(pName, "zero") == 0) {
			zero = FTOI(pValue) == 1;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT x = pAffineTP->x;
		JWF_FLOAT y = pAffineTP->y;
		if (((x < _xmin) || (x > _xmax) || (y < _ymin) || (y > _ymax)) && (zero != 0)) {
			x = y = 0.0;
		}
		else {
			if (x < _xmin)
				x = _xmin + pContext->randGen->random() * _w;
			else if (x > _xmax)
				x = _xmax - pContext->randGen->random() * _w;
			if (y < _ymin)
				y = _ymin + pContext->randGen->random() * _h;
			else if (y > _ymax)
				y = _ymax - pContext->randGen->random() * _h;
		}
		pAffineTP->x = pAmount * x;
		pAffineTP->y = pAmount * y;
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		_xmin = JWF_MIN(left, right);
		_ymin = JWF_MIN(top, bottom);
		_xmax = JWF_MAX(left, right);
		_ymax = JWF_MAX(top, bottom);
		_w = (_xmax - _xmin) * 0.5 * scatter_area;
		_h = (_ymax - _ymin) * 0.5 * scatter_area;
	}

	int const getPriority() {
		return -1;
	}

	PreCropFunc* makeCopy() {
		return new PreCropFunc(*this);
	}

private:
	JWF_FLOAT left;
	JWF_FLOAT right;
	JWF_FLOAT top;
	JWF_FLOAT bottom;
	JWF_FLOAT scatter_area;
	bool zero;

	JWF_FLOAT _xmin, _xmax, _ymin, _ymax, _w, _h;

};

