package com.odoo.addons.caja_chica;

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
import com.odoo.addons.caja_chica.models.AccountBankStatement;
import com.odoo.addons.caja_chica.models.AccountJournal;
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

import static android.R.attr.value;

/**
 * Created by alan on 14/03/17.
 */
// TODO: 15/03/17 Agregar formulario caja details
// TODO: 15/03/17 Agregar distinto color y un icono caja abierta
// TODO: 15/03/17 Agregar funcion al mantener apretado mucho tiempo longclick
// TODO: 15/03/17 Agregar el menu estado cuentas bancos

public class CajaChica extends BaseFragment implements
        OCursorListAdapter.OnViewBindListener, SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, View.OnClickListener, ISyncStatusObserverListener {

    public static final String TAG = CajaChica.class.getSimpleName();
    public static final String EXTRA_KEY_TYPE = "extra_key_type";
    private OCursorListAdapter mAdapter = null;
    private String mCurFilter = null;
    private View mView;
    private boolean syncRequested = false;



    public enum Type {
        Cash, Bank
    }

    private Type mType = Type.Cash;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        setHasSyncStatusObserver(TAG, this, db());
        return inflater.inflate(R.layout.common_listview, container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        setHasSwipeRefreshView(view, R.id.swipe_container, this);
        mView = view;
        mType = Type.valueOf(getArguments().getString(EXTRA_KEY_TYPE));
        ListView mCajaList = (ListView) view.findViewById(R.id.listview);
        mAdapter = new OCursorListAdapter(getActivity(), null, R.layout.caja_row_item);
        mAdapter.setOnViewBindListener(this);
        mCajaList.setAdapter(mAdapter);
        mCajaList.setFastScrollAlwaysVisible(true);
        mCajaList.setOnItemClickListener(this);
        setHasFloatingButton(view,R.id.fabButton, mCajaList, this);
        hideFab();
        getLoaderManager().initLoader(0,null,this);
    }




    @Override
    public void onViewBind(View view, Cursor cursor, ODataRow row) {
        OControls.setText(view, R.id.name, row.getString("name"));
        System.out.println(row.getString("journal_type"));
        if(row.getString("state").equals("confirm"))
            OControls.setTextColor(view, R.id.name, getResources().getColor(R.color.drawer_icon_tint));
        if(row.getString("state").equals("open")) {
            OControls.setTextColor(view, R.id.name, getResources().getColor(R.color.android_red_dark));
            view.findViewById(R.id.img_caja).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
        String where = "";
        List<String> args = new ArrayList<>();
        switch (mType){
            case Cash:
                where = "journal_type like ?";
                args.add("cash");
                break;
            case Bank:
                where = "journal_type like ?";
                args.add("bank");
                break;
        }

        if (mCurFilter != null){
            where += " and name like ?";
            args.add("%"+ mCurFilter+ "%");
        }
        String selection= (args.size()>0) ? where : null;
        String[] selectionArgs = (args.size()>0) ? args.toArray(new String[args.size()]) : null;
        return new CursorLoader(getActivity(), db().uri(),
                null, selection, selectionArgs, "date desc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
        if(data.getCount()>0){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setVisible(mView, R.id.swipe_container);
                    OControls.setGone(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.swipe_container, CajaChica.this);
                }
            },500);
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setGone(mView, R.id.swipe_container);
                    OControls.setVisible(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView,R.id.data_list_no_item, CajaChica.this);
                    OControls.setImage(mView, R.id.icon, R.drawable.ic_action_cash);
                    OControls.setText(mView, R.id.title, _s(R.string.label_no_cash_record_found));
                    OControls.setText(mView, R.id.subTitle, "");
                }
            }, 500);
            if (db().isEmptyTable()&& !syncRequested){
                syncRequested = true;
                onRefresh();
            }
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    @Override
    public Class<AccountBankStatement> database() {
        return AccountBankStatement.class;
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> items = new ArrayList<>();
        items.add(new ODrawerItem(TAG).setTitle("Registros de Caja")
                .setIcon(R.drawable.ic_monetization_on_black_24dp)
                .setExtra(extra(Type.Cash))
                .setInstance(new CajaChica()));
        items.add(new ODrawerItem(TAG)
                .setTitle("Extractos Bancarios")
                .setIcon(R.drawable.ic_account_balance_black_24dp)
                .setExtra(extra(Type.Bank))
                .setInstance(new CajaChica()));
        return items;
    }

    public Bundle extra(Type type) {
        Bundle extra = new Bundle();
        extra.putString(EXTRA_KEY_TYPE, type.toString());
        return extra;
    }

    @Override
    public void onStatusChange(Boolean refreshing) {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onRefresh() {
        if(inNetwork()){
            parent().sync().requestSync(AccountBankStatement.AUTHORITY);
            setSwipeRefreshing(true);
        } else {
            hideRefreshingProgress();
            Toast.makeText(getActivity(),_s(R.string.toast_network_required), Toast.LENGTH_LONG).show();
        }

    }



    private void loadActivity(ODataRow row){
        Bundle data = new Bundle();
        AccountJournal journal = new AccountJournal(getContext(), null);
        System.out.println(journal.browse(row.getInt("journal_id")).getString("type"));
        if (row != null){
            data = row.getPrimaryBundleData();
        }
        IntentUtils.startActivity(getActivity(), CajaDetails.class, data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ODataRow row = OCursorUtils.toDatarow((Cursor) mAdapter.getItem(position));
        loadActivity(row);
    }

    @Override
    public void onClick(View v) {
        //no hacer nada
    }

}
