package com.m.component.bitmaploader.download;

import com.m.component.bitmaploader.core.ImageConfig;

public interface Downloader {

	public byte[] downloadBitmap(String url, ImageConfig config) throws Exception;

}
