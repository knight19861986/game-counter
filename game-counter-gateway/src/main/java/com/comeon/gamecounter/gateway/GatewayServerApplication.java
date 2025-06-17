package com.comeon.gamecounter.gateway;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class GatewayApplication extends Application<GatewayConfiguration> {
    public static void main(String[] args) throws Exception {
        new GatewayApplication().run(args);
    }

    @Override
    public String getName() {
        return "game-counter-gateway";
    }

    @Override
    public void initialize(Bootstrap<GatewayConfiguration> bootstrap) {

    }

    @Override
    public void run(GatewayConfiguration gatewayConfiguration, Environment environment) throws Exception {
        final GatewayResource resource = new GatewayResource();
        environment.jersey().register(resource);
    }
}
