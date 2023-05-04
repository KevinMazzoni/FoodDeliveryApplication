import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

import order_service.kafka_handlers.OrderConsumer;
import order_service.order_api_helpers.ItemApiHelper;
import order_service.order_api_helpers.OrderApiHelper;
import order_service.order_api_helpers.ItemApiHelper.GetItemsHandler;
import order_service.order_api_helpers.ItemApiHelper.PostNewItemHandler;
import order_service.order_api_helpers.ItemApiHelper.PutItemsHandler;
import order_service.order_api_helpers.OrderApiHelper.PostNewOrderHandler;

public class OrderApiHandler {
    public static void main(String[] args) throws Exception {
        // New Item Handler is launched in order to start the item thread
        OrderConsumer orderConsumer = new OrderConsumer();
        orderConsumer.start();
        new ItemApiHelper(orderConsumer);
        new OrderApiHelper(orderConsumer);


        HttpServer server = HttpServer.create(new InetSocketAddress(8001), 0);
        server.createContext("/items", new GetItemsHandler());
        server.createContext("/new-item", new PostNewItemHandler());
        server.createContext("/update-items", new PutItemsHandler());

        server.createContext("/new-order", new PostNewOrderHandler());

        server.setExecutor(null); // creates a default executor
        server.start();
    }

}