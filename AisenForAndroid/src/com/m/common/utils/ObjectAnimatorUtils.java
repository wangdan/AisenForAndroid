package com.m.common.utils;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

public class ObjectAnimatorUtils {

	/**
	 * 动态改变一个View的属性值
	 * 
	 * @param view
	 * @param attr
	 * @param value
	 */
	public static void changeAttrValue(final View view, String attr, int value, int duration, final OnAttrChangedCallback callback) {
		ObjectAnimator oa = null;
		oa = ObjectAnimator.ofPropertyValuesHolder(view, PropertyValuesHolder.ofInt(attr, value));
		oa.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int value = (Integer) animation.getAnimatedValue();
				ViewGroup.LayoutParams params = callback.onAttrValueChanged((ViewGroup.LayoutParams) view.getLayoutParams(), value);
//				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
//				params.height = height;
//				Logger.e(height);
				view.setLayoutParams(params);
			}
		});
		oa.setDuration(duration);
		oa.start();
	}

	public interface OnAttrChangedCallback {

		public ViewGroup.LayoutParams onAttrValueChanged(ViewGroup.LayoutParams params, int value);

	}

}
