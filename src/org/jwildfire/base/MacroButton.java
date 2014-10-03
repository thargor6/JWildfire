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
package org.jwildfire.base;

import java.io.Serializable;

import org.jwildfire.create.tina.edit.Assignable;

public class MacroButton implements Assignable<MacroButton>, Serializable {
  static final String KEY_MACRO_BUTTON_COUNT = "tina.editor.macro_button.count";
  static final String KEY_MACRO_BUTTON_CAPTION = "tina.editor.macro_button.caption";
  static final String KEY_MACRO_BUTTON_HINT = "tina.editor.macro_button.hint";
  static final String KEY_MACRO_BUTTON_IMAGE = "tina.editor.macro_button.image";
  static final String KEY_MACRO_BUTTON_MACRO = "tina.editor.macro_button.macro";
  static final String KEY_MACRO_BUTTON_INTERNAL = "tina.editor.macro_button.internal";

  private static final long serialVersionUID = 1L;
  private String caption;
  private String hint;
  private String image;
  private String macro;
  private boolean internal;

  public String getCaption() {
    return caption;
  }

  public void setCaption(String caption) {
    this.caption = caption;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getMacro() {
    return macro;
  }

  public void setMacro(String macro) {
    this.macro = macro;
  }

  @Override
  public void assign(MacroButton pSrc) {
    caption = pSrc.caption;
    hint = pSrc.hint;
    image = pSrc.image;
    macro = pSrc.macro;
    internal = pSrc.internal;
  }

  @Override
  public MacroButton makeCopy() {
    MacroButton res = new MacroButton();
    res.assign(this);
    return res;
  }

  @Override
  public boolean isEqual(MacroButton pSrc) {
    if (!Tools.stringEquals(caption, pSrc.caption) || !Tools.stringEquals(hint, pSrc.hint) || !Tools.stringEquals(image, pSrc.image) ||
        !Tools.stringEquals(macro, pSrc.macro) || internal != pSrc.internal) {
      return false;
    }
    return true;
  }

  public String getHint() {
    return hint;
  }

  public void setHint(String hint) {
    this.hint = hint;
  }

  public boolean isInternal() {
    return internal;
  }

  public void setInternal(boolean internal) {
    this.internal = internal;
  }
}
