package com.sam_chordas.android.stockhawk.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by nickbradshaw on 8/22/16.
 */
public class StockWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        //Return remote view factory here
        return new StockWidgetProvider(this, intent);
    }
}
