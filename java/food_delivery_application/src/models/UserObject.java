package models;

public class UserObject {
    private String offset;
    private String name;
    private String surname;

    public UserObject(String offset, String name, String surname) {
        this.offset = offset;
        this.name = name;
        this.surname = surname;
    }
    public String getOffset() {
        return offset;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String serialize() {
        return "{\"name\": \"" + name + "\", \"surname\": \"" + surname + "\"}";
    }
}
