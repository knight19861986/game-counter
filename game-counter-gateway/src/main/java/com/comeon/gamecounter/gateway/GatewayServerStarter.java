package com.comeon.gamecounter.gateway;

import com.comeon.gamecounter.gateway.resources.GatewayResource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class GatewayServerStarter {
    private static Server server;

    public static void start(int port,
                             String coreHost,
                             int corePort,
                             String coreBasePath,
                             boolean wait) throws Exception {
        server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        ResourceConfig config = new ResourceConfig();
        GatewayResource resource = new GatewayResource(coreHost, corePort, coreBasePath);
        config.register(resource);
        config.register(JacksonFeature.class);
        config.register(org.glassfish.jersey.logging.LoggingFeature.class);

        ServletHolder servletHolder = new ServletHolder(new ServletContainer(config));
        context.addServlet(servletHolder, "/*");

        server.start();
        System.out.printf(
                "Gateway server started on port %d, connecting to core %s%n",
                port,
                String.format("http://%s:%d%s", coreHost, corePort, coreBasePath)
        );

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
