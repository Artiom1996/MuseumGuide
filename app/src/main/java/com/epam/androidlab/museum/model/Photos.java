package com.epam.androidlab.museum.model;

/**
 * Created by Artiom on 05.07.2017.
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Photos implements Serializable {
    @SerializedName("groups")
    private ArrayList<Group> groups;

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }

    public class Group implements Serializable {

        @SerializedName("items")
        private ArrayList<Photo> photos;

        public ArrayList<Photo> getPhotos() {
            return photos;
        }

        public void setPhotos(ArrayList<Photo> photos) {
            this.photos = photos;
        }
    }
}