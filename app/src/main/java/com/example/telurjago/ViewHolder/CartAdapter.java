package com.example.telurjago.ViewHolder;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.telurjago.Interface.ItemClickListner;
import com.example.telurjago.Model.Order;
import com.example.telurjago.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txt_cart_name, txt_price, txt_store_id;
    public ImageView img_cart_count;

    private ItemClickListner itemClickListner;

    public void setTxt_cart_name(TextView txt_cart_name) {
        this.txt_cart_name = txt_cart_name;
    }

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        txt_cart_name = (TextView) itemView.findViewById(R.id.cart_item_name);
        txt_price = (TextView) itemView.findViewById(R.id.cart_item_price);
        txt_store_id = (TextView) itemView.findViewById(R.id.cart_store_id);
        img_cart_count = (ImageView) itemView.findViewById(R.id.cart_item_count);

    }

    @Override
    public void onClick(View v) {




    }
}

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{

    private List<Order> lisData = new ArrayList<>();
    private Context context;

    public CartAdapter(List<Order> lisData, Context context) {
        this.lisData = lisData;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {

        holder.txt_store_id.setText(lisData.get(position).getSid());

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(""+ lisData.get(position).getQuantity(), Color.RED);
        holder.img_cart_count.setImageDrawable(drawable);

        Locale locale = new Locale("id", "ID");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(lisData.get(position).getPrice())) * (Integer.parseInt(lisData.get(position).getQuantity()));
        holder.txt_price.setText(fmt.format(price));

        holder.txt_cart_name.setText(lisData.get(position).getProductName());

    }

    @Override
    public int getItemCount() {
        return lisData.size();
    }
}
