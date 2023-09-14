package order_service.order_api_helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import order_service.kafka_handlers.OrderConsumer;
import order_service.kafka_handlers.OrderProducer;
import order_service.models.ItemObject;
import order_service.models.ItemTable;
import order_service.models.OrderObject;

import java.util.Hashtable;
import java.util.List;

import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;

public class OrderApiHelper {
    
    public static OrderConsumer consumer;
    public OrderApiHelper(OrderConsumer cons) {
        OrderApiHelper.consumer = cons;
    }

    public static class PostNewOrderHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("POST /newvdsvsdvs-order");
            if ("POST".equals(exchange.getRequestMethod())) {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Access-Control-Allow-Headers","x-prototype-version,x-requested-with");
                headers.add("Access-Control-Allow-Methods","GET,POST");
                headers.add("Access-Control-Allow-Origin","*");
                InputStream requestBody = exchange.getRequestBody();
                String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Body: " + body);
                OrderObject order = OrderObject.deserialize(body);
                List<ItemObject> itemsFromQueue = consumer.getItemsFast();
                String response = "Order received";
                boolean proceedWithOrder = true;
                

                // This whole thing could have been done with a transaction but I'm short on time
                // This whole thing is a mess I know but it works so this will do for now 
                if (order.getItems().size() > 0) {
                    for (ItemObject item : order.getItems()) {
                        for (ItemObject item2 : itemsFromQueue) {

                            if (item.getName().equals(item2.getName())) {
                                if (item.getQuantity() > item2.getQuantity()) {
                                    response = "Not enough items in stock";
                                    proceedWithOrder = false;
                                }
                            }
                        }
                    }
                    if (proceedWithOrder) {
                        OrderProducer.addOrder(order);
                        for (ItemObject item : order.getItems()) {
                            for (ItemObject item2 : itemsFromQueue) {
                                if (item.getName().equals(item2.getName())) {
                                    final long quantity = item2.getQuantity() - item.getQuantity();
                                    OrderProducer.addItem(new ItemObject(item.getOffset(), item.getName(), quantity));
                                }
                            }
                        }
                    }
                }
            

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
