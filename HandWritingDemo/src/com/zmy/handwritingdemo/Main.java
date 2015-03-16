package com.zmy.handwritingdemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.zmy.handwritingdemo.other.HandDrawListener;
import com.zmy.handwritingdemo.view.HandDrawView;

public class Main extends Activity {

	ImageView mImageView;
	HandDrawView mHandDrawView;

	Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mImageView = (ImageView) findViewById(R.id.showBitmap);
		mHandDrawView = (HandDrawView) findViewById(R.id.handDrawView);
		mHandDrawView.setHandDrawListener(new MyHandDrawListenner(), mHandler);
		mHandDrawView.setDrawColor(0xFF00FF00);
		mHandDrawView.setDrawWidth(18);
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
		
			//saveBitmap(bitmap);
		}

	}

	/** 将图片存入文件缓存 **/
	public void saveBitmap(Bitmap bm) {
		if (bm == null) {
			return;
		}
		String filename = Math.random() + ".png";

		File file = new File(getAvatarDir(), filename);
		try {
			file.createNewFile();
			OutputStream outStream = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
			outStream.flush();
			outStream.close();
		} catch (FileNotFoundException e) {
			Log.w("ImageFileCache", "FileNotFoundException");
		} catch (IOException e) {
			Log.w("ImageFileCache", "IOException");
		}
	}

	public File getAvatarDir() {
		File mRootDir = Environment.getExternalStorageDirectory();
		File avatarDir = new File(mRootDir, getPackageName());
		if (!avatarDir.exists()) {
			avatarDir.mkdirs();
		}
		return avatarDir;
	}

}
