/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.peaks.cnvcat.util;

/**
 *
 * @author tebel
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.awt.Desktop;

/**
 * @author Thomas.Darimont
 *
 */
public class StreamPDFExample {

    public final static int WEB_SERVER_PORT = 4711;
    static HttpServer httpServer = null;

    /**
     * @param args
     */
    public static void browse(final String pdfPath) throws Exception {
        if (httpServer == null) {
            httpServer = HttpServer.create(new InetSocketAddress(
                    WEB_SERVER_PORT), 0);
            httpServer.createContext("/", new HttpHandler() {

                public void handle(HttpExchange httpExchange) throws IOException {
                    httpExchange.getResponseHeaders().add("Content-type",
                            "application/pdf");

                    httpExchange.sendResponseHeaders(200, 0);

                    OutputStream outputStream = httpExchange.getResponseBody();

                    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(pdfPath);

                    transfer(inputStream, outputStream);

                    outputStream.close();

                }
            });

            httpServer.start();
        }
        Desktop.getDesktop().browse(
                new URI("http://localhost:" + WEB_SERVER_PORT));



    }

    public static void stop() {
        httpServer.stop(1);
    }

    protected static long transfer(InputStream source, OutputStream destination)
            throws IOException {
        final byte[] buffer = new byte[16384];
        long bytesSent = 0;
        for (int len = source.read(buffer); len > 0; len = source.read(buffer)) {
            destination.write(buffer, 0, len);
            bytesSent += len;
        }
        destination.flush();
        return bytesSent;
    }
}
