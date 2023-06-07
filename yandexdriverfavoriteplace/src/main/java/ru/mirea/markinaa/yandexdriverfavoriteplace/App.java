package ru.mirea.markinaa.yandexdriverfavoriteplace;

import android.app.Application;

import com.yandex.mapkit.MapKitFactory;

public class App extends Application {
    private final String MAPKIT_API_KEY = "4b1f77f4-1d2a-4b9c-94ec-c58961858ce5";

    @Override
    public void onCreate() {
        super.onCreate();
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
    }
}