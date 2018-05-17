package com.example.hp.scissorsshop.Adapters;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.hp.scissorsshop.R;

import java.util.ArrayList;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder>{
    private ArrayList<Object>objects=new ArrayList<>();
    private String contentType;
    private ContentActionListener actionListener=null;

    public ContentAdapter(ArrayList<Object> objects,String contentType,
                          ContentActionListener actionListener){
        this.objects=objects;
        this.contentType=contentType;
        this.actionListener=actionListener;

    }

    @NonNull
    @Override
    public ContentAdapter.ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContentViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_content_view,parent,false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ContentAdapter.ContentViewHolder holder, final int position) {
        holder.contentNo.setText(contentType+" "+(position+1));
        holder.contentText.setText(objects.get(position).toString());

        holder.moreOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(v.getContext(), holder.moreOptionsButton);
                popup.inflate(R.menu.content_more_options);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.editOption:
                                actionListener.edit(holder.getAdapterPosition());
                                break;
                            case R.id.deleteOption:
                                actionListener.delete(holder.getAdapterPosition());
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return objects.size();
    }

    public void removeItem(int position) {
        objects.remove(position);
        notifyItemRemoved(position);
    }

    public Object getItem(int position) {
        if (position<objects.size())
            return objects.get(position);
        return null;
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView contentNo,contentText;
        ImageButton moreOptionsButton;
        ContentViewHolder(View view) {
            super(view);

            contentNo=view.findViewById(R.id.contentNumber);
            contentText=view.findViewById(R.id.contentText);
            moreOptionsButton=view.findViewById(R.id.moreOptionsButton);
        }
    }
}
