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
 * Vertex.java
 * 
 * The vertex abstraction for planar maps.  A Vertex has the usual graph
 * component, a list of adjacent edges.  It also has the planar component,
 * a position.  Finally, there's a user data field for applications.
 */

package csk.taprats.geometry;

import java.util.Vector;
import java.util.Enumeration;

class neighbour
{
	Edge		edge;
	double		angle;

	neighbour	next;

	neighbour( Vertex v, Edge e )
	{
		this.edge = e;
		recalcAngle( v );
		this.next = null;
	}

	void recalcAngle( Vertex v )
	{
		Vertex o = edge.getOther( v );
		Point pd = o.getPosition().subtract( v.getPosition() );
		pd.normalizeD();

		angle = pseudoAtan2( pd.getX(), pd.getY() );
	}

	/* 
	 * What the heck is this?
	 *
	 * For the purposes of maintaining the planar map data structure,
	 * we need to store the edges leaving a vertex in sorted order by
	 * their angles.  The start point isn't important.  What matters is
	 * that a radius swept counterclockwise at a vertex encounters 
	 * edges in the order they're stored in Vertex.edges.
	 *
	 * It's pretty expensive to compute atan2, the accurate measure of 
	 * an angle.  But all we're doing is comparing the angles of two
	 * different edges.  We don't need the exact values of those angles.
	 * 
	 * So this function provides a value that can be used in the place
	 * of angles for this purpose.  "Angles" returned by pseudoAtan2
	 * will always compare the same way as angles returned by Math.atan2.
	 * It's the composition of atan2 with a monotonic function.  Its
	 * range is [0,1) instead of [-PI,PI).  But it works fine.
	 *
	 * The last issue is whether it's still faster to use the built-in
	 * atan2 rather than rely on Java to make this function faster.
	 * That's a toughie.
	 */
	static double pseudoAtan2( double x, double y )
	{
		if( x > 0 ) {
			if( y >= 0 ) {
				return y * 0.25;
			} else {
				return 1.0 + (y * 0.25);
			}
		} else {
			if( y > 0 ) {
				return 0.25 * (2.0 - y);
			} else {
				return 0.5 - (y * 0.25);
			}
		}
	}
}

public class Vertex 
{
	Map			map;
		
	Point 		pos;
	Object		data;

	neighbour	edges;
	int			num_edges;

	Vertex		copy; 	// Used when cloning the map.

	Vertex( Map map, Point pos )
	{
		this.map = map;
		this.pos = pos;
		this.num_edges = 0;

		this.data = null;
		this.edges = null;
		this.copy = null;
	}

	public final Point getPosition()
	{
		return pos;
	}

	public final Object getData()
	{
		return data;
	}

	public final void setData( Object data )
	{
		this.data = data;
	}

	public final Map getMap()
	{
		return map;
	}

	public final int numNeighbours()
	{
		return num_edges;
	}

	/* 
	 * Define some enumerations for examining the neighbours of 
	 * a vertex.  Since neighbours are stored in a circular linked
	 * list, we can't make do with indexing.  We need sequential
	 * access, ergo Enumerations.
	 */

	class NeighbourEnum
		implements Enumeration
	{
		private neighbour start;
		private neighbour cur;

		NeighbourEnum( neighbour start )
		{
			this.start = start;
			this.cur = start;
		}

		public boolean hasMoreElements()
		{
			return cur != null;
		}

		public Object nextElement()
		{
			neighbour ret = cur;
			cur = cur.next;
			if( cur.equals( start ) ) {
				cur = null;
			}

			return ret;
		}
	}

	class EdgeEnum
		extends NeighbourEnum
	{
		EdgeEnum( neighbour start )
		{
			super( start );
		}

		public Object nextElement()
		{
			neighbour ret = (neighbour)( super.nextElement() );
			return ret.edge;
		}
	}

	final Enumeration internalNeighbours()
	{
		return new NeighbourEnum( edges );
	}

	/*
	 * FIXME -- this should probably be called getNeighbours()
	 */

	public final Enumeration neighbours()
	{
		return new EdgeEnum( edges );
	}

	public final Edge[] getBeforeAndAfter( Edge edge )
	{
		neighbour last = edges;
		neighbour cur = last.next;
		neighbour next = cur.next;

		do {
			if( cur.edge.equals( edge ) ) {
				Edge[] ret = { last.edge, next.edge };
				return ret;
			}

			last = cur;
			cur = next;
			next = next.next;
		} while( last != edges );

		return null;
	}

	final neighbour findNeighbour( Vertex other )
	{
		if( edges == null ) {
			return null;
		} else {
			neighbour cur = edges;
			do {
				if( cur.edge.getOther( this ).equals( other ) ) {
					return cur;
				}

				cur = cur.next;
			} while( cur != edges );

			return null;
		}
	}

	public final Edge getNeighbour( Vertex other )
	{
		neighbour n = findNeighbour( other );
		if( n != null ) {
			return n.edge;
		} else {
			return null;
		}
	}

	public final boolean connectsTo( Vertex other )
	{
		return findNeighbour( other ) != null;
	}

	/*
	 * Insert the edge into the vertex's neighbour list.  Edges
	 * are stored in sorted order by angle (though the starting point
	 * isn't important).  So traverse the list until there's a spot
	 * where the arc swept by consecutive edges contains the new edge.
	 */
	final void insertEdge( Edge edge )
	{
		neighbour n = new neighbour( this, edge );

		if( edges == null ) {
			n.next = n;
			edges = n;
			num_edges = 1;
		} else {
			double a = n.angle;

			neighbour cur = edges;
			double acur = cur.angle;
			do {
				neighbour next = cur.next;
				double ncur = next.angle;
				
				boolean here = false;

				if( ncur > acur ) {
					here = ( (acur <= a) && (a <= ncur) );
				} else {
					here = ( (acur <= a) || (a <= ncur) );
				}

				if( here ) {
					// insert between cur and next.
					n.next = next;
					cur.next = n;
					++num_edges;
					return;
				}

				acur = ncur;
				cur = next;
			} while( cur != edges );

			/*
			 * This shouldn't happen. 
			 */
			throw new InternalError( 
				"Unable to insert Edge in geometry.Vertex" );
		}
	}

	/*
	 * Removing is always easier than adding.  Just splice
	 * the edge out of the list.
	 */
	final void removeEdge( Edge edge )
	{
		if( edges == null ) {
			return;
		} else {
			neighbour cur = edges;
			do {
				neighbour next = cur.next;
				
				if( next.edge.equals( edge ) ) {
					if( num_edges == 1 ) {
						// alone in the list.
						edges = null;
						num_edges = 0;
						return;
					} else {
						// You know, it's so nice to not have to
						// worry about memory management.
						cur.next = next.next;
						edges = cur;
						--num_edges;
						return;
					}
				}

				cur = next;
			} while( cur != edges );
		}
	}

	/*
	 * Apply a transform.  Recalculate all the angles.  The order
	 * doesn't change, although the list might need to get reversed
	 * if the transform flips.  FIXME -- make this work.
	 * 
	 * Fortunately, the rigid motions we'll apply in Islamic design
	 * won't contain flips.  So we're okay for now.
	 */
	final void applyRigidMotion( Transform T )
	{
		pos = T.apply( pos );

		if( edges == null ) {
			return;
		} else {
			neighbour cur = edges;
			do {
				cur.recalcAngle( this );
				cur = cur.next;
			} while( cur != edges );
		}
	}

	/*
	 * When an edge is split in the map, this vertex's adjacency list
	 * needs to be updated -- some neighbour will now have a new
	 * edge instance to represent the new half of an edge that was 
	 * created.  Do a hacky rewrite here.
	 */
	void swapEdge( Vertex other, Edge nedge )
	{
		neighbour n = findNeighbour( other );
		/*
		if( n == null ) {
			System.err.println( "bleah." );
		}
		*/
		n.edge = nedge;
	}

	/*
	 * A test of the pseudoAtan2() function.
	 */
	public static final void main( String[] args )
	{
		for( int idx = 0; idx < 100; ++idx ) {
			double ang = ((double)idx / 100.0) * 2.0 * Math.PI;
			double x = Math.cos( ang );
			double y = Math.sin( ang );

			double pa = neighbour.pseudoAtan2( x, y );
			System.out.println( pa );
		}
	}
}
