package org.jwildfire.cli;

import org.apache.commons.cli.*;
import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.create.tina.randomflame.AllRandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSampler;
import org.jwildfire.create.tina.randomgradient.AllRandomGradientGenerator;
import org.jwildfire.create.tina.randomgradient.RandomGradientGeneratorList;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGeneratorList;
import org.jwildfire.create.tina.randomweightingfield.RandomWeightingFieldGeneratorList;
import org.jwildfire.create.tina.render.*;
import org.jwildfire.create.tina.render.filter.FilteringType;
import org.jwildfire.create.tina.swing.RandomBatchQuality;
import org.jwildfire.create.tina.variation.VariationFuncList;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageWriter;

import java.util.UUID;

public class CreateRandomFlame {


  private static final String OPT_W = "w";
  private static final String OPT_H = "h";
  private static final String OPT_Q = "q";

  public CreateRandomFlame(String[] args) throws Exception {
    Options options = new Options();
    options.addOption(OPT_W, "renderWidth", true, "render width");
    options.addOption(OPT_H, "renderHeight",   true, "render height");
    options.addOption(OPT_Q, "renderQuality", true, "render quality");
    try {
      CommandLineParser parser = new DefaultParser();
      CommandLine cmd = parser.parse( options, args);
      int renderWidth = Integer.parseInt(cmd.getOptionValue(OPT_W, "800"));
      int renderHeight = Integer.parseInt(cmd.getOptionValue(OPT_H, "600"));
      double renderQuality = Integer.parseInt(cmd.getOptionValue(OPT_Q, "80"));

      Flame flame = createRandomFlame();
      SimpleImage img = renderFlame(flame, renderWidth, renderHeight, renderQuality);

      String flameId = UUID.randomUUID().toString();
      new FlameWriter().writeFlame(flame, flameId+".flame");
      new ImageWriter().saveImage(img, flameId+".png");

    }
    catch( ParseException exp ) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(this.getClass().getSimpleName(), options);
    }
  }

  public static void main (String[] args) throws Exception {
     new CreateRandomFlame(args).createRandomFlame();
  }

  private SimpleImage renderFlame(Flame flame, int renderWidth, int renderHeight, double renderQuality) {
    Flame renderFlame = flame.makeCopy();
    double wScl = (double) renderWidth / (double) renderFlame.getWidth();
    double hScl = (double) renderHeight / (double) renderFlame.getHeight();
    renderFlame.setPixelsPerUnit((wScl + hScl) * 0.5 * renderFlame.getPixelsPerUnit());
    renderFlame.setWidth(renderWidth);
    renderFlame.setHeight(renderHeight);
    renderFlame.setSampleDensity(renderQuality);
    renderFlame.setSpatialOversampling(2);
    renderFlame.setSpatialFilteringType(FilteringType.ADAPTIVE);
    renderFlame.setSpatialFilterRadius(0.75);

    RenderInfo renderInfo = new RenderInfo(renderWidth, renderHeight, RenderMode.PRODUCTION);
    renderInfo.setRenderHDR(false);
    renderInfo.setRenderZBuffer(false);

    RenderedFlame renderResult = new FlameRenderer(renderFlame, Prefs.getPrefs(), false, false).renderFlame(renderInfo);
    return renderResult.getImage();
  }

  private Flame createRandomFlame() {
    VariationFuncList.considerVariationCosts = false;
    final int IMG_WIDTH = 40;
    final int IMG_HEIGHT = 30;
    final RandomFlameGenerator randGen = new AllRandomFlameGenerator();
    final int palettePoints = 3 + Tools.randomInt(21);
    final boolean fadePaletteColors = Math.random() > 0.09;
    final boolean uniformSize = Math.random() > 0.75;
    RandomBatchQuality quality = RandomBatchQuality.NORMAL;
    RandomFlameGeneratorSampler sampler = new RandomFlameGeneratorSampler(IMG_WIDTH, IMG_HEIGHT, Prefs.getPrefs(), randGen, RandomSymmetryGeneratorList.SPARSE, new AllRandomGradientGenerator(), RandomWeightingFieldGeneratorList.SPARSE, palettePoints, fadePaletteColors, uniformSize, quality);
    return sampler.createSample().getFlame();
  }
}
