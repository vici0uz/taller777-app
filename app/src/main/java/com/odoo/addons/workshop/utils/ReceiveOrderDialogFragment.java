package com.odoo.addons.workshop.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.R;
import com.odoo.addons.workshop.autopart_receiving.SimpleScannerFragmentActivity;
import com.odoo.addons.workshop.models.WorkshopAutopartReceivingLot;
import com.odoo.core.orm.ODataRow;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by alan on 29/05/17.
 */

public class ReceiveOrderDialogFragment extends DialogFragment {
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    private static final String TAG = "ALAN DEBUG: ";
    Activity activity;
    Activity actividad;
    private View view;
    private Context mContext;
    private WorkshopAutopartReceivingLot receivingLot;
    private ODataRow record = null;
    private TextView tvUbicacion;
    private String[] npValues;
    private int lot_id;
    private int locationId;
    private boolean wantToCloseDialog = false;

    public static ReceiveOrderDialogFragment newInstance(int num, ArrayList<String> elementos, Activity actividad) {
        ReceiveOrderDialogFragment f = new ReceiveOrderDialogFragment();

        f.actividad = actividad;
        Bundle args = new Bundle();
        String autopartName = elementos.get(1);
        Integer autopartQty = Integer.parseInt(elementos.get(2));
        String stockLocation = elementos.get(3);
        int id = Integer.parseInt(elementos.get(4));
        System.out.println("LOT ID " + id);
        args.putInt("num", num);
        args.putString("autopart_name", autopartName);
        args.putString("stock_location_adapter", stockLocation);
        args.putInt("autopartQty", autopartQty);
        args.putInt("lot_id", id);

        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mContext = getActivity().getApplicationContext();
        lot_id = getArguments().getInt("lot_id");

        view = (View) inflater.inflate(R.layout.fragment_dialog_from_rec_details_to_scan, null);
        TextView autopart_name = (TextView) view.findViewById(R.id.autopart_name);
        tvUbicacion = (TextView) view.findViewById(R.id.ub_autopart);
        tvUbicacion.setText(getArguments().getString("stock_location_adapter"));


        autopart_name.setText(getArguments().getString("autopart_name"));
        NumberPicker np = (NumberPicker) view.findViewById(R.id.np);
        int apQty = getArguments().getInt("autopartQty");

        if (apQty == 1) {
            npValues = new String[]{"1"};
            np.setValue(1);
            np.setMinValue(1);
            np.setMaxValue(1);
        } else {
            String[] nums = new String[apQty];
            for (int i = 0; i < nums.length; i++) {
                nums[i] = Integer.toString((i + 1));
            }
            np.setValue(1);
            np.setMinValue(1);
            np.setMaxValue(apQty);
            np.setDisplayedValues(nums);
        }
        np.setWrapSelectorWheel(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setTitle(R.string.receive_dialog_title)
                .setPositiveButton(R.string.save_record, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNeutralButton(R.string.scan_code_label, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //nada
                    }
                })
                .setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                });


        return builder.create();

    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Boolean wantToCloseDialog = false;
                    if (wantToCloseDialog) {
                        EventBus.getDefault().post(new MessageEvent(lot_id, locationId));
                        d.dismiss();
                    } else
                        Toast.makeText(v.getContext(), R.string.toast_scan_code_first, Toast.LENGTH_SHORT).show();
                }
            });
            Button neutralButton = d.getButton(Dialog.BUTTON_NEUTRAL);
            neutralButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean wantToCloseDialog = false;
                    if (wantToCloseDialog)
                        d.dismiss();
                    else {
                        Intent intent = new Intent(mContext, SimpleScannerFragmentActivity.class);
                        startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
                    }
                }
            });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String jsonString = data.getStringExtra("DATA");
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject newJson = jsonObject.getJSONObject("location");
            String name = newJson.getString("name");
            locationId = newJson.getInt("id");
            if (!name.isEmpty()) {
                tvUbicacion.setText(name);
            }
            System.out.println("Ejecuta hasta aca");
            wantToCloseDialog = true;

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "El codigo escaneado no es valido", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }


}
