package com.panwenyou.moodpicture;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class ClipView extends View {

	public int top_margin = 0;
	public int left_margin = 0;
	public int right_margin = 0;
	public int bottom_margin = 0;
	
	public int top_bound = 0;
	public int left_bound = 0;
	public int right_bound = 0;
	public int bottom_bound = 0;
	
	private Paint shadow_paint = new Paint();
	private Paint bar_paint = new Paint();
	
	public ClipView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ClipView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ClipView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public void initProperties(int top, int left, int right, int bottom) {
		top_bound = top;
		left_bound = left;
		right_bound = right;
		bottom_bound = bottom;
		
		updateProperties(top_bound, left_bound, right_bound, bottom_bound);
		invalidate();
	}
	
	public void updateProperties(int top, int left, int right, int bottom) {
		if (top < top_bound) {
			top_margin = top_bound;
		} else if (top > bottom) {
			// do nothing
		} else {
			top_margin = top;
		}
		
		if (left < left_bound) {
			left_margin = left_bound;
		} else if (left > right) {
			// do nothing
		} else {
			left_margin = left;
		}
		
		if (bottom > bottom_bound) {
			bottom_margin = bottom_bound;
		} else if (top > bottom) {
			// do nothing
		} else {
			bottom_margin = bottom;
		}
		
		if (right > right_bound) {
			right_margin = right_bound;
		} else if (left > right) {
			// do nothing
		} else {
			right_margin = right;
		}
		
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		// Draw shadows
		shadow_paint.setAlpha(200);
		canvas.drawRect(0, 0, this.getWidth(), top_margin, shadow_paint);
		canvas.drawRect(0, top_margin, left_margin, this.getHeight(), shadow_paint);
		canvas.drawRect(right_margin, top_margin, this.getWidth(), this.getHeight(), shadow_paint);
		canvas.drawRect(left_margin, bottom_margin, right_margin, this.getHeight(), shadow_paint);
		
		// Draw Bars
		bar_paint.setStyle(Style.STROKE);
		bar_paint.setColor(Color.WHITE);
		bar_paint.setStrokeWidth(2);
		
		canvas.drawRect(left_margin, top_margin, right_margin, bottom_margin, bar_paint);
	}
}
