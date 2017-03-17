package com.odoo.addons.caja_chica.services;

import android.content.Context;
import android.os.Bundle;

import com.odoo.addons.caja_chica.models.AccountBankStatement;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

/**
 * Created by alan on 14/03/17.
 */

public class CajaSyncService extends OSyncService {
    @Override
    public OSyncAdapter getSyncAdapter(OSyncService service, Context context) {
        return new OSyncAdapter(context, AccountBankStatement.class, service, true);
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {

    }
}
