package com.zmy.handwritingdemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zmy.handwritingdemo.other.HandDrawListener;
import com.zmy.handwritingdemo.view.DrawableEditView;
import com.zmy.handwritingdemo.view.HandDrawPenColorChooserView;
import com.zmy.handwritingdemo.view.HandDrawPenColorChooserView.ColorSlector;
import com.zmy.handwritingdemo.view.HandDrawPenStyleChooserView;
import com.zmy.handwritingdemo.view.HandDrawView2;
import com.zmy.handwritingdemo.view.ImageUtils;

public class HandDrawerActivity extends Activity implements OnClickListener {
	
	public static final String HAND_DRAW_IMAGE_FILE = "hand_draw_bitmap_file";
	
	private Context mContext;
	
	//导航栏
	
	private DrawableEditView mDrawavleEv;
	private HandDrawView2 mHandDrawView;
	private HandDrawPenStyleChooserView mPenStyleChooserView;
	private HandDrawPenColorChooserView mPenColorChooserView;
	private View mHandDrawBottomToolbar;
	
	/**
	 * 底部工具栏
	 */
	private LinearLayout mPenstyleChooser;
	private LinearLayout mPencolorChooser;
	private LinearLayout mTextLineChange;
	private LinearLayout mTextSpace;
	private LinearLayout mTextDelete;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.hand_draw_fragment_layout);
		
		this.mContext = this;
	
		
		mDrawavleEv = (DrawableEditView) findViewById(R.id.hand_draw_et);
		mDrawavleEv.setLongClickable(false);
//		AndroidUtil.hideKeyBoard(this, mDrawavleEv);
		
		mHandDrawView = (HandDrawView2) findViewById(R.id.hand_draw_board);
		
		mHandDrawView.setHandDrawListener(new HandDrawListener() {
			@Override
			public void onDrawableCommit(final Bitmap bitmap) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(bitmap != null) {
							Bitmap tempBitmap = bitmap;
							//图片在EditText中的显示高度
							int w = (int) mContext.getResources().getDimension(R.dimen.hand_draw_iamge_size);
							tempBitmap = bitmapZoom(tempBitmap, w);
							mDrawavleEv.insertHandImage(tempBitmap);
						}
					}
				});
			}
		}, new Handler());
		mHandDrawView.setAutoCommitTime(500);
		//mHandDrawView.setDrawColor(getResources().getColor(R.color.hand_draw_pen_color_1));
		mHandDrawView.setDrawColor(ColorSlector.BLACK);
		mHandDrawView.setDrawWidth(getResources().getDimension(R.dimen.hand_draw_pen_sytle_1));
		
		mPenStyleChooserView = (HandDrawPenStyleChooserView) findViewById(R.id.hand_draw_penstyle_toolbar);
		mPenStyleChooserView
				.setStyleChooserListener(new HandDrawPenStyleChooserView.StyleChooserListener() {
			@Override
			public void setPenStyle(float penStyle) {
				//修改画笔粗细
				mHandDrawView.setDrawWidth(penStyle);
				mPenStyleChooserView.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						mPenStyleChooserView.setVisibility(View.GONE);
						mPenstyleChooser.setBackgroundResource(R.color.transparent);
					}
				}, 300);
			}
		});
		mPenColorChooserView = (HandDrawPenColorChooserView) findViewById(R.id.hand_draw_pencolor_toolbar);
		mPenColorChooserView
				.setColorChooserListener(new HandDrawPenColorChooserView.ColorChooserListener() {
			@Override
			public void setPenColor(int penColor) {
				//修改画笔颜色
				mHandDrawView.setDrawColor(penColor);
				mPenColorChooserView.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						mPenColorChooserView.setVisibility(View.GONE);
						mPencolorChooser.setBackgroundResource(R.color.transparent);
					}
				}, 300);
			}
		});
		initBottomToolBarView();
	}
	
	@Override
	public void onBackPressed() {
		if(mDrawavleEv!=null && !TextUtils.isEmpty(mDrawavleEv.getText().toString().trim())){
			showSaveDialog();
		} else {
			super.onBackPressed();
		}
	}
	
	/**
	 * 是否保存手写内容提示框
	 */
	private void showSaveDialog() {
		saveHandDrawBitmap();
//		String title = mContext.getResources().getString(R.string.hand_draw_save_dialog_title);
//		String content = mContext.getResources().getString(R.string.hand_draw_save_dialog_content);;
//		String okAction = mContext.getResources().getString(R.string.save_action);
//		String noAction = mContext.getResources().getString(R.string.give_up_ation);
//		CustomDialogHelper.showTextViewDialog(mContext, title, content, okAction, noAction, new ButtonOnclickListener() {
//			
//			@Override
//			public void yesClicked(TextView et, Dialog dialog) {
//				saveHandDrawBitmap();
//			}
//			
//			@Override
//			public void cancelClicked(TextView et) {
//				HandDrawerActivity.this.finish();
//			}
//		});
	}
	
	/**
	 * 初始化底部工具栏
	 */
	private void initBottomToolBarView() {
		mHandDrawBottomToolbar = findViewById(R.id.hand_draw_toolbar_layout);
		mPenstyleChooser = (LinearLayout) findViewById(R.id.hand_draw_penstyle_chooser_view);
		mPenstyleChooser.setOnClickListener(this);
		mPencolorChooser = (LinearLayout) findViewById(R.id.hand_draw_pencolor_chooser_view);
		mPencolorChooser.setOnClickListener(this);
		mTextLineChange = (LinearLayout) findViewById(R.id.hand_draw_text_line_change_view);
		mTextLineChange.setOnClickListener(this);
		mTextSpace = (LinearLayout) findViewById(R.id.hand_draw_text_space_view);
		mTextSpace.setOnClickListener(this);
		mTextDelete = (LinearLayout) findViewById(R.id.hand_draw_text_delete_view);
		mTextDelete.setOnClickListener(this);
	}

	/**
	 * 画笔大小选择栏显示控制
	 */
	private void togglePenStyleBar() {
		mPenColorChooserView.setVisibility(View.GONE);
		mPencolorChooser.setBackgroundResource(R.color.transparent);
		if(mPenStyleChooserView.isShowing()) {
			mPenStyleChooserView.setVisibility(View.GONE);
			mPenstyleChooser.setBackgroundResource(R.color.transparent);
		} else {
			mPenStyleChooserView.setVisibility(View.VISIBLE);
			mPenstyleChooser.setBackgroundResource(R.color.hand_draw_toolbar_btn_press);
		}
	}
	
	/**
	 * 画笔颜色选择栏显示控制
	 */
	private void togglePenColorBar() {
		mPenStyleChooserView.setVisibility(View.GONE);
		mPenstyleChooser.setBackgroundResource(R.color.transparent);
		if(mPenColorChooserView.isShowing()) {
			mPenColorChooserView.setVisibility(View.GONE);
			mPencolorChooser.setBackgroundResource(R.color.transparent);
		} else {
			mPenColorChooserView.setVisibility(View.VISIBLE);
			mPencolorChooser.setBackgroundResource(R.color.hand_draw_toolbar_btn_press);
		}
	}
	
	/**
	 * 手写图片缩放
	 * @param bmp
	 * @param height
	 * @return
	 */
	private Bitmap bitmapZoom(Bitmap bmp, int height) {
        int bmpWidth = bmp.getWidth();      
        int bmpHeght = bmp.getHeight();      
        Matrix matrix = new Matrix();      
        float f= (float) height / bmpHeght;
        matrix.postScale(f, f);
        return ImageUtils.createScaledBitmap(bmp, (int)(bmpWidth*f), (int)(bmpHeght*f), ScaleType.FIT_XY);
//        return Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeght, matrix, true);
//        return Bitmap.createScaledBitmap(bmp, (int)(bmpWidth*f), (int)(bmpHeght*f), true);
    }
	
	/**
	 * 保存手写内容
	 */
	private void saveHandDrawBitmap() {
		//该方法只能截取屏幕显示的内容
//		boolean drawCached = mDrawavleEv.isDrawingCacheEnabled();
//		if(!drawCached) {
//			mDrawavleEv.setDrawingCacheEnabled(true);
//			mDrawavleEv.buildDrawingCache();
//		}
//		Bitmap bit = mDrawavleEv.getDrawingCache();
		
		mDrawavleEv.setFocusable(false);
		int top = 0;
		int specWidth = MeasureSpec.makeMeasureSpec(mDrawavleEv.getWidth(), MeasureSpec.AT_MOST);
        int specHeight = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        mDrawavleEv.measure(specWidth, specHeight);
        Bitmap bit = Bitmap.createBitmap(mDrawavleEv.getMeasuredWidth(), mDrawavleEv.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bit);
        mDrawavleEv.layout(0, top, mDrawavleEv.getMeasuredWidth(), top + mDrawavleEv.getMeasuredHeight());
        mDrawavleEv.draw(canvas);
		
		if(bit != null) {
			String time = String.valueOf(System.currentTimeMillis());
			File filePath = new File("sdcard");//StorageConfig.getHandDrawDir();
			File file = new File(filePath, "handdraw_" + time + ".jpg");
			try {
				FileOutputStream fos = new FileOutputStream(file);
				bit.compress(CompressFormat.PNG, 100, fos);
				fos.flush();
	   			fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			Intent i = new Intent();
			i.putExtra(HAND_DRAW_IMAGE_FILE, file.getPath());
			setResult(RESULT_OK, i);
			HandDrawerActivity.this.finish();
		}
	}

	@Override
	public void onClick(View v) {
		if(v == mPenstyleChooser) {
			togglePenStyleBar();
		} else if(v == mPencolorChooser) {
			togglePenColorBar();
		} else if(v == mTextLineChange) {
			mDrawavleEv.newLine();
		} else if(v == mTextSpace) {
			//生成空白Bitmap插入
			int w = (int) mContext.getResources().getDimension(R.dimen.hand_draw_blank_width);
			int h = (int) mContext.getResources().getDimension(R.dimen.hand_draw_iamge_size);
		    Bitmap emptyBit = Bitmap.createBitmap(w,h, Config.ARGB_8888);
			mDrawavleEv.insertHandImage(emptyBit);
		} else if(v == mTextDelete) {
			mDrawavleEv.deleteHandImage();
		}
	}
	
}
