/*
 JWildfire - an image and animation processor written in Java
 Copyright (C) 1995-2021 Andreas Maschke

 This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 General Public License as published by the Free Software Foundation; either version 2.1 of the
 License, or (at your option) any later version.

 This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.d

 You should have received a copy of the GNU Lesser General Public License along with this software;
 if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jwildfire.create.tina.variation;

public enum VariationFuncType {

  /**
   * id with prefix, because we are using this also in files which my be edited by the user, which
   * should not made too cryptic
   */
  VARTYPE_BLUR {
    @Override
    public String getCaption() {
      return "Blur";
    }
  },
  VARTYPE_2D {
    @Override
    public String getCaption() {
      return "2D";
    }
  },
  VARTYPE_ZTRANSFORM {
    @Override
    public String getCaption() {
      return "ZTransform";
    }
  },
  VARTYPE_3D {
    @Override
    public String getCaption() {
      return "3D";
    }
  },
  VARTYPE_DC {
    @Override
    public String getCaption() {
      return "DC";
    }
  },
  VARTYPE_SIMULATION {
    @Override
    public String getCaption() {
      return "Simulation";
    }
  },
  VARTYPE_BASE_SHAPE {
    @Override
    public String getCaption() {
      return "Base shape";
    }
  },
  VARTYPE_PRE {
    @Override
    public String getCaption() {
      return "Pre";
    }
  },
  VARTYPE_PREPOST {
    @Override
    public String getCaption() {
      return "PrePost";
    }
  },
  VARTYPE_POST {
    @Override
    public String getCaption() {
      return "Post";
    }
  },
  VARTYPE_CROP {
    @Override
    public String getCaption() {
      return "Crop";
    }
  },
  VARTYPE_EDIT_FORMULA {
    @Override
    public String getCaption() {
      return "Edit formula/code";
    }
  },
  VARTYPE_ESCAPE_TIME_FRACTAL {
    @Override
    public String getCaption() {
      return "Escape-time fractal";
    }
  },
  VARTYPE_SUPPORTS_EXTERNAL_SHAPES {
    @Override
    public String getCaption() {
      return "Supports external shapes";
    }
  };

  public abstract String getCaption();
}
