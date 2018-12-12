/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
package org.jwildfire.create.tina.variation.iflames;

import java.io.Serializable;

public class FlameParams implements Serializable {
  private static final long serialVersionUID = 1L;

  public static final int MAX_MUTATION_COUNT = 3;

  private double minVal;
  private double maxVal;
  private double weight;
  private double size;
  private double sizeVar;
  private double rotateAlpha;
  private double rotateAlphaVar;
  private double rotateAlphaSpeed;
  private double rotateAlphaSpeedVar;
  private double rotateBeta;
  private double rotateBetaVar;
  private double rotateBetaSpeed;
  private double rotateBetaSpeedVar;
  private double rotateGamma;
  private double rotateGammaVar;
  private double rotateGammaSpeed;
  private double rotateGammaSpeedVar;
  private double speedX;
  private double speedXVar;
  private double speedY;
  private double speedYVar;
  private double speedZ;
  private double speedZVar;
  private double radialAcceleration;
  private double radialAccelerationVar;
  private double tangentialAcceleration;
  private double tangentialAccelerationVar;
  private double centreX;
  private double centreY;
  private double centreZ;
  private String flameXML;
  private String flameParamMap1Filename;
  private double flameParam1Min;
  private double flameParam1Max;
  private String flameParam1;
  private String flameParamMap2Filename;
  private double flameParam2Min;
  private double flameParam2Max;
  private String flameParam2;
  private String flameParamMap3Filename;
  private double flameParam3Min;
  private double flameParam3Max;
  private String flameParam3;
  private int previewR;
  private int previewG;
  private int previewB;
  private int gridXOffset;
  private int gridYOffset;
  private int gridXSize;
  private int gridYSize;
  private double brightnessMin;
  private double brightnessMax;
  private double brightnessChange;
  private boolean instancing;

  public FlameParams() {
  }

  public double getMinVal() {
    return minVal;
  }

  public void setMinVal(double pMinVal) {
    minVal = pMinVal;
  }

  public double getMaxVal() {
    return maxVal;
  }

  public void setMaxVal(double pMaxVal) {
    maxVal = pMaxVal;
  }

  public double getWeight() {
    return weight;
  }

  public void setWeight(double pWeight) {
    weight = pWeight;
  }

  public String getFlameXML() {
    return flameXML;
  }

  public void setFlameXML(String pFlameXML) {
    flameXML = pFlameXML;
  }

  private static final String EXAMPLE_FLAME_1 = "<flame name=\"JWildfire\" version=\"JWildfire 2.00 R5 (28.10.2014)\" size=\"777 437\" center=\"0.0 0.0\" scale=\"200.0002012506664\" rotate=\"0.0\" filter=\"1.0\" filter_kernel=\"GAUSSIAN\" quality=\"50.0\" background=\"0.0 0.0 0.0\" bg_transparency=\"0\" brightness=\"4.0\" saturation=\"1.0\" gamma=\"4.0\" gamma_threshold=\"0.04\" vibrancy=\"1.0\" contrast=\"1.0\" white_level=\"200.0\" temporal_samples=\"1.0\" cam_zoom=\"1.0\" cam_pitch=\"0.0\" cam_yaw=\"0.0\" cam_persp=\"0.0\" cam_xfocus=\"0.0\" cam_yfocus=\"0.0\" cam_zfocus=\"0.0\" cam_pos_x=\"0.0\" cam_pos_y=\"0.0\" cam_pos_z=\"0.0\" cam_zpos=\"0.0\" cam_dof=\"0.0\" cam_dof_area=\"0.5\" cam_dof_exponent=\"2.0\" cam_dof_shape=\"BUBBLE\" cam_dof_scale=\"1.0\" cam_dof_rotate=\"0.0\" cam_dof_fade=\"1.0\" shading_shading=\"FLAT\" antialias_amount=\"0.75\" antialias_radius=\"0.36\" post_symmetry_type=\"NONE\" post_symmetry_order=\"3\" post_symmetry_centre_x=\"0.0\" post_symmetry_centre_y=\"0.0\" post_symmetry_distance=\"1.25\" post_symmetry_rotation=\"6.0\" frame=\"1\" frame_count=\"300\" mixer_mode=\"OFF\" >\r\n"
          +
          "  <xform weight=\"37.974195875650885\" color=\"0.0\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" symmetry=\"0.6363142683575415\" waves2_wf=\"1.0\" waves2_wf_scalex=\"0.05411632642405888\" waves2_wf_scaley=\"0.07140430473672771\" waves2_wf_freqx=\"5.665411884739101\" waves2_wf_freqy=\"3.5622214535317194\" waves2_wf_use_cos_x=\"0\" waves2_wf_use_cos_y=\"0\" waves2_wf_dampx=\"0.0\" waves2_wf_dampy=\"-0.0749313500620006\" popcorn2=\"1.1747945422649702E-4\" popcorn2_x=\"1.0\" popcorn2_y=\"0.5\" popcorn2_c=\"1.5\" coefs=\"0.29869999951569876 0.9193040710637221 -0.9193040710637221 0.29869999951569876 -1.6842788839099072 2.0224216083110305\" chaos=\"1.0 1.0 1.0\" />\r\n" +
          "  <xform weight=\"0.5\" color=\"0.0\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" symmetry=\"-1.0\" spherical3D=\"0.43660175471191676\" coefs=\"1.0 0.0 0.0 1.0 0.0 0.0\" chaos=\"1.0 1.0 1.0\" />\r\n" +
          "  <xform weight=\"0.5\" color=\"0.0\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" symmetry=\"-1.0\" linear3D=\"1.0\" coefs=\"0.9321767990927353 -0.36200333594211836 0.36200333594211836 0.9321767990927353 -0.1636856703682093 0.5528632251910492\" chaos=\"1.0 1.0 1.0\" />\r\n" +
          "  <finalxform weight=\"0.0\" color=\"0.0\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" symmetry=\"0.0\" linear3D=\"1.0\" coefs=\"1.0 0.0 0.0 1.0 0.0 0.0\" post=\"0.7425415813272683 0.0 0.0 0.7425415813272683 1.6341570039761018 0.08326914669941926\" chaos=\"1.0 1.0 1.0\" />\r\n" +
          "  <palette count=\"256\" format=\"RGB\" >\r\n" +
          "D45334CF5635CA5936C55C38C05F39BC623AB7653BB2683DAD6B3EA86E3FA371409E7442\r\n" +
          "997743947A448F7D458B80478683488186497C894A778C4C728F4D6D924E68944F639751\r\n" +
          "5E9A525A9D5355A05450A3564BA65746A95841AC593CAF5B37B25C32B55D2DB85E29BB5F\r\n" +
          "24BE611FC1621AC46315C76410CA660BCD6706D06804D26A07D16C0AD06E0DCF710FCE73\r\n" +
          "12CD7615CC7818CB7A1BCA7D1EC97F21C88124C78427C68629C5882CC48B2FC38D32C28F\r\n" +
          "35C19238C0943BBF963EBE9941BE9B43BD9D46BCA049BBA24CBAA44FB9A752B8A955B7AB\r\n" +
          "58B6AE5AB5B05DB4B260B3B563B2B766B1B969B0BC6CAFBE6FAEC172ADC374ACC577ABC8\r\n" +
          "7AAACA7DA9CC80A9CD82A9CD85A8CD87A8CD8AA8CD8DA7CD8FA7CD92A7CD94A7CD97A6CD\r\n" +
          "9AA6CD9CA6CD9FA5CDA1A5CDA4A5CDA6A5CDA9A4CDACA4CDAEA4CDB1A3CDB3A3CDB6A3CC\r\n" +
          "B8A3CCBBA2CCBEA2CCC0A2CCC3A2CCC5A1CCC8A1CCCAA1CCCDA0CCD0A0CCD2A0CCD5A0CC\r\n" +
          "D79FCCDA9FCCDD9FCCDF9ECCE29ECCE49ECCE79ECCE99DCCEC9DCCEA9BC8E798C4E596C0\r\n" +
          "E394BCE091B7DE8FB3DB8DAFD98AABD788A7D486A3D2839FD0819BCD7F97CB7C93C87A8E\r\n" +
          "C6788AC47586C17382BF707EBD6E7ABA6C76B86972B6676EB3656AB16265AE6061AC5E5D\r\n" +
          "AA5B59A75955A55751A3544DA052499E50459C4D41994B3C97493894463492443090422C\r\n" +
          "8D3F288B3D24893B2087391E89381F8A37218B36238D35258E34268F332891322A92312B\r\n" +
          "93302D952F2F962E31972D32992D349A2C369B2B389C2A399E293B9F283DA0273FA22640\r\n" +
          "A32542A42444A62345A72247A82149AA204BAB1F4CAC1E4EAE1E50AF1D52B01C53B11B55\r\n" +
          "B31A57B41959B5185AB7175CB8165EB9155FBB1461BC1363BD1265BF1166BE1166BC1066\r\n" +
          "BA1065B71064B50F63B30F63B10F62AF0E61AD0E60AB0E60A90E5FA70D5EA50D5DA30D5C\r\n" +
          "A10C5C9F0C5B9D0C5A9B0B59990B59960B58940A57920A56900A558E09558C09548A0953\r\n" +
          "88085286085284085182075080074F7E074F7C064E7A064D78064C75054B73054B71054A\r\n" +
          "6F04496D04486B0448690347</palette>\r\n" +
          "</flame>\r\n";

  private static final String EXAMPLE_FLAME_2 = "<flame name=\"JWildfire\" version=\"JWildfire 2.00 R5 (28.10.2014)\" size=\"777 437\" center=\"0.0 0.0\" scale=\"200.0\" rotate=\"0.0\" filter=\"0.0\" filter_kernel=\"GAUSSIAN\" quality=\"800.0\" background=\"0.0 0.0 0.0\" bg_transparency=\"0\" brightness=\"1.18\" saturation=\"1.0\" gamma=\"1.22\" gamma_threshold=\"0.04\" vibrancy=\"1.0\" contrast=\"1.0\" white_level=\"200.0\" temporal_samples=\"1.0\" cam_zoom=\"1.0\" cam_pitch=\"0.0\" cam_yaw=\"0.0\" cam_persp=\"0.0\" cam_xfocus=\"0.0\" cam_yfocus=\"0.0\" cam_zfocus=\"0.0\" cam_pos_x=\"0.0\" cam_pos_y=\"0.0\" cam_pos_z=\"0.0\" cam_zpos=\"0.0\" cam_dof=\"0.0\" cam_dof_area=\"0.5\" cam_dof_exponent=\"2.0\" cam_dof_shape=\"BUBBLE\" cam_dof_scale=\"1.0\" cam_dof_rotate=\"0.0\" cam_dof_fade=\"1.0\" resolution_profile=\"512x512\" quality_profile=\"null\" shading_shading=\"FLAT\" antialias_amount=\"0.75\" antialias_radius=\"0.36\" post_symmetry_type=\"NONE\" post_symmetry_order=\"3\" post_symmetry_centre_x=\"0.0\" post_symmetry_centre_y=\"0.0\" post_symmetry_distance=\"1.25\" post_symmetry_rotation=\"6.0\" frame=\"289\" frame_count=\"300\" mixer_mode=\"OFF\" >\r\n"
          +
          "  <xform weight=\"0.4789\" color=\"0.0\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" symmetry=\"0.0\" gaussian_blur=\"0.258\" blur=\"0.19\" coefs=\"1.0 0.0 0.0 1.0 0.0 0.0\" chaos=\"1.0 1.0\" />\r\n" +
          "  <xform weight=\"1.44\" color=\"0.33\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" symmetry=\"0.0\" linear3D=\"1.03\" ztranslate=\"0.14\" waves2=\"1.27\" waves2_scalex=\"0.25\" waves2_scaley=\"0.5\" waves2_freqx=\"3.57\" waves2_freqy=\"0.7853981633974483\" epispiral=\"0.027\" epispiral_n=\"6.0\" epispiral_thickness=\"0.0\" epispiral_holes=\"1.0\" circlize=\"0.22131\" circlize_hole=\"0.3\" bubble=\"0.10265\" cross=\"0.00514\" swirl=\"0.05\" coefs=\"-2.149723642303038 -0.1681122642634287 0.1681122642634287 -2.149723642303038 -0.17687638662130672 -0.06576069488200381\" chaos=\"1.0 1.0\" />\r\n" +
          "  <finalxform weight=\"0.0\" color=\"0.0\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" symmetry=\"0.0\" zscale=\"0.09196\" julia3D=\"1.0\" julia3D_power=\"-6\" pre_circlecrop=\"1.0\" pre_circlecrop_radius=\"10000.0\" pre_circlecrop_x=\"0.0\" pre_circlecrop_y=\"0.0\" pre_circlecrop_scatter_area=\"0.0\" pre_circlecrop_zero=\"0\" coefs=\"1.0 0.0 0.0 1.0 0.0 0.0\" chaos=\"1.0 1.0\" />\r\n" +
          "  <palette count=\"256\" format=\"RGB\" >\r\n" +
          "9BC0746A9F6C3D7F640C5D5B14385D850C6AF6007BFF008AFF008FFF008FB6278E69548F\r\n" +
          "562E714508563300393B0023713119A97B0FE0C505DAB34EBD78B8A03DFF7F0AFF4A2EFF\r\n" +
          "1353D300797B00966423A7A96FBBF0BBCCFFAAB8FF7D98D54F78912C5B58808A78D6BA98\r\n" +
          "FFE9B8FFFFC2C0FFAF51FF9D00FF8A0FE080677679C00C74FF0071BB0F8773679F28BFB5\r\n" +
          "31C2BF9D62BDFF03B8FF00B6FF00B5FF76B5FFF3B5F0FFAEA2F887539D6207423B110C1B\r\n" +
          "760203DF0000FF0000FF3800FF9A00FFFD00E5FF00C5FF00A7C900878C00987908D08E2A\r\n" +
          "FFA24EFFB671FF8269E74256B10345890047983898A791EBB8E7FFDBF8FFFFCAAAFF9D34\r\n" +
          "FF7100FF3800FF0000FF0000FF0000FF004EFF00ACDF0AFFD831FFEC59F3FF82CCFFACA4\r\n" +
          "F56F62CA2A1DA200007F00006E0018592F73496ACE7460FFCC2CFFFF00FFFF00FFFF00FF\r\n" +
          "A936FF3B73FF00ACF000DD7F08FF0C14FF002AFF0049FF1D64E98782C2F3518EC71D5998\r\n" +
          "00236A001E5600808400E0AE00FFDB00FFEC00FFF000C5F00089F13893F88E9FFFE4A7FF\r\n" +
          "FFC0FFB3EEF662FFE414FFD347FF9AB5BF4CFF6200FF0800FF1B0C7B2E620040BA0060D6\r\n" +
          "00989B00D05E00FF2019FF0085FA00F0E500FFD100F08F008C4C282A086E000780226749\r\n" +
          "49C90D6EFF0079FF007FDB0082A200826F0069A4004ED60036FF0028FF0033D100399333\r\n" +
          "42546C58744E74B1168FEE00A0FF006EF02E38C2A20396FF0394FF42C4AE7FF63DBDFF00\r\n" +
          "98FF0D59F0801BCCF300A5FF0073FF0042FF000DFF0000FF5400AAAF0027FF0000EB2500\r\n" +
          "AE940071FF0038FF1D18FF2000FF2200DA2300A03333564E7F0C69C90082F6006EFF434E\r\n" +
          "FF8E31FFD027FFF16AD8FFAF98FFF69FFFEBDFDB9AFF984AFF5100FF3600DA2200910A02\r\n" +
          "58051364364F6F67897B96C29A94F3C56CFFF143FFFF1DFFE93DFFA962FF6987FF499AFF\r\n" +
          "7F6FFFB547CAE91E8AFF2F71FF6774FF9F76FFD67BE7B873AF946A76716264654CAA9A13\r\n" +
          "EED000FFFF00FFF500BFBD586785D50D4EFF0A7FFF08B5FF07ECEE0AF1DF1391F31D2EFF\r\n" +
          "2500FF3100FF3B0CFF4743EB</palette>\r\n" +
          "</flame>\r\n";

  private static final String EXAMPLE_FLAME_3 = "<flame name=\"Gnarl - 112731782\" version=\"JWildfire 2.00 R5 (28.10.2014)\" size=\"777 437\" center=\"0.0 0.0\" scale=\"200.0002012506664\" rotate=\"0.0\" filter=\"0.0\" filter_kernel=\"GAUSSIAN\" quality=\"25.0\" background=\"0.0 0.0 0.0\" bg_transparency=\"0\" brightness=\"4.0\" saturation=\"1.0\" gamma=\"4.0\" gamma_threshold=\"0.01\" vibrancy=\"1.0\" contrast=\"1.0\" white_level=\"200.0\" temporal_samples=\"1.0\" cam_zoom=\"1.0\" cam_pitch=\"0.0\" cam_yaw=\"0.0\" cam_persp=\"0.0\" cam_xfocus=\"0.0\" cam_yfocus=\"0.0\" cam_zfocus=\"0.0\" cam_pos_x=\"0.0\" cam_pos_y=\"0.0\" cam_pos_z=\"0.0\" cam_zpos=\"0.0\" cam_dof=\"0.0\" cam_dof_area=\"0.5\" cam_dof_exponent=\"2.0\" cam_dof_shape=\"BUBBLE\" cam_dof_scale=\"1.0\" cam_dof_rotate=\"0.0\" cam_dof_fade=\"1.0\" shading_shading=\"FLAT\" antialias_amount=\"0.75\" antialias_radius=\"0.36\" post_symmetry_type=\"NONE\" post_symmetry_order=\"3\" post_symmetry_centre_x=\"0.0\" post_symmetry_centre_y=\"0.0\" post_symmetry_distance=\"1.25\" post_symmetry_rotation=\"6.0\" frame=\"1\" frame_count=\"300\" mixer_mode=\"OFF\" >\r\n"
          +
          "  <xform weight=\"135.15842073171234\" color=\"0.9\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" symmetry=\"0.8983800715203163\" waves2b=\"1.0004755934497962\" waves2b_freqx=\"3.8465645746577124\" waves2b_freqy=\"2.747984229782468\" waves2b_pwx=\"-0.8431809904821272\" waves2b_pwy=\"-1.5686874784453617\" waves2b_scalex=\"-0.07979007464731053\" waves2b_scaleinfx=\"-0.07979007464731053\" waves2b_scaley=\"-0.07944761822404547\" waves2b_scaleinfy=\"-0.07944761822404547\" waves2b_unity=\"1.0\" waves2b_jacok=\"-0.11220273736509079\" ztranslate=\"-0.002366627476504845\" coefs=\"6.092617825758082E-17 0.995 -0.995 6.092617825758082E-17 -2.8642212104606593 2.8581419081279162\" chaos=\"1.0 1.0\" />\r\n"
          +
          "  <xform weight=\"0.5\" color=\"0.0\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" symmetry=\"-1.0\" radial_blur=\"0.014322029425185769\" radial_blur_angle=\"0.5\" julian=\"0.1458522898389012\" julian_power=\"-8\" julian_dist=\"4.931951031111716\" coefs=\"1.0 0.0 0.0 1.0 0.0 0.0\" chaos=\"1.0 1.0\" />\r\n" +
          "  <finalxform weight=\"0.0\" color=\"0.0\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" symmetry=\"0.0\" linear3D=\"1.0\" coefs=\"1.0 0.0 0.0 1.0 0.0 0.0\" post=\"0.3357192066228679 0.0 0.0 0.3357192066228679 0.9159606136936114 2.220446049250313E-16\" chaos=\"1.0 1.0\" />\r\n" +
          "  <palette count=\"256\" format=\"RGB\" >\r\n" +
          "C89308CD9715D39B23D89F30DEA33DE3A74BE9AB58EEAF65F4B373F9B780E3A776C89368\r\n" +
          "AD7F59916B4B76573D5B432F402F21251B120A0704080600110D01191401221B012B2202\r\n" +
          "3329023C30024537034D3E03573F03603A046A36047331057D2D058729059024069A2006\r\n" +
          "A41C07A2180890150C7F130F6D10125C0D154B0B1839081B28061F1603220E03231A0820\r\n" +
          "260E1C3214193F1A154B201257250F632B0B7031087C38078A4A16985B25A66C34B47D44\r\n" +
          "C38E53D19F62DFB071EDC180FBD28FF1C99BE8C1A6DEB8B2D4AFBECAA7CAC19ED5B796E1\r\n" +
          "AD8DEDA384F89577E38569C7765CAC674E9157407648325A38253F291724190909221101\r\n" +
          "3521024831035B41036E5104816205937205A68206B99207BD9916B69831AF974BA89766\r\n" +
          "A296819B959B9495B68D94D08693EB808DF97A7DF8746EF76F5FF66950F56441F35E32F2\r\n" +
          "5923F15314F0530EE86020CF6E32B57B449C885683966869A37A50B08C37BE9E1EC7AD08\r\n" +
          "BDAD08B2AD08A7AE089DAE0892AE0787AE077CAF0772AF0767AF076EA721759F3C7B9756\r\n" +
          "828F7189878B907EA69676C09D6EDBA466F5AD6DF9B677FAC081FAC98BFAD394FBDC9EFB\r\n" +
          "E6A8FBEFB1FCF8BBFCF0BAE9E0B5CED0B0B3C1AB98B1A67DA1A162919B4782962C729112\r\n" +
          "6E8706727A06776D067C5F068152068545068A37068F2A06931D069B1E0CA6341AB14929\r\n" +
          "BC5F37C67445D18A54DCA062E7B570F2CB7FF3D385DABC76C1A567A78D588E7649755F3A\r\n" +
          "5C482C43301D2A190E180602300D05491309621A0C7A2010932714AC2D17C5341BDD3A1E\r\n" +
          "F64122DD3B30C4343EAB2E4C92275A792167601A754714832E0D9115079F240C933A1282\r\n" +
          "511972671F617D2650942C40AA332FC03A1ED7400EDF4B09E2580AE4650AE7720BEA7E0C\r\n" +
          "ED8B0CEF980DF2A50DF5B20EF6BB19F7C12BF8C83EF8CE50F9D563FADB75FAE187FBE89A\r\n" +
          "FCEEACF2E7B6DBCDB4C4B3B2AD9AB09580AE7E66AC674CAA5032A83918A52E0AA1451995\r\n" +
          "5C278A72357F894473A05268B6615DCD6F51E47E46F6883BEF7A35E96C30E25F2ADB5124\r\n" +
          "D5431ECE3518C72813C11A0D</palette>\r\n" +
          "</flame>\r\n";

  private static final String EXAMPLE_FLAME_4 = "<flame name=\"JWildfire\" version=\"JWildfire 2.00 R5 (28.10.2014)\" size=\"777 437\" center=\"0.0 0.0\" scale=\"200.0\" rotate=\"0.0\" filter=\"1.2\" filter_kernel=\"GAUSSIAN\" quality=\"100.0\" background=\"0.0 0.0 0.0\" bg_transparency=\"0\" brightness=\"4.0\" saturation=\"1.0\" gamma=\"4.0\" gamma_threshold=\"0.04\" vibrancy=\"1.0\" contrast=\"1.0\" white_level=\"200.0\" temporal_samples=\"1.0\" cam_zoom=\"1.0\" cam_pitch=\"0.0\" cam_yaw=\"0.0\" cam_persp=\"0.0\" cam_xfocus=\"0.0\" cam_yfocus=\"0.0\" cam_zfocus=\"0.0\" cam_pos_x=\"0.0\" cam_pos_y=\"0.0\" cam_pos_z=\"0.0\" cam_zpos=\"0.0\" cam_dof=\"0.0\" cam_dof_area=\"0.5\" cam_dof_exponent=\"2.0\" cam_dof_shape=\"BUBBLE\" cam_dof_scale=\"1.0\" cam_dof_rotate=\"0.0\" cam_dof_fade=\"1.0\" shading_shading=\"FLAT\" antialias_amount=\"0.75\" antialias_radius=\"0.36\" post_symmetry_type=\"NONE\" post_symmetry_order=\"3\" post_symmetry_centre_x=\"0.0\" post_symmetry_centre_y=\"0.0\" post_symmetry_distance=\"1.25\" post_symmetry_rotation=\"6.0\" frame=\"1\" frame_count=\"300\" mixer_mode=\"OFF\" >\r\n"
          +
          "  <xform weight=\"0.5\" color=\"0.0\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" symmetry=\"-0.5\" ngon=\"0.5\" ngon_circle=\"2.0\" ngon_corners=\"2.0\" ngon_power=\"1.0\" ngon_sides=\"4.0\" pre_blur=\"1.0\" coefs=\"1.0 0.0 0.0 1.0 0.0 0.0\" chaos=\"1.0 1.0\" />\r\n" +
          "  <xform weight=\"1.0\" color=\"0.0\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" symmetry=\"0.9\" linear=\"1.0\" coefs=\"1.0 0.0 0.0 1.0 0.0 0.0\" post=\"0.7670557878945545 -0.22721227576313813 0.22721227576313813 0.7670557878945545 0.0 0.0\" chaos=\"1.0 1.0\" />\r\n" +
          "  <palette count=\"256\" format=\"RGB\" >\r\n" +
          "4E77BC4E77B84E77B54E76B14E76AE4F76AA4F76A64F75A34F759F4F759C4F75984F7495\r\n" +
          "4F749150748D50748A50738650738350737F50737B50737850727450727151726D51726A\r\n" +
          "51716651716251715F51715B51705851705452705052704D527049526F46526F42526F3E\r\n" +
          "526F3B526E37526E34536E30536E2D536D29536D25546C24586A265C682760672964652B\r\n" +
          "69632D6D612F715F31755D33795B357D593681573885553A89533C8D513E914F40954D42\r\n" +
          "994B449D4945A14747A54549A9444BAD424DB1404FB53E51B93C53BD3A54C13856C53658\r\n" +
          "C9345ACD325CD1305ED52E60D92C62DD2A63E12865E52667EA2469EE226BF2206DF61F6F\r\n" +
          "FA1D71FE1B72FC1A74F71B75F21B77EC1C78E71C7AE21D7BDD1D7CD81D7ED31E7FCE1E81\r\n" +
          "C91F82C41F83BF2085BA2086B52188B02189AB218AA6228CA1228D9B238F962390912491\r\n" +
          "8C24938725948225967D259778269973269A6E279B69279D64289E5F28A05A29A15529A2\r\n" +
          "5029A44A2AA5452AA7402BA83B2BA9362CAB312CAC2C2DAE272DAF2630AE2633AE2537AD\r\n" +
          "253AAD243DAC2340AB2344AB2247AA224AAA214DA92151A92054A81F57A71F5AA71E5EA6\r\n" +
          "1E61A61D64A51C67A41C6AA41B6EA31B71A31A74A21A77A2197BA1187EA01881A017849F\r\n" +
          "17889F168B9E158E9D15919D14959C14989C139B9B129E9A12A19A11A59911A89910AB98\r\n" +
          "10AE980FB2970EB5960FB69611B39813B09915AD9A17A99B19A69C1BA39D1DA09E1F9DA0\r\n" +
          "219AA12497A22694A32891A42A8DA52C8AA62E87A83084A93281AA347EAB367BAC3878AD\r\n" +
          "3B75AF3D71B03F6EB1416BB24368B34565B44762B5495FB74B5CB84D58B94F55BA5152BB\r\n" +
          "544FBC564CBD5849BF5A46C05C43C15E40C2603CC36239C46436C56633C76832C56A32C3\r\n" +
          "6C32C16E32BE7031BC7231BA7431B77631B57831B37A31B07C31AE7E31AC8031A98230A7\r\n" +
          "8430A58630A28830A08A309E8C309B8E3099903097922F94942F92962F90982F8D9A2F8B\r\n" +
          "9C2F899D2F869F2F84A12F81A32E7FA52E7DA72E7AA92E78AB2E76AD2E73AF2E71B12E6F\r\n" +
          "B32D6CB52D6AB72D68B92D65</palette>\r\n" +
          "</flame>\r\n";

  private static final String EXAMPLE_FLAME_5 = "<flame version=\"JWildfire 2.00 R5 (28.10.2014)\" size=\"777 437\" center=\"0.0 0.0\" scale=\"200.0\" rotate=\"0.0\" filter=\"0.0\" filter_kernel=\"GAUSSIAN\" quality=\"1000.0\" background=\"0.0 0.0 0.0\" bg_transparency=\"0\" brightness=\"4.0\" saturation=\"1.0\" gamma=\"4.0\" gamma_threshold=\"0.01\" vibrancy=\"1.0\" contrast=\"1.0\" white_level=\"200.0\" temporal_samples=\"1.0\" cam_zoom=\"1.0\" cam_pitch=\"0.0\" cam_yaw=\"0.0\" cam_persp=\"0.0\" cam_xfocus=\"0.0\" cam_yfocus=\"0.0\" cam_zfocus=\"0.0\" cam_pos_x=\"0.0\" cam_pos_y=\"0.0\" cam_pos_z=\"0.0\" cam_zpos=\"0.0\" cam_dof=\"0.0\" cam_dof_area=\"0.5\" cam_dof_exponent=\"2.0\" cam_dof_shape=\"BUBBLE\" cam_dof_scale=\"1.0\" cam_dof_rotate=\"0.0\" cam_dof_fade=\"1.0\" shading_shading=\"FLAT\" antialias_amount=\"0.75\" antialias_radius=\"0.36\" post_symmetry_type=\"NONE\" post_symmetry_order=\"3\" post_symmetry_centre_x=\"0.0\" post_symmetry_centre_y=\"0.0\" post_symmetry_distance=\"1.25\" post_symmetry_rotation=\"6.0\" frame=\"1\" frame_count=\"300\" mixer_mode=\"OFF\" >\r\n"
          +
          "  <xform weight=\"0.5\" color=\"0.0\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" symmetry=\"0.0\" linear3D=\"1.0\" coefs=\"0.5342240448089189 0.4703673703788216 -0.4703673703788216 0.5342240448089189 0.02094111850209407 -0.004502118644067805\" chaos=\"1.0 1.0\" />\r\n" +
          "  <xform weight=\"0.5\" color=\"0.0\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" symmetry=\"0.0\" julian2=\"1.0\" julian2_power=\"13\" julian2_dist=\"1.0\" julian2_a=\"1.0\" julian2_b=\"0.0\" julian2_c=\"0.0\" julian2_d=\"1.0\" julian2_e=\"0.0\" julian2_f=\"0.0\" coefs=\"-0.008134849967633001 -1.0009672446469635 1.0009672446469635 -0.008134849967633001 0.938162108893816 -0.40519067796610164\" chaos=\"1.0 1.0\" />\r\n" +
          "  <palette count=\"256\" format=\"RGB\" >\r\n" +
          "A47762A079659C7B67997C6A957E6D91806F8D827289837485857782877A7E897C7A8A7F\r\n" +
          "768C82728E846F90876B918967938C63958F5F97915B9894589A97549C99509E9C4C9F9E\r\n" +
          "48A1A144A3A441A5A63DA6A939A8AC35AAAE31ACB12EADB32AAFB626B1B922B3BB1EB5BE\r\n" +
          "1AB6C117B8C313BAC60FBCC80BBDCB07BFCE04C1D002C1D104BDCE07BACB0AB7C80CB3C5\r\n" +
          "0FB0C211ACBF14A9BD16A5BA19A2B71B9EB41E9BB12198AE2394AB2691A8288DA52B8AA2\r\n" +
          "2D869F30839C327F99357C963879943A75913D728E3F6E8B426B8844678547648249607F\r\n" +
          "4C5D7C4E5979515676545373564F70594C6D5B486A5E4568604165633E62653A5F68375C\r\n" +
          "6B34596D30566C30576A325968335B66355E6337606138625F3A655C3C675A3D69583F6C\r\n" +
          "56416E5342705144734F46754C47774A497A484B7C454C7E434E814150833F51853C5388\r\n" +
          "3A558A38568C35588F335A91315B942E5D962C5F982A609B28629D25649F2365A22167A4\r\n" +
          "1E69A61C6AA91A6CAB176EAD156FB01371B21173B40E74B70C76B90C73B50D71B10D6EAE\r\n" +
          "0E6CAA0E69A60E67A20F649F0F619B105F97105C93105A8F11578C115488125284124F80\r\n" +
          "124D7D134A7913487514457114426E14406A153D66153B6216385E16365B163357173053\r\n" +
          "172E4F182B4C18294818264419244019213C1A1E391A1C351A19311B172D1B142A1C1126\r\n" +
          "1C0F221C0C1E1D0A1B1D09191C0E1C1C12201B16231A1B261A1F2919232D182830182C33\r\n" +
          "17303616353A16393D153D40154243144647134A4A134F4D125350115754115C5710605A\r\n" +
          "10655E0F69610E6D640E72670D766B0C7A6E0C7F710B83740A87780A8C7B09907E099481\r\n" +
          "089985079D8807A18B06A68E05AA9205AE9504B39803B79B03BB9F02C0A202BFA203BBA0\r\n" +
          "04B89F04B49D05B19B06AD9A07AA9807A69708A395099F93099C920A99900B958F0B928D\r\n" +
          "0C8E8B0D8B8A0D87880E84860F80850F7D8310798211768011727E126F7D136B7B136879\r\n" +
          "146578156176155E75165A7317577118537018506E194C6D1A496B1A45691B42681C3E66\r\n" +
          "1C3B641D37631E34611E3060</palette>\r\n" +
          "</flame>\r\n";

  private static final String EXAMPLE_FLAME_6 = "<flame name=\"Gnarl - 1923014384\" version=\"JWildfire 2.00 R5 (28.10.2014)\" size=\"777 437\" center=\"0.0 0.0\" scale=\"200.0001509379808\" rotate=\"0.0\" filter=\"0.0\" filter_kernel=\"GAUSSIAN\" quality=\"25.0\" background=\"0.0 0.0 0.0\" bg_transparency=\"0\" brightness=\"4.0\" saturation=\"1.0\" gamma=\"4.0\" gamma_threshold=\"0.01\" vibrancy=\"1.0\" contrast=\"1.0\" white_level=\"200.0\" temporal_samples=\"1.0\" cam_zoom=\"1.0\" cam_pitch=\"0.0\" cam_yaw=\"0.0\" cam_persp=\"0.0\" cam_xfocus=\"0.0\" cam_yfocus=\"0.0\" cam_zfocus=\"0.0\" cam_pos_x=\"0.0\" cam_pos_y=\"0.0\" cam_pos_z=\"0.0\" cam_zpos=\"0.0\" cam_dof=\"0.0\" cam_dof_area=\"0.5\" cam_dof_exponent=\"2.0\" cam_dof_shape=\"BUBBLE\" cam_dof_scale=\"1.0\" cam_dof_rotate=\"0.0\" cam_dof_fade=\"1.0\" shading_shading=\"FLAT\" antialias_amount=\"0.75\" antialias_radius=\"0.36\" post_symmetry_type=\"NONE\" post_symmetry_order=\"3\" post_symmetry_centre_x=\"0.0\" post_symmetry_centre_y=\"0.0\" post_symmetry_distance=\"1.25\" post_symmetry_rotation=\"6.0\" frame=\"1\" frame_count=\"300\" mixer_mode=\"OFF\" >\r\n"
          +
          "  <xform weight=\"149.57122134783094\" color=\"0.9\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" symmetry=\"0.9341893600351556\" waves2b=\"1.0003083342605\" waves2b_freqx=\"3.421198240541759\" waves2b_freqy=\"3.6125192255956446\" waves2b_pwx=\"2.098767142982067\" waves2b_pwy=\"-1.0E-6\" waves2b_scalex=\"0.04236912369938721\" waves2b_scaleinfx=\"0.04236912369938721\" waves2b_scaley=\"0.04882649089227655\" waves2b_scaleinfy=\"-0.2650489349091797\" waves2b_unity=\"1.0\" waves2b_jacok=\"0.25\" linear3D=\"-0.0021125674351290496\" coefs=\"6.123233995736766E-17 1.0 -1.0 6.123233995736766E-17 -1.5245776921755698 2.321684485915477\" chaos=\"1.0 1.0\" />\r\n" +
          "  <xform weight=\"0.5\" color=\"0.0\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" symmetry=\"-1.0\" bubble=\"0.6526119507124961\" radial_blur=\"0.37892073186557274\" radial_blur_angle=\"0.5\" coefs=\"1.0 0.0 0.0 1.0 0.0 0.0\" chaos=\"1.0 1.0\" />\r\n" +
          "  <finalxform weight=\"0.0\" color=\"0.0\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" symmetry=\"0.0\" linear3D=\"1.0\" coefs=\"1.0 0.0 0.0 1.0 0.0 0.0\" post=\"0.18517886944824444 0.0 0.0 0.18517886944824444 0.3851198034848142 -0.08326914669941915\" chaos=\"1.0 1.0\" />\r\n" +
          "  <palette count=\"256\" format=\"RGB\" >\r\n" +
          "F79B11E48C10D27E0EBF6F0DAC610B99520A8744087435076127054E18043C09023A1302\r\n" +
          "3D24034135034446034857044B68044F7A05528B05569C0559AD0659AD06559C05508C05\r\n" +
          "4C7C05486C04445C04404B033B3B03372B03331B02341505412C0F4E431A5C5A2469712F\r\n" +
          "768839839F4491B64E9ECD59ABE463B8FA6DBDFA75C2FB7DC7FB85CBFB8DD0FB95D5FC9D\r\n" +
          "DAFCA5DEFCADE3FDB5E8FDBCDAF1AFC8E39DB5D48BA2C67990B7677DA9556B9A42588C30\r\n" +
          "467D1E336E0C3367044266045165056063056F62057E61068D60069C5E06AB5D07BA5C07\r\n" +
          "BF5A07AF55069F51068F4D05804905704504604004503C03403803313402223102263802\r\n" +
          "293F022D4603304D03335503375C033A63033E6A044172044479044071163B662A355A3F\r\n" +
          "304F542B446925397E202E931A23A81518BD0F0CD2120BCD1A10B82315A32B1A8E331E79\r\n" +
          "3B236444284F4C2D3A5432255D3710673C0576420C854913944F1AA25521B15B28C0612E\r\n" +
          "CF6735DD6D3CEC7443F87A4BF37E5DEE836EE98880E38D91DE92A3D997B4D39CC5CEA1D7\r\n" +
          "C9A6E8C4AAFAB6A2E7A798CE998EB68A849D7B7A856C706C5E66544F5C3C40522331480B\r\n" +
          "3947064D4A0B614E1175521788561D9C5A22B05E28C4622ED86634EC6A3AF86D3DF8703C\r\n" +
          "F8733BF8763AF8793AF87C39F87F38F88237F88536F88836F78A34F48730F0832BEC7F27\r\n" +
          "E97C22E5781EE17419DD7015DA6D10D6690CD26507D66E13DA7820DE822DE28C3AE69647\r\n" +
          "EAA054EEAA61F2B46EF6BE7BFAC888ECBE83D7AC75C29968AC875A97744C82623F6C4F31\r\n" +
          "573D24412A162C17092915053F280D543A16694C1E7E5F2793712FA88438BE9640D3A949\r\n" +
          "E8BB51F8CA5BF5CA6BF1CA7BEECA8CEACA9CE7C9ACE3C9BCE0C9CCDCC9DCD9C9ECD5C9FC\r\n" +
          "CFC0FDC9B7FCC3AEFCBDA5FCB69CFCB093FBAA8AFBA481FB9E79FA9870FA9D6EEFA76FE0\r\n" +
          "B070D1BA71C1C473B2CD74A3D77594E17684EA7775F47866EF7461DB6868C75D6FB35275\r\n" +
          "9F477C8B3B8377308A6325904F1A973B0F9E2D09A53318AD3827B63D35BE4244C74852CF\r\n" +
          "4D61D7526FE0577EE85D8CF1</palette>\r\n" +
          "</flame>\r\n";

  private static final String EXAMPLE_FLAME_7 = "<flame name=\"Linear only - 1308948364\" version=\"JWildfire 2.00 R5 (28.10.2014)\" size=\"777 437\" center=\"0.0 0.0\" scale=\"150.5964619644154\" rotate=\"0.0\" filter=\"0.0\" filter_kernel=\"GAUSSIAN\" quality=\"25.0\" background=\"0.0 0.0 0.0\" bg_transparency=\"0\" brightness=\"4.0\" saturation=\"1.0\" gamma=\"4.0\" gamma_threshold=\"0.01\" vibrancy=\"1.0\" contrast=\"1.0\" white_level=\"200.0\" temporal_samples=\"1.0\" cam_zoom=\"1.0\" cam_pitch=\"0.0\" cam_yaw=\"0.0\" cam_persp=\"0.0\" cam_xfocus=\"0.0\" cam_yfocus=\"0.0\" cam_zfocus=\"0.0\" cam_pos_x=\"0.0\" cam_pos_y=\"0.0\" cam_pos_z=\"0.0\" cam_zpos=\"0.0\" cam_dof=\"0.0\" cam_dof_area=\"0.5\" cam_dof_exponent=\"2.0\" cam_dof_shape=\"BUBBLE\" cam_dof_scale=\"1.0\" cam_dof_rotate=\"0.0\" cam_dof_fade=\"1.0\" shading_shading=\"FLAT\" antialias_amount=\"0.75\" antialias_radius=\"0.36\" post_symmetry_type=\"NONE\" post_symmetry_order=\"3\" post_symmetry_centre_x=\"0.0\" post_symmetry_centre_y=\"0.0\" post_symmetry_distance=\"1.25\" post_symmetry_rotation=\"6.0\" frame=\"1\" frame_count=\"300\" mixer_mode=\"OFF\" >\r\n"
          +
          "  <xform weight=\"2.14665945815808\" color=\"0.3943962123370437\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" symmetry=\"0.0\" linear3D=\"0.6394343397248277\" coefs=\"0.8134158146518513 0.11023431728552485 -0.11023431728552485 0.8134158146518513 0.9022397464316282 1.2712752715867295\" chaos=\"1.0 1.0 1.0 1.0 1.0\" />\r\n" +
          "  <xform weight=\"1.9731420005370912\" color=\"0.9370193010128562\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" symmetry=\"0.0\" linear3D=\"0.9947786539820798\" coefs=\"0.6978271488069498 0.09032145802238267 -0.09032145802238267 0.6978271488069498 -0.6010959795008435 -1.6104567666085674\" chaos=\"1.0 1.0 1.0 1.0 1.0\" />\r\n" +
          "  <xform weight=\"1.5623309502080376\" color=\"0.11510495920287345\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" symmetry=\"0.0\" linear3D=\"0.7306124526886906\" coefs=\"0.5667920382144069 0.027347468777055597 -0.027347468777055597 0.5667920382144069 -1.087204277147988 1.3070291965246417\" chaos=\"1.0 1.0 1.0 1.0 1.0\" />\r\n" +
          "  <xform weight=\"5.927180145531867\" color=\"0.7270572294428134\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" symmetry=\"0.0\" linear3D=\"0.7454667607571777\" coefs=\"0.42649258873014323 -0.2612400658133047 0.2612400658133047 0.42649258873014323 0.672791851721616 0.020688071497564797\" chaos=\"1.0 1.0 1.0 1.0 1.0\" />\r\n" +
          "  <xform weight=\"1.4995925210929908\" color=\"0.7486445157933451\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" symmetry=\"0.0\" linear3D=\"0.9566548033263693\" coefs=\"0.3834450072412761 -0.18169569708979585 0.18169569708979585 0.3834450072412761 -0.5393905632074423 -0.6479633712998991\" chaos=\"1.0 1.0 1.0 1.0 1.0\" />\r\n" +
          "  <palette count=\"256\" format=\"RGB\" >\r\n" +
          "65F20365F20365F20365F20365F20365F20365F20365F20365F20365F20365F20365F203\r\n" +
          "65F20365F20365F20365F20365F20365F203FDD07AFDD07AFDD07AFDD07AFDD07AFDD07A\r\n" +
          "FDD07AFDD07AFDD07AFDD07AFDD07AFDD07AFDD07AFDD07AFDD07AFDD07AFDD07A3502C9\r\n" +
          "3502C93502C93502C93502C93502C93502C93502C93502C93502C93502C93502C93502C9\r\n" +
          "3502C93502C93502C93502C9EDBB03EDBB03EDBB03EDBB03EDBB03EDBB03EDBB03EDBB03\r\n" +
          "EDBB03EDBB03EDBB03EDBB03EDBB03EDBB03EDBB03EDBB03EDBB0374F7FD74F7FD74F7FD\r\n" +
          "74F7FD74F7FD74F7FD74F7FD74F7FD74F7FD74F7FD74F7FD74F7FD74F7FD74F7FD74F7FD\r\n" +
          "74F7FD74F7FDFECCB2FECCB2FECCB2FECCB2FECCB2FECCB2FECCB2FECCB2FECCB2FECCB2\r\n" +
          "FECCB2FECCB2FECCB2FECCB2FECCB2FECCB2FECCB26DA1FD6DA1FD6DA1FD6DA1FD6DA1FD\r\n" +
          "6DA1FD6DA1FD6DA1FD6DA1FD6DA1FD6DA1FD6DA1FD6DA1FD6DA1FD6DA1FD6DA1FD6DA1FD\r\n" +
          "C0C4FEC0C4FEC0C4FEC0C4FEC0C4FEC0C4FEC0C4FEC0C4FEC0C4FEC0C4FEC0C4FEC0C4FE\r\n" +
          "C0C4FEC0C4FEC0C4FEC0C4FEC0C4FEFED2C9FED2C9FED2C9FED2C9FED2C9FED2C9FED2C9\r\n" +
          "FED2C9FED2C9FED2C9FED2C9FED2C9FED2C9FED2C9FED2C9FED2C9FED2C9C1C2FEC1C2FE\r\n" +
          "C1C2FEC1C2FEC1C2FEC1C2FEC1C2FEC1C2FEC1C2FEC1C2FEC1C2FEC1C2FEC1C2FEC1C2FE\r\n" +
          "C1C2FEC1C2FEC1C2FED07603D07603D07603D07603D07603D07603D07603D07603D07603\r\n" +
          "D07603D07603D07603D07603D07603D07603D07603D07603FC9B2EFC9B2EFC9B2EFC9B2E\r\n" +
          "FC9B2EFC9B2EFC9B2EFC9B2EFC9B2EFC9B2EFC9B2EFC9B2EFC9B2EFC9B2EFC9B2EFC9B2E\r\n" +
          "FC9B2EFCCE07FCCE07FCCE07FCCE07FCCE07FCCE07FCCE07FCCE07FCCE07FCCE07FCCE07\r\n" +
          "FCCE07FCCE07FCCE07FCCE07FCCE07FCCE07E07803E07803E07803E07803E07803E07803\r\n" +
          "E07803E07803E07803E07803E07803E07803E07803E07803E07803E07803E078033B0501\r\n" +
          "3B05013B05013B05013B05013B05013B05013B05013B05013B05013B05013B05013B0501\r\n" +
          "3B05013B05013B05013B0501</palette>\r\n" +
          "</flame>\r\n";

  public static final String[] EXAMPLE_FLAMES = {EXAMPLE_FLAME_1, EXAMPLE_FLAME_2, EXAMPLE_FLAME_3, EXAMPLE_FLAME_4, EXAMPLE_FLAME_5, EXAMPLE_FLAME_6, EXAMPLE_FLAME_7};

  public boolean hasFlame() {
    return flameXML != null && flameXML.length() > 0;
  }

  public double getSize() {
    return size;
  }

  public void setSize(double pSize) {
    size = pSize;
  }

  public double getSizeVar() {
    return sizeVar;
  }

  public void setSizeVar(double pSizeVar) {
    sizeVar = pSizeVar;
  }

  public double getCentreX() {
    return centreX;
  }

  public void setCentreX(double pCentreX) {
    centreX = pCentreX;
  }

  public double getCentreY() {
    return centreY;
  }

  public void setCentreY(double pCentreY) {
    centreY = pCentreY;
  }

  public String getFlameParamMap1Filename() {
    return flameParamMap1Filename;
  }

  public void setFlameParamMap1Filename(String pFlameParamMap1Filename) {
    flameParamMap1Filename = pFlameParamMap1Filename;
  }

  public double getFlameParam1Min() {
    return flameParam1Min;
  }

  public void setFlameParam1Min(double pFlameParam1Min) {
    flameParam1Min = pFlameParam1Min;
  }

  public double getFlameParam1Max() {
    return flameParam1Max;
  }

  public void setFlameParam1Max(double pFlameParam1Max) {
    flameParam1Max = pFlameParam1Max;
  }

  public String getFlameParam1() {
    return flameParam1;
  }

  public void setFlameParam1(String pFlameParam1) {
    flameParam1 = pFlameParam1;
  }

  public String getFlameParamMap2Filename() {
    return flameParamMap2Filename;
  }

  public void setFlameParamMap2Filename(String pFlameParamMap2Filename) {
    flameParamMap2Filename = pFlameParamMap2Filename;
  }

  public double getFlameParam2Min() {
    return flameParam2Min;
  }

  public void setFlameParam2Min(double pFlameParam2Min) {
    flameParam2Min = pFlameParam2Min;
  }

  public double getFlameParam2Max() {
    return flameParam2Max;
  }

  public void setFlameParam2Max(double pFlameParam2Max) {
    flameParam2Max = pFlameParam2Max;
  }

  public String getFlameParam2() {
    return flameParam2;
  }

  public void setFlameParam2(String pFlameParam2) {
    flameParam2 = pFlameParam2;
  }

  public String getFlameParamMap3Filename() {
    return flameParamMap3Filename;
  }

  public void setFlameParamMap3Filename(String pFlameParamMap3Filename) {
    flameParamMap3Filename = pFlameParamMap3Filename;
  }

  public double getFlameParam3Min() {
    return flameParam3Min;
  }

  public void setFlameParam3Min(double pFlameParam3Min) {
    flameParam3Min = pFlameParam3Min;
  }

  public double getFlameParam3Max() {
    return flameParam3Max;
  }

  public void setFlameParam3Max(double pFlameParam3Max) {
    flameParam3Max = pFlameParam3Max;
  }

  public String getFlameParam3() {
    return flameParam3;
  }

  public void setFlameParam3(String pFlameParam3) {
    flameParam3 = pFlameParam3;
  }

  public int getPreviewR() {
    return previewR;
  }

  public void setPreviewR(int pPreviewR) {
    previewR = pPreviewR;
  }

  public int getPreviewG() {
    return previewG;
  }

  public void setPreviewG(int pPreviewG) {
    previewG = pPreviewG;
  }

  public int getPreviewB() {
    return previewB;
  }

  public void setPreviewB(int pPreviewB) {
    previewB = pPreviewB;
  }

  public double getRotateAlphaVar() {
    return rotateAlphaVar;
  }

  public void setRotateAlphaVar(double pRotateAlphaVar) {
    rotateAlphaVar = pRotateAlphaVar;
  }

  public double getRotateBeta() {
    return rotateBeta;
  }

  public void setRotateBeta(double pRotateBeta) {
    rotateBeta = pRotateBeta;
  }

  public double getRotateBetaVar() {
    return rotateBetaVar;
  }

  public void setRotateBetaVar(double pRotateBetaVar) {
    rotateBetaVar = pRotateBetaVar;
  }

  public double getRotateAlpha() {
    return rotateAlpha;
  }

  public void setRotateAlpha(double pRotateAlpha) {
    rotateAlpha = pRotateAlpha;
  }

  public double getRotateGamma() {
    return rotateGamma;
  }

  public void setRotateGamma(double pRotateGamma) {
    rotateGamma = pRotateGamma;
  }

  public double getRotateGammaVar() {
    return rotateGammaVar;
  }

  public void setRotateGammaVar(double pRotateGammaVar) {
    rotateGammaVar = pRotateGammaVar;
  }

  public double getCentreZ() {
    return centreZ;
  }

  public void setCentreZ(double pCentreZ) {
    centreZ = pCentreZ;
  }

  public int getGridXOffset() {
    return gridXOffset;
  }

  public void setGridXOffset(int pGridXOffset) {
    gridXOffset = pGridXOffset;
  }

  public int getGridYOffset() {
    return gridYOffset;
  }

  public void setGridYOffset(int pGridYOffset) {
    gridYOffset = pGridYOffset;
  }

  public int getGridXSize() {
    return gridXSize;
  }

  public void setGridXSize(int pGridXSize) {
    gridXSize = pGridXSize;
  }

  public int getGridYSize() {
    return gridYSize;
  }

  public void setGridYSize(int pGridYSize) {
    gridYSize = pGridYSize;
  }

  public double getRotateAlphaSpeed() {
    return rotateAlphaSpeed;
  }

  public void setRotateAlphaSpeed(double pRotateAlphaSpeed) {
    rotateAlphaSpeed = pRotateAlphaSpeed;
  }

  public double getRotateBetaSpeed() {
    return rotateBetaSpeed;
  }

  public void setRotateBetaSpeed(double pRotateBetaSpeed) {
    rotateBetaSpeed = pRotateBetaSpeed;
  }

  public double getRotateGammaSpeed() {
    return rotateGammaSpeed;
  }

  public void setRotateGammaSpeed(double pRotateGammaSpeed) {
    rotateGammaSpeed = pRotateGammaSpeed;
  }

  public double getSpeedX() {
    return speedX;
  }

  public void setSpeedX(double pSpeedX) {
    speedX = pSpeedX;
  }

  public double getSpeedXVar() {
    return speedXVar;
  }

  public void setSpeedXVar(double pSpeedXVar) {
    speedXVar = pSpeedXVar;
  }

  public double getSpeedY() {
    return speedY;
  }

  public void setSpeedY(double pSpeedY) {
    speedY = pSpeedY;
  }

  public double getSpeedYVar() {
    return speedYVar;
  }

  public void setSpeedYVar(double pSpeedYVar) {
    speedYVar = pSpeedYVar;
  }

  public double getSpeedZ() {
    return speedZ;
  }

  public void setSpeedZ(double pSpeedZ) {
    speedZ = pSpeedZ;
  }

  public double getSpeedZVar() {
    return speedZVar;
  }

  public void setSpeedZVar(double pSpeedZVar) {
    speedZVar = pSpeedZVar;
  }

  public double getRotateAlphaSpeedVar() {
    return rotateAlphaSpeedVar;
  }

  public void setRotateAlphaSpeedVar(double pRotateAlphaSpeedVar) {
    rotateAlphaSpeedVar = pRotateAlphaSpeedVar;
  }

  public double getRotateBetaSpeedVar() {
    return rotateBetaSpeedVar;
  }

  public void setRotateBetaSpeedVar(double pRotateBetaSpeedVar) {
    rotateBetaSpeedVar = pRotateBetaSpeedVar;
  }

  public double getRotateGammaSpeedVar() {
    return rotateGammaSpeedVar;
  }

  public void setRotateGammaSpeedVar(double pRotateGammaSpeedVar) {
    rotateGammaSpeedVar = pRotateGammaSpeedVar;
  }

  public double getRadialAcceleration() {
    return radialAcceleration;
  }

  public void setRadialAcceleration(double pRadialAcceleration) {
    radialAcceleration = pRadialAcceleration;
  }

  public double getRadialAccelerationVar() {
    return radialAccelerationVar;
  }

  public void setRadialAccelerationVar(double pRadialAccelerationVar) {
    radialAccelerationVar = pRadialAccelerationVar;
  }

  public double getTangentialAcceleration() {
    return tangentialAcceleration;
  }

  public void setTangentialAcceleration(double pTangentialAcceleration) {
    tangentialAcceleration = pTangentialAcceleration;
  }

  public double getTangentialAccelerationVar() {
    return tangentialAccelerationVar;
  }

  public void setTangentialAccelerationVar(double pTangentialAccelerationVar) {
    tangentialAccelerationVar = pTangentialAccelerationVar;
  }

  public double getBrightnessMin() {
    return brightnessMin;
  }

  public void setBrightnessMin(double pBrightnessMin) {
    brightnessMin = pBrightnessMin;
  }

  public double getBrightnessMax() {
    return brightnessMax;
  }

  public void setBrightnessMax(double pBrightnessMax) {
    brightnessMax = pBrightnessMax;
  }

  public double getBrightnessChange() {
    return brightnessChange;
  }

  public void setBrightnessChange(double pBrightnessChange) {
    brightnessChange = pBrightnessChange;
  }

  public boolean isInstancing() {
    return instancing;
  }

  public void setInstancing(boolean pInstancing) {
    instancing = pInstancing;
  }

}
