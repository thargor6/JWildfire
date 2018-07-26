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
 * Feature.java
 *
 * A Feature is an element of a tiling, i.e. a tile.  It's really
 * just an array of points.  I could just use a csk.taprats.geometry.Polygon,
 * but I keep adding (and removing) per-point information, which makes it
 * useful to store the array explicitly -- sometimes I switch Point
 * for a FancyPoint of some kind.  Also, we don't expect the number
 * of points to change once the feature is created, so the array
 * is fine.
 *
 * We also store whether the Feature was created as a regular polygon.
 * This helps later when deciding what Features can have Rosettes
 * in them.
 */

package csk.taprats.tile;

import csk.taprats.geometry.*;

public class Feature
	implements Cloneable
{
	private boolean 		regular;
	private Point[]			points;

	/*
	 * Create an n-sided regular polygon with a vertex at (1,0).
	 */
	public Feature( int n )
	{
		this.regular = true;
		this.points = new Point[ n ];

		for( int idx = 0; idx < n; ++idx ) {
			double angle = (Math.PI / (double)n) * (double)( 2 * idx + 1 );
			double sc = 1.0 / Math.cos( Math.PI / (double)n );
			points[ idx ] = new Point( 
				sc * Math.cos( angle ), sc * Math.sin( angle ) );
		}
	}

	/*
	 * Create a feature explicitly from a polygon.
	 */
	public Feature( Polygon pgon )
	{
		this.regular = false;
		this.points = new Point[ pgon.numVertices() ];
		for( int idx = 0; idx < pgon.numVertices(); ++idx ) {
			this.points[ idx ] = (Point)( pgon.getVertex( idx ).clone() );
		}
	}

	public Feature( Point[] pts )
	{
		this.regular = false;
		this.points = new Point[ pts.length ];
		System.arraycopy( pts, 0, points, 0, pts.length );
	}

	private Feature( Feature other )
	{
		this.regular = other.regular;
		this.points = new Point[ other.points.length ];

		System.arraycopy( other.points, 0, points, 0, other.points.length );
	}

	public Object clone()
	{
		return new Feature( this );
	}

	public final boolean isRegular()
	{
		return regular;
	}

	/* 
	 * Since this method constructs a new polygon each time, getPoints()
	 * should be favoured when possible.
	 */
	public Polygon getPolygon()
	{
		Polygon ret = new Polygon( points.length );
		for( int idx = 0; idx < points.length; ++idx ) {
			ret.addVertex( points[ idx ] );
		}
		return ret;
	}

	public Point[] getPoints()
	{
		return points;
	}

	public final int numPoints()
	{
		return points.length;
	}
}
