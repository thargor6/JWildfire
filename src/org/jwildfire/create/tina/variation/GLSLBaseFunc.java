package org.jwildfire.create.tina.variation;

import js.glsl.glslFuncRunner;
import js.glsl.vec3;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;


public class GLSLBaseFunc extends VariationFunc {

  /*
   * Variation : glsl_code
   *
   * Autor: Jesus Sosa
   * Date: October 31, 2018
   * Reference https://www.interactiveshaderformat.com/sketches/3403#
   */


  private static final long serialVersionUID = 1L;

  private static final String PARAM_RESOLUTIONX = "ResolutionX";
  private static final String PARAM_RESOLUTIONY = "ResolutionY";

  private static final String PARAM_ZOOM = "zoom";
  private static final String PARAM_GRADIENT = "Gradient";


  private static final String[] paramNames = {PARAM_RESOLUTIONX, PARAM_RESOLUTIONY, PARAM_ZOOM, PARAM_GRADIENT};


  private static final String RESSOURCE_CODE = "code";
  private static final String[] ressourceNames = {RESSOURCE_CODE};


  int resolutionX = 1000000;
  int resolutionY = resolutionX;

  double zoom = 4.0;

  int gradient = 1;

  glslFuncRunner cf_runner = null;

  String header0 = "import js.glsl.vec2;\n" +
          "import js.glsl.vec3;\n" +
          "import js.glsl.vec4;\n" +
          "import js.glsl.mat2;\n" +
          "import js.glsl.mat3;\n" +
          "import js.glsl.mat4;\n";

  String code_func = "public vec3 getRGBColor(int i,int j)\n" +
          "{\n" +
          "  double xt=(double)i +0.5;\n" +
          "  double yt=(double)j +0.5;\n" +
          "  vec2 st=new vec2(2.0*xt/resolutionX-1.0,2.0*yt/resolutionY-1.0);\n" +
          "  vec3 color=new vec3(0.0);\n" +

          "  vec2 c = st.multiply(zoom).minus(new vec2(0.5,0.0));\n" +
          "  color = fractal(c);\n" +
          "  return color;\n" +
          "}\n";


  @Override
  public void initOnce(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    super.initOnce(pContext, pLayer, pXForm, pAmount);

  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    compile();
  }

  public String AddParams() {
    String sReturn = "int resolutionX=" + resolutionX + ";\n" +
            "int resolutionY=" + resolutionY + ";\n" +
            "double zoom=" + zoom + ";\n";

    return sReturn;
  }

  public void compile() {

    try {
      cf_runner = glslFuncRunner.compile(header0 + AddParams() + code_func);
    } catch (Throwable ex) {
      System.out.println("##############################################################");
      System.out.println(ex.getMessage());
      System.out.println("##############################################################");
      System.out.println(code_func);
      System.out.println("##############################################################");
    }
  }

  @Override
  public void validate() {

    try {
      if (code_func != null) {
        glslFuncRunner.compile(header0 + AddParams() + code_func);
      }
    } catch (Throwable ex) {
      throw new RuntimeException(ex);
    }
  }

  public int[] dbl2int(vec3 theColor) {
    int[] color = new int[3];

    int col = (int) (256D * theColor.x);
    if (col > 255)
      col = 255;
    color[0] = col;
    col = (int) (256D * theColor.y);
    if (col > 255)
      col = 255;
    color[1] = col;
    col = (int) (256D * theColor.z);
    if (col > 255)
      col = 255;
    color[2] = col;
    return color;
  }

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {


    int i = (int) (pContext.random() * resolutionX);
    int j = (int) (pContext.random() * resolutionY);

    vec3 color = new vec3(0.0);

    color = cf_runner.getRGBColor(i, j);

    if (gradient == 0) {
      int[] tcolor = new int[3];
      tcolor = dbl2int(color);

      pVarTP.rgbColor = true;
      ;
      pVarTP.redColor = tcolor[0];
      pVarTP.greenColor = tcolor[1];
      pVarTP.blueColor = tcolor[2];

    } else {
      //	pVarTP.color=color.r;
      pVarTP.color = color.r * color.g;
      //	pVarTP.color=color.r*color.g*color.b;
    }
    pVarTP.x += pAmount * ((double) (i) / resolutionX - 0.5);
    pVarTP.y += pAmount * ((double) (j) / resolutionY - 0.5);

  }


  public String getName() {
    return "glsl_code";
  }

  public String[] getParameterNames() {
    return paramNames;
  }

  public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
    return new Object[]{resolutionX, resolutionY, zoom, gradient};
  }

  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_RESOLUTIONX)) {
      resolutionX = (int) Tools.limitValue(pValue, 100, 1000000);
    } else if (pName.equalsIgnoreCase(PARAM_RESOLUTIONY)) {
      resolutionY = (int) Tools.limitValue(pValue, 100, 1000000);
    } else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
      zoom = Tools.limitValue(pValue, 0, 100);
    } else if (pName.equalsIgnoreCase(PARAM_GRADIENT)) {
      gradient = (int) Tools.limitValue(pValue, 0, 1);
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public boolean dynamicParameterExpansion() {
    return true;
  }

  @Override
  public boolean dynamicParameterExpansion(String pName) {
    // preset_id doesn't really expand parameters, but it changes them; this will make them refresh
    return true;
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }


  @Override
  public byte[][] getRessourceValues() {
    return new byte[][]{(code_func != null ? code_func.getBytes() : null)};
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (pName.equals(RESSOURCE_CODE)) {
      return RessourceType.JAVA_CODE;
    } else {
      return super.getRessourceType(pName);
    }
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_CODE.equalsIgnoreCase(pName)) {
      code_func = pValue != null ? new String(pValue) : "";
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_EDIT_FORMULA};
  }

}

