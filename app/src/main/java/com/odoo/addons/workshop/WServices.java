package com.odoo.addons.workshop;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.R;
import com.odoo.addons.workshop.autopart_receiving.Order;
import com.odoo.addons.workshop.models.WorkshopService;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.addons.fragment.IOnSearchViewChangeListener;
import com.odoo.core.support.addons.fragment.ISyncStatusObserverListener;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.support.list.OCursorListAdapter;
import com.odoo.core.utils.IntentUtils;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.OCursorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alan on 18/01/17.
 */

public class WServices extends BaseFragment implements OCursorListAdapter.OnViewBindListener,
        LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener, ISyncStatusObserverListener, IOnSearchViewChangeListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemSelectedListener {

    public static final String KEY = WServices.class.getSimpleName();
    private View mView;
    private String mCurfilter = null;
    private OCursorListAdapter mAdapter = null;
    private boolean syncRequested = false;
    private String wState = null;
    private Spinner spinner;
    private ODataRow record = null;
    private WorkshopService workshopService;
    private int rowId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setHasSyncStatusObserver(KEY, this, db());
        return inflater.inflate(R.layout.common_listview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasSwipeRefreshView(view, R.id.swipe_container, this);
        mView = view;
        ListView mWServicesList = (ListView) view.findViewById(R.id.listview);
        mAdapter = new OCursorListAdapter(getActivity(), null, R.layout.workshop_service_row_item);
        mAdapter.setOnViewBindListener(this);
        mWServicesList.setAdapter(mAdapter);
        mWServicesList.setFastScrollAlwaysVisible(true);
        mWServicesList.setOnItemClickListener(this);
        mWServicesList.setOnItemLongClickListener(this);
        parent().setHasActionBarSpinner(true);
        setHasFloatingButton(view, R.id.fabButton, mWServicesList, this);
        hideFab();
        initSpinner();
        getLoaderManager().initLoader(0, null, this);
        workshopService = new WorkshopService(getActivity(), null);

    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> items = new ArrayList<>();
        items.add(new ODrawerItem(KEY).setTitle("Services")
                .setIcon(R.drawable.ic_view_list_black_24dp)
                .setInstance(new WServices()));
        return items;
    }

    @Override
    public Class<WorkshopService> database() {
        return WorkshopService.class;
    }

    @Override
    public void onViewBind(View view, Cursor cursor, ODataRow row) {
        if (!row.getBoolean("have_images"))
            OControls.setGone(view, R.id.have_images_badge);

//        List autopart_receiving_ids = row.getO2MRecord("autopart_ids").browseEach();
//        System.out.println(autopart_receiving_ids.size());
        OControls.setText(view, R.id.name, row.getString("name"));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
        List<String> args = new ArrayList<>();
        String where = "";
        if (mCurfilter != null) {
            where += "name like ? COLLATE NOCASE";
            args.add("%" + mCurfilter + "%");
            if (wState != null) {
                where += " and state like ?";
                args.add("%" + wState + "%");
            }
        } else if (wState != null) {
            where += "state like ?";
            args.add("%" + wState + "%");
        }
        String selection = (args.size() > 0) ? where : null;
        String[] selectionArgs = (args.size() > 0) ? args.toArray(new String[args.size()]) : null;
        return new CursorLoader(getActivity(), db().uri(), null, selection, selectionArgs, "name");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
        if (data.getCount() > 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setVisible(mView, R.id.swipe_container);
                    OControls.setGone(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.swipe_container, WServices.this);
                }
            }, 500);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setGone(mView, R.id.swipe_container);
                    OControls.setVisible(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.data_list_no_item, WServices.this);
                    OControls.setImage(mView, R.id.icon, R.drawable.ic_view_list_black_24dp);
                    OControls.setText(mView, R.id.title, _s(R.string.label_no_service_found));
                    OControls.setText(mView, R.id.subTitle, "");
                }
            }, 500);
            if (db().isEmptyTable() && !syncRequested) {
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
    public void onRefresh() {
        if (inNetwork()) {
            parent().sync().requestSync(WorkshopService.AUTHORITY);
            setSwipeRefreshing(true);
        } else {
            hideRefreshingProgress();
            Toast.makeText(getActivity(), _s(R.string.toast_network_required), Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_partners, menu);
        setHasSearchView(this, menu, R.id.menu_partner_search);
    }

    @Override
    public void onClick(View view) {
//        NO PASA NARANJAS
    }

    @Override
    public void onStatusChange(Boolean refreshing) {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public boolean onSearchViewTextChange(String newFilter) {
        mCurfilter = newFilter;
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }

    @Override
    public void onSearchViewClose() {
        //do nothing
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        ODataRow row = OCursorUtils.toDatarow((Cursor) mAdapter.getItem(position));
        loadActivity(row);
    }



    private void loadActivity(ODataRow row) {
        Bundle data = new Bundle();
        if (row != null) {
            data = row.getPrimaryBundleData();
        }
        IntentUtils.startActivity(getActivity(), ServiceDetails.class, data);
    }

    private void initSpinner() {
        // TODO: 13/03/17 Agregar filtro tiene imagenes 
        if (getActivity() == null) {
            return;
        }
        List<String> list = new ArrayList<String>();
        list.add("Estado");   //  Initial dummy entry
        list.add("Borrador");
        list.add("Aprobado");

        int hidingItemIndex = 0;

        spinner = parent().getActionBarSpinner();
        CustomAdapter dataAdapter = new CustomAdapter(getContext(), R.layout.spinner, list, hidingItemIndex);
        dataAdapter.setDropDownViewResource(R.layout.spinner);

        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

        switch (pos) {
            case 1:
                wState = "draft";
                break;
            case 2:
                wState = "approved";
                break;
            case 3:
                wState = "pending";
                break;
        }
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ODataRow row = OCursorUtils.toDatarow((Cursor) mAdapter.getItem(position));
        Bundle data = new Bundle();
        if (row != null) {
            data = row.getPrimaryBundleData();
        }
        loadDialog(data);

        return true;
    }


    public class CustomAdapter extends ArrayAdapter<String> {

        private int hidingItemIndex;

        CustomAdapter(Context context, int textViewResourceId, List<String> objects, int hidingItemIndex) {
            super(context, textViewResourceId, objects);
            this.hidingItemIndex = hidingItemIndex;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View v = null;
            if (position == hidingItemIndex) {
                TextView tv = new TextView(getContext());
                tv.setVisibility(View.GONE);
                v = tv;
            } else {
                v = super.getDropDownView(position, null, parent);
            }
            return v;
        }
    }


    private void loadDialog(Bundle data){

        rowId = data.getInt(OColumn.ROW_ID);
        System.out.println("ALAN DEBUG: " + rowId);
        record = workshopService.browse(rowId);

        List autopart_receiving_ids = record.getO2MRecord("autopart_ids").browseEach();
        if (autopart_receiving_ids.size() > 0) {
            setupList(autopart_receiving_ids);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("rec_dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ServiceOrdersDialogFragment newFragment = ServiceOrdersDialogFragment.newInstance(getActivity(), setupList(autopart_receiving_ids));
            newFragment.show(getActivity().getSupportFragmentManager(), "rec_dialog");
        } else
            Toast.makeText(getContext(), R.string.toast_no_orders, Toast.LENGTH_SHORT).show();

    }

    private ArrayList setupList(List lines) {

        ArrayList<Order> ordenes = new ArrayList<Order>();
        if (lines.size() > 0) {
            for (int i = 0; i < lines.size(); i++) {
                String l = lines.get(i).toString();
                System.out.println(l);
                String name = "";
                String fecha = "";
                int id = 1;
//              Nombre
                Pattern patternName = Pattern.compile("\\sname=(.*?),");
                Matcher matcherName = patternName.matcher(l);
                if (matcherName.find())
                    name = matcherName.group(1);

//              Fecha
                Pattern patternFecha = Pattern.compile("\\screate_date=(.*?)\\s");
                Matcher matcherFecha = patternFecha.matcher(l);
                if (matcherFecha.find())
                    fecha = matcherFecha.group(1);

                Pattern patternId = Pattern.compile("\\s_id=(.*?),");
                Matcher matcherId = patternId.matcher(l);
                if (matcherId.find())
                    id = Integer.parseInt(matcherId.group(1));

                ordenes.add(new Order(name, fecha, id));
            }

        }
        return ordenes;
    }


}
