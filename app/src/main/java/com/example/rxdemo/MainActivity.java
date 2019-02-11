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
import com.example.rxdemo.component.ListClickListner;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.observers.SubscriberCompletableObserver;
import io.reactivex.schedulers.Schedulers;
import rx.Subscription;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    MovieViewModel movieViewModel;
    RecyclerView recyclerView;
    MovieRecycleAdapter adapter;
    EditText txt;
    ImageView cancel;
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
        adapter = new MovieRecycleAdapter(MainActivity.this,new ArrayList<Movies>(),listClickListner);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        movieViewModel.getMovieData().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

      txt.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {

          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
              String str = txt.getText().toString().trim();
              if(TextUtils.isEmpty(str)){
                  cancel.setVisibility(View.GONE);
                  adapter.setFilter("");
              }else{
                  cancel.setVisibility(View.VISIBLE);
                  adapter.setFilter(str);
              }
          }

          @Override
          public void afterTextChanged(Editable s) {

          }
      });



        RxView.clicks(cancel).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                cancel.setVisibility(View.GONE);
                txt.setText("");
            }
        });


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    ListClickListner listClickListner = new ListClickListner() {
        @Override
        public void onClick(Movies weatherModel) {

        }
    };


    SingleObserver<List<Movies>> observer = new SingleObserver<List<Movies>>() {
        Disposable disposable;
        @Override
        public void onSubscribe(Disposable d) {
            disposable =d;
        }

        @Override
        public void onSuccess(final List<Movies> value) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyData(value);
                    disposable.dispose();
                }
            });
        }

        @Override
        public void onError(Throwable e) {

        }
    };

}
