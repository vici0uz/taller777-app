package com.odoo.addons.workshop;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.App;
import com.odoo.R;
import com.odoo.addons.caja_chica.Tabla;
import com.odoo.addons.workshop.models.WorkshopAutopartReceiving;
import com.odoo.addons.workshop.models.WorkshopAutopartReceivingLot;
import com.odoo.addons.workshop.models.WorkshopAutopartStockLocation;
import com.odoo.addons.workshop.models.WorkshopNewAutopartType;
import com.odoo.addons.workshop.utils.MessageEvent;
import com.odoo.base.addons.ir.feature.OFileManager;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.rpc.helper.OArguments;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.OStringColorUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;
import odoo.controls.OForm;

/**
 * Created by alan on 04/05/17.
 */

public class ReceivingOrdersDetails extends OdooCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private App app;
    private WorkshopAutopartReceiving workshopAutopartReceiving;
    private int rowId;
    private ODataRow record = null;
    private Bundle extras;
    private OForm mForm;
    private OFileManager fileManager;
    private Boolean mEditMode = false;
    private WorkshopNewAutopartType autopartType;
    private Context mContext;
    private TextView mResultTextView;
    private WorkshopAutopartStockLocation stockLocation;
    private WorkshopAutopartReceivingLot receivingLot;
    private Menu mMenu;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receiving_order_detail);
        mContext = getApplicationContext();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fileManager = new OFileManager(this);
        if (toolbar != null)
            toolbar.setTitle("");
        app = (App) getApplicationContext();
        workshopAutopartReceiving = new WorkshopAutopartReceiving(this, null);
        extras = getIntent().getExtras();
        if (!hasRecordInExtra())
            mEditMode = true;
        setupToolbar();
        findViewById(R.id.vehicle_id).setOnClickListener(this);
    }

    private boolean hasRecordInExtra() {
        return extras != null && extras.containsKey(OColumn.ROW_ID);
    }

    private void setMode(Boolean edit) {
//        if(mMenu != null){
//            mMenu.findI
//        }
        int color = Color.DKGRAY;
        if (record != null) {
            color = OStringColorUtil.getStringColor(this, record.getString("name"));
        }
        if (edit) {
            if (!hasRecordInExtra()) {
                toolbar.setTitle("New");
            }
            mForm = (OForm) findViewById(R.id.receivingOrderFormEdit);
            findViewById(R.id.order_view_layout).setVisibility(View.GONE);
            findViewById(R.id.receiving_order_detail_edit_layout).setVisibility(View.VISIBLE);
        } else {
            mForm = (OForm) findViewById(R.id.orderForm);
            findViewById(R.id.receiving_order_detail_edit_layout).setVisibility(View.GONE);
            findViewById(R.id.order_view_layout).setVisibility(View.VISIBLE);
        }
    }

    private void setupToolbar() {
        if (!hasRecordInExtra()) {
            setMode(mEditMode);
            mForm.setEditable(mEditMode);
            mForm.initForm(null);
        } else {
            rowId = extras.getInt(OColumn.ROW_ID);
            record = workshopAutopartReceiving.browse(rowId);
            setMode(mEditMode);
            mForm.setEditable(mEditMode);
            mForm.initForm(record);
            getSupportActionBar().setTitle(record.getString("name"));
            setupList();
        }
    }

    private void setupList() {
        List lines = record.getO2MRecord("autopart_receiving_lot_ids").browseEach();
        autopartType = new WorkshopNewAutopartType(this, null);
        stockLocation = new WorkshopAutopartStockLocation(this, null);
        receivingLot = new WorkshopAutopartReceivingLot(this, null);
        ArrayList<String> parte = new ArrayList<>();
        ArrayList<String> cantidad = new ArrayList<>();
        ArrayList<String> location = new ArrayList<>();
        ArrayList<String> lot_id = new ArrayList<>();
        ArrayList<String> stored = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String l = lines.get(i).toString();
//            Parte
            Pattern patternPart = Pattern.compile("\\snew_autopart_type_id=(.*?),");
            Matcher matcherPart = patternPart.matcher(l);
            if (matcherPart.find()) {
                ODataRow recordAutopart = autopartType.browse((Integer.parseInt(matcherPart.group(1))));
                parte.add(recordAutopart.getString("name"));
            }

//            Cantidad
            Pattern patternCantidad = Pattern.compile("\\sqty=(.*?),");
            Matcher matcherCantidad = patternCantidad.matcher(l);
            if (matcherCantidad.find())
                cantidad.add(matcherCantidad.group(1));

//            Stock Location
            Pattern patternLocation = Pattern.compile("stock_location_id=(.*?),");
            Matcher matcherLocation = patternLocation.matcher(l);
            if (matcherLocation.find()) {
                if (!matcherLocation.group(1).equals("false")) {
                    ODataRow recordStockLocation = stockLocation.browse(Integer.parseInt(matcherLocation.group(1)));
                    location.add(recordStockLocation.getString("complete_name"));
                } else
                    location.add(getResources().getString(R.string.label_no_data));
            }

//            ID
            Pattern patternLotId = Pattern.compile("\\s_id=(.*?),");
            Matcher matcherLotId = patternLotId.matcher(l);
            if (matcherLotId.find()) {
                String id = matcherLotId.group(1);
                lot_id.add(id);
            }

//            Check
            Pattern patternStored = Pattern.compile("\\sstored=(.*?),");
            Matcher matcherStored = patternStored.matcher(l);
            if (matcherStored.find()) {
                String st = matcherStored.group(1);
                stored.add(st);
            }
        }

        Tabla tabla = new Tabla(this, (TableLayout) findViewById(R.id.tabla), getApplicationContext());
        tabla.agregarCabecera(R.array.cabecera_partes);
        for (int x = 0; x < parte.size(); x++) {
            ArrayList<String> elementos = new ArrayList<String>();
            elementos.add(stored.get(x));
            elementos.add(parte.get(x));
            elementos.add(cantidad.get(x));
            elementos.add(location.get(x));
            elementos.add(lot_id.get(x));
            tabla.agregarFilaTabla(elementos, x);
        }
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.vehicle_id:
//                Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
//                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);

//                break;
//        }
    }

    // TODO: 16/07/17 Cambiar de EventBus a una interface
    public void onEvent(MessageEvent event) {
        int lotId = event.lotId;
        int locationId = event.locationId;
        ODataRow registro = receivingLot.browse(lotId);
        registro.getAll();
        OValues values = new OValues();
        String sqlStr = "SELECT _id FROM workshop_autopart_stock_location WHERE id = ?";
        String[] arr = new String[1];
        arr[0] = String.valueOf(locationId);
        List<ODataRow> q = stockLocation.query(sqlStr, arr);
        int locID = q.get(0).getInt("_id");
        values.put("stock_location_id", locID);
        values.put("stored", "true");

        List lines = record.getO2MRecord("autopart_receiving_lot_ids").browseEach();
        Boolean marcador = new Boolean(true);
        for (int i = 0; i < lines.size(); i++) {
            Pattern patternStored = Pattern.compile("\\sstored=(.*?),");
            Matcher matcherStored = patternStored.matcher(lines.get(i).toString());
            if (matcherStored.find()) {
                String st = matcherStored.group(1);
                System.out.println("ALAN DEBUG: valor st" + st);

                if (st.equals("false"))
                    marcador = false;
            }
            if (marcador.equals(false)) {
                break;
            }
        }
        receivingLot.update(lotId, values);
        receivingLot.sync().requestSync(WorkshopAutopartReceivingLot.AUTHORITY);
        OValues values1 = new OValues();
        values1.put("processed", marcador);
        workshopAutopartReceiving.update(record.getInt(OColumn.ROW_ID), values1);
        workshopAutopartReceiving.sync().requestSync(WorkshopAutopartReceiving.AUTHORITY);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_receive_order_detail, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_accion:
//                if(record.getString("processed").equals("false"))
//                    Toast.makeText(mContext, R.string.toast_not_processed, Toast.LENGTH_SHORT).show();
//                else {
                System.out.println("impresa");
                Toast.makeText(mContext, R.string.toast_printing, Toast.LENGTH_SHORT).show();
                RecOrderOperations recOrderOperations = new RecOrderOperations();
                recOrderOperations.execute(record);
//                }
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    private class RecOrderOperations extends AsyncTask<ODataRow, Void, Void> {

        @Override
        protected Void doInBackground(ODataRow... params) {
            OArguments args = new OArguments();
            JSONArray dat = new JSONArray();
            dat.put(record.getInt("id"));
            args.add(dat);
            workshopAutopartReceiving.getServerDataHelper().callMethod("print_direct_report", args);
            return null;
        }
    }
}
