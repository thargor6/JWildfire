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
 * Infer.java
 *
 * Infer is the black magic part of the Islamic design system.  Given 
 * an empty, irregular feature, it infers a map for that feature by 
 * extending line segments from neighbouring features.  For many common
 * Islamic designs, this extension is natural and easy to carry out.
 * Of course, because this design tool lets you explore designs that
 * were never drawn in the Islamic tradition, there will be cases where
 * it's not clear what to do.  There will, in fact, be cases where
 * natural extensions of line segments don't exist.  So Infer has to
 * have black magic built-in.  It has to use heuristics to construct
 * plausible inferred maps, or at least maps that don't break the rest
 * of the system.
 *
 * If you're using Taprats and experiencing weird behaviour, it's 
 * probably coming from in here.  But you knew what you were infer
 * when you started using Taprats (sorry -- couldn't resist the pun).
 */

package csk.taprats.app;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;

import csk.taprats.tile.*;
import csk.taprats.geometry.*;

import csk.taprats.general.Loose;

class placed_points
{
	Feature 	feature;		
	Transform 	T;

	// The transformed midpoints of the feature's edges.
	// Since the tilings are edge to edge, a tile edge can
	// be uniquely identified by its midpoint.  So we can 
	// compare two edges by just comparing their midpoints
	// instead of comparing the endpoints pairwise.
	Point[] 	mids;

	placed_points( Feature feature, Transform T, Point[] mids )
	{
		this.feature = feature;
		this.mids = mids;
		this.T = T;
	}
}

// Information about what feature and edge on that feature is adjacent
// to a given edge on a given feature.

class adjacency_info
{
	Feature 	feature;
	int			edge;
	Transform	T;

	adjacency_info( Feature feature, int edge, Transform T )
	{
		this.feature = feature;
		this.edge = edge;
		this.T = T;
	}
}

// The information about one point of contact on the boundary of the
// region being inferred.

class contact
{
	Point 		position;
	Point		other;		
	Point 		end;

	Point 		isect;
	int			isect_idx;

	int 		colinear;

	boolean 	taken;

	static final int COLINEAR_NONE = 0;
	static final int COLINEAR_MASTER = 1;
	static final int COLINEAR_SLAVE = 2;

	contact( Point position, Point other )
	{
		this.position = position;
		this.other = other;

		this.end = position.add( 
			position.subtract( other ).normalize().scale( 100.0 ) );

		this.isect = null;
		this.taken = false;

		this.isect_idx = -1;

		this.colinear = COLINEAR_NONE;
	}

	public String toString()
	{
		return position.toString() + " from " + other.toString();
	}
}

public class Infer
{
	private Tiling 			tiling;
	private Hashtable		maps;
	private placed_points[]	placed;

	// The different kinds of connections that can be made between
	// contacts, in increasing order of badness.  This is used to 
	// compare two possible connections.

	private static final int INSIDE_EVEN 		= 0;
	private static final int INSIDE_COLINEAR 	= 1;
	private static final int INSIDE_UNEVEN 		= 2;
	private static final int OUTSIDE_EVEN 		= 3;
	private static final int OUTSIDE_UNEVEN 	= 4;
	private static final int NONE 				= 5;

	public Infer( Prototype proto )
	{
		this.tiling = proto.getTiling();
		this.maps = new Hashtable();

		// Get a map for each feature in the prototype.
		// FIXME -- this again is inefficient because we'll see some
		// features more than once.
		for( Enumeration e = proto.getFeatures(); e.hasMoreElements(); ) {
			Feature feature = (Feature)( e.nextElement() );
			Figure figure = proto.getFigure( feature );

			maps.put( feature, figure.getMap() );
		}

		// I'm going to generate all the tiles in the translational units
		// (x,y) where -1 <= x, y <= 1.  This is guaranteed to surround
		// every feature in the (0,0) unit by tiles.  You can then get
		// a sense of what other tiles surround a tile on every edge.

		placed = new placed_points[ tiling.numFeatures() * 9 ];

		int count = 0;

		for( int y = -1; y <= 1; ++y ) {
			for( int x = -1; x <= 1; ++x ) {
				count = add( x, y, count );
			}
		}
	}

	/* 
	 * The next three routines create placed_points instances for all
	 * the tiles in the nine translational units generated above.
	 */

	private int add( Transform T, Feature feature, int count )
	{
		int sz = feature.numPoints();
		Point[] fpts = T.apply( feature.getPoints() );
		Point[] mids = new Point[ sz ];

		for( int idx = 0; idx < sz; ++idx ) {
			mids[ idx ] = fpts[idx].convexSum( fpts[(idx+1)%sz], 0.5 );
		}

		placed[ count ] = new placed_points( feature, T, mids );
		return count + 1;
	}

	private int add( Transform T, int count )
	{
		for( int idx = 0; idx < tiling.numFeatures(); ++idx ) {
			PlacedFeature pf = (PlacedFeature)( tiling.getFeature( idx ) );
			Transform Tf = pf.getTransform();
			Feature feature = pf.getFeature();

			count = add( T.compose( Tf ), feature, count );
		}

		return count;
	}

	private int add( int t1, int t2, int count )
	{
		Transform T = Transform.translate( 
			tiling.getTrans1().scale( (double)t1 ).add(
				tiling.getTrans2().scale( (double)t2 ) ) );
		return add( T, count );
	}

	/*
	 * Choose an appropriate transform of the feature to infer, i.e.
	 * one that is surrounded by other features.  That means that we
	 * should just find an instance of that feature in the (0,0) unit.
	 */
	private int findPrimaryFeature( Feature feature )
	{
		// The start and end of the tiles in the (0,0) unit.
		int start = tiling.numFeatures() * 4;
		int end = tiling.numFeatures() * 5;
		int cur = -1;

		for( int idx = start; idx < end; ++idx ) {
			placed_points pp = placed[ idx ];

			if( pp.feature.equals( feature ) ) {
				cur = idx;
				break;
			}
		}

		if( cur == -1 ) {
			throw new InternalError( "Couldn't find feature in (0,0) unit!" );
		}

		return cur;
	}

	/*
	 * For this edge of the feature being inferred, find the edges of
	 * neighbouring features and store.
	 */
	private adjacency_info[] getAdjacencies( placed_points pp, int main_idx )
	{
		adjacency_info[] ret = new adjacency_info[ pp.feature.numPoints() ];

		for( int v = 0; v < pp.mids.length; ++v ) {
			Point pv = pp.mids[ v ];

			// System.err.println( "Searching for adjacency for " + pv );

			outer: for( int idx = 0; idx < placed.length; ++idx ) {
				if( idx == main_idx ) {
					continue;
				}

				placed_points pcur = placed[ idx ];

				for( int ov = 0; ov < pcur.mids.length; ++ov ) {
					if( Loose.equals( pcur.mids[ ov ], pv ) ) {
						// This is it.
						ret[ v ] = new adjacency_info( 
							pcur.feature, ov, pcur.T );
						break outer;
					}
				}
			}

			if( ret[ v ] == null ) {
				// This happens if the surrounding translational units
				// were inadequate to surround some feature with other
				// tiles.
				throw new InternalError( "Couldn't find adjacent feature!" );
			}
		}

		return ret;
	}

	/*
	 * debug_contacts is a quick helper class for viewing the transformed
	 * feature along with the contacts along its boundary.
	 */
	class debug_contacts
		extends csk.taprats.toolkit.GeoView
	{
		Point[] pts;
		Vector contacts;

		debug_contacts( Point[] pts, Vector contacts )
		{
			super( -2.0, 2.0, 4.0 );
			setSize( 400, 400 );
			this.pts = pts;
			this.contacts = contacts;
		}

		public void redraw( csk.taprats.toolkit.GeoGraphics gg )
		{
			gg.setColor( java.awt.Color.black );
			for( int idx = 0; idx < pts.length; ++idx ) {
				gg.drawLine( pts[ idx ], pts[ (idx+1) % pts.length ] );
			}
			gg.setColor( java.awt.Color.blue );
			for( int idx = 0; idx < contacts.size(); ++idx ) {
				contact c = (contact)( contacts.elementAt( idx ) );
				gg.drawLine( c.other, c.position );
			}
		}
	}

	/*
	 * Take the adjacencies and build a list of contacts by looking at
	 * vertices of the maps for the adjacent features that lie on the 
	 * boundary with the inference region.
	 */
	private Vector buildContacts( placed_points pp, adjacency_info[] adjs )
	{
		Vector ret = new Vector();

		Point[] fpts = pp.T.apply( pp.feature.getPoints() );
		
		// Get the transformed map for each adjacent feature.  I'm surprised
		// at how fast this ends up being!
		Map[] amaps = new Map[ adjs.length ];
		for( int idx = 0; idx < amaps.length; ++idx ) {
			Map fig = (Map)( maps.get( adjs[ idx ].feature ) );
			fig = (Map)( fig.clone() );
			fig.transformMap( adjs[ idx ].T );
			amaps[ idx ] = fig;
		}

		// Now, for each edge in the transformed feature, find a (hopefully
		// _the_) vertex in the adjacent map that lies on the edge.  When
		// a vertex is found, all (hopefully _both_) the edges incident 
		// on that vertex are added as contacts.

		for( int idx = 0; idx < fpts.length; ++idx ) {
			Point a = fpts[ idx ];
			Point b = fpts[ (idx+1) % fpts.length ];

			Map map = amaps[ idx ];

			for( Enumeration v = map.getVertices(); v.hasMoreElements(); ) {
				Vertex vert = (Vertex)( v.nextElement() );
				Point pos = vert.getPosition();
				if( Loose.zero( pos.distToLine( a, b ) ) && 
						(pos.dist2(a) > Loose.TOL2) ) {
					// This vertex lies on the edge.  Add all its edges
					// to the contact list.
					for( Enumeration e = vert.neighbours(); 
							e.hasMoreElements(); ) {
						Edge edge = (Edge)( e.nextElement() );
						Vertex overt = edge.getOther( vert );
						Point opos = overt.getPosition();

						ret.addElement( new contact( pos, opos ) );
					}
				}
			}
		}

/*
		csk.taprats.toolkit.Util.openTestFrame(
			new debug_contacts( fpts, ret ) );
*/

		return ret;
	}

	private static boolean isColinear( Point p, Point q, Point a )
	{
		double px = p.getX();
		double py = p.getY();

		double qx = q.getX();
		double qy = q.getY();

		double x = a.getX();
		double y = a.getY();

		double left = (qx-px)*(y-py);
		double right = (qy-py)*(x-px);

		return Loose.equals( left, right );
	}

	public Map infer( Feature feature )
	{
		// Get the index of a good transform for this feature.
		int cur = findPrimaryFeature( feature );
		placed_points pmain = placed[ cur ];
		Point[] fpts = pmain.T.apply( pmain.feature.getPoints() );

		adjacency_info[] adjs = getAdjacencies( pmain, cur );
		Vector cons = buildContacts( pmain, adjs );

		// For every contact, if it hasn't found an extension,
		// Look at all other contacts for likely candidates.
		for( int idx = 0; idx < cons.size(); ++idx ) {
			contact con = (contact)( cons.elementAt( idx ) );
			if( con.taken ) {
				continue;
			}
			
			int jbest = -1;

			Point bestisect = null;
			double bestdist = 0.0;
			int bestkind = NONE;

			for( int jdx = 0; jdx < cons.size(); ++jdx ) {
				Point isect = null;

				if( jdx == idx ) {
					continue;
				}

				contact ocon = (contact)( cons.elementAt( jdx ) );

				if( ocon.taken ) {
					continue;
				}

				// Don't try on two contacts that involve the same vertex.
				if( Loose.equals( con.position, ocon.position ) ) {
					continue;
				}

				double mydist = 0.0;
				int mykind = NONE;

				// First check if everybody's colinear.
				if( isColinear( con.other, con.position, ocon.position ) &&
						isColinear( con.position, ocon.position, ocon.other )) {

					// The two segments have to point at each other.
					Point d1 = con.position.subtract( con.other );
					Point d2 = ocon.position.subtract( ocon.other );
					Point dc = con.position.subtract( ocon.position );

					if( d1.dot( d2 ) > 0.0 ) {
						// They point in the same direction.
						continue;
					}

					// The first segment must point at the second point.
					if( d1.dot( dc ) > 0.0 ) {
						continue;
					}
					if( d2.dot( dc ) < 0.0 ) {
						continue;
					}

					mykind = INSIDE_COLINEAR;
					mydist = con.position.dist( ocon.position );
				} else {
					isect = Intersect.getTrueIntersection( 
						con.position, con.end, ocon.position, ocon.end );
					if( isect != null ) {
						// We don't want the case where the intersection
						// lies too close to either vertex.  Note that
						// I think these checks are subsumed by 
						// getTrueIntersection.
						if( Loose.equals( isect, con.position ) ) {
							continue;
						}
						if( Loose.equals( isect, ocon.position ) ) {
							continue;
						}

						double dist = con.position.dist( isect );
						double odist = ocon.position.dist( isect );

						boolean inside = Polygon.pointInPoly( 
							fpts, 0, fpts.length, isect );
						
						if( !Loose.equals( dist, odist ) ) {
							if( inside ) {
								mykind = INSIDE_UNEVEN;
							} else {
								mykind = OUTSIDE_UNEVEN;
							}
							mydist = Math.abs( dist - odist );
						} else {
							if( inside ) {	
								mykind = INSIDE_EVEN;
							} else {
								mykind = OUTSIDE_EVEN;
							}
							mydist = dist;
						}
					} else {
						continue;
					}
				}

				if( lexCompareDistances( 
						mykind, mydist, bestkind, bestdist ) < 0 ) {
					/*
					System.err.println( "New best:" );
					System.err.println( "\t" + con );
					System.err.println( "\t" + ocon );
					*/

					jbest = jdx;
					bestkind = mykind;
					bestdist = mydist;
					bestisect = isect;
				}
			}

			if( jbest == -1 ) {
				// System.err.println( "Couldn't find a match." );
				continue;
			}

			contact ocon = (contact)( cons.elementAt( jbest ) );
			con.taken = true;
			ocon.taken = true;

			if( bestkind == INSIDE_COLINEAR ) {
				// System.err.println( "best is colinear" );
				con.colinear = contact.COLINEAR_MASTER;
				ocon.colinear = contact.COLINEAR_SLAVE;
			} else {
			/*
				System.err.println( "isect: " + bestisect );
				System.err.println( "\t" + con );
				System.err.println( "\t" + ocon );
			*/
				con.isect = bestisect;
				ocon.isect = bestisect;
			}

			con.isect_idx = jbest;
			ocon.isect_idx = idx;
		}

		// Using the stored intersections in the contacts, 
		// build a final inferred map.

		Map ret = new Map();

		for( int idx = 0; idx < cons.size(); ++idx ) {
			contact con = (contact)( cons.elementAt( idx ) );

			if( con.isect == null ) {
				if( con.colinear == contact.COLINEAR_MASTER ) {
					Map tmap = new Map();
					Vertex v1 = tmap.insertVertex( con.position );
					Vertex v2 = tmap.insertVertex( ((contact)( 
						cons.elementAt( con.isect_idx ) )).position );
					tmap.insertEdge( v1, v2 );
					ret.mergeMap( tmap, true );
				}
			} else {
				Map tmap = new Map();
				Vertex v1 = tmap.insertVertex( con.position );
				Vertex v2 = tmap.insertVertex( con.isect );
				tmap.insertEdge( v1, v2 );
				ret.mergeMap( tmap, true );
			}

		}

		// Try to link up unlinked edges.
		double minlen = fpts[0].dist( fpts[fpts.length-1] );
		for( int idx = 1; idx < fpts.length; ++idx ) {
			minlen = Math.min( minlen, fpts[idx-1].dist( fpts[ idx ] ) );
		}

		for( int idx = 0; idx < cons.size(); ++idx ) {
			contact con = (contact)( cons.elementAt( idx ) );
			if( con.isect_idx == -1 ) {
				for( int jdx = 0; jdx < cons.size(); ++jdx ) {
					if( jdx == idx ) {
						continue;
					}
					contact ocon = (contact)( cons.elementAt( jdx ) );
					if( ocon.isect_idx != -1 ) {
						continue;
					}

					// Two unmatched edges.  match them up.
					Map t = new Map();
					Point ex1 = con.position.add( 
						con.position.subtract(con.other).normalize().scale(minlen*0.5));
					Point ex2 = ocon.position.add( 
						ocon.position.subtract(ocon.other).normalize().scale(minlen*0.5));

					Vertex vx1 = t.insertVertex( ex1 );
					Vertex vx2 = t.insertVertex( ex2 );

					Vertex c = t.insertVertex( con.position );
					Vertex oc = t.insertVertex( ocon.position );

					t.insertEdge( c, vx1 );
					t.insertEdge( vx1, vx2 );
					t.insertEdge( vx2, oc );

					ret.mergeMap( t, true );

					con.isect_idx = jdx;
					ocon.isect_idx = idx;
				}
			}
		}

		ret.transformMap( pmain.T.invert() );

/*
		if( !ret.verify() ) {
			csk.taprats.ui.MapViewer mv = new csk.taprats.ui.MapViewer( 
				-5.0, 5.0, 10.0, ret, true );
			csk.taprats.toolkit.Util.openTestFrame( mv );
		}
*/

		return ret;
	}

	private static int lexCompareDistances(
		int kind1, double dist1, int kind2, double dist2 )
	{
		if( kind1 < kind2 ) {
			return -1;
		} else if( kind1 > kind2 ) {
			return 1;
		} else {
			if( Loose.equals( dist1, dist2 ) ) {
				return 0;
			} else if( dist1 < dist2 ) {
				return -1;
			} else {
				return 1;
			}
		}
	}
}
