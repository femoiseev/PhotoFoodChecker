package com.abu.photofoodchecker.android;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;

import android.net.*;
import java.io.*;
import android.os.Environment;
import android.provider.MediaStore;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;

import com.abu.photofoodchecker.AnalysisResultActivity;
import com.abu.photofoodchecker.R;

public class MainActivity extends Activity {

	private final int TAKE_PICTURE = 0;
	private final int SELECT_FILE = 1;
	private final int CROP_PICTURE = 2;
	
	private String resultUrl = "result.txt";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ActivityCompat.requestPermissions(this,
				new String[]{Manifest.permission.INTERNET}, 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	
	public void captureImageFromSdCard( View view ) {
    	Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    	intent.setType("image/*");

    	startActivityForResult(intent, SELECT_FILE);
    }
	
	public static final int MEDIA_TYPE_IMAGE = 1;

	private static Uri getOutputMediaFileUri(){
	      return Uri.fromFile(getOutputMediaFile());
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "ABBYY Cloud OCR SDK Demo App");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            return null;
	        }
	    }

	    // Create a media file name
	    File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "image.jpg" );

	    return mediaFile;
	}
    
    public void captureImageFromCamera( View view) {
    	Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
    	Uri fileUri = getOutputMediaFileUri(); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        
        startActivityForResult(intent, TAKE_PICTURE);
    } 
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;

		if (requestCode == TAKE_PICTURE || requestCode == SELECT_FILE) {
            String imageFilePath = null;

            Uri imageUri = null;
            switch (requestCode) {
                case TAKE_PICTURE:
                    imageUri = getOutputMediaFileUri();
                    Intent results = new Intent( this, ResultsActivity.class);
                    results.putExtra("IMAGE_PATH", imageUri.getPath());
                    results.putExtra("RESULT_PATH", resultUrl);
                    startActivity(results);
                    break;
                case SELECT_FILE:
                    imageUri = data.getData();

                    break;
            }

            Intent intent = new Intent("com.android.camera.action.CROP");
           // intent.setClassName("com.android.camera", "com.android.camera.CropImage");
            intent.setDataAndType(imageUri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 256);
            intent.putExtra("outputY", 256);
            intent.putExtra("noFaceDetection", true);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, CROP_PICTURE);
        } else if (requestCode == CROP_PICTURE) {
            deleteFile(resultUrl);


            Bundle extras = data.getExtras();
            Bitmap picture = extras.getParcelable("data");
            String imageFilePath = getOutputMediaFile().getPath();
            try {
                FileOutputStream out = new FileOutputStream(imageFilePath);
                picture.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
                e.printStackTrace();
            }


            Intent results = new Intent( this, ResultsActivity.class);
            results.putExtra("IMAGE_PATH", imageFilePath);
            results.putExtra("RESULT_PATH", resultUrl);
            startActivity(results);
        }
    }
}
