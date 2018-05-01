package com.gm.virtualization.application.filter;

import org.apache.commons.logging.Log;

public abstract class SlaUtils {

    public static void slaLogging(Log log, long time, String name) {
        if (time > 15) {
            log.info("Method " + name + " SLA performance not reached");
            log.warn("Method execution longer than 15 ms! Actual time taken = " + time + " ms");
        } else if (log.isTraceEnabled()) {
            log.trace("Method " + name + " SLA performance reached");
            log.trace("Method execution less than 15 ms! Actual time taken = " + time + " ms");
        }
    }
}
