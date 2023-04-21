import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

import shipping_service.shipping_api_helpers.ShippingApiHelper;
import shipping_service.shipping_api_helpers.ShippingApiHelper.GetShippingsHandler;
import user_service.user_api_helpers.AdminApiHelper.GetAdminsHandler;
import user_service.user_api_helpers.AdminApiHelper.PostNewAdminHandler;
import user_service.user_api_helpers.CustomerApiHelper.GetCustomersHandler;
import user_service.user_api_helpers.CustomerApiHelper.PostNewCustomerHandler;
import user_service.user_api_helpers.DeliveryManApiHelper.GetDeliveryMenHandler;
import user_service.user_api_helpers.DeliveryManApiHelper.PostNewDeliveryManHandler;

public class ShippingApiHandler {
    public static void main(String[] args) throws Exception {
        
        new ShippingApiHelper();

        // New Item Handler is launched in order to start the item thread
        HttpServer server = HttpServer.create(new InetSocketAddress(8002), 0);
        server.createContext("/orders", new GetShippingsHandler());
        // server.createContext("/new-shipping", new PostNewShippingHandler());
        // server.createContext("/update-shipping", new PutShippingHandler());

        server.setExecutor(null); // creates a default executor
        server.start();
    }
}
