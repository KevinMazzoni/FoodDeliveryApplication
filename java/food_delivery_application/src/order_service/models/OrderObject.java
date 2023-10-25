package order_service.models;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.JSONArray;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;

public class OrderObject {
    private String userOffset;
    private String orderKey;
    private List<ItemObject> items;
    static JSONParser parser = new JSONParser();
    String status = "pending";

    // Set status variable with a default value
    public OrderObject(String userOffset, List<ItemObject> items) {
        this.userOffset = userOffset;
        this.items = items;
    }

    public String getUserOffset() {
        return userOffset;
    }

    public List<ItemObject> getItems() {
        return items;
    }

    public String getOrderKey() {
        return orderKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOrderKey(String orderKey) {
        this.orderKey = orderKey;
    }

    public String serialize() {
        JSONObject orderJson = new JSONObject();
        orderJson.put("user_id", getUserOffset());
        if (status != null) {
            orderJson.put("status", status.toString());
        } else {
            orderJson.put("status", "pending");
        }
        JSONObject itemsJson = new JSONObject();
        for (ItemObject item : getItems()) {
            itemsJson.put(item.getOffset(), item.toJson());
        }
        orderJson.put("items", itemsJson);
        return orderJson.toJSONString();
    }

    public JSONObject toJson() {
        JSONObject orderJson = new JSONObject();
        orderJson.put("user_id", getUserOffset());
        orderJson.put("status", status.toString());
        JSONObject itemsJson = new JSONObject();
        for (ItemObject item : getItems()) {
            itemsJson.put(item.getOffset(), item.toJson());
        }
        orderJson.put("items", itemsJson);
        orderJson.put("id", getOrderKey());
        return orderJson;
    }

    public static OrderObject deserialize(String json) {
        try {
            JSONObject orderJson = (JSONObject) parser.parse(json);
            
            String status = (String) orderJson.get("status");
            if (orderJson.containsKey("order")) {
                orderJson = (JSONObject) orderJson.get("order");
            }
            String userOffset = (String) orderJson.get("user_id");
            System.out.println("Deserializing order");
            System.out.println("Order: " + orderJson.toJSONString());
            // JSONArray jsonArray = new JSONArray(orderJson.get("items"));

            // for (int i = 0; i < jsonArray.length(); i++) {
            //     JSONObject jsonObject = jsonArray.getJSONObject(i); 
            //     System.out.println("Items: " + jsonObject.toString());
            // }

            orderJson = (JSONObject) orderJson.get("items");  

            List<ItemObject> items = new ArrayList<ItemObject>();
            for (Object itemJson : orderJson.keySet()) {
                ItemObject item = ItemObject.deserialize((String) itemJson, (String) orderJson.get(itemJson).toString());
                items.add(item);
            }
            OrderObject order = new OrderObject(userOffset, items);
            
            order.setStatus(status);
        
            return order;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}


