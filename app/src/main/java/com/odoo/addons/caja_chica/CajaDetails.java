package com.odoo.addons.caja_chica;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.odoo.R;
import com.odoo.addons.caja_chica.models.AccountBankStatement;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.JSONUtils;
import com.odoo.core.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import odoo.controls.OField;
import odoo.controls.OForm;

/**
 * Created by alan on 15/03/17.
 */

public class CajaDetails extends OdooCompatActivity {
    public static final String TAG = CajaDetails.class.getSimpleName();
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private AccountBankStatement accountBankStatement;
    private Bundle extras;
    private ODataRow record = null;
    private OForm mForm;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.caja_detail);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.caja_collapsing_toolbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        extras = getIntent().getExtras();

        accountBankStatement = new AccountBankStatement(this, null);
        setupToolbar();
    }

    private void setupToolbar(){
        int rowId = extras.getInt(OColumn.ROW_ID);
        record = accountBankStatement.browse(rowId);
        mForm = (OForm) findViewById(R.id.cajaForm);
        mForm.initForm(record);
        collapsingToolbarLayout.setTitle(record.getString("name"));
        setupList();
    }

    private void setupList(){
        List lines = record.getO2MRecord("line_ids").browseEach();
        ArrayList<String> concepto = new ArrayList<String>();
        ArrayList<String> monto = new ArrayList<String>();
        ArrayList<String> date = new ArrayList<String>();
        for(int i= 0; i<lines.size(); i++){
            String l = lines.get(i).toString();
//            NAME
            Pattern patternName = Pattern.compile("name=(.*?),");
            Matcher matcherName = patternName.matcher(l);
            if (matcherName.find())
                concepto.add(matcherName.group(1));
//            MONTO
            Pattern patternMount = Pattern.compile("mount=(.*?),");
            Matcher matcherMount = patternMount.matcher(l);
            if(matcherMount.find())
                monto.add(matcherMount.group(1));
//            Fecha
            Pattern patternDate = Pattern.compile("\\sdate=(.*?),");
            Matcher matcherDate = patternDate.matcher(l);
            if(matcherDate.find())
                date.add(matcherDate.group(1));

        }
        for(String p: concepto)
            System.out.println(p);
        Tabla tabla = new Tabla(this, (TableLayout)findViewById(R.id.tabla));
        tabla.agregarCabecera(R.array.cabecera_tabla);
        for(int x=0; x<concepto.size(); x++){
            ArrayList<String> elementos = new ArrayList<String>();
            elementos.add(concepto.get(x));
            elementos.add(monto.get(x));
            elementos.add(date.get(x));
            tabla.agregarFilaTabla(elementos);
        }
    }
}
