package org.jwildfire.create.tina.render.denoiser;

import org.jwildfire.create.GradientCreator;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageReader;
import org.jwildfire.io.ImageWriter;
import org.jwildfire.transform.ComposeTransformer;
import org.jwildfire.transform.CropTransformer;
import org.jwildfire.transform.RectangleTransformer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public abstract class CmdLineAIPostDenoiser implements AIPostDenoiser {
  private final String FILE_EXT = "_denoise";
  private final String DENOISER_PATH = "Denoiser";

  @Override
  public SimpleImage denoise(SimpleImage img, double blend) {
    try {
      File tmpFile = File.createTempFile("jwf", ".png");
      try {
        String inputFilename = tmpFile.getAbsolutePath();
        new ImageWriter().saveImage(img, inputFilename);
        String outputFilename = denoise(inputFilename, blend);
        try {
          SimpleImage denoisedImage = new ImageReader(new JPanel()).loadImage(outputFilename);
          if(denoisedImage.getImageWidth()>img.getImageWidth() || denoisedImage.getImageHeight() > denoisedImage.getImageHeight()) {
            CropTransformer crop=new CropTransformer();
            crop.setLeft(0);
            crop.setWidth(img.getImageWidth());
            crop.setTop(0);
            crop.setHeight(img.getImageHeight());
            crop.transformImage(denoisedImage);
          }
          return denoisedImage;
        } finally {
          try {
            new File(outputFilename).delete();
          } catch (Exception ex) {
            new File(outputFilename).deleteOnExit();
          }
        }
      } finally {
        try {
          tmpFile.delete();
        } catch (Exception ex) {
          tmpFile.deleteOnExit();
        }
      }
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public String denoise(String inputFilename, double blend) {
    try {
      String outputFilename;
      if (inputFilename.toLowerCase().endsWith(".png")) {
        outputFilename = inputFilename.substring(0, inputFilename.length() - ".png".length()) + FILE_EXT + ".png";
      } else {
        outputFilename = inputFilename + FILE_EXT + ".png";
      }

      String encodedCodeSource = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
      String codeSource = URLDecoder.decode(encodedCodeSource, StandardCharsets.UTF_8.toString());

      if(codeSource.toLowerCase().endsWith(".jar")) {
        for(int i=codeSource.length()-1; i>=0; i--) {
          if(codeSource.charAt(i)=='/' || codeSource.charAt(i)=='\\') {
            codeSource = codeSource.substring(0, i);
            break;
          }
        }
      }

      String denoiserPath=new File( codeSource, DENOISER_PATH).getAbsolutePath();
      String[] denoiserCmd = getDenoiserCmd( denoiserPath, inputFilename, outputFilename, blend);
      System.out.println("Executing command:");
      System.out.println(String.join(" ", denoiserCmd));

      ProcessBuilder builder = new ProcessBuilder(denoiserCmd);
      builder.directory(new File(denoiserPath));
      builder.redirectErrorStream(true);
      try {
        Process process = builder.start();
        try (InputStream in = process.getInputStream(); Scanner scanner = new Scanner(in)) {
          while(scanner.hasNextLine()) {
            System.out.println(scanner.useDelimiter("\\Z").next());
          }
        }
        process.waitFor(30, TimeUnit.SECONDS);
      } catch (IOException e) {
        e.printStackTrace(System.out);
        throw e;
      }
      return outputFilename;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  protected abstract String[] getDenoiserCmd(String denoiserPath, String inputFilename, String outputFilename, double blend);

  protected abstract Color getIndicatorColor();

  @Override
  public void addDenoisePreviewToImage(SimpleImage img, double blend) {
    SimpleImage denoisedImage = denoise(img, blend);
    int wHalve = img.getImageWidth() / 3;
    RectangleTransformer rect = new RectangleTransformer();
    rect.setColor(getIndicatorColor());
    rect.setTop(0);
    rect.setHeight(img.getImageHeight());
    rect.setLeft(wHalve);
    rect.setWidth(2);
    rect.setThickness(2);
    rect.transformImage(img);

    CropTransformer crop=new CropTransformer();
    crop.setLeft(wHalve+2);
    crop.setWidth(img.getImageWidth()-crop.getLeft());
    crop.setTop(0);
    crop.setHeight(img.getImageHeight());
    crop.transformImage(denoisedImage);

    ComposeTransformer compose = new ComposeTransformer();
    compose.setForegroundImage(denoisedImage);
    compose.setLeft(wHalve+2);
    compose.setHAlign(ComposeTransformer.HAlignment.OFF);
    compose.setVAlign(ComposeTransformer.VAlignment.TOP);
    compose.transformImage(img);
  }

  @Override
  public boolean performSelfTests() {
    try {
      SimpleImage img = new SimpleImage(64, 64);
      GradientCreator gradientCreator = new GradientCreator();
      gradientCreator.fillImage(img);
      File tmpFile = File.createTempFile("jwf", ".png");
      try {
        String inputFilename = tmpFile.getAbsolutePath();
        new ImageWriter().saveImage(img, inputFilename);

        String outputFilename = denoise(inputFilename, 0.0);
        try {
          SimpleImage denoisedImage = new ImageReader(new JPanel()).loadImage(outputFilename);
          if(denoisedImage.getImageWidth()!=img.getImageWidth() || denoisedImage.getImageHeight()!=img.getImageHeight()) {
            throw new RuntimeException("Image sizes do not match");
          }
          int x = img.getImageWidth() / 2;
          int y = img.getImageHeight() / 2;
          if(denoisedImage.getRValue(x,y) <= 0 || denoisedImage.getGValue(x,y) <= 0 || denoisedImage.getBValue(x,y) <= 0) {
            throw new RuntimeException("Colors do not match");
          }
        } finally {
          try {
            new File(outputFilename).delete();
          } catch (Exception ex) {
            new File(outputFilename).deleteOnExit();
          }
        }
      } finally {
        try {
          tmpFile.delete();
        } catch (Exception ex) {
          tmpFile.deleteOnExit();
        }
      }
      return true;
    }
    catch(Throwable ex) {
      ex.printStackTrace(System.out);
      return false;
    }
  }

}
