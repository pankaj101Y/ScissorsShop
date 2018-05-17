package com.example.hp.scissorsshop.UI;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hp.scissorsshop.R;


public class SingleImageFragment extends Fragment {
    private static final String IMAGE_URL = "imageUrl";
    private static final String IMAGE_POSITION="imagePosition";

    private String imageUrl;
    private int imagePosition;
    private ImageActionListener mListener;


    public SingleImageFragment() {
    }

    public static SingleImageFragment newInstance(int imagePosition,String imageUrl) {
        SingleImageFragment fragment = new SingleImageFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_URL, imageUrl);
        args.putInt(IMAGE_POSITION, imagePosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUrl = getArguments().getString(IMAGE_URL);
            imagePosition = getArguments().getInt(IMAGE_POSITION);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_single_image, container, false);
        final ImageView imageMoreOptions=v.findViewById(R.id.imageMoreOptions);
        imageMoreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(v.getContext(), imageMoreOptions);
                popup.inflate(R.menu.shop_image_more_option);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.deleteImage:
                                mListener.deleteImage(imagePosition);
                                break;
                            case R.id.takeImage:
                                mListener.takeImage();
                                break;
                            case R.id.uploadImage:
                                mListener.uploadImage();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        ImageView shopImage=v.findViewById(R.id.shopImage);
        if (imageUrl!=null){
            Glide.with(shopImage)
                    .load(imageUrl)
                    .apply(new RequestOptions().centerCrop())
                    .into(shopImage);
        }

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ImageActionListener) {
            mListener = (ImageActionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface ImageActionListener {
        void deleteImage(int position);
        void takeImage();
        void uploadImage();
    }
}
