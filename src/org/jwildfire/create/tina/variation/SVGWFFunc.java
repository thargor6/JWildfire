/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

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
package org.jwildfire.create.tina.variation;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGUniverse;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;

import java.awt.*;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.jwildfire.base.mathlib.MathLib.*;

public class SVGWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALEX = "scale_x";
  private static final String PARAM_SCALEY = "scale_y";
  private static final String PARAM_OFFSETX = "offset_x";
  private static final String PARAM_OFFSETY = "offset_y";
  private static final String PARAM_RESOLUTION_MULTIPLIER = "resolution_multiplier";
  private static final String PARAM_PRE_ANTIALIAS = "pre_antialias";
  private static final String PARAM_ANTIALIAS_RADIUS = "antialias_radius";
  private static final String PARAM_TRUE_COLOR = "true_color";

  private static final String RESSOURCE_SVG = "svg";

  private static final String[] paramNames = {PARAM_ANTIALIAS_RADIUS, PARAM_RESOLUTION_MULTIPLIER, PARAM_TRUE_COLOR, PARAM_PRE_ANTIALIAS, PARAM_SCALEX, PARAM_SCALEY, PARAM_OFFSETX, PARAM_OFFSETY};
  private static final String[] ressourceNames = {RESSOURCE_SVG};

  private double antialias_radius = 0.5;
  private double resolution_multiplier = 2.0;
  private double scale_x = 1.0;
  private double scale_y = 1.0;
  private double offset_x = 0.0;
  private double offset_y = 0.0;
  private int true_color = 1;
  private int pre_antialias = 1;
  private String svg = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n"
          +
          "<!-- Created with Inkscape (http://www.inkscape.org/) -->\r\n"
          +
          "\r\n"
          +
          "<svg\r\n"
          +
          "   xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\r\n"
          +
          "   xmlns:cc=\"http://creativecommons.org/ns#\"\r\n"
          +
          "   xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\r\n"
          +
          "   xmlns:svg=\"http://www.w3.org/2000/svg\"\r\n"
          +
          "   xmlns=\"http://www.w3.org/2000/svg\"\r\n"
          +
          "   xmlns:sodipodi=\"http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd\"\r\n"
          +
          "   xmlns:inkscape=\"http://www.inkscape.org/namespaces/inkscape\"\r\n"
          +
          "   width=\"744.09448819\"\r\n"
          +
          "   height=\"1052.3622047\"\r\n"
          +
          "   id=\"svg2\"\r\n"
          +
          "   version=\"1.1\"\r\n"
          +
          "   inkscape:version=\"0.48.2 r9819\"\r\n"
          +
          "   sodipodi:docname=\"drawing-1.svg\">\r\n"
          +
          "  <defs\r\n"
          +
          "     id=\"defs4\">\r\n"
          +
          "    <inkscape:perspective\r\n"
          +
          "       sodipodi:type=\"inkscape:persp3d\"\r\n"
          +
          "       inkscape:vp_x=\"0 : 52.18109 : 1\"\r\n"
          +
          "       inkscape:vp_y=\"0 : 100 : 0\"\r\n"
          +
          "       inkscape:vp_z=\"74.09448 : 52.18109 : 1\"\r\n"
          +
          "       inkscape:persp3d-origin=\"372.04724 : 350.78739 : 1\"\r\n"
          +
          "       id=\"perspective2987\" />\r\n"
          +
          "  </defs>\r\n"
          +
          "  <sodipodi:namedview\r\n"
          +
          "     id=\"base\"\r\n"
          +
          "     pagecolor=\"#ffffff\"\r\n"
          +
          "     bordercolor=\"#666666\"\r\n"
          +
          "     borderopacity=\"1.0\"\r\n"
          +
          "     inkscape:pageopacity=\"0.0\"\r\n"
          +
          "     inkscape:pageshadow=\"2\"\r\n"
          +
          "     inkscape:zoom=\"1.01\"\r\n"
          +
          "     inkscape:cx=\"375\"\r\n"
          +
          "     inkscape:cy=\"520\"\r\n"
          +
          "     inkscape:document-units=\"px\"\r\n"
          +
          "     inkscape:current-layer=\"layer1\"\r\n"
          +
          "     showgrid=\"false\"\r\n"
          +
          "     inkscape:object-paths=\"true\"\r\n"
          +
          "     inkscape:window-width=\"1443\"\r\n"
          +
          "     inkscape:window-height=\"752\"\r\n"
          +
          "     inkscape:window-x=\"150\"\r\n"
          +
          "     inkscape:window-y=\"150\"\r\n"
          +
          "     inkscape:window-maximized=\"0\"\r\n"
          +
          "     inkscape:snap-bbox=\"true\"\r\n"
          +
          "     inkscape:snap-midpoints=\"true\"\r\n"
          +
          "     inkscape:snap-page=\"true\" />\r\n"
          +
          "  <metadata\r\n"
          +
          "     id=\"metadata7\">\r\n"
          +
          "    <rdf:RDF>\r\n"
          +
          "      <cc:Work\r\n"
          +
          "         rdf:about=\"\">\r\n"
          +
          "        <dc:format>image/svg+xml</dc:format>\r\n"
          +
          "        <dc:type\r\n"
          +
          "           rdf:resource=\"http://purl.org/dc/dcmitype/StillImage\" />\r\n"
          +
          "      </cc:Work>\r\n"
          +
          "    </rdf:RDF>\r\n"
          +
          "  </metadata>\r\n"
          +
          "  <g\r\n"
          +
          "     inkscape:label=\"Layer 1\"\r\n"
          +
          "     inkscape:groupmode=\"layer\"\r\n"
          +
          "     id=\"layer1\">\r\n"
          +
          "    <path\r\n"
          +
          "       style=\"fill:none;stroke:#000000;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1\"\r\n"
          +
          "       d=\"m 127.72277,485.03545 c 31.90334,-18.92185 63.46475,-48.29786 100.9901,-55.44554 21.75599,25.44064 16.13838,57.96746 39.60396,80.19802\"\r\n"
          +
          "       id=\"path3017\"\r\n"
          +
          "       inkscape:connector-curvature=\"0\" />\r\n"
          +
          "    <path\r\n"
          +
          "       style=\"fill:none;stroke:#000000;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1\"\r\n"
          +
          "       d=\"m 127.72277,485.03545 c 27.40577,12.60743 72.69091,20.82793 91.08911,28.71287 -29.68845,20.50926 -71.04375,23.76238 -99.0099,23.76238 62.2892,24.4275 116.71191,41.88104 145.54455,-7.92079 33.52518,62.25451 -11.68309,144.01327 -7.92079,147.52475 28.77833,-34.01701 31.87688,-78.4653 35.64357,-121.78218 31.45011,21.19881 22.7596,69.38298 16.83168,104.95049 22.50757,-46.98595 52.65168,-86.60691 16.83168,-130.69306 52.80883,8.51464 97.39876,98.58484 48.51485,125.74257 49.48087,-26.93421 22.7019,-71.65663 14.85149,-112.87129 58.32891,8.25915 61.24665,56.12934 57.42574,101.9802 27.7207,-43.16363 -0.11028,-90.13658 -12.87128,-132.67327 19.59577,37.42338 34.68607,61.69074 55.44554,98.01981 -5.86952,-35.46895 4.35123,-77.40751 -34.65347,-89.10892 82.02245,-30.00642 188.8403,-60.02123 274.25743,-74.25742 -152.49819,15.97231 -313.88105,29.84243 -461.38614,63.36634\"\r\n"
          +
          "       id=\"path3019\"\r\n"
          +
          "       inkscape:connector-curvature=\"0\" />\r\n"
          +
          "    <path\r\n"
          +
          "       sodipodi:type=\"spiral\"\r\n"
          +
          "       style=\"fill:none;stroke:#000000;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1\"\r\n"
          +
          "       id=\"path3025\"\r\n"
          +
          "       sodipodi:cx=\"204.9505\"\r\n"
          +
          "       sodipodi:cy=\"475.13446\"\r\n"
          +
          "       sodipodi:expansion=\"1\"\r\n"
          +
          "       sodipodi:revolution=\"5\"\r\n"
          +
          "       sodipodi:radius=\"16.238831\"\r\n"
          +
          "       sodipodi:argument=\"-30.760231\"\r\n"
          +
          "       sodipodi:t0=\"0\"\r\n"
          +
          "       d=\"m 204.9505,475.13446 c 0.38721,0.29785 -0.21046,0.6689 -0.49505,0.64356 -0.77121,-0.0687 -1.01692,-1.01295 -0.79208,-1.63366 0.40218,-1.1103 1.78298,-1.41433 2.77228,-0.94059 1.45184,0.69522 1.82182,2.56282 1.08911,3.91089 -0.9766,1.79676 -3.34609,2.23352 -5.04951,1.23762 -2.14357,-1.25323 -2.64736,-4.13096 -1.38614,-6.18812 1.52751,-2.49149 4.91673,-3.06244 7.32674,-1.53465 2.8401,1.80045 3.4783,5.70306 1.68317,8.46535 -2.07256,3.18917 -6.48977,3.89468 -9.60396,1.83168 -3.53855,-2.34411 -4.31143,-7.27673 -1.9802,-10.74257 2.61528,-3.88816 8.06389,-4.72844 11.88118,-2.12872 4.23793,2.88618 5.14564,8.8512 2.27723,13.0198 -3.15687,4.58783 -9.63861,5.563 -14.15841,2.42575 -4.93782,-3.42741 -5.98048,-10.42612 -2.57426,-15.29703 3.69781,-5.2879 11.21369,-6.39805 16.43564,-2.72278 5.63804,3.96812 6.8157,12.00133 2.87129,17.57426 -4.23834,5.98823 -12.789,7.23341 -18.71287,3.01981 -6.33847,-4.50851 -7.65117,-13.57673 -3.16832,-19.85149 4.77861,-6.68874 14.36448,-8.06898 20.9901,-3.31683 7.03904,5.04867 8.48683,15.15226 3.46535,22.12871\"\r\n"
          +
          "       transform=\"translate(4.950495,0)\" />\r\n"
          +
          "    <path\r\n"
          +
          "       style=\"fill:#800080;stroke:none\"\r\n"
          +
          "       d=\"m 259.7745,671.52373 c -0.0121,-0.29779 1.77946,-5.86709 3.98127,-12.37624 17.90134,-52.92115 19.47647,-92.83961 4.92438,-124.79897 -2.66807,-5.85964 -3.28148,-6.60301 -4.2879,-5.19639 -7.91178,11.05784 -10.41104,14.0805 -14.36895,17.37813 -6.6834,5.56845 -10.55999,7.71557 -19.47875,10.78862 -7.1563,2.46579 -9.21085,2.72511 -21.28712,2.68695 -19.52734,-0.0617 -38.59802,-4.51269 -70.62899,-16.48429 l -13.6983,-5.11975 13.32065,-0.63117 c 19.83444,-0.93981 37.86643,-4.24096 53.73152,-9.83671 13.80316,-4.8685 29.24786,-13.15091 27.53741,-14.76729 -1.26158,-1.19218 -16.33493,-5.84504 -45.90586,-14.17027 -14.43069,-4.06273 -30.11005,-8.88823 -34.84301,-10.72335 l -8.60538,-3.33656 25.2744,-16.83168 c 13.90092,-9.25743 29.3575,-19.18049 34.34796,-22.05125 9.27209,-5.33377 25.15646,-12.01966 33.57323,-14.1313 4.44237,-1.11453 4.80439,-1.05119 6.66088,1.16535 4.10823,4.90494 10.88635,19.71727 15.34427,33.53205 7.91773,24.53645 11.54719,32.23102 19.00914,40.29999 l 3.79297,4.10153 25.24752,-5.39621 c 60.93776,-13.02437 140.15376,-25.51264 228.21782,-35.97816 49.70987,-5.90752 163.71048,-17.94296 168.50195,-17.78934 l 2.78518,0.0893 -2.97029,0.80131 c -1.63367,0.44073 -10.54456,2.43115 -19.80199,4.42316 -38.45101,8.27389 -94.05431,23.11307 -140.59405,37.52116 -22.47655,6.95845 -71.36253,23.51517 -74.32215,25.17146 -1.61102,0.90158 -1.38602,1.19905 1.96646,2.5998 2.0821,0.86996 3.98987,1.58174 4.23951,1.58174 0.24962,0 2.85422,1.59327 5.78801,3.5406 3.37896,2.24283 6.80072,5.73684 9.3353,9.5324 6.30269,9.43835 8.26043,18.00156 9.91365,43.36264 0.55023,8.4406 1.24858,17.35149 1.55191,19.80198 0.54067,4.36801 0.43148,4.25144 -5.56436,-5.94059 -11.28784,-19.18761 -33.24238,-57.80367 -40.10699,-70.54455 -6.3928,-11.86521 -8.3459,-14.49874 -8.3459,-11.25349 0,0.75366 3.76341,12.04762 8.36312,25.09769 9.8071,27.82419 13.9566,43.07393 15.30389,56.24291 1.56345,15.28188 -1.04154,31.12073 -6.90571,41.98807 l -1.89882,3.51888 0.60016,-10.89109 c 0.71246,-12.92882 -0.88185,-30.78412 -3.66487,-41.04449 -6.70107,-24.70534 -26.19073,-41.76446 -51.64925,-45.20814 -4.00419,-0.54162 -4.70297,-0.38785 -4.70297,1.03492 0,0.9191 2.96187,13.11759 6.58193,27.10776 6.32531,24.44495 6.58588,25.9192 6.68316,37.81292 0.0845,10.32808 -0.24203,13.28141 -1.97302,17.84587 -2.06582,5.4474 -7.15705,14.34195 -7.82627,13.67274 -0.19341,-0.19341 0.47799,-2.79739 1.49198,-5.78661 8.48133,-25.00274 -3.8623,-59.9723 -30.15348,-85.42491 -5.44816,-5.2744 -16.03273,-12.55006 -21.93908,-15.08058 -4.48822,-1.92293 -16.54734,-5.43878 -17.02305,-4.96308 -0.1787,0.1787 1.7747,3.37818 4.34088,7.10995 9.6284,14.0017 12.7554,24.5587 11.98088,40.4484 -0.52932,10.85903 -2.33075,18.90281 -6.76292,30.19802 -3.06826,7.81932 -21.62517,45.88806 -22.12491,45.38832 -0.2308,-0.23081 0.31412,-4.85691 1.21095,-10.28022 2.45258,-14.83135 2.39384,-49.34462 -0.0977,-57.38533 -3.16873,-10.22626 -8.31497,-18.87809 -14.80058,-24.88261 -7.22075,-6.68515 -6.84782,-7.21034 -8.52928,12.01132 -3.63966,41.60701 -10.5748,68.85165 -22.72723,89.28374 -3.93742,6.62007 -7.9696,12.23814 -8.01323,11.16494 l 4e-5,0 z M 216.88262,487.45177 c 4.70973,-2.87169 6.57895,-7.20624 5.96672,-13.83625 l -0.50889,-5.51076 1.45462,3.39549 c 1.42763,3.3325 1.09171,6.75339 -1.21112,12.33367 -0.56691,1.37374 -0.40456,1.7457 0.61376,1.40625 0.76151,-0.25383 1.96779,-2.52654 2.68065,-5.05045 4.1868,-14.8237 -11.95208,-26.50793 -24.89784,-18.02554 -5.92045,3.87922 -7.88812,12.50757 -4.40291,19.30691 3.82127,7.45497 13.30557,10.24849 20.30501,5.98068 z\"\r\n"
          +
          "       id=\"path3039\"\r\n" +
          "       inkscape:connector-curvature=\"0\" />\r\n" +
          "  </g>\r\n" +
          "</svg>\r\n" +
          "";

  //  private String svg = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n"
  //      +
  //      "<!-- Created with Inkscape (http://www.inkscape.org/) -->\r\n"
  //      +
  //      "\r\n"
  //      +
  //      "<svg\r\n"
  //      +
  //      "   xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\r\n"
  //      +
  //      "   xmlns:cc=\"http://creativecommons.org/ns#\"\r\n"
  //      +
  //      "   xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\r\n"
  //      +
  //      "   xmlns:svg=\"http://www.w3.org/2000/svg\"\r\n"
  //      +
  //      "   xmlns=\"http://www.w3.org/2000/svg\"\r\n"
  //      +
  //      "   xmlns:sodipodi=\"http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd\"\r\n"
  //      +
  //      "   xmlns:inkscape=\"http://www.inkscape.org/namespaces/inkscape\"\r\n"
  //      +
  //      "   width=\"744.09448819\"\r\n"
  //      +
  //      "   height=\"1052.3622047\"\r\n"
  //      +
  //      "   id=\"svg3073\"\r\n"
  //      +
  //      "   version=\"1.1\"\r\n"
  //      +
  //      "   inkscape:version=\"0.48.2 r9819\"\r\n"
  //      +
  //      "   sodipodi:docname=\"New document 2\">\r\n"
  //      +
  //      "  <defs\r\n"
  //      +
  //      "     id=\"defs3075\" />\r\n"
  //      +
  //      "  <sodipodi:namedview\r\n"
  //      +
  //      "     id=\"base\"\r\n"
  //      +
  //      "     pagecolor=\"#ffffff\"\r\n"
  //      +
  //      "     bordercolor=\"#666666\"\r\n"
  //      +
  //      "     borderopacity=\"1.0\"\r\n"
  //      +
  //      "     inkscape:pageopacity=\"0.0\"\r\n"
  //      +
  //      "     inkscape:pageshadow=\"2\"\r\n"
  //      +
  //      "     inkscape:zoom=\"0.35\"\r\n"
  //      +
  //      "     inkscape:cx=\"-67.857143\"\r\n"
  //      +
  //      "     inkscape:cy=\"520\"\r\n"
  //      +
  //      "     inkscape:document-units=\"px\"\r\n"
  //      +
  //      "     inkscape:current-layer=\"layer1\"\r\n"
  //      +
  //      "     showgrid=\"false\"\r\n"
  //      +
  //      "     inkscape:window-width=\"1103\"\r\n"
  //      +
  //      "     inkscape:window-height=\"779\"\r\n"
  //      +
  //      "     inkscape:window-x=\"50\"\r\n"
  //      +
  //      "     inkscape:window-y=\"50\"\r\n"
  //      +
  //      "     inkscape:window-maximized=\"0\" />\r\n"
  //      +
  //      "  <metadata\r\n"
  //      +
  //      "     id=\"metadata3078\">\r\n"
  //      +
  //      "    <rdf:RDF>\r\n"
  //      +
  //      "      <cc:Work\r\n"
  //      +
  //      "         rdf:about=\"\">\r\n"
  //      +
  //      "        <dc:format>image/svg+xml</dc:format>\r\n"
  //      +
  //      "        <dc:type\r\n"
  //      +
  //      "           rdf:resource=\"http://purl.org/dc/dcmitype/StillImage\" />\r\n"
  //      +
  //      "        <dc:title></dc:title>\r\n"
  //      +
  //      "      </cc:Work>\r\n"
  //      +
  //      "    </rdf:RDF>\r\n"
  //      +
  //      "  </metadata>\r\n"
  //      +
  //      "  <g\r\n"
  //      +
  //      "     inkscape:label=\"Layer 1\"\r\n"
  //      +
  //      "     inkscape:groupmode=\"layer\"\r\n"
  //      +
  //      "     id=\"layer1\">\r\n"
  //      +
  //      "    <path\r\n"
  //      +
  //      "       sodipodi:type=\"spiral\"\r\n"
  //      +
  //      "       style=\"fill:none;stroke:#ca0000;stroke-width:16.5;stroke-linecap:round;stroke-linejoin:round;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none\"\r\n"
  //      +
  //      "       id=\"path3083\"\r\n"
  //      +
  //      "       sodipodi:cx=\"151.42857\"\r\n"
  //      +
  //      "       sodipodi:cy=\"240.93361\"\r\n"
  //      +
  //      "       sodipodi:expansion=\"1\"\r\n"
  //      +
  //      "       sodipodi:revolution=\"5\"\r\n"
  //      +
  //      "       sodipodi:radius=\"189.90868\"\r\n"
  //      +
  //      "       sodipodi:argument=\"-30.630527\"\r\n"
  //      +
  //      "       sodipodi:t0=\"0\"\r\n"
  //      +
  //      "       d=\"m 151.42857,240.93361 c 4.03972,4.03973 -3.45241,7.43852 -6.71429,6.71428 -8.83948,-1.96265 -10.26047,-13.28493 -6.71427,-20.14286 6.34334,-12.26725 22.81557,-13.70434 33.57144,-6.71426 15.78467,10.25822 17.25027,32.4755 6.71424,47.00001 -14.04286,19.35888 -42.18132,20.83969 -60.42857,6.71423 -22.962309,-17.77545 -24.451617,-51.90903 -6.71422,-73.85715 21.48235,-26.58212 61.64905,-28.07656 87.28571,-6.71421 30.21193,25.17479 31.70969,71.39676 6.7142,100.71429 -28.85828,33.84826 -81.14958,35.34827 -114.14286,6.71419 -37.489056,-32.53587 -38.990671,-90.90602 -6.714174,-127.57144 36.209354,-41.13305 100.665074,-42.63584 141.000004,-6.71416 44.77941,39.87987 46.28308,110.42612 6.71415,154.42858 -43.54818,48.42756 -120.18868,49.93192 -167.857146,6.71414 C 12.065671,281.00445 10.560779,198.2668 57.428657,146.93353 108.30875,91.205765 197.14583,89.700442 252.14295,140.21941 c 59.3793,54.54434 60.88497,149.48269 6.7141,208.14286 -58.20772,63.03157 -159.248846,64.53753 -221.571432,6.71409 -66.684436,-61.87039 -68.190635,-169.01554 -6.71408,-235 65.532497,-70.337805 178.782712,-71.844205 248.428572,-6.71407 73.9916,69.19411 75.49818,188.55026 6.71406,261.85715\"\r\n"
  //      +
  //      "       transform=\"translate(222.85714,262.85715)\" />\r\n" +
  //      "  </g>\r\n" +
  //      "</svg>\r\n" +
  //      "";

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{antialias_radius, resolution_multiplier, true_color, pre_antialias, scale_x, scale_y, offset_x, offset_y};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_ANTIALIAS_RADIUS.equalsIgnoreCase(pName))
      antialias_radius = pValue;
    else if (PARAM_RESOLUTION_MULTIPLIER.equalsIgnoreCase(pName)) {
      resolution_multiplier = pValue;
      if (resolution_multiplier < 0.1) {
        resolution_multiplier = 0.1;
      } else if (resolution_multiplier > 100.0) {
        resolution_multiplier = 100.0;
      }
    } else if (PARAM_SCALEX.equalsIgnoreCase(pName))
      scale_x = pValue;
    else if (PARAM_SCALEY.equalsIgnoreCase(pName))
      scale_y = pValue;
    else if (PARAM_OFFSETX.equalsIgnoreCase(pName))
      offset_x = pValue;
    else if (PARAM_OFFSETY.equalsIgnoreCase(pName))
      offset_y = pValue;
    else if (PARAM_TRUE_COLOR.equalsIgnoreCase(pName))
      true_color = Tools.FTOI(pValue);
    else if (PARAM_PRE_ANTIALIAS.equalsIgnoreCase(pName))
      pre_antialias = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  private static class Point {
    private final double x, y;
    private final int r, g, b;

    public Point(double pX, double pY, int pR, int pG, int pB) {
      x = pX;
      y = pY;
      r = pR;
      g = pG;
      b = pB;
    }

  }

  private List<Point> _points;
  private RenderColor[] renderColors;
  private boolean previewMode = false;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    // renderColors = pContext.getFlameRenderer().getColorMap();
    // TODO optimize
    renderColors = pLayer.getPalette().createRenderPalette(pContext.getFlameRenderer().getFlame().getWhiteLevel());
    lastR = lastG = lastB = -1;
    previewMode = pContext.isPreview();
    if (previewMode) {
      resolution_multiplier = 0.25;
      if (scale_x > 1.0) {
        scale_x = 1.0;
      }
      if (scale_y > 1.0) {
        scale_y = 1.0;
      }
      true_color = 1;
      pre_antialias = 0;
    }
  }

  private String makeRessourceKey() {
    return getName() + "#" + resolution_multiplier + "#" + pre_antialias + "#" + svg;
  }

  @SuppressWarnings("unchecked")
  private List<Point> getPoints() {
    if (_points == null) {
      String key = makeRessourceKey();
      _points = (List<Point>) RessourceManager.getRessource(key);
      if (_points == null) {
        try {
          SVGUniverse svgUniverse = new SVGUniverse();
          StringReader reader = new StringReader(svg);
          SVGDiagram diagram = svgUniverse.getDiagram(svgUniverse.loadSVG(reader, "svgImage"));

          int imgWidth = Tools.FTOI(diagram.getWidth() * resolution_multiplier);
          int imgHeight = Tools.FTOI(diagram.getHeight() * resolution_multiplier);

          //          SVGRoot root = diagram.getRoot();
          //          root.setAttribute("width", AnimationElement.AT_XML, Integer.toString(imgWidth));
          //          root.setAttribute("height", AnimationElement.AT_XML, Integer.toString(imgHeight));
          //          root.build();

          SimpleImage imgMap = new SimpleImage(imgWidth, imgHeight);

          Graphics2D g = imgMap.getBufferedImg().createGraphics();
          g.scale(resolution_multiplier, resolution_multiplier);
          if (pre_antialias != 0) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
          } else {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
          }
          diagram.render(g);

          Pixel pixel = new Pixel();
          _points = new ArrayList<Point>();

          int xMin = imgMap.getImageWidth() - 1, xMax = 0;
          int yMin = imgMap.getImageHeight() - 1, yMax = 0;
          for (int i = 0; i < imgMap.getImageHeight(); i++) {
            for (int j = 0; j < imgMap.getImageWidth(); j++) {
              int argb = imgMap.getARGBValue(j, i);
              if (argb != 0) {
                if (j < xMin) {
                  xMin = j;
                } else if (j > xMax) {
                  xMax = j;
                }
                if (i < yMin) {
                  yMin = i;
                } else if (i > yMax) {
                  yMax = i;
                }
              }
            }
          }
          int xSize = xMax - xMin;
          int ySize = yMax - yMin;
          int maxSize = xSize > ySize ? xSize : ySize;

          if (maxSize > 0) {
            for (int i = 0; i < imgMap.getImageHeight(); i++) {
              for (int j = 0; j < imgMap.getImageWidth(); j++) {
                int argb = imgMap.getARGBValue(j, i);
                if (argb != 0) {
                  double x = ((j - xMin) - xSize / 2.0) / (double) maxSize;
                  double y = ((i - yMin) - ySize / 2.0) / (double) maxSize;
                  pixel.setARGBValue(argb);
                  _points.add(new Point(x, y, pixel.r, pixel.g, pixel.b));
                }
              }
            }
          }
        } catch (Exception ex) {
          ex.printStackTrace();
          _points = new ArrayList<Point>();
        }
        RessourceManager.putRessource(key, _points);
      }
    }
    return _points;
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][]{(svg != null ? svg.getBytes() : null)};
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_SVG.equalsIgnoreCase(pName)) {
      svg = pValue != null ? new String(pValue) : "";
      _points = null;
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_SVG.equalsIgnoreCase(pName)) {
      return RessourceType.SVG_FILE;
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    List<Point> points = getPoints();
    if (points.size() > 1) {
      Point point = points.get(pContext.random(points.size()));
      double rawX = point.x;
      double rawY = point.y;
      if (antialias_radius > 0.01) {
        double dr = (exp(antialias_radius * sqrt(-log(pContext.random()))) - 1.0) * 0.001;
        double da = pContext.random() * 2.0 * M_PI;
        rawX += dr * cos(da);
        rawY += dr * sin(da);
      }
      pVarTP.x += (rawX * scale_x + offset_x);
      pVarTP.y += (rawY * scale_y + offset_y);
      if (true_color == 1) {
        pVarTP.rgbColor = true;
        pVarTP.redColor = point.r;
        pVarTP.greenColor = point.g;
        pVarTP.blueColor = point.b;
      }
      pVarTP.color = getColorIdx(point.r, point.g, point.b);
    } else {
      pVarTP.x += pContext.random();
      pVarTP.y += pContext.random();
      pVarTP.rgbColor = true;
      pVarTP.redColor = 0;
      pVarTP.greenColor = 0;
      pVarTP.blueColor = 0;
    }
  }

  @Override
  public String getName() {
    return "svg_wf";
  }

  private double lastR, lastG, lastB;
  private double lastColorIdx;

  private double getColorIdx(double pR, double pG, double pB) {
    if (pR == lastR && pG == lastG && pB == lastB) {
      return lastColorIdx;
    }
    int nearestIdx = 0;
    RenderColor color = renderColors[0];
    double dr, dg, db;
    dr = (color.red - pR);
    dg = (color.green - pG);
    db = (color.blue - pB);
    double nearestDist = sqrt(dr * dr + dg * dg + db * db);
    for (int i = 1; i < renderColors.length; i++) {
      color = renderColors[i];
      dr = (color.red - pR);
      dg = (color.green - pG);
      db = (color.blue - pB);
      double dist = sqrt(dr * dr + dg * dg + db * db);
      if (dist < nearestDist) {
        nearestDist = dist;
        nearestIdx = i;
      }
    }
    lastColorIdx = (double) nearestIdx / (double) (renderColors.length - 1);
    lastR = pR;
    lastG = pG;
    lastB = pB;
    return lastColorIdx;
  }
}
