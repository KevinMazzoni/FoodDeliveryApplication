package user_service.user_api_helpers;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import user_service.kafka_handlers.UserConsumer;
import user_service.kafka_handlers.UserProducer;
import user_service.models.DeliveryManObject;

import java.util.ArrayList;
import java.util.List;
import org.jose4j.json.internal.json_simple.JSONObject;



public class DeliveryManApiHelper {

    public static class GetDeliveryMenHandler implements HttpHandler {
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
                List<DeliveryManObject> deliveryMen = UserConsumer.getDeliveryMen();
                JSONObject deliveryMenJson = new JSONObject();
                ArrayList<JSONObject> deliveryManJson = new ArrayList<JSONObject>();

                // ListCustomerObject customer = BasicConsumer.getCustomer(47);
                
                for (DeliveryManObject deliveryMan : deliveryMen) {
                    deliveryManJson.add(deliveryMan.toJson());
                }
                deliveryMenJson.put("deliveryMen", deliveryManJson);
                String response = deliveryMenJson.toJSONString();
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

    public static class PostNewDeliveryManHandler implements HttpHandler {
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

                // send the response
                try {
                    DeliveryManObject deliveryMan = DeliveryManObject.deserialize("", requestBodyString);
                    UserProducer.addDeliveryMan(deliveryMan);
                    String response = "new delivery man added correctly";
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    OutputStream responseBody = exchange.getResponseBody();
                    responseBody.write(response.getBytes());
                    responseBody.close();
                } catch (NullPointerException e) {
                    String response = "error on adding new delivery man";
                    exchange.sendResponseHeaders(500, response.getBytes().length);
                    OutputStream responseBody = exchange.getResponseBody();
                    responseBody.write(response.getBytes());
                    responseBody.close();
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // method not allowed
            }
        }
    }



}
