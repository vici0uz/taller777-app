package com.odoo.addons.caja_chica.models;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by alan on 23/03/17.
 */

public class AccountJournal extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn type = new OColumn("Type", OSelection.class)
            .addSelection("cash", "Cash")
            .addSelection("bank", "Bank")
            .addSelection("sale", "Sale")
            .addSelection("purchase", "Purchase")
            .addSelection("general", "General")
            .addSelection("sale_refund", "Sale Refund")
            .addSelection("purchase_refund", "Purchase Refund")
            .addSelection("situation", "Situation");

    public AccountJournal(Context context, OUser user) {
        super(context, "account.journal", user);
    }
}
