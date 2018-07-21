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
 * GeoGraphics.java
 *
 * A GeoGraphics instance acts like the 2D geometry version of
 * java.awt.Graphics (or, more recently, like java2d's Graphics2D class).
 * It understands how to draw a bunch of ordinary geometric primitives
 * by doing the appropriate coordinate transforms and drawing the AWT
 * versions.
 *
 * Note that circles are drawn in screen space -- circles drawn
 * from a sheared coordinate system won't show up as ellipses.  Darn.
 *
 * GeoGraphics maintains a stack of coordinate transforms, meant to
 * behave like OpenGL's matrix stack.  Makes it easy to execute a
 * bunch of primitives in some pushed graphics state.
 */

package csk.taprats.toolkit;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Image;
import java.awt.Component;
import java.util.Stack;

import csk.taprats.geometry.Point;
import csk.taprats.geometry.Transform;


public class GeoTransform
{

	private Transform		transform;
	private Stack			pushed;
    private double color;

	public GeoTransform( Transform transform )
	{

		this.transform = transform;
		this.pushed = null;

	}



	public final Transform getTransform()
	{
		return transform;
	}

	public final void drawLine( double x1, double y1, double x2, double y2 ) {
		double v1x = transform.applyX( x1, y1 );
		double v1y = transform.applyY( x1, y1 );
		double v2x = transform.applyX( x2, y2 );
		double v2y = transform.applyY( x2, y2 );

		drawLine( (int)v1x, (int)v1y, (int)v2x, (int)v2y );
	}

	public final void drawLine( Point v1, Point v2 )
	{
		drawLine( v1.getX(), v1.getY(), v2.getX(), v2.getY() );
		/*
		Point v1p = transform.apply( v1 );
		Point v2p = transform.apply( v2 );

		graphics.drawLine( 
			(int)( v1p.getX() ), (int)( v1p.getY() ),
			(int)( v2p.getX() ), (int)( v2p.getY() ) );
		*/
	}

	// Draw a thick like as a rectangle.
	public final void drawThickLine( 
		double x1, double y1, double x2, double y2, double width )
	{
		drawThickLine( new Point( x1, y1 ), new Point( x2, y2 ), width );
	}

	public final void drawThickLine( Point v1, Point v2, double width )
	{
		Point p = (v2.subtract( v1 )).perp();

		Point[] pts = { 
			v1.add( p.scale( width ) ), v1.subtract( p.scale( width ) ),
			v2.subtract( p.scale( width ) ), v2.add( p.scale( width ) ) };

		drawPolygon( pts, true );

		drawCircle( v1, width / 2.0, true );
		drawCircle( v2, width / 2.0, true );
	}

	public final void drawRect( Point topleft, double width, double height,
		boolean filled )
	{
		double x = topleft.getX();
		double y = topleft.getY();

		Point[] pts = {
			topleft, 
			new Point( x + width, y ),
			new Point( x + width, y + height ),
			new Point( x, y + height ) };
		
		drawPolygon( pts, filled );
	}

	public final void drawPolygon( Point[] pts, boolean filled )
	{
		drawPolygon( pts, 0, pts.length, filled );
	}

	public final void drawPolygon( Point[] pts, int start, int end, 
		boolean filled )
	{
		int len = end - start;
		int[] xpts = new int[ len ];
		int[] ypts = new int[ len ];

		int index = 0;
		for( int i = start; i < end; ++i ) {
			double x = pts[i].getX();
			double y = pts[i].getY();

			xpts[ index ] = (int)( transform.applyX( x, y ) );
			ypts[ index ] = (int)( transform.applyY( x, y ) );
			++index;
		}

		if( filled ) {
			//put_fillPolygon( xpts, ypts, len );
		} else {
			// put_drawPolygon( xpts, ypts, len );
		}
	}

	public final void drawPolygon( csk.taprats.geometry.Polygon pgon, 
		boolean filled )
	{
		int len = pgon.numVertices();
		int[] xpts = new int[ len ];
		int[] ypts = new int[ len ];

		for( int i = 0; i < len; ++i ) {
			Point v = pgon.getVertex( i );
			double x = v.getX();
			double y = v.getY();

			xpts[ i ] = (int)( transform.applyX( x, y ) );
			ypts[ i ] = (int)( transform.applyY( x, y ) );
		}

		if( filled ) {
			//put_fillPolygon( xpts, ypts, len );
		} else {
			//put_drawPolygon( xpts, ypts, len );
		}
	}

	public final void drawCircle( Point origin, double radius, 
		boolean filled )
	{
		Point rad = transform.apply( new Point( radius, 0.0 ) );
		Point orig = transform.apply( new Point( 0.0, 0.0 ) );
		double true_radius = rad.dist( orig );

		Point new_origin = transform.apply( origin );
		Point new_topleft = new_origin.subtract( 
			new Point( true_radius, true_radius ) );

		int r2 = (int)( true_radius * 2.0 );

		if( filled ) {
			//put_fillOval( 
			//	(int)( new_topleft.getX() ), 
			//	(int)( new_topleft.getY() ),
			//	r2, r2 );
		} else {
			//put_drawOval( 
			//	(int)( new_topleft.getX() ), 
			//	(int)( new_topleft.getY() ),
			//	r2, r2 );
		}
	}

	public final void drawCircle( Point origin, double radius )
	{
		drawCircle( origin, radius, false );
	}



	public final double getColor()
	{
		return color;
	}

	public final void setColor( double c )
	{
		this.color= c;
	}

	public final void pushAndCompose( Transform T )
	{
		if( pushed == null ) {
			pushed = new Stack();
		}

		pushed.push( transform );
		transform = transform.compose( T );
	}

	public final Transform pop()
	{
		Transform it = transform;
		transform = (Transform)( pushed.pop() );
		return it;
	}
}
