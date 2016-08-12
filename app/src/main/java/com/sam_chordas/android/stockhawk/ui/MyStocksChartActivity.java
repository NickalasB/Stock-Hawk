package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

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


    private String LOG_TAG = MyStocksChartActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_my_stocks);
        ButterKnife.bind(this);

        Intent mChartIntent = getIntent();
        if (mChartIntent.hasExtra("SYMBOL"))
            if (symbolTextView != null) {
//                nameTextView.setText(getIntent().getStringExtra("NAME"));
                symbolTextView.setText(getIntent().getStringExtra("SYMBOL").toUpperCase());
                bidPriceTextView.setText(getIntent().getStringExtra("BIDPRICE"));
                percentChangeTextView.setText(getIntent().getStringExtra("PERCENT_CHANGE"));

            }

        LineChart lineChart = (LineChart) findViewById(R.id.chart1);
        lineChart.setNoDataTextDescription("No historical data yet");

        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(4f, 0));
        entries.add(new Entry(8f, 1));
        entries.add(new Entry(6f, 2));
        entries.add(new Entry(2f, 3));
        entries.add(new Entry(18f, 4));
        entries.add(new Entry(9f, 5));
        entries.add(new Entry(14f, 6));
        entries.add(new Entry(7f, 7));
        entries.add(new Entry(12f, 8));
        entries.add(new Entry(13f, 9));
        entries.add(new Entry(1f, 10));
        entries.add(new Entry(3f, 11));

        chartLegend = lineChart.getLegend();
        chartLegend.setEnabled(false);
        xAxis = lineChart.getXAxis();
        yAxisLeft = lineChart.getAxisLeft();
        yAxisRight = lineChart.getAxisRight();
        yAxisLeft.setTextSize(12);
        yAxisRight.setTextSize(12);
        xAxis.setTextSize(12);


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

        lineChart.setData(data);
        lineChart.animateY(5000);




    }



}

