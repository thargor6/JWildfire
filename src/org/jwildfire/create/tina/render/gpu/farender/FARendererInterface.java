/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2025 Andreas Maschke

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
package org.jwildfire.create.tina.render.gpu.farender;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.cli.RenderOptions;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.batch.Job;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.render.denoiser.AIPostDenoiserFactory;
import org.jwildfire.create.tina.render.denoiser.AIPostDenoiserType;
import org.jwildfire.create.tina.render.gpu.GPURenderer;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.swing.FileDialogTools;
import org.jwildfire.create.tina.swing.FlameMessageHelper;
import org.jwildfire.create.tina.swing.FlamesGPURenderController;
import org.jwildfire.create.tina.swing.GpuProgressUpdater;
import org.jwildfire.create.tina.swing.TinaControllerContextService;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanelConfig;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageReader;
import org.jwildfire.swing.ErrorHandler;
import org.slf4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class FARendererInterface implements GPURenderer {
  private final String FLAME_XML =
      "<flame smooth_gradient=\"0\" version=\"JWildfire V6.50 (11.04.2021)\" size=\"1920 1080\" center=\"0.0 0.0\" scale=\"105.0\" rotate=\"0.0\" filter=\"0.75\" filter_type=\"GLOBAL_SHARPENING\" filter_kernel=\"MITCHELL_SMOOTH\" filter_indicator=\"0\" filter_sharpness=\"4.0\" filter_low_density=\"0.025\" oversample=\"1\" ai_post_denoiser=\"OPTIX\" post_optix_denoiser_blend=\"0.11111\" quality=\"100.0\" background_type=\"GRADIENT_2X2_C\" background_ul=\"0.0 0.0 0.0\" background_ur=\"0.0 0.0 0.0\" background_ll=\"0.0 0.0 0.0\" background_lr=\"0.0 0.0 0.0\" background_cc=\"0.0 0.0 0.0\" bg_transparency=\"0\" fg_opacity=\"1.0\" brightness=\"4.0\" saturation=\"1.0\" gamma=\"4.0\" gamma_threshold=\"0.01\" vibrancy=\"1.0\" contrast=\"1.0\" white_level=\"220.0\" temporal_samples=\"1.0\" cam_zoom=\"1.0\" cam_pitch=\"0.0\" cam_yaw=\"0.0\" cam_roll=\"0.0\" cam_persp=\"0.0\" cam_xfocus=\"0.0\" cam_yfocus=\"0.0\" cam_zfocus=\"0.0\" cam_pos_x=\"0.0\" cam_pos_y=\"0.0\" cam_pos_z=\"0.0\" cam_zpos=\"0.0\" cam_dof=\"0.0\" cam_dof_area=\"0.5\" cam_dof_exponent=\"2.0\" low_density_brightness=\"0.24\" balancing_red=\"1.0\" balancing_green=\"1.0\" balancing_blue=\"1.0\" cam_dof_shape=\"BUBBLE\" cam_dof_scale=\"1.0\" cam_dof_rotate=\"0.0\" cam_dof_fade=\"1.0\" antialias_amount=\"0.25\" antialias_radius=\"0.5\" post_symmetry_type=\"NONE\" post_symmetry_order=\"3\" post_symmetry_centre_x=\"0.0\" post_symmetry_centre_y=\"0.0\" post_symmetry_distance=\"1.25\" post_symmetry_rotation=\"6.0\" frame=\"1\" frame_count=\"100\" fps=\"25\" post_blur_radius=\"0\" post_blur_fade=\"0.95\" post_blur_falloff=\"2.0\" zbuffer_scale=\"1.0\" zbuffer_bias=\"0.0\" zbuffer_filename=\"PRE_ZBUF\" mixer_mode=\"OFF\" grad_edit_hue_curve_enabled=\"false\" grad_edit_hue_curve_view_xmin=\"-5\" grad_edit_hue_curve_view_xmax=\"260\" grad_edit_hue_curve_view_ymin=\"-5.0\" grad_edit_hue_curve_view_ymax=\"260.0\" grad_edit_hue_curve_interpolation=\"SPLINE\" grad_edit_hue_curve_selected_idx=\"0\" grad_edit_hue_curve_locked=\"false\" grad_edit_hue_curve_point_count=\"11\" grad_edit_hue_curve_x0=\"0\" grad_edit_hue_curve_y0=\"29.694323144104807\" grad_edit_hue_curve_x1=\"15\" grad_edit_hue_curve_y1=\"56.14963503649635\" grad_edit_hue_curve_x2=\"32\" grad_edit_hue_curve_y2=\"178.39285714285717\" grad_edit_hue_curve_x3=\"53\" grad_edit_hue_curve_y3=\"128.39005235602096\" grad_edit_hue_curve_x4=\"71\" grad_edit_hue_curve_y4=\"175.3125\" grad_edit_hue_curve_x5=\"90\" grad_edit_hue_curve_y5=\"185.45454545454544\" grad_edit_hue_curve_x6=\"101\" grad_edit_hue_curve_y6=\"12.30263157894737\" grad_edit_hue_curve_x7=\"129\" grad_edit_hue_curve_y7=\"54.95689655172414\" grad_edit_hue_curve_x8=\"137\" grad_edit_hue_curve_y8=\"51.49038461538463\" grad_edit_hue_curve_x9=\"149\" grad_edit_hue_curve_y9=\"11.687500000000002\" grad_edit_hue_curve_x10=\"255\" grad_edit_hue_curve_y10=\"24.984848484848488\" grad_edit_saturation_curve_enabled=\"false\" grad_edit_saturation_curve_view_xmin=\"-5\" grad_edit_saturation_curve_view_xmax=\"260\" grad_edit_saturation_curve_view_ymin=\"-5.0\" grad_edit_saturation_curve_view_ymax=\"260.0\" grad_edit_saturation_curve_interpolation=\"SPLINE\" grad_edit_saturation_curve_selected_idx=\"0\" grad_edit_saturation_curve_locked=\"false\" grad_edit_saturation_curve_point_count=\"11\" grad_edit_saturation_curve_x0=\"0\" grad_edit_saturation_curve_y0=\"244.3305439330544\" grad_edit_saturation_curve_x1=\"24\" grad_edit_saturation_curve_y1=\"255.0\" grad_edit_saturation_curve_x2=\"36\" grad_edit_saturation_curve_y2=\"248.72950819672133\" grad_edit_saturation_curve_x3=\"60\" grad_edit_saturation_curve_y3=\"247.2335025380711\" grad_edit_saturation_curve_x4=\"82\" grad_edit_saturation_curve_y4=\"231.8181818181819\" grad_edit_saturation_curve_x5=\"103\" grad_edit_saturation_curve_y5=\"230.99999999999997\" grad_edit_saturation_curve_x6=\"117\" grad_edit_saturation_curve_y6=\"230.71428571428572\" grad_edit_saturation_curve_x7=\"141\" grad_edit_saturation_curve_y7=\"250.67796610169492\" grad_edit_saturation_curve_x8=\"165\" grad_edit_saturation_curve_y8=\"245.5555555555556\" grad_edit_saturation_curve_x9=\"183\" grad_edit_saturation_curve_y9=\"242.85714285714283\" grad_edit_saturation_curve_x10=\"255\" grad_edit_saturation_curve_y10=\"255.0\" grad_edit_luminosity_curve_enabled=\"false\" grad_edit_luminosity_curve_view_xmin=\"-5\" grad_edit_luminosity_curve_view_xmax=\"260\" grad_edit_luminosity_curve_view_ymin=\"-5.0\" grad_edit_luminosity_curve_view_ymax=\"260.0\" grad_edit_luminosity_curve_interpolation=\"SPLINE\" grad_edit_luminosity_curve_selected_idx=\"0\" grad_edit_luminosity_curve_locked=\"false\" grad_edit_luminosity_curve_point_count=\"11\" grad_edit_luminosity_curve_x0=\"0\" grad_edit_luminosity_curve_y0=\"135.5\" grad_edit_luminosity_curve_x1=\"7\" grad_edit_luminosity_curve_y1=\"68.5\" grad_edit_luminosity_curve_x2=\"35\" grad_edit_luminosity_curve_y2=\"133.0\" grad_edit_luminosity_curve_x3=\"61\" grad_edit_luminosity_curve_y3=\"156.5\" grad_edit_luminosity_curve_x4=\"80\" grad_edit_luminosity_curve_y4=\"233.00000000000003\" grad_edit_luminosity_curve_x5=\"87\" grad_edit_luminosity_curve_y5=\"127.5\" grad_edit_luminosity_curve_x6=\"105\" grad_edit_luminosity_curve_y6=\"21.0\" grad_edit_luminosity_curve_x7=\"125\" grad_edit_luminosity_curve_y7=\"195.99999999999997\" grad_edit_luminosity_curve_x8=\"151\" grad_edit_luminosity_curve_y8=\"227.99999999999997\" grad_edit_luminosity_curve_x9=\"161\" grad_edit_luminosity_curve_y9=\"105.00000000000001\" grad_edit_luminosity_curve_x10=\"255\" grad_edit_luminosity_curve_y10=\"82.5\">\n"
          + "  <xform weight=\"0.5\" color_type=\"DIFFUSION\" color=\"0.0\" symmetry=\"0.0\" material=\"0.0\" material_speed=\"0.0\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" mod_hue=\"0.0\" mod_hue_speed=\"0.0\" linear3D=\"1.0\" linear3D_fx_priority=\"0\" coefs=\"0.5092943768363981 0.0 0.0 0.5092943768363981 -2.367738432768294 1.6438438800748028\" chaos=\"1.0 1.0 1.0\"/>\n"
          + "  <xform weight=\"0.5\" color_type=\"DIFFUSION\" color=\"0.0\" symmetry=\"0.0\" material=\"0.0\" material_speed=\"0.0\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" mod_hue=\"0.0\" mod_hue_speed=\"0.0\" linear3D=\"1.0\" linear3D_fx_priority=\"0\" coefs=\"0.5092943768363981 0.0 0.0 0.5092943768363981 2.59395548048501 1.6890872896181455\" chaos=\"1.0 1.0 1.0\"/>\n"
          + "  <xform weight=\"0.5\" color_type=\"DIFFUSION\" color=\"0.0\" symmetry=\"0.0\" material=\"0.0\" material_speed=\"0.0\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" mod_hue=\"0.0\" mod_hue_speed=\"0.0\" linear3D=\"1.0\" linear3D_fx_priority=\"0\" coefs=\"0.5092943768363981 0.0 0.0 0.5092943768363981 0.030162273028895598 -1.7343306991614909\" chaos=\"1.0 1.0 1.0\"/>\n"
          + "  <palette count=\"256\" format=\"RGB\">\n"
          + "FAB515DDA205B68904A17B039373038C70028870028772028877028A7E028D8602909101\n"
          + "8B95018599017C9E0170A3015EA90146AE0129B40106B90100BF2300C54F00CA7E00D0B0\n"
          + "00C8D600A0DB0078E10052E6002FEB010FF00E01F42502F83402FC3F06FD430AFC420EFC\n"
          + "3D11FC3414FC2816FC1A18FC1A2AFC1C3EFC1D53FC1F68FC207DFC2191FC22A5FC22B7FC\n"
          + "23C8FC24D8FC25E5FC26EFFC27F6FC28F8FC2AF7FC2BF3FC2DEDFC30E4FC32D9FC35CDFC\n"
          + "39BFFC3DB1FC42A4FC4897FC4F8CFC5783FB607CFB6979FB7378FB817DFB9288FCA093FC\n"
          + "AD9DFCB9A8FCC5B2FCCFBBFCD8C4FDE0CCFDE5D2FDE9D5FDEBD6FDEAD1FDE6C6FCDBA7FB\n"
          + "C46AF8B23BF5A41FF4960CF3810BE2690BD14D0ABF2B09AE08129D07498D067D73066F1D\n"
          + "2961054A5404483A043E24033616032F0F022A0B022609022608022809022D0A02360D03\n"
          + "411103501804612005732B068738079D4808B35A09C9700ADF880BF3A10FF4B624F6C837\n"
          + "F7D84AF8E55BF9F06BFAFA78F4FA84EEFB8DEAFB95E7FC9CE5FCA3E4FDA9E4FDAFE4FDB4\n"
          + "E4FEB9E5FEBDE7FEC1EBFEC4F0FEC8F2FECBFEE7CDFFE5D0FFE4D2FFE4D3FFE4D4FFE3D5\n"
          + "FFE3D6FFE3D6FFE2D6FFE1D5FFDFD3FFDED1FFDBCEFFD8CAFED7C9FC4A0BF74203ED3F03\n"
          + "E53D03DF3B03DA3A03D53903D23803CF3803CC3704C93704C73704C53604C43604C23604\n"
          + "C03604BF3604BD3604BC3604BB3605BA3705B93705B83705B73705B63705B53805B43805\n"
          + "B43804B33804B23904B23904B13904B13A04B03A04B03A04AF3B04AF3B04AE3C04AE3C04\n"
          + "AE3C04AD3D04AD3D04AD3E04AC3E04AC3F03AC3F03AB4003AB4003AB4103AB4103AA4203\n"
          + "AA4203AA4303AA4303AA4403A94403A94503A94603A94603A94703A94703A94802A94902\n"
          + "A84902A84A02A84A02A84B02A84C02A84C02A84D02A84E02A84E02A84F02A85002A85002\n"
          + "A85102A85202A75201A75301A75401A75401A75501A75601A75601A75701A75801A75801\n"
          + "A75901A75A01A75A01A75B01A75B01A75C01A75D01A75D00A75E00A65E00A65F00A65F00\n"
          + "A66000A66000A56100A56100  </palette>\n"
          + "</flame>\n";

  @Override
  public boolean performSelfTests() {
    if (!Tools.OSType.WINDOWS.equals(Tools.getOSType())) {
      return false;
    }
    try {
      Flame flame = new FlameReader(Prefs.getPrefs()).readFlamesfromXML(FLAME_XML).get(0);
      File tmpFile = File.createTempFile("jwf", ".flame");
      try {
        FileDialogTools.ensureFileAccess(new Frame(), new JPanel(), tmpFile.getAbsolutePath());
        new FAFlameWriter().writeFlame(Collections.singletonList(flame), tmpFile.getAbsolutePath());
        FARenderResult openClRenderRes = FARenderTools.invokeFARender(tmpFile.getAbsolutePath(), 64, 64, 10, false, flame);
        if(openClRenderRes.getReturnCode()==0 && openClRenderRes.getOutputFilename()!=null) {
          try {
            new File(openClRenderRes.getOutputFilename()).delete();
          } catch (Exception ex) {
            new File(openClRenderRes.getOutputFilename()).deleteOnExit();
          }
          return true;
        }
        else {
          return false;
        }
      }
      finally {
        try {
          tmpFile.delete();
        } catch (Exception ex) {
          tmpFile.deleteOnExit();
        }
      }
    }
    catch(Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }

  @Override
  public SimpleImage renderPreview(Flame flame, int width, int height, Prefs prefs, ProgressUpdater mainProgressUpdater, GpuProgressUpdater gpuProgressUpdater, FlameMessageHelper messageHelper, FlamePanelConfig flamePanelConfig, ErrorHandler errorHandler, Logger logger) {
    try {
      final int PROGRESS_STEPS = 25;
      File tmpFile = File.createTempFile("jwf", ".flame");
      try {
        FileDialogTools.ensureFileAccess(
                new Frame(), new JPanel(), tmpFile.getAbsolutePath());
        mainProgressUpdater.initProgress(PROGRESS_STEPS);
        if (gpuProgressUpdater != null) {
          gpuProgressUpdater.signalCancel();
        }
        if (gpuProgressUpdater != null && gpuProgressUpdater.isFinished()) {
          gpuProgressUpdater = null;
        }
        if (gpuProgressUpdater == null) {
          gpuProgressUpdater = new GpuProgressUpdater(mainProgressUpdater, PROGRESS_STEPS);
          new Thread(gpuProgressUpdater).start();
        }
        FARenderResult openClRenderRes;
        long t0 = System.currentTimeMillis();
        try {
          List<Flame> preparedFlames =
                  FARenderTools.prepareFlame(
                          flame, TinaControllerContextService.getContext().isZPass());
          new FAFlameWriter().writeFlame(preparedFlames, tmpFile.getAbsolutePath());
          openClRenderRes =
                  FARenderTools.invokeFARender(
                          tmpFile.getAbsolutePath(),
                          width,
                          height,
                          prefs.getTinaRenderPreviewQuality(),
                          preparedFlames.size() > 1, flame);
        } finally {
          try {
            if (gpuProgressUpdater != null) {
              gpuProgressUpdater.signalCancel();
            }
          } catch (Exception ex) {
            // EMPTY
          }
        }
        if (openClRenderRes.getReturnCode() != 0) {
          throw new Exception(openClRenderRes.getMessage());
        } else {
          SimpleImage img = new ImageReader().loadImage(openClRenderRes.getOutputFilename());
          mainProgressUpdater.updateProgress(PROGRESS_STEPS);
          if (!flamePanelConfig.isNoControls() && messageHelper != null) {
            long t1 = System.currentTimeMillis();
            messageHelper.showStatusMessage(
                    flame, "render time (GPU): " + Tools.doubleToString((t1 - t0) * 0.001) + "s");
            logger.info(openClRenderRes.getMessage());
          }
          return img;
        }
      } finally {
        if (!tmpFile.delete()) {
          tmpFile.deleteOnExit();
        }
        if (gpuProgressUpdater != null) {
          gpuProgressUpdater.signalCancel();
        }
      }
    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
      errorHandler.handleError(ex);
    }
    return null;
  }

  @Override
  public void renderFlameFromCli(Flame renderFlame, String flameFilename, RenderOptions renderOptions)  throws Exception {
    boolean zForPass = false;
    String tmpFlam3Filename = Tools.trimFileExt(flameFilename) + ".flam3";
    String gpuRenderFlameFilename = zForPass ? Tools.makeZBufferFilename(tmpFlam3Filename, renderFlame.getZBufferFilename()) : tmpFlam3Filename;
    try {
      List<Flame> preparedFlames = FARenderTools.prepareFlame(renderFlame, zForPass);
      new FAFlameWriter().writeFlame(preparedFlames, gpuRenderFlameFilename);
      FARenderResult gpuRenderRes = FARenderTools.invokeFARender(gpuRenderFlameFilename,
              renderOptions.getRenderWidth(), renderOptions.getRenderHeight(), Tools.FTOI(renderOptions.getRenderQuality()), preparedFlames.size() > 1, renderFlame);
      if (gpuRenderRes.getReturnCode() != 0) {
        throw new Exception(gpuRenderRes.getMessage());
      } else {
        if (!AIPostDenoiserType.NONE.equals(renderFlame.getAiPostDenoiser()) && !renderFlame.isPostDenoiserOnlyForCpuRender()) {
          AIPostDenoiserFactory.denoiseImage(new File(gpuRenderRes.getOutputFilename()).getAbsolutePath(), renderFlame.getAiPostDenoiser(), renderFlame.getPostOptiXDenoiserBlend());
        }
      }
    } finally {
      if (!new File(gpuRenderFlameFilename).delete()) {
        new File(gpuRenderFlameFilename).deleteOnExit();
      }
    }
  }

  @Override
  public void renderFlameForEditor(Flame newFlame, String gpuRenderFlameFilename, int width, int height, int quality, boolean zForPass) throws Exception {
    try {
      List<Flame> preparedFlames = FARenderTools.prepareFlame(newFlame, zForPass);
      new FAFlameWriter().writeFlame(preparedFlames, gpuRenderFlameFilename);
      FARenderResult gpuRenderRes = FARenderTools.invokeFARender(gpuRenderFlameFilename, width, height, quality, preparedFlames.size() > 1, newFlame);
      if (gpuRenderRes.getReturnCode() != 0) {
        throw new Exception(gpuRenderRes.getMessage());
      } else {
        if (!AIPostDenoiserType.NONE.equals(newFlame.getAiPostDenoiser()) && !newFlame.isPostDenoiserOnlyForCpuRender()) {
          AIPostDenoiserFactory.denoiseImage(gpuRenderRes.getOutputFilename(), newFlame.getAiPostDenoiser(), newFlame.getPostOptiXDenoiserBlend());
        }
      }
    } finally {
      if (!new File(gpuRenderFlameFilename).delete()) {
        new File(gpuRenderFlameFilename).deleteOnExit();
      }
    }
  }

  @Override
  public void renderFlameForGpuController(Flame currFlame, int width, int height, int quality, JTextArea statsTextArea, JTextArea gpuFlameParamsTextArea, JCheckBox aiPostDenoiserDisableCheckbox, JPanel imageRootPanel, FlamesGPURenderController controller, JLabel gpuRenderInfoLbl, SimpleImage image, boolean keepFlameFileOnError) throws Exception {
    boolean hasError=false;
    File tmpFile =
            File.createTempFile(
                    System.currentTimeMillis() + "_" + Thread.currentThread().getId(), ".flam3");
    try {
      statsTextArea.setText("");
      FAFlameWriter gpuFlameWriter = new FAFlameWriter(controller);
      List<Flame> preparedFlames = FARenderTools.prepareFlame(currFlame, TinaControllerContextService.getContext().isZPass());
      String gpuFlameParams = gpuFlameWriter.getFlameXML(preparedFlames);
      gpuFlameParamsTextArea.setText(gpuFlameParams);
      gpuFlameWriter.writeFlame(gpuFlameParams, tmpFile.getAbsolutePath());
      long t0 = System.currentTimeMillis();
      FARenderResult renderResult =
              FARenderTools.invokeFARender(tmpFile.getAbsolutePath(), width, height, quality, preparedFlames.size() > 1, currFlame);
      long t1 = System.currentTimeMillis();
      try {
        if (renderResult.getReturnCode() == 0) {
          if (renderResult.getMessage() != null) {
            statsTextArea.append(renderResult.getMessage() + "\n");
          }
          if (!aiPostDenoiserDisableCheckbox.isSelected()
                  && !AIPostDenoiserType.NONE.equals(currFlame.getAiPostDenoiser())) {
            long dt0 = System.currentTimeMillis();
            if (AIPostDenoiserFactory.denoiseImage(
                    renderResult.getOutputFilename(),
                    currFlame.getAiPostDenoiser(),
                    currFlame.getPostOptiXDenoiserBlend())) {
              long dt1 = System.currentTimeMillis();
              t1 = dt1;
              statsTextArea.append(
                      "\n\n"
                              + "AI-Post-Denoiser: "
                              + Tools.doubleToString((dt1 - dt0) / 1000.0)
                              + "s");
            }
          }
          SimpleImage img = new ImageReader().loadImage(renderResult.getOutputFilename());
          if (img.getImageWidth() == image.getImageWidth()
                  && img.getImageHeight() == image.getImageHeight()) {
            image.setBufferedImage(
                    img.getBufferedImg(), img.getImageWidth(), img.getImageHeight());
            imageRootPanel.repaint();
            gpuRenderInfoLbl.setText(
                    "Elapsed: " + Tools.doubleToString((t1 - t0) / 1000.0) + "s");
          } else {
            hasError = true;
            throw new Exception(
                    "Invalid image size <"
                            + img.getImageWidth()
                            + "x"
                            + img.getImageHeight()
                            + ">");
          }
        } else {
          hasError = true;
          statsTextArea.append("\n\n" +
                  (renderResult.getMessage() != null ? renderResult.getMessage() : "")
                  + "\n\n"
                  + (renderResult.getCommand() != null ? renderResult.getCommand() : ""));
        }
      } finally {
        try {
          if (renderResult.getOutputFilename() != null
                  && !renderResult.getOutputFilename().isEmpty()) {
            File f = new File(renderResult.getOutputFilename());
            if (f.exists()) {
              if (!f.delete()) {
                f.deleteOnExit();
              }
            }
          }
        } catch (Exception innerError) {
          innerError.printStackTrace();
        }
      }
    } finally {
      if (!(hasError && keepFlameFileOnError)) {
        if (!tmpFile.delete()) {
          tmpFile.deleteOnExit();
        }
      }
    }
  }

  @Override
  public void renderFlameForBatch(Flame newFlame, String openClFlameFilename, int width, int height, int quality, boolean zForPass, boolean disablePostDenoiser, boolean updateProgress, Job job) throws Exception {
    try {
      List<Flame> preparedFlames = FARenderTools.prepareFlame(newFlame, zForPass);
      new FAFlameWriter().writeFlame(preparedFlames, openClFlameFilename);
      long t0 = Calendar.getInstance().getTimeInMillis();
      FARenderResult openClRenderRes = FARenderTools.invokeFARender(openClFlameFilename, width, height, quality, preparedFlames.size() > 1, newFlame);
      long t1 = Calendar.getInstance().getTimeInMillis();
      if (openClRenderRes.getReturnCode() != 0) {
        throw new Exception(openClRenderRes.getMessage());
      } else {
        if (!disablePostDenoiser && !AIPostDenoiserType.NONE.equals(newFlame.getAiPostDenoiser()) && !newFlame.isPostDenoiserOnlyForCpuRender()) {
          AIPostDenoiserFactory.denoiseImage(openClRenderRes.getOutputFilename(), newFlame.getAiPostDenoiser(), newFlame.getPostOptiXDenoiserBlend());
          t1 = Calendar.getInstance().getTimeInMillis();
        }
        if (updateProgress) {
          job.setElapsedSeconds(((double) (t1 - t0) / 1000.0));
        }
      }
    } finally {
      if (!new File(openClFlameFilename).delete()) {
        new File(openClFlameFilename).deleteOnExit();
      }
    }
  }
}
