package com.sliit.sliitmadnew02.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sliit.sliitmadnew02.Interface.ItemClickListner;
import com.sliit.sliitmadnew02.R;

public class RequestsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtProductName, txtProductDescription, txtProductPrice;
    public TextView txtRequesterName, txtRequesterAddress, txtRequesterPhone;
    public ImageView imageView;
    public ItemClickListner listner;

    public RequestsViewHolder(View itemView)
    {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.product_image);
        txtProductName = (TextView) itemView.findViewById(R.id.product_name);
        txtProductDescription = (TextView) itemView.findViewById(R.id.product_description);
        txtProductPrice = (TextView) itemView.findViewById(R.id.product_price);

        txtRequesterName = (TextView) itemView.findViewById(R.id.requester_user_name);
        txtRequesterAddress = (TextView) itemView.findViewById(R.id.requester_address);
        txtRequesterPhone = (TextView) itemView.findViewById(R.id.requester_phone);

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