/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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
package org.jwildfire.create.tina.variation.plot;

public class ParPlot2DWFFuncPreset extends WFFuncPreset {
	private final String xformula;
	private final String yformula;
	private final String zformula;
	private final double umin, umax;
	private final double vmin, vmax;
	private final double param_a, param_b, param_c, param_d, param_e, param_f, param_g, param_h, param_i, param_j,
			param_k, param_l, param_m, param_n, param_o, param_p, param_q, param_r;

	public ParPlot2DWFFuncPreset(int id, String xformula, String yformula, String zformula, double umin, double umax,
			double vmin, double vmax, double param_a, double param_b, double param_c, double param_d, double param_e,
			double param_f, double param_g, double param_h, double param_i, double param_j, double param_k,
			double param_l, double param_m, double param_n, double param_o, double param_p, double param_q,
			double param_r) {
		super(id);
		this.xformula = xformula;
		this.yformula = yformula;
		this.zformula = zformula;
		this.umin = umin;
		this.umax = umax;
		this.vmin = vmin;
		this.vmax = vmax;
		this.param_a = param_a;
		this.param_b = param_b;
		this.param_c = param_c;
		this.param_d = param_d;
		this.param_e = param_e;
		this.param_f = param_f;
		this.param_g = param_g;
		this.param_h = param_h;
		this.param_i = param_i;
		this.param_j = param_j;
		this.param_k = param_k;
		this.param_l = param_l;
		this.param_m = param_m;
		this.param_n = param_n;
		this.param_o = param_o;
		this.param_p = param_p;
		this.param_q = param_q;
		this.param_r = param_r;
	}

	public String getXformula() {
		return xformula;
	}

	public String getYformula() {
		return yformula;
	}

	public String getZformula() {
		return zformula;
	}

	public double getUmin() {
		return umin;
	}

	public double getUmax() {
		return umax;
	}

	public double getVmin() {
		return vmin;
	}

	public double getVmax() {
		return vmax;
	}

	public int getId() {
		return id;
	}

	public double getParam_a() {
		return param_a;
	}

	public double getParam_b() {
		return param_b;
	}

	public double getParam_c() {
		return param_c;
	}

	public double getParam_d() {
		return param_d;
	}

	public double getParam_e() {
		return param_e;
	}

	public double getParam_f() {
		return param_f;
	}

	public double getParam_g() {
		return param_g;
	}

	public double getParam_h() {
		return param_h;
	}

	public double getParam_i() {
		return param_i;
	}

	public double getParam_j() {
		return param_j;
	}

	public double getParam_k() {
		return param_k;
	}

	public double getParam_l() {
		return param_l;
	}

	public double getParam_m() {
		return param_m;
	}

	public double getParam_n() {
		return param_n;
	}

	public double getParam_o() {
		return param_o;
	}

	public double getParam_p() {
		return param_p;
	}

	public double getParam_q() {
		return param_q;
	}

	public double getParam_r() {
		return param_r;
	}

}
