package js.glsl;

import java.io.Serializable;

public class mat3 implements Serializable {
  private static final long serialVersionUID = 1L;

	
/*a00 a01 a02
  a10 a11 a12
  a20 a21 a22*/


	    public double a00,a10,a20,a01,a11,a21,a02,a12,a22;

	    public mat3(double a00,double a10,double a20,double a01,double a11,double a21,double a02,double a12,double a22)
	    {
	    	this.a00=a00;
	    	this.a10=a10;
	    	this.a20=a20;
	    	
	    	this.a01=a01;
	    	this.a11=a11;
	    	this.a21=a21;
	    	
	    	this.a02=a02;
	    	this.a12=a12;
	    	this.a22=a22;	    	
	    }
	    
	    public mat3(vec3 v1, vec3 v2, vec3 v3)
	    {
	    	this.a00=v1.x;
	    	this.a10=v1.y;
	    	this.a20=v1.z;
	    	
	    	this.a01=v2.x;
	    	this.a11=v2.y;
	    	this.a21=v2.z;
	    	
	    	this.a02=v3.x;
	    	this.a12=v3.y;
	    	this.a22=v3.z;	    	
	    }
	    
	    public vec3 times(vec3 v)   // vec3=mat3*vec3
	    {	    	
	    	return new vec3(a00*v.x + a01*v.y + a02*v.z , a10*v.x +a11*v.y + a12*v.z , a20*v.x +a21*v.y +a22*v.z); 
	    }
	    
	    /*a00 a01 a02
	    a10 a11 a12
	    a20 a21 a22*/
	    
	    public mat3 add(double f)
	    {
	    	return new mat3(a00+f , a10+f, a20+f , a01+f, a11+f , a21+f , a02+f ,a12+f ,a22+f);
	    }
	    
	    public mat3 minus(double f)
	    {
	    	return new mat3(a00-f , a10-f , a20-f , a01-f ,a11-f , a21-f , a02-f ,a12-f ,a22-f);
	    }
	    public mat3 times(double f)
	    {
	    	return new mat3(a00*f , a10*f , a20*f , a01*f , a11*f , a21*f , a02*f ,a12*f ,a22*f);
	    }
	    
	    public mat3 division(double f)
	    {
	    	return new mat3(a00/f , a10/f , a20/f , a01/f , a11/f , a21/f , a02/f , a12/f , a22/f);
	    }
}
