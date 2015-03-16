package com.zmy.handwritingdemo.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.zmy.handwritingdemo.R;

/**
 * @author hong 
 * 手写板笔画大小选择View
 */
public class HandDrawPenStyleChooserView extends LinearLayout implements
		View.OnClickListener {

	private Context mContext;
	private LayoutInflater mInflater;

	private LinearLayout mStyle1;
	private LinearLayout mStyle2;
	private LinearLayout mStyle3;
	private LinearLayout mStyle4;
	private LinearLayout mStyle5;

	StyleChooserListener mStyleChooserListener; // 笔画大小选择监听

	public interface StyleChooserListener {
		public void setPenStyle(float penStyle);
	}

	public void setStyleChooserListener(StyleChooserListener listener) {
		mStyleChooserListener = listener;
	}

	public HandDrawPenStyleChooserView(Context context) {
		super(context);
		init(context);
	}

	public HandDrawPenStyleChooserView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public HandDrawPenStyleChooserView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		this.mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = mInflater.inflate(
				R.layout.hand_draw_penstyle_chooser_layout, null);
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mStyle1 = (LinearLayout) view.findViewById(R.id.penstyle_1);
		mStyle1.setOnClickListener(this);
		mStyle2 = (LinearLayout) view.findViewById(R.id.penstyle_2);
		mStyle2.setOnClickListener(this);
		mStyle3 = (LinearLayout) view.findViewById(R.id.penstyle_3);
		mStyle3.setOnClickListener(this);
		mStyle4 = (LinearLayout) view.findViewById(R.id.penstyle_4);
		mStyle4.setOnClickListener(this);
		mStyle5 = (LinearLayout) view.findViewById(R.id.penstyle_5);
		mStyle5.setOnClickListener(this);

		addView(view);
	}
	
	public boolean isShowing() {
		if(getVisibility() == View.VISIBLE) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onClick(View v) {
		float handLineWidth = mContext.getResources().getDimension(
				R.dimen.hand_draw_pen_sytle_1);
		if (v == mStyle1) {
			handLineWidth = mContext.getResources().getDimension(
					R.dimen.hand_draw_pen_sytle_1);
			mStyle1.setBackgroundResource(R.drawable.hand_draw_penstyle_check);
		} else {
			mStyle1.setBackgroundResource(R.color.transparent);
		}
		if (v == mStyle2) {
			handLineWidth = mContext.getResources().getDimension(
					R.dimen.hand_draw_pen_sytle_2);
			mStyle2.setBackgroundResource(R.drawable.hand_draw_penstyle_check);
		} else {
			mStyle2.setBackgroundResource(R.color.transparent);
		}
		if (v == mStyle3) {
			handLineWidth = mContext.getResources().getDimension(
					R.dimen.hand_draw_pen_sytle_3);
			mStyle3.setBackgroundResource(R.drawable.hand_draw_penstyle_check);
		} else {
			mStyle3.setBackgroundResource(R.color.transparent);
		}
		if (v == mStyle4) {
			handLineWidth = mContext.getResources().getDimension(
					R.dimen.hand_draw_pen_sytle_4);
			mStyle4.setBackgroundResource(R.drawable.hand_draw_penstyle_check);
		} else {
			mStyle4.setBackgroundResource(R.color.transparent);
		}
		if (v == mStyle5) {
			handLineWidth = mContext.getResources().getDimension(
					R.dimen.hand_draw_pen_sytle_5);
			mStyle5.setBackgroundResource(R.drawable.hand_draw_penstyle_check);
		} else {
			mStyle5.setBackgroundResource(R.color.transparent);
		}
		if (mStyleChooserListener != null) {
			mStyleChooserListener.setPenStyle(handLineWidth);
		}
	}

}
