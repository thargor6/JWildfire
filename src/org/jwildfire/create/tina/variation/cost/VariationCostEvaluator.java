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
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

import java.util.*;

public class VariationCostEvaluator {
  private final long ROUNDS = 100000;

  public static void main(String args[]) {
    new VariationCostEvaluator().evaluate();
  }

  private static class Item implements Comparable<Item> {
    private final Flame flame;
    private final VariationFunc fnc;
    private final String variationName;
    private long cost;

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

    public long getCost() {
      return cost;
    }

    public void setCost(long cost) {
      this.cost = cost;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Item item = (Item) o;
      return cost == item.cost &&
              Objects.equals(variationName, item.variationName);
    }

    @Override
    public int hashCode() {
      return Objects.hash(variationName, cost);
    }

    @Override
    public int compareTo(Item o) {
      return Long.valueOf(this.getCost()).compareTo(Long.valueOf(o.getCost()));
    }
  }


  private static List<String> BLACKLIST;

  static {
    BLACKLIST = new ArrayList<String>();
    BLACKLIST.add("bubbleT3D");
    BLACKLIST.add("dc_triTile");
    BLACKLIST.add("metaballs3d_wf");
    BLACKLIST.add("iflames_wf");
    BLACKLIST.add("lsystem3D_js");
  }


  public void evaluate() {
    AbstractRandomGenerator randGen = new MarsagliaRandomGenerator();

    FlameTransformationContext ctx;
    {
      Flame flame = createFlame();
      FlameRenderer renderer = new FlameRenderer(flame, Prefs.getPrefs(), false, false);
      ctx = new FlameTransformationContext(renderer, randGen, 0, 0, 0.0, 0.0);
    }

    Mutation rndParamMutation = new RandomParamMutation();
    XYZPoint affine = new XYZPoint();
    XYZPoint var = new XYZPoint();

    System.err.println("Creating flames...");
    List<Item> items = new ArrayList<>();
    for(String variationName: VariationFuncList.getNameList()) {
      Flame flame = createFlame();
      XForm xForm = flame.getFirstLayer().getXForms().get(0);
      xForm.clearVariations();
      VariationFunc fnc = VariationFuncList.getVariationFuncInstance(variationName);
      xForm.addVariation(1.0, fnc);
      Item item = new Item(flame, fnc, variationName);
      items.add(item);
      if(BLACKLIST.contains(variationName)) {
        item.setCost(-1);
      }
      else {
        try {
          fnc.init(ctx, flame.getFirstLayer(), flame.getFirstLayer().getXForms().get(0), 1.0);
        } catch (Exception ex) {
          item.setCost(-1);
        }
      }
    }

    for(Item item: items) {
      if(item.getCost()>=0) {
        System.err.println("Probing " + item.getVariationName() + "...");
        try {
          long t0 = System.nanoTime();
          for (int i = 0; i < ROUNDS; i++) {
            rndParamMutation.execute(item.getFlame().getFirstLayer());
            var.clear();
            affine.x = 0.5 - randGen.random();
            affine.y = 0.5 - randGen.random();
            affine.z = 0.5 - randGen.random();
            item.getFnc().transform(ctx, item.getFlame().getFirstLayer().getXForms().get(0), affine, var, 1.0);
          }
          long t1 = System.nanoTime();
          item.setCost(t1 - t0);
        }
        catch(Exception ex) {
          item.setCost(-1);
        }
      }
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
    long maxCost = items.get(0).getCost();
    for(Item item: items) {
      sb.append(item.getVariationName()).append(": ").append((item.getCost() > 0 ? Tools.doubleToString((double)item.getCost()/(double)maxCost) : -1)).append("\n");
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
