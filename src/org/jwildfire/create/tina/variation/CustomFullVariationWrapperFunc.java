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

import java.lang.reflect.Field;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import org.codehaus.janino.SimpleCompiler;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class CustomFullVariationWrapperFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;
  public static boolean DEBUG = false;

  private static final String RESSOURCE_CODE = "code";

  private static final String[] ressourceNames = { RESSOURCE_CODE };

  private String code = "";
  private String filtered_code = code;
  
  private VariationFunc full_variation = new AsteriaFunc();
  //  private CustomFullVariationWrapperRunner runner = null;
  
  public CustomFullVariationWrapperFunc()  {
    if (DEBUG) { System.out.println("called CustomFullVariationWrapperFunc constructor"); }
  }
  
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    if (full_variation != null)  {
      full_variation.transform(pContext, pXForm, pAffineTP, pVarTP, pAmount);
    }
  }

  @Override
  public String[] getParameterNames() {
    if (full_variation != null)  {
      return full_variation.getParameterNames();
    }
    else  {
      return new String[0];
    }
  }

  @Override
  public Object[] getParameterValues() {
    if (full_variation != null)  {
      return full_variation.getParameterValues();
    }
    else  {
      return new Object[0];
    }
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (full_variation != null)  {
      full_variation.setParameter(pName, pValue);
    }
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][] { (code != null ? code.getBytes() : null) };
  }

  /* 
  *  setting resource
  *  if setting RESSOURCE_CODE, then filter out Java annotation lines (since Janino compiler throws error on these)
  *  otherwise pass through to wrapped VariationFunc
  */
  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (DEBUG)  { System.out.println("called setRessource: " + pName); }
    if (RESSOURCE_CODE.equalsIgnoreCase(pName)) {
      if (pValue == null)  {
        if (DEBUG) { System.out.println(" code resource null, setting full_variation to null"); }
        filtered_code = "";
        full_variation = null;
        return;
      }
      else {
        String new_code = new String(pValue);
        if (new_code.equals(code))  {
          if (DEBUG) { System.out.println(" code resource unchanged, returning without filtering/validating/compiling"); }
          return;
        }
        code = new_code;
        StringBuffer bufcode = new StringBuffer(code.length());
        Scanner codescanner = new Scanner(code);
        
        while (codescanner.hasNextLine()) {
          String line = codescanner.nextLine();
          if (! line.matches("\\s*@.*")) { 
            bufcode.append(line);
            bufcode.append("\n");
          }
          else { // filter out Java annotation lines (lines that start with "@"), since Janino compiler will throw error on annotations
            if (DEBUG) { System.out.println("filtering out: " + line); }
          }
        }
        filtered_code = bufcode.toString();
        validate();  // compiles
      }
    }
    else if (full_variation != null)  {
      full_variation.setRessource(pName, pValue);
    }
    else  {
      throw new IllegalArgumentException(pName);
    }
  }

  @Override
  public String getName() {
    return "custom_full";
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    if (DEBUG) { System.out.println("called init"); }
    if (full_variation == null)  {
      validate();
    }
    if (full_variation != null)  {
      full_variation.init(pContext, pLayer, pXForm, pAmount);
    }
  }

  @Override
  public void validate() {
    try {
      if (DEBUG) { System.out.println("called validate"); }
      if (code != null) {
        this.compile();
      }
    }
    catch (Throwable ex) {
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
    if (DEBUG) { System.out.println("called compile()"); }
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
      if (classesLoaded instanceof AbstractMap)  {
        classIter = ((AbstractMap)classesLoaded).keySet().iterator();
      }
      else if (classesLoaded instanceof AbstractCollection) {
        classIter =((AbstractCollection)classesLoaded).iterator();
      }
      // construct full_variation as instance of first Class from classloader that is a subclass of VariationFunc
      while (classIter.hasNext()) {
        Object val = classIter.next();
        String varClassName = null;
        if (val instanceof String)  { varClassName = (String)val; }
        else if (val instanceof Class)  { varClassName = ((Class)val).getName(); }
        else if (val instanceof Map.Entry) {
          Map.Entry me = (Map.Entry)val;
          if (me.getKey() instanceof String) { varClassName = (String)me.getKey(); }
          else if (me.getValue() instanceof String)  { varClassName = (String)me.getValue(); }
          else if (me.getKey() instanceof Class) { varClassName = ((Class)me.getKey()).getName(); }
          else if (me.getValue() instanceof Class) { varClassName = ((Class)me.getValue()).getName(); }
          else { varClassName = me.getValue().toString(); }
        }
        else { 
          varClassName = val.toString(); 
        }
        varClass = Class.forName(varClassName, true, cloader);
        
        if (DEBUG)  {
          System.out.println("className: " + varClassName);        
          System.out.println("instance of VariationFunc: " + VariationFunc.class.isAssignableFrom(varClass));
        }

        if (VariationFunc.class.isAssignableFrom(varClass)) {
          full_variation = null;
          full_variation = (VariationFunc)varClass.newInstance();
          if (DEBUG)  {
            System.out.println("full_variation: " + full_variation);
            System.out.println("variation name: " + full_variation.getName());
          }
          
          break;
        }
      }
      if (full_variation != null)  {
        // copy shared params from prev_variation
        String[] prev_params = prev_variation.getParameterNames();
        for (String prev_param : prev_params) {
          Object param = full_variation.getParameter(prev_param);
          if (param != null) {
            full_variation.setParameter(prev_param, (Double)prev_variation.getParameter(prev_param));
            if (DEBUG) { System.out.println("param: " + prev_param + ", value: " + (Double)full_variation.getParameter(prev_param)); }
          }
        }
        // should also copy shared resources??
      }
    }
    catch (Throwable ex) {
      System.out.println("##############################################################");
      System.out.println(ex.getMessage());
      System.out.println("##############################################################");
      System.out.println(filtered_code);
      System.out.println("##############################################################");
      // full_variation = null;
    }
  }

  /*  
      Overriding VariationFunc.makeCopy() to make sure resources are copied first, 
      thus ensuring dynamic code is compiled before parameter names and values are copied
  */
  public CustomFullVariationWrapperFunc makeCopy() {
    CustomFullVariationWrapperFunc varCopy = (CustomFullVariationWrapperFunc)VariationFuncList.getVariationFuncInstance(this.getName());
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
        Object val = this.getParameterValues()[i];
        if (val instanceof Double) {
          varCopy.setParameter(paramNames[i], (Double) val);
        }
        else if (val instanceof Integer) {
          varCopy.setParameter(paramNames[i], Double.valueOf(((Integer) val)));
        }
        else {
          throw new IllegalStateException();
        }
      }
    }
 
    return varCopy;
  }


}
