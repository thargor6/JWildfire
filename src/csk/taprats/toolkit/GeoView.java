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
 * GeoView.java
 *
 * This class has been my Java mainstay for quite some time.  A GeoView
 * is a kind of Canvas that understands the notion of a 2D coordinate
 * system.  Subclasses of GeoView implement various interactive 2D
 * viewers.  It's sort of like having an OpenGL window with an Orthographic
 * 2D projection.
 *
 * GeoViews have all kinds of useful built-in interactions for changing
 * the viewport.  See the online documentation for more details.
 */

package csk.taprats.toolkit;

import java.awt.*;
import java.awt.image.BufferedImage;

import csk.taprats.geometry.Point;
import csk.taprats.geometry.Polygon;
import csk.taprats.geometry.Transform;
import csk.taprats.geometry.Rect;

public class GeoView

{
	private double left;
	private double top;
	private double width;

	private double theta;

	private Transform transform;
	private Transform inverse;

	private BufferedImage backing_store;

	private boolean sink;

	protected int last_x, last_y;

	protected int track_x, track_y;

	private Dimension last_size;
	
	private int d_width;
	private int d_height;
	
	GeoGraphics g2d =null;

	public GeoView( double left, double top, double width )
	{
		super();

		this.left = left;
		this.top = top;
		this.width = width;

		this.transform = null;
		this.backing_store = null;

		this.sink = false;

        this.d_width=400;
        this.d_height=400;
		this.last_size = new Dimension( d_width, d_height );
        
	}


	public void setSink( boolean sink )
	{
		this.sink = sink;
	}


	public Image paint( )
	{
		buildBackingStore();
		return  backing_store;
	}

	
	private final void buildBackingStore()
	{
		if( backing_store == null ) {
			Dimension d = new Dimension(d_width,d_height);

			backing_store = new BufferedImage( d.width, d.height, BufferedImage.TYPE_INT_RGB);

			// Since we now have a fresh backing store, redraw everything.
 		    redraw();
		}
	}
	

	public GeoGraphics getGraphics()
	{
		return g2d;
	}
	
	protected Graphics getBackGraphics()
	{
		return backing_store.getGraphics();
	}

	
	
	public boolean redraw()
	{
		if( backing_store == null ) {
			return false;
		}

		computeTransform();

		Graphics g = getBackGraphics();
		Dimension d = new Dimension(d_width,d_height);
	//	g.clearRect( 0, 0, d.width, d.height );

		g2d = new GeoGraphics( g, transform, null );

		redraw( g2d );
/*
		if( sink ) {
			int w = d.width;
			int h = d.height;

			drawH( w, 0, bevel_h_0, g );
			drawH( w, 1, bevel_h_1, g );
			drawH( w, h - 2, bevel_h_2, g );
			drawH( w, h - 1, bevel_h_3, g );

			drawV( h, 0, bevel_v[0], g );
			drawV( h, 1, bevel_v[1], g );
			drawV( h, w - 2, bevel_v[1], g );
			drawV( h, w - 1, bevel_v[2], g );
		}
*/
		return true;
	}

	public final void computeTransform()
	{
		if( transform == null ) {
			Dimension d = new Dimension(d_width,d_height);

			double wwidth = (double)( d.width );
			double wheight = (double)( d.height );
			double aspect = wwidth / wheight;

			double height = width / aspect;

			Point mid = new Point( wwidth / 2.0, wheight / 2.0 );

			// Hmmmm - because the aspect ratios are the same, I should
			// be able to simply rely on 'matchLineSegments'.

			Transform first = Transform.translate( 
				-left, -( top - height ) );
			Transform second = Transform.scale(
				wwidth / width, -( wheight / height ) );
			Transform third = Transform.translate(
				0.0, wheight );
			Transform fourth = Transform.rotateAroundPoint( mid, theta );

			transform = fourth.compose( 
				third.compose( second.compose( first ) ) );
			inverse = transform.invert();
		}
	}

	public void setSize( int w, int h )
	{
//		super.setSize( w, h );
		d_width=w;
		d_height=h;
		last_size = new Dimension( w, h );
	}

	public Dimension getPreferredSize()
	{
		return last_size;
	}

	public Dimension getMinimumSize()
	{
		return last_size;
	}

	/* END OF HACK */

	public void setBounds( double left, double top, double width )
	{
		this.left = left;
		this.top = top;
		this.width = width;
		this.transform = null;
	}

	public void setTheta( double theta )
	{
		this.theta = theta;
		this.transform = null;

	}

	protected void redraw( GeoGraphics g2d )
	{
		// Subclasses must override this method to do anything useful.
	}


	public void invalidate()
	{
		backing_store = null;
		transform = null;
	}

	public final double getLeft()
	{
		return left;
	}

	public final double getTop()
	{
		return top;
	}

	public final double getViewWidth()
	{
		return width;
	}

	public final double getTheta()
	{
		return theta;
	}

	public final Polygon getBoundary()
	{
		Dimension d =new Dimension(d_width,d_height);

		double wwidth = (double)( d.width );
		double wheight = (double)( d.height );

		Polygon ret = new Polygon( 4 );

		ret.addVertex( inverse.apply( 0.0, 0.0 ) );
		ret.addVertex( inverse.apply( wwidth, 0.0 ) );
		ret.addVertex( inverse.apply( wwidth, wheight ) );
		ret.addVertex( inverse.apply( 0.0, wheight ) );

		return ret;
	}

	public void lookAt( Rect rect )
	{
		// Leave some breathing room around the rectangle to look at.
		// Having the region of interest bleed right to edge of the 
		// window doesn't look too good.

		rect = rect.centralScale( 1.25 );

		Dimension d = new Dimension(d_width,d_height);
		Transform Tc = rect.centerInside( new Rect(
			0.0, 0.0, (double)( d.width ), (double)( d.height ) ) );
		Transform inv = Tc.invert();

		Point pt = inv.apply( new Point( 0.0, 0.0 ) );
		Point tr = inv.apply( new Point( (double)( d.width ), 0.0 ) );

		left = pt.getX();
		top = pt.getY();
		width = tr.getX() - pt.getX();

		theta = 0.0;

		transform = null;
	}

	public final Point worldToScreen( Point pt )
	{
		if( transform == null ) {
			computeTransform();
		}
			
		return transform.apply( pt );
	}

	public final Point screenToWorld( Point pt )
	{
		if( transform == null ) {
			computeTransform();
		}

		return inverse.apply( pt );
	}

	public final Point screenToWorld( int x, int y )
	{
		double xx = (double)( x );
		double yy = (double)( y );

		return inverse.apply( new Point( xx, yy ) );
	}

	public final Transform getTransform()
	{
		computeTransform();
		return transform;
	}

	public final Transform getInverseTransform()
	{
		computeTransform();
		return inverse;
	}



	/*
	 * Some helper data and functions for drawing the bevel around the outside
	 * of the view.
	 */

	private static java.awt.Color[] bevel_h_0 = {
		new Color( 1, 1, 1 ),
		new Color( 1, 1, 1 ),
		new Color( 1, 1, 1 ),
		new Color( 76, 76, 76 ),
		new Color( 103, 103, 103 ) };

	private static java.awt.Color[] bevel_h_1 = {
		new Color( 27, 27, 27 ),
		new Color( 53, 53, 53 ),
		new Color( 78, 78, 78 ),
		new Color( 153, 153, 153 ),
		new Color( 180, 180, 180 ) };

	private static java.awt.Color[] bevel_h_2 = {
		new Color( 127, 127, 127 ),
		new Color( 178, 178, 178 ),
		new Color( 228, 228, 228 ),
		new Color( 255, 255, 255 ),
		new Color( 255, 255, 255 ) };

	private static java.awt.Color[] bevel_h_3 = {
		new Color( 154, 154, 154 ),
		new Color( 205, 205, 205 ),
		new Color( 255, 255, 255 ),
		new Color( 255, 255, 255 ),
		new Color( 255, 255, 255 ) };

	private static java.awt.Color[] bevel_v = {
		new Color( 52, 52, 52 ),
		new Color( 103, 103, 103 ),
		new Color( 228, 228, 228 ),
		new Color( 255, 255, 255 ) };

	private static final void drawH( int width, int y, java.awt.Color[] cs,
		Graphics g )
	{
		g.setColor( cs[0] );
		g.drawLine( 0, y, 0, y );
		g.setColor( cs[1] );
		g.drawLine( 1, y, 1, y );
		g.setColor( cs[2] );
		g.drawLine( 2, y, width - 2, y );
		g.setColor( cs[3] );
		g.drawLine( width - 2, y, width - 2, y );
		g.setColor( cs[4] );
		g.drawLine( width - 1, y, width - 1, y );
	}

	private static final void drawV( int height, int x, java.awt.Color c,
		Graphics g )
	{
		g.setColor( c );
		g.drawLine( x, 2, x, height - 2 );
	}
}
