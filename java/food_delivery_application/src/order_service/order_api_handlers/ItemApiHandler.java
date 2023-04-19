package order_service.order_api_handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import order_service.kafka_handlers.OrderConsumer;
import order_service.kafka_handlers.OrderProducer;
import order_service.models.ItemObject;

import java.util.List;

import org.jose4j.json.internal.json_simple.JSONObject;

public class ItemApiHandler {
    private static OrderConsumer consumer = new OrderConsumer();
    static {
        consumer.start();
    }

    public static class GetItemsHandler implements HttpHandler{

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                List<ItemObject> items = consumer.getItemsFast();
                // Json variable containing all the items
                JSONObject itemsJson = new JSONObject();
                // Json variable containing a single item
                JSONObject itemJson = new JSONObject();
                
                for (ItemObject item : items) {
                    // Use the offset as the key for the item
                    itemJson.put(item.getOffset(), item.toJson());
                }
                itemsJson.put("items", itemJson);

                String response = itemsJson.toJSONString();
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream responseBody = exchange.getResponseBody();
                responseBody.write(response.getBytes());
                responseBody.close();
            } else {
                exchange.sendResponseHeaders(405, -1); // method not allowed
            }
        }
    }

    public static class PostNewItemHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStream requestBody = exchange.getRequestBody();
                String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
                ItemObject item = ItemObject.deserialize("", body);
                OrderProducer.addItem(item);
                
                String response = "Item added";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream responseBody = exchange.getResponseBody();
                responseBody.write(response.getBytes());
                responseBody.close();
            } else {
                exchange.sendResponseHeaders(405, -1); // method not allowed
            }
        }
    }
    



}