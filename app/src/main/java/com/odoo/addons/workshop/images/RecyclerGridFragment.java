package com.odoo.addons.workshop.images;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

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
    UpdateFrag mCallback;

    public interface UpdateFrag{
        public void onUpFrag(int pos, String msg);
    }
    public RecyclerGridFragment(){
    }

    @SuppressLint("ValidFragment")
    public RecyclerGridFragment(int color, String imgBruto){
        this.color = color;
        this.imgBruto = imgBruto;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment, container, false);

        final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.frag_bg);
        frameLayout.setBackgroundColor(color);


        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_fragment);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2 );

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        arregloImagenes = prepareData(imgBruto);

        dataAdapter = new DataAdapter(getActivity().getApplicationContext(),arregloImagenes);

        recyclerView.setAdapter(dataAdapter);


        return view;


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



    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try{
            mCallback = (UpdateFrag) activity;

        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString()+ " must implement UpdateFrag");
        }

    }

    public void updateFrag(String msg){
        arregloImagenes.clear();
        arregloImagenes.addAll(prepareData(msg));
        dataAdapter.notifyDataSetChanged();
    }
}
