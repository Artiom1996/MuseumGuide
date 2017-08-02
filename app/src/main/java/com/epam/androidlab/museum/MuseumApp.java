package com.epam.androidlab.museum;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by Artiom on 27.07.2017.
 */

public class MuseumApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
