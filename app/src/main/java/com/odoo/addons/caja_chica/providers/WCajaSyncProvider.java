package com.odoo.addons.caja_chica.providers;

import com.odoo.addons.caja_chica.models.AccountBankStatement;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by alan on 14/03/17.
 */

public class WCajaSyncProvider extends BaseModelProvider {
    public static final String TAG = WCajaSyncProvider.class.getSimpleName();

    @Override
    public String authority(){
        return AccountBankStatement.AUTHORITY;
    }
}
