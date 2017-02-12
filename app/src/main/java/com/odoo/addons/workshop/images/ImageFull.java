package com.odoo.addons.workshop.images;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.odoo.R;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by alan on 05/02/17.
 */

public class ImageFull extends AppCompatActivity {

    private PhotoView imgV;
    private Bundle extras;
    private Context context;
    private String imgName;
    private Toolbar toolbar;
    private final static String urlTaller = "http://www.taller777.com.py:8072/web/static/src/img/image_multi/";

    @Override
    protected void onCreate(Bundle savedInstanteState){
        super.onCreate(savedInstanteState);
        setContentView(R.layout.image_full);
        context = getApplicationContext();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Image");
        imgV = (PhotoView) findViewById(R.id.img_photoview);

        extras = getIntent().getExtras();
        if(extras != null) {
            imgName = extras.getString("img");
            Picasso.with(context).load(urlTaller + imgName).into(imgV);
        }
    }
}
