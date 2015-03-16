package com.zmy.handwritingdemo.utils;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageUtils {
	private static final String ASSETS_KEY_PREFIX = "assets_img_";
	private static final String DB_THUMBNAIL_KEY_PREFIX = "dbthu";
	private static Bitmap DEFALTBITMAP;
	private static Bitmap DEFALTBITMAP_SMALL;
	private static Bitmap DEFALT_USER_HEAD_BITMAP;
	private static Bitmap DEFALT_USER_HEAD_SQUARE_BITMAP;
	public static final Bitmap EMPTY_BITMAP = Bitmap.createBitmap(20, 20,
			Bitmap.Config.ARGB_8888);
	private static final int HANDWRITE_SNIPPET_HEIGHT = 245;
	private static final int HANDWRITE_SNIPPET_WIDTH = 490;
	public static final int HIGH_SIZE = 1200;
	public static final int LOW_SIZE = 640;
	public static final int MAX_SIZE = 1600;
	public static final int NOMAL_SIZE = 800;
	public static final int QUALITY_HIGH = 3;
	public static final int QUALITY_LOW = 1;
	public static final int QUALITY_NORMAL = 2;
	public static final int QUALITY_ORIGINAL = 4;
	private static final Map<Integer, Bitmap> RESOURCES_BITMAP_CACHE = new HashMap();
	private static final int SNIPPET_MAX_SIZE = 90;
	private static final int THUMBNAIL_SIZE = 480;
	private static final String URI_BITMAP_KEY_SEPATOR = "-imgq-";
	private static final String sThreeChar = "...";

	public static Bitmap decodeResourcesBitmap(Context paramContext,
			int paramInt) {
		boolean bool = RESOURCES_BITMAP_CACHE.containsKey(Integer
				.valueOf(paramInt));
		Bitmap localBitmap = null;
		if (bool) {
			localBitmap = (Bitmap) RESOURCES_BITMAP_CACHE.get(Integer
					.valueOf(paramInt));
		}
		if ((localBitmap == null) || (localBitmap.isRecycled())) {
			localBitmap = BitmapFactory.decodeStream(paramContext
					.getResources().openRawResource(paramInt));
			RESOURCES_BITMAP_CACHE.put(Integer.valueOf(paramInt), localBitmap);
		}
		return localBitmap;
	}
}
