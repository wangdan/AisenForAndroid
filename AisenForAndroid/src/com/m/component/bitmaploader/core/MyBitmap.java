package com.m.component.bitmaploader.core;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;

import com.m.common.context.GlobalContext;
import com.m.common.utils.Logger;

public class MyBitmap {

	static final String TAG = "MyBitmap";
	
	static int createdCount = 0;
	
	private String id;

	private String url;

	private Bitmap bitmap;
	
	private static Map<String, WeakReference<Bitmap>> cacheMap;
	
	static {
		cacheMap = new HashMap<String, WeakReference<Bitmap>>();
	}
	
	static Bitmap getCacheBitmap(int resId) {
		String key = String.valueOf(resId);
		Bitmap bitmap = null;
		
		if (cacheMap.containsKey(key)) {
			bitmap = cacheMap.get(key).get();
		}

		if (bitmap == null) {
			try {
				bitmap = BitmapFactory.decodeResource(GlobalContext.getInstance().getResources(), resId);
				cacheMap.put(key, new WeakReference<Bitmap>(bitmap));
			} catch (Error e) {
				return Bitmap.createBitmap(0, 0, Config.ARGB_4444);
			}
		}
		
		return bitmap;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		createdCount--;
		Logger.v(TAG, createdCount + "");
	}

    public MyBitmap(int resId) {
        this.bitmap = getCacheBitmap(resId);
    }
	
	public MyBitmap(int resId, String url) {
		this.bitmap = getCacheBitmap(resId);
        this.url = url;
	}

	public MyBitmap(Bitmap bitmap, String url) {
		this.url = url;
		this.bitmap = bitmap;
		
		createdCount++;
		Logger.v(TAG, createdCount + "");
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
