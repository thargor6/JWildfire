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
import js.glsl.mat4;
import js.glsl.vec2;
import js.glsl.vec3;



public class DC_EscherFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_escher
	 * Autor: Jesus Sosa
	 * Date: February 12, 2019
	 */



	private static final long serialVersionUID = 1L;



	private static final String PARAM_SHIFT = "shift";
	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";




	




	double shift=0.0;
	private int seed = 10000;
	double time=0.0;
	double zoom=4.0;


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	
	private static final String[] additionalParamNames = { PARAM_SHIFT,PARAM_SEED,PARAM_TIME,PARAM_ZOOM};


	public vec3 getPix(int tex_x, mat4 d)
	{
	double x_=0.;

	if(tex_x<4)		x_=d.a00;
	else if(tex_x<8)	x_=d.a10;
	else if(tex_x<12)	x_=d.a20;
	else if(tex_x<16)	x_=d.a30;
	else if(tex_x<20)	x_=d.a01;
	else if(tex_x<24)	x_=d.a11;
	else if(tex_x<28)	x_=d.a21;
	else if(tex_x<32)	x_=d.a31;
	else if(tex_x<36)	x_=d.a02;
	else if(tex_x<40)	x_=d.a12;
	else if(tex_x<44)	x_=d.a22;
	else if(tex_x<48)	x_=d.a32;
	else if(tex_x<52)	x_=d.a03;
	else if(tex_x<56)	x_=d.a13;
	else if(tex_x<60)	x_=d.a23;
	else if(tex_x<64)	x_=d.a33;

	double mask = G.mod(tex_x, 4.);
	double tmp = 0.;
	if(mask==0.)		tmp=G.mod(x_,16.)/16.;
	else if(mask==1.)	tmp=G.mod(G.floor(x_/16.),16.)/16.;
	else if(mask==2.)	tmp=G.mod(G.floor(x_/256.),16.)/16.;
	else			tmp=G.mod(G.floor(x_/4096.),16.)/16.;
	vec3 col=new vec3(tmp,tmp,tmp);
	return col;
	}



	public vec3 getColor(int tex_x, int tex_y)
	{
    vec3 col=new vec3(0.);
	if(tex_y<10.){
	if(tex_y==0)col=getPix(tex_x,new mat4(61436.,65519.,34617.,5240.,52412.,56525.,61149.,65534.,6655.,16704.,13396.,47201.,34953.,22390.,26230.,20789.));
	if(tex_y==1)col=getPix(tex_x,new mat4(65019.,65535.,8639.,919.,52146.,52460.,60892.,65518.,7935.,4864.,8226.,35187.,22135.,30035.,8754.,24897.));
	if(tex_y==2)col=getPix(tex_x,new mat4(61433.,65534.,2303.,9521.,64544.,60911.,60875.,64989.,32767.,512.,8226.,22421.,17543.,9011.,4113.,4112.));
	if(tex_y==3)col=getPix(tex_x,new mat4(65524.,65263.,28671.,12288.,58688.,64476.,56254.,64989.,61439.,4096.,20497.,26216.,4422.,291.,0.,0.));
	if(tex_y==4)col=getPix(tex_x,new mat4(65474.,57343.,65534.,345.,49712.,64700.,51935.,61149.,65535.,4100.,21504.,25975.,8452.,12289.,52119.,48077.));
	if(tex_y==5)col=getPix(tex_x,new mat4(65077.,65535.,65518.,49151.,45094.,65467.,48639.,61147.,65535.,10.,34304.,17751.,36.,64914.,60927.,65535.));
	if(tex_y==6)col=getPix(tex_x,new mat4(45591.,65535.,61183.,61439.,56014.,65533.,57343.,60875.,65533.,46.,34832.,13143.,37378.,65535.,44287.,65534.));
	if(tex_y==7)col=getPix(tex_x,new mat4(88.,30803.,65533.,57069.,65484.,65535.,65535.,56508.,65533.,143.,26944.,4949.,65376.,65199.,44799.,65534.));
	if(tex_y==8)col=getPix(tex_x,new mat4(13942.,16.,65408.,52719.,61163.,65263.,65535.,56271.,65517.,223.,25920.,19.,65529.,60111.,45055.,65517.));
	if(tex_y==9)col=getPix(tex_x,new mat4(1401.,4978.,63232.,44511.,56814.,61150.,65534.,48383.,65229.,1535.,13632.,40962.,65247.,52223.,49151.,65500.));
	}
	else if(tex_y<20.){
	if(tex_y==10)col=getPix(tex_x,new mat4(1384.,26244.,28673.,52191.,56798.,56781.,65519.,48895.,60892.,3327.,5168.,63745.,59823.,48639.,53245.,65499.));
	if(tex_y==11)col=getPix(tex_x,new mat4(5223.,18311.,4.,56296.,56798.,56780.,65262.,61439.,56779.,24574.,4928.,65345.,51967.,49150.,61420.,65499.));
	if(tex_y==12)col=getPix(tex_x,new mat4(13687.,1128.,16384.,60545.,52702.,56796.,60893.,65534.,52654.,53213.,288.,48593.,48636.,53229.,65515.,65243.));
	if(tex_y==13)col=getPix(tex_x,new mat4(25447.,40.,16707.,64004.,56815.,56524.,56780.,65517.,47839.,65228.,803.,43767.,52956.,65500.,65227.,65212.));
	if(tex_y==14)col=getPix(tex_x,new mat4(33366.,24580.,39631.,60533.,57071.,60893.,56796.,60893.,44543.,64972.,4106.,48894.,57308.,64971.,64973.,64957.));
	if(tex_y==15)col=getPix(tex_x,new mat4(33317.,49152.,60365.,56047.,61183.,52974.,52413.,52701.,57342.,56521.,20527.,60351.,61183.,60619.,64975.,64975.));
	if(tex_y==16)col=getPix(tex_x,new mat4(26149.,53249.,56268.,52462.,65535.,57071.,52429.,56540.,65245.,56237.,37022.,61390.,52221.,60862.,56527.,56543.));
	if(tex_y==17)col=getPix(tex_x,new mat4(22532.,53504.,48093.,60399.,65535.,61183.,56798.,52428.,56796.,48095.,51421.,56814.,56303.,56526.,60927.,52479.));
	if(tex_y==18)col=getPix(tex_x,new mat4(22804.,49152.,48350.,65005.,65535.,65535.,57070.,52685.,56780.,44781.,61130.,56782.,56255.,52222.,56830.,60653.));
	if(tex_y==19)col=getPix(tex_x,new mat4(22578.,40960.,52702.,65211.,65535.,65535.,61439.,56798.,52444.,61148.,57066.,64733.,56765.,48620.,53245.,65533.));
	}
	else if(tex_y<30.){
	if(tex_y==20)col=getPix(tex_x,new mat4(14385.,20480.,52703.,43725.,65227.,65535.,65535.,56815.,52445.,60620.,56780.,65517.,64715.,49102.,61405.,65535.));
	if(tex_y==21)col=getPix(tex_x,new mat4(18978.,0.,52989.,52445.,43436.,65227.,65535.,61439.,56798.,48604.,48093.,53245.,47819.,57055.,65516.,65535.));
	if(tex_y==22)col=getPix(tex_x,new mat4(22530.,1.,57079.,52700.,52429.,43691.,60841.,65535.,61183.,48589.,61163.,48366.,48588.,65259.,22171.,51029.));
	if(tex_y==23)col=getPix(tex_x,new mat4(37889.,37.,57024.,56813.,56781.,48332.,39612.,56235.,61439.,52446.,64683.,52159.,52443.,64717.,5.,0.));
	if(tex_y==24)col=getPix(tex_x,new mat4(32769.,5.,61296.,57070.,60893.,56797.,56542.,43708.,56490.,48334.,64187.,56271.,60876.,64989.,11.,4352.));
	if(tex_y==25)col=getPix(tex_x,new mat4(29441.,2.,65492.,65535.,61167.,65262.,60910.,56797.,44236.,64714.,60877.,48382.,56797.,61165.,141.,28928.));
	if(tex_y==26)col=getPix(tex_x,new mat4(26113.,1.,64507.,65535.,65535.,65535.,65535.,65535.,56830.,48092.,65533.,52974.,60875.,65518.,1790.,25600.));
	if(tex_y==27)col=getPix(tex_x,new mat4(22544.,0.,55530.,65535.,65535.,65535.,65535.,61183.,61166.,48878.,61419.,61166.,60876.,57070.,28652.,33792.));
	if(tex_y==28)col=getPix(tex_x,new mat4(14129.,0.,64403.,65535.,65535.,43727.,13415.,4370.,8464.,52987.,40412.,60602.,56798.,65518.,64971.,8234.));
	if(tex_y==29)col=getPix(tex_x,new mat4(1089.,0.,25344.,35207.,9320.,1.,0.,0.,20480.,52719.,48364.,48093.,56798.,65534.,60620.,1775.));
	}
	else if(tex_y<40.){
	if(tex_y==30)col=getPix(tex_x,new mat4(864.,4096.,3.,0.,0.,0.,272.,8448.,57859.,52972.,48125.,60620.,61166.,65535.,48335.,53229.));
	if(tex_y==31)col=getPix(tex_x,new mat4(897.,8192.,8195.,1.,8448.,4625.,578.,8721.,64002.,52714.,51949.,60876.,61166.,65535.,56559.,60604.));
	if(tex_y==32)col=getPix(tex_x,new mat4(114.,16384.,4099.,257.,13089.,258.,8192.,324.,65344.,56812.,51918.,56795.,65261.,65535.,65518.,48895.));
	if(tex_y==33)col=getPix(tex_x,new mat4(9.,28672.,4.,13108.,8739.,529.,17152.,1092.,57248.,56828.,48078.,56780.,60910.,65535.,61438.,65535.));
	if(tex_y==34)col=getPix(tex_x,new mat4(38.,29440.,12291.,13364.,8740.,528.,21808.,852.,40163.,65003.,57036.,56524.,57086.,61150.,61182.,53230.));
	if(tex_y==35)col=getPix(tex_x,new mat4(68.,37904.,12546.,21573.,17459.,290.,22097.,306.,52984.,57068.,60602.,56542.,52462.,48060.,61437.,53213.));
	if(tex_y==36)col=getPix(tex_x,new mat4(594.,34368.,17153.,22100.,13397.,35.,26469.,35.,57340.,52956.,56812.,57069.,61167.,52718.,65499.,65535.));
	if(tex_y==37)col=getPix(tex_x,new mat4(9541.,34385.,16672.,30548.,18023.,8210.,17237.,307.,65533.,61134.,61439.,65519.,33849.,61437.,65228.,65263.));
	if(tex_y==38)col=getPix(tex_x,new mat4(20737.,26471.,25616.,26470.,9574.,16386.,13684.,8209.,43743.,43691.,52683.,43963.,1.,59152.,64479.,56799.));
	if(tex_y==39)col=getPix(tex_x,new mat4(0.,14723.,25137.,26214.,4933.,20738.,17767.,12307.,52479.,52427.,56524.,52444.,2.,86.,48632.,61438.));
	}
	else if(tex_y<50.){
	if(tex_y==40)col=getPix(tex_x,new mat4(1.,2384.,25616.,25958.,13126.,25089.,13926.,16404.,61439.,65262.,65262.,65006.,16649.,22373.,64560.,65243.));
	if(tex_y==41)col=getPix(tex_x,new mat4(3.,5952.,25104.,17765.,16949.,25347.,9320.,16405.,65535.,65535.,65535.,65279.,24623.,38518.,24618.,52445.));
	if(tex_y==42)col=getPix(tex_x,new mat4(70.,26144.,20992.,25956.,9542.,25089.,21864.,16421.,65535.,52446.,48332.,64972.,4271.,30549.,647.,22096.));
	if(tex_y==43)col=getPix(tex_x,new mat4(87.,38448.,16384.,30053.,12614.,33538.,22376.,17188.,43983.,52154.,56797.,65262.,1535.,21841.,14741.,8466.));
	if(tex_y==44)col=getPix(tex_x,new mat4(3.,42304.,17409.,30308.,9303.,33536.,30311.,12596.,56799.,60893.,65518.,65535.,24575.,25088.,34645.,30838.));
	if(tex_y==45)col=getPix(tex_x,new mat4(1.,38480.,8195.,21844.,9541.,25346.,26470.,4132.,61438.,65534.,61439.,56798.,65535.,4119.,33826.,30777.));
	if(tex_y==46)col=getPix(tex_x,new mat4(0.,33824.,8196.,26196.,9285.,33537.,22153.,323.,65531.,61439.,52172.,56797.,65534.,14559.,12561.,257.));
	if(tex_y==47)col=getPix(tex_x,new mat4(4097.,21025.,8196.,30291.,17750.,29203.,26489.,308.,65528.,43981.,60620.,65262.,61183.,65535.,52463.,52428.));
	if(tex_y==48)col=getPix(tex_x,new mat4(4096.,29489.,72.,26450.,13687.,16404.,26745.,36.,44786.,52667.,65245.,61183.,61436.,65534.,65535.,65535.));
	if(tex_y==49)col=getPix(tex_x,new mat4(3.,16914.,5492.,34644.,18022.,16386.,26503.,565.,61328.,61166.,61438.,52429.,65501.,60894.,61165.,65278.));
	}
	else if(tex_y<60.){
	if(tex_y==50)col=getPix(tex_x,new mat4(5.,17425.,5735.,30256.,26231.,12306.,30599.,4932.,65312.,65535.,48351.,56796.,65518.,52447.,56797.,56797.));
	if(tex_y==51)col=getPix(tex_x,new mat4(8214.,21554.,14388.,25650.,39063.,8740.,25975.,13413.,63232.,57343.,52411.,61150.,65263.,56831.,56524.,52684.));
	if(tex_y==52)col=getPix(tex_x,new mat4(4407.,8465.,25735.,38689.,29560.,313.,22421.,5238.,40960.,43727.,56780.,65534.,60894.,61183.,52429.,56780.));
	if(tex_y==53)col=getPix(tex_x,new mat4(26487.,599.,31024.,6242.,4096.,825.,30818.,4660.,16384.,56527.,61149.,52991.,60892.,61439.,56798.,65245.));
	if(tex_y==54)col=getPix(tex_x,new mat4(21880.,18054.,41984.,52.,32.,599.,30529.,294.,16400.,61439.,65535.,52446.,65261.,65535.,56815.,65518.));
	if(tex_y==55)col=getPix(tex_x,new mat4(25362.,30563.,28691.,4.,99.,9109.,22352.,278.,8242.,65535.,53247.,60875.,65518.,65500.,61183.,20222.));
	if(tex_y==56)col=getPix(tex_x,new mat4(57288.,35164.,22.,4151.,871.,9633.,29744.,53.,12579.,65535.,44015.,65245.,56831.,65245.,65535.,975.));
	if(tex_y==57)col=getPix(tex_x,new mat4(25975.,39501.,4.,12561.,5786.,1424.,37648.,8261.,4404.,65535.,56476.,65533.,56527.,61421.,53247.,8453.));
	if(tex_y==58)col=getPix(tex_x,new mat4(323.,25169.,4.,29952.,5785.,10080.,33552.,20517.,4117.,48638.,56538.,65535.,60859.,52991.,1503.,8704.));
	if(tex_y==59)col=getPix(tex_x,new mat4(1718.,8836.,4.,18224.,9874.,14384.,29200.,21253.,37.,51692.,65499.,48895.,65259.,60639.,28.,8704.));
	}
	else if(tex_y<70.){
	if(tex_y==60)col=getPix(tex_x,new mat4(17745.,13912.,2.,1104.,14208.,23072.,29472.,25349.,21.,61178.,65533.,52207.,65261.,60879.,2.,17456.));
	if(tex_y==61)col=getPix(tex_x,new mat4(25395.,14248.,0.,0.,23088.,22545.,37697.,29717.,309.,65526.,65535.,56509.,61422.,28652.,26928.,8480.));
	if(tex_y==62)col=getPix(tex_x,new mat4(26420.,10273.,0.,0.,22272.,22528.,41808.,29956.,4407.,65523.,57343.,60875.,52991.,2812.,22948.,8770.));
	if(tex_y==63)col=getPix(tex_x,new mat4(9266.,13317.,0.,0.,29440.,14081.,29986.,25363.,615.,65505.,44543.,65244.,52223.,13038.,18568.,22561.));
	if(tex_y==64)col=getPix(tex_x,new mat4(19233.,12437.,39520.,344.,16384.,5380.,18452.,34113.,854.,65456.,51919.,65500.,56527.,37247.,26729.,33298.));
	if(tex_y==65)col=getPix(tex_x,new mat4(52289.,9471.,65456.,48895.,4.,13316.,5906.,26144.,9334.,65392.,56493.,65517.,61118.,30748.,38776.,12836.));
	if(tex_y==66)col=getPix(tex_x,new mat4(8736.,3013.,65440.,65517.,959.,16419.,8770.,35393.,13159.,60960.,64986.,65535.,64987.,34629.,34153.,4937.));
	if(tex_y==67)col=getPix(tex_x,new mat4(43922.,10004.,65440.,60621.,28671.,0.,4624.,34947.,4936.,64256.,65534.,53247.,49131.,34163.,26215.,22135.));
	if(tex_y==68)col=getPix(tex_x,new mat4(29457.,5002.,65408.,56797.,65517.,7.,30032.,25992.,13638.,62723.,65535.,48639.,24557.,25942.,30054.,34679.));
	if(tex_y==69)col=getPix(tex_x,new mat4(4166.,23138.,65376.,52446.,65262.,127.,31168.,25975.,17494.,49169.,65535.,56047.,18941.,21335.,21588.,17783.));
	}
	else if(tex_y<80.){
	if(tex_y==70)col=getPix(tex_x,new mat4(31948.,34592.,65333.,52463.,64988.,2047.,39216.,30855.,17238.,20497.,65535.,56783.,26623.,17238.,12852.,548.));
	if(tex_y==71)col=getPix(tex_x,new mat4(60911.,29703.,64774.,52719.,57053.,32767.,28928.,30873.,17495.,19.,65534.,61116.,18127.,4660.,4656.,0.));
	if(tex_y==72)col=getPix(tex_x,new mat4(61439.,24893.,62996.,56815.,61132.,65535.,636.,27011.,17493.,19.,61430.,65243.,9119.,292.,0.,29762.));
	if(tex_y==73)col=getPix(tex_x,new mat4(65263.,16525.,54168.,52719.,65244.,52991.,49134.,29461.,21862.,580.,57280.,65517.,4685.,1.,55906.,65535.));
	if(tex_y==74)col=getPix(tex_x,new mat4(64991.,8668.,33401.,52991.,65244.,61439.,65229.,14831.,26211.,12613.,65328.,65535.,41.,46592.,65535.,65535.));
	if(tex_y==75)col=getPix(tex_x,new mat4(64991.,4828.,13418.,57087.,65500.,61167.,56799.,61421.,22171.,4404.,63488.,65535.,6.,65493.,48639.,65534.));
	if(tex_y==76)col=getPix(tex_x,new mat4(65534.,16859.,9591.,57340.,65516.,60879.,53247.,60875.,48894.,9321.,49152.,57343.,45824.,65535.,48895.,65531.));
	if(tex_y==77)col=getPix(tex_x,new mat4(61437.,24811.,1639.,57336.,65533.,56799.,65518.,52175.,65246.,27887.,8212.,28670.,65408.,65517.,57343.,65483.));
	if(tex_y==78)col=getPix(tex_x,new mat4(53246.,28893.,1094.,57335.,61439.,52991.,61149.,53247.,56523.,65534.,925.,7923.,65531.,65213.,61439.,64956.));
	if(tex_y==79)col=getPix(tex_x,new mat4(53246.,24766.,599.,61176.,57087.,61438.,56779.,65535.,52174.,65261.,53247.,52118.,65535.,52191.,61439.,60365.));
	}
	return col;
	}
	
	public vec3 getRGBColor(double xt,double yt)
	{


		int n=6; 
		int v=4;

		int pattern_width = 64;
		int pattern_height = 80;


		double mas = pattern_height / (Math.sqrt(2.-Math.sqrt(3.))*Math.sqrt(3.)/2.);

		boolean vert_move = true;

		vec3 col = new vec3(0.0, .0, .0);

		int tex_x,tex_y;
		double u1=0.,u2=0.;
		double rrk,rr,d,dk,c,s,u,uu;
		double ca,sa,k_1,k_2,rk5;
		double rxyk,rk,rk2,rmk,rmk0,xm,ym;
		rmk=0.0;
		rmk0=0.0;
		double tt,tf,pn,r1k,r1,h,rnk1,rnk2,rn1,count,tmp,fz;
		count=0.0;
		double euc0,euc1,euc2,hyp0,hyp1,hyp2;

		vec2 kn,kn2,k0,k1,k2,k5,km,kk;
		kn=new vec2(0.0);
		kn2=new vec2(0.0);
		k0=new vec2(0.0);
		k1=new vec2(0.0);
		k2=new vec2(0.0);
		k5=new vec2(0.0);
		km=new vec2(0.0);
		kk=new vec2(0.0);
		double pi = 3.14159265;

		double per=10.;
		double kseq = 2. * G.mod(time,per)/per - 1.;

		kk=new vec2(xt,yt).multiply(zoom);
		
/*		vec2 u_mouse=new vec2(shift*resolutionY);


		double r = resolutionY / 2. - 10.;
		//		double r = resolutionY / 2. - 10.;
		//k0=new vec2(resolutionX/2.,resolutionY/2.);
		//kk=new vec2((xt-k0.x)/r,(yt-k0.y)/r);

		if (u_mouse.x > 0.){
			double r_old=r;
			double iry=resolutionY;
			r*=(iry+5.)/(iry+5.-G.abs(u_mouse.y*2.-iry));
			k0.y+=(r-r_old)*G.sign(iry/2.-u_mouse.y);
			mas = pattern_height / (Math.sqrt(2.-Math.sqrt(3.))*Math.sqrt(3.)/2.);
		}*/

		tt=Math.tan(pi/n);
		tf=Math.tan(pi/v);

		euc0 = vert_move ? 0. : Math.sqrt(1.-tf*tf*tt*tt);
		hyp0 = (1. - euc0) / (1. + euc0);
		euc1 = (1. - hyp0) / (1. + hyp0);
		u = Math.pow((1. - euc1) / (1. + euc1), kseq);
		kn.x = (1. - u) / (1. + u);

		euc0 = vert_move ? Math.sqrt((1.-tf*tf*tt*tt)/(tf*tf*(1.+tt*tt))) : 0.;
		hyp0 = (1. - euc0) / (1. + euc0);
		euc2 = (1. - hyp0) / (1. + hyp0);
		u = G.pow((1. - euc2) / (1. + euc2), kseq);
		kn.y = (1. - u) / (1. + u);


		pn=2.*pi/n;
		r1k=tt*tt*(1.+tf*tf)/(1.-tt*tt*tf*tf);
		r1=G.sqrt(r1k);
		h=G.sqrt(r1k+1.);
		rnk1=G.dot(kn,kn);
		rn1=G.sqrt(rnk1);
		kn2=(kn.multiply(2.)).division((1.+rnk1));
		rnk2=G.dot(kn2,kn2);
		rk=1.;




		rrk=G.dot(kk,kk);

		if(rrk<rk)
		{

			rr=Math.sqrt(rrk);
			d=(1.-rr)/(1.+rr);
			dk=d*d;
			if(rr!=0.)
			{
				c=kk.x/rr;
				s=kk.y/rr;}
			else {
				c=1.;
				s=0.;
			}
			u=Math.sqrt((rk-rnk1)*(rk-rnk1)+4.*rk*(kn.y*c-kn.x*s)*(kn.y*c-kn.x*s));
			ca=(c*(rk-kn.x*kn.x+kn.y*kn.y)-2.*s*kn.x*kn.y)/u;
			sa=(s*(rk+kn.x*kn.x-kn.y*kn.y)-2.*c*kn.x*kn.y)/u;
			tmp=kn2.x*ca+kn2.y*sa;
			k_1=-tmp+Math.sqrt(tmp*tmp-rnk2+rk);
			k_2=-tmp-Math.sqrt(tmp*tmp-rnk2+rk);
			u=k_1*k_2*(dk-1.)/(dk*k_1-k_2);
			k5.x=kn2.x+u*ca; 
			k5.y=kn2.y+u*sa; 
			rk5=G.dot(k5,k5);
			if(rk5>rk)
				rk5=rk;
			uu=1./(1.+Math.sqrt(rk-rk5));
			k1=k5.multiply(uu);
			rxyk=G.dot(k1,k1);

			rmk=rxyk;
			for(int count_=0; count_<20; count_++)
			{
				count=count_;
				rmk0=rk;km=k1;
				for(int k=0; k<n; k++)
				{
					double si = Math.sin(2.*pi*k/n);
					double ci = Math.cos(2.*pi*k/n);
					k2 = k1.times(new mat2(ci,-si,si,ci));
					double k_ = r1k / (k2.x*k2.x+(k2.y-h)*(k2.y-h));
					k2.x *= k_;
					k2.y = k_*(k2.y-h)+h;
					rk2 = G.dot(k2,k2);
					if(rk2<rmk0) 
					{
						rmk0=rk2;
						km=k2.times(new mat2(ci,si,-si,ci));
					}
				}

				if( (rmk0>rmk) )
					break;
				rmk = rmk0; 
				k1 = km;
			}

			u1 = Math.sqrt(rmk);
			u2 = G.atan2(k1.y,k1.x);
			if(u2<0.) u2 += 2.*pi;

			if(G.mod(count,2.)==0.)
				u2 += pn*0.5; 
			else 
				u2 += pn*1.5;		

			u2 -= 2. * pn * G.floor(u2 / (2. * pn)); 
			if(u2 > pn)
				u2 = 2. * pn - u2;

			tex_x = (int) G.mod(G.floor(u1*mas*G.sin(u2)),pattern_width);
			tex_y = (int) G.mod(G.floor(u1*mas*G.cos(u2)),pattern_height);
			col = getColor(tex_x,tex_y);
		} 	
		return new vec3(col);
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
	   			 uV.x=2.0*pContext.random()-1.0;
				 uV.y=2.0*pContext.random()-1.0;
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
		return "dc_escher";
	}

	public String[] getParameterNames() {
		 return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { shift,seed,time,zoom},super.getParameterValues());
	}


	
	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_SHIFT)) {
			
			shift = Tools.limitValue(pValue, 0.0 , 1.);
		}
		else if (PARAM_SEED.equalsIgnoreCase(pName)) 
		{	   seed =  (int) pValue;
		       randomize=new Random(seed);
		          long current_time = System.currentTimeMillis();
		          elapsed_time += (current_time - last_time);
		          last_time = current_time;
		          time = (double) (elapsed_time / 1000.0);

	    }
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
	
			time = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			
			zoom = pValue;
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
	
}

