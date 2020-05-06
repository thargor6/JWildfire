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
package org.jwildfire.base;

import org.jwildfire.create.tina.edit.Assignable;

public class WindowPrefs implements Assignable<WindowPrefs> {
  static final String KEY_WINDOW_COUNT = "windowPrefs.count";
  static final String KEY_NAME = "windowPrefs.name";
  static final String KEY_WIDTH = "windowPrefs.width";
  static final String KEY_HEIGHT = "windowPrefs.height";
  static final String KEY_LEFT = "windowPrefs.left";
  static final String KEY_TOP = "windowPrefs.top";
  static final String KEY_MAXIMIZED = "windowPrefs.maximized";
  static final String KEY_VISIBLE = "windowPrefs.visible";

  public static final String WINDOW_TINA = "TINA";
  public static final String WINDOW_MESHGEN = "MESHGEN";
  public static final String WINDOW_MUTAGEN = "MUTAGEN";
  public static final String WINDOW_INTERACTIVERENDERER = "INTERACTIVERENDERER";
  public static final String WINDOW_HELP = "HELP";
  public static final String WINDOW_FLAMEBROWSER = "FLAMEBROWSER";
  public static final String WINDOW_DANCINGFLAMES = "DANCINGFLAMES";
  public static final String WINDOW_EASYMOVIEMAKER = "EASYMOVIEMAKER";
  public static final String WINDOW_BATCHFLAMERENDERER = "BATCHFLAMERENDERER";
  public static final String WINDOW_IFLAMES = "IFLAMES";
  public static final String WINDOW_TINA_PREVIEW = "TINA_PREVIEW";
  public static final String WINDOW_IMAGEPROCESSING = "IMAGEPROCESSING";
  public static final String WINDOW_FORMULAEXPLORER = "FORMULAEXPLORER";
  public static final String WINDOW_PREFERENCES = "PREFERENCES";
  public static final String WINDOW_SYSTEMINFO = "SYSTEMINFO";
  public static final String WINDOW_WELCOME = "WELCOME";
  public static final String WINDOW_TIPOFTHEDAY = "TIPOFTHEDAY";
  public static final String WINDOW_LOOKANDFEEL = "LOOKANDFEEL";
  public static final String WINDOW_FLAMES_GPU = "FLAMESGPU";
  public static final String WINDOW_LIST_OF_CHANGES = "LISTOFCHANGES";
  public static final String WINDOW_GPU_RENDERING = "GPURENDERING";
  public static final String WINDOW_OPTIX_DENOISER_INFO = "OPTIX_DENOISER_INFO";
  public static final String WINDOW_VARIATIONSSETTINGS = "VARIATIONSSETTINGS";
  public static final String WINDOW_QUILTFLAMERENDERER = "QUILTFLAMERENDERER";

  private int width;
  private int height;
  private int left;
  private int top;
  private boolean maximized;
  private final String name;
  private boolean visible;

  public WindowPrefs(String pName) {
    name = pName;
  }

  @Override
  public void assign(WindowPrefs pSrc) {
    width = pSrc.width;
    height = pSrc.height;
    left = pSrc.left;
    top = pSrc.top;
    maximized = pSrc.maximized;
    visible = pSrc.visible;
  }

  public int getWidth() {
    return width;
  }

  public int getWidth(int pDefault) {
    return width > 0 ? width : pDefault;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public int getHeight(int pDefault) {
    return height > 0 ? height : pDefault;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public int getLeft() {
    return left;
  }

  public void setLeft(int left) {
    this.left = left;
  }

  public int getTop() {
    return top;
  }

  public void setTop(int top) {
    this.top = top;
  }

  public boolean isMaximized() {
    return maximized;
  }

  public void setMaximized(boolean maximized) {
    this.maximized = maximized;
  }

  @Override
  public WindowPrefs makeCopy() {
    WindowPrefs res = new WindowPrefs(this.name);
    res.assign(this);
    return res;
  }

  @Override
  public boolean isEqual(WindowPrefs pSrc) {
    if (width != pSrc.width || height != pSrc.height ||
        left != pSrc.left || top != pSrc.top || maximized != pSrc.maximized || visible != pSrc.visible)
      return false;
    if (name == null) {
      if (pSrc.name != null)
        return false;
    }
    else if (pSrc.name == null || !name.equals(pSrc.name)) {
      return false;
    }
    return true;
  }

  public String getName() {
    return name;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

}