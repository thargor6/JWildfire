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
package org.jwildfire.transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class TransformersList {
  private static List<Class<? extends Transformer>> items = new ArrayList<Class<? extends Transformer>>();
  private static final Vector<String> itemVector;

  private static void registerCreator(Class<? extends Transformer> pCreator) {
    items.add(pCreator);
  }

  static {
    registerCreator(AntiqueTransformer.class);
    registerCreator(BitMaskTransformer.class);
    registerCreator(BlackholeTransformer.class);
    registerCreator(ColorToGrayTransformer.class);
    registerCreator(NegativeTransformer.class);
    registerCreator(OilTransferTransformer.class);
    registerCreator(PosteriseTransformer.class);
    registerCreator(RefractTransformer.class);
    registerCreator(TwirlTransformer.class);
    registerCreator(WaveTransformer.class);
    registerCreator(NoiseTransformer.class);
    registerCreator(ThresholdTransformer.class);
    registerCreator(Wave3DTransformer.class);
    registerCreator(SphereTransformer.class);
    registerCreator(CubeTransformer.class);
    registerCreator(Twirl3DTransformer.class);
    registerCreator(MagnetTransformer.class);
    registerCreator(Magnet3DTransformer.class);
    registerCreator(BumpTransformer.class);
    registerCreator(DisplaceTransformer.class);
    registerCreator(SwapRGBTransformer.class);
    registerCreator(ScaleTransformer.class);
    registerCreator(Transform3DTransformer.class);
    registerCreator(ComposeTransformer.class);
    registerCreator(WaterTransformer.class);
    registerCreator(Bump3DTransformer.class);
    registerCreator(EmbossTransformer.class);
    registerCreator(AddTransformer.class);
    registerCreator(FlipTransformer.class);
    registerCreator(TurnTransformer.class);
    registerCreator(BalancingTransformer.class);
    registerCreator(Genlock3DTransformer.class);
    registerCreator(Cartesian2PolarTransformer.class);
    registerCreator(Polar2CartesianTransformer.class);
    registerCreator(ShiftLinesTransformer.class);
    registerCreator(ShearTransformer.class);
    registerCreator(WindTransformer.class);
    registerCreator(HSLTransformer.class);
    registerCreator(RotateTransformer.class);
    registerCreator(RotateBlurTransformer.class);
    registerCreator(RectangleTransformer.class);
    registerCreator(AddBorderTransformer.class);
    registerCreator(NegativeBrightnessTransformer.class);
    registerCreator(PerspectiveTransformer.class);
    registerCreator(TwistTransformer.class);
    registerCreator(BalancingGradientTransformer.class);
    registerCreator(HSLGradientTransformer.class);
    registerCreator(MergeImagesTransformer.class);
    registerCreator(LineArtTransformer.class);
    registerCreator(CropTransformer.class);
    registerCreator(MotionBlurTransformer.class);
    registerCreator(RollTransformer.class);
    registerCreator(PixelizeTransformer.class);
    registerCreator(ErodeTransformer.class);
    registerCreator(TextTransformer.class);
    registerCreator(WrapTransformer.class);
    registerCreator(ConvolveTransformer.class);
    registerCreator(DisplaceMapTransformer.class);
    registerCreator(AlphaTransformer.class);
    registerCreator(AlphaComposeTransformer.class);
    registerCreator(ZPlot2DTransformer.class);
    registerCreator(ZPlot3DTransformer.class);
    registerCreator(ParPlot3DTransformer.class);
    registerCreator(FormulaColorTransformer.class);
    registerCreator(FormulaComposeTransformer.class);
    registerCreator(Pixelize3DTransformer.class);
    registerCreator(HDRComposeTransformer.class);
    itemVector = new Vector<String>();
    for (Class<? extends Transformer> creator : items) {
      itemVector.add(creator.getSimpleName());
    }
    Collections.sort(itemVector);
  }

  public static Vector<String> getItemVector() {
    return itemVector;
  }

  public static Transformer getTransformerInstance(String pName) {
    for (Class<? extends Transformer> transformerCls : items) {
      if (transformerCls.getSimpleName().equals(pName)) {
        try {
          Transformer transformer = transformerCls.newInstance();
          transformer.initPresets();
          return transformer;
        }
        catch (Throwable e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }

}
