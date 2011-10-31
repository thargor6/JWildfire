/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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
package org.jwildfire.swing;

import java.util.Vector;

public class FrameSizePresets {
  private static final Vector<FrameSizePreset> presets = new Vector<FrameSizePreset>();

  static {
    presets.add(new FrameSizePreset(320, 240));
    presets.add(new FrameSizePreset(480, 360));
    presets.add(new FrameSizePreset(640, 360));
    presets.add(new FrameSizePreset(640, 480));
    presets.add(new FrameSizePreset(800, 600));
    presets.add(new FrameSizePreset(1600, 1000));
    presets.add(new FrameSizePreset(1680, 1050));
    presets.add(new FrameSizePreset(1920, 1080));
  }

  private FrameSizePresets() {
  }

  public static class FrameSizePreset {
    private final int width;
    private final int height;

    public FrameSizePreset(int pWidth, int pHeight) {
      width = pWidth;
      height = pHeight;
    }

    public int getWidth() {
      return width;
    }

    public int getHeight() {
      return height;
    }

    @Override
    public String toString() {
      return width + " x " + height;
    }
  }

  public static Vector<FrameSizePreset> getPresets() {
    return presets;
  }

}
