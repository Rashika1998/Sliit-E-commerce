package com.sliit.sliitmadnew02.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sliit.sliitmadnew02.Interface.ItemClickListner;
import com.sliit.sliitmadnew02.R;


public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{

    public TextView txtProductName, txtProductDescription, txtProductPrice;
    public ImageView imageView;
    public ItemClickListner listner;

    public TextView txtRequesterName, txtRequesterAddress, txtRequesterPhone , txtRequesterProductStatus;



    public ItemViewHolder(View itemView)
    {
        super(itemView);

        txtRequesterProductStatus = (TextView) itemView.findViewById(R.id.requester_product_status);
        txtRequesterName = (TextView) itemView.findViewById(R.id.requester_user_name);
        txtRequesterAddress = (TextView) itemView.findViewById(R.id.requester_address);
        txtRequesterPhone = (TextView) itemView.findViewById(R.id.requester_phone);

        imageView = (ImageView) itemView.findViewById(R.id.requester_product_image);
        txtProductName = (TextView) itemView.findViewById(R.id.requester_product_name);
        txtProductDescription = (TextView) itemView.findViewById(R.id.requester_product_description);
        txtProductPrice = (TextView) itemView.findViewById(R.id.requester_product_price);


    }

    public void setItemClickListner(ItemClickListner listner)
    {
        this.listner = listner;
    }

    @Override
    public void onClick(View view)
    {
        listner.onClick(view, getAdapterPosition(), false);
    }
}
