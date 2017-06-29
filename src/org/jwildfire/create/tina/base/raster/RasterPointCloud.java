/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2017 Andreas Maschke

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
package org.jwildfire.create.tina.base.raster;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jwildfire.base.Tools;
import org.jwildfire.base.Unchecker;
import org.jwildfire.create.tina.OctreeNode;
import org.jwildfire.create.tina.OctreeNodeVisitor;
import org.jwildfire.create.tina.OctreeValue;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.render.LightViewCalculator;
import org.jwildfire.create.tina.render.PlotSample;
import org.jwildfire.create.tina.render.ZBufferSample;

public class RasterPointCloud implements AbstractRaster, Serializable {
  private static final long serialVersionUID = 1L;
  protected int rasterWidth, rasterHeight;
  private int oversample;
  private double sampleDensity;
  private Flame flame;
  private final double zmin, zmax;

  private static final double DFLT_MAX_OCTREE_CELL_SIZE = 0.001;
  private double maxOctreeCellSize = DFLT_MAX_OCTREE_CELL_SIZE;
  private OctreeNode octree;
  private AtomicInteger atomicAddCount;

  public static class Point {
    double x, y, z;
    double r, g, b;
    int count;
  }

  public RasterPointCloud(double pZmin, double pZmax) {
    zmin = pZmin;
    zmax = pZmax;
  }

  @Override
  public void allocRaster(Flame pFlame, int pWidth, int pHeight, int pOversample, double pSampleDensity) {
    flame = pFlame;
    rasterWidth = pWidth;
    rasterHeight = pHeight;
    oversample = pOversample;
    sampleDensity = pSampleDensity;

    octree = new OctreeNode(100.0);
    atomicAddCount = new AtomicInteger(0);
  }

  @Override
  public void readRasterPoint(int pX, int pY, RasterPoint pDestRasterPoint) {
    pDestRasterPoint.clear();
  }

  @Override
  public void readRasterPointSafe(int pX, int pY, RasterPoint pDestRasterPoint) {
    pDestRasterPoint.clear();
  }

  // TODO speedup: nodes merken, auf visitor beim condense verzichten
  @Override
  public void addSamples(PlotSample[] pPlotBuffer, int pCount) {
    for (int i = 0; i < pCount; i++) {
      PlotSample sample = pPlotBuffer[i];

      if (sample.originalZ >= zmin && sample.originalZ <= zmax) {
        Point p = new Point();
        p.x = sample.originalX;
        p.y = sample.originalY;
        p.z = sample.originalZ;
        p.r = sample.r;
        p.g = sample.g;
        p.b = sample.b;
        p.count = 1;

        synchronized (octree) {
          octree.addValue(p.x, p.y, p.z, p, maxOctreeCellSize);
        }

        if (atomicAddCount.incrementAndGet() % 1000000 == 0) {
          condenseOctreeNodes();
        }

      }
    }
  }

  @Override
  public void incCount(int pX, int pY) {
    // EMPTY
  }

  @Override
  public void finalizeRaster() {
    condenseOctreeNodes();
    List<Point> points = collectOctreeNodes();
    octree = null;
    writePLY(points, "D:\\tmp\\wf.ply");
  }

  private List<Point> collectOctreeNodes() {
    final List<Point> res = new ArrayList<>();
    octree.accept(new OctreeNodeVisitor() {

      @Override
      public void visit(OctreeNode pNode) {
        if (pNode.getValues() != null && !pNode.getValues().isEmpty()) {
          if (pNode.getValues().size() > 1) {
            throw new RuntimeException("Call <condenseOctreeNodes> first");
          }
          Point cumPoint = (Point) pNode.getValues().iterator().next().getValue();
          if (cumPoint.count > 1) {
            Point avgPoint = new Point();
            avgPoint.x = cumPoint.x / (double) cumPoint.count;
            avgPoint.y = cumPoint.y / (double) cumPoint.count;
            avgPoint.z = cumPoint.z / (double) cumPoint.count;
            avgPoint.r = cumPoint.r / (double) cumPoint.count;
            avgPoint.g = cumPoint.g / (double) cumPoint.count;
            avgPoint.b = cumPoint.b / (double) cumPoint.count;
            avgPoint.count = 1;
            res.add(avgPoint);
          }
          else {
            res.add(cumPoint);
          }
        }
      }

    });
    return res;
  }

  private void condenseOctreeNodes() {
    System.out.println("CONDENSE " + atomicAddCount.get());
    synchronized (octree) {
      octree.accept(new OctreeNodeVisitor() {

        @Override
        public void visit(OctreeNode pNode) {
          if (pNode.getValues() != null && pNode.getValues().size() > 1) {
            Point cumPoint = new Point();
            for (OctreeValue val : pNode.getValues()) {
              Point p = (Point) val.getValue();
              cumPoint.x += p.x;
              cumPoint.y += p.y;
              cumPoint.z += p.z;
              cumPoint.r += p.r;
              cumPoint.g += p.g;
              cumPoint.b += p.b;
              cumPoint.count += p.count;
            }
            OctreeValue firstVal = pNode.getValues().iterator().next();

            OctreeValue cum = new OctreeValue(firstVal.getX(), firstVal.getY(), firstVal.getZ(), cumPoint);
            pNode.getValues().clear();
            pNode.getValues().add(cum);
          }

        }
      });
    }
  }

  private void writePLY(List<Point> pPoints, String pFilename) {
    StringBuilder sb = new StringBuilder();
    sb.append("ply\r\n" +
        "format ascii 1.0\r\n" +
        "comment VCGLIB generated\r\n" +
        "element vertex " + pPoints.size() + "\r\n" +
        "property float x\r\n" +
        "property float y\r\n" +
        "property float z\r\n" +
        "property uchar red\r\n" +
        "property uchar green\r\n" +
        "property uchar blue\r\n" +
        "property uchar alpha\r\n" +
        "element face 0\r\n" +
        "property list uchar int vertex_indices\r\n" +
        "end_header\r\n");
    for (Point p : pPoints) {
      sb.append(p.x + " " + p.y + " " + p.z + " " + Tools.roundColor(p.r) + " " + Tools.roundColor(p.g) + " " + Tools.roundColor(p.b) + " " + 255 + "\r\n");
    }
    try {
      Tools.writeUTF8Textfile(pFilename, sb.toString());
    }
    catch (Exception ex) {
      Unchecker.rethrow(ex);
    }
  }

  @Override
  public void addShadowMapSamples(int pShadowMapIdx, PlotSample[] pPlotBuffer, int pCount) {
    // EMPTY
  }

  @Override
  public void notifyInit(LightViewCalculator lightViewCalculator) {
    // EMPTY    
  }

  @Override
  public void readZBuffer(int pX, int pY, ZBufferSample pDest) {
    // EMPTY    
  }

  @Override
  public void readZBufferSafe(int pX, int pY, ZBufferSample pDest) {
    // EMPTY    
  }

  @Override
  public LightViewCalculator getLightViewCalculator() {
    // EMPTY    
    return null;
  }

  @Override
  public int getRasterWidth() {
    return rasterWidth;
  }

  @Override
  public int getRasterHeight() {
    return rasterHeight;
  }

  @Override
  public int getOversample() {
    return oversample;
  }

  @Override
  public double getSampleDensity() {
    return sampleDensity;
  }

}

/*
<flame name="Flowers3D (filled) - 380059705" smooth_gradient="0" version="JWildfire 3.11r2 (05.06.2017)" size="1920 1080" center="0.0 0.0" scale="372.16250664681866" rotate="0.0" filter="0.75" filter_type="ADAPTIVE" filter_kernel="MITCHELL_SINEPOW" filter_indicator="0" filter_sharpness="4.0" filter_low_density="0.025" oversample="2" post_noise_filter="0" post_noise_filter_threshold="0.35" quality="25.0" background_type="GRADIENT_2X2_C" background_ul="0.0 0.0 0.0" background_ur="0.0 0.0 0.0" background_ll="0.0 0.0 0.0" background_lr="0.0 0.0 0.0" background_cc="0.0 0.0 0.0" bg_transparency="0" brightness="4.0" saturation="1.0" gamma="4.0" gamma_threshold="0.01" vibrancy="1.0" contrast="1.0" white_level="220.0" temporal_samples="1.0" cam_zoom="2.0" cam_pitch="0.6457718232379019" cam_yaw="0.0" cam_persp="0.32" cam_xfocus="0.0" cam_yfocus="0.0" cam_zfocus="0.0" cam_pos_x="0.0" cam_pos_y="0.0" cam_pos_z="0.0" cam_zpos="0.0" cam_dof="0.0" cam_dof_area="0.5" cam_dof_exponent="2.0" low_density_brightness="0.24" balancing_red="1.0" balancing_green="1.0" balancing_blue="1.0" cam_dof_shape="BUBBLE" cam_dof_scale="1.0" cam_dof_rotate="0.0" cam_dof_fade="1.0" antialias_amount="0.25" antialias_radius="0.5" post_symmetry_type="NONE" post_symmetry_order="3" post_symmetry_centre_x="0.0" post_symmetry_centre_y="0.0" post_symmetry_distance="1.25" post_symmetry_rotation="6.0" frame="1" frame_count="300" fps="30" post_blur_radius="0" post_blur_fade="0.95" post_blur_falloff="2.0" zbuffer_scale="1.0" mixer_mode="OFF">
  <xform weight="0.4207700507478322" color="0.0" symmetry="0.0" material="0.0" material_speed="0.0" mod_gamma="0.0" mod_gamma_speed="0.0" mod_contrast="0.0" mod_contrast_speed="0.0" mod_saturation="0.0" mod_saturation_speed="0.0" mod_hue="0.0" mod_hue_speed="0.0" gaussian_blur="0.6912182939984166" gaussian_blur_fx_priority="0" coefs="1.0 0.0 0.0 1.0 0.0 0.0" chaos="1.0 1.0 1.0 1.0 1.0 1.0 1.0"/>
  <xform weight="11.89387923950445" color="0.33" symmetry="0.0" material="0.0" material_speed="0.0" mod_gamma="0.0" mod_gamma_speed="0.0" mod_contrast="0.0" mod_contrast_speed="0.0" mod_saturation="0.0" mod_saturation_speed="0.0" mod_hue="0.0" mod_hue_speed="0.0" linear3D="1.0" linear3D_fx_priority="0" epispiral_wf="-0.007998409122138833" epispiral_wf_fx_priority="0" epispiral_wf_waves="4.0" ztranslate="0.10236572432732077" ztranslate_fx_priority="0" zcone="8.25161392955583E-6" zcone_fx_priority="0" bubble="0.06555074281730452" bubble_fx_priority="0" fan="0.08619263105580736" fan_fx_priority="0" waves4_wf="-0.039804917669911746" waves4_wf_fx_priority="0" waves4_wf_scalex="1.4512727756788166" waves4_wf_scaley="1.317180143831143" waves4_wf_freqx="1.1886558121159485" waves4_wf_freqy="2.36812478119373" waves4_wf_use_cos_x="1" waves4_wf_use_cos_y="0" waves4_wf_dampx="0.0" waves4_wf_dampy="0.0" kaleidoscope="0.00708476273478333" kaleidoscope_fx_priority="0" kaleidoscope_pull="0.0" kaleidoscope_rotate="1.0" kaleidoscope_line_up="1.0" kaleidoscope_x="0.0" kaleidoscope_y="0.0" coefs="-1.6479116472790856 0.9026914974785921 -0.9026914974785921 -1.6479116472790856 -0.27660737897465576 0.26321299693073846" chaos="1.0 1.0 1.0 1.0 1.0 1.0 1.0"/>
  <xform weight="0.6325360291609392" color="0.0" symmetry="0.0" material="0.0" material_speed="0.0" mod_gamma="0.0" mod_gamma_speed="0.0" mod_contrast="0.0" mod_contrast_speed="0.0" mod_saturation="0.0" mod_saturation_speed="0.0" mod_hue="0.0" mod_hue_speed="0.0" gaussian_blur="0.5315339131339005" gaussian_blur_fx_priority="0" coefs="1.0 0.0 0.0 1.0 0.0 0.0" post="1.0 0.0 0.0 1.0 0.09935495202515757 -0.2544441204345949" chaos="1.0 1.0 1.0 1.0 1.0 1.0 1.0"/>
  <xform weight="0.4744020218707044" color="0.0" symmetry="0.0" material="0.0" material_speed="0.0" mod_gamma="0.0" mod_gamma_speed="0.0" mod_contrast="0.0" mod_contrast_speed="0.0" mod_saturation="0.0" mod_saturation_speed="0.0" mod_hue="0.0" mod_hue_speed="0.0" gaussian_blur="0.5716942875180364" gaussian_blur_fx_priority="0" coefs="1.0 0.0 0.0 1.0 0.0 0.0" post="1.0 0.0 0.0 1.0 -0.04280761952440498 0.2816084813492326" chaos="1.0 1.0 1.0 1.0 1.0 1.0 1.0"/>
  <xform weight="0.3558015164030283" color="0.0" symmetry="0.0" material="0.0" material_speed="0.0" mod_gamma="0.0" mod_gamma_speed="0.0" mod_contrast="0.0" mod_contrast_speed="0.0" mod_saturation="0.0" mod_saturation_speed="0.0" mod_hue="0.0" mod_hue_speed="0.0" gaussian_blur="0.385295098629002" gaussian_blur_fx_priority="0" coefs="1.0 0.0 0.0 1.0 0.0 0.0" post="1.0 0.0 0.0 1.0 0.1335690569226226 -0.06113660284792749" chaos="1.0 1.0 1.0 1.0 1.0 1.0 1.0"/>
  <xform weight="0.2668511373022712" color="0.0" symmetry="0.0" material="0.0" material_speed="0.0" mod_gamma="0.0" mod_gamma_speed="0.0" mod_contrast="0.0" mod_contrast_speed="0.0" mod_saturation="0.0" mod_saturation_speed="0.0" mod_hue="0.0" mod_hue_speed="0.0" gaussian_blur="0.44876779777578846" gaussian_blur_fx_priority="0" coefs="1.0 0.0 0.0 1.0 0.0 0.0" post="1.0 0.0 0.0 1.0 -0.2891299315618055 0.003802753705341999" chaos="1.0 1.0 1.0 1.0 1.0 1.0 1.0"/>
  <xform weight="0.6753235705177991" color="0.0" symmetry="0.8639289930901689" material="0.0" material_speed="0.0" mod_gamma="0.0" mod_gamma_speed="0.0" mod_contrast="0.0" mod_contrast_speed="0.0" mod_saturation="0.0" mod_saturation_speed="0.0" mod_hue="0.0" mod_hue_speed="0.0" bubble="0.08266770101370355" bubble_fx_priority="0" bubble#1#="0.10365704508451448" bubble#1#_fx_priority="0" coefs="1.0 0.0 0.0 1.0 0.0 0.0" post="1.0 0.0 0.0 1.0 -0.01810966333024272 0.020927875870330015" chaos="1.0 1.0 1.0 1.0 1.0 1.0 1.0"/>
  <finalxform weight="0.0" color="0.0" symmetry="0.0" material="0.0" material_speed="0.0" mod_gamma="0.0" mod_gamma_speed="0.0" mod_contrast="0.0" mod_contrast_speed="0.0" mod_saturation="0.0" mod_saturation_speed="0.0" mod_hue="0.0" mod_hue_speed="0.0" zscale="0.14572054537769322" zscale_fx_priority="0" julia3D="1.0" julia3D_fx_priority="0" julia3D_power="-4" pre_circlecrop="1.0" pre_circlecrop_fx_priority="-1" pre_circlecrop_radius="10000.0" pre_circlecrop_x="0.0" pre_circlecrop_y="0.0" pre_circlecrop_scatter_area="0.0" pre_circlecrop_zero="1" coefs="1.0 0.0 0.0 1.0 0.0 0.0" chaos="1.0 1.0 1.0 1.0 1.0 1.0 1.0"/>
  <palette count="256" format="RGB">
8B85FF917EEF9777DF9D70CFA369BFA862AFAE5B9FB4548FBA4E80C04770C64060CC3950
D23240D72B30DD2420E31D10E91600E41D10DE2320D92A30D43040CE3750C93D60C44470
BF4A80B9518FB4579FAF5EAFA964BFA46BCF9F71DF9978EF947EFF8E76F9896EF38366EE
7E5FE87857E2734FDC6D47D6683FD16237CB5C2FC55727BF5120B94C18B34610AE4108A8
3B00A24304984B098E520D845A117A62156F6A1A65711E5B792251812647892B3D902F33
983329A0371EA83C14AF400AB74400AC400CA13C179737238C332F812F3A762B466B2652
61225E561E694B1A7540158135118C2A0D982009A41504AF0A00BB1908B22910A93818A1
47209857288F66308675387D85417594496CA35163B2595AC26151D16948E07140F07937
FF812EF58931EA9133E09936D5A139CBA83BC0B03EB6B840ACC043A1C84697D0488CD84B
82E04E77E7506DEF5362F75558FF5858F56257EB6D57E17756D78256CD8C55C39755B9A1
54AFAC54A4B6539AC05390CB5286D5527CE05172EA5168F5505EFF505CEF4F59DF4F57CF
4E55BF4E52AF4D509F4D4D8F4C4B804C49704B46604B44504A42404A3F30493D20493A10
48380053430C5F4E196A5925766431816E3D8D794A988456A48F62AF9A6EBAA57BC6B087
D1BB93DDC59FE8D0ACF4DBB8FFE6C4F8DEB8F1D5ACEACD9FE3C493DCBC87D5B37BCEAB6E
C7A262C09A56B9914AB2893DAB8031A478259D6F1996670C8F5E00865C0F7D591E74572D
6B543C62524B594F5A504D69484B783F48873646962D43A52441B41B3EC3123CD20939E1
0037F00435E10834D20B32C30F31B4132FA5172D961A2C871E2A7822286926275A29254B
2D243C31222D35201E381F0F3C1D003C1B083B190F3B18173B161E3A14263A122D391035
390F3D390D44380B4C38095338075B37056237046A3602713600794305764F0B735C1071
68156E751B6B8120688E25659B2B63A73060B4355DC03A5ACD4057D94554E64A52F2504F
FF554CFF5447FF5343FF523EFF513AFF5035FF4F30FF4E2CFF4D27FF4C22FF4B1EFF4A19
FF4915FF4810FF470BFF4607  </palette>
</flame>

*/