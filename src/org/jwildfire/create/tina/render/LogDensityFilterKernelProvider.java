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
package org.jwildfire.create.tina.render;

import java.util.HashMap;
import java.util.Map;

import org.jwildfire.create.tina.render.filter.FilterKernelType;

public class LogDensityFilterKernelProvider {
  private LogDensityFilterKernelProvider() {

  }

  private static class FilterKernelKey {
    private final FilterKernelType filterKernelType;
    private final int oversampling;
    private final double filterRadius;

    public FilterKernelKey(FilterKernelType filterKernelType, int oversampling, double filterRadius) {
      super();
      this.filterKernelType = filterKernelType;
      this.oversampling = oversampling;
      this.filterRadius = filterRadius;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((filterKernelType == null) ? 0 : filterKernelType.hashCode());
      long temp;
      temp = Double.doubleToLongBits(filterRadius);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      result = prime * result + oversampling;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      FilterKernelKey other = (FilterKernelKey) obj;
      if (filterKernelType != other.filterKernelType)
        return false;
      if (Double.doubleToLongBits(filterRadius) != Double.doubleToLongBits(other.filterRadius))
        return false;
      if (oversampling != other.oversampling)
        return false;
      return true;
    }
  }

  private static Map<FilterKernelKey, FilterHolder> filterStore = new HashMap<>();

  public static FilterHolder getFilter(FilterKernelType filterKernelType, int oversampling, double filterRadius) {
    FilterKernelKey key = new FilterKernelKey(filterKernelType, oversampling, filterRadius);
    FilterHolder res = filterStore.get(key);
    if (res == null) {
      res = new FilterHolder(filterKernelType, oversampling, filterRadius);
      synchronized (filterStore) {
        if (!filterStore.containsKey(key))
          filterStore.put(key, res);
        else
          res = filterStore.get(key);
      }
    }
    return res;
  }

}
