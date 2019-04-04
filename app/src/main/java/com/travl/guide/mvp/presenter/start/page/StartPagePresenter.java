package com.travl.guide.mvp.presenter.start.page;

import android.annotation.SuppressLint;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.travl.guide.mvp.model.api.city.content.City;
import com.travl.guide.mvp.model.api.city.content.CityContent;
import com.travl.guide.mvp.model.network.CoordinatesRequest;
import com.travl.guide.mvp.model.repo.CityRepo;
import com.travl.guide.mvp.model.user.User;
import com.travl.guide.mvp.view.start.page.StartPageView;
import com.travl.guide.ui.App;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import timber.log.Timber;

@InjectViewState
public class StartPagePresenter extends MvpPresenter<StartPageView> {

    @Inject
    CityRepo cityRepo;
    private Scheduler scheduler;

    public StartPagePresenter(Scheduler scheduler) {
        this.scheduler = scheduler;
        App.getInstance().getAppComponent().inject(this);

    }

    @SuppressLint("CheckResult")
    public void loadCitiesList() {
        cityRepo.getCitiesList().observeOn(scheduler).subscribe(citiesList -> getViewState().setCityObjectList(citiesList), Timber::e);
    }

    @SuppressLint("CheckResult")
    public void loadCityContentByCoordinates(double[] coordinates) {
        CoordinatesRequest position = new CoordinatesRequest(coordinates);
        cityRepo.getCityContent(position).observeOn(scheduler).subscribe(cityContent -> {
            getViewState().setCityContentByCoordinates(cityContent);
        }, Timber::e);
    }


    public void initTravlZineArticlesFragment() {
        getViewState().initTravlZineFragment();
    }

    public void initCityArticlesFragment() {
        getViewState().initCityArticlesFragment();
    }

    @SuppressLint("CheckResult")
    public void loadCityContentByLinkId(int id) {
        cityRepo.loadCity(id).observeOn(scheduler).subscribe(cityContent -> getViewState().setCityContentByLinkId(cityContent), Timber::e);

    }

    public void setCity(CityContent cityContent) {
        getViewState().setCity(cityContent);
    }

    public void requestCoordinates() {
        getViewState().requestCoordinates();
    }

    public void requestLocation() {
        getViewState().requestLocation();
    }

    public void setCityStringNames(ArrayList<String> citiesListToCitiesNameList) {
        getViewState().setCityStringNames(citiesListToCitiesNameList);
    }

    public void onSpinnerItemClick(String selectedCity) {
        getViewState().onSpinnerItemClick(selectedCity);
    }

    public void addToCityList(City city) {
        getViewState().addToCityList(city);
    }

    public void initCitySpinner() {
        getViewState().initCitySpinner();
    }

    public void setCitySelectedName(String citySelected) {
        getViewState().setCitySelectedName(citySelected);
    }

    public void setSpinnerPositionSelected(int position) {
        getViewState().setSpinnerPositionSelected(position);
    }

    public void placeSelectedCityOnTop(String placeName) {
        getViewState().placeSelectedCityOnTop(placeName);
    }

    public void removeFromCitySpinnerAdapter(String placeName) {
        getViewState().removePlaceIfIsAdded(placeName);
    }

    public void addNamesToCitySpinner(List<String> cityStringNames) {
        getViewState().addNamesToCitySpinner(cityStringNames);
    }

    public void setCityArrayAdapter() {
        getViewState().setCityArrayAdapter();
    }

    public void addCityArrayAdapterToSpinner() {
        getViewState().addCityArrayAdapterToSpinner();
    }

    public void setUserCoordinates(double[] coordinates) {
        User.getInstance().setCoordinates(coordinates);
    }

    public void setCityArcticles() {
        getViewState().setCityArticles();
    }
}
