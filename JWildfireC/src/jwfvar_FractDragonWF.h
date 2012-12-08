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

#include "jwf_Variation.h"
#include "jwf_Constants.h"
#include "jwfvar_AbstractFractWF.h"

class FractDragonWFFunc: public AbstractFractWFFunc {
public:
	FractDragonWFFunc() {
		initParameterNames(16, "max_iter", "xmin", "xmax", "ymin", "ymax", "direct_color", "scalez", "clip_iter_min", "clip_iter_max", "scale", "offsetx", "offsety","offsetz","max_clip_iter","xseed", "yseed" );
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "max_iter") == 0) {
			max_iter = FTOI(pValue);
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
		else if (strcmp(pName, "direct_color") == 0) {
			direct_color = FTOI(pValue);
		}
		else if (strcmp(pName, "scalez") == 0) {
			scalez = pValue;
		}
		else if (strcmp(pName, "clip_iter_min") == 0) {
			clip_iter_min = FTOI(pValue);
		}
		else if (strcmp(pName, "clip_iter_max") == 0) {
			clip_iter_max = FTOI(pValue);
		}
		else if (strcmp(pName, "max_clip_iter") == 0) {
			max_clip_iter = FTOI(pValue);
		}
		else if (strcmp(pName, "scale") == 0) {
			scale = pValue;
		}
		else if (strcmp(pName, "offsetx") == 0) {
			offsetx = pValue;
		}
		else if (strcmp(pName, "offsety") == 0) {
			offsety = pValue;
		}
		else if (strcmp(pName, "offsetz") == 0) {
			offsetz = pValue;
		}
		else if (strcmp(pName, "xseed") == 0) {
			xseed = pValue;
		}
		else if (strcmp(pName, "yseed") == 0) {
			yseed = pValue;
		}
	}

	const char* getName() const {
		return "fract_dragon_wf";
	}

	FractDragonWFFunc* makeCopy() {
		return new FractDragonWFFunc(*this);
	}


protected:

	void initParams() {
    xmin = -0.25;
    xmax = 1.2;
    ymin = -0.75;
    ymax = 0.75;
    xseed = 1.4;
    yseed = 0.85;
    clip_iter_min = 4;
    offsetx = -0.5;
    scale = 5.0;
	}

	int iterate(double pX, double pY) {
    int currIter = 0;
    double x1 = pX;
    double y1 = pY;

    double xs = x1 * x1;
    double ys = y1 * y1;
    while ((currIter++ < max_iter) && (xs + ys < 4.0)) {
      double x2 = (x1 - xs + ys) * xseed - (y1 - 2.0 * x1 * y1) * yseed;
      y1 = (x1 - xs + ys) * yseed + (y1 - 2.0 * x1 * y1) * xseed;

      x1 = x2;
      xs = x1 * x1;
      ys = y1 * y1;
    }
    return currIter;
	}


private:
	double xseed;
	double yseed;
};

