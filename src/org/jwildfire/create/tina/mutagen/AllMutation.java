/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

  This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser 
  General Public License as published by the Free Software Foundation; either version 2.1 of the 
  License, or (at your option) any later version.
 
  This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License along with this software; 
  if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jwildfire.create.tina.mutagen;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;

public class AllMutation implements Mutation {
  private static List<MutationType> types;

  static {
    types = new ArrayList<MutationType>();
    types.add(MutationType.BOKEH);
    types.add(MutationType.ADD_TRANSFORM);
    types.add(MutationType.AFFINE);
    types.add(MutationType.CHANGE_WEIGHT);
    types.add(MutationType.RANDOM_GRADIENT);
    types.add(MutationType.AFFINE_3D);
    types.add(MutationType.LOCAL_GAMMA);
    types.add(MutationType.COLOR_TYPE);
    types.add(MutationType.RANDOM_PARAMETER);
    types.add(MutationType.AFFINE);
    types.add(MutationType.WEIGHTING_FIELD);
    types.add(MutationType.ADD_TRANSFORM);
    types.add(MutationType.LOCAL_GAMMA);
    types.add(MutationType.SIMILAR_GRADIENT);
    types.add(MutationType.AFFINE);
    types.add(MutationType.CHANGE_WEIGHT);
    types.add(MutationType.AFFINE_3D);
    types.add(MutationType.ADD_VARIATION);
    types.add(MutationType.CHANGE_WEIGHT);
    types.add(MutationType.WEIGHTING_FIELD);
    types.add(MutationType.ADD_TRANSFORM);
    types.add(MutationType.LOCAL_GAMMA);
    types.add(MutationType.COLOR_TYPE);
    types.add(MutationType.AFFINE);
    types.add(MutationType.RANDOM_PARAMETER);
    types.add(MutationType.AFFINE_3D);
    types.add(MutationType.BOKEH);
    types.add(MutationType.SIMILAR_GRADIENT);
    types.add(MutationType.GRADIENT_POSITION);
    types.add(MutationType.COLOR_TYPE);
    types.add(MutationType.LOCAL_GAMMA);
    types.add(MutationType.ADD_TRANSFORM);
    types.add(MutationType.AFFINE);
    types.add(MutationType.CHANGE_WEIGHT);
    types.add(MutationType.AFFINE);
    types.add(MutationType.WEIGHTING_FIELD);
    types.add(MutationType.CHANGE_WEIGHT);
    types.add(MutationType.AFFINE_3D);
    types.add(MutationType.RANDOM_PARAMETER);
  }

  @Override
  public void execute(Layer pLayer) {
    Mutation mutation = types.get(Tools.randomInt(types.size())).createMutationInstance();
    mutation.execute(pLayer);
  }

}
