package com.odoo.addons.workshop.models;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by alan on 28/04/17.
 */

public class WorkshopNewAutopartType extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class).setSize(100).setRequired();
    OColumn autopart_receiving_lot_id = new OColumn("Lot", WorkshopAutopartReceivingLot.class, OColumn.RelationType.OneToMany).setRelatedColumn("new_autopart_type_id");

    public WorkshopNewAutopartType(Context context, OUser user) {
        super(context, "workshop.new_autopart.type", user);
    }
}
