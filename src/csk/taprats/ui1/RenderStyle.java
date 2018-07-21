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
 * A RenderStyle is a panel that encapsulates a set of controls for 
 * drawing a map with some interesting style.  It also contains the
 * code for the actual drawing.  In the long run, I would almost certainly
 * break the actual draw code out from the UI code, the way 
 * csk.taprats.app.Star is different from csk.taprats.ui.StarEditor.
 * For now, I'll leave it this way.
 */

package csk.taprats.ui1;


import csk.taprats.toolkit.GeoGraphics;

import java.util.ArrayList;

import csk.taprats.geometry.Line;
import csk.taprats.geometry.Map;
import csk.taprats.geometry.Point;
import csk.taprats.geometry.Primitive;
import csk.taprats.geometry.Triangle;
import csk.taprats.geometry.Triangulate;

public class RenderStyle
{
	/* 
	 * When a slider is twiddled, the subclass fires the changed signal
	 * so the viewing area can redraw.
	 */
	
	 ArrayList< Primitive > primitives = new ArrayList<Primitive>();
	
	protected RenderStyle()
	{
	}

	public void draw( GeoGraphics gg )
	{}

	public void setMap( Map map )
	{}
	

	public void draw(boolean fill)
	{
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
}
