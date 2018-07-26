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
 * RenderEmboss.java
 *
 * A rendering style for maps that pretends that the map is carves out
 * of wood with a diamond cross section and shines a directional light 
 * on the wood from some angle.  The map is drawn as a collection of 
 * trapezoids, and the darkness of each trapezoid is controlled by the
 * angle the trapezoid makes with the light source.  The result is a 
 * simple but highly effective 3D effect, similar to flat-shaded 3D
 * rendering.
 */

package csk.taprats.ui1;


import java.awt.*;

import csk.taprats.toolkit.GeoGraphics;
import csk.taprats.geometry.Line;
import csk.taprats.geometry.Point;
import csk.taprats.geometry.Triangle;


/*
 * In practice, we can make this a subclass of RenderOutline -- it uses the
 * same pre-computed point array, and just add one controls and overloads
 * the draw function.
 */

public class RenderEmboss
	extends RenderOutline
{
	private double 		angle;
	private double 		light_x;
	private double 		light_y;



	public RenderEmboss(double width)
	{
		super(width);

		setAngle( Math.PI * 0.25 );


		
	}

	private void setAngle( double angle )
	{
		this.angle = angle;
		this.light_x = Math.cos( angle );
		this.light_y = Math.sin( angle );
	}

	protected void updateAngle()
	{
		double aval = angle;
		setAngle( ((double)aval * Math.PI) / 180.0 );

	}

	private final void drawTrap( Point a, Point b, Point c, Point d , boolean fill)
	{
		Point p1,p2,p3;
		Triangle triangle;
		double x,y;
		
		Point N = a.subtract( d );
		N.perpD();
		N.normalizeD();

		// dd is a normalized floating point value corresponding to 
		// the brightness to use.
		double dd = 0.5 * ( N.getX() * light_x + N.getY() * light_y + 1.0 );

		// Quantize to sixteen grey values.
		int bb = (int)( 16.0 * dd );
	//	gg.setColor( greys[ bb ] );

		Point[] trap_pts = { a, b, c, d };
	//	gg.drawPolygon( trap_pts, true );
		
        drawPolygon(trap_pts,0,trap_pts.length,fill,dd);
		/*
	    p1=new Point (trap_pts[0].getX(),trap_pts[0].getY());
	    p2=new Point (trap_pts[1].getX(),trap_pts[1].getY());
	    p3=new Point (trap_pts[2].getX(),trap_pts[2].getY());
	    triangle= new Triangle(p1,p2,p3,dd);
	    primitives.add(triangle);
	    
	    p1=new Point (trap_pts[0].getX(),trap_pts[0].getY());
	    p2=new Point (trap_pts[2].getX(),trap_pts[2].getY());
	    p3=new Point (trap_pts[3].getX(),trap_pts[3].getY());
	    triangle= new Triangle(p1,p2,p3,dd);
	    primitives.add(triangle);
*/
	}

	public void draw( boolean fill  )
	{
      double x,y;
      Point p1,p2,p3;
      Triangle triangle;
      
			for( int idx = 0; idx < pts.length; idx += 6 )
			{
				drawTrap(  pts[idx+1], pts[idx+2], pts[idx+3], pts[idx+4], fill );
				drawTrap(  pts[idx+4], pts[idx+5], pts[idx+0], pts[idx+1], fill );
				
		//		gg.setColor( Color.white );
		//		gg.drawPolygon( pts, idx, idx + 6, false );
				
				drawPolygon(pts,idx,idx+6,fill,1.0);
/*
			    p1=new Point (pts[idx+0].getX(),pts[idx+0].getY());
			    p2=new Point (pts[idx+1].getX(),pts[idx+1].getY());
			    p3=new Point (pts[idx+2].getX(),pts[idx+2].getY());
			    triangle= new Triangle(p1,p2,p3,1.0);
			    primitives.add(triangle);
			    
			    p1=new Point (pts[idx+0].getX(),pts[idx+0].getY());
			    p2=new Point (pts[idx+2].getX(),pts[idx+2].getY());
			    p3=new Point (pts[idx+3].getX(),pts[idx+3].getY());
			    triangle= new Triangle(p1,p2,p3,1.0);
			    primitives.add(triangle);
			    
			    p1=new Point (pts[idx+0].getX(),pts[idx+0].getY());
			    p2=new Point (pts[idx+3].getX(),pts[idx+3].getY());
			    p3=new Point (pts[idx+4].getX(),pts[idx+4].getY());		
			    triangle= new Triangle(p1,p2,p3,1.0);
			    primitives.add(triangle);
			    
			    p1=new Point (pts[idx+0].getX(),pts[idx+0].getY());
			    p2=new Point (pts[idx+4].getX(),pts[idx+4].getY());
			    p3=new Point (pts[idx+5].getX(),pts[idx+5].getY());		
			    triangle= new Triangle(p1,p2,p3,1.0);
			    primitives.add(triangle);	
			    */

		//		gg.drawLine( pts[ idx + 1 ], pts[ idx + 4 ] );

					 p1= pts[idx+1];						
					 p2= pts[idx+4];  
					 primitives.add(new Line(p1.getX(),p1.getY(),p2.getX(),p2.getY(),0.0)); // Color=0.0	

			}
	}

	/*
	public void draw( GeoGraphics gg )
	{
		if( pts != null ) {
			for( int idx = 0; idx < pts.length; idx += 6 ) {
				drawTrap( gg, pts[idx+1], pts[idx+2], pts[idx+3], pts[idx+4] );
				drawTrap( gg, pts[idx+4], pts[idx+5], pts[idx+0], pts[idx+1] );
				
				gg.setColor( Color.white );
				gg.drawPolygon( pts, idx, idx + 6, false );
				gg.drawLine( pts[ idx + 1 ], pts[ idx + 4 ] );
			}
		}
	}
*/
	private static Color[] greys;

	static {
		float frac = 240.0f / 255.0f;

		greys = new Color[ 17 ];

		for( int idx = 0; idx < 17; ++idx ) {
			float t = (float)idx / 16.0f;
			float g = (1.0f-t)*0.4f + t*0.99f;

			greys[ idx ] = new Color( g * frac, g * frac, g );
		}
	}
}
