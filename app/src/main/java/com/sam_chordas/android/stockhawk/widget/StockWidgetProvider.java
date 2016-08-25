package com.sam_chordas.android.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nickbradshaw on 8/22/16.
 */
public class StockWidgetProvider implements RemoteViewsService.RemoteViewsFactory {

    List<String> stockCollection = new ArrayList<>();
    Context mContext;
    Intent mIntent;

    private void initializeData(){
        stockCollection.clear();
        for (int i = 0; i <= 10; i++) {
            stockCollection.add("ListView Item" + i);

        }
    }


    public StockWidgetProvider(Context mContext, Intent mIntent) {
        this.mContext = mContext;
        this.mIntent = mIntent;
    }




    @Override
    public void onCreate() {
        initializeData();

    }

    @Override
    public void onDataSetChanged() {
        initializeData();

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return stockCollection.size();
    }

    //gives us views per list item
    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteView = new RemoteViews(mContext.getPackageName(),
                R.layout.stock_widget_list_item);
        remoteView.setTextViewText(android.R.id.text1, stockCollection.get(position));
        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}


//
//    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//        String stockSymbol = "GOOG";
//        String description = "+.35";
//        String bid_price = "774.87";
//        String formattedBidPrice = Utils.truncateBidPrice(bid_price);
//
//        // Perform this loop procedure for each Today widget
//        for (int appWidgetId : appWidgetIds) {
//            int layoutId = R.layout.stock_widget;
//            RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);
//
//            // Add the data to the RemoteViews
//            views.setTextViewText(R.id.widget_stock_symbol, stockSymbol);
//            // Content Descriptions for RemoteViews were only added in ICS MR1
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//                setRemoteContentDescription(views, description);
//            }
//            views.setTextViewText(R.id.widget_bid_price, formattedBidPrice);
//
//            // Create an Intent to launch MainActivity
//            Intent launchIntent = new Intent(context, MyStocksActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
//            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
//
//            // Tell the AppWidgetManager to perform an update on the current app widget
//            appWidgetManager.updateAppWidget(appWidgetId, views);
//        }
//    }
//
//
//
//
//    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
//    private void setRemoteContentDescription(RemoteViews views, String description) {
//        views.setContentDescription(R.id.widget_stock_symbol, description);
//    }
//}




