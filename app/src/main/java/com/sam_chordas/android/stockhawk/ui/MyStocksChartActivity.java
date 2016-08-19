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
import com.sam_chordas.android.stockhawk.data.mappers.StockHistoryMapper;
import com.sam_chordas.android.stockhawk.service.YahooStockServiceFactory;

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
    private StockHistory stockHistory;


    private ArrayList<StockHistory> stockHistories;


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

    private Calendar calendar;

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

        String stockSymbolText = symbolTextView.getText().toString();
        loadStockHistory(stockSymbolText);

        LineData data = getLineData(new ArrayList<Entry>());
        lineChart.setNoDataTextDescription(getString(R.string.chart_no_data_string));
        lineChart.setData(data);
        lineChart.animateY(5000);
        lineChart.setDescription(getString(R.string.chart_description_string));
        lineChart.setDescriptionColor(getResources().getColor(R.color.common_plus_signin_btn_text_light));


        chartLegend = lineChart.getLegend();
        chartLegend.setEnabled(false);

        yAxisLeft = lineChart.getAxisLeft();
        yAxisRight = lineChart.getAxisRight();
        xAxis = lineChart.getXAxis();


        yAxisLeft.setTextSize(12);
        yAxisRight.setTextSize(12);
        yAxisLeft.setTextColor(getResources().getColor(R.color.common_plus_signin_btn_text_light));
        yAxisRight.setTextColor(getResources().getColor(R.color.common_plus_signin_btn_text_light));
        yAxisLeft.setStartAtZero(false);
        yAxisRight.setStartAtZero(false);

        xAxis.setTextColor(getResources().getColor(R.color.common_plus_signin_btn_text_light));
    }



    @NonNull
    private LineData getLineData(List<Entry> entries, List<StockHistory> stockHistories){
        LineDataSet dataset = new LineDataSet(entries, "Highest Stock Price");

        //calling method we created to define labels at top of chart
//        ArrayList<String> labels = getLabels();
        ArrayList<String> labels = getLabels(stockHistories);


        LineData data = new LineData(labels, dataset);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
        dataset.setDrawFilled(true);
        dataset.setFillColor(getResources().getColor(R.color.material_blue_700));
        dataset.setFillAlpha(125);
        dataset.setValueTextSize(10f);
        return data;
    }

    private void loadStockHistory(String stockSymbolText) {
        String historyQuery = "select * from yahoo.finance.historicaldata where symbol = \"" + stockSymbolText.toUpperCase() + "\" and startDate = \"" + getAMonthAgo() + "\" and endDate =\"" + getCurrentDate() + "\"";

        new YahooStockServiceFactory().create().stockHistory(historyQuery).enqueue(new Callback<List<StockHistory>>() {
            @Override
            public void onResponse(Call<List<StockHistory>> call, Response<List<StockHistory>> response) {
                if (response.isSuccessful()) {
                    lineChart.setData(getLineData(new StockHistoryMapper().mapStockHistoryForPastMonth(response.body())));
                }

            }

            @Override
            public void onFailure(Call<List<StockHistory>> call, Throwable t) {

            }
        });

    }


    //where we define our dates
    public String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        calendar = Calendar.getInstance();
        String presentDate = dateFormat.format(calendar.getTime());
        Log.v(LOG_TAG, "Today's date is " + presentDate);
        return presentDate;
    }

    public String getAMonthAgo() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, +-1);
        String oneMonthAgo = dateFormat.format(calendar.getTime());
        Log.v(LOG_TAG, "The date one month ago from today was " + oneMonthAgo);
        return oneMonthAgo;

    }

    //method that uses for loop to calculate a list of strings representing
    // each day for the previous 30 days
    private ArrayList<String> getLabels(List<StockHistory>stockHistories){
        ArrayList<String> labels = new ArrayList<>();
        for (int i = 0; i < stockHistories.size(); i++) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            String label = stockHistories.get(i).getDate();
            labels.add(label);
        }
        return labels;
    }

//    public ArrayList<String> getLabels() {
//        ArrayList<String> labels = new ArrayList<>();
//        for(int i = 30; i >=0; i--) {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            calendar = Calendar.getInstance();
//            calendar.add(Calendar.MONTH, +-i);
//            String label = dateFormat.format(calendar.getTime());
//            labels.add(label);
//        }
//        return labels;
//    }

}

