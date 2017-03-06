package com.odoo.addons.workshop;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.odoo.App;
import com.odoo.R;
import com.odoo.addons.customers.CustomerDetails;
import com.odoo.addons.workshop.images.ImagesActivity;
import com.odoo.addons.workshop.models.WorkshopService;
import com.odoo.base.addons.ir.feature.OFileManager;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.support.OUser;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.IntentUtils;
import com.odoo.core.utils.OCursorUtils;
import com.odoo.core.utils.OStringColorUtil;

import odoo.controls.OForm;

/**
 * Created by alan on 23/01/17.
 */


public class ServiceDetails extends OdooCompatActivity implements View.OnClickListener {
    public static final String TAG = ServiceDetails.class.getSimpleName();
    private final String KEY_MODE = "key_edit_mode";
    private Bundle extras;
    private ODataRow record = null;
    private OFileManager fileManager;
    private OForm mForm;
    private App app;
    private WorkshopService workshopService;
    private Boolean mEditMode = false;
    private Menu mMenu;
    private Toolbar toolbar;
    private String[] imgs = new String[3];
    private OUser mUser;
    private int rowId;
    private CustomerDetails customerDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_detail);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fileManager = new OFileManager(this);
        if (toolbar != null)
            toolbar.setTitle("");
        if (savedInstanceState != null) {
            mEditMode = savedInstanceState.getBoolean(KEY_MODE);
        }
        app = (App) getApplicationContext();
        workshopService = new WorkshopService(this, null);
        extras = getIntent().getExtras();
        if (!hasRecordInExtra())
            mEditMode = true;
        mUser = OUser.current(this);

        setupToolbar();
        prepareData();
    }

    private boolean hasRecordInExtra(){
        return extras != null && extras.containsKey(OColumn.ROW_ID);
    }

    private void setMode(Boolean edit){
        if(mMenu != null){
            mMenu.findItem(R.id.menu_service_detail_more).setVisible(!edit);
            mMenu.findItem(R.id.menu_service_edit).setVisible(!edit);
            mMenu.findItem(R.id.menu_service_save).setVisible(edit);
            mMenu.findItem(R.id.menu_service_cancel).setVisible(edit);
        }
        int color = Color.DKGRAY;
        if (record != null) {
            color = OStringColorUtil.getStringColor(this, record.getString("name"));
        }
        if (edit) {
            if(!hasRecordInExtra()){
                toolbar.setTitle("New");
            }
            // Futura edicion
            mForm = (OForm) findViewById(R.id.serviceFormEdit);
            findViewById(R.id.service_view_layout).setVisibility(View.GONE);
            findViewById(R.id.service_edit_layout).setVisibility(View.VISIBLE);
        } else {
            mForm = (OForm) findViewById(R.id.serviceForm);
            findViewById(R.id.service_edit_layout).setVisibility(View.GONE);
            findViewById(R.id.service_view_layout).setVisibility(View.VISIBLE);
        }
        setColor(color);
    }

    private void setColor(int color){
        mForm.setIconTintColor(color);
    }


    private void setupToolbar(){
        if (!hasRecordInExtra()) {
            setMode(mEditMode);
            mForm.setEditable(mEditMode);
            mForm.initForm(null);
        } else {
            rowId = extras.getInt(OColumn.ROW_ID);
            record = workshopService.browse(rowId);
            checkControls();
            setMode(mEditMode);
            mForm.setEditable(mEditMode);
            mForm.initForm(record);
            getSupportActionBar().setTitle(record.getString("name"));
            setView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_service_detail, menu);
        mMenu = menu;
        setMode(mEditMode);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.service_images:
                Intent intent = new Intent(this, ImagesActivity.class);
                intent.putExtra("images", imgs);
                intent.putExtra("id",rowId);
                intent.putExtra("username", mUser.getName());
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_MODE, mEditMode);
    }

    private void prepareData(){
        imgs[0] = (!record.getString("multi_images").equals("false"))? record.getString("multi_images"): "";
        imgs[1] = (!record.getString("multi_images_received").equals("false")) ? record.getString("multi_images_received") : "";
        imgs[2] = (!record.getString("multi_images_delivered").equals("false")) ? record.getString("multi_images_delivered") : "";
    }
    private void setView(){
        if (!record.getBoolean("t_insurance")){
            findViewById(R.id.t_insurance).setVisibility(View.GONE);
            findViewById(R.id.insurer_id).setVisibility(View.GONE);
            findViewById(R.id.n_incident).setVisibility(View.GONE);
        }
    }

    private void checkControls(){
        findViewById(R.id.partner_id).setOnClickListener(this);
        findViewById(R.id.insurer_id).setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        // TODO: 05/03/17 Crear VehicleDetails y apuntar a el vehiculo
        switch (view.getId()){
            case R.id.partner_id:
                loadActivity("partner_id");
                break;
            case R.id.insurer_id:
                loadActivity("insurer_id");
                break;
        }
    }

    private void loadActivity(String field){
            Bundle bundle = new Bundle();
            bundle.putInt("_id", record.getInt(field));
            bundle.putString("partner_type", "Customer");
            IntentUtils.startActivity(this, CustomerDetails.class, bundle);
    }
}
