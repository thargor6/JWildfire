package org.jwildfire.applet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.create.tina.randomflame.AllRandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSampler;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGeneratorList;
import org.jwildfire.create.tina.render.AbstractRenderThread;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.IterationObserver;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.swing.RandomBatchQuality;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ImagePanel;

public class JWildfireApplet extends JApplet implements IterationObserver {
  private static final long serialVersionUID = 1L;

  private enum State {
    IDLE, RENDER
  }

  private class ImgSize {
    private final int width;
    private final int height;

    public ImgSize(int width, int height) {
      this.width = width;
      this.height = height;
    }

    public int getWidth() {
      return width;
    }

    public int getHeight() {
      return height;
    }

  }

  private State state = State.IDLE;
  private List<AbstractRenderThread> threads;
  private FlameRenderer renderer;
  private final Prefs prefs = new Prefs();
  private Flame currFlame;
  private JScrollPane imageScrollPane;
  private SimpleImage image;
  private long sampleCount = 0;
  private long renderStartTime = 0;
  private long pausedRenderTime = 0;
  private JPanel imgMainPnl;

  public JWildfireApplet() {

    JPanel panel = new JPanel();
    getContentPane().add(panel, BorderLayout.NORTH);

    JPanel panel_1 = new JPanel();
    panel_1.setPreferredSize(new Dimension(10, 42));
    getContentPane().add(panel_1, BorderLayout.SOUTH);

    JButton btnNextnormal = new JButton();
    btnNextnormal.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        nextButton_clicked(RandomBatchQuality.NORMAL);
      }
    });
    btnNextnormal.setToolTipText("Cancel render, generate new random fractal and start render");
    btnNextnormal.setText("Create next");
    btnNextnormal.setPreferredSize(new Dimension(125, 32));
    btnNextnormal.setMnemonic(KeyEvent.VK_D);
    btnNextnormal.setMaximumSize(new Dimension(32000, 32000));
    btnNextnormal.setFont(new Font("Dialog", Font.BOLD, 10));
    btnNextnormal.setAlignmentX(0.5f);
    panel_1.add(btnNextnormal);

    JButton button_1 = new JButton();
    button_1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          copyToClipboardBtn_clicked();
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });
    button_1.setToolTipText("Copy the current fractal into the clipboard");
    button_1.setText("To Clipboard");
    button_1.setPreferredSize(new Dimension(125, 32));
    button_1.setMnemonic(KeyEvent.VK_D);
    button_1.setMinimumSize(new Dimension(100, 24));
    button_1.setMaximumSize(new Dimension(32000, 24));
    button_1.setFont(new Font("Dialog", Font.BOLD, 10));
    panel_1.add(button_1);

    imgMainPnl = new JPanel();
    getContentPane().add(imgMainPnl, BorderLayout.CENTER);
  }

  protected void copyToClipboardBtn_clicked() throws Exception {
    Flame currFlame = getCurrFlame();
    if (currFlame != null) {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      String xml = new FlameWriter().getFlameXML(currFlame);
      StringSelection data = new StringSelection(xml);
      clipboard.setContents(data, data);
    }
  }

  protected void nextButton_clicked(RandomBatchQuality pQuality) {
    cancelRender();
    genRandomFlame(pQuality);
    renderButton_clicked();
    enableControls();
  }

  private void cancelRender() {
    if (state == State.RENDER) {
      while (true) {
        boolean done = true;
        for (AbstractRenderThread thread : threads) {
          if (!thread.isFinished()) {
            done = false;
            thread.cancel();
            try {
              Thread.sleep(1);
            }
            catch (InterruptedException e) {
              e.printStackTrace();
            }
            break;
          }
        }
        if (done) {
          break;
        }
      }
      state = State.IDLE;
    }
  }

  public void genRandomFlame(RandomBatchQuality pQuality) {
    final int IMG_WIDTH = 80;
    final int IMG_HEIGHT = 60;

    //RandomFlameGenerator randGen = RandomFlameGeneratorList.getRandomFlameGeneratorInstance((String) randomStyleCmb.getSelectedItem(), true);
    RandomFlameGenerator randGen = new AllRandomFlameGenerator();
    int palettePoints = 3 + (int) (Math.random() * 68.0);
    boolean fadePaletteColors = Math.random() > 0.33;
    RandomFlameGeneratorSampler sampler = new RandomFlameGeneratorSampler(IMG_WIDTH, IMG_HEIGHT, prefs, randGen, RandomSymmetryGeneratorList.SPARSE, palettePoints, fadePaletteColors, pQuality);
    currFlame = sampler.createSample().getFlame();
  }

  public void renderButton_clicked() {
    clearScreen();
    ImgSize size = getImgSize();
    RenderInfo info = new RenderInfo(size.getWidth(), size.getHeight(), RenderMode.PREVIEW);
    Flame flame = getCurrFlame();
    double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
    double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
    flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
    flame.setWidth(info.getImageWidth());
    flame.setHeight(info.getImageHeight());
    flame.setSampleDensity(10);
    info.setRenderHDR(prefs.isTinaSaveHDRInIR());
    info.setRenderHDRIntensityMap(false);
    if (flame.getBGColorRed() > 0 || flame.getBGColorGreen() > 0 || flame.getBGColorBlue() > 0) {
      image.fillBackground(flame.getBGColorRed(), flame.getBGColorGreen(), flame.getBGColorBlue());
    }
    renderer = new FlameRenderer(flame, prefs, flame.isBGTransparency(), false);
    renderer.registerIterationObserver(this);
    sampleCount = 0;
    renderStartTime = System.currentTimeMillis();
    pausedRenderTime = 0;
    threads = renderer.startRenderFlame(info);
    state = State.RENDER;
    enableControls();
  }

  private ImgSize getImgSize() {
    return new ImgSize(getImgMainPnl().getSize().width, getImgMainPnl().getSize().height);
  }

  private void clearScreen() {
    try {
      int scrollX = (image.getImageWidth() - (int) getImgMainPnl().getBounds().getWidth()) / 2;
      if (scrollX > 0)
        imageScrollPane.getHorizontalScrollBar().setValue(scrollX);
      int scrollY = (image.getImageHeight() - (int) getImgMainPnl().getBounds().getHeight()) / 2;
      if (scrollY > 0)
        imageScrollPane.getVerticalScrollBar().setValue(scrollY);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    image.fillBackground(prefs.getTinaRandomBatchBGColorRed(), prefs.getTinaRandomBatchBGColorGreen(), prefs.getTinaRandomBatchBGColorBlue());
    getImgMainPnl().repaint();
  }

  private void refreshImagePanel() {
    if (imageScrollPane != null) {
      getImgMainPnl().remove(imageScrollPane);
      imageScrollPane = null;
    }
    ImgSize size = getImgSize();
    image = new SimpleImage(size.getWidth(), size.getHeight());
    image.fillBackground(prefs.getTinaRandomBatchBGColorRed(), prefs.getTinaRandomBatchBGColorGreen(), prefs.getTinaRandomBatchBGColorBlue());
    ImagePanel imagePanel = new ImagePanel(image, 0, 0, image.getImageWidth());
    imagePanel.setSize(image.getImageWidth(), image.getImageHeight());
    imagePanel.setPreferredSize(new Dimension(image.getImageWidth(), image.getImageHeight()));

    imageScrollPane = new JScrollPane(imagePanel);
    imageScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    imageScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    getImgMainPnl().add(imageScrollPane, BorderLayout.CENTER);

    getImgMainPnl().getParent().validate();
  }

  public Flame getCurrFlame() {
    return currFlame;
  }

  public void enableControls() {
    //    saveImageButton.setEnabled(image != null);
    //    stopButton.setEnabled(state == State.RENDER);
    //    pauseButton.setEnabled(state == State.RENDER);
    //    resumeButton.setEnabled(state != State.RENDER);
  }

  @Override
  public void notifyIterationFinished(AbstractRenderThread pEventSource, int pX, int pY) {
    incSampleCount();
    if (pX >= 0 && pX < image.getImageWidth() && pY >= 0 && pY < image.getImageHeight()) {
      image.setARGB(pX, pY, pEventSource.getTonemapper().tonemapSample(pX, pY));
      if (sampleCount % 2000 == 0) {
        updateImage();
      }
      if (sampleCount % 10000 == 0) {
        double quality = pEventSource.getTonemapper().calcDensity(sampleCount);
        updateStats(pEventSource, quality);
        pEventSource.getTonemapper().setDensity(quality);
      }
    }
  }

  private synchronized void incSampleCount() {
    sampleCount++;
  }

  private synchronized void updateImage() {
    getImgMainPnl().repaint();
  }

  private synchronized void updateStats(AbstractRenderThread pEventSource, double pQuality) {
    //    statsTextArea.setText("Current quality: " + Tools.doubleToString(pQuality) + "\n" +
    //        "samples so far: " + sampleCount + "\n" +
    //        "render time: " + Tools.doubleToString((System.currentTimeMillis() - renderStartTime + pausedRenderTime) / 1000.0) + "s");
    //    statsTextArea.validate();
  }

  public JPanel getImgMainPnl() {
    return imgMainPnl;
  }

  @Override
  public void start() {
    refreshImagePanel();
    super.start();
    nextButton_clicked(RandomBatchQuality.NORMAL);
  }

  @Override
  public void stop() {
    cancelRender();
    super.stop();
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {

      public void run() {
        JFrame frame = new JFrame(Tools.APP_TITLE + "Applet" + " " + Tools.APP_VERSION);
        JWildfireApplet applet = new JWildfireApplet();
        frame.getContentPane().add(applet);
        frame.setSize(640, 480);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        applet.start();
      }

    });

  }
}
