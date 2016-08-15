package com.sam_chordas.android.stockhawk.service;

import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by nickbradshaw on 8/10/16.
 */
public class StocksHistoryService extends GcmTaskService {
    private String LOG_TAG = StocksHistoryService.class.getSimpleName();

    private OkHttpClient client = new OkHttpClient();
    private Context mContext = this;
    private StringBuilder mStoredSymbols = new StringBuilder();
    private boolean isUpdate;
    private Calendar pastCalendar;
    private Calendar currentCalendar;

    public StocksHistoryService() {
    }

    public StocksHistoryService(Context context) {
        mContext = context;
    }

    String fetchData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }


    public String currentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        currentCalendar = Calendar.getInstance();
        String presentDate = dateFormat.format(currentCalendar.getTime());
        Log.v(LOG_TAG, "Today's date is " + presentDate);
        return presentDate;
    }

    public String aYearAgo() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        pastCalendar = Calendar.getInstance();
        pastCalendar.add(Calendar.YEAR, +-1);
        String oneYearAgo = dateFormat.format(pastCalendar.getTime());
        Log.v(LOG_TAG, "The date one year from today was " + oneYearAgo);
        return oneYearAgo;

    }



    @Override
    public int onRunTask(TaskParams params) {
        Cursor mCursor;
        mCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{"Distinct " + QuoteColumns.SYMBOL}, null,
                null, null);
        // get symbol from params.getExtra and build query
        String stockSymbolText = params.getExtras().getString("symbol");


        if (mContext == null) {
            mContext = this;
        }

        StringBuilder urlStringBuilder = new StringBuilder();
                urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
                try {
                    //base url for the yahoo stock history query
                    urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.historicaldata where symbol =" +
                            " '" + stockSymbolText.toUpperCase() +"' and startDate = '" + aYearAgo() + "' and endDate ='" + currentDate() + "'", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        urlStringBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
                + "org%2Falltableswithkeys&callback=");


        String urlString;
        String getResponse;
        int result = GcmNetworkManager.RESULT_FAILURE;
        isUpdate = false;


        if (urlStringBuilder != null) {
            urlString = urlStringBuilder.toString();
            try {
                getResponse = fetchData(urlString);
                result = GcmNetworkManager.RESULT_SUCCESS;
                ContentValues contentValues = new ContentValues();
                // update ISCURRENT to 0 (false) so new data is current
                if (isUpdate) {
                    contentValues.put(QuoteColumns.ISCURRENT, 0);
                    mContext.getContentResolver().update(QuoteProvider.Quotes.CONTENT_URI, contentValues,
                            null, null);
                }
                if (Utils.quoteJsonToContentVals(getResponse) != null) {
                    //if the stock is valid then add it to the database
                    mContext.getContentResolver().applyBatch(QuoteProvider.AUTHORITY, Utils.quoteJsonToContentVals(getResponse));
                }
            } catch (RemoteException | OperationApplicationException e) {
                Log.e(LOG_TAG, "Error applying batch insert", e);


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return result;
    }
}