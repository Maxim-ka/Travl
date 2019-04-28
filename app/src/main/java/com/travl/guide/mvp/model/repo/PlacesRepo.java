package com.travl.guide.mvp.model.repo;

import com.travl.guide.mvp.model.api.places.articles.Root;
import com.travl.guide.mvp.model.api.places.map.ManyPlacesContainer;
import com.travl.guide.mvp.model.network.CoordinatesRequest;
import com.travl.guide.mvp.model.network.NetService;
import com.travl.guide.ui.utils.NetworkStatus;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class PlacesRepo {

    private NetService netService;

    public PlacesRepo(NetService netService) {
        this.netService = netService;
    }

    public Single<ManyPlacesContainer> loadPlacesForMap(CoordinatesRequest position, double radius, int detailed) {
        Timber.d("Loading Places");
        if (NetworkStatus.isOnline()) {
            return netService.loadPlacesForMap(position, radius, detailed).subscribeOn(Schedulers.io()).onErrorReturn(throwable -> {
                Timber.e(throwable);
                return null;
            });
        } else {
            return new Single<ManyPlacesContainer>() {
                @Override
                protected void subscribeActual(SingleObserver<? super ManyPlacesContainer> observer) {
                }
            };
        }
    }

    public Single<Root> loadNewPlace(int id) {
        Timber.e("loading place with id = %s", id);
        if (NetworkStatus.isOnline()) {
            return netService.loadNewPlace(id).subscribeOn(Schedulers.io()).onErrorReturn(throwable -> {
                Timber.e(throwable);
                return null;
            });
        } else {
            return new Single<Root>() {
                @Override
                protected void subscribeActual(SingleObserver<? super Root> observer) {
                }
            };
        }
    }

    public Single<ManyPlacesContainer> loadNextPlaces(int page, int detailed, CoordinatesRequest position, double radius) {
        if (NetworkStatus.isOnline()) {
            return netService.loadNextPlaces(page, detailed, position, radius).subscribeOn(Schedulers.io()).onErrorReturn(throwable -> {
                Timber.e(throwable);
                return null;
            });
        } else {
            return new Single<ManyPlacesContainer>() {
                @Override
                protected void subscribeActual(SingleObserver<? super ManyPlacesContainer> observer) {
                }
            };
        }
    }
}
