package net.ssehub.rightsmanagement.rest;
import java.io.Closeable;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import net.ssehub.rightsmanagement.conf.Settings;

/**
 * Simple rest server that listens for changes on the Student-Management-System.
 * Based on <a href="https://www.dovydasvenckus.dev/rest/2017/08/20/jersey-on-embedded-jetty/">
 * https://www.dovydasvenckus.dev/rest/2017/08/20/jersey-on-embedded-jetty</a>
 * @author El-Sharkawy
 *
 */
public class RestServer implements Closeable {
    private static final String REST_PATH = "/rest/*";
    private Server server;
    
    public RestServer(int port) {
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
        } catch (Exception ex) {
            server.destroy();
            System.exit(1);
        }
    }
    
    @Override
    public void close() {
        server.destroy();
    }
    

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
    
    public static void main(String[] args) {
        new RestServer(Settings.INSTANCE.getAsInt("server.listen.port"));
    }

}
