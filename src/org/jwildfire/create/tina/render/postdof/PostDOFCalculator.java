/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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
package org.jwildfire.create.tina.render.postdof;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.render.filter.FilterKernel;
import org.jwildfire.create.tina.render.filter.SineBlurFilterKernel;

public class PostDOFCalculator {
  private final PostDOFBuffer buffer;
  private final double imgSize;
  private final FilterKernel kernel;
  private final List<PostDOFFilteredSample> filteredSamples;
  private final double kernelScale;

  public PostDOFCalculator(PostDOFBuffer buffer) {
    super();
    this.buffer = buffer;
    imgSize = MathLib.sqrt(MathLib.sqr(buffer.getWidth()) + MathLib.sqr(buffer.getHeight()));

    kernel = new SineBlurFilterKernel();//new GaussianFilterKernel();

    filteredSamples = new ArrayList<>();

    kernelScale = 1.0;
  }

  public void addSample(int x, int y, int r, int g, int b, double dofDist, double z) {
    if (r > 0 || g > 0 || b > 0) {
      PostDOFSample sample = new PostDOFSample(x, y, (float) z, (float) dofDist, r, g, b);
      processSample(sample);
    }
  }

  private void processSample(PostDOFSample sample) {
    double plainRadius = MathLib.fabs(sample.getDofDist()) * 10.0;
    double radius = Tools.FTOI(MathLib.fabs(sample.getDofDist()) * 10.0);
    if (radius < 2.0) {
      radius = 2.0;
    }

    double bokehIntensity = 0.005 * 1000.0 / imgSize;
    double bokehBrightness = 1.0;
    double bokehSize = 2.0;

    int bokehMinIntensity = 6;

    if (radius > 0.0) {
      filteredSamples.clear();

      if (Math.random() > 1.0 - bokehIntensity && (sample.getR() >= bokehMinIntensity || sample.getG() >= bokehMinIntensity || sample.getB() >= bokehMinIntensity)) {
        double intensity = (Math.random() + 1.0) / 5.0;

        sample.setR(Tools.FTOI(sample.getR() * intensity * radius * radius * bokehBrightness));
        sample.setG(Tools.FTOI(sample.getG() * intensity * radius * radius * bokehBrightness));
        sample.setB(Tools.FTOI(sample.getB() * intensity * radius * radius * bokehBrightness));

        radius *= bokehSize * (1.0 + Math.random() * 2.0);
      }

      double scaledInvRadius = 1.0 / plainRadius * kernelScale * kernel.getSpatialSupport();

      double intensitySum = 0.0;
      double stepSize = (radius < 5.0) ? 0.5 : 1.0;

      for (double i = -radius; i < radius + MathLib.EPSILON; i += stepSize) {
        double dstX = sample.getX() + i;
        double i_square = MathLib.sqr(i * scaledInvRadius);
        for (double j = -radius; j < radius + MathLib.EPSILON; j += stepSize) {
          double dstY = sample.getY() + j;

          double r = MathLib.sqrt(i_square + MathLib.sqr(j * scaledInvRadius));
          double intensity = kernel.getFilterCoeff(r);

          if (intensity > MathLib.EPSILON) {
            intensitySum += intensity;
            filteredSamples.add(new PostDOFFilteredSample(Tools.FTOI(dstX), Tools.FTOI(dstY), sample, intensity));
          }
        }
      }
      if (filteredSamples.size() != 0) {
        buffer.addSamples(filteredSamples, intensitySum);
        filteredSamples.clear();
      }
      else {
        filteredSamples.add(new PostDOFFilteredSample(sample.getX(), sample.getY(), sample, 1.0));
        buffer.addSamples(filteredSamples, 1.0);
        filteredSamples.clear();
      }
    }
    else {
      filteredSamples.add(new PostDOFFilteredSample(sample.getX(), sample.getY(), sample, 1.0));
      buffer.addSamples(filteredSamples, 1.0);
      filteredSamples.clear();
    }
  }

}
