package com.example.telurjago.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telurjago.Interface.ItemClickListner;
import com.example.telurjago.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName, txtProductDesc,txtProductPrice;
    public ImageView imageView;
    public ItemClickListner listner;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.product_Image);
        txtProductDesc = (TextView) itemView.findViewById(R.id.product_Desc);
        txtProductName = (TextView) itemView.findViewById(R.id.product_Name);
        txtProductPrice = (TextView) itemView.findViewById(R.id.product_Price);

    }

    public void setItemClickListner (ItemClickListner listner){

        this.listner = listner;

    }

    @Override
    public void onClick(View v) {

        listner.onClick(v, getAdapterPosition(), false);

    }
}
