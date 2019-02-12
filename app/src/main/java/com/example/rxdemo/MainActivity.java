package com.example.rxdemo;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.rxdemo.Models.Movies;
import com.example.rxdemo.UIUtils.MovieRecycleAdapter;
import com.example.rxdemo.component.GenerateList;
import com.example.rxdemo.component.ListClickListner;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    MovieViewModel movieViewModel;
    RecyclerView recyclerView;
    MovieRecycleAdapter adapter;
    EditText txt;
    ImageView cancel;
    CompositeDisposable compositeDisposable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView)findViewById(R.id.rcycleweather);
        txt = (EditText) toolbar.findViewById(R.id.searchBar);
        cancel = (ImageView) toolbar.findViewById(R.id.clear);

        movieViewModel= ViewModelProviders.of(MainActivity.this).get(MovieViewModel.class);
        movieViewModel.setListUpdateListner(listnerList);
        adapter = new MovieRecycleAdapter(MainActivity.this,new ArrayList<Movies>(),listClickListner);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        compositeDisposable=new CompositeDisposable();


        compositeDisposable.add(RxTextView.textChanges(txt).debounce(100,TimeUnit.MILLISECONDS).subscribe(new Consumer<CharSequence>() {
            @Override
            public void accept(CharSequence charSequence) throws Exception {
                updateView(charSequence.toString());
            }
        }));

        compositeDisposable.add(RxView.clicks(cancel).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                cancel.setVisibility(View.GONE);
                txt.setText("");
            }
        }));

    }

    GenerateList listnerList = new GenerateList() {
        @Override
        public void updateList(List<Movies> value) {
            if(value!=null)
                adapter.notifyData(value);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    ListClickListner listClickListner = new ListClickListner() {
        @Override
        public void onClick(Movies weatherModel) {

        }
    };

    private void updateView(final String str){
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(TextUtils.isEmpty(str)){
                    cancel.setVisibility(View.GONE);
                    movieViewModel.filterList("");
                }else{
                    cancel.setVisibility(View.VISIBLE);
                    movieViewModel.filterList(str);
                }
            }
        });
    }

}
