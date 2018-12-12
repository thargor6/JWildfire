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

import org.codehaus.janino.SimpleCompiler;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomFullVariationWrapperFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;
  public static final boolean DEBUG = false;
  private static final String RESSOURCE_CODE = "code_full_variation";

  private static HashMap<String, Class> builtin_variations;
  private static String classDeclRegex = "\\s*public\\s+class\\s+(\\S+?Func)\\s+(.*)";
  private static Pattern classDecl = Pattern.compile(classDeclRegex);

  private static String default_code =
          "package org.jwildfire.create.tina.variation;\n" +
                  "import org.jwildfire.create.tina.base.XForm;\n" +
                  "import org.jwildfire.create.tina.base.XYZPoint;\n" +
                  "\n" +
                  "public class DynamicCompiledLinear3DFunc extends SimpleVariationFunc {\n" +
                  "  private static final long serialVersionUID = 1L;\n" +
                  "  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {\n" +
                  "    pVarTP.x += pAmount * pAffineTP.x;\n" +
                  "    pVarTP.y += pAmount * pAffineTP.y;\n" +
                  "    if (pContext.isPreserveZCoordinate()) {\n" +
                  "      pVarTP.z += pAmount * pAffineTP.z;\n" +
                  "    }\n" +
                  "  }\n" +
                  "\n" +
                  "  public String getName() {\n" +
                  "    return \"linear3D_dynamic\";\n" +
                  "  }\n" +
                  "}\n";

  private String code = default_code;
  private String filtered_code = code;
  private VariationFunc full_variation = null;
  private String[] ressourceNames = {RESSOURCE_CODE};

  static {
    builtin_variations = new HashMap<String, Class>();
    List<Class<? extends VariationFunc>> varClasses = VariationFuncList.getVariationClasses();
    for (Class varClass : varClasses) {
      builtin_variations.put(varClass.getSimpleName(), varClass);
    }
  }

  public CustomFullVariationWrapperFunc() {
    if (DEBUG) {
      System.out.println("called CustomFullVariationWrapperFunc constructor");
    }
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    if (full_variation != null) {
      full_variation.transform(pContext, pXForm, pAffineTP, pVarTP, pAmount);
    }
  }

  @Override
  public void invtransform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    if (full_variation != null) {
      full_variation.invtransform(pContext, pXForm, pAffineTP, pVarTP, pAmount);
    }
  }

  @Override
  public String[] getParameterNames() {
    if (full_variation != null) {
      return full_variation.getParameterNames();
    } else {
      return new String[0];
    }
  }

  @Override
  public Object[] getParameterValues() {
    if (full_variation != null) {
      return full_variation.getParameterValues();
    } else {
      return new Object[0];
    }
  }

  public int getParameterIndex(String pName) {
    if (full_variation != null) {
      return full_variation.getParameterIndex(pName);
    } else {
      return -1;
    }
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (full_variation != null) {
      full_variation.setParameter(pName, pValue);
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String[] getRessourceNames() {
    // ressourceNames gets recreated in compile() to include inner full_variation resources (after the RESSOURCE_CODE name)
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    byte[] codeval = (code != null ? code.getBytes() : null);
    if (ressourceNames.length == 1) { // only one resource, the RESSOURCE_CODE, so no resources for inner full_variation
      return new byte[][]{codeval};
    } else {
      byte[][] inner_resvals = full_variation.getRessourceValues();
      byte[][] all_vals = new byte[inner_resvals.length + 1][]; // ressourceNames.length should be = inner_resvals.length + 1
      all_vals[0] = codeval;
      for (int i = 0; i < inner_resvals.length; i++) {
        all_vals[i + 1] = inner_resvals[i];
      }
      return all_vals;
    }
    //    return new byte[][] { (code != null ? code.getBytes() : null) };
  }

  // public int getResourceIndex(String pName) // method inherited from VariationFunc should work
  // public byt[] getResource(String pName) // method inherited from VariationFunc should work
  @Override
  public RessourceType getRessourceType(String pName) {
    if (pName.equals(RESSOURCE_CODE)) {
      return RessourceType.JAVA_CODE;
    } else {
      return full_variation.getRessourceType(pName);
    }
  }

  /*
   *  setting resource
   *  if setting RESSOURCE_CODE, then filter out Java annotation lines (since Janino compiler throws error on these)
   *  otherwise pass through to wrapped VariationFunc
   */
  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (DEBUG) {
      System.out.println("called setRessource: " + pName);
    }
    if (RESSOURCE_CODE.equalsIgnoreCase(pName)) {
      if (pValue == null) {
        if (DEBUG) {
          System.out.println(" code resource null, setting full_variation to null");
        }
        filtered_code = "";
        full_variation = null;
        return;
      } else {
        String new_code = new String(pValue);
        if (new_code.equals(code)) {
          if (DEBUG) {
            System.out.println(" code resource unchanged, returning without filtering/validating/compiling");
          }
          return;
        }
        code = new_code;
        StringBuffer bufcode = new StringBuffer(code.length());
        Scanner codescanner = new Scanner(code);

        while (codescanner.hasNextLine()) {
          String line = codescanner.nextLine();
          // filter out Java annotation lines (lines that start with "@"), since Janino compiler will throw error on annotations
          if (line.matches("\\s*@.*")) {
            if (DEBUG) {
              System.out.println("filtering out: " + line);
            }
          }
          // else if (line.matches("\\s*public\\s+class\\s+\\S+?Func\\s+.*")) {
          else if (line.matches(classDeclRegex)) {
            // extract name of class, make it Dynamic instead
            Matcher mtch = classDecl.matcher(line);
            String modline = line;
            if (mtch.find()) {
              String funcClass = mtch.group(1);
              String remainder = mtch.group(2);
              if (DEBUG) {
                System.out.println("found class declaration: " + line);
                System.out.println("variation class: " + funcClass);
                System.out.println("remainder: " + remainder);
              }
              if (builtin_variations.get(funcClass) != null) {
                String newClassName = "DynamicCompiled" + funcClass;
                modline = "public class " + newClassName + " " + remainder;
                if (DEBUG) {
                  System.out.println("found existing variation: " + ((Class) builtin_variations.get(funcClass)).getName());
                  System.out.println("REVISED LINE: " + modline);
                }
              }
            }
            bufcode.append(modline);
            bufcode.append("\n");
          } else {
            bufcode.append(line);
            bufcode.append("\n");
          }
        }
        filtered_code = bufcode.toString();
        validate(); // compiles
      }
    } else if (full_variation != null) {
      full_variation.setRessource(pName, pValue);
    } else {
      throw new IllegalArgumentException(pName);
    }
  }

  @Override
  public String getName() {
    return "custom_wf_full";
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    if (DEBUG) {
      System.out.println("called init");
    }
    if (full_variation == null) {
      validate();
    }
    if (full_variation != null) {
      full_variation.initOnce(pContext, pLayer, pXForm, pAmount);
      full_variation.init(pContext, pLayer, pXForm, pAmount);
    }
  }

  @Override
  public void validate() {
    try {
      if (DEBUG) {
        System.out.println("called validate");
      }
      if (code != null) {
        this.compile();
      }
    } catch (Throwable ex) {
      throw new RuntimeException(ex);
    }
  }

  /*
   * compile "code" String,
   * then take first class in compiled code that is a subclass of VariationFunc,
   * create a new instance of this VariationFunc, and assign it to full_variation
   *
   * Still need to figure out a way to trigger TinaController.refreshParamCmb(TinaNonlinearControlsRow pRow, XForm pXForm, Variation pVar)
   *    in order to update TinaNonlinearControlsRow to reflect changed param names and values when code  changes
   */
  public void compile() {
    if (DEBUG) {
      System.out.println("called compile()");
    }
    // if there was a previous full_variation, keep it to try and copy shared params
    VariationFunc prev_variation = full_variation;
    try {
      SimpleCompiler compiler = new SimpleCompiler();
      compiler.cook(filtered_code);
      ClassLoader cloader = compiler.getClassLoader();
      Class varClass = null;
      // a bunch of mucking about to find all classes compiled by compiler.cook(filtered_code)
      // based on suggestion in 
      //     https://stolenkid.wordpress.com/2009/03/11/browse-classloader/
      // and 
      //     http://stackoverflow.com/questions/2681459/how-can-i-list-all-classes-loaded-in-a-specific-class-loader
      // but expanded to catch many possibilities for the "classes" field in ClassLoader, 
      //    since above links only work if classesField.get(classloader) is a Vector, 
      //    and it can be other Objects instead -- with current version of Janino it is a HashMap with class names as the keys
      Field classesField = cloader.getClass().getDeclaredField("classes");
      classesField.setAccessible(true);
      Object classesLoaded = classesField.get(cloader);
      Iterator classIter = null;
      if (classesLoaded instanceof AbstractMap) {
        classIter = ((AbstractMap) classesLoaded).keySet().iterator();
      } else if (classesLoaded instanceof AbstractCollection) {
        classIter = ((AbstractCollection) classesLoaded).iterator();
      } else {
        throw new IllegalArgumentException("unknown class " + String.valueOf(classesLoaded));
      }
      // construct full_variation as instance of first Class from classloader that is a subclass of VariationFunc
      while (classIter.hasNext()) {
        Object val = classIter.next();
        String varClassName = null;
        if (val instanceof String) {
          varClassName = (String) val;
        } else if (val instanceof Class) {
          varClassName = ((Class) val).getName();
        } else if (val instanceof Map.Entry) {
          Map.Entry me = (Map.Entry) val;
          if (me.getKey() instanceof String) {
            varClassName = (String) me.getKey();
          } else if (me.getValue() instanceof String) {
            varClassName = (String) me.getValue();
          } else if (me.getKey() instanceof Class) {
            varClassName = ((Class) me.getKey()).getName();
          } else if (me.getValue() instanceof Class) {
            varClassName = ((Class) me.getValue()).getName();
          } else {
            varClassName = me.getValue().toString();
          }
        } else {
          varClassName = val.toString();
        }
        varClass = Class.forName(varClassName, true, cloader);

        if (DEBUG) {
          System.out.println("className: " + varClassName);
          System.out.println("instance of VariationFunc: " + VariationFunc.class.isAssignableFrom(varClass));
        }

        if (VariationFunc.class.isAssignableFrom(varClass)) {
          full_variation = null;
          full_variation = (VariationFunc) varClass.newInstance();
          String[] inner_resource_names = full_variation.getRessourceNames();
          // redo ressourceNames to include any resources of the inner full_variation (added after the RESSOURCE_CODE name)
          if (inner_resource_names == null) {
            ressourceNames = new String[1];
            ressourceNames[0] = RESSOURCE_CODE;
          } else {
            ressourceNames = new String[inner_resource_names.length + 1];
            ressourceNames[0] = RESSOURCE_CODE;
            for (int k = 0; k < inner_resource_names.length; k++) {
              ressourceNames[k + 1] = inner_resource_names[k];
            }
            if (DEBUG) {
              System.out.println("new ressourceNames: " + Arrays.toString(ressourceNames));
            }
          }
          if (DEBUG) {
            System.out.println("full_variation: " + full_variation);
            System.out.println("variation name: " + full_variation.getName());
          }

          break;
        }
      }
      if (full_variation != null && prev_variation != null) {
        // copy shared params from prev_variation
        if (full_variation.getClass().getName().equals(prev_variation.getClass().getName())) {
          if (DEBUG) {
            System.out.println("variations compatible, copying params: " + full_variation.getClass().getName());
          }
          String[] prev_params = prev_variation.getParameterNames();
          for (String prev_param : prev_params) {
            Object prev_val = prev_variation.getParameter(prev_param);
            Object cur_val = full_variation.getParameter(prev_param);
            if (prev_val != null && cur_val != null) {
              if (prev_val instanceof Number) {
                full_variation.setParameter(prev_param, ((Number) prev_val).doubleValue());
                if (DEBUG) {
                  System.out.println("param: " + prev_param + ", value: " + (Number) full_variation.getParameter(prev_param));
                }
              } else {
                if (DEBUG) {
                  System.out.println("prev_val not a number: " + prev_val + ", " + prev_val.getClass().getName());
                }
              }
            }
          }
        } else {
          if (DEBUG) {
            System.out.println("variations not compatible: " + full_variation.getClass().getName() + ", " + prev_variation.getClass().getName());
          }
        }
        // should also copy shared resources??
      }
    } catch (Throwable ex) {
      System.out.println("##############################################################");
      System.out.println(ex.getMessage());
      ex.printStackTrace();
      System.out.println("##############################################################");
      // full_variation = null;
    }
  }

  /*  
      Overriding VariationFunc.makeCopy() to make sure resources are copied first, 
      thus ensuring dynamic code is compiled before parameter names and values are copied
  */
  public CustomFullVariationWrapperFunc makeCopy() {
    CustomFullVariationWrapperFunc varCopy = (CustomFullVariationWrapperFunc) VariationFuncList.getVariationFuncInstance(this.getName());
    // CustomFullVariationWrapperFunc varCopy = new CustomFullVariationWrapperFunc();
    // ressources
    String[] ressNames = this.getRessourceNames();
    if (ressNames != null) {
      for (int i = 0; i < ressNames.length; i++) {
        byte[] val = this.getRessourceValues()[i];
        varCopy.setRessource(ressNames[i], val);
      }
    }
    // params
    String[] paramNames = this.getParameterNames();
    if (paramNames != null) {
      for (int i = 0; i < paramNames.length; i++) {
        String paramName = paramNames[i];
        Object val = this.getParameter(paramName);
        Object copyVal = varCopy.getParameter(paramName);
        if (val != null && copyVal != null) {
          if (val instanceof Number) {
            varCopy.setParameter(paramName, ((Number) val).doubleValue());
          } else {
            throw new IllegalStateException();
          }
        } else {
          if (DEBUG) {
            System.out.println("Copying, got a null for param " + paramName + ", prev = " + val + ", new = " + copyVal);
          }
        }
      }
    }
    return varCopy;
  }

  public boolean ressourceCanModifyParams(String resourceName) {
    if (resourceName.equalsIgnoreCase(RESSOURCE_CODE)) {
      return true;
    } else {
      return full_variation.ressourceCanModifyParams(resourceName);
    }
  }

  @Override
  public boolean ressourceCanModifyParams() {
    return true;
  }

  @Override
  public boolean dynamicParameterExpansion(String paramName) {
    return full_variation.dynamicParameterExpansion(paramName);
  }

  public boolean dynamicParameterExpansion() {
    return full_variation != null ? full_variation.dynamicParameterExpansion() : false;
  }

  @Override
  public int getPriority() {
    return full_variation != null ? full_variation.getPriority() : 0;
  }

}
