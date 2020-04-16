package com.sliit.sliitmadnew02.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sliit.sliitmadnew02.Interface.ItemClickListner;
import com.sliit.sliitmadnew02.R;

public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtUserName, txtUserPhone;
    public ImageView userImageView;
    public ItemClickListner listner;


    public UserViewHolder(View itemView)
    {
        super(itemView);

        userImageView = (ImageView) itemView.findViewById(R.id.all_requester_profile_image);
        txtUserName = (TextView) itemView.findViewById(R.id.all_requester_profile_full_name);
        txtUserPhone = (TextView) itemView.findViewById(R.id.all_requester_phone);

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
