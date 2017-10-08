package com.odoo.addons.caja_chica;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.odoo.R;
import com.odoo.addons.workshop.utils.ReceiveOrderDialogFragment;

import java.util.ArrayList;


/**
 * Created by francis on 14/10/14.
 * Esta clase representa una tabla
 */
public class Tabla
{
    // Variables de la clase

    private TableLayout tabla;          // Layout donde se pintará la tabla
    private ArrayList<TableRow> filas;  // Array de las filas de la tabla
    private Activity actividad;
    private Resources rs;
    private int FILAS, COLUMNAS;        // Filas y columnas de nuestra tabla
    private Context context;

    /**
     * Constructor de la tabla
     * @param actividad Actividad donde va a estar la tabla
     * @param tabla TableLayout donde se pintará la tabla
     */
    public Tabla(Activity actividad, TableLayout tabla, Context context)
    {
        this.actividad = actividad;
        this.tabla = tabla;
        rs = this.actividad.getResources();
        FILAS = COLUMNAS = 0;
        filas = new ArrayList<TableRow>();
        this.context = context;

    }

    /**
     * Añade la cabecera a la tabla
     * @param recursocabecera Recurso (array) donde se encuentra la cabecera de la tabla
     */
    public void agregarCabecera(int recursocabecera)
    {
        TableRow.LayoutParams layoutCelda;
        TableRow fila = new TableRow(actividad);

        TableRow.LayoutParams layoutFila = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        fila.setLayoutParams(layoutFila);
        tabla.setStretchAllColumns(true);
        String[] arraycabecera = rs.getStringArray(recursocabecera);
        COLUMNAS = arraycabecera.length;

        for(int i = 0; i < arraycabecera.length; i++)
        {
            TextView texto = new TextView(actividad);
            layoutCelda = new TableRow.LayoutParams(obtenerAnchoPixelesTexto(arraycabecera[i]), TableRow.LayoutParams.WRAP_CONTENT);
            texto.setText(arraycabecera[i]);
            texto.setGravity(Gravity.CENTER_HORIZONTAL);
            texto.setTextAppearance(actividad, R.style.estilo_celda);
            texto.setBackgroundResource(R.drawable.tabla_celda_cabecera);
            texto.setLayoutParams(layoutCelda);

            fila.addView(texto);
        }

        tabla.addView(fila);
        filas.add(fila);

        FILAS++;
    }

    /**
     * Agrega una fila a la tabla
     * @param elementos Elementos de la fila
     *                  Clase original
     */
    public void agregarFilaTabla(ArrayList<String> elementos){
        TableRow.LayoutParams layoutCelda;
        TableRow.LayoutParams layoutFila = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        TableRow fila = new TableRow(actividad);
        fila.setLayoutParams(layoutFila);

        for(int i = 0; i< elementos.size(); i++){
            TextView texto = new TextView(actividad);
            texto.setText(String.valueOf(elementos.get(i)));
            texto.setGravity(Gravity.CENTER_HORIZONTAL);
            texto.setTextAppearance(actividad, R.style.estilo_celda);
            texto.setBackgroundResource(R.drawable.tabla_celda);
            layoutCelda = new TableRow.LayoutParams(obtenerAnchoPixelesTexto(texto.getText().toString()), TableRow.LayoutParams.WRAP_CONTENT);
            texto.setLayoutParams(layoutCelda);

            fila.addView(texto);
        }

        tabla.addView(fila);
        filas.add(fila);

        tabla.setStretchAllColumns(true);

        FILAS++;
    }

/**
 * Nueva clase usada por recepcion de repuestos
* */
    public void agregarFilaTabla(final ArrayList<String> elementos, final int position){
        TableRow.LayoutParams layoutCelda;
        TableRow.LayoutParams layoutFila = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        TableRow fila = new TableRow(actividad);
        if (position % 2 == 0){
            fila.setBackgroundResource(R.drawable.tabla_celda_impar);
        } else {
            fila.setBackgroundResource(R.drawable.tabla_celda_par);
        }
        fila.setFocusable(true);
        fila.setFocusableInTouchMode(true);
        fila.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View v) {
                FragmentTransaction ft = actividad.getFragmentManager().beginTransaction();
                android.app.Fragment prev = actividad.getFragmentManager().findFragmentByTag("dialog");
                if(prev != null){
                    ft.remove(prev);
                }
                ReceiveOrderDialogFragment newFragment = ReceiveOrderDialogFragment.newInstance(1,elementos, actividad);
                newFragment.show(actividad.getFragmentManager(), "dialog");
                return false;
            }
        });
        fila.setLayoutParams(layoutFila);
        for(int i = 0; i< 3; i++){
            if( i == 0){
                ImageView statusIcon = new ImageView(actividad.getApplicationContext());
                String status = String.valueOf(elementos.get(0));
                if (status.equals("true")) {
                    statusIcon.setImageResource(R.drawable.ic_check_black_24dp);
                }else {
                    statusIcon.setImageResource(R.drawable.ic_close_black_24dp);
                }
                statusIcon.setLayoutParams(new TableRow.LayoutParams(12, TableRow.LayoutParams.WRAP_CONTENT));
                fila.addView(statusIcon);
            }else {
                TextView texto = new TextView(actividad);
                texto.setText(String.valueOf(elementos.get(i)));
                texto.setGravity(Gravity.CENTER_HORIZONTAL);
                texto.setTextAppearance(actividad, R.style.estilo_celda);
                layoutCelda = new TableRow.LayoutParams(obtenerAnchoPixelesTexto(texto.getText().toString()), TableRow.LayoutParams.WRAP_CONTENT);
                texto.setLayoutParams(layoutCelda);
                fila.addView(texto);
            }
        }

        tabla.addView(fila);
        filas.add(fila);

        tabla.setStretchAllColumns(true);

        FILAS++;
    }


    /**
     * Elimina una fila de la tabla
     * @param indicefilaeliminar Indice de la fila a eliminar
     */
    public void eliminarFila(int indicefilaeliminar)
    {
        if( indicefilaeliminar > 0 && indicefilaeliminar < FILAS )
        {
            tabla.removeViewAt(indicefilaeliminar);
            FILAS--;
        }
    }

    /**
     * Devuelve las filas de la tabla, la cabecera se cuenta como fila
     * @return Filas totales de la tabla
     */
    public int getFilas()
    {
        return FILAS;
    }

    /**
     * Devuelve las columnas de la tabla
     * @return Columnas totales de la tabla
     */
    public int getColumnas()
    {
        return COLUMNAS;
    }

    /**
     * Devuelve el número de celdas de la tabla, la cabecera se cuenta como fila
     * @return Número de celdas totales de la tabla
     */
    public int getCeldasTotales()
    {
        return FILAS * COLUMNAS;
    }

    /**
     * Obtiene el ancho en píxeles de un texto en un String
     * @param texto Texto
     * @return Ancho en píxeles del texto
     */
    private int obtenerAnchoPixelesTexto(String texto)
    {
        Paint p = new Paint();
        Rect bounds = new Rect();
        p.setTextSize(55);
//        p.setTextSize(65);

        p.getTextBounds(texto, 0, texto.length(), bounds);
        return bounds.width();
    }
}