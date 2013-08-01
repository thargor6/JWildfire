package org.jwildfire.create.tina.browser;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

public class FlamesTreeNode extends DefaultMutableTreeNode {
  private static final long serialVersionUID = 1L;
  private final List<FlameFlatNode> flames;

  public FlamesTreeNode(String pCaption, boolean pHasChilds, List<FlameFlatNode> pFlames) {
    super(pCaption, pHasChilds);
    flames = pFlames;
  }

  public List<FlameFlatNode> getFlames() {
    return flames;
  }

}
