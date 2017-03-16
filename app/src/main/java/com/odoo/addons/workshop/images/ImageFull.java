package com.odoo.addons.workshop.images;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.odoo.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by alan on 05/02/17.
 */

public class ImageFull  extends AppCompatActivity {
    private PhotoView imgV;
    private Bundle extras;
    private Context context;
    private String imgName;
    private final static String urlTaller = "http://www.taller777.com.py:8072/web/static/src/img/image_multi/";
    private android.support.v7.widget.ShareActionProvider mShareActionProvider;
    private MenuItem item;

    @Override
    protected void onCreate(Bundle savedInstanteState){
        super.onCreate(savedInstanteState);
        setContentView(R.layout.image_full);
        context = getApplicationContext();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        imgV = (PhotoView) findViewById(R.id.img_photoview);
        final ImageView imgError = (ImageView) findViewById(R.id.img_error);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        extras = getIntent().getExtras();
        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {

                // TODO: 04/03/17 Mejorar velocidad de carga
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
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                progressBar.setVisibility(View.GONE);
                imgError.setVisibility(View.VISIBLE);

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                progressBar.setVisibility(View.VISIBLE);
            }
        };
        imgV.setTag(target);
        if(extras != null) {
            imgName = extras.getString("img");
            Picasso.with(context).load(urlTaller + imgName).into(target);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.image_full_menu, menu);

        // Locate MenuItem with ShareActionProvider
        item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(item);
//        mShareActionProvider.setOnShareTargetSelectedListener(this);
        if( mShareActionProvider != null){
            shareIt();
        }
        // Return true to display menu
        return(super.onCreateOptionsMenu(menu));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_share:
                shareIt();
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent shareIt(){
        File imagePath = new File(context.getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(context, "com.odoo.fileprovider", newFile);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.setDataAndType(contentUri,getContentResolver().getType(contentUri));
        share.putExtra(Intent.EXTRA_STREAM, contentUri);
        mShareActionProvider.setShareIntent(share);
        return  share;
    }
}
