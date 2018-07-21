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
 * PlacedFeature.java
 *
 * A PlacedFeature is a Feature together with a transform matrix.
 * It allows us to share an underlying feature while putting several
 * copies together into a tiling.  A tiling is represented as a 
 * collection of PlacedFeatures (that may share Features) that together
 * make up a translational unit.
 */

package csk.taprats.tile;

import csk.taprats.geometry.Transform;

public class PlacedFeature
{
	private Feature 		feature;
	private Transform		T;

	PlacedFeature( Feature feature, Transform T )
	{
		this.feature = feature;
		this.T = T;
	}

	public final Feature getFeature()
	{
		return feature;
	}

	public final Transform getTransform()
	{
		return T;
	}

	public final void setTransform( Transform T )
	{
		this.T = T;
	}
}
