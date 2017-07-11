package com.odoo.addons.workshop.providers;

import com.odoo.addons.workshop.models.WorkshopAutopartReceiving;
import com.odoo.addons.workshop.models.WorkshopAutopartReceivingLot;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by alan on 24/06/17.
 */

public class ReceivingLotSyncProvider extends BaseModelProvider {
    public static final String TAG = ReceivingLotSyncProvider.class.getSimpleName();

    @Override
    public String authority(){
        return WorkshopAutopartReceivingLot.AUTHORITY;
    }

}
