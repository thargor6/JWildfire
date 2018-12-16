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

/*************************************************************************************************
 * @author Jesus Sosa
 * @date November 22, 2017
 *
 * Variation: lsystem3d_js
 *
 * Based on work of Laurens Lepre, who let me access
 * the Ansi "C" source code of his LParser program.
 *
 * Check The work of Laurens Lapre
 * http://laurenslapre.nl/lapre_004.htm
 *
 * Also I'm using SimpleMesh.java and AbstractOBJMeshWFFunc.java classes by Andreas Maschke
 * included in source code of Java WildFire.
 *************************************************************************************************/

package org.jwildfire.create.tina.variation.mesh;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.RessourceType;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LSystem3DWFFunc extends AbstractOBJMeshWFFunc {
  private static final long serialVersionUID = 1L;

  private static final String RESSOURCE_GRAMMAR = "grammar";
  private static final String PARAM_PRESETID = "presetId";

  public static final String PARAM_SCALEX = "scale_x";
  public static final String PARAM_SCALEY = "scale_y";
  protected static final String PARAM_SCALEZ = "scale_z";
  protected static final String PARAM_OFFSETX = "offset_x";
  protected static final String PARAM_OFFSETY = "offset_y";
  protected static final String PARAM_OFFSETZ = "offset_z";

  protected static final String PARAM_SUBDIV_LEVEL = "subdiv_level";
  protected static final String PARAM_SUBDIV_SMOOTH_PASSES = "subdiv_smooth_passes";
  protected static final String PARAM_SUBDIV_SMOOTH_LAMBDA = "subdiv_smooth_lambda";
  protected static final String PARAM_SUBDIV_SMOOTH_MU = "subdiv_smooth_mu";

  protected static final String PARAM_BLEND_COLORMAP = "blend_colormap";
  protected static final String PARAM_DISPL_AMOUNT = "displ_amount";
  protected static final String PARAM_BLEND_DISPLMAP = "blend_displ_map";

  protected static final String PARAM_RECEIVE_ONLY_SHADOWS = "receive_only_shadows";

  private static final String[] paramNames = {PARAM_PRESETID, PARAM_SCALEX, PARAM_SCALEY, PARAM_SCALEZ, PARAM_OFFSETX, PARAM_OFFSETY, PARAM_OFFSETZ, PARAM_SUBDIV_LEVEL, PARAM_SUBDIV_SMOOTH_PASSES, PARAM_SUBDIV_SMOOTH_LAMBDA, PARAM_SUBDIV_SMOOTH_MU, PARAM_BLEND_COLORMAP, PARAM_DISPL_AMOUNT, PARAM_BLEND_DISPLMAP, PARAM_RECEIVE_ONLY_SHADOWS};

  private String grammar = null;

  int presetId = (int) (Math.random() * 21.0 + 1.0);

  private static final String[] ressourceNames = {RESSOURCE_GRAMMAR};

  @Override
  public Object[] getParameterValues() {
    return new Object[]{presetId, scaleX, scaleY, scaleZ, offsetX, offsetY, offsetZ, subdiv_level, subdiv_smooth_passes, subdiv_smooth_lambda, subdiv_smooth_mu, colorMapHolder.getBlend_colormap(), displacementMapHolder.getDispl_amount(), displacementMapHolder.getBlend_displ_map(), receive_only_shadows};
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_PRESETID.equalsIgnoreCase(pName)) {
      String Strgrammar = new String();
      presetId = (int) pValue;
      if (presetId > 0) {
        try {
          Strgrammar = getGrammarId();
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        setRessource(RESSOURCE_GRAMMAR, Strgrammar.getBytes(Charset.forName("UTF-8")));
      }
    } else if (PARAM_SCALEX.equalsIgnoreCase(pName))
      scaleX = pValue;
    else if (PARAM_SCALEY.equalsIgnoreCase(pName))
      scaleY = pValue;
    else if (PARAM_SCALEZ.equalsIgnoreCase(pName))
      scaleZ = pValue;
    else if (PARAM_OFFSETX.equalsIgnoreCase(pName))
      offsetX = pValue;
    else if (PARAM_OFFSETY.equalsIgnoreCase(pName))
      offsetY = pValue;
    else if (PARAM_OFFSETZ.equalsIgnoreCase(pName))
      offsetZ = pValue;
    else if (PARAM_SUBDIV_LEVEL.equalsIgnoreCase(pName))
      subdiv_level = limitIntVal(Tools.FTOI(pValue), 0, 6);
    else if (PARAM_SUBDIV_SMOOTH_PASSES.equalsIgnoreCase(pName))
      subdiv_smooth_passes = limitIntVal(Tools.FTOI(pValue), 0, 24);
    else if (PARAM_SUBDIV_SMOOTH_LAMBDA.equalsIgnoreCase(pName))
      subdiv_smooth_lambda = pValue;
    else if (PARAM_SUBDIV_SMOOTH_MU.equalsIgnoreCase(pName))
      subdiv_smooth_mu = pValue;
    else if (PARAM_BLEND_COLORMAP.equalsIgnoreCase(pName))
      colorMapHolder.setBlend_colormap(limitIntVal(Tools.FTOI(pValue), 0, 1));
    else if (PARAM_DISPL_AMOUNT.equalsIgnoreCase(pName))
      displacementMapHolder.setDispl_amount(pValue);
    else if (PARAM_BLEND_DISPLMAP.equalsIgnoreCase(pName))
      displacementMapHolder.setBlend_displ_map(limitIntVal(Tools.FTOI(pValue), 0, 1));
    else if (PARAM_RECEIVE_ONLY_SHADOWS.equalsIgnoreCase(pName))
      receive_only_shadows = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][]{(grammar != null ? grammar.getBytes() : null)};
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_GRAMMAR.equalsIgnoreCase(pName)) {
      grammar = pValue != null ? new String(pValue) : "";

      lsystem3D ls = new lsystem3D();
      if (ls.readGrammar(grammar) == 0) {
        ls.expand();
        ls.L_draw();
        mesh = ls.getMesh();
      }
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_GRAMMAR.equalsIgnoreCase(pName)) {
      return RessourceType.BYTEARRAY;
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "lsystem3D_js";
  }

  public String getGrammarId() throws Exception {
    StringBuilder result = new StringBuilder("");
    String grammar = new String("");
    try {
      InputStream inputStream = getClass().getResourceAsStream("lsystems3D.txt");
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        result.append(line + "\n");
      }
      inputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    String fileContent = result.toString();
    Pattern pattern = Pattern.compile("##[0-9][0-9]");
    Matcher matcher = pattern.matcher(fileContent);
    while (matcher.find()) {
      int istart = matcher.end();
      String sSeparator = new String(matcher.group());
      // System.out.println(sSeparator);
      String sNumber = sSeparator.substring(2, 4);
      int vNumber = Integer.parseInt(sNumber);
      // System.out.println("vNumber " +vNumber);

      String str = fileContent.substring(istart, fileContent.length());
      int end = str.indexOf("##END");
      grammar = str.substring(0, end + 1);
      if (vNumber == presetId) {
        break;
      }
    }
    //    	 System.out.println("Grammar " + grammar);
    return grammar;
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    super.init(pContext, pLayer, pXForm, pAmount);

    //    if(  presetId > 0)
    //    {    try 
    //        {
    //		  String  Strgrammar=getGrammarId();
    //		  setRessource(RESSOURCE_GRAMMAR, Strgrammar.getBytes(Charset.forName("UTF-8")));
    //	    } catch (Exception e) {
    //		  e.printStackTrace();
    //	    }
    //    }
  }

  static class point3D {

    public float x;
    public float y;
    public float z;

    public point3D(float a, float b, float c) {
      // TODO Auto-generated constructor stub

      this.x = a;
      this.y = b;
      this.z = c;
    }

  }

  static class face {
    public int nvertex;

    int[] pVertex = new int[4];

    public face() {
      // TODO Auto-generated constructor stub
      nvertex = 0;
      pVertex[0] = 0;
      pVertex[1] = 0;
      pVertex[2] = 0;
      pVertex[3] = 0;
    }

    public face(int v1, int v2, int v3) {
      // TODO Auto-generated constructor stub
      nvertex = 3;
      pVertex[0] = v1;
      pVertex[1] = v2;
      pVertex[2] = v3;
      pVertex[3] = 0;
    }

    public face(int v1, int v2, int v3, int v4) {
      // TODO Auto-generated constructor stub
      nvertex = 4;
      pVertex[0] = v1;
      pVertex[1] = v2;
      pVertex[2] = v3;
      pVertex[3] = v4;
    }

  }

  /* Polygon stack used for solving {} references */
  static class p_rec {
    public int count; // Number of vertices
    //C++ TO JAVA CONVERTER TODO TASK: Java does not have an equivalent to pointers to value types:
    //ORIGINAL LINE: float *ver[3];
    public ArrayList<point3D> ver = new ArrayList<point3D>(); // Vertex store

    public p_rec(int count, ArrayList<point3D> ver) {
      this.count = count;
      for (int i = 0; i < count; i++) {
        this.ver.add(ver.get(i));
      }

    }
  }

  /* Settings stack used for solving [] references */
  static public class s_rec {
    public float[] pos = new float[3]; /* Position in 3space of turtle
     * origin */
    public float[] fow = new float[3]; // Forward direction

    public float[] lef = new float[3]; // Left direction
    public float[] upp = new float[3]; // Up direction
    public float[] last = new float[3]; /* Last position used for
     * connecting cylinders */
    public float[][] last_v = new float[9][3]; /* Last vertices of object used for
     * connecting cylinders */
    public double dis; // Value of F distance
    public double ang; // Value of basic angle
    public double thick; // Value of thickness
    public double dis2; // Value of Z distance
    public double tr; // Trope value
    public int col; // Current color
    public int last_col; // Color of last object
    public int obj; // Current object #
    public int last_obj; // Last object #
  }

  //import org.jwildfire.create.tina.variation.mesh.SimpleMesh;

  static class DynamicStringArray {

    // The storage for the elements. 
    // The capacity is the length of this array.
    private String[] data;

    // The number of elements (logical size).
    // It must be smaller than the capacity.
    private int size;

    public DynamicStringArray() {
      // TODO Auto-generated constructor stub
      data = new String[16];
      size = 0;
    }

    // Constructs an empty DynamicArray with the
    // specified initial capacity.
    public DynamicStringArray(int capacity) {
      if (capacity < 16)
        capacity = 16;
      data = new String[capacity];
      size = 0;
    }

    // Increases the capacity, if necessary, to ensure that
    // it can hold at least the number of elements
    // specified by the minimum capacity argument.
    public void ensureCapacity(int minCapacity) {
      int oldCapacity = data.length;
      if (minCapacity > oldCapacity) {
        int newCapacity = (oldCapacity * 2);
        if (newCapacity < minCapacity)
          newCapacity = minCapacity;
        data = Arrays.copyOf(data, newCapacity);
      }
    }

    // Returns the logical size
    public int size() {
      return size;
    }

    public boolean isEmpty() {
      return size == 0;
    }

    // Checks if the given index is in range.
    private void rangeCheck(int index) {
      if (index >= size || index < 0)
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    // Returns the element at the specified position.
    public String get(int index) {
      rangeCheck(index);
      return data[index];
    }

    // Appends the specified element to the end.
    public boolean add(String element) {
      ensureCapacity(size + 1);
      data[size++] = new String(element);
      return true;
    }

    // Removes all of the elements.
    public void clear() {
      size = 0;
    }

    // Replaces the element at the specified position
    public String set(int index, String element) {
      rangeCheck(index);
      String oldValue = data[index];
      data[index] = new String(element);
      return oldValue;
    }

    public int capacity() {
      return data.length;
    }
  }

  static class lsystem3D {
    public static final int _x = 0;
    public static final int _y = 1;
    public static final int _z = 2;
    public static final int max_colors = 100; // Max color indices
    public static final int max_p_object = 400; // Max polygons per object
    public static final int rule_n = 200;
    public static int max_prod = 4 * 1024 * 1024; // Maximum size l-system production string
    public static final String min_bar = "---------------------------------------------------------";
    public static double zero = 0.0;
    public static double one = 1.0;
    public static double half = 0.5;
    public static double pi = 3.141592;
    public static final int max_stack = 1024; // Max size of the [] and {} stacks during drawing

    public int col = 2;
    public int lev = 1;
    public int last_col = 0;
    public int muts = 0;
    public double ang = (double) 45.0;
    public double thick = (double) 100.0;
    public float min_thick = (float) 0.0;
    public int pcount_limit = 0;
    public String object_s = new String();
    public double closed = 1.0;
    public boolean poly_limit = false;
    public boolean switch_yz = false;
    public boolean closed_form = false;
    ;
    public boolean trace = false;
    public boolean nowait = false;

    public DynamicStringArray rule = new DynamicStringArray(200); // The rule store
    public int[][] poly_store = new int[max_p_object + 1][4];
    public float[][] color_store = new float[max_colors][3];
    public short[] size = new short[rule_n];

    public s_rec[] stack = new s_rec[max_stack];
    public float[] trope = new float[]{0.0F, 0.0F, 0.0F};
    public s_rec org = new s_rec();
    public s_rec save = new s_rec();
    public float[] C1 = new float[3];
    public float[] C2 = new float[3];
    public float[] C3 = new float[3];
    public ArrayList<p_rec> pstack = new ArrayList<p_rec>();
    public double dis = one, dis2 = half, tr = 0.2;
    public int obj = 0;
    public float[] sky = new float[]{0.0F, 0.0F, 1.0F};
    public float[][] last_v = new float[9][3];

    public int poly_count = 0;
    public int scount = 0;
    public float[] last = new float[]{0.0F, 0.0F, 0.0F};
    public float[][] ver = new float[max_p_object][3];
    public int pscount = 0;

    public float zmin = (float) 1e30;

    public float x_max;
    public float x_min;
    public float y_max;
    public float y_min;
    public float z_max;
    public float z_min;
    public int mesh_count = 0;
    public int vert_count = 0;

    //    public ArrayList<point3D> mvertex= new ArrayList<point3D>();
    //    public ArrayList<face>   faces = new ArrayList<face>();
    int avertices = 0;
    SimpleMesh mesh = new SimpleMesh();

    public String Remove_spaces(String input) {
      char[] characters = input.toCharArray();
      StringBuffer buffer = new StringBuffer();

      for (char c : characters) {
        if (!Character.isWhitespace(c)) {
          buffer.append(c);
        }
      }
      return buffer.toString();
    }

    public void Add_deafult_colors() {
      /* Setting default values ----------------------------------------------- */
      short i;

      for (i = 0; i < max_colors; i++) {
        this.color_store[i][_x] = 0.5F;
        color_store[i][_y] = 0.5F;
        color_store[i][_z] = 0.5F;

      }

      color_store[2][_x] = 0.8F;
      color_store[2][_y] = 0.4F;
      color_store[2][_z] = 0.4F;

      color_store[3][_x] = 0.8F;
      color_store[3][_y] = 0.8F;
      color_store[3][_z] = 0.4F;

      color_store[4][_x] = 0.4F;
      color_store[4][_y] = 0.8F;
      color_store[4][_z] = 0.4F;

      color_store[5][_x] = 0.4F;
      color_store[5][_y] = 0.8F;
      color_store[5][_z] = 0.8F;

      color_store[6][_x] = 0.4F;
      color_store[6][_y] = 0.4F;
      color_store[6][_z] = 0.8F;

      color_store[7][_x] = 0.8F;
      color_store[7][_y] = 0.4F;
      color_store[7][_z] = 0.8F;

      color_store[8][_x] = 0.2F;
      color_store[8][_y] = 0.5F;
      color_store[8][_z] = 0.2F;

      color_store[9][_x] = 0.2F;
      color_store[9][_y] = 0.5F;
      color_store[9][_z] = 0.5F;

      color_store[10][_x] = 0.2F;
      color_store[10][_y] = 0.2F;
      color_store[10][_z] = 0.5F;

      color_store[11][_x] = 0.5F;
      color_store[11][_y] = 0.2F;
      color_store[11][_z] = 0.5F;

      color_store[12][_x] = 0.6F;
      color_store[12][_y] = 0.2F;
      color_store[12][_z] = 0.2F;

      color_store[13][_x] = 0.5F;
      color_store[13][_y] = 0.5F;
      color_store[13][_z] = 0.5F;

      color_store[14][_x] = 0.75F;
      color_store[14][_y] = 0.75F;
      color_store[14][_z] = 0.75F;

      color_store[15][_x] = 1.0F;
      color_store[15][_y] = 1.0F;
      color_store[15][_z] = 1.0F;

    }

    private static String activeString;
    private static int activePosition;

    public String strTok(String stringToTokenize, String delimiters) {

      if (stringToTokenize != null) {
        activeString = stringToTokenize;
        activePosition = -1;
      }

      //the stringToTokenize was never set:
      if (activeString == null)
        return null;

      //all tokens have already been extracted:
      if (activePosition == activeString.length())
        return null;

      //bypass delimiters:
      activePosition++;
      while (activePosition < activeString.length() && delimiters.indexOf(activeString.charAt(activePosition)) > -1) {
        activePosition++;
      }

      //only delimiters were left, so return null:
      if (activePosition == activeString.length())
        return null;

      //get starting position of string to return:
      int startingPosition = activePosition;

      //read until next delimiter:
      do {
        activePosition++;
      }
      while (activePosition < activeString.length() && delimiters.indexOf(activeString.charAt(activePosition)) == -1);

      return activeString.substring(startingPosition, activePosition);
    }

    public int readGrammar(String inputname) {
      String temp = new String();
      String keyword = new String();
      String command = new String();

      //  		BufferedReader br = null;
      String r_1 = new String();
      String r_2 = new String();

      int error = 0;
      int tt = 0;

      int num = 0;
      // File name 

      if (inputname.isEmpty())
        return -1;
      byte[] filecontent = inputname.getBytes();
      InputStream is = null;
      BufferedReader br = null;
      is = new ByteArrayInputStream(filecontent);

      // Construct BufferedReader from FileReader
      br = new BufferedReader(new InputStreamReader(is));
      /*		
        		try
        		{
        		  File file = new File(inputname);
        		  FileReader fileReader = new FileReader(file);
        		  br = new BufferedReader(fileReader);
        		}
        		catch (IOException e)
        		{
        			//User_error("Cannot find file " + inputname);
        			error=1;
        			return error;
        		}
       */
      Add_deafult_colors();

      // Read input file 
      try {
        while ((temp = br.readLine()) != null) {
          if (trace)
            System.out.println("R<" + temp + ">");
          if (temp.isEmpty())
            continue;
          if (temp.charAt(0) == '#')
            continue;
          if (temp.length() < 5) // Leading white spaces, not a real line
            continue;

          keyword = strTok(temp, " \r\n#");
          command = strTok(null, "\r\n#");

          if (trace) {
            System.out.println("K[" + keyword + "] C[" + command + "]");
          }

          if (keyword.equals("recursion")) { // Use keyword to determine action
            lev = Integer.parseInt(command);
          } else if (keyword.equals("mutation")) {
            muts = (short) Integer.parseInt(command);
          } else if (keyword.equals("angle")) {
            ang = Float.parseFloat(command);
          } else if (keyword.equals("thickness")) {
            thick = Float.parseFloat(command);
          } else if (keyword.equals("min_thickness")) {
            min_thick = Float.parseFloat(command);
          } else if (keyword.equals("switch_yz")) {
            tt = (short) Integer.parseInt(command);
            switch_yz = (tt == 1);
          } else if (keyword.equals("trace")) {
            tt = (short) Integer.parseInt(command);
            trace = (tt == 1);
          } else if (keyword.equals("poly_limit")) {
            pcount_limit = Integer.parseInt(command);
            poly_limit = true;
          } else if (keyword.equals("no_wait")) {
            tt = (short) Integer.parseInt(command);
            nowait = (tt == 1);
          } else if (keyword.equals("shape")) {
            closed = Float.parseFloat(command);
          } else if (keyword.equals("color")) {
            StringTokenizer st2 = new StringTokenizer(command, " ");
            String sNumber = (String) st2.nextElement();
            tt = Integer.parseInt(sNumber);
            sNumber = (String) st2.nextElement();
            int red = Integer.parseInt(sNumber);
            sNumber = (String) st2.nextElement();
            int green = Integer.parseInt(sNumber);
            sNumber = (String) st2.nextElement();
            int blue = Integer.parseInt(sNumber);
            color_store[tt][_x] = (float) red / (float) 255;
            color_store[tt][_y] = (float) green / (float) 255;
            color_store[tt][_z] = (float) blue / 255;
          } else if (keyword.equals("axiom")) {
            String tmp = Remove_spaces(command);
            object_s = new String(strTok(tmp, "\r\n\t#"));
          } else if (keyword.equals("rule")) {
            String tmp = Remove_spaces(command);
            r_1 = strTok(tmp, "=");
            r_2 = strTok(null, "\r\n\t#");
            rule.add(r_1 + "=" + r_2);
            num++;
          } else {
            error = 2;
            return error;
            //  					User_error("Unknown keyword " + keyword);
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        br.close();
      } catch (IOException e) {
        e.printStackTrace();
      }

      // Echo input
      if(trace) {
        System.out.println("L-system file      : " + inputname);
        System.out.println("Recursion depth    : " + lev);
        System.out.println("Mutations          : " + muts);
        System.out.println("Basic angle        : " + ang + " degrees");
        System.out.println("Starting thickness : " + thick + "  of length");
        System.out.println("Minimum thickness  : " + min_thick + " of length");

        if (switch_yz) {
          System.out.println("Switching y and z");
        }
        if (poly_limit) {
          System.out.println("Limiting lsystem to: " + pcount_limit + " polygons");
        }

        System.out.println("Shape style        : ");
      }
      if (closed == 1.0) {
        if(trace) {
          System.out.println("Attached cylinders");
        }
        closed_form = true;
      } else {
        if(trace) {
          System.out.println("Blocks");
        }
        closed_form = false;
      }
        System.out.println("Axiom              : " + object_s);
      // Add default rules

      rule.add("+=+");
      num++;
      rule.add("-=-");
      num++;
      rule.add("&=&");
      num++;
      rule.add("^=^");
      num++;
      rule.add("<=<");
      num++;
      rule.add(">=>");
      num++;

      rule.add("%=%");
      num++;
      rule.add("|=|");
      num++;
      rule.add("!=!");
      num++;
      rule.add("?=?");
      num++;
      rule.add(":=:");
      num++;
      rule.add(";=;");
      num++;
      rule.add("\'=\'");
      num++;
      rule.add("\"=\"");
      num++;
      rule.add("c=c");
      num++;

      rule.add("[=[");
      num++;
      rule.add("]=]");
      num++;
      rule.add("{={");
      num++;
      rule.add("}=}");
      num++;

      rule.add("F=F");
      num++;
      rule.add("f=f");
      num++;
      rule.add("t=t");
      num++;
      rule.add("g=g");
      num++;
      rule.add("Z=Z");
      num++;
      rule.add("z=z");
      num++;
      rule.add("*=*");
      num++;
      rule.add("$=$");
      num++;
      rule.add("~=~");
      num++;

      rule.add(".=.");
      num++;
      rule.add("1=1");
      num++;
      rule.add("2=2");
      num++;
      rule.add("3=3");
      num++;
      rule.add("4=4");
      num++;
      rule.add("5=5");
      num++;
      rule.add("6=6");
      num++;
      rule.add("7=7");
      num++;
      rule.add("8=8");
      num++;
      rule.add("9=9");
      num++;
      rule.add("0=0");
      num++;
      rule.add("(=(");
      num++;
      rule.add(")=)");
      num++;

      rule.add("_=_");
      num++; // Closer default

      for (int i = 0; i < num; i++) {
        size[i] = (short) (rule.get(i).length() - 2); // Set length of rules
      }

      // Echo rules  
      for (int i = 0; i < num; i++) {
        if (rule.get(i).charAt(0) == '+') {
          break;
        }
        if(trace) {
          System.out.println("Rule " + (i + 1) + " = " + rule.get(i));
        }
      }

      // Set values ready 
      ang = (float) ((ang / (double) 180.0) * pi);
      thick /= 100.0;
      min_thick /= 100.0;
      return 0;
    }

    public String expand() {
      // Expand l-system into production
      //                                             * string. Object_s is read with
      //                                             * the k counter and the next
      //                                             * generation is build up in otemp
      //                                             * with the ot counter. 

      int k;
      int st;
      int s;
      int ss;
      int max = max_prod - 50;
      String otemp = new String();

      String ot;
      int[] S = new int[256];
      int i;
      int l;
      boolean incomplete = false;
      short[] size = new short[rule_n];

      int num = rule.size();

      // calculate size of each rule         
      for (int m = 0; m < num; m++) {
        size[m] = (short) (rule.get(m).length() - 2); // Set length of rules
      }

      //
      // Clear fast access array. This array is to quickly find the rule asociated
      // with a char.
      //
      for (i = 0; i < 256; i++)
        S[i] = num - 1; // Num -1 contains the default rule which does nothing 
      // Each char gets a rule number 
      for (i = num - 1; i >= 0; i--) {
        S[(int) rule.get(i).charAt(0)] = i;
      }

      for (l = 0; l < lev; l++) { // For each recursion
        st = object_s.length();
        ot = otemp;
        ss = 0;

        for (k = (int) 0; k < st; k++) { // For each char in the string
          i = S[object_s.charAt(k)]; // i = rule number attached to current char 
          s = size[i]; // s = size of current rule
          ss += s;

          if (ss >= max) { // Overflow
            l = lev;
            incomplete = true;
            break;
          } else {

            String cInrule = rule.get(i).substring(2);
            ot = ot + cInrule;

          }
        }
        ;

        object_s = new String(ot); // Copy the temp string to object_s and repeat cycle 
      }
      ;
      if(trace) {
        System.out.println("Size of string     : " + object_s.length() + " chars " + (incomplete ? "(incomplete)" : ""));
      }
      return object_s;
    }

    public float vector_length(float[] Vector) {
      return (float) MathLib.sqrt(Vector[_x] * Vector[_x] + Vector[_y] * Vector[_y] + Vector[_z] * Vector[_z]);
    }

    public float scalar_product(float[] VectorA, float[] VectorB) {
      return (float) VectorA[_x] * VectorB[_x] + VectorA[_y] * VectorB[_y] + VectorA[_z] * VectorB[_z];
    }

    public float[] util_t(float[] In, float[] C1, float[] C2, float[] C3) {
      float[] out = new float[3];
      out[_x] = scalar_product(C1, In);
      out[_y] = scalar_product(C2, In);
      out[_z] = scalar_product(C3, In);
      return out;
    }

    public void Vector_plus_fac(float[] A, float[] B, double t, float[] C) {
      C[_x] = (float) (A[_x] + (t) * B[_x]);
      C[_y] = (float) (A[_y] + (t) * B[_y]);
      C[_z] = (float) (A[_z] + (t) * B[_z]);
    }

    public void Vector_product(float[] A, float[] B, float[] C) {
      C[_x] = A[_y] * B[_z] - A[_z] * B[_y];
      C[_y] = A[_z] * B[_x] - A[_x] * B[_z];
      C[_z] = A[_x] * B[_y] - A[_y] * B[_x];
    }

    public void Vector_min(float[] A, float[] B, float[] C) {
      C[_x] = A[_x] - B[_x];
      C[_y] = A[_y] - B[_y];
      C[_z] = A[_z] - B[_z];
    }

    public void Vector_neg(float[] A) {
      A[_x] = (-A[_x]);
      A[_y] = (-A[_y]);
      A[_z] = (-A[_z]);
    }

    public double Get_value(int j) { /* Read a (xx) value from a
     * production string at location j
     * and make it into a real */
      double r = zero;
      StringBuffer bValue = new StringBuffer("");

      for (; ; ) {
        if (object_s.charAt(j + 2) == ')')
          break;
        bValue.append(object_s.charAt(j + 2));
        j++;
      }

      String sValue = new String(bValue);
      r = Double.parseDouble(sValue);
      return r;
    }

    public void vector_copy(float[] S, float[] D) {
      D[_x] = S[_x];
      D[_y] = S[_y];
      D[_z] = S[_z];
    }

    public void Set_rot(float a, float[] n) { // Set up a rotation matrix
      float n11;
      float n22;
      float n33;
      float nxy;
      float nxz;
      float nyz;
      float sina;
      float cosa;

      cosa = (float) MathLib.cos(a);
      sina = (float) MathLib.sin(a);

      n11 = n[_x] * n[_x];
      n22 = n[_y] * n[_y];
      n33 = n[_z] * n[_z];

      nxy = n[_x] * n[_y];
      nxz = n[_x] * n[_z];
      nyz = n[_y] * n[_z];

      C1[_x] = n11 + ((float) 1.0 - n11) * cosa;
      C1[_y] = nxy * ((float) 1.0 - cosa) - n[_z] * sina;
      C1[_z] = nxz * ((float) 1.0 - cosa) + n[_y] * sina;

      C2[_x] = nxy * ((float) 1.0 - cosa) + n[_z] * sina;
      C2[_y] = n22 + ((float) 1.0 - n22) * cosa;
      C2[_z] = nyz * ((float) 1.0 - cosa) - n[_x] * sina;

      C3[_x] = nxz * ((float) 1.0 - cosa) - n[_y] * sina;
      C3[_y] = nyz * ((float) 1.0 - cosa) + n[_x] * sina;
      C3[_z] = n33 + ((float) 1.0 - n33) * cosa;
    }

    public void Define_block(float[] p1, float[] p2, float[] up, int c) {
      /* Insert basic block. Here we
       * build a cube shape directly on
       * the input vectors. */
      float[] dis = new float[3];
      float[] d1 = new float[3];
      float[] d2 = new float[3];
      float[] d3 = new float[3];
      short i;
      double s;
      double d;

      zmin = (((zmin) < (p1[_z])) ? (zmin) : (p1[_z]));
      zmin = (((zmin) < (p2[_z])) ? (zmin) : (p2[_z]));

      /* Setup */
      {
        dis[_x] = p2[_x] - p1[_x];
        dis[_y] = p2[_y] - p1[_y];
        dis[_z] = p2[_z] - p1[_z];
      }
      ;
      d = ((float) MathLib.sqrt((float) (dis[_x] * dis[_x]) + (float) (dis[_y] * dis[_y]) + (float) (dis[_z] * dis[_z])));
      if (d == (float) 0.0) {
        return;
      }
      s = d * thick;
      s = (s < min_thick) ? min_thick : s;
      s *= 0.7071;

      /* D1 */
      //C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memcpy' has no equivalent in Java:
      //  			memcpy(((Object)(d1)), ((Object)(dis)), 12);
      d1[_x] = dis[_x];
      d1[_y] = dis[_y];
      d1[_z] = dis[_z];

      {
        float Dist = (float) 1.0 / ((float) MathLib.sqrt((float) (d1[_x] * d1[_x]) + (float) (d1[_y] * d1[_y]) + (float) (d1[_z] * d1[_z])));
        d1[_x] *= Dist;
        d1[_y] *= Dist;
        d1[_z] *= Dist;
      }
      ;

      /* D2 */
      //C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memcpy' has no equivalent in Java:
      //  			memcpy(((Object)(d2)), ((Object)(up)), 12);
      d2[_x] = up[_x];
      d2[_y] = up[_y];
      d2[_z] = up[_z];

      {
        float Dist = (float) 1.0 / ((float) MathLib.sqrt((float) (d2[_x] * d2[_x]) + (float) (d2[_y] * d2[_y]) + (float) (d2[_z] * d2[_z])));
        d2[_x] *= Dist;
        d2[_y] *= Dist;
        d2[_z] *= Dist;
      }
      ;

      /* D3 */
      {
        d3[_x] = d1[_y] * d2[_z] - d1[_z] * d2[_y];
        d3[_y] = d1[_z] * d2[_x] - d1[_x] * d2[_z];
        d3[_z] = d1[_x] * d2[_y] - d1[_y] * d2[_x];
      }
      ;
      {
        float Dist = (float) 1.0 / ((float) MathLib.sqrt((float) (d3[_x] * d3[_x]) + (float) (d3[_y] * d3[_y]) + (float) (d3[_z] * d3[_z])));
        d3[_x] *= Dist;
        d3[_y] *= Dist;
        d3[_z] *= Dist;
      }
      ;

      /* Base 1, 3 */
      {
        d1[_x] = d2[_x] + d3[_x];
        d1[_y] = d2[_y] + d3[_y];
        d1[_z] = d2[_z] + d3[_z];
      }
      ;
      {
        float Dist = (float) 1.0 / ((float) MathLib.sqrt((float) (d1[_x] * d1[_x]) + (float) (d1[_y] * d1[_y]) + (float) (d1[_z] * d1[_z])));
        d1[_x] *= Dist;
        d1[_y] *= Dist;
        d1[_z] *= Dist;
      }
      ;
      {
        ver[1][_x] = (float) (p1[_x] + (s) * d1[_x]);
        ver[1][_y] = (float) (p1[_y] + (s) * d1[_y]);
        ver[1][_z] = (float) (p1[_z] + (s) * d1[_z]);
      }
      ;
      {
        ver[3][_x] = (float) (p1[_x] + (-s) * d1[_x]);
        ver[3][_y] = (float) (p1[_y] + (-s) * d1[_y]);
        ver[3][_z] = (float) (p1[_z] + (-s) * d1[_z]);
      }
      ;

      /* Base 2, 4 */
      {
        d1[_x] = d2[_x] - d3[_x];
        d1[_y] = d2[_y] - d3[_y];
        d1[_z] = d2[_z] - d3[_z];
      }
      ;
      {
        float Dist = (float) 1.0 / ((float) MathLib.sqrt((float) (d1[_x] * d1[_x]) + (float) (d1[_y] * d1[_y]) + (float) (d1[_z] * d1[_z])));
        d1[_x] *= Dist;
        d1[_y] *= Dist;
        d1[_z] *= Dist;
      }
      ;
      {
        ver[2][_x] = (float) (p1[_x] + (s) * d1[_x]);
        ver[2][_y] = (float) (p1[_y] + (s) * d1[_y]);
        ver[2][_z] = (float) (p1[_z] + (s) * d1[_z]);
      }
      ;
      {
        ver[4][_x] = (float) (p1[_x] + (-s) * d1[_x]);
        ver[4][_y] = (float) (p1[_y] + (-s) * d1[_y]);
        ver[4][_z] = (float) (p1[_z] + (-s) * d1[_z]);
      }
      ;

      /* End */
      for (i = 1; i <= 4; i++) {
        ver[i + 4][_x] = ver[i][_x] + dis[_x];
        ver[i + 4][_y] = ver[i][_y] + dis[_y];
        ver[i + 4][_z] = ver[i][_z] + dis[_z];
      }
      ;

      /* Polygons */
      poly_store[1][0] = 1;
      poly_store[1][1] = 5;
      poly_store[1][2] = 6;
      poly_store[1][3] = 2;

      poly_store[2][0] = 2;
      poly_store[2][1] = 6;
      poly_store[2][2] = 7;
      poly_store[2][3] = 3;

      poly_store[3][0] = 3;
      poly_store[3][1] = 7;
      poly_store[3][2] = 8;
      poly_store[3][3] = 4;

      poly_store[4][0] = 4;
      poly_store[4][1] = 8;
      poly_store[4][2] = 5;
      poly_store[4][3] = 1;

      poly_store[5][0] = 1;
      poly_store[5][1] = 2;
      poly_store[5][2] = 3;
      poly_store[5][3] = 4;

      poly_store[6][0] = 8;
      poly_store[6][1] = 7;
      poly_store[6][2] = 6;
      poly_store[6][3] = 5;

      Save_object(8, 6, c);

    }

    public void Define_closed(float[] p1, float[] p2, float[] up, int c) {
      /* Insert connected cylinder shape.
       * The lastxxx vars are used to
       * store the previous top of the
       * cylinder for connecting a next
       * one. Since the vars are stacked
       * for [] we can connect correctly
       * according to current nesting
       * level. */
      float[] dis = new float[3];
      float[] d1 = new float[3];
      float[] d2 = new float[3];
      float[] d3 = new float[3];
      float[] t1 = new float[3];
      float[] t2 = new float[3];
      int i;
      int ii = 0;
      double s;
      double d;
      double dd = (float) 1e30;

      zmin = (((zmin) < (p1[_z])) ? (zmin) : (p1[_z]));
      zmin = (((zmin) < (p2[_z])) ? (zmin) : (p2[_z]));

      /* Setup */
      {
        dis[_x] = p2[_x] - p1[_x];
        dis[_y] = p2[_y] - p1[_y];
        dis[_z] = p2[_z] - p1[_z];
      }
      ;
      d = ((float) MathLib.sqrt((float) (dis[_x] * dis[_x]) + (float) (dis[_y] * dis[_y]) + (float) (dis[_z] * dis[_z])));
      if (d == (float) 0.0) {
        return;
      }
      s = d * thick;
      s = (s < (float) min_thick) ? min_thick : s;
      s *= (float) 0.5;

      /* D1 */
      //C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memcpy' has no equivalent in Java:
      //  			memcpy(((Object)(d1)), ((Object)(dis)), 12);
      d1[_x] = dis[_x];
      d1[_y] = dis[_y];
      d1[_z] = dis[_z];

      {
        float Dist = (float) 1.0 / ((float) MathLib.sqrt((float) (d1[_x] * d1[_x]) + (float) (d1[_y] * d1[_y]) + (float) (d1[_z] * d1[_z])));
        d1[_x] *= Dist;
        d1[_y] *= Dist;
        d1[_z] *= Dist;
      }
      ;

      /* D2 */
      //C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memcpy' has no equivalent in Java:
      //  			memcpy(((Object)(d2)), ((Object)(up)), 12);
      d2[_x] = up[_x];
      d2[_y] = up[_y];
      d2[_z] = up[_z];

      {
        float Dist = (float) 1.0 / ((float) MathLib.sqrt((float) (d2[_x] * d2[_x]) + (float) (d2[_y] * d2[_y]) + (float) (d2[_z] * d2[_z])));
        d2[_x] *= Dist;
        d2[_y] *= Dist;
        d2[_z] *= Dist;
      }
      ;

      /* D3 */
      {
        d3[_x] = d1[_y] * d2[_z] - d1[_z] * d2[_y];
        d3[_y] = d1[_z] * d2[_x] - d1[_x] * d2[_z];
        d3[_z] = d1[_x] * d2[_y] - d1[_y] * d2[_x];
      }
      ;
      {
        float Dist = (float) 1.0 / ((float) MathLib.sqrt((float) (d3[_x] * d3[_x]) + (float) (d3[_y] * d3[_y]) + (float) (d3[_z] * d3[_z])));
        d3[_x] *= Dist;
        d3[_y] *= Dist;
        d3[_z] *= Dist;
      }
      ;

      {
        t1[_x] = d2[_x] + d3[_x];
        t1[_y] = d2[_y] + d3[_y];
        t1[_z] = d2[_z] + d3[_z];
      }
      ;
      {
        float Dist = (float) 1.0 / ((float) MathLib.sqrt((float) (t1[_x] * t1[_x]) + (float) (t1[_y] * t1[_y]) + (float) (t1[_z] * t1[_z])));
        t1[_x] *= Dist;
        t1[_y] *= Dist;
        t1[_z] *= Dist;
      }
      ;
      {
        t2[_x] = d2[_x] - d3[_x];
        t2[_y] = d2[_y] - d3[_y];
        t2[_z] = d2[_z] - d3[_z];
      }
      ;
      {
        float Dist = (float) 1.0 / ((float) MathLib.sqrt((float) (t2[_x] * t2[_x]) + (float) (t2[_y] * t2[_y]) + (float) (t2[_z] * t2[_z])));
        t2[_x] *= Dist;
        t2[_y] *= Dist;
        t2[_z] *= Dist;
      }
      ;

      {
        ver[1][_x] = (float) (p1[_x] + (s) * t1[_x]);
        ver[1][_y] = (float) (p1[_y] + (s) * t1[_y]);
        ver[1][_z] = (float) (p1[_z] + (s) * t1[_z]);
      }
      ;
      {
        ver[5][_x] = (float) (p1[_x] + (-s) * t1[_x]);
        ver[5][_y] = (float) (p1[_y] + (-s) * t1[_y]);
        ver[5][_z] = (float) (p1[_z] + (-s) * t1[_z]);
      }
      ;
      {
        ver[3][_x] = (float) (p1[_x] + (s) * t2[_x]);
        ver[3][_y] = (float) (p1[_y] + (s) * t2[_y]);
        ver[3][_z] = (float) (p1[_z] + (s) * t2[_z]);
      }
      ;
      {
        ver[7][_x] = (float) (p1[_x] + (-s) * t2[_x]);
        ver[7][_y] = (float) (p1[_y] + (-s) * t2[_y]);
        ver[7][_z] = (float) (p1[_z] + (-s) * t2[_z]);
      }
      ;

      s *= (float) 0.7071;
      {
        ver[2][_x] = (float) (p1[_x] + (s) * t1[_x] + (s) * t2[_x]);
        ver[2][_y] = (float) (p1[_y] + (s) * t1[_y] + (s) * t2[_y]);
        ver[2][_z] = (float) (p1[_z] + (s) * t1[_z] + (s) * t2[_z]);
      }
      ;
      {
        ver[4][_x] = (float) (p1[_x] + (-s) * t1[_x] + (s) * t2[_x]);
        ver[4][_y] = (float) (p1[_y] + (-s) * t1[_y] + (s) * t2[_y]);
        ver[4][_z] = (float) (p1[_z] + (-s) * t1[_z] + (s) * t2[_z]);
      }
      ;
      {
        ver[6][_x] = (float) (p1[_x] + (-s) * t1[_x] + (-s) * t2[_x]);
        ver[6][_y] = (float) (p1[_y] + (-s) * t1[_y] + (-s) * t2[_y]);
        ver[6][_z] = (float) (p1[_z] + (-s) * t1[_z] + (-s) * t2[_z]);
      }
      ;
      {
        ver[8][_x] = (float) (p1[_x] + (s) * t1[_x] + (-s) * t2[_x]);
        ver[8][_y] = (float) (p1[_y] + (s) * t1[_y] + (-s) * t2[_y]);
        ver[8][_z] = (float) (p1[_z] + (s) * t1[_z] + (-s) * t2[_z]);
      }
      ;

      /* End */
      for (i = 1; i <= 8; i++) {
        ver[i + 8][_x] = ver[i][_x] + dis[_x];
        ver[i + 8][_y] = ver[i][_y] + dis[_y];
        ver[i + 8][_z] = ver[i][_z] + dis[_z];
      }
      ;

      if (last_col == c) {
        {
          dis[_x] = p1[_x] - last[_x];
          dis[_y] = p1[_y] - last[_y];
          dis[_z] = p1[_z] - last[_z];
        }
        ;
        d = ((float) MathLib.sqrt((float) (dis[_x] * dis[_x]) + (float) (dis[_y] * dis[_y]) + (float) (dis[_z] * dis[_z])));

        if (d < (float) 1.0) {
          for (i = 1; i <= 8; i++) {
            {
              dis[_x] = ver[1][_x] - last_v[i][_x];
              dis[_y] = ver[1][_y] - last_v[i][_y];
              dis[_z] = ver[1][_z] - last_v[i][_z];
            }
            ;
            d = ((float) MathLib.sqrt((float) (dis[_x] * dis[_x]) + (float) (dis[_y] * dis[_y]) + (float) (dis[_z] * dis[_z])));
            if (d < dd) {
              dd = d;
              ii = i;
            }
          }
          for (i = 1; i <= 8; i++) {
            //C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memcpy' has no equivalent in Java:
            //  						memcpy(((Object)(ver[i])), ((Object)(last_v[ii])), 12);

            ver[i][_x] = last_v[ii][_x];
            ver[i][_y] = last_v[ii][_y];
            ver[i][_z] = last_v[ii][_z];

            ii = (ii + 1) % 9;
            if (ii == 0) {
              ii = 1;
            }
          }
        }
      }

      /* Polygons */
      poly_store[1][0] = 1;
      poly_store[1][1] = 9;
      poly_store[1][2] = 10;
      poly_store[1][3] = 2;

      poly_store[2][0] = 2;
      poly_store[2][1] = 10;
      poly_store[2][2] = 11;
      poly_store[2][3] = 3;

      poly_store[3][0] = 3;
      poly_store[3][1] = 11;
      poly_store[3][2] = 12;
      poly_store[3][3] = 4;

      poly_store[4][0] = 4;
      poly_store[4][1] = 12;
      poly_store[4][2] = 13;
      poly_store[4][3] = 5;

      poly_store[5][0] = 5;
      poly_store[5][1] = 13;
      poly_store[5][2] = 14;
      poly_store[5][3] = 6;

      poly_store[6][0] = 6;
      poly_store[6][1] = 14;
      poly_store[6][2] = 15;
      poly_store[6][3] = 7;

      poly_store[7][0] = 7;
      poly_store[7][1] = 15;
      poly_store[7][2] = 16;
      poly_store[7][3] = 8;

      poly_store[8][0] = 8;
      poly_store[8][1] = 16;
      poly_store[8][2] = 9;
      poly_store[8][3] = 1;

      Save_object(16, 8, c);

      last_col = c;
      //C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memcpy' has no equivalent in Java:
      //  			memcpy(((Object)(last)), ((Object)(p2)), 12);
      last[_x] = p2[_x];
      last[_y] = p2[_y];
      last[_z] = p2[_z];

      //  			
      //  				memcpy(((Object)(last_v[i])), ((Object)(ver[i + 8])), 12);
      //  			
      for (i = 1; i <= 8; i++) {
        last_v[i][_x] = ver[i + 8][_x];
        last_v[i][_y] = ver[i + 8][_y];
        last_v[i][_z] = ver[i + 8][_z];
      }
    }

    /*
    public static SimpleMesh createDfltMesh() {
    SimpleMesh mesh = new SimpleMesh();
    int v0 = mesh.addVertex(-1.0, -1.0, 1.0);
    int v1 = mesh.addVertex(1.0, -1.0, 1.0);
    int v2 = mesh.addVertex(1.0, 1.0, 1.0);
    int v3 = mesh.addVertex(-1.0, 1.0, 1.0);
    int v4 = mesh.addVertex(-1.0, -1.0, -1.0);
    int v5 = mesh.addVertex(1.0, -1.0, -1.0);
    int v6 = mesh.addVertex(1.0, 1.0, -1.0);
    int v7 = mesh.addVertex(-1.0, 1.0, -1.0);
    mesh.addFace(v0, v1, v2, v3);
    mesh.addFace(v1, v5, v6, v2);
    mesh.addFace(v5, v6, v7, v4);
    mesh.addFace(v4, v7, v3, v0);
    mesh.addFace(v3, v2, v6, v7);
    mesh.addFace(v0, v1, v5, v4);
    return mesh;
    }
    */

    public void Save_object(int vertices, int polygons, int color) {
      /* Save an object from store to disc */
      int t;

      //          ArrayList<point3D> mvertex= new ArrayList<point3D>();
      //          ArrayList<face>   faces = new ArrayList<face>();

      int[] nvert = new int[max_p_object];

      /* Write vertices */
      for (t = 1; t <= vertices; t++) {
        nvert[t - 1] = mesh.addVertex(ver[t][_x], ver[t][_y], ver[t][_z]);
        // 			   mvertex.add(new point3D(ver[t][_x],ver[t][_y],ver[t][_z]));
      }

      /* Write polygons */
      for (t = 1; t <= polygons; t++) {

        if (poly_store[t][2] == poly_store[t][3]) {
          // 				     mesh.addFace(vert_count+ poly_store[t][0],vert_count+ poly_store[t][1],vert_count+ poly_store[t][2]);
          int v1 = nvert[poly_store[t][0] - 1];
          int v2 = nvert[poly_store[t][1] - 1];
          int v3 = nvert[poly_store[t][2] - 1];
          mesh.addFace(v1, v2, v3);
          //                   faces.add(new face(vert_count+ poly_store[t][0],vert_count+ poly_store[t][1],vert_count+ poly_store[t][2]));
        } else {
          int v1 = nvert[poly_store[t][0] - 1];
          int v2 = nvert[poly_store[t][1] - 1];
          int v3 = nvert[poly_store[t][2] - 1];
          int v4 = nvert[poly_store[t][3] - 1];
          mesh.addFace(v1, v2, v3, v4);
          //  					mesh.addFace(vert_count + poly_store[t][0] - 1,vert_count + poly_store[t][1] - 1,vert_count +poly_store[t][2] - 1, vert_count+ poly_store[t][3] - 1);
          //                    faces.add(new face(vert_count + poly_store[t][0] - 1,vert_count + poly_store[t][1] - 1,vert_count +poly_store[t][2] - 1, vert_count+ poly_store[t][3] - 1));
        }
      }
      mesh_count++;
      vert_count += vertices;
    }

    public void L_draw() {
      // Process a production string and generate form 
      float[] pos = new float[3];
      float[] end = new float[3];
      float[] v = new float[3];
      float[] fow = new float[3];
      float[] upp = new float[3];
      float[] lef = new float[3];
      //C++ TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
      //ORIGINAL LINE: u32 i, max = strlen(object_s);
      int i;
      //  			int max = object_s.length();
      double r;
      double a;
      int pcount = 0;
      int vcount = 0;
      int j;
      char next;
      boolean poly_on = false;

      // Setup vectors 
      pos[_x] = (float) zero;
      pos[_y] = (float) zero;
      pos[_z] = (float) zero;

      fow[_x] = (float) zero;
      fow[_y] = (float) zero;
      fow[_z] = (float) one;

      lef[_x] = (float) zero;
      lef[_y] = (float) one;
      lef[_z] = (float) zero;

      upp[_x] = (float) one;
      upp[_y] = (float) zero;
      upp[_z] = (float) zero;

      //  			Vector_make(pos, zero, zero, zero);
      //  			Vector_make(fow, zero, zero, one);
      //  			Vector_make(lef, zero, one, zero);
      //  			Vector_make(upp, one, zero, zero);

      // initizalize stack array		
      for (i = 0; i < max_stack; i++) {
        stack[i] = new s_rec();
      }
      // Normalize vector trope

      float vLength = vector_length(trope);

      trope[_x] = trope[_x] / vLength;
      trope[_y] = trope[_y] / vLength;
      trope[_z] = trope[_z] / vLength;

      // Start values 
      org.col = col;
      org.obj = obj;
      org.dis = dis;
      org.dis2 = dis2;
      org.ang = ang;
      org.thick = thick;
      org.tr = tr;

      // Feedback 

      for (i = 0; i < object_s.length() - 1; i++) {

        next = object_s.charAt(i + 1); // The next char in the string

        if (poly_limit && (poly_count > pcount_limit)) {
          break;
        }

        switch (object_s.charAt(i)) { // The current char in the string

          default:
            break;

          case '+':
            save.ang = ang;
            if (next == '(') {
              ang = ((double) 0.017453) * Get_value(i);
            }
            Set_rot((float) -ang, upp);
            //  					Util_t(fow, C1, C2, C3, v);
            v = util_t(fow, C1, C2, C3);
            vector_copy(v, fow);

            //  					Util_t(lef, C1, C2, C3, v);
            v = util_t(lef, C1, C2, C3);

            vector_copy(v, lef);

            //  					Vector_normalize(fow);
            vLength = vector_length(fow);
            fow[_x] = fow[_x] / vLength;
            fow[_y] = fow[_y] / vLength;
            fow[_z] = fow[_z] / vLength;

            //  					Vector_normalize(lef);
            vLength = vector_length(lef);
            lef[_x] = lef[_x] / vLength;
            lef[_y] = lef[_y] / vLength;
            lef[_z] = lef[_z] / vLength;

            ang = save.ang;
            break;

          case '-':
            save.ang = ang;
            if (next == '(') {
              ang = ((double) 0.017453) * Get_value(i);
            }
            Set_rot((float) ang, upp);
            //  					Util_t(fow, C1, C2, C3, v);
            v = util_t(fow, C1, C2, C3);

            vector_copy(v, fow);
            //  					Util_t(lef, C1, C2, C3, v);
            v = util_t(lef, C1, C2, C3);

            vector_copy(v, lef);

            //  					Vector_normalize(fow);
            vLength = vector_length(fow);
            fow[_x] = fow[_x] / vLength;
            fow[_y] = fow[_y] / vLength;
            fow[_z] = fow[_z] / vLength;

            //  					Vector_normalize(lef);
            vLength = vector_length(lef);
            lef[_x] = lef[_x] / vLength;
            lef[_y] = lef[_y] / vLength;
            lef[_z] = lef[_z] / vLength;
            ang = save.ang;
            break;

          case '~':
            if (next == '(') {
              r = ((double) 0.017453) * Get_value(i);
            } else {
              r = (double) 6.0;
            }
            a = (Math.random() * r * (double) 2.0) - r;
            Set_rot((float) a, upp);
            //  					Util_t(fow, C1, C2, C3, v);
            v = util_t(fow, C1, C2, C3);

            vector_copy(v, fow);
            //  					Util_t(lef, C1, C2, C3, v);
            v = util_t(lef, C1, C2, C3);

            vector_copy(v, lef);

            //  					Vector_normalize(fow);
            vLength = vector_length(fow);
            fow[_x] = fow[_x] / vLength;
            fow[_y] = fow[_y] / vLength;
            fow[_z] = fow[_z] / vLength;

            //  					Vector_normalize(lef);
            vLength = vector_length(lef);
            lef[_x] = lef[_x] / vLength;
            lef[_y] = lef[_y] / vLength;
            lef[_z] = lef[_z] / vLength;

            a = (Math.random() * r * (double) 2.0) - r;

            Set_rot((float) a, lef);

            //  					Util_t(fow, C1, C2, C3, v);
            v = util_t(fow, C1, C2, C3);

            vector_copy(v, fow);
            //  					Util_t(upp, C1, C2, C3, v);
            v = util_t(upp, C1, C2, C3);

            vector_copy(v, upp);

            //  					Vector_normalize(fow);
            vLength = vector_length(fow);
            fow[_x] = fow[_x] / vLength;
            fow[_y] = fow[_y] / vLength;
            fow[_z] = fow[_z] / vLength;

            //  					Vector_normalize(upp);
            vLength = vector_length(upp);
            upp[_x] = upp[_x] / vLength;
            upp[_y] = upp[_y] / vLength;
            upp[_z] = upp[_z] / vLength;

            a = (Math.random() * r * (double) 2.0) - r;

            Set_rot((float) a, fow);
            //  					Util_t(lef, C1, C2, C3, v);
            v = util_t(lef, C1, C2, C3);

            vector_copy(v, lef);
            //  					Util_t(upp, C1, C2, C3, v);
            v = util_t(upp, C1, C2, C3);

            vector_copy(v, upp);

            //  					Vector_normalize(lef);
            vLength = vector_length(lef);
            lef[_x] = lef[_x] / vLength;
            lef[_y] = lef[_y] / vLength;
            lef[_z] = lef[_z] / vLength;

            //  					Vector_normalize(upp);
            vLength = vector_length(upp);
            upp[_x] = upp[_x] / vLength;
            upp[_y] = upp[_y] / vLength;
            upp[_z] = upp[_z] / vLength;
            break;

          case 't':
            if ((fow[_x] == zero) && (fow[_y] == zero)) {
              break;
            }
            save.tr = tr;
            if (next == '(') {
              tr = Get_value(i);
            }

            vector_copy(fow, trope);

            //  					Vector_make(trope, -trope[_x], -trope[_y], zero);
            trope[_x] = -trope[_x];
            trope[_y] = -trope[_y];
            trope[_z] = (float) zero;

            //  					Vector_normalize(trope);

            vLength = vector_length(trope);
            trope[_x] = trope[_x] / vLength;
            trope[_y] = trope[_y] / vLength;
            trope[_z] = trope[_z] / vLength;

            r = tr * scalar_product(fow, trope);

            Set_rot((float) -r, lef);

            //  					Util_t(fow, C1, C2, C3, v);
            v = util_t(fow, C1, C2, C3);

            vector_copy(v, fow);
            //  					Util_t(upp, C1, C2, C3, v);
            v = util_t(upp, C1, C2, C3);

            vector_copy(v, upp);

            //  					Vector_normalize(fow);
            vLength = vector_length(fow);
            fow[_x] = fow[_x] / vLength;
            fow[_y] = fow[_y] / vLength;
            fow[_z] = fow[_z] / vLength;
            //  					Vector_normalize(upp);
            vLength = vector_length(upp);
            upp[_x] = upp[_x] / vLength;
            upp[_y] = upp[_y] / vLength;
            upp[_z] = upp[_z] / vLength;
            tr = save.tr;
            break;

          case '$':
            Vector_min(fow, sky, v);
            if (vector_length(v) == zero) {
              break;
            }
            Vector_product(fow, sky, lef);
            Vector_product(fow, lef, upp);
            if (upp[_z] < zero) {
              Vector_neg(upp);
              Vector_neg(lef);
            }
            break;

          case '&':
            save.ang = ang;
            if (next == '(') {
              ang = ((double) 0.017453) * Get_value(i);
            }
            Set_rot((float) ang, lef);
            //  					Util_t(fow, C1, C2, C3, v);
            v = util_t(fow, C1, C2, C3);

            vector_copy(v, fow);
            //  					Util_t(upp, C1, C2, C3, v);
            v = util_t(upp, C1, C2, C3);

            vector_copy(v, upp);
            //  					Vector_normalize(fow);
            vLength = vector_length(fow);
            fow[_x] = fow[_x] / vLength;
            fow[_y] = fow[_y] / vLength;
            fow[_z] = fow[_z] / vLength;
            //  					Vector_normalize(upp);
            vLength = vector_length(upp);
            upp[_x] = upp[_x] / vLength;
            upp[_y] = upp[_y] / vLength;
            upp[_z] = upp[_z] / vLength;
            ang = save.ang;
            break;

          case '^':
            save.ang = ang;
            if (next == '(') {
              ang = ((double) 0.017453) * Get_value(i);
            }
            Set_rot((float) -ang, lef);
            //  					Util_t(fow, C1, C2, C3, v);
            v = util_t(fow, C1, C2, C3);
            vector_copy(v, fow);
            //  					Util_t(upp, C1, C2, C3, v);
            v = util_t(upp, C1, C2, C3);

            vector_copy(v, upp);
            //  					Vector_normalize(fow);
            vLength = vector_length(fow);
            fow[_x] = fow[_x] / vLength;
            fow[_y] = fow[_y] / vLength;
            fow[_z] = fow[_z] / vLength;
            //  					Vector_normalize(upp);
            vLength = vector_length(upp);
            upp[_x] = upp[_x] / vLength;
            upp[_y] = upp[_y] / vLength;
            upp[_z] = upp[_z] / vLength;
            ang = save.ang;
            break;

          case '<':
            save.ang = ang;
            if (next == '(') {
              ang = ((double) 0.017453) * Get_value(i);
            }
            Set_rot((float) -ang, fow);
            //  					Util_t(lef, C1, C2, C3, v);
            v = util_t(lef, C1, C2, C3);

            vector_copy(v, lef);
            //  					Util_t(upp, C1, C2, C3, v);
            v = util_t(upp, C1, C2, C3);

            vector_copy(v, upp);
            //  					Vector_normalize(lef);
            vLength = vector_length(lef);
            lef[_x] = lef[_x] / vLength;
            lef[_y] = lef[_y] / vLength;
            lef[_z] = lef[_z] / vLength;

            //  					Vector_normalize(upp);
            vLength = vector_length(upp);
            upp[_x] = upp[_x] / vLength;
            upp[_y] = upp[_y] / vLength;
            upp[_z] = upp[_z] / vLength;
            ang = save.ang;
            break;

          case '>':
            save.ang = ang;
            if (next == '(') {
              ang = ((double) 0.017453) * Get_value(i);
            }
            Set_rot((float) ang, fow);

            //  					Util_t(lef, C1, C2, C3, v);
            v = util_t(lef, C1, C2, C3);

            vector_copy(v, lef);
            //  					Util_t(upp, C1, C2, C3, v);
            v = util_t(upp, C1, C2, C3);

            vector_copy(v, upp);
            //  					Vector_normalize(lef);
            vLength = vector_length(lef);
            lef[_x] = lef[_x] / vLength;
            lef[_y] = lef[_y] / vLength;
            lef[_z] = lef[_z] / vLength;

            //  					Vector_normalize(upp);
            vLength = vector_length(upp);
            upp[_x] = upp[_x] / vLength;
            upp[_y] = upp[_y] / vLength;
            upp[_z] = upp[_z] / vLength;
            ang = save.ang;
            break;

          case '%':
            Set_rot((float) pi, fow);
            //  					Util_t(lef, C1, C2, C3, v);
            v = util_t(lef, C1, C2, C3);

            vector_copy(v, lef);
            //  					Util_t(upp, C1, C2, C3, v);
            v = util_t(upp, C1, C2, C3);

            vector_copy(v, upp);
            //  					Vector_normalize(lef);
            vLength = vector_length(lef);
            lef[_x] = lef[_x] / vLength;
            lef[_y] = lef[_y] / vLength;
            lef[_z] = lef[_z] / vLength;

            //  					Vector_normalize(upp);
            vLength = vector_length(upp);
            upp[_x] = upp[_x] / vLength;
            upp[_y] = upp[_y] / vLength;
            upp[_z] = upp[_z] / vLength;
            break;

          case '|':
            Set_rot((float) pi, upp);
            //  					Util_t(fow, C1, C2, C3, v);
            v = util_t(fow, C1, C2, C3);

            vector_copy(v, fow);
            //  					Util_t(lef, C1, C2, C3, v);
            v = util_t(lef, C1, C2, C3);

            vector_copy(v, lef);
            //  					Vector_normalize(fow);
            vLength = vector_length(fow);
            fow[_x] = fow[_x] / vLength;
            fow[_y] = fow[_y] / vLength;
            fow[_z] = fow[_z] / vLength;

            //  					Vector_normalize(lef);
            vLength = vector_length(lef);
            lef[_x] = lef[_x] / vLength;
            lef[_y] = lef[_y] / vLength;
            lef[_z] = lef[_z] / vLength;
            break;

          case '!':
            if (next == '(') {
              thick *= Get_value(i);
            } else {
              thick *= (double) 0.7;
            }
            break;

          case '?':
            if (next == '(') {
              thick *= Get_value(i);
            } else {
              thick /= (double) 0.7;
            }
            break;

          case ':':
            if (next == '(') {
              ang *= Get_value(i);
            } else {
              ang *= (double) 0.9;
            }
            break;

          case ';':
            if (next == '(') {
              ang *= Get_value(i);
            } else {
              ang /= (double) 0.9;
            }
            break;

          case '\'':
            if (next == '(') {
              r = Get_value(i);
              dis *= r;
              dis2 *= r;
            } else {
              dis *= (double) 0.9;
              dis2 *= (double) 0.9;
            }
            break;

          case '\"':
            if (next == '(') {
              r = Get_value(i);
              dis *= r;
              dis2 *= r;
            } else {
              dis /= (double) 0.9;
              dis2 /= (double) 0.9;
            }
            break;

          case 'Z':
            save.dis2 = dis2;
            if (next == '(') {
              dis2 = Get_value(i);
            }
            Vector_plus_fac(pos, fow, dis2, end);
            if (closed_form) {
              Define_closed(pos, end, upp, col);
            } else {
              Define_block(pos, end, upp, col);
            }
            vector_copy(end, pos);
            dis2 = save.dis2;
            break;

          case 'F':
            save.dis = dis;
            if (next == '(') {
              dis = Get_value(i);
            }
            Vector_plus_fac(pos, fow, dis, end);
            if (closed_form) {
              Define_closed(pos, end, upp, col);
            } else {
              Define_block(pos, end, upp, col);
            }
            vector_copy(end, pos);
            dis = save.dis;
            break;

          case '[':
            if (scount > max_stack) {
              System.out.println("Ran out of stack");
            }
            vector_copy(pos, stack[scount].pos);
            vector_copy(fow, stack[scount].fow);
            vector_copy(lef, stack[scount].lef);
            vector_copy(upp, stack[scount].upp);

            stack[scount].col = col;
            stack[scount].dis = dis;
            stack[scount].dis2 = dis2;
            stack[scount].ang = ang;
            stack[scount].thick = thick;
            stack[scount].tr = tr;

            if (closed_form) {
              vector_copy(last, stack[scount].last);
              stack[scount].last_col = last_col;

              //  						vector_copy(last_v[j][k], stack[scount].last_v[j][k]);
              for (j = 1; j <= 8; j++) {
                for (int k = 0; k < 3; k++)
                  stack[scount].last_v[j][k] = last_v[j][k];
              }
            }
            scount++;
            break;

          case ']':
            scount--;
            vector_copy(stack[scount].pos, pos);
            vector_copy(stack[scount].fow, fow);
            vector_copy(stack[scount].lef, lef);
            vector_copy(stack[scount].upp, upp);

            col = stack[scount].col;
            dis = stack[scount].dis;
            dis2 = stack[scount].dis2;
            ang = stack[scount].ang;
            thick = stack[scount].thick;
            tr = stack[scount].tr;
            if (closed_form) {
              vector_copy(stack[scount].last, last);
              last_col = stack[scount].last_col;
              //  							vector_copy(stack[scount].last_v[j], last_v[j]);
              for (j = 1; j <= 8; j++) {
                for (int k = 0; k < 3; k++)
                  last_v[j][k] = stack[scount].last_v[j][k];
              }
            }

            break;

          case '{':
            if (poly_on) {
              //  						pstack[pscount].count = vcount;
              //C++ TO JAVA CONVERTER TODO TASK: The memory management function 'malloc' has no equivalent in Java:
              //  						pstack[pscount].ver = (vector) malloc(vcount * 12);

              //  						if (pstack[pscount].ver == null)
              //  						{
              //  							User_error("Ran out of memory");
              //  						}
              //  						Vector_copy_max_r32(vcount, ver, pstack[pscount].ver);

              ArrayList<point3D> vPoints = new ArrayList<point3D>();

              for (int k = 0; k < vcount; k++) {
                vPoints.add(new point3D(ver[k][_x], ver[k][_y], ver[k][_z]));
              }

              pstack.add(new p_rec(vcount, vPoints));
              pscount++;

              if (pscount > max_stack) {
                System.out.println("Ran out of stack");
              }
            }
            poly_on = true;
            vcount = (int) 1;
            pcount = (int) 1;
            break;

          case 'f':
            save.dis = dis;
            if (next == '(') {
              dis = Get_value(i);
            }
            Vector_plus_fac(pos, fow, dis, pos);
            if (poly_on) {
              vector_copy(pos, ver[vcount++]);
            }
            dis = save.dis;
            break;

          case '.':
            if (poly_on) {
              vector_copy(pos, ver[vcount++]);
            }
            break;

          case 'g':
            save.dis = dis;
            if (next == '(') {
              dis = Get_value(i);
            }
            Vector_plus_fac(pos, fow, dis, pos);
            dis = save.dis;
            break;

          case 'z':
            save.dis2 = dis2;
            if (next == '(') {
              dis2 = Get_value(i);
            }
            Vector_plus_fac(pos, fow, dis2, pos);
            if (poly_on) {
              vector_copy(pos, ver[vcount++]);
            }
            dis2 = save.dis2;
            break;

          case '}':
            if (vcount > (int) 3) {
              for (j = 1; j < vcount - 2; j++) {
                poly_store[pcount][0] = 1;
                poly_store[pcount][1] = j + 1;
                poly_store[pcount][2] = j + 2;
                poly_store[pcount][3] = j + 2;
                pcount++;
              }
              Save_object(vcount - 1, pcount - 1, col);
            }
            poly_on = false;
            if (pscount > 0) {
              pscount--;
              //  						Vector_copy_max_r32(pstack[pscount].count, pstack[pscount].ver, ver);

              ArrayList<point3D> vPoints = new ArrayList<point3D>();

              int l = pstack.get(pscount).count;
              ArrayList<point3D> vecs = pstack.get(pscount).ver;

              for (int k = 0; k < l; k++) {
                vPoints.add(new point3D(vecs.get(k).x, vecs.get(k).y, vecs.get(k).z));
              }

              for (int k = 0; k < l; k++) {
                ver[k][_x] = vPoints.get(k).x;
                ver[k][_y] = vPoints.get(k).y;
                ver[k][_z] = vPoints.get(k).z;
              }
              vcount = l;
              poly_on = true;
            }
            break;

          case 'c':
            if (next == '(') {
              col = (int) Get_value(i);
            } else {
              col++;
            }
            break;
        }
      }

    }

    public SimpleMesh getMesh() {
      return this.mesh;
    }
  }

}
