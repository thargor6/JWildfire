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
 * Rosette.java
 *
 * The rosette is a classic feature of Islamic art.  It's a star
 * shape surrounded by hexagons.
 *
 * This class implements the rosette as a RadialFigure using the 
 * geometric construction given in Lee [1].
 *
 * [1] A.J. Lee, _Islamic Star Patterns_.  Muqarnas 4.
 */

package csk.taprats.app;

import csk.taprats.geometry.*;

public class Rosette
	extends RadialFigure
	implements Cloneable
{
	private double  q;
	private int 	s;

	public Rosette( int n, double q, int s )
	{
		super( n );
		this.q = q;
		this.s = s;
	}

	public Object clone()
	{
		return new Rosette( getN(), getQ(), getS() );
	}
	
	public double getQ()
	{
		return q;
	}

	public void setQ( double q )
	{
		this.q = q;
	}

	public int getS()
	{
		return s;
	}

	public void setS( int s )
	{
		this.s = s;
	}

	public Map buildUnit()
	{
		Point tip = new Point( 1.0, 0.0 );  // The point to build from
		Point rtip = getArc( don );			// The next point over.
		
		double q_clamp = Math.min( Math.max( q, -0.99 ), 0.99 );
		int s_clamp = Math.min( s, (n-1) / 2 );

		// Consider an equilateral triangle formed by the origin,
		// up_outer and a vertical edge extending down from up_outer.
		// The center of the bottom edge of that triangle defines the
		// bisector of the angle leaving up_outer that we care about.
		double r_outer = 1.0 / Math.cos( Math.PI * don );
		Point up_outer = getArc( 0.5 * don ).scale( r_outer );
		Point bisector = up_outer.subtract( new Point( 0.0, r_outer ) );
		bisector.scaleD( 0.5 );

		double th = rtip.subtract( tip ).getAngle();

		Point stable_isect = up_outer.add(
			up_outer.normalize().scale( -up_outer.getY() ) );
		double stable_angle = stable_isect.subtract( tip ).getAngle();

		if( q_clamp >= 0.0 ) {
			th = th * (1.0 - q_clamp) + (Math.PI * 0.5) * q_clamp;
		} else {
			// th = th * (1.0 - (-q_clamp)) + Math.PI * (-q_clamp);
			th = th * (1.0 + q_clamp) - stable_angle * q_clamp;
		}

		// Heh heh - you said q-tip - heh heh.
		Point qtip = new Point( 1.0 + Math.cos( th ), Math.sin( th ) );

		Point key_point = Intersect.getIntersection( 
			tip, qtip, up_outer, bisector );

		Point key_end = key_point.convexSum( stable_isect, 10.0 );

		Point[] points = new Point[ s_clamp + 2 ];
		int index = 0;

		points[index] = key_point;
		++index;

		Point key_r_p = new Point( key_point.getX(), -key_point.getY() );
		Point key_r_e = new Point( key_end.getX(), -key_end.getY() );

		for( int idx = 1; idx <= s_clamp; ++idx ) {
			Tr.applyD( key_r_p );
			Tr.applyD( key_r_e );

			Point mid = Intersect.getIntersection( 
				key_point, key_end, key_r_p, key_r_e );
			
			// FIXME --
			// For some combinations of n, q and s (for example, 
			// n = 12, q = -0.8, s = 4), the intersection fails
			// because the line segments being checked end up 
			// parallel.  Rather than knowing mathematically when
			// that happens, I punt and check after the fact whether
			// the intersection test failed.

			if( mid != null ) {
				points[index] = mid;
				++index;
			}
		}

		Map from = new Map();

		Vertex vt = from.insertVertex( tip );
		Vertex top_prev = vt;
		Vertex bot_prev = vt;

		for( int idx = 0; idx < index; ++idx ) {
			Vertex top = from.insertVertex( points[ idx ] );
			Vertex bot = from.insertVertex(
				new Point( points[ idx ].getX(), -points[ idx ].getY() ) );
			
			from.insertEdge( top_prev, top );
			from.insertEdge( bot_prev, bot );

			top_prev = top;
			bot_prev = bot;
		}

		return from;
	}

	/*
	public static final void main( String[] args )
	{
		int n = Integer.parseInt( args[0] );
		double q = (new Double(args[1])).doubleValue();
		int s = Integer.parseInt( args[2] );

		Rosette rosette = new Rosette( n, q, s );
		Map m = rosette.getMap();

		csk.taprats.ui.MapViewer mv = 
			new csk.taprats.ui.MapViewer( -1.5, 1.5, 3.0, m, true );
		mv.setSize( 500, 500 );
		csk.taprats.toolkit.Util.openTestFrame( mv );
		// (new Rosette(8,0.0,2)).buildUnit().dump( System.err );
	}
	*/
}
