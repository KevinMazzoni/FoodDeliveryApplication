
// package com.stackoverflow.q3732109;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import models.CustomerObject;

import java.util.HashMap;
import java.util.List;
// import com.google.gson.Gson;
import java.util.Map;

import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.ParseException;


public class Test {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/customers", new GetCustomersHandler());
        server.createContext("/new-customer", new PostNewCustomerHandler());
        // server.createContext("/customers", new MyHandler());
        // server.createContext("/customers", new MyHandler());
        // server.createContext("/customers", new MyHandler());
        // server.createContext("/customers", new MyHandler());
        // server.createContext("/customers", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();

    }

    static class GetCustomersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                // get the query string
                String queryString = exchange.getRequestURI().getQuery();
                
                // do something with the query string
                List<CustomerObject> customers = BasicConsumer.getCustomers();
                JSONObject customersJson = new JSONObject();
                JSONObject customerJson = new JSONObject();

                // ListCustomerObject customer = BasicConsumer.getCustomer(47);
                
                for (CustomerObject customer : customers) {
                    customerJson.put(customer.getOffset(), customer.toJson());
                }
                customersJson.put("customers", customerJson);
                String response = customersJson.toJSONString();
                // String response = customer.serialize();
                // send the response
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream responseBody = exchange.getResponseBody();
                responseBody.write(response.getBytes());
                responseBody.close();
            } else {
                exchange.sendResponseHeaders(405, -1); // method not allowed
            }
        }
    }

    static class PostNewCustomerHandler implements HttpHandler {

        // private BasicProducer userProducer = new BasicProducer();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                // read the request body
                InputStream requestBody = exchange.getRequestBody();
                
                byte[] requestBodyBytes = requestBody.readAllBytes();
                String requestBodyString = new String(requestBodyBytes, StandardCharsets.UTF_8);
                // HashMap<String, String> map = g.fromJson(requestBodyString, HashMap.class);
                // do something with the request body
                
                // send the response


                
                try {
                    
                    CustomerObject customer = CustomerObject.deserialize("", requestBodyString);
                    BasicProducer.addCostumer(customer);
                    String response = "new customer added correctly";
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    OutputStream responseBody = exchange.getResponseBody();
                    responseBody.write(response.getBytes());
                    responseBody.close();
                
                // catch null pointer exception
                } catch (NullPointerException e) {
                    String response = "error on adding new customer";
                    exchange.sendResponseHeaders(500, response.getBytes().length);
                    OutputStream responseBody = exchange.getResponseBody();
                    responseBody.write(response.getBytes());
                    responseBody.close();
                }
                                // HashMap<String, String> map = g.fromJson(requestBodyString, HashMap.class);
                // CustomerObject customer = new CustomerObject(map.get("name"), map.get("surname"), map.get("address"));
                // System.out.println(customer.toString());
            } else {
                exchange.sendResponseHeaders(405, -1); // method not allowed
            }

        }
    }
}
