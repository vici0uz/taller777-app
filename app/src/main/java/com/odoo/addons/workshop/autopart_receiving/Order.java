package com.odoo.addons.workshop.autopart_receiving;

/**
 * Created by alan on 05/10/17.
 */

public class Order {

    String name;
    String fecha;
    int id;

    public Order(String name, String fecha, int id) {
        this.name = name;
        this.fecha = fecha;
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public String getFecha() {
        return this.fecha;
    }

    public int getId() {
        return this.id;
    }
}
