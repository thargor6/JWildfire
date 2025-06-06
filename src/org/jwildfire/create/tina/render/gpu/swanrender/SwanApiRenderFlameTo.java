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

public class SwanApiRenderFlameTo {
  private String flame;
  private int render_width;
  private int render_height;
  private Double render_quality;
  private Integer swarm_size;
  private Integer iterations_per_frame;
  private Integer frame;

  public String getFlame() {
    return flame;
  }

  public void setFlame(String flame) {
    this.flame = flame;
  }

  public int getRender_width() {
    return render_width;
  }

  public void setRender_width(int render_width) {
    this.render_width = render_width;
  }

  public int getRender_height() {
    return render_height;
  }

  public void setRender_height(int render_height) {
    this.render_height = render_height;
  }

  public Double getRender_quality() {
    return render_quality;
  }

  public void setRender_quality(Double render_quality) {
    this.render_quality = render_quality;
  }

  public Integer getSwarm_size() {
    return swarm_size;
  }

  public void setSwarm_size(Integer swarm_size) {
    this.swarm_size = swarm_size;
  }

  public Integer getIterations_per_frame() {
    return iterations_per_frame;
  }

  public void setIterations_per_frame(Integer iterations_per_frame) {
    this.iterations_per_frame = iterations_per_frame;
  }

  public Integer getFrame() {
    return frame;
  }

  public void setFrame(Integer frame) {
    this.frame = frame;
  }
}


/*
const RESULT_IMAGE = "image"
const RESULT_REACHED_QUALITY = "reached_quality"
const RESULT_IMAGE_WIDTH = "image_width"
const RESULT_IMAGE_HEIGHT = "image_height"
const RESULT_ELAPSED_TIME = "elapsed_time"
 */