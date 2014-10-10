package org.android.loader.core;

import android.graphics.Bitmap;

import com.m.common.utils.BitmapUtil.BitmapType;
import com.m.common.utils.Logger;

public class MyBitmap {

	private String id;

	private String url;

	private Bitmap bitmap;

	private BitmapType bitmapType;

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		createdCount--;
		Logger.v(MyBitmap.class.getSimpleName(), createdCount + "");
	}

	private static int createdCount = 0;

	public MyBitmap(Bitmap bitmap, BitmapType type, String url) {
		createdCount++;
		Logger.v(MyBitmap.class.getSimpleName(), createdCount + "");
		this.url = url;
		this.bitmap = bitmap;
		this.bitmapType = (type == null ? BitmapType.jpg : type);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public BitmapType getBitmapType() {
		return bitmapType;
	}

	public void setBitmapType(BitmapType bitmapType) {
		this.bitmapType = bitmapType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
