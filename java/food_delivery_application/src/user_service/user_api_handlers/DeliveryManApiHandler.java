package user_service.user_api_handlers;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.List;
import org.jose4j.json.internal.json_simple.JSONObject;

import user_service.UserConsumer;
import user_service.UserProducer;
import user_service.models.DeliveryManObject;



public class DeliveryManApiHandler {

    public static class GetDeliveryMenHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                // get the query string
                String queryString = exchange.getRequestURI().getQuery();
                
                // do something with the query string
                List<DeliveryManObject> deliveryMen = UserConsumer.getDeliveryMen();
                JSONObject deliveryMenJson = new JSONObject();
                JSONObject deliveryManJson = new JSONObject();

                // ListCustomerObject customer = BasicConsumer.getCustomer(47);
                
                for (DeliveryManObject deliveryMan : deliveryMen) {
                    deliveryManJson.put(deliveryMan.getOffset(), deliveryMan.toJson());
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
