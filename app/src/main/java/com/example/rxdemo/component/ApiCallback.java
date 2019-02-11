package com.example.rxdemo.component;

import com.example.rxdemo.Models.Movies;

import java.util.ArrayList;

public interface ApiCallback {
    public void onCallBack(boolean success, ArrayList<Movies> weatherModel);
}
