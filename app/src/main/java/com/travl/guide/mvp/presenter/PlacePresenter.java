package com.travl.guide.mvp.presenter;

import android.annotation.SuppressLint;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.travl.guide.mvp.model.api.places.Place;
import com.travl.guide.mvp.model.repo.PlacesRepo;
import com.travl.guide.mvp.view.PlaceView;
import com.travl.guide.ui.App;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;

@InjectViewState
public class PlacePresenter extends MvpPresenter<PlaceView> {

    @Inject
    PlacesRepo placesRepo;
    private Scheduler scheduler;
    private String argument1;
    private int argument2;

    public PlacePresenter(Scheduler scheduler, String arg1, int arg2) {
        this.scheduler = scheduler;
        this.argument1 = arg1;
        this.argument2 = arg2;
        App.getInstance().getAppComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        loadPlaceCardInfo();
    }

    @SuppressLint("CheckResult")
    private void loadPlaceCardInfo() {
        placesRepo.loadPlace(argument1, argument2).observeOn(scheduler).subscribe(new Consumer<Place>() {
            @Override
            public void accept(Place place) throws Exception {
                getViewState().setPlaceAuthorNameTextView(place.getAuthor());
                getViewState().setTextView(place.getDescription());
//                getViewState().setImageView(place.getImageUrls().get(0));
            }
        });
    }
}
