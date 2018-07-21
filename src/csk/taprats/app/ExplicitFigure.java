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
 * ExplicitFigure.java
 *
 * A variety of Figure which contains an explicit map, which is
 * simple returned when the figure is asked for its map.
 */

package csk.taprats.app;

import csk.taprats.geometry.*;

public class ExplicitFigure 
	extends Figure
{
	private Map map;

	public ExplicitFigure( Map map )
	{
		this.map = map;
	}

	public Object clone()
	{
		return new ExplicitFigure( (Map)( map.clone() ) );
	}

	public Map getMap()
	{
		// FIXME -- does this need to be cloned?
		return (Map)( map.clone() );
	}
}
