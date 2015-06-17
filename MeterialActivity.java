package com.panwenyou.moodpicture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MeterialActivity extends Activity implements OnItemClickListener,
		OnTouchListener {

	private Bitmap bitmap;
	private SlidingMenu m_sliding_menu;
	private String mood;
	private int begin_x = 0, begin_y = 0;

	// view list stores the views that users have added into main activity
	private ArrayList<View> view_list = new ArrayList<View>();
	// point_list stores the raw position of all the views in view list
	private ArrayList<Point> point_list = new ArrayList<Point>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// Title and main view
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.meterial_activity);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_layout);

		// Get mood and classify material
		mood = getIntent().getStringExtra("mood");

		ImageView iv = (ImageView) findViewById(R.id.image_view3);

		try {
			FileInputStream fis = openFileInput("tempBMP.bmp");
			bitmap = BitmapFactory.decodeStream(fis);
			iv.setImageBitmap(bitmap);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		((TextView) findViewById(R.id.title_step)).setText("Ìí¼ÓËØ²Ä");

		// Add a sliding menu to store the available materials
		m_sliding_menu = new SlidingMenu(this);
		m_sliding_menu.setMode(SlidingMenu.LEFT);
		m_sliding_menu.setBehindOffsetRes(R.dimen.sliding_menu_offset);
		m_sliding_menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		m_sliding_menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		m_sliding_menu.setMenu(R.layout.slidingmenu_layout);

		ListView lv = (ListView) findViewById(R.id.listView1);
		SimpleAdapter s_adapter = new SimpleAdapter(this, getData(),
				R.layout.list_layout, new String[] { "image" },
				new int[] { R.id.list_img });
		lv.setAdapter(s_adapter);

		lv.setOnItemClickListener(this);

		findViewById(R.id.title_next).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Get main image's raw position and data
				ImageView iv = (ImageView) findViewById(R.id.image_view3);
				Matrix matrix = iv.getImageMatrix();
				Rect rect = iv.getDrawable().getBounds();
				float[] values = new float[9];
				matrix.getValues(values);
				Meterial renderImage = new Meterial();
				renderImage.y = (int) values[5];
				renderImage.x = (int) values[2];
				renderImage.width = (int) (rect.width() * values[0]);
				renderImage.height = (int) (rect.height() * values[0]);
				Matrix scale_matrix = new Matrix();
				float scale_x = renderImage.width*1.0f / bitmap.getWidth();
				float scale_y = renderImage.height*1.0f / bitmap.getHeight();
				scale_matrix.postScale(scale_x, scale_y);
				Bitmap bitmap_temp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), scale_matrix, true);
				renderImage.pixels = new int[bitmap_temp.getWidth()
						* bitmap_temp.getHeight()];
				bitmap_temp.getPixels(renderImage.pixels, 0, bitmap_temp.getWidth(),
						0, 0, bitmap_temp.getWidth(), bitmap_temp.getHeight());
				Log.d("renderImage", String.format("%d %d %d %d",
						renderImage.x, renderImage.y, renderImage.width,
						renderImage.height));
				Log.d("111", String.format("%d %d -- %d %d", renderImage.width,
						renderImage.height, bitmap_temp.getWidth(),
						bitmap_temp.getHeight()));
				// Get every views' raw position and data
				ArrayList<Meterial> meterial_list = new ArrayList<Meterial>();
				for (int i = 0; i < view_list.size(); i++) {
					ImageView iv_temp = (ImageView) view_list.get(i);
					iv_temp.setDrawingCacheEnabled(true);
					Bitmap bm_temp = iv_temp.getDrawingCache();
					Meterial m = new Meterial();
					m.pixels = new int[bm_temp.getHeight() * bm_temp.getWidth()];
					m.x = (int) iv_temp.getX();
					m.y = (int) iv_temp.getY();
					m.width = bm_temp.getWidth();
					m.height = bm_temp.getHeight();
					bm_temp.getPixels(m.pixels, 0, bm_temp.getWidth(), 0, 0,
							bm_temp.getWidth(), bm_temp.getHeight());
					meterial_list.add(m);
					Log.d("renderImage", String.format("%d %d %d %d", m.x, m.y,
							m.width, m.height));
				}
				Meterial meterial = ImageProcess.renderImage(renderImage,
						meterial_list);
				Bitmap resultBitmap = Bitmap.createBitmap(meterial.pixels, 0,
						meterial.width, meterial.width, meterial.height,
						Config.ARGB_8888);
				File file = new File(Environment.getExternalStorageDirectory(),
						"tempimg.jpg");
				try {
					FileOutputStream fos = new FileOutputStream(file);
					resultBitmap.compress(CompressFormat.JPEG, 100, fos);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Intent intent = new Intent(MeterialActivity.this,
						ShareActivity.class);
				startActivity(intent);
			}
		});
	}

	// Used in simple adapter
	private List<Map<String, Object>> getData() {
		// TODO Auto-generated method stub
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Log.d("mood", mood);
		if (mood.equalsIgnoreCase("happy")) {
			Log.d("mood", mood);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", R.drawable.k1);
			list.add(map);

			map = new HashMap<String, Object>();
			map.put("image", R.drawable.k2);
			list.add(map);

			map = new HashMap<String, Object>();
			map.put("image", R.drawable.k3);
			list.add(map);
		} else if (mood.equalsIgnoreCase("sad")) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", R.drawable.s1);
			list.add(map);

			map = new HashMap<String, Object>();
			map.put("image", R.drawable.s2);
			list.add(map);

			map = new HashMap<String, Object>();
			map.put("image", R.drawable.s3);
			list.add(map);
		} else if (mood.equalsIgnoreCase("victory")) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", R.drawable.q1);
			list.add(map);

			map = new HashMap<String, Object>();
			map.put("image", R.drawable.q2);
			list.add(map);

			map = new HashMap<String, Object>();
			map.put("image", R.drawable.q3);
			list.add(map);
		} else if (mood.equalsIgnoreCase("memory")) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", R.drawable.h1);
			list.add(map);

			map = new HashMap<String, Object>();
			map.put("image", R.drawable.h2);
			list.add(map);

			map = new HashMap<String, Object>();
			map.put("image", R.drawable.h3);
			list.add(map);
		}

		return list;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		m_sliding_menu.showContent();

		ImageView iv = new ImageView(this);
		switch (arg2) {
		case 0: {
			if (mood.equalsIgnoreCase("happy")) {
				iv.setImageResource(R.drawable.k1);
			} else if (mood.equalsIgnoreCase("sad")) {
				iv.setImageResource(R.drawable.s1);
			} else if (mood.equalsIgnoreCase("victory")) {
				iv.setImageResource(R.drawable.q1);
			} else if (mood.equalsIgnoreCase("memory")) {
				iv.setImageResource(R.drawable.h1);
			}
			break;
		}
		case 1: {
			if (mood.equalsIgnoreCase("happy")) {
				iv.setImageResource(R.drawable.k2);
			} else if (mood.equalsIgnoreCase("sad")) {
				iv.setImageResource(R.drawable.s2);
			} else if (mood.equalsIgnoreCase("victory")) {
				iv.setImageResource(R.drawable.q2);
			} else if (mood.equalsIgnoreCase("memory")) {
				iv.setImageResource(R.drawable.h2);
			}
			break;
		}
		case 2: {
			if (mood.equalsIgnoreCase("happy")) {
				iv.setImageResource(R.drawable.k3);
			} else if (mood.equalsIgnoreCase("sad")) {
				iv.setImageResource(R.drawable.s3);
			} else if (mood.equalsIgnoreCase("victory")) {
				iv.setImageResource(R.drawable.q3);
			} else if (mood.equalsIgnoreCase("memory")) {
				iv.setImageResource(R.drawable.h3);
			}
			break;
		}
		}
		view_list.add(iv);
		Point p = new Point((int) iv.getX(), (int) iv.getY());
		point_list.add(p);
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.relative_layout);
		rl.addView(iv, new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		iv.setOnTouchListener(this);

		for (int i = 0; i < view_list.size(); i++) {
			Point point = point_list.get(i);
			ImageView view = (ImageView) view_list.get(i);
			Log.d("position", String.format("%d %d", point.x, point.y));
			view.layout(point.x, point.y, point.x + view.getWidth(), point.y
					+ view.getHeight());
		}
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub

		ImageView iv = (ImageView) arg0;
		int top = (int) iv.getY();
		int left = (int) iv.getX();
		int bottom = top + iv.getHeight();
		int right = left + iv.getWidth();

		switch (arg1.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			begin_x = (int) arg1.getRawX();
			begin_y = (int) arg1.getRawY();
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			int x_distance = (int) (arg1.getRawX() - begin_x);
			int y_distance = (int) (arg1.getRawY() - begin_y);
			iv.layout(left + x_distance, top + y_distance, right + x_distance,
					bottom + y_distance);
			begin_x = (int) arg1.getRawX();
			begin_y = (int) arg1.getRawY();
			int index = view_list.indexOf(iv);
			Point p = point_list.get(index);
			p.x = begin_x;
			p.y = begin_y;
			break;
		}
		}
		return true;
	}

}
