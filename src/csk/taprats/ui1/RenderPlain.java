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
 * RenderPlain.java
 *
 * The trivial rendering style.  Render the map as a collection of
 * line segments.  Not very useful considering that DesignPreview does
 * this better.  But there needs to be a default style for the RenderView.
 * Who knows -- maybe some diagnostic information could be added later.
 */

package csk.taprats.ui1;

import java.util.Enumeration;
import java.awt.Color;


import csk.taprats.geometry.*;

public class RenderPlain
	extends RenderStyle
{
	protected double[] pts;


	public RenderPlain()
	{
		super();
		// setSize( 10, 10 );
		pts = null;
	}

	public void setMap( Map map )
	{
		pts = null;

		if( map != null ) {
			pts = new double[ 4 * map.numEdges() ];
			int index = 0;

			for( Enumeration e = map.getEdges(); e.hasMoreElements(); ) {
				Edge edge = (Edge)( e.nextElement() );
				Point v1 = edge.getV1().getPosition();
				Point v2 = edge.getV2().getPosition();

				pts[ index ] = v1.getX();
				pts[ index + 1 ] = v1.getY();
				pts[ index + 2 ] = v2.getX();
				pts[ index + 3 ] = v2.getY();

				index += 4;
			}
		}
	}

	
	public void draw( boolean fill )
	{
//		gg.setColor( Color.white );

			for( int idx = 0; idx < pts.length; idx += 4 ) 
			{
		//		gg.setColor( Color.white );
		//		gg.drawLine( pts[idx], pts[idx+1], pts[idx+2], pts[idx+3] );
				
				Point p1=new Point(pts[idx],pts[idx+1]);
				Point p2=new Point(pts[idx+2],pts[idx+3]);				
				Primitive primitive=new Line(p1,p2,0.0);  //Color =0.0
                primitives.add(primitive);
			}

	}
/*
	public void draw( GeoGraphics gg )
	{
		gg.setColor( Color.white );
		if( pts != null ) {
			for( int idx = 0; idx < pts.length; idx += 4 ) {
				gg.drawLine( pts[idx], pts[idx+1], pts[idx+2], pts[idx+3] );
			}
		}
	}
	*/
}
