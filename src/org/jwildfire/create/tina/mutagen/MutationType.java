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

public enum MutationType {
  ALL {
    @Override
    protected Class<? extends Mutation> getMutationClass() {
      return AllMutation.class;
    }
  },
  ADD_TRANSFORM {
    @Override
    protected Class<? extends Mutation> getMutationClass() {
      return AddTransformMutation.class;
    }
  },
  CHANGE_WEIGHT {
    @Override
    protected Class<? extends Mutation> getMutationClass() {
      return ChangeWeightMutation.class;
    }
  },
  COLOR {
    @Override
    protected Class<? extends Mutation> getMutationClass() {
      return ColorMutation.class;
    }
  },
  AFFINE {
    @Override
    protected Class<? extends Mutation> getMutationClass() {
      return AffineMutation.class;
    }
  },
  RANDOM_FLAME {
    @Override
    protected Class<? extends Mutation> getMutationClass() {
      return RandomFlameMutation.class;
    }
  },
  RANDOM_PARAMETER {
    @Override
    protected Class<? extends Mutation> getMutationClass() {
      return RandomParamMutation.class;
    }
  };

  protected abstract Class<? extends Mutation> getMutationClass();

  public Mutation createMutationInstance() {
    try {
      return getMutationClass().newInstance();
    }
    catch (InstantiationException e) {
      throw new RuntimeException(e);
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

}
