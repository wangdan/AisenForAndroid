package org.android.loader.download;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.android.loader.core.ImageConfig;

public class SdcardDownloader implements Downloader {

	@Override
	public byte[] downloadBitmap(String url, ImageConfig config) throws Exception {
		try {
			File imgFile = new File(url);
			if (imgFile.exists()) {
				DownloadProcess process = config.getProgress();

				if (process != null)
					process.prepareDownload(url);

				InputStream in = new FileInputStream(new File(url));

				if (process != null)
					process.sendLength(in.available());

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buffer = new byte[8 * 1024];
				int length = -1;
				long readBytes = 0;
				while ((length = in.read(buffer)) != -1) {
					readBytes += length;
					if (process != null)
						process.sendProgress(readBytes);
					out.write(buffer, 0, length);
				}
				out.flush();
				byte[] result = out.toByteArray();
				in.close();
				out.close();

				if (process != null)
					process.sendFinishedDownload(result);

				return result;
			}
			
			if (config.getProgress() != null)
				config.getProgress().downloadFailed(null);
			throw new Exception("");
		} catch (Exception e) {
			if(config.getProgress()!=null)
				config.getProgress().sendException(e);
			throw new Exception(e.getCause());
		}
	}

}
