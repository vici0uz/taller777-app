package com.odoo.addons.workshop.providers;

import com.odoo.addons.workshop.models.WorkshopService;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by alan on 20/01/17.
 */

public class WServiceSyncProvider extends BaseModelProvider {
    public static final String TAG = WServiceSyncProvider.class.getSimpleName();

    @Override
    public String authority() {
        return WorkshopService.AUTHORITY;
    }
}
