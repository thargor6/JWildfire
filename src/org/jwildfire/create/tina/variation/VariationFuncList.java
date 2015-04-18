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

package org.jwildfire.create.tina.variation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jwildfire.create.tina.variation.iflames.IFlamesFunc;

public class VariationFuncList {
  public static final String DEFAULT_VARIATION = "linear3D";
  private static List<Class<? extends VariationFunc>> items = new ArrayList<Class<? extends VariationFunc>>();
  private static List<String> unfilteredNameList = null;
  private static List<String> filteredNameList = null;
  private static Map<Class<? extends VariationFunc>, String> aliasMap = new HashMap<Class<? extends VariationFunc>, String>();
  private static Map<String, String> resolvedAliasMap = null;

  static {
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

  }

  private static void registerVariationFunc(
      Class<? extends VariationFunc> pVariationFunc) {
    items.add(pVariationFunc);
    unfilteredNameList = null;
  }

  private static String getVariationName(
      Class<? extends VariationFunc> pFuncCls, boolean pFatal) {
    try {
      return pFuncCls.newInstance().getName();
    }
    catch (InstantiationException ex) {
      if (pFatal) {
        throw new RuntimeException(ex);
      }
      else {
        ex.printStackTrace();
      }
    }
    catch (IllegalAccessException ex) {
      if (pFatal) {
        throw new RuntimeException(ex);
      }
      else {
        ex.printStackTrace();
      }
    }
    return null;
  }

  private static void refreshNameList() {
    unfilteredNameList = new ArrayList<String>();
    filteredNameList = new ArrayList<String>();
    for (Class<? extends VariationFunc> funcCls : items) {
      String vName = getVariationName(funcCls, false);
      if (vName != null) {
        unfilteredNameList.add(vName);
        if (!vName.startsWith("_")) {
          filteredNameList.add(vName);
        }
      }
    }
  }

  public static List<String> getNameList() {
    if (filteredNameList == null) {
      refreshNameList();
    }
    return filteredNameList;
  }

  private static List<String> getUnfilteredNameList() {
    if (unfilteredNameList == null) {
      refreshNameList();
    }
    return unfilteredNameList;
  }

  public static Map<String, String> getAliasMap() {
    if (resolvedAliasMap == null) {
      resolvedAliasMap = new HashMap<String, String>();
      for (Class<? extends VariationFunc> funcCls : aliasMap.keySet()) {
        String vName = getVariationName(funcCls, false);
        if (vName != null) {
          resolvedAliasMap.put(aliasMap.get(funcCls), vName);
        }
      }
    }
    return resolvedAliasMap;
  }

  public static String getRandomVariationname() {
    while (true) {
      int idx = (int) (Math.random() * getNameList().size());
      String name = getNameList().get(idx);
      if (!(name.indexOf("inflate") == 0) && !name.equals("svg_wf") && !(name.indexOf("post_") == 0) && !(name.indexOf("pre_") == 0) && !(name.indexOf("iflames_wf") > 0)) {
        return name;
      }
    }
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
      }
      catch (Throwable e) {
        e.printStackTrace();
      }
    }
    if (pFatal) {
      throw new RuntimeException("Variation \"" + pName + "\" could not be found");
    }
    return null;
  }

}
