package com.odoo.addons.workshop.images;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.odoo.R;
import com.odoo.addons.workshop.models.WorkshopService;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OValues;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import nl.changer.polypicker.Config;
import nl.changer.polypicker.ImagePickerActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



/**
 * Created by alan on 26/01/17.
 */

public class ImagesActivity extends AppCompatActivity  {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private final String urlTaller = "http://www.taller777.com.py:8072";
    private ArrayList<String> arregloImagenes;
    private FloatingActionButton fab;
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final String TAG = ImagesActivity.class.getSimpleName();
    private String url = "http://www.taller777.com.py:7979/uploader";
    private MediaType type = MediaType.parse("image/jpeg");
    private static final String ALAN = "ALAN DEBUG : ";

    private String[] imgs;
    private Bundle extras;
    private WorkshopService workshopService;
    private ODataRow record;
    private OValues values;
    private int rowId;
    private String field;
    private Pager mAdapter;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.images_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        viewPager = (ViewPager) findViewById(R.id.pager);

        extras = getIntent().getExtras();

        imgs = extras.getStringArray("images");
        workshopService = new WorkshopService(this, null);
        rowId = extras.getInt("id");
        userName = extras.getString("username");
        record = workshopService.browse(rowId);
        getSupportActionBar().setTitle(record.getString("name"));

        setupViewPager(viewPager);


        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImages();
            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        mAdapter = new Pager(getSupportFragmentManager());
        mAdapter.addFrag(new RecyclerGridFragment(getResources().getColor(android.support.design.R.color.button_material_dark), imgs[0]), "Presupuesto");
        mAdapter.addFrag(new RecyclerGridFragment(getResources().getColor(android.support.design.R.color.button_material_dark), imgs[1]), "Recepción");
        mAdapter.addFrag(new RecyclerGridFragment(getResources().getColor(android.support.design.R.color.button_material_dark), imgs[2]), "Entrega");
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(1);
    }

    private void getImages() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        Config config = new Config.Builder()
                .setTabBackgroundColor(R.color.white)
                .setTabSelectionIndicatorColor(R.color.android_orange_dark)
                .build();
        ImagePickerActivity.setConfig(config);
        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_IMAGES) {
                Parcelable[] parcelableUris = intent.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                if (parcelableUris == null) {
                    return;
                }

                // Java doesn't allow array casting, this is a little hack
                Uri[] uris = new Uri[parcelableUris.length];
                System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);
                if (uris != null) {
                    OkHttpClient client = new OkHttpClient();
                    MultipartBody.Builder builderNew = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM);
                    if(imgs[tabLayout.getSelectedTabPosition()].length()>1){
                        builderNew.addFormDataPart("images", imgs[tabLayout.getSelectedTabPosition()]);
                    }
                    // TODO: 25/02/17 Corregir Tema hora guardado Imagen Servidor 
                    builderNew.addFormDataPart("user",userName);
                    for (Uri uri : uris) {
                        File f = new File(uri.getPath());
                        if (f.exists()) {
                            builderNew.addFormDataPart("file", uri.getLastPathSegment(), RequestBody.create(type, f));
                        }
                    }
                    MultipartBody multipartBody = builderNew.build();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(multipartBody)
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                            }

                        // TODO: 25/02/17 Mostrar cantidad imagenes subidas 
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()){
                                final int tabPos = tabLayout.getSelectedTabPosition();
                                final String msg = response.body().string();
                                switch (tabPos){
                                    case 0:
                                        field = "multi_images";
                                        break;
                                    case 1:
                                        field = "multi_images_received";
                                        break;
                                    case 2:
                                        field = "multi_images_delivered";
                                        break;
                                }
                                values = new OValues();
                                values.put(field, msg);
                                workshopService.update(rowId, values);
                                ImagesActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ImagesActivity.this, "Las imagenes se guardaron correctamente.", Toast.LENGTH_SHORT).show();
                                        RecyclerGridFragment frag = (RecyclerGridFragment) mAdapter.getItem(tabPos);
                                        frag.updateFrag(msg);

                                    }
                                });
                            }
                            if (!response.isSuccessful()) {
                                // en caso de error
                                Toast.makeText(ImagesActivity.this, "Algo ha salido horriblemente mal :(", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }
    }
}

