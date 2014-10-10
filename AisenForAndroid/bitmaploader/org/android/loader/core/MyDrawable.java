package org.android.loader.core;

import java.lang.ref.WeakReference;

import org.android.loader.BitmapLoader;
import org.android.loader.BitmapLoader.MyBitmapLoaderTask;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.m.common.utils.BitmapUtil.BitmapType;
import com.m.common.utils.Logger;

public class MyDrawable extends BitmapDrawable {

	private BitmapType type;

	private String imageUrl;

	private WeakReference<MyBitmapLoaderTask> task;

	public MyDrawable(Resources res, Bitmap bitmap, BitmapType type,
			String imageUrl, MyBitmapLoaderTask task) {
		super(res, bitmap);
		this.type = type;
		this.imageUrl = imageUrl;
		this.task = new WeakReference<BitmapLoader.MyBitmapLoaderTask>(
				task);
		count++;
		Logger.d(MyDrawable.class.getSimpleName(), count + "");
	}

	@Override
	protected void finalize() throws Throwable {
		count--;
		Logger.d(MyDrawable.class.getSimpleName(), count + "");
		super.finalize();
	}

	private static int count = 0;

	public BitmapType getType() {
		return type;
	}

	public void setType(BitmapType type) {
		this.type = type;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public WeakReference<MyBitmapLoaderTask> getTask() {
		return task;
	}

	public void setTask(WeakReference<MyBitmapLoaderTask> task) {
		this.task = task;
	}

}
