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

import java.util.ArrayList;
import java.util.List;

public class VariationFuncList {
  public static final String DEFAULT_VARIATION = "linear3D";
  private static List<Class<? extends VariationFunc>> items = new ArrayList<Class<? extends VariationFunc>>();
  private static List<String> nameList = new ArrayList<String>();

  static {
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
    registerVariationFunc(TEpispiralFunc.class);
    registerVariationFunc(TRoseFunc.class);
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
    registerVariationFunc(TPostMirrorFunc.class);
    registerVariationFunc(TPreWave3DFunc.class);
    registerVariationFunc(BubbleWrapFunc.class);
    registerVariationFunc(TColorScaleFunc.class);
    registerVariationFunc(THeartFunc.class);
    registerVariationFunc(XHeartFunc.class);
    registerVariationFunc(EllipticFunc.class);
    registerVariationFunc(TXYWavesFunc.class);
    registerVariationFunc(CropFunc.class);
    registerVariationFunc(PostCropFunc.class);
    registerVariationFunc(PreCropFunc.class);
    registerVariationFunc(CircleCropFunc.class);
    registerVariationFunc(PreCircleCropFunc.class);
    registerVariationFunc(PostCircleCropFunc.class);
    registerVariationFunc(HexesFunc.class);
    registerVariationFunc(CrackleFunc.class);
    //    registerVariationFunc(TPostBumpMapFunc.class);
  }

  private static void registerVariationFunc(Class<? extends VariationFunc> pVariationFunc) {
    items.add(pVariationFunc);
    nameList = null;
  }

  public static List<String> getNameList() {
    if (nameList == null) {
      nameList = new ArrayList<String>();
      for (Class<? extends VariationFunc> funcCls : items) {
        try {
          nameList.add(funcCls.newInstance().getName());
        }
        catch (InstantiationException e) {
          e.printStackTrace();
        }
        catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
    return nameList;
  }

  public static VariationFunc getVariationFuncInstance(String pName) {
    return getVariationFuncInstance(pName, false);
  }

  public static VariationFunc getVariationFuncInstance(String pName, boolean pFatal) {
    int idx = getNameList().indexOf(pName);
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
      throw new IllegalArgumentException(pName);
    }
    return null;
  }

}
