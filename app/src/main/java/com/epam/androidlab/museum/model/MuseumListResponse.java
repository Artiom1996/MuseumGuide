package com.epam.androidlab.museum.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Artiom on 05.07.2017.
 */

public class MuseumListResponse implements Serializable {
    @SerializedName("response")
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public class Response implements Serializable {

        @SerializedName("venues")
        private ArrayList<Museum> museums = new ArrayList<>();

        public ArrayList<Museum> getMuseums() {
            return museums;
        }

        public void setMuseums(ArrayList<Museum> museums) {
            this.museums = museums;
        }
    }
}
