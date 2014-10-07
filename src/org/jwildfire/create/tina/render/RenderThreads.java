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
package org.jwildfire.create.tina.render;

import java.util.List;

public class RenderThreads {
  private final List<AbstractRenderThread> renderThreads;
  private final List<Thread> executingThreads;

  public RenderThreads(List<AbstractRenderThread> renderThreads, List<Thread> executingThreads) {
    this.renderThreads = renderThreads;
    this.executingThreads = executingThreads;
  }

  public List<AbstractRenderThread> getRenderThreads() {
    return renderThreads;
  }

  public List<Thread> getExecutingThreads() {
    return executingThreads;
  }

}
