/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2025 Andreas Maschke

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
package org.jwildfire.create.tina.render.gpu.swanrender;

public class SwanApiRenderFlameResultTo {
  private String image_hex;
  private Double reached_quality;
  private Integer image_width;
  private Integer image_height;
  private Double elapsed_time;

  public String getImage_hex() {
    return image_hex;
  }

  public void setImage_hex(String image_hex) {
    this.image_hex = image_hex;
  }

  public Double getReached_quality() {
    return reached_quality;
  }

  public void setReached_quality(Double reached_quality) {
    this.reached_quality = reached_quality;
  }

  public Integer getImage_width() {
    return image_width;
  }

  public void setImage_width(Integer image_width) {
    this.image_width = image_width;
  }

  public Integer getImage_height() {
    return image_height;
  }

  public void setImage_height(Integer image_height) {
    this.image_height = image_height;
  }

  public Double getElapsed_time() {
    return elapsed_time;
  }

  public void setElapsed_time(Double elapsed_time) {
    this.elapsed_time = elapsed_time;
  }

}
