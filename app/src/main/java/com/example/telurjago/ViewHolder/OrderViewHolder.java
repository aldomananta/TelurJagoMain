package com.example.telurjago.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telurjago.Interface.ItemClickListner;
import com.example.telurjago.R;

public class OrderViewHolder extends RecyclerView.ViewHolder{

    public TextView txtOrderID, txtOrderStatus, txtOrderPhone, txtOrderAddress;

    public Button detailsBtn, cancelBtn;

    public ItemClickListner itemClickListner;

    public OrderViewHolder(@NonNull final View itemView) {
        super(itemView);

        txtOrderID = (TextView) itemView.findViewById(R.id.order_id);
        txtOrderStatus = (TextView) itemView.findViewById(R.id.order_status);
        txtOrderPhone = (TextView) itemView.findViewById(R.id.order_phone);
        txtOrderAddress = (TextView) itemView.findViewById(R.id.order_address);

        detailsBtn = (Button) itemView.findViewById(R.id.btnDetail_customer);
        cancelBtn = (Button) itemView.findViewById(R.id.btnCancel_customer);

//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Toast.makeText(itemView.getContext(), "Clicked", Toast.LENGTH_SHORT).show();
//
//            }
//        });


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
}
