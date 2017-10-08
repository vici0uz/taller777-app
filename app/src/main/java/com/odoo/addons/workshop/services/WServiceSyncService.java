package com.odoo.addons.workshop.services;

import android.content.Context;
import android.os.Bundle;

import com.odoo.addons.workshop.models.WorkshopService;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

/**
 * Created by alan on 20/01/17.
 */

public class WServiceSyncService extends OSyncService {
    public static final String TAG = WServiceSyncService.class.getSimpleName();

    @Override
    public OSyncAdapter getSyncAdapter(OSyncService service, Context context) {
        return new OSyncAdapter(context, WorkshopService.class, service, true);
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {
        adapter.syncDataLimit(80);
    }
}
