package hehl.gl;
/**
handles (error)messages thrown from package routines.
author    hehl@tfh-berlin.de
version   17-May-2005
*/
public class Msg extends Exception {
        private String _routine,_errortxt;

/**
constructs a message
@param routine name of the routine the message is thrown from
@param txt error text
*/
        public Msg(String routine, String txt) {
                _routine = "(routine)";
                if(null != routine && !routine.equals("")) _routine = routine;
                _errortxt = "(errortext)";
                if(null != txt && !txt.equals("")) _errortxt = txt;
        }

/**
@return formatted error message text
*/
        public String toString() {
                return "%%% message from ("+_routine+"): "+_errortxt;
        }

        public static void demo() {
                System.out.println("***************************");
                System.out.println("*** Test of Class 'Msg' ***");
                System.out.println("***************************");
                System.out.println();

                try {
                        if(1 == 1) throw new Msg("Msg.demo","test of user-defined exception");
                }
                catch(Msg msg) {
                        System.err.println(msg+"\n");
                }
                catch(Exception ex) {
                }
        }

} // end class Msg
