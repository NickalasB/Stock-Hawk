//package com.sam_chordas.android.stockhawk.service;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.OperationApplicationException;
//import android.database.Cursor;
//import android.database.DatabaseUtils;
//import android.os.RemoteException;
//import android.support.v4.widget.CursorAdapter;
//import android.util.Log;
//
//import com.google.android.gms.gcm.GcmNetworkManager;
//import com.google.android.gms.gcm.GcmTaskService;
//import com.google.android.gms.gcm.TaskParams;
//import com.sam_chordas.android.stockhawk.data.QuoteColumns;
//import com.sam_chordas.android.stockhawk.data.QuoteProvider;
//import com.sam_chordas.android.stockhawk.rest.Utils;
//import com.sam_chordas.android.stockhawk.ui.MyStocksChartActivity;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.Response;
//
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//
///**
// * Created by nickbradshaw on 8/10/16.
// */
//public class StocksHistoryService extends GcmTaskService {
//    private String LOG_TAG = StocksHistoryService.class.getSimpleName();
//
//    private OkHttpClient client = new OkHttpClient();
//    private Context mContext = this;
//    private StringBuilder mStoredSymbols = new StringBuilder();
//    private boolean isUpdate;
//    private CursorAdapter mCursorAdapter;
//    public Calendar pastCalendar;
//    public Calendar currentCalendar;
//
//    public StocksHistoryService() {
//    }
//
//    public StocksHistoryService(Context context) {
//        mContext = context;
//    }
//
//
//    public String  currentDate() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        currentCalendar = Calendar.getInstance();
//        String presentDate = dateFormat.format(currentCalendar.getTime());
//        Log.v(LOG_TAG, "Today's date is " + presentDate);
//        return presentDate;
//    }
//
//
//
//    public String aYearAgo() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        pastCalendar = Calendar.getInstance();
//        pastCalendar.add(Calendar.YEAR, +-1);
//        String oneYearAgo = dateFormat.format(pastCalendar.getTime());
//        Log.v(LOG_TAG, "The date one year from today was " + oneYearAgo);
//        return oneYearAgo;
//
//    }
//
//
//    String fetchData(String url) throws IOException {
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//
//        Response response = client.newCall(request).execute();
//        return response.body().string();
//    }
//
//    @Override
//    public int onRunTask(TaskParams params) {
//        Cursor initQueryCursor;
//        if (mContext == null) {
//            mContext = this;
//        }
//
//        String getResponse;
//        int result = GcmNetworkManager.RESULT_FAILURE;
//
//
//        StringBuilder urlStringBuilder = new StringBuilder();
//        urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
//        try {
//            urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.historicaldata " +
//                            "where symbol = \"" + mCursorAdapter.getCursor().getString(1).toString() + "\" " +
//                            "and endDate = \"" + aYearAgo() + "\" and startDAte = \"" + currentDate() + "\" "
//                    , "UTF-8"));
////
////            urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.historicaldata where symbol = \"GOO\""
////                    , "UTF-8"));
//
//            urlStringBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
//                    + "org%2Falltableswithkeys&callback=");
//
//            String urlString;
//
//
//            if (urlStringBuilder != null) {
//                urlString = urlStringBuilder.toString();
//                try {
//                    getResponse = StockTaskService.fetchData(urlString);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//
//        }
//
//        return result;
//    }
//
//}