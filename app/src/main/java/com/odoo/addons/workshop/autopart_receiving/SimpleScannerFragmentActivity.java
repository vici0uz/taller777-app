package com.odoo.addons.workshop.autopart_receiving;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.odoo.R;
import com.odoo.addons.workshop.autopart_receiving.SimpleScannerFragment;

public class SimpleScannerFragmentActivity extends AppCompatActivity implements SimpleScannerFragment.OnCodeScanned {
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    @Override
    public void onCreate(Bundle state){
        super.onCreate(state);
        setContentView(R.layout.activity_simple_scanner_fragment);
    }

    @Override
    public void onNewCodeScanned(String ubicacion) {
        Intent intent = new Intent();
        intent.putExtra("DATA", ubicacion);
        setResult(BARCODE_READER_REQUEST_CODE, intent);
        finish();
    }
}