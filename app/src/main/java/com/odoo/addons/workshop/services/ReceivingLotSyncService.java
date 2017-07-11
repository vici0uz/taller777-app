package com.odoo.addons.workshop.services;

import android.content.Context;
import android.os.Bundle;

import com.odoo.addons.workshop.models.WorkshopAutopartReceivingLot;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

/**
 * Created by alan on 24/06/17.
 */

public class ReceivingLotSyncService extends OSyncService{
    public static final String TAG = ReceivingLotSyncService.class.getSimpleName();

    @Override
    public OSyncAdapter getSyncAdapter(OSyncService lot, Context context){
        return new OSyncAdapter(context, WorkshopAutopartReceivingLot.class, lot, true);
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user){
        adapter.syncDataLimit(80);
    }
}
