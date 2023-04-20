
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

import user_service.user_api_helpers.AdminApiHelper.GetAdminsHandler;
import user_service.user_api_helpers.AdminApiHelper.PostNewAdminHandler;
import user_service.user_api_helpers.CustomerApiHelper.GetCustomersHandler;
import user_service.user_api_helpers.CustomerApiHelper.PostNewCustomerHandler;
import user_service.user_api_helpers.DeliveryManApiHelper.GetDeliveryMenHandler;
import user_service.user_api_helpers.DeliveryManApiHelper.PostNewDeliveryManHandler;

public class UserApiHandler {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/customers", new GetCustomersHandler());
        server.createContext("/new-customer", new PostNewCustomerHandler());
        server.createContext("/new-admin", new PostNewAdminHandler());
        server.createContext("/admins", new GetAdminsHandler());
        server.createContext("/new-delivery-man", new PostNewDeliveryManHandler());
        server.createContext("/delivery-men", new GetDeliveryMenHandler());
        server.setExecutor(null); // creates a default executor
        server.start();

    }

    



}
