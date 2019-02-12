package com.example.rxdemo;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.example.rxdemo.Models.Movies;
import com.example.rxdemo.component.GenerateList;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MovieViewModel extends AndroidViewModel {

    private Single<ArrayList<Movies>> movies ;
    MovieRepository movieRepository;
    GenerateList listner;
    private List<Movies> originalmovies=null;
    private List<Movies> templist=new ArrayList<>();
    Context cn;
    public MovieViewModel(@NonNull Application application) {
        super(application);
        this.cn=application;
        movieRepository=new MovieRepository(application);
        movies=movieRepository.getMoviesData();
        initData();
    }

    public void setListUpdateListner(GenerateList listner){
        this.listner=listner;
    }

    public void initData(){
            getMovieData().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public Single<ArrayList<Movies>> getMovieData(){
            return movieRepository.getApiData();
    }

    public void refreshData(){
            initData();
    }

    SingleObserver<List<Movies>> observer = new SingleObserver<List<Movies>>() {
        Disposable disposable;
        @Override
        public void onSubscribe(Disposable d) {
            disposable =d;
        }

        @Override
        public void onSuccess(final List<Movies> value) {
                    listner.updateList(value);
                    originalmovies=new ArrayList<>();
                    originalmovies.addAll(value);
                    disposable.dispose();
        }

        @Override
        public void onError(Throwable e) {

        }
    };

    public void filterList(String str){
            getFilter(str);
    }

    public void getFilter(final String str){
        templist.clear();
        if(!TextUtils.isEmpty(str)){
            getListObservable().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter(new Predicate<Movies>() {
                        @Override
                        public boolean test(Movies s) throws Exception {
                            return s.getTitle().toLowerCase().contains(str.toLowerCase());
                        }
                    })
                    .subscribeWith(observerFilter);
        }else{
            listner.updateList(originalmovies);
        }
    }

    private Observable<Movies> getListObservable() {
        return Observable.create(new ObservableOnSubscribe<Movies>() {
            @Override
            public void subscribe(ObservableEmitter<Movies> emitter) throws Exception {
                for (Movies note : originalmovies) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(note);
                    }
                }

                if (!emitter.isDisposed()) {
                    emitter.onComplete();
                }
            }
        });
    }

    Observer<Movies> observerFilter = new Observer<Movies>(){
        Disposable disposable;
        @Override
        public void onSubscribe(Disposable d) {
            disposable=d;
        }

        @Override
        public void onNext(Movies value) {
            templist.add(value);
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {
            listner.updateList(templist);
            disposable.dispose();
        }
    };

}
