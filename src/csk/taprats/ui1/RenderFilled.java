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
 * RenderFilled.java
 *
 * A rendering style that converts the map to a collection of 
 * polygonal faces.  The faces are divided into two groups according to
 * a two-colouring of the map (which is always possible for the
 * kinds of Islamic designs we're building).
 *
 * The code to build the faces from the map is contained in 
 * csk.taprats.geometry.Faces.
 */

package csk.taprats.ui1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Stack;
import java.awt.*;
import java.awt.event.*;

import csk.taprats.toolkit.GeoGraphics;
import csk.taprats.geometry.*;

import csk.taprats.geometry.Point;
import csk.taprats.geometry.Polygon;

public class RenderFilled
	extends RenderStyle
{
	Vector		white;
	Vector		black;
	Map			map;

	public RenderFilled()
	{
		super();
		this.map = null;
	}

	public void setMap( Map map )
	{
		this.black = null;
		this.white = null;
		this.map = map;

		if( this.map != null ) {
			white = new Vector();
			black = new Vector();

			// Get the faces.
			Faces.extractFaces( this.map, this.black, this.white );
		}
	}

	private static Color light_interior = new Color( 240, 240, 255 );
	private static Color dark_interior = new Color( 90, 90, 120 );

	public void draw( boolean fill  )
	{
  //    double x,y;
  //    Point p1,p2,p3;
  //		ArrayList<Point> result;

	//		gg.setColor( dark_interior );
			for( Enumeration e = white.elements(); e.hasMoreElements(); ) {
				Point[] pts = (Point[])( e.nextElement() );
		//		gg.drawPolygon( pts, true );
				 drawPolygon(pts,0,pts.length,fill,0.0);
	/*			
				  result=new ArrayList<Point>();
                  ArrayList<Point> a = new ArrayList<Point>(pts.length);
				  for (int i=0;i<pts.length;i++) {
					    a.add(pts[i]);
				  }

				  //  Invoke the triangulator to triangulate this polygon.
				  Triangulate.Process(a, result);
				  int tcount = result.size() / 3;

				  for (int i = 0; i < tcount; i++)
				  {
					  p1 = result.get(i * 3 + 0);
					  p2 = result.get(i * 3 + 1);
					  p3 = result.get(i * 3 + 2);
					  Triangle triangle= new Triangle(p1,p2,p3,0.0);
					  primitives.add(triangle);
				  }
*/
			}

	//		gg.setColor( light_interior );
			for( Enumeration e = black.elements(); e.hasMoreElements(); ) {
				Point[] pts = (Point[])( e.nextElement() );
		//		gg.drawPolygon( pts, true );
			     drawPolygon(pts,0,pts.length,fill,1.0);	
		/*
			    result=new ArrayList<Point>();
                ArrayList<Point> polygon = new ArrayList<Point>(pts.length);
				  for (int i=0;i<pts.length;i++) {
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
					  Triangle triangle= new Triangle(p1,p2,p3,1.0);
					  primitives.add(triangle);
				  }
	*/
			}

	}

	/*
	public void draw( GeoGraphics gg )
	{
		if( map != null ) {
			gg.setColor( dark_interior );
			for( Enumeration e = white.elements(); e.hasMoreElements(); ) {
				Point[] pts = (Point[])( e.nextElement() );
				gg.drawPolygon( pts, true );
			}

			gg.setColor( light_interior );
			for( Enumeration e = black.elements(); e.hasMoreElements(); ) {
				Point[] pts = (Point[])( e.nextElement() );
				gg.drawPolygon( pts, true );
			}
		}
	}
	*/
}
