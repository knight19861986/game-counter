package com.comeon.gamecounter.gateway.legacy;

import com.comeon.gamecounter.gateway.legacy.handlers.HitGameHandler;
import com.comeon.gamecounter.gateway.legacy.handlers.LoginHandler;
import com.comeon.gamecounter.gateway.legacy.handlers.LogoutHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Server {
    private final static String address = "localhost";
    private final static int port = 8001;
    private final static int capacity = 1000;

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(address, port),0);
        httpServer.createContext("/login",new LoginHandler());
        httpServer.createContext("/logout",new LogoutHandler());
        httpServer.createContext("/hitgame",new HitGameHandler());
        httpServer.setExecutor(Executors.newFixedThreadPool(capacity));
        httpServer.start();
    }
}

