package user_service.user_api_helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import user_service.kafka_handlers.UserConsumer;
import user_service.kafka_handlers.UserProducer;
import user_service.models.AdminObject;
import user_service.models.CustomerObject;
import user_service.models.DeliveryManObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
// import com.google.gson.Gson;
import java.util.Map;

import org.jose4j.json.internal.json_simple.JSONObject;

public class CustomerApiHelper {
    public static class GetCustomersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Access-Control-Allow-Headers","x-prototype-version,x-requested-with");
                headers.add("Access-Control-Allow-Methods","GET,POST");
                headers.add("Access-Control-Allow-Origin","*");
                // get the query string
                String queryString = exchange.getRequestURI().getQuery();
                
                // do something with the query string
                List<CustomerObject> customers = UserConsumer.getCustomers();
                JSONObject customersJson = new JSONObject();
                // JSONObject customerJson = new JSONObject();
                ArrayList<JSONObject> customerJson = new ArrayList<JSONObject>();
                // ListCustomerObject customer = BasicConsumer.getCustomer(47);
                
                for (CustomerObject customer : customers) {
                    // customerJson.put(customer.getOffset(), customer.toJson());
                    customerJson.add(customer.toJson());
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

    public static class PostNewCustomerHandler implements HttpHandler {

        // private BasicProducer userProducer = new BasicProducer();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Access-Control-Allow-Headers","x-prototype-version,x-requested-with");
                headers.add("Access-Control-Allow-Methods","GET,POST");
                headers.add("Access-Control-Allow-Origin","*");
                // read the request body
                InputStream requestBody = exchange.getRequestBody();
                
                byte[] requestBodyBytes = requestBody.readAllBytes();
                String requestBodyString = new String(requestBodyBytes, StandardCharsets.UTF_8);
                // HashMap<String, String> map = g.fromJson(requestBodyString, HashMap.class);
                // do something with the request body
                
                // send the response


                
                try {
                    
                    CustomerObject customer = CustomerObject.deserialize("", requestBodyString);
                    UserProducer.addCostumer(customer);
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
