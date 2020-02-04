package js.glsl;

import java.io.Serializable;

public class mat4 implements Serializable {
  private static final long serialVersionUID = 1L;



		
	/*a00 a01 a02 a03
	  a10 a11 a12 a13
	  a20 a21 a22 a23
	  a30 a31 a32 a33*/


		    public double a00,a10,a20,a30;
		    public double a01,a11,a21,a31;
		    public double a02,a12,a22,a32;
		    public double a03,a13,a23,a33;
		    

		    public mat4(double a00,double a10,double a20,double a30,
		    		    double a01,double a11,double a21,double a31,
		    		    double a02,double a12,double a22,double a32,
		    		    double a03,double a13,double a23,double a33 )
		    {
		    	this.a00=a00;
		    	this.a10=a10;
		    	this.a20=a20;
		    	this.a30=a30;
		    	
		    	this.a01=a01;
		    	this.a11=a11;
		    	this.a21=a21;
		    	this.a31=a31;
		    	
		    	this.a02=a02;
		    	this.a12=a12;
		    	this.a22=a22;
		    	this.a32=a32;
		    	
		    	this.a03=a03;
		    	this.a13=a13;
		    	this.a23=a23;
		    	this.a33=a33;
		    }
		    
		    
		    public mat4(vec4 v1, vec4 v2, vec4 v3, vec4 v4)
		    {
		    	this.a00=v1.x;
		    	this.a10=v1.y;
		    	this.a20=v1.z;
		    	this.a30=v1.w;
		    	
		    	this.a01=v2.x;
		    	this.a11=v2.y;
		    	this.a21=v2.z;
		    	this.a31=v2.w;
		    	
		    	this.a02=v3.x;
		    	this.a12=v3.y;
		    	this.a22=v3.z;	 
		    	this.a32=v3.w;	
		    	
		    	this.a03=v4.x;
		    	this.a13=v4.y;
		    	this.a23=v4.z;	 
		    	this.a33=v4.w;	
		    }
		    
		    public vec4 times(vec4 v)  //v4= M4*V4
		    {	    	
		    	return new vec4(a00*v.x + a01*v.y + a02*v.z + a03*v.w ,
		                        a10*v.x + a11*v.y + a12*v.z + a13*v.w ,
		                        a20*v.x + a21*v.y + a22*v.z + a23*v.w ,
		                        a30*v.x + a31*v.y + a32*v.z + a33*v.w ); 
		    }
		    
		    public mat4 add(double f) 
		    {
		    	return new mat4(a00+f , a01+f, a02+f , a03+f,
		    			        a10+f, a11+f , a12+f , a13+f,
		    			        a20+f ,a21+f , a22+f  , a23+f,
		    			        a30+f ,a31+f , a32+f  , a33+f) ;
		    }
		    
		    public mat4 minus(double f)
		    {
		    	return new mat4(a00-f , a01-f, a02-f , a03-f,
    			        a10-f, a11-f , a12-f , a13-f,
    			        a20-f ,a21-f , a22-f  , a23-f,
    			        a30-f ,a31-f , a32-f  , a33-f) ;
		    }
		    
		    public mat4 times(double f)
		    {
		    	return new mat4(a00*f , a01*f, a02*f , a03*f,
    			        a10*f, a11*f , a12*f , a13*f,
    			        a20*f ,a21*f , a22*f  , a23*f,
    			        a30*f ,a31*f , a32*f  , a33*f) ;
		    }
		    
		    public mat4 division(double f)
		    {
		    	return new mat4(a00/f , a01/f, a02/f , a03/f,
    			        a10/f, a11/f , a12/f , a13/f,
    			        a20/f ,a21/f , a22/f  , a23/f,
    			        a30/f ,a31/f , a32/f  , a33/f) ;
		    }
	}
	