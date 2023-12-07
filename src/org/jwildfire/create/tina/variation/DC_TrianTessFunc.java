package org.jwildfire.create.tina.variation;


import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

public class DC_TrianTessFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_triantess
	 * Autor: Jesus Sosa
	 * Date: February 12, 2019
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_CENTERX = "CenterX";
	private static final String PARAM_CENTERY = "CenterY";
	private static final String PARAM_ITERATIONS = "Iterations";
	private static final String PARAM_PPRAM = "pParam";
	private static final String PARAM_QPARAM = "qParam";
	private static final String PARAM_RPARAM = "rParam";
	private static final String PARAM_U = "U";
	private static final String PARAM_V = "V";
	private static final String PARAM_W = "W";
	
	private static final String PARAM_VRADIUS = "VRadius";
	private static final String PARAM_SRADIUS = "SRadius";

	private static final String PARAM_ROTANGLE = "RotAngle";
	
	private static final String PARAM_DISPLAYV = "Vertex";
	private static final String PARAM_DISPLAYS = "Segments";
	private static final String PARAM_DISPLAYF = "Faces";
	private static final String PARAM_AASCALE = "aaScale";
	
	private double zoom = 0.50;
	private double centerX = 0.0;
	private double centerY = 0.0;
	int Iterations=13;
	int pParam=2;
	int qParam=3;
	int rParam=7;
	
	
	double U=1.0;
	double V=1.0;
	double W=0.0;
	
	double VRadius=0.0;
	double SRadius=0.02528;
	
	vec2	RotVector=new	vec2(0.0,1.0);
	double RotAngle=0.0;
	
	int DisplayVertices=1;
	int DisplaySegments=1;
	int DisplayFaces=1;
	
	double aascale=0.0005;
	
	vec3 nb,nc,p,q;
	vec3 pA,pB,pC;
	double tVR,tSR,cRA,sRA;
	//float qq,Aq,Bq,Cq;
	vec3 gA,gB,gC;
	double nbrFolds=0.;
	
	
	private static final String[] additionalParamNames = { PARAM_ZOOM,PARAM_CENTERX,PARAM_CENTERY,PARAM_ITERATIONS,
			PARAM_PPRAM,PARAM_QPARAM,PARAM_RPARAM,
			PARAM_U,PARAM_V,PARAM_W,
			PARAM_VRADIUS,PARAM_SRADIUS,PARAM_ROTANGLE,PARAM_DISPLAYV,PARAM_DISPLAYS,PARAM_DISPLAYF};

	double spaceType=0.;
	
	  private static final String RESSOURCE_FACEACOLOR = "FaceA_-RGB";
	  private static final String RESSOURCE_FACEBCOLOR = "FaceB_-RGB";
	  private static final String RESSOURCE_FACECCOLOR = "FaceC_-RGB";
	  private static final String RESSOURCE_SEGMCOLOR = "Segment_-RGB";
	  private static final String RESSOURCE_VERTCOLOR = "Vertex_-RGB";
	  private static final String RESSOURCE_BACKGROUND = "Background_-RGB";
	  
	  private String faceARGB = new String("FFFFFF");
	  private String faceBRGB = new String("000000");
	  private String faceCRGB = new String("000000");
	  
	  private String SegmentRGB = new String("FFFFFF");
	  private String VertexRGB = new String("000000");
	  private String BackgroundRGB = new String("000000");
	  
	  
		vec3 faceAColor = new vec3 (1.,1.,1.);
		vec3 faceBColor = new vec3(0,0,0);
		vec3 faceCColor = new vec3(0,0,0);
		vec3 segColor = new vec3(1.0,1.0,1.0);
		vec3 vertexColor = new vec3(0.0,0.0,0.0);
		vec3 backGroundColor = new vec3(.0,.0,.0);
		
	  
	  private static final String[] ressourceNames = { RESSOURCE_FACEACOLOR ,RESSOURCE_FACEBCOLOR,RESSOURCE_FACECCOLOR,RESSOURCE_SEGMCOLOR,RESSOURCE_VERTCOLOR,RESSOURCE_BACKGROUND };
	   
	  
	 static boolean isHexNumber (String cadena) {
			  try {
			    Long.parseLong(cadena, 16);
			    return true;
			  }
			  catch (NumberFormatException ex) {
			    // Error handling code...
			    return false;
			  }
			}
	  
	 
	 public vec3 RGBtovec3(String RGB,vec3  color)
	 {
		 String colorRGB=RGB.substring(0,6);
         vec3 retcolor=new vec3(0.0);
		 if(isHexNumber(colorRGB))
		 {
     	   int r=Integer.parseInt(colorRGB.substring(0,2),16);
     	   int g=Integer.parseInt(colorRGB.substring(2,4),16);
     	   int b=Integer.parseInt(colorRGB.substring(4,6),16);
     	    retcolor=new vec3(r/255.0,g/255.0,b/255.0);
     	 }
		 else
			retcolor=color;			 
   	   return retcolor;
	 }
     
     
	double hdotd(vec3 a, vec3 b){//dot product for vectors.
		return G.dot(new vec2(a.x,a.y),new vec2(b.x,b.y))+spaceType*a.z*b.z;
	}
	double hdotv(vec3 a, vec3 b){//dot product for duals.
		return spaceType*G.dot(new vec2(a.x,a.y),new vec2(b.x,b.y))+a.z*b.z;
	}
	
	double hdotdv(vec3 d, vec3 v){//dot product for vectors and duals.
		return G.dot(d,v);
	}

	double hlengthv(vec3 v){
		return G.sqrt(G.abs(hdotv(v,v)));
	}
	double hlengthd(vec3 v){
		return G.sqrt(G.abs(hdotd(v,v)));
	}

	vec3 hnormalizev(vec3 v){//normalization of vectors.
//	double l=1./hlengthv(v);
		return  v.multiply(1./hlengthv(v));
	}
	vec3 hnormalized(vec3 v){//normalization of duals.
//		double l=1./hlengthd(v);
		return v.multiply(1./hlengthd(v));
	}

	void init()
{
		spaceType=(double)(G.sign(qParam*rParam+pParam*rParam+pParam*qParam-pParam*qParam*rParam));//1./pParam+1./qParam+1./rParam-1.;

		double cospip=Math.cos(Math.PI/(double)(pParam)), sinpip=Math.sin(Math.PI/(double)(pParam));
		double cospiq=Math.cos(Math.PI/(double)(qParam)), sinpiq=Math.sin(Math.PI/(double)(qParam));
		double cospir=Math.cos(Math.PI/(double)(rParam)), sinpir=Math.sin(Math.PI/(double)(rParam));
		double ncsincos=(cospiq+cospip*cospir)/sinpip;

		//na is simply vec3(1.,0.,0.).
		nb=new vec3(-cospip,sinpip,0.);
		nc=new vec3(-cospir,-ncsincos,G.sqrt(G.abs((ncsincos+sinpir)*(-ncsincos+sinpir))));

		if(spaceType==0.){//This case is a little bit special
			nc.z=0.25;
		}

		pA=new vec3(nb.y*nc.z,-nb.x*nc.z,nb.x*nc.y-nb.y*nc.x);
		pB=new vec3(0.,nc.z,-nc.y);
		pC=new vec3(0.,0.,nb.y);

		q=pA.multiply(U).plus(pB.multiply(V)).plus(pC.multiply(W));
		double qq=hlengthv(q), Aq=hdotv(pA,q), Bq=hdotv(pB,q), Cq=hdotv(pC,q);//needed for face identification
		gA=pA.multiply(qq/Aq).minus(q); gB=pB.multiply(qq/Bq).minus(q); gC=pC.multiply(qq/Cq).minus(q);
		p=hnormalizev(q);//p=q;

		if(spaceType==-1.){
			tVR=G.sinh(0.5*VRadius)/G.cosh(0.5*VRadius);
			tSR=G.sinh(0.5*SRadius)/G.cosh(0.5*SRadius);
			cRA=G.cosh(RotAngle);sRA=G.sinh(RotAngle);
		}else if (spaceType==1.){
			tVR=G.sin(0.5*VRadius)/G.cos(0.5*VRadius);
			tSR=G.sin(0.5*SRadius)/G.cos(0.5*SRadius);
			cRA=G.cos(RotAngle);sRA=G.sin(RotAngle);
		}else{
			tVR=0.5*VRadius;
			tSR=0.5*SRadius;
			cRA=1.;sRA=RotAngle;
		}
	}

	vec3 Rotate(vec3 p){
		vec3 p1=p;
		vec2 rv;
		rv=G.normalize(RotVector);
		double vp=G.dot(rv,new vec2(p.x,p.y));
		vec2 t1=rv.multiply((vp*(cRA-1.))-p.z*sRA);
		p1.x+=t1.x;p1.y+=t1.y;
		p1.z+=vp*spaceType*sRA+p.z*(cRA-1.);
		return p1;
	}


	
	vec3 fold(vec3 pos) {
		vec3 ap=pos.plus(1.);
		
	//	for(int i=0;i<Iterations && G.any(G.notEqual(pos,ap));i++){
		for(int i=0;i<Iterations && (pos.x!=ap.x || pos.y!=ap.y);i++){
			ap=pos;
			pos.x=G.abs(pos.x);
			double t=-2.*G.min(0.,hdotdv(nb,pos)); 
			pos=pos.plus(nb.multiply(new vec3(1.,1.,spaceType)).multiply(t));
			t=-2.*G.min(0.,hdotdv(nc,pos)); 
			pos=pos.add(nc.multiply(new vec3(1.,1.,spaceType)).multiply(t));
			nbrFolds+=1.;
		}
		return pos;
	}

	double DD(double tha, double r){
		return tha*(1.+spaceType*r*r)/(1.+spaceType*spaceType*r*tha);
	}

	double dist2Vertex(vec3 z, double r){
		double tha=hlengthd(p.minus(z))/hlengthv(p.plus(z));
		return DD((tha-tVR)/(1.+spaceType*tha*tVR),r);
	}

	vec3 closestFTVertex(vec3 z){
			double fa=hdotd(gA,z);
			double fb=hdotd(gB,z);
			double fc=hdotd(gC,z);

			double f=G.max(fa,G.max(fb,fc));
			vec3 c=new vec3((fa==f?1d:0d),(fb==f?1d:0d),(fc==f?1d:0d));
			double k=1./(c.x+c.y+c.z);
			return c.multiply(k);
	}

	double dist2Segment(vec3 z, vec3 n, double r){
		//pmin is the orthogonal projection of z onto the plane defined by p and n
		//then pmin is projected onto the unit sphere
		
		/*float zn=hdotd(n,z),np=hdotd(n,p),zp=hdotv(z,p);
		float alpha=zp-spaceType*zn*np, beta=zn-zp*np;
		
		vec3 pmin=hnormalizev(alpha*p+min(0.,beta)*n);
		float tha=hlengthd(pmin-z)/hlengthv(pmin+z);
		return DD((tha-tSR)/(1.+spaceType*tha*tSR),r);*/

		double pn=hdotv(p,n),nn=hdotv(n,n),pp=hdotv(p,p),zn=hdotv(z,n),zp=hdotv(z,p);
		double det=1./(pn*pn-pp*nn);
		double alpha=det*(pn*zn-zp*nn), beta=det*(-pp*zn+pn*zp);
		
		vec3 pmin=hnormalizev(p.multiply(alpha).plus(n.multiply(G.min(0.,beta))));
		double tha=hlengthd(pmin.minus(z))/hlengthv(pmin.plus(z));
		return DD((tha-tSR)/(1.+spaceType*tha*tSR),r);
	}

	double dist2Segments(vec3 z, double r){
		double da=dist2Segment(z, new vec3(1.,0.,0.), r);
		double db=dist2Segment(z, nb, r);
		double dc=dist2Segment(z, nc.multiply(new vec3(1.,1.,spaceType)), r);
		
		return G.min(G.min(da,db),dc);
	}
	  
	public vec3 getRGBColor(double xp,double yp)
	{
        vec2	aaScale=new vec2(aascale);
		vec2 pos=new vec2(xp,yp).division(zoom).plus(new vec2(centerX,centerY));//.multiply(zoom);

/*		vec3 faceAColor = new vec3 (1,0,0);
		vec3 faceBColor = new vec3(0,1,0);
		vec3 faceCColor = new vec3(0,0,1);
		vec3 segColor = new vec3(0,0,0);
		vec3 vertexColor = new vec3(0.5,0.5,0);
		vec3 backGroundColor = new vec3(.0,.0,.0);*/

		
		double r=G.length(pos);
		vec3 z3=new vec3(pos.multiply(2.0),1.-spaceType*r*r).multiply(1./(1.+spaceType*r*r));
		if(spaceType==-1. && r>=1.) 
			return backGroundColor;//We are outside Poincar� disc.
		
		z3=Rotate(z3);
		z3=fold(z3);
		
		vec3 color=backGroundColor;
		if(DisplayFaces==1){
			vec3 c=closestFTVertex(z3);
			color=faceAColor.multiply(c.x).plus(faceBColor.multiply(c.y)).plus(faceCColor.multiply(c.z));
		}
		//antialiasing using distance de segments and vertices (ds and dv) (see:http://www.iquilezles.org/www/articles/distance/distance.htm)
		if(DisplaySegments==1){
			double ds=dist2Segments(z3, r);
			color=G.mix(segColor,color,G.smoothstep(-1.,1.,ds*0.5/aaScale.y));//clamp(ds/aaScale.y,0.,1.));
		}
		if(DisplayVertices==1){
			double dv=dist2Vertex(z3,r);
			color=G.mix(vertexColor,color,G.smoothstep(-1.,1.,dv*0.5/aaScale.y));//clamp(dv/aaScale.y,0.,1.));
		}
		//final touch in order to remove jaggies at the edge of the circle
		if(spaceType==-1.) 
			color=G.mix(backGroundColor,color,G.smoothstep(0.,1.,(1.-r)*0.5/aaScale.y));//clamp((1.-r)/aaScale.y,0.,1.));
		return color;
	}
 	
	@Override
	public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) 
	{
      init();
	}

	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) 
	{

        vec3 color=new vec3(0.0); 
		 vec2 uV=new vec2(0.),p=new vec2(0.);
	       int[] tcolor=new int[3];  


		 
	     if(colorOnly==1)
		 {
			 uV.x=pAffineTP.x;
			 uV.y=pAffineTP.y;
		 }
		 else
		 {
	   			 uV.x=1.0*pContext.random()-0.5;
				 uV.y=1.0*pContext.random()-.5;
		}
        
        color=getRGBColor(uV.x,uV.y);
        tcolor=dbl2int(color);
        
        //z by color (normalized)
        double z=greyscale(tcolor[0],tcolor[1],tcolor[2]);
        
        if(gradient==0)
        {
  	  	
    	  pVarTP.rgbColor  =true;;
    	  pVarTP.redColor  =tcolor[0];
    	  pVarTP.greenColor=tcolor[1];
    	  pVarTP.blueColor =tcolor[2];
    		
        }
        else if(gradient==1)
        {

            	Layer layer=pXForm.getOwner();
            	RGBPalette palette=layer.getPalette();      	  
          	    RGBColor col=findKey(palette,tcolor[0],tcolor[1],tcolor[2]);
          	    
          	  pVarTP.rgbColor  =true;;
          	  pVarTP.redColor  =col.getRed();
          	  pVarTP.greenColor=col.getGreen();
          	  pVarTP.blueColor =col.getBlue();

        }
        else 
        {
        	pVarTP.color=z;
        }

        pVarTP.x+= pAmount*(uV.x);
        pVarTP.y+= pAmount*(uV.y);
        
        
	    double dz = z * scale_z + offset_z;
	    if (reset_z == 1) {
	      pVarTP.z = dz;
	    }
	    else {
	      pVarTP.z += dz;
	    }
	}
	
	public String getName() {
		return "dc_triantess";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}

	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { zoom,centerX,centerY,Iterations,pParam,qParam,rParam,U,V,W,VRadius,SRadius,RotAngle,DisplayVertices,DisplaySegments,DisplayFaces},super.getParameterValues());
	}


	public void setParameter(String pName, double pValue) {
		if (PARAM_ZOOM.equalsIgnoreCase(pName)) 
		{	   zoom =   pValue;Tools.limitValue(pValue, 0.0 , 10.0);

	    }
		else	if (PARAM_CENTERX.equalsIgnoreCase(pName)) 
		{	   centerX =   pValue;Tools.limitValue(pValue, -5.0 , 5.0);

	    }
		else	if (PARAM_CENTERY.equalsIgnoreCase(pName)) 
		{	   centerY =   pValue;Tools.limitValue(pValue, -5.0 , 5.0);

	    }
		else if (pName.equalsIgnoreCase(PARAM_ITERATIONS)) {
			Iterations = (int)Tools.limitValue(pValue, 0 , 20);
		}
		else if (pName.equalsIgnoreCase(PARAM_PPRAM)) {
			pParam = (int)Tools.limitValue(pValue, 2 , 20);
		}
		else if (pName.equalsIgnoreCase(PARAM_QPARAM)) {
			qParam = (int)Tools.limitValue(pValue, 2 , 20);
		}
		else if (pName.equalsIgnoreCase(PARAM_RPARAM)) {
			rParam = (int)Tools.limitValue(pValue, 2 , 20);
		}
		else if (pName.equalsIgnoreCase(PARAM_U)) {
			U = Tools.limitValue(pValue, 0. , 1.);
		}
		else if (pName.equalsIgnoreCase(PARAM_V)) {
			V = Tools.limitValue(pValue, 0.0 , 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_W)) {
			W = Tools.limitValue(pValue, 0.0 , 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_VRADIUS)) {
			VRadius = Tools.limitValue(pValue, 0.0 , 0.25);
		}
		else if (pName.equalsIgnoreCase(PARAM_SRADIUS)) {
			SRadius = Tools.limitValue(pValue, 0.0 , 0.05);
		}
		else if (pName.equalsIgnoreCase(PARAM_ROTANGLE)) {
			RotAngle = Tools.limitValue(pValue, -3.0 , 3.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_DISPLAYV)) {
			DisplayVertices = (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_DISPLAYS)) {
			DisplaySegments = (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_DISPLAYF)) {
			DisplayFaces = (int)Tools.limitValue(pValue, 0 , 1);
		}
//		else if (pName.equalsIgnoreCase(PARAM_AASCALE)) {
//			aascale = pValue;
//		}
		else
			super.setParameter(pName, pValue);
	}

    @Override
    public String[] getRessourceNames() {
      return ressourceNames;
    }

	  
    @Override
    public byte[][] getRessourceValues() {
      return new byte[][] { (faceARGB != null ? faceARGB.getBytes() : null), 
    	  (faceBRGB != null ? faceBRGB.getBytes() : null), (faceCRGB != null ? faceCRGB.getBytes() : null),
    	  (SegmentRGB != null ? SegmentRGB.getBytes() : null),(VertexRGB != null ? VertexRGB.getBytes() : null),
    	  (BackgroundRGB != null ? BackgroundRGB.getBytes() : null)};
    }

    @Override
    public RessourceType getRessourceType(String pName) {
	  if (pName.equals(RESSOURCE_FACEACOLOR)) {
		   return RessourceType.BYTEARRAY;
	  }
	  else if (pName.equals(RESSOURCE_FACEBCOLOR)) {
           return RessourceType.BYTEARRAY;
      }
      else if (pName.equals(RESSOURCE_FACECCOLOR)) {
    	  return RessourceType.BYTEARRAY;
	  }
      else	  if (pName.equals(RESSOURCE_SEGMCOLOR)) {
		   return RessourceType.BYTEARRAY;
	  }
	  else if (pName.equals(RESSOURCE_VERTCOLOR)) {
          return RessourceType.BYTEARRAY;
     }
     else if (pName.equals(RESSOURCE_BACKGROUND)) {
   	  return RessourceType.BYTEARRAY;
	  }
      else {
        return super.getRessourceType(pName);
      }
    }


	
    @Override
    public void setRessource(String pName, byte[] pValue) {
	  if (RESSOURCE_FACEACOLOR.equalsIgnoreCase(pName)) {
		        faceARGB = pValue!= null ? new String(pValue) : "";
                faceAColor=RGBtovec3(faceARGB,faceAColor);
	  }
	  else if (RESSOURCE_FACEBCOLOR.equalsIgnoreCase(pName)) {
	        faceBRGB = pValue!= null ? new String(pValue) : "";
            faceBColor=RGBtovec3(faceBRGB,faceBColor);
      }
      else 	if (RESSOURCE_FACECCOLOR.equalsIgnoreCase(pName)) {
	        faceCRGB = pValue!= null ? new String(pValue) : "";
            faceCColor=RGBtovec3(faceCRGB,faceCColor);
	  }
      else	  if (RESSOURCE_SEGMCOLOR.equalsIgnoreCase(pName)) {
	        SegmentRGB = pValue!= null ? new String(pValue) : "";
            segColor=RGBtovec3(SegmentRGB,segColor);
  }
  else if (RESSOURCE_VERTCOLOR.equalsIgnoreCase(pName)) {
        VertexRGB = pValue!= null ? new String(pValue) : "";
        vertexColor=RGBtovec3(VertexRGB,vertexColor);
  }
  else 	if (RESSOURCE_BACKGROUND.equalsIgnoreCase(pName)) {
        BackgroundRGB = pValue!= null ? new String(pValue) : "";
        backGroundColor=RGBtovec3(BackgroundRGB,backGroundColor);
  }
      else
    	  throw new IllegalArgumentException(pName);
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTS_BACKGROUND};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "float x,y;"
	    		+"float3 color=make_float3(1.0,1.0,0.0);"
	    		+"float z=0.5;"
	    		+"if( __dc_triantess_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=RANDFLOAT()-0.5;"
	    		+"  y=RANDFLOAT()-0.5;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)/__dc_triantess_zoom +make_float2(__dc_triantess_CenterX,__dc_triantess_CenterY);"
	   	
	    		+"color=dc_triantess_getRGBColor(uv, __dc_triantess_Iterations, __dc_triantess_pParam, __dc_triantess_qParam, __dc_triantess_rParam,"
                +"      __dc_triantess_U, __dc_triantess_V, __dc_triantess_W,"
                + "     __dc_triantess_VRadius,__dc_triantess_SRadius, __dc_triantess_RotAngle,"
                +"      __dc_triantess_Vertex, __dc_triantess_Segments, __dc_triantess_Faces);"
                
	    		+"if( __dc_triantess_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_triantess_Gradient ==1 )"
	    		+"{"
	    		+"float4 pal_color=make_float4(color.x,color.y,color.z,1.0);"
	    		+"float4 simcol=pal_color;"
	    		+"float diff=1000000000.0f;"
////read palette colors to find the nearest color to pixel color
	    		+" for(int index=0; index<numColors;index++)"
              +" {      pal_color = read_imageStepMode(palette, numColors, (float)index/(float)numColors);"
	    		+"        float3 pal_color3=make_float3(pal_color.x,pal_color.y,pal_color.z);"
              // implement:  float distance(float,float,float,float,float,float) in GPU function
	        	+"    float dvalue= distance_color(color.x,color.y,color.z,pal_color.x,pal_color.y,pal_color.z);"
	        	+ "   if (diff >dvalue) "
	        	+ "    {" 
	        	+"	     diff = dvalue;" 
	        	+"       simcol=pal_color;" 
	        	+"	   }"
              +" }"
////use nearest palette color as the pixel color                
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = simcol.x;"
	    		+"   __colorG  = simcol.y;"
	    		+"   __colorB  = simcol.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_triantess_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_triantess*x;"
	    		+"__py+= __dc_triantess*y;"
	    		+"float dz = z * __dc_triantess_scale_z + __dc_triantess_offset_z;"
	    		+"if ( __dc_triantess_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	 
	 public String getGPUFunctions(FlameTransformationContext context) {
		 return   "__device__	float  dc_triantess_hdotd (float3 a, float3 b, float spaceType){"
				 +"		return dot(make_float2(a.x,a.y),make_float2(b.x,b.y))+spaceType*a.z*b.z;"
				 +"	}"
				 
				 +"__device__	float  dc_triantess_hdotv (float3 a, float3 b, float spaceType){"
				 +"		return spaceType*dot(make_float2(a.x,a.y),make_float2(b.x,b.y))+a.z*b.z;"
				 +"	}"

				 +"__device__	float  dc_triantess_hdotdv (float3 d, float3 v){"
				 +"		return dot(d,v);"
				 +"	}"
				 
				 +"__device__	float  dc_triantess_hlengthv (float3 v, float spaceType){"
				 +"		return sqrtf(fabsf( dc_triantess_hdotv (v,v,spaceType)));"
				 +"	}"
				 
				 +"__device__	float  dc_triantess_hlengthd (float3 v, float spaceType){"
				 +"		return sqrtf(fabsf( dc_triantess_hdotd (v,v,spaceType)));"
				 +"	}"
				 
				 +"__device__	float3  dc_triantess_hnormalizev (float3 v, float spaceType){"
				 +"		return  v*(1./ dc_triantess_hlengthv(v,spaceType));"
				 +"	}"
				 
				 +"__device__	float3  dc_triantess_hnormalized (float3 v, float spaceType){"
				 +"		return v*(1./ dc_triantess_hlengthd(v,spaceType));"
				 +"	}"

				 +"__device__	float3  dc_triantess_Rotate (float3 p, float spaceType, float cRA, float sRA){"
				 +"		float3 p1=p;"
				 +"		float2 rv;"
				 +"	    float2	RotVector=make_float2(0.0,1.0);"
				 +"		rv=normalize(RotVector);"
				 +"		float vp=dot(rv,make_float2(p.x,p.y));"
				 +"		float2 t1=rv*((vp*(cRA-1.))-p.z*sRA);"
				 +"		p1.x+=t1.x;p1.y+=t1.y;"
				 +"		p1.z+=vp*spaceType*sRA+p.z*(cRA-1.);"
				 +"		return p1;"
				 +"	}"
				 
				 +"__device__	float3  dc_triantess_fold (float3 pos, float spaceType,float Iterations, float3 nb, float3 nc) {"
				 +"		float3 ap=pos+1.0;"
				 +"		float nbrFolds=0.;"
				 +"		for(int i=0;i<Iterations && (pos.x!=ap.x || pos.y!=ap.y);i++){"
				 +"			ap=pos;"
				 +"			pos.x=fabsf(pos.x);"
				 +"			float t=-2.*fminf(0.,dc_triantess_hdotdv(nb,pos)); "
				 +"			pos=pos+(nb*(make_float3(1.,1.,spaceType))*(t));"
				 +"			t=-2.*fminf(0.,dc_triantess_hdotdv(nc,pos)); "
				 +"			pos=pos + nc*(make_float3(1.,1.,spaceType))*t;"
				 +"			nbrFolds+=1.0;"
				 +"		}"
				 +"		return pos;"
				 +"	}"
				 
				 +"__device__	float  dc_triantess_DD (float tha, float r, float spaceType){"
				 +"		return tha*(1.+spaceType*r*r)/(1.+spaceType*spaceType*r*tha);"
				 +"	}"
				 
				 +"__device__	float  dc_triantess_dist2Vertex (float3 z, float r, float spaceType, float3 p, float tVR){"
				 +"		float tha= dc_triantess_hlengthd (p-z,spaceType)/dc_triantess_hlengthv(p+z, spaceType);"
				 +"		return  dc_triantess_DD ((tha-tVR)/(1.+spaceType*tha*tVR),r,spaceType);"
				 +"	}"
				 
				 +"__device__	float3  dc_triantess_closestFTVertex (float3 z, float spaceType, float3 gA,float3 gB,float3 gC){"
				 +"			float fa= dc_triantess_hdotd (gA,z,spaceType);"
				 +"			float fb= dc_triantess_hdotd (gB,z,spaceType);"
				 +"			float fc= dc_triantess_hdotd (gC,z,spaceType);"
				 +"			float f=fmaxf(fa,fmaxf(fb,fc));"
				 +"			float3 c=make_float3((fa==f?1.0f:0.0f),(fb==f?1.0f:0.0f),(fc==f?1.0f:0.0f));"
				 +"			float k=1./(c.x+c.y+c.z);"
				 +"			return c*(k);"
				 +"	}"
				 
				 +"__device__	float  dc_triantess_dist2Segment (float3 z, float3 n, float r, float spaceType,float3 p, float tSR){"
				 +"		float pn= dc_triantess_hdotv (p,n,spaceType),nn= dc_triantess_hdotv (n,n,spaceType),pp= dc_triantess_hdotv (p,p,spaceType),zn= dc_triantess_hdotv (z,n,spaceType),zp= dc_triantess_hdotv (z,p,spaceType);"
				 +"		float det=1./(pn*pn-pp*nn);"
				 +"		float alpha=det*(pn*zn-zp*nn), beta=det*(-pp*zn+pn*zp);"
				 +"		float3 pmin= dc_triantess_hnormalizev (p*(alpha)+(n*(fminf(0.,beta))),spaceType);"
				 +"		float tha= dc_triantess_hlengthd (pmin-z, spaceType)/dc_triantess_hlengthv(pmin+z,spaceType);"
				 +"		return  dc_triantess_DD ((tha-tSR)/(1.+spaceType*tha*tSR),r,spaceType);"
				 +"	}"
				 
				 +"__device__	float  dc_triantess_dist2Segments (float3 z, float r,float spaceType, float3 nb,float3 nc, float3 p, float tSR){"
				 +"		float da= dc_triantess_dist2Segment (z, make_float3(1.,0.,0.), r, spaceType,p, tSR);"
				 +"		float db= dc_triantess_dist2Segment (z, nb, r, spaceType,p,tSR);"
				 +"		float dc= dc_triantess_dist2Segment (z, nc*(make_float3(1.,1.,spaceType)), r, spaceType,p,tSR);"
				 +"		return fminf(fminf(da,db),dc);"
				 +"	}"


				 +"__device__ float3  dc_triantess_getRGBColor (float2 pos, float Iterations, float pParam, float qParam, float rParam,"
				 + "                                            float U, float V, float W, float VRadius,float SRadius, float RotAngle,"
				 + "                                            float Vertex, float Segments, float Faces)"
				 +"	{"			 
				 +"     float tVR,tSR,cRA,sRA;"
// init
				 +"		float spaceType= sign (qParam*rParam + pParam*rParam + pParam*qParam - pParam*qParam*rParam);"
				 
				 +"		float cospip=cosf(PI/pParam), sinpip=sinf(PI/pParam);"
				 +"		float cospiq=cosf(PI/qParam), sinpiq=sinf(PI/qParam);"
				 +"		float cospir=cosf(PI/rParam), sinpir=sinf(PI/rParam);"
				 +"		float ncsincos=(cospiq+cospip*cospir)/sinpip;"

				 +"		float3 nb=make_float3(-cospip,sinpip,0.);"
				 +"		float3 nc=make_float3(-cospir,-ncsincos,sqrtf(fabsf( (ncsincos+sinpir)*(-ncsincos+sinpir) ) ) );"
				 +"		if(spaceType==0.0f){"
				 +"			nc.z=0.25f;"
				 +"		}"
				 
				 +"		float3 pA=make_float3(nb.y*nc.z, -nb.x*nc.z, nb.x*nc.y-nb.y*nc.x);"
				 +"		float3 pB=make_float3(0.,nc.z,-nc.y);"
				 +"		float3 pC=make_float3(0.,0.,nb.y);"
				 
				 +"		float3 q=pA*(U)+(pB*(V))+(pC*(W));"
				 +"		float qq= dc_triantess_hlengthv (q,spaceType), Aq= dc_triantess_hdotv (pA,q,spaceType), Bq= dc_triantess_hdotv (pB,q,spaceType), Cq= dc_triantess_hdotv (pC,q,spaceType);"
				 +"		float3 gA=pA*(qq/Aq)-(q);"
				 +"     float3 gB=pB*(qq/Bq)-(q);"
				 +"     float3 gC=pC*(qq/Cq)-(q);"
				 +"		float3 p= dc_triantess_hnormalizev (q,spaceType);"
				 
				 +"		if(spaceType==-1.){"
				 +"			tVR=sinhf(0.5*VRadius)/coshf(0.5*VRadius);"
				 +"			tSR=sinhf(0.5*SRadius)/coshf(0.5*SRadius);"
				 +"			cRA=coshf(RotAngle);sRA=sinhf(RotAngle);"
				 +"		}else if (spaceType==1.){"
				 +"			tVR=sinf(0.5*VRadius)/cosf(0.5*VRadius);"
				 +"			tSR=sinf(0.5*SRadius)/cosf(0.5*SRadius);"
				 +"			cRA=cosf(RotAngle);sRA=sinf(RotAngle);"
				 +"		}else{"
				 +"			tVR=0.5*VRadius;"
				 +"			tSR=0.5*SRadius;"
				 +"			cRA=1.;sRA=RotAngle;"
				 +"		}"
//// end init
				 +"      float3 faceAColor = make_float3 (1.,1.,1.);"
				 +"      float3 faceBColor = make_float3(0.,0.,0.);"
				 +"      float3 faceCColor = make_float3(0.,0.,0.);"
				 +"      float3 segColor = make_float3(1.0,1.0,1.0);"
				 +"      float3 vertexColor = make_float3(0.0,0.0,0.0);"
				 +"      float3 backGroundColor = make_float3(.0,.0,.0);"
				 
				 +"      float aascale=0.0005;"
				 +"      float2	aaScale=make_float2(aascale,aascale);"
				 +"		 float r=length(pos);"
				 +"		 float3 z3=make_float3(pos.x*2.0, pos.y*2.0, 1.-spaceType*r*r)/(1.+spaceType*r*r);"
				 
				 +"		 if(spaceType==-1. && r>=1.) "
				 +"			return backGroundColor;"
				 
				 +"		 z3= dc_triantess_Rotate (z3,spaceType, cRA , sRA);"
				 +"		 z3= dc_triantess_fold (z3,spaceType, Iterations, nb,nc);"
				 +"		 float3 color=backGroundColor;"
				 +"		 if(Faces==1.0){"
				 +"			float3 c= dc_triantess_closestFTVertex (z3,spaceType,gA,gB,gC);"
				 +"			color=faceAColor*c.x + faceBColor*c.y + faceCColor*c.z;"
				 +"		 }"
				 +"		 if(Segments==1.0){"
				 +"			float ds= dc_triantess_dist2Segments (z3, r,  spaceType, nb, nc, p, tSR );"
				 +"			color=mix( segColor,color,smoothstep(-1.0, 1.0 , ds*0.5/aaScale.y) );"
				 +"		 }"
				 +"		 if(Vertex==1.0){"
				 +"			float dv= dc_triantess_dist2Vertex (z3,r,spaceType, p,tVR);"
				 +"			color=mix( vertexColor, color, smoothstep(-1.0 , 1.0 , dv*0.5/aaScale.y) );"
				 +"		 }"
				 +"		 if(spaceType==-1.)"
				 +"			color=mix(backGroundColor,color,smoothstep(0.,1.,(1.-r)*0.5/aaScale.y));"
//				 +"      float3 color=make_float3(1.0,1.0,0.0);"
				 +"		 return color;"
				 +"	}";
	 }	
}

