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
package org.jwildfire.create.eden.sunflow;

import org.jwildfire.create.eden.sunflow.base.ImageFilter;
import org.jwildfire.create.eden.sunflow.base.IntPair;
import org.jwildfire.create.eden.sunflow.base.IntSingle;
import org.jwildfire.create.eden.sunflow.base.PartBuilder;

public class ImageBuilder implements PartBuilder {
  private final SunflowSceneBuilder parent;
  private IntPair resolution = new IntPair();
  private IntPair aa = new IntPair();
  private IntSingle samples = new IntSingle();
  private ImageFilter filter = ImageFilter.GAUSSIAN;

  public ImageBuilder(SunflowSceneBuilder pParent) {
    parent = pParent;
  }

  public ImageBuilder withResolution(int pWidth, int pHeight) {
    resolution = new IntPair(pWidth, pHeight);
    return this;
  }

  public ImageBuilder withAa(int pAa1, int pAa2) {
    aa = new IntPair(pAa1, pAa2);
    return this;
  }

  public ImageBuilder withSamples(int pSamples) {
    samples = new IntSingle(pSamples);
    return this;
  }

  public ImageBuilder withFilter(ImageFilter pFilter) {
    filter = pFilter;
    return this;
  }

  @Override
  public void buildPart(StringBuilder pTarget) {
    pTarget.append("image {\n");

    if (!resolution.isEmpty())
      pTarget.append("  resolution " + resolution.toSceneStringPart() + "\n");

    if (!aa.isEmpty())
      pTarget.append("  aa " + aa.toSceneStringPart() + "\n");

    if (!samples.isEmpty())
      pTarget.append("  samples " + samples.toSceneStringPart() + "\n");

    if (!filter.isEmpty())
      pTarget.append("  filter " + filter.toSceneStringPart() + "\n");

    pTarget.append("}\n");
  }

  public SunflowSceneBuilder close() {
    return parent;
  }

}
