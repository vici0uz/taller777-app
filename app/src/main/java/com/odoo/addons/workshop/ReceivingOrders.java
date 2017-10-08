package com.odoo.addons.workshop;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.odoo.R;
import com.odoo.addons.workshop.models.WorkshopAutopartReceiving;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.addons.fragment.ISyncStatusObserverListener;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.support.list.OCursorListAdapter;
import com.odoo.core.utils.IntentUtils;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.OCursorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alan on 30/04/17.
 */

public class ReceivingOrders extends BaseFragment implements OCursorListAdapter.OnViewBindListener, View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener, ISyncStatusObserverListener {

    public static final String TAG = ReceivingOrders.class.getSimpleName();
    private View mView;
    private ListView mReceivingOrdersList;
    private OCursorListAdapter mAdapter = null;
    private String mCurfilter;
    private boolean syncRequested = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        setHasSyncStatusObserver(TAG, this,db());
        return inflater.inflate(R.layout.common_listview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        mReceivingOrdersList = (ListView) view.findViewById(R.id.listview);
        mAdapter = new OCursorListAdapter(getActivity(), null, R.layout.receiving_order_row_item);
        mAdapter.setOnViewBindListener(this);
        mReceivingOrdersList.setAdapter(mAdapter);
        setHasFloatingButton(mView, R.id.fabButton, mReceivingOrdersList, this);
        mReceivingOrdersList.setOnItemClickListener(this);
        hideFab();
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> items = new ArrayList<>();
        items.add(new ODrawerItem(TAG).setTitle("Repuestos")
        .setIcon(R.drawable.ic_move_to_inbox_black_24dp)
        .setInstance(new ReceivingOrders()));
        return items;
    }

    @Override
    public Class<WorkshopAutopartReceiving> database() {
        return WorkshopAutopartReceiving.class;
    }

    @Override
    public void onViewBind(View view, Cursor cursor, ODataRow row) {
        OControls.setText(view, R.id.name, row.getString("name"));
        if(row.getBoolean("processed")){
            OControls.setVisible(view, R.id.completado);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        List<String> args = new ArrayList<>();
        String where = "";
        if (mCurfilter != null){
            where += "name like ? COLLATE NOCASE";
            args.add("%" + mCurfilter + "%");
        }
        String selection = (args.size()>0)? where : null;
        String[] selectionArgs = (args.size()>0) ? args.toArray(new String[args.size()]): null;
        return new CursorLoader(getActivity(), db().uri(), null, selection, selectionArgs, "create_date desc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
        if (data.getCount() > 0){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setVisible(mView, R.id.swipe_container);
                    OControls.setGone(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.swipe_container, ReceivingOrders.this);
                }
            }, 500);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setGone(mView, R.id.swipe_container);
                    OControls.setGone(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.data_list_no_item, ReceivingOrders.this);
                    OControls.setImage(mView, R.id.icon, R.drawable.ic_view_list_black_24dp);
                    OControls.setText(mView, R.id.title, _s(R.string.label_no_orders_found));

                }
            }, 500);
            if (db().isEmptyTable() && !syncRequested){
                syncRequested = true;
                onRefresh();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onRefresh() {
        if(inNetwork()){
            parent().sync().requestSync(WorkshopAutopartReceiving.AUTHORITY);
            setSwipeRefreshing(true);
        } else {
            hideRefreshingProgress();
            Toast.makeText(getActivity(), _s(R.string.toast_network_required), Toast.LENGTH_LONG).show();
        }
    }

    private void loadActivity(ODataRow row){
        Bundle data = new Bundle();
        if (row != null){
            data = row.getPrimaryBundleData();
        }
        IntentUtils.startActivity(getActivity(), ReceivingOrdersDetails.class, data);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       ODataRow row = OCursorUtils.toDatarow((Cursor) mAdapter.getItem(position));
       loadActivity(row);
    }

    @Override
    public void onStatusChange(Boolean refreshing) {
        getLoaderManager().restartLoader(0,null,this);
    }
}
