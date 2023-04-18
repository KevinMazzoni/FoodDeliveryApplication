package user_service.models;

import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;

public class AdminObject extends UserObject {
    private String address;
    static JSONParser parser = new JSONParser();
    

    public AdminObject(String offset, String name, String surname) {
        super(offset, name, surname);
    }   

    public String serialize() {
        JSONObject adminJson = new JSONObject();
        adminJson.put("name", getName());
        adminJson.put("surname", getSurname());
        return adminJson.toJSONString();
    }

    public JSONObject toJson() {
        JSONObject adminJson = new JSONObject();
        adminJson.put("name", getName());
        adminJson.put("surname", getSurname());
        return adminJson;
    }

    public static AdminObject deserialize(String offset, String json) {
        try {
            JSONObject adminJson = (JSONObject) parser.parse(json);
            if (adminJson.containsKey("admin")) {
                adminJson = (JSONObject) adminJson.get("admin");
            }
            String name = (String) adminJson.get("name");
            String surname = (String) adminJson.get("surname");
            return new AdminObject(offset, name, surname);
        } catch (ParseException e) {
            e.printStackTrace();
            // raise exception
            return null;
        }
    }
}
