import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

import order_service.order_api_handlers.ItemApiHandler;
import order_service.order_api_handlers.ItemApiHandler.GetItemsHandler;
import order_service.order_api_handlers.ItemApiHandler.PostNewItemHandler;
import order_service.order_api_handlers.ItemApiHandler.PutItemsHandler;

public class OrderApiHandler {
    public static void main(String[] args) throws Exception {
        // New Item Handler is launched in order to start the item thread
        new ItemApiHandler();

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/items", new GetItemsHandler());
        server.createContext("/new-item", new PostNewItemHandler());
        server.createContext("/update-items", new PutItemsHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

}