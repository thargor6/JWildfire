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

// Port of Hexes and Crackle plugin by slobo777, see http://slobo777.deviantart.com/art/Apo-Plugins-Hexes-And-Crackle-99243824
// All credits for this wonderful plugin to him!
#ifndef __JWF_VORONOI_TOOLS_H__
#define __JWF_VORONOI_TOOLS_H__

#include "jwf_Math.h"

#define VORONOI_MAXPOINTS 25

JWF_FLOAT vratio(JWF_FLOAT P[2], JWF_FLOAT Q[2], JWF_FLOAT U[2]);
int closest(JWF_FLOAT P[VORONOI_MAXPOINTS][2], int n, JWF_FLOAT U[2]);
JWF_FLOAT voronoi(JWF_FLOAT P[VORONOI_MAXPOINTS][2], int n, int q, JWF_FLOAT U[2]);

#undef _x_
#define _x_ 0
#undef _y_
#define _y_ 1
#undef _z_
#define _z_ 2

#endif // __JWF_VORONOI_TOOLS_H__
