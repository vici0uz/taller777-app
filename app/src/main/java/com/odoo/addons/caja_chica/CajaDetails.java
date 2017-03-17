package com.odoo.addons.caja_chica;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;

import com.odoo.R;
import com.odoo.addons.caja_chica.models.AccountBankStatement;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.support.OdooCompatActivity;

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

        extras = getIntent().getExtras();

        accountBankStatement = new AccountBankStatement(this, null);
        setupToolbar();

    }

    private void setupToolbar(){
        int rowId = extras.getInt(OColumn.ROW_ID);
        record = accountBankStatement.browse(rowId);
        mForm = (OForm) findViewById(R.id.cajaForm);
        mForm.initForm(record);
    }
}
