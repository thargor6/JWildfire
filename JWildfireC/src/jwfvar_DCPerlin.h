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

#include "limits.h"
#include "jwf_Constants.h"
#include "jwf_Variation.h"
#include "jwf_NoiseTools.h"

class DCPerlinFunc: public Variation {
public:
#define SHAPE_SQUARE 0
#define SHAPE_DISC 1
#define SHAPE_BLUR 2

#define MAP_FLAT 0
#define MAP_SPHERICAL 1
#define MAP_HSPHERE 2
#define MAP_QSPHERE 3
#define MAP_BUBBLE 4
#define MAP_BUBBLE2 5

	DCPerlinFunc() {
		shape = 0;
		map = 0;
		select_centre = 0.0;
		select_range = 1.0;
		centre = 0.25;
		range = 0.25;
		edge = 0.0;
		scale = 1.0;
		octaves = 2;
		amps = 2.0;
		freqs = 2.0;
		z = 0.0;
		select_bailout = 10;
		initParameterNames(9, "shape", "map", "select_centre", "select_range", "centre", "range", "edge", "scale", "octaves", "amps", "freqs", "z", "select_bailout");
	}

	const char* getName() const {
		return "dc_perlin";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "shape") == 0) {
			shape = FTOI(pValue);
		}
		else if (strcmp(pName, "map") == 0) {
			map = FTOI(pValue);
		}
		else if (strcmp(pName, "select_centre") == 0) {
			select_centre = pValue;
		}
		else if (strcmp(pName, "select_range") == 0) {
			select_range = pValue;
		}
		else if (strcmp(pName, "centre") == 0) {
			centre = pValue;
		}
		else if (strcmp(pName, "range") == 0) {
			range = pValue;
		}
		else if (strcmp(pName, "edge") == 0) {
			edge = pValue;
		}
		else if (strcmp(pName, "scale") == 0) {
			scale = pValue;
		}
		else if (strcmp(pName, "octaves") == 0) {
			octaves = FTOI(pValue);
		}
		else if (strcmp(pName, "amps") == 0) {
			amps = pValue;
		}
		else if (strcmp(pName, "freqs") == 0) {
			freqs = pValue;
		}
		else if (strcmp(pName, "z") == 0) {
			z = pValue;
		}
		else if (strcmp(pName, "select_bailout") == 0) {
			select_bailout = FTOI(pValue);
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    /* dc_perlin by slobo777, http://slobo777.deviantart.com/art/dc-perlin-Apophysis-Plugin-186190256 */
    JWF_FLOAT V[3];
    JWF_FLOAT Vx = 0.0, Vy = 0.0, Col, r, theta, s, c, p, e;
    int t;

    t = 0;
    do {
      // Default edge value
      e = 0.0;
      // Assign Vx, Vy according to shape
      switch (shape) {
        case SHAPE_SQUARE:
          Vx = (1.0 + edge) * (pContext->randGen->random() - 0.5);
          Vy = (1.0 + edge) * (pContext->randGen->random() - 0.5);
          r = Vx * Vx > Vy * Vy ? Vx : Vy;
          if (r > 1.0 - edge) {
            e = 0.5 * (r - 1.0 + edge) / edge;
          }
          break;

        case SHAPE_DISC:
          r = pContext->randGen->random() + pContext->randGen->random();
          r = (r > 1.0) ? 2.0 - r : r;
          r *= (1.0 + edge);
          if (r > 1.0 - edge) {
            e = 0.5 * (r - 1.0 + edge) / edge;
          }
          theta = pContext->randGen->random() * M_2PI;
          JWF_SINCOS(theta, &s, &c);
          Vx = 0.5 * r * s;
          Vy = 0.5 * r * c;
          break;

        case SHAPE_BLUR:
          r = (1.0 + edge) * pContext->randGen->random();
          if (r > 1.0 - edge) {
            e = 0.5 * (r - 1.0 + edge) / edge;
          }
          theta = pContext->randGen->random() * M_2PI;
          JWF_SINCOS(theta, &s, &c);
          Vx = 0.5 * r * s;
          Vy = 0.5 * r * c;
          break;
      }

      // Assign V for noise vector position according to map
      switch (map) {
        case MAP_FLAT:
          V[0] = scale * Vx;
          V[1] = scale * Vy;
          V[2] = scale * z;
          break;

        case MAP_SPHERICAL:
          r = 1.0 / (Vx * Vx + Vy * Vy + EPSILON);
          V[0] = scale * Vx * r;
          V[1] = scale * Vy * r;
          V[2] = scale * z;
          break;

        case MAP_HSPHERE:
          r = 1.0 / (Vx * Vx + Vy * Vy + 0.5);
          V[0] = scale * Vx * r;
          V[1] = scale * Vy * r;
          V[2] = scale * z;
          break;

        case MAP_QSPHERE:
          r = 1.0 / (Vx * Vx + Vy * Vy + 0.25);
          V[0] = scale * Vx * r;
          V[1] = scale * Vy * r;
          V[2] = scale * z;
          break;

        case MAP_BUBBLE:
          r = 0.25 - (Vx * Vx + Vy * Vy);
          if (r < 0.0) {
            r = sqrt(-r);
          }
          else {
            r = sqrt(r);
          }
          V[0] = scale * Vx;
          V[1] = scale * Vy;
          V[2] = scale * (r + z);
          break;

        case MAP_BUBBLE2:
          r = 0.25 - (Vx * Vx + Vy * Vy);
          if (r < 0.0) {
            r = sqrt(-r);
          }
          else {
            r = sqrt(r);
          }
          V[0] = scale * Vx;
          V[1] = scale * Vy;
          V[2] = scale * (2 * r + z);
          break;
      }

      p = perlinNoise3D(V, amps, freqs, octaves);
      // Add edge effects
      if (p > 0.0) {
        e = p * (1.0 + e * e * 20.0) + 2.0 * e;
      }
      else {
        e = p * (1.0 + e * e * 20.0) - 2.0 * e;
      }
    }
    while ((e < _notch_bottom || e > _notch_top) && t++ < select_bailout);

    // Add blur effect to transform
    pVarTP->x += pAmount * Vx;
    pVarTP->y += pAmount * Vy;
    if (pContext->isPreserveZCoordinate) {
      pVarTP->z += pAmount * pAffineTP->z;
    }

    // Calculate and add direct colour
    Col = centre + range * p;
    pVarTP->color = Col - floor(Col);
	}

	DCPerlinFunc* makeCopy() {
		return new DCPerlinFunc(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
    _notch_bottom = select_centre - select_range;
    _notch_bottom = (_notch_bottom > 0.75) ? 0.75 : _notch_bottom;
    _notch_bottom = (_notch_bottom < -2.0) ? -3.0 : _notch_bottom;
    _notch_top = select_centre + select_range;
    _notch_top = (_notch_top < -0.75) ? -0.75 : _notch_top;
    _notch_top = (_notch_top > 3.0) ? 3.0 : _notch_top;
	}

private:
	int shape;
	int map;
	double select_centre;
	double select_range;
	double centre;
	double range;
	double edge;
	double scale;
	int octaves;
	double amps;
	double freqs;
	double z;
	int select_bailout;
	double _notch_bottom, _notch_top;
};

