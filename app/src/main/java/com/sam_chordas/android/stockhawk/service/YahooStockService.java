package com.sam_chordas.android.stockhawk.service;

import com.sam_chordas.android.stockhawk.data.StockHistory;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by nickbradshaw on 8/14/16.
 */
public interface YahooStockService  {

    @GET("yql?format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys")
    Call<List<StockHistory>> stockHistory(@Query("q") String yqlQuery);

}
