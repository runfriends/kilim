/* Copyright (c) 2006, Sriram Srinivasan
 *
 * You may distribute this software under the terms of the license 
 * specified in the file "License"
 */

package kilim.tools;

import me.jor.util.Log4jUtil;

import org.apache.commons.logging.Log;

// Various print routines to call from jvml files (*.j). More convenient
// than calling System.out.println.

public class P {
	private static final Log log=Log4jUtil.getLog(P.class);
    // Call as invokestatic  kilim/tools/P/pi(I)V
    public static void pi(int i) {
        log.info(i);
    }

    // Call as invokestatic kilim/tools/P/pn()V
    public static void pn() {
        log.info("");
    }

    // Call as invokestatic kilim/tools/P/pn(Ljava/lang/Object;)V
    public static void pn(Object o) {
        log.info(o);
    }

    // Call as invokestatic  kilim/tools/P/p(Ljava/lang/Object;)V
    public static void p(Object o) {
        log.info(o);
    }

    // Call as invokestatic kilim/tools/P/ps(Ljava/lang/Object;)V
    public static void ps(Object o) {
        log.info(o);
        log.info(" ");
    }
    // Call as invokestatic kilim/tools/P/ptest()V
    public static void ptest() {
        log.info("test");
    }

}
