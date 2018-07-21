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
 * RenderSketch.java
 *
 * One day, it occured to me that I might be able to get a sketchy
 * hand-drawn effect by drawing an edge as a set of line segments whose
 * endpoints are jittered relative to the original edge.  And it worked!
 * Also, since the map is fixed, we can just reset the random seed every
 * time we draw the map to get coherence.  Note that coherence might not
 * be a good thing -- some animations work well precisely because the 
 * random lines that make up some object change from frame to frame (c.f.
 * Bill Plympton).  It's just a design decision, and easy to reverse
 * (or provide a UI for).
 *
 * I haven't tried it yet, but I doubt this looks any good as postscript.
 * the resolution is too high and it would probably look like, well, 
 * a bunch of lines.
 */

package csk.taprats.ui1;

import java.util.Enumeration;
import java.awt.Color;

import csk.taprats.toolkit.GeoGraphics;
import csk.taprats.geometry.*;

public class RenderSketch
	extends RenderPlain
{
	protected java.util.Random rand;

	public RenderSketch()
	{
		super();

		rand = new java.util.Random();
	}

	
	public void draw( boolean fill )
	{
//		gg.setColor( new Color( 0.1f, 0.1f, 0.1f ) );
//		Transform T = gg.getTransform();
//		java.awt.Graphics g = gg.getDirectGraphics();
        Point p1,p2;
		rand.setSeed( 279401L );

		if( pts != null ) {
			for( int idx = 0; idx < pts.length; idx += 4 ) {
				double x1;
				double y1;
				double x2;
				double y2;

				x1 =  pts[ idx ];
				y1 =  pts[ idx + 1 ];
				
				x2 = pts[ idx + 2 ];
				y2 = pts[ idx + 3 ];
				
				for( int c = 0; c < 8; ++c ) 
				{
						x1=x1 + (rand.nextDouble() * .05) - .025;
						y1=y1 + (rand.nextDouble() * .05) - .025;
						x2=x2 + (rand.nextDouble() * .05) - .025;
						y2=y2 + (rand.nextDouble() * .05) - .025;

						p1=new Point(x1,y1);
						p2=new Point(x2,y2);
						Primitive primitive=new Line(p1,p2,rand.nextDouble());  //Color =0.0
					    primitives.add(primitive);
				}
			}
		}
	}
}
