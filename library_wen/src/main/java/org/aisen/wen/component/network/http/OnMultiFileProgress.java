package org.aisen.wen.component.network.http;

/**
 * 上传文件的进度回到
 *
 * Created by wangdan on 16/8/29.
 */
public interface OnMultiFileProgress {

    void onProgress(long progress, long total);

}
