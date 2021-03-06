package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.melnykov.fab.FloatingActionButton;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.QuoteCursorAdapter;
import com.sam_chordas.android.stockhawk.rest.RecyclerViewItemClickListener;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.service.StockIntentService;
import com.sam_chordas.android.stockhawk.service.StockTaskService;
import com.sam_chordas.android.stockhawk.touch_helper.SimpleItemTouchHelperCallback;
import com.sam_chordas.android.stockhawk.widget.StockHawkAppWidget;

import butterknife.ButterKnife;

public class MyStocksActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String UNIT_CHANGE_ICON = "unit_change_icon";
    private int mUnitChangeIcon = R.drawable.ic_attach_percentage__white_24dp;


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Intent mServiceIntent;
    private Intent mChartIntent;
    private ItemTouchHelper mItemTouchHelper;
    private static final int CURSOR_LOADER_ID = 0;
    private QuoteCursorAdapter mCursorAdapter;
    private Context mContext;
    private Cursor mCursor;
    boolean isConnected;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final String LOG_TAG = MyStocksActivity.class.getSimpleName();


        super.onCreate(savedInstanceState);
        mContext = this;
        ButterKnife.bind(this);
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        setContentView(R.layout.activity_my_stocks);
        // The intent service is for executing immediate pulls from the Yahoo API
        // GCMTaskService can only schedule tasks, they cannot execute immediately
        mServiceIntent = new Intent(this, StockIntentService.class);
        if (savedInstanceState == null) {
            // Run the initialize task service so that some stocks appear upon an empty database
            mServiceIntent.putExtra("tag", "init");
            if (isConnected) {
                startService(mServiceIntent);
            } else {
                networkToast();
            }
        } else {
            mUnitChangeIcon = savedInstanceState.getInt(UNIT_CHANGE_ICON, R.drawable.ic_attach_percentage__white_24dp);
        }
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        emptyView = findViewById(R.id.empty_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        mCursorAdapter = new QuoteCursorAdapter(this, null);
        recyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(this,
                new RecyclerViewItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        //TODO:
                        // do something on item click
                        Cursor cursor = mCursorAdapter.getCursor();
                        cursor.moveToPosition(position);
                        mChartIntent = new Intent(getApplicationContext(), MyStocksChartActivity.class);

                        String symbol = mCursor.getString(mCursor.getColumnIndex("symbol")).toUpperCase();
                        String bid_price = mCursor.getString(mCursor.getColumnIndex("bid_price"));
                        String percent_change = mCursor.getString(mCursor.getColumnIndex("percent_change"));
                        String name = mCursor.getString(mCursor.getColumnIndex("Name"));


                        mChartIntent.putExtra("SYMBOL", symbol);
                        mChartIntent.putExtra("BIDPRICE", bid_price);
                        mChartIntent.putExtra("PERCENT_CHANGE", percent_change);
                        mChartIntent.putExtra("NAME", name);


                        startActivity(mChartIntent);


                    }
                }));

        recyclerView.setAdapter(mCursorAdapter);
        checkAdapterIsEmpty();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToRecyclerView(recyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected) {
                    new MaterialDialog.Builder(mContext).title(R.string.symbol_search)
                            .content(R.string.content_test)
                            .inputType(InputType.TYPE_CLASS_TEXT)
                            .inputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS)
                            .input(R.string.input_hint, R.string.input_prefill, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    // On FAB click, receive user input. Make sure the stock doesn't already exist
                                    // in the DB and proceed accordingly
                                    Cursor c = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                                            new String[]{QuoteColumns.SYMBOL}, QuoteColumns.SYMBOL + "= ?",
                                            new String[]{input.toString()}, null);
                                    if (c.getCount() != 0) {
                                        Toast toast =
                                                Toast.makeText(MyStocksActivity.this, getString(R.string.already_saved_stock),
                                                        Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, Gravity.CENTER, 0);
                                        toast.show();
                                        return;
                                    } else {
                                        // Add the stock to DB
                                        mServiceIntent.putExtra("tag", "add");
                                        mServiceIntent.putExtra("symbol", input.toString());
                                        startService(mServiceIntent);


                                    }

                                }
                            })
                            .show();

                } else {
                    networkToast();
                }

            }
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mCursorAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        mTitle = getTitle();
        if (isConnected) {
            long period = 3600L;
            long flex = 10L;
            String periodicTag = "periodic";

            // create a periodic task to pull stocks once every hour after the app has been opened. This
            // is so Widget data stays up to date.
            PeriodicTask periodicTask = new PeriodicTask.Builder()
                    .setService(StockTaskService.class)
                    .setPeriod(period)
                    .setFlex(flex)
                    .setTag(periodicTag)
                    .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                    .setRequiresCharging(false)
                    .build();
            // Schedule task with tag "periodic." This ensure that only the stocks present in the DB
            // are updated.
            GcmNetworkManager.getInstance(this).schedule(periodicTask);
        }
    }

    //display our emptyView if there are no stocks in the adapter
    private Boolean checkAdapterIsEmpty() {
        if (mCursorAdapter.getItemCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            return true;
        } else {
            emptyView.setVisibility(View.GONE);
        }
        return false;
    }


    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }

    public void networkToast() {
        Toast.makeText(mContext, getString(R.string.network_toast), Toast.LENGTH_SHORT).show();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_stocks, menu);
        restoreActionBar();
        MenuItem menuItem = menu.findItem(R.id.action_change_units);
        if (menuItem != null) {
            menuItem.setIcon(mUnitChangeIcon);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_change_units:
                Utils.showPercent = !Utils.showPercent;
                this.getContentResolver().notifyChange(QuoteProvider.Quotes.CONTENT_URI, null);
                //check to see if percent change or price change is selected then set
                //the respective icon
                if (Utils.showPercent) {
                    mUnitChangeIcon = R.drawable.ic_attach_percentage__white_24dp;
                } else {
                    mUnitChangeIcon = R.drawable.ic_attach_money_white_24dp;
                }
                item.setIcon(mUnitChangeIcon);
                return true;
            default:

                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(UNIT_CHANGE_ICON, mUnitChangeIcon);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This narrows the return to only the stocks that are most current.
        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns._ID,
                        QuoteColumns.SYMBOL,
                        QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE,
                        QuoteColumns.CHANGE,
                        QuoteColumns.NAME,
                        QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String LOG_TAG = MyStocksActivity.class.getSimpleName();

        mCursorAdapter.swapCursor(data);
        mCursor = data;
        checkAdapterIsEmpty();
        updateWidgets();
//        Log.v(LOG_TAG, "This should return the number of stocks  " + mCursorAdapter.getItemCount());
//        Log.v(LOG_TAG, "This should return a true if list is empty " + checkAdapterIsEmpty());
//        Log.v(LOG_TAG, "And the name of the stock is... " + QuoteColumns.NAME);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    //this ensures widgets get updated when user adds or deletes a stock
    private void updateWidgets() {
        ComponentName name = new ComponentName(this, StockHawkAppWidget.class);
        int[] ids = AppWidgetManager.getInstance(this).getAppWidgetIds(name);
        Intent intent = new Intent(this, StockHawkAppWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);

    }
}
