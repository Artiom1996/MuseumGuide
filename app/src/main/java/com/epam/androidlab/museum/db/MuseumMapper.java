package com.epam.androidlab.museum.db;


import com.epam.androidlab.museum.model.Contact;
import com.epam.androidlab.museum.model.Location;
import com.epam.androidlab.museum.model.Museum;

public class MuseumMapper {

    public static Museum map(MuseumRealm museumRealm) {
        Museum museum = new Museum();
        museum.setId(museumRealm.getId());
        museum.setUrl(museumRealm.getUrl());
        museum.setShortUrl(museumRealm.getShortUrl());
        museum.setName(museumRealm.getName());
        museum.setRating(museumRealm.getRating());
        museum.setRatingSignals(museumRealm.getRatingSignals());
        museum.setLocation(new Location());
        museum.getLocation().setAddress(museumRealm.getAddress());
        museum.setContact(new Contact());
        museum.getContact().setFormattedPhone(museumRealm.getPhone());
        return museum;
    }

    public static MuseumRealm map(Museum museum) {
        MuseumRealm museumRealm = new MuseumRealm();
        museumRealm.setId(museum.getId());
        museumRealm.setName(museum.getName());
        museumRealm.setRating(museum.getRating());
        museumRealm.setRatingSignals(museum.getRatingSignals());
        museumRealm.setPhone(museum.getContact().getFormattedPhone());
        museumRealm.setUrl(museum.getUrl());
        museumRealm.setShortUrl(museum.getShortUrl());
        museumRealm.setAddress(museum.getLocation().getAddress());
        return museumRealm;
    }
}
