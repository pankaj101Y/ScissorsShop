package com.example.hp.scissorsshop.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.hp.scissorsshop.R;

import java.util.zip.Inflater;

/**
 * Created by PANKAJ KUMAR on 4/10/2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReviewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_review,parent,false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        holder.userView.setText("user");
        holder.commentView.setText("your shop provide good services");
        int n=(int) (Math.random()*5);
        holder.ratingBar.setNumStars(n);
        holder.ratingBar.setProgress(n);
    }

    @Override
    public int getItemCount() {
        return 50;
    }

    static class  ReviewHolder extends RecyclerView.ViewHolder{
         TextView userView,commentView;
         RatingBar ratingBar;
        public ReviewHolder(View itemView) {
            super(itemView);

            userView=itemView.findViewById(R.id.userView);
            commentView=itemView.findViewById(R.id.commentView);
            ratingBar=itemView.findViewById(R.id.ratingBar);
        }
    }
}
