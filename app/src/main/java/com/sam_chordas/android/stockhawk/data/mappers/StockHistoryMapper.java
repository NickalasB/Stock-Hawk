package com.sam_chordas.android.stockhawk.data.mappers;

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

    private String LOG_TAG = StockHistoryMapper.class.getSimpleName();

    public List<Entry> mapStockHistoryForPastMonth(List<StockHistory> stockHistoryList) {
        List<Entry> entries = new ArrayList<>();

        int currentMonth = 0;
        float currentHigh = 0;
        int chartIndex = 0;


        for (int i = stockHistoryList.size() - 1; i >= 0; i--) {
//        for (int i = 0; i < stockHistoryList.size(); i++) {

            StockHistory stockHistory = stockHistoryList.get(i);
            Calendar stockHistoryCalendar = convertStringToDate(stockHistory.getDate());
            float stockHistoryHighPrice = Float.parseFloat(stockHistory.getClose());
            int stockHistoryCalendarMonth = stockHistoryCalendar.get(Calendar.DAY_OF_MONTH);

            if (currentMonth != stockHistoryCalendarMonth) {
                if (currentMonth != 0) {
                    entries.add(new Entry(currentHigh, chartIndex));
                    chartIndex++;
                    currentHigh = 0;
                }

                currentMonth = stockHistoryCalendarMonth;
            }
            if (currentHigh < stockHistoryHighPrice) {
                currentHigh = stockHistoryHighPrice;
            }
            if (i == stockHistoryList.size() - 1) {
                entries.add(new Entry(currentHigh, chartIndex));
//                chartIndex++;

            }
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
