package com.odoo.addons.workshop.images;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
//import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.odoo.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

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
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanteState){
        super.onCreate(savedInstanteState);
        setContentView(R.layout.image_full);
        context = getApplicationContext();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Image");
        imgV = (PhotoView) findViewById(R.id.img_photoview);

        extras = getIntent().getExtras();
        if(extras != null) {
            imgName = extras.getString("img");
            Picasso.with(context).load(urlTaller + imgName).into(new Target() {
                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {

                    try {

                        File cachePath = new File(context.getCacheDir(), "images");
                        cachePath.mkdirs(); // don't forget to make the directory
                        FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        stream.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imgV.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    // TODO: 27/02/17 crear error
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    // TODO: 27/02/17 crear placeholder barra de carga
                }
            });

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.image_full_menu, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);





        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        // Return true to display menu
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_share:
                shareIt();
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareIt(){
        File imagePath = new File(context.getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(context, "com.odoo.fileprovider", newFile);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.setDataAndType(contentUri,getContentResolver().getType(contentUri));
        share.putExtra(Intent.EXTRA_STREAM, contentUri);
        startActivity(Intent.createChooser(share, "Choose an app"));
    }
}
