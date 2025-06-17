package com.comeon.gamecounter.gateway;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class GatewayServerApplication {
    public static void main(String[] args) throws Exception {
        ResourceConfig config = new ResourceConfig();
        config.packages("com.comeon.gamecounter.gateway.resources")
                .register(JacksonFeature.class);;

        ServletHolder servlet = new ServletHolder(new ServletContainer(config));
        Server server = new Server(8080);

        ServletContextHandler contextHandler = new ServletContextHandler(server, "/");
        contextHandler.addServlet(servlet, "/*");

        server.start();
        System.out.println("Server started at http://localhost:8080/");
        server.join();
    }
}
