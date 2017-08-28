/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2017 Andreas Maschke

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

import java.util.ArrayList;
import java.util.List;

public enum FilterKernelType {
  BELL {

    @Override
    public Class<? extends FilterKernel> getPrimaryFilterClass() {
      return BellFilterKernel.class;
    }

    @Override
    public boolean isSharpening() {
      return false;
    }

    @Override
    public boolean isAdaptive() {
      return false;
    }

  },
  BLACKMAN {

    @Override
    public Class<? extends FilterKernel> getPrimaryFilterClass() {
      return BlackmanFilterKernel.class;
    }

    @Override
    public boolean isSharpening() {
      return true;
    }

    @Override
    public boolean isAdaptive() {
      return false;
    }

  },
  BOX {

    @Override
    public Class<? extends FilterKernel> getPrimaryFilterClass() {
      return BoxFilterKernel.class;
    }

    @Override
    public boolean isSharpening() {
      return false;
    }

    @Override
    public boolean isAdaptive() {
      return false;
    }

  },
  BSPLINE {

    @Override
    public Class<? extends FilterKernel> getPrimaryFilterClass() {
      return BSplineFilterKernel.class;
    }

    @Override
    public boolean isSharpening() {
      return false;
    }

    @Override
    public boolean isAdaptive() {
      return false;
    }

  },
  CATROM {

    @Override
    public Class<? extends FilterKernel> getPrimaryFilterClass() {
      return CatRomFilterKernel.class;
    }

    @Override
    public boolean isSharpening() {
      return true;
    }

    @Override
    public boolean isAdaptive() {
      return false;
    }

  },
  GAUSSIAN {

    @Override
    public Class<? extends FilterKernel> getPrimaryFilterClass() {
      return GaussianFilterKernel.class;
    }

    @Override
    public boolean isSharpening() {
      return false;
    }

    @Override
    public boolean isAdaptive() {
      return false;
    }

  },
  HAMMING {

    @Override
    public Class<? extends FilterKernel> getPrimaryFilterClass() {
      return HammingFilterKernel.class;
    }

    @Override
    public boolean isSharpening() {
      return true;
    }

    @Override
    public boolean isAdaptive() {
      return false;
    }

  },
  HANNING {

    @Override
    public Class<? extends FilterKernel> getPrimaryFilterClass() {
      return HanningFilterKernel.class;
    }

    @Override
    public boolean isSharpening() {
      return true;
    }

    @Override
    public boolean isAdaptive() {
      return false;
    }

  },
  HERMITE {

    @Override
    public Class<? extends FilterKernel> getPrimaryFilterClass() {
      return HermiteFilterKernel.class;
    }

    @Override
    public boolean isSharpening() {
      return false;
    }

    @Override
    public boolean isAdaptive() {
      return false;
    }

  },
  LANCZOS2 {

    @Override
    public Class<? extends FilterKernel> getPrimaryFilterClass() {
      return Lanczos2FilterKernel.class;
    }

    @Override
    public boolean isSharpening() {
      return true;
    }

    @Override
    public boolean isAdaptive() {
      return false;
    }

  },
  LANCZOS3 {

    @Override
    public Class<? extends FilterKernel> getPrimaryFilterClass() {
      return Lanczos3FilterKernel.class;
    }

    @Override
    public boolean isSharpening() {
      return true;
    }

    @Override
    public boolean isAdaptive() {
      return false;
    }

  },
  MITCHELL {

    @Override
    public Class<? extends FilterKernel> getPrimaryFilterClass() {
      return MitchellFilterKernel.class;
    }

    @Override
    public boolean isSharpening() {
      return true;
    }

    @Override
    public boolean isAdaptive() {
      return false;
    }

  },
  MITCHELL_SMOOTH {

    @Override
    public Class<? extends FilterKernel> getPrimaryFilterClass() {
      return MitchellSmoothFilterKernel.class;
    }

    @Override
    public boolean isSharpening() {
      return true;
    }

    @Override
    public boolean isAdaptive() {
      return false;
    }

  },
  QUADRATIC {

    @Override
    public Class<? extends FilterKernel> getPrimaryFilterClass() {
      return QuadraticFilterKernel.class;
    }

    @Override
    public boolean isSharpening() {
      return false;
    }

    @Override
    public boolean isAdaptive() {
      return false;
    }

  },
  SINEPOW5 {

    @Override
    public Class<? extends FilterKernel> getPrimaryFilterClass() {
      return SinePow5FilterKernel.class;
    }

    @Override
    public boolean isSharpening() {
      return false;
    }

    @Override
    public boolean isAdaptive() {
      return false;
    }

  },
  SINEPOW10 {

    @Override
    public Class<? extends FilterKernel> getPrimaryFilterClass() {
      return SinePow10FilterKernel.class;
    }

    @Override
    public boolean isSharpening() {
      return false;
    }

    @Override
    public boolean isAdaptive() {
      return false;
    }

  },
  SINEPOW15 {

    @Override
    public Class<? extends FilterKernel> getPrimaryFilterClass() {
      return SinePow15FilterKernel.class;
    }

    @Override
    public boolean isSharpening() {
      return false;
    }

    @Override
    public boolean isAdaptive() {
      return false;
    }

  },
  TRIANGLE {

    @Override
    public Class<? extends FilterKernel> getPrimaryFilterClass() {
      return TriangleFilterKernel.class;
    }

    @Override
    public boolean isSharpening() {
      return false;
    }

    @Override
    public boolean isAdaptive() {
      return false;
    }

  },
  MITCHELL_SINEPOW {

    @Override
    public Class<? extends FilterKernel> getPrimaryFilterClass() {
      return MitchellSmoothFilterKernel.class;
    }

    @Override
    public boolean isSharpening() {
      return true;
    }

    @Override
    public boolean isAdaptive() {
      return true;
    }

    @Override
    public FilterKernelType getSmoothingKernelType() {
      return FilterKernelType.SINEPOW10;
    }

    @Override
    public FilterKernelType getLowDensityKernelType() {
      return FilterKernelType.SINEPOW10;
    }

  },

  ;

  public abstract Class<? extends FilterKernel> getPrimaryFilterClass();

  public abstract boolean isSharpening();

  public abstract boolean isAdaptive();

  public FilterKernelType getSmoothingKernelType() {
    return null;
  }

  public FilterKernelType getLowDensityKernelType() {
    return null;
  }

  public static List<FilterKernelType> getSharpeningFilters() {
    List<FilterKernelType> res = new ArrayList<>();
    for (FilterKernelType kernel : values()) {
      if (!kernel.isAdaptive() && kernel.isSharpening()) {
        res.add(kernel);
      }
    }
    return res;
  }

  public static List<FilterKernelType> getSmoothingFilters() {
    List<FilterKernelType> res = new ArrayList<>();
    for (FilterKernelType kernel : values()) {
      if (!kernel.isAdaptive() && !kernel.isSharpening()) {
        res.add(kernel);
      }
    }
    return res;
  }

  public static List<FilterKernelType> getAdapativeFilters() {
    List<FilterKernelType> res = new ArrayList<>();
    for (FilterKernelType kernel : values()) {
      if (kernel.isAdaptive()) {
        res.add(kernel);
      }
    }
    return res;
  }

  public FilterKernel createFilterInstance() {
    try {
      return getPrimaryFilterClass().newInstance();
    }
    catch (InstantiationException e) {
      throw new RuntimeException(e);
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
