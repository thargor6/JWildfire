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
package org.jwildfire.create.tina.randomflame;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.palette.MedianCutQuantizer;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.swing.RandomBatchQuality;
import org.jwildfire.create.tina.variation.AbstractColorMapWFFunc;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.io.ImageReader;

public class WikimediaCommonsRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  protected Flame createFlame(RandomFlameGeneratorState pState) {
    Prefs prefs = new Prefs();
    try {
      prefs.loadFromFile();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    Flame flame = createSubFlame(prefs);
    flame.setPixelsPerUnit(200);
    return flame;
  }

  private Flame createSubFlame(Prefs prefs) {
    final int IMG_WIDTH = 120;
    final int IMG_HEIGHT = 90;

    FlameGenerator generator = getRandomGenerator();

    RandomFlameGeneratorSampler sampler = new RandomFlameGeneratorSampler(IMG_WIDTH, IMG_HEIGHT, prefs, generator.createRandomFlameGenerator(), 3, false, RandomBatchQuality.LOW);

    Flame flame = sampler.createSample().getFlame();

    return generator.postProcessFlame(flame);
  }

  @Override
  public String getName() {
    return "Wikimedia Commons";
  }

  @Override
  public boolean isUseFilter(RandomFlameGeneratorState pState) {
    return false;
  }

  private static List<FlameGenerator> generators;

  private FlameGenerator getRandomGenerator() {
    return generators.get((int) (Math.random() * generators.size()));
  }

  private static class ImageData {
    private final String imgUrl;
    private final String pageUrl;
    private final byte[] data;
    private final RGBPalette gradient;

    public ImageData(String pPageUrl, String pImgUrl, byte[] pData, RGBPalette pGradient) {
      pageUrl = pPageUrl;
      imgUrl = pImgUrl;
      data = pData;
      gradient = pGradient;
    }

    public String getImgUrl() {
      return imgUrl;
    }

    public byte[] getData() {
      return data;
    }

    public RGBPalette getGradient() {
      return gradient;
    }

    public String getPageUrl() {
      return pageUrl;
    }
  }

  private static abstract class FlameGenerator {
    public abstract RandomFlameGenerator createRandomFlameGenerator();

    public abstract Flame postProcessFlame(Flame pFlame);

    protected void addImgVariation(Flame pFlame, XForm pXForm) {
      VariationFunc imgFunc = VariationFuncList.getVariationFuncInstance("post_colormap_wf");
      pXForm.addVariation(1.0, imgFunc);
      ImageData imgData = getRandomImage();
      if (imgData != null) {
        double size = 1.5 + Math.random() * 5.0;
        imgFunc.setParameter(AbstractColorMapWFFunc.PARAM_SCALEX, size);
        imgFunc.setParameter(AbstractColorMapWFFunc.PARAM_SCALEY, size);
        if (Math.random() < 0.5) {
          pXForm.setWeight(2.0 * pXForm.getWeight());
        }
        imgFunc.setRessource(AbstractColorMapWFFunc.RESSOURCE_IMAGE_DESC_SRC, imgData.getPageUrl().getBytes());
        imgFunc.setRessource(AbstractColorMapWFFunc.RESSOURCE_INLINED_IMAGE, imgData.getData());
        imgFunc.setRessource(AbstractColorMapWFFunc.RESSOURCE_IMAGE_DESC_SRC, imgData.getPageUrl().getBytes());
        imgFunc.setRessource(AbstractColorMapWFFunc.RESSOURCE_IMAGE_SRC, imgData.getImgUrl().getBytes());
        pFlame.getFirstLayer().setPalette(imgData.getGradient());
      }
    }

    private ImageData getRandomImage() {
      if (images.size() == 0 || (Math.random() < 0.15)) {
        ImageData imgData = null;
        int iter = 0;
        int maxIter = images.size() == 0 ? 16 : 1;
        while (imgData == null && iter++ < maxIter) {
          imgData = downloadImage();
          if (imgData != null) {
            if (images.size() >= MAX_IMAGES) {
              images.set((int) (Math.random() * images.size()), imgData);
            }
            else {
              images.add(imgData);
            }
          }
        }
      }
      return images.size() > 0 ? images.get((int) (Math.random() * images.size())) : null;
    }

    private String getImgUrl(String html) {
      String prefix = "href=\"";
      int startPos = html.indexOf(prefix + "//upload.wikimedia.org/");
      int endPos = html.indexOf("\"", startPos + prefix.length() + 1);
      if (startPos > 0 && endPos > startPos) {
        return "http:" + html.substring(startPos + prefix.length(), endPos);
      }
      return null;
    }

    private String getPageUrl(String html) {
      String prefix = "<link rel=\"canonical\" href=\"";
      int startPos = html.indexOf(prefix);
      int endPos = html.indexOf("\"", startPos + prefix.length() + 1);
      if (startPos > 0 && endPos > startPos) {
        return html.substring(startPos + prefix.length(), endPos);
      }
      return null;
    }

    private ImageData downloadImage() {
      try {
        String url = "http://commons.wikimedia.org/wiki/Special:Random/File";
        int minSize = 16;
        int maxSize = 16000;
        byte[] htmlData = downloadRessource(url);
        String html = new String(htmlData);
        String imgUrl = getImgUrl(html);
        String pageUrl = getPageUrl(html);
        if (imgUrl != null && pageUrl != null && isValidImgUrl(imgUrl)) {
          byte[] imgData = downloadRessource(imgUrl);
          String fileExt = RessourceManager.guessImageExtension(imgData);
          File f = File.createTempFile("tmp", "." + fileExt);
          f.deleteOnExit();
          Tools.writeFile(f.getAbsolutePath(), imgData);
          WFImage img = new ImageReader(new JLabel()).loadImage(f.getAbsolutePath());
          if (img.getImageWidth() >= minSize && img.getImageWidth() <= maxSize && img.getImageHeight() >= minSize && img.getImageHeight() <= maxSize) {
            int hashcode = RessourceManager.calcHashCode(imgData);
            SimpleImage wfImg = (SimpleImage) RessourceManager.getImage(hashcode, imgData);
            RGBPalette gradient = new MedianCutQuantizer().createPalette(wfImg);
            return new ImageData(pageUrl, imgUrl, imgData, gradient);
          }
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
      return null;
    }

    private boolean isValidImgUrl(String imgUrl) {
      String lowerImgUrl = imgUrl.toLowerCase();
      return lowerImgUrl.endsWith(".jpg") || lowerImgUrl.endsWith(".png") || lowerImgUrl.endsWith(".jpeg");
    }

    public byte[] downloadRessource(String pURL) throws Exception {
      BufferedInputStream in = null;
      ByteArrayOutputStream fout = null;
      try {
        in = new BufferedInputStream(new URL(pURL).openStream());
        fout = new ByteArrayOutputStream();
        byte data[] = new byte[1024];
        int count;
        while ((count = in.read(data, 0, 1024)) != -1) {
          fout.write(data, 0, count);
        }
        return fout.toByteArray();
      }
      finally {
        if (in != null)
          in.close();
        if (fout != null)
          fout.close();
      }
    }

    private static List<ImageData> images = new ArrayList<ImageData>();

    private static final int MAX_IMAGES = 20;

  }

  private static class BubblesFlameGenerator extends FlameGenerator {

    @Override
    public RandomFlameGenerator createRandomFlameGenerator() {
      return new BubblesRandomFlameGenerator();
    }

    @Override
    public Flame postProcessFlame(Flame pFlame) {
      Layer layer = pFlame.getFirstLayer();
      int idx = Math.random() < 0.25 ? 0 : 1;
      XForm xForm = layer.getXForms().get(idx);
      addImgVariation(pFlame, xForm);
      return pFlame;
    }
  }

  private static class DuckiesFlameGenerator extends FlameGenerator {

    @Override
    public RandomFlameGenerator createRandomFlameGenerator() {
      return new DuckiesRandomFlameGenerator();
    }

    @Override
    public Flame postProcessFlame(Flame pFlame) {
      Layer layer = pFlame.getFirstLayer();
      int idx = Math.random() < 0.5 ? 0 : 1;
      XForm xForm = layer.getXForms().get(idx);
      addImgVariation(pFlame, xForm);
      return pFlame;
    }
  }

  private static class SplitsFlameGenerator extends FlameGenerator {

    @Override
    public RandomFlameGenerator createRandomFlameGenerator() {
      return new SplitsRandomFlameGenerator();
    }

    @Override
    public Flame postProcessFlame(Flame pFlame) {
      Layer layer = pFlame.getFirstLayer();
      int idx = (int) (Math.random() * 3.0);
      XForm xForm = layer.getXForms().get(idx);
      addImgVariation(pFlame, xForm);
      return pFlame;
    }
  }

  private static class SphericalFlameGenerator extends FlameGenerator {

    @Override
    public RandomFlameGenerator createRandomFlameGenerator() {
      return new SphericalRandomFlameGenerator();
    }

    @Override
    public Flame postProcessFlame(Flame pFlame) {
      Layer layer = pFlame.getFirstLayer();
      int idx = Math.random() < 0.5 ? 0 : 1;
      XForm xForm = layer.getXForms().get(idx);
      addImgVariation(pFlame, xForm);
      return pFlame;
    }
  }

  private static class GnarlFlameGenerator extends FlameGenerator {

    @Override
    public RandomFlameGenerator createRandomFlameGenerator() {
      return new GnarlRandomFlameGenerator();
    }

    @Override
    public Flame postProcessFlame(Flame pFlame) {
      Layer layer = pFlame.getFirstLayer();
      int idx = 0;
      XForm xForm = layer.getXForms().get(idx);
      addImgVariation(pFlame, xForm);
      return pFlame;
    }
  }

  private static class SynthFlameGenerator extends FlameGenerator {

    @Override
    public RandomFlameGenerator createRandomFlameGenerator() {
      return new SynthRandomFlameGenerator();
    }

    @Override
    public Flame postProcessFlame(Flame pFlame) {
      Layer layer = pFlame.getFirstLayer();
      int idx = Math.random() < 0.6 ? 0 : 1;
      XForm xForm = layer.getXForms().get(idx);
      addImgVariation(pFlame, xForm);
      return pFlame;
    }
  }

  private static class SimpleTilingFlameGenerator extends FlameGenerator {

    @Override
    public RandomFlameGenerator createRandomFlameGenerator() {
      return new SimpleTilingRandomFlameGenerator();
    }

    @Override
    public Flame postProcessFlame(Flame pFlame) {
      Layer layer = pFlame.getFirstLayer();
      int idx = Math.random() < 0.5 ? 0 : 1;
      XForm xForm = layer.getXForms().get(idx);
      addImgVariation(pFlame, xForm);
      return pFlame;
    }
  }

  static {
    generators = new ArrayList<FlameGenerator>();
    generators.add(new BubblesFlameGenerator());
    generators.add(new DuckiesFlameGenerator());
    generators.add(new GnarlFlameGenerator());
    generators.add(new SimpleTilingFlameGenerator());
    generators.add(new SphericalFlameGenerator());
    generators.add(new SplitsFlameGenerator());
    generators.add(new SynthFlameGenerator());
  }
}
