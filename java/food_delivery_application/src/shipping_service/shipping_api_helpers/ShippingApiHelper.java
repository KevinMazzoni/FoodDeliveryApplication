package shipping_service.shipping_api_helpers;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import order_service.kafka_handlers.OrderConsumer;
import order_service.kafka_handlers.OrderProducer;
import order_service.models.ItemObject;
import order_service.models.ItemTable;
import order_service.models.OrderObject;
import shipping_service.kafka_handlers.ShippingConsumer;
import shipping_service.kafka_handlers.ShippingProducer;

import java.util.Hashtable;
import java.util.List;

import org.jose4j.json.internal.json_simple.JSONObject;

public class ShippingApiHelper {
    static ShippingProducer shippingProducer = new ShippingProducer();
    static {
        shippingProducer.start();
    }

    public static class GetShippingsHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String queryString = exchange.getRequestURI().getQuery();
                String userId = queryString.split("=")[1];
                List<OrderObject> orders = ShippingConsumer.getShippings(userId);
                JSONObject OrdersJson = new JSONObject();
                JSONObject orderJson = new JSONObject();
                for (OrderObject order : orders) {
                    orderJson.put(order.getOrderKey(), order.toJson());
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
}
