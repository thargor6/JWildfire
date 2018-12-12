package hehl.gl;
/**
handles angles in various units (degrees, grades, dms, radians, semicircles).
author    hehl@tfh-berlin.de
version   17-May-2005
*/
public final class Angle  {

/** global constant (2 * Pi) = 6.283185307179586... */
        public static final double TWOPI = 2.*Math.PI;
/** global constant 'rho' in degrees */
        public static final double RHODEG = 180. / Math.PI;
/** global constant 'rho' in grades */
        public static final double RHOGON = 200. / Math.PI;
/** global constant 'rho' in 'semi circles' (Trimble) */
        public static final double RHOSEMI = 1. / Math.PI;
/** unit  'radians' [-2pi..2pi] */
        public static final int RAD = 0;
/** unit  'degrees' [-360..+360] */
        public static final int DEG = 1;
/** unit  'grades' [-400..+400] */
        public static final int GON = 2; // grades (german: gon)
/** unit  'grades' [-400..+400] */
        public static final int GRA = 2; // grades
/** unit  'degrees minutes seconds' */
        public static final int DMS = 3;
/** unit  'semicircles' [-2..+2] */
        public static final int SEMI = 4;

/** private data */
        private double  _radians; // angle in radians
        private int             _unit; // defining unit on angle

/**
constructs an angle 0 rad
*/
        public Angle()  {
                _radians = 0.0;
                _unit = RAD;
        }
/**
constructs an angle in default unit 'grades'.
@param x angle in grades
@exception Msg
*/
        public Angle(double x) {
                _radians = (x/RHOGON) % TWOPI;
                _unit = GON;
        }

/**
constructs an angle with given unit RAD, DEG, GON, etc.
@param x value of the angle
@param unit unit of: RAD, DEG, GON, DMS or SEMI
@exception Msg
*/
        public Angle(double x, int unit) throws Msg {
          String r = "Angle.constructor";
                switch(unit) {
                        case RAD : _radians = x; break;
                        case DEG :
                        case DMS : _radians = x/RHODEG; break;
                        case GON : _radians = x/RHOGON; break;
                        case SEMI: _radians = x/RHOSEMI; break;
                        default:  throw new Msg(r,"invalid angle unit "+unit);
                }
                _radians %= TWOPI;
                _unit = unit;
        }
/**
constructs an angle in (degrees, minutes, seconds) format
@param d degrees in [-359..359]
@param m minutes [0..59]
@param s seconds [0.0 to 59.999999999999]
@throws Msg
*/
        public Angle(int d, int m, double s) throws Msg {
                this((double)d + (double)m/60. + s/3600.0,DMS);
        }
/**
* Returns value of current angle in radians
* @return value of current angle in radians
*/
        public double rad() {
                return _radians;
        }
/**
* Returns value of current angle in degrees.
* @return value of current angle in degrees
*/
        public double deg() {
                return rad()*RHODEG;
        }
/**
* Returns value of current angle in grades.
* @return value of current angle in grades
*/
        public double gon() {
                return rad()*RHOGON;
        }
/**
* Returns value of current angle in grades.
* @return value of current angle in grades
*/
        public double gra() {
                return gon();
        }
/**
/**
* Returns value of current angle in milligrades.
* @return value of current angle in milligrades
*/
        public double mgon() {
                return gon()*1000.0;
        }
/**
* Returns value of current angle in seconds of arc.
* @return value of current angle in seconds of arc
*/
        public double arcsec() {
                return deg()*3600.0;
        }
/**
* Returns value of current angle in semi-circles (Trimble).
* @return value of current angle in semi-circles
*/
        public double semi() {
                return rad()*RHOSEMI;
        }
/**
* Computes the sine of the argument.
* @param x an angle
* @return sine of the argument
*/
        public static double sin(Angle x) {
                return Math.sin(x.rad());
        }
/**
* Computes the cosine of the argument.
* @param x an angle
* @return cosine of the argument
*/
        public static double cos(Angle x) {
                return Math.cos(x.rad());
        }
/**
* Computes the sine of current angle.
* @return sine of current angle
*/
        public double sin() {
                return Math.sin(this.rad());
        }
/**
* Computes the cosine of current angle.
* @return cosine of current angle
*/
        public double cos() {
                return Math.cos(this.rad());
        }
/**
* @return angle in degrees, minutes, and seconds in String respresentation
*/
        public String toDMS() {
                double x = deg();
                boolean negative = (x < 0.0);
                if(negative) x = -x;
                int d = (int)x;
            x = (x-d)*60.;
            int m = (int)x;
            double s = (x-m)*60.0;
            String result = String.format("%3dd %02dm %012.9fs",d,m,s);
            if(negative) result = "-"+result;
                return result;
        }
/**
* @return angle in same unit as in construction
*/
        public String toString() {
                String result;
                switch(_unit) {
                        case RAD: result = "["+rad()+" rad]"; break;
                        case DEG: result = String.format("[%12.9f deg]",deg()); break;
                        case GON: result = "["+gon()+" gon]"; break;
                        case DMS: result = "["+toDMS()+"]"; break;
                        case SEMI:result = "["+semi()+" semi circles]"; break;
                        default : result = "[** invalid **]";
                }
                return result;
        }
/**
adds the right hand side to current angle and returns their sum
@param rhs right hand side angle
@return sum of current and rhs angle
*/
        public Angle add(Angle rhs) {
                _radians += rhs.rad();
                _radians %= TWOPI;
                return this;
        }

/** demonstrates functionality of class 'Angle' */
        public static void demo()  {
          try {
                System.out.println("*****************************");
                System.out.println("*** Test of Class 'Angle' ***");
                System.out.println("*****************************");
                System.out.println();

                Angle alpha = new Angle(Math.PI/2.,Angle.RAD); // in radians
                Angle beta  = new Angle(400.0,Angle.GON);      // in grades
                Angle gamma = new Angle(45.,Angle.DEG);        // in degrees
                Angle delta = new Angle(52,30,0.0);            // in DMS
                Angle eps   = new Angle(100.);                 // in grades

                System.out.println("alpha= "+alpha);
                System.out.println("beta = "+beta);
                System.out.println();
                System.out.println("sin"+gamma+"= "+Math.sin(gamma.rad()));
                System.out.println("sin"+gamma+"= "+gamma.sin());
                System.out.println("sin"+gamma+"= "+Angle.sin(gamma));
                System.out.println();
                System.out.println("delta= "+delta);
                System.out.println("eps  = "+eps);
                System.out.println("anonymous angle= "+new Angle(47.43,Angle.DEG));
                }
                catch(Exception ex) { }
        }
} // end class Angle