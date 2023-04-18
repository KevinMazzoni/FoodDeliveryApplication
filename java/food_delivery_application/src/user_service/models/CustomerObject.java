package user_service.models;

import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;

public class CustomerObject extends UserObject {
    private String address;
    static JSONParser parser = new JSONParser();
    

    public CustomerObject(String offset, String name, String surname, String address ) {
        super(offset, name, surname);
        this.address = address;
    }   

    public String getAddress() {
        return address;
    }

    public String serialize() {
        JSONObject customerJson = new JSONObject();
        customerJson.put("name", getName());
        customerJson.put("surname", getSurname());
        customerJson.put("address", address);
        return customerJson.toJSONString();
    }

    public JSONObject toJson() {
        JSONObject customerJson = new JSONObject();
        customerJson.put("name", getName());
        customerJson.put("surname", getSurname());
        customerJson.put("address", address);
        return customerJson;
    }

    public static CustomerObject deserialize(String offset, String json) {
        try {
            JSONObject customerJson = (JSONObject) parser.parse(json);
            if (customerJson.containsKey("customer")) {
                customerJson = (JSONObject) customerJson.get("customer");
            }
            String name = (String) customerJson.get("name");
            String surname = (String) customerJson.get("surname");
            String address = (String) customerJson.get("address");
            return new CustomerObject(offset, name, surname, address);
        } catch (ParseException e) {
            e.printStackTrace();
            // raise exception
            return null;
        }
    }
}
