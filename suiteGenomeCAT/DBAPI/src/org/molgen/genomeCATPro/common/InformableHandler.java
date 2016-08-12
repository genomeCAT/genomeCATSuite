/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.common;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 *
 * @author tebel
 */
public abstract class InformableHandler extends Handler implements Informable {

    public InformableHandler() {
        super();

    }

    /* (non-API documentation)
	 * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
     */
    public void publish(LogRecord record) {
        // ensure that this log record should be logged by this Handler
        if (!isLoggable(record)) {
            return;
        }

        // Output the formatted data to the file
        messageChanged(getFormatter().format(record));
    }

    /* (non-API documentation)
	 * @see java.util.logging.Handler#flush()
     */
    @SuppressWarnings("empty-statement")
    public void flush() {
        ;
    }

    /* (non-API documentation)
	 * @see java.util.logging.Handler#close()
     */
    @SuppressWarnings("empty-statement")
    public void close() throws SecurityException {
        ;
    }

}
