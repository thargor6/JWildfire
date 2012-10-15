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
#include "jwf_NoiseTools.h"
#include "jwf_VoronoiTools.h"

class CrackleFunc: public Variation {
public:
  //These set cache size for cell centres, they take a lot of processing, so it's handy to
  //keep values between calls
  #define CACHE_NUM 10
  #define CACHE_WIDTH 21

	CrackleFunc() {
		cellsize = 1.0;
		power = 1.0;
		distort = 0.0;
		scale = 1.0;
	  z=0.0;
		initParameterNames(5, "cellsize", "power", "distort", "scale", "z");

	  offset[0][0] = -1;
	  offset[0][1] = -1;
	  offset[1][0] = -1;
	  offset[1][1] = 0;
	  offset[2][0] = -1;
	  offset[2][1] = 1;
	  offset[3][0] = 0;
	  offset[3][1] = -1;
	  offset[4][0] = 0;
	  offset[4][1] = 0;
	  offset[5][0] = 0;
	  offset[5][1] = 1;
	  offset[6][0] = 1;
	  offset[6][1] = -1;
	  offset[7][0] = 1;
	  offset[7][1] = 0;
	  offset[8][0] = 1;
	  offset[8][1] = 1;
	}

	const char* getName() const {
		return "crackle";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "cellsize") == 0) {
			cellsize = pValue;
		}
		else if (strcmp(pName, "power") == 0) {
			power = pValue;
		}
		else if (strcmp(pName, "distort") == 0) {
			distort = pValue;
		}
		else if (strcmp(pName, "scale") == 0) {
			scale = pValue;
		}
		else if (strcmp(pName, "z") == 0) {
			z = pValue;
		}
	}


	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    JWF_FLOAT DXo, DYo, L, R, s, trgL;
    JWF_FLOAT U[2];
    int XCv, YCv;

    // An infinite number of invisible cells? No thanks!
    if (0.0 == cellsize) {
      return;
    }

    // Scaling factor
    s = cellsize / 2.0;

    // For a blur effect, base everything starting on a circle radius 1.0
    // (as opposed to reading the values from FTx and FTy)
    JWF_FLOAT blurr = (pContext->randGen->random() + pContext->randGen->random()) / 2.0 + (pContext->randGen->random() - 0.5) / 4.0;

    JWF_FLOAT theta = 2 * M_PI * pContext->randGen->random();

    JWF_FLOAT sint, cost;
    JWF_SINCOS(theta, &sint, &cost);
    U[_x_] = blurr * sint;
    U[_y_] = blurr * cost;

    // Use integer values as Voronoi grid co-ordinates
    XCv = (int) JWF_FLOOR(U[_x_] / s);
    YCv = (int) JWF_FLOOR(U[_y_] / s);

    // Get a set of 9 square centre points, based around the one above
    int di, dj;
    int i = 0;
    for (di = -1; di < 2; di++) {
      for (dj = -1; dj < 2; dj++) {
        cached_position(XCv + di, YCv + dj, z, s, distort, P[i]);
        i++;
      }
    }

    int q = closest(P, 9, U);

    // Remake list starting from chosen square, ensure it is completely surrounded (total 9 points)

    // First adjust centres according to which one was found to be closest
    XCv += offset[q][_x_];
    YCv += offset[q][_y_];

    // Get a new set of 9 square centre points, based around the definite closest point
    i = 0;
    for (di = -1; di < 2; di++) {
      for (dj = -1; dj < 2; dj++) {
        cached_position(XCv + di, YCv + dj, z, s, distort, P[i]);
        i++;
      }
    }

    L = voronoi(P, 9, 4, U); // index 4 is centre cell

    // Delta vector from centre
    DXo = U[_x_] - P[4][_x_];
    DYo = U[_y_] - P[4][_y_];

    /////////////////////////////////////////////////////////////////
    // Apply "interesting bit" to cell's DXo and DYo co-ordinates

    // trgL is the new value of L
    trgL = JWF_POW(L + 1e-100, power) * scale; // ( 0.9 )

    R = trgL / (L + 1e-100);

    DXo *= R;
    DYo *= R;

    // Add cell centre co-ordinates back in
    DXo += P[4][_x_];
    DYo += P[4][_y_];

    // Finally add values in
    pVarTP->x += pAmount * DXo;
    pVarTP->y += pAmount * DYo;
    if (pContext->isPreserveZCoordinate) {
      pVarTP->z += pAmount * pAffineTP->z;
    }
	}

	CrackleFunc* makeCopy() {
		return new CrackleFunc(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
    // Pre-calculate cache of grid centres, to save time later . . .
    for (int x = -CACHE_NUM; x <= CACHE_NUM; x++) {
      for (int y = -CACHE_NUM; y <= CACHE_NUM; y++) {
        position(x, y, z, cellsize / 2.0, distort, C[x + CACHE_NUM][y + CACHE_NUM]);
      }
    }
	}

private:
	JWF_FLOAT cellsize;
	JWF_FLOAT power;
	JWF_FLOAT distort;
	JWF_FLOAT scale;
	JWF_FLOAT z;

  // P is a working list of points
  JWF_FLOAT P[VORONOI_MAXPOINTS][2];
  // C is a cache of pre-calculated centres
  JWF_FLOAT C[CACHE_WIDTH][CACHE_WIDTH][2];

  int offset[9][2];

  //Waffle's cells are based on a simple square grid which is distorted using a noise function

  //position() calculates cell centre for cell (x, y), given plane slice z, scale factor s, distortion d
  // and stores in supplied array
  void position(int x, int y, JWF_FLOAT z, JWF_FLOAT s, JWF_FLOAT d, JWF_FLOAT *V) {
    JWF_FLOAT E[3];
    JWF_FLOAT F[3];

    // Values here are arbitrary, chosen simply to be far enough apart so they do not correlate
    E[_x_] = x * 2.5;
    E[_y_] = y * 2.5;
    E[_z_] = z * 2.5;
    // Cross-over between x and y is intentional
    F[_x_] = y * 2.5 + 30.2;
    F[_y_] = x * 2.5 - 12.1;
    F[_z_] = z * 2.5 + 19.8;

    V[_x_] = (x + d * simplexNoise3D(E)) * s;
    V[_y_] = (y + d * simplexNoise3D(F)) * s;
  }

  //cached_position gives centre co-ordinates either from cache, or calculated from scratch if needed
  void cached_position(int x, int y, JWF_FLOAT z, JWF_FLOAT s, JWF_FLOAT d, JWF_FLOAT *V) {
    if (JWF_FABS(x) <= CACHE_NUM && JWF_FABS(y) <= CACHE_NUM) {
      V[_x_] = C[x + CACHE_NUM][y + CACHE_NUM][_x_];
      V[_y_] = C[x + CACHE_NUM][y + CACHE_NUM][_y_];
    }
    else {
      position(x, y, z, s, d, V);
    }
  }

};

