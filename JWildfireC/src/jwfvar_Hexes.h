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
#ifndef JWFVAR_HEXES_H_
#define JWFVAR_HEXES_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"
#include "jwf_NoiseTools.h"
#include "jwf_VoronoiTools.h"


class HexesFunc: public Variation {
public:
	HexesFunc() {
    cellsize = 1.0;
    power = 1.0;
    rotate = 0.166;
    scale = 1.0;
		initParameterNames(4, "cellsize", "power", "rotate","scale");

		cell_choice[0][0] = -1;
		cell_choice[0][1] = -1;
		cell_choice[1][0] = -1;
		cell_choice[1][1] = 0;
		cell_choice[2][0] = -1;
		cell_choice[2][1] = 1;
		cell_choice[3][0] = 0;
		cell_choice[3][1] = -1;
		cell_choice[4][0] = 0;
		cell_choice[4][1] = 0;
		cell_choice[5][0] = 0;
		cell_choice[5][1] = 1;
		cell_choice[6][0] = 1;
		cell_choice[6][1] = -1;
		cell_choice[7][0] = 1;
		cell_choice[7][1] = 0;
		cell_choice[8][0] = 1;
		cell_choice[8][1] = 1;
	}

	const char* getName() const {
		return "hexes";
	}

	void setParameter(char *pName, float pValue) {
		if (strcmp(pName, "cellsize") == 0) {
			cellsize = pValue;
		}
		else if (strcmp(pName, "power") == 0) {
			power = pValue;
		}
		else if (strcmp(pName, "rotate") == 0) {
			rotate = pValue;
		}
		else if (strcmp(pName, "scale") == 0) {
			scale = pValue;
		}
	}

	// Following are pre-calculated fixed multipliers for converting
	// between "Hex" co-ordinates and "Original" co-ordinates.

	// This is, in fact, an affine transform. So we'll use same notation
	// as for Apophysis transforms:
	// x_hex = a_hex * x_cartesian + b_hex * y_cartesian
	// y_hex = c_hex * x_cartesian + d_hex * y_cartesian
	//  . . . and the reverse:
	// x_cartesian =  a_cart * x_hex + b_cart * y_hex
	// y_cartesian =  c_cart * x_hex + d_cart * y_hex
	// Values for e and f are 0.0 in both cases, so not required.

	// Xh = (Xo + sqrt(3) * Yo) / (3 * l)
	static const float a_hex = 1.0/3.0;
	static const float b_hex = 1.7320508075688772935/3.0;
	// Now:  Xh = ( a_hex * Xo + b_hex * Yo ) / l;

	// Yh = (-Xo + sqrt(3) * Yo) / (3 * l)
	static const float c_hex = -1.0/3.0;
	static const float d_hex = 1.7320508075688772935/3.0;
	// Now:  Yh = ( c_hex * Xo + d_hex * Yo ) / l;

	// Xo = 3/2 * l * (Xh - Yh)
	static const float a_cart = 1.5;
	static const float b_cart = -1.5;
	// Now:  Xo = ( a_cart * Xh + b_cart * Yh ) * l;

	// Yo = sqrt(3)/2 * l * (Xh + Yh)
	static const float c_cart = 1.7320508075688772935/2.0;
	static const float d_cart = 1.7320508075688772935/2.0;
	// Now:  Yo = ( c_cart * Xh + d_cart * Yh ) * l;

	// centre gives centre co-ordinates either from cache,
	// or calculated from scratch if needed
	void cell_centre( int x, int y, float s, float V[2] ) {
		V[_x_] = (a_cart * x + b_cart * y ) * s;
		V[_y_] = (c_cart * x + d_cart * y ) * s;
	}


	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
		float P[VORONOI_MAXPOINTS][2];
		float U[2];
		float DXo, DYo, L, L1, L2, R, s, trgL, Vx, Vy;
		int Hx, Hy;

		// For speed/convenience
		s = cellsize;

		// Infinite number of small cells? No effect . . .
	    if ( 0.0 == s ) {
	    	return;
	    }

		// Get cartesian co-ordinates, and convert to hex co-ordinates
	    U[_x_] = pAffineTP->x;
	    U[_y_] = pAffineTP->y;

	    Hx = (int) floor( ( a_hex * U[_x_] + b_hex * U[_y_] ) / s );
	    Hy = (int) floor( ( c_hex * U[_x_] + d_hex * U[_y_] ) / s );

		// Get a set of 9 hex centre points, based around the one above
		int i = 0;
		int di, dj;
		for (di = -1; di < 2; di++) {
			for (dj = -1; dj < 2; dj++) {
				cell_centre( Hx + di, Hy + dj, s, P[i] );
				i++;
			}
		}

		int q = closest( P, 9, U );

		// Remake list starting from chosen hex, ensure it is completely surrounded (total 7 points)

		// First adjust centres according to which one was found to be closest
		Hx += cell_choice[q][_x_];
		Hy += cell_choice[q][_y_];

		// First point is central/closest
		cell_centre( Hx, Hy, cellsize, P[0] );

		// Next six points are based on hex graph (6 hexes around centre). As long as
		// centre points are not too distorted from simple hex, this defines all possible edges

		// In hex co-ords, offsets are: (0,1) (1,1) (1,0) (0,-1) (-1,-1) (-1, 0)
		cell_centre( Hx    , Hy + 1, s, P[1] );
		cell_centre( Hx + 1, Hy + 1, s, P[2] );
		cell_centre( Hx + 1, Hy    , s, P[3] );
		cell_centre( Hx    , Hy - 1, s, P[4] );
		cell_centre( Hx - 1, Hy - 1, s, P[5] );
		cell_centre( Hx - 1, Hy    , s, P[6] );

		L1 = voronoi( P, 7, 0, U );

		// Delta vector from centre of hex
		DXo = U[_x_] - P[0][_x_];
		DYo = U[_y_] - P[0][_y_];

		/////////////////////////////////////////////////////////////////
		// Apply "interesting bit" to cell's DXo and DYo co-ordinates

		// trgL is the defined value of l, independent of any rotation
		trgL = powf( L1 + 1e-100, power ) * scale;

		// Rotate
		Vx = DXo * _rotCos + DYo * _rotSin;
	    Vy = -DXo * _rotSin + DYo * _rotCos;

		//////////////////////////////////////////////////////////////////
		// Measure voronoi distance again

		U[_x_] = Vx + P[0][_x_];
		U[_y_] = Vy + P[0][_y_];
		L2 = voronoi( P, 7, 0, U );

		//////////////////////////////////////////////////////////////////
		// Scale to meet target size . . . adjust according to how close
		// we are to the edge

		// Code here attempts to remove the "rosette" effect caused by
		// scaling difference between corners and closer edges

		// L is maximum of L1 or L2 . . .
		// When L = 0.8 or higher . . . match trgL/L2 exactly
		// When L = 0.5 or less . . . match trgL/L1 exactly


		L = ( L1 > L2 ) ? L1 : L2;

		if ( L < 0.5 ) {
			R = trgL / L1;
		} else {
			if ( L > 0.8 ) {
				R = trgL / L2;
			} else {
				R = ( ( trgL / L1 ) * ( 0.8 - L )  + ( trgL / L2 ) * ( L - 0.5 ) ) / 0.3;
			}
		}

		Vx *= R;
		Vy *= R;

		// Add cell centre co-ordinates back in
	    Vx += P[0][_x_];
	    Vy += P[0][_y_];

		// Finally add values in
		pVarTP->x += pAmount * Vx;
		pVarTP->y += pAmount * Vy;

		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	HexesFunc* makeCopy() {
		return new HexesFunc(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, float pAmount) {
    _rotSin = sinf(rotate * 2.0f * M_PI);
    _rotCos = cosf(rotate * 2.0f * M_PI);
	}

private:
	int cell_choice[9][2];
  float cellsize;
  float power;
  float rotate;
  float scale;
  float _rotSin, _rotCos;
};

#endif // JWFVAR_HEXES_H_
