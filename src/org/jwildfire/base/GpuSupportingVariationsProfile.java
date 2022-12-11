/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2022 Andreas Maschke

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
package org.jwildfire.base;

import org.jwildfire.create.tina.swing.VariationFavouriteService;
import org.jwildfire.create.tina.variation.VariationFuncList;
import org.jwildfire.create.tina.variation.VariationFuncType;

import java.util.Set;

public class GpuSupportingVariationsProfile implements VariationProfileFilter {
  @Override
  public boolean evaluate(String variationName, boolean onlyFavourites) {
    Set<VariationFuncType> varTypes = VariationFuncList.getVariationTypes(variationName);
    return varTypes.contains(VariationFuncType.VARTYPE_SUPPORTS_GPU) && (!onlyFavourites || VariationFavouriteService.isFavourite(variationName));
  }

  @Override
  public boolean isNegative() {
    return false;
  }
}
