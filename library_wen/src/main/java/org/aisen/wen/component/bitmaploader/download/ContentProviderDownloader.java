package org.aisen.wen.component.bitmaploader.download;

import android.content.Context;
import android.net.Uri;

import org.aisen.wen.component.bitmaploader.core.ImageConfig;
import org.aisen.wen.support.utils.FileUtils;

import java.io.InputStream;

public class ContentProviderDownloader implements Downloader {

	@Override
	public byte[] downloadBitmap(Context context, String url, ImageConfig config) throws Exception {
		
		try {
			InputStream is = context.getContentResolver().openInputStream(Uri.parse(url));
			byte[] datas = FileUtils.readStreamToBytes(is);
			return datas;
		} catch (Exception e) {
			if (config.getProgress() != null)
				config.getProgress().downloadFailed(e);
			e.printStackTrace();
			throw e;
		}
	}

}
