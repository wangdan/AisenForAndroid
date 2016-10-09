package org.aisen.wen.support.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * Created by wangdan on 16/10/8.
 */
public class ViewUtils {

    public static void showMessage(Context context, String message) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showMessage(Context context, int messageId) {
        if (context != null) {
            Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show();
        }
    }

    public static ProgressDialog progressDialog2;

    public static ProgressDialog createProgressDialog(Activity context, String message, int widgetColor) {
        dismissProgressDialog();
        //Theme.Material.Dialog.Alert
        progressDialog2 = new MProgressDialog(context, widgetColor);
        progressDialog2.setMessage(message);
        progressDialog2.setIndeterminate(true);
        progressDialog2.setCancelable(false);

        return progressDialog2;
    }

    public static void updateProgressDialog(String message) {
        if (progressDialog2 != null && progressDialog2.isShowing()) {
            progressDialog2.setMessage(message);
        }
    }

    public static void dismissProgressDialog() {
        if (progressDialog2 != null && progressDialog2.isShowing()) {
            try {
                progressDialog2.dismiss();
            } catch (IllegalArgumentException e) {
            }
            progressDialog2 = null;
        }
    }

    public static class MProgressDialog extends ProgressDialog {

        private int color;

        public MProgressDialog(Context context, int color) {
            super(context);

            this.color = color;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (color != 0) {
                try {
                    Field progressBarField = MProgressDialog.class.getSuperclass().getDeclaredField("mProgress");
                    progressBarField.setAccessible(true);
                    ProgressBar progressBar = (ProgressBar) progressBarField.get(this);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ColorStateList stateList = ColorStateList.valueOf(color);
                        progressBar.setProgressTintList(stateList);
                        progressBar.setSecondaryProgressTintList(stateList);
                        progressBar.setIndeterminateTintList(stateList);
                    } else {
                        PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
                            mode = PorterDuff.Mode.MULTIPLY;
                        }
                        if (progressBar.getIndeterminateDrawable() != null)
                            progressBar.getIndeterminateDrawable().setColorFilter(color, mode);
                        if (progressBar.getProgressDrawable() != null)
                            progressBar.getProgressDrawable().setColorFilter(color, mode);
                    }
                } catch (Throwable throwable) {
//					throwable.printStackTrace();
                }
            }
        }
    }

}
