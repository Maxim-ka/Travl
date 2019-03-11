package com.travl.guide.mvp.model.repo;

import com.travl.guide.mvp.model.network.CoordinatesRequest;
import com.travl.guide.mvp.model.network.NetService;
import com.travl.guide.mvp.model.places.PlaceCardEtity;
import com.travl.guide.mvp.model.places.PlacesMap;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class PlacesRepo {
    private NetService netService;

    public PlacesRepo(NetService netService) {
        this.netService = netService;
    }

    public Single<PlacesMap> loadPlaces(String userName, CoordinatesRequest position, double radius) {
        Timber.d("Loading Places");
        return netService.getPlaces(userName, position, radius).subscribeOn(Schedulers.io());
    }

    public  Single<PlaceCardEtity> loadPlaceCard(String somethingParameter){
        Timber.d("Loading Place Card");
        return netService.getPlaceCard(somethingParameter).subscribeOn(Schedulers.io());
    }
}
