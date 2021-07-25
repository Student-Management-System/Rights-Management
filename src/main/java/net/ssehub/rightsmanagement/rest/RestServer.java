package net.ssehub.rightsmanagement.rest;
import java.io.Closeable;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * Simple rest server that listens for changes on the Student-Management-System.
 * Based on <a href="https://www.dovydasvenckus.dev/rest/2017/08/20/jersey-on-embedded-jetty/">
 * https://www.dovydasvenckus.dev/rest/2017/08/20/jersey-on-embedded-jetty</a>
 * @author El-Sharkawy
 *
 */
public class RestServer implements Closeable {
    private static final String REST_PATH = "/rest/*";

    private static final Logger LOGGER = Log.getLogger(RestServer.class);
    
    private Server server;
    
    /**
     * Creates a new {@link RestServer} instance.
     * @param port The port at which it should listen for new messages.
     */
    // checkstyle: stop exception type check (used interface of library throws exception)
    public RestServer(int port) throws Exception {
    // checkstyle: resume exception type check
        LOGGER.info("Starting server on port: {}", port);
        server = new Server(port);
        
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);

        servletContextHandler.setContextPath("/");
        server.setHandler(servletContextHandler);

        ServletHolder servletHolder = servletContextHandler.addServlet(ServletContainer.class, REST_PATH);
        servletHolder.setInitOrder(0);
        servletHolder.setInitParameter(
                "jersey.config.server.provider.packages",
                "net.ssehub.rightsmanagement.rest.resources"
        );

        server.start();
    }
    
    /**
     * Waits for the server to finish.
     * 
     * @throws InterruptedException If waiting fails.
     */
    public void join() throws InterruptedException {
        server.join();
    }
    
    @Override
    public void close() {
        server.destroy();
    }
    
}
