package model;

import java.io.Serializable;

/**
 * Created by molariu on 11/2/2017.
 */

public class Payment implements Serializable{
    //TODO : not really. The timestamp must be added as an value in firebase (to the "timestamp" itselft) in order to be mapped to this field
    public String timestamp; //this is returned as the key of the snapshot (a String);
    private float cost;
    private String name;
    private String type;

    public Payment(){
        //Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Payment(String timestamp,  String name, float cost, String type) {
        this.timestamp = timestamp;
        this.cost = cost;
        this.name = name;
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getName() {return name;}

    public String getType() {
        return type;
    }

    public float getCost() {return cost;}

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

}
