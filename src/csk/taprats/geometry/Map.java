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
 * Map.java
 *
 * The implementation of a planar map abstraction.  A planar map is
 * an (undirected) graph represented on the plane in such a way that
 * edges don't cross vertices or other edges.  
 *
 * This is one of the big daddy structures of computational geometry.
 * The right way to do it is with a doubly-connected edge list structure,
 * complete with half edges and a face abstraction.  Because I'm lazy
 * and because of the sheer coding involved, I'm doing something simpler,
 * more like an ordinary graph.  The disadvantage is that I don't maintain
 * faces explicitly, which can make face colouring for islamic patterns
 * tricky later.  But it's more tractable than computing overlays of
 * DCELs.
 */

package csk.taprats.geometry;

import java.util.Vector;
import java.util.Enumeration;

import csk.taprats.general.Loose;
import csk.taprats.general.Sort;
import csk.taprats.general.Comparison;

public class Map
	implements Cloneable
{
	private Vector		vertices;
	private Vector		edges;
    private int index;
    
	public Map()
	{
		this.vertices = new Vector();
		this.edges = new Vector();
		this.index=0;
	}

	/*
	 * Some obvious getters.
	 */

	public int numVertices()
	{
		return vertices.size();
	}

	public Enumeration getVertices()
	{
		return vertices.elements();
	}

	public int numEdges()
	{
		return edges.size();
	}

	public Enumeration getEdges()
	{
		return edges.elements();
	}

	/*
	 * Remove stuff from the map.
	 */

	public void removeEdge( Edge e )
	{
		e.getV1().removeEdge( e );
		e.getV2().removeEdge( e );
		edges.removeElement( e );
	}

	public void removeVertex( Vertex v )
	{
		for( Enumeration e = v.neighbours(); e.hasMoreElements(); ) {
			Edge edge = (Edge)( e.nextElement() );
			edge.getOther( v ).removeEdge( edge );
			edges.removeElement( edge );
		}
		vertices.removeElement( v );
	}

	public Object clone()
	{
		Map ret = new Map();

		for( Enumeration e = vertices.elements(); e.hasMoreElements(); ) {
			Vertex vert = (Vertex)( e.nextElement() );
			Vertex nv = new Vertex( ret, vert.getPosition() );

			vert.copy = nv;
			ret.vertices.addElement( nv );
		}

		for( Enumeration e = edges.elements(); e.hasMoreElements(); ) {
			Edge edge = (Edge)( e.nextElement() );

			Edge ne = new Edge( ret, edge.getV1().copy, edge.getV2().copy );

			ret.edges.addElement( ne );

			// FIXME -- this could be a bit faster.  Really we could
			// copy the order of the edges leaving a vertex wholesale.
			// I don't think it'll make that big a difference.  The
			// max expected degree of the vertices in the maps we'll be
			// dealing with is 6.

			ne.getV1().insertEdge( ne );
			ne.getV2().insertEdge( ne );
		}

		return ret;
	}

	/*
	 * Routines used for spatial sorting of edges and vertices.
	 */

	private static int lexCompareEdges( double a, double b )
	{
		double d = a - b;

		if( Loose.zero( d ) ) {
			return 0;
		} else if( d < 0.0 ) {
			return -1;
		} else {
			return 1;
		}
	}

	private static int lexComparePoints( Point a, Point b )
	{
		double dx = a.getX() - b.getX();

		if( Loose.zero( dx ) ) {
			double dy = a.getY() - b.getY();

			if( Loose.zero( dy ) ) {
				return 0;
			} else if( dy < 0.0 ) {
				return -1;
			} else {
				return 1;
			}
		} else if( dx < 0.0 ) {
			return -1;
		} else {
			return 1;
		}
	}

	private void sortVertices()
	{
		Sort.quickSort( vertices, 0, vertices.size() - 1, 
			new Comparison() {
				public int compare( Object a, Object b )
				{
					Vertex v1 = (Vertex)a;
					Vertex v2 = (Vertex)b;

					return lexComparePoints( 
						v1.getPosition(), v2.getPosition() );
				}
			} );
	}

	private void sortEdges()
	{
		Sort.quickSort( edges, 0, edges.size() - 1, 
			new Comparison() {
				public int compare( Object a, Object b )
				{
					Edge e1 = (Edge)a;
					Edge e2 = (Edge)b;

					return lexCompareEdges( e1.getMinX(), e2.getMinX() );
				}
			} );
	}

	/*
	 * Get a Map Vertex given that we're asserting the vertex 
	 * doesn't lie on an edge in the map.
	 */
	final Vertex getVertex_Simple( Point pt )
	{
		for( int idx = 0; idx < vertices.size(); ++idx ) {
			Vertex v = (Vertex)( vertices.elementAt( idx ) );
			Point cur = v.getPosition();
			int cmp = lexComparePoints( pt, cur );

			if( cmp == 0 ) {
				return v;
			} else if( cmp < 0 ) {
				Vertex vert = new Vertex( this, pt );
				vertices.insertElementAt( vert, idx );
				return vert;
			}
		}

		Vertex vert = new Vertex( this, pt );
		vertices.addElement( vert );
		return vert;
	}

	/* 
	 * Insert an edge given that we know the edge doesn't interact
	 * with other edges or vertices other than its endpoints.
	 */
	final void insertEdge_Simple( Edge edge )
	{
		double xm = edge.getMinX();

		for( int idx = 0; idx < edges.size(); ++idx ) {
			Edge e = (Edge)( edges.elementAt( idx ) );
			double xmcur = e.getMinX();

			if( lexCompareEdges( xm, xmcur ) < 0 ) {
				edges.insertElementAt( edge, idx );
				return;
			}
		}

		edges.addElement( edge );
	}

	/* 
	 * Insert the edge connecting two vertices, including updating
	 * the neighbour lists for the vertices.
	 */
	public final Edge insertEdge( Vertex v1, Vertex v2 )
	{
	/*
		* I don't want this in here because I don't like silently fixing
		* mistakes.  If someone tries to insert a trivial edge, I want
		* to solve the problem, not hide the symptom.

		if( v1.equals( v2 ) ) {
			System.err.println( "Warning - trivial edge not inserted." );
			return null;
		}
	*/

		Edge e = new Edge( this, v1, v2 );
		insertEdge_Simple( e );
		v1.insertEdge( e );
		v2.insertEdge( e );
		return e;
	}

	/* 
	 * Split any edge (there is at most one) that intersects
	 * this new vertex.  You'll want to make sure this vertex isn't
	 * a duplicate of one already in the map.  That would just
	 * make life unpleasant.
	 */
	final void splitEdgesByVertex( Vertex vert )
	{
		Point vp = vert.getPosition();
		double x = vp.getX();

		for( int idx = 0; idx < edges.size(); ++idx ) {
			Edge e = (Edge)( edges.elementAt( idx ) );
			double xm = e.getMinX();

			if( lexCompareEdges( xm, x ) > 0 ) {
				/* 
				 * The edges are sorted by xmin, and this xmin exceeds
				 * the x value of the vertex.  No more interactions.
				 */
				
				return;
			}

			Vertex v1 = e.getV1();
			Vertex v2 = e.getV2();

			if( Loose.zero( 
					vp.distToLine( v1.getPosition(), v2.getPosition() ) ) ) {
				if( Loose.zero( vp.dist( v1.getPosition() ) ) ||
						Loose.zero( vp.dist( v2.getPosition() ) ) ) {
					// Don't split if too near endpoints.
					continue;
				}

				// Create the new edge instance.
				Edge nedge = new Edge( this, vert, v2 );

				// We don't need to fix up v1 -- it can still point
				// to the same edge.

				// Fix up v2.
				v2.swapEdge( v1, nedge );

				// Fix up the edge object -- it now points to the
				// intervening edge.
				e.v2 = vert;

				// Insert the new edge.
				edges.removeElementAt( idx );
				insertEdge_Simple( nedge );
				insertEdge_Simple( e );

				// Update the adjacencies for the splitting vertex
				vert.insertEdge( e );
				vert.insertEdge( nedge );

				// That's it.
				return;
			}
		}
	}

	/*
	 * The "correct" version of inserting a vertex.  Make sure the
	 * map stays consistent.
	 */
	final Vertex getVertex_Complex( Point pt )
	{
		Vertex vert = getVertex_Simple( pt );
		splitEdgesByVertex( vert );
		return vert;
	}

	/* 
	 * The publically-accessible version.
	 */
	public final Vertex insertVertex( Point pt )
	{
		return getVertex_Complex( pt );
	}

	/*
	 * Applying a motion made up only of uniform scales and translations,
	 * Angles don't change.  So we can just transform each vertex.
	 */
	void applyTrivialRigidMotion( Transform T )
	{
		for( Enumeration e = vertices.elements(); e.hasMoreElements(); ) {
			Vertex vert = (Vertex)( e.nextElement() );
			vert.pos = T.apply( vert.getPosition() );
		}
	}

	public void scale( double s )
	{
		applyTrivialRigidMotion( Transform.scale( s ) );
	}

	public void translate( double x, double y )
	{
		applyTrivialRigidMotion( Transform.translate( x, y ) );
	}

	/*
	 * In the general case, the vertices and edges must be re-sorted.
	 */
	void applyGeneralRigidMotion( Transform T )
	{
		// Transform all the vertices.
		for( Enumeration e = vertices.elements(); e.hasMoreElements(); ) {
			Vertex vert = (Vertex)( e.nextElement() );
			vert.applyRigidMotion( T );
		}

		// Now sort everything.
		sortVertices();
		sortEdges();
	}

	public void transformMap( Transform T )
	{
		applyGeneralRigidMotion( T );
	}

	/*
	 * Transform a single vertex.
	 */
	public void transformVertex( Vertex v, Transform T )
	{
		// This isn't entirely trivial:
		// 	1. Transform the vertex itself and recalculate the neighbour 
		//     angles.
		//  2. For each vertex adjacent to this one, reinsert the connecting
		//     edge into the other vertex's neighbour list.
		//  3. Reinsert the vertex into the map's vertex list -- its
		//     x position may have changed.
		//  4. Re-sort the edge list, since edges which began at the 
		//     transformed vertex may shift in the edge list.
		//
		// Right now most of this, especially steps 3 and 4, are done
		// in the obvious, slow way.  With more careful programming, we
		// could break the process down further and do some parts faster,
		// like searching for the vertex's new position relative to its
		// current position.  At this point, however, that's not a huge
		// win.

		// Transform the position of the vertex.
		v.applyRigidMotion( T );

		// Reorganize the vertices adjacent to this vertex.
		for( Enumeration e = v.neighbours(); e.hasMoreElements(); ) {
			Edge edge = (Edge)( e.nextElement() );
			Vertex other = edge.getOther( v );

			other.removeEdge( edge );
			other.insertEdge( edge );
		}

		// This vertex's x position may have changed.  Reposition
		// it in the array of vertices.
		vertices.removeElement( v );
		Point pt = v.getPosition();

		for( int idx = 0; idx < vertices.size(); ++idx ) {
			Vertex vo = (Vertex)( vertices.elementAt( idx ) );
			Point cur = vo.getPosition();
			int cmp = lexComparePoints( pt, cur );

			if( cmp < 0 ) {
				vertices.insertElementAt( v, idx );
				return;
			}
		}

		vertices.addElement( v );

		// Sigh -- I guess resort the edges.
		sortEdges();
	}

	/*
	 * Given another vector of vertices, add them to the vertices of the
	 * current map.  We can do this in linear time with a simple merge 
	 * algorithm.  Note that we want to coalesce identical vertices to
	 * eliminate duplicates.
	 */
	private void mergeVertices( Vector your_verts )
	{
		Vector my_verts = vertices;

		int my_size = my_verts.size();
		int your_size = your_verts.size();

		vertices = new Vector( my_size + your_size );

		int my_i = 0;
		int your_i = 0;

		while( true ) {
			if( my_i == my_size ) {
				if( your_i == your_size ) {
					// done!
					return;
				} else {
					Vertex your_v = (Vertex)( your_verts.elementAt( your_i ) );
					Vertex nv = new Vertex( this, your_v.getPosition() );
					vertices.addElement( nv );
					your_v.copy = nv;
					++your_i;
				}
			} else {
				if( your_i == your_size ) {
					vertices.addElement( my_verts.elementAt( my_i ) );
					++my_i;
				} else {
					// Darn -- have to actually merge.
					Vertex my_v = (Vertex)( my_verts.elementAt( my_i ) );
					Vertex your_v = (Vertex)( your_verts.elementAt( your_i ) );
					int cmp = lexComparePoints( 
						my_v.getPosition(), your_v.getPosition() );
					
					if( cmp < 0 ) {
						// my_v goes first.
						vertices.addElement( my_v );
						++my_i;
					} else if( cmp == 0 ) {
						// It's a toss up.
						vertices.addElement( my_v );
						your_v.copy = my_v;
						++my_i;
						++your_i;
					} else if( cmp > 0 ) {
						// your_v goes first.
						Vertex nv = new Vertex( this, your_v.getPosition() );
						vertices.addElement( nv );
						your_v.copy = nv;
						++your_i;
					}
				}
			}
		}
	}

	/*
	 * Merge two maps.  The bread and butter of the Map class.  This is a 
	 * complicated computational geometry algorithm with a long and 
	 * glorious tradition :^)
 	 *
	 * The goal is to form a map from the union of the two sets of
	 * vertices (eliminating duplicates) and the union of the two sets
	 * of edges (splitting edges whenever intersections occur).
	 *
	 * There are very efficient ways to do this, reporting edge-edge
	 * intersections using a plane-sweep algorithm.  Implementing
	 * this merge code in all its glory would be too much work.  Since
	 * I have to use all my own code, I'm going to resort to a simplified
	 * (and slower) version of the algorithm.
	 */
	public void mergeMap( Map other, boolean consume )
	{
	/*
		* Sanity checks to use when debugging.
		boolean at_start = verify();
		if( !at_start ) {
			System.err.println( "Bad at start." );
		}
		if( !other.verify() ) {
			System.err.println( "Other bad at start." );
		}
	*/

		// Here's how I'm going to do this.
		//
		// 1. Check all intersections between edges from this map 
		//    and from the other map.  Record the positions of the
		//    intersections.
		//
		// 2. For each intersection position, get the vertex in both
		//    maps, forcing edges to split where the intersections will
		//    occur.
		// 
		// 3. Merge the vertices.
		//
		// 4. Add the edges in the trivial way, since now there will
		//    be no intersections.
		//
		// Yech -- pretty slow.  But I shudder at the thought of 
		// doing it with maximal efficiency.
		//
		// In practice, this routine has proven to be efficient enough
		// for the Islamic design tool.  Phew!

		// Step 0 -- setup.
		// Create a vector to hold the intersections.

		if( !consume ) {
			// Copy the other map so we don't destroy it.
			other = (Map)( other.clone() );
		}

		Vector intersections = new Vector();

		// Step 1

		for( Enumeration e = other.getEdges(); e.hasMoreElements(); ) {
			Edge edge = (Edge)( e.nextElement() );

			// To check all intersections of this edge with edges in 
			// the current map, we can use the optimization that 
			// edges are stored in sorted order by min x.  So at the
			// very least, skip the suffix of edges whose minimum x values
			// are past the max x of the edge to check.

			Point ep = edge.getV1().getPosition();
			Point eq = edge.getV2().getPosition();

			double exm = Math.max( ep.getX(), eq.getX() );

			for( Enumeration me = getEdges(); me.hasMoreElements(); ) {
				Edge cur = (Edge)( me.nextElement() );

				Point cp = cur.getV1().getPosition();
				Point cq = cur.getV2().getPosition();

				if( lexCompareEdges( cur.getMinX(), exm ) > 0 ) {
					break;
				}

				Point ipt = Intersect.getTrueIntersection( ep, eq, cp, cq );

				if( ipt != null ) {
					intersections.addElement( ipt );
				}
			}
		}

		// Step 2

		for( Enumeration e = intersections.elements(); e.hasMoreElements(); ) {
			Point p = (Point)( e.nextElement() );

			getVertex_Complex( p );
			other.getVertex_Complex( p );
		}

		// Step 3

		mergeVertices( other.vertices );

		// Step 4

		for( Enumeration e = other.edges.elements(); e.hasMoreElements(); ) {
			Edge edge = (Edge)( e.nextElement() );

			Vertex v1 = edge.getV1().copy;
			Vertex v2 = edge.getV2().copy;

			Edge ne = new Edge( this, v1, v2 );

			// Rather than using insertEdge_Simple, we tack all new edges
			// to the end of the edge list...
			edges.addElement( ne );

			v1.insertEdge( ne );
			v2.insertEdge( ne );
		}

		// ... and then sort everything together for speed.
		sortEdges();

		// I guess that's it!

/*
		if( at_start && !verify() ) {
			System.err.println( "it went bad." );
			dump( System.err );
		}
*/
	}

	public void mergeMap( Map other ) 
	{
		mergeMap( other, false );
	}

	/*
	 * A simpler merge routine that assumes that the two maps 
	 * don't interact except at vertices, so edges don't need to
	 * be checked for intersections.
	 */
	public void mergeSimple( Map other )
	{
		// System.err.println( "mergeSimple begin" );

		mergeVertices( other.vertices );

		for( Enumeration e = other.getEdges(); e.hasMoreElements(); ) {
			Edge edge = (Edge)( e.nextElement() );
			Vertex v1 = edge.getV1().copy;
			Vertex v2 = edge.getV2().copy;

			// insertEdge( edge.getV1().copy, edge.getV2().copy );
			Edge nedge = new Edge( this, v1, v2 );
			v1.insertEdge( nedge );
			v2.insertEdge( nedge );
			edges.addElement( nedge );
		}

		sortEdges();
	}

	/*
	 * It's often the case that we want to merge a transformed copy of
	 * a map into another map, or even a collection of transformed copies.
	 * Since transforming a map requires a slow cloning, we can save lots
	 * of time and memory by transforming and merging simultaneously.
	 * Here, we transform vertices as they are put into the current map.
	 */
	public void mergeSimpleMany( Map other, Vector transforms )
	{
		for( Enumeration e = transforms.elements(); e.hasMoreElements(); ) {
			Transform T = (Transform)( e.nextElement() );

			for( Enumeration v = other.getVertices(); v.hasMoreElements(); ) {
				Vertex vert = (Vertex)( v.nextElement() );
				vert.copy = getVertex_Simple( T.apply( vert.getPosition() ) );
			}

			for( Enumeration v = other.getEdges(); v.hasMoreElements(); ) {
				Edge edge = (Edge)( v.nextElement() );

				Vertex v1 = edge.getV1().copy;
				Vertex v2 = edge.getV2().copy;

				Edge nedge = new Edge( this, v1, v2 );
				v1.insertEdge( nedge );
				v2.insertEdge( nedge );
				edges.addElement( nedge );
			}
		}

		sortEdges();
	}

	public Edge getLine()
	{
		index=(int) (Math.random()*edges.size());
		Edge edge = (Edge)( edges.elementAt( index ) );
        return edge;
	}
	
	/*
	 * Print a text version of the map.
	 */
	public void dump( java.io.PrintStream ow )
	{
		ow.println( "" + vertices.size() + " vertices." );
		ow.println( "" + edges.size() + " edges.\n" );

		for( int idx = 0; idx < vertices.size(); ++idx ) {
			Vertex v = (Vertex)( vertices.elementAt( idx ) );
			ow.println( "vertex " + idx + " at " + v.getPosition() );
			for( Enumeration e = v.neighbours(); e.hasMoreElements(); ) {
				Edge edge = (Edge)( e.nextElement() );
				ow.println( "\t--> " + edges.indexOf( edge ) );
			}
		}
		for( int idx = 0; idx < edges.size(); ++idx ) {
			Edge edge = (Edge)( edges.elementAt( idx ) );
			ow.println( "edge " + idx + 
				" from " + vertices.indexOf( edge.getV1() ) +
				" to " + vertices.indexOf( edge.getV2() ) );
		}
	}

	/*
	 * A big 'ole sanity check for maps.  Throw together a whole collection
	 * of consistency checks.  When debugging maps, call early and call often.
	 *
	 * It would probably be better to make this function provide the error
	 * messages through a return value or exception, but whatever.
	 */
	public boolean verify()
	{
		boolean good = true;
		// Make sure there are no trivial edges.

		for( int idx = 0; idx < edges.size(); ++idx ) {
			Edge e = (Edge)( edges.elementAt( idx ) );
			if( e.getV1().equals( e.getV2() ) ) {
				System.err.println( "Trivial edge " + idx );
				good = false;
			}
		}

		for( Enumeration e = edges.elements(); e.hasMoreElements(); ) {
			Edge edge = (Edge)( e.nextElement() );

			// Make sure that for every edge, the edge's endpoints
			// are know vertices in the map.

			if( !vertices.contains( edge.getV1() ) ) {
				System.err.println( "edge " + 
					edges.indexOf( edge ) + " V1 not in vertex list." );
				good = false;
			}
			if( !vertices.contains( edge.getV2() ) ) {
				System.err.println( "edge " + 
					edges.indexOf( edge ) + " V2 not in vertex list." );
				good = false;
			}

			// Make sure that the edge's endpoints know about the
			// edge, and that the edge is the only connection between
			// the two endpoints.

			Edge ed = edge.getV1().getNeighbour( edge.getV2() );
			if( ed == null ) {
				System.err.println( "edge " + edges.indexOf( edge ) + 
					" not found in vertex v1 neighbours for vertex " + 
					vertices.indexOf( edge.getV1() ) );
				good = false;
			}
			if( !ed.equals( edge ) )  {
				System.err.println( "edge " + edges.indexOf( edge ) + 
					" not matched in vertex v1 neighbours for vertex " + 
					vertices.indexOf( edge.getV1() ) );
				good = false;
			}

			ed = edge.getV2().getNeighbour( edge.getV1() );
			if( ed == null ) {
				System.err.println( "edge " + edges.indexOf( edge ) + 
					" not found in vertex v2 neighbours for vertex " + 
					vertices.indexOf( edge.getV2() ) );
				good = false;
			}
			if( !ed.equals( edge ) )  {
				System.err.println( "edge " + edges.indexOf( edge ) + 
					" not matched in vertex v2 neighbours for vertex " + 
					vertices.indexOf( edge.getV2() ) );
				good = false;
			}
		}

		// Make sure the edges are in sorted order.

		for( int idx = 1; idx < edges.size(); ++idx ) {
			Edge e1 = (Edge)( edges.elementAt( idx - 1 ) );
			Edge e2 = (Edge)( edges.elementAt( idx ) );

			double e1x = e1.getMinX();
			double e2x = e2.getMinX();

			if( e1x > (e2x + Loose.TOL) ) {
				System.err.println( "Sortedness check failed for edges." );
				good = false;
			}
		}

		// Make sure the vertices are in sorted order.

		for( int idx = 1; idx < vertices.size(); ++idx ) {
			Vertex v1 = (Vertex)( vertices.elementAt( idx - 1 ) );
			Vertex v2 = (Vertex)( vertices.elementAt( idx ) );

			int cmp = lexComparePoints( v1.getPosition(), v2.getPosition() );

			if( cmp == 0 ) {
				System.err.println( "Duplicate vertices." );
				good = false;
			} else if( cmp > 0 ) {
				System.err.println( "Sortedness check failed for vertices." );
				good = false;
			}
		}

		// Other possible checks:
		//	- Make sure there's no edge from a vertex to itself.

		return good;
	}
}
