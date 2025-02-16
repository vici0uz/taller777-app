package com.odoo.addons.workshop.images;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.odoo.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by alan on 31/01/17.
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private final String urlTallerMin = "http://www.taller777.com.py:8072/web/static/src/img/image_multi/thumbs/";
    private ArrayList<String> arregloImgs;
//    private final String urlTallerMin = "http://192.168.1.5:8072/web/static/src/img/image_multi/thumbs/";
    private Context context;

    DataAdapter(Context context, ArrayList<String> arregloImgs) {
        this.context = context;
        this.arregloImgs = arregloImgs;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Picasso.with(context).load(urlTallerMin + arregloImgs.get(position)).placeholder(R.drawable.photo).into(viewHolder.img);
        viewHolder.img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ImageFull.class);
                intent.putExtra("img", arregloImgs.get(position));
                view.getContext().startActivity(intent);
            }
        });
        viewHolder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return arregloImgs.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img_view);
        }
    }


}
