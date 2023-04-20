package order_service.models;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;

public class OrderObject {
    private String userOffset;
    private List<ItemObject> items;
    static JSONParser parser = new JSONParser();

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

    public String serialize() {
        JSONObject orderJson = new JSONObject();
        orderJson.put("userOffset", getUserOffset());
        for (ItemObject item : getItems()) {
            orderJson.put(item.getOffset(), item.toJson());
        }
        

        return orderJson.toJSONString();
    }

    public JSONObject toJson() {
        JSONObject orderJson = new JSONObject();
        orderJson.put("userOffset", getUserOffset());
        orderJson.put("items", getItems());
        return orderJson;
    }

    public static OrderObject deserialize(String json) {
        try {
            JSONObject orderJson = (JSONObject) parser.parse(json);
            if (orderJson.containsKey("order")) {
                orderJson = (JSONObject) orderJson.get("order");
            }
            String userOffset = (String) orderJson.get("user_id");
            orderJson = (JSONObject) orderJson.get("items");    
            List<ItemObject> items = new ArrayList<ItemObject>();
            for (Object itemJson : orderJson.keySet()) {
                ItemObject item = ItemObject.deserialize((String) itemJson, (String) orderJson.get(itemJson).toString());
                items.add(item);
            }
            return new OrderObject(userOffset, items);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}


