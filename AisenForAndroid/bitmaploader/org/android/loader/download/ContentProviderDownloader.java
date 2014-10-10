package org.android.loader.download;

import java.io.InputStream;

import org.android.loader.core.ImageConfig;

import android.net.Uri;

import com.m.common.context.GlobalContext;
import com.m.common.utils.FileUtility;

public class ContentProviderDownloader implements Downloader {

	@Override
	public byte[] downloadBitmap(String url, ImageConfig config) throws Exception {
		
		try {
			InputStream is = GlobalContext.getInstance().getContentResolver().openInputStream(Uri.parse(url));
			byte[] datas = FileUtility.readStreamToBytes(is);
			return datas;
		} catch (Exception e) {
			if (config.getProgress() != null)
				config.getProgress().downloadFailed(e);
			e.printStackTrace();
			throw e;
		}
	}

}
