package com.panwenyou.moodpicture;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ShareActivity extends Activity {
	
	private String title;
	private String content;
	private Uri uri;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.share_layout);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_layout);
		
		((TextView)findViewById(R.id.title_step)).setText("你要说点什么？");
		((TextView)findViewById(R.id.title_next)).setText("分享");
		Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/tempimg.jpg");
		((ImageView)findViewById(R.id.share_img)).setImageBitmap(bitmap);
		
		findViewById(R.id.title_next).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//uri = getIntent().getParcelableExtra("Uri");
				uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "tempimg.jpg"));
				title = ((EditText)findViewById(R.id.share_title)).getText().toString();
				content = ((EditText)findViewById(R.id.share_content)).getText().toString();
				
				Intent share_intent = new Intent(Intent.ACTION_SEND);
				share_intent.setType("image/jpg");
				share_intent.putExtra(Intent.EXTRA_STREAM, uri);
				share_intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
				share_intent.putExtra(Intent.EXTRA_TITLE, title);
				share_intent.putExtra(Intent.EXTRA_TEXT, content);
				share_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(share_intent, "请选择"));
			}
		});
	}
}
