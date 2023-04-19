package order_service.models;

import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;

public class ItemObject {
    private String name;
    private String offset;
    private long quantity;
    static JSONParser parser = new JSONParser();


    public ItemObject(String offset, String name, long quantity) {
        this.offset = offset;
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getOffset() {
        return offset;
    }

    public long getQuantity() {
        return quantity;
    }

    public String getQuantityAsString() {
        return Long.toString(quantity);
    }

    public String serialize() {
        JSONObject itemJson = new JSONObject();
        itemJson.put("name", getName());
        itemJson.put("quantity", getQuantity());
        return itemJson.toJSONString();
    }

    public JSONObject toJson() {
        JSONObject itemJson = new JSONObject();
        itemJson.put("name", getName());
        itemJson.put("quantity", getQuantity());
        return itemJson;
    }

    public static ItemObject deserialize(String offset, String json) {
        try {
            JSONObject itemJson = (JSONObject) parser.parse(json);
            if (itemJson.containsKey("item")) {
                itemJson = (JSONObject) itemJson.get("item");
            }
            String name = (String) itemJson.get("name");
            long quantity = (long) itemJson.get("quantity");
            return new ItemObject(offset, name, quantity);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemObject createNew(String offset, String name, long quantity) {
        return new ItemObject(offset, name, quantity);
    }

}
