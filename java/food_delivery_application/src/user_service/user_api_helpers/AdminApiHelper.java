package user_service.user_api_helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import user_service.kafka_handlers.UserConsumer;
import user_service.kafka_handlers.UserProducer;
import user_service.models.AdminObject;
import user_service.models.CustomerObject;
import user_service.models.DeliveryManObject;

import java.util.HashMap;
import java.util.List;
// import com.google.gson.Gson;
import java.util.Map;

import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.ParseException;

public class AdminApiHelper {
    public static class GetAdminsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                // get the query string
                String queryString = exchange.getRequestURI().getQuery();
                
                // do something with the query string
                List<AdminObject> admins = UserConsumer.getAdmins();
                JSONObject adminsJson = new JSONObject();
                JSONObject adminJson = new JSONObject();

                // ListCustomerObject customer = BasicConsumer.getCustomer(47);
                
                for (AdminObject admin : admins) {
                    adminJson.put(admin.getOffset(), admin.toJson());
                }
                adminsJson.put("admins", adminJson);
                String response = adminsJson.toJSONString();
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
 
    public static class PostNewAdminHandler implements HttpHandler {

        // private BasicProducer userProducer = new BasicProducer();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                // read the request body
                InputStream requestBody = exchange.getRequestBody();
                
                byte[] requestBodyBytes = requestBody.readAllBytes();
                String requestBodyString = new String(requestBodyBytes, StandardCharsets.UTF_8);

                // send the response
                try {
                    AdminObject admin = AdminObject.deserialize("", requestBodyString);
                    UserProducer.addAdmin(admin);
                    String response = "new admin added correctly";
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    OutputStream responseBody = exchange.getResponseBody();
                    responseBody.write(response.getBytes());
                    responseBody.close();
                } catch (NullPointerException e) {
                    String response = "error on adding new admin";
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
