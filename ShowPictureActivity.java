package com.panwenyou.moodpicture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ShowPictureActivity extends Activity implements OnClickListener {
	// Bitmap to be shown
	private Bitmap bmBitmap;
	private Bitmap bmBitmap2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		// Title bar and main view
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.showpicture_layout);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_layout);
		
		
		ImageView iv = (ImageView)findViewById(R.id.image_view);
		
		// URL passed by last activity
		Intent i = getIntent();
		Uri uri = i.getParcelableExtra("Uri");
		int request_code = i.getIntExtra("requestCode", 0);
		
		// Request code 0: galley picture
		// Request code 1: camera picture
		if (request_code == 0) {
			ContentResolver contentResolver = getContentResolver();
			try {
				bmBitmap = MediaStore.Images.Media.getBitmap(contentResolver,
						uri);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// Read from file
			try {
				File file = new File(Environment.getExternalStorageDirectory(),"tempimg.jpg");
				FileInputStream fis = new FileInputStream(file);
				bmBitmap = BitmapFactory.decodeStream(fis);
				fis.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				Toast.makeText(this, "Sorry, your storage have some problem", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		float scale = 1.0f;
		if (bmBitmap.getWidth() > bmBitmap.getHeight()) {
			if (bmBitmap.getWidth() > 1280) {
				scale = 1280 * 1.0f / bmBitmap.getWidth();
			}
		} else {
			if (bmBitmap.getHeight() > 1280) {
				scale = 1280 * 1.0f / bmBitmap.getHeight();
			}
		}
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		bmBitmap2 = Bitmap.createBitmap(bmBitmap, 0, 0, bmBitmap.getWidth(), bmBitmap.getHeight(), matrix, true);
		
		try {
			FileOutputStream fos = openFileOutput("tempBMP.bmp", Context.MODE_PRIVATE);
			bmBitmap2.compress(CompressFormat.JPEG, 100, fos);
			fos.close();
			bmBitmap.recycle();
			FileInputStream fis = openFileInput("tempBMP.bmp");
			bmBitmap2 = BitmapFactory.decodeStream(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		iv.setImageBitmap(bmBitmap2);
		
		findViewById(R.id.happy_btn).setOnClickListener(this);
		findViewById(R.id.sad_btn).setOnClickListener(this);
		findViewById(R.id.memory_btn).setOnClickListener(this);
		findViewById(R.id.victory_btn).setOnClickListener(this);
		
		findViewById(R.id.title_next).setVisibility(View.INVISIBLE);
		((TextView)findViewById(R.id.title_step)).setText("今天心情如何？");
		
		LinearLayout linear_layout = (LinearLayout) findViewById(R.id.show_linear_layout);
		AlphaAnimation aa = new AlphaAnimation(0, 1);
		aa.setDuration(500);
		LayoutAnimationController lac = new LayoutAnimationController(aa, 0.5f);
		linear_layout.setLayoutAnimation(lac);
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
		// Get the mood that users chose
		String string = "";
		switch (arg0.getId()) {
		case R.id.happy_btn: {
			string = "happy";
			break;
		}
		case R.id.sad_btn: {
			string = "sad";
			break;
		}
		case R.id.memory_btn: {
			string = "memory";
			break;
		}
		case R.id.victory_btn: {
			string = "victory";
			break;
		}
		default: break;
		}
		
		// Get pixels' values
		int x = 0, y = 0;
		int width = bmBitmap2.getWidth();
		int height = bmBitmap2.getHeight();
		int[] pixels = new int[width*height];
		bmBitmap2.getPixels(pixels, 0, width, x, y, width, height);
		
		// color processing
		ImageProcess.colorAdjustment(string, pixels, width, height);
		// re-write into bitmap
		Bitmap temp = Bitmap.createBitmap(pixels, 0, width, width, height, Config.ARGB_8888);
		
		
		// Store it into temporary file
		try {
			FileOutputStream fos = openFileOutput("tempBMP.bmp", Context.MODE_PRIVATE);
			temp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Intent i = new Intent(this, ClipActivity.class);
		i.putExtra("mood", string);
		startActivity(i);
		
	}
}
