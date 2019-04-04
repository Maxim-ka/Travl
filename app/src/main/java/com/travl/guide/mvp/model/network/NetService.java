package com.travl.guide.mvp.model.network;

import com.travl.guide.mvp.model.api.articles.ArticleLinksContainer;
import com.travl.guide.mvp.model.api.city.content.CitiesList;
import com.travl.guide.mvp.model.api.city.content.CityContent;
import com.travl.guide.mvp.model.api.places.ManyPlacesContainer;
import com.travl.guide.mvp.model.api.places.PlaceContainer;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NetService {
    @GET("api/map/")
    Single<ManyPlacesContainer> loadPlacesForMap(@Query(value = "position", encoded = true) CoordinatesRequest position, @Query("radius") double radius, @Query("detailed") int detailed);

    @GET("api/places/{id}/")
    Single<PlaceContainer> loadPlace(@Path("id") int id);

    @GET("api/query/")
    Single<CityContent> loadCityContent(@Query(value = "position", encoded = true) CoordinatesRequest position);

    @GET("api/articles/")
    Single<ArticleLinksContainer> loadArticles(@Query("travlzine") boolean isTravlZine);

    @GET("api/cities/")
    Single<CitiesList> loadCitiesList();

    @GET("api/cities/{id}")
    Single<CityContent> loadCity(@Path("id") int id);
}
