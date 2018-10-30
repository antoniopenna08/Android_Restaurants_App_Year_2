package com.example.danut.restaurant;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Grigor DAnut on 14/04/2018.
 */

@IgnoreExtraProperties
public class RestaurantOwner {

    private String Owner_ID;
    private String Owner_Name;
    private String Owner_Email;

    public RestaurantOwner(){

    }

    public RestaurantOwner(String owner_ID, String owner_Name, String owner_Email) {
        Owner_ID = owner_ID;
        Owner_Name = owner_Name;
        Owner_Email = owner_Email;
    }

    public String getOwner_ID() {
        return Owner_ID;
    }

    public void setOwner_ID(String owner_ID) {
        Owner_ID = owner_ID;
    }

    public String getOwner_Name() {
        return Owner_Name;
    }

    public void setOwner_Name(String owner_Name) {
        Owner_Name = owner_Name;
    }

    public String getOwner_Email() {
        return Owner_Email;
    }

    public void setOwner_Email(String owner_Email) {
        Owner_Email = owner_Email;
    }
}
