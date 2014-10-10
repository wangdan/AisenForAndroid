package com.m.ui.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.m.R;
import com.m.common.context.GlobalContext;
import com.m.common.utils.SystemUtility;

public class ViewUtils {

	public static int getResId(String resName, String defType) {
		try {
			String packageName = GlobalContext.getInstance().getPackageName();
			Resources resources = GlobalContext.getInstance().getPackageManager().getResourcesForApplication(packageName);

			int resId = resources.getIdentifier(resName, defType, packageName);

			return resId;
		} catch (Exception e) {
		}
		return 0;
	}

	public static int getStringResId(String resName) {
		return getResId(resName, "string");
	}

	public static int getDrawableResId(String resName) {
		return getResId(resName, "drawable");
	}

	public static void setTextViewValue(Activity context, int txtId, String content) {
		if (context != null)
			((TextView) context.findViewById(txtId)).setText(content);
	}

	public static void setTextViewValue(View container, int txtId, String content) {
		((TextView) container.findViewById(txtId)).setText(content);
	}

	public static void setTextViewValue(Activity context, View container, int txtId, int contentId) {
		if (context != null)
			((TextView) container.findViewById(txtId)).setText(context.getString(contentId));
	}

	public static void setImgResource(Activity context, int imgId, int sourceId) {
		if (context != null)
			((ImageView) context.findViewById(imgId)).setImageResource(sourceId);
	}

	public static void setImgResource(Activity context, int imgId, Bitmap source) {
		if (context != null)
			((ImageView) context.findViewById(imgId)).setImageBitmap(source);
	}

	public static void setImgResource(View container, int imgId, int sourceId) {
		((ImageView) container.findViewById(imgId)).setImageResource(sourceId);
	}

	public static void setImgResource(View container, int imgId, Bitmap source) {
		((ImageView) container.findViewById(imgId)).setImageBitmap(source);
	}

	public static void showMessage(String message) {
		MToast.showMessage(message);
	}

	public static void showMessage(int messageId) {
		MToast.showMessage(messageId);
	}

	public static <T> ArrayList<T[]> transToArray(int pagerSize, List<T> datas) {

		ArrayList<T[]> productList = new ArrayList<T[]>();

		int count = datas.size() / pagerSize + ((datas.size() % pagerSize) == 0 ? 0 : 1);
		for (int i = 0; i < count; i++) {
			Object[] ps = new Object[pagerSize];
			for (int j = 0; j < pagerSize; j++) {
				int dataPosition = i * pagerSize + j;
				if (dataPosition < datas.size()) {
					ps[j] = datas.get(dataPosition);
				}
			}
			productList.add((T[]) ps);
		}

		return productList;
	}
	
	public static ProgressDialog progressDialog;
	public static ProgressDialog createProcessingDialog(Context context, String msg){
		dismissDialog();
		
		progressDialog = new ProgressDialog(context);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(false);
		progressDialog.show();
		progressDialog.setContentView(R.layout.progress_layout);
		
		WindowManager.LayoutParams params = progressDialog.getWindow().getAttributes();
		progressDialog.getWindow().setLayout((int) Math.round(SystemUtility.getScreenWidth() * 0.7f), 
				(int) Math.round(SystemUtility.getScreenWidth() * 0.7f * 0.5f));
		progressDialog.getWindow().setAttributes(params);
		if(msg != null){
			TextView tv = (TextView) progressDialog.findViewById(R.id.progressMsg);
			tv.setText(msg);
			LinearLayout lo = (LinearLayout) progressDialog.findViewById(R.id.progressBg);
			lo.setBackgroundColor(context.getResources().getColor(R.color.white));
		}
		return progressDialog;
	}

	public static void dismissDialog(){
		if(progressDialog != null && progressDialog.isShowing()){
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
	
	public static ProgressDialog progressDialog2;
	
	public static ProgressDialog createNormalProgressDialog(Activity context, String message) {
		dismissNormalProgressDialog();
		
		progressDialog2 = new ProgressDialog(context);
		progressDialog2.setMessage(message);
		progressDialog2.setIndeterminate(true);
		progressDialog2.setCancelable(false);
		
		return progressDialog2;
	}
	
	public static void updateNormalProgressDialog(String message) {
		if (progressDialog2 != null && progressDialog2.isShowing()) {
			progressDialog2.setMessage(message);
		}
	}
	
	public static void dismissNormalProgressDialog() {
		if (progressDialog2 != null && progressDialog2.isShowing()) {
			progressDialog2.dismiss();
			progressDialog2 = null;
		}
	}

}
