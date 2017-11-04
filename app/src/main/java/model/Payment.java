package model;

/**
 * Created by molariu on 11/2/2017.
 */

public class Payment {
    public String timestamp;
    private float cost;
    private String name;
    private String type;

    public Payment(){
        //Default constructor required for calls to DataSnapshot.getValue(User.class)
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

    public String getName() {

        return name;
    }

    public String getType() {
        return type;
    }

    public float getCost() {

        return cost;
    }
}
