/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2022 Andreas Maschke

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

import org.jwildfire.create.tina.swing.TinaControllerContextService;
import org.jwildfire.create.tina.variation.iflames.IFlamesFunc;
import org.jwildfire.create.tina.variation.mesh.*;
import org.jwildfire.create.tina.variation.plot.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class
VariationFuncList {
  public static final String DEFAULT_VARIATION = "linear3D";
  private static final double VARIATION_COST_THRESHOLD = 0.15;
  private static final double MEMORY_THRESHOLD = 20.0;
  private static final Map<String, String> resolvedAliasMap;
  private static final Set<String> supportedVariations = new HashSet<>();
  public static boolean considerVariationCosts = true;
  private static final List<Class<? extends VariationFunc>> items = new ArrayList<Class<? extends VariationFunc>>();
  private static List<String> unfilteredNameList = null;
  private static List<String> nonInternalVariationsNameList = null;
  private static List<String> validatedRandomVariationsNameList = null;
  private static Map<String, Set<VariationFuncType>> variationTypes = null;
  private static Map<VariationFuncType, List<String>> variationsByType = null;
  private static final Map<Class<? extends VariationFunc>, String> aliasMap = new HashMap<Class<? extends VariationFunc>, String>();
  private static Map<String, Double> variationInitCosts;
  private static Map<String, Double> variationEvalCosts;

  static {
    initializeCostsMaps();
    // define alias for renamed variations to allow loading of old flame
    // files
    aliasMap.put(KaleidoscopeFunc.class, "kaleidoscope");
    aliasMap.put(MandelbrotFunc.class, "Mandelbrot");
    aliasMap.put(SpirographFunc.class, "Spirograph");
    aliasMap.put(TruchetFunc.class, "Truchet");
    aliasMap.put(GlynnSim1Func.class, "GlynnSim1");
    aliasMap.put(GlynnSim2Func.class, "GlynnSim2");
    aliasMap.put(GlynnSim3Func.class, "GlynnSim3");
    aliasMap.put(Spherical3DFunc.class, "Spherical3D");
    aliasMap.put(EpispiralFunc.class, "Epispiral");
    aliasMap.put(SphericalNFunc.class, "SphericalN");
    aliasMap.put(GlynniaFunc.class, "Glynnia");
    aliasMap.put(SuperShape3DFunc.class, "SuperShape3d");
    aliasMap.put(CustomWFFunc.class, "t_custom");
    aliasMap.put(EpispiralWFFunc.class, "t_epispiral");
    aliasMap.put(ColorScaleWFFunc.class, "t_colorscale");
    aliasMap.put(HeartWFFunc.class, "t_heart");
    aliasMap.put(Waves2WFFunc.class, "t_xy_waves");
    aliasMap.put(BubbleWFFunc.class, "t_bubble");
    aliasMap.put(PostBumpMapWFFunc.class, "t_post_bump_map");
    aliasMap.put(PostMirrorWFFunc.class, "t_post_mirror");
    aliasMap.put(PreWave3DWFFunc.class, "t_pre_wave3D");
    aliasMap.put(RoseWFFunc.class, "t_rose");
    aliasMap.put(SplitBrdrFunc.class, "SplitBrdr");
    aliasMap.put(JuliaCFunc.class, "Juliac");
    aliasMap.put(VoronFunc.class, "Voron");
    aliasMap.put(CrossFunc.class, "cross2");
    aliasMap.put(CircleLinearFunc.class, "CircleLinear");
    aliasMap.put(CircleRandFunc.class, "CircleRand");
    aliasMap.put(CircleTrans1Func.class, "CircleTrans1");
    aliasMap.put(MobiusNFunc.class, "MobiusN");
    aliasMap.put(Tile_LogFunc.class, "tile_log");
    aliasMap.put(DCTriTileFunc.class, "FiveFold");
    aliasMap.put(Rays1Func.class, "rays_rk1");
    aliasMap.put(Rays2Func.class, "rays_rk2");
    aliasMap.put(Rays3Func.class, "rays_rk3");
    aliasMap.put(SquareFunc.class, "blur_square");
    aliasMap.put(PreBlurFunc.class, "pre_gaussian");
    aliasMap.put(GaussianBlurFunc.class, "gaussian");

    //
    registerVariationFunc(LinearFunc.class);
    registerVariationFunc(SphericalFunc.class);
    registerVariationFunc(WavesFunc.class);
    registerVariationFunc(SwirlFunc.class);
    registerVariationFunc(SpiralFunc.class);
    registerVariationFunc(CurlFunc.class);
    registerVariationFunc(HeartFunc.class);
    registerVariationFunc(Linear3DFunc.class);
    registerVariationFunc(Curl3DFunc.class);
    registerVariationFunc(ZTranslateFunc.class);
    registerVariationFunc(ZScaleFunc.class);
    registerVariationFunc(JuliaNFunc.class);
    registerVariationFunc(SinusoidalFunc.class);
    registerVariationFunc(HorseshoeFunc.class);
    registerVariationFunc(PolarFunc.class);
    registerVariationFunc(HandkerchiefFunc.class);
    registerVariationFunc(DiscFunc.class);
    registerVariationFunc(HyperbolicFunc.class);
    registerVariationFunc(DiamondFunc.class);
    registerVariationFunc(BlurFunc.class);
    registerVariationFunc(ExFunc.class);
    registerVariationFunc(JuliaFunc.class);
    registerVariationFunc(BentFunc.class);
    registerVariationFunc(FisheyeFunc.class);
    registerVariationFunc(PopcornFunc.class);
    registerVariationFunc(ExponentialFunc.class);
    registerVariationFunc(PowerFunc.class);
    registerVariationFunc(CosineFunc.class);
    registerVariationFunc(RingsFunc.class);
    registerVariationFunc(FanFunc.class);
    registerVariationFunc(EyefishFunc.class);
    registerVariationFunc(BubbleFunc.class);
    registerVariationFunc(CylinderFunc.class);
    registerVariationFunc(NoiseFunc.class);
    registerVariationFunc(GaussianBlurFunc.class);
    registerVariationFunc(PreBlurFunc.class);
    registerVariationFunc(ZBlurFunc.class);
    registerVariationFunc(Blur3DFunc.class);
    registerVariationFunc(PreZScaleFunc.class);
    registerVariationFunc(PreZTranslateFunc.class);
    registerVariationFunc(PreRotateXFunc.class);
    registerVariationFunc(PreRotateYFunc.class);
    registerVariationFunc(PostRotateXFunc.class);
    registerVariationFunc(PostRotateYFunc.class);
    registerVariationFunc(ZConeFunc.class);
    registerVariationFunc(HemisphereFunc.class);
    registerVariationFunc(Rings2Func.class);
    registerVariationFunc(Rings3Func.class);
    registerVariationFunc(RingerFunc.class);
    registerVariationFunc(RectanglesFunc.class);
    registerVariationFunc(PDJFunc.class);
    registerVariationFunc(JuliaScopeFunc.class);
    registerVariationFunc(Julia3DFunc.class);
    registerVariationFunc(Julia3DZFunc.class);
    registerVariationFunc(Fan2Func.class);
    registerVariationFunc(RadialBlurFunc.class);
    registerVariationFunc(Spherical3DFunc.class);
    registerVariationFunc(BlobFunc.class);
    registerVariationFunc(Blob3DFunc.class);
    registerVariationFunc(PerspectiveFunc.class);
    registerVariationFunc(PieFunc.class);
    registerVariationFunc(Pie3DFunc.class);
    registerVariationFunc(ButterflyFunc.class);
    registerVariationFunc(Butterfly3DFunc.class);
    registerVariationFunc(ArchFunc.class);
    registerVariationFunc(SquareFunc.class);
    registerVariationFunc(Square3DFunc.class);
    registerVariationFunc(TangentFunc.class);
    registerVariationFunc(Tangent3DFunc.class);
    registerVariationFunc(BladeFunc.class);
    registerVariationFunc(Blade3DFunc.class);
    registerVariationFunc(CrossFunc.class);
    registerVariationFunc(BipolarFunc.class);
    registerVariationFunc(WedgeFunc.class);
    registerVariationFunc(ScryFunc.class);
    registerVariationFunc(Waves2Func.class);
    registerVariationFunc(Polar2Func.class);
    registerVariationFunc(Popcorn2Func.class);
    registerVariationFunc(Secant2Func.class);
    registerVariationFunc(NgonFunc.class);
    registerVariationFunc(EpispiralWFFunc.class);
    registerVariationFunc(RoseWFFunc.class);
    registerVariationFunc(RaysFunc.class);
    registerVariationFunc(TwintrianFunc.class);
    registerVariationFunc(FociFunc.class);
    registerVariationFunc(Disc2Func.class);
    registerVariationFunc(SuperShapeFunc.class);
    registerVariationFunc(FlowerFunc.class);
    registerVariationFunc(ConicFunc.class);
    registerVariationFunc(ParabolaFunc.class);
    registerVariationFunc(Bent2Func.class);
    registerVariationFunc(BoardersFunc.class);
    registerVariationFunc(CellFunc.class);
    registerVariationFunc(CPowFunc.class);
    registerVariationFunc(CurveFunc.class);
    registerVariationFunc(EDiscFunc.class);
    registerVariationFunc(EscherFunc.class);
    registerVariationFunc(LazySusanFunc.class);
    registerVariationFunc(ExpFunc.class);
    registerVariationFunc(LogFunc.class);
    registerVariationFunc(SinFunc.class);
    registerVariationFunc(CosFunc.class);
    registerVariationFunc(TanFunc.class);
    registerVariationFunc(SecFunc.class);
    registerVariationFunc(CscFunc.class);
    registerVariationFunc(CotFunc.class);
    registerVariationFunc(SinhFunc.class);
    registerVariationFunc(CoshFunc.class);
    registerVariationFunc(TanhFunc.class);
    registerVariationFunc(SechFunc.class);
    registerVariationFunc(CschFunc.class);
    registerVariationFunc(CothFunc.class);
    registerVariationFunc(ModulusFunc.class);
    registerVariationFunc(LoonieFunc.class);
    registerVariationFunc(OscilloscopeFunc.class);
    registerVariationFunc(SeparationFunc.class);
    registerVariationFunc(SplitFunc.class);
    registerVariationFunc(SplitsFunc.class);
    registerVariationFunc(StripesFunc.class);
    registerVariationFunc(WedgeJuliaFunc.class);
    registerVariationFunc(WedgeSphFunc.class);
    registerVariationFunc(WhorlFunc.class);
    registerVariationFunc(AugerFunc.class);
    registerVariationFunc(FluxFunc.class);
    registerVariationFunc(MobiusFunc.class);
    registerVariationFunc(CirclizeFunc.class);
    registerVariationFunc(PostMirrorWFFunc.class);
    registerVariationFunc(PreWave3DWFFunc.class);
    registerVariationFunc(BWraps7Func.class);
    registerVariationFunc(ColorScaleWFFunc.class);
    registerVariationFunc(HeartWFFunc.class);
    registerVariationFunc(XHeartFunc.class);
    registerVariationFunc(EllipticFunc.class);
    registerVariationFunc(Waves2WFFunc.class);
    registerVariationFunc(CropFunc.class);
    registerVariationFunc(PostCropFunc.class);
    registerVariationFunc(PreCropFunc.class);
    registerVariationFunc(CircleCropFunc.class);
    registerVariationFunc(PreCircleCropFunc.class);
    registerVariationFunc(PostCircleCropFunc.class);
    registerVariationFunc(HexesFunc.class);
    registerVariationFunc(CrackleFunc.class);
    registerVariationFunc(MandelbrotFunc.class);
    registerVariationFunc(BubbleWFFunc.class);
    registerVariationFunc(SpirographFunc.class);
    registerVariationFunc(CustomWFFunc.class);
    registerVariationFunc(TruchetFunc.class);
    registerVariationFunc(PostColorScaleWFFunc.class);
    registerVariationFunc(PostCustomWFFunc.class);
    registerVariationFunc(PreCustomWFFunc.class);
    registerVariationFunc(CloverLeafWFFunc.class);
    registerVariationFunc(CannabisCurveWFFunc.class);
    registerVariationFunc(SubFlameWFFunc.class);
    registerVariationFunc(PreSubFlameWFFunc.class);
    registerVariationFunc(KaleidoscopeFunc.class);
    registerVariationFunc(Waves3WFFunc.class);
    registerVariationFunc(Waves4WFFunc.class);
    registerVariationFunc(GlynnSim1Func.class);
    registerVariationFunc(GlynnSim2Func.class);
    registerVariationFunc(GlynnSim3Func.class);
    registerVariationFunc(PostColorMapWFFunc.class);
    registerVariationFunc(PostBumpMapWFFunc.class);
    registerVariationFunc(EclipseFunc.class);
    registerVariationFunc(Spherical3DWFFunc.class);
    registerVariationFunc(PostZTranslateWFFunc.class);
    registerVariationFunc(PostZScaleWFFunc.class);
    registerVariationFunc(NBlurFunc.class);
    registerVariationFunc(LinearTFunc.class);
    registerVariationFunc(LinearT3DFunc.class);
    registerVariationFunc(NPolarFunc.class);
    registerVariationFunc(LissajousFunc.class);
    registerVariationFunc(WaffleFunc.class);
    registerVariationFunc(UnpolarFunc.class);
    registerVariationFunc(ChecksFunc.class);
    registerVariationFunc(BilinearFunc.class);
    registerVariationFunc(FarBlurFunc.class);
    registerVariationFunc(Falloff2Func.class);
    registerVariationFunc(PostFalloff2Func.class);
    registerVariationFunc(FlipYFunc.class);
    registerVariationFunc(FlipCircleFunc.class);
    registerVariationFunc(EpispiralFunc.class);
    registerVariationFunc(DCCubeFunc.class);
    registerVariationFunc(LayeredSpiralFunc.class);
    registerVariationFunc(CollideoscopeFunc.class);
    registerVariationFunc(HypertileFunc.class);
    registerVariationFunc(Hypertile1Func.class);
    registerVariationFunc(Hypertile2Func.class);
    registerVariationFunc(DCPerlinFunc.class);
    registerVariationFunc(ColorMapWFFunc.class);
    registerVariationFunc(SphericalNFunc.class);
    registerVariationFunc(Waves2_3DFunc.class);
    registerVariationFunc(Scry3DFunc.class);
    registerVariationFunc(Foci_3DFunc.class);
    registerVariationFunc(InflateZ_1Func.class);
    registerVariationFunc(InflateZ_2Func.class);
    registerVariationFunc(InflateZ_3Func.class);
    registerVariationFunc(InflateZ_4Func.class);
    registerVariationFunc(InflateZ_5Func.class);
    registerVariationFunc(InflateZ_6Func.class);
    registerVariationFunc(Loonie_3DFunc.class);
    registerVariationFunc(PreSpinZFunc.class);
    registerVariationFunc(PostSpinZFunc.class);
    registerVariationFunc(RoundSpher3DFunc.class);
    registerVariationFunc(Cubic_3DFunc.class);
    registerVariationFunc(Disc3DFunc.class);
    registerVariationFunc(CubicLattice3DFunc.class);
    registerVariationFunc(Popcorn2_3DFunc.class);
    registerVariationFunc(Hexaplay3DFunc.class);
    registerVariationFunc(Hexnix3DFunc.class);
    registerVariationFunc(PhoenixJuliaFunc.class);
    registerVariationFunc(Boarders2Func.class);
    registerVariationFunc(PreBoarders2Func.class);
    registerVariationFunc(BCollideFunc.class);
    registerVariationFunc(BModFunc.class);
    registerVariationFunc(BSwirlFunc.class);
    registerVariationFunc(BTransformFunc.class);
    registerVariationFunc(GlynniaFunc.class);
    registerVariationFunc(CylinderApoFunc.class);
    registerVariationFunc(ECollideFunc.class);
    registerVariationFunc(EJuliaFunc.class);
    registerVariationFunc(EModFunc.class);
    registerVariationFunc(EMotionFunc.class);
    registerVariationFunc(EPushFunc.class);
    registerVariationFunc(ERotateFunc.class);
    registerVariationFunc(EScaleFunc.class);
    registerVariationFunc(ESwirlFunc.class);
    registerVariationFunc(DCZTranslFunc.class);
    registerVariationFunc(PreDCZTranslFunc.class);
    registerVariationFunc(PostDCZTranslFunc.class);
    registerVariationFunc(LogApoFunc.class);
    registerVariationFunc(RippleFunc.class);
    registerVariationFunc(JuliaN2Func.class);
    registerVariationFunc(PostBWraps2Func.class);
    registerVariationFunc(PreBWraps2Func.class);
    registerVariationFunc(PostCurlFunc.class);
    registerVariationFunc(PostCurl3DFunc.class);

    registerVariationFunc(FractDragonWFFunc.class);
    registerVariationFunc(FractMandelbrotWFFunc.class);
    registerVariationFunc(FractMeteorsWFFunc.class);
    registerVariationFunc(FractJuliaWFFunc.class);
    registerVariationFunc(FractPearlsWFFunc.class);
    registerVariationFunc(FractSalamanderWFFunc.class);

    registerVariationFunc(TextWFFunc.class);
    registerVariationFunc(XTrbFunc.class);
    registerVariationFunc(SuperShape3DFunc.class);
    registerVariationFunc(Fibonacci2Func.class);
    registerVariationFunc(Ovoid3DFunc.class);
    registerVariationFunc(HOFunc.class);
    registerVariationFunc(PRose3DFunc.class);
    registerVariationFunc(CircusFunc.class);
    registerVariationFunc(LazyTravisFunc.class);
    registerVariationFunc(Hypertile3DFunc.class);
    registerVariationFunc(Hypertile3D1Func.class);
    registerVariationFunc(Hypertile3D2Func.class);
    registerVariationFunc(Poincare3DFunc.class);
    registerVariationFunc(GDOffsFunc.class);
    registerVariationFunc(Sinusoidal3DFunc.class);
    registerVariationFunc(SVFFunc.class);
    registerVariationFunc(TaurusFunc.class);
    registerVariationFunc(PreSinusoidal3DFunc.class);
    registerVariationFunc(PreDisc3DFunc.class);
    registerVariationFunc(SintrangeFunc.class);
    registerVariationFunc(TargetFunc.class);
    registerVariationFunc(Bubble2Func.class);
    registerVariationFunc(BlockYFunc.class);
    registerVariationFunc(SplitBrdrFunc.class);
    registerVariationFunc(MCarpetFunc.class);
    registerVariationFunc(OctagonFunc.class);
    registerVariationFunc(FourthFunc.class);
    registerVariationFunc(TwoFaceFunc.class);
    registerVariationFunc(FractFormulaJuliaWFFunc.class);
    registerVariationFunc(FractFormulaMandWFFunc.class);
    registerVariationFunc(BarycentroidFunc.class);
    registerVariationFunc(SVGWFFunc.class);
    registerVariationFunc(JuliaCFunc.class);
    registerVariationFunc(JuliaQFunc.class);
    registerVariationFunc(Julia3DQFunc.class);
    registerVariationFunc(PostJuliaQFunc.class);
    registerVariationFunc(PostJulia3DQFunc.class);
    registerVariationFunc(PostSmartCropFunc.class);
    registerVariationFunc(CircleBlurFunc.class);
    registerVariationFunc(SineBlurFunc.class);
    registerVariationFunc(StarBlurFunc.class);
    registerVariationFunc(PostDepthFunc.class);
    registerVariationFunc(Grid3DWFFunc.class);
    registerVariationFunc(DisplacementMapWFFunc.class);
    registerVariationFunc(PostDisplacementMapWFFunc.class);
    registerVariationFunc(PostRBlurFunc.class);
    registerVariationFunc(ShredlinFunc.class);
    registerVariationFunc(PrimitivesWFFunc.class);
    registerVariationFunc(FlattenFunc.class);
    registerVariationFunc(BlurZoomFunc.class);
    registerVariationFunc(LazyJessFunc.class);
    registerVariationFunc(VoronFunc.class);
    registerVariationFunc(OrthoFunc.class);
    registerVariationFunc(SynthFunc.class);
    registerVariationFunc(OnionFunc.class);
    registerVariationFunc(BubbleT3DFunc.class);
    registerVariationFunc(ExtrudeFunc.class);
    registerVariationFunc(LineFunc.class);
    registerVariationFunc(Onion2Func.class);
    registerVariationFunc(DCCrackleWFFunc.class);
    registerVariationFunc(DCHexesWFFunc.class);
    registerVariationFunc(MurlFunc.class);
    registerVariationFunc(Murl2Func.class);
    registerVariationFunc(PreCurlFunc.class);
    registerVariationFunc(Falloff3Func.class);
    registerVariationFunc(PreFalloff3Func.class);
    registerVariationFunc(PostFalloff3Func.class);
    registerVariationFunc(DLAWFFunc.class);
    registerVariationFunc(JuliaN3DXFunc.class);
    registerVariationFunc(Waves2BFunc.class);
    registerVariationFunc(BWRandsFunc.class);
    registerVariationFunc(Loonie2Func.class);
    registerVariationFunc(Loonie3Func.class);
    registerVariationFunc(JacCnFunc.class);
    registerVariationFunc(JacDnFunc.class);
    registerVariationFunc(JacSnFunc.class);
    registerVariationFunc(TanCosFunc.class);
    registerVariationFunc(RippledFunc.class);
    registerVariationFunc(FunnelFunc.class);
    registerVariationFunc(RoundSpherFunc.class);
    registerVariationFunc(SpiralwingFunc.class);
    registerVariationFunc(Rays1Func.class);
    registerVariationFunc(Rays2Func.class);
    registerVariationFunc(Rays3Func.class);
    registerVariationFunc(PetalFunc.class);
    registerVariationFunc(EnnepersFunc.class);
    registerVariationFunc(SquirrelFunc.class);
    registerVariationFunc(Splits3DFunc.class);
    registerVariationFunc(SquarizeFunc.class);
    registerVariationFunc(SquishFunc.class);
    registerVariationFunc(Circlize2Func.class);
    registerVariationFunc(SphereNjaFunc.class);
    registerVariationFunc(Oscilloscope2Func.class);
    registerVariationFunc(Scry2Func.class);
    registerVariationFunc(ScramblyFunc.class);
    registerVariationFunc(InternalSliceRangeIndicatorWFFunc.class);
    registerVariationFunc(ZTwisterFunc.class);
    registerVariationFunc(AsteriaFunc.class);
    registerVariationFunc(LogDbFunc.class);
    registerVariationFunc(Sph3DFunc.class);
    registerVariationFunc(XHeartBlurWFFunc.class);
    registerVariationFunc(DinisSurfaceWFFunc.class);
    registerVariationFunc(PreRectWFFunc.class);
    registerVariationFunc(Ennepers2Func.class);
    registerVariationFunc(WhitneyUmbrellaFunc.class);
    registerVariationFunc(BSplitFunc.class);
    registerVariationFunc(PolylogarithmFunc.class);
    registerVariationFunc(DevilWarpFunc.class);
    registerVariationFunc(YinYangFunc.class);
    registerVariationFunc(TargetSpFunc.class);
    registerVariationFunc(ExBlurFunc.class);
    registerVariationFunc(PowBlockFunc.class);
    registerVariationFunc(PreBlur3DFunc.class);
    registerVariationFunc(VogelFunc.class);
    registerVariationFunc(IFlamesFunc.class);
    registerVariationFunc(CurlSpFunc.class);
    registerVariationFunc(DCLinearFunc.class);
    registerVariationFunc(DCCarpetFunc.class);
    registerVariationFunc(DCBubbleFunc.class);
    registerVariationFunc(DCTriangleFunc.class);
    registerVariationFunc(WaveBlurWFFunc.class);
    registerVariationFunc(PostHeatFunc.class);
    registerVariationFunc(CPow3Func.class);

    registerVariationFunc(Rational3Func.class);
    registerVariationFunc(STwinFunc.class);
    registerVariationFunc(FlowerDbFunc.class);
    registerVariationFunc(RosoniFunc.class);
    registerVariationFunc(Glynnia3Func.class);
    registerVariationFunc(MaskFunc.class);
    registerVariationFunc(FDiscFunc.class);
    registerVariationFunc(RhodoneaFunc.class);
    registerVariationFunc(ButterflyFayFunc.class);

    registerVariationFunc(BlurCircleFunc.class);
    registerVariationFunc(BlurPixelizeFunc.class);
    registerVariationFunc(CircleLinearFunc.class);
    registerVariationFunc(CircleRandFunc.class);
    registerVariationFunc(CircleTrans1Func.class);
    registerVariationFunc(IDiscFunc.class);
    registerVariationFunc(DeltaAFunc.class);
    registerVariationFunc(WDiscFunc.class);
    registerVariationFunc(TradeFunc.class);

    registerVariationFunc(WFunc.class);
    registerVariationFunc(XFunc.class);
    registerVariationFunc(YFunc.class);
    registerVariationFunc(ZFunc.class);
    registerVariationFunc(CustomFullVariationWrapperFunc.class);

    registerVariationFunc(PostPointSymmetryWFFunc.class);
    registerVariationFunc(PostAxisSymmetryWFFunc.class);
    registerVariationFunc(MobiusStripFunc.class);

    registerVariationFunc(YPlot2DWFFunc.class);
    registerVariationFunc(YPlot3DWFFunc.class);
    registerVariationFunc(PolarPlot2DWFFunc.class);
    registerVariationFunc(PolarPlot3DWFFunc.class);
    registerVariationFunc(ParPlot2DWFFunc.class);
    registerVariationFunc(MobiusNFunc.class);
    registerVariationFunc(OBJMeshWFFunc.class);
    registerVariationFunc(OBJMeshPrimitiveWFFunc.class);
    registerVariationFunc(PlaneWFFunc.class);
    registerVariationFunc(CheckerboardWFFunc.class);
    registerVariationFunc(IsoSFPlot3DWFFunc.class);

    registerVariationFunc(DLA3DWFFunc.class);

    registerVariationFunc(Waves2RadialFunc.class);
    registerVariationFunc(CirclesplitFunc.class);
    registerVariationFunc(LogTile2Func.class);
    registerVariationFunc(MobiqFunc.class);
    registerVariationFunc(TileHlpFunc.class);
    registerVariationFunc(SplipticBSFunc.class);
    registerVariationFunc(Tile_LogFunc.class);
    registerVariationFunc(Cos2_BSFunc.class);
    registerVariationFunc(Cot2_BSFunc.class);
    registerVariationFunc(Csc2_BSFunc.class);
    registerVariationFunc(Tan2_BSFunc.class);
    registerVariationFunc(Sec2_BSFunc.class);
    registerVariationFunc(Exp2_BSFunc.class);
    registerVariationFunc(Sin2_BSFunc.class);
    registerVariationFunc(Csch2_BSFunc.class);
    registerVariationFunc(Cosh2_BSFunc.class);
    registerVariationFunc(Sech2_BSFunc.class);
    registerVariationFunc(Coth2_BSFunc.class);
    registerVariationFunc(Sinh2_BSFunc.class);
    registerVariationFunc(Tanh2_BSFunc.class);
    registerVariationFunc(CosqFunc.class);
    registerVariationFunc(SinqFunc.class);
    registerVariationFunc(TanqFunc.class);
    registerVariationFunc(TanhqFunc.class);
    registerVariationFunc(CoshqFunc.class);
    registerVariationFunc(SinhqFunc.class);
    registerVariationFunc(CotqFunc.class);
    registerVariationFunc(CothqFunc.class);
    registerVariationFunc(CscqFunc.class);
    registerVariationFunc(CschqFunc.class);
    registerVariationFunc(EstiqFunc.class);
    registerVariationFunc(SecqFunc.class);
    registerVariationFunc(SechqFunc.class);
    registerVariationFunc(LoqFunc.class);
    registerVariationFunc(Spirograph3DFunc.class);
    registerVariationFunc(HypershiftFunc.class);
    registerVariationFunc(DSphericalFunc.class);
    registerVariationFunc(CircularFunc.class);
    registerVariationFunc(Circular2Func.class);
    registerVariationFunc(ErfFunc.class);
    registerVariationFunc(Erf3DFunc.class);
    registerVariationFunc(XErfFunc.class);
    registerVariationFunc(Pressure_WaveFunc.class);
    registerVariationFunc(AtanFunc.class);
    registerVariationFunc(HelixFunc.class);
    registerVariationFunc(HelicoidFunc.class);
    registerVariationFunc(GammaFunc.class);
    registerVariationFunc(ShiftFunc.class);
    registerVariationFunc(ChunkFunc.class);
    registerVariationFunc(CrobFunc.class);
    registerVariationFunc(Hole2Func.class);
    registerVariationFunc(CPow2Func.class);
    registerVariationFunc(GridoutFunc.class);
    registerVariationFunc(Gridout2Func.class);
    registerVariationFunc(BlurLinearFunc.class);
    registerVariationFunc(SigmoidFunc.class);
    registerVariationFunc(DCCracklePWFFunc.class);
    registerVariationFunc(MinkQMFunc.class);
    registerVariationFunc(MinkowskopeFunc.class);
    registerVariationFunc(R_CircleblurFunc.class);
    registerVariationFunc(TruchetAEFunc.class);
    registerVariationFunc(TruchetFillFunc.class);
    // registerVariationFunc(SphTiling3Func.class);
    registerVariationFunc(SphTiling3V2Func.class);
    registerVariationFunc(Panorama1Func.class);
    registerVariationFunc(Panorama2Func.class);

    registerVariationFunc(MaurerRoseFunc.class);
    registerVariationFunc(MaurerLinesFunc.class);
    registerVariationFunc(CPow3WFFunc.class);
    registerVariationFunc(DCCylinderFunc.class);
    registerVariationFunc(DCCylinder2Func.class);
    registerVariationFunc(Swirl3Func.class);
    registerVariationFunc(CardioidFunc.class);
    registerVariationFunc(ShredradFunc.class);
    registerVariationFunc(DCTriTileFunc.class);
    registerVariationFunc(JubiQFunc.class);
    registerVariationFunc(ComplexFunc.class);
    registerVariationFunc(QuaternionFunc.class);
    registerVariationFunc(Crop3DFunc.class);
    registerVariationFunc(SphereCropFunc.class);

    registerVariationFunc(LsystemFunc.class);
    registerVariationFunc(LSystem3DWFFunc.class);
    registerVariationFunc(HamidFunc.class);
    registerVariationFunc(TreeFunc.class);
    registerVariationFunc(BrownianFunc.class);

    registerVariationFunc(DragonFunc.class);
    registerVariationFunc(GosperIslandFunc.class);
    registerVariationFunc(HtreeFunc.class);
    registerVariationFunc(KochFunc.class);
    registerVariationFunc(RsquaresFunc.class);
    registerVariationFunc(HilbertFunc.class);

    registerVariationFunc(SattractorFunc.class);
    registerVariationFunc(WallPaperFunc.class);
    registerVariationFunc(HadamardFunc.class);
    registerVariationFunc(CrownFunc.class);
    registerVariationFunc(ApocarpetFunc.class);
    registerVariationFunc(InvTreeFunc.class);
    registerVariationFunc(SiercarpetFunc.class);
    registerVariationFunc(WoggleFunc.class);
    registerVariationFunc(LaceFunc.class);
    registerVariationFunc(HarmonographFunc.class);
    registerVariationFunc(CliffordFunc.class);
    registerVariationFunc(SvenssonFunc.class);
    registerVariationFunc(LorenzFunc.class);

    registerVariationFunc(PreStabilizeFunc.class);
    registerVariationFunc(PreSphericalFunc.class);
    registerVariationFunc(PostSphericalFunc.class);
    registerVariationFunc(PrePostMobiusFunc.class);
    registerVariationFunc(PrePostAffineFunc.class);
    registerVariationFunc(PrePostCirclizeFunc.class);
    registerVariationFunc(PTransformFunc.class);

    registerVariationFunc(InversionFunc.class);
    registerVariationFunc(KleinGroupFunc.class);
    registerVariationFunc(DCDModulusFunc.class);
    registerVariationFunc(DCGnarlyFunc.class);

    registerVariationFunc(Strange3DFunc.class);
    registerVariationFunc(DustPointFunc.class);
    registerVariationFunc(IconAttractorFunc.class);
    registerVariationFunc(ApollonyFunc.class);
    registerVariationFunc(ChrysanthemumFunc.class);
    registerVariationFunc(SeaShell3DFunc.class);
    registerVariationFunc(ThreePointIFSFunc.class);

    registerVariationFunc(Knots3DFunc.class);
    registerVariationFunc(RecurrencePlotFunc.class);
    registerVariationFunc(MacMillanFunc.class);


    registerVariationFunc(RingSubFlameFunc.class);
    registerVariationFunc(GlynnS3SubflWFFunc.class);
    registerVariationFunc(FracTerrain3DFunc.class);

    registerVariationFunc(Disc3Func.class);
    registerVariationFunc(DCKaleidotileFunc.class);
    registerVariationFunc(LazySensenFunc.class);
    registerVariationFunc(ProjectiveFunc.class);
    registerVariationFunc(LoziFunc.class);
    registerVariationFunc(Vibration2Func.class);
    registerVariationFunc(PulseFunc.class);

    registerVariationFunc(WangTilesFunc.class);

    registerVariationFunc(TapratsFunc.class);
    registerVariationFunc(GPatternFunc.class);
    registerVariationFunc(NSudokuFunc.class);

    registerVariationFunc(AnamorphCylFunc.class);
    registerVariationFunc(HyperbolicEllipseFunc.class);
    registerVariationFunc(SunFlowersFunc.class);
    registerVariationFunc(SunflowerVoroniFunc.class);
    registerVariationFunc(Cylinder2Func.class);
    registerVariationFunc(Affine3DFunc.class);
    registerVariationFunc(Q_odeFunc.class);

    registerVariationFunc(CurliecueFunc.class);
    registerVariationFunc(CurliecueFunc2.class);
    registerVariationFunc(SZubietaFunc.class);
    registerVariationFunc(StarFractalFunc.class);
    registerVariationFunc(TrianTruchetFunc.class);
    registerVariationFunc(ArcTruchetFunc.class);
    registerVariationFunc(Swirl3DWFFunc.class);
    registerVariationFunc(MandalaFunc.class);
    registerVariationFunc(Mandala2Func.class);
    registerVariationFunc(JacAsnFunc.class);
    registerVariationFunc(ColorDomainFunc.class);

    registerVariationFunc(InvSquircularFunc.class);
    registerVariationFunc(SquircularFunc.class);
    registerVariationFunc(PyramidFunc.class);
    registerVariationFunc(CornersFunc.class);
    registerVariationFunc(CscSquaredFunc.class);
    registerVariationFunc(Atan2_SpiralsFunc.class);
    registerVariationFunc(IntersectionFunc.class);
    registerVariationFunc(InvpolarFunc.class);
    registerVariationFunc(StripfitFunc.class);
    registerVariationFunc(Hypershift2Func.class);
    registerVariationFunc(HypercropFunc.class);
    registerVariationFunc(Truchet2Func.class);
    registerVariationFunc(ArcsinhFunc.class);
    registerVariationFunc(Arcsech2Func.class);
    registerVariationFunc(ArctanhFunc.class);
    registerVariationFunc(Inverted_JuliaFunc.class);
    registerVariationFunc(AcosechFunc.class);
    registerVariationFunc(AcothFunc.class);
    registerVariationFunc(AcoshFunc.class);
    registerVariationFunc(DucksFunc.class);
    registerVariationFunc(Bipolar2Func.class);
    registerVariationFunc(Elliptic2Func.class);
    registerVariationFunc(JuliaOutsideFunc.class);

    registerVariationFunc(Metaballs3DWFFunc.class);
    registerVariationFunc(PointGridWFFunc.class);
    registerVariationFunc(PointGrid3DWFFunc.class);

    registerVariationFunc(GLSLMandalaFunc.class);
    registerVariationFunc(GLSLApollonianFunc.class);
    registerVariationFunc(GLSLFractalDotsFunc.class);
    registerVariationFunc(GLSLCircuitsFunc.class);
    registerVariationFunc(GLSLMandelBox2DFunc.class);
    registerVariationFunc(GLSLHoshiFunc.class);
    registerVariationFunc(GLSLKaleidoComplexFunc.class);
    registerVariationFunc(GLSLStarsFieldFunc.class);
    registerVariationFunc(GLSLKaleidoscopicFunc.class);
    registerVariationFunc(GLSLRandomOctreeFunc.class);
    registerVariationFunc(GLSLAcrilicFunc.class);
    registerVariationFunc(GLSLCirclesBlueFunc.class);
    registerVariationFunc(GLSLKaliSetFunc.class);
    registerVariationFunc(GLSLKaliSet2Func.class);
    registerVariationFunc(GLSLGrid3DFunc.class);
    registerVariationFunc(GLSLHyperbolicFunc.class);
    registerVariationFunc(GLSLBaseFunc.class);
    registerVariationFunc(GLSLSquaresFunc.class);

    registerVariationFunc(Tile_Reverse_Func.class);
    registerVariationFunc(Sqrt_AcothFunc.class);
    registerVariationFunc(Sqrt_AcoshFunc.class);
    registerVariationFunc(Sqrt_AsechFunc.class);
    registerVariationFunc(Sqrt_AtanhFunc.class);
    registerVariationFunc(Sqrt_AsinhFunc.class);
    registerVariationFunc(Sqrt_AcosechFunc.class);
    registerVariationFunc(HolesqFunc.class);
    registerVariationFunc(SpligonFunc.class);
    registerVariationFunc(HenonFunc.class);
    registerVariationFunc(PlusRecipFunc.class);
    registerVariationFunc(TQMirrorFunc.class);
    registerVariationFunc(DCCarpet3DFunc.class);
    registerVariationFunc(ConeFunc.class);
    registerVariationFunc(Gridout3DFunc.class);

    registerVariationFunc(DC_MandelBox2DFunc.class);
    registerVariationFunc(DC_MandalaFunc.class);
    registerVariationFunc(DC_ApollonianFunc.class);
    registerVariationFunc(DC_FractalDotsFunc.class);
    registerVariationFunc(DC_CircuitsFunc.class);
    registerVariationFunc(DC_HoshiFunc.class);
    registerVariationFunc(DC_KaleidoComplexFunc.class);
    registerVariationFunc(DC_StarsFieldFunc.class);
    registerVariationFunc(DC_KaleidoscopicFunc.class);
    registerVariationFunc(DC_RandomOctreeFunc.class);
    registerVariationFunc(DC_AcrilicFunc.class);
    registerVariationFunc(DC_CirclesBlueFunc.class);
    registerVariationFunc(DC_KaliSetFunc.class);
    registerVariationFunc(DC_KaliSet2Func.class);
    registerVariationFunc(DC_Grid3DFunc.class);
    registerVariationFunc(DC_HyperbolicFunc.class);
    registerVariationFunc(DC_SquaresFunc.class);
    registerVariationFunc(DC_MandBrotFunc.class);
    registerVariationFunc(DC_DucksFunc.class);
    registerVariationFunc(DC_LayersFunc.class);
    registerVariationFunc(DC_TreeFunc.class);
    registerVariationFunc(DC_TeslaFunc.class);
    registerVariationFunc(DC_RotationsFunc.class);
    registerVariationFunc(DC_HexagonsFunc.class);
    registerVariationFunc(DC_TurbulenceFunc.class);
    registerVariationFunc(DC_MengerFunc.class);
    registerVariationFunc(DC_TruchetFunc.class);
    registerVariationFunc(DC_VoronoiseFunc.class);
    registerVariationFunc(DC_CodeFunc.class);

    registerVariationFunc(GlynnLissaFunc.class);
    registerVariationFunc(GlynnSpiroGraf3DFunc.class);
    registerVariationFunc(GlynnSuperShapeFunc.class);

    registerVariationFunc(PreRecipFunc.class);

    registerVariationFunc(JuliaScope3DbFunc.class);
    registerVariationFunc(DC_TrianTessFunc.class);
    registerVariationFunc(DC_PoincareDiscFunc.class);
    registerVariationFunc(DC_WorleyFunc.class);
    registerVariationFunc(DC_GlyphoFunc.class);
    registerVariationFunc(DC_FingerPrintFunc.class);
    registerVariationFunc(DC_PentaTilesFunc.class);
    registerVariationFunc(DC_QuadtreeFunc.class);
    registerVariationFunc(DC_SunFlowerFunc.class);
    registerVariationFunc(DC_GaborNoiseFunc.class);
    registerVariationFunc(DC_CairoTilesFunc.class);
    registerVariationFunc(DC_MoebiusLogFunc.class);
    registerVariationFunc(DC_InversionFunc.class);

    registerVariationFunc(Waves22Func.class);
    registerVariationFunc(Waves23Func.class);
    registerVariationFunc(Waves3Func.class);
    registerVariationFunc(Waves42Func.class);
    registerVariationFunc(Waves4Func.class);

    registerVariationFunc(HexModulusFunc.class);
    registerVariationFunc(TruchetHexCropFunc.class);
    registerVariationFunc(TruchetHexFillFunc.class);

    registerVariationFunc(ExpMultiFunc.class);
    registerVariationFunc(ParallelFunc.class);

    registerVariationFunc(MSTruchetFunc.class);
    registerVariationFunc(F_ComplexFunc.class);
    registerVariationFunc(TruchetFlowFunc.class);

    registerVariationFunc(CutTruchetFunc.class);
    registerVariationFunc(CutFingerPrintFunc.class);
    registerVariationFunc(CutGlyphoFunc.class);
    registerVariationFunc(CutMetaBallsFunc.class);

    registerVariationFunc(CutBricksFunc.class);
    registerVariationFunc(CutShapesFunc.class);
    registerVariationFunc(CutKaleidoFunc.class);
    registerVariationFunc(CutPatternFunc.class);
    registerVariationFunc(CutFunFunc.class);
    registerVariationFunc(CutSnowflakeFunc.class);
    registerVariationFunc(CutCircleDesignFunc.class);
    registerVariationFunc(CutSinCosFunc.class);
    registerVariationFunc(CutSWarpFunc.class);
    registerVariationFunc(CutSqSplitsFunc.class);
    registerVariationFunc(CutSqCirFunc.class);
    registerVariationFunc(ShreddedFunc.class);

    registerVariationFunc(CutRGridFunc.class);
    registerVariationFunc(CutYuebingFunc.class);
    registerVariationFunc(CutRandomTileFunc.class);
    registerVariationFunc(CutTriScaleTruchetFunc.class);
    registerVariationFunc(CutAlienTextFunc.class);
    registerVariationFunc(CutTrianTessFunc.class);
    registerVariationFunc(Cut2eWangTileFunc.class);

    registerVariationFunc(CutBooleansFunc.class);
    registerVariationFunc(CutApollonianFunc.class);
    registerVariationFunc(CutTruchetWeavingFunc.class);
    registerVariationFunc(CutHexTruchetFlowFunc.class);
    registerVariationFunc(CutHexDotsFunc.class);
    registerVariationFunc(CutJigsawFunc.class);
    registerVariationFunc(CutChainsFunc.class);
    registerVariationFunc(CutWoodFunc.class);

    registerVariationFunc(DC_BooleansFunc.class);
    registerVariationFunc(DC_SpacefoldFunc.class);
    registerVariationFunc(DC_ButerfliesFunc.class);

    registerVariationFunc(CutTileIllusionFunc.class);
    registerVariationFunc(DC_FractColorFunc.class);
    registerVariationFunc(CutFractalFunc.class);
    registerVariationFunc(CutZigZagFunc.class);
    registerVariationFunc(CutXFunc.class);
    registerVariationFunc(CutSpiralFunc.class);
    registerVariationFunc(CutSpiralCBFunc.class);
    registerVariationFunc(JoukowskiFunc.class);
    registerVariationFunc(JacElkFunc.class);
    registerVariationFunc(ZSymmetryFunc.class);
    registerVariationFunc(CutCelticFunc.class);
    registerVariationFunc(CutTriskelFunc.class);

    registerVariationFunc(CutVasarelyFunc.class);
    registerVariationFunc(CutWebFunc.class);
    registerVariationFunc(CutSpotsFunc.class);
    registerVariationFunc(CutBasicTruchetFunc.class);
    registerVariationFunc(CutZFunc.class);
    registerVariationFunc(ZVarFunc.class);

    registerVariationFunc(CutMagFieldFunc.class);
    registerVariationFunc(CutBTreeFunc.class);
    registerVariationFunc(OctapolFunc.class);
    registerVariationFunc(PostCrosscropFunc.class);
    registerVariationFunc(CSinFunc.class);
    registerVariationFunc(SinusGridFunc.class);

    registerVariationFunc(CombimirrorFunc.class);



    registerVariationFunc(GumowskiMiraFunc.class);

    registerVariationFunc(HopalongFunc.class);
    registerVariationFunc(GingerBreadManFunc.class);
    registerVariationFunc(ThreeplyFunc.class);

    registerVariationFunc(TriangleFunc.class);
    registerVariationFunc(CropTriangleFunc.class);
    registerVariationFunc(PostCropTriangleFunc.class);

    registerVariationFunc(CropPolygonFunc.class);
    registerVariationFunc(PostCropPolygonFunc.class);

    registerVariationFunc(CropRhombusFunc.class);
    registerVariationFunc(PostCropRhombusFunc.class);

    registerVariationFunc(CropStarsFunc.class);
    registerVariationFunc(PostCropStarsFunc.class);

    registerVariationFunc(CropTrapezoidFunc.class);
    registerVariationFunc(PostCropTrapezoidFunc.class);

    registerVariationFunc(CropCrossFunc.class);
    registerVariationFunc(PostCropCrossFunc.class);

    registerVariationFunc(CropBoxFunc.class);
    registerVariationFunc(PostCropBoxFunc.class);

    registerVariationFunc(CropVesicaFunc.class);
    registerVariationFunc(PostCropVesicaFunc.class);

    registerVariationFunc(CropXFunc.class);
    registerVariationFunc(PostCropXFunc.class);

    registerVariationFunc(PreZSymmetryFunc.class);
    registerVariationFunc(PostZSymmetryFunc.class);

    registerVariationFunc(PreZVarFunc.class);
    registerVariationFunc(PostZVarFunc.class);

    registerVariationFunc(PixelFlowFunc.class);
    registerVariationFunc(Glitchy1Func.class);

    registerVariationFunc(DC_WarpingFunc.class);
    registerVariationFunc(DC_VortexFunc.class);
    registerVariationFunc(VortexFunc.class);

    registerVariationFunc(Box3DFunc.class);
    registerVariationFunc(RoundBox3DFunc.class);
    registerVariationFunc(BoundingBox3DFunc.class);

    registerVariationFunc(Rhombus3DFunc.class);
    registerVariationFunc(Ellipsoid3DFunc.class);
    registerVariationFunc(Torus3DFunc.class);

    registerVariationFunc(HexPrism3DFunc.class);
    registerVariationFunc(Cylinder3DFunc.class);
    registerVariationFunc(OrientedCylinder3DFunc.class);
    registerVariationFunc(OctogonPrism3DFunc.class);

    registerVariationFunc(Pyramid3DFunc.class);
    registerVariationFunc(Octahedron3DFunc.class);
    registerVariationFunc(SolidAngle3DFunc.class);

    registerVariationFunc(TriPrism3DFunc.class);
    registerVariationFunc(CappedTorus3DFunc.class);
    registerVariationFunc(CappedCone3DFunc.class);
    registerVariationFunc(OrientedCappedCone3DFunc.class);
    registerVariationFunc(Cone3DFunc.class);

    registerVariationFunc(OrientedRoundCone3DFunc.class);
    registerVariationFunc(Capsule3DFunc.class);

    registerVariationFunc(PostTrigFunc.class);
    registerVariationFunc(PostLogTile2Func.class);

    registerVariationFunc(Glitchy2Func.class);

    registerVariationFunc(Hypertile3DbFunc.class);
    registerVariationFunc(Hypertile3D2bFunc.class);
    registerVariationFunc(PDJ3DFunc.class);
    registerVariationFunc(Petal3DFunc.class);
    registerVariationFunc(Petal3DApoFunc.class);
    registerVariationFunc(CutKleinianFunc.class);
    registerVariationFunc(CutMandalaFunc.class);

    registerVariationFunc(SnowflakeWFFunc.class);

    registerVariationFunc(DC_PortalFunc.class);
    registerVariationFunc(KaplanFunc.class);
    registerVariationFunc(KaleidoImgFunc.class);

    registerVariationFunc(PostMandelbulb3dCropFunc.class);
    registerVariationFunc(PostMandelbox3dCropFunc.class);

    registerVariationFunc(FresnelFunc.class);
    registerVariationFunc(TunnelFunc.class);
    registerVariationFunc(BulgeFunc.class);

    registerVariationFunc(PostAexion3dCropFunc.class);
    registerVariationFunc(PostBulbTorusCropFunc.class);
    registerVariationFunc(PostBristorbrotCropFunc.class);
    registerVariationFunc(PostBenesiCropFunc.class);
    registerVariationFunc(PostCoastalbrotCropFunc.class);
    registerVariationFunc(PostAmazingSurfCropFunc.class);
    registerVariationFunc(DC_GenericMandelbrootFunc.class);

    registerVariationFunc(SymNetG1Func.class);
    registerVariationFunc(SymNetG2Func.class);
    registerVariationFunc(SymNetG3Func.class);
    registerVariationFunc(SymNetG4Func.class);
    registerVariationFunc(SymNetG5Func.class);
    registerVariationFunc(SymNetG6Func.class);
    registerVariationFunc(SymNetG7Func.class);
    registerVariationFunc(SymNetG8Func.class);
    registerVariationFunc(SymNetG9Func.class);
    registerVariationFunc(SymNetG10Func.class);
    registerVariationFunc(SymNetG11Func.class);

    registerVariationFunc(SymNetG12Func.class);

    registerVariationFunc(SymNetG13Func.class);
    registerVariationFunc(SymNetG14Func.class);
    registerVariationFunc(SymNetG15Func.class);

    registerVariationFunc(SymNetG16Func.class);
    registerVariationFunc(SymNetG17Func.class);


    registerVariationFunc(SymBandG1Func.class);
    registerVariationFunc(SymBandG2Func.class);
    registerVariationFunc(SymBandG3Func.class);
    registerVariationFunc(SymBandG4Func.class);
    registerVariationFunc(SymBandG5Func.class);
    registerVariationFunc(SymBandG6Func.class);
    registerVariationFunc(SymBandG7Func.class);

    registerVariationFunc(OvoidFunc.class);
    registerVariationFunc(PostPointCropFunc.class);
    registerVariationFunc(HoleFunc.class);
    registerVariationFunc(PreFlattenFunc.class);
    registerVariationFunc(PostFlattenFunc.class);
    registerVariationFunc(PreDiscFunc.class);
    registerVariationFunc(PostRotateZFunc.class);
    registerVariationFunc(MapleLeafFunc.class);
    registerVariationFunc(OakLeafFunc.class);
    registerVariationFunc(JapaneseMapleLeafFunc.class);
    registerVariationFunc(Mobius3DWithInverseFunc.class);
    registerVariationFunc(BrushStrokeWFFunc.class);
    registerVariationFunc(PostBrushStrokeWFFunc.class);
    
    registerVariationFunc(Cell2Func.class);

    registerVariationFunc(Hourglass3DFunc.class);
    registerVariationFunc(PreAffine3DFunc.class);
    registerVariationFunc(PostAffine3DFunc.class);

    registerVariationFunc(MobiusDragon3DFunc.class);
    
    registerVariationFunc(QuadFunc.class);
    registerVariationFunc(RingTileFunc.class);
    registerVariationFunc(WhirligigFunc.class);
    registerVariationFunc(TessShapeFunc.class);
    registerVariationFunc(KIFS3DFunc.class);
    registerVariationFunc(GlynnSim2BFunc.class);
    registerVariationFunc(ShapeWarpFunc.class);
    registerVariationFunc(DrunkenTilesFunc.class);
    registerVariationFunc(ConicalSpiralFunc.class);
    registerVariationFunc(BoxfoldFunc.class);
    registerVariationFunc(Cell3DFunc.class);
    registerVariationFunc(GeometricPrimitivesFunc.class);
    registerVariationFunc(CactusGlobeFunc.class);
    registerVariationFunc(JuliaScopePlusFunc.class);

    

    resolvedAliasMap = new HashMap<>();
    for (Entry<Class<? extends VariationFunc>, String> funcCls : aliasMap.entrySet()) {
      VariationFunc varFunc = getVariationInstance(funcCls.getKey(), false);
      if (varFunc != null) {
        resolvedAliasMap.put(funcCls.getValue(), varFunc.getName());
      }
    }
  }

  private static void initializeCostsMaps() {
    variationInitCosts = new HashMap<>();
    variationEvalCosts = new HashMap<>();
    try {
      InputStream inputStream =  VariationFuncList.class.getResourceAsStream("variation_costs.txt");
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      String line;
      // skip first line
      bufferedReader.readLine();

      while ((line = bufferedReader.readLine()) != null) {
        try {
          StringTokenizer tokenizer = new StringTokenizer(line, ";", false);
          String variationName = tokenizer.nextToken().trim();
          Double evalCostMs = Double.parseDouble(tokenizer.nextToken().trim());
          Double initCostMs = Double.parseDouble(tokenizer.nextToken().trim());
          Double evalMemoryKb = Double.parseDouble(tokenizer.nextToken().trim());
          Double initMemoryKb = Double.parseDouble(tokenizer.nextToken().trim());
          Boolean evalError = Boolean.parseBoolean(tokenizer.nextToken().trim());
          Boolean initError = Boolean.parseBoolean(tokenizer.nextToken().trim());

          double estimatedEvalCost = evalError ? -1.0 : evalMemoryKb > MEMORY_THRESHOLD ? -1.0 : evalCostMs;
          variationEvalCosts.put(variationName, estimatedEvalCost);

          double estimatedInitCost = initError ? -1.0 : initMemoryKb > MEMORY_THRESHOLD ? -1.0 : initCostMs;
          variationInitCosts.put(variationName, estimatedInitCost);
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      inputStream.close();
    } catch (Exception e) {
      e.printStackTrace();
      considerVariationCosts = false;
    }
  }

  public static double getVariationEvalCost(String variationName) {
    Double cost = Optional.ofNullable(variationEvalCosts.get(variationName)).orElse(-1.0);
    return cost > 0.0 ? cost : Double.MAX_VALUE;
  }

  public static double getVariationInitCost(String variationName) {
    Double cost = Optional.ofNullable(variationInitCosts.get(variationName)).orElse(-1.0);
    return cost > 0.0 ? cost : Double.MAX_VALUE;
  }

  private static void registerVariationFunc(
          Class<? extends VariationFunc> pVariationFunc) {
    items.add(pVariationFunc);
    unfilteredNameList = null;
  }

  private static VariationFunc getVariationInstance(
          Class<? extends VariationFunc> pFuncCls, boolean pFatal) {
    try {
      return pFuncCls.newInstance();
    } catch (InstantiationException ex) {
      if (pFatal) {
        throw new RuntimeException(ex);
      } else {
        ex.printStackTrace();
      }
    } catch (IllegalAccessException ex) {
      if (pFatal) {
        throw new RuntimeException(ex);
      } else {
        ex.printStackTrace();
      }
    }
    return null;
  }

  private static void refreshNameList() {
    unfilteredNameList = new ArrayList<>();
    nonInternalVariationsNameList = new ArrayList<>();
    variationTypes = new HashMap<>();
    for (Class<? extends VariationFunc> funcCls : items) {
      VariationFunc varFunc = getVariationInstance(funcCls, false);
      if (varFunc != null) {
        String vName = varFunc.getName();
        unfilteredNameList.add(vName);
        if (!vName.startsWith("_")) {
          nonInternalVariationsNameList.add(vName);
        }
        variationTypes.put(vName, new HashSet<>(Arrays.asList(varFunc.getVariationTypes())));
      }
    }

    validatedRandomVariationsNameList = new ArrayList<>();
    for(String name:nonInternalVariationsNameList) {
      if (isValidRandomVariation(name)) {
        validatedRandomVariationsNameList.add(name);
      }
    }

    variationsByType = new HashMap<>();
    for(String name: validatedRandomVariationsNameList) {
      for(VariationFuncType variationFuncType: variationTypes.get(name)) {
        List<String> variations = variationsByType.get(variationFuncType);
        if(variations==null) {
          variations = new ArrayList<>();
          variationsByType.put(variationFuncType, variations);
        }
        variations.add(name);
      }
    }
  }

  public static List<String> getNameList() {
    if (nonInternalVariationsNameList == null) {
      refreshNameList();
    }
    return nonInternalVariationsNameList;
  }

  private static List<String> getUnfilteredNameList() {
    if (unfilteredNameList == null) {
      refreshNameList();
    }
    return unfilteredNameList;
  }

  public static Map<String, String> getAliasMap() {
    return resolvedAliasMap;
  }

  public static String getRandomVariationname() {
    if(TinaControllerContextService.getContext().isGpuMode()) {
      return internalGetRandomVariationname(VariationFuncType.VARTYPE_SUPPORTS_GPU);
    } else {
      int idx =
          Math.min(
              (int) (Math.random() * getRandomVariationnames().size()),
              getRandomVariationnames().size() - 1);
      return getRandomVariationnames().get(idx);
    }
  }

  public static String getRandomVariationname(VariationFuncType variationFuncType) {
    if(TinaControllerContextService.getContext().isGpuMode()) {
      return internalGetRandomVariationname(VariationFuncType.VARTYPE_SUPPORTS_GPU, variationFuncType);
    }
    else {
      return internalGetRandomVariationname(variationFuncType);
    }
  }

  private static String internalGetRandomVariationname(VariationFuncType variationFuncType) {
    if(variationsByType==null) {
      refreshNameList();
    }
    List<String> variations = variationsByType.get(variationFuncType);
    int idx = Math.min((int) (Math.random() * variations.size()), variations.size()-1);
    return idx >=0 ? variations.get(idx) : DEFAULT_VARIATION;
  }


  private static String internalGetRandomVariationname(VariationFuncType variationFuncType1,VariationFuncType variationFuncType2) {
    if(variationsByType==null) {
      refreshNameList();
    }
    List<String> variationsType1 = variationsByType.get(variationFuncType1);
    List<String> variationsType2 = variationsByType.get(variationFuncType2);
    List<String> variations = variationsType1.stream().filter(variationsType2::contains).collect(Collectors.toList());
    int idx = Math.min((int) (Math.random() * variations.size()), variations.size()-1);
    return idx >=0 ? variations.get(idx) : DEFAULT_VARIATION;
  }

  public static List<String> getRandomVariationnames() {
    if(validatedRandomVariationsNameList==null) {
      refreshNameList();
    }
    return validatedRandomVariationsNameList;
  }

  public static boolean isValidRandomVariation(String name) {
    return (((!considerVariationCosts || getVariationEvalCost(name) < VARIATION_COST_THRESHOLD) || Math.random()<0.1) &&
           !(name.indexOf("inflate") == 0) && !name.equals("svg_wf") && !(name.indexOf("post_") == 0) && !(name.indexOf("pre_") == 0)
           && !(name.indexOf("prepost_") == 0) && !name.equals("iflames_wf") && !name.equals("flatten") && !(name.indexOf("colorscale") >= 0))
           && (VariationFuncList.supportedVariations.isEmpty() || VariationFuncList.supportedVariations.contains(name));
  }

  public static boolean isValidVariationForWeightingFields(String name) {
    return getVariationEvalCost(name) < VARIATION_COST_THRESHOLD && getVariationInitCost(name) < VARIATION_COST_THRESHOLD;
  }

  public static VariationFunc getVariationFuncInstance(String pName) {
    return getVariationFuncInstance(pName, false);
  }

  public static VariationFunc getVariationFuncInstance(String pName,
                                                       boolean pFatal) {
    int idx = getUnfilteredNameList().indexOf(pName);
    if (idx >= 0) {
      Class<? extends VariationFunc> funcCls = items.get(idx);
      try {
        VariationFunc func = funcCls.newInstance();
        return func;
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
    if (pFatal) {
      throw new RuntimeException("Variation \"" + pName + "\" could not be found");
    }
    return null;
  }
  public static boolean hasVariation(String pName) {
    return getUnfilteredNameList().indexOf(pName)  >= 0;
  }

  public static List<Class<? extends VariationFunc>> getVariationClasses() {
    return items;
  }

  public static Set<VariationFuncType> getVariationTypes(String variationName) {
    if (nonInternalVariationsNameList == null) {
      refreshNameList();
    }
    return variationTypes.get(variationName);
  }

  public static String[] filterVariations(String[] source) {
    List<String> variations = new ArrayList<>();
    Arrays.stream(source).forEach(name -> {if(isValidRandomVariation(name)) {
        variations.add(name);
      }
    });
    if(source.length>0 && variations.isEmpty()) {
      variations.add(new Linear3DFunc().getName());
    }
    return variations.toArray(new String[variations.size()]);
  }

  public static void setSupportedVariations(List<String> supportedVariations) {
    VariationFuncList.supportedVariations.clear();
    if (supportedVariations != null && supportedVariations.isEmpty()) {
      VariationFuncList.supportedVariations.addAll(supportedVariations);
    }
    VariationFuncList.refreshNameList();
  }
}

