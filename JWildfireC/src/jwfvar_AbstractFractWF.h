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

#ifndef ABSTRACT_FRACTWF_FUNC
#define ABSTRACT_FRACTWF_FUNC

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class AbstractFractWFFunc: public Variation {
public:

	AbstractFractWFFunc() {
    max_iter = 100;
    xmin = -1.6;
    xmax = 1.6;
    xmin = -1.2;
    ymax = 1.2;
    direct_color = 1;
    scalez = 1.0;
    clip_iter_min = 3;
    clip_iter_max = -5;
    scale = 3.0;
    offsetx = 0.0;
    offsety = 0.0;
    offsetz = 0.0;
    max_clip_iter = 3;
    initParams();
	}

protected:
  int max_iter;
  double xmin;
  double xmax;
  double ymin;
  double ymax;
  int direct_color;
  double scalez;
  int clip_iter_min;
  int clip_iter_max;
  double scale;
  double offsetx;
  double offsety;
  double offsetz;
  int max_clip_iter;

  virtual void initParams() {

  }

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    double x0 = 0.0, y0 = 0.0;
    int iterCount = 0;
    for (int i = 0; i < max_clip_iter; i++) {
      x0 = (xmax - xmin) * pContext->randGen->random() + xmin;
      y0 = (ymax - ymin) * pContext->randGen->random() + ymin;
      iterCount = iterate(x0, y0);
      if ((clip_iter_max < 0 && iterCount >= (max_iter + clip_iter_max)) || (clip_iter_min > 0 && iterCount <= clip_iter_min)) {
        if (i == max_clip_iter - 1) {
          pVarTP->x = pVarTP->y = pVarTP->z = -120000.0 * (pContext->randGen->random() + 0.5);
          return;
        }
      }
      else {
        break;
      }
    }

    pVarTP->x += scale * pAmount * (x0 + offsetx);
    pVarTP->y += scale * pAmount * (y0 + offsety);
    pVarTP->z += scale * pAmount * (scalez / 10 * ((double) iterCount / (double) max_iter) + offsetz);
    if (direct_color != 0) {
      pVarTP->color = (double) iterCount / (double) max_iter;
      if (pVarTP->color < 0)
        pVarTP->color = 0;
      else if (pVarTP->color > 1.0)
        pVarTP->color = 1.0;
    }
	}


  virtual int iterate(double pX, double pY)=0;
};

#endif
