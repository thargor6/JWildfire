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
package org.jwildfire.create.tina.meshgen.filter;

public enum PreFilterType {
  NONE {
    @Override
    protected Class<? extends PreFilter> getFilterType() {
      return EmptyPreFilter.class;
    }
  },
  GAUSS3X3 {
    @Override
    protected Class<? extends PreFilter> getFilterType() {
      return Gauss3x3PreFilter.class;
    }
  },
  GAUSS5X5 {
    @Override
    protected Class<? extends PreFilter> getFilterType() {
      return Gauss5x5PreFilter.class;
    }
  },
  DILATE3 {
    @Override
    protected Class<? extends PreFilter> getFilterType() {
      return Dilate3PreFilter.class;
    }
  },
  DILATE5 {
    @Override
    protected Class<? extends PreFilter> getFilterType() {
      return Dilate5PreFilter.class;
    }
  };

  protected abstract Class<? extends PreFilter> getFilterType();

  public PreFilter getFilter() {
    Class<? extends PreFilter> cls = getFilterType();
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
