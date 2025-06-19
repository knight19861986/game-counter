package com.comeon.gamecounter.gateway;

public class GatewayServerApplication {
    public static void main(String[] args) throws Exception {
        GatewayServerStarter.start(8080, true);
    }
}
