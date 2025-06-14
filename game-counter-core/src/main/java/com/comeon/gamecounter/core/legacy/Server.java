package com.comeon.gamecounter.core.legacy;

import com.sun.net.httpserver.HttpServer;
import com.comeon.gamecounter.core.legacy.handlers.HitGameHandler;
import com.comeon.gamecounter.core.legacy.handlers.LoginHandler;
import com.comeon.gamecounter.core.legacy.handlers.LogoutHandler;
import com.comeon.gamecounter.core.legacy.handlers.SigninHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Server {
    private final static String address = "localhost";
    private final static int port = 8002;
    private final static int capacity = 1000;

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(address, port),0);
        httpServer.createContext("/signin",new SigninHandler());
        httpServer.createContext("/login",new LoginHandler());
        httpServer.createContext("/logout",new LogoutHandler());
        httpServer.createContext("/hitgame",new HitGameHandler());
        httpServer.setExecutor(Executors.newFixedThreadPool(capacity));
        httpServer.start();
    }
}

