package com.zmy.handwritingdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.EditText;

import com.zmy.handwritingdemo.R;

public class DrawableEditView extends EditText {
	
	private Context mContext;
	
	private static final int MAX_LINES = 20;	//允许输入的最大行数
	
	public DrawableEditView(Context context) {
		super(context);
		this.mContext = context;
	}

	public DrawableEditView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}
	
	public DrawableEditView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}
	
	/**
	 * 插入手写内容
	 */
	public void insertHandImage(Bitmap handImage) {
		
//		String bitStr = "1";
//		SpannableString ss = new SpannableString(bitStr);  
//		ImageSpan span = new ImageSpan(mContext, handImage);
//		ss.setSpan(span, 0,bitStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		SpannableString ss = createSpannable("I", handImage);
		
		Editable et = getText();
		int start = getSelectionStart();
		et.insert(start, ss);
		setText(et);
		setSelection(start + ss.length());
		
		if(getLineCount() > MAX_LINES) {
			deleteHandImage();
//			String toastMsg = mContext.getResources().getString(R.string.hand_draw_max_lines, MAX_LINES);
//			AndroidUtil.showShortToast(mContext, toastMsg);
		}
	}
	
	/**
	 * 删除手写内容
	 */
	public void deleteHandImage() {
		int index = getSelectionStart();
		if(index==0)return;
		Editable editable = getText(); 
		editable.delete(index-1, index);
	}
	
	/**
	 * 换行
	 */
	public void newLine() {
		if(getLineCount() >= MAX_LINES) {
//			String toastMsg = mContext.getResources().getString(R.string.hand_draw_max_lines, MAX_LINES);
//			AndroidUtil.showShortToast(mContext, toastMsg);
			return;
		}
		int index = getSelectionStart();
		Editable editable = getText();
		editable.insert(index, "\n");
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//不支持非返回键的其它键盘输入
		if(keyCode == KeyEvent.KEYCODE_BACK){
			return super.onKeyDown(keyCode, event);
		} else {
			return true;
		}
	}
	
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean willPopupPasteOption = false;
        if(event.getAction() == MotionEvent.ACTION_UP) {
            willPopupPasteOption = isEnabled();
        }
        boolean flag = super.onTouchEvent(event);
        // FIXME: We do this trick to prevent the paste option to popup.
        if(willPopupPasteOption) {
            setEnabled(false);
            setEnabled(true);
        }
        return flag;
    }
    
    @Override
    public Parcelable onSaveInstanceState() {
    	return super.onSaveInstanceState();
    }
    
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    private SpannableString createSpannable(String seq, Bitmap bitmap) {
        SpannableString ss = new SpannableString(seq);
        ImageSpan span = new ImageSpan(mContext, bitmap);
        ss.setSpan(span, 0,seq.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return ss;
    }
    
    static class SpanEditableFactory extends Editable.Factory {
    	private HandDrawSaveState mState;
    	public void setRestoreState(HandDrawSaveState state) {
    		mState = state;
    	}

		@Override
		public Editable newEditable(CharSequence source) {
			Editable editable = super.newEditable(source);
			if(mState != null) {
				mState.restore(editable);
			}
			return editable;
		}
    	
    }
    
    static class HandDrawSaveState implements Parcelable {
    	private Parcelable mData;
    	private SpanData [] mSpanDatas;
    	public HandDrawSaveState(Editable editable, Parcelable data) {
    		mData = data;
    		Object [] spans = editable.getSpans(0, editable.length(), ImageSpan.class);
			SpanData [] spanDatas = new SpanData[0];
			int num = 0;
			if(spans != null) {
				num = spans.length;
				spanDatas = new SpanData[num];
				int i = 0;
				SpanData spData = null;
				for(Object obj : spans) {
					ImageSpan imageSpan = (ImageSpan)obj;
					BitmapDrawable drawable = (BitmapDrawable)imageSpan.getDrawable();
					spData = new SpanData(editable.getSpanStart(obj), editable.getSpanEnd(obj), drawable.getBitmap());
					spanDatas[i] = spData;
					++i;
				}
			}
			mSpanDatas = spanDatas;
    		
    	}
    	static class SpanData implements Parcelable {
    		public SpanData(int st, int ed, Bitmap bm) {
    			start = st;
    			end = ed;
    			bitmap = bm;
    		}
    		public int start;
    		public int end;
    		public Bitmap bitmap;
			@Override
			public int describeContents() {
				return 0;
			}
			@Override
			public void writeToParcel(Parcel dest, int flags) {
				dest.writeInt(start);
				dest.writeInt(end);
				dest.writeParcelable(bitmap, 0);
				
			}
			public static final Parcelable.Creator<SpanData> CREATOR
			= new Parcelable.Creator<SpanData>() {
				public SpanData createFromParcel(Parcel in) {
					return new SpanData(in);
				}

				public SpanData[] newArray(int size) {
					return new SpanData[size];
				}
			};

			private SpanData(Parcel in) {
				start = in.readInt();
				end = in.readInt();
				bitmap = in.readParcelable(null);
				Log.d("Test", "Restore bitmap: w=" + bitmap.getWidth() + " h=" + bitmap.getHeight());
			}
    		
    	}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeParcelable(mData, 0);
			dest.writeInt(mSpanDatas.length);
			dest.writeParcelableArray(mSpanDatas, 0);
		}
		
		public void restore(Editable editable) {
//			editable.clearSpans();
			for(SpanData spanData : mSpanDatas) {
				editable.setSpan(new ImageSpan(spanData.bitmap), spanData.start, spanData.end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			}
		}
		public Parcelable getOrgData() {
			return mData;
		}
		
		public static final Parcelable.Creator<HandDrawSaveState> CREATOR
		= new Parcelable.Creator<HandDrawSaveState>() {
			public HandDrawSaveState createFromParcel(Parcel in) {
				return new HandDrawSaveState(in);
			}

			public HandDrawSaveState[] newArray(int size) {
				return new HandDrawSaveState[size];
			}
		};

		private HandDrawSaveState(Parcel in) {
			mData = in.readParcelable(null);
			int num = in.readInt();
			if(num > 0) {
				Object [] objs = in.readParcelableArray(SpanData.class.getClassLoader());
				if(objs != null) {
					mSpanDatas = new SpanData [objs.length];
					int i = 0;
					SpanData spanData = null;
					for(Object obj : objs) {
						spanData = (SpanData)obj;
						mSpanDatas[i] = spanData;
						++i;
					}
				} else {
					mSpanDatas = new SpanData[0];
				}
			}
		}
    	
    }
}
