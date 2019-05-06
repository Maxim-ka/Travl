package com.travl.guide.mvp.model.user;

import com.travl.guide.mvp.model.network.CoordinatesRequest;

import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.annotations.Nullable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import timber.log.Timber;

public class User {
    private PublishSubject<CoordinatesRequest> coordinatesRequestPublishSubject = PublishSubject.create();
    private static User instance;
    // first item = latitude, second item = longitude
    private double[] coordinates;
    private double[] citySelectedCoordinates;
    private String cityName;
    private User() {

    }

    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    @Nullable
    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        if (!Arrays.equals(this.coordinates, coordinates)) {
            this.coordinates = coordinates;
            coordinatesRequestPublishSubject.onNext(new CoordinatesRequest(coordinates));
        }
    }

    public String getDefaultUserName() {
        return "travl";
    }

    public Observable<CoordinatesRequest> getCoordinatesRequestPublishSubject() {
        return coordinatesRequestPublishSubject.subscribeOn(Schedulers.io());
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double[] getCitySelectedCoordinates() {
        return citySelectedCoordinates;
    }

    public void setCitySelectedCoordinates(double[] citySelectedCoordinates) {
        Timber.e("CitySelectedCoords = " + Arrays.toString(citySelectedCoordinates));
        this.citySelectedCoordinates = citySelectedCoordinates;
    }
}
