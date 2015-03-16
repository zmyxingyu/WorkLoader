package com.zmy.handwritingdemo.view;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.widget.ImageView.ScaleType;

/**
 * 
 * @author zhangmy
 * 
 */
public class ImageUtils {

	private static final Map<Integer, Bitmap> BITMAP_CACHE = new HashMap();

	public static Bitmap decodeResourcesBitmap(Context context, int res) {
		boolean bool = BITMAP_CACHE.containsKey(Integer.valueOf(res));
		Bitmap localBitmap = null;
		if (bool) {
			localBitmap = (Bitmap) BITMAP_CACHE.get(Integer.valueOf(res));
		}
		if ((localBitmap == null) || (localBitmap.isRecycled())) {
			localBitmap = BitmapFactory.decodeStream(context.getResources()
					.openRawResource(res));
			BITMAP_CACHE.put(Integer.valueOf(res), localBitmap);
		}
		return localBitmap;
	}

	/**
	 * 压缩图片
	 * 
	 * @param unscaledBitmap
	 * @param dstWidth
	 * @param dstHeight
	 * @param scalingLogic
	 * @return
	 */
	public static Bitmap createScaledBitmap(Bitmap unscaledBitmap,
			int dstWidth, int dstHeight, ScaleType scalingLogic) {
		Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(),
				unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
		Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(),
				unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
		Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(),
				dstRect.height(), Config.ARGB_8888);
		Canvas canvas = new Canvas(scaledBitmap);
		canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(
				Paint.FILTER_BITMAP_FLAG));
		return scaledBitmap;
	}

	public static Rect calculateSrcRect(int srcWidth, int srcHeight,
			int dstWidth, int dstHeight, ScaleType scalingLogic) {
		if (scalingLogic == ScaleType.CENTER_CROP) {
			final float srcAspect = (float) srcWidth / (float) srcHeight;
			final float dstAspect = (float) dstWidth / (float) dstHeight;
			if (srcAspect > dstAspect) {
				final int srcRectWidth = (int) (srcHeight * dstAspect);
				final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
				return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth,
						srcHeight);
			} else {
				final int srcRectHeight = (int) (srcWidth / dstAspect);
				final int scrRectTop = (int) (srcHeight - srcRectHeight) / 2;
				return new Rect(0, scrRectTop, srcWidth, scrRectTop
						+ srcRectHeight);
			}
		} else {
			return new Rect(0, 0, srcWidth, srcHeight);
		}
	}

	public static Rect calculateDstRect(int srcWidth, int srcHeight,
			int dstWidth, int dstHeight, ScaleType scalingLogic) {
		if (scalingLogic == ScaleType.FIT_XY) {
			final float srcAspect = (float) srcWidth / (float) srcHeight;
			final float dstAspect = (float) dstWidth / (float) dstHeight;
			if (srcAspect > dstAspect) {
				return new Rect(0, 0, dstWidth, (int) (dstWidth / srcAspect));
			} else {
				return new Rect(0, 0, (int) (dstHeight * srcAspect), dstHeight);
			}
		} else {
			return new Rect(0, 0, dstWidth, dstHeight);
		}
	}

}
