////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////
//
// taprats -- an interactive design tool for computer-generated 
//            Islamic patterns.
//
// Copyright 2000 Craig S. Kaplan.
// email: csk at cs.washington.edu
//
// Copyright 2010 Pierre Baillargeon
// email: pierrebai at hotmail.com
//
// This file is part of Taprats.
//
// Taprats is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Taprats is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Taprats.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Loose.java
 *
 * A bunch of fuzzy comparisons that have a margin of error.
 * Useful whenever you're doing computational geometry.
 */

package csk.taprats.general;

import csk.taprats.geometry.Point;

public class Loose
{
	public static final double TOL = 1e-7;
	public static final double TOL2 = 1e-10;

	public static final boolean equals( double a, double b )
	{
		return Math.abs( a - b ) < TOL;
	}

	public static final boolean zero( double a )
	{
		return Math.abs( a ) < TOL;
	}

	public static final boolean lessThan( double a, double b )
	{
		return a < (b + TOL);
	}

	public static final boolean greaterThan( double a, double b )
	{
		return a > (b - TOL);
	}

	public static final boolean equals( Point a, Point b )
	{
		return a.dist2( b ) < TOL2;
	}

	public static final boolean zero( Point a )
	{
		return a.mag2() < TOL2;
	}
}
