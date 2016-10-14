package org.aisen.wen.component.bitmaploader.download;

import android.content.Context;

import org.aisen.wen.component.bitmaploader.core.ImageConfig;

public interface Downloader {

	byte[] downloadBitmap(Context context, String url, ImageConfig config) throws Exception;

}
