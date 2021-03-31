package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class PreSubFlameWFFunc extends SubFlameWFFunc {
  private static final long serialVersionUID = 1L;

  @Override
  public int getPriority() {
    return -1;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    subflameIter(pContext);
    pAffineTP.x = q.x;
    pAffineTP.y = q.y;
    pAffineTP.z = q.z;

    setColor(pAffineTP);
  }

  @Override
  public String getName() {
    return "pre_subflame_wf";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_PRE, VariationFuncType.VARTYPE_SUPPORTS_EXTERNAL_SHAPES};
  }


}
