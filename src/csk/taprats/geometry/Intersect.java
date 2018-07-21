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
 * Intersect.java
 *
 * Some routines for testing line segment intersections.
 */

package csk.taprats.geometry;

import csk.taprats.general.Loose;

public class Intersect
{
	private static final double TOL = 1.0E-9;

	/*
	 * Return a point (s,t), where s is the fraction of the from p1 to
	 * q1 where an intersection occurs.  t is defined similarly for p2 and q2.
	 * If there's no intersection, return null.
	 */
	static public Point getIntersectionParams( 
		Point p1, Point q1, Point p2, Point q2 )
	{
		double p1x = p1.getX();
		double p1y = p1.getY();

		double q1x = q1.getX();
		double q1y = q1.getY();

		double p2x = p2.getX();
		double p2y = p2.getY();

		double q2x = q2.getX();
		double q2y = q2.getY();

		double d1x = q1x - p1x;
		double d1y = q1y - p1y;
		double d2x = q2x - p2x;
		double d2y = q2y - p2y;

		double det = (d1x * d2y) - (d1y * d2x);

		if( Loose.zero( det ) ) {
			// Parallel.  We won't worry about cases where endpoints touch
			// and we certainly won't worry about overlapping lines.
			// That leaves, um, nothing.  Done!
			return null;
		}

		// These two lines are adapted from O'Rourke's
		// _Computational Geometry in C_ segment-segment intersection code.
		double is = -((p1x*d2y) + p2x*(p1y-q2y) + q2x*(p2y-p1y)) / det;
		double it = ((p1x*(p2y-q1y)) + q1x*(p1y-p2y) + p2x*d1y) / det;

		if( (is < -Loose.TOL) || (is > (1.0 + Loose.TOL)) ) {
			return null;
		}
		if( (it < -Loose.TOL) || (it > (1.0 + Loose.TOL)) ) {
			return null;
		}

		return new Point( is, it );
	}

	/*
	 * Get the position of the intersection by interpolating. 
	 */
	static public Point getIntersection( 
		Point p1, Point q1, Point p2, Point q2 )
	{
		Point ip = getIntersectionParams( p1, q1, p2, q2 );
		if( ip != null ) {
			return p1.convexSum( q1, ip.getX() );
		} else {
			return null;
		}
	}

	/*
	 * Don't return the intersection if it is at the enpoints of 
	 * both segments.
	 */
	static public Point getTrueIntersection(
		Point p1, Point q1, Point p2, Point q2 )
	{
		Point ip = getIntersectionParams( p1, q1, p2, q2 );

		if( ip != null ) {
			double s = ip.getX();
			double t = ip.getY();

			if( s < Loose.TOL ) {
				if( (t < Loose.TOL) || ((1.0-t) < Loose.TOL) ) {
					return null;
				}
			} else if( (1.0-s) < Loose.TOL ) {
				if( (t < Loose.TOL) || ((1.0-t) < Loose.TOL) ) {
					return null;
				}
			}

			return p1.convexSum( q1, s );
		} else {
			return null;
		}
	}
}
