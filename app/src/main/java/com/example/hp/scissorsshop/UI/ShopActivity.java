package com.example.hp.scissorsshop.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.scissorsshop.DataClasses.Accounts;
import com.example.hp.scissorsshop.DataClasses.Attrs;
import com.example.hp.scissorsshop.DataClasses.Shop;
import com.example.hp.scissorsshop.Networking.Headers;
import com.example.hp.scissorsshop.Networking.NetworkCallback;
import com.example.hp.scissorsshop.Networking.NetworkRequestObject;
import com.example.hp.scissorsshop.Networking.NetworkTask;
import com.example.hp.scissorsshop.Networking.ResponseHandler;
import com.example.hp.scissorsshop.Networking.Responses;
import com.example.hp.scissorsshop.R;
import com.example.hp.scissorsshop.Utility.Activities;
import com.example.hp.scissorsshop.Utility.FileUtility;
import com.example.hp.scissorsshop.Utility.ImageCompressor;
import com.example.hp.scissorsshop.Utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class ShopActivity extends AppCompatActivity implements SingleImageFragment.ImageActionListener{
    private static final int REQUEST_TAKE_PHOTO = 2;
    private static final int CROP_REQUEST = 3;
    private static final int IMAGE_PICK_INTENT = 4;
    private static final int MAXIMUM_IMAGES=5;
    private static final int WIDTH=400;
    private static final int HEIGHT=300;

    private TextView shopNameView,shopTypeView,shopSexView,shopAddressView,shopImageNo,shopOwner;
    private ProgressBar p;
    private static Shop myShop=null;
    private static final String SEPARATOR=": ";
    private ViewPager viewPager;
    private FragmentAdapter fragmentAdapter;
    private static File photoFile;
    private int IMAGE_QUALITY=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        FloatingActionButton fab = findViewById(R.id.addPhoto);
        fab.setImageResource(R.drawable.ic_lock);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        p= Utility.getProgressBar(this);
        addContentView(p,Utility.progressBarParams());

        shopNameView=findViewById(R.id.shopNameView);
        shopTypeView=findViewById(R.id.shopTypeView);
        shopSexView=findViewById(R.id.shopSexView);
        shopAddressView=findViewById(R.id.shopAddressView);
        shopImageNo=findViewById(R.id.shopImageNo);
        shopOwner=findViewById(R.id.shopOwnerView);
        viewPager = findViewById(R.id.shopImages);

        fragmentAdapter=new FragmentAdapter(getSupportFragmentManager());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                shopImageNo.setText((position+1)+"/"+MAXIMUM_IMAGES);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        IMAGE_QUALITY=100;
        if (myShop==null)
            requestShopDetails();
        else showShop(myShop);
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            photoFile=null;
            try {
                photoFile = FileUtility.getNewImageFile();
            } catch (IOException ignored) {
                showMessage("Make Sure You Have Enough Memory");
            }
            if (photoFile!= null) {
                Uri photoUri= FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK){
            if (requestCode==REQUEST_TAKE_PHOTO){
                handleResult(photoFile.getAbsolutePath());
            }else if (requestCode==IMAGE_PICK_INTENT&&data!=null){
                handleResult(imageFileFromUri(data.getData()));
            }else if (requestCode==CROP_REQUEST){
                Bundle extras = data.getExtras();
                if (extras!=null){
                    Bitmap bitmap = extras.getParcelable("data");
                    tryImageUpload(ImageCompressor.compressedBitmap(bitmap, Bitmap.CompressFormat.JPEG,IMAGE_QUALITY));
                }else showMessage("Something Went Wrong");
            }
        }
    }

    private void handleResult(String imageFile) {
        if (imageFile!=null){
            File file=new File(imageFile);
            Intent cropIntent=getCroppedImageIntent(Uri.fromFile(file));
            if (cropIntent!=null)
                startActivityForResult(cropIntent,CROP_REQUEST);
            else {
                try {
                    IMAGE_QUALITY=91;
                    File compressed=FileUtility.getNewImageFile();
                    byte[] bytes=ImageCompressor.compressImage(file,WIDTH, (int) HEIGHT, Bitmap.CompressFormat.JPEG,
                            IMAGE_QUALITY,compressed.getAbsolutePath());
                    tryImageUpload(bytes);
                } catch (IOException e) {
                    showMessage("Make Sure You Have Enough Memory");
                }
            }
        }else showMessage("Something Went Wrong");
    }

    private Intent getCroppedImageIntent(Uri uri){
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        File f= new File(uri.getPath());
        uri=Uri.fromFile(f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            cropIntent.setDataAndTypeAndNormalize(uri,"image/*");
        }else cropIntent.setDataAndType(uri,"image/*");
        cropIntent.putExtra("outputX", WIDTH);
        cropIntent.putExtra("outputY", HEIGHT);
        cropIntent.putExtra("aspectX", 7);
        cropIntent.putExtra("aspectY",5);
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("return-data", true);

        if (cropIntent.resolveActivity(getPackageManager())!=null)
            return cropIntent;
        else return null;
    }


    private void selectImageFromGallery(){
        Intent galleyIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(galleyIntent, "Select Image From Gallery"), IMAGE_PICK_INTENT);
    }

    private String imageFileFromUri(Uri uri){
        String picturePath=null;
        String[] filePathColumn = {MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri,
                filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
             picturePath= cursor.getString(columnIndex);
            cursor.close();
        }
        return picturePath;
    }


    private void tryImageUpload(byte[] bytes){
        Log.e("networkTask",bytes.length+"");
        String encodedImage= Base64.encodeToString(bytes,Base64.DEFAULT);
        NetworkTask<NetworkRequestObject>task=new NetworkTask<>(Headers.Scope.SHOP_PROFILE, Headers.Query.ADD_IMAGE
                , Activities.SHOP_ACTIVITY, new NetworkCallback() {
            @Override
            public void onResponse(JSONObject jsonObject) {

            }
        });

        NetworkRequestObject networkRequestObject=new NetworkRequestObject(Attrs.Shop.ID, Accounts.getAccountKey());
        networkRequestObject.add(Attrs.Shop.IMAGES,encodedImage);
        task.execute(networkRequestObject);
    }


    private void requestShopDetails() {
        setRequestedOrientation(getResources().getConfiguration().orientation);
        p.setVisibility(View.VISIBLE);
        NetworkTask<NetworkRequestObject>task=new NetworkTask<>(Headers.Scope.SHOP_PROFILE,
                Headers.Query.SHOP_DETAILS, Activities.SHOP_ACTIVITY, new NetworkCallback() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                p.setVisibility(View.GONE);
                String status;
                try {
                    status=jsonObject.getString(Responses.STATUS);
                    if (status.equals(Responses.SUCCESS)){
                        myShop=new Shop(jsonObject);
                        showShop(myShop);
                    }
                } catch (JSONException e) {
                    status=Responses.FAILED;
                }
                if (status.equals(Responses.FAILED)){
                    finish();
                    showMessage("Something Went Wrong.");
                }
            }
        });

        NetworkRequestObject networkRequestObject=new NetworkRequestObject(Attrs.Shop.ID, Accounts.getAccountKey());
        task.execute(networkRequestObject);
    }

    @SuppressLint("SetTextI18n")
    private void showShop(Shop shop) {
        if (viewPager.getAdapter()==null)
            viewPager.setAdapter(fragmentAdapter);
        shopNameView.setText(SEPARATOR+shop.getName());
        shopAddressView.setText(SEPARATOR+shop.getAddress());
        shopTypeView.setText(SEPARATOR+shop.getType());
        shopSexView.setText(SEPARATOR+shop.getSex());
        shopOwner.setText(SEPARATOR+shop.getOwner());
    }


    private void showMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
        finish();
    }


    @Override
    protected void onPause() {
        super.onPause();
        p.setVisibility(View.GONE);
        ResponseHandler.clearCallbacks(Activities.SHOP_ACTIVITY);
    }

    @Override
    public void deleteImage(int position) {
    }

    @Override
    public void takeImage() {
        dispatchTakePictureIntent();
    }

    @Override
    public void uploadImage() {
        selectImageFromGallery();
    }

    public static class FragmentAdapter extends FragmentPagerAdapter{

        FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            String imageUrl=null;
            if (myShop!=null&&myShop.getImages()!=null){
                if (myShop.getImages().length>position)
                    imageUrl=myShop.getImages()[position];
            }
            if (position<MAXIMUM_IMAGES)
                return SingleImageFragment.newInstance(position,imageUrl);
            return null;
        }

        @Override
        public int getCount() {
            return MAXIMUM_IMAGES;
        }
    }
}
