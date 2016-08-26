package com.sam_chordas.android.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;

/**
 * Created by nickbradshaw on 8/22/16.
 */
public class StockWidgetProvider extends RemoteViewsService {

    private static final String SYMBOL = "symbol";


    private final String[] STOCK_COLUMNS = {
            QuoteColumns._ID,
            QuoteColumns.BIDPRICE,
            QuoteColumns.SYMBOL,
            QuoteColumns.NAME,
            QuoteColumns.CHANGE,
            QuoteColumns.CREATED,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.ISUP,
            QuoteColumns.ISCURRENT
    };

    static final int INDEX_ID = 0;
    static final int INDEX_BID_PRICE = 1;
    static final int INDEX_SYMBOL = 2;
    static final int INDEX_COMPANY_NAME = 3;
    static final int INDEX_CHANGE = 4;
    static final int INDEX_CREATED = 5;
    static final int INDEX_PERCENT_CHANGE = 6;
    static final int INDEX_IS_UP = 7;
    static final int INDEX_IS_CURRENT = 8;


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;


            @Override
            public void onCreate() {
                //nothing to do at this point
//                initializeData();

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        STOCK_COLUMNS,
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);
                Binder.restoreCallingIdentity(identityToken);

            }

//            private void initializeData() {
//                stockCollection.clear();
//                for (int i = 0; i <= 10; i++) {
//                    stockCollection.add("ListView Item" + i);
//
//                }
//            }


            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }

            }

            @Override
            public int getCount() {
                if (data == null) return 0;
                else return data.getCount();
            }

            //gives us views per list item
            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.stock_widget_list_item);

                String symbol = data.getString(INDEX_SYMBOL);
                mRemoteViews.setTextViewText(R.id.widget_stock_symbol, symbol);

                String bidPrice = data.getString(INDEX_BID_PRICE);
                mRemoteViews.setTextViewText(R.id.widget_bid_price, bidPrice);

                String priceChange = data.getString(INDEX_CHANGE);
                String percentChange = data.getString(INDEX_PERCENT_CHANGE);

                mRemoteViews.setTextViewText(R.id.widget_change, percentChange);
                if (Utils.showPercent) {
                    mRemoteViews.setTextViewText(R.id.widget_change, percentChange);
                } else {
                    mRemoteViews.setTextViewText(R.id.widget_change, priceChange);
                }
                setRemoteContentDescription(mRemoteViews, symbol);


                final Intent intent = new Intent();
                intent.putExtra(SYMBOL, symbol);
                mRemoteViews.setOnClickFillInIntent(R.id.single_stock_widget_list_item_frame_layout, intent);

                return mRemoteViews;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.stock_widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews mRemoteViews, String description) {
                mRemoteViews.setContentDescription(R.id.single_stock_widget_list_item_frame_layout, description);

            }


            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

        };


    }
}


