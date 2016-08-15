package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.StockHistory;
import com.sam_chordas.android.stockhawk.data.StockItem;
import com.sam_chordas.android.stockhawk.data.mappers.StockHistoryMapper;
import com.sam_chordas.android.stockhawk.service.YahooStockServiceFactory;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nickbradshaw on 8/9/16.
 * Chart code pulled from the opensource library: MPAndroidChart created by PhilJay
 * https://github.com/PhilJay/MPAndroidChart.git
 */
public class MyStocksChartActivity extends AppCompatActivity {


    private Legend chartLegend;
    private XAxis xAxis;
    private YAxis yAxisLeft;
    private YAxis yAxisRight;
    private StockItem stockItem;




    private ArrayList<StockItem> mStockArrayList;
    private List<StockItem> items;


    @BindView(R.id.chart_name_textview)
    TextView nameTextView;

    @BindView(R.id.chart_symbol_textview)
    TextView symbolTextView;

    @BindView(R.id.chart_bid_price_textview)
    TextView bidPriceTextView;

    @BindView(R.id.chart_percent_change_textview)
    TextView percentChangeTextView;

    @BindView(R.id.chart1)
    LineChart lineChart;

    private Calendar pastCalendar;
    private Calendar currentCalendar;


    private String LOG_TAG = MyStocksChartActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_my_stocks);
        ButterKnife.bind(this);


        Intent mChartIntent = getIntent();
        if (mChartIntent.hasExtra("SYMBOL"))
            if (symbolTextView != null) {
                nameTextView.setText(getIntent().getStringExtra("NAME"));
                symbolTextView.setText(getIntent().getStringExtra("SYMBOL").toUpperCase());
                bidPriceTextView.setText(getIntent().getStringExtra("BIDPRICE"));
                percentChangeTextView.setText(getIntent().getStringExtra("PERCENT_CHANGE"));
            }



        mStockArrayList = new ArrayList<>();
        String stockSymbolText = symbolTextView.getText().toString();
        loadStockHistory(stockSymbolText);


//
//        StringBuilder urlStringBuilder = new StringBuilder();
//        //base url for the yahoo stock history query
//        urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
//        //the guts of the query
//        String historyQuery = "select * from yahoo.finance.historicaldata where symbol = \"" + stockSymbolText.toUpperCase() +"\" and startDate = \"" + aYearAgo + "\" and endDate =\"" + currentDate + "\"";
//        urlStringBuilder.append(URLEncoder.encode(historyQuery));
//        urlStringBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
//                + "org%2Falltableswithkeys&callback=");


//        Log.v(LOG_TAG, "This should be the corresponding symbol you just clicked on " + stockSymbolText);
//        Log.v(LOG_TAG, "This should be the history link " + urlStringBuilder);


        lineChart.setNoDataTextDescription("No historical data yet");



//        entries.add(new Entry(entries.toArray(float), 0));
//        entries.add(new Entry(4f, 0));
//        entries.add(new Entry(8f, 1));
//        entries.add(new Entry(6f, 2));
//        entries.add(new Entry(2f, 3));
//        entries.add(new Entry(18f, 4));
//        entries.add(new Entry(9f, 5));
//        entries.add(new Entry(14f, 6));
//        entries.add(new Entry(7f, 7));
//        entries.add(new Entry(12f, 8));
//        entries.add(new Entry(13f, 9));
//        entries.add(new Entry(1f, 10));
//        entries.add(new Entry(3f, 11));

        chartLegend = lineChart.getLegend();
        chartLegend.setEnabled(false);

        yAxisLeft = lineChart.getAxisLeft();
        yAxisRight = lineChart.getAxisRight();
        xAxis = lineChart.getXAxis();
        yAxisLeft.setTextSize(12);
        yAxisRight.setTextSize(12);
        xAxis.setTextColor(getResources().getColor(R.color.common_plus_signin_btn_text_light));
        yAxisLeft.setTextColor(getResources().getColor(R.color.common_plus_signin_btn_text_light));
        yAxisRight.setTextColor(getResources().getColor(R.color.common_plus_signin_btn_text_light));

        LineData data = getLineData(new ArrayList<Entry>());

        lineChart.setData(data);
        lineChart.animateY(5000);
        loadStockHistory(stockSymbolText);
    }



    @NonNull
    private LineData getLineData(List<Entry> entries) {
        LineDataSet dataset = new LineDataSet(entries, "Highest Stock Price");

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");
        labels.add("July");
        labels.add("August");
        labels.add("September");
        labels.add("October");
        labels.add("November");
        labels.add("December");


        LineData data = new LineData(labels, dataset);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
//        dataset.setDrawCubic(true);
        dataset.setDrawFilled(true);
        dataset.setValueTextSize(10f);
        labels.size();
        return data;
    }

    private void loadStockHistory(String stockSymbolText) {
        String historyQuery = "select * from yahoo.finance.historicaldata where symbol = \"" + stockSymbolText.toUpperCase() +"\" and startDate = \"" + getAYearAgo() + "\" and endDate =\"" + getCurrentDate() + "\"";

        new YahooStockServiceFactory().create().stockHistory(URLEncoder.encode(historyQuery)).enqueue(new Callback<List<StockHistory>>() {
            @Override
            public void onResponse(Call<List<StockHistory>> call, Response<List<StockHistory>> response) {
                if (response.isSuccessful()){
                    lineChart.setData(getLineData(new StockHistoryMapper().mapStockHistoryForMonthlyHighestClose(response.body())));
                }
            }

            @Override
            public void onFailure(Call<List<StockHistory>> call, Throwable t) {

            }
        });

    }

    public String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        currentCalendar = Calendar.getInstance();
        String presentDate = dateFormat.format(currentCalendar.getTime());
        Log.v(LOG_TAG, "Today's date is " + presentDate);
        return presentDate;
    }

    public String getAYearAgo() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        pastCalendar = Calendar.getInstance();
        pastCalendar.add(Calendar.YEAR, +-1);
        String oneYearAgo = dateFormat.format(pastCalendar.getTime());
        Log.v(LOG_TAG, "The date one year from today was " + oneYearAgo);
        return oneYearAgo;

    }

}

