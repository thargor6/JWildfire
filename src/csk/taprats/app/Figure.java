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
 * Figure.java
 *
 * Making the user interface operate directly on maps would be 
 * a hassle.  Maps are a very low level geometry and topological 
 * structure.  Not so good for interacting with users.  So I
 * define a Figure class, which is a higher level structure -- 
 * an object that knows how to build maps.  Subclasses of Feature
 * understand different ways of bulding maps, but have the advantage
 * of being parameterizable at a high level.
 */

package csk.taprats.app;

import csk.taprats.geometry.Map;

public abstract class Figure
	implements Cloneable
{
	abstract public Object clone();
	abstract public Map getMap();
}
