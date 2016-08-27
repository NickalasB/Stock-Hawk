package com.sam_chordas.android.stockhawk.data.mappers;

import android.annotation.TargetApi;
import android.os.Build;

import com.github.mikephil.charting.data.Entry;
import com.sam_chordas.android.stockhawk.data.StockHistory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by nickbradshaw on 8/14/16.
 */
public class StockHistoryMapper {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public List<Entry> mapStockHistoryForPastMonth(List<StockHistory> stockHistoryList) {
        List<Entry> entries = new ArrayList<>();

        int chartIndex = 0;
        for (int i = stockHistoryList.size() - 1; i >= 0; i--) {
            StockHistory stockHistory = stockHistoryList.get(i);
            float stockHistoryClosePrice = Float.parseFloat(stockHistory.getClose());
            entries.add(new Entry(stockHistoryClosePrice, chartIndex));
            chartIndex++;
        }
        return entries;
    }

    public Calendar convertStringToDate(String dateString) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(dateString));
        } catch (ParseException e) {
        }

        return calendar;

    }
}
