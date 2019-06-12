    package org.jwildfire.create.tina.base;
    
    import org.jwildfire.create.tina.variation.FlameTransformationContext;
    
    public final class TransformationTargetColorStep extends AbstractTransformationStep {
      private static final long serialVersionUID = 1L;
    
      public TransformationTargetColorStep(XForm pXForm) {
        super(pXForm);
      }
      
      private double lerp(double a, double b, double t) {
        return (1 - t) * a + t * b;
      }
    
      @Override
      public void transform(FlameTransformationContext pContext, XYZPoint pAffineT, XYZPoint pVarT, XYZPoint pSrcPoint, XYZPoint pDstPoint) {
        
        if (pDstPoint.rgbColor) {  
          return;
        }
        
        double t = (xform.getColorSymmetry() + 1.0) / 2.0;
        pDstPoint.redColor = lerp(xform.targetRenderColor.red, pDstPoint.redColor, t);
        pDstPoint.greenColor = lerp(xform.targetRenderColor.green, pDstPoint.greenColor, t);
        pDstPoint.blueColor = lerp(xform.targetRenderColor.blue, pDstPoint.blueColor, t);
    
      }
    
    }
