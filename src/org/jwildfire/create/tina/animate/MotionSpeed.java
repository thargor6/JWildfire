/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2012 Andreas Maschke

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
package org.jwildfire.create.tina.animate;

public enum MotionSpeed {

  S1_10 {
    public String toString() {
      return "1/10";
    }

    @Override
    double getDivisor() {
      return 10.0;
    }

    @Override
    double getMultiplier() {
      return 1.0;
    }

  },
  S1_9 {
    public String toString() {
      return "1/9";
    }

    @Override
    double getDivisor() {
      return 9.0;
    }

    @Override
    double getMultiplier() {
      return 1.0;
    }
  },
  S1_8 {
    public String toString() {
      return "1/8";
    }

    @Override
    double getDivisor() {
      return 8.0;
    }

    @Override
    double getMultiplier() {
      return 1.0;
    }
  },
  S1_7 {
    public String toString() {
      return "1:7";
    }

    @Override
    double getDivisor() {
      return 7.0;
    }

    @Override
    double getMultiplier() {
      return 1.0;
    }
  },
  S1_6 {
    public String toString() {
      return "1/6";
    }

    @Override
    double getDivisor() {
      return 6.0;
    }

    @Override
    double getMultiplier() {
      return 1.0;
    }
  },
  S1_5 {
    public String toString() {
      return "1/5";
    }

    @Override
    double getDivisor() {
      return 5.0;
    }

    @Override
    double getMultiplier() {
      return 1.0;
    }
  },
  S1_4 {
    public String toString() {
      return "1/4";
    }

    @Override
    double getDivisor() {
      return 4.0;
    }

    @Override
    double getMultiplier() {
      return 1.0;
    }
  },
  S1_3 {
    public String toString() {
      return "1/3";
    }

    @Override
    double getDivisor() {
      return 3.0;
    }

    @Override
    double getMultiplier() {
      return 1.0;
    }
  },
  S1_2 {
    public String toString() {
      return "1/2";
    }

    @Override
    double getDivisor() {
      return 2.0;
    }

    @Override
    double getMultiplier() {
      return 1.0;
    }
  },
  S1_1 {
    public String toString() {
      return "default";
    }

    @Override
    double getDivisor() {
      return 1.0;
    }

    @Override
    double getMultiplier() {
      return 1.0;
    }
  },
  S2_1 {
    public String toString() {
      return "2x";
    }

    @Override
    double getDivisor() {
      return 1.0;
    }

    @Override
    double getMultiplier() {
      return 2.0;
    }
  },
  S3_1 {
    public String toString() {
      return "3x";
    }

    @Override
    double getDivisor() {
      return 1.0;
    }

    @Override
    double getMultiplier() {
      return 3.0;
    }
  },
  S4_1 {
    public String toString() {
      return "4x";
    }

    @Override
    double getDivisor() {
      return 1.0;
    }

    @Override
    double getMultiplier() {
      return 4.0;
    }
  },
  S5_1 {
    public String toString() {
      return "5x";
    }

    @Override
    double getDivisor() {
      return 1.0;
    }

    @Override
    double getMultiplier() {
      return 5.0;
    }
  },
  S6_1 {
    public String toString() {
      return "6x";
    }

    @Override
    double getDivisor() {
      return 1.0;
    }

    @Override
    double getMultiplier() {
      return 6.0;
    }
  },
  S7_1 {
    public String toString() {
      return "7x";
    }

    @Override
    double getDivisor() {
      return 1.0;
    }

    @Override
    double getMultiplier() {
      return 7.0;
    }
  },
  S8_1 {
    public String toString() {
      return "8x";
    }

    @Override
    double getDivisor() {
      return 1.0;
    }

    @Override
    double getMultiplier() {
      return 8.0;
    }
  },
  S9_1 {
    public String toString() {
      return "9x";
    }

    @Override
    double getDivisor() {
      return 1.0;
    }

    @Override
    double getMultiplier() {
      return 9.0;
    }
  },
  S10_1 {
    public String toString() {
      return "10x";
    }

    @Override
    double getDivisor() {
      return 1.0;
    }

    @Override
    double getMultiplier() {
      return 10.0;
    }
  };

  abstract double getDivisor();

  abstract double getMultiplier();

  public double calcTime(int pFrame, int pFrames, boolean pDoWrap) {
    double t = (double) (pFrame - 1) * getMultiplier() / ((double) pFrames * getDivisor());
    if (pDoWrap && t > 1.0) {
      t -= (int) (t);
    }
    System.out.println(t);
    return t;
  }

}
