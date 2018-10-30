package com.example.danut.restaurant;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static android.icu.text.DateFormat.NONE;
import static com.example.danut.restaurant.Menu.*;
import static com.example.danut.restaurant.R.*;

/**
 * Created by danut on 24/03/2018.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>{

    //declare variables
    private Context mContext;
    private List<Menu> mUploads;
    private OnItemClickListener clickListener;

    public ImageAdapter(Context context, List<Menu> uploads){
        mContext = context;
        mUploads = uploads;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    //set the iten layout view
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Menu uploadCurrent = mUploads.get(position);
        holder.textViewName.setText("Item Name: "+uploadCurrent.getItemName());
        holder.textViewDescription.setText("Description: "+uploadCurrent.getItemDescription());
        holder.textViewPrice.setText("Price: € "+uploadCurrent.getItemPrice());
        Picasso.with(mContext)
                .load(uploadCurrent.getItemImage())
                .placeholder(mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    //assign the values of textViews
    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{

        public TextView textViewName;
        public TextView textViewDescription;
        public TextView textViewPrice;
        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(id.text_view_name);
            textViewDescription = itemView.findViewById(id.text_view_description);
            textViewPrice = itemView.findViewById(id.text_view_price);
            imageView = itemView.findViewById(id.image_view_upload);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if(clickListener !=null){
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    clickListener.onItemClick(position);
                }
            }
        }

        //create onItem click menu
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select an Action");
            MenuItem doUpdate  = menu.add(NONE, 1, 1, "Update");
            MenuItem doDelete  = menu.add(NONE, 2, 2, "Delete");

            doUpdate.setOnMenuItemClickListener(this);
            doDelete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(clickListener !=null){
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    switch (item.getItemId()){
                        case 1:
                            clickListener.onUpdateClick(position);
                            return true;

                        case 2:
                            clickListener.onDeleteClick(position);
                            return true;
                    }
                }
            }

            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onUpdateClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItmClickListener(OnItemClickListener listener){
        clickListener = listener;
    }
}
