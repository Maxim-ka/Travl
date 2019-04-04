package com.travl.guide.mvp.view.start.page;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.travl.guide.mvp.model.api.city.content.CitiesList;
import com.travl.guide.mvp.model.api.city.content.City;
import com.travl.guide.mvp.model.api.city.content.CityContent;

import java.util.ArrayList;

@StateStrategyType(value = AddToEndSingleStrategy.class)
public interface StartPageView extends MvpView {

    @StateStrategyType(value = OneExecutionStateStrategy.class)
    void initArticlesFragment();

    @StateStrategyType(value = OneExecutionStateStrategy.class)
    void initCityArticlesFragment();

    @StateStrategyType(value = OneExecutionStateStrategy.class)
    void setCityObjectList(CitiesList cityObjectList);

    @StateStrategyType(value = SkipStrategy.class)
    void setCityContentByCoordinates(CityContent cityContent);

    @StateStrategyType(value = OneExecutionStateStrategy.class)
    void setCityContentByLinkId(CityContent cityContent);

    @StateStrategyType(value = OneExecutionStateStrategy.class)
    void setCityName(CityContent cityContent);

    void requestCoordinates();

    void requestLocation();

    void setCityStringNames(ArrayList<String> citiesListToCitiesNameList);

    void onSpinnerItemClick(String selectedCity);

    void editCityList(City city);
}
