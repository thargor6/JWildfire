/*
 * taprats -- an interactive design tool for computer-generated 
 *            Islamic patterns.
 *
 * Copyright (C) 2000 Craig S. Kaplan, all rights reserved
 *
 * email: csk@cs.washington.edu
 * www:   http://www.cs.washington.edu/homes/csk/taprats/
 *
 * You may not copy, redistribute or reuse this source code at
 * this time.  It is likely that in the future I will make the
 * source more freely available.  In the meantime, please be
 * patient, and contact me if you have questions about the use
 * of this source code or the compiled applet.
 *
 */

/*
 * FeatureView.java
 *
 * It's unlikely this file will get used in the applet.  A FeatureView
 * is a special kind of GeoView that assumes a subclass will maintain
 * a collection of Features.  It knows how to draw Features quickly,
 * and provides a bunch of services to subclasses for mouse-based
 * interaction with features.
 */

package csk.taprats.tile;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Enumeration;

import csk.taprats.geometry.*;

import csk.taprats.geometry.Polygon;
import csk.taprats.geometry.Point;
import csk.taprats.toolkit.GeoTransform;
import csk.taprats.toolkit.GeoView;


public class FeatureView
	extends GeoView
{
	// The default colour of the interior of a polygon.
	protected static Color pcol;
	
	 ArrayList< Primitive > primitives = new ArrayList<Primitive>();

	FeatureView( double l, double t, double w )
	{
		super( l, t, w );
	}

	 void drawFeature( GeoTransform gt, PlacedFeature pf, double icol )
	{
		Feature f = pf.getFeature();


		Transform T = gt.getTransform().compose( pf.getTransform() );

		// Polygon pgon = f.getPolygon();
		Point[] pts = f.getPoints();

		int sz = pts.length;

		int[] xpts = new int[ sz ];
		int[] ypts = new int[ sz ];

		Point v;
		Point cent = new Point( 0.0, 0.0 );

		for( int i = 0; i < sz; ++i ) {
			v = T.apply( pts[ i ] );
			xpts[ i ] = (int)( v.getX() );
			ypts[ i ] = (int)( v.getY() );

			// Also build the center.
			cent.setX( cent.getX() + v.getX() );
			cent.setY( cent.getY() + v.getY() );
		}

		/*
		for( int i = 0; i < sz; ++i ) {
		   System.out.println("Point : {" + i + " : (" + pts[i].getX() + " , " + pts[i].getY() + " )");
		}
		*/
	//	g.setColor( icol );
	//	g.fillPolygon( xpts, ypts, sz );
		drawPolygon( pts, 0, sz, true, icol );
	//	g.setColor( Color.black );
	//	g.drawPolygon( xpts, ypts, sz );
		drawPolygon( pts, 0, sz, false, icol );


	}

	/*
	 * Subclasses are expected to override the following two methods
	 * to point to their own Feature collections.  Really, these should
	 * probably be abstract methods.  I guess I wanted the class to
	 * stand on its own for some unknown reason.
	 */
	public int numFeatures()
	{
		return 0;
	}
	public PlacedFeature getFeature( int idx ) 
	{
		return null;
	}

	static Point featureCenter( Feature f )
	{
		Polygon pgon = f.getPolygon();

		double x = 0.0;
		double y = 0.0;

		for( int v = 0; v < pgon.numVertices(); ++v ) {
			Point a = pgon.getVertex( v );

			x += a.getX();
			y += a.getY();
		}

		double nv = (double)pgon.numVertices();
		return new Point( x / nv, y / nv );
	}

	Selection findFeature( Point spt )
	{
		Point wpt = screenToWorld( spt );

		// Of course, find receivers in reverse order from draw order.
		// Much more intuitive this way.
		for( int idx = numFeatures() - 1; idx >= 0; --idx ) {
			PlacedFeature pf = getFeature( idx );
			Transform T = pf.getTransform();
			Feature f = pf.getFeature();

			Polygon pgon = f.getPolygon();
			pgon.applyTransform( T );

			/*
			Point sc = worldToScreen( T.apply( featureCenter( f ) ) );

			if( spt.dist2( sc ) < 49.0 ) {
				return new Selection( idx, 0 );
			}
			*/

			if( pgon.containsPoint( wpt ) ) {
				return new Selection( idx, 0 );
			}
		}

		return null;
	}

	Selection findVertex( Point spt )
	{
		for( int idx = numFeatures() - 1; idx >= 0; --idx ) {
			PlacedFeature pf = getFeature( idx );
			Transform T = pf.getTransform();
			Feature f = pf.getFeature();
			Polygon pgon = f.getPolygon();

			for( int v = 0; v < pgon.numVertices(); ++v ) {
				Point a = T.apply( pgon.getVertex( v ) );
				Point sa = worldToScreen( a );

				if( spt.dist2( sa ) < 49.0 ) {
					return new Selection( idx, v );
				}
			}
		}

		return null;
	}

	Selection findEdge( Point spt )
	{
		for( int idx = numFeatures() - 1; idx >= 0; --idx ) {
			PlacedFeature pf = getFeature( idx );
			Transform T = pf.getTransform();
			Feature f = pf.getFeature();
			Polygon pgon = f.getPolygon();

			for( int v = 0; v < pgon.numVertices(); ++v ) {
				Point a = T.apply( pgon.getVertex( v ) );
				Point b = T.apply( 
					pgon.getVertex( (v+1) % pgon.numVertices() ) );

				Point sa = worldToScreen( a );
				Point sb = worldToScreen( b );

				if( spt.distToLine( sa, sb ) < 7.0 ) {
					return new Selection( idx, v );
				}
			}
		}

		return null;
	}

	static {
		pcol = new Color( 0.85f, 0.85f, 1.0f );
	}
	
	public void	drawPolygon( Point[] pts, int idx1, int idx2, boolean fill, double color )
	{
	  int i1,i2,istart;	
	  double x1,y1,x2,y2;
	  
	  if(fill)   // draw solid polygons    		  
		drawTrianPolygon(pts,idx1,idx2,color);  
	  else
	  { //  draw lines only
		istart=idx1;
		for (int i=idx1;i<idx2;i++)
		{	
			i1=i;
            x1=pts[i1].getX();
			y1=pts[i1].getY();
			i2=i+1;
			if(i2==idx2)
				i2=istart;
			x2=pts[i2].getX();
			y2=pts[i2].getY();
            primitives.add(new Line(x1,y1,x2,y2,color));
		}
	  }
	}
	
	public void drawTrianPolygon(Point[] pts,int idx1, int idx2, double color)
	{
		Point p1,p2,p3;
		ArrayList<Point> result;
		
	  result=new ArrayList<Point>();
      ArrayList<Point> polygon = new ArrayList<Point>(pts.length);
	  
      for (int i=idx1;i<idx2;i++) {
		    polygon.add(pts[i]);
	  }

	  //  Invoke the triangulator to triangulate this polygon.
	  Triangulate.Process(polygon, result);
	  int tcount = result.size() / 3;

	  for (int i = 0; i < tcount; i++)
	  {
		  p1 = result.get(i * 3 + 0);
		  p2 = result.get(i * 3 + 1);
		  p3 = result.get(i * 3 + 2);
		  Triangle triangle= new Triangle(p1,p2,p3,color);
		  primitives.add(triangle);
	  }
	}
	
	public ArrayList<Primitive> getPrimitives()
	{
		return primitives;
	}

}
