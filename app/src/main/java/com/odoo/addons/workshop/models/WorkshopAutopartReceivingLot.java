package com.odoo.addons.workshop.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.BuildConfig;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.support.OUser;

/**
 * Created by alan on 28/04/17.
 */

public class WorkshopAutopartReceivingLot extends OModel {
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".addons.workshop.content.sync.receiving_lot";

    OColumn new_autopart_type_id = new OColumn("Autopart", WorkshopNewAutopartType.class, OColumn.RelationType.ManyToOne);
    OColumn workshop_autopart_receiving_id = new OColumn("Autopart Receiving Order", WorkshopAutopartReceiving.class, OColumn.RelationType.ManyToOne);
    OColumn qty = new OColumn("Quantity", OInteger.class);
    OColumn stock_location_id = new OColumn("Location", WorkshopAutopartStockLocation.class, OColumn.RelationType.ManyToOne);

    public WorkshopAutopartReceivingLot(Context context, OUser user) {
        super(context, "workshop.autopart.receiving.lot", user);
    }

    public Uri uri(){
        return buildURI(AUTHORITY);
    }

}
