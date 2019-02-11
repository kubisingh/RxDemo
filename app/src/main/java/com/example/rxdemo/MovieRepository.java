package com.example.rxdemo;

import android.content.Context;
import android.os.Handler;

import com.example.rxdemo.Models.Movies;
import com.example.rxdemo.Network.OAuthManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;


public class MovieRepository {

    private Single<ArrayList<Movies>> movies ;
    private Context cn;
    private MovieRepository(){}

    public MovieRepository(Context cn){
        this.cn=cn;
    }

    public Single<ArrayList<Movies>> getMoviesData(){
        return movies;
    }


    public Single<ArrayList<Movies>> getApiData(){
        movies= OAuthManager.getInstance().getMovieApiData();
        return movies;
    }

    /*private Single<ArrayList<Movies>> getListObservable() {


        return Single.create(new SingleOnSubscribe<ArrayList<Movies>>() {
            @Override
            public void subscribe(SingleEmitter<ArrayList<Movies>> emitter) throws Exception {

                        Movies l = new Movies();
                        l.setTitle("dasdasd");
                        List<String> st = new ArrayList<>();
                        st.add("asa");
                        st.add("asasdsd");
                        l.setGenre(st);
                        l.setRating(5.7);
                        l.setReleaseYear(2344);
                        ArrayList<Movies> note = new ArrayList<>();
                        note.add(l);
                        emitter.onSuccess(note);

            }
        });


    }*/
}
