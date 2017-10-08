package com.odoo.addons.workshop.services;

import android.content.Context;
import android.os.Bundle;

import com.odoo.addons.workshop.models.WorkshopAutopartReceiving;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

/**
 * Created by alan on 12/05/17.
 */

public class AutopartSyncService extends OSyncService {
    public static final String TAG = AutopartSyncService.class.getSimpleName();

    @Override
    public OSyncAdapter getSyncAdapter(OSyncService service, Context context) {
        return new OSyncAdapter(context, WorkshopAutopartReceiving.class, service, true);
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {
        adapter.syncDataLimit(80);
    }
}
