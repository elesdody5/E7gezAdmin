package example.com.e7gezadmain.firebase.collection;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Stadium implements Serializable {
    private String id;
    private String name;

    public String getDescription() {
        return description;
    }

    private String description;
    private String Address;
    private HashMap<String,Long> price;
    private String img;

    public Stadium(String id, String name, String address, HashMap<String, Long> price, String img) {
        this.id = id;
        this.name = name;
        Address = address;
        this.price = price;
        this.img = img;
    }

    public Stadium(String name,String description, String address, HashMap<String, Long> price) {
        this.name = name;
        Address = address;
        this.price = price;
        this.description= description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return Address;
    }

    public Map<String, Long> getPrice() {
        return price;
    }

    public String getImg() {
        return img;
    }


}
