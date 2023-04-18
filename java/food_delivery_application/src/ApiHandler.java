
// package com.stackoverflow.q3732109;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

import user_service.user_api_handlers.AdminApiHandler.PostNewAdminHandler;
import user_service.user_api_handlers.CustomerApiHandler.GetCustomersHandler;
import user_service.user_api_handlers.CustomerApiHandler.PostNewCustomerHandler;
import user_service.user_api_handlers.AdminApiHandler.GetAdminsHandler;
import user_service.user_api_handlers.DeliveryManApiHandler.GetDeliveryMenHandler;
import user_service.user_api_handlers.DeliveryManApiHandler.PostNewDeliveryManHandler;

public class ApiHandler {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/customers", new GetCustomersHandler());
        server.createContext("/new-customer", new PostNewCustomerHandler());
        server.createContext("/new-admin", new PostNewAdminHandler());
        server.createContext("/admins", new GetAdminsHandler());
        server.createContext("/new-delivery-man", new PostNewDeliveryManHandler());
        server.createContext("/delivery-men", new GetDeliveryMenHandler());

        // server.createContext("/customers", new MyHandler());
        // server.createContext("/customers", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();

    }

    



}
