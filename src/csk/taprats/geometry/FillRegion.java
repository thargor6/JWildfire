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
 * FillRegion.java
 *
 * When working with periodic geometry, it often becomes necessary to 
 * fill some region with copies of a motif.  This class encapsulates
 * a system for filling quadrilateral regions with the integer linear 
 * combinations of two translation vectors where each translate places
 * the origin inside the quad.  It's sort of a modified polygon
 * filling algorithm, where everything is first transformed into the
 * coordinate system of the translation vectors.  The the quad is 
 * filled in the usual way.
 *
 * The algorithm isn't perfect.  It can leave gaps around the edge of
 * the region to fill.  This is usually worked around by the client --
 * the region is simply expanded before filling.
 *
 * To make the algorithm general, the output is provided through a 
 * callback that gets a sequence of calls, one for each translate.
 */

package csk.taprats.geometry;

public class FillRegion
{
	double[] xmins;
	double[] xmaxs;

	public FillRegion()
	{
		xmins = null;
		xmaxs = null;
	}

	private double[] getBasisChange( Point p1, Point p2 )
	{
		double x1 = p1.getX();
		double y1 = p1.getY();
		double x2 = p2.getX();
		double y2 = p2.getY();

		double det = 1.0 / (x1*y2-x2*y1);

		double[] ret = { y2 * det, -x2 * det, -y1 * det, x1 * det };
		return ret;
	}

	private Point applyBasis( double[] basis, Point pt )
	{
		double x = pt.getX();
		double y = pt.getY();

		return new Point( basis[0]*x+basis[1]*y, basis[2]*x+basis[3]*y );
	}

	public void fill( Polygon pgon, Point T1, Point T2, UnitCallback c )
	{
		/* 
		 * I don't remember exactly where I saw this algorithm before.
		 * It was some site for game developers, and the guy was giving
		 * a tip about filling convex polygons by keeping a list of 
		 * [xmin,xmax] for each row that will be filled, and then walking
		 * down the list.  No doubt my version is sub-optimal.  But
		 * it seems to perform fairly well.
		 */

		double[] basis = getBasisChange( T1, T2 );
		Point[] ps = {
			applyBasis( basis, pgon.getVertex( 0 ) ),
			applyBasis( basis, pgon.getVertex( 1 ) ),
			applyBasis( basis, pgon.getVertex( 2 ) ),
			applyBasis( basis, pgon.getVertex( 3 ) ) };

		double ymin = ps[0].getY();
		double ymax = ymin;

		for( int idx = 1; idx < 4; ++idx ) {
			double yy = ps[idx].getY();
			if( yy < ymin ) {
				ymin = yy;
			}
			if( yy > ymax ) {
				ymax = yy;
			}
		}

		double fymin = Math.floor( ymin );
		double fymax = Math.floor( ymax );

		int iymin = (int)fymin;
		int iymax = (int)fymax;

		if( iymax - iymin > 999 ) {
			throw new IllegalArgumentException( "Too many units" );
		}

		if( xmins == null ) {
			xmins = new double[ 1000 ];
			xmaxs = new double[ 1000 ];
		}

		for( int idx = 0; idx <= (iymax-iymin); ++idx ) {
			xmins[idx] = Double.MAX_VALUE;
			xmaxs[idx] = -Double.MAX_VALUE;
		}

		for( int lc = 0; lc < 4; ++lc ) {
			Point from = ps[ lc ];
			Point to = ps[ (lc+1) % 4 ];

			// point* from = ps + lc;
			// point* to = ps + ((lc+1) % 4);

			double fx = from.getX();
			double fy = from.getY();
			double tx = to.getX();
			double ty = to.getY();

			if( fy == ty ) {
				// Horizontal line.

				double y = Math.floor( fy );
				int iy = (int)y;
				int offs = iy - iymin;

				if( fx < xmins[offs] ) {
					xmins[offs] = fx;
				}
				if( fx > xmaxs[offs] ) {
					xmaxs[offs] = fx;
				}
				if( tx < xmins[offs] ) {
					xmins[offs] = tx;
				}
				if( tx > xmaxs[offs] ) {
					xmaxs[offs] = tx;
				}
			} else {
				// Not horizontal.  Make from be lower.

				if( fy > ty ) {
					double tmp = fy;
					fy = ty;
					ty = tmp;
					tmp = fx;
					fx = tx;
					tx = tmp;
				}

				double y = Math.floor( fy );
				int iy = (int)y;

				double ym = Math.floor( ty );
				double iym = (int)ym;

				double m = (tx-fx)/(ty-fy);

				double x = fx + (y - fy) * m;

				while( iy <= iym ) {
					int offs = iy - iymin;

					double nx = x + m;
					double ny = y + 1;

					if( y <= fy ) {
						if( fx < xmins[offs] ) {
							xmins[offs] = fx;
						}
						if( fx > xmaxs[offs] ) {
							xmaxs[offs] = fx;
						}
					} else {
						if( x < xmins[offs] ) {
							xmins[offs] = x;
						}
						if( x > xmaxs[offs] ) {
							xmaxs[offs] = x;
						}
					}

					if( ny > ty ) {
						if( tx < xmins[offs] ) {
							xmins[offs] = tx;
						}
						if( tx > xmaxs[offs] ) {
							xmaxs[offs] = tx;
						}
					} else {
						if( nx < xmins[offs] ) {
							xmins[offs] = nx;
						}
						if( nx > xmaxs[offs] ) {
							xmaxs[offs] = nx;
						}
					}

					++iy;
					y = ny;
					x = nx;
				}
			}
		}

		for( int idx = 0; idx <= (iymax-iymin); ++idx ) {
			double y = (double)( idx + iymin );

			double startx = Math.floor( xmins[idx] );
			double endx = Math.floor( xmaxs[idx] );

			while( startx <= endx ) {
				c.receive( (int)startx, (int)y );
				startx += 1.0;
			}
		}
	}
}
