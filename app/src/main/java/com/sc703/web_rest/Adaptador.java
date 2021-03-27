package com.sc703.web_rest;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Adaptador extends BaseAdapter {

    private Activity contexto;
    ArrayList<Pais> paises;

    public Adaptador (Activity contexto, ArrayList<Pais> paises){
        this.contexto = contexto;
        this.paises = paises;
    }

    public static class ViewHolder{
        TextView tvID;
        TextView tvNombre;
    }
    @Override
    public int getCount() {
        if (paises.size() <= 0){
            return 1;
        }
        return paises.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemRow = convertView;
        LayoutInflater inflater = contexto.getLayoutInflater();
        ViewHolder viewHolder;

        if (convertView == null){
            viewHolder = new ViewHolder();
            itemRow = inflater.inflate(R.layout.item_row, null, true);
            viewHolder.tvID = (TextView) itemRow.findViewById(R.id.tv_ID);
            viewHolder.tvNombre = (TextView) itemRow.findViewById(R.id.tv_Pais);
            itemRow.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();

        }
        viewHolder.tvID.setText("#" + paises.get(position).getID());
        viewHolder.tvNombre.setText("Pais: " + paises.get(position).getNombre());
        return itemRow;
    }
}
