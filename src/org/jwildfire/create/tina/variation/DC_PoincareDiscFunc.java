package org.jwildfire.create.tina.variation;

import java.util.Random;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

import js.glsl.G;
import js.glsl.mat2;
import js.glsl.mat3;
import js.glsl.vec2;
import js.glsl.vec3;



public class DC_PoincareDiscFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_poincaredisc
	 * Autor: Jesus Sosa
	 * Date: February 12, 2019
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_MAXITER = "MaxIter";
	private static final String PARAM_N = "n";
	private static final String PARAM_P = "p";
	
	private static final String PARAM_CHECKER0 = "checkers0";
	private static final String PARAM_CHECKER1 = "checkers1";
	private static final String PARAM_CHECKER2 = "checkers2";
	
	private double zoom = 2.0;
	
	int maxiter=10;
	int n=4;
	int p=5;
vec2 mba=new vec2(1.,0.);
vec2 mbb=new vec2(0.,0.);

int checkers0=1;
int checkers1=1;
int checkers2=1;

	double sx;
	double sr;
	vec2 nfp=new vec2(0.0);
	vec2 pcenter=new vec2(0.0);
	vec2 pscale=new vec2(1.0);
	double angle=29.9664;
	
	private static final String[] additionalParamNames = { PARAM_ZOOM,PARAM_MAXITER,
			PARAM_N,PARAM_P,PARAM_CHECKER0,PARAM_CHECKER1,PARAM_CHECKER2};


	void init() {
		double a=Math.PI/(double)(n);
		double b=Math.PI/(double)(p);
		//we should have: 1/n+1/p<1/2
		//otherwise the result is undefined
		if(a+b>=0.5*Math.PI) b=0.5*Math.PI-a-0.01;
		double ca=Math.cos(a), sa=Math.sin(a);
		double sb=Math.sin(b);
		double c1=Math.cos(a+b);
		double s=c1/Math.sqrt(1.-sa*sa-sb*sb);
		sx=(s*s+1.)/(2.*s*ca);
		sr=Math.sqrt(sx*sx-1.);
		nfp=new vec2(sa,-ca);
	}
	
	vec2 mbtpc(vec2 z){//Möbius Transform Peserving (unit) Circle
		vec2 zn=new vec2(z.x*mba.x-z.y*mba.y+mbb.x,
						z.x*mba.y+z.y*mba.x+mbb.y);
		vec2 zd=new vec2(z.x*mbb.x+z.y*mbb.y+mba.x,
						-z.x*mbb.y+z.y*mbb.x-mba.y);
		double idn2=1./G.dot(zd,zd);
		z=new vec2(G.dot(zn,zd),G.dot(zn,new vec2(zd.y,zd.x).multiply(new vec2(-1.,1.)))).multiply(idn2);
		return z;
	}
	
	public double radians(double angle)
	{
		return Math.PI*angle/180.0;
	}
	
	public vec3 getRGBColor(double xp,double yp)
	{
		vec2 z=new vec2(xp,yp).multiply(zoom);
		
		if(G.dot(z,z)>1.) 
			return new vec3(0.);//if you don't want to see the patternes outside the unit disc
		z=mbtpc(z);
		vec2 az=z.plus(1.);
		vec3 fc=new vec3(0.);//captures the number of folds performed
		for(int i=0; i<maxiter && az!=z; i++){
			az=z;
			//first fold
			z.y=G.abs(z.y);
			fc.x+=(az.y!=z.y?1.d:0.d);
			//second fold
			double t=2.*G.min(0.,G.dot(z,nfp));
			fc.y+=(t<0.?1.d:0.d);
			z=z.minus(nfp.multiply(t));
			//third fold
			z.x-=sx;
			double r2=G.dot(z,z);
			double k=G.max(sr*sr/r2,1.);
			fc.z+=(k!=1.?1.d:0.d);
			z=z.multiply(k);
			z.x+=sx;
		}
		
		double r=G.abs(G.length(new vec2(z.x-sx,z.y))-sr);
		r=Math.pow(r,0.25);
		double k=G.mod((checkers0==1?1:0)*fc.x+(checkers1==1?1:0)*fc.y+(checkers2==1?1:0)*fc.z,2.);
		vec3 col1=new	vec3(r).multiply(0.5*k+0.5);

		vec2 ptex=z;
		ptex.y*=k*2.-1.;
		ptex=ptex.multiply(pscale);
		ptex=new mat2(Math.cos(radians(angle)),Math.sin(radians(angle)),-Math.sin(radians(angle)),Math.cos(radians(angle))).times(ptex);
		ptex=ptex.minus(pcenter);
		ptex=ptex.multiply(0.5).plus(0.5);
		//vec3 col= texture2DLod(texture,ptex,0).xyz;
		return col1;
		//return mix(col,col1,fade);
	}

 	
	@Override
	public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) 
	{
      init();
	}

	public String getName() {
		return "dc_poincaredisc";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}

	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { zoom,maxiter,n,p,checkers0,checkers1,checkers2},super.getParameterValues());
	}


	public void setParameter(String pName, double pValue) {
		if (PARAM_ZOOM.equalsIgnoreCase(pName)) 
		{	   zoom =   pValue;

	    }
		else if (pName.equalsIgnoreCase(PARAM_MAXITER)) {
			maxiter = (int)Tools.limitValue(pValue, 0 , 20);
		}
		else if (pName.equalsIgnoreCase(PARAM_N)) {
			n = (int)Tools.limitValue(pValue, 3 , 20);
		}
		else if (pName.equalsIgnoreCase(PARAM_P)) {
			p = (int)Tools.limitValue(pValue, 3 , 20);
		}
		else if (pName.equalsIgnoreCase(PARAM_CHECKER0)) {
			checkers0 = (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_CHECKER1)) {
			checkers1 = (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_CHECKER2)) {
			checkers2 = (int)Tools.limitValue(pValue, 0 , 1);
		}
		else
			super.setParameter(pName, pValue);
	}

	@Override
	public boolean dynamicParameterExpansion() {
		return true;
	}

	@Override
	public boolean dynamicParameterExpansion(String pName) {
		// preset_id doesn't really expand parameters, but it changes them; this will make them refresh
		return true;
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE};
	}

}

