package com.odoo.addons.workshop;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TableLayout;

import com.odoo.App;
import com.odoo.R;
import com.odoo.addons.caja_chica.Tabla;
import com.odoo.addons.workshop.models.WorkshopAutopartReceiving;
import com.odoo.addons.workshop.models.WorkshopNewAutopartType;
import com.odoo.base.addons.ir.feature.OFileManager;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.OStringColorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import odoo.controls.OForm;

/**
 * Created by alan on 04/05/17.
 */

public class ReceivingOrdersDetails extends OdooCompatActivity {

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receiving_order_detail);

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
    }
    private boolean hasRecordInExtra(){
        return extras != null && extras.containsKey(OColumn.ROW_ID);
    }

    private void setMode(Boolean edit){
//        if(mMenu != null){
//            mMenu.findI
//        }
        int color = Color.DKGRAY;
        if(record != null){
            color = OStringColorUtil.getStringColor(this, record.getString("name"));
        }
        if (edit){
            if(!hasRecordInExtra()){
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
    private void setupToolbar(){
        if (!hasRecordInExtra()){
            setMode(mEditMode);
            mForm.setEditable(mEditMode);
            mForm.initForm(null);
        } else{
            rowId = extras.getInt(OColumn.ROW_ID);
            record = workshopAutopartReceiving.browse(rowId);
            setMode(mEditMode);
            mForm.setEditable(mEditMode);
            mForm.initForm(record);
            getSupportActionBar().setTitle(record.getString("name"));
            setupList();
        }
    }

    private void setupList(){
        List lines = record.getO2MRecord("autopart_receiving_lot_ids").browseEach();
        autopartType = new WorkshopNewAutopartType(this, null);
        System.out.println(lines);
        ArrayList<String> parte = new ArrayList<>();
        ArrayList<String> cantidad = new ArrayList<>();
        for(int i= 0; i<lines.size(); i++){
            String l = lines.get(i).toString();
//            Parte
            Pattern patternPart = Pattern.compile("\\snew_autopart_type_id=(.*?),");
            Matcher matcherPart = patternPart.matcher(l);
            if(matcherPart.find()) {
                record = autopartType.browse((Integer.parseInt(matcherPart.group(1))));
                parte.add(record.getString("name"));
            }
//            Cantidad
            Pattern patternCantidad = Pattern.compile("\\sqty=(.*?),");
            Matcher matcherCantidad = patternCantidad.matcher(l);
            if(matcherCantidad.find())
                cantidad.add(matcherCantidad.group(1));

        }

        Tabla tabla = new Tabla(this, (TableLayout)findViewById(R.id.tabla), getApplicationContext());
        tabla.agregarCabecera(R.array.cabecera_partes);
        for(int x=0; x<parte.size(); x++){
            ArrayList<String> elementos = new ArrayList<String>();
            elementos.add(parte.get(x));
            elementos.add(cantidad.get(x));
            tabla.agregarFilaTabla(elementos);
        }
    }
}
