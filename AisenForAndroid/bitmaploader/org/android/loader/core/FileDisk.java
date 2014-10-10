package org.android.loader.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.text.TextUtils;

import com.m.common.settings.SettingUtility;
import com.m.common.utils.Logger;

public class FileDisk {

	private String filePath;

	private final String IMG_SUFFIX;

	public FileDisk(String filePath) {
		if (!TextUtils.isEmpty(SettingUtility.getStringSetting("image_suffix")))
			IMG_SUFFIX = SettingUtility.getStringSetting("image_suffix");
		else
			IMG_SUFFIX = "is";
		File file = new File(filePath);
		if (!file.exists())
			file.mkdirs();

		this.filePath = filePath;
	}

	public void writeOutStream(byte[] datas, String url, String key) throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream(datas);
		File file = new File(filePath + File.separator + key + "." + getImageSuffix(url));
		if (file.getParentFile().exists())
			file.getParentFile().mkdirs();
		FileOutputStream out = new FileOutputStream(file);
		byte[] buffer = new byte[64 * 1024];
		int len = -1;
		while ((len = in.read(buffer)) != -1) {
			out.write(buffer, 0, len);
		}
		out.flush();
		out.close();
		in.close();
	}

	public File getFile(String url, String key) {
		return new File(filePath + File.separator + key + "." + getImageSuffix(url));
	}

	public FileInputStream getInputStream(String url, String key) throws Exception {
		File file = getFile(url, key);

		if (file.exists()) {
			if (file.length() == 0) {
				file.delete();
				Logger.w("文件已损坏，url = " + url);
				
				return null;
			}
			
			return new FileInputStream(file);
		}
		else
			Logger.d("getInputStream(String key) not exist");

		return null;
	}

	public OutputStream getOutputStream(String url, String key) throws Exception {
		
		Logger.d("getOutputStream(String key)" + filePath + File.separator + key + "." + getImageSuffix(url) + ".temp");

		return new FileOutputStream(filePath + File.separator + key + "." + getImageSuffix(url) + ".temp");
	}

	public void deleteFile(String url, String key) {
		
		File file = new File(filePath + File.separator + key + "." + getImageSuffix(url));
		if (file.exists())
			file.delete();
	}

	public void renameFile(String url, String key) {
		
		File file = new File(filePath + File.separator + key + "." + getImageSuffix(url) + ".temp");
		File newFile = new File(filePath + File.separator + key + "." + getImageSuffix(url));
		if (file.exists() && file.length() != 0)
			file.renameTo(newFile);
	}
	
	private String getImageSuffix(String url) {
		return getImageSuffix(url, IMG_SUFFIX);
	}
	
	public static String getImageSuffix(String url, String suffix) {
		if ("auto".equals(suffix)) {
			try {
				String temp = url;
				temp = temp.toLowerCase();
				if (temp.endsWith(".gif") || temp.endsWith(".jpg") || temp.endsWith(".jpeg")
						|| temp.endsWith(".bmp") || temp.endsWith(".png")) {
					return url.substring(url.lastIndexOf(".") + 1, url.length());
				}
				else {
					return "jpg";
				}
			} catch (Exception e) {
			}
		}
		
		return suffix;
	}
	
}
