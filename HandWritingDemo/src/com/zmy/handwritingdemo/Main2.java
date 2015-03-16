package com.zmy.handwritingdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.zmy.handwritingdemo.other.HandDrawListener;
import com.zmy.handwritingdemo.view.HandDrawView2;

public class Main2 extends Activity {

	ImageView mImageView;
	HandDrawView2 mHandDrawView;

	Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);
		mImageView = (ImageView) findViewById(R.id.showBitmap);
		mHandDrawView = (HandDrawView2) findViewById(R.id.handDrawView);
		mHandDrawView.setHandDrawListener(new MyHandDrawListenner(), mHandler);
		//mHandDrawView.setDrawColor(0xFF00FF00);
		//mHandDrawView.setDrawWidth(18);
	}

	class MyHandDrawListenner implements HandDrawListener {

		@Override
		public void onDrawableCommit(final Bitmap bitmap) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mImageView.setImageBitmap(bitmap);
				}
			});

		}

	}
	
	public void start(View v){
		startActivity(new Intent(getApplicationContext(), Main.class));
	}

}
