package com.odoo.addons.workshop.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.BuildConfig;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OText;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OUser;

/**
 * Created by alan on 20/01/17.
 */

public class WorkshopService extends OModel{
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID +
            ".addons.workshop.content.sync.workshop_service";


    OColumn name = new OColumn("Name", OVarchar.class).setSize(100).setRequired();
    OColumn state = new OColumn("State", OSelection.class)
            .addSelection("draft", "Draft")
            .addSelection("done", "Done")
            .addSelection("approved", "Approved")
            .addSelection("cancelled", "Cancelled")
            .addSelection("pending", "Pending");
    OColumn t_insurance = new OColumn("Insured", OBoolean.class);
    OColumn partner_id = new OColumn("Partner", ResPartner.class, OColumn.RelationType.ManyToOne);
    OColumn insurer_id = new OColumn("Insurer", ResPartner.class, OColumn.RelationType.ManyToOne)
            .addDomain("insurer", "=", true);
    OColumn vehicle_id = new OColumn("Vehicle", WorkshopVehicle.class, OColumn.RelationType.ManyToOne);
    OColumn multi_images = new OColumn("Budget Images", OText.class);
    OColumn multi_images_received = new OColumn("Received Images", OText.class);
    OColumn multi_images_delivered = new OColumn("Delivered Images", OText.class);
    OColumn job_no = new OColumn("Job Number", OVarchar.class).setSize(10);
    OColumn n_incident = new OColumn("Incident Number", OVarchar.class).setSize(64);

    @Odoo.Functional(method="storeHaveImages", depends={"multi_images", "multi_images_received", "multi_images_delivered"}, store = true)
    OColumn have_images = new OColumn("Have pictures?", OVarchar.class);
    OColumn autopart_ids = new OColumn("Autopart Receiving Order", WorkshopAutopartReceiving.class, OColumn.RelationType.OneToMany).setRelatedColumn("service_id");

    public WorkshopService(Context context,  OUser user) {
        super(context, "workshop.service", user);
        setHasMailChatter(true);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }

    public String storeHaveImages(OValues value){
        if(!value.getString("multi_images").equals("false") || !value.getString("multi_images_received").equals("false") || !value.getString("multi_images_delivered").equals("false"))
            return "true";
        else
            return "false";
    }

    @Override
    public ODomain defaultDomain(){
        ODomain domain = new ODomain();
        domain.add("|");
        domain.add("state", "!=","done");
        domain.add("state", "!=", "cancelled");
        return domain;
    }



    @Override
    public boolean allowCreateRecordOnServer(){
        return false;
    }

    @Override
    public boolean allowDeleteRecordOnServer(){
        return false;
    }
}
