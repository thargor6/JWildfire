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

class SubFlameWFFunc: public Variation {
public:
	SubFlameWFFunc() {
		offset_x = 0.0;
		offset_y = 0.0;
		offset_z = 0.0;

		flame = NULL;
		xf = NULL;
		p = NULL;
		q = new XYZPoint();

		initParameterNames(3, "offset_x", "offset_y", "offset_z");
		initRessourceNames(1, "flame");
	}

	~SubFlameWFFunc() {
		delete q;
	}

	const char* getName() const {
		return "subflame_wf";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "offset_x") == 0) {
			offset_x = pValue;
		}
		else if (strcmp(pName, "offset_y") == 0) {
			offset_y = pValue;
		}
		else if (strcmp(pName, "offset_z") == 0) {
			offset_z = pValue;
		}
	}

	void setRessource(char *pName, void *pValue) {
		if (strcmp(pName, "flame") == 0) {
			flame = (Flame*) pValue;
			xf = NULL;
			p = NULL;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		if (xf != NULL) {
			int xfIdx = xf->nextAppliedXFormIdxTable[pContext->randGen->random(NEXT_APPLIED_XFORM_TABLE_SIZE)];
			xf = xfIdx >= 0 ? flame->xForms[xfIdx] : NULL;
			if (xf == NULL) {
				return;
			}
			xf->transformPoint(pContext, pAffineTP, pVarTP, p, p);
			if (xf->drawMode == DRAWMODE_HIDDEN)
				return;
			else if ((xf->drawMode == DRAWMODE_OPAQUE) && (pContext->randGen->random() > xf->opacity))
				return;

			XForm *finalXForm = flame->finalXForm;
			if (finalXForm != NULL) {
				finalXForm->transformPoint(pContext, pAffineTP, pVarTP, p, q);
			}
		}

		pVarTP->x += offset_x;
		pVarTP->y += offset_y;
		pVarTP->z += offset_z;
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		if (flame != NULL) {
			// TODO remove hack!
			flame->prepareFlame(pContext, pContext->threadIdx + 1);
			xf = flame->xForms[0];
			p = new XYZPoint();
			p->x = 2.0 * pContext->randGen->random() - 1.0;
			p->y = 2.0 * pContext->randGen->random() - 1.0;
			p->z = 0.0;
			p->color = pContext->randGen->random();
		}
	}

	SubFlameWFFunc* makeCopy() {
		return new SubFlameWFFunc(*this);
	}

protected:
	JWF_FLOAT offset_x;
	JWF_FLOAT offset_y;
	JWF_FLOAT offset_z;

	Flame *flame;
	XForm *xf;
	XYZPoint *p;
	XYZPoint *q;
};

