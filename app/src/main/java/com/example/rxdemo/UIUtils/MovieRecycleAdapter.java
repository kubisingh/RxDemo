package com.example.rxdemo.UIUtils;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.rxdemo.Models.Movies;
import com.example.rxdemo.R;
import com.example.rxdemo.component.ListClickListner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MovieRecycleAdapter extends RecyclerView.Adapter<MovieRecycleAdapter.MyViewHolder> {

private List<Movies> movies=null;
private List<Movies> originalmovies=null;
private ListClickListner event;
private Context cn;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView movieImg;
        public TextView title, rating,rdate,genre;
        CardView card_view;

        public MyViewHolder(View view) {
            super(view);
            card_view=(CardView)view.findViewById(R.id.card_view);
             movieImg = (ImageView) view.findViewById(R.id.mimage);
            title = (TextView) view.findViewById(R.id.txt_title);
            rating = (TextView) view.findViewById(R.id.txt_rating);
            rdate = (TextView) view.findViewById(R.id.txt_rdate);
            genre = (TextView) view.findViewById(R.id.txt_genre);
        }
    }

    public MovieRecycleAdapter(Context cn,List<Movies> list, ListClickListner event) {
        this.movies = list;
        this.event=event;
        this.cn=cn;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listrow, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Movies obj = movies.get(position);
        holder.title.setText(obj.getTitle());
        holder.rating.setText(String.valueOf(obj.getRating()));
        holder.rdate.setText(String.valueOf(obj.getReleaseYear()));
        holder.genre.setText(String.valueOf(obj.getGenre().toString().replaceAll(Pattern.quote("["),"").replaceAll(Pattern.quote("]"),"")));
        holder.card_view.setTag(obj);
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.onClick((Movies) v.getTag());
            }
        });
        Glide.with(cn).load(obj.getImage())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.movieImg);

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void notifyData(List<Movies> weatherList){
        movies.clear();
        movies.addAll(weatherList);
        originalmovies=new ArrayList<>();
        originalmovies.addAll(weatherList);
        notifyDataSetChanged();
    }

    public void setFilter(final String str){
        movies.clear();
        if(!TextUtils.isEmpty(str)){
                getListObservable().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter(new Predicate<Movies>() {
                        @Override
                        public boolean test(Movies s) throws Exception {
                            return s.getTitle().toLowerCase().contains(str.toLowerCase());
                        }
                    })
                    .subscribeWith(observer);
        }else{
            movies.addAll(originalmovies);
            notifyDataSetChanged();
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

    Observer<Movies> observer = new Observer<Movies>(){
        Disposable disposable;
        @Override
        public void onSubscribe(Disposable d) {
            disposable=d;
        }

        @Override
        public void onNext(Movies value) {
            movies.add(value);
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {
            notifyDataSetChanged();
            disposable.dispose();
        }
    };
}