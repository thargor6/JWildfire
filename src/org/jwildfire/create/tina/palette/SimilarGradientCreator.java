/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2019 Andreas Maschke

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
package org.jwildfire.create.tina.palette;

import org.jwildfire.image.Pixel;
import org.jwildfire.transform.HSLTransformer;

import java.util.*;

public class SimilarGradientCreator {

  private class GradientColor {
    private final HSLTransformer.HSLPixel hsl = new HSLTransformer.HSLPixel();
    private final Pixel rgb = new  Pixel();

    public GradientColor(int r, int g, int b) {
      rgb.r = r;
      rgb.g = g;
      rgb.b = b;
      HSLTransformer.rgb2hsl(rgb, hsl);
    }
  }

  private class RGBColorWithWeight extends RGBColor {
    private double weight;

    public RGBColorWithWeight(int pRed, int pGreen, int pBlue, double weight) {
      super(pRed, pGreen, pBlue);
      this.weight = weight;
    }
  }

  private RGBColor getAvgRgb(List<GradientColor> colors) {
    int avgR=0, avgG=0, avgB=0;
    for(GradientColor c: colors) {
      avgR += c.rgb.r;
      avgG += c.rgb.g;
      avgB += c.rgb.b;
    }
    return new RGBColor((int)((double)avgR/(double)colors.size() +0.5), (int)((double)avgG/(double)colors.size() +0.5), (int)((double)avgB/(double)colors.size() +0.5));
  }

  private HSLTransformer.HSLPixel getAvgHsl(List<GradientColor> colors) {
    int avgH=0, avgS=0, avgL=0;
    for(GradientColor c: colors) {
      avgH += c.hsl.hue;
      avgS += c.hsl.saturation;
      avgL += c.hsl.luminosity;
    }
    return new HSLTransformer.HSLPixel(avgH/(double)colors.size(), avgS/(double)colors.size(), avgL/(double)colors.size());
  }

  public List<RGBColor> createKeyFrames(RGBColor[] gradient) {
    final int keyFrames = 20 + (int)(Math.random() * 10 + 0.5);
    final int weightedKeyFrames = 3 + (int)(Math.random() * 3 + 0.5);
    final int maxSize = 160;

    List<List<GradientColor>> colors = new ArrayList<>();
    for(RGBColor c: gradient) {
      List<GradientColor> colorArray = new ArrayList();
      colors.add(colorArray);
      colorArray.add(new GradientColor(c.getRed(), c.getGreen(), c.getBlue()));
    }
    while(colors.size()>keyFrames) {
      double minDist = Double.MAX_VALUE;
      int idx_i = -1, idx_j = -1;
      int size = colors.size();
      for(int i=0;i<size;i++) {
        HSLTransformer.HSLPixel hsl_i = getAvgHsl(colors.get(i));
        for(int j=0;j<size;j++) {
          if(i!=j) {
            HSLTransformer.HSLPixel hsl_j = getAvgHsl(colors.get(j));
            double dist = Math.sqrt( hsl_i.hue * hsl_j.hue  + hsl_i.saturation * hsl_j.saturation + hsl_i.luminosity * hsl_j.luminosity);
            if(dist < minDist) {
              minDist = dist;
              idx_i = i;
              idx_j = j;
            }
          }
        }
      }
      List<GradientColor> newColors = new ArrayList<>();
      newColors.addAll(colors.get(idx_i));
      newColors.addAll(colors.get(idx_j));
      if(idx_i>idx_j) {
        colors.remove(idx_i);
        colors.remove(idx_j);
      }
      else {
        colors.remove(idx_j);
        colors.remove(idx_i);
      }
      colors.add(newColors);
    }

    int maxSubCount = 0;
    for(List<GradientColor> subColors: colors) {
      if(subColors.size() > maxSubCount) {
        maxSubCount = subColors.size();
      }
    }
    List<RGBColor> res = new ArrayList<>();
    for(List<GradientColor> subColors: colors) {
      int maxCount = (int)(weightedKeyFrames * (double)subColors.size()/(double)maxSubCount + 0.5);
      for(int i=0;i<maxCount;i++) {
        int rndIdx = (int)(Math.random() * subColors.size());
        GradientColor rndColor = subColors.get(rndIdx);
        if(Math.random()<0.125) {
          GradientColor g = new GradientColor(rndColor.rgb.r, rndColor.rgb.g, rndColor.rgb.b);
          g.hsl.hue = Math.random();
          HSLTransformer.hsl2rgb(g.hsl, g.rgb);
          res.add(new RGBColor(g.rgb.r, g.rgb.g, g.rgb.b));
        }
        else if(Math.random()<0.25) {
          GradientColor g = new GradientColor(rndColor.rgb.r, rndColor.rgb.g, rndColor.rgb.b);
          g.hsl.luminosity = Math.random();
          HSLTransformer.hsl2rgb(g.hsl, g.rgb);
          res.add(new RGBColor(g.rgb.r, g.rgb.g, g.rgb.b));
        }
        else {
          res.add(new RGBColor(rndColor.rgb.r, rndColor.rgb.g, rndColor.rgb.b));
        }
        if(subColors.size()>0) {
          subColors.remove(rndIdx);
        }
      }
    }
    if(Math.random()<0.25) {
      Collections.shuffle(res);
    }

    while(res.size()>maxSize) {
      res.remove((int)(Math.random()*res.size()));
    }

    return res;
  }
}
