package com.sam_chordas.android.stockhawk.service;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.sam_chordas.android.stockhawk.data.StockHistory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nickbradshaw on 8/14/16.
 */
public class StockHistoryListDeserializer implements JsonDeserializer<List<StockHistory>> {
    final String LOG_TAG = StockHistoryListDeserializer.class.getSimpleName();
//    private final Gson gson;

    public StockHistoryListDeserializer() {
//        gson = new Gson();
    }

    @Override
    public List<StockHistory> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
            throws JsonParseException

    {

        JsonArray content = je.getAsJsonObject().getAsJsonObject("query").getAsJsonObject("results").getAsJsonArray("quote");
        List<StockHistory> entries = new ArrayList<>(content.size());
        for (JsonElement element : content) {
            entries.add(new Gson().fromJson(element, StockHistory.class));

            Log.v(LOG_TAG, "This should be a list of stock history " + entries);

        }
        return entries;
    }
}