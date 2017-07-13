package com.odoo.addons.workshop.services;

import android.content.Context;
import android.os.Bundle;

import com.odoo.addons.workshop.models.WorkshopAutopartStockLocation;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

/**
 * Created by alan on 13/07/17.
 */

public class WorkshopAutopartStockLocationService extends OSyncService {
    public static final String TAG = WorkshopAutopartStockLocationService.class.getSimpleName();

    @Override
    public OSyncAdapter getSyncAdapter(OSyncService location, Context context){
        return new OSyncAdapter(context, WorkshopAutopartStockLocation.class, location, true);
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user){
        adapter.syncDataLimit(80);
    }
}
