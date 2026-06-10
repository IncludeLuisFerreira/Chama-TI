package com.example.chamati;

import android.app.Application;
import com.parse.Parse;

public class ChamaTIApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("SEU_APPLICATION_ID")
                .clientKey("SEU_CLIENT_KEY")
                .server("https://parseapi.back4app.com")
                .build());
    }
}
