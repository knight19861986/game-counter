package com.comeon.gamecounter.gateway;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class GatewayServerStarter {
    private static Server server;

    public static void start(int port, boolean wait) throws Exception {
        server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        ResourceConfig config = new ResourceConfig()
                .packages("com.comeon.gamecounter.gateway.resources")
                .register(JacksonFeature.class);
        ServletHolder servletHolder = new ServletHolder(new ServletContainer(config));
        context.addServlet(servletHolder, "/*");

        server.start();
        System.out.println("Gateway server started on port " + port);

        if (wait) {
            server.join();
        }
    }

    public static void stop() throws Exception {
        if (server != null) {
            server.stop();
            System.out.println("Gateway server stopped");
        }
    }
}
