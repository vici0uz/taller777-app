package com.odoo.addons.workshop.utils;

/**
 * Created by alan on 19/06/17.
 */

public class MessageEvent {
    public final int lotId;
    public final int locationId;

    public MessageEvent(int lotId, int locationId) {
        this.lotId = lotId;
        this.locationId = locationId;
    }
}
