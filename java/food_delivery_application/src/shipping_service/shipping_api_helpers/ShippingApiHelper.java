package shipping_service.shipping_api_helpers;


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
import shipping_service.kafka_handlers.ShippingConsumer;
import shipping_service.kafka_handlers.ShippingProducer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;

public class ShippingApiHelper {
    static ShippingProducer shippingProducer = new ShippingProducer();
    static {
        shippingProducer.start();
    }

    public static class GetCustomerShippingsHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Access-Control-Allow-Headers","x-prototype-version,x-requested-with");
                headers.add("Access-Control-Allow-Methods","GET,POST");
                headers.add("Access-Control-Allow-Origin","*");
                String queryString = exchange.getRequestURI().getQuery();
                String userId = queryString.split("=")[1];
                List<OrderObject> orders = ShippingConsumer.getCustomerShippings(userId);
                JSONObject OrdersJson = new JSONObject();
                ArrayList<JSONObject> orderJson = new ArrayList<JSONObject>();
                for (OrderObject order : orders) {
                    orderJson.add(order.toJson());
                }
                OrdersJson.put("orders", orderJson);
                String response = OrdersJson.toJSONString();
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
    public static class GetShippingsHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Access-Control-Allow-Headers","x-prototype-version,x-requested-with");
                headers.add("Access-Control-Allow-Methods","GET,POST");
                headers.add("Access-Control-Allow-Origin","*");
                List<OrderObject> orders = ShippingConsumer.getShippings();
                JSONObject OrdersJson = new JSONObject();
                ArrayList<JSONObject> orderJson = new ArrayList<JSONObject>();
                for (OrderObject order : orders) {
                    orderJson.add(order.toJson());
                }
                OrdersJson.put("orders", orderJson);
                String response = OrdersJson.toJSONString();
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    public static class PutShippingHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("PUT".equals(exchange.getRequestMethod())) {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Access-Control-Allow-Headers","x-prototype-version,x-requested-with");
                headers.add("Access-Control-Allow-Methods","GET,POST");
                headers.add("Access-Control-Allow-Origin","*");
                InputStream requestBody = exchange.getRequestBody();
                String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject orderJson = (JSONObject) parser.parse(body);
                    orderJson = (JSONObject) orderJson.get("shipment");
                    OrderObject shipment = ShippingConsumer.getShipment(orderJson.get("order_id").toString());
                    shipment.setOrderKey(orderJson.get("order_id").toString()+":"+shipment.getUserOffset());
                    shipment.setStatus(orderJson.get("status").toString());
                    ShippingProducer.addShipping(shipment);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // OrderObject order = OrderObject.deserialize(body);
                // String[] bodyParts = body.split("&");
                // String orderId = bodyParts[0].split("=")[1];
                // String status = bodyParts[1].split("=")[1];
                // shippingProducer.updateShipping(orderId, status);
                exchange.sendResponseHeaders(200, -1);
            }
        }
    }
}
