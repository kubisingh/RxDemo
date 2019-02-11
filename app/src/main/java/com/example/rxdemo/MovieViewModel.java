package com.example.rxdemo;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.rxdemo.Models.Movies;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import rx.SingleSubscriber;
import rx.Subscription;

public class MovieViewModel extends AndroidViewModel {

    private Single<ArrayList<Movies>> movies ;
    MovieRepository movieRepository;
    Context cn;
    public MovieViewModel(@NonNull Application application) {
        super(application);
        this.cn=application;
        movieRepository=new MovieRepository(application);
        movies=movieRepository.getMoviesData();
       // initData();
    }

    public void initData(){
          //  movieRepository.generateData();
    }

    public Single<ArrayList<Movies>> getMovieData(){
            return movieRepository.getApiData();
    }


    public void refreshData(){
            initData();
    }
}
