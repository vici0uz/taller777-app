package com.odoo.addons.workshop.images;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.odoo.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

/**
 * Created by alan on 31/01/17.
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private ArrayList<String> arregloImgs;
    private final String urlTallerMin = "http://www.taller777.com.py:8072/web/static/src/img/image_multi/thumbs/";


    private Context context;
    private DataAdapter.ViewHolder viewHolder;

    public DataAdapter(Context context, ArrayList<String> arregloImgs) {
        this.context = context;
        this.arregloImgs = arregloImgs;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_item, viewGroup, false);
         viewHolder = new DataAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int w= displayMetrics.widthPixels;
        int h = displayMetrics.heightPixels;

        System.out.println("ALAN DEBUG: w "+ w+ " h "+h);
        int nw = w -20 / 2;
        int nh = (((h-20)/4)*3) / 2;

        // TODO: Mejorar Dimensiones Grid Piccaso
//        Picasso.with(context).load(urlTallerMin + arregloImgs.get(position)).resize(nw,nh).centerInside().placeholder(R.drawable.photo).into(viewHolder.img);
        Picasso.with(context).load(urlTallerMin + arregloImgs.get(position)).resize(nw,nh).centerInside().placeholder(R.drawable.photo).into(viewHolder.img);

        viewHolder.img.setOnClickListener(new View.OnClickListener(){

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



    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);

            img = (ImageView)itemView.findViewById(R.id.img_view);
        }
    }


}
