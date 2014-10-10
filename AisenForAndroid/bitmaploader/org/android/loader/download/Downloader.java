package org.android.loader.download;

import org.android.loader.core.ImageConfig;

public interface Downloader {

	public byte[] downloadBitmap(String url, ImageConfig config) throws Exception;

}
