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
    public RestServer(int port) {
        LOGGER.info("Starting server on port: {}", port);
        Server server = new Server(port);
        
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);

        servletContextHandler.setContextPath("/");
        server.setHandler(servletContextHandler);

        ServletHolder servletHolder = servletContextHandler.addServlet(ServletContainer.class, REST_PATH);
        servletHolder.setInitOrder(0);
        servletHolder.setInitParameter(
                "jersey.config.server.provider.packages",
                "net.ssehub.rightsmanagement.rest.resources"
        );

        try {
            server.start();
            server.join();
            // checkstyle: stop exception type check (used interface of library throws exception)
        } catch (Exception ex) {
            // checkstyle: resume exception type check
            server.destroy();
            System.exit(1);
        }
    }
    
    @Override
    public void close() {
        server.destroy();
    }
    
}
