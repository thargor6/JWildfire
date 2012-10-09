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

class Boarders2Func: public Variation {
public:
	Boarders2Func() {
    c = 0.4;
    left = 0.65;
    right = 0.35;
		initParameterNames(3, "c", "left", "right");
	}

	const char* getName() const {
		return "boarders2";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "c") == 0) {
			c = pValue;
		}
		else if (strcmp(pName, "left") == 0) {
			left = pValue;
		}
		else if (strcmp(pName, "right") == 0) {
			right = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    JWF_FLOAT roundX = JWF_RINT(pAffineTP->x);
    JWF_FLOAT roundY = JWF_RINT(pAffineTP->y);
    JWF_FLOAT offsetX = pAffineTP->x - roundX;
    JWF_FLOAT offsetY = pAffineTP->y - roundY;
    if (pContext->randGen->random() >= _cr) {
      pVarTP->x += pAmount * (offsetX * _c + roundX);
      pVarTP->y += pAmount * (offsetY * _c + roundY);
    }
    else {
      if (JWF_FABS(offsetX) >= JWF_FABS(offsetY)) {
        if (offsetX >= 0.0) {
          pVarTP->x += pAmount * (offsetX * _c + roundX + _cl);
          pVarTP->y += pAmount * (offsetY * _c + roundY + _cl * offsetY / offsetX);
        }
        else {
          pVarTP->x += pAmount * (offsetX * _c + roundX - _cl);
          pVarTP->y += pAmount * (offsetY * _c + roundY - _cl * offsetY / offsetX);
        }
      }
      else {
        if (offsetY >= 0.0) {
          pVarTP->y += pAmount * (offsetY * _c + roundY + _cl);
          pVarTP->x += pAmount * (offsetX * _c + roundX + offsetX / offsetY * _cl);
        }
        else {
          pVarTP->y += pAmount * (offsetY * _c + roundY - _cl);
          pVarTP->x += pAmount * (offsetX * _c + roundX - offsetX / offsetY * _cl);
        }
      }
    }
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	Boarders2Func* makeCopy() {
		return new Boarders2Func(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
    _c = JWF_FABS(c);
    _cl = JWF_FABS(left);
    _cr = JWF_FABS(right);
    _c = _c == 0 ? EPSILON : _c;
    _cl = _cl == 0 ? EPSILON : _cl;
    _cr = _cr == 0 ? EPSILON : _cr;
    _cl = _c * _cl;
    _cr = _c + (_c * _cr);
	}

private:
	JWF_FLOAT c;
	JWF_FLOAT left;
	JWF_FLOAT right;

  JWF_FLOAT _c, _cl, _cr;
};

