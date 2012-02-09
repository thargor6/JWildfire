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
package org.jwildfire.create;

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyMax;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.base.Tools;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.Buffer;
import org.jwildfire.swing.NonHDRImageBufferComboBoxEditor;
import org.jwildfire.transform.BalancingTransformer;
import org.jwildfire.transform.ComposeTransformer;
import org.jwildfire.transform.ScaleAspect;
import org.jwildfire.transform.ScaleTransformer;


public class TileBrickCreator extends ImageCreator {

  @Property(description = "Horizontal brick size")
  private int brickSize = 90;

  @Property(description = "Horizontal brick shift")
  private int brickShift = 30;

  @Property(description = "Image building the bricks", editorClass = NonHDRImageBufferComboBoxEditor.class)
  private Buffer brickImage;

  @Property(description = "Image variance probability")
  @PropertyMin(0)
  @PropertyMax(255)
  private int probability = 60;

  @Property(description = "Maximal red variance")
  @PropertyMin(0)
  @PropertyMax(255)
  private int redVariance = 30;

  @Property(description = "Maximal green variance")
  @PropertyMin(0)
  @PropertyMax(255)
  private int greenVariance = 30;

  @Property(description = "Maximal blue variance")
  @PropertyMin(0)
  @PropertyMax(255)
  private int blueVariance = 30;

  @Property(description = "Seed for the random number generator")
  @PropertyMin(0)
  private int seed = 123;

  @Override
  protected void fillImage(SimpleImage res) {
    Tools.srand123(this.seed);
    double rprob = (double) ((double) 1.0 - (double) probability / (double) 100.0);
    SimpleImage brickImg = this.brickImage.getImage().clone();
    {
      ScaleTransformer scaleT = new ScaleTransformer();
      scaleT.setScaleWidth(brickSize);
      scaleT.setAspect(ScaleAspect.KEEP_WIDTH);
      scaleT.transformImage(brickImg);
    }

    int width = res.getImageWidth();
    int height = res.getImageHeight();
    int brickWidth = brickImg.getImageWidth();
    int brickHeight = brickImg.getImageHeight();
    int colCount = width / brickWidth;
    if (colCount * brickWidth < width)
      colCount++;
    int rowCount = height / brickHeight;
    if (rowCount * brickHeight < height)
      rowCount++;
    for (int i = 0; i < rowCount; i++) {
      int off = i * this.brickShift;
      while (off > 0)
        off -= brickWidth;
      for (int j = 0; j < (off == 0 ? colCount : colCount + 1); j++) {
        SimpleImage compImg = brickImg;
        if (Tools.drand() >= rprob) {
          int bRed = (int) (redVariance - Tools.drand() * 2 * redVariance + 0.5);
          int bGreen = (int) (greenVariance - Tools.drand() * 2 * greenVariance + 0.5);
          int bBlue = (int) (blueVariance - Tools.drand() * 2 * blueVariance + 0.5);
          compImg = compImg.clone();
          BalancingTransformer bT = new BalancingTransformer();
          bT.setRed(bRed);
          bT.setGreen(bGreen);
          bT.setBlue(bBlue);
          bT.transformImage(compImg);
        }

        ComposeTransformer cT = new ComposeTransformer();
        cT.setHAlign(ComposeTransformer.HAlignment.OFF);
        cT.setVAlign(ComposeTransformer.VAlignment.OFF);
        cT.setTop(i * brickHeight);
        int left = j * brickWidth + off;
        cT.setLeft(left);
        cT.setForegroundImage(compImg);
        cT.transformImage(res);
      }
    }
  }

  public int getBrickSize() {
    return brickSize;
  }

  public void setBrickSize(int brickSize) {
    this.brickSize = brickSize;
  }

  public int getBrickShift() {
    return brickShift;
  }

  public void setBrickShift(int brickShift) {
    this.brickShift = brickShift;
  }

  public Buffer getBrickImage() {
    return brickImage;
  }

  public void setBrickImage(Buffer brickImage) {
    this.brickImage = brickImage;
  }

  public int getProbability() {
    return probability;
  }

  public void setProbability(int probability) {
    this.probability = probability;
  }

  public int getRedVariance() {
    return redVariance;
  }

  public void setRedVariance(int redVariance) {
    this.redVariance = redVariance;
  }

  public int getGreenVariance() {
    return greenVariance;
  }

  public void setGreenVariance(int greenVariance) {
    this.greenVariance = greenVariance;
  }

  public int getBlueVariance() {
    return blueVariance;
  }

  public void setBlueVariance(int blueVariance) {
    this.blueVariance = blueVariance;
  }

  public int getSeed() {
    return seed;
  }

  public void setSeed(int seed) {
    this.seed = seed;
  }
}
