package com.sam_chordas.android.stockhawk.service;

import android.support.annotation.NonNull;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sam_chordas.android.stockhawk.data.StockHistory;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by nickbradshaw on 8/14/16.
 */
public class YahooStockServiceFactory {


    public YahooStockService create(){


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://query.yahooapis.com/v1/public/")
                .addConverterFactory(getFactory())
                .build();

        return retrofit.create(YahooStockService.class);
    }


    @NonNull
    private GsonConverterFactory getFactory() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        StockHistoryListDeserializer mStockHistoryListDeserializer = new StockHistoryListDeserializer();

        Type type = new TypeToken<List<StockHistory>>() {}.getType();

        gsonBuilder.registerTypeAdapter(type, mStockHistoryListDeserializer);

        return GsonConverterFactory.create(gsonBuilder.create());
    }


}
