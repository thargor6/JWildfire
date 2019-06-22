package org.jwildfire.create.tina.variation.cost;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.mutagen.Mutation;
import org.jwildfire.create.tina.mutagen.RandomParamMutation;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;
import org.jwildfire.create.tina.randomflame.AllRandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorState;
import org.jwildfire.create.tina.randomflame.SimpleRandomFlameGenerator;
import org.jwildfire.create.tina.randomgradient.StrongHueRandomGradientGenerator;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.variation.*;
import org.jwildfire.create.tina.variation.iflames.IFlamesFunc;
import org.jwildfire.create.tina.variation.mesh.OBJMeshWFFunc;
import org.jwildfire.create.tina.variation.mesh.Strange3DFunc;

import java.util.*;

public class VariationCostEvaluator {
  private final long EVAL_ROUNDS = 200000;
  private final long INIT_ROUNDS = 30000;

  private final long MAX_INIT_TIME = 10000000000L;
  private final long MAX_EVAL_TIME = 5000000000L;

  public static void main(String args[]) {
    new VariationCostEvaluator().evaluate();
  }

  private static class Item implements Comparable<Item> {
    private final Flame flame;
    private final VariationFunc fnc;
    private final String variationName;
    private long initRounds;
    private long initCost;
    private long initMemory;
    private boolean initError;
    private long evalRounds;
    private long evalCost;
    private long evalMemory;
    private boolean evalError;

    public Item(Flame flame, VariationFunc fnc, String variationName) {
      this.flame = flame;
      this.fnc = fnc;
      this.variationName = variationName;
    }

    public Flame getFlame() {
      return flame;
    }

    public VariationFunc getFnc() {
      return fnc;
    }

    public String getVariationName() {
      return variationName;
    }

    public long getEvalCost() {
      return evalCost;
    }

    public void setEvalCost(long evalCost) {
      this.evalCost = evalCost;
    }

    public long getInitCost() {
      return initCost;
    }

    public void setInitCost(long initCost) {
      this.initCost = initCost;
    }

    public long getInitMemory() {
      return initMemory;
    }

    public void setInitMemory(long initMemory) {
      this.initMemory = initMemory;
    }

    public long getEvalMemory() {
      return evalMemory;
    }

    public void setEvalMemory(long evalMemory) {
      this.evalMemory = evalMemory;
    }

    public boolean isInitError() {
      return initError;
    }

    public void setInitError(boolean initError) {
      this.initError = initError;
    }

    public boolean isEvalError() {
      return evalError;
    }

    public void setEvalError(boolean evalError) {
      this.evalError = evalError;
    }

    public long getInitRounds() {
      return initRounds;
    }

    public void setInitRounds(long initRounds) {
      this.initRounds = initRounds;
    }

    public long getEvalRounds() {
      return evalRounds;
    }

    public void setEvalRounds(long evalRounds) {
      this.evalRounds = evalRounds;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Item item = (Item) o;
      return evalCost == item.evalCost &&
              Objects.equals(variationName, item.variationName);
    }

    @Override
    public int hashCode() {
      return Objects.hash(variationName, evalCost);
    }

    @Override
    public int compareTo(Item o) {
      return Long.valueOf(this.getEvalCost()).compareTo(Long.valueOf(o.getEvalCost()));
    }
  }

  // bad variations which may fall into an endless loop or cause an OutOfMemoryError
  private static List<String> BLACKLIST;

  static {
    BLACKLIST = new ArrayList<String>();
    BLACKLIST.add(new CurliecueFunc().getName());
    BLACKLIST.add(new CustomWFFunc().getName());
    BLACKLIST.add(new CustomWFFunc().getName());
    BLACKLIST.add(new DLAWFFunc().getName());
    BLACKLIST.add(new GPatternFunc().getName());
    BLACKLIST.add(new IFlamesFunc().getName());
    BLACKLIST.add(new Knots3DFunc().getName());
    BLACKLIST.add(new MandalaFunc().getName());
    BLACKLIST.add(new MandelbrotFunc().getName());
    BLACKLIST.add(new NBlurFunc().getName());
    BLACKLIST.add(new NSudokuFunc().getName());
    BLACKLIST.add(new OBJMeshWFFunc().getName());
    BLACKLIST.add(new PostCustomWFFunc().getName());
    BLACKLIST.add(new PreCustomWFFunc().getName());
    BLACKLIST.add(new SattractorFunc().getName());
    BLACKLIST.add(new SunFlowersFunc().getName());
    BLACKLIST.add(new SunflowerVoroniFunc().getName());
    BLACKLIST.add(new Strange3DFunc().getName());
    BLACKLIST.add(new SZubietaFunc().getName());
    BLACKLIST.add(new TapratsFunc().getName());
    BLACKLIST.add(new TrianTruchetFunc().getName());
  }


  public void evaluate() {
    AbstractRandomGenerator randGen = new MarsagliaRandomGenerator();

    FlameTransformationContext ctx;
    {
      Flame flame = createFlame();
      FlameRenderer renderer = new FlameRenderer(flame, Prefs.getPrefs(), false, false);
      ctx = new FlameTransformationContext(renderer, randGen, 0, 0);
    }

    Mutation rndParamMutation = new RandomParamMutation();
    XYZPoint affine = new XYZPoint();
    XYZPoint var = new XYZPoint();

    System.err.println("ROUND 1/2...");
    List<Item> items = new ArrayList<>();
    for(String variationName: VariationFuncList.getNameList()) {
      Flame flame = createFlame();
      XForm xForm = flame.getFirstLayer().getXForms().get(0);
      xForm.clearVariations();
      VariationFunc fnc = VariationFuncList.getVariationFuncInstance(variationName);
      xForm.addVariation(1.0, fnc);
      Item item = new Item(flame, fnc, variationName);
      items.add(item);
      System.err.println("Probing initialization of " + item.getVariationName() + "...");
      if(BLACKLIST.contains(item.getVariationName())) {
        item.setInitCost(-1);
        item.setEvalCost(-1);
        item.setInitError(true);
        item.setEvalError(true);
      }
      else {
        System.gc();
        long m0 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long t0 = System.nanoTime();
        for (int i = 0; i < INIT_ROUNDS; i++) {
          try {
            rndParamMutation.execute(item.getFlame().getFirstLayer());
            fnc.init(ctx, flame.getFirstLayer(), flame.getFirstLayer().getXForms().get(0), 1.0);
          } catch (Exception ex) {
            item.setInitError(true);
          }
          item.setInitRounds(i);
          if (System.nanoTime() - t0 >= MAX_INIT_TIME) {
            System.err.println("  TOO LONG");
            break;
          }
        }
        long t1 = System.nanoTime();
        long m1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.gc();
        item.setInitCost(t1 - t0);
        System.err.println("  "+item.getInitCost());
        item.setInitMemory(m1 - m0);
      }
    }

    System.err.println("ROUND 2/2...");
    for(Item item: items) {
      if(!item.isInitError()) {
        System.err.println("probing evaluation of " + item.getVariationName() + "...");
        System.gc();
        long m0 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long t0 = System.nanoTime();
        for (int i = 0; i < EVAL_ROUNDS; i++) {
          rndParamMutation.execute(item.getFlame().getFirstLayer());
          var.clear();
          affine.x = 0.5 - randGen.random();
          affine.y = 0.5 - randGen.random();
          affine.z = 0.5 - randGen.random();
          try {
            item.getFnc().transform(ctx, item.getFlame().getFirstLayer().getXForms().get(0), affine, var, 1.0);
          }
          catch(Exception ex) {
            item.setEvalError(true);
          }
          item.setEvalRounds(i);
          if(System.nanoTime() - t0 > MAX_EVAL_TIME) {
            System.err.println("  TOO LONG");
            break;
          }
        }
        long t1 = System.nanoTime();
        long m1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.gc();
        item.setEvalCost(t1 - t0);
        System.err.println("  " + item.getEvalCost());
        item.setEvalMemory(m1 - m0);
      }
      else {
        item.setEvalError(true);
        item.setEvalCost(-1);
      }
    }

    for(Item item: items) {
      long initPenalty = (item.getInitRounds() < INIT_ROUNDS - INIT_ROUNDS / 10) ? 2 : 1;
      long evalPenalty = (item.getEvalRounds() < EVAL_ROUNDS - EVAL_ROUNDS / 10) ? 2 : 1;
      item.setInitCost( initPenalty * item.getInitCost() );
      item.setEvalCost( evalPenalty * item.getEvalCost() );
    }
    Collections.sort(items, new Comparator<Item>() {
      @Override
      public int compare(Item o1, Item o2) {
        // sort descending
        return o2.compareTo(o1);
      }
    });
    System.err.println("--------------------------");
    StringBuffer sb = new StringBuffer();
    sb.append("#variation; average eval-cost in ms; average init-cost in ms; average eval-memory in KB; average init-memory in KB; had eval-error; had init-error\n");
    for(Item item: items) {
      sb.append(item.getVariationName()).append("; ")
              .append((item.getEvalCost() > 0 ? Tools.doubleToString((double)item.getEvalCost()/(double)EVAL_ROUNDS/1000000.0) : -1)).append("; ")
              .append((item.getInitCost() > 0 ? Tools.doubleToString((double)item.getInitCost()/(double)INIT_ROUNDS/1000000.0) : -1)).append("; ")
              .append((item.getEvalCost() > 0 ? Tools.doubleToString((double)item.getEvalMemory()/(double)EVAL_ROUNDS/1024.0) : -1)).append("; ")
              .append((item.getEvalCost() > 0 ? Tools.doubleToString((double)item.getInitMemory()/(double)INIT_ROUNDS/1024.0) : -1)).append("; ")
              .append(item.isEvalError()).append("; ")
              .append(item.isInitError()).append("\n");
    }
    System.err.println(sb);
    try {
      Tools.writeUTF8Textfile("src/org/jwildfire/create/tina/variation/variation_costs.txt", sb.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Flame createFlame() {
    RandomFlameGenerator randGen = new SimpleRandomFlameGenerator();
    RandomFlameGeneratorState state=randGen.initState(Prefs.getPrefs(),new StrongHueRandomGradientGenerator());
    randGen.prepareFlame(state);
    Flame flame = randGen.createFlame(Prefs.getPrefs(),state);
    while(flame.getFirstLayer().getXForms().size()>1) {
      flame.getFirstLayer().getXForms().remove(0);
    }
    flame.getFirstLayer().getFinalXForms().clear();
    return flame;
  }
}
