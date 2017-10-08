package com.odoo.addons.workshop.autopart_receiving;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.odoo.R;
import com.odoo.addons.workshop.ReceivingOrdersDetails;
import com.odoo.core.utils.IntentUtils;

import java.util.ArrayList;

/**
 * Created by alan on 07/10/17.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private ArrayList<Order> mDataSet;
    private Context mContext;
    private View mView;

    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_order,viewGroup,false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(OrderAdapter.ViewHolder holder, final int position) {
        holder.txt.setText(String.valueOf(mDataSet.get(position).getName()));
        holder.txtFecha.setText(String.valueOf(mDataSet.get(position).getFecha()));
        holder.txtFecha.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   Intent intent = new Intent(mContext, ReceivingOrdersDetails.class);
                                                   intent.putExtra("_id", mDataSet.get(position).getId());
                                                   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                   v.getContext().startActivity(intent);

                                               }
                                           });
        holder.txt.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(mContext, ReceivingOrdersDetails.class);
                                                intent.putExtra("_id", mDataSet.get(position).getId());
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                v.getContext().startActivity(intent);

                                            }
                                        });

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public OrderAdapter(Context passedContext, ArrayList<Order> myDataSet){
        mDataSet = myDataSet;
        mContext = passedContext;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt;
        TextView txtFecha;

        ViewHolder(View itemView){
            super(itemView);
            txt = (TextView)itemView.findViewById(R.id.texto);
            txtFecha = (TextView)itemView.findViewById(R.id.texto2);
        }
    }
}
