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
 * Polygon.java
 *
 * A plain old polygon, in this case a dynamically-sized array of Point
 * instances.
 */

package csk.taprats.geometry;

public class Polygon
	implements Cloneable
{
	Point[] 		pts;
	int 			size;
	int 			total_size;
	int				grow;

	public Polygon( int init_size, int grow )
	{
		this.total_size = init_size;
		this.grow = grow;
		this.size = 0;
		this.pts = new Point[ total_size ];
	}

	public Polygon( int init_size )
	{
		this( init_size, 0 );
	}

	public Polygon()
	{
		this( 8 );
	}

	public Polygon( Point[] pts )
	{
		this.pts = new Point[ pts.length ];
		this.grow = 0;
		this.size = pts.length;
		System.arraycopy( pts, 0, this.pts, 0, pts.length );
	}

	public Polygon( Point[] pts, int start, int end )
	{
		this.size = end - start;
		this.pts = new Point[ this.size ];
		this.grow = 0;
		System.arraycopy( pts, start, this.pts, 0, this.size );
	}

	public Object clone()
	{
		return new Polygon( pts, 0, size );
	}

	public final void addVertex( Point pt )
	{
		ensureSize( size + 1 );
		pts[ size ] = pt;
		size++;
	}

	public final void setVertex( int n, Point pt )
	{
		pts[ n ] = pt;
	}

	public final Point getVertex( int idx )
	{
		return pts[ idx ];
	}

	public final int numVertices()
	{
		return size;
	}

	public final void applyTransform( Transform T )
	{
		for( int idx = 0; idx < size; idx++ ) {
			pts[ idx ] = T.apply( pts[ idx ] );
		}
	}

	private final void ensureSize( int desired )
	{
		int new_size = Math.max( total_size, 1 );
		while( new_size < desired ) {
			if( grow == 0 ) {
				new_size <<= 1;
			} else {
				new_size += grow;
			}
		}

		Point[] nvec = new Point[ new_size ];
		System.arraycopy( pts, 0, nvec, 0, size );

		pts = nvec;
		total_size = new_size;
	}

	public final String toString()
	{
		StringBuffer sb = new StringBuffer();

		sb.append( "[" );
		boolean start = true;
		for( int idx = 0; idx < size; idx++ ) {
			if( start ) { 
				start = false;
			} else {
				sb.append( ", " );
			}
			sb.append( pts[ idx ] );
		}
		sb.append( "]" );
		return sb.toString();
	}

	public final double arcLength( boolean closed )
	{
		double len = 0.0;
		for( int idx = 0; idx < size - 1; idx++ ) {
			len += pts[ idx ].dist( pts[ idx + 1 ] );
		}

		if( closed ) {
			return len + pts[ 0 ].dist( pts[ size - 1 ] );
		} else {
			return len;
		}
	}

	public final double area()
	{
		double total = 0.0;

		for( int idx = 0; idx < size; ++idx ) {
			total += (pts[idx].getX()*pts[(idx+1)%size].getY()) -
				(pts[idx].getY()*pts[(idx+1)%size].getX());
		}
		
		return Math.abs( total ) * 0.5;
	}

	/*
 	 * This algorithm is taken from Bob Stein's article _A Point 
	 * about Polygons_[1], in the March 1997 Linux Journal.  
	 *
	 * I was really worried that using Stein's inpoly.c code would
	 * compromise the originality requirement of my Quest for Java
	 * submission.  So I read the web page a couple of times to 
	 * see how the algorithm worked, and then implemented it on my own
	 * without looking at his code.  The result is my original 
	 * code for his algorithm.  I think that's due dilligence.
	 *
	 * [1] http://home.earthlink.net/~bobstein/inpoly/
	 */
	public static final boolean pointInPoly( 
		Point[] pts, int start, int end, Point apt )
	{
		int size = end - start;
		boolean outside = false;

		double x = apt.getX();
		double y = apt.getY();

		Point a = pts[ start ];
		double ax = a.getX();
		double ay = a.getY();

		double xmin = 0.0;
		double ymin = 0.0;
		double xmax = 0.0;
		double ymax = 0.0;

		for( int idx = 0; idx < size; ++idx ) {	
			Point b = pts[ (idx + start + 1) % size ];

			double bx = b.getX();
			double by = b.getY();

			if( ax < bx ) {
				xmin = ax;
				ymin = ay;
				xmax = bx;
				ymax = by;
			} else {
				xmin = bx;
				ymin = by;
				xmax = ax;
				ymax = ay;
			}

			// Does it straddle?
			if( (xmin <= x) && (x < xmax) ) {
				// Is the intersection point north?
				if( (x-xmin)*(ymax-ymin) > (y-ymin)*(xmax-xmin) ) {
					outside = !outside;
				}
			}

			a = b;
			ax = bx;
			ay = by;
		}

		return outside;
	}

    public final boolean containsPoint( Point apt )
    {
		return pointInPoly( pts, 0, size, apt );
    }
}
