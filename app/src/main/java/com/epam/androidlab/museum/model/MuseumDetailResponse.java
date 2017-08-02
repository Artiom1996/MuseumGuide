package com.epam.androidlab.museum.model;

/**
 * Created by Artiom on 05.07.2017.
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MuseumDetailResponse implements Serializable {
    @SerializedName("response")
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public class Response implements Serializable {

        @SerializedName("venue")
        private Museum museum;

        public Museum getMuseum() {
            return museum;
        }

        public void setMuseum(Museum museum) {
            this.museum = museum;
        }
    }
}