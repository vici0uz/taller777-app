package com.odoo.addons.caja_chica.models;

import android.content.Context;

import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODate;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by alan on 15/03/17.
 */

public class AccountBankStatementLine extends OModel{

    OColumn name = new OColumn("Name", OVarchar.class).setRequired();
    OColumn date = new OColumn("Date", ODate.class).setRequired();
    OColumn ref =  new OColumn("Reference", OVarchar.class);
    OColumn partner_id = new OColumn("Partner", ResPartner.class, OColumn.RelationType.ManyToOne);
    OColumn amount = new OColumn("Amount", OFloat.class);
    OColumn statement_id = new OColumn("Statement", AccountBankStatement.class, OColumn.RelationType.ManyToOne).setRequired();

    public AccountBankStatementLine(Context context, OUser user) {
        super(context, "account.bank.statement.line", user);
    }
}
