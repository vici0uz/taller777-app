package com.odoo.addons.caja_chica.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.BuildConfig;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODate;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by alan on 14/03/17.
 */

public class AccountBankStatement extends OModel{
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".addons.caja_chica.content.sync.caja_chica";

        OColumn name = new OColumn("Name", OVarchar.class).setSize(100).setRequired();
        OColumn state = new OColumn("State", OSelection.class)
                .addSelection("draft", "Draft")
                .addSelection("open", "Open")
                .addSelection("confirm", "Closed");
        OColumn date = new OColumn("Date", ODate.class);
        OColumn line_ids = new OColumn("Records", AccountBankStatementLine.class, OColumn.RelationType.OneToMany).setRelatedColumn("statement_id");

    public AccountBankStatement(Context context, OUser user) {
        super(context, "account.bank.statement", user);
    }

    public Uri uri(){
        return buildURI(AUTHORITY);
    }
}
