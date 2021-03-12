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
package org.jwildfire.create.tina.variation;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.DoubleWrapperWF;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;

import java.util.*;

import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.sinAndCos;

// utilizes the same algorithm (with the same params) as my online-snowflake-generator: http://overwhale.com/snowflake ,
// which is an implementation of the excellent paper "A local cellular model for snow crystal growth" by Clifford A. Reiter
public class SnowflakeWFFunc extends VariationFunc {
  public static final String PARAM_BUFFER_SIZE = "buffer_size";
  public static final String PARAM_MAX_ITER = "max_iter";
  public static final String PARAM_BG_FREEZE_LEVEL = "bg_freeze_level";
  public static final String PARAM_FG_FREEZE_SPEED = "fg_freeze_speed";
  public static final String PARAM_DIFFUSION_SPEED = "diffusion_speed";
  private static final String PARAM_DIFFUSION_ASYMMETRY = "diffusion_asymmetry";
  public static final String PARAM_RND_BG_NOISE = "rnd_bg_noise";
  private static final String PARAM_THRESHOLD = "threshold";
  public static final String PARAM_SEED = "seed";
  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_JITTER = "jitter";
  private static final String PARAM_DC_COLOR = "dc_color";
  private static final String PARAM_DC_COLOR_SCALE = "dc_color_scale";
  private static final String PARAM_DC_COLOR_OFFSET = "dc_color_offset";
  private static final String[] paramNames = {PARAM_BUFFER_SIZE, PARAM_MAX_ITER,
          PARAM_BG_FREEZE_LEVEL, PARAM_FG_FREEZE_SPEED, PARAM_DIFFUSION_SPEED, PARAM_DIFFUSION_ASYMMETRY,
          PARAM_RND_BG_NOISE, PARAM_THRESHOLD, PARAM_SEED, PARAM_SCALE, PARAM_JITTER, PARAM_DC_COLOR,
          PARAM_DC_COLOR_SCALE, PARAM_DC_COLOR_OFFSET};

  private int buffer_size = 128;
  private int max_iter = 500;
  private double bg_freeze_level = 0.5;
  private double fg_freeze_speed = 0.0005;
  private double diffusion_speed = 0.01;
  private double diffusion_asymmetry = 1.0;
  private double rnd_bg_noise = 0.25;
  private double threshold = 0.65;
  private int seed = 12345;
  private double scale = 1.0;
  private double jitter = 0.001;
  private int dc_color = 1;
  private double dc_color_scale = 2.0;
  private double dc_color_offset = 0.1;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    Point point = getRandomPoint(pContext);
    pVarTP.x += pAmount * point.x;
    pVarTP.y += pAmount * point.y;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
    if (dc_color > 0) {
      pVarTP.color = point.intensity * dc_color_scale + dc_color_offset;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{buffer_size, max_iter,
            bg_freeze_level, fg_freeze_speed, diffusion_speed, diffusion_asymmetry, rnd_bg_noise,
            threshold, seed, scale, jitter, dc_color, dc_color_scale, dc_color_offset};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_BUFFER_SIZE.equalsIgnoreCase(pName))
      buffer_size = Tools.FTOI(pValue);
    else if (PARAM_MAX_ITER.equalsIgnoreCase(pName))
      max_iter = Tools.FTOI(pValue);
    else if (PARAM_BG_FREEZE_LEVEL.equalsIgnoreCase(pName))
      bg_freeze_level = pValue;
    else if (PARAM_FG_FREEZE_SPEED.equalsIgnoreCase(pName))
      fg_freeze_speed = pValue;
    else if (PARAM_DIFFUSION_SPEED.equalsIgnoreCase(pName))
      diffusion_speed = pValue;
    else if (PARAM_DIFFUSION_ASYMMETRY.equalsIgnoreCase(pName))
      diffusion_asymmetry = pValue;
    else if (PARAM_RND_BG_NOISE.equalsIgnoreCase(pName))
      rnd_bg_noise = pValue;
    else if (PARAM_THRESHOLD.equalsIgnoreCase(pName))
      threshold = pValue;
    else if (PARAM_SEED.equalsIgnoreCase(pName))
      seed = Tools.FTOI(pValue);
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else if (PARAM_JITTER.equalsIgnoreCase(pName))
      jitter = pValue;
		else if (PARAM_DC_COLOR.equalsIgnoreCase(pName))
			dc_color = Tools.FTOI(pValue);
    else if (PARAM_DC_COLOR_SCALE.equalsIgnoreCase(pName))
      dc_color_scale = pValue;
    else if (PARAM_DC_COLOR_OFFSET.equalsIgnoreCase(pName))
      dc_color_offset = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "snowflake_wf";
  }

  private String makeKey() {
    return buffer_size + "#" + max_iter + "#" + bg_freeze_level + "#" + fg_freeze_speed + "#" + diffusion_speed + "#" + diffusion_asymmetry + "#" + rnd_bg_noise + "#" + threshold + "#" + seed + "#" + scale + "#" + jitter;
  }

  private static class Point {
    double x, y;
    float intensity;
  }

  private static final Point ZERO = new Point();

  private static Map<String, List<Point>> cache = new HashMap<String, List<Point>>();

  private DoubleWrapperWF sina = new DoubleWrapperWF();
  private DoubleWrapperWF cosa = new DoubleWrapperWF();
  private List<Point> _points;
  private int _psize;

  private final static double STRETCH_FACTOR = 1.5 / 1.7321;

  private List<Point> getPoints() {
    double jitterRadius = Math.max(Math.min(1.0, jitter), 0.0);

    String key = makeKey();
    List<Point> res = cache.get(key);
    if (res == null) {
      AbstractRandomGenerator randGen = new MarsagliaRandomGenerator();
      randGen.randomize(seed);
      float[][] points = calculate();
      res = new ArrayList<Point>();

      for (int i = 0; i < points.length; i++) {
        for (int j = 0; j < points[i].length; j++) {
          double aRnd;
          // Always calculate random jitter at each cell to ensure to have always the same result, even if more cells get populated.
          // This way the growth may be animated by just increasing the max_iter param.
          if (jitterRadius > MathLib.EPSILON) {
            aRnd = randGen.random();
          } else {
            aRnd = 0.0;
          }
          if (points[i][j] > MathLib.EPSILON) {
            Point point = new Point();
            point.x = (i - points.length * 0.5) * scale / (double)(points.length);
            point.y = (j - points[i].length * 0.5) * scale / (double)(points[i].length) * STRETCH_FACTOR;
            point.intensity = points[i][j];
            if (jitterRadius > MathLib.EPSILON) {
              double alpha = aRnd * 2 * M_PI;
              sinAndCos(alpha, sina, cosa);
              point.x += jitterRadius * cosa.value;
              point.y += jitterRadius * sina.value;
            }
            res.add(point);
          }

        }
      }
      cache.put(key, res);
    }
    return res;
  }

  private Point getRandomPoint(FlameTransformationContext ctx) {
    if (_psize > 0) {
      return _points.get(Tools.FTOI(ctx.random() * _psize));
    } else
      return ZERO;
  }

  private float[][] calculate() {
    Snowflake snowflake = new Snowflake(buffer_size, bg_freeze_level, fg_freeze_speed, diffusion_speed, diffusion_asymmetry, rnd_bg_noise, threshold, seed);
    for(int iter = 0; iter < max_iter; iter++) {
      snowflake.iterate();
    }
    return snowflake.renderSnowflake();
  }

  @Override
  public void initOnce(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    // causes the points to be written to cache, happens only once for all threads
    getPoints();
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    // points are read from cache
    _points = getPoints();
    _psize = _points.size() - 1;
  }

  private static class Snowflake {
    private final int snowFlakeWidth;
    private final double bgFreezeLevel;
    private final double fgFreezeSpeed;
    private final double diffusionSpeed;
    private final double diffusionAsymmetry;
    private final double rndBgNoise;
    private final double threshold;
    private final int rndSeed;
    // derived data
    private final int snowFlakeHeight;
    private final int cellCount;
    private final int[][] neighbours;
    private final double[] snowflake;
    private final double[] nonReceptivePart;
    private final double[] receptivePart;
    private final double[] nonReceptiveTmpBuffer;

    public Snowflake(int snowFlakeWidth, double bgFreezeLevel, double fgFreezeSpeed, double diffusionSpeed, double diffusionAsymmetry, double rndBgNoise, double threshold, int rndSeed) {
      this.snowFlakeWidth = snowFlakeWidth;
      this.bgFreezeLevel = bgFreezeLevel;
      this.fgFreezeSpeed = fgFreezeSpeed;
      this.diffusionSpeed = diffusionSpeed;
      this.diffusionAsymmetry = diffusionAsymmetry;
      this.rndBgNoise = rndBgNoise;
      this.threshold = threshold;
      this.rndSeed = rndSeed;

      snowFlakeHeight = snowFlakeWidth + snowFlakeWidth - 1;
      cellCount = snowFlakeWidth * snowFlakeHeight + (snowFlakeWidth - 1) * (snowFlakeHeight-1);
      neighbours = createNeighbours();
      snowflake = createSnowFlakeWithBgValue();
      nonReceptivePart = new double[cellCount];
      nonReceptiveTmpBuffer = new double[cellCount];
      receptivePart = new double[cellCount];
      initializeStartingPoint();
    }

    private int[][] createNeighbours() {
      int[][] neighbours = new int[6][];
      for(int i = 0; i < neighbours.length; i++) {
        neighbours[i] = new int[cellCount];
      }
      for(int n = 0; n < cellCount; n++) {
        neighbours[0][n] = n - snowFlakeWidth - ( snowFlakeWidth - 1 );
        neighbours[1][n] = n - ( snowFlakeWidth - 1 );
        neighbours[2][n] = n + snowFlakeWidth;
        neighbours[3][n] = n + snowFlakeWidth + ( snowFlakeWidth - 1 );
        neighbours[4][n] = n + ( snowFlakeWidth - 1 );
        neighbours[5][n] = n - snowFlakeWidth;
      }
      return neighbours;
    }

    private double[] createSnowFlakeWithBgValue() {
      AbstractRandomGenerator randGen = new MarsagliaRandomGenerator();
      randGen.randomize(rndSeed);
      double[] snowflake = new double[cellCount];
      for(int i = 0; i < cellCount; i++) {
        snowflake[i] = bgFreezeLevel +  (rndBgNoise - 2.0 * rndBgNoise * randGen.random());
      }
      return snowflake;
    }

    private void initializeStartingPoint() {
      snowflake[  (int)Math.floor( cellCount / 2.0 ) ] = 1.0;
    }

    private void splitIntoNonReceptiveAndReceptivePart(double[] data) {
      double tFreezeSpeed = fgFreezeSpeed / 1000.0;
      double tDiffusionSpeed = diffusionSpeed / 1000.0 + 1;
      initPart(data);
      freezePart(data, tFreezeSpeed);
      diffusionPart(tDiffusionSpeed);
    }

    private void initPart(double[] data) {
      /*
        for(int i = 0; i < cellCount; i++) {
          nonReceptiveTmpBuffer[i] = data[i];
          receptivePart[i] = 0.0;
        }
     */
      System.arraycopy(data, 0, nonReceptiveTmpBuffer, 0, cellCount);
      Arrays.fill(receptivePart, 0.0);
    }

    private void freezePart(double[] data, double tFreezeSpeed) {
      for(int i = 0; i < cellCount; i++) {
        if(data[i] >= 1.0) {
          nonReceptivePart[i] = 0.0;
          receptivePart[i] = data[i];
          for(int j = 0; j < 6; j++) {
            int nb = neighbours[j][i];
            if( nb >= 0 && nb < cellCount) {
              nonReceptiveTmpBuffer[nb] = 0.0;
              receptivePart[nb] = data[nb] > 0 ? data[nb] + tFreezeSpeed : data[nb];
            }
          }
        }
      }
    }

    private void diffusionPart(double tDiffusionSpeed) {
      double cWeight = 0.5 * diffusionAsymmetry / tDiffusionSpeed;
      double nbWeight = (1.0 * tDiffusionSpeed - cWeight) / 6.0;
      for(int i = 0; i < cellCount; i++) {
        nonReceptivePart[i] = nonReceptiveTmpBuffer[i] * cWeight;
        for(int j = 0; j < 6; j++) {
          int nb = neighbours[j][i];
          if(nb>=0 && nb<cellCount) {
            nonReceptivePart[i] += nonReceptiveTmpBuffer[nb] * nbWeight;
          }
        }
      }
    }

    public void iterate() {
      splitIntoNonReceptiveAndReceptivePart(snowflake);
      for(int i = 0; i < cellCount; i++) {
        snowflake[i] = nonReceptivePart[i] + receptivePart[i];
      }
    }

    public float[][] renderSnowflake() {
      int canvas_width = (int)Math.floor(2 * snowFlakeWidth * STRETCH_FACTOR);
      int canvas_height = snowFlakeHeight;

      float[][] canvas = new float[canvas_height][];
      for(int i=0;i<canvas_height;i++) {
        canvas[i]=new float[canvas_width];
      }

      // c0   c1   c2    |  c0   (0+c3)/2   c1   (0+c4)/2   c2
      //    c3   c4      |  c5   (c3+c8)/2  c6   (c4+c9)/2  c7
      // c5   c6   c7    |  c10  (c8+0)/2   c11  (c9+0)/2   c12
      //   c8    c9      |
      // c10  c11 c12    |

      for(int i = 0; i < snowFlakeHeight; i++) {
        for(int j = 0; j < snowFlakeWidth; j++) {
          int baseIdx = ( snowFlakeWidth + snowFlakeWidth - 1 ) * i + j;
          int x = Tools.FTOI((2*j) * STRETCH_FACTOR);
          renderVal(canvas, snowflake[baseIdx], x, i);

          x = Tools.FTOI((2*j+1) * STRETCH_FACTOR);
          int idx1 = baseIdx - snowFlakeWidth + 1;
          int idx2 = baseIdx + snowFlakeWidth;
          if (idx1 >= 0 && idx1 < cellCount && idx2 >= 0 && idx2 < cellCount) {
            renderVal(canvas, (snowflake[idx1] + snowflake[idx2]) * 0.5, x, i);
          }
        }
      }
      return canvas;
    }

    private void renderVal(float[][] canvas, double value, int x, int y) {
      if(value>threshold + MathLib.EPSILON && y>=0 && y<canvas.length && x>=0 && x<canvas[y].length) {
        // double rawIntensity = MathLib.min(1.0, MathLib.log((value - threshold + 1.0) * 2.0));
        // double intensity = rawIntensity * rawIntensity * rawIntensity;
        // canvas[y][x] = (float) intensity;
        canvas[y][x] = (float) (value - threshold);
      }
    }

  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_BASE_SHAPE };
  }

}
