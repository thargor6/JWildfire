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
 * DesignElement.java
 *
 * A DesignElement is the core of the process of building a finished design.
 * It's a Feature together with a Figure.  The Feature comes from the
 * tile library and will be used to determine where to place copies of the
 * Figure, which is designed by the user.
 */

package csk.taprats.app;

import csk.taprats.tile.*;
import csk.taprats.geometry.*;

public class DesignElement
	implements Cloneable
{
	private Feature 	feature;
	private Figure		figure;

	
	public DesignElement( Feature feature, Figure figure )
	{
		this.feature = feature;
		this.figure = figure;
		if(figure  instanceof RadialFigure)
		{	
		if( feature.isRegular() && (feature.numPoints() > 4) )
		 {
			((RadialFigure) figure).setN(feature.numPoints());
			this.figure = figure;
		  }
		}
	}

	public DesignElement( Feature feature)
	{
		this.feature = feature;
		if( feature.isRegular() && (feature.numPoints() > 4) ) {
			this.figure = new Rosette( feature.numPoints(), 0.0, 3 );
		} else {
			this.figure = new ExplicitFigure( new Map() );
		}
	}


/*	
	public DesignElement( Feature feature )
	{
		this.feature = feature;
		
		if( feature.isRegular() && (feature.numPoints() > 4) ) {
			float q=(float) (-1 + (2.0) * Math.random());
			int s=(int) ( 1 + 3.0*Math.random());
			if (Math.random()<0.5)
				this.figure= new Rosette(feature.numPoints(), q, s );
			else
				this.figure = new Star(feature.numPoints(), (float) (1.5 + (5.5-1.5) * Math.random()),(int) ( 1 + 4 *Math.random()) );
	
		} else {
			this.figure = new ExplicitFigure( new Map() );
		}
	}
*/
	public Object clone()
	{
		return new DesignElement( feature, (Figure)( figure.clone() ) );
	}

	public Feature getFeature()
	{
		return feature;
	}

	public Figure getFigure()
	{
		return figure;
	}

	public void setFigure( Figure figure )
	{
		this.figure = figure;
	}
}
