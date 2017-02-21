package org.lgbt_news.analysis.util;

import org.apache.log4j.Logger;

/**
 * @author max
 */
public class ExceptionHandler {

    private static Logger logger = Logger.getLogger("infoLogger");

    public static void processFatalException(Object o, Exception e) {
        processFatalException(o, e, "");
    }

    public static void processFatalException(Object o, Exception e, String additionalNote) {
        processException(o, e, additionalNote);
        System.err.println("--- end ---");
        System.exit(1);
    }

    public static void processException(Object o, Exception e) {
        processException(o, e, "");
    }

    public static void processException(Object o, Exception e, String additionalNote) {
        System.err.println(additionalNote);
        e.printStackTrace();
        additionalNote = (additionalNote.trim().length() > 0) ? "\t"+additionalNote : "";
        logger.error(o.getClass().getSimpleName()+"\t"+e.getMessage()+additionalNote);
    }
}
