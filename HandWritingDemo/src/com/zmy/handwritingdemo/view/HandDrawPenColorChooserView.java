package com.zmy.handwritingdemo.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.zmy.handwritingdemo.R;

/**
 * @author hong 
 * 手写板颜色选择View
 */
public class HandDrawPenColorChooserView extends LinearLayout implements
		View.OnClickListener {

	private Context mContext;
	private LayoutInflater mInflater;

	private LinearLayout mColor1;
	private LinearLayout mColor2;
	private LinearLayout mColor3;
	private LinearLayout mColor4;
	private LinearLayout mColor5;
	private LinearLayout mColor6;

	ColorChooserListener mColorChooserListener; // 笔画颜色选择监听

	public interface ColorChooserListener {
		public void setPenColor(int penColor);
	}

	public void setColorChooserListener(ColorChooserListener listener) {
		mColorChooserListener = listener;
	}

	public HandDrawPenColorChooserView(Context context) {
		super(context);
		init(context);
	}

	public HandDrawPenColorChooserView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public HandDrawPenColorChooserView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		this.mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = mInflater.inflate(
				R.layout.hand_draw_pencolor_chooser_layout, null);
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mColor1 = (LinearLayout) view.findViewById(R.id.pencolor_1);
		mColor1.setOnClickListener(this);
		mColor2 = (LinearLayout) view.findViewById(R.id.pencolor_2);
		mColor2.setOnClickListener(this);
		mColor3 = (LinearLayout) view.findViewById(R.id.pencolor_3);
		mColor3.setOnClickListener(this);
		mColor4 = (LinearLayout) view.findViewById(R.id.pencolor_4);
		mColor4.setOnClickListener(this);
		mColor5 = (LinearLayout) view.findViewById(R.id.pencolor_5);
		mColor5.setOnClickListener(this);
		mColor6 = (LinearLayout) view.findViewById(R.id.pencolor_6);
		mColor6.setOnClickListener(this);

		addView(view);
	}
	
	public boolean isShowing() {
		if(getVisibility() == View.VISIBLE) {
			return true;
		} else {
			return false;
		}
	}

	public static class ColorSlector {
		public static int TYPE_F = 0;
		public static int TYPE_B = 1;
		
		public final static int BLACK = 0;
		public final static int RED = 1;
		public final static int ORANGE = 2;
		public final static int YELLOW = 3;
		public final static int GREEN = 4;
		public final static int BLUE = 5;

		private static int ColorNum = 6;
		private static Bitmap[] paints = new Bitmap[2 * ColorNum];
		
		public static Bitmap getPaint(Context context ,int color,int type){
			if (color >= ColorNum || type > 1 || color * type < 0) {
				throw new IllegalArgumentException();
			}
			if(type==0){
				return ImageUtils.decodeResourcesBitmap(context, R.drawable.shufa2);
			}else{
				return ImageUtils.decodeResourcesBitmap(context, R.drawable.shufa4_alpha);
			}
			
		}
	}
	@Override
	public void onClick(View v) {
		int penColor = mContext.getResources().getColor(
				R.color.hand_draw_pen_color_1);
		penColor = ColorSlector.BLACK;
		if (v == mColor1) {
			penColor = mContext.getResources().getColor(
					R.color.hand_draw_pen_color_1);
			penColor = ColorSlector.BLACK;
			mColor1.setBackgroundResource(R.drawable.hand_draw_pencolor_check);
		} else {
			mColor1.setBackgroundResource(R.color.transparent);
		}
		if (v == mColor2) {
			penColor = mContext.getResources().getColor(
					R.color.hand_draw_pen_color_2);
			penColor = ColorSlector.RED;
			mColor2.setBackgroundResource(R.drawable.hand_draw_pencolor_check);
		} else {
			mColor2.setBackgroundResource(R.color.transparent);
		}
		if (v == mColor3) {
			penColor = mContext.getResources().getColor(
					R.color.hand_draw_pen_color_3);
			penColor = ColorSlector.ORANGE;
			mColor3.setBackgroundResource(R.drawable.hand_draw_pencolor_check);
		} else {
			mColor3.setBackgroundResource(R.color.transparent);
		}
		if (v == mColor4) {
			penColor = mContext.getResources().getColor(
					R.color.hand_draw_pen_color_4);
			penColor = ColorSlector.YELLOW;
			mColor4.setBackgroundResource(R.drawable.hand_draw_pencolor_check);
		} else {
			mColor4.setBackgroundResource(R.color.transparent);
		}
		if (v == mColor5) {
			penColor = mContext.getResources().getColor(
					R.color.hand_draw_pen_color_5);
			penColor = ColorSlector.GREEN;
			mColor5.setBackgroundResource(R.drawable.hand_draw_pencolor_check);
		} else {
			mColor5.setBackgroundResource(R.color.transparent);
		}
		if (v == mColor6) {
			penColor = mContext.getResources().getColor(
					R.color.hand_draw_pen_color_6);
			penColor = ColorSlector.BLUE;
			mColor6.setBackgroundResource(R.drawable.hand_draw_pencolor_check);
		} else {
			mColor6.setBackgroundResource(R.color.transparent);
		}
		if (mColorChooserListener != null) {
			mColorChooserListener.setPenColor(penColor);
		}
		this.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				setVisibility(View.GONE);
			}
		}, 300);
	}

}
