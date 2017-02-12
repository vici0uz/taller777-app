package com.odoo.addons.workshop.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.BuildConfig;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by alan on 26/01/17.
 */

public class WorkshopVehicle extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class).setSize(100);
    OColumn license_plate = new OColumn("License Plate", OVarchar.class).setSize(10);
    OColumn vin_sn = new OColumn("Chassis Number", OVarchar.class).setSize(24);
    OColumn partner_id = new OColumn("Owner", ResPartner.class, OColumn.RelationType.ManyToOne);

    public WorkshopVehicle(Context context, OUser user) {
        super(context, "workshop.vehicle", user);
    }
}
