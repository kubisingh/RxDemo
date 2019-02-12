package com.example.rxdemo.Network;

import com.example.rxdemo.Models.Movies;
import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class OAuthManager {

    private static Retrofit retrofit = null;
    private static OAuthManager instance;
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    static CallInterface movieApiInterface;
    Call call;
    public final static String BASEURL="https://api.androidhive.info/json/";

    private OAuthManager(){}

    public static OAuthManager getInstance(){
        if(instance==null){
            instance=new OAuthManager();
            instance.getClient();
        }
        return instance;
    }

    private void getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASEURL)
                    .addConverterFactory(GsonConverterFactory.create(new Gson()))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            movieApiInterface = retrofit.create(CallInterface.class);
        }
    }
    public interface CallInterface {
        @GET("movies.json")
        Single<ArrayList<Movies>> getMoviesList();

    }

    public Single<ArrayList<Movies>> getMovieApiData(){
        return movieApiInterface.getMoviesList();
    }

}
