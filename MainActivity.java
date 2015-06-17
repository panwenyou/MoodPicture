package com.panwenyou.moodpicture;

import java.io.File;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set style of the title bar & set the main view
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_layout);
        
        findViewById(R.id.fromtuku_btn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// open gel activity
				Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
				getAlbum.setType("image/*");
				startActivityForResult(getAlbum, 0);
			}
		});
        findViewById(R.id.camera_btn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// open camera activity
				Intent open_camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// Captured picture will store in this path
				Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"tempimg.jpg"));
				open_camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				startActivityForResult(open_camera_intent, 1);
			}
		});
        findViewById(R.id.title_next).setVisibility(View.INVISIBLE);
        ((TextView)findViewById(R.id.title_step)).setText("Ê×Ò³");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    	if (resultCode != RESULT_OK) {
    		Log.e("wrong_load", "loading mistake");
    	}

    	// requestCode 0: picture from galley
    	// requestCode 1: picture from camera
    	if (requestCode == 0) {
			if (data != null) {
				Uri uri = data.getData();
				Intent intent = new Intent(MainActivity.this,
						ShowPictureActivity.class);
				intent.putExtra("Uri", uri);
				intent.putExtra("requestCode", requestCode);
				startActivity(intent);
			} else {
				Toast.makeText(this, "ÇëÑ¡Ôñ", Toast.LENGTH_SHORT).show();
			}
    	} else if (requestCode == 1) {
    		Intent intent = new Intent(MainActivity.this, ShowPictureActivity.class);
    		intent.putExtra("requestCode", requestCode);
    		startActivity(intent);
    	}
    }
    
}
