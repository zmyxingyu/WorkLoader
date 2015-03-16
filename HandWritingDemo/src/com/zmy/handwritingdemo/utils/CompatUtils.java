package com.zmy.handwritingdemo.utils;

import android.view.MotionEvent;

public class CompatUtils {
	public static int getActionMasked(MotionEvent paramMotionEvent) {
		return 0xFF & paramMotionEvent.getAction();
	}
}
