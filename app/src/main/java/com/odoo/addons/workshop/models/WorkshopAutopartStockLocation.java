package com.odoo.addons.workshop.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.BuildConfig;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by alan on 14/06/17.
 */

public class WorkshopAutopartStockLocation extends OModel {
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".addons.workshop.content.sync.w_stock_location";

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn complete_name = new OColumn("Route", OVarchar.class);
    OColumn parent_id = new OColumn("Parent Ubication", WorkshopAutopartStockLocation.class, OColumn.RelationType.ManyToOne);

    public WorkshopAutopartStockLocation(Context context, OUser user) {
        super(context, "workshop.autopart.stock.location", user);
    }

    public Uri uri() {
        return buildURI(AUTHORITY);
    }
}
