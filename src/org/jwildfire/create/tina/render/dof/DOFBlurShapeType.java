/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
package org.jwildfire.create.tina.render.dof;

public enum DOFBlurShapeType {
  BUBBLE {
    @Override
    protected Class<? extends DOFBlurShape> getDOFBlurShapeType() {
      return BubbleDOFBlurShape.class;
    }
  },
  CANNABISCURVE {
    @Override
    protected Class<? extends DOFBlurShape> getDOFBlurShapeType() {
      return CannabisCurveDOFBlurShape.class;
    }
  },
  CLOVERLEAF {
    @Override
    protected Class<? extends DOFBlurShape> getDOFBlurShapeType() {
      return CloverleafDOFBlurShape.class;
    }
  },
  FLOWER {
    @Override
    protected Class<? extends DOFBlurShape> getDOFBlurShapeType() {
      return FlowerDOFBlurShape.class;
    }
  },
  PERLIN_NOISE {
    @Override
    protected Class<? extends DOFBlurShape> getDOFBlurShapeType() {
      return PerlinNoiseDOFBlurShape.class;
    }
  },
  RECT {
    @Override
    protected Class<? extends DOFBlurShape> getDOFBlurShapeType() {
      return RectDOFBlurShape.class;
    }
  },
  SINEBLUR {
    @Override
    protected Class<? extends DOFBlurShape> getDOFBlurShapeType() {
      return SineBlurDOFBlurShape.class;
    }
  },
  STARBLUR {
    @Override
    protected Class<? extends DOFBlurShape> getDOFBlurShapeType() {
      return StarBlurDOFBlurShape.class;
    }
  },
  TAURUS {
    @Override
    protected Class<? extends DOFBlurShape> getDOFBlurShapeType() {
      return TaurusDOFBlurShape.class;
    }
  },
  NBLUR {
    @Override
    protected Class<? extends DOFBlurShape> getDOFBlurShapeType() {
      return NBlurDOFBlurShape.class;
    }
  },
  HEART {
    @Override
    protected Class<? extends DOFBlurShape> getDOFBlurShapeType() {
      return HeartDOFBlurShape.class;
    }
  },
  SUB_FLAME {
    @Override
    protected Class<? extends DOFBlurShape> getDOFBlurShapeType() {
      return SubFlameDOFBlurShape.class;
    }
  };

  protected abstract Class<? extends DOFBlurShape> getDOFBlurShapeType();

  public DOFBlurShape getDOFBlurShape() {
    Class<? extends DOFBlurShape> cls = getDOFBlurShapeType();
    try {
      return cls.newInstance();
    }
    catch (InstantiationException ex) {
      throw new RuntimeException(ex);
    }
    catch (IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
  }

}
