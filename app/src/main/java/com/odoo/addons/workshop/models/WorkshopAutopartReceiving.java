package com.odoo.addons.workshop.models;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.odoo.BuildConfig;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alan on 28/04/17.
 */

public class WorkshopAutopartReceiving extends OModel {
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".addons.workshop.content.sync.autopart_receiving";
    private ResPartner partner = new ResPartner(getContext(), null);
    private WorkshopService workshopService = new WorkshopService(getContext(), null);

    OColumn name = new OColumn("Name", OVarchar.class).setSize(100).setRequired();
    OColumn autopart_receiving_lot_ids = new OColumn("Lot", WorkshopAutopartReceivingLot.class, OColumn.RelationType.OneToMany).setRelatedColumn("workshop_autopart_receiving_id");
    OColumn service_id = new OColumn("Service", WorkshopService.class, OColumn.RelationType.ManyToOne);
    OColumn vehicle_id = new OColumn("Vehicle", WorkshopVehicle.class, OColumn.RelationType.ManyToOne);
    OColumn partner_id = new OColumn("Partner", ResPartner.class, OColumn.RelationType.ManyToOne);

    @Odoo.Functional(method = "getPartnerName", depends = "service_id", store = true)
    OColumn partner_name = new OColumn("Partner Name", OVarchar.class).setLocalColumn();

    //    @Odoo.Functional(method="setProcessed", depends = {"autopart_receiving_lot_ids"}, store=true)
    OColumn processed = new OColumn("Processed", OBoolean.class).setDefaultValue(false);

    public WorkshopAutopartReceiving(Context context, OUser user) {
        super(context, "workshop.autopart.receiving", user);
        setHasMailChatter(true);
    }

    //    Funcion que tiene que retornar "true" o "false"
    public String setProcessed(OValues values) {
        System.out.println(values);
        return "true";
    }

    public String getPartnerName(OValues values){
        String data = "";
        String serviceData =  values.getString("service_id");
        System.out.println("ALAN DEBUG: "+ serviceData);
        Pattern patternServiceId = Pattern.compile("(\\d+)");
        Matcher matcherServiceId = patternServiceId.matcher(serviceData);
        if (matcherServiceId.find()){
            data = matcherServiceId.group(1);
        }
        System.out.println("ALAN DEBUG: "+ data);
        int serverId = Integer.parseInt(data);
        System.out.println("ALAN DEBUG: rowId " + serverId);
        int rowId = workshopService.selectRowId(serverId);

        ODataRow record = workshopService.browse(rowId);
        System.out.println(record.get("job_no"));



//        return partner.browse(pid).getString("name");

        return "Hola puta";
    }
    public Uri uri() {
        return buildURI(AUTHORITY);
    }

}
