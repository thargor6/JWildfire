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
package org.jwildfire.create.tina.variation;

public interface SupportsGPU {
  String getGPUCode(FlameTransformationContext context);
  default String getGPUFunctions(FlameTransformationContext context) {
    return "";
  }
  // For stateful variants, multiple copies are created, parameterized with %d as placeholder for the instance ID
  // Example: __post_scrop_x, which is some kind of global variable in the post_point_crop-variation
  // Instead of naming it __post_scrop_x, we can name it __state%d_post_scrop_x in both in the code and in the declarations.
  // JWildfire will then create copies of the code and fill the placeholders with instance IDs.
  default boolean isStateful() { return false; };

  default String[] getGPUExtraParameterNames() {
    return new String[]{};
  }

}
