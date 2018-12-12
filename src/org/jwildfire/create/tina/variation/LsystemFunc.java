package org.jwildfire.create.tina.variation;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jwildfire.base.mathlib.MathLib.*;

public class LsystemFunc extends VariationFunc {

  private static final long serialVersionUID = 1L;

  private static final String PARAM_ITERS = "iters";
  private static final String PARAM_STEP = "step";
  private static final String PARAM_ANGLE = "angle";
  private static final String PARAM_PRESETID = "presetId";
  private static final String PARAM_SHOW_LINES = "show_lines_param";
  private static final String PARAM_LINE_THICKNESS = "line_thickness";
  private static final String PARAM_SHOW_POINTS = "show_points_param";
  private static final String PARAM_POINT_THICKNESS = "point_thickness_param";

  private static final String RESSOURCE_GRAMMAR = "grammar";

  private static final String[] paramNames = {PARAM_ITERS, PARAM_STEP, PARAM_ANGLE, PARAM_PRESETID, PARAM_SHOW_LINES, PARAM_LINE_THICKNESS, PARAM_SHOW_POINTS, PARAM_POINT_THICKNESS};
  private static final String[] ressourceNames = {RESSOURCE_GRAMMAR};

  String string = new String(
          "; Small set of Commands for the Lsystem_js variation en JWF \n" +
                  "; F, or d : Draw segment forward (Step) size \n" +
                  "; G, or m : move forward, without drawing (Step) Size \n" +
                  "; + : rotate the turtle 90 Degress counterclockwise \n" +
                  "; - : rotate the turtle 90 Degress clockwise \n" +
                  "; | : rotate the turtle 180 Degress \n" +
                  "; ! : Switch the rotatation angle commands '+' clockwise, '-' = counterclockwise \n" +
                  "; [ : push the Turtle state on stack \n" +
                  "; ] : pop the Turtle Stack from Stack \n" +
                  "ADH155 { ; Anthony Hanmer 2000\n" +
                  "Angle 45 ; Cesaro variation\n" +
                  "Axiom f\n" +
                  "f=f++++++++++f--------------------f++++++++++f\n" +
                  "}\n");

  /*    "SnowFlake2 { ; Adrian Mariano from The Fractal Geometry of Nature by Mandelbrot\n" +
    "angle 12\n" +
    "axiom F\n" +
    "F=++!F!F--F--F@IQ3|+F!F--\n" +
    "F=F--F!+++@Q3F@QI3|+F!F@Q3|+F!F\n" +
    "}\n");
  */

  int Iters = 3;
  double step = 0.1;
  double angle = 0.0;
  int presetId = -1;
  private int show_lines_param = 1;
  double line_thickness_param = 0.5;
  double line_thickness;
  private int show_points_param = 0;
  private double point_thickness_param = 3.0;
  private double point_thickness;

  private double line_fraction, point_fraction;
  private double line_threshold, point_threshold;

  Render canvas;

  private interface ParameterModifier {

    public void parameterModify(int i, SymbolString pred, SymbolString succ);
  }

  private interface ConditionEvaluator {
    public boolean conditionEval(int i, SymbolString ms);
  }

  private class Symbol {

    private char id;
    private int numberOfParams;
    private Double params[];

    public Symbol(char id, int numberOfParams) {
      this.id = id;
      this.numberOfParams = numberOfParams;
      params = new Double[numberOfParams];
      for (int i = 0; i < numberOfParams; i++) {
        params[i] = new Double(0.0);
        params[i] = 0.0;
      }
    }

    public Symbol(Symbol module) {
      this.id = module.id;
      this.numberOfParams = module.numberOfParams;
      this.params = new Double[numberOfParams];

      for (int i = 0; i < numberOfParams; i++) {
        this.params[i] = new Double(0.0);
        this.params[i] = module.params[i];
      }
    }

    public char getId() {
      return this.id;
    }

    public void setId(char id) {
      this.id = id;
    }

    public int getNumberOfParams() {
      return this.numberOfParams;
    }

    public void setParam(int i, double param) {
      params[i] = param;
    }

    public Double getParam(int i) {
      return this.params[i];
    }

    public boolean equals(Symbol m) {
      return (this.id == m.id && this.numberOfParams == m.numberOfParams);
    }

  }

  private class SymbolString {

    private Vector<Symbol> symbols = new Vector<Symbol>();

    public SymbolString() {

    }

    public SymbolString(SymbolString ms) {
      addSymbol(ms);
    }

    public SymbolString(int fromIndex, int toIndex, SymbolString ms) {
      if (fromIndex > toIndex)
        toIndex ^= fromIndex ^= toIndex ^= fromIndex;
      for (Symbol m : ms.symbols.subList(fromIndex, toIndex + 1))
        addSymbol(m);

    }

    public void addSymbol(Symbol m) {
      symbols.add(new Symbol(m));
    }

    public void addSymbol(SymbolString ms) {
      for (Symbol m : ms.symbols) {
        addSymbol(m);
      }
    }

    public void deleteSymbol(int index) {
      symbols.remove(index);
    }

    public void insertSymbol(int index, Symbol m) {
      symbols.add(index, new Symbol(m));
    }

    public void insertSymbol(int index, SymbolString ms) {
      int i = 0;
      for (Symbol m : ms.symbols) {
        insertSymbol(index + i, m);
        i++;
      }
    }

    public void replaceSymbol(int index, Symbol m) {
      deleteSymbol(index);
      insertSymbol(index, m);
    }

    public void replaceSymbol(int index, SymbolString ms) {
      deleteSymbol(index);
      insertSymbol(index, ms);
    }

    public int numOfSymbols() {
      return symbols.size();
    }

    public Symbol getSymbol(int index) {
      return symbols.get(index);
    }

    public void setSymbol(int index, Symbol m) {
      replaceSymbol(index, m);
    }

    public boolean equals(SymbolString ms) {
      if (numOfSymbols() != ms.numOfSymbols())
        return false;
      for (int i = 0; i < numOfSymbols(); i++) {
        if (!getSymbol(i).equals(ms.getSymbol(i)))
          return false;
      }
      return true;
    }
  }

  private class Rule {

    private Symbol predRef;
    private SymbolString succRef;
    private SymbolString lContextRef;
    private SymbolString rContextRef;
    private ConditionEvaluator evaluate;
    private ParameterModifier modify;

    public Rule(Symbol predecessor,
                SymbolString successor,
                ConditionEvaluator conditionEval,
                ParameterModifier parameterModify,
                SymbolString leftContext,
                SymbolString rightContext) {
      predRef = new Symbol(predecessor);
      succRef = new SymbolString(successor);
      lContextRef = leftContext == null ? new SymbolString() : new SymbolString(leftContext);
      rContextRef = rightContext == null ? new SymbolString() : new SymbolString(rightContext);
      if (conditionEval == null)
        evaluate = new ConditionEvaluator() {
          public boolean conditionEval(int i, SymbolString ms) {
            return true;
          }
        };
      else
        evaluate = conditionEval;
      if (parameterModify == null)
        modify = new ParameterModifier() {
          public void parameterModify(int i, SymbolString pred, SymbolString succ) {
          }

        };
      else
        modify = parameterModify;
    }

    public Rule(Rule prod) {
      predRef = new Symbol(prod.predRef);
      succRef = new SymbolString(prod.succRef);
      lContextRef = new SymbolString(prod.lContextRef);
      rContextRef = new SymbolString(prod.rContextRef);
      evaluate = prod.evaluate;
      modify = prod.modify;
    }

    public boolean applyAtIndex(int index, SymbolString mPredString, SymbolString mSuccString) {
      if (mPredString.getSymbol(index).equals(predRef)) {
        boolean lCon = checkLContext(index, mPredString);
        boolean rCon = checkRContext(index, mPredString);
        boolean cond = evaluate.conditionEval(index, mPredString);
        if (lCon && rCon && cond) {
          mSuccString.replaceSymbol(index, succRef);
          modify.parameterModify(index, mPredString, mSuccString);
          return true;
        }
      }
      return false;
    }

    private boolean checkLContext(int index, SymbolString mString) {
      int length = lContextRef.numOfSymbols();
      boolean res = (length == 0);
      if (length > 0 && length <= index) {
        SymbolString temp = new SymbolString(index - length, index - 1, mString);
        res = (temp.equals(lContextRef));
      }
      return res;
    }

    private boolean checkRContext(int index, SymbolString mString) {
      int length = rContextRef.numOfSymbols();
      boolean res = (length == 0);
      if (length > 0 && index + length < mString.numOfSymbols()) {
        SymbolString temp = new SymbolString(index + 1, index + length, mString);
        res = (temp.equals(rContextRef));
      }
      return res;
    }
  }

  private class Productions {

    private Vector<Rule> productions = new Vector<Rule>();
    private int MaxLength = Prefs.getPrefs().getTinaLSystemMaxLength();

    public Productions() {

    }

    public void addProduction(Rule prod) {
      productions.add(prod);
    }

    public void deleteProduction(int index) {
      productions.remove(index);
    }

    public Rule getProduction(int index) {
      return productions.get(index);
    }

    public void setProduction(int index, Rule prod) {
      productions.remove(index);
      productions.add(index, prod);
    }

    public int numberOfProductions() {
      return productions.size();
    }

    public boolean apply(SymbolString mString) {
      boolean altered = false;
      SymbolString mPredString = new SymbolString(mString);
      for (int i = mString.numOfSymbols() - 1; i >= 0; i--) {
        for (int j = 0; j < productions.size(); j++) {
          Rule prod = productions.get(j);
          altered = prod.applyAtIndex(i, mPredString, mString);
          if (altered)
            break;
        }
        if (mString.numOfSymbols() > MaxLength) break;
      }
      return altered;
    }

    public String iterate(int noIters, SymbolString axiom) {
      String sIteration = new String();
      for (int i = 0; i < noIters; i++) {
        StringBuilder sExpanded = new StringBuilder();
        apply(axiom);
        if (axiom.numOfSymbols() > MaxLength) break;
        for (int j = 0; j < axiom.numOfSymbols(); j++) {
          sExpanded.append(axiom.getSymbol(j).getId());
        }
        //	    System.out.println("Iteracion No " + i + " -{ " + sExpanded + "} ");
        sIteration = sExpanded.substring(0, sExpanded.length());
      }
      return sIteration;
    }
  }

  private class LSystem {

    SymbolString axioma = null;

    double angle = 90;

    public void setAngle(double angle) {
      this.angle = angle;
    }

    public double getAngle() {
      return this.angle;
    }

    public LSystem() {

    }

    private SymbolString build_axiom(String axiom) {
      Character Symbol;
      HashMap<Character, Symbol> Symbols = new HashMap<Character, Symbol>();
      SymbolString axioma = new SymbolString();

      for (int i = 0; i < axiom.length(); i++) {
        Symbol = axiom.charAt(i);
        if (!Symbols.containsKey(Symbol))
          Symbols.put(Symbol, new Symbol(Symbol, 0));
      }

      for (int i = 0; i < axiom.length(); i++) {
        Symbol = axiom.charAt(i);
        axioma.addSymbol(Symbols.get(Symbol));
      }

      return axioma;
    }

    private Rule build_rule(String lhs, String rhs) {
      Character Symbol;
      SymbolString succ = new SymbolString();

      HashMap<Character, Symbol> Symbols = new HashMap<Character, Symbol>();

      Rule prod = null;

      Symbol left = new Symbol(lhs.charAt(0), 0);
      //      Symbols.put(lhs.charAt(0),left);	

      for (int i = 0; i < rhs.length(); i++) {
        Symbol = rhs.charAt(i);
        if (!Symbols.containsKey(Symbol))
          Symbols.put(Symbol, new Symbol(Symbol, 0));
      }
      //	    		
      for (int i = 0; i < rhs.length(); i++) {
        Symbol = rhs.charAt(i);
        succ.addSymbol(Symbols.get(Symbol));
      }

      prod = new Rule(left, succ, null, null, null, null);
      return prod;
    }

    public String processFile(String expanded_str, int nIter) throws IOException {
      byte[] filecontent = expanded_str.getBytes();
      InputStream is = null;
      BufferedReader br = null;
      is = new ByteArrayInputStream(filecontent);

      // Construct BufferedReader from FileReader
      br = new BufferedReader(new InputStreamReader(is));
      StringTokenizer st;
      String token;
      Double Value;
      Rule rule = null;
      Productions pmanager = new Productions();

      String line = null;
      while ((line = br.readLine()) != null) {
        //   System.out.println(line);
        int poscomment = line.indexOf(";");
        if (poscomment == 0) // descarta lineas solo de comentario
          continue;
        if (poscomment > -1) // Descarta Comentarios al final de la linea de todas las lineas que tengan
          line = line.substring(0, line.indexOf(";") - 1);
        line = line.toUpperCase();
        if (line.indexOf("{") > -1 || line.indexOf("}") > -1) // Saltarse lineas de Inicio y Fin 
          continue;
        else {
          if (line.indexOf("=") > -1 || line.toUpperCase().indexOf("AXIOM") > -1) // procesa lineas AXIOM y PRODUCCIONES
          {
            st = new StringTokenizer(line);
            while (st.hasMoreElements()) {
              token = (String) st.nextElement();
              if ("AXIOM".equals(token.toUpperCase())) {
                String axiom = (String) st.nextElement();
                axioma = build_axiom(axiom);
                //			   System.out.println("AXIOM " + axiom);
              } else {
                int idxequal = line.indexOf("=");
                String lhs = line.substring(0, idxequal);
                lhs = lhs.replaceAll("\\s", "");
                String rhs = line.substring(idxequal + 1, line.length());
                rhs = rhs.replaceAll("\\s", "");
                rule = build_rule(lhs, rhs);
                pmanager.addProduction(rule);
                //				   System.out.println("P " + lhs + "=" + rhs);
              }
            }
          } else // Procesa linea de parametro ANGLE
          {
            st = new StringTokenizer(line);
            while (st.hasMoreElements()) {
              token = (String) st.nextElement();
              if ("ANGLE".equals(token.toUpperCase())) {
                token = (String) st.nextElement();
                if ("=".equals(token))
                  token = (String) st.nextElement();
                Value = Double.parseDouble(token);
                //				System.out.println("ANGLE " + "= " + Value);
                setAngle(360.0 / Value);
              } else //lineas en blanco
                continue;
            }
          }
        }
      }
      br.close();
      String strExpanded = pmanager.iterate(nIter, axioma);
      return strExpanded;
    }
  }

  private class DynamicArray {
    // The storage for the elements. 
    // The capacity is the length of this array.
    private double[] data;

    // The number of elements (logical size).
    // It must be smaller than the capacity.
    private int size;

    // Constructs an empty DynamicArray
    public DynamicArray() {
      data = new double[16];
      size = 0;
    }

    // Constructs an empty DynamicArray with the
    // specified initial capacity.
    public DynamicArray(int capacity) {
      if (capacity < 16)
        capacity = 16;
      data = new double[capacity];
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
    public double get(int index) {
      rangeCheck(index);
      return data[index];
    }

    // Appends the specified element to the end.
    public boolean add(double element) {
      ensureCapacity(size + 1);
      data[size++] = element;
      return true;
    }

    // Removes all of the elements.
    public void clear() {
      size = 0;
    }

    // Replaces the element at the specified position
    public double set(int index, double element) {
      rangeCheck(index);
      double oldValue = data[index];
      data[index] = element;
      return oldValue;
    }

    public int capacity() {
      return data.length;
    }
  }

  private class Turtle implements Cloneable {

    private double tx, ty; // turtle is at (x, y)
    private double tangle; // facing this many degrees counterclockwise from the x-axis
    private double step;

    private DynamicArray xpoints = new DynamicArray();
    private DynamicArray ypoints = new DynamicArray();

    private int idx;

    public Object clone() throws CloneNotSupportedException {
      return new Turtle(this);
    }
    // start at (x0, y0), facing a0 degrees counterclockwise from the x-axis

    public Turtle(Turtle turtle) {
      tx = turtle.tx;
      ty = turtle.ty;

      tangle = turtle.tangle;
      step = turtle.step;

      xpoints = turtle.xpoints;
      ypoints = turtle.ypoints;
    }

    public Turtle(double x0, double y0, double a0, double b0) {
      tx = x0;
      ty = y0;
      tangle = a0;
      setStep(b0);
      clearPoints();
    }

    public void setStep(double step) {
      this.step = step;
    }

    public double getStep() {
      return this.step;
    }

    // rotate orientation delta degrees counterclockwise
    public void turnRigth(double delta) {
      tangle -= delta;
    }

    // rotate orientation delta degrees counterclockwise
    public void turnLeft(double delta) {
      tangle += delta;
    }

    // move forward the given amount, with the pen up

    public void goUp(double step) {
      this.step = step;
      tx += step * MathLib.cos(Math.toRadians(tangle));
      ty += step * MathLib.sin(Math.toRadians(tangle));

    }

    // move forward the given amount, with the pen down

    public void goDown(double step) {
      double oldx = tx;
      double oldy = ty;

      this.step = step;

      tx += step * MathLib.cos(Math.toRadians(tangle));
      ty += step * MathLib.sin(Math.toRadians(tangle));

      xpoints.add(oldx);
      ypoints.add(oldy);

      xpoints.add(tx);
      ypoints.add(ty);
      //	      System.out.println("x1,y1 - x2,y2 >" + oldx +" , " + oldy+ " - " + tx +" , "+ty);

    }

    public void getPoint(DoublePoint2D result) {
      result.x = xpoints.get(idx);
      result.y = ypoints.get(idx);
      idx++;
      if (idx >= xpoints.size())
        idx = 0;
    }

    public void clearPoints() {
      xpoints.clear();
      ypoints.clear();
      idx = 0;
    }
  }

  private class DoublePoint2D {
    public double x;
    public double y;
  }

  private class Render {

    /**
     * The stack of turtles.
     */
    Stack<Turtle> turtleStack = new Stack<Turtle>();

    /**
     * The current turtle.
     */
    public Turtle currentTurtle;
    String theString;
    double render_angle;
    double render_step;

    public Render() {
      currentTurtle = new Turtle(0.0, 0.0, 0.0, render_step);
    }

    public double getStep() {
      return this.render_step;
    }

    public void setStep(double step) {
      this.render_step = step;
      currentTurtle.setStep(render_step);
    }

    public double getAngle() {
      return this.render_angle;
    }

    public void setAngle(double angle) {
      this.render_angle = angle;
    }

    public String getString() {
      return theString;
    }

    public void setString(String string) {
      this.theString = new StringBuffer(string).toString();
    }

    /**
     * This handles pushing on the turtle stack.
     *
     * @throws CloneNotSupportedException
     */
    private void PushTurtle() throws CloneNotSupportedException {
      turtleStack.push((Turtle) (currentTurtle.clone()));
    }

    /**
     * This handles popping the turtle stack.
     *
     * @throws CloneNotSupportedException
     */
    private void PopTurtle() throws CloneNotSupportedException {
      Turtle lt = (Turtle) turtleStack.pop();
      currentTurtle = lt;
      render_step = currentTurtle.getStep();
    }

    public void render() throws CloneNotSupportedException {
      String sub_line = new String();

      Pattern pattern;
      Matcher matcher;

      double sign = 1.0;
      //		for(int i=0;i<theString.length();i++)
      //		{
      int i = 0;
      do {
        char character = theString.charAt(i);
        switch (character) {
          case 'D':
          case 'F':
            currentTurtle.goDown(render_step);
            i++;
            break;
          case 'M':
          case 'G':
            currentTurtle.goUp(render_step);
            i++;
            break;
          case '+':
            currentTurtle.turnLeft(render_angle * sign);
            i++;
            break;
          case '-':
            currentTurtle.turnRigth(render_angle * sign);
            i++;
            break;
          case '|':
            currentTurtle.turnLeft(180);
            i++;
            break;
          case '!':
            sign = -1.0 * sign;
            i++;
            break;
          case '[':
            PushTurtle();
            i++;
            break;
          case ']':
            PopTurtle();
            render_step = currentTurtle.getStep();
            i++;
            break;
          case '/':
          case '\\':
            //					 if(character=='\\')
            sub_line = theString.substring(i + 1, theString.length());
            //					 if(character=='/')
            //						 sub_line=theString.substring(i+1,theString.length());
            pattern = Pattern.compile("[0-9]*\\.?[0-9]*");
            matcher = pattern.matcher(sub_line);
            if (matcher.find()) {
              String sAngle = matcher.group();
              Double vAngle = Double.parseDouble(sAngle);
              if (character == '\\')
                currentTurtle.turnLeft(vAngle * sign);
              else
                currentTurtle.turnRigth(vAngle * sign);
              i = i + matcher.end() + 1;
            }
            break;
          case '@':
            int k = i + 1;
            String strComb = new String("");
            if (theString.charAt(k) == 'Q' || theString.charAt(k) == 'I') {
              strComb = strComb + theString.charAt(k);
              k++;
              i++;
            }
            if (theString.charAt(k) == 'I' || theString.charAt(k) == 'Q') {
              strComb = strComb + theString.charAt(k);
              k++;
              i++;
            }
            sub_line = theString.substring(k, theString.length());
            pattern = Pattern.compile("[0-9]*\\.?[0-9]*");
            matcher = pattern.matcher(sub_line);
            if (matcher.find()) {
              String sScale = matcher.group();
              Double vScale = Double.parseDouble(sScale);
              if (strComb.equals(""))
                setStep(render_step * vScale);
              if (strComb.equals("Q"))
                setStep(render_step * MathLib.sqrt(vScale));
              if (strComb.equals("I"))
                setStep(render_step * 1.0 / vScale);
              if (strComb.equals("IQ"))
                setStep(render_step * 1.0 / MathLib.sqrt(vScale));
              if (strComb.equals("QI"))
                setStep(render_step * MathLib.sqrt(vScale) * 1.0 / vScale);
              i = i + matcher.end() + 1;
            }
            break;
          case '<':
          case '>':
          case 'C':
            sub_line = theString.substring(i + 1, theString.length());
            // pattern=Pattern.compile("[\\d]*[\\.]?[\\d]*");
            pattern = Pattern.compile("[0-9]*\\.?[0-9]*");
            matcher = pattern.matcher(sub_line);
            if (matcher.find()) {
              //  String sAngle=matcher.group();
              //  Double vAngle=Double.parseDouble(sAngle);	
              i = i + matcher.end() + 1;
            }
            break;
          default:
            i++;
            continue;
        }
      }
      while (i < theString.length());
    }

    public Turtle getTurtle() {
      return currentTurtle;
    }
  }

  private DoublePoint2D endpoint1 = new DoublePoint2D();
  private DoublePoint2D endpoint2 = new DoublePoint2D();

  public DoublePoint2D plotLine(FlameTransformationContext pContext, double x1, double y1, double x2, double y2) {
    double ydiff = y2 - y1;
    double xdiff = x2 - x1;
    double m;
    if (xdiff == 0)
      m = 10000;
    else
      m = ydiff / xdiff; // slope
    double line_length = MathLib.sqrt((xdiff * xdiff) + (ydiff * ydiff));
    double xout = 0, yout = 0;
    double xoffset = 0, yoffset = 0;

    // draw point at a random distance along line
    //    (along straight line connecting endpoint1 and endpoint2)
    double d = pContext.random() * line_length;
    // x = x1 [+-] (d / (sqrt(1 + m^2)))
    // y = y1 [+-] (m * d / (sqrt(1 + m^2)))
    // determine sign based on orientation of endpoint2 relative to endpoint1
    xoffset = d / MathLib.sqrt(1 + m * m);
    if (x2 < x1) {
      xoffset = -1 * xoffset;
    }
    yoffset = Math.abs(m * xoffset);
    if (y2 < y1) {
      yoffset = -1 * yoffset;
    }
    if (line_thickness != 0) {
      xoffset += ((pContext.random() - 0.5) * line_thickness);
      yoffset += ((pContext.random() - 0.5) * line_thickness);
    }
    xout = x1 + xoffset;
    yout = y1 + yoffset;
    DoublePoint2D value = new DoublePoint2D();
    value.x = xout;
    value.y = yout;
    return value;
  }

  public DoublePoint2D plotPoint(FlameTransformationContext pContext, double x1, double y1) {
    double xout = 0, yout = 0;
    double xoffset = 0, yoffset = 0;
    // draw endpoints
    if (point_thickness != 0) {
      double roffset = pContext.random() * point_thickness;
      double rangle = (pContext.random() * M_2PI);
      xoffset = roffset * cos(rangle);
      yoffset = roffset * sin(rangle);
    } else {
      xoffset = 0;
      yoffset = 0;
    }
    // if (rnd <= point_half_threshold) {
    xout = x1 + xoffset;
    yout = y1 + yoffset;
    // }
    DoublePoint2D value = new DoublePoint2D();
    value.x = xout;
    value.y = yout;
    return value;
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    double show_sum = show_lines_param + show_points_param;

    line_fraction = show_lines_param / show_sum;
    point_fraction = show_points_param / show_sum;

    line_threshold = line_fraction;
    point_threshold = line_fraction + point_fraction;
    // point_half_threshold = line_fraction + (point_fraction/2);

    line_thickness = line_thickness_param / 100;
    point_thickness = point_thickness_param / 100;

    /*		  
    		  byte[] filecontent= string.getBytes();
    		  InputStream is=null;
    		  BufferedReader bfReader=null;
    		  try{
    			  is=new ByteArrayInputStream(filecontent);
    			  bfReader=new BufferedReader(new InputStreamReader(is));
    			  String temp=null;
    			  while((temp=bfReader.readLine())!= null)
    			  { 
    				  System.out.println(temp);
    			  }
    		  }
    		  catch (IOException e)
        {
    			  e.printStackTrace();
        } 
    		  finally 
    		  {
    			  try{
    			   if (is != null) is.close();
    		      }
    		      catch (Exception ex)
            {}
    		  }
    */
    LSystem ls = new LSystem();
    double file_angle;

    String expanded = new String("f+f+f+f");
    try {
      if (presetId != -1)
        string = getGrammarId();
      //   String str= getGrammarId();
      //    System.out.println("Grammar " + str);

      expanded = ls.processFile(string, Iters);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {

    }
    file_angle = ls.getAngle();
    canvas = new Render();
    canvas.setString(expanded);
    // System.out.println("String Expanded: " + expanded);    
    if (angle == 0.0)
      canvas.setAngle(file_angle);
    else
      canvas.setAngle(angle);
    //     System.out.println("File Angle: " + file_angle);
    canvas.setStep(step);
    try {

      canvas.render();

    } catch (Exception e) {
      e.printStackTrace();
    } finally {

    }

  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

    double x1 = 0.0;
    double y1 = 0.0;
    double x2 = 0.0;
    double y2 = 0.0;

    DoublePoint2D out = new DoublePoint2D();
    Turtle turtle = canvas.getTurtle();

    turtle.getPoint(endpoint1);
    x1 = endpoint1.x;
    y1 = endpoint1.y;

    turtle.getPoint(endpoint2);
    x2 = endpoint2.x;
    y2 = endpoint2.y;

    double rnd = pContext.random();

    if (rnd < line_threshold)
      out = plotLine(pContext, x1, y1, x2, y2);
    else if (rnd <= point_threshold)
      out = plotPoint(pContext, x1, y1);

    pVarTP.x += pAmount * out.x;
    pVarTP.y += pAmount * out.y;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  public String getGrammarId() throws Exception {

    StringBuilder result = new StringBuilder("");
    String grammar = new String("");
    try {
      InputStream inputStream = getClass().getResourceAsStream("lsystems.txt");
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
      int end = str.indexOf("}");
      grammar = str.substring(0, end + 1);
      if (vNumber == presetId) {
        break;
      }
    }
    //    	 System.out.println("Grammar " + grammar);
    return grammar;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{Iters, step, angle, presetId, show_lines_param, line_thickness_param, show_points_param, point_thickness_param};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_ITERS.equalsIgnoreCase(pName))
      Iters = (int) pValue;
    else if (PARAM_STEP.equalsIgnoreCase(pName))
      step = pValue;
    else if (PARAM_ANGLE.equalsIgnoreCase(pName))
      angle = pValue;
    else if (PARAM_PRESETID.equalsIgnoreCase(pName)) {
      presetId = (int) pValue;
    } else if (PARAM_LINE_THICKNESS.equalsIgnoreCase(pName))
      line_thickness = pValue;
    else if (PARAM_SHOW_LINES.equalsIgnoreCase(pName))
      show_lines_param = (int) pValue;
    else if (PARAM_LINE_THICKNESS.equalsIgnoreCase(pName))
      line_thickness_param = pValue;
    else if (PARAM_SHOW_POINTS.equalsIgnoreCase(pName))
      show_points_param = (int) pValue;
    else if (PARAM_POINT_THICKNESS.equalsIgnoreCase(pName))
      point_thickness_param = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][]{(string != null ? string.getBytes() : null)};
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_GRAMMAR.equalsIgnoreCase(pName)) {
      string = pValue != null ? new String(pValue) : "";
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
    return "lsystem_js";
  }
}
