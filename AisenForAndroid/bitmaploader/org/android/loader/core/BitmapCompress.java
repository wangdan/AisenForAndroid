package org.android.loader.core;

import java.io.File;

import org.android.loader.BitmapLoader;

import com.m.common.utils.Logger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapCompress implements IBitmapCompress {

	@Override
	public Bitmap compress(byte[] bitmapBytes, File file, String url, ImageConfig config, int origW, int origH) throws Exception {
		Bitmap bitmap = null;
		try {
			if (config.getMaxHeight() > 0 && config.getMaxWidth() > 0) {
				bitmap = BitmapDecoder.decodeSampledBitmapFromByte(bitmapBytes, config.getMaxWidth(), config.getMaxHeight());
			}
			else if (config.getMaxHeight() > 0) {
				bitmap = BitmapDecoder.decodeSampledBitmapFromByte(bitmapBytes, config.getMaxHeight(), config.getMaxHeight());
			}
			else if (config.getMaxWidth() > 0) {
				bitmap = BitmapDecoder.decodeSampledBitmapFromByte(bitmapBytes, config.getMaxWidth(), config.getMaxWidth());
			}
			else {
				bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);	
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		
		Logger.d(BitmapLoader.TAG, String.format("原始尺寸是%dX%d, 压缩后尺寸是%dX%d", origW, origH, bitmap.getWidth(), bitmap.getHeight()));
		
		return bitmap;
	}

}
