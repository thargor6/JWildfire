/*
 JWildfire - an image and animation processor written in Java
 Copyright (C) 1995-2021 Andreas Maschke

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
package org.jwildfire.create.tina.swing;

import org.jwildfire.base.VariationProfileFilter;
import org.jwildfire.base.VariationProfileRepository;

public class VariationFuncFilter {
  public static final String NEGATION_PREFIX = "!=";

  private final VariationProfileFilter filter1;
  private final VariationProfileFilter filter2;
  private final VariationProfileFilter filter3;

  public VariationFuncFilter(String profileName1, String profileName2) {
    this.filter1 = VariationProfileRepository.getVariationProfileFilter(profileName1);
    this.filter2 = VariationProfileRepository.getVariationProfileFilter(profileName2);
    this.filter3 = TinaControllerContextService.getContext().isGpuMode() ? VariationProfileRepository.getGpuFilter() : VariationProfileRepository.getEmptyFilter();
  }

  public boolean evaluate(String variationName) {
    return filter1.evaluate(variationName) && (filter2.isNegative() ^ filter2.evaluate(variationName)) && (filter3.isNegative() ^ filter3.evaluate(variationName));
  }
}
