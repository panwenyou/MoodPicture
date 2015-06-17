package com.panwenyou.moodpicture;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ClipActivity extends Activity {
	
	// stands for the area users tab in
	private static int left_area = 1;
	private static int top_area = 2;
	private static int right_area = 3;
	private static int bottom_area = 4;
	
	int area = 0;
	float begin_x = 0;
	float begin_y = 0;
	
	private Bitmap bitmap;
	private String mood;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		// Title and main view
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.clip_activity);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_layout);
		
		
		mood = getIntent().getStringExtra("mood");
		// read bitmap from temporary file first
		try {
			FileInputStream fis = openFileInput("tempBMP.bmp");
			bitmap = BitmapFactory.decodeStream(fis);
			ImageView iv = (ImageView)findViewById(R.id.image_view2);
			iv.setImageBitmap(bitmap);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		((TextView)findViewById(R.id.title_step)).setText("Ότ²Γ");
		
		// next-button clicked
		// clip the image and store into temporary file
		findViewById(R.id.title_next).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				// Calculate the area that the clip view capture
				ClipView cv = (ClipView)findViewById(R.id.clip_view);
				float cv_width = cv.right_bound - cv.left_bound;
				float cv_height = cv.bottom_bound - cv.top_bound;
				float x = (cv.left_margin - cv.left_bound) / cv_width * bitmap.getWidth();
				float y = (cv.top_margin - cv.top_bound) / cv_height * bitmap.getHeight();
				float width = (cv.right_margin - cv.left_margin) / cv_width * bitmap.getWidth();
				float height = (cv.bottom_margin - cv.top_margin) / cv_height * bitmap.getHeight();
				
				// Create a new bitmap for clipped bitmap
				Bitmap temp = Bitmap.createBitmap(bitmap, (int)x, (int)y, (int)width, (int)height);
				
				// Store the bitmap into temporary file
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
				
				Intent i = new Intent(ClipActivity.this, MeterialActivity.class);
				i.putExtra("mood", mood);
				startActivity(i);
				
			}
		});
		
		// When clip view is touched
		findViewById(R.id.clip_view).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				ClipView cv = (ClipView)arg0;
				switch (arg1.getAction() & MotionEvent.ACTION_MASK) {
				// Locate area that user touch in
				case MotionEvent.ACTION_DOWN: {
					float x = arg1.getX();
					float y = arg1.getY();
					begin_x = x;
					begin_y = y;
					Log.d("x", String.format("%f", x));
					Log.d("y", String.format("%f", y));
					if (x > cv.left_margin + 50 && x < cv.right_margin-50) {
						if (y > cv.top_margin -50 && y < cv.top_margin + 50) {
							area = top_area;
						} else if (y > cv.bottom_margin - 50 && y < cv.bottom_margin + 50) {
							area = bottom_area;
						}
					} else if (x <= cv.left_margin + 50 && x >= cv.left_margin - 50) {
						area = left_area;
					} else if (x >= cv.right_margin - 50 && x <= cv.right_margin + 50) {
						area = right_area;
					} else {
						area = 0;
					}
				}
				
				
				case MotionEvent.ACTION_MOVE: {
					Log.d("area", String.format("%d", area));
					switch (area) {
					case 1: {
						int distance = (int) (arg1.getX() - begin_x);
						begin_x = arg1.getX();
						cv.left_margin += distance;
						cv.updateProperties(cv.top_margin, cv.left_margin,
								cv.right_margin, cv.bottom_margin);
						break;
					}
					case 2: {
						int distance = (int)(arg1.getY() - begin_y);
						begin_y = arg1.getY();
						cv.top_margin += distance;
						cv.updateProperties(cv.top_margin, cv.left_margin,
								cv.right_margin, cv.bottom_margin);
						break;
					}
					case 3: {
						int distance = (int)(arg1.getX() - begin_x);
						begin_x = arg1.getX();
						cv.right_margin += distance;
						cv.updateProperties(cv.top_margin, cv.left_margin,
								cv.right_margin, cv.bottom_margin);
						break;
					}
					case 4: {
						int distance = (int)(arg1.getY() - begin_y);
						begin_y = arg1.getY();
						cv.bottom_margin += distance;
						cv.updateProperties(cv.top_margin, cv.left_margin,
								cv.right_margin, cv.bottom_margin);
						break;
					}
					default:
						break;
					}
				}
				}
				
				return true;
			}
		});
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		
		// Get the bitmap raw position
		ImageView iv = (ImageView)findViewById(R.id.image_view2);
		Matrix matrix = iv.getImageMatrix();
		Rect rect = iv.getDrawable().getBounds();
		float[] values = new float[9];
		matrix.getValues(values);
		int top = (int) values[5];
		int left = (int) values[2];
		int right = (int) (left + rect.width() * values[0]);
		int bottom = (int) (top + rect.height() * values[0]);
		/*Log.d("width", String.format("%d", width));
		Log.d("height", String.format("%d", height));
		Log.d("top", String.format("%d", top));
		Log.d("left", String.format("%d", left));*/
		ClipView cv = (ClipView)findViewById(R.id.clip_view);
		cv.initProperties(top, left, right, bottom);
	}
	
}
