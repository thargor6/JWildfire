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
 * RenderOutline.java
 *
 * The simplest non-trivial rendering style.  RenderOutline just uses
 * some trig to fatten up the map's edges, also drawing a line-based
 * outline for the resulting fat figure.
 *
 * The same code that computes the draw elements for RenderOutline can
 * be used by other "fat" styles, such as RenderEmboss.
 */

package csk.taprats.ui1;

import java.util.ArrayList;
import java.util.Enumeration;
import java.awt.*;



import csk.taprats.toolkit.GeoGraphics;
import csk.taprats.geometry.*;

import csk.taprats.geometry.Point;


public class RenderOutline
	extends RenderStyle
{
	Map			map;
	Point[] 	pts;
	double 		width;


	protected GridBagLayout	layout;

	public RenderOutline(double width)
	{
		super();
		this.pts = null;

		this.width = width;

	}



	protected double getLineWidth()
	{
		return this.width;
	}

	/*
	 * Do a mitered join of the two fat lines (a la postscript, for example).
	 * The join point on the other side of the joint can be computed by
	 * reflecting the point returned by this function through the joint.
	 */
	static final Point getJoinPoint( 
		Point joint, Point a, Point b, double width )
	{
		double th = joint.sweep( a, b );

		if( Math.abs( th - Math.PI ) < 1e-7 ) {
			return null;
		} else {
			Point d1 = joint.subtract( a );
			d1.normalizeD();
			Point d2 = joint.subtract( b );
			d2.normalizeD();

			double l = width / Math.sin( th );
			double isx = joint.getX() - (d1.getX() + d2.getX()) * l;
			double isy = joint.getY() - (d1.getY() + d2.getY()) * l;
			return new Point( isx, isy );
		}
	}

	/*
	 * Look at a given edge and construct a plausible set of points
	 * to draw at the edge's 'to' vertex.  Call this twice to get the
	 * complete outline of the hexagon to draw for this edge.
	 */
	static final Point[] getPoints( 
		Edge edge, Vertex from, Vertex to, double width )
	{
		Point pfrom = from.getPosition();
		Point pto = to.getPosition();

		Point dir = pto.subtract( pfrom );
		dir.normalizeD();
		Point perp = dir.perp();

		int nn = to.numNeighbours();

		Point above = null;
		Point below = null;

		if( nn == 1 ) {
			below = pto.subtract( perp.scale( width ) );
			above = pto.add( perp.scale( width ) );
		} else if( nn == 2 ) {
			Edge[] ba = to.getBeforeAndAfter( edge );
			Vertex ov = ba[0].getOther( to );
			Point pov = ov.getPosition();

			Point jp = getJoinPoint( pto, pfrom, pov, width );

			if( jp == null ) {
				below = pto.subtract( perp.scale( width ) );
				above = pto.add( perp.scale( width ) );
			} else {
				below = jp;
				above = jp.convexSum( pto, 2.0 );
			}
		} else {
			Edge[] ba = to.getBeforeAndAfter( edge );
			Point before_pt = ba[0].getOther( to ).getPosition();
			Point after_pt = ba[1].getOther( to ).getPosition();

			below = getJoinPoint( pto, pfrom, after_pt, width );
			above = getJoinPoint( pto, before_pt, pfrom, width );
		}

		Point[] ret = { below, above };
		return ret;
	}

	public void setMap( Map map )
	{
		pts = null;

		this.map = map;

		if( this.map != null ) {
			double width = getLineWidth();

			pts = new Point[ map.numEdges() * 6 ];
			int index = 0;

			for( Enumeration e = map.getEdges(); e.hasMoreElements(); ) {
				Edge edge = (Edge)( e.nextElement() );
				Vertex v1 = edge.getV1();
				Vertex v2 = edge.getV2();

				Point[] top   =   getPoints( edge, v1, v2, width );
				Point[] fromp = getPoints( edge, v2, v1, width );

				pts[ index ] = top[0];
				pts[ index + 1 ] = v2.getPosition();
				pts[ index + 2 ] = top[1];
				pts[ index + 3 ] = fromp[0];
				pts[ index + 4 ] = v1.getPosition();
				pts[ index + 5 ] = fromp[1];

				index += 6;
			}
		}
	}


	public void draw( boolean fill )
	{
		Color interior = new Color( 255, 0, 255 );
	    double x,y;	
	    Point p1,p2,p3;
	    Triangle triangle;
	    
		 for(int idx=0;idx<pts.length;idx+=6)
		 {
//				gg.setColor( interior );
//				gg.drawPolygon( pts, idx, idx + 6, true );
	            drawPolygon(pts,idx,idx+6,fill,1.0);
			 /*
			    p1=new Point (pts[idx+0].getX(),pts[idx+0].getY());
			    p2=new Point (pts[idx+1].getX(),pts[idx+1].getY());
			    p3=new Point (pts[idx+2].getX(),pts[idx+2].getY());
			    triangle= new Triangle(p1,p2,p3);
			    primitives.add(triangle);
			    
			    p1=new Point (pts[idx+0].getX(),pts[idx+0].getY());
			    p2=new Point (pts[idx+2].getX(),pts[idx+2].getY());
			    p3=new Point (pts[idx+3].getX(),pts[idx+3].getY());
			    triangle= new Triangle(p1,p2,p3);
			    primitives.add(triangle);
			    
			    p1=new Point (pts[idx+0].getX(),pts[idx+0].getY());
			    p2=new Point (pts[idx+3].getX(),pts[idx+3].getY());
			    p3=new Point (pts[idx+4].getX(),pts[idx+4].getY());		
			    triangle= new Triangle(p1,p2,p3);
			    primitives.add(triangle);
			    
			    p1=new Point (pts[idx+0].getX(),pts[idx+0].getY());
			    p2=new Point (pts[idx+4].getX(),pts[idx+4].getY());
			    p3=new Point (pts[idx+5].getX(),pts[idx+5].getY());		
			    triangle= new Triangle(p1,p2,p3);
			    primitives.add(triangle);
 */
// Draw Lines
//				gg.setColor( Color.black );
//				gg.drawLine( pts[ idx + 2 ], pts[ idx + 3 ] );

				 p1= pts[idx+2];
				 p2= pts[idx+3];
			     primitives.add(new Line(p1.getX(),p1.getY(),p2.getX(),p2.getY(),0.0)); // Color=0.0
               
//				gg.drawLine( pts[ idx + 5 ], pts[ idx ] );             
			    p1= pts[idx+5];
			    p2= pts[idx];
			    primitives.add(new Line(p1.getX(),p1.getY(),p2.getX(),p2.getY(),0.0)); // Color=0.0     
		 } 
	}
	
	/*
	public void draw( GeoGraphics gg )
	{
		Color interior = new Color( 255, 0, 255 );

		gg.setColor( Color.white );
		if( pts != null ) {
			for( int idx = 0; idx < pts.length; idx += 6 ) {
				gg.setColor( interior );
				gg.drawPolygon( pts, idx, idx + 6, true );
				gg.setColor( Color.black );
				gg.drawLine( pts[ idx + 2 ], pts[ idx + 3 ] );
				gg.drawLine( pts[ idx + 5 ], pts[ idx ] );
			}
		}
	}
	*/
}
