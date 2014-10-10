package org.android.loader.core;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;

import com.m.common.utils.BitmapUtil;

public class BitmapCompress implements IBitmapCompress {

	@Override
	public Bitmap compress(byte[] bitmapBytes, File file, String url, ImageConfig config, int origW, int origH) throws Exception {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
			// 文图高度大于需要显示尺寸的高度
			if (config.getMaxHeight() > 0 && origH - config.getMaxHeight() > 0) {
				BitmapRegionDecoder bitmapDecoder = BitmapRegionDecoder.newInstance(bitmapBytes, 0, bitmapBytes.length, true);
				Rect rect = new Rect(0, 0, origW, config.getMaxWidth());
				bitmap = bitmapDecoder.decodeRegion(rect, null);
			}
			// 位图宽度大于需要显示的尺寸或者接近显示的尺寸的宽度
			if (config.getMaxWidth() > 0)
				bitmap = BitmapUtil.zoomBitmap(bitmap, config.getMaxWidth());
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return bitmap;
	}

}
