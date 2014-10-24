package org.android.loader.core;

import java.io.File;

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
		return bitmap;
	}

}
