package com.example.admin.cameraintenttutorial;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraIntentActivity extends AppCompatActivity {

    private  static final int ACTIVITY_START_CAMERA_APP= 0;
    private ImageView mPhotoCapturedImageView;
    private String mImageFileLocation = "";
    private File mGalleryFolder;
    private RecyclerView  mRecyclerView;

    private String GALLERY_LOCATION = "image gallery"; // where will be all images located



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_intent); //way u want to display ur activity into a layout
        createImageGallery();

        mRecyclerView = (RecyclerView) findViewById(R.id.galleryRecyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1 /* every row has one column*/);
        mRecyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter imageAdapter = new ImageAdapter(mGalleryFolder);
        mRecyclerView.setAdapter(imageAdapter);

    }


    public void takePhoto(View view){
        Intent callCameraApplicationIntent = new Intent();
        callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try{
            photoFile = createImageFile();
        }catch (IOException e){
            e.printStackTrace();
        }


        callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT /* way to store requested image or file*/, Uri.fromFile(photoFile));
        startActivityForResult(callCameraApplicationIntent,  ACTIVITY_START_CAMERA_APP); //start another actvt(camera i. e. and will return to our activity
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK){
            //Toast.makeText(getApplicationContext(), "Photo has been taken succesfully", Toast.LENGTH_SHORT).show();
            //Bundle extras = data.getExtras();
            //Bitmap photoCapturedBitmap = (Bitmap) extras.get("data");
            //mPhotoCapturedImageView.setImageBitmap(photoCapturedBitmap);
            //Bitmap photoCapturedBitmap = BitmapFactory.decodeFile(mImageFileLocation);
            //mPhotoCapturedImageView.setImageBitmap(photoCapturedBitmap);

            // rotateImage(setReducedImageSze());

            RecyclerView.Adapter newImageAdapter = new ImageAdapter(mGalleryFolder);
            mRecyclerView.swapAdapter(newImageAdapter, false);
        }
    }

    private void createImageGallery(){
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        mGalleryFolder = new File(storageDirectory, GALLERY_LOCATION);
        if(!mGalleryFolder.exists()){
            mGalleryFolder.mkdirs(); // will create parent folders, if required

        }

    }





    File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyMMdd_hhmmss").format(new Date());
        String imageFileName = "IMAGE_" + timestamp + "_";
        File image = File.createTempFile(imageFileName, ".jpg", mGalleryFolder);
        mImageFileLocation = image.getAbsolutePath();
        return image;

    }


    private Bitmap setReducedImageSze(){
        int targetImageViewWidth = mPhotoCapturedImageView.getWidth();
        int targetImageViewHeight = mPhotoCapturedImageView.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options(); // Options will help us to get some informatios
        bmOptions.inJustDecodeBounds = true; // now we can load image without actually loaded it- we do this to get information
        BitmapFactory.decodeFile(mImageFileLocation, bmOptions); // this will fed up out BitmapFactory bmOptions with details of actual image itself
        int cameraImageWidth = bmOptions.outWidth;
        int cameraImageHeight = bmOptions.outHeight;
        int scaleFactor = Math.min(cameraImageWidth/targetImageViewWidth, cameraImageHeight/targetImageViewHeight);
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false; //set this to false, so we can finally load the bitmap
        //Bitmap photoReducedSizeBitmap = BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
        //mPhotoCapturedImageView.setImageBitmap(photoReducedSizeBitmap);
        return BitmapFactory.decodeFile(mImageFileLocation, bmOptions);

    }


    private void rotateImage(Bitmap bitmap){
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(mImageFileLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix(); // we do with this orientation
        switch (orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            default:
        }

        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        mPhotoCapturedImageView.setImageBitmap(rotatedBitmap);
    }




/* --------------------------------------------------------------------------------------------------*/





}
