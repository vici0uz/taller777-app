package com.odoo.addons.workshop.images;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.odoo.R;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

/**
 * Created by alan on 29/01/17.
 */

public class RecyclerGridFragment extends Fragment {
    int color;
    private String imgBruto;
    private ArrayList<String> arregloImagenes;
    public DataAdapter dataAdapter;
    private TextView emptyView;
    private RecyclerView recyclerView;
    private FrameLayout frameLayout;
    private View fView;

    public RecyclerGridFragment(){
    }

    @SuppressLint("ValidFragment")
    public RecyclerGridFragment(int color, String imgBruto){
        this.color = color;
        this.imgBruto = imgBruto;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        fView = inflater.inflate(R.layout.fragment, container, false);

        frameLayout = (FrameLayout) fView.findViewById(R.id.frag_bg);
        frameLayout.setBackgroundColor(color);


        recyclerView = (RecyclerView) fView.findViewById(R.id.recycler_fragment);
        emptyView = (TextView) fView.findViewById(R.id.empty_view);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2 );

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        arregloImagenes = prepareData(imgBruto);

        dataAdapter = new DataAdapter(getActivity().getApplicationContext(),arregloImagenes);

        checkEmpty();
        recyclerView.setAdapter(dataAdapter);

        return fView;
    }

    private ArrayList<String> prepareData(String imgs){
        ArrayList<String> temporal = new ArrayList<String>();
        String images = imgs;
        JSONParser parser = new JSONParser();
        arregloImagenes = new  ArrayList<String>();
        try {
            Object object = parser.parse(images);
            JSONArray array = (JSONArray)object;
            for (Object img:array){
                try {
                    JSONObject object1 = new JSONObject(img.toString());
                    String imgName = object1.get("name").toString();
                    String fileName = FilenameUtils.getName(imgName);
                    temporal.add(fileName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return temporal;
    }


    private void checkEmpty(){
        if(dataAdapter.getItemCount()<=0){
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }


    public void updateFrag(String msg){
        arregloImagenes.clear();
        arregloImagenes.addAll(prepareData(msg));
        dataAdapter.notifyDataSetChanged();
        checkEmpty();
    }
}
