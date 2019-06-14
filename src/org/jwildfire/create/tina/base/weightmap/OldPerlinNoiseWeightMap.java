package org.jwildfire.create.tina.base.weightmap;

import fastnoise.FastNoise;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.NoiseTools;

public class PerlinNoiseWeightMap implements WeightMap {
  private final double v[] = new double[3];
  private final double aScale;
  private final double fScale;
  private final int octaves;

  private final FastNoise noise;

  public PerlinNoiseWeightMap(double aScale, double fScale, int octaves) {
    this.aScale = aScale;
    this.fScale = fScale;
    this.octaves = octaves;

    int seed = 11234;


    noise = new FastNoise(seed);
// simple noise

/*
    noise.SetFrequency(2.0f);
    noise.SetNoiseType(FastNoise.NoiseType.Perlin);
 */

/*
    noise.SetFrequency(2.0f);
    noise.SetNoiseType(FastNoise.NoiseType.Cubic);
*/

// fractal noise
/*
    noise.SetFrequency(2.0f);
  noise.SetFractalOctaves(4);
  noise.SetFractalGain(0.5f);
  noise.SetFractalLacunarity(2.0f);
  noise.SetFrequency(2.0f);
  noise.SetFractalType(FastNoise.FractalType.FBM);
  noise.SetNoiseType(FastNoise.NoiseType.ValueFractal);
*/

/*
    noise.SetFrequency(2.0f);
    noise.SetFractalOctaves(4);
    noise.SetFractalGain(0.5f);
    noise.SetFractalLacunarity(2.0f);
    noise.SetFrequency(2.0f);
    noise.SetFractalType(FastNoise.FractalType.FBM);
    noise.SetNoiseType(FastNoise.NoiseType.PerlinFractal);
*/

/*
    noise.SetFrequency(2.0f);
    noise.SetFractalOctaves(4);
    noise.SetFractalGain(0.5f);
    noise.SetFractalLacunarity(2.0f);
    noise.SetFrequency(2.0f);
    noise.SetFractalType(FastNoise.FractalType.FBM);
    noise.SetNoiseType(FastNoise.NoiseType.SimplexFractal);
*/
/*
    noise.SetFrequency(2.0f);
    noise.SetFractalOctaves(4);
    noise.SetFractalGain(0.5f);
    noise.SetFractalLacunarity(2.0f);
    noise.SetFrequency(2.0f);
    noise.SetFractalType(FastNoise.FractalType.FBM);
    noise.SetNoiseType(FastNoise.NoiseType.CubicFractal);
*/


    noise.SetFrequency(4.0f);
    //public enum CellularReturnType {CellValue, NoiseLookup, Distance, Distance2, Distance2Add, Distance2Sub, Distance2Mul, Distance2Div}
    //noise.SetCellularReturnType(FastNoise.CellularReturnType.Distance);
    //noise.SetCellularReturnType(FastNoise.CellularReturnType.Distance2);
    noise.SetCellularReturnType(FastNoise.CellularReturnType.Distance2Mul);
    // public enum CellularDistanceFunction {Euclidean, Manhattan, Natural}
    noise.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);
    noise.SetNoiseType(FastNoise.NoiseType.Cellular);

// cell noise

  }

  @Override
  public double getValue(FlameTransformationContext pContext, double x, double y, double z) {
    /*
    v[0]=x;
    v[1]=y;
    v[2]=z;
    return NoiseTools.perlinNoise3D(v, aScale, fScale, octaves);
     */

    /*
			case Cellular:
				switch (m_cellularReturnType) {
					case CellValue:
					case NoiseLookup:
					case Distance:
						return SingleCellular(x, y, z);
					default:
						return SingleCellular2Edge(x, y, z);
				}
		}
     */

    return noise.GetNoise((float)x,(float)y,(float)z);
  }
}
