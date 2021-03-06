package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.rest.Utils;

/**
 * Created by sam_chordas on 10/1/15.
 */
public class StockIntentService extends IntentService {
    //for using a toast from a service
    Handler mHandler;


    public StockIntentService() {
        super(StockIntentService.class.getName());
        mHandler = new Handler();

    }

    public StockIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(StockIntentService.class.getSimpleName(), "Stock Intent Service");
        StockTaskService stockTaskService = new StockTaskService(this);
        Bundle args = new Bundle();
        if (intent.getStringExtra("tag").equals("add")) {
            args.putString("symbol", intent.getStringExtra("symbol"));
        }
        // We can call OnRunTask from the intent service to force it to run immediately instead of
        // scheduling a task.
        stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));
        //here's where we check if the stock was valid from Utils and toast if it ain't
        if (Utils.NO_SYMBOL) {
            Log.v("StockIntentService", "No symbol matches that input!!!");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(StockIntentService.this.getApplicationContext(), getString(R.string.invalid_stock_toast), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}

