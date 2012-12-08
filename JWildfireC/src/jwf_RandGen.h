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
#ifndef __JWF_RANDGEN_H__
#define __JWF_RANDGEN_H__

class RandGen {
public:
	RandGen() {
    u = 12244355;
    v = 34384;
		rrmax = 1.0 / (JWF_FLOAT) RAND_MAX123;
	}

	void init(int seed) {
    u = (int) (seed << 16);
    v = (int) (seed << 16) >> 16;
	}

	inline JWF_FLOAT random() {
    v = 36969 * (v & 65535) + (v >> 16);
    u = 18000 * (u & 65535) + (u >> 16);
    int rnd = (v << 16) + u;
    double res = (double) rnd * rrmax;
    return res < 0 ? -res : res;
	}

	inline int random(int pMax) {
		int res = (int) (random() * pMax);
		if (res < 0)
			res = -res;
		if (res >= pMax)
			res = pMax - 1;
		return res;
	}

private:
  int u, v;

	static const int RAND_MAX123 = 0x7fffffff;
	JWF_FLOAT rrmax;
};

#endif // __JWF_RANDGEN_H__
