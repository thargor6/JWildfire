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
package org.jwildfire.script;

import java.awt.Color;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jwildfire.base.ManagedObject;
import org.jwildfire.base.Tools;
import org.jwildfire.envelope.Envelope;
import org.jwildfire.swing.Buffer;
import org.jwildfire.swing.BufferList;

public class Action implements Cloneable {
  private static final String TOKEN_INDENT = "  ";
  private static final String TOKEN_INPUT = "input";
  private static final String TOKEN_OUTPUT = "output";
  private static final String TOKEN_OUTPUT_HDR = "outputHDR";
  private static final String TOKEN_OUTPUT_3D = "output3d";
  private static final String TOKEN_PARAM = "param";
  private static final Object TOKEN_DIMENSION = "dimension";
  private static final String TOKEN_ENVELOPE = "envelope";
  private static final String TOKEN_VIEW = "view";
  private static final String TOKEN_SELECTED = "selected";
  private static final String TOKEN_INTERPOLATION = "interpolation";
  private static final String TOKEN_LOCKED = "locked";
  private static final String TOKEN_POINTS = "points";

  private ActionType actionType;
  private String parameter;
  private String inputBuffer;
  private String outputBuffer;
  private String outputHDRBuffer;
  private String outputBuffer3D;
  private int width;
  private int height;
  private List<Parameter> parameterList = new ArrayList<Parameter>();

  public Action clone() {
    Action res = new Action();
    res.actionType = actionType;
    res.parameter = parameter;
    res.inputBuffer = inputBuffer;
    res.outputBuffer = outputBuffer;
    res.outputHDRBuffer = outputHDRBuffer;
    res.outputBuffer3D = outputBuffer3D;
    res.width = width;
    res.height = height;
    for (Parameter parameter : parameterList) {
      res.parameterList.add(parameter.clone());
    }
    return res;
  }

  public ActionType getActionType() {
    return actionType;
  }

  public void setActionType(ActionType actionType) {
    this.actionType = actionType;
  }

  public String getParameter() {
    return parameter;
  }

  public void setParameter(String pParameter) {
    parameter = pParameter;
  }

  public Parameter getParameterByName(String pName) {
    for (Parameter parameter : parameterList) {
      if (parameter.getName().equals(pName))
        return parameter;
    }
    return null;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void setProperties(ManagedObject pManagedObject, BufferList pBufferList) throws Exception {
    BeanInfo beanInfo = pManagedObject.getBeanInfo();
    if (beanInfo != null) {
      PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
      if (props != null) {
        for (PropertyDescriptor prop : props) {
          Method writeMethod = prop.getWriteMethod();
          if (writeMethod != null) {
            Object val = null;
            Parameter parameter = getParameterByName(prop.getName());
            if (parameter != null) {
              String strVal = parameter.getValue();
              Class<?> cls = prop.getPropertyType();
              if (cls == Integer.class)
                val = Integer.valueOf(strVal);
              else if (cls == int.class)
                val = Tools.FTOI(Double.parseDouble(strVal));
              else if (cls == String.class)
                val = strVal;
              else if (cls == Double.class)
                val = Double.valueOf(strVal);
              else if (cls == double.class)
                val = Double.parseDouble(strVal);
              else if (cls == Boolean.class)
                val = Boolean.valueOf(strVal);
              else if (cls == boolean.class)
                val = Boolean.parseBoolean(strVal);
              else if (cls.isEnum())
                val = Enum.valueOf((Class<Enum>) cls, strVal);
              else if (cls == Buffer.class)
                val = pBufferList.bufferByName(strVal);
              else if (cls == Color.class) {
                int r = 0, g = 0, b = 0;
                Pattern pattern = Pattern
                    .compile("java\\.awt\\.Color\\[r=([0-9]+),g=([0-9]+),b=([0-9]+)\\]");
                Matcher matcher = pattern.matcher(strVal);
                if (!matcher.find())
                  throw new IllegalArgumentException(strVal);
                r = Integer.parseInt(matcher.group(1));
                g = Integer.parseInt(matcher.group(2));
                b = Integer.parseInt(matcher.group(3));
                val = new Color(r, g, b);
              }
              else
                throw new IllegalArgumentException(cls.toString());
              //  System.out.println("set " + parameter.getName() + " " + val + " (" + cls + ")");
            }
            if (parameter == null) {
              parameter = getParameterByName(prop.getName() + ".r");
              if (parameter != null) {
                Parameter parameterR = parameter;
                Parameter parameterG = getParameterByName(prop.getName() + ".g");
                Parameter parameterB = getParameterByName(prop.getName() + ".b");
                int r = Tools.roundColor(Double.parseDouble(parameterR.getValue()));
                int g = Tools.roundColor(Double.parseDouble(parameterG.getValue()));
                int b = Tools.roundColor(Double.parseDouble(parameterB.getValue()));
                val = new Color(r, g, b);
                //System.out.println("set " + parameter.getName() + " " + val + " (" + java.awt.Color.class + ")");
              }
            }

            if (val != null) {
              try {
                writeMethod.invoke(pManagedObject, val);
              }
              catch (Exception ex) {
                throw new Exception("Error setting property " + prop.getName() + " " + val, ex);
              }
            }
          }
        }
      }
    }
  }

  public void importProperties(ManagedObject pManagedObject) {
    BeanInfo beanInfo = pManagedObject.getBeanInfo();
    if (beanInfo != null) {
      PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
      if (props != null) {
        for (PropertyDescriptor prop : props) {
          Method readMethod = prop.getReadMethod();
          if (readMethod != null) {
            try {
              Object val = readMethod.invoke(pManagedObject);
              Class<?> cls = prop.getPropertyType();
              if (cls == Color.class) {
                Color color = (Color) val;
                String name = prop.getName();
                Parameter param;

                param = new Parameter();
                parameterList.add(param);
                param.setName(name + ".r");
                param.setValue(color != null ? String.valueOf(color.getRed()) : null);

                param = new Parameter();
                parameterList.add(param);
                param.setName(name + ".g");
                param.setValue(color != null ? String.valueOf(color.getGreen()) : null);

                param = new Parameter();
                parameterList.add(param);
                param.setName(name + ".b");
                param.setValue(color != null ? String.valueOf(color.getBlue()) : null);
              }
              else {
                Parameter param = new Parameter();
                parameterList.add(param);
                param.setName(prop.getName());
                param.setValue(val != null ? val.toString() : "");
              }
            }
            catch (Throwable ex) {
              ex.printStackTrace();
            }
          }
        }
      }
    }
  }

  public List<Parameter> getParameterList() {
    return parameterList;
  }

  public String getInputBuffer() {
    return inputBuffer;
  }

  public void setInputBuffer(String pInputBuffer) {
    inputBuffer = pInputBuffer;
  }

  public void setInputBuffer(Buffer pInputBuffer) {
    inputBuffer = (pInputBuffer != null) ? pInputBuffer.getName() : null;
  }

  public String getOutputBuffer() {
    return outputBuffer;
  }

  public String getOutputHDRBuffer() {
    return outputHDRBuffer;
  }

  public void setOutputBuffer(Buffer pOutputBuffer) {
    outputBuffer = (pOutputBuffer != null) ? pOutputBuffer.getName() : null;
  }

  public void setOutputHDRBuffer(Buffer pOutputHDRBuffer) {
    outputHDRBuffer = (pOutputHDRBuffer != null) ? pOutputHDRBuffer.getName() : null;
  }

  public void setOutputBuffer(String pOutputBuffer) {
    outputBuffer = pOutputBuffer;
  }

  public void setOutputHDRBuffer(String pOutputHDRBuffer) {
    outputHDRBuffer = pOutputHDRBuffer;
  }

  public String getOutputBuffer3D() {
    return outputBuffer3D;
  }

  public void setOutputBuffer3D(Buffer pOutputBuffer3D) {
    outputBuffer3D = (pOutputBuffer3D != null) ? pOutputBuffer3D.getName() : null;
  }

  public void setOutputBuffer3D(String pOutputBuffer3D) {
    outputBuffer3D = pOutputBuffer3D;
  }

  public static Action readFromLines(List<String> currLines) {
    Action res = new Action();
    // parse header and create appropriate action
    {
      String header = currLines.get(0);
      Pattern pattern = Pattern.compile("([\\w_]+)(\\s+)(.+)");
      Matcher matcher = pattern.matcher(header);
      if (matcher.find()) {
        String actionType = matcher.group(1);
        String parameter = matcher.group(3);
        res.setActionType(ActionType.valueOf(actionType));
        res.setParameter(parameter);
      }
      else
        throw new IllegalArgumentException(header);
    }
    // parse and set params
    {
      final String intPattern = "([0-9\\+\\-]+)";
      final String doublePattern = "([0-9\\+\\-\\.]+)";
      final String spacePattern = "(\\s+)";

      Pattern inputPattern = Pattern.compile("(\\s+)" + TOKEN_INPUT
          + "(\\s+)([\\w\\-_ \\(\\)\\.]+)");
      Pattern outputPattern = Pattern.compile("(\\s+)" + TOKEN_OUTPUT
          + "(\\s+)([\\w\\-_ \\(\\)\\.]+)");
      Pattern outputHDRPattern = Pattern.compile("(\\s+)" + TOKEN_OUTPUT_HDR
          + "(\\s+)([\\w\\-_ \\(\\)\\.]+)");
      Pattern output3dPattern = Pattern.compile("(\\s+)" + TOKEN_OUTPUT_3D
          + "(\\s+)([\\w\\-_ \\(\\)\\.]+)");
      Pattern dimensionPattern = Pattern.compile("(\\s+)" + TOKEN_DIMENSION
          + "(\\s+)([0-9\\-]+)(\\s+)([0-9\\-]+)");
      Pattern paramPattern = Pattern.compile("(\\s+)" + TOKEN_PARAM
          + "(\\s+)([\\w_\\.]+)(\\s+)([\\w_\\-\\.\\+\\(\\)\\[\\] =^,:\\\\/\\*]*)");
      Pattern envelopePattern = Pattern.compile(spacePattern + TOKEN_ENVELOPE + spacePattern
          + "([\\w_\\.]+)");

      Pattern envelopeViewPattern = Pattern.compile(spacePattern + TOKEN_VIEW + spacePattern
          + doublePattern + spacePattern + doublePattern + spacePattern + doublePattern
          + spacePattern + doublePattern);
      Pattern envelopePointsPattern = Pattern.compile(spacePattern + TOKEN_POINTS
          + "([0-9\\+\\-\\.\\s]+)");
      Pattern envelopeSelectedPattern = Pattern.compile(spacePattern + TOKEN_SELECTED
          + spacePattern + intPattern);
      Pattern envelopeInterpolationPattern = Pattern.compile(spacePattern + TOKEN_INTERPOLATION
          + spacePattern + "([A-Z]+)");
      Pattern envelopeLockedPattern = Pattern.compile(spacePattern + TOKEN_LOCKED + spacePattern
          + "(true|false)");
      int i = 0;
      while (i < currLines.size() - 1) {
        i++;
        {
          Matcher matcher = inputPattern.matcher(currLines.get(i));
          if (matcher.find()) {
            String input = matcher.group(3);
            res.setInputBuffer(input);
            continue;
          }
        }
        {
          Matcher matcher = outputPattern.matcher(currLines.get(i));
          if (matcher.find()) {
            String output = matcher.group(3);
            res.setOutputBuffer(output);
            continue;
          }
        }
        {
          Matcher matcher = outputHDRPattern.matcher(currLines.get(i));
          if (matcher.find()) {
            String outputHDR = matcher.group(3);
            res.setOutputHDRBuffer(outputHDR);
            continue;
          }
        }
        {
          Matcher matcher = output3dPattern.matcher(currLines.get(i));
          if (matcher.find()) {
            String output3d = matcher.group(3);
            res.setOutputBuffer3D(output3d);
            continue;
          }
        }
        {
          Matcher matcher = dimensionPattern.matcher(currLines.get(i));
          if (matcher.find()) {
            String width = matcher.group(3);
            String height = matcher.group(5);
            res.setWidth(Integer.parseInt(width));
            res.setHeight(Integer.parseInt(height));
            continue;
          }
        }
        {
          Matcher matcher = paramPattern.matcher(currLines.get(i));
          if (matcher.find()) {
            Parameter param = new Parameter();
            res.parameterList.add(param);
            String paramName = matcher.group(3);
            String paramValue = matcher.group(5);
            // System.out.println(paramName + " " + paramValue);
            param.setName(paramName);
            param.setValue(paramValue);
            continue;
          }
        }
        {
          Matcher matcher = envelopePattern.matcher(currLines.get(i));
          if (matcher.find()) {
            String ident = matcher.group(1);
            String paramName = matcher.group(3);
            Parameter param = res.getParameterByName(paramName);
            if (param == null)
              throw new IllegalArgumentException(currLines.get(i));
            Envelope envelope = new Envelope();
            param.setEnvelope(envelope);
            while (i < currLines.size() - 1) {
              i++;
              // view
              {
                Matcher subMatcher = envelopeViewPattern.matcher(currLines.get(i));
                if (subMatcher.find()) {
                  String subIdent = subMatcher.group(1);
                  if (subIdent.length() < ident.length())
                    throw new IllegalArgumentException(currLines.get(i));
                  try {
                    envelope.setViewXMin(Tools.FTOI(Double.parseDouble(subMatcher.group(3))));
                    envelope.setViewXMax(Tools.FTOI(Double.parseDouble(subMatcher.group(5))));
                    envelope.setViewYMin(Double.parseDouble(subMatcher.group(7)));
                    envelope.setViewYMax(Double.parseDouble(subMatcher.group(9)));
                  }
                  catch (Exception ex) {
                    throw new IllegalArgumentException(currLines.get(i));
                  }
                  continue;
                }
              }
              // points
              {
                Matcher subMatcher = envelopePointsPattern.matcher(currLines.get(i));
                if (subMatcher.find()) {
                  String subIdent = subMatcher.group(1);
                  if (subIdent.length() < ident.length())
                    throw new IllegalArgumentException(currLines.get(i));
                  try {
                    String points[] = subMatcher.group(2).trim().split(" ");
                    int x[] = new int[points.length / 2];
                    double y[] = new double[points.length / 2];
                    for (int j = 0; j < points.length; j += 2) {
                      x[j / 2] = Tools.FTOI(Double.parseDouble(points[j]));
                      y[j / 2] = Double.parseDouble(points[j + 1]);
                    }
                    envelope.setValues(x, y);
                  }
                  catch (Exception ex) {
                    throw new IllegalArgumentException(currLines.get(i));
                  }
                  continue;
                }
              }
              // selected
              {
                Matcher subMatcher = envelopeSelectedPattern.matcher(currLines.get(i));
                if (subMatcher.find()) {
                  String subIdent = subMatcher.group(1);
                  if (subIdent.length() < ident.length())
                    throw new IllegalArgumentException(currLines.get(i));
                  try {
                    envelope.setSelectedIdx(Tools.FTOI(Double.parseDouble(subMatcher.group(3))));
                  }
                  catch (Exception ex) {
                    throw new IllegalArgumentException(currLines.get(i));
                  }
                  continue;
                }
              }
              // interpolation
              {
                Matcher subMatcher = envelopeInterpolationPattern.matcher(currLines.get(i));
                if (subMatcher.find()) {
                  String subIdent = subMatcher.group(1);
                  if (subIdent.length() < ident.length())
                    throw new IllegalArgumentException(currLines.get(i));
                  try {
                    String interpolationStr = subMatcher.group(3);
                    envelope.setInterpolation(Envelope.Interpolation.valueOf(interpolationStr));
                  }
                  catch (Exception ex) {
                    throw new IllegalArgumentException(currLines.get(i));
                  }
                  continue;
                }
              }
              // locked
              {
                Matcher subMatcher = envelopeLockedPattern.matcher(currLines.get(i));
                if (subMatcher.find()) {
                  String subIdent = subMatcher.group(1);
                  if (subIdent.length() < ident.length())
                    throw new IllegalArgumentException(currLines.get(i));
                  try {
                    String lockedStr = subMatcher.group(3);
                    envelope.setLocked(lockedStr.equalsIgnoreCase("true"));
                  }
                  catch (Exception ex) {
                    throw new IllegalArgumentException(currLines.get(i));
                  }
                  continue;
                }
              }
              i--;
              break;
            }
            continue;
          }
        }
        throw new IllegalArgumentException(currLines.get(i));
      }
    }
    return res;
  }

  public void saveToBuffer(StringBuffer pBuffer, String pLineSeparator) {
    // actiontype [parameter]
    pBuffer.append(actionType);
    if ((parameter != null) && (parameter.length() > 0)) {
      pBuffer.append(" ");
      pBuffer.append(parameter);
    }
    pBuffer.append(pLineSeparator);
    // input <buffer>
    if ((inputBuffer != null) && (inputBuffer.length() > 0)) {
      pBuffer.append(TOKEN_INDENT);
      pBuffer.append(TOKEN_INPUT);
      pBuffer.append(" ");
      pBuffer.append(inputBuffer);
      pBuffer.append(pLineSeparator);
    }
    // output <buffer>
    if ((outputBuffer != null) && (outputBuffer.length() > 0)) {
      pBuffer.append(TOKEN_INDENT);
      pBuffer.append(TOKEN_OUTPUT);
      pBuffer.append(" ");
      pBuffer.append(outputBuffer);
      pBuffer.append(pLineSeparator);
    }
    // outputHDR <buffer>
    if ((outputHDRBuffer != null) && (outputHDRBuffer.length() > 0)) {
      pBuffer.append(TOKEN_INDENT);
      pBuffer.append(TOKEN_OUTPUT_HDR);
      pBuffer.append(" ");
      pBuffer.append(outputHDRBuffer);
      pBuffer.append(pLineSeparator);
    }
    // output3d <buffer>
    if ((outputBuffer3D != null) && (outputBuffer3D.length() > 0)) {
      pBuffer.append(TOKEN_INDENT);
      pBuffer.append(TOKEN_OUTPUT_3D);
      pBuffer.append(" ");
      pBuffer.append(outputBuffer3D);
      pBuffer.append(pLineSeparator);
    }
    // dimension <width> <height>
    if ((width > 0) || (height > 0)) {
      pBuffer.append(TOKEN_INDENT);
      pBuffer.append(TOKEN_DIMENSION);
      pBuffer.append(" ");
      pBuffer.append(width);
      pBuffer.append(" ");
      pBuffer.append(height);
      pBuffer.append(pLineSeparator);
    }
    // param <paramname> <paramvalue>
    for (Parameter param : parameterList) {
      pBuffer.append(TOKEN_INDENT);
      pBuffer.append(TOKEN_PARAM);
      pBuffer.append(" ");
      pBuffer.append(param.getName());
      pBuffer.append(" ");
      pBuffer.append(param.getValue());
      pBuffer.append(pLineSeparator);
      Envelope envelope = param.getEnvelope();
      if (envelope != null) {
        // envelope <paramname>
        pBuffer.append(TOKEN_INDENT);
        pBuffer.append(TOKEN_ENVELOPE);
        pBuffer.append(" ");
        pBuffer.append(param.getName());
        pBuffer.append(pLineSeparator);
        // view <xmin> <xmax> <ymin> <ymax>
        pBuffer.append(TOKEN_INDENT);
        pBuffer.append(TOKEN_INDENT);
        pBuffer.append(TOKEN_VIEW);
        pBuffer.append(" ");
        pBuffer.append(Tools.doubleToString(envelope.getViewXMin()));
        pBuffer.append(" ");
        pBuffer.append(Tools.doubleToString(envelope.getViewXMax()));
        pBuffer.append(" ");
        pBuffer.append(Tools.doubleToString(envelope.getViewYMin()));
        pBuffer.append(" ");
        pBuffer.append(Tools.doubleToString(envelope.getViewYMax()));
        pBuffer.append(pLineSeparator);
        // points <x0> <y0> ... <xN> <yN>
        pBuffer.append(TOKEN_INDENT);
        pBuffer.append(TOKEN_INDENT);
        pBuffer.append(TOKEN_POINTS);
        for (int p = 0; p < envelope.size(); p++) {
          pBuffer.append(" ");
          pBuffer.append(Tools.intToString(envelope.getX()[p]));
          pBuffer.append(" ");
          pBuffer.append(Tools.doubleToString(envelope.getY()[p]));
        }
        pBuffer.append(pLineSeparator);
        // selected <selected>        
        pBuffer.append(TOKEN_INDENT);
        pBuffer.append(TOKEN_INDENT);
        pBuffer.append(TOKEN_SELECTED);
        pBuffer.append(" ");
        pBuffer.append(Tools.intToString(envelope.getSelectedIdx()));
        pBuffer.append(pLineSeparator);
        // interpolation <interpolation>
        pBuffer.append(TOKEN_INDENT);
        pBuffer.append(TOKEN_INDENT);
        pBuffer.append(TOKEN_INTERPOLATION);
        pBuffer.append(" ");
        pBuffer.append(envelope.getInterpolation().toString());
        pBuffer.append(pLineSeparator);
        // locked <locked>
        pBuffer.append(TOKEN_INDENT);
        pBuffer.append(TOKEN_INDENT);
        pBuffer.append(TOKEN_LOCKED);
        pBuffer.append(" ");
        pBuffer.append(envelope.isLocked());
        pBuffer.append(pLineSeparator);
      }
    }
    //
    pBuffer.append(pLineSeparator);
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public boolean hasEnvelopes() {
    for (Parameter parameter : parameterList) {
      if (parameter.getEnvelope() != null)
        return true;
    }
    return false;
  }
}
