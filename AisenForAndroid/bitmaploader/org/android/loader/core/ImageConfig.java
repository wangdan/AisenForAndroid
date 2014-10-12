package org.android.loader.core;

import org.android.loader.display.Displayer;
import org.android.loader.display.FadeInDisplayer;
import org.android.loader.download.DownloadProcess;
import org.android.loader.download.Downloader;
import org.android.loader.download.WebDownloader;

import android.view.animation.Animation;

public class ImageConfig {

	private String id;// 图片缓存id,区分相同图片显示在不同地方的时候作用

	private int maxWidth = 0;// 图片最大宽度

	private int maxHeight = 0;// 图片最大高度

	private int cornerSize = 0;// 图片圆角大小

	private Animation animation;// 自定义动画

	private DownloadProcess progress;

	private Class<? extends Downloader> downloaderClass;

	private Class<? extends IBitmapCompress> bitmapCompress;

	private Displayer displayer;

	private String loadingBitmap;

	private String loadfaildBitmap;
	
	private int loadingBitmapRes;
	
	private int loadfaildBitmapRes;
	
	private int pieceHeiht;

	private int piecePosition;

	public ImageConfig() {
		downloaderClass = WebDownloader.class;
		bitmapCompress = BitmapCompress.class;
		displayer = new FadeInDisplayer();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getCornerSize() {
		return cornerSize;
	}

	public void setCornerSize(int cornerSize) {
		this.cornerSize = cornerSize;
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public DownloadProcess getProgress() {
		return progress;
	}

	public void setProgress(DownloadProcess progress) {
		this.progress = progress;
	}

	public Class<? extends Downloader> getDownloaderClass() {
		return downloaderClass;
	}

	public void setDownloaderClass(Class<? extends Downloader> downloaderClass) {
		this.downloaderClass = downloaderClass;
	}

	public Displayer getDisplayer() {
		return displayer;
	}

	public void setDisplayer(Displayer displayer) {
		this.displayer = displayer;
	}

	public String getLoadingBitmap() {
		return loadingBitmap;
	}

	public void setLoadingBitmap(String loadingBitmap) {
		this.loadingBitmap = loadingBitmap;
	}

	public String getLoadfaildBitmap() {
		return loadfaildBitmap;
	}

	public void setLoadfaildBitmap(String loadfaildBitmap) {
		this.loadfaildBitmap = loadfaildBitmap;
	}

	public Class<? extends IBitmapCompress> getBitmapCompress() {
		return bitmapCompress;
	}

	public void setBitmapCompress(Class<? extends IBitmapCompress> bitmapCompress) {
		this.bitmapCompress = bitmapCompress;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	public int getPieceHeiht() {
		return pieceHeiht;
	}

	public void setPieceHeiht(int pieceHeiht) {
		this.pieceHeiht = pieceHeiht;
	}

	public int getPiecePosition() {
		return piecePosition;
	}

	public void setPiecePosition(int piecePosition) {
		this.piecePosition = piecePosition;
	}

	public int getLoadingBitmapRes() {
		return loadingBitmapRes;
	}

	public void setLoadingBitmapRes(int loadingBitmapRes) {
		this.loadingBitmapRes = loadingBitmapRes;
	}

	public int getLoadfaildBitmapRes() {
		return loadfaildBitmapRes;
	}

	public void setLoadfaildBitmapRes(int loadfaildBitmapRes) {
		this.loadfaildBitmapRes = loadfaildBitmapRes;
	}

}
