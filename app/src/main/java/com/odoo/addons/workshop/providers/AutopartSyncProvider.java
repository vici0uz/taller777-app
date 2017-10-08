package com.odoo.addons.workshop.providers;

import com.odoo.addons.workshop.models.WorkshopAutopartReceiving;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by alan on 12/05/17.
 */

public class AutopartSyncProvider extends BaseModelProvider {
    public static final String TAG = AutopartSyncProvider.class.getSimpleName();

    @Override
    public String authority() {
        return WorkshopAutopartReceiving.AUTHORITY;
    }
}
