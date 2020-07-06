package com.example.telurjago.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telurjago.Interface.ItemClickListner;
import com.example.telurjago.R;

public class SellerOrderViewHolder extends RecyclerView.ViewHolder {

    public TextView txtOrderID, txtOrderStatus, txtOrderPhone, txtOrderAddress;

    public Button btnEdit, btnRemove, btnDetail;

    public ItemClickListner itemClickListner;

    public SellerOrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderID = (TextView) itemView.findViewById(R.id.seller_order_id);
        txtOrderStatus = (TextView) itemView.findViewById(R.id.seller_order_status);
        txtOrderPhone = (TextView) itemView.findViewById(R.id.seller_order_phone);
        txtOrderAddress = (TextView) itemView.findViewById(R.id.seller_order_address);

        btnEdit = (Button) itemView.findViewById(R.id.btnEdit);
        btnRemove = (Button) itemView.findViewById(R.id.btnRemove);
        btnDetail = (Button) itemView.findViewById(R.id.btnDetail);

//        itemView.setOnClickListener(this);
//        itemView.setOnCreateContextMenuListener(this);

    }
//
//    public void setItemClickListner(ItemClickListner itemClickListner) {
//        this.itemClickListner = itemClickListner;
//
//
//
//    }
//
//    @Override
//    public void onClick(View v) {
//
//        itemClickListner.onClick(v, getAdapterPosition(), false);
//
//    }
//
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//
//        menu.setHeaderTitle("Select The Action");
//
//        menu.add(0,0,getAdapterPosition(), "Update");
//        menu.add(0,1,getAdapterPosition(), "Delete");
//
//    }
}
