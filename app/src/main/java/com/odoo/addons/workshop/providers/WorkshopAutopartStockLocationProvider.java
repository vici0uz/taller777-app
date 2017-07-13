package com.odoo.addons.workshop.providers;

import com.odoo.addons.workshop.models.WorkshopAutopartStockLocation;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by alan on 13/07/17.
 */

public class WorkshopAutopartStockLocationProvider extends BaseModelProvider {
    public static final String TAG = WorkshopAutopartStockLocationProvider.class.getSimpleName();

    @Override
    public String authority(){
        return WorkshopAutopartStockLocation.AUTHORITY;
    }
}
