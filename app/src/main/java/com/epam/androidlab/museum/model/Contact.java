package com.epam.androidlab.museum.model;

/**
 * Created by Artiom on 05.07.2017.
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Contact implements Serializable{

    @SerializedName("formattedPhone")
    private String formattedPhone;

    public String getFormattedPhone() {
        return formattedPhone;
    }

    public void setFormattedPhone(String formattedPhone) {
        this.formattedPhone = formattedPhone;
    }
}