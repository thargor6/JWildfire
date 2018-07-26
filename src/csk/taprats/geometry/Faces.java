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
 * Faces.java
 *
 * Here's where I pay the price for not implementing a complete
 * doubly-connected edge list data structure.  When some piece
 * of code finally wants to operate on the faces of a planar map,
 * they aren't there.
 *
 * This class contains the algorithm to extract a collection of
 * faces from a planar map.  It actually does a bit more than that.
 * It assume the map represents a checkerboard diagram (i.e., every
 * vertex has even degree except possibly for some at the borders
 * of the map) and returns two arrays of faces, representing a 
 * two-colouring of the map.  The algorithm returns polygons because
 * the only code that cares about faces doesn't need graph information
 * about the faces, just the coordinates of their corners.
 */

package csk.taprats.geometry;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Stack;

class face_info
{
	static final int UNDONE 	= 0;
	static final int PROCESSING = 1;
	static final int DONE_BLACK = 2;
	static final int DONE_WHITE = 3;

	Vector 	face;
	int 	state;

	face_info( Vector face, int state )
	{
		this.face = face;
		this.state = state;
	}
}

public class Faces
{
	/* 
	 * Walk from a vertex along an edge, always taking the
	 * current edge's neighbour at every vertex.  The set of
	 * vertices encountered determines a face.
	 */
	private static final Vector extractFace( Vertex from, Edge edge )
	{
		Vector ret = new Vector();

		Vertex cur = from;
		Edge ecur = edge;

		while( true ) {
			ret.addElement( cur );
			Vertex n = ecur.getOther( cur );

			if( n.numNeighbours() < 2 ) {
				return null;
			}

			Edge[] ba = n.getBeforeAndAfter( ecur );

			cur = n;
			ecur = ba[0];

			if( cur.equals( from ) ) {
				return ret;
			}
		}
	}

	/*
	 * Find the face_info that lies across the given edge.  Used to
	 * propagate the search to adjacent faces, giving them opposite colours.
	 */
	private static final face_info getTwin( face_info fi, int idx, 
		Hashtable v1, Hashtable v2 )
	{
		Vertex from = (Vertex)( fi.face.elementAt( idx ) );
		Vertex to = (Vertex)( fi.face.elementAt( (idx+1)%fi.face.size() ) );
		Edge conn = from.getNeighbour( to );

		if( conn.getV1().equals( from ) ) {
			return (face_info)( v2.get( conn ) );
		} else {
			return (face_info)( v1.get( conn ) );
		}
	}

	/* Is a polygon (given here as a vector of Vertex instances) clockwise?
	 * We can answer that question by finding a spot on the polygon where
	 * we can distinguish inside from outside and looking at the edges
	 * at that spot.  In this case, we look at the vertex with the
	 * maximum X value.  Whether the polygon is clockwise depends on whether
	 * the edge leaving that vertex is to the left or right of the edge
	 * entering the vertex.  Left-or-right is computed using the sign of
	 * the cross product.
	 */
	private static final boolean isClockwise( Vector pts )
	{
		int sz = pts.size();

		// First, find the vertex with the greatest X coordinate.

		int imax = 0;
		double xmax = ((Vertex)( pts.elementAt( 0 ))).getPosition().getX();

		for( int idx = 1; idx < sz; ++idx ) {
			double x = ((Vertex)( pts.elementAt( idx ))).getPosition().getX();
			if( x > xmax ) {
				imax = idx;
				xmax = x;
			}
		}

		Point pmax = ((Vertex)( pts.elementAt( imax ) )).getPosition();
		Point pnext = ((Vertex)(pts.elementAt((imax+1)%sz))).getPosition();
		Point pprev = ((Vertex)(pts.elementAt((imax+sz-1)%sz))).getPosition();

		Point dprev = pmax.subtract( pprev );
		Point dnext = pnext.subtract( pmax );

		return dprev.cross( dnext ) < 0.0;
	}

	public static final void handleVertex( Vertex vert, Edge edge,
		Hashtable v1, Hashtable v2, Vector faces )
	{
		Vector fc = extractFace( vert, edge );
		if( fc == null ) {
			return;
		}
		face_info fi = new face_info( fc, face_info.UNDONE );

		// This algorithm doesn't distinguish between clockwise and
		// counterclockwise.  So every map will produce one extraneous
		// face, namely the countour that surrounds the whole map.
		// By the nature of extractFace, the surrounding polygon will
		// be the only clockwise polygon in the set.  So check for
		// clockwise and throw it away.
		if( !isClockwise( fc ) ) {
			faces.addElement( fi );
		}

		for( int v = 0; v < fc.size(); ++v ) {
			Vertex from = (Vertex)( fc.elementAt( v ) );
			Vertex to = (Vertex)( fc.elementAt( (v+1)%fc.size() ) );
			Edge conn = from.getNeighbour( to );

			if( conn.getV1().equals( from ) ) {
				v1.put( conn, fi );
			} else {
				v2.put( conn, fi );
			}
		}
	}

	/*
	 * The main interface to this file.  Given a map and two 
	 * empty vectors, this function fills the vectors with arrays
	 * of points corresponding to a two-colouring of the faces of
	 * the map.
	 */
	public static final void extractFaces( Map map, Vector blk, Vector wht )
	{
		Hashtable v1 = new Hashtable();
		Hashtable v2 = new Hashtable();
		Vector faces = new Vector();

		// First, build all the faces.
		
		for( Enumeration e = map.getEdges(); e.hasMoreElements(); ) {
			Edge edge = (Edge)( e.nextElement() );

			if( !v1.containsKey( edge ) ) {
				handleVertex( edge.getV1(), edge, v1, v2, faces );
			}
			if( !v2.containsKey( edge ) ) {
				handleVertex( edge.getV2(), edge, v1, v2, faces );
			}
		}

		
		// Now propagate colours using a DFS.

		int num_faces = faces.size();
		Stack st = new Stack();

		face_info fi = (face_info)( faces.elementAt( 0 ) );
		st.push( fi );
		fi.state = face_info.PROCESSING;

		while( !st.empty() ) {
			fi = (face_info)( st.pop() );

			int sz = fi.face.size();
			int col = -1;

			for( int idx = 0; idx < sz; ++idx ) {
				face_info nfi = getTwin( fi, idx, v1, v2 );

				if( nfi == null ) {
					continue;
				}

				switch( nfi.state ) {
				case face_info.UNDONE:
					nfi.state = face_info.PROCESSING;
					st.push( nfi );
					break;
				case face_info.PROCESSING:
					break;
				case face_info.DONE_BLACK:
					if( col == 0 ) {
						throw new InternalError( "Filling problem 1" );
					} 
					col = 1;
					break;
				case face_info.DONE_WHITE:
					if( col == 1 ) {
						throw new InternalError( "Filling problem 2" );
					} 
					col = 0;
					break;
				}
			}

			if( col == 1 ) {
				fi.state = face_info.DONE_WHITE;
			} else {
				fi.state = face_info.DONE_BLACK;
			}
		}

		// Finally, turn all the face_info objects into arrays of points.

		for( int idx = 0; idx < faces.size(); ++idx ) {
			fi = (face_info)( faces.elementAt( idx ) );

			Point[] pts = new Point[ fi.face.size() ];
			for( int v = 0; v < fi.face.size(); ++v ) {
				pts[ v ] = ((Vertex)fi.face.elementAt(v)).getPosition();
			}

			if( fi.state == face_info.DONE_WHITE ) {
				wht.addElement( pts );
			} else if( fi.state == face_info.DONE_BLACK ) {
				blk.addElement( pts );
			} else {
				// This shouldn't happen.
			}
		}
	}
}
