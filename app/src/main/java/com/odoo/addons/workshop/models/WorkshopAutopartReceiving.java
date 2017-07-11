package com.odoo.addons.workshop.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.BuildConfig;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by alan on 28/04/17.
 */

public class WorkshopAutopartReceiving extends OModel {
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".addons.workshop.content.sync.autopart_receiving";

    OColumn name = new OColumn("Name", OVarchar.class).setSize(100).setRequired();
    OColumn autopart_receiving_lot_ids = new OColumn("Lot", WorkshopAutopartReceivingLot.class, OColumn.RelationType.OneToMany).setRelatedColumn("workshop_autopart_receiving_id");
    OColumn service_id = new OColumn("Service", WorkshopService.class, OColumn.RelationType.ManyToOne);
    OColumn vehicle_id = new OColumn("Vehicle", WorkshopVehicle.class, OColumn.RelationType.ManyToOne);

    public WorkshopAutopartReceiving(Context context, OUser user) {
        super(context, "workshop.autopart.receiving", user);
        setHasMailChatter(true);
    }

    public Uri uri(){
        return buildURI(AUTHORITY);
    }
}
