package com.odoo.addons.workshop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.odoo.R;
import com.odoo.addons.workshop.autopart_receiving.OrderAdapter;

import java.util.ArrayList;

/**
 * Created by alan on 05/10/17.
 */

public class ServiceOrdersDialogFragment extends DialogFragment  {
    private Context mContext;
    private View mView;
    private Activity actividad;
    private ArrayList ordenes;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private OrderAdapter orderAdapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        super.onCreateDialog(savedInstanceState);

        mContext = getActivity().getApplicationContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mView = LayoutInflater.from(mContext).inflate(R.layout.fragment_rec_order, null);
        builder.setView(mView);
        builder.setTitle(R.string.menu_to_receive);

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.receiving_orders_recycler_view);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        orderAdapter = new OrderAdapter(mContext, ordenes);

        mRecyclerView.setAdapter(orderAdapter);
        return builder.create();
    }

    public static ServiceOrdersDialogFragment newInstance(Activity activity, ArrayList ordenes){

        ServiceOrdersDialogFragment sOrdersDialogFragment = new ServiceOrdersDialogFragment();

        sOrdersDialogFragment.actividad = activity;
        sOrdersDialogFragment.ordenes = ordenes;

        return sOrdersDialogFragment;
    }
}
