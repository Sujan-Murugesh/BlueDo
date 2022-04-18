package com.sujan.bluedo.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sujan.bluedo.Interface.ItemClickListner;
import com.sujan.bluedo.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtCategoryName,txtCategoryDescription;
    public CircleImageView  category_imageView;
    public ItemClickListner listner;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        category_imageView = (CircleImageView) itemView.findViewById(R.id.category_list_image);
        txtCategoryName = (TextView) itemView.findViewById(R.id.category_list_name);
        txtCategoryDescription = (TextView) itemView.findViewById(R.id.category_list_description);
    }

    public void setItemClickListner(ItemClickListner listner){
        this.listner = listner;
    }

    @Override
    public void onClick(View v) {
        listner.onClick(v,getAdapterPosition(),false);
    }
}
