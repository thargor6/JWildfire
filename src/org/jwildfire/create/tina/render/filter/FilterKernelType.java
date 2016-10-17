/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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
package org.jwildfire.create.tina.render.filter;

public enum FilterKernelType {
  BELL {

    @Override
    public Class<? extends FilterKernel> getFilterClass() {
      return BellFilterKernel.class;
    }
  },
  BLACKMAN {

    @Override
    public Class<? extends FilterKernel> getFilterClass() {
      return BlackmanFilterKernel.class;
    }

  },
  BOX {

    @Override
    public Class<? extends FilterKernel> getFilterClass() {
      return BoxFilterKernel.class;
    }
  },
  BSPLINE {

    @Override
    public Class<? extends FilterKernel> getFilterClass() {
      return BSplineFilterKernel.class;
    }
  },
  CATROM {

    @Override
    public Class<? extends FilterKernel> getFilterClass() {
      return CatRomFilterKernel.class;
    }

  },
  GAUSSIAN {

    @Override
    public Class<? extends FilterKernel> getFilterClass() {
      return GaussianFilterKernel.class;
    }
  },
  HAMMING {

    @Override
    public Class<? extends FilterKernel> getFilterClass() {
      return HammingFilterKernel.class;
    }
  },
  HANNING {

    @Override
    public Class<? extends FilterKernel> getFilterClass() {
      return HanningFilterKernel.class;
    }
  },
  HERMITE {

    @Override
    public Class<? extends FilterKernel> getFilterClass() {
      return HermiteFilterKernel.class;
    }
  },
  LANCZOS2 {

    @Override
    public Class<? extends FilterKernel> getFilterClass() {
      return Lanczos2FilterKernel.class;
    }
  },
  LANCZOS3 {

    @Override
    public Class<? extends FilterKernel> getFilterClass() {
      return Lanczos3FilterKernel.class;
    }
  },
  MITCHELL {

    @Override
    public Class<? extends FilterKernel> getFilterClass() {
      return MitchellFilterKernel.class;
    }
  },
  QUADRATIC {

    @Override
    public Class<? extends FilterKernel> getFilterClass() {
      return QuadraticFilterKernel.class;
    }
  },
  SINEPOW5 {

    @Override
    public Class<? extends FilterKernel> getFilterClass() {
      return SinePow5FilterKernel.class;
    }
  },
  SINEPOW10 {

    @Override
    public Class<? extends FilterKernel> getFilterClass() {
      return SinePow10FilterKernel.class;
    }
  },
  SINEPOW15 {

    @Override
    public Class<? extends FilterKernel> getFilterClass() {
      return SinePow15FilterKernel.class;
    }
  },
  TRIANGLE {

    @Override
    public Class<? extends FilterKernel> getFilterClass() {
      return TriangleFilterKernel.class;
    }
  };

  public abstract Class<? extends FilterKernel> getFilterClass();

  public FilterKernel createFilterInstance() {
    try {
      return getFilterClass().newInstance();
    }
    catch (InstantiationException e) {
      throw new RuntimeException(e);
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
