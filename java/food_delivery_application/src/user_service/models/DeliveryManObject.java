package user_service.models;

import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;

public class DeliveryManObject extends UserObject {
    private String address;
    static JSONParser parser = new JSONParser();
    

    public DeliveryManObject(String offset, String name, String surname) {
        super(offset, name, surname);
    }   

    public String serialize() {
        JSONObject deliveryManJson = new JSONObject();
        deliveryManJson.put("name", getName());
        deliveryManJson.put("surname", getSurname());
        return deliveryManJson.toJSONString();
    }

    public JSONObject toJson() {
        JSONObject deliveryManJson = new JSONObject();
        deliveryManJson.put("name", getName());
        deliveryManJson.put("surname", getSurname());
        deliveryManJson.put("id", getOffset());
        return deliveryManJson;
    }

    public static DeliveryManObject deserialize(String offset, String json) {
        try {
            JSONObject deliveryManJson = (JSONObject) parser.parse(json);
            if (deliveryManJson.containsKey("delivery-man")) {
                deliveryManJson = (JSONObject) deliveryManJson.get("delivery-man");
            }
            String name = (String) deliveryManJson.get("name");
            String surname = (String) deliveryManJson.get("surname");
            return new DeliveryManObject(offset, name, surname);
        } catch (ParseException e) {
            e.printStackTrace();
            // raise exception
            return null;
        }
    }
}
