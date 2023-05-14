package order_service.order_api_helpers;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import order_service.kafka_handlers.OrderConsumer;
import order_service.kafka_handlers.OrderProducer;
import order_service.models.ItemObject;
import order_service.models.ItemTable;

import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;

public class ItemApiHelper {

    public static OrderConsumer consumer;

    public ItemApiHelper(OrderConsumer cons) {
        ItemApiHelper.consumer = cons;
    }

    public static class GetItemsHandler implements HttpHandler{

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Access-Control-Allow-Headers","x-prototype-version,x-requested-with");
                headers.add("Access-Control-Allow-Methods","GET,POST");
                headers.add("Access-Control-Allow-Origin","*");
                List<ItemObject> items = consumer.getItemsFast();
                // Json variable containing all the items
                JSONObject itemsJson = new JSONObject();
                // Json variable containing a single item
                ArrayList<JSONObject> itemJson = new ArrayList<JSONObject>();
                for (ItemObject item : items) {
                    // Use the offset as the key for the item
                    itemJson.add(item.toJson());
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
                Headers headers = exchange.getResponseHeaders();
                headers.add("Access-Control-Allow-Headers","x-prototype-version,x-requested-with");
                headers.add("Access-Control-Allow-Methods","GET,POST");
                headers.add("Access-Control-Allow-Origin","*");
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
    
    public static class PutItemsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            if ("PUT".equals(exchange.getRequestMethod())) {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Access-Control-Allow-Headers","x-prototype-version,x-requested-with");
                headers.add("Access-Control-Allow-Methods","GET,POST");
                headers.add("Access-Control-Allow-Origin","*");
                InputStream requestBody = exchange.getRequestBody();
                String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
            
                JSONObject json = new JSONObject();
                JSONParser parser = new JSONParser();
                try {
                    json = (JSONObject) parser.parse(body);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                json = (JSONObject) json.get("items");
                for (Object key : json.keySet()) {
                    JSONObject itemJson = (JSONObject) json.get(key);
                    ItemObject item = ItemObject.createNew(key.toString(), itemJson.get("name").toString(), Long.parseLong(itemJson.get("quantity").toString()));
                    OrderProducer.addItem(item);
                }
                // ItemObject item = ItemObject.deserialize("", body);
                // OrderProducer.updateItem(item);
                
                String response = "Item updated";
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